/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.exec;

import ma.vi.esql.database.Database;
import ma.vi.esql.syntax.*;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.expression.NamedParameter;
import ma.vi.esql.syntax.expression.literal.Literal;
import ma.vi.esql.syntax.expression.literal.NullLiteral;
import ma.vi.esql.syntax.query.QueryUpdate;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static ma.vi.base.lang.Errors.unchecked;

/**
 * The implementation of {@link EsqlConnection}.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class EsqlConnectionImpl implements EsqlConnection {
  public EsqlConnectionImpl(Database db, Connection con) {
    this.db = db;
    this.con = con;
  }

  public Connection connection() {
    return con;
  }

  @Override
  public Database database() {
    return db;
  }

  @Override
  public void commit() {
    unchecked(con::commit);
  }

  @Override
  public void rollback() {
    try {
      con.rollback();
    } catch (SQLException sqle) {
      throw unchecked(sqle);
    }
  }

  @Override
  public void rollbackOnly() {
    rollbackOnly = true;
  }

  @Override
  public boolean isRollbackOnly() {
    return rollbackOnly;
  }

  @Override
  public Connection con() {
    return con;
  }

  @Override
  public Result exec(Esql<?, ?> esql, Param... params) {
    try {
      if (esql instanceof Program) {
        return exec((Program)esql, params);
      } else if (esql instanceof Expression) {
        return exec((Expression<?, ?>)esql, params);
      } else {
        throw new RuntimeException("Can't execute " + esql);
      }
    } catch (Exception e) {
      throw unchecked(e);
    }
  }

  /**
   * Execute the Esql program in order of the statements defined.
   *
   * @param program The program to execute
   * @return The last non-null result produced when executing a statement
   * in the program.
   */
  private Result exec(Program program, Param... params) {
    Result result = null;
    for (Expression<?, ?> s: program.expressions()) {
      result = exec(s, params);
    }
    return result;
  }

  private Result exec(Expression<?, ?> expression, Param... params) {
    Expression<?, ?> st = expression;
    if (params.length > 0) {
      /*
       * Substitute parameters into statement.
       */
      final Map<String, Param> parameters = new HashMap<>();
      for (Param p: params) {
        parameters.put(p.a, p);
      }
      Parser parser = new Parser(expression.context.structure);
      st = (Expression<?, ?>)expression.map((e, p) -> {
        if (e instanceof NamedParameter) {
          String paramName = ((NamedParameter)e).name();
          if (!parameters.containsKey(paramName)) {
            throw new TranslationException("Parameter named " + paramName + " is present in expression ("
                                         + expression + ") but has not been supplied.");
          } else {
            Param param = parameters.get(paramName);
            Object value = param.b;
            if (value instanceof Esql) {
              return (Esql<?, ?>)value;

            } else if (value == null) {
              return new NullLiteral(expression.context);

            } else if (param instanceof ExpressionParam) {
              return parser.parseExpression(value.toString());

            } else {
              return Literal.makeLiteral(expression.context, value);
            }
          }
        }
        return e;
      });
    }
    if (st instanceof QueryUpdate) {
      /*
       * Macro expansion for queries only (for ddl, macro expansion would expand
       * expressions without any contextual information such as the current query
       * which is needed for some cases - e.g., when computing derived expressions)
       */
      st = expandMacros(st);
    }
    /*
     * Transform ESQL through registered transformers prior to execution.
     */
    Esql<?, ?> esql = st;
    for (EsqlTransformer t: db.esqlTransformers()) {
      esql = t.transform(db, esql);
    }
    return esql.execute(db, con, new EsqlPath(esql));
  }

  private static <T extends Esql<?, ?>> T expandMacros(T esql) {
    T expanded;
    int iteration = 0;
    while ((expanded = (T)esql.map((e, p) -> e instanceof Macro m ? m.expand(e, p) : e)) != esql) {
      esql = expanded;
      iteration++;
      if (iteration > 50) {
        throw new TranslationException(
            "Macro expansion continued for more than 50 iterations and was stopped. "
          + "A macro could be expanding recursively into other macros. Esql: " + esql);
      }
    }
    return expanded;
  }

  protected final Database db;

  private boolean rollbackOnly;

  /**
   * Underlying connection.
   */
  protected final Connection con;
}