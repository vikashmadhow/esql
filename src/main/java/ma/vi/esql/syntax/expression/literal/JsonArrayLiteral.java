/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.literal;

import ma.vi.base.tuple.T2;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import org.json.JSONArray;
import org.pcollections.PMap;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

/**
 * Json array literal in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class JsonArrayLiteral extends Literal<List<? extends Literal<?>>> {
  public JsonArrayLiteral(Context context, List<? extends Literal<?>> items) {
    super(context, items);
  }

  public JsonArrayLiteral(JsonArrayLiteral other) {
    super(other);
  }

  @SafeVarargs
  public JsonArrayLiteral(JsonArrayLiteral other, List<? extends Literal<?>> value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public JsonArrayLiteral copy() {
    return new JsonArrayLiteral(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public JsonArrayLiteral copy(List<? extends Literal<?>> value, T2<String, ? extends Esql<?, ?>>... children) {
    return new JsonArrayLiteral(this, value, children);
  }

  @Override
  public Type computeType(EsqlPath path) {
    /*
     * The database should treat this as an opaque string.
     */
    return Types.JsonType;
  }

  @Override
  protected String trans(Target               target,
                         EsqlConnection       esqlCon,
                         EsqlPath             path,
                         PMap<String, Object> parameters,
                         Environment          env) {
    String t = items().stream()
                      .map(e -> e.translate(target, esqlCon, path.add(e), parameters, env))
                      .collect(joining(",", "[", "]"));
    if (path.tail() != null && path.tail().hasAncestor(JsonArrayLiteral.class, JsonObjectLiteral.class)) {
      return t;
    } else {
      return switch (target) {
        case ESQL,
            JAVASCRIPT -> t;
        default -> '\'' + t.replace("'", "''") + '\'';
      };
    }
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    st.append('[');
    boolean first = true;
    for (Literal<?> e: items()) {
      if (first) { first = false; }
      else       { st.append(", "); }
      e._toString(st, level, indent);
    }
    st.append(']');
  }

  @Override
  public JSONArray exec(Target               target,
                        EsqlConnection       esqlCon,
                        EsqlPath             path,
                        PMap<String, Object> parameters,
                        Environment          env) {
    return new JSONArray(items().stream()
                                .map(e -> e.exec(target, esqlCon, path, parameters, env))
                                .collect(Collectors.toList()));
  }

  public List<? extends Literal<?>> items() {
    return value;
  }
}