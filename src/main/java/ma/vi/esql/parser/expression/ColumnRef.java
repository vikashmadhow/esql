/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.base.tuple.T2;
import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.Esql;
import ma.vi.esql.parser.QueryUpdate;
import ma.vi.esql.parser.TranslationException;
import ma.vi.esql.parser.define.AlterTable;
import ma.vi.esql.parser.define.ColumnDefinition;
import ma.vi.esql.parser.define.CreateTable;
import ma.vi.esql.parser.define.DerivedColumnDefinition;
import ma.vi.esql.parser.query.Column;
import ma.vi.esql.type.BaseRelation;
import ma.vi.esql.type.Type;

/**
 * Reference to a column.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class ColumnRef extends Expression<String> { // implements Macro {
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
      Column column = column(ancestor(QueryUpdate.class));
      if (column != null) {
        type = column.type();
      } else {
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
            column = table.column(name());
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
      column = qu.tables().type().column(qualifier(), name());
      qu = qu.parent() == null ? null : qu.parent().ancestor(QueryUpdate.class);
    }
    return column;
  }

//  @Override
//  public int expansionOrder() {
//    return HIGH;
//  }
//
//  /**
//   * Expands derived columns to their base expressions and add
//   * missing metadata in cases where the column ref is specified
//   * on its own (i.e. without being part of an expression).
//   */
//  @Override
//  public boolean expand(String name, Esql<?, ?> esql) {
//    QueryUpdate stmt = ancestor(QueryUpdate.class);
//    if (stmt != null) {
//      if (ancestor(Metadata.class) != null
//          && ancestorDistance(Metadata.class) < ancestorDistance(QueryUpdate.class)) {
//        /*
//         * Field is part of Metadata which might have been copied from
//         * another part of the query. Ensure that the qualifier used in
//         * the expression is the same as the column for this metadata.
//         */
//        Column column = ancestor(Column.class);
//        if (column != null) {
//          Expression<?> colExpr = column.expr();
//          ColumnRef col = colExpr.firstChild(ColumnRef.class);
//          if (col != null && col.qualifier() != null) {
//            qualify(this, col.qualifier(), null, true);
//          }
//        }
//      }
//      Field field = field(stmt);
//      if (field == null) {
//        throw new TranslationException(qualifiedName() + " could not be found in the tables of query");
//      }
//      if (field.typeOfField() == DERIVED) {
//        Expression<?> expr = field.expression().expression();
//        if (qualifier() != null) {
//          qualify(expr, qualifier(), null, true);
//        }
//        expr.basedOn(field);
//        parent.replaceWith(name, new GroupedExpression(context, expr));
//        if (parent instanceof Column && ((Column)parent).alias() == null) {
//          ((Column)parent).alias(name());
//        }
//        // more expansion may be needed for derived field referring
//        // to other derived fields
//        return true;
//
//      } else if (parent instanceof Column) {
//        Column column = (Column)parent;
//        if (column.expr() instanceof ColumnRef) {
//          List<Attribute> attributes = new ArrayList<>();
//          if (column.metadata() != null && column.metadata().attributes() != null) {
//            attributes.addAll(column.metadata().attributes());
//          }
//          Map<String, Attribute> colMetadata = new HashMap<>();
//          for (Attribute a: attributes) {
//            colMetadata.put(a.name(), a);
//          }
//
//          /*
//           * Add missing metadata when the column reference is the sole
//           * element of the column (i.e. it is not part of a larger expression)
//           * if select is not set to explicit mode (in explicit mode only
//           * expressions specified explicitly are computed, no implicit
//           * expressions are added to the query).
//           */
//          if (!(stmt instanceof Select) || !((Select)stmt).explicit()) {
//            boolean added = false;
//            Map<String, DbExpression> fieldMetadata = field.attributes();
//            for (String attr: fieldMetadata.keySet()) {
//              if (!colMetadata.containsKey(attr)) {
//                colMetadata.put(attr, new Attribute(context, attr, fieldMetadata.get(attr).expression()));
//                added = true;
//              }
//            }
//            if (added) {
//              column.metadata(new Metadata(context, new ArrayList<>(colMetadata.values())));
//            }
//            return added;
//          }
//        }
//      }
//    }
//    return false;
//  }

  @Override
  public String translate(Target target) {
    return switch (target) {
      case ESQL, JAVASCRIPT -> qualifiedName();
      case JSON             -> '"' + qualifiedName() + '"';
      default               -> (qualifier() != null ? '"' + qualifier() + "\"." : "") + '"' + name() + "\"";
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
        if (selExpr == null || e.ancestorDistance(SelectExpression.class) > e.ancestorDistance(QueryUpdate.class)) {
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