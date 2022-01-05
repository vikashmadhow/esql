/*
 * Copyright (c) 2020-2021 Vikash Madhow
 */

package ma.vi.esql.syntax;

import ma.vi.base.lang.NotFoundException;
import ma.vi.base.tuple.T2;
import ma.vi.esql.database.Database;
import ma.vi.esql.exec.Result;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.translator.TranslatorFactory;

import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import static java.util.Collections.emptyMap;
import static ma.vi.esql.semantic.type.Types.VoidType;
import static org.apache.commons.lang3.StringUtils.repeat;

/**
 * The node type in the parsed representation of ESQL statements. The
 * latter consists of a tree of Esql objects which can be further processed
 * by macros and other methods before being translated for execution on a
 * target database.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Esql<V, R> implements Copy<Esql<V, R>>, Translatable<R> {
  public Esql(Context context) {
    this.value = getDefaultValue();
    this.context = context;
  }

  public Esql(Context context, V value) {
    if (value instanceof Esql<?,?>) {
      throw new EsqlException(VALUE_OF_TYPE_ESQL_ERROR);
    }
    this.value = value;
    this.context = context;
  }

  @SafeVarargs
  public Esql(Context context, T2<String, ? extends Esql<?, ?>>... children) {
    this(context, null, children);
  }

  @SafeVarargs
  public Esql(Context context, V value, T2<String, ? extends Esql<?, ?>>... children) {
    if (value instanceof Esql<?,?>) {
      throw new EsqlException(VALUE_OF_TYPE_ESQL_ERROR);
    }
    if (value == null) {
      value = getDefaultValue();
    }
    this.value = value;
    this.context = context;
    int index = 0;
    childrenNames = new LinkedHashMap<>();
    for (T2<String, ? extends Esql<?, ?>> c: children) {
      childrenNames.put(c.a, index);
      this.children.add(c.b);
      index++;
    }
  }

  public Esql(Context context, Map<String, ? extends Esql<?, ?>> children) {
    this(context, null, children);
  }

  public Esql(Context context, V value, Map<String, ? extends Esql<?, ?>> children) {
    if (value instanceof Esql<?,?>) {
      throw new EsqlException(VALUE_OF_TYPE_ESQL_ERROR);
    }
    if (value == null) {
      value = getDefaultValue();
    }
    this.value = value;
    this.context = context;
    int index = 0;
    childrenNames = new LinkedHashMap<>();
    for (Map.Entry<String, ? extends Esql<?, ?>> e: children.entrySet()) {
      childrenNames.put(e.getKey(), index);
      this.children.add(index, e.getValue());
      index++;
    }
  }

  public Esql(Context context, List<? extends Esql<?, ?>> children, Boolean useIndexAsChildrenNames) {
    this(context, null, children, useIndexAsChildrenNames);
  }

  public Esql(Context context, V value, List<? extends Esql<?, ?>> children) {
    this(context, value, children, null);
  }

  public Esql(Context context, V value, List<? extends Esql<?, ?>> children, Boolean useIndexAsChildrenNames) {
    if (value instanceof Esql<?,?>) {
      throw new EsqlException(VALUE_OF_TYPE_ESQL_ERROR);
    }
    if (value == null) {
      value = getDefaultValue();
    }
    this.value = value;
    this.context = context;
    if (children != null) {
      int index = 0;
      childrenNames = new LinkedHashMap<>();
      boolean hasDuplicateNames = useIndexAsChildrenNames != null && useIndexAsChildrenNames;
      if (!hasDuplicateNames) {
        for (Esql<?, ?> c: children) {
          if (c.value == null || childrenNames.containsKey(c.value.toString())) {
            hasDuplicateNames = true;
            break;
          }
          childrenNames.put(c.value.toString(), index);
          index++;
        }
      }
      index = 0;
      if (hasDuplicateNames) {
        childrenNames.clear();
        for (Esql<?, ?> c: children) {
          childrenNames.put(String.valueOf(index), index);
          this.children.add(c);
          index++;
        }
      } else {
        for (Esql<?, ?> c: children) {
          childrenNames.put(c.value.toString(), index);
          this.children.add(c);
          index++;
        }
      }
    }
  }

  public Esql(Esql<V, R> other) {
    this.context = other.context;
    this.value = other.value;
    this.childrenNames = other.childrenNames;
    this.children.addAll(other.children);
  }

  @SafeVarargs
  public Esql(Esql<V, R> other, T2<String, ? extends Esql<?, ?>>... children) {
    this(other, null, children);
  }

  @SafeVarargs
  public Esql(Esql<V, R> other, V value, T2<String, ? extends Esql<?, ?>>... children) {
    if (value instanceof Esql<?,?>) {
      throw new EsqlException(VALUE_OF_TYPE_ESQL_ERROR);
    }
    if (value == null) {
      value = getDefaultValue();
    }
    this.context = other.context;
    this.value = value;
    this.childrenNames = other.childrenNames;
    this.children.addAll(other.children);
    for (T2<String, ? extends Esql<?, ?>> child: children) {
      this.children.set(this.indexOf(child.a), child.b);
    }
  }

  /**
   * Returns a shallow copy of this object. The copy will have a separate children
   * list pointing to the same children as this object.
   */
  public Esql<V, R> copy() {
    return new Esql<>(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  public Esql<V, R> copy(V value, T2<String, ? extends Esql<?, ?>>... children) {
    return new Esql<>(this, value, children);
  }

  private V getDefaultValue() {
    java.lang.reflect.Type type = getClass().getGenericSuperclass();
    if (type instanceof ParameterizedType p) {
      if (p.getActualTypeArguments()[0] instanceof Class<?> c) {
        return c.isAssignableFrom(String.class) ? (V)getClass().getSimpleName() : null;
      }
    }
    return null;
  }

  public <T extends Esql<?, ?>> List<T> children() {
    return (List<T>)Collections.unmodifiableList(children);
  }

  public <T extends Esql<?, ?>> Map<String, T> childrenMap() {
    Map<String, Integer> names = childrenNames();
    Map<String, T> children = new LinkedHashMap<>(names.size());
    for (Map.Entry<String, Integer> e: names.entrySet()) {
      children.put(e.getKey(), (T)this.children.get(e.getValue()));
    }
    return children;
  }

  public final Map<String, Integer> childrenNames() {
    if (childrenNames == null) {
      childrenNames = Collections.unmodifiableMap(nodeChildrenNames());
    }
    return childrenNames;
  }

  protected Map<String, Integer> nodeChildrenNames() {
    Map<String, Integer> names = new LinkedHashMap<>();
    for (int i = 0; i < childrenCount(); i++) {
      names.put(String.valueOf(i), i);
    }
    return names;
  }

  public int childrenCount() {
    return children.size();
  }

  public int indexOf(String childName) {
    int index = _indexOf(childName);
    if (index == -1) {
      throw new NotFoundException("Unknown child: " + childName);
    }
    return index;
  }

  private int _indexOf(String childName) {
    if (childrenNames().containsKey(childName)) {
      return childrenNames().get(childName);
    } else {
      try {
        int index = Integer.parseInt(childName);
        if (children.size() <= index) {
          return -1;
        }
        return index;
      } catch (NumberFormatException nfe) {
        return -1;
      }
    }
  }

  public boolean has(String childName) {
    return _indexOf(childName) != -1;
  }

  public <T extends Esql<?, ?>> T child(String child) {
    return get(indexOf(child));
  }

  public <T> T childValue(String child) {
    Esql<?, ?> e = get(indexOf(child));
    return e == null ? null : (T)get(indexOf(child)).value;
  }

  public <T extends Esql<?, ?>> T get(int child) {
    return (T)children.get(child);
  }

  public Esql<?, ?> get(String path) {
    return get(path.split("/"));
  }

  public Esql<?, ?> get(String... path) {
    Esql<?, ?> node = this;
    for (String p: path) {
      if (p != null && p.length() > 0) {
        node = node.get(node.indexOf(p));
      }
    }
    return node;
  }

  public <T extends Esql<V, R>> T set(V value) {
    return (T)copy(value);
  }

  public <T extends Esql<V, R>> T set(String childName, Esql<?, ?> child) {
    if (has(childName)) {
      return set(indexOf(childName), child);
    } else {
      return set(children.size(), child);
    }
  }

  public <T extends Esql<V, R>> T set(int index, Esql<?, ?> child) {
    Esql<V, R> copy = copy();
    while (copy.children.size() <= index) {
      copy.children.add(null);
    }
    copy.children.set(index, child);
    return (T)copy;
  }

  public <T extends Esql<V, R>> T setPath(String path, Esql<?, ?> child) {
    return _setPath(path.split("/"), 0, child);
  }

  private <T extends Esql<V, R>> T _setPath(String[] path,
                                            int indexInPath,
                                            Esql<?, ?> child) {
    while (indexInPath < path.length
       && (path[indexInPath] == null || path[indexInPath].length() == 0)) {
      indexInPath++;
    }
    if (indexInPath < path.length) {
      Esql<V, R> copy = this.copy();
      int childIndex = indexOf(path[indexInPath]);
      Esql<?, ?> childCopy = copy.get(childIndex);
      if (childCopy != null) {
        Esql<?, ?> rep = childCopy._setPath(path, indexInPath++, child);
        copy.children.set(childIndex, rep);
      }
      return (T)copy;
    } else {
      return (T)child;
    }
  }

  /**
   * Return the first child (or itself) of the specified type in the Esql tree.
   * Return null if no such child exist.
   */
  public <T> T descendent(Class<T> cls) {
    if (cls.isAssignableFrom(getClass())) {
      return (T)this;
    }
    for (Esql<?, ?> child: children) {
      if (child != null) {
        T firstChild = child.descendent(cls);
        if (firstChild != null) {
          return firstChild;
        }
      }
    }
    return null;
  }

  public boolean forEach(BiFunction<Esql<?, ?>, EsqlPath, Boolean> visitor) {
    return forEach(visitor, null);
  }

  /**
   * Executes the visitor function for each Esql children of this Esql object
   * and itself in depth-first post-order. If the visitor returns false on any
   * Esql part, the visit is terminated.
   */
  public boolean forEach(BiFunction<Esql<?, ?>, EsqlPath, Boolean> visitor,
                         Predicate<Esql<?, ?>> explore) {
    return _forEach(visitor, explore, new EsqlPath(this));
  }

  /**
   * Executes the visitor function for each Esql children of this Esql object
   * and itself in depth-first post-order. If the visitor returns false on any
   * Esql part, the visit is terminated.
   */
  private boolean _forEach(BiFunction<Esql<?, ?>, EsqlPath, Boolean> visitor,
                           Predicate<Esql<?, ?>> explore,
                           EsqlPath path) {
    if (explore == null || explore.test(this)) {
      for (Esql<?, ?> child: children) {
        if (child != null) {
          if (!child._forEach(visitor, explore, path.add(child))) {
            return false;
          }
        }
      }
      return visitor.apply(this, path);
    } else {
      return true;
    }
  }

  public <T extends Esql<V, R>> T map(BiFunction<Esql<?, ?>, EsqlPath, Esql<?, ?>> mapper) {
    return map(mapper, null);
  }

  /**
   * Maps the ESQL tree to another tree where modified nodes (and all their
   * ancestors) are copied and non-modified nodes are shared between this tree
   * and the mapped tree. This method allows for the ESQL tree to be immutable
   * with all mutating operations creating a new persistent tree.
   *
   * @param mapper
   * @param explore
   * @return
   */
  public <T extends Esql<V, R>> T map(BiFunction<Esql<?, ?>, EsqlPath, Esql<?, ?>> mapper,
                                      Predicate<Esql<?, ?>> explore) {
    return map(mapper, explore, new EsqlPath(this));
  }

  /**
   * <p>
   * Maps the ESQL tree to another tree where modified nodes (and all their
   * ancestors) are copied and non-modified nodes are shared between this tree
   * and the mapped tree. This method allows for the ESQL tree to be immutable
   * with all mutating operations creating a new persistent tree.
   * </p>
   *
   * <p>
   * Explore the ESQL in depth-first order. For breadth-first mapping, use
   * {@link #bfsMap(BiFunction, Predicate, EsqlPath)}
   * </p>
   *
   * @param mapper
   * @param explore
   * @return
   */
  public <T extends Esql<V, R>> T map(BiFunction<Esql<?, ?>, EsqlPath, Esql<?, ?>> mapper,
                                      Predicate<Esql<?, ?>> explore,
                                      EsqlPath path) {
    if (explore == null || explore.test(this)) {
      Esql<V, R> copy = null;
      for (int i = 0; i < children.size(); i++) {
        Esql<?, ?> child = children.get(i);
        if (child != null) {
          Esql<?, ?> mappedChild = child.map(mapper, explore, path.add(child));
          if (mappedChild != child) {
            if (copy == null) {
              copy = copy();
              path = path.replaceHead(copy);   // replace head of path with its copy
            }
            copy.children.set(i, mappedChild);
          }
        }
      }
      return (T)mapper.apply(copy != null ? copy : this, path);
    }
    return (T)this;
  }

  public <T extends Esql<V, R>> T bfsMap(BiFunction<Esql<?, ?>, EsqlPath, Esql<?, ?>> mapper) {
    return bfsMap(mapper, null);
  }

  public <T extends Esql<V, R>> T bfsMap(BiFunction<Esql<?, ?>, EsqlPath, Esql<?, ?>> mapper,
                                         Predicate<Esql<?, ?>> explore) {
    return bfsMap(mapper, explore, new EsqlPath(this));
  }

  public <T extends Esql<V, R>> T bfsMap(BiFunction<Esql<?, ?>, EsqlPath, Esql<?, ?>> mapper,
                                         Predicate<Esql<?, ?>> explore,
                                         EsqlPath path) {
    if (explore == null || explore.test(this)) {
      Esql<V, R> mapped = (Esql<V, R>)mapper.apply(this, path);
      if (mapped == null) {
        return null;
      }
      Esql<V, R> copy = null;
      for (int i = 0; i < mapped.children.size(); i++) {
        Esql<?, ?> child = mapped.children.get(i);
        if (child != null) {
          Esql<?, ?> mappedChild = child.bfsMap(mapper, explore, path.add(child));
          if (mappedChild != child) {
            if (copy == null) {
              copy = mapped == this ? mapped.copy() : mapped;
              path = path.replaceHead(copy);   // replace head of path with its copy
            }
            copy.children.set(i, mappedChild);
          }
        }
      }
      return (T)(copy != null ? copy : mapped);
    }
    return (T)this;
  }

  public <T extends Esql<?, ?>> T replace(Esql<?, ?> searchFor, Esql<?, ?> replaceWith) {
    return (T)map((e, path) -> e == searchFor ? replaceWith : e);
  }

  /**
   * The type of expressions produced when the statement is executed.
   * Default is the {@link Type#Void} type representing the case that
   * the expression does not produce anything.
   */
  public Type type(EsqlPath path) {
    return VoidType;
  }

  @Override
  public final R translate(Target target) {
    return translate(target, new EsqlPath(this), emptyMap());
  }

  @Override
  public final R translate(Target target, EsqlPath path, Map<String, Object> parameters) {
    return trans(target, path, parameters);
  }

  protected R trans(Target target, EsqlPath path, Map<String, Object> parameters) {
    return TranslatorFactory.get(target).translate(this, path, parameters);
  }

  public Result execute(Database db, Connection con, EsqlPath path) {
    return Result.Nothing;
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
      Map<String, Integer> childrenNames = childrenNames();
      for (Map.Entry<String, Integer> e: childrenNames.entrySet()) {
        Esql<?, ?> child = children.get(e.getValue());
        if (child != null) {
          st.append(repeat(" ", level * indent))
            .append(e.getKey())
            .append(": ");
          child._toString(st, level + 1, indent);
          st.append("\n");
        }
      }
      st.append(repeat(" ", (level > 0 ? level-1 : 0) * indent))
        .append("}");
    }
  }

  /**
   * The value associated with this Esql node.
   */
  public final V value;

  /**
   * A list of children of this Esql node.
   */
  private final List<Esql<?, ?>> children = new ArrayList<>(0);

  private Map<String, Integer> childrenNames;

  /**
   * Translation context.
   */
  public final Context context;

  private static final String VALUE_OF_TYPE_ESQL_ERROR = """
      Value of an ESQL object is not allowed to be of ESQL type. All ESQL parameters \
      of an ESQL object should be set as its children which can then participate \
      in the different mechanisms implemented in the ESQL class such as mapping.""";
}