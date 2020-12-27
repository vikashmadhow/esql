/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.query;

import ma.vi.base.tuple.T2;
import ma.vi.esql.parser.*;
import ma.vi.esql.type.Selection;

import java.util.List;

import static java.util.stream.Collectors.joining;
import static ma.vi.base.tuple.T2.of;

/**
 * A common table expression is a part of a With query.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Cte extends QueryUpdate {
  public Cte(Context context,
             String name,
             List<String> fields,
             QueryUpdate query) {
    super(context, name,
          of("fields", fields == null ? null : new Esql<>(context, fields)),
          of("query", query));

    child("columns", query.columnsAsEsql(), false);
    child("tables", query.tables(), false);
    child("metadata", query.metadata(), false);
  }

  public Cte(Cte other) {
    super(other);
  }

  @Override
  public Cte copy() {
    if (!copying()) {
      try {
        copying(true);
        return new Cte(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  /**
   * Get the type for this CTE and register it in the context
   * for use when translating the WITH query that this CTE is
   * part of.
   */
  @Override
  public Selection type() {
    Selection type = query().type();

    // rename columns using CTE field list
    List<String> fields = fields();
    if (fields != null && !fields.isEmpty()) {
      List<Column> typeFields = type.columns();
      if (typeFields.size() != fields.size()) {
        throw new TranslationException("CTE " + name() + " has different number of fields and columns. Fields " +
            "defined are [" + String.join(", ", fields) + "] while columns " +
            "defined are [" + typeFields.stream().map(Column::alias).collect(joining(", ")) + ']');
      }
      for (int i = 0; i < fields.size(); i++) {
        typeFields.get(i).alias(fields.get(i));
      }
    }
    return type;
  }

  @Override
  public boolean modifying() {
    return query().modifying();
  }

  @Override
  public QueryTranslation translate(Target target) {
    // translate query and surround by CTE fields definition
    QueryTranslation q = query().translate(target);
    String s = name()
        + (fields() == null
           ? ""
           : fields().stream()
                     .map(f -> '"' + f + '"')
                     .collect(joining(", ", "(", ")")))
        + " as (" + q.statement + ')';

    return new QueryTranslation(s, q.columns, q.columnToIndex,
        q.resultAttributeIndices, q.resultAttributes);
  }

  @Override
  public T2<Boolean, String> restrict(Restriction restriction,
                                      String targetAlias,
                                      boolean ignoreHiddenFields,
                                      boolean followSubSelect) {
    return query().restrict(restriction, targetAlias, ignoreHiddenFields, followSubSelect);
  }

  @Override
  public String name() {
    return value;
  }

  public List<String> fields() {
    return childValue("fields");
  }

  public QueryUpdate query() {
    return child("query");
  }
}