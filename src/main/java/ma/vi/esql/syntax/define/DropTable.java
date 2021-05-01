/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.define;

import ma.vi.esql.database.Database;
import ma.vi.esql.database.Structure;
import ma.vi.esql.exec.Result;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.semantic.type.BaseRelation;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import static ma.vi.esql.syntax.Translatable.Target.ESQL;
import static ma.vi.esql.semantic.type.Type.dbTableName;

public class DropTable extends Define<String> {
  public DropTable(Context context, String name) {
    super(context, name);
  }

  public DropTable(DropTable other) {
    super(other);
  }

  @Override
  public DropTable copy() {
    if (!copying()) {
      try {
        copying(true);
        return new DropTable(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  protected String trans(Target target, Map<String, Object> parameters) {
    return "drop table " + (target == ESQL ? name() : dbTableName(name(), target));
  }

  /**
   * Drops a table, removing dependent tables in cascade, if necessary.
   */
  @Override
  public Result execute(Database db, Connection con) {
    try {
      /*
       * Execute drop cascading to dependents and updating internal structures.
       */
      cascadeDrop(this, con, db.structure(), db.target());
      return Result.Nothing;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private static void cascadeDrop(DropTable drop,
                                  Connection con,
                                  Structure structure,
                                  Target target) throws SQLException {
    BaseRelation table = structure.relation(drop.name());
    if (table != null) {
      for (ForeignKeyConstraint constraint: table.dependentConstraints()) {
        cascadeDrop(new DropTable(drop.context, constraint.table()), con, structure, target);
      }

      // drop in database
      String dropSql = drop.translate(target);
      con.createStatement().executeUpdate(dropSql);

      // remove from information tables and cached structure
      structure.database.dropTable(con, table.id());
      structure.remove(table);
    }
  }

  public String name() {
    return value;
  }
}
