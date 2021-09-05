/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.define;

import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.EsqlPath;

import java.util.Map;

/**
 * Drop table metadata.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class DropMetadata extends Alteration {
  public DropMetadata(Context context) {
    super(context);
  }

  public DropMetadata(DropMetadata other) {
    super(other);
  }

  @Override
  public DropMetadata copy() {
    return new DropMetadata(this);
  }

  @Override
  protected String trans(Target target, EsqlPath path, Map<String, Object> parameters) {
    return "drop metadata";
  }
}
