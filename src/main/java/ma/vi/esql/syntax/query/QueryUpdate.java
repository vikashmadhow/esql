/*
 * Copyright (c) 2020-2021 Vikash Madhow
 */

package ma.vi.esql.syntax.query;

import ma.vi.base.tuple.T2;
import ma.vi.esql.database.Database;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.ColumnMapping;
import ma.vi.esql.exec.Result;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.exec.function.FunctionCall;
import ma.vi.esql.semantic.type.*;
import ma.vi.esql.syntax.*;
import ma.vi.esql.syntax.define.Metadata;
import ma.vi.esql.syntax.expression.*;
import ma.vi.esql.syntax.expression.literal.Literal;
import ma.vi.esql.syntax.expression.logical.And;
import ma.vi.esql.syntax.macro.Macro;
import ma.vi.esql.syntax.modify.Delete;
import ma.vi.esql.syntax.modify.Insert;
import ma.vi.esql.syntax.modify.InsertRow;
import ma.vi.esql.syntax.modify.Update;
import ma.vi.esql.translation.TranslationException;
import org.pcollections.PMap;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static java.lang.System.Logger.Level.ERROR;
import static java.sql.ResultSet.CONCUR_READ_ONLY;
import static java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE;
import static java.util.Collections.unmodifiableMap;
import static ma.vi.base.string.Strings.random;
import static ma.vi.esql.database.Database.NULL_DB;
import static ma.vi.esql.database.EsqlConnection.NULL_CONNECTION;
import static ma.vi.esql.translation.SqlServerTranslator.ADD_IIF;

