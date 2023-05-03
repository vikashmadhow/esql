/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression;

import ma.vi.base.tuple.T2;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.ExecutionException;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.exec.function.FunctionCall;
import ma.vi.esql.semantic.scope.Scope;
import ma.vi.esql.semantic.scope.Symbol;
import ma.vi.esql.semantic.type.BaseRelation;
import ma.vi.esql.semantic.type.Column;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.define.Attribute;
import ma.vi.esql.syntax.define.Define;
import ma.vi.esql.syntax.define.Metadata;
import ma.vi.esql.syntax.define.table.AlterTable;
import ma.vi.esql.syntax.define.table.ColumnDefinition;
import ma.vi.esql.syntax.define.table.CreateTable;
import ma.vi.esql.syntax.define.table.DerivedColumnDefinition;
import ma.vi.esql.syntax.expression.comparison.*;
import ma.vi.esql.syntax.macro.TypedMacro;
import ma.vi.esql.syntax.query.QueryUpdate;
import ma.vi.esql.syntax.query.TableExpr;
import ma.vi.esql.translation.TranslationException;
import org.pcollections.PMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ma.vi.esql.semantic.type.Types.UnknownType;
import static ma.vi.esql.translation.SqlServerTranslator.requireIif;

/**
 * Reference to a column.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class ColumnRef extends    Expression<String, String>
                       implements TypedMacro, Symbol {
  public ColumnRef(Context context, String qualifier, String columnName) {
    this(context, qualifier, columnName, UnknownType);
  }

  public ColumnRef(Context context, String qualifier, String columnName, Type type) {
    super(context, "ColumnRef",
          T2.of("qualifier", new Esql<>(context, qualifier)),
          T2.of("columnName", new Esql<>(context, columnName)));
    this.type = type == null ? UnknownType : type;
  }

  public ColumnRef(ColumnRef other) {
    super(other);
  }

  @SafeVarargs
  public ColumnRef(ColumnRef other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public ColumnRef copy() {
    return new ColumnRef(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public ColumnRef copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new ColumnRef(this, value, children);
  }

  public static ColumnRef of(String qualifier, String name) {
    return new ColumnRef(null, qualifier, name);
  }

  @Override
  public Type computeType(EsqlPath path) {
    if (type == UnknownType) {
      Type computedType = null;
      Type t = type();
      if (t != UnknownType) {
        computedType = t;
      } else {
        QueryUpdate qu = QueryUpdate.ancestor(path);
        if (qu != null) {
          /*
           * In a query check all levels as the column might refer to a table in
           * an outer level if it is in a subquery.
           */
          Column column = column(qu, this, path);
          if (column != null) {
            computedType = column.type();
            if (computedType == UnknownType
            && !(column.expression() instanceof ColumnRef)) {
              computedType = column.expression().computeType(path.add(column.expression()));
            }
          }
        } else {
          Column column = path.ancestor(Column.class);
          if (column != null
              && column.has("relation")
              && column.childValue("relation") instanceof BaseRelation rel) {
            /*
             * Derived columns which are part of base relations can use the type
             * information of the relation to find their correct type. (the columns
             * of base relations are loaded on startup and their type set to void
             * for derived columns).
             */
            Column col = rel.findColumn(columnName());
            if (col != null) {
              computedType = col.computeType(path.add(col));
            }
          }
        }
        if (computedType == null) {
          /*
           * The column could be part of a `create` statement.
           */
          CreateTable create = path.ancestor(CreateTable.class);
          if (create != null) {
            for (ColumnDefinition col : create.columns()) {
              if (col.name().equals(columnName())) {
                if (col instanceof DerivedColumnDefinition) {
                  computedType = col.expression().computeType(path.add(col.expression()));
                } else {
                  computedType = col.type();
                }
              }
            }
          } else {
            /*
             * The column could be part of an alter table statement.
             */
            AlterTable alter = path.ancestor(AlterTable.class);
            if (alter != null) {
              BaseRelation table = context.structure.relation(alter.name());
              if (table == null) {
                throw new TranslationException(this, "Could not find table " + alter.name());
              }
              Column column = table.column(columnName());
              if (column == null) {
                throw new TranslationException(this, "Could not find field " + columnName() + " in table " + alter.name());
              }
              computedType = column.computeType(path.add(column));
            }
          }
        }
        if (computedType == null) {
          Symbol symbol = Scope.findSymbol(qualifiedName(), path);
          if (symbol != null) {
            computedType = symbol.type();
          }
        }
      }
      if (computedType == null) {
        /*
         * For definitions and uncomputed expressions, assume unknown when type
         * could not be computed.
         */
        if (path.hasAncestor(Define.class, UncomputedExpression.class)) {
          return UnknownType;
        } else {
          throw new TranslationException(this, "Could not determine the type of " + qualifiedName());
        }
      }
      if (path.hasAncestor(OngoingMacroExpansion.class)) {
        return computedType;
      } else {
        type = computedType;
      }
    }
    return type;
  }

  /**
   * Expand derived columns to their base expressions and find the qualifier for
   * unqualified columns.
   */
  @Override
  public Esql<?, ?> expand(Esql<?, ?> e, EsqlPath path)  {
    QueryUpdate qu = QueryUpdate.ancestor(path);
    Column col = column(qu, this, path);
    if (col != null) {
      if (col.derived()) {
        return new GroupedExpression(context, col.expression());
      }
      if (col.expression() instanceof ColumnRef ref
       && !path.hasAncestor(UncomputedExpression.class)) {
        String alias = ref.qualifier();
        return alias == null || alias.equals(qualifier()) ? e : qualify(e, alias);
      }
    }
    return e;
  }

  /**
   * Find the column referred to by this column reference. Since selects may be nested,
   * this method will move outside of the current select to find surrounding ones if the
   * column cannot be successfully matched in the current selection context.
   */
  public static Column column(QueryUpdate qu, ColumnRef ref, EsqlPath path) {
    Column column = null;
    String qualifier = ref.qualifier();
    while (column == null && qu != null) {
      TableExpr tables = qu.tables();
      if (tables != null && qualifier != null) {
        /*
         * In a correlated query, the qualifier might reference a table outside
         * of this query, resulting in the following statement returning null
         * when looking for that named table. This is handled below where the
         * parent of the current query is checked.
         */
        tables = tables.aliased(qualifier);
      }
      if (tables != null && tables.exists(path)) {
        column = tables.columnList(path).stream()
                       .filter(c -> c.name().equals(ref.columnName()))
                       .findFirst().orElse(null);
      }
      if (column == null) {
        T2<QueryUpdate, EsqlPath> ancestor = path.tail() == null
                                           ? null
                                           : QueryUpdate.ancestorAndPath(path.tail());
        if (ancestor == null) {
          /*
           * If the query is inside a create table (e.g., a select expression as
           * the expression for a derived column), the target table might not be
           * created yet (e.g. the column is referring to a column in the current
           * create table statement). In that case, extract the column from the
           * column definition in the create table.
           */
          CreateTable create = path.ancestor(CreateTable.class);
          if (create != null) {
            Map<String, ColumnDefinition> cols = create.columnsByName();
            if (cols.containsKey(ref.columnName())) {
              ColumnDefinition def = cols.get(ref.columnName());
              column = Column.fromDefinition(def);
            }
          }
          qu = null;
        } else {
          qu = ancestor.a;
          path = ancestor.b;
        }
      }
    }
    return column;
  }

  public static ColumnRef from(Expression<?, String> expr) {
    return expr instanceof ColumnRef r ? r
         : expr instanceof ExpandedDerivedColumnRef r ? ColumnRef.of(r.qualifier(), r.name())
         : null;
  }

  @Override
  protected String trans(Target               target,
                         EsqlConnection       esqlCon,
                         EsqlPath             path,
                         PMap<String, Object> parameters,
                         Environment          env) {
    boolean sqlServerBool = target == Target.SQLSERVER
                         && computeType(path.add(this)) == Types.BoolType
                         && !requireIif(path, parameters)
                         && path.ancestor(FunctionCall.class)       == null
                         && path.ancestor(LessThan.class)           == null
                         && path.ancestor(LessThanOrEqual.class)    == null
                         && path.ancestor(GreaterThan.class)        == null
                         && path.ancestor(GreaterThanOrEqual.class) == null
                         && path.ancestor(Equality.class)           == null
                         && path.ancestor(Inequality.class)         == null
                         && path.ancestor(IsNull.class)             == null
                         && path.ancestor("set")                    == null;
    return switch (target) {
      case ESQL        -> qualifiedName();
      case JAVASCRIPT  -> qualifier() == null
                        ? "row." + columnName() + ".$v"
                        : "row." + qualifier()  + ".$m." + columnName();

      default          ->
          (sqlServerBool ? "(" : "")
        + (qualifier() != null ? '"' + qualifier() + "\"." : "") + '"' + columnName() + "\""
        + (sqlServerBool ? "=1)" : "");
    };
  }

  @Override
  public String toString() {
    return qualifiedName();
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    st.append(qualifiedName())
      .append(type() != UnknownType ? " (" + type() + ')' : "");
  }

  public String qualifier() {
    return childValue("qualifier");
  }

  public ColumnRef qualifier(String qualifier) {
    return set("qualifier", new Esql<>(context, qualifier));
  }

  public String columnName() {
    return childValue("columnName");
  }

  public ColumnRef columnName(String columnName) {
    return set("columnName", new Esql<>(context, columnName));
  }

  public String qualifiedName() {
    String q = qualifier();
    return (q != null ? q + '.' : "") + columnName();
  }

  @Override
  public String name() {
    return qualifiedName();
  }

  @Override
  public Esql<?, ?> scope(Scope scope, EsqlPath path) {
    Symbol symbol = scope.findSymbol(name());
    if (symbol != null) {
      /*
       * Symbol for column reference will not be found when it is used in a query
       * (semantic analysis for query statements do not currently put the symbols
       * in the query in a symbol table).
       */
      super.scope(scope, path);
    }
    return this;
  }

  @Override
  public Object exec(Target               target,
                     EsqlConnection       esqlCon,
                     EsqlPath             path,
                     PMap<String, Object> parameters,
                     Environment          env) {
    if (!env.knows(name())) {
      throw new ExecutionException(this, "Unknown variable " + name() + " in " + env);
    }
    return env.get(name());
  }

  /**
   * Change the qualifier of all column references in an expression to the specified
   * one, replacing the existing ones if replaceExistingQualifier is true. This is
   * necessary in cases where the qualifier needs to be a specific one ('inserted', for
   * instance, in the output clause of an insert command in SQL Server) or to qualify
   * attribute expressions so that they refer to correct table when being used in a
   * selection with a compound table expressions.
   */
  public static <T extends Esql<?, ?>> T qualify(T      esql,
                                                 String qualifier) {
    return qualify(esql, qualifier, true);
  }

  /**
   * Change the qualifier of all column references in an expression to the specified
   * one, replacing the existing ones if replaceExistingQualifier is true. This is
   * necessary in cases where the qualifier needs to be a specific one ('inserted', for
   * instance, in the output clause of an insert command in SQL Server) or to qualify
   * attribute expressions so that they refer to correct table when being used in a
   * selection with a compound table expressions.
   */
  public static <T extends Esql<?, ?>> T qualify(T       esql,
                                                 String  qualifier,
                                                 boolean replaceExisting) {
    return (T)esql.map((e, path) -> {
      if (e instanceof ColumnRef ref
       && !path.hasAncestor(UncomputedExpression.class)) {
        SelectExpression selExpr = path.ancestor(SelectExpression.class);
        if (selExpr == null
        && (replaceExisting || ref.qualifier() == null)) {
          return ref.qualifier(qualifier);
        }
      }
      return e;
    });
  }

  public static Metadata qualify(Metadata metadata, String qualifier) {
    if (metadata != null
     && metadata.attributes() != null
     && !metadata.attributes().isEmpty()) {
      List<Attribute> atts = new ArrayList<>();
      for (Attribute a: metadata.attributes().values()) {
        atts.add(new Attribute(a.context, a.name(), qualify(a.attributeValue(), qualifier)));
      }
      return new Metadata(metadata.context, atts);
    }
    return metadata;
  }

  /**
   * Change the name of all column references in the expression using the supplied
   * name mapping. I.e., if 'a' is mapped to 'b' in mapping, all column references
   * to 'a' in expression are changed to b.
   *
   * @param expr The expression containing the column references to rename.
   * @param mapping The name mappings.
   * @return The expression with the renamed column references.
   */
  public static Expression<?, ?> rename(Expression<?, ?>    expr,
                                        Map<String, String> mapping) {
    return (Expression<?, ?>)expr.map(
              (e, p) -> e instanceof ColumnRef r && mapping.containsKey(r.columnName())
                      ? r.columnName(mapping.get(r.columnName()))
                      : e);
  }

  /**
   * Removes qualifiers from the column ref in the expression.
   * @param expr The expression containing column refs to unqualify.
   * @return The unqualified expression.
   */
  public static Expression<?, ?> unqualify(Expression<?, ?> expr) {
    return (Expression<?, ?>)expr.map(
              (e, p) -> e instanceof ColumnRef r && r.qualifier() != null
                      ? r.qualifier(null)
                      : e);
  }
}