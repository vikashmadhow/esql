/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.semantic.type;

import ma.vi.base.lang.NotFoundException;
import ma.vi.base.tuple.T2;
import ma.vi.esql.semantic.scope.Symbol;
import ma.vi.esql.syntax.expression.ColumnRef;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public abstract class Relation extends AbstractType implements Symbol {
  /**
   * Relation type.
   */
  public enum RelationType {
    TABLE         ('T'),
    VIEW          ('V'),
    INDEX         ('I'),
    SEQUENCE      ('S'),
    COMPOSITE_TYPE('C'),
    PROJECTION    ('P'),
    JOIN          ('J');

    public final char marker;

    RelationType(char marker) {
      this.marker = marker;
    }

    public static RelationType fromMarker(char marker) {
      return switch (marker) {
        case 'T' -> TABLE;
        case 'V' -> VIEW;
        case 'I' -> INDEX;
        case 'S' -> SEQUENCE;
        case 'C' -> COMPOSITE_TYPE;
        default  -> throw new IllegalArgumentException("Unknown relation type: " + marker);
      };
    }
  }

  public Relation(String name) {
    super(name);
  }

  public Relation(Relation other) {
    super(other);
  }

  @Override
  public abstract Relation copy();

  @Override
  public Kind kind() {
    return Kind.COMPOSITE;
  }

  @Override
  public boolean isAbstract() {
    return false;
  }

  public abstract String alias();

  public abstract Set<String> aliases();

  public abstract List<T2<Relation, Column>> columns();

  public List<T2<Relation, Column>> columns(String prefix) {
    List<T2<Relation, Column>> cols = new ArrayList<>();
    for (T2<Relation, Column> col: columns()) {
      Column c = col.b();
      String alias = c.name();
      if (alias != null
          && (alias.equals(prefix)
           || (alias.startsWith(prefix)
            && alias.length() > prefix.length()
            && alias.charAt(prefix.length()) == '/'))) {
        cols.add(col);
      }
    }
    return cols;
  }

  public Column column(String column) throws NotFoundException, AmbiguousColumnException {
    return column(ColumnRef.of(null, column)).b;
  }

  public T2<Relation, Column> column(ColumnRef ref) throws NotFoundException, AmbiguousColumnException {
    T2<Relation, Column> col = findColumn(ref);
    if (col == null) {
      throw new NotFoundException("Column " + ref + " could not be found in relation " + this);
    }
    return col;
  }

  public Column findColumn(String name) {
    T2<Relation, Column> c = findColumn(ColumnRef.of(null, name));
    return c == null ? null : c.b;
  }

  public T2<Relation, Column> findColumn(ColumnRef ref) {
    T2<Relation, Column> col = null;
    for (T2<Relation, Column> relCol: columns()) {
      Column c = relCol.b();
      if (c.name() != null && c.name().equals(ref.columnName())) {
        if (col != null) {
          throw new AmbiguousColumnException("Ambiguous column " + ref.columnName());
        }
        col = relCol;
      }
    }
    return null;
  }

  @Override
  public String toString() {
    StringBuilder st = new StringBuilder(name()).append(" {\n");
    for (T2<Relation, Column> c: columns()) {
      c.b()._toString(st, 2, 2);
      st.append('\n');
    }
    st.append('}');
    return st.toString();
  }

  public static final Relation UNKNOWN = new Selection(Collections.emptyList(),
                                                       Collections.emptyList(),
                                                       null, "__unknown");
}
