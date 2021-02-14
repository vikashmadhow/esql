/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.base.tuple.T2;
import ma.vi.esql.parser.*;
import ma.vi.esql.parser.define.AlterTable;
import ma.vi.esql.parser.define.ColumnDefinition;
import ma.vi.esql.parser.define.CreateTable;
import ma.vi.esql.parser.define.DerivedColumnDefinition;
import ma.vi.esql.parser.query.Column;
import ma.vi.esql.type.*;

import java.util.Map;

import static ma.vi.esql.builder.Attributes.TYPE;

/**
 * Reference to a column.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class ColumnRef extends Expression<String> implements Macro {
  public ColumnRef(Context context, String qualifier, String name) {
    super(context, name, T2.of("qualifier", new Esql<>(context, qualifier)));
  }

  public ColumnRef(ColumnRef other) {
    super(other);
  }

  @Override
  public ColumnRef copy() {
    if (!copying()) {
      try {
        copying(true);
        return new ColumnRef(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public Type type() {
    if (type == null) {
      if (qualifier() != null && context.type(qualifier()) instanceof Relation) {
        /*
         * In a With query, the column could be part of a common-table-expression (CTE),
         * in which case, the type of the latter would be in the local context.
         */
        Column column = ((Relation)context.type(qualifier())).findColumn(null, name());
        if (column != null) {
          type = column.type();
        }
      }
      if (type == null) {
        QueryUpdate qu = ancestor(QueryUpdate.class);
        if (qu != null) {
          /*
           * In a query check all levels as the column might refer to a table in
           * an outer level, if it is in a subquery, e.g.
           */
          Column column = column(qu);
          if (column.expr() instanceof ColumnRef) {
            if (column.metadata() != null && column.metadata().attribute(TYPE) != null) {
              type = Types.typeOf((String)column.metadata().evaluateAttribute(TYPE));
            } else {
              type = Types.VoidType;
            }
          } else {
            type = column.type();
          }
        } else {
          Column column = ancestor(Column.class);
          if (column != null && column.parent != null && column.parent.value instanceof BaseRelation) {
            /*
             * Derived columns which are part of base relations can use the type
             * information of the relation to find their correct type. (the columns
             * of base relations are loaded on startup and their type set to void
             * for derived columns).
             */
            BaseRelation rel = (BaseRelation)column.parent.value;
            Column col = rel.findColumn(null, name());
            if (col != null) {
              type = col.type();
            }
          }
        }
      }
      if (type == null) {
        /*
         * The column could be part of a create statement.
         */
        CreateTable create = ancestor(CreateTable.class);
        if (create != null) {
          for (ColumnDefinition col: create.columns()) {
            if (col.name().equals(name())) {
              if (col instanceof DerivedColumnDefinition) {
                type = col.expression().type();
              } else {
                type = col.type();
              }
            }
          }
        } else {
          /*
           * The column could be part of an alter table statement.
           */
          AlterTable alter = ancestor(AlterTable.class);
          if (alter != null) {
            BaseRelation table = context.structure.relation(alter.name());
            if (table == null) {
              throw new TranslationException("Could not find table " + alter.name());
            }
            Column column = table.column(name());
            if (column == null) {
              throw new TranslationException("Could not find field " + name() + " in table " + alter.name());
            }
            type = column.type();
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
  private Column column(QueryUpdate qu) {
    Column column = null;
    while (column == null && qu != null) {
      column = qu.tables().type().findColumn(qualifier(), name());
      qu = qu.parent() == null ? null : qu.parent().ancestor(QueryUpdate.class);
    }
    return column;
  }

  @Override
  public int expansionOrder() {
    return HIGH;
  }

  /**
   * Expand derived columns to their base expressions.
   */
  @Override
  public boolean expand(String name, Esql<?, ?> esql) {
    QueryUpdate stmt = ancestor(QueryUpdate.class);
    if (stmt != null) {
      Relation rel = stmt.tables().type();
      if (qualifier() != null) {
        rel = rel.forAlias(qualifier());
      }
      if (rel instanceof BaseRelation
      || (rel instanceof AliasedRelation && ((AliasedRelation)rel).relation instanceof BaseRelation)) {
        Column column = rel.column(qualifier(), name());
        if (column.derived()) {
          Expression<?> expr = column.expr().copy();
          if (qualifier() != null) {
            qualify(expr, qualifier(), null, true);
          }
          parent.replaceWith(name, new GroupedExpression(context, expr));
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public String translate(Target target, Map<String, Object> parameters) {
    boolean sqlServerBool = target == Target.SQLSERVER
                         && type() == Types.BoolType
                         && (ancestor("on") != null || ancestor("where") != null || ancestor("having") != null)
                         && (parent == null || parent.ancestor(Coalesce.class) == null);
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

  public void qualifier(String qualifier) {
    Esql<String, ?> v = (Esql<String, ?>)children.get("qualifier");
    v.value = qualifier;
  }

  public String name() {
    return value;
  }

  public void name(String name) {
    value = name;
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
                                                 String suffix,
                                                 boolean replaceExistingQualifier) {
    esql.forEach(e -> {
      if (e instanceof ColumnRef) {
        SelectExpression selExpr = e.ancestor(SelectExpression.class);
        if (selExpr == null) {
          changeQualifierInColumnRef((ColumnRef)e, qualifier, suffix, replaceExistingQualifier);
        }
      }
      return true;
    });
    return esql;
  }

  private static void changeQualifierInColumnRef(ColumnRef ref,
                                                 String qualifier,
                                                 String suffix,
                                                 boolean replaceExistingQuantifier) {
    if (ref.qualifier() == null || replaceExistingQuantifier) {
      ref.qualifier(qualifier);
    }
    if (suffix != null) {
      ref.name(ref.name() + suffix);
    }
  }

  private transient volatile Type type;
}