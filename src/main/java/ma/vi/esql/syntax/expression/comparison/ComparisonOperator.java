/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.comparison;

import ma.vi.base.tuple.T2;
import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.BinaryOperator;
import ma.vi.esql.syntax.expression.Expression;
import org.pcollections.PMap;

import static ma.vi.esql.translation.SqlServerTranslator.requireIif;

/**
 * A relational expression (such as comparison) always has a
 * {@link Types#BoolType} type.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class ComparisonOperator extends BinaryOperator {
  public ComparisonOperator(Context context,
                            String op,
                            Expression<?, ?> expr1,
                            Expression<?, ?> expr2) {
    super(context, op, expr1, expr2);
  }

  public ComparisonOperator(ComparisonOperator other) {
    super(other);
  }

  @SafeVarargs
  public ComparisonOperator(ComparisonOperator other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public ComparisonOperator copy() {
    return new ComparisonOperator(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public ComparisonOperator copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new ComparisonOperator(this, value, children);
  }

  @Override
  public Type computeType(EsqlPath path) {
    return Types.BoolType;
  }

  @Override
  protected String trans(Target target,
                         EsqlConnection esqlCon, EsqlPath path,
                         PMap<String, Object> parameters, Environment env) {
    boolean sqlServerBool = target == Target.SQLSERVER && requireIif(path, parameters);
    return (sqlServerBool ? "iif(" : "")
         + super.trans(target, esqlCon, path, parameters, env)
         + (sqlServerBool ? ", 1, 0)" : "");
//    return (sqlServerBool ? "iif" : "") + '('
//         + super.trans(target, esqlCon, path, parameters, env)
//         + (sqlServerBool ? ", 1, 0" : "") + ')';
  }
}
