/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser;

import ma.vi.base.tuple.T2;
import ma.vi.esql.database.Structure;
import ma.vi.esql.exec.Result;
import ma.vi.esql.type.Type;

import java.lang.reflect.Array;
import java.sql.Connection;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static ma.vi.esql.type.Types.VoidType;
import static org.apache.commons.lang3.StringUtils.repeat;

/**
 * The node type in the parsed representation of ESQL statements. The
 * latter consists of a tree of Esql objects which can be further processed
 * by macros and other methods before being translated for executing on a
 * target database.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class  Esql<V, R> implements Close, Copy<Esql<V, R>>, Translatable<R> {
  public Esql(Context context,
              V value,
              T2<String, ? extends Esql<?, ?>>... children) {
    this.value = value;
    this.context = context;
    if (value instanceof Esql) {
      ((Esql<?, ?>)value).parent = this;

    } else if (value instanceof List<?>) {
      for (Object e: (List<?>)value) {
        if (e instanceof Esql) {
          ((Esql<?, ?>)e).parent = this;
        }
      }
    }
    for (T2<String, ? extends Esql<?, ?>> child: children) {
      child(child.a, child.b);
    }
  }

  public Esql(Context context, V value, Esql<?, ?>[] children) {
    this(context, value, fromArray(children));
  }

  public Esql(Context context, V value, List<? extends Esql<?, ?>> children) {
    this(context, value, children == null ? new Esql<?, ?>[0] : children.toArray(new Esql[0]));
  }

  public Esql(Esql<V, R> other) {
    context = other.context;

    // copy value
    if (other.value instanceof Esql<?, ?>) {
      value = (V)((Esql<?, ?>)other.value).copy();
      ((Esql<?, ?>)value).parent = this;

    } else if (other.value instanceof List<?>) {
      ArrayList<Object> list = new ArrayList<>();
      for (Object e: (List<?>)other.value) {
        if (e instanceof Esql) {
          Esql<?, ?> v = ((Esql<?, ?>)e).copy();
          v.parent = this;
          list.add(v);
        } else {
          list.add(e);
        }
      }
      value = (V)list;

    } else {
      this.value = other.value;
    }

    // copy children
    for (Map.Entry<String, ? extends Esql<?, ?>> child: other.children.entrySet()) {
      child(child.getKey(), child.getValue().copy());
    }

    // link to previous parent as copying the parent
    // will result in a copy of the whole statement tree.
    parent = other.parent;
  }

  /**
   * Returns a copy of this Esql object with its value and children, but not
   * its parent, copied as well.
   */
  @Override
  public Esql<V, R> copy() {
    if (!copying()) {
      try {
        copying(true);
        return new Esql<>(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  private static T2<String, Esql<?, ?>>[] fromArray(Esql<?, ?>[] esqls) {
    T2<String, Esql<?, ?>>[] array = new T2[esqls.length];
    for (int i = 0; i < esqls.length; i++) {
      array[i] = T2.of(String.valueOf(i), esqls[i]);
    }
    return array;
  }

  public <T extends Esql<?, ?>> T[] childrenArray(Class<T> childrenType) {
    int i = 0;
    T[] array = (T[])Array.newInstance(childrenType, children.size());
    for (Esql<?, ?> e: children.values()) {
      array[i++] = (T)e;
    }
    return array;
  }

  public <T extends Esql<?, ?>> List<T> childrenList() {
    List<T> list = new ArrayList<>();
    for (Esql<?, ?> e: children.values()) {
      list.add((T)e);
    }
    return list;
  }

  public <T extends Esql<?, ?>> Esql<?, ?> childrenList(String childName, List<T> childrenList) {
    child(childName, new Esql<>(context, childName, childrenList));
    return this;
  }

  public <T extends Esql<?, ?>> T child(String child) {
    return (T)children.get(child);
  }

  public <T> T childValue(String child) {
    Esql<?, ?> v = children.get(child);
    return v == null ? null : (T)v.value;
  }

  public <T> Esql<T, ?> childValue(String child, T value) {
    Esql<T, ?> v = (Esql<T, ?>)children.get(child);
    if (v == null) {
      v = new Esql<>(context, value);
      child(child, v);
    } else {
      v.value = value;
    }
    return v;
  }

  public <T extends Esql<?, ?>> T child(String name, T child) {
    return child(name, child, true);
  }

  /**
   * Adds a child to this Esql.
   *
   * @param name       The name that the child will be associated to.
   * @param child      The child object to be associated to the name.
   *                   This must be a subclass of {@link Esql}.
   * @param hookParent Hooks the parent link of the child to this Esql instance.
   * @param <T>        The type of the type which must be a subclass of Esql.
   * @return The previously associated child to this child name
   * or null if none.
   */
  public <T extends Esql<?, ?>> T child(String name, T child, boolean hookParent) {
    if (child != null) {
      if (hookParent) {
        child.parent = this;
      }
      return (T)children.put(name, child);
    } else {
      children.remove(name);
      return null;
    }
  }

  /**
   * Adds a copy of the child as a child of this Esql.
   *
   * @param name  The name that the child will be associated to.
   * @param child The child for which a copy will be associated to the name.
   * @param <T>   The type of the type which must be a subclass of Esql.
   * @return The previously associated child to this child name
   * or null if none.
   */
  public <T extends Esql<?, ?>> T childCopy(String name, T child) {
    return child(name, child == null ? null : (T)child.copy());
  }

  public Esql<?, ?> parent() {
    return parent;
  }

  public Esql<?, ?> remove(String name) {
    return children.remove(name);
  }

  /**
   * The type of expressions produced when the statement is executed.
   * Default is the {@link Type#Void} type representing the case that
   * the expression does not produce anything.
   */
  public Type type() {
    return VoidType;
  }

  public Result execute(Connection connection,
                        Structure structure,
                        Target target) {
    return Result.Nothing;
  }

  /**
   * Executes the visitor function for each Esql children of this Esql object
   * including itself, its value, Esql values in its value list and the same
   * for its children, in depth-first post-order. If the visitor returns false
   * on any Esql part, the visit is terminated.
   */
  public boolean forEach(Function<Esql<?, ?>, Boolean> visitor,
                         Predicate<Esql<?, ?>> explore) {
    return _forEach(this, visitor, explore, new HashSet<>());
  }

  public boolean forEach(Function<Esql<?, ?>, Boolean> visitor) {
    return forEach(visitor, null);
  }

  private static boolean _forEach(Esql<?, ?> esql,
                                  Function<Esql<?, ?>, Boolean> visitor,
                                  Predicate<Esql<?, ?>> explore,
                                  Set<Esql<?, ?>> explored) {
    if (explore == null || explore.test(esql)) {
      explored.add(esql);
      if (esql.value instanceof Esql && !explored.contains(esql.value)) {
        if (!_forEach((Esql<?, ?>)esql.value, visitor, explore, explored)) {
          return false;
        }

      } else if (esql.value instanceof List) {
        for (Object e: (List<?>)esql.value) {
          if (e instanceof Esql && !explored.contains(e)) {
            if (!_forEach((Esql<?, ?>)e, visitor, explore, explored)) {
              return false;
            }
          }
        }
      }
      for (Esql<?, ?> child: esql.children.values()) {
        if (!explored.contains(child) && !explored.contains(child)) {
          if (!_forEach(child, visitor, explore, explored)) {
            return false;
          }
        }
      }
      return visitor.apply(esql);
    } else {
      return true;
    }
  }

  /**
   * Creates a copy of this Esql and execute the mapper function on all its
   * Esql-typed containing values and replacing them with the returned value
   * of the function. The mapping is performed in depth-first post-order.
   *
   * @param mapper  A function mapping each ESQL component of this ESQL to a resulting ESQL
   * @param explore A boolean function applied to the ESQL component to map with the mapping
   *                done only if this function returns true. If null is provided for this
   *                function, all ESQL components are mapped.
   */
  public Esql<?, ?> map(Function<Esql<?, ?>, Esql<?, ?>> mapper,
                        Predicate<Esql<?, ?>> explore) {
    return _map(copy(), mapper, explore, new HashSet<>());
  }

  public Esql<?, ?> map(Function<Esql<?, ?>, Esql<?, ?>> mapper) {
    return map(mapper, null);
  }

  private static Esql<?, ?> _map(Esql esql,
                                 Function<Esql<?, ?>, Esql<?, ?>> mapper,
                                 Predicate<Esql<?, ?>> explore,
                                 Set<Esql<?, ?>> explored) {
    if (explore == null || explore.test(esql)) {
      explored.add(esql);
      if (esql.value instanceof Esql && !explored.contains(esql.value)) {
        esql.value = _map((Esql)esql.value, mapper, explore, explored);

      } else if (esql.value instanceof List) {
        esql.value = ((List)esql.value).stream()
                                       .map(e -> e instanceof Esql && !explored.contains(e)
                                                 ? _map((Esql<?, ?>)e, mapper, explore, explored)
                                                 : e).collect(toList());
      }
      esql.children = ((Map<String, Esql<?, ?>>)esql.children)
          .entrySet().stream()
          .collect(toMap(Map.Entry::getKey,
              e -> !explored.contains(e)
                   ? _map(e.getValue(), mapper, explore, explored)
                   : e.getValue()));
      return mapper.apply(esql);
    } else {
      return esql;
    }
  }

  @Override
  public R translate(Target target) {
    return null;
  }

  /**
   * Returns this instance or its first ancestor, going up from the current parent,
   * which is an instance of the specified class. Returns null if no such instance
   * can be found.
   */
  public <T extends Esql<?, ?>> T ancestor(Class<T> cls) {
    if (cls.isAssignableFrom(getClass())) {
      return (T)this;
    } else if (parent != null) {
      return parent.ancestor(cls);
    } else {
      return null;
    }
  }

  public int ancestorDistance(Class<?> cls) {
    if (cls.isAssignableFrom(getClass())) {
      return 0;
    } else if (parent != null) {
      int parentLevel = parent.ancestorDistance(cls);
      if (parentLevel == -1) {
        return -1;
      } else {
        return parentLevel + 1;
      }
    } else {
      return -1;
    }
  }

  /**
   * Return the first child (or itself) of the specified type in the Esql.
   * Return null if no such child exist.
   */
  public <T extends Esql<?, ?>> T firstChild(Class<T> cls) {
    if (cls.isAssignableFrom(getClass())) {
      return (T)this;
    }

    if (value instanceof Esql<?, ?>) {
      T firstChild = ((Esql<?, ?>)value).firstChild(cls);
      if (firstChild != null) {
        return firstChild;
      }
    }

    if (value instanceof List) {
      for (Object v: (List<?>)value) {
        if (v instanceof Esql<?, ?>) {
          T firstChild = ((Esql<?, ?>)v).firstChild(cls);
          if (firstChild != null) {
            return firstChild;
          }
        }
      }
    }

    for (Esql<?, ?> child: children.values()) {
      if (child != null) {
        T firstChild = child.firstChild(cls);
        if (firstChild != null) {
          return firstChild;
        }
      }
    }

    return null;
  }

  /**
   * Replaces the current Esql value within the parent with the target value.
   * If the replacement was successful, returns the name of the children that
   * this Esql was known as by its parent. This name will now be associated to
   * the replacement Esql object in the parent's children map.
   */
  public String replaceWith(Esql<?, ?> replacement) {
    String thisChildName = null;
    if (parent != null) {
      for (Map.Entry<String, Esql<?, ?>> child: parent.children.entrySet()) {
        if (child.getValue() == this) {
          thisChildName = child.getKey();
          break;
        }
      }
      if (thisChildName != null) {
        parent.replaceWith(thisChildName, replacement);
      }
    }
    return thisChildName;
  }

  /**
   * Replaces the child with the specified name by the provided replacement,
   * returning the previous child with that name, or null of no such child
   * existed. If the childName is null, the value of this Esql is replaced
   * instead, returning the previous value held.
   */
  public Object replaceWith(String childName, Esql<?, ?> replacement) {
    replacement.parent = this;
    if (childName == null) {
      V oldValue = value;
      value = (V)replacement;
      return oldValue;
    } else {
      return children.put(childName, replacement);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Esql<?, ?> esql = (Esql<?, ?>)o;
    return Objects.equals(value, esql.value)
        && Objects.equals(children, esql.children);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value, children);
  }

  @Override
  public String toString() {
    StringBuilder st = new StringBuilder();
    _toString(st, 1, 2);
    return st.toString();
  }

  public void _toString(StringBuilder st, int level, int indent) {
    if (value != null) {
      st.append(value);
    }
    if (!children.isEmpty()) {
      st.append(" {\n");
      for (Map.Entry<String, Esql<?, ?>> c: children.entrySet()) {
        Esql<?, ?> v = c.getValue();
        if (v != null && v.value != null) {
          st.append(repeat(" ", level * indent))
            .append(c.getKey())
            .append(": ");
          c.getValue()._toString(st, level + 1, indent);
          st.append("\n");
        }
      }
      st.append(repeat(" ", (level > 0 ? level-1 : 0) * indent))
        .append("}");
    }
  }

  @Override
  public void close() {
//    if (!closed() && !closing()) {
//      try {
//        closing(true);
//        if (value instanceof Esql) {
//          ((Esql)value).close();
//
//        } else if (value instanceof List<?>) {
//          for (Object e: (List<?>)value) {
//            if (e instanceof Esql) {
//              ((Esql)e).close();
//            }
//          }
//        }
//        value = null;
//        for (Esql<?, ?> e: children.values()) {
//          e.close();
//        }
//        children.clear();
//        if (parent != null) {
//          parent.close();
//        }
//        parent = null;
//
//      } finally {
//        closing(false);
//        closed(true);
//      }
//    }
  }

  @Override
  public boolean closed() {
    return closed;
  }

  @Override
  public void closed(boolean closed) {
    this.closed = closed;
  }

  @Override
  public boolean closing() {
    return closing;
  }

  @Override
  public void closing(boolean closing) {
    this.closing = closing;
  }

  @Override
  public boolean copying() {
    return copying;
  }

  @Override
  public void copying(boolean copying) {
    this.copying = copying;
  }

  /**
   * Set to true while copying.
   */
  private volatile boolean copying;

  /**
   * Set to true while closing.
   */
  private volatile boolean closing;

  /*
   * True when the object is closed.
   */
  private volatile boolean closed;

  /**
   * The syntactic parent of the current statement or null if this is the
   * top statement. The syntactic parent is the containing statement and is
   * needed, for instance, in subqueries which can refer to the tables in the
   * parent query.
   */
  public Esql<?, ?> parent;

  /**
   * An arbitrary value associated with the Esql.
   */
  public volatile V value;

  /**
   * A map of children of this Esql node mapped to names. Leaf nodes will
   * have an empty map.
   */
  public Map<String, Esql<?, ?>> children = new LinkedHashMap<>();

  /**
   * Translation context.
   */
  public final Context context;
}