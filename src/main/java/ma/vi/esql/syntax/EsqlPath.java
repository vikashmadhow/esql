package ma.vi.esql.syntax;

import ma.vi.base.tuple.T2;

import static java.lang.Integer.MAX_VALUE;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class EsqlPath {
  public EsqlPath(Esql<?, ?> esql) {
    this(esql, null);
  }

  private EsqlPath(Esql<?, ?> esql, EsqlPath tail) {
    this.head = esql;
    this.tail = tail;
  }

  public EsqlPath add(Esql<?, ?> esql) {
    return new EsqlPath(esql, this);
  }

  public EsqlPath replaceHead(Esql<?, ?> esql) {
    return new EsqlPath(esql, tail());
  }

  public Esql<?, ?> head() {
    return head;
  }

  public EsqlPath tail() {
    return tail;
  }

  /**
   * Returns this instance or its first ancestor, going up from the current parent,
   * which is an instance of the specified class. Returns null if no such instance
   * can be found.
   */
  public <T extends Esql<?, ?>> T ancestor(Class<T> cls) {
    return cls.isAssignableFrom(head.getClass()) ? (T)head
         : tail == null                          ? null
         : tail.ancestor(cls);
  }

  public boolean hasAncestor(Class<?>... cls) {
    for (Class<?> c: cls) {
      if (c.isAssignableFrom(head.getClass())) {
        return true;
      }
    }
    return tail != null && tail.hasAncestor(cls);
  }

  public <T extends Esql<?, ?>> T closestAncestor(Class<?>... cls) {
    for (Class<?> c: cls) {
      if (c.isAssignableFrom(head.getClass())) {
        return (T)head;
      }
    }
    return tail == null ? null : tail.closestAncestor(cls);
  }

  public <T extends Esql<?, ?>> T2<T, EsqlPath> closestAncestorAndPath(Class<?>... cls) {
    for (Class<?> c: cls) {
      if (c.isAssignableFrom(head.getClass())) {
        return T2.of((T)head, this);
      }
    }
    return tail == null ? null : tail.closestAncestorAndPath(cls);
  }

  /**
   * Returns this instance or its first ancestor, going up from the current parent,
   * which is an instance of the specified class. Returns null if no such instance
   * can be found.
   */
  public <T extends Esql<?, ?>> T2<T, EsqlPath> ancestorAndPath(Class<T> cls) {
    return cls.isAssignableFrom(head.getClass()) ? T2.of((T)head, this) :
           tail == null                          ? null    :
           tail.ancestorAndPath(cls);
  }

  /**
   * Returns the ancestor by the specified name if this esql is a
   * descendant of that ancestor.
   */
  public <T extends Esql<?, ?>> T ancestor(String name) {
    EsqlPath path = tail;
    Esql<?, ?> previous = head;
    while (path != null) {
      if (path.head.has(name) && path.head.child(name) == previous) {
        return (T)previous;
      }
      previous = path.head;
      path = path.tail;
    }
    return null;
  }

  public <T extends Esql<?, ?>> T2<T, EsqlPath> ancestorAndPath(String name) {
    EsqlPath path = tail;
    Esql<?, ?> previous = head;
    while (path != null) {
      if (path.head.has(name) && path.head.child(name) == previous) {
        return T2.of((T)previous, path);
      }
      previous = path.head;
      path = path.tail;
    }
    return null;
  }

  /**
   * Returns the distance of the ancestor from the head of the path, if found.
   * Otherwise returns -1.
   * @param cls The class of ancestor to find
   */
  public int ancestorDistance(Class<? extends Esql<?, ?>> cls) {
    int distance = 0;
    EsqlPath path = this;
    while (path != null) {
      if (cls.isAssignableFrom(path.head.getClass())) {
        return distance;
      }
      distance++;
      path = path.tail;
    }
    return -1;
  }

  public int ancestorDistance(String name) {
    int distance = 0;
    EsqlPath path = tail;
    Esql<?, ?> previous = head;
    while (path != null) {
      if (path.head.has(name) && path.head.child(name) == previous) {
        return distance;
      }
      distance++;
      previous = path.head;
      path = path.tail;
    }
    return MAX_VALUE;
  }

  private final Esql<?, ?> head;
  private final EsqlPath tail;
}
