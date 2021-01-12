/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;

import static ma.vi.base.string.Escape.escapeJsonString;
import static ma.vi.esql.parser.Translatable.Target.JSON;

/**
 * Logical or operator in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class LogicalOr extends RelationalOperator {
  public LogicalOr(Context context, Expression<?> expr1, Expression<?> expr2) {
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
  public String translate(Target target) {
    switch (target) {
      case JSON, JAVASCRIPT -> {
        String e = expr1().translate(target) + " || " + expr2().translate(target);
        return target == JSON ? '"' + escapeJsonString(e) + '"' : e;
      }
      case SQLSERVER -> {
        if (ancestor("where") == null
         && ancestor("having") == null) {
          /*
           * For SQL Server, boolean expressions outside of where and having
           * clauses are not allowed and we simulate it with bitwise operations.
           */
          return "cast(" + expr1().translate(target) + " | " + expr2().translate(target) + " as bit)";
        } else {
          return super.translate(target);
        }
      }
      default -> {
        return super.translate(target);
      }
    }
  }
}
