/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression;

import ma.vi.esql.syntax.Context;
import ma.vi.esql.type.Type;
import ma.vi.esql.type.Types;

import java.util.Map;

import static ma.vi.esql.translator.SqlServerTranslator.requireIif;

/**
 * A relational expression (such as comparison) always has a
 * {@link Types#BoolType} type.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class RelationalOperator extends BinaryOperator {
  public RelationalOperator(Context context,
                            String op,
                            Expression<?, ?> expr1,
                            Expression<?, ?> expr2) {
    super(context, op, expr1, expr2);
  }

  public RelationalOperator(RelationalOperator other) {
    super(other);
  }

  @Override
  public RelationalOperator copy() {
    if (!copying()) {
      try {
        copying(true);
        return new RelationalOperator(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public Type type() {
    return Types.BoolType;
  }

  @Override
  protected String trans(Target target, Map<String, Object> parameters) {
    boolean sqlServerBool = target == Target.SQLSERVER && requireIif(this, parameters);
    return (sqlServerBool ? "iif" : "") + '('
         + super.trans(target, parameters)
         + (sqlServerBool ? ", 1, 0" : "") + ')';
  }
}
