/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.modify;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.define.Metadata;
import ma.vi.esql.syntax.query.*;

import java.util.List;

import static ma.vi.base.tuple.T2.of;

/**
 * Insert statement.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Insert extends QueryUpdate {
  public Insert(Context         context,
                SingleTableExpr table,
                List<String>    fields,
                List<InsertRow> rows,
                boolean         defaultValues,
                Select          select,
                Metadata        metadata,
                List<Column>    returning) {
    super(context, "Insert",
          of("tables",        table),
//          of("fields",        new Esql<>(context, "fields", fields.stream().map(f -> new Esql<>(context, f)).toList())),
          of("fields",        new Esql<>(context, fields)),
          of("rows",          new Esql<>(context, "rows", rows)),
          of("defaultValues", new Esql<>(context, defaultValues)),
          of("select",        select),
          of("metadata",      metadata),
          of("columns",       new ColumnList(context, returning)));
  }

  public Insert(Insert other) {
    super(other);
  }

  public Insert(Insert other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public Insert copy() {
    return new Insert(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public Insert copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new Insert(this, value, children);
  }

  @Override
  public boolean modifying() {
    return true;
  }

//  @Override
//  public T2<Boolean, String> restrict(Restriction restriction,
//                                      String targetAlias,
//                                      boolean ignoreHiddenFields,
//                                      boolean followSubSelect) {
//    return select() != null ? select().restrict(restriction, targetAlias, ignoreHiddenFields, followSubSelect)
//                            : T2.of(false, null);
//  }

  public List<String> fields() {
    return childValue("fields");
  }

  public List<InsertRow> rows() {
    return child("rows").children();
  }

  public Boolean defaultValues() {
    return childValue("defaultValues");
  }

  public Select select() {
    return child("select");
  }
}