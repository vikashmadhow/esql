/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.define;

import ma.vi.base.tuple.T2;
import ma.vi.esql.database.Database;
import ma.vi.esql.database.Structure;
import ma.vi.esql.exec.Result;
import ma.vi.esql.semantic.type.BaseRelation;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import static ma.vi.esql.semantic.type.Type.dbTableName;
import static ma.vi.esql.syntax.Translatable.Target.ESQL;

/**
 * Drop table statement.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class DropTable extends Define {
  public DropTable(Context context, String name) {
    super(context, "DropTable", T2.of("name", new Esql<>(context, name)));
  }

  public DropTable(DropTable other) {
    super(other);
  }

  @SafeVarargs
  public DropTable(DropTable other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public DropTable copy() {
    return new DropTable(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public DropTable copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new DropTable(this, value, children);
  }

  @Override
  protected String trans(Target target, EsqlPath path, Map<String, Object> parameters) {
    return "drop table " + (target == ESQL ? name() : dbTableName(name(), target));
  }

  /**
   * Drops a table, removing dependent tables in cascade, if necessary.
   */
  @Override
  public Result execute(Database db, Connection con, EsqlPath path) {
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
    return childValue("name");
  }
}
