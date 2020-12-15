package ma.vi.esql.parser;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public interface Close extends AutoCloseable {
  void close();

  boolean closing();

  void closing(boolean closing);

  boolean closed();

  void closed(boolean closed);
}
