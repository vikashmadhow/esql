package ma.vi.esql.semantic.type;

import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.define.Metadata;
import ma.vi.esql.syntax.expression.literal.Literal;

import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.stream.Collectors.toMap;
import static ma.vi.esql.translation.Translatable.Target.JAVASCRIPT;

/**
 * {@link Column} restructured to a Java record for simpler access.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public record SimpleColumn(Context  context,
                           String   name,
                           String   type,
                           boolean  derived,
                           boolean  notNull,
                           Object   expression,
                           Metadata metadata,
                           Map<String, Object> attributes) {
  public SimpleColumn(Context  context,
                      String   name,
                      String   type,
                      boolean  derived,
                      boolean  notNull,
                      Object   expression,
                      Metadata metadata) {
    this(context,
         name,
         type,
         derived,
         notNull,
          expression,
         metadata,
         metadata == null
       ? new LinkedHashMap<>()
       : metadata.attributes().entrySet().stream()
                 .collect(toMap(Map.Entry::getKey,
                                e -> e.getValue().attributeValue() instanceof Literal<?>
                                   ? e.getValue().evaluateAttribute()
                                   : "$(" + e.getValue().attributeValue().translate(JAVASCRIPT) + ")",
                                (e1, e2) -> e1,
                                LinkedHashMap::new)));
  }

  public boolean has(String attr) {
    return attributes != null
        && attributes.containsKey(attr);
  }

  public boolean is(String attr) {
    return  is(attr, false);
  }

  public boolean is(String attr, boolean defaultValue) {
    return attributes != null && attributes.containsKey(attr)
         ? attributes.get(attr) instanceof Boolean b && b
         : defaultValue;
  }

  public <T> T get(String attr) {
    return get(attr, null);
  }

  public <T> T get(String attr, T defaultValue) {
    return attributes == null
         ? defaultValue
         : (T)attributes.getOrDefault(attr, defaultValue);
  }

  public void set(String attr, Object value) {
    attributes.put(attr, value);
  }
}