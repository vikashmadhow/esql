package ma.vi.esql.exec.composable;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public interface Composable {
  enum Op { AND, OR }

  enum Order { NONE, ASC, DESC }

  String table();

  String alias();

  String expression();
}
