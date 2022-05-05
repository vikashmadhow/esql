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
 * Delete statement.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Delete extends QueryUpdate {
  public Delete(Context       context,
                String        deleteTableAlias,
                TableExpr     from,
                Expression<?, String> where,
                Metadata      returnMetadata,
                List<Column>  returnColumns) {
    super(context, "Delete",
          of("deleteTableAlias", new Esql<>(context, deleteTableAlias)),
          of("tables",           from),
          of("where",            where),
          of("metadata",         returnMetadata),
          of("columns",          new ColumnList(context, returnColumns)));
  }

  public Delete(Delete other) {
    super(other);
  }

  @SafeVarargs
  public Delete(Delete other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public Delete copy() {
    return new Delete(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public Delete copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new Delete(this, value, children);
  }

  @Override
  public Delete filter(Filter filter, boolean firstFilter, EsqlPath path) {
    Select.FilterResult result = Select.filter(tables(), filter, firstFilter, where());
    if (result != null) {
      return new Delete(context,
                        deleteTableAlias(),
                        result.tables(),
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

  public String deleteTableAlias() {
    return childValue("deleteTableAlias");
  }
}