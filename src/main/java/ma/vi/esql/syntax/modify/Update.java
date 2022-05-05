/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.modify;

import ma.vi.base.tuple.T2;
import ma.vi.esql.exec.Filter;
import ma.vi.esql.semantic.type.Column;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.define.Metadata;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.query.ColumnList;
import ma.vi.esql.syntax.query.QueryUpdate;
import ma.vi.esql.syntax.query.Select;
import ma.vi.esql.syntax.query.TableExpr;

import java.util.List;

import static ma.vi.base.tuple.T2.of;

/**
 * Update statement.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Update extends QueryUpdate {
  public Update(Context       context,
                String        updateTableAlias,
                TableExpr     from,
                Metadata      set,
                Expression<?, String> where,
                Metadata      returnMetadata,
                List<Column>  returnColumns) {
    super(context, "Update",
          of("updateTableAlias", new Esql<>(context, updateTableAlias)),
          of("set",              set),
          of("tables",           from),
          of("where",            where),
          of("metadata",         returnMetadata),
          of("columns",          new ColumnList(context, returnColumns)));
  }

  public Update(Update other) {
    super(other);
  }

  @SafeVarargs
  public Update(Update other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public Update copy() {
    return new Update(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public Update copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new Update(this, value, children);
  }

  @Override
  public Update filter(Filter filter, boolean firstFilter, EsqlPath path) {
    Select.FilterResult result = Select.filter(tables(), filter, firstFilter, where());
    if (result != null) {
      return new Update(context,
                        updateTableAlias(),
                        result.tables(),
                        set(),
                        result.condition(),
                        metadata(),
                        columns());
    } else {
      return this;
    }
  }

  @Override
  public boolean modifying() {
    return true;
  }

  public Metadata set() {
    return child("set");
  }

  public String updateTableAlias() {
    return childValue("updateTableAlias");
  }
}