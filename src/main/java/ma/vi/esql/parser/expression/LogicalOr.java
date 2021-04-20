/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;

import java.util.Map;

import static ma.vi.base.string.Escape.escapeJsonString;
import static ma.vi.esql.parser.Translatable.Target.JSON;

/**
 * Logical or operator in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class LogicalOr extends RelationalOperator {
  public LogicalOr(Context context,
                   Expression<?, String> expr1,
                   Expression<?, String> expr2) {
    super(context, "or", expr1, expr2);
  }

  public LogicalOr(LogicalOr other) {
    super(other);
  }

  @Override
  public LogicalOr copy() {
    if (!copying()) {
      try {
        copying(true);
        return new LogicalOr(this);
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
        String e = expr1().translate(target, parameters) + " || " + expr2().translate(target, parameters);
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
          return "cast(" + expr1().translate(target, parameters) + " | " + expr2().translate(target, parameters) + " as bit)";
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
