/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.query;

import ma.vi.base.tuple.T2;
import ma.vi.esql.parser.*;
import ma.vi.esql.parser.define.Attribute;
import ma.vi.esql.parser.define.Metadata;
import ma.vi.esql.parser.expression.Expression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Selects combined with set operators: union (all), intersect and except.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class CompositeSelects extends Select {
  public CompositeSelects(Context context, String operator, List<Select> selects) {
    super(context,
        null,
        selects.get(0).distinct(),
        selects.get(0).distinctOn(),
        selects.get(0).explicit(),
        selects.get(0).columns(),
        selects.get(0).tables() == null ? null : selects.get(0).tables().copy(),
        selects.get(0).where(),
        selects.get(0).groupBy(),
        selects.get(0).having(),
        selects.get(0).orderBy(),
        selects.get(0).offset(),
        selects.get(0).limit());

    value = operator;
    child("selects", new Esql<>(context, selects));

    // count columns of all selects: they should have the same number
    int columnsCount = -1;
    for (Select select: selects) {
      if (columnsCount == -1) {
        columnsCount = select.columns().size();
      } else if (columnsCount != select.columns().size()) {
        throw new TranslationException("The number of columns is not consistent over all selects in the composite " +
            "select by " + operator + ". The number of columns in the first select is " + columnsCount + " while " +
            "a select with " + select.columns().size() + " was also found in the same statement.");
      }
    }

    // merge metadata over all selects and copy metadata for this composite select
    mergeMetadata(selects);
    metadata(selects.get(0).metadata() == null ? null : selects.get(0).metadata().copy());

    // Merge column metadata and keep a copy for the columns of this composite
    for (int i = 0; i < columnsCount; i++) {
      List<Column> columns = new ArrayList<>();
      for (Select select: selects) {
        columns.add(select.columns().get(i));
      }
      mergeMetadata(columns);
    }
    columns(selects.get(0).columnsAsEsql() == null ? null : selects.get(0).columnsAsEsql().copy().value);
  }

  public CompositeSelects(CompositeSelects other) {
    super(other);
  }

  @Override
  public CompositeSelects copy() {
    if (!copying()) {
      try {
        copying(true);
        return new CompositeSelects(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  public QueryTranslation translate(Target target, boolean addAttributes,
                                    boolean optimiseAttributesLoading) {
    boolean first = true;
    StringBuilder st = new StringBuilder();
    QueryTranslation q = null;
    for (Select select: selects()) {
      if (first) {
        first = false;
      } else {
        st.append(' ').append(operator()).append(' ');
      }
      QueryTranslation trans = select.translate(target, addAttributes, false);
      st.append(trans.statement);
      if (q == null) {
        q = trans;
      }
    }
    return new QueryTranslation(st.toString(), q.columns, q.columnToIndex,
        q.resultAttributeIndices, q.resultAttributes);
  }

  /**
   * Merge (union) metadata over several selects or columns (metadata containers).
   */
  private void mergeMetadata(List<? extends MetadataContainer> containers) {
    // first, split each set of metadata attributes into a map for faster comparison
    Map<MetadataContainer, Map<String, Expression<?>>> containerAttrs = new HashMap<>();
    for (MetadataContainer container: containers) {
      Map<String, Expression<?>> attrMap = new HashMap<>();
      containerAttrs.put(container, attrMap);
      if (container.metadata() != null) {
        for (Attribute a: container.metadata().attributes().values()) {
          attrMap.put(a.name(), a.attributeValue());
        }
      }
    }

    // go through each container and create the union of the attributes by attribute names
    Map<String, Expression<?>> combinedAttr = new HashMap<>();
    for (Map<String, Expression<?>> containerAttr: containerAttrs.values()) {
      for (String attrName: containerAttr.keySet()) {
        if (!combinedAttr.containsKey(attrName)) {
          combinedAttr.put(attrName, containerAttr.get(attrName));
        }
      }
    }

    // Using the combined attributes name, add missing attributes to each metadata container;
    // at the end of this all metadata containers will have the same named attributes
    for (Map<String, Expression<?>> containerAttr: containerAttrs.values()) {
      for (String attrName: combinedAttr.keySet()) {
        if (!containerAttr.containsKey(attrName)) {
          containerAttr.put(attrName, combinedAttr.get(attrName));
        }
      }
    }

    // repack select attributes into metadata (list of attributes)
    for (Map.Entry<MetadataContainer, Map<String, Expression<?>>> e: containerAttrs.entrySet()) {
      MetadataContainer container = e.getKey();
      Map<String, Expression<?>> containerAttr = e.getValue();
      List<Attribute> attrs = new ArrayList<>();
      for (Map.Entry<String, Expression<?>> a: containerAttr.entrySet()) {
        attrs.add(new Attribute(context, a.getKey(), a.getValue()));
      }
      container.metadata(new Metadata(context, attrs));
    }
  }

  public List<Select> selects() {
    return childValue("selects");
  }

  public String operator() {
    return value;
  }

  @Override
  public T2<Boolean, String> restrict(Restriction restriction,
                                      String targetAlias,
                                      boolean ignoreHiddenFields,
                                      boolean followSubSelect) {
    boolean joined = false;
    String alias = null;
    for (Select sel: selects()) {
      T2<Boolean, String> res = sel.restrict(restriction, targetAlias, ignoreHiddenFields, followSubSelect);
      joined |= res.a;
      alias = alias == null ? res.b : alias;
    }
    return T2.of(joined, alias);
  }
}