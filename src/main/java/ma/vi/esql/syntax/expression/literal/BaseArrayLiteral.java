/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.literal;

import ma.vi.base.tuple.T2;
import ma.vi.base.util.Convert;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.DefaultValue;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.joining;
import static ma.vi.base.collections.ArrayUtils.ARRAY_ESCAPE;
import static ma.vi.esql.syntax.Translatable.Target.SQLSERVER;

/**
 * A simple array literal consisting only of items of base types.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class BaseArrayLiteral extends Literal<Type> {
  public BaseArrayLiteral(Context context, Type componentType, List<BaseLiteral<?>> items) {
    super(context, componentType, items);
  }

  public BaseArrayLiteral(BaseArrayLiteral other) {
    super(other);
  }

  public BaseArrayLiteral(BaseArrayLiteral other, Type value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public BaseArrayLiteral copy() {
    return new BaseArrayLiteral(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public BaseArrayLiteral copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new BaseArrayLiteral(this, value, children);
  }

  @Override
  public Type type() {
    return componentType().array();
  }

  @Override
  protected String trans(Target target, EsqlPath path, Map<String, Object> parameters) {
    return switch (target) {
      case POSTGRESQL
          -> "array[" + items().stream()
                               .map(e -> e.translate(target, path.add(e), parameters))
                               .collect(joining(",")) + "]::" + componentType().array().translate(target, parameters);

      case HSQLDB
          -> "array[" + items().stream()
                               .map(e -> e.translate(target, path.add(e), parameters))
                               .collect(joining(",")) + "]";

      case SQLSERVER, MARIADB, MYSQL
          -> items().stream()
                    .map(e -> e instanceof StringLiteral
                                  ? ARRAY_ESCAPE.escape((String)e.value(target))
                                  : ARRAY_ESCAPE.escape(e.translate(target, path.add(e), parameters)))
                    .collect(joining(",", (target == SQLSERVER ? "N" : "") + "'[", "]'"));

      case JSON, JAVASCRIPT
          -> items().stream()
                    .map(e -> e.translate(target, path.add(e), parameters))
                    .collect(joining(",", "[", "]"));

      default
          -> componentType().translate(Target.ESQL, path, parameters)
               + items().stream()
                        .map(e -> ARRAY_ESCAPE.escape(e.translate(target, path.add(e), parameters)))
                        .collect(joining(",", "[", "]"));
    };
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    st.append(componentType().name()).append('[');
    boolean first = true;
    for (BaseLiteral<?> e: items()) {
      if (first) { first = false; }
      else       { st.append(", "); }
      e._toString(st, level, indent);
    }
    st.append(']');
  }

  @Override
  public Object value(Target target) {
    Class<?> componentType = Types.classOf(componentType().name());
    List<BaseLiteral<?>> items = items();
    Object array = Array.newInstance(componentType, items.size());
    for (int i = 0; i < items.size(); i++) {
      BaseLiteral<?> item = items.get(i);
      Array.set(array, i, Convert.convert(item, componentType));
    }
    return array;
  }

  public Type componentType() {
    return value;
  }

  public List<BaseLiteral<?>> items() {
    return children();
  }
}