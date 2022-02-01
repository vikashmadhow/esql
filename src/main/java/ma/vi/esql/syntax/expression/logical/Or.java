/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.logical;

import ma.vi.base.tuple.T2;
import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.exec.ExecutionException;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.expression.comparison.ComparisonOperator;
import org.pcollections.PMap;

import static ma.vi.base.string.Escape.escapeJsonString;
import static ma.vi.esql.translation.Translatable.Target.JSON;

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

  @SafeVarargs
  public Or(Or other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public Or copy() {
    return new Or(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public Or copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new Or(this, value, children);
  }

  @Override
  protected String trans(Target target, EsqlPath path, PMap<String, Object> parameters) {
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

  @Override
  public Object postTransformExec(EsqlConnection esqlCon,
                                  EsqlPath       path,
                                  Environment env) {
    Object left = expr1().exec(esqlCon, path.add(expr1()), env);
    Object right = expr2().exec(esqlCon, path.add(expr2()), env);

    if (left instanceof Boolean lb
     && right instanceof Boolean rb) {
      return lb || rb;
    } else {
      throw new ExecutionException("Incompatible types for " + op()
                                 + ": left " + left + ", right: " + right);
    }
  }
}
