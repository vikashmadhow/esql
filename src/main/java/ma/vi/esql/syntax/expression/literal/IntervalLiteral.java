/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.literal;

import ma.vi.base.tuple.T2;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.semantic.type.Interval;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import org.pcollections.PMap;

/**
 * A interval literal in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class IntervalLiteral extends BaseLiteral<String> {
  public IntervalLiteral(Context context, String value) {
    super(context, value);
  }

  public IntervalLiteral(IntervalLiteral other) {
    super(other);
  }

  @SafeVarargs
  public IntervalLiteral(IntervalLiteral other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public IntervalLiteral copy() {
    return new IntervalLiteral(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public IntervalLiteral copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new IntervalLiteral(this, value, children);
  }

  @Override
  public Type computeType(EsqlPath path) {
    return Types.IntervalType;
  }

  @Override
  protected String trans(Target target, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env) {
    return switch (target) {
      case POSTGRESQL -> '\'' + value + "'::interval";
      case SQLSERVER  -> '\'' + value + '\'';
      case JAVASCRIPT -> "new Interval('" + value + "')";
      default         -> "i'" + value + "'";
    };
  }

  @Override
  public Object exec(Target target, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env) {
    return new Interval(value);
  }
}