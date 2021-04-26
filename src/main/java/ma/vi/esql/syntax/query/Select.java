/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.query;

import ma.vi.base.string.Strings;
import ma.vi.esql.function.Function;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.Macro;
import ma.vi.esql.syntax.QueryUpdate;
import ma.vi.esql.syntax.define.Attribute;
import ma.vi.esql.syntax.define.GroupBy;
import ma.vi.esql.syntax.define.Metadata;
import ma.vi.esql.syntax.expression.ColumnRef;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.expression.FunctionCall;
import ma.vi.esql.syntax.expression.SelectExpression;
import ma.vi.esql.type.AliasedRelation;
import ma.vi.esql.type.BaseRelation;
import ma.vi.esql.type.Relation;

import java.util.*;

import static ma.vi.base.tuple.T2.of;

/**
 * An ESQL select statement.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Select extends QueryUpdate implements Macro {
  public Select(Context             context,
                Metadata            metadata,
                boolean             distinct,
                List<Expression<?, String>> distinctOn,
                boolean             explicit,
                List<Column>        columns,
                TableExpr           from,
                Expression<?, String>       where,
                GroupBy             groupBy,
                Expression<?, String>       having,
                List<Order>         orderBy,
                Expression<?, String>       offset,
                Expression<?, String>       limit) {
    super(context, "Select",
        of("distinct",    new Esql<>(context, distinct)),
        of("distinctOn",  new Esql<>(context, "distinctOn", distinctOn)),
        of("explicit",    new Esql<>(context, explicit)),
        of("metadata",    metadata),
        of("columns",     new Esql<>(context, columns)),
        of("tables",      from),
        of("where",       where),
        of("groupBy",     groupBy),
        of("having",      having),
        of("orderBy",     new Esql<>(context, "orderBy", orderBy)),
        of("offset",      offset),
        of("limit",       limit));

    /*
     * Rename column names to be unique without random characters.
     */
    Set<String> names = new HashSet<>();
    for (Column column: columns) {
      String alias = column.alias();
      if (alias.startsWith("__auto_col")) {
        if (column.expr() instanceof FunctionCall) {
          alias = makeUnique(names, ((FunctionCall)column.expr()).functionName());
        } else {
          alias = makeUnique(names, "column");
        }
      } else {
        alias = makeUnique(names, alias);
      }
      column.alias(alias);
    }
  }

  public Select(Select other) {
    super(other);
  }

  @Override
  public Select copy() {
    if (!copying()) {
      try {
        copying(true);
        return new Select(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
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

  @Override
  public int expansionOrder() {
    return HIGHEST;
  }

  @Override
  public boolean expand(String name, Esql<?, ?> esql) {
    /*
     * Expand star columns (*) to the individual columns they refer to
     */
    TableExpr from = tables();
    Relation fromType = from.type();

    boolean expanded = false;
    Map<String, String> aliased = new HashMap<>();
    Map<String, Column> resolvedColumns = new LinkedHashMap<>();
    for (Column column: columns()) {
      if (column instanceof StarColumn) {
        expanded = true;
        StarColumn all = (StarColumn)column;
        String qualifier = all.qualifier();
        Relation rel = qualifier == null ? fromType : fromType.forAlias(qualifier);
        for (Column relCol: rel.columns()) {
          String alias = relCol.alias();
          if (alias == null) {
            alias = Strings.makeUnique(resolvedColumns.keySet(), "col", false);
          }
          Column col;
          if (rel instanceof BaseRelation
           || (rel instanceof AliasedRelation && ((AliasedRelation)rel).relation instanceof BaseRelation)) {
            col = relCol.copy();
            if (qualifier != null) {
              ColumnRef.qualify(col.expr(), qualifier, null, true);
            }
            if (col.metadata() != null) {
              for (Attribute attr: col.metadata().attributes().values()) {
                ColumnRef.qualify(attr.attributeValue(), qualifier, null, true);
              }
            }
          } else {
            col = new Column(context,
                             alias,
                             new ColumnRef(context, qualifier, relCol.alias()),
                             null);
          }

          int pos = alias.indexOf('/');
          if (pos == -1) {
            /*
             * Normal column (not metadata).
             */
            if (resolvedColumns.containsKey(alias)) {
              alias = Strings.makeUnique(resolvedColumns.keySet(), alias, false);
            }
            if (relCol.alias() != null && !alias.equals(relCol.alias())) {
              aliased.put(relCol.alias(), alias);
              col.alias(alias);
            }
            resolvedColumns.put(alias, col);

          } else if (pos > 0) {
            /*
             * Column metadata
             */
            String columnName = alias.substring(0, pos);
            if (aliased.containsKey(columnName)) {
              // replace column name with replacement if the column name was changed
              String aliasName = aliased.get(columnName);
              alias = aliasName + alias.substring(pos);
              col.alias(alias);
            }
            resolvedColumns.put(alias, col);

          } else if (!resolvedColumns.containsKey(alias)) {
            /*
             * table metadata first encounter (by elimination, pos==0 in this case)
             */
            resolvedColumns.put(alias, col);
          }
        }
      } else {
        column.expr().forEach(e -> {
          if (e instanceof ColumnRef) {
            SelectExpression selExpr = e.ancestor(SelectExpression.class);
            if (selExpr == null) {
              ColumnRef c = (ColumnRef)e;
              if (c.qualifier() == null) {
                Column col = fromType.column(c.name());
                c.replaceWith(col.expr());
              }
            }
          }
          return true;
        });
        resolvedColumns.put(column.alias(), column);
      }
    }
    columns(new ArrayList<>(resolvedColumns.values()));
    return expanded;
  }

  @Override protected boolean grouped() {
    if (groupBy() != null) {
      return true;
    } else if (columns().size() == 1) {
      /*
       * A select is also grouped if its single column is an aggregate function
       * such as count or max.
       */
      Column col = columns().get(0);
      Expression<?, String> expr = col.expr();
      if (expr instanceof FunctionCall) {
        FunctionCall fc = (FunctionCall)expr;
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
    expression.forEach(e -> {
      if (e instanceof ColumnRef) {
        ColumnRef ref = (ColumnRef)e;
        if (!addedInnerCols.containsKey(ref.qualifiedName())) {
          String alias = Strings.makeUnique(new HashSet<>(addedInnerCols.values()), ref.name());
          innerCols.add(new Column(
              context, alias, ref.copy(), null
          ));
          addedInnerCols.put(ref.qualifiedName(), alias);
        }
      }
      return true;
    });

    /*
     * Remap expression to be added to outer query.
     */
    return (Expression<?, String>)expression.map(e -> {
      if (e instanceof ColumnRef) {
        ColumnRef ref = (ColumnRef)e;
        String qualifiedName = ref.qualifiedName();
        ref.qualifier(innerSelectAlias);
        if (addedInnerCols.containsKey(qualifiedName)) {
          ref.name(addedInnerCols.get(qualifiedName));
        }
      }
      return e;
    });
  }

  public Boolean distinct() {
    return childValue("distinct");
  }

  public Select distinct(Boolean distinct) {
    childValue("distinct", distinct);
    return this;
  }

  public List<Expression<?, String>> distinctOn() {
    return child("distinctOn").childrenList();
  }

  public Select distinctOn(List<Expression<?, String>> on) {
    childrenList("distinctOn", on);
    return this;
  }

  public Boolean explicit() {
    return childValue("explicit");
  }

  public Select explicit(Boolean explicit) {
    childValue("explicit", explicit);
    return this;
  }

  public GroupBy groupBy() {
    return child("groupBy");
  }

  public Select groupBy(GroupBy groupBy) {
    child("groupBy", groupBy);
    return this;
  }

  public Expression<?, String> having() {
    return child("having");
  }

  public Select having(Expression<?, String> having) {
    child("having", having);
    return this;
  }

  public List<Order> orderBy() {
    return child("orderBy").childrenList();
  }

  public Select orderBy(List<Order> orderBy) {
    childrenList("orderBy", orderBy);
    return this;
  }

  public Expression<?, String> offset() {
    return child("offset");
  }

  public Select offset(Expression<?, String> offset) {
    child("offset", offset);
    return this;
  }

  public Expression<?, String> limit() {
    return child("limit");
  }

  public Select limit(Expression<?, String> limit) {
    child("limit", limit);
    return this;
  }
}
