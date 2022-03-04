/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression;

import ma.vi.base.tuple.T2;
import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.semantic.type.Column;
import ma.vi.esql.syntax.query.Order;
import ma.vi.esql.syntax.query.Select;
import org.pcollections.PMap;

import java.util.List;

import static java.util.stream.Collectors.joining;
import static ma.vi.esql.translation.SqlServerTranslator.DONT_ADD_IIF;

/**
 * A single-column, single-row select in a column list.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class SelectExpression extends Expression<String, String> {
  public SelectExpression(Context context, Select select) {
    super(context, "SelectExpression", T2.of("select", select));
  }

  public SelectExpression(SelectExpression other) {
    super(other);
  }

  @SafeVarargs
  public SelectExpression(SelectExpression other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public SelectExpression copy() {
    return new SelectExpression(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public SelectExpression copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new SelectExpression(this, value, children);
  }

  @Override
  public Type computeType(EsqlPath path) {
    Column col = select().columns().get(0);
    return col.computeType(path.add(col));
  }

  @Override
  protected String trans(Target target, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env) {
    if (target == Target.ESQL) {
      Select sel = select();
      StringBuilder st = new StringBuilder();
      st.append("(from ").append(sel.tables().translate(target, esqlCon, path.add(sel.tables()), parameters, env)).append(" select ");
      if (sel.distinct()) {
        st.append("distinct ");
        List<Expression<?, String>> distinctOn = sel.distinctOn();
        if (distinctOn != null && !distinctOn.isEmpty()) {
          st.append("on (")
            .append(distinctOn.stream().map(e -> e.translate(target, esqlCon, path.add(e), parameters, env)).collect(joining(", ")))
            .append(") ");
        }
      }

      Column col = sel.columns().get(0);
      if (col.name() != null) {
        st.append(col.name()).append(':');
      }
      st.append(col.expression().translate(target, esqlCon, path.add(col.expression()), parameters, env));

      if (sel.where() != null) {
        st.append(" where ").append(sel.where().translate(target, esqlCon, path.add(sel.where()), parameters, env));
      }
      if (sel.orderBy() != null && !sel.orderBy().isEmpty()) {
        st.append(" order by ")
          .append(sel.orderBy().stream()
                     .map(e -> e.translate(target, esqlCon, path.add(e), parameters, env))
                     .collect(joining(", ")));
      }
      if (sel.offset() != null) {
        st.append(" offset ").append(sel.offset().translate(target, esqlCon, path.add(sel.offset()), parameters, env));
      }
//      if (sel.limit() != null) {
//        st.append(" limit ").append(sel.limit().translate(target, esqlCon, path.add(sel.limit()), parameters, env));
//      }
      st.append(')');
      return st.toString();
    } else {
      return "(" + select().translate(target, esqlCon, path.add(select()), parameters.plusAll(DONT_ADD_IIF), env).translation() + ")";
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
    return child("select");
  }
}
