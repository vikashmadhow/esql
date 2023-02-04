/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.query;

import ma.vi.base.tuple.T2;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import org.pcollections.PMap;

import java.util.List;

/**
 * Selects combined with set operators: union (all), intersect and except.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class CompositeSelect extends Select {
  public CompositeSelect(Context      context,
                         String       operator,
                         List<Select> selects) {
    super(context,
          null,
          selects.get(0).unfiltered(),
          selects.get(0).explicit(),
          selects.get(0).distinct(),
          selects.get(0).distinctOn(),
          selects.get(0).columns(),
          selects.get(0).tables(),
          selects.get(0).where(),
          selects.get(0).groupBy(),
          selects.get(0).having(),
          selects.get(0).orderBy(),
          selects.get(0).offset(),
          selects.get(0).limit(),
          T2.of("operator", new Esql<>(context, operator)),
          T2.of("composedOf", new Esql<>(context, "ComposedOf", selects)));
  }

  public CompositeSelect(CompositeSelect other) {
    super(other);
  }

  @SafeVarargs
  public CompositeSelect(CompositeSelect other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public CompositeSelect copy() {
    return new CompositeSelect(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public CompositeSelect copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new CompositeSelect(this, value, children);
  }

  @Override
  public QueryTranslation trans(Target target, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env) {
    boolean first = true;
    StringBuilder st = new StringBuilder();
    QueryTranslation q = null;
    for (Select select: selects()) {
      if (first) {
        first = false;
      } else {
        String operator = operator();
        if (parameters.containsKey("recursive")
         && target == Target.SQLSERVER) {
          /*
           * SQL Server only support union all as the operator in the first CTE
           * of a recursive WITH query.
           */
          operator = "union all";
        }
        st.append(' ').append(operator).append(' ');
      }
      QueryTranslation trans = select.translate(target, null, path.add(select),
                                                parameters.plus("addAttributes", parameters.getOrDefault("addAttributes", true))
                                                          .plus("optimiseAttributesLoading", false), null);
      st.append(trans.translation());
      if (q == null) {
        q = trans;
      }
    }
    return new QueryTranslation(this,
                                st.toString(),
                                q.columns(),
                                q.resultAttributes());
  }

  public String operator() {
    return childValue("operator");
  }

  public List<Select> selects() {
    return child("composedOf").children();
  }
}