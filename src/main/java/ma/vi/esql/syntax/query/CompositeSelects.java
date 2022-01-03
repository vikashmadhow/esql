/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.query;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;

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
          selects.get(0).limit(),
          T2.of("operator", new Esql<>(context, operator)),
          T2.of("composedOf", new Esql<>(context, "ComposedOf", selects)));

//    // count columns of all selects: they should have the same number
//    int columnsCount = -1;
//    for (Select select: selects) {
//      if (columnsCount == -1) {
//        columnsCount = select.columns().size();
//      } else if (columnsCount != select.columns().size()) {
//        throw new TranslationException("The number of columns is not consistent over all selects in the composite " +
//            "select by " + operator + ". The number of columns in the first select is " + columnsCount + " while " +
//            "a select with " + select.columns().size() + " was also found in the same statement.");
//      }
//    }
//
//    // merge metadata over all selects and copy metadata for this composite select
//    mergeMetadata(selects);
//    metadata(selects.get(0).metadata() == null ? null : selects.get(0).metadata().copy());
//
//    // Merge column metadata and keep a copy for the columns of this composite
//    for (int i = 0; i < columnsCount; i++) {
//      List<Column> columns = new ArrayList<>();
//      for (Select select: selects) {
//        columns.add(select.columns().get(i));
//      }
//      mergeMetadata(columns);
//    }
////    columns(selects.get(0).columnsAsEsql() == null ? null : selects.get(0).columnsAsEsql().copy().value);
//    columnList(selects.get(0).columnList());
  }

  public CompositeSelects(CompositeSelects other) {
    super(other);
  }

  @SafeVarargs
  public CompositeSelects(CompositeSelects other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public CompositeSelects copy() {
    return new CompositeSelects(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public CompositeSelects copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new CompositeSelects(this, value, children);
  }

  @Override
  public QueryTranslation trans(Target target, EsqlPath path, Map<String, Object> parameters) {
    boolean first = true;
    StringBuilder st = new StringBuilder();
    QueryTranslation q = null;
    for (Select select: selects()) {
      if (first) {
        first = false;
      } else {
        st.append(' ').append(operator()).append(' ');
      }
      Map<String, Object> params = new HashMap<>();
      params.put("addAttributes", parameters.getOrDefault("addAttributes", true));
      params.put("optimiseAttributesLoading", false);
      QueryTranslation trans = select.translate(target, path.add(select), params);
      st.append(trans.statement());
      if (q == null) {
        q = trans;
      }
    }
    return new QueryTranslation(st.toString(), q.columns(), q.columnToIndex(),
                                q.resultAttributeIndices(), q.resultAttributes());
  }

//  /**
//   * Merge (union) metadata over several selects or columns (metadata containers).
//   */
//  private static Metadata mergeMetadata(List<? extends MetadataContainer<?, ?>> containers) {
//    // first, split each set of metadata attributes into a map for faster comparison
//    Map<MetadataContainer<?, ?>, Map<String, Expression<?, String>>> containerAttrs = new HashMap<>();
//    for (MetadataContainer<?, ?> container: containers) {
//      Map<String, Expression<?, String>> attrMap = new HashMap<>();
//      containerAttrs.put(container, attrMap);
//      if (container.metadata() != null) {
//        for (Attribute a: container.metadata().attributes().values()) {
//          attrMap.put(a.name(), a.attributeValue());
//        }
//      }
//    }
//
//    // go through each container and create the union of the attributes by attribute names
//    Map<String, Expression<?, String>> combinedAttr = new HashMap<>();
//    for (Map<String, Expression<?, String>> containerAttr: containerAttrs.values()) {
//      for (String attrName: containerAttr.keySet()) {
//        if (!combinedAttr.containsKey(attrName)) {
//          combinedAttr.put(attrName, containerAttr.get(attrName));
//        }
//      }
//    }
//
//    // Using the combined attributes name, add missing attributes to each metadata container;
//    // at the end of this all metadata containers will have the same named attributes
//    for (Map<String, Expression<?, String>> containerAttr: containerAttrs.values()) {
//      for (String attrName: combinedAttr.keySet()) {
//        if (!containerAttr.containsKey(attrName)) {
//          containerAttr.put(attrName, combinedAttr.get(attrName));
//        }
//      }
//    }
//
//    // repack select attributes into metadata (list of attributes)
//    for (Map.Entry<MetadataContainer<?, ?>, Map<String, Expression<?, String>>> e: containerAttrs.entrySet()) {
//      MetadataContainer<?, ?> container = e.getKey();
//      Map<String, Expression<?, String>> containerAttr = e.getValue();
//      List<Attribute> attrs = new ArrayList<>();
//      for (Map.Entry<String, Expression<?, String>> a: containerAttr.entrySet()) {
//        attrs.add(new Attribute(context, a.getKey(), a.getValue()));
//      }
//      container.metadata(new Metadata(context, attrs));
//    }
//  }

  public String operator() {
    return childValue("operator");
  }

  public List<Select> selects() {
    return child("composedOf").children();
  }

//  @Override
//  public T2<Boolean, String> restrict(Restriction restriction,
//                                      String targetAlias,
//                                      boolean ignoreHiddenFields,
//                                      boolean followSubSelect) {
//    boolean joined = false;
//    String alias = null;
//    for (Select sel: selects()) {
//      T2<Boolean, String> res = sel.restrict(restriction, targetAlias, ignoreHiddenFields, followSubSelect);
//      joined |= res.a;
//      alias = alias == null ? res.b : alias;
//    }
//    return T2.of(joined, alias);
//  }
}