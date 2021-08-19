package ma.vi.esql.semantic.type;

/**
 * The kind (type) of types.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public interface Kind extends Type {

  class InternalKind extends InternalType implements Kind {
    public InternalKind(String name) {
      super(name);
    }

    @Override
    public Kind kind() {
      return this;
    }
  }

  Kind BASE = new InternalKind("Base");
  Kind ARRAY = new InternalKind("Array");
  Kind COMPOSITE = new InternalKind("Composite");
  Kind FUNCTION = new InternalKind("Function");
}
