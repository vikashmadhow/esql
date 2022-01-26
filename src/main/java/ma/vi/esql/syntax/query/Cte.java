/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.query;

import ma.vi.base.tuple.T2;
import ma.vi.esql.semantic.type.Relation;
import ma.vi.esql.semantic.type.Selection;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.SyntaxException;
import ma.vi.esql.syntax.expression.ColumnRef;
import ma.vi.esql.translation.TranslationException;
import org.pcollections.PMap;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

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
    super(context, "CTE",
          Stream.concat(
            Stream.of(
              T2.of("name", new Esql<>(context, name)),
              T2.of("fields", fields == null ? null : new Esql<>(context, fields))),
            Stream.of(queryColumns(query, fields))).toArray(T2[]::new));
  }

  public Cte(Cte other) {
    super(other);
  }

  @SafeVarargs
  public Cte(Cte other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public Cte copy() {
    return new Cte(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public Cte copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new Cte(this, value, children);
  }

  private static T2<String, Esql<?, ?>>[] queryColumns(QueryUpdate query, List<String> fields) {
    if (fields != null && !fields.isEmpty()) {
      /*
       * Set the alias of columns to the name of the corresponding field
       */
      if (query instanceof CompositeSelects com) {
        List<Select> aliased = new ArrayList<>();
        List<Select> selects = com.selects();
        for (Select q: selects) {
          aliased.add(renameColumns(q, fields));
        }
        query = new CompositeSelects(com.context, com.operator(), aliased);
      } else {
        query = renameColumns(query, fields);
      }
    }
    return new T2[]{
        T2.of("query",    query),
        T2.of("tables",   query.tables()),
        T2.of("metadata", query.metadata()),
        T2.of("columns",  query.columnList())};
  }

  private static <Q extends QueryUpdate> Q renameColumns(Q query, List<String> fields) {
    if (fields.size() != query.columns().size()) {
      throw new SyntaxException("Number of fields for CTE is " + fields.size()
                              + " while number of columns in the query for the CTE is " + query.columns().size());
    }
    List<Column> cols = query.columns();
    List<Column> columns = new ArrayList<>();
    for (int i = 0; i < fields.size(); i++) {
      columns.add(cols.get(i).name(fields.get(i)));
    }
    return query.set("columns", new ColumnList(query.context, columns));
  }

  /**
   * Get the type for this CTE and register it in the context
   * for use when translating the WITH query that this CTE is
   * part of.
   */
  @Override
  public Selection computeType(EsqlPath path) {
    if (type == Types.UnknownType) {
      type = query().computeType(path.add(query()));
      type.name(name());

      /*
       * Rename columns using CTE field list.
       */
      List<String> fields = fields();
      if (fields != null && !fields.isEmpty()) {
        List<T2<Relation, Column>> typeCols = ((Selection)type).columns();
        if (typeCols.size() != fields.size()) {
          throw new TranslationException("CTE " + name() + " has different number of fields and columns. Fields "
                                       + "defined are [" + String.join(", ", fields) + "] while columns "
                                       + "defined are [" + typeCols.stream().map(c -> c.b.name()).collect(joining(", ")) + ']');
        }
        List<Column> typeFields = new ArrayList<>();
        for (int i = 0; i < fields.size(); i++) {
          Column col = typeCols.get(i).b();
          Type type = col.computeType(path.add(col));
//          typeFields.add(col.type(type)
//                            .expression(new ColumnRef(context, name(), fields.get(i)))
//                            .name(fields.get(i)));
          col.type(type);
          typeFields.add(col.expression(new ColumnRef(context, name(), fields.get(i)))
                            .name(fields.get(i)));
        }
        type = new Selection(typeFields, type.attributesList(), query().from(), name());
      }
      context.type(name(), type);
    }
    return (Selection)type;
  }

  @Override
  public boolean modifying() {
    return query().modifying();
  }

  @Override
  public QueryTranslation trans(Target target, EsqlPath path, PMap<String, Object> parameters) {
    /*
     * Translate query and surround by CTE fields definition.
     */
    QueryTranslation q = query().translate(target, path, parameters);
    String s = '"' + name() + '"'
             + (fields() == null
                ? ""
                : fields().stream()
                          .map(f -> '"' + f + '"')
                          .collect(joining(", ", "(", ")")))
             + " as (" + q.translation() + ')';
    return new QueryTranslation(this, s,
                                q.columns(),
                                q.resultAttributeIndices(),
                                q.resultAttributes());
  }

//  @Override
//  public T2<Boolean, String> restrict(Restriction restriction,
//                                      String targetAlias,
//                                      boolean ignoreHiddenFields,
//                                      boolean followSubSelect) {
//    return query().restrict(restriction, targetAlias, ignoreHiddenFields, followSubSelect);
//  }

  @Override
  public String name() {
    return childValue("name");
  }

  public List<String> fields() {
    return childValue("fields");
  }

  public QueryUpdate query() {
    return child("query");
  }
}