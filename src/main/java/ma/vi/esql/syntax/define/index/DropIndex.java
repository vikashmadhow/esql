/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.define.index;

import ma.vi.base.tuple.T2;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.define.Define;
import org.pcollections.PMap;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import static ma.vi.esql.exec.Result.Nothing;
import static ma.vi.esql.semantic.type.Type.dbTableName;
import static ma.vi.esql.translation.Translatable.Target.SQLSERVER;

/**
 * Drop index statement.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class DropIndex extends Define {
  public DropIndex(Context context,
                   String  name,
                   String  table) {
    super(context, "DropIndex",
          T2.of("name",    new Esql<>(context, name)),
          T2.of("table",   new Esql<>(context, table)));
  }

  public DropIndex(DropIndex other) {
    super(other);
  }

  @SafeVarargs
  public DropIndex(DropIndex other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public DropIndex copy() {
    return new DropIndex(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public DropIndex copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new DropIndex(this, value, children);
  }

  @Override
  protected String trans(Target               target,
                         EsqlConnection       esqlCon,
                         EsqlPath             path,
                         PMap<String, Object> parameters,
                         Environment          env) {
    return switch (target) {
      case POSTGRESQL -> {
        var schema = Type.schema(table());
        yield "drop index if exists "
            + Type.dbTableName((schema == null ? "" : schema + '.' ) + name(), target);
      }
      case SQLSERVER -> "drop index \"" + name() + '"'
                      + " on " + dbTableName(table(), target);

      default ->  "drop index " + name() + " on " + table();
    };
  }

  @Override
  protected Object postTransformExec(Target               target,
                                     EsqlConnection       esqlCon,
                                     EsqlPath             path,
                                     PMap<String, Object> parameters,
                                     Environment          env) {
    try {
      Connection con = esqlCon.connection();
      if (target == SQLSERVER) {
        try (ResultSet rs = con.createStatement().executeQuery(
              "select 1"
            + "  from sys.indexes "
            + " where object_id = object_id('" + Type.dbTableName(table(), target) + "')"
            + "   and name='" + name() + "'")) {
          if (rs.next()) {
            con.createStatement().executeUpdate(
              translate(target, esqlCon, path, parameters, env));
          }
        }
      } else {
        con.createStatement().executeUpdate(
          translate(target, esqlCon, path, parameters, env));
      }
      return Nothing;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public String name() {
    return childValue("name");
  }

  public String table() {
    return childValue("table");
  }
}
