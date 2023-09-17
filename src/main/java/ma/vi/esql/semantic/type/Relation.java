/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.semantic.type;

import ma.vi.base.lang.NotFoundException;
import ma.vi.base.tuple.T2;
import ma.vi.esql.semantic.scope.Symbol;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.define.Attribute;
import ma.vi.esql.syntax.expression.ColumnRef;
import ma.vi.esql.syntax.expression.literal.Literal;
import org.pcollections.HashPMap;
import org.pcollections.IntTreePMap;

import java.util.*;
import java.util.stream.Collectors;

import static ma.vi.esql.database.Database.NULL_DB;
import static ma.vi.esql.translation.Translatable.Target.ESQL;
import static ma.vi.esql.translation.Translatable.Target.JAVASCRIPT;

/**
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public abstract class Relation extends AbstractType implements Symbol {
  /**
   * Relation type.
   */
  public enum RelationType {
    TABLE ('T'),
    VIEW  ('V'),
    STRUCT('S');

    public final char marker;

    RelationType(char marker) {
      this.marker = marker;
    }

    public static RelationType fromMarker(char marker) {
      return switch (marker) {
        case 'T' -> TABLE;
        case 'V' -> VIEW;
        case 'S' -> STRUCT;
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

  /**
   * Utility function returning the list of columns in this relation mapped to
   * their name. When there is a more than one column with the same name in the
   * relation (such as in a join), only one of the column will be added,
   * non-deterministically, to the map.
   *
   * @return Map of column names to columns.
   */
  public Map<String, Column> columnMap() {
    return columns().stream()
                    .collect(Collectors.toMap(
                      c -> c.b().name(),
                      T2::b,
                      (c1, c2) -> c1,
                      LinkedHashMap::new
                    ));
  }

  /**
   * Utility function returning the list of columns in this relation as a list
   * of {@link SimpleColumn}s which are easier to access and work with.
   *
   * @return List of columns as {@link SimpleColumn}s.
   */
  public List<SimpleColumn> cols() {
    return cols(JAVASCRIPT);
  }

  public List<SimpleColumn> cols(Target target) {
    return columnMap().values().stream()
                      .map(c -> new SimpleColumn(c.context,
                                                 target,
                                                 c.name(),
                                                 c.type().name(),
                                                 c.derived(),
                                                 c.notNull(),
                                                 c.expression() instanceof Literal<?> ? c.expression().exec(target, null,
                                                                                                            new EsqlPath(c.expression()),
                                                                                                            HashPMap.empty(IntTreePMap.empty()),
                                                                                                            NULL_DB.structure())
                                               : "$(" + c.expression().translate(target) + ")",
                                                 c.metadata()))
                      .toList();
  }

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
    Map<String, Attribute> attrs = attributes();
    if (attrs != null && !attrs.isEmpty()) {
      st.append("{\n");
      for (var a: attrs.values()) {
        st.append("  ");
        a._toString(st, 3, 2);
        st.append('\n');
      }
      st.append("}\n");
    }
    for (T2<Relation, Column> c: columns()) {
      c.b()._toString(st, 2, 2);
      st.append('\n');
    }
    st.append('}');
    return st.toString();
  }
}