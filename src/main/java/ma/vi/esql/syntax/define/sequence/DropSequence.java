/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.define.sequence;

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
import static ma.vi.esql.translation.Translatable.Target.POSTGRESQL;

/**
 * Create sequence statement.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class DropSequence extends Define {
  public DropSequence(Context context,
                      String  name) {
    super(context, "DropSequence",
          T2.of("name", new Esql<>(context, name)));
  }

  public DropSequence(DropSequence other) {
    super(other);
  }

  @SafeVarargs
  public DropSequence(DropSequence other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public DropSequence copy() {
    return new DropSequence(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public DropSequence copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new DropSequence(this, value, children);
  }

  @Override
  protected String trans(Target               target,
                         EsqlConnection       esqlCon,
                         EsqlPath             path,
                         PMap<String, Object> parameters,
                         Environment          env) {
    return "drop sequence " + (target == POSTGRESQL ? "if exists " : "")
         + dbTableName(name(), target);
  }

  @Override
  protected Object postTransformExec(Target               target,
                                     EsqlConnection       esqlCon,
                                     EsqlPath             path,
                                     PMap<String, Object> parameters,
                                     Environment          env) {
    try {
      Connection con = esqlCon.connection();
      T2<String, String> splitName = Type.splitName(name());
      String schema = splitName.a;
      esqlCon.database().createSchema(schema);

      String dbSchemaName = Type.dbSchemaName(schema, target);
      try (ResultSet rs = con.createStatement().executeQuery(
            "select 1"
          + "  from information_schema.sequences"
          + " where sequence_schema='" + dbSchemaName + "'"
          + "   and sequence_name='" + splitName.b + "'")) {
        if (rs.next()) {
          con.createStatement().executeUpdate(
            translate(target, esqlCon, path, parameters, env));
        }
      }
      return Nothing;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public String name() {
    return childValue("name");
  }
}
