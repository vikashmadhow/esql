package ma.vi.esql.exec;

import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.exec.env.ProgramEnvironment;
import ma.vi.esql.exec.function.NamedParameter;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.EsqlTransformer;
import ma.vi.esql.syntax.define.Define;
import ma.vi.esql.syntax.expression.literal.NullLiteral;
import ma.vi.esql.syntax.macro.Macro;
import ma.vi.esql.syntax.macro.TypedMacro;
import ma.vi.esql.syntax.macro.UntypedMacro;
import ma.vi.esql.syntax.query.QueryTranslation;
import ma.vi.esql.translation.TranslationException;
import org.pcollections.HashPMap;
import org.pcollections.IntTreePMap;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

import static ma.vi.esql.syntax.expression.literal.Literal.makeLiteral;
import static ma.vi.esql.syntax.macro.Macro.ONGOING_MACRO_EXPANSION;

/**
 * The default ESQL execution engine. Custom executors can be added with the
 * {@link ma.vi.esql.database.Database#addExecutor(Executor)} method by extensions.
 * Executors are kept in a stack with the last one added used for execution first.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class DefaultExecutor implements Executor {
  @Override
  public DefaultExecutor with(EsqlConnection con, Iterator<Executor> executors) {
    return new DefaultExecutor(con);
  }

  @Override
  public EsqlConnection connection() {
    return connection;
  }

  @Override
  public Esql<?, ?> prepare(Esql<?, ?> esql, QueryParams qp) {
    Esql<?, ?> st = esql;

    /*
     * Substitute parameters into statement.
     */
    if (qp !=null && !qp.params.isEmpty()) {
      st = esql.map((e, p) -> {
        if (e instanceof NamedParameter) {
          String paramName = ((NamedParameter)e).name();
          if (qp.params.containsKey(paramName)) {
            Param param = qp.params.get(paramName);
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
    var db = connection().database();
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
      AtomicBoolean first = new AtomicBoolean(true);
      for (Filter filter: qp.filters) {
        st = st.map((e, p) -> e.filter(filter, first.get(), p));
        first.set(false);
      }
    }
    return st;
  }

  @Override
  public <R> R execPrepared(Esql<?, ?> esql, QueryParams qp) {
    var db = connection.database();
    Environment env = new ProgramEnvironment(db.structure());
    if (qp != null) {
      for (Param p: qp.params.values()) env.add(p.a, p.b);
    }
    return (R)esql.exec(db.target(),
                        connection,
                        new EsqlPath(esql),
                        HashPMap.empty(IntTreePMap.empty()),
                        env);
  }

  private static <T extends Esql<?, ?>,
                  M extends Macro    > T expand(T esql, Class<M> macroType) {
    T expanded;
    int iteration = 0;
    while ((expanded = (T)esql.map((e, p) ->  macroType.isAssignableFrom(e.getClass())
                                           ? ((Macro)e).expand(e, p.add(ONGOING_MACRO_EXPANSION))
                                           : e)) != esql) {
      esql = expanded;
      iteration++;
      if (iteration > 50) {
        throw new TranslationException(esql, "Macro expansion continued for more than 50 iterations and was stopped. "
                                           + "A macro could be expanding recursively into other macros. Esql: " + esql);
      }
    }
    return expanded;
  }

  public DefaultExecutor() {
    this.connection = null;
  }

  private DefaultExecutor(EsqlConnection connection) {
    this.connection = connection;
  }

  protected final EsqlConnection connection;
}
