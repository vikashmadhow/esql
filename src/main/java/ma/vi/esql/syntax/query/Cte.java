/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.query;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.*;
import ma.vi.esql.syntax.expression.ColumnRef;
import ma.vi.esql.semantic.type.Selection;
import ma.vi.esql.semantic.type.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.joining;

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
          T2.of("fields", fields == null ? null : new Esql<>(context, fields)),
          T2.of("query", query));

    T2.of("tables", query.tables(), false);
    T2.of("metadata", query.metadata(), false);

    if (fields != null && !fields.isEmpty()) {
      /*
       * Set the alias of columns to the name of the corresponding field
       */
      List<QueryUpdate> queries = new ArrayList<>();
      if (query instanceof CompositeSelects) {
        queries.addAll(((CompositeSelects)query).selects());
      } else {
        queries.add(query);
      }
      for (QueryUpdate q: queries) {
        if (fields.size() != q.columns().size()) {
          throw new SyntaxException("Number of fields for CTE is " + fields.size()
                                  + " while number of columns in the query for the CTE is " + q.columns().size());
        }
        for (int i = 0; i < fields.size(); i++) {
          Column col = q.columns().get(i);
          col.alias(fields.get(i));
        }
      }
    }
    child("columns", query.columnsAsEsql(), false);
  }

  public Cte(Cte other) {
    super(other);
  }

  @Override
  public Cte copy() {
    return new Cte(this);
  }

  /**
   * Get the type for this CTE and register it in the context
   * for use when translating the WITH query that this CTE is
   * part of.
   */
  @Override
  public Selection type() {
    if (type == null) {
      type = query().type();
      type.name(name());

      /*
       * rename columns using CTE field list.
       */
      List<String> fields = fields();
      if (fields != null && !fields.isEmpty()) {
        List<Column> typeCols = type.columns();
        if (typeCols.size() != fields.size()) {
          throw new TranslationException("CTE " + name() + " has different number of fields and columns. Fields "
                                       + "defined are [" + String.join(", ", fields) + "] while columns "
                                       + "defined are [" + typeCols.stream().map(Column::alias).collect(joining(", ")) + ']');
        }
        List<Column> typeFields = new ArrayList<>();
        for (int i = 0; i < fields.size(); i++) {
//          typeFields.get(i).alias(fields.get(i));
          Column col = typeCols.get(i).copy();
          Type type = col.type();
          col.type(type);
          col.expr(new ColumnRef(context, name(), fields.get(i)));
          col.alias(fields.get(i));
          typeFields.add(col);
        }
        type = new Selection(typeFields, query().from());
      }
      context.type(name(), type);
    }
    return type;
  }

  @Override
  public boolean modifying() {
    return query().modifying();
  }

  @Override
  public QueryTranslation trans(Target target, EsqlPath path, Map<String, Object> parameters) {
    /*
     * translate query and surround by CTE fields definition
     */
    QueryTranslation q = query().translate(target, parameters);
    String s = '"' + name() + '"'
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

  private Selection type;
}