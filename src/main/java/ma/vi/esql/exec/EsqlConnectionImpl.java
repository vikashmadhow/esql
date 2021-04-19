/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.exec;

import ma.vi.base.collections.Maps;
import ma.vi.base.tuple.T2;
import ma.vi.esql.database.Database;
import ma.vi.esql.parser.*;
import ma.vi.esql.parser.expression.Expression;
import ma.vi.esql.parser.expression.Literal;
import ma.vi.esql.parser.expression.NamedParameter;
import ma.vi.esql.parser.expression.NullLiteral;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

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
      st = (Expression<?, ?>)expression.map(e -> {
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
              Parser parser = new Parser(expression.context.structure);
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
       * expressions without any contextual information such as the current
       * query which is needed for some cases, such as when computing derived
       * expressions)
       */
      expand(st);
    }
    /*
     * Transform ESQL through registered transformers prior to execution.
     */
    Esql<?, ?> esql = st;
    for (EsqlTransformer t: db.esqlTransformers()) {
      esql = t.transform(db, esql);
    }
    return esql.execute(db, con);
  }

  private static void expand(Esql<?, ?> esql) {
    /*
     * The result of the expansion of the macros in a previous iteration of this
     * method. This is used to exclude from subsequent request for expansion
     * macros which have not expanded previously, indicating (most probably)
     * that their expansion is complete or not necessary.
     */
    IdentityHashMap<Macro, Boolean> previousExpansionResult = new IdentityHashMap<>();
    while (_expand(esql, previousExpansionResult));
  }

  private static boolean _expand(Esql<?, ?> esql,
                                 IdentityHashMap<Macro, Boolean> previousExpansionResult) {
    /*
     * First collect macros in terms of priority.
     */
    PriorityQueue<Integer> orders = new PriorityQueue<>();
    Map<Integer, List<T2<String, Macro>>> macros = new HashMap<>();
    loadMacros(null, esql, 0, orders, macros, previousExpansionResult, new IdentityHashMap<>());

    /*
     * Expand in priority order.
     */
    Set<Macro> executed = new HashSet<>();
    boolean changed = false;
    while (!orders.isEmpty()) {
      Integer order = orders.poll();
      for (T2<String, Macro> m: macros.get(order)) {
        Macro macro = m.b;
        if (!executed.contains(macro)) {
          executed.add(macro);
          boolean expanded = macro.expand(m.a, macro instanceof Esql ? (Esql<?, ?>)macro : null);
          previousExpansionResult.put(m.b, expanded);
          changed |= expanded;
        }
      }
    }
    return changed;
  }

  private static void loadMacros(String childName,
                                 Object child,
                                 int level,
                                 PriorityQueue<Integer> orders,
                                 Map<Integer, List<T2<String, Macro>>> macros,
                                 IdentityHashMap<Macro, Boolean> previousExpansionResult,
                                 IdentityHashMap<Object, Object> cycleDetector) {
    if (child != null) {
      if (cycleDetector.containsKey(child)) {
        if (child instanceof Esql) {
          Esql<?, ?> esql = (Esql<?, ?>)child;
          throw new TranslationException("Cycle detected during macro expansion with the following Esql element "
                                       + "present in the cycle: " + esql.getClass() + " {value: " + esql.value
                                       + ", parent: " + esql.parent + (esql.parent == null ? "" : " of class "
                                       + esql.parent.getClass()) + ", children: " + Maps.toString(esql.children) + "}");
        } else {
          throw new TranslationException("Cycle detected during macro expansion with the following object "
                                       + "present in the cycle: " + child.getClass() + " {value: " + child + "}");
        }
      }
      try {
        cycleDetector.put(child, child);
        if (child instanceof Esql<?, ?>) {
          Esql<?, ?> esql = (Esql<?, ?>)child;
          loadMacros(null, esql.value, level + 1, orders,
                     macros, previousExpansionResult, cycleDetector);

          if (esql.value instanceof List<?>) {
            for (Object v: (List<?>)esql.value) {
              loadMacros(null, v, level + 1, orders,
                         macros, previousExpansionResult, cycleDetector);
            }
          }

          Set<String> childrenNames = new HashSet<>(esql.children.keySet());
          for (String c: childrenNames) {
            loadMacros(c, esql.children.get(c), level + 1, orders,
                       macros, previousExpansionResult, cycleDetector);
          }
        }
        if (child instanceof Macro) {
          Macro macro = (Macro)child;

          /*
           * Only add this macro for expansion if it has not been seen before
           * or it was expanded before (which means that it could expand more).
           */
          if (previousExpansionResult.getOrDefault(macro, true)) {
            int order = macro.expansionOrder() - level;
            if (macros.containsKey(order)) {
              macros.get(order).add(T2.of(childName, macro));
            } else {
              orders.add(order);
              macros.put(order, new ArrayList<>(Collections.singletonList(T2.of(childName, macro))));
            }
          }
        }
      } finally {
        cycleDetector.remove(child);
      }
    }
  }

  protected final Database db;

  private boolean rollbackOnly;

  /**
   * Underlying connection.
   */
  protected final Connection con;
}