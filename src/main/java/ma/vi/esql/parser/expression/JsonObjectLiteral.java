/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.define.Attribute;
import ma.vi.esql.type.Type;
import ma.vi.esql.type.Types;
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
    if (!copying()) {
      try {
        copying(true);
        return new JsonObjectLiteral(this);
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
    return members().stream()
                    .map(e -> e.translate(target, parameters))
                    .collect(joining(",", "{", "}"));
  }

  @Override
  public JSONObject value(Target target) {
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