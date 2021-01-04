/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.base.util.Convert;
import ma.vi.esql.parser.Context;
import ma.vi.esql.type.Type;
import ma.vi.esql.type.Types;

import java.lang.reflect.Array;
import java.util.List;

import static java.util.stream.Collectors.joining;
import static ma.vi.base.collections.ArrayUtils.ARRAY_ESCAPE;

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

  @Override
  public BaseArrayLiteral copy() {
    if (!copying()) {
      try {
        copying(true);
        return new BaseArrayLiteral(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public Type type() {
    return componentType().array();
  }

  @Override
  public String translate(Target target) {
    return switch (target) {
      case POSTGRESQL
          -> "array[" + items().stream()
                               .map(e -> e.translate(target))
                               .collect(joining(",")) + "]::" + componentType().array().translate(target);

      case HSQLDB
          -> "array[" + items().stream()
                               .map(e -> e.translate(target))
                               .collect(joining(",")) + "]";

      case SQLSERVER
          -> items().stream()
                    .map(e -> e instanceof StringLiteral
                                  ? ARRAY_ESCAPE.escape((String)e.value(target))
                                  : ARRAY_ESCAPE.escape(e.translate(target)))
                    .collect(joining(",", "N'[", "]'"));

      case JSON, JAVASCRIPT
          -> items().stream()
                    .map(e -> e.translate(target))
                    .collect(joining(",", "[", "]"));

      default
          -> componentType().translate(Target.ESQL)
               + items().stream()
                        .map(e -> ARRAY_ESCAPE.escape(e.translate(target)))
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
    return childrenList();
  }
}