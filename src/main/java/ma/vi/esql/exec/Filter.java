package ma.vi.esql.exec;

import ma.vi.esql.semantic.type.Type;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public record Filter(Op        op,
                     String    table,
                     String    alias,
                     String    condition,
                     boolean   exclude,
                     Filter... children) {
  public enum Op {AND, OR}

  public Filter(String table, String condition) {
    this(Op.AND,
         table,
         Type.unqualifiedName(table),
         condition,
         false);
  }

  public Filter(String table, String alias, String condition) {
    this(Op.AND,
         table,
         alias,
         condition,
         false);
  }

  public Filter(Op op, Filter... children) {
    this(op, false, children);
  }

  public Filter(Op op, boolean exclude, Filter... children) {
    this(op, null, null, null, exclude, children);
  }
}
