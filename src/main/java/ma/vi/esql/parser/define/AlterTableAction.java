/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.define;

import ma.vi.base.tuple.T2;
import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.Esql;

/**
 * An alter table action (rename table, rename column, etc.).
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public abstract class AlterTableAction extends Define<String> {
  public AlterTableAction(Context context, T2<String, ? extends Esql<?, ?>>... children) {
    super(context, "alter", children);
  }

  public AlterTableAction(AlterTableAction other) {
    super(other);
  }

  @Override
  public abstract AlterTableAction copy();
}