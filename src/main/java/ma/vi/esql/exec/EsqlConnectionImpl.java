/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.exec;

import ma.vi.base.collections.Maps;
import ma.vi.base.tuple.T2;
import ma.vi.esql.parser.Parser;
import ma.vi.esql.parser.Translatable;
import ma.vi.esql.parser.*;
import ma.vi.esql.parser.expression.Literal;
import ma.vi.esql.parser.expression.NamedParameter;
import ma.vi.esql.parser.expression.NullLiteral;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import static ma.vi.base.lang.Errors.unchecked;

/**
 * An abstract ESQL connection which facilitates the creation of database-specific
 * ESQL connections types. Two such DB-specific ESQL connections are included in
 * the system, one for MS SQL Server and the other for PostgreSQL. Both are equally
 * well supported.
 *
 * An interface representing a connection to the database through which ESQL
 * statement can be sent. An ESQL connection will normally wrap and specialise
 * a JDBC database connection for querying the database using ESQL, but still
 * allowing direct access to the underlying connection for when SQL commands
 * need to be sent.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class EsqlConnectionImpl implements EsqlConnection {
  public EsqlConnectionImpl(Connection con, Translatable.Target target) {
    this.target = target;
    this.con = con;
  }

  public Connection connection() {
    return con;
  }

  @Override
  public Translatable.Target target() {
    return target;
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
      } else if (esql instanceof Statement) {
        return exec((Statement<?, ?>)esql, params);
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
    for (Statement<?, ?> s: program.statements()) {
      result = exec(s, params);
    }
    return result;
  }

  private Result exec(Statement<?, ?> statement, Param... params) {
    Statement<?, ?> st = statement;
    if (params.length > 0) {
      // substitute parameters into statement
      final Map<String, Param> parameters = new HashMap<>();
      for (Param p: params) {
        parameters.put(p.a, p);
      }
      st = (Statement<?, ?>)statement.map(e -> {
        if (e instanceof NamedParameter) {
          String paramName = ((NamedParameter)e).name();
          if (!parameters.containsKey(paramName)) {
            throw new TranslationException("Parameter named " + paramName + " is present in statement ("
                                            + statement + ") but has not been supplied.");
          } else {
            Param param = parameters.get(paramName);
            Object value = param.b;
            // @todo dynamic parsing of parameters would allow ESQL expressions to be provided as parameters
            if (value instanceof Esql) {
              return (Esql<?, ?>)value;

            } else if (value == null) {
              return new NullLiteral(statement.context);

            } else if (param instanceof ExpressionParam) {
              Parser parser = new Parser(statement.context.structure);
              return parser.parseExpression(value.toString());

            } else {
              return Literal.makeLiteral(statement.context, value);
            }
          }
        }
        return e;
      });
    }
    if (st instanceof QueryUpdate) {
      // macro expansion for queries only (for ddl, macro expansion would expand expressions
      // without any contextual information such as the current query which is needed for
      // some cases, such as when computing derived expressions)
      expand(st);
    }
    return st.execute(con, statement.context.structure, target);
  }

  private static void expand(Esql<?, ?> esql) {
    // The result of the expansion of the macros in a previous iteration of this
    // method. This is used to exclude from subsequent request for expansion macros
    // which have not expanded previously, indicating (most probably) that their
    // expansion is complete or not necessary.
    IdentityHashMap<Macro, Boolean> previousExpansionResult = new IdentityHashMap<>();
    while (_expand(esql, previousExpansionResult));
  }

  private static boolean _expand(Esql<?, ?> esql,
                                 IdentityHashMap<Macro, Boolean> previousExpansionResult) {
    // first collect macros in terms of priority
    PriorityQueue<Integer> orders = new PriorityQueue<>();
    Map<Integer, List<T2<String, Macro>>> macros = new HashMap<>();
    loadMacros(esql, 0, orders, macros, previousExpansionResult, new IdentityHashMap<>());

    // expand in priority order
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

  private static void loadMacros(Esql<?, ?> esql,
                                 int level,
                                 PriorityQueue<Integer> orders,
                                 Map<Integer, List<T2<String, Macro>>> macros,
                                 IdentityHashMap<Macro, Boolean> previousExpansionResult,
                                 IdentityHashMap<Object, Object> cycleDetector) {
    exploreChild(null, esql.value, level + 1, orders,
                 macros, previousExpansionResult, cycleDetector);

    if (esql.value instanceof List<?>) {
      for (Object v: (List<?>)esql.value) {
        exploreChild(null, v, level + 1, orders,
                     macros, previousExpansionResult, cycleDetector);
      }
    }

    Set<String> childrenNames = new HashSet<>(esql.children.keySet());
    for (String childName: childrenNames) {
      exploreChild(childName, esql.children.get(childName), level + 1, orders,
                   macros, previousExpansionResult, cycleDetector);
    }
  }

  private static void exploreChild(String childName,
                                   Object child,
                                   int level,
                                   PriorityQueue<Integer> orders,
                                   Map<Integer, List<T2<String, Macro>>> macros,
                                   IdentityHashMap<Macro, Boolean> previousExpansionResult,
                                   IdentityHashMap<Object, Object> cycleDetector) {
    if (cycleDetector.containsKey(child)) {
      if (child instanceof Esql) {
        Esql<?, ?> esql = (Esql<?, ?>)child;
        throw new TranslationException("Cycle detected during macro expansion with the following Esql element " +
            "present in the cycle: " + esql.getClass() + " {value: " +
            esql.value + ", parent: " + esql.parent +
            (esql.parent == null ? "" : " of class " + esql.parent.getClass()) +
            ", children: " + Maps.toString(esql.children) + "}");
      } else {
        throw new TranslationException("Cycle detected during macro expansion with the following object " +
            "present in the cycle: " + child.getClass() + " {value: " +
            child + "}");
      }
    }
    cycleDetector.put(child, child);

    if (child instanceof Esql<?, ?>) {
      loadMacros((Esql<?, ?>)child, level, orders, macros,
          previousExpansionResult, cycleDetector);
    }
    if (child instanceof Macro) {
      Macro macro = (Macro)child;

      // only add this macro for expansion if it has not been seen before
      // or it expanded before (which means that it could expand more).
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
    cycleDetector.remove(child);
  }

  protected final Translatable.Target target;

  private boolean rollbackOnly;

  /**
   * Underlying connection.
   */
  protected final Connection con;
}