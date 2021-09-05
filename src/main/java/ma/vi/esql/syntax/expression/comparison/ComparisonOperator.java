/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.comparison;

import ma.vi.esql.syntax.Context;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.BinaryOperator;
import ma.vi.esql.syntax.expression.Expression;

import java.util.Map;

import static ma.vi.esql.translator.SqlServerTranslator.requireIif;

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

  @Override
  public ComparisonOperator copy() {
    return new ComparisonOperator(this);
  }

  @Override
  public Type type() {
    return Types.BoolType;
  }

  @Override
  protected String trans(Target target,
                         EsqlPath path,
                         Map<String, Object> parameters) {
    boolean sqlServerBool = target == Target.SQLSERVER && requireIif(path, parameters);
    return (sqlServerBool ? "iif" : "") + '('
         + super.trans(target, path, parameters)
         + (sqlServerBool ? ", 1, 0" : "") + ')';
  }
}
