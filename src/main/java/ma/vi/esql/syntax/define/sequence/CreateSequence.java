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
import ma.vi.esql.syntax.define.Create;
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
public class CreateSequence extends Define implements Create {
  public CreateSequence(Context context,
                        String  name,
                        Long    start,
                        Long    increment,
                        Long    minimum,
                        Long    maximum,
                        Integer cache,
                        boolean cycle) {
    super(context, "CreateSequence",
          T2.of("name",      new Esql<>(context, name)),
          T2.of("start",     new Esql<>(context, start)),
          T2.of("increment", new Esql<>(context, increment)),
          T2.of("minimum",   new Esql<>(context, minimum)),
          T2.of("maximum",   new Esql<>(context, maximum)),
          T2.of("cache",     new Esql<>(context, cache)),
          T2.of("cycle",     new Esql<>(context, cycle)));
  }

  public CreateSequence(CreateSequence other) {
    super(other);
  }

  @SafeVarargs
  public CreateSequence(CreateSequence other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public CreateSequence copy() {
    return new CreateSequence(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public CreateSequence copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new CreateSequence(this, value, children);
  }

  @Override
  protected String trans(Target               target,
                         EsqlConnection       esqlCon,
                         EsqlPath             path,
                         PMap<String, Object> parameters,
                         Environment          env) {
    return "create sequence " + (target == POSTGRESQL ? "if not exists " : "")
         + dbTableName(name(), target)
         + (start    () != null ? " start with "   + start()     : "start with 1")
         + (increment() != null ? " increment by " + increment() : "")
         + (minimum()   != null ? " minvalue "     + minimum()   : "")
         + (maximum()   != null ? " maxvalue "     + maximum()   : "")
         + (cache()     != null ? " cache "        + cache()     : "")
         + (cycle()             ? " cycle"                       : "");
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
      esqlCon.database().createSchema(con, schema);

      String dbSchemaName = Type.dbSchemaName(schema, target);
      try (ResultSet rs = con.createStatement().executeQuery(
            "select 1"
          + "  from INFORMATION_SCHEMA.SEQUENCES"
          + " where sequence_schema='" + dbSchemaName + "'"
          + "   and sequence_name='" + splitName.b + "'")) {
        if (!rs.next()) {
          con.createStatement().executeUpdate(
            translate(target, esqlCon, path, parameters, env));
        }
      }
      return Nothing;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public String  name()      { return childValue("name");      }
  public Long    start()     { return childValue("start");     }
  public Long    increment() { return childValue("increment"); }
  public Long    minimum()   { return childValue("minimum");   }
  public Long    maximum()   { return childValue("maximum");   }
  public Integer cache()     { return childValue("cache");     }
  public boolean cycle()     { return childValue("cycle");     }
}
