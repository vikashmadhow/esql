/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;

import static ma.vi.base.string.Escape.escapeJsonString;
import static ma.vi.esql.parser.Translatable.Target.JSON;

/**
 * The logical inverse (not) operator in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Not extends SingleSubExpression {
  public Not(Context context, Expression<?> expr) {
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
  public String translate(Target target) {
    switch (target) {
      case JSON, JAVASCRIPT -> {
        String e = "!" + expr().translate(target);
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
          return "not " + expr().translate(target);
        }
      }
      default -> {
        return "not " + expr().translate(target);
      }
    }
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    st.append("not ");
    expr()._toString(st, level, indent);
  }
}