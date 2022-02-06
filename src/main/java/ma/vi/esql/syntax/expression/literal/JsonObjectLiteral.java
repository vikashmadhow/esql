/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.literal;

import ma.vi.base.tuple.T2;
import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.define.Attribute;
import org.json.JSONObject;
import org.pcollections.PMap;

import java.util.List;

import static java.util.stream.Collectors.joining;

/**
 * Json object literal in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class JsonObjectLiteral extends Literal<List<Attribute>> {
  public JsonObjectLiteral(Context context, List<Attribute> members) {
    super(context, members);
  }

  public JsonObjectLiteral(JsonObjectLiteral other) {
    super(other);
  }

  @SafeVarargs
  public JsonObjectLiteral(JsonObjectLiteral other, List<Attribute> value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public JsonObjectLiteral copy() {
    return new JsonObjectLiteral(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public JsonObjectLiteral copy(List<Attribute> value, T2<String, ? extends Esql<?, ?>>... children) {
    return new JsonObjectLiteral(this, value, children);
  }

  @Override
  public Type computeType(EsqlPath path) {
    /*
     * The database should treat this as an opaque string.
     */
    return Types.JsonType;
  }

  @Override
  protected String trans(Target target, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env) {
    String t = members().stream()
                        .map(e -> e.translate(target, esqlCon, path.add(e), parameters, env))
                        .collect(joining(",", "{", "}"));
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
  public JSONObject exec(Target target, EsqlConnection esqlCon, EsqlPath path, Environment env) {
    JSONObject object = new JSONObject();
    for (Attribute member: members()) {
      object.put(member.name(), member.attributeValue().exec(target, esqlCon, path, env));
    }
    return object;
  }

  public List<Attribute> members() {
    return value;
  }
}