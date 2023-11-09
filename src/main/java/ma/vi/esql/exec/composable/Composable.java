package ma.vi.esql.exec.composable;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public interface Composable {
  String table();
  String alias();
  String expression();
}
