/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.esql.parser.define;

import ma.vi.esql.parser.Context;

public class DropMetadata extends AlterTableAction {
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
  public String translate(Target target) {
    return "drop metadata";
  }
}
