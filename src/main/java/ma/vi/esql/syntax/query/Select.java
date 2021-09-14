/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.query;

import ma.vi.base.string.Strings;
import ma.vi.base.tuple.T2;
import ma.vi.esql.function.Function;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.define.Metadata;
import ma.vi.esql.syntax.expression.ColumnRef;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.expression.FunctionCall;

import java.util.*;

import static ma.vi.base.tuple.T2.of;

/**
 * An ESQL select statement.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Select extends QueryUpdate /* implements Macro */ {
  public Select(Context                     context,
                String                      value,
                Metadata                    metadata,
                boolean                     distinct,
                List<Expression<?, String>> distinctOn,
                boolean                     explicit,
                List<Column>                columns,
                TableExpr                   from,
                Expression<?, String>       where,
                GroupBy                     groupBy,
                Expression<?, String>       having,
                List<Order>                 orderBy,
                Expression<?, String>       offset,
                Expression<?, String>       limit) {
    super(context,
          value,
          of("distinct",    new Esql<>(context, distinct)),
          of("distinctOn",  new Esql<>(context, "distinctOn", distinctOn)),
          of("explicit",    new Esql<>(context, explicit)),
          of("metadata",    metadata),
          of("columns",     renameColumns(context, columns)),
          of("tables",      from),
          of("where",       where),
          of("groupBy",     groupBy),
          of("having",      having),
          of("orderBy",     orderBy == null ? null : new Esql<>(context, "orderBy", orderBy)),
          of("offset",      offset),
          of("limit",       limit));
  }

  public Select(Select other) {
    super(other);
  }

  public Select(Select other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public Select copy() {
    return new Select(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public Select copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new Select(this, value, children);
  }

  /*
   * Rename column names to be unique without random characters.
   */
  private static ColumnList renameColumns(Context context, List<Column> columns) {
    List<Column> renamed = new ArrayList<>();
    Set<String> names = new HashSet<>();
    for (Column column: columns) {
      String alias = column.alias();
      if (alias.startsWith("__auto_col")) {
        if (column.expression() instanceof FunctionCall) {
          alias = makeUnique(names, ((FunctionCall)column.expression()).functionName());
        } else {
          alias = makeUnique(names, "column");
        }
      } else {
        alias = makeUnique(names, alias);
      }
      renamed.add(column.copy(alias));
    }
    return new ColumnList(context, renamed);
  }

  @Override
  public boolean modifying() {
    return false;
  }

  private static String makeUnique(Set<String> names, String alias) {
    int unique = 1;
    String name = alias;
    while (names.contains(name)) {
      name = alias + "_" + (unique++);
    }
    names.add(name);
    return name;
  }

//  @Override
//  public Esql<?, ?> expand(Esql<?, ?> esql, EsqlPath path) {
//    /*
//     * Expand star columns (*) to the individual columns they refer to
//     */
//    TableExpr from = tables();
//    Relation fromType = from.type(path);
//
//    boolean expanded = false;
//    Map<String, String> aliased = new HashMap<>();
//    Map<String, Column> resolvedColumns = new LinkedHashMap<>();
//    for (Column column: columns()) {
//      if (column instanceof StarColumn all) {
//        expanded = true;
//        String qualifier = all.qualifier();
//        Relation rel = qualifier == null ? fromType : fromType.forAlias(qualifier);
//        for (Column relCol: rel.columns()) {
//          String alias = relCol.alias();
//          if (alias == null) {
//            alias = Strings.makeUnique(resolvedColumns.keySet(), "col", false);
//          }
//          Column col;
//          if (rel instanceof BaseRelation
//           || (rel instanceof AliasedRelation && ((AliasedRelation)rel).relation instanceof BaseRelation)) {
//            col = relCol.copy();
//            if (qualifier != null) {
//              ColumnRef.qualify(col.expression(), qualifier, null, true);
//            }
//            if (col.metadata() != null) {
//              for (Attribute attr: col.metadata().attributes().values()) {
//                ColumnRef.qualify(attr.attributeValue(), qualifier, null, true);
//              }
//            }
//          } else {
//            col = new Column(context,
//                             alias,
//                             new ColumnRef(context, qualifier, relCol.alias()),
//                             null);
//          }
//
//          int pos = alias.indexOf('/');
//          if (pos == -1) {
//            /*
//             * Normal column (not metadata).
//             */
//            if (resolvedColumns.containsKey(alias)) {
//              alias = Strings.makeUnique(resolvedColumns.keySet(), alias, false);
//            }
//            if (relCol.alias() != null && !alias.equals(relCol.alias())) {
//              aliased.put(relCol.alias(), alias);
//              col.alias(alias);
//            }
//            resolvedColumns.put(alias, col);
//
//          } else if (pos > 0) {
//            /*
//             * Column metadata
//             */
//            String columnName = alias.substring(0, pos);
//            if (aliased.containsKey(columnName)) {
//              // replace column name with replacement if the column name was changed
//              String aliasName = aliased.get(columnName);
//              alias = aliasName + alias.substring(pos);
//              col.alias(alias);
//            }
//            resolvedColumns.put(alias, col);
//
//          } else if (!resolvedColumns.containsKey(alias)) {
//            /*
//             * table metadata first encounter (by elimination, pos==0 in this case)
//             */
//            resolvedColumns.put(alias, col);
//          }
//        }
//      } else {
//        column.expression().forEach((e, p) -> {
//          if (e instanceof ColumnRef) {
//            SelectExpression selExpr = p.ancestor(SelectExpression.class);
//            if (selExpr == null) {
//              ColumnRef c = (ColumnRef)e;
//              if (c.qualifier() == null) {
//                Column col = fromType.column(c.name());
//                c.replaceWith(col.expression());
//              }
//            }
//          }
//          return true;
//        });
//        resolvedColumns.put(column.alias(), column);
//      }
//    }
//    columns(new ArrayList<>(resolvedColumns.values()));
//    return expanded;
//  }

  @Override protected boolean grouped() {
    if (groupBy() != null) {
      return true;
    } else if (columns().size() == 1) {
      /*
       * A select is also grouped if its single column is an aggregate function
       * such as count or max.
       */
      Column col = columns().get(0);
      Expression<?, String> expr = col.expression();
      if (expr instanceof FunctionCall fc) {
        Function function = context.structure.function(fc.functionName());
        return function != null && function.aggregate;
      }
    }
    return false;
  }

  /**
   * For SQL Server complex groups support:
   * <ol>
   *     <li>Find column references in the expression and add to the columns
   *         of the inner query.</li>
   *     <li>Create a column referencing the inner columns to be added to
   *         the outer query.</li>
   * </ol>
   */
  public Expression<?, String> remapExpression(Expression<?, String> expression,
                                               Map<String, String> addedInnerCols,
                                               List<Column> innerCols,
                                               String innerSelectAlias) {
    /*
     * Find column references in the expression and add to the columns
     * of the inner query.
     */
    expression.forEach((e, p) -> {
      if (e instanceof ColumnRef ref) {
        if (!addedInnerCols.containsKey(ref.qualifiedName())) {
          String alias = Strings.makeUnique(new HashSet<>(addedInnerCols.values()), ref.name());
          innerCols.add(new Column(context, alias, ref.copy(), null));
          addedInnerCols.put(ref.qualifiedName(), alias);
        }
      }
      return true;
    });

    /*
     * Remap expression to be added to outer query.
     */
    return (Expression<?, String>)expression.map((e, p) -> {
      if (e instanceof ColumnRef ref) {
        String qualifiedName = ref.qualifiedName();
        ref = ref.qualifier(innerSelectAlias);
        return addedInnerCols.containsKey(qualifiedName)
                  ? ref.name(addedInnerCols.get(qualifiedName))
                  : ref;
      }
      return e;
    });
  }

  public Boolean distinct() {
    return childValue("distinct");
  }

//  public Select distinct(Boolean distinct) {
//    childValue("distinct", distinct);
//    return this;
//  }

  public List<Expression<?, String>> distinctOn() {
    return child("distinctOn").children();
  }

//  public Select distinctOn(List<Expression<?, String>> on) {
//    childrenList("distinctOn", on);
//    return this;
//  }

  public Boolean explicit() {
    return childValue("explicit");
  }

//  public Select explicit(Boolean explicit) {
//    childValue("explicit", explicit);
//    return this;
//  }

  public GroupBy groupBy() {
    return child("groupBy");
  }

  public Select groupBy(GroupBy groupBy) {
    return set("groupBy", groupBy);
  }

  public Expression<?, String> having() {
    return child("having");
  }

//  public Select having(Expression<?, String> having) {
//    child("having", having);
//    return this;
//  }

  public List<Order> orderBy() {
    return child("orderBy").children();
  }

//  public Select orderBy(List<Order> orderBy) {
//    childrenList("orderBy", orderBy);
//    return this;
//  }

  public Expression<?, String> offset() {
    return child("offset");
  }

//  public Select offset(Expression<?, String> offset) {
//    child("offset", offset);
//    return this;
//  }

  public Expression<?, String> limit() {
    return child("limit");
  }

//  public Select limit(Expression<?, String> limit) {
//    child("limit", limit);
//    return this;
//  }
}
