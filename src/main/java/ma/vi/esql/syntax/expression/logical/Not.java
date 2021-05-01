/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.logical;

import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.expression.SingleSubExpression;

import java.util.Map;

import static ma.vi.base.string.Escape.escapeJsonString;
import static ma.vi.esql.syntax.Translatable.Target.JSON;

/**
 * The logical inverse (not) operator in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Not extends SingleSubExpression {
  public Not(Context context, Expression<?, String> expr) {
    super(context, expr);
  }

  public Not(Not other) {
    super(other);
  }

  @Override
  public Not copy() {
    if (!copying()) {
      try {
        copying(true);
        return new Not(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  protected String trans(Target target, Map<String, Object> parameters) {
    switch (target) {
      case JSON, JAVASCRIPT -> {
        String e = "!" + expr().translate(target, parameters);
        return target == JSON ? '"' + escapeJsonString(e) + '"' : e;
      }
      case SQLSERVER -> {
        if (ancestor("on") == null
         && ancestor("where") == null
         && ancestor("having") == null) {
          /*
           * For SQL Server, boolean expressions outside of where and having
           * clauses are not allowed and we simulate it with bitwise operations.
           */
          return "cast(~(" + expr() + ") as bit)";
        } else {
          return "not " + expr().translate(target, parameters);
        }
      }
      default -> {
        return "not " + expr().translate(target, parameters);
      }
    }
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    st.append("not ");
    expr()._toString(st, level, indent);
  }
}