/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.function;

import ma.vi.base.tuple.T2;
import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import org.pcollections.PMap;

/**
 * The frame definition in window functions.
 *
 * @author vikash.madhow@gmail.com
 */
public class WindowFrame extends Esql<String, String> {
  public WindowFrame(Context context,
                     String frameType,
                     FrameBound preceding,
                     FrameBound following) {
    super(context, "WindowFrame",
          T2.of("frameType", new Esql<>(context, frameType)),
          T2.of("preceding", preceding),
          T2.of("following", following));
  }

  public WindowFrame(WindowFrame other) {
    super(other);
  }

  @SafeVarargs
  public WindowFrame(WindowFrame other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public WindowFrame copy() {
    return new WindowFrame(this);
  }

  @Override
  public WindowFrame copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new WindowFrame(this, value, children);
  }

  @Override
  public String trans(Target target, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env) {
    FrameBound preceding = preceding();
    FrameBound following = following();
    return frameType() + " between "
         + (preceding.unbounded()  ? "unbounded preceding" :
            preceding.currentRow() ? "current row"         :
            preceding.rows()      + " preceding")
         + " and "
         + (following.unbounded()  ? "unbounded following" :
            following.currentRow() ? "current row"         :
            following.rows()      + " following");
  }

  public String frameType() {
    return childValue("frameType");
  }

  public FrameBound preceding() {
    return child("preceding");
  }

  public FrameBound following() {
    return child("following");
  }
}