/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.literal;

import ma.vi.base.tuple.T2;
import ma.vi.base.util.Convert;
import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import org.pcollections.PMap;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.joining;
import static ma.vi.base.collections.ArrayUtils.ARRAY_ESCAPE;
import static ma.vi.base.util.Convert.convert;
import static ma.vi.esql.translation.Translatable.Target.SQLSERVER;

/**
 * A simple array literal consisting only of items of base types.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class BaseArrayLiteral extends Literal<Type> {
  public BaseArrayLiteral(Context context, Type componentType, List<BaseLiteral<?>> items) {
    super(context, componentType, items == null ? Collections.emptyList() : items);
  }

  public BaseArrayLiteral(BaseArrayLiteral other) {
    super(other);
  }

  @SafeVarargs
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
  public BaseArrayLiteral copy(Type value, T2<String, ? extends Esql<?, ?>>... children) {
    return new BaseArrayLiteral(this, value, children);
  }

  @Override
  public Type computeType(EsqlPath path) {
    return componentType().array();
  }

  @Override
  protected String trans(Target target, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env) {
    return switch (target) {
      case POSTGRESQL
          -> "array[" + items().stream()
                               .map(e -> e.translate(target, esqlCon, path.add(e), parameters, env))
                               .collect(joining(",")) + "]::" + componentType().array().translate(target, esqlCon, path, parameters, env);

      case HSQLDB
          -> "array[" + items().stream()
                               .map(e -> e.translate(target, esqlCon, path.add(e), parameters, env))
                               .collect(joining(",")) + "]";

      case SQLSERVER, MARIADB, MYSQL
          -> items().stream()
                    .map(e -> e instanceof StringLiteral
                                  ? ARRAY_ESCAPE.escape((String)e.exec(target, esqlCon, path, env))
                                  : ARRAY_ESCAPE.escape(e.translate(target, esqlCon, path.add(e), parameters, env)))
                    .collect(joining(",", (target == SQLSERVER ? "N" : "") + "'[", "]'"));

      case JSON, JAVASCRIPT
          -> items().stream()
                    .map(e -> e.translate(target, esqlCon, path.add(e), parameters, env))
                    .collect(joining(",", "[", "]"));

      default
          -> items().stream()
                    .map(e -> ARRAY_ESCAPE.escape(e.translate(target, esqlCon, path.add(e), parameters, env)))
                    .collect(joining(",", "[", "]"))
           + componentType().translate(Target.ESQL, esqlCon, path, parameters, env);
    };
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    st.append('[');
    boolean first = true;
    for (BaseLiteral<?> e: items()) {
      if (first) { first = false; }
      else       { st.append(", "); }
      e._toString(st, level, indent);
    }
    st.append(']').append(componentType().name());
  }

  @Override
  public Object exec(Target         target,
                     EsqlConnection esqlCon,
                     EsqlPath       path,
                     Environment    env) {
    Class<?> componentType = Types.classOf(componentType().name());
    List<BaseLiteral<?>> items = items();
    Object array = Array.newInstance(componentType, items.size());
    for (int i = 0; i < items.size(); i++) {
      BaseLiteral<?> item = items.get(i);
      Array.set(array, i, convert(item.exec(target, esqlCon, path.add(item), env),
                                  componentType));
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