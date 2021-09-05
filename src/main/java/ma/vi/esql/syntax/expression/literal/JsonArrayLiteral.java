/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.literal;

import ma.vi.esql.syntax.Context;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.Translatable;
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
    return new JsonArrayLiteral(this);
  }

  @Override
  public Type type() {
    /*
     * The database should treat this as an opaque string.
     */
    return Types.StringType;
  }

  @Override
  protected String trans(Target target, EsqlPath path, Map<String, Object> parameters) {
    return items().stream()
                  .map(e -> e.translate(target, path.add(e), parameters))
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
  public JSONArray value(Translatable.Target target) {
    return new JSONArray(items().stream()
                                .map(e -> e.value(target))
                                .collect(Collectors.toList()));
  }

  public List<Literal<?>> items() {
    return value;
  }
}