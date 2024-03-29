/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.logical;

import ma.vi.base.tuple.T2;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.ExecutionException;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.expression.comparison.ComparisonOperator;
import ma.vi.esql.translation.SqlServerTranslator;
import org.pcollections.PMap;

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
  protected String trans(Target               target,
                         EsqlConnection       esqlCon,
                         EsqlPath             path,
                         PMap<String, Object> parameters,
                         Environment          env) {
    switch (target) {
      case JAVASCRIPT -> {
        return expr1().translate(target, null, path.add(expr1()), parameters, null) + " || "
             + expr2().translate(target, null, path.add(expr2()), parameters, null);
      }
      case SQLSERVER -> {
        if (SqlServerTranslator.requireIif(path, parameters)) {
          /*
           * For SQL Server, boolean expressions outside of where and having
           * clauses are not allowed and we simulate it with bitwise operations.
           */
          return "cast(" + String.valueOf(expr1().translate(target, null, path.add(expr1()), parameters, null))
               + " | "   + String.valueOf(expr2().translate(target, null, path.add(expr2()), parameters, null))
               + " as bit)";
        } else {
          return super.trans(target, esqlCon, path, parameters, env);
        }
      }
      default -> {
        return super.trans(target, esqlCon, path, parameters, env);
      }
    }
  }

  @Override
  public Object postTransformExec(Target               target,
                                  EsqlConnection       esqlCon,
                                  EsqlPath             path,
                                  PMap<String, Object> parameters,
                                  Environment          env) {
    Object left  = expr1().exec(target, esqlCon, path.add(expr1()), parameters, env);
    Object right = expr2().exec(target, esqlCon, path.add(expr2()), parameters, env);

    if (left  instanceof Boolean lb
     && right instanceof Boolean rb) {
      return lb || rb;
    } else {
      throw new ExecutionException(this, "Incompatible types for " + op()
                                       + ": left " + left + ", right: " + right);
    }
  }
}
