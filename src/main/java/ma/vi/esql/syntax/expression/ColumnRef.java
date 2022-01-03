/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression;

import ma.vi.base.tuple.T2;
import ma.vi.esql.semantic.type.BaseRelation;
import ma.vi.esql.semantic.type.Relation;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.*;
import ma.vi.esql.syntax.define.AlterTable;
import ma.vi.esql.syntax.define.ColumnDefinition;
import ma.vi.esql.syntax.define.CreateTable;
import ma.vi.esql.syntax.define.DerivedColumnDefinition;
import ma.vi.esql.syntax.query.Column;
import ma.vi.esql.syntax.query.QueryUpdate;

import java.util.Map;

import static ma.vi.esql.builder.Attributes.TYPE;
import static ma.vi.esql.translator.SqlServerTranslator.requireIif;

/**
 * Reference to a column.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class ColumnRef extends Expression<String, String> implements Macro {
  public ColumnRef(Context context, String qualifier, String name) {
    super(context, "ColumnRef",
          T2.of("qualifier", new Esql<>(context, qualifier)),
          T2.of("name", new Esql<>(context, name)));
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
  public Type type(EsqlPath path) {
    if (type == null) {
      QueryUpdate qu = QueryUpdate.ancestor(path);
      if (qu != null) {
        /*
         * In a query check all levels as the column might refer to a table in
         * an outer level if it is in a subquery.
         */
        T2<Relation, Column> relCol = column(qu, this, path);
        if (relCol != null) {
          Column column = relCol.b;
          if (column.expression() instanceof ColumnRef) {
            if (column.metadata() != null && column.metadata().attribute(TYPE) != null) {
              type = Types.typeOf((String)column.metadata().evaluateAttribute(TYPE));
            } else {
              type = Types.VoidType;
            }
          } else {
            type = column.type(path.add(column));
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
          Column col = rel.findColumn(name());
          if (col != null) {
            type = col.type(path.add(col));
          }
        }
      }
      if (type == null) {
        /*
         * The column could be part of a create statement.
         */
        CreateTable create = path.ancestor(CreateTable.class);
        if (create != null) {
          for (ColumnDefinition col: create.columns()) {
            if (col.name().equals(name())) {
              if (col instanceof DerivedColumnDefinition) {
                type = col.expression().type(path.add(col.expression()));
              } else {
                type = col.type();
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
              throw new TranslationException("Could not find table " + alter.name());
            }
            Column column = table.column(name());
            if (column == null) {
              throw new TranslationException("Could not find field " + name() + " in table " + alter.name());
            }
            type = column.type(path.add(column));
          }
        }
      }
    }
    if (type == null) {
      throw new TranslationException("Could not determine the type of " + qualifiedName());
    }
    return type;
  }

  /**
   * Expand derived columns to their base expressions.
   */
  @Override
  public Esql<?, ?> expand(Esql<?, ?> e, EsqlPath path)  {
    QueryUpdate qu = QueryUpdate.ancestor(path);
    T2<Relation, Column> col = column(qu, this, path);
    if (col != null) {
      Relation rel = col.a;
      String alias = rel.alias();
      Column column = col.b;
      if (column.derived()) {
        GroupedExpression expr = new GroupedExpression(
            context, alias != null       ? qualify(column.expression(), alias)
                   : qualifier() != null ? qualify(column.expression(), qualifier())
                   : column.expression());
        return new ExpandedDerivedColumnRef(context, alias != null ? alias : qualifier(), name(), expr);
      }
      return alias == null || alias.equals(qualifier()) ? e : qualify(e, alias);
    }
    return e;
  }

  /**
   * Find the column referred to by this column reference. Since selects may be nested,
   * this method will move outside of the current select to find surrounding ones if the
   * column cannot be successfully matched in the current selection context.
   */
  public static T2<Relation, Column> column(QueryUpdate qu,
                                            ColumnRef ref,
                                            EsqlPath path) {
    T2<Relation, Column> column = null;
    while (column == null && qu != null && qu.tables().exists()) {
      column = qu.tables()
                 .type(path.add(qu.tables()))
                 .findColumn(ref);
      if (column == null) {
        T2<QueryUpdate, EsqlPath> ancestor = path.tail() == null
                                           ? null
                                           : QueryUpdate.ancestorAndPath(path.tail());
        if (ancestor == null) {
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
  protected String trans(Target target,
                         EsqlPath path,
                         Map<String, Object> parameters) {
    boolean sqlServerBool = target == Target.SQLSERVER
                         && type(path.add(this)) == Types.BoolType
                         && !requireIif(path, parameters)
                         && (path.ancestor(FunctionCall.class) == null);
    return switch (target) {
      case ESQL, JAVASCRIPT -> qualifiedName();
      case JSON             -> '"' + qualifiedName() + '"';
      default               ->
          (sqlServerBool ? "(" : "")
        + (qualifier() != null ? '"' + qualifier() + "\"." : "") + '"' + name() + "\""
        + (sqlServerBool ? "=1)" : "");
    };
  }

  @Override
  public String toString() {
    return qualifiedName();
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    st.append(qualifiedName());
  }

  public String qualifier() {
    return childValue("qualifier");
  }

  public ColumnRef qualifier(String qualifier) {
    return set("qualifier", new Esql<>(context, qualifier));
  }

  public String name() {
    return childValue("name");
  }

  public ColumnRef name(String name) {
    return set("name", new Esql<>(context, name));
  }

  public String qualifiedName() {
    String q = qualifier();
    return (q != null ? q + '.' : "") + name();
  }

  /**
   * Change the qualifier of all column references in an expression to the specified
   * one, replacing the existing ones if replaceExistingQualifier is true. This is
   * necessary in cases where the qualifier needs to be a specific one ('inserted', for
   * instance, in the output clause of an insert command in SQL Server) or to qualify
   * attribute expressions so that they refer to correct table when being used in a
   * selection with a compound table expressions.
   */
  public static <T extends Esql<?, ?>> T qualify(T esql, String qualifier) {
    return (T)esql.map((e, path) -> {
      if (e instanceof ColumnRef ref) {
        SelectExpression selExpr = path.ancestor(SelectExpression.class);
        if (selExpr == null || ref.qualifier() == null) {
          return ref.qualifier(qualifier);
        }
      }
      return e;
    });
  }

  private transient volatile Type type;
}