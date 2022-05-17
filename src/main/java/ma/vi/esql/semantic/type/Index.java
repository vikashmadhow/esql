package ma.vi.esql.semantic.type;

import java.util.List;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public record Index(String       name,
                    boolean      unique,
                    String       table,
                    List<String> columns) {}
