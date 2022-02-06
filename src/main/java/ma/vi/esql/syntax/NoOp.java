/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax;

import ma.vi.base.tuple.T2;
import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import org.pcollections.PMap;

import java.util.List;

/**
 * No-operation; can be used by macros to remove a statement in a program.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class NoOp extends Esql<String, Void> {
  public NoOp(Context context) {
    super(context, "NoOp");
  }

  public NoOp(Context context, List<? extends Esql<?, ?>> children) {
    super(context, null, children);
  }

  public NoOp(NoOp other) {
    super(other);
  }

  @SafeVarargs
  public NoOp(NoOp other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public NoOp copy() {
    return new NoOp(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  public NoOp copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new NoOp(this, value, children);
  }

  @Override
  public Void trans(Target target, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env) {
    return null;
  }
}
