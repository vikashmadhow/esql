/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.define;

import ma.vi.base.tuple.T2;
import ma.vi.esql.database.Database;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.database.Structure;
import ma.vi.esql.exec.Result;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.semantic.type.BaseRelation;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import org.pcollections.PMap;

import java.sql.Connection;
import java.sql.SQLException;

import static ma.vi.esql.semantic.type.Type.dbTableName;
import static ma.vi.esql.translation.Translatable.Target.ESQL;

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
  protected String trans(Target               target,
                         EsqlConnection       esqlCon,
                         EsqlPath             path,
                         PMap<String, Object> parameters,
                         Environment          env) {
    return "drop table " + (target == ESQL ? name() : dbTableName(name(), target));
  }

  /**
   * Drops a table, removing dependent tables in cascade, if necessary.
   * @return
   */
  @Override
  protected Object postTransformExec(Target               target,
                                     EsqlConnection       esqlCon,
                                     EsqlPath             path,
                                     PMap<String, Object> parameters,
                                     Environment          env) {
    try {
      /*
       * Execute drop cascading to dependents and updating internal structures.
       */
      cascadeDrop(this, esqlCon);
      return Result.Nothing;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private static void cascadeDrop(DropTable drop,
                                  EsqlConnection esqlCon) throws SQLException {
    Database db = esqlCon.database();
    Structure structure = db.structure();
    if (structure.relationExists(drop.name())) {
      BaseRelation table = structure.relation(drop.name());
      for (ForeignKeyConstraint constraint: table.dependentConstraints()) {
        cascadeDrop(new DropTable(drop.context, constraint.table()), esqlCon);
      }

      /*
       * Drop in database.
       */
      Connection con = esqlCon.connection();
      String dropSql = drop.translate(db.target());
      con.createStatement().executeUpdate(dropSql);

      /*
       * Remove from information tables and cached structure.
       */
      structure.database.dropTable(esqlCon, table.id());
      structure.remove(table);
    }
  }

  public String name() {
    return childValue("name");
  }
}
