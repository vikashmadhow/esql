package ma.vi.esql.semantic.type;

import java.util.Map;

/**
 * {@link Column} restructured to a java record for impler access
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public record SimpleColumn(String  name,
                           String  type,
                           boolean derived,
                           boolean notNull,
                           Object  expression,
                           Map<String, Object> attributes) {
  public boolean has(String attr) {
    return attributes != null
        && attributes.containsKey(attr);
  }

  public boolean is(String attr) {
    return attributes != null
        && attributes.containsKey(attr)
        && attributes.get(attr) instanceof Boolean b
        && b;
  }

  public <T> T get(String attr) {
    return attributes == null
         ? null
         : (T)attributes.get(attr);
  }

  public void set(String attr, Object value) {
    attributes.put(attr, value);
  }
}