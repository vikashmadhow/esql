/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.modify;

import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.Esql;
import ma.vi.esql.parser.QueryUpdate;
import ma.vi.esql.parser.define.Metadata;
import ma.vi.esql.parser.expression.Expression;
import ma.vi.esql.parser.query.AbstractJoinTableExpr;
import ma.vi.esql.parser.query.Column;
import ma.vi.esql.parser.query.SingleTableExpr;
import ma.vi.esql.parser.query.TableExpr;

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
          of("tables", from),
          of("where", where),
          of("metadata", returnMetadata),
          of("columns", new Esql<>(context, returnColumns)));
  }

  public Delete(Delete other) {
    super(other);
  }

  @Override
  public Delete copy() {
    if (!copying()) {
      try {
        copying(true);
        return new Delete(this);
      } finally {
        copying(false);
      }
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

  public static SingleTableExpr findSingleTable(AbstractJoinTableExpr join,
                                                String alias) {
    if (join.left() instanceof SingleTableExpr
     && alias.equals(((SingleTableExpr)join.left()).alias())) {
      return (SingleTableExpr)join.left();

    } else if (join.right() instanceof SingleTableExpr
            && alias.equals(((SingleTableExpr)join.right()).alias())) {
      return (SingleTableExpr)join.right();

    } else if (join.left() instanceof AbstractJoinTableExpr) {
      return findSingleTable((AbstractJoinTableExpr)join.left(), alias);

    } else if (join.right() instanceof AbstractJoinTableExpr) {
      return findSingleTable((AbstractJoinTableExpr)join.right(), alias);

    } else {
      return null;
    }
  }
}