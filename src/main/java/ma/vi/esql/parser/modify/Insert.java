/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.modify;

import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.Esql;
import ma.vi.esql.parser.QueryUpdate;
import ma.vi.esql.parser.Restriction;
import ma.vi.esql.parser.TranslationException;
import ma.vi.esql.parser.define.Metadata;
import ma.vi.esql.parser.query.*;
import ma.vi.esql.type.Type;
import ma.vi.base.tuple.T2;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.joining;
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
  public QueryTranslation translate(Target target) {
    StringBuilder st = new StringBuilder("insert into ");
    TableExpr table = tables();
    if (!(table instanceof SingleTableExpr)) {
      throw new TranslationException("Insert only works with single tables. A " + table.getClass().getSimpleName()
          + " was found instead.");
    }
    st.append(Type.dbName(((SingleTableExpr)table).tableName(), target));

    List<String> fields = fields();
    if (fields != null && !fields.isEmpty()) {
      st.append(fields.stream()
                      .map(f -> '"' + f + '"')
                      .collect(joining(", ", "(", ")")));
    }

    // output clause for SQL Server if specified
    QueryTranslation q = null;
    if (target == Target.SQLSERVER && columns() != null) {
      st.append(" output ");
      q = constructResult(st, target, "inserted", true, true);
    }

    List<InsertRow> rows = rows();
    if (rows != null && !rows.isEmpty()) {
      st.append(rows.stream()
                    .map(row -> row.translate(target))
                    .collect(joining(", ", " values", "")));

    } else if (defaultValues()) {
      st.append(" default values");

    } else {
      st.append(' ').append(select().translate(target, false).statement);
    }

    if (target == Target.POSTGRESQL && columns() != null) {
      st.append(" returning ");
      q = constructResult(st, target, null, true, true);
    }

    if (q == null) {
      return new QueryTranslation(st.toString(), emptyList(), emptyMap(), emptyList(), emptyMap());
    } else {
      return new QueryTranslation(st.toString(), q.columns, q.columnToIndex,
          q.resultAttributeIndices, q.resultAttributes);
    }
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