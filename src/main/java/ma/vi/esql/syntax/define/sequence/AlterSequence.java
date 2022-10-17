/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.define.sequence;

import ma.vi.base.lang.NotFoundException;
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

/**
 * Alter sequence statement.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class AlterSequence extends Define {
  public AlterSequence(Context context,
                       String  name,
                       Long    restart,
                       Long    increment,
                       Long    minimum,
                       Long    maximum,
                       Integer cache,
                       boolean cycle) {
    super(context, "AlterSequence",
          T2.of("name",      new Esql<>(context, name)),
          T2.of("restart",   new Esql<>(context, restart)),
          T2.of("increment", new Esql<>(context, increment)),
          T2.of("minimum",   new Esql<>(context, minimum)),
          T2.of("maximum",   new Esql<>(context, maximum)),
          T2.of("cache",     new Esql<>(context, cache)),
          T2.of("cycle",     new Esql<>(context, cycle)));
  }

  public AlterSequence(AlterSequence other) {
    super(other);
  }

  @SafeVarargs
  public AlterSequence(AlterSequence other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public AlterSequence copy() {
    return new AlterSequence(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public AlterSequence copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new AlterSequence(this, value, children);
  }

  @Override
  protected String trans(Target               target,
                         EsqlConnection       esqlCon,
                         EsqlPath             path,
                         PMap<String, Object> parameters,
                         Environment          env) {
    return "alter sequence "
         + dbTableName(name(), target)
         + (target == Target.POSTGRESQL
         && restart()   != null ? " start with "   + restart()   : "")
         + (restart()   != null ? " restart with " + restart()   : "")
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
          + "  from information_schema.sequences"
          + " where sequence_schema='" + dbSchemaName + "'"
          + "   and sequence_name='" + splitName.b + "'")) {
        if (rs.next()) {
          con.createStatement().executeUpdate(
            translate(target, esqlCon, path, parameters, env));
        } else {
          throw new NotFoundException("Sequence named " + name() + " does not exist");
        }
      }
      return Nothing;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public String  name()      { return childValue("name");      }
  public Long    restart()   { return childValue("restart");   }
  public Long    increment() { return childValue("increment"); }
  public Long    minimum()   { return childValue("minimum");   }
  public Long    maximum()   { return childValue("maximum");   }
  public Integer cache()     { return childValue("cache");     }
  public boolean cycle()     { return childValue("cycle");     }
}
