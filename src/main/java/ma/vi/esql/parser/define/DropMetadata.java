/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.define;

import ma.vi.esql.parser.Context;

import java.util.Map;

public class DropMetadata extends Alteration {
  public DropMetadata(Context context) {
    super(context);
  }

  public DropMetadata(DropMetadata other) {
    super(other);
  }

  @Override
  public DropMetadata copy() {
    if (!copying()) {
      try {
        copying(true);
        return new DropMetadata(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public String translate(Target target, Map<String, Object> parameters) {
    return "drop metadata";
  }
}
