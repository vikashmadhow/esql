/*
 * Copyright (c) 2018-2022 Vikash Madhow
 */

package ma.vi.esql.exec.function;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;

/**
 * A frame bound in a frame definition.
 *
 * @author vikash.madhow@gmail.com
 */
public class FrameBound extends Esql<String, String> {
  public FrameBound(Context context,
                    boolean unbounded,
                    boolean currentRow,
                    Integer rows) {
    super(context, "FrameBound",
          T2.of("unbounded",  new Esql<>(context, unbounded)),
          T2.of("currentRow", new Esql<>(context, currentRow)),
          T2.of("rows",       new Esql<>(context, rows)));
  }

  public FrameBound(FrameBound other) {
    super(other);
  }

  @SafeVarargs
  public FrameBound(FrameBound other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public FrameBound copy() {
    return new FrameBound(this);
  }

  @Override
  public FrameBound copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new FrameBound(this, value, children);
  }

  public boolean unbounded() {
    return childValue("unbounded");
  }

  public boolean currentRow() {
    return childValue("currentRow");
  }

  public Integer rows() {
    return childValue("rows");
  }
}