/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.arithmetic;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.translation.Translatable;
import org.pcollections.PMap;

import static ma.vi.base.string.Escape.escapeJsonString;
import static ma.vi.esql.translation.Translatable.Target.JSON;

/**
 * The exponentiation operator (^) in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Exponentiation extends ArithmeticOperator {
  public Exponentiation(Context context, Expression<?, String> expr1, Expression<?, String> expr2) {
    super(context, "^", expr1, expr2);
  }

  public Exponentiation(Exponentiation other) {
    super(other);
  }

  @SafeVarargs
  public Exponentiation(Exponentiation other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public Exponentiation copy() {
    return new Exponentiation(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public Exponentiation copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new Exponentiation(this, value, children);
  }

  @Override
  protected String trans(Target target, EsqlPath path, PMap<String, Object> parameters) {
    if (target == Translatable.Target.SQLSERVER) {
      return "POWER(" + expr1().translate(target, path.add(expr1()), parameters) + ", " + expr2().translate(target, path.add(expr2()), parameters) + ")";
    } else {
      String e = expr1().translate(target, path.add(expr1()), parameters) + " ^ " + expr2().translate(target, path.add(expr2()), parameters);
      return target == JSON ? '"' + escapeJsonString(e) + '"' : e;
    }
  }
}
