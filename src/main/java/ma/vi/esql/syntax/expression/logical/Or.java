/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.logical;

import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.comparison.ComparisonOperator;
import ma.vi.esql.syntax.expression.Expression;

import java.util.Map;

import static ma.vi.base.string.Escape.escapeJsonString;
import static ma.vi.esql.syntax.Translatable.Target.JSON;

/**
 * Logical or operator in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Or extends ComparisonOperator {
  public Or(Context context,
            Expression<?, String> expr1,
            Expression<?, String> expr2) {
    super(context, "or", expr1, expr2);
  }

  public Or(Or other) {
    super(other);
  }

  @Override
  public Or copy() {
    return new Or(this);
  }

  @Override
  protected String trans(Target target, EsqlPath path, Map<String, Object> parameters) {
    switch (target) {
      case JSON, JAVASCRIPT -> {
        String e = expr1().translate(target, path.add(expr1()), parameters) + " || " + expr2().translate(target, path.add(expr2()), parameters);
        return target == JSON ? '"' + escapeJsonString(e) + '"' : e;
      }
      case SQLSERVER -> {
        if (path.ancestor("on") == null
         && path.ancestor("where") == null
         && path.ancestor("having") == null) {
          /*
           * For SQL Server, boolean expressions outside of where and having
           * clauses are not allowed and we simulate it with bitwise operations.
           */
          return "cast(" + expr1().translate(target, path.add(expr1()), parameters) + " | " + expr2().translate(target, path.add(expr2()), parameters) + " as bit)";
        } else {
          return super.trans(target, path, parameters);
        }
      }
      default -> {
        return super.trans(target, path, parameters);
      }
    }
  }
}
