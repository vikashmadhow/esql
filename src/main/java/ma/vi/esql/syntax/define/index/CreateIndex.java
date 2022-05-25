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
import ma.vi.esql.syntax.expression.ColumnRef;
import ma.vi.esql.syntax.expression.Expression;
import org.pcollections.PMap;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import static ma.vi.esql.exec.Result.Nothing;
import static ma.vi.esql.semantic.type.Type.dbTableName;
import static ma.vi.esql.translation.Translatable.Target.*;

/**
 * Create index statement.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class CreateIndex extends Define {
  public CreateIndex(Context context,
                     boolean unique,
                     String  name,
                     String  table,
                     List<Expression<?, ?>> columns) {
    super(context, "CreateIndex",
          T2.of("unique",  new Esql<>(context, unique)),
          T2.of("name",    new Esql<>(context, name)),
          T2.of("table",   new Esql<>(context, table)),
          T2.of("columns", new Esql<>(context, "columns", columns, true)));
  }

  public CreateIndex(CreateIndex other) {
    super(other);
  }

  @SafeVarargs
  public CreateIndex(CreateIndex other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public CreateIndex copy() {
    return new CreateIndex(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public CreateIndex copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new CreateIndex(this, value, children);
  }

  @Override
  protected String trans(Target               target,
                         EsqlConnection       esqlCon,
                         EsqlPath             path,
                         PMap<String, Object> parameters,
                         Environment          env) {
    return "create " + (unique() ? "unique " : "")
         + "index "  + (target == POSTGRESQL ? "if not exists " : "")
         + (target == ESQL ? name() : '"' + name() + '"')
         + " on "    + (target == ESQL ? table() : dbTableName(table(), target))
         + '(' + columns().stream()
                          .map(c -> {
                            boolean column = c instanceof ColumnRef;
                            return (!column && target == POSTGRESQL? "(" : "")
                                 + c.translate(target, esqlCon, path.add(c), parameters, env)
                                 + (!column && target == POSTGRESQL? ")" : "");
                          })
                          .collect(Collectors.joining(", "))
         + ')';
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
          if (!rs.next()) {
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

  public boolean unique() {
    return childValue("unique");
  }

  public String table() {
    return childValue("table");
  }

  public List<Expression<?, ?>> columns() {
    return child("columns").children();
  }
}
