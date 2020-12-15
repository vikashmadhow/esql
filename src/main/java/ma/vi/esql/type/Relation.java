package ma.vi.esql.type;

import ma.vi.esql.parser.query.Column;
import ma.vi.base.lang.NotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public abstract class Relation extends AbstractType {
  /**
   * Relation type.
   */
  public enum RelationType {
    TABLE('T'),
    VIEW('V'),
    INDEX('I'),
    SEQUENCE('S'),
    COMPOSITE_TYPE('C'),
    PROJECTION('P'),
    JOIN('J');

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
        default -> throw new IllegalArgumentException("Unknown relation type: " + marker);
      };
    }
  }

  public Relation(String name) {
    super(name);
  }

  public Relation(Relation other) {
    super(other);
  }

  public Relation forAlias(String alias) {
    return alias == null ? this : null;
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

  public abstract List<Column> columns();

  public List<Column> columns(String name) {
    return columns(null, name);
  }

  /**
   * Returns any column that has the specified name as
   * a prefix. This includes, for instance, a, a[x], a[y], a[x][m], ...
   */
  public List<Column> columns(String relationAlias, String name) {
    List<Column> cols = new ArrayList<>();
    Relation rel = this;
    if (relationAlias != null) {
      rel = forAlias(relationAlias);
    }
    for (Column c: rel.columns()) {
      String alias = c.alias();
      if (alias != null
          && (alias.equals(name)
          || (alias.startsWith(name)
          && alias.length() > name.length()
          && alias.charAt(name.length()) == '/'))) {
        cols.add(c);
      }
    }
    return cols;
  }

  public boolean hasColumn(String name) {
    return hasColumn(null, name);
  }

  public boolean hasColumn(String relationAlias, String name) {
    return findColumn(relationAlias, name) != null;
  }

  public Column column(String name) throws NotFoundException, AmbiguousColumnException {
    return column(null, name);
  }

  public Column column(String relationAlias, String name) throws NotFoundException {
    Column col = findColumn(relationAlias, name);
    if (col == null) {
      throw new NotFoundException("Column " + (relationAlias == null ? "" : relationAlias + '.') + name
                                + " could not be found in relation " + forAlias(relationAlias).name());
    }
    return col;
  }

  protected Column findColumn(String relationAlias, String name) {
    Relation rel = this;
    if (relationAlias != null) {
      rel = forAlias(relationAlias);
      if (rel == null) {
        throw new NotFoundException("Relation with alias " + relationAlias + " could not be found");
      }
    }
    Column col = null;
    for (Column c: rel.columns()) {
      if (c.alias() != null && c.alias().equals(name)) {
        if (col != null) {
          throw new AmbiguousColumnException("Ambiguous column " + name);
        }
        col = c;
      }
    }
    return col;
  }

  public List<Column> columnsPrefixedBy(String relationAlias, String prefix) {
    Relation rel = this;
    if (relationAlias != null) {
      rel = forAlias(relationAlias);
      if (rel == null) {
        throw new NotFoundException("Relation with alias " + relationAlias + " could not be found");
      }
    }
    List<Column> cols = new ArrayList<>();
    for (Column c: rel.columns()) {
      if (c.alias() != null && c.alias().startsWith(prefix)) {
        cols.add(c);
      }
    }
    return cols;
  }
}

/*
    select a.x, b.x, a.x+b.x from A a, B b

    A: BaseRelation
    A a: AliasedRelation(BaseRelation(A), a)
    B: BaseRelation
    B b: AliasedRelation(BaseRelation(B), b)

    A a, B b: Join(AliasedRelation(BaseRelation(B), b), AliasedRelation(BaseRelation(B), b))

 */