/**
 * The parent of all query (select) and update (update, insert and delete)
 * ESQL statements.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public abstract class QueryUpdate extends MetadataContainer<QueryTranslation> {
  @SafeVarargs
  public QueryUpdate(Context context, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(context, value, children);
  }

  public QueryUpdate(QueryUpdate other) {
    super(other);
    this.type = other.type == null ? null : other.type.copy();
  }

  @SafeVarargs
  public QueryUpdate(QueryUpdate other,
                     String value,
                     T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public abstract QueryUpdate copy();

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public abstract QueryUpdate copy(String value, T2<String, ? extends Esql<?, ?>>... children);

  @Override
  public Type computeType(EsqlPath path) {
    if (type == Types.UnknownType) {
      Selection sel = new Selection(new ArrayList<>(columns()), null, tables(), null);
      if (path.hasAncestor(Macro.OngoingMacroExpansion.class)) {
        return sel;
      } else {
        type = sel;
      }
    }
    return type;
  }

  /**
   * Returns the QueryUpdate ancestor in the path or, if not found, the Select
   * inside a SelectExpression in the ancestor path, if found. Otherwise returns
   * null. This utility method is useful to find the query-type of ancestor on
   * the path, normalizing how both QueryUpdate and SelectExpression are treated.
   */
  public static QueryUpdate ancestor(EsqlPath path) {
    Esql<?, ?> closest = path.closestAncestor(QueryUpdate.class,
                                              SelectExpression.class);
    return closest instanceof SelectExpression s ? s.select()
         : closest instanceof QueryUpdate      q ? q
         : null;
  }

  /**
   * Same as {@link #ancestor} but including the path.
   */
  public static T2<QueryUpdate, EsqlPath> ancestorAndPath(EsqlPath path) {
    T2<Esql<?, ?>, EsqlPath> closest = path.closestAncestorAndPath(QueryUpdate.class,
                                                                   SelectExpression.class);
    return closest == null                          ? null
         : closest.a instanceof SelectExpression s  ? T2.of(s.select(), closest.b)
         : closest.a instanceof QueryUpdate      q  ? T2.of(q, closest.b)
         : null;
  }

  /**
   * @return Whether this is a grouping query or not. In grouping queries
   *         column expansions must be done carefully (or not at all) as they
   *         can affect the result of the query.
   */
  protected boolean grouped() {
    return false;
  }

  /**
   * @return Whether this is a modifying query (update, insert, delete) or not.
   */
  public abstract boolean modifying();

  /**
   * <p>
   * Adds the output clause to the query. For selects this is the selected columns. For
   * insert, update and delete this is the output clause (returning section for PostgreSql,
   * output for SQL Server).
   * </p>
   *
   * <p>
   * The following parameters in the parameters map are used in this function:
   * <ul>
   * <li><b>addAttributes:</b> Whether to add attributes (metadata) to the query result or not.</li>
   *
   * <li><b>optimiseAttributesLoading:</b> Whether to optimise attributes loading or not. If optimised, attributes
   *                                  whose value can be determined at compile time (such as literals) will
   *                                  not be added to the query. However, for certain type of queries, such as
   *                                  unions combining several queries, the optimisation for column attributes
   *                                  assume that the value is the same for all rows but this might not be the
   *                                  case for unions, as each separate query could provide different values
   *                                  for the same attributes.
   *                                  </li>
   * </ul>
   *
   * @param query                     The generated query result is added to this string builder.
   * @param target                    The target database being translated to, such as Postgresql and Sql server.
   * @param qualifier                 The qualifier to apply to the column references in the columns expressions
   *                                  of the result. This is used, e.g., to qualify columns in the output list of
   *                                  SQL Server update queries (columns must be qualified with inserted or deleted
   *                                  in those cases)
   *
   * @return The query translation along with supporting information for its execution.
   */
  public QueryTranslation constructResult(StringBuilder        query,
                                          Target               target,
                                          EsqlPath             path,
                                          String               qualifier,
                                          PMap<String, Object> parameters,
                                          Environment          env) {

    boolean addAttributes = (Boolean)parameters.getOrDefault("addAttributes", true);
    boolean optimiseAttributesLoading = (Boolean)parameters.getOrDefault("optimiseAttributesLoading", true);

    /*
     * If this query is part of another query, it is a subquery and its attributes
     * must not be optimised away (removed from the query and calculated statically)
     * as they could be referenced in the outer query. The single exception is
     * when the query is the last query in a `With` query; in that case, attributes
     * loading can be optimised as the columns will not be referred outside of
     * the `With` query.
     */
    if (path.tail() != null) {
      QueryUpdate qu = path.tail().ancestor(QueryUpdate.class);
      if (qu != null && !(qu instanceof With w && w.query() == this)) {
        optimiseAttributesLoading = false;
      }
    }

    Map<String, ColumnMapping> columnMappings = new LinkedHashMap<>();
    Map<String, Object> resultAttributes = new LinkedHashMap<>();
    int itemIndex = 0;
    Type type = computeType(path.add(this));

    if (type instanceof Selection selection) {
      /*
       * Output columns.
       */
      for (T2<Relation, Column> c: selection.columns().stream()
                                            .filter(c -> !c.b.name().contains("/"))
                                            .toList()) {
        itemIndex++;
        if (itemIndex > 1) {
          query.append(", ");
        }
        Column column = c.b;
        if (qualifier == null) {
          query.append(column.translate(target,
                                        null,
                                        path.add(column),
                                        ADD_IIF,
                                        env));
        } else {
          Expression<?, ?> expr = ColumnRef.qualify(column.expression(), qualifier, true);
          query.append(expr.translate(target,
                                      null,
                                      path.add(expr),
                                      ADD_IIF,
                                      env));
        }
        String colName = column.name();
        columnMappings.put(colName, new ColumnMapping(itemIndex,
                                                      column,
                                                      column.computeType(path.add(column)),
                                                      new ArrayList<>(),
                                                      new HashMap<>()));
      }

      /*
       * Do not expand column list of selects inside expressions as the whole
       * expression is a single-value and expanding the column list will break
       * the query or not be of any use. The same applies to when select is used
       * as an insert value, or part of a column list.
       */
      if (!path.hasAncestor(SelectExpression.class,
                            InsertRow       .class,
                            Column          .class,
                            FunctionCall    .class,
                            BinaryOperator  .class,
                            Insert          .class,
                            Update          .class,
                            Delete          .class)) {
        /*
         * Output result metadata
         */
        List<Column> resultMetadata = selection.columns().stream()
                                               .filter(c -> c.b.name().startsWith("/"))
                                               .map(T2::b)
                                               .toList();
        for (Column column: resultMetadata) {
          String colName = column.name();
          String attrName = colName.substring(1);
          Expression<?, ?> attributeValue = column.expression();
          Map<Select, QueryTranslation> selectTableMapping = new HashMap<>();

          if (attributeValue instanceof ColumnRef ref) {
            /*
             * Result metadata column referring to a column in an inner-query: get
             * literal value by translating inner query.
             */
            String q = ref.qualifier();
            if (tables().aliased(q) instanceof SelectTableExpr sel) {
              QueryTranslation sub = selectTableMapping.computeIfAbsent(
                  sel.select(), s -> s.constructResult(new StringBuilder(),
                                                       target, path.add(s),
                                                       q, parameters, env));
              resultAttributes.put(attrName, sub.resultAttributes().get(attrName));

            } else if (tables().aliased(q) instanceof DynamicTableExpr dyn) {
              Metadata dynMeta = dyn.metadata();
              if (dynMeta != null
               && dynMeta.attributes() != null
               && dynMeta.attributes().containsKey(attrName)) {
                resultAttributes.put(attrName,
                                     dynMeta.attributes().get(attrName)
                                            .exec(target, NULL_CONNECTION, path, parameters, NULL_DB.structure()));
              }
            }
          } else if (attributeValue instanceof Literal<?>){
            /*
             * Result metadata is a literal: add its computed value.
             */
//            resultAttributes.put(attrName, attributeValue.exec(target, NULL_CONNECTION, path, parameters, NULL_DB.structure()));
            resultAttributes.put(attrName, attributeValue);
          } else {
            throw new TranslationException(
                "Result metadata column " + column + " is a " + attributeValue.getClass().getSimpleName()
              + ". Only literals and reference to literals are supported for result metadata columns.");
          }
        }

        /*
         * Output columns metadata.
         */
        for (T2<Relation, Column> col: selection.columns().stream()
                                                .filter(c -> c.b.name().charAt(0) != '/' && c.b.name().indexOf('/', 1) != -1)
                                                .toList()) {
          String alias = col.b.name();
          int pos = alias.indexOf('/');
          String colName = alias.substring(0, pos);
          ColumnMapping mapping = columnMappings.get(colName);
          if (mapping == null) {
            throw new RuntimeException("Could not find column mapping for column " + colName
                                     + " while there is an attribute for that columns (" + alias + ")");
          }

          String attrName = alias.substring(pos + 1);
          Expression<?, ?> attributeValue = col.b.expression();
          if (optimiseAttributesLoading
           && attributeValue instanceof Literal) {
           if (attributeValue instanceof UncomputedExpression) {
             mapping.attributes().put(attrName, attributeValue);
           } else {
             mapping.attributes().put(attrName,
                                      attributeValue.exec(target,
                                                          NULL_CONNECTION,
                                                          path,
                                                          parameters,
                                                          NULL_DB.structure()));
           }
          } else if (addAttributes) {
            itemIndex += 1;
            query.append(", ");
            appendExpression(query, attributeValue, target, path, alias, env);
            mapping.attributeIndices().add(new AttributeIndex(itemIndex, attrName, attributeValue.computeType(path.add(attributeValue))));
          }
        }
      }
    }
    return new QueryTranslation(this, query.toString(),
                                List.copyOf(columnMappings.values()),
                                unmodifiableMap(resultAttributes));
  }

  /**
   * Appends the expression to the output clause of this query.
   */
  protected void appendExpression(StringBuilder    query,
                                  Expression<?, ?> expression,
                                  Target           target,
                                  EsqlPath         path,
                                  String           alias,
                                  Environment      env) {
    query.append(expression.translate(target, null, path.add(expression), ADD_IIF, env));
    if (alias != null) {
      query.append(" \"").append(alias).append('"');
    }
  }

  /**
   * A name for this query; only common-table-expressions (CTE) have meaningful name
   * which can be referred to in other part of the query; in all other cases a randomly-
   * generated name is sufficient (and the default).
   */
  protected String name() {
    return "Projection_" + random(2);
  }

  /**
   * The list of columns being returned by this query or update.
   * For a query (select) this will be the list of expressions being
   * selected; for updates this will be the list of returning columns
   * if any.
   */
  public List<Column> columns() {
    return child("columns").children();
  }

  /**
   * Returns the columns embedded in its Esql envelop.
   */
  public ColumnList columnList() {
    return child("columns");
  }

  /**
   * Returns the table expression for selection or updates.
   */
  public TableExpr tables() {
    return child("tables");
  }

  public TableExpr from() {
    return tables();
  }

  public Expression<?, String> where() {
    return child("where");
  }

  public <T extends QueryUpdate> T where(Expression<?, String> where) {
    Expression<?, String> w = where();
    return set("where", w == null ? where
                                  : new And(context,
                                            new GroupedExpression(context, w),
                                            new GroupedExpression(context, where)));
  }

  public QueryUpdate where(String where) {
    return where(new Parser(context.structure).parseExpression(where));
  }

  @Override
  protected Object postTransformExec(Target               target,
                                     EsqlConnection       esqlCon,
                                     EsqlPath             path,
                                     PMap<String, Object> parameters,
                                     Environment          env) {
    Database db = esqlCon.database();
    Connection con = esqlCon.connection();
    QueryTranslation q = translate(db.target(), esqlCon, path, env);
    try {
      boolean producesResult = this instanceof Select;
      if (!producesResult) {
        Type type = computeType(path.add(this));
        producesResult = type instanceof Selection selection
                      && !selection.columns().isEmpty();
      }
      if (producesResult) {
        ResultSet rs = con.createStatement(TYPE_SCROLL_INSENSITIVE, CONCUR_READ_ONLY)
                          .executeQuery(q.translation());
        return new Result(esqlCon, rs, q);
      } else {
        con.createStatement().executeUpdate(q.translation());
        return new Result(esqlCon, null, q);
      }
    } catch (SQLException e) {
      log.log(ERROR, e.getMessage());
      log.log(ERROR, "Original query: " + this);
      log.log(ERROR, "Translation: " + q.translation());
      throw new RuntimeException(e);
    }
  }

  private static final System.Logger log = System.getLogger(QueryUpdate.class.getName());
}