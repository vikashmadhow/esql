/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.literal;

import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.Translatable;
import ma.vi.esql.syntax.define.Attribute;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.semantic.type.Types;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

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

  @Override
  public JsonObjectLiteral copy() {
    return new JsonObjectLiteral(this);
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
    return members().stream()
                    .map(e -> e.translate(target, path.add(e), parameters))
                    .collect(joining(",", "{", "}"));
  }

  @Override
  public JSONObject value(Translatable.Target target) {
    JSONObject object = new JSONObject();
    for (Attribute member: members()) {
      object.put(member.name(), member.attributeValue().value(target));
    }
    return object;
  }

  public List<Attribute> members() {
    return value;
  }
}