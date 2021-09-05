/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax;

import java.util.List;
import java.util.Map;

/**
 * No-operation; can be used by macros to remove a statement in a program.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class NoOp extends Esql<Void, Void> {
  public NoOp(Context context) {
    super(context, null);
  }

  public NoOp(Context context, List<? extends Esql<?, ?>> children) {
    super(context, null, children);
  }

  public NoOp(NoOp other) {
    super(other);
  }

  @Override
  public NoOp copy() {
    return new NoOp(this);
  }

  @Override
  public Void trans(Target target, EsqlPath path, Map<String, Object> parameters) {
    return null;
  }
}
