package ma.vi.esql.semantic.scope;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Allocator {
  public int allocate(Symbol symbol) {
    return elements++;
  }

  public int elements() {
    return elements;
  }

  private int elements = 0;
}
