/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression;

import ma.vi.esql.syntax.Context;

import java.util.Map;

import static ma.vi.base.string.Escape.escapeJsonString;
import static ma.vi.esql.syntax.Translatable.Target.JSON;

/**
 * Logical and operator in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class LogicalAnd extends RelationalOperator {
  public LogicalAnd(Context context,
                    Expression<?, String> expr1,
                    Expression<?, String> expr2) {
    super(context, "and", expr1, expr2);
  }

  public LogicalAnd(LogicalAnd other) {
    super(other);
  }

  @Override
  public LogicalAnd copy() {
    if (!copying()) {
      try {
        copying(true);
        return new LogicalAnd(this);
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
        String e = expr1().translate(target, parameters) + " && " + expr2().translate(target, parameters);
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
          return "cast(" + expr1().translate(target, parameters) + " & " + expr2().translate(target, parameters) + " as bit)";
        } else {
          return super.trans(target, parameters);
        }
      }
      default -> {
        return super.trans(target, parameters);
      }
    }
  }
}
