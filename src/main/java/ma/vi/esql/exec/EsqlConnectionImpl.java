/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.exec;

import ma.vi.esql.database.Database;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.exec.env.ProgramEnvironment;
import ma.vi.esql.exec.function.NamedParameter;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.EsqlTransformer;
import ma.vi.esql.syntax.expression.literal.NullLiteral;
import ma.vi.esql.syntax.macro.Macro;
import ma.vi.esql.syntax.macro.TypedMacro;
import ma.vi.esql.syntax.macro.UntypedMacro;
import ma.vi.esql.syntax.query.QueryTranslation;
import ma.vi.esql.translation.TranslationException;
import org.pcollections.HashPMap;
import org.pcollections.IntTreePMap;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import static ma.vi.base.lang.Errors.unchecked;
import static ma.vi.esql.syntax.expression.literal.Literal.makeLiteral;
import static ma.vi.esql.syntax.macro.Macro.ONGOING_MACRO_EXPANSION;

/**
 * The implementation of {@link EsqlConnection}.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class EsqlConnectionImpl implements EsqlConnection {
  public EsqlConnectionImpl(Database db, Connection con, Stack<Connection> cons) {
    this.db = db;
    this.con = con;
    this.cons = cons;
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
  public Esql<?, ?> prepare(Esql<?, ?> esql, QueryParams qp) {
    Esql<?, ?> st = esql;

    /*
     * Substitute parameters into statement.
     */
    if (qp !=null && !qp.params.isEmpty()) {
      final Map<String, Param> parameters = new HashMap<>();
      for (Param p: qp.params) {
        parameters.put(p.a, p);
      }
      st = esql.map((e, p) -> {
        if (e instanceof NamedParameter) {
          String paramName = ((NamedParameter)e).name();
//          if (!parameters.containsKey(paramName)) {
//            throw new TranslationException("Parameter named " + paramName
//                                               + " is present but not supplied in " + esql);
//          } else {
          if (parameters.containsKey(paramName)) {
            Param param = parameters.get(paramName);
            Object value = param.b;
            return value instanceof Esql             ? (Esql<?, ?>)value
                                                     : value == null                     ? new NullLiteral(esql.context)
                                                                                         : value instanceof QueryTranslation ? makeLiteral(esql.context, value.toString())
                                                                                                                             : makeLiteral(esql.context, value);
          }
        }
        return e;
      });
    }

    /*
     * Base macro expansion for base-level changes not requiring any type information
     * or necessary for determining type information later (such as expanding `*`
     * in column lists).
     */
    st = expand(st, UntypedMacro.class);

    /*
     * Expand typed macros in statement (macros requiring type information to be
     * present when expanding).
     */
    st = expand(st, TypedMacro.class);

    /*
     * Scope symbols.
     */
    st.scope(db.structure(), new EsqlPath(st));

    /*
     * Type computation.
     */
    st.forEach((e, p) -> {
      e.computeType(p);
      return true;
    });

    /*
     * Transform ESQL through registered transformers prior to execution.
     */
    for (EsqlTransformer t: db.esqlTransformers()) {
      st = t.transform(db, st);
    }

    /*
     * Apply filters, if any.
     */
    if (qp != null && !qp.filters.isEmpty()) {
      for (Filter filter: qp.filters) {
        st = st.map((e, p) -> e.filter(filter, p));
      }
    }
    return st;
  }

  @Override
  public <R> R execPrepared(Esql<?, ?> esql, QueryParams qp) {
    Environment env = new ProgramEnvironment(db.structure());
    if (qp != null) {
      for (Param p : qp.params) env.add(p.a, p.b);
    }
    return (R)esql.exec(database().target(),
                        this,
                        new EsqlPath(esql),
                        HashPMap.empty(IntTreePMap.empty()),
                        env);
  }

  @Override
  public void close() {
    try {
      EsqlConnection.super.close();
    } finally {
      cons.remove(con);
    }
  }

  private static <T extends Esql<?, ?>,
                  M extends Macro> T expand(T esql, Class<M> macroType) {
    T expanded;
    int iteration = 0;
    while ((expanded = (T)esql.map((e, p) -> macroType.isAssignableFrom(e.getClass())
                                           ? ((Macro)e).expand(e, p.add(ONGOING_MACRO_EXPANSION))
                                           : e)) != esql) {
      esql = expanded;
      iteration++;
      if (iteration > 50) {
        throw new TranslationException(esql,
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

  /**
   * The connection stack linked to the thread from which this connection must
   * be removed from when it's closed.
   */
  private final Stack<Connection> cons;
}