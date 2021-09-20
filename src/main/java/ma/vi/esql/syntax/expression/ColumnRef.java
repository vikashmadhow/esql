/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression;

import ma.vi.base.tuple.T2;
import ma.vi.esql.semantic.type.*;
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
    super(context, name, T2.of("qualifier", new Esql<>(context, qualifier)));
  }

  public ColumnRef(ColumnRef other) {
    super(other);
  }

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

  @Override
  public Type type(EsqlPath path) {
    if (type == null) {
      if (qualifier() != null && context.type(qualifier()) instanceof Relation) {
        /*
         * In a With query, the column could be part of a common-table-expression (CTE),
         * in which case, the type of the latter would be in the local context.
         */
        Column column = ((Relation)context.type(qualifier())).findColumn(null, name());
        if (column != null) {
          type = column.type(path);
        }
      }
      if (type == null) {
        QueryUpdate qu = path.ancestor(QueryUpdate.class);
        if (qu != null) {
          /*
           * In a query check all levels as the column might refer to a table in
           * an outer level, if it is in a subquery, e.g.
           */
          Column column = column(qu, path);
          if (column.expression() instanceof ColumnRef) {
            if (column.metadata() != null && column.metadata().attribute(TYPE) != null) {
              type = Types.typeOf((String)column.metadata().evaluateAttribute(TYPE));
            } else {
              type = Types.VoidType;
            }
          } else {
            type = column.type(path);
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
            Column col = rel.findColumn(null, name());
            if (col != null) {
              type = col.type(path);
            }
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
                type = col.expression().type(path);
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
            type = column.type(path);
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
   * Find the column referred to by this column reference. Since selects
   * may be nested, this method will move outside of the current select to
   * find surrounding ones if the column cannot be successfully matched in
   * the current selection context.
   */
  private Column column(QueryUpdate qu, EsqlPath path) {
    Column column = null;
    while (column == null && qu != null) {
      column = qu.tables().type(path).findColumn(qualifier(), name());
      if (path == null) {
        qu = null;
      } else {
        T2<QueryUpdate, EsqlPath> ancestor = path.tail().ancestorAndPath(QueryUpdate.class);
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

  /**
   * Expand derived columns to their base expressions.
   */
  @Override
  public Esql<?, ?> expand(Esql<?, ?> esql, EsqlPath path)  {
    QueryUpdate stmt = path.ancestor(QueryUpdate.class);
    if (stmt != null) {
      Relation rel = stmt.tables().type(path);
      if (qualifier() != null) {
        rel = rel.forAlias(qualifier());
      }
      if (rel instanceof BaseRelation
      || (rel instanceof AliasedRelation && ((AliasedRelation)rel).relation instanceof BaseRelation)) {
        Column column = rel.column(qualifier(), name());
        if (column.derived()) {
          Expression<?, String> expr = column.expression().copy();
          if (qualifier() != null) {
            qualify(expr, qualifier(), true);
          }
          return new GroupedExpression(context, expr);
        }
      }
    }
    return esql;
  }

  @Override
  protected String trans(Target target,
                         EsqlPath path,
                         Map<String, Object> parameters) {
    boolean sqlServerBool = target == Target.SQLSERVER
                         && type(path) == Types.BoolType
                         && !requireIif(path, parameters)
                         && (path.ancestor(Coalesce.class) == null);
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
//    Esql<String, ?> v = (Esql<String, ?>)children.get("qualifier");
//    v.value = qualifier;
    return set("qualifier", new Esql<>(context, qualifier));
  }

  public String name() {
    return value;
  }

  public ColumnRef name(String name) {
    return copy(name);
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
  public static <T extends Esql<?, ?>> T qualify(T esql,
                                                 String qualifier,
                                                 boolean replaceExistingQualifier) {
    return (T)esql.map((e, path) -> {
      if (e instanceof ColumnRef ref) {
        SelectExpression selExpr = path.ancestor(SelectExpression.class);
        if (selExpr == null) {
          if (ref.qualifier() == null || replaceExistingQualifier) {
            return ref.qualifier(qualifier);
          }
        }
      }
      return e;
    });
  }

//  private static void changeQualifierInColumnRef(ColumnRef ref,
//                                                 String qualifier,
//                                                 String suffix,
//                                                 boolean replaceExistingQuantifier) {
//    if (ref.qualifier() == null || replaceExistingQuantifier) {
//      ref.qualifier(qualifier);
//    }
//    if (suffix != null) {
//      ref.name(ref.name() + suffix);
//    }
//  }

  private transient volatile Type type;
}