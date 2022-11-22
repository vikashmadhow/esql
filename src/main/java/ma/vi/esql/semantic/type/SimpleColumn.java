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
                           Map<String, Object> attributes) {}