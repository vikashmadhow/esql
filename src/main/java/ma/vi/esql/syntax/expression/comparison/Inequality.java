/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.comparison;

import ma.vi.base.tuple.T2;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import org.pcollections.PMap;

import java.util.Objects;

import static ma.vi.esql.translation.SqlServerTranslator.requireIif;

/**
 * The inequality operator in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Inequality extends ComparisonOperator {
  public Inequality(Context context, Expression<?, String> expr1, Expression<?, String> expr2) {
    super(context, "!=", expr1, expr2);
  }

  public Inequality(Inequality other) {
    super(other);
  }

  @SafeVarargs
  public Inequality(Inequality other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public Inequality copy() {
    return new Inequality(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public Inequality copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new Inequality(this, value, children);
  }

  @Override
  protected String trans(Target               target,
                         EsqlConnection       esqlCon,
                         EsqlPath             path,
                         PMap<String, Object> parameters,
                         Environment          env) {
    switch (target) {
      case JAVASCRIPT:
       return expr1().translate(target, null, path.add(expr1()), parameters, null) + " !== "
            + expr2().translate(target, null, path.add(expr2()), parameters, null);

      case SQLSERVER:
        boolean string = expr1().type() == Types.StringType
                      || expr1().type() == Types.TextType
                      || expr2().type() == Types.StringType
                      || expr2().type() == Types.TextType;
        boolean iif = requireIif(path, parameters);
        return (iif ? "iif(" : "")
             + String.valueOf(expr1().translate(target, esqlCon, path.add(expr1()), parameters, env))
             + (string ? " collate Latin1_General_CS_AS != " : " != ")
             + String.valueOf(expr2().translate(target, esqlCon, path.add(expr2()), parameters, env))
             + (string ? " collate Latin1_General_CS_AS" : "")
             + (iif ? ", 1, 0)" : "");

      default:
        return super.trans(target, esqlCon, path, parameters, env);
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
    return !Objects.equals(left, right);
  }
}
