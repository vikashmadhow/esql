/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.literal;

import ma.vi.base.tuple.T2;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
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

  public JsonArrayLiteral(JsonArrayLiteral other, List<Literal<?>> value, T2<String, ? extends Esql<?, ?>>... children) {
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
  public JsonArrayLiteral copy(List<Literal<?>> value, T2<String, ? extends Esql<?, ?>>... children) {
    return new JsonArrayLiteral(this, value, children);
  }

  @Override
  public Type type(EsqlPath path) {
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
  public JSONArray value(Translatable.Target target, EsqlPath path) {
    return new JSONArray(items().stream()
                                .map(e -> e.value(target, path))
                                .collect(Collectors.toList()));
  }

  public List<Literal<?>> items() {
    return value;
  }
}