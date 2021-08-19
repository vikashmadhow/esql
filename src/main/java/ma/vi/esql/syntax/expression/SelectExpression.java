/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression;

import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.query.Column;
import ma.vi.esql.syntax.query.Order;
import ma.vi.esql.syntax.query.Select;
import ma.vi.esql.semantic.type.Type;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.joining;

/**
 * A single-column, single-row select in a column list.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class SelectExpression extends Expression<Select, String> {
  public SelectExpression(Context context, Select select) {
    super(context, select);
  }

  public SelectExpression(SelectExpression other) {
    super(other);
  }

  @Override
  public SelectExpression copy() {
    if (!copying()) {
      try {
        copying(true);
        return new SelectExpression(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public Type type() {
    return select().columns().get(0).type();
  }

  @Override
  protected String trans(Target target, Map<String, Object> parameters) {
    if (target == Target.ESQL) {
      Select sel = select();
      StringBuilder st = new StringBuilder();
      st.append("(from ").append(sel.tables().translate(target, parameters)).append(" select ");
      if (sel.distinct()) {
        st.append("distinct ");
        List<Expression<?, String>> distinctOn = sel.distinctOn();
        if (distinctOn != null && !distinctOn.isEmpty()) {
          st.append("on (")
            .append(distinctOn.stream().map(e -> e.translate(target, parameters)).collect(joining(", ")))
            .append(") ");
        }
      }

      Column col = sel.columns().get(0);
      if (col.alias() != null) {
        st.append(col.alias()).append(':');
      }
      st.append(col.expr().translate(target, parameters));

      if (sel.where() != null) {
        st.append(" where ").append(sel.where().translate(target, parameters));
      }
      if (sel.orderBy() != null && !sel.orderBy().isEmpty()) {
        st.append(" order by ")
          .append(sel.orderBy().stream()
                     .map(e -> e.translate(target, parameters))
                     .collect(joining(", ")));
      }
      if (sel.offset() != null) {
        st.append(" offset ").append(sel.offset().translate(target, parameters));
      }
      if (sel.limit() != null) {
        st.append(" limit ").append(sel.limit().translate(target, parameters));
      }
      st.append(')');
      return st.toString();
    } else {
      return '(' + select().translate(target, parameters).statement + ')';
    }
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    st.append("(from ");
    Select sel = select();
    sel.from()._toString(st, level, indent);
    st.append(" select ");
    Boolean distinct = sel.distinct();
    if (distinct != null && distinct) {
      st.append("distinct ");
      if (sel.distinctOn() != null && !sel.distinctOn().isEmpty()) {
        st.append('(');
        boolean first = true;
        for (Expression<?, String> e: sel.distinctOn()) {
          if (first) { first = false; }
          else       { st.append(", "); }
          e._toString(st, level, indent);
        }
        st.append(") ");
      }
    }
    sel.columns().get(0)._toString(st, level, indent);
    if (sel.where() != null) {
      st.append(" where ");
      sel.where()._toString(st, level, indent);
    }
    if (sel.orderBy() != null && !sel.orderBy().isEmpty()) {
      boolean first = true;
      st.append(" order by ");
      for (Order o: sel.orderBy()) {
        if (first) { first = false;   }
        else       { st.append(", "); }
        o._toString(st, level, indent);
      }
    }
    if (sel.offset() != null) {
      st.append(" offset ");
      sel.offset()._toString(st, level, indent);
    }
    st.append(')');
  }

  public Select select() {
    return value;
  }
}
