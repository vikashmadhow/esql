/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression;

import ma.vi.esql.syntax.Context;
import ma.vi.esql.type.Type;
import ma.vi.esql.type.Types;
import org.json.JSONArray;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

/**
 * Json array literal in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class JsonArrayLiteral extends Literal<List<Literal<?>>> {
  public JsonArrayLiteral(Context context, List<Literal<?>> items) {
    super(context, items);
  }

  public JsonArrayLiteral(JsonArrayLiteral other) {
    super(other);
  }

  @Override
  public JsonArrayLiteral copy() {
    if (!copying()) {
      try {
        copying(true);
        return new JsonArrayLiteral(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public Type type() {
    /*
     * The database should treat this as an opaque string.
     */
    return Types.StringType;
  }

  @Override
  protected String trans(Target target, Map<String, Object> parameters) {
    return items().stream()
                  .map(e -> e.translate(target, parameters))
                  .collect(joining(",", "[", "]"));
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
  public JSONArray value(Target target) {
    return new JSONArray(items().stream()
                                .map(e -> e.value(target))
                                .collect(Collectors.toList()));
  }

  public List<Literal<?>> items() {
    return value;
  }
}