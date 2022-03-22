/*
 * Copyright (c) 2020-2021 Vikash Madhow
 */

package ma.vi.esql.syntax;

import ma.vi.base.lang.NotFoundException;
import ma.vi.base.tuple.T1;
import ma.vi.base.tuple.T2;
import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.exec.Filter;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.semantic.scope.Scope;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.syntax.macro.Macro;
import ma.vi.esql.translation.Translatable;
import ma.vi.esql.translation.TranslationException;
import ma.vi.esql.translation.TranslatorFactory;
import org.pcollections.HashPMap;
import org.pcollections.IntTreePMap;
import org.pcollections.PMap;

import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import static ma.vi.esql.semantic.type.Types.UnknownType;
import static ma.vi.esql.syntax.macro.Macro.ONGOING_MACRO_EXPANSION;
import static org.apache.commons.lang3.StringUtils.repeat;

/**
 * The node type in the parsed representation of ESQL statements. The
 * latter consists of a tree of Esql objects which can be further processed
 * by macros and other methods before being translated for execution on a
 * target database.
 * 
 * @param <V> The type of value held.
 * @param <T> The type that this ESQL node is transated to.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Esql<V, T> implements Copy<Esql<V, T>>, Translatable<T> {
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
          if (c != null) {
            if (c.value == null || childrenNames.containsKey(c.value.toString())) {
              hasDuplicateNames = true;
              break;
            }
            childrenNames.put(c.value.toString(), index);
            index++;
          }
        }
      }
      index = 0;
      if (hasDuplicateNames) {
        childrenNames.clear();
        for (Esql<?, ?> c: children) {
          if (c != null) {
            childrenNames.put(String.valueOf(index), index);
            this.children.add(c);
            index++;
          }
        }
      } else {
        for (Esql<?, ?> c: children) {
          if (c != null) {
            childrenNames.put(c.value.toString(), index);
            this.children.add(c);
            index++;
          }
        }
      }
    }
  }

  public Esql(Esql<V, T> other) {
    this.context = other.context;
    this.value = other.value;
    this.childrenNames = other.childrenNames;
    this.children.addAll(other.children);
  }

  @SafeVarargs
  public Esql(Esql<V, T> other, T2<String, ? extends Esql<?, ?>>... children) {
    this(other, null, children);
  }

  @SafeVarargs
  public Esql(Esql<V, T> other, V value, T2<String, ? extends Esql<?, ?>>... children) {
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
  public Esql<V, T> copy() {
    return new Esql<>(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  public Esql<V, T> copy(V value, T2<String, ? extends Esql<?, ?>>... children) {
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

  public <X extends Esql<?, ?>> List<X> children() {
    return (List<X>)Collections.unmodifiableList(children);
  }

  public <X extends Esql<?, ?>> Map<String, X> childrenMap() {
    Map<String, Integer> names = childrenNames();
    Map<String, X> children = new LinkedHashMap<>(names.size());
    for (Map.Entry<String, Integer> e: names.entrySet()) {
      children.put(e.getKey(), (X)this.children.get(e.getValue()));
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

  public <X extends Esql<?, ?>> X child(String child) {
    return get(indexOf(child));
  }

  public <X> X childValue(String child) {
    Esql<?, ?> e = get(indexOf(child));
    return e == null ? null : (X)get(indexOf(child)).value;
  }

  public <X extends Esql<?, ?>> X get(int child) {
    return (X)children.get(child);
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

  public <X extends Esql<V, T>> X set(V value) {
    return (X)copy(value);
  }

  public <X extends Esql<V, T>> X set(String childName, Esql<?, ?> child) {
    if(has(childName)) {
      return set(indexOf(childName), child);
    } else {
      childrenNames.put(childName, children().size());
      return set(children.size(), child);
    }
  }

  public <X extends Esql<V, T>> X set(int index, Esql<?, ?> child) {
    Esql<V, T> copy = copy();
    while (copy.children.size() <= index) {
      copy.children.add(null);
    }
    copy.children.set(index, child);
    return (X)copy;
  }

  protected void _set(String childName, Esql<?, ?> child) {
    if(has(childName)) {
      _set(indexOf(childName), child);
    } else {
      childrenNames.put(childName, children().size());
      _set(children.size(), child);
    }
  }

  protected void _set(int index, Esql<?, ?> child) {
    while (children.size() <= index) {
      children.add(null);
    }
    children.set(index, child);
  }

  public <X extends Esql<V, T>> X setPath(String path, Esql<?, ?> child) {
    return setPath(path.split("/"), 0, child);
  }

  private <X extends Esql<V, T>> X setPath(String[] path,
                                           int indexInPath,
                                           Esql<?, ?> child) {
    while (indexInPath < path.length
       && (path[indexInPath] == null || path[indexInPath].length() == 0)) {
      indexInPath++;
    }
    if (indexInPath < path.length) {
      Esql<V, T> copy = this.copy();
      int childIndex = indexOf(path[indexInPath]);
      Esql<?, ?> childCopy = copy.get(childIndex);
      if (childCopy != null) {
        Esql<?, ?> rep = childCopy.setPath(path, indexInPath++, child);
        copy.children.set(childIndex, rep);
      }
      return (X)copy;
    } else {
      return (X)child;
    }
  }

  /**
   * Return the first child (or itself) of the specified type in the Esql tree.
   * Return null if no such child exist.
   */
  public <X> X descendent(Class<X> cls) {
    if (cls.isAssignableFrom(getClass())) {
      return (X)this;
    }
    for (Esql<?, ?> child: children) {
      if (child != null) {
        X firstChild = child.descendent(cls);
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

  public <X extends Esql<?, ?>> X find(Predicate<Esql<?, ?>> predicate) {
    T1<X> found = new T1<>(null);
    forEach((e, path) -> {
      if (predicate.test(e)) {
        found.a = (X)e;
        return false;
      }
      return true;
    } , null);
    return found.a;
  }

  public <X extends Esql<?, ?>> X find(Class<X> cls) {
    return find(e -> e.getClass().equals(cls));
  }

  public <X extends Esql<V, T>> X map(BiFunction<Esql<?, ?>, EsqlPath, Esql<?, ?>> mapper) {
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
  public <X extends Esql<V, T>> X map(BiFunction<Esql<?, ?>, EsqlPath, Esql<?, ?>> mapper,
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
  public <X extends Esql<V, T>> X map(BiFunction<Esql<?, ?>, EsqlPath, Esql<?, ?>> mapper,
                                      Predicate<Esql<?, ?>> explore,
                                      EsqlPath path) {
    if (explore == null || explore.test(this)) {
      Esql<V, T> copy = null;
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
      return (X)mapper.apply(copy != null ? copy : this, path);
    }
    return (X)this;
  }

  public <X extends Esql<V, T>> X bfsMap(BiFunction<Esql<?, ?>, EsqlPath, Esql<?, ?>> mapper) {
    return bfsMap(mapper, null);
  }

  public <X extends Esql<V, T>> X bfsMap(BiFunction<Esql<?, ?>, EsqlPath, Esql<?, ?>> mapper,
                                         Predicate<Esql<?, ?>> explore) {
    return bfsMap(mapper, explore, new EsqlPath(this));
  }

  public <X extends Esql<V, T>> X bfsMap(BiFunction<Esql<?, ?>, EsqlPath, Esql<?, ?>> mapper,
                                         Predicate<Esql<?, ?>> explore,
                                         EsqlPath path) {
    if (explore == null || explore.test(this)) {
      Esql<V, T> mapped = (Esql<V, T>)mapper.apply(this, path);
      if (mapped == null) {
        return null;
      }
      Esql<V, T> copy = null;
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
      return (X)(copy != null ? copy : mapped);
    }
    return (X)this;
  }

  public <X extends Esql<?, ?>> X replace(Esql<?, ?> searchFor, Esql<?, ?> replaceWith) {
    return (X)map((e, path) -> e == searchFor ? replaceWith : e);
  }

  /**
   * The type of expressions produced when the statement is executed. Default is
   * the {@link ma.vi.esql.semantic.type.Types#UnknownType} type representing the
   * case that the expression does not produce anything.
   */
  public Type computeType(EsqlPath path) {
    return type;
  }

  @Override
  public final T translate(Target target) {
    return translate(target, null, new EsqlPath(this), HashPMap.empty(IntTreePMap.empty()), null);
  }

  @Override
  public final T translate(Target               target,
                           EsqlConnection       esqlCon,
                           EsqlPath             path,
                           PMap<String, Object> parameters,
                           Environment          env) {
    return trans(target, esqlCon, path, parameters, env);
  }

  protected T trans(Target               target,
                    EsqlConnection       esqlCon,
                    EsqlPath             path,
                    PMap<String, Object> parameters,
                    Environment          env) {
    return TranslatorFactory.get(target).translate(this, esqlCon, path, parameters, env);
  }

  /**
   * Places symbols in the ESQL node in the current scope and/or creates children
   * scopes as needed.
   */
  public Esql<?, ?> scope(Scope scope, EsqlPath path) {
    this.scope = scope;
    return this;
  }

  public Type type() {
    return type;
  }

  public Esql<?, ?> type(Type type) {
    this.type = type;
    return this;
  }

  /**
   * Executes this ESQL node and returns its result. The default returns null.
   */
  @Override
  public Object exec(Target               target,
                     EsqlConnection       esqlCon,
                     EsqlPath             path,
                     PMap<String, Object> parameters,
                     Environment          env) {
    Esql<?, ?> esql = preExecTransform(target, esqlCon, path, parameters, env);
    return esql.postTransformExec(target, esqlCon, path.replaceHead(esql), parameters, env);
  }

  protected Esql<?, ?> preExecTransform(Target               target,
                                        EsqlConnection       esqlCon,
                                        EsqlPath             path,
                                        PMap<String, Object> parameters,
                                        Environment          env) {
    return this;
  }

  protected Object postTransformExec(Target               target,
                                     EsqlConnection       esqlCon,
                                     EsqlPath             path,
                                     PMap<String, Object> parameters,
                                     Environment          env) {
    return null;
  }

  private static <T extends Esql<?, ?>,
                  M extends Macro> T expand(T esql, Class<M> macroType) {
    T expanded;
    int iteration = 0;
    while ((expanded = (T)esql.map((e, p) -> macroType.isAssignableFrom(e.getClass())
                                             ? ((Macro)e).expand(e, p.add(ONGOING_MACRO_EXPANSION))
                                             : e)) != esql) {
      esql = expanded;
      iteration++;
      if (iteration > 50) {
        throw new TranslationException(
            "Macro expansion continued for more than 50 iterations and was stopped. "
          + "A macro could be expanding recursively into other macros. Esql: " + esql);
      }
    }
    return expanded;
  }

  /**
   * Transforms this ESQL by applying the supplied filter to it. The default
   * version returns the ESQL as-is.
   *
   * @param filter Filter to apply.
   * @param path The path to this ESQL providing context on the surrounding expressions.
   * @return The ESQL resulting from applying the filter to this one.
   */
  public Esql<V, T> filter(Filter filter, EsqlPath path) {
    return this;
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
        st.append(repeat(" ", level * indent))
          .append(e.getKey())
          .append(": ");
        int index = e.getValue();
        if (index >= 0 && index < children.size()) {
          Esql<?, ?> child = children.get(e.getValue());
          if (child != null) {
            child._toString(st, level + 1, indent);
          }
        }
        st.append("\n");
      }
      st.append(repeat(" ", (level > 0 ? level-1 : 0) * indent))
        .append("}");
    }
  }

  public Scope scope() {
    return scope;
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

  /**
   * Scope of this esql node.
   */
  protected transient Scope scope;

  /**
   * Type of this esql node. This is set to {@link ma.vi.esql.semantic.type.Types#UnknownType}
   * initially and resolved during scoping and type computation.
   */
  protected transient Type type = UnknownType;

  /**
   * The line number where this node was defined in the input; used for debugging
   * purposes only.
   */
  public int line;

  private static final String VALUE_OF_TYPE_ESQL_ERROR = """
      Value of an ESQL object is not allowed to be of ESQL type. All ESQL parameters \
      of an ESQL object should be set as its children which can then participate \
      in the different mechanisms implemented in the ESQL class such as mapping.""";
}