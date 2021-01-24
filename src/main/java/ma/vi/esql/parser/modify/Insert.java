/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.modify;

import ma.vi.base.tuple.T2;
import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.Esql;
import ma.vi.esql.parser.QueryUpdate;
import ma.vi.esql.parser.Restriction;
import ma.vi.esql.parser.define.Metadata;
import ma.vi.esql.parser.query.Column;
import ma.vi.esql.parser.query.Select;
import ma.vi.esql.parser.query.SingleTableExpr;

import java.util.List;

import static ma.vi.base.tuple.T2.of;

/**
 * Insert statement.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Insert extends QueryUpdate {
  public Insert(Context context,
                SingleTableExpr table,
                List<String> fields,
                List<InsertRow> rows,
                boolean defaultValues,
                Select select,
                Metadata metadata,
                List<Column> returning) {
    super(context, "Insert",
        of("tables", table),
        of("fields", new Esql<>(context, fields)),
        of("rows", new Esql<>(context, rows)),
        of("defaultValues", new Esql<>(context, defaultValues)),
        of("select", select),
        of("metadata", metadata),
        of("columns", new Esql<>(context, returning)));
  }

  public Insert(Insert other) {
    super(other);
  }

  @Override
  public Insert copy() {
    if (!copying()) {
      try {
        copying(true);
        return new Insert(this);
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

  @Override
  public T2<Boolean, String> restrict(Restriction restriction,
                                      String targetAlias,
                                      boolean ignoreHiddenFields,
                                      boolean followSubSelect) {
    return select() != null ? select().restrict(restriction, targetAlias, ignoreHiddenFields, followSubSelect)
                            : T2.of(false, null);
  }

  public List<String> fields() {
    return childValue("fields");
  }

  public List<InsertRow> rows() {
    return childValue("rows");
  }

  public Boolean defaultValues() {
    return childValue("defaultValues");
  }

  public Select select() {
    return child("select");
  }
}