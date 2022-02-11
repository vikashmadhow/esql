/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.exec.variable;

import ma.vi.base.reflect.Dissector;
import ma.vi.base.reflect.Property;
import ma.vi.base.tuple.T2;
import ma.vi.base.util.Classes;
import ma.vi.base.util.Numbers;
import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.exec.ExecutionException;
import ma.vi.esql.exec.Result;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import org.pcollections.PMap;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.joining;

/**
 * A member selector of the form a[member] where `member` is selected on `a`.
 * Both the member and the object that the selection is performed on can be
 * arbitrary expressions.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Selector extends Expression<String, Selector> {
  public Selector(Context                context,
                  Expression<?, ?>       on,
                  List<Expression<?, ?>> members) {
    super(context, "Selector",
          T2.of("on",      on),
          T2.of("members", new Esql<>(context, "members", members, true)));
  }

  public Selector(Selector other) {
    super(other);
  }

  @SafeVarargs
  public Selector(Selector other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public Selector copy() {
    return new Selector(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  public Selector copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new Selector(this, value, children);
  }

  @Override
  public Selector trans(Target               target,
                        EsqlConnection       esqlCon,
                        EsqlPath             path,
                        PMap<String, Object> parameters,
                        Environment          env) {
    return this;
  }

  @Override
  public Object exec(Target         target,
                     EsqlConnection esqlCon,
                     EsqlPath       path,
                     Environment    env) {
    Object on = on().exec(target, esqlCon, path.add(on()), env);
    EsqlPath extPath = path.add(on());
    List<?> memberList = members().stream()
                                  .map(m -> m.exec(target, esqlCon, extPath.add(m), env))
                                  .toList();
    if (on == null) {
      throw new ExecutionException(this, "Object to select from is null: " + on());
    }

    if (on instanceof BoundMethods m) {
      return m.invoke(memberList);

    } else if (memberList.size() == 1) {
      Object member = memberList.get(0);
      if (member instanceof Number i) {
        int index = i.intValue();
        if      (on.getClass().isArray())    { return Array.get(on, index-1); }
        else if (on instanceof List       l) { return l.get(index-1);       }
        else if (on instanceof Result     r) { return r.value(index);       }
        else if (on instanceof Result.Row r) { return r.value(index);       }
        else if (on instanceof Map        m) { return m.get(i.intValue());  }
        else {
          throw new ExecutionException(this,
              "Selection by numeric index is only supported on arrays, lists, maps "
            + "and results. The selection was performed on a " + on.getClass().getSimpleName() + '.');
        }
      } else if (member instanceof String name) {
        if      (on instanceof Map m)                             { return m.get(name);   }
        else if (on instanceof Result r && r.hasColumn(name))     { return r.value(name); }
        else if (on instanceof Result.Row r && r.hasColumn(name)) { return r.value(name); }
        else {
          Optional<Property> p = Dissector.property(on.getClass(), name);
          if (p.isPresent()) {
            return p.get().get(on);
          } else {
            Optional<Method> method = Dissector.method(on.getClass(), name);
            if (method.isPresent()) {
              try {
                return method.get().invoke(on);
              } catch (Exception e) {
                throw new ExecutionException(this, "Could not execute method " + name
                    + " on " + on + ": " + e.getMessage(), e);
              }
            } else {
              List<Method> methods = Dissector.methods(on.getClass(), name);
              if (!methods.isEmpty()) {
                return new BoundMethods(on, name, methods);
              } else {
                throw new ExecutionException(this,
                   "Selection by string is supported on maps, results, properties and "
                 + "methods on arbitrary classes. The selection was performed on "
                 + "class " + on.getClass().getSimpleName() + " which does not have "
                 + "a field, property or method named " + name + '.');
              }
            }
          }
        }
      } else if (on instanceof Map m) {
        return m.get(member);
      } else {
        throw new ExecutionException(this,
            "Cannot select " + member + " (type: " + member.getClass().getSimpleName()
          + ") on " + on + " (type: " + on.getClass().getSimpleName() + ").");
      }
    } else {
      throw new ExecutionException(this, "Cannot select multiple members " + memberList
                                       + " on " + on + " (type: " + on.getClass().getSimpleName() + ").");
    }
  }

  private record BoundMethods(Object       on,
                              String       methodName,
                              List<Method> methods) {
    public Object invoke(List<?> params) {
      Method match = null;
      int matchDistance = Integer.MAX_VALUE;
      Object[] convertedParams = new Object[params.size()];

      matchMethod: for (Method method : methods) {
        Class<?>[] methodParamTypes = method.getParameterTypes();
        if (methodParamTypes.length == params.size()) {
          int methodMatchDistance = 0;
          Object[] methodConvertedParams = new Object[params.size()];

          for (int i = 0; i < methodParamTypes.length; i++) {
            Object param = params.get(i);
            Class<?> paramType = methodParamTypes[i];
            if (param != null) {
              if (!paramType.isAssignableFrom(param.getClass())) {
                /*
                 * The type of the supplied parameter value is not an instance
                 * of the type of parameter of the method at that position:
                 * check other form of compatibility (numeric compatibility,
                 * primitive and wrapper types, ...), and perform conversion if
                 * possible. Otherwise, skip this method and move to the next.
                 */
                if (paramType.isPrimitive()
                 && Classes.getPrimitiveType(param.getClass()).equals(paramType)) {
                  /*
                   * Param is of the wrapper type corresponding to the primitive
                   * parameter type of the method, thus compatible (e.g. expected
                   * parameter type is boolean and Boolean was supplied).
                   */
                  methodConvertedParams[i] = param;

                } else if (Numbers.isIntegral(paramType)
                        && param instanceof Number num
                        && Numbers.isIntegral(num)) {
                  /*
                   * If the expected parameter is an integer type (byte, short, int, etc.)
                   * and the passed number is a different integer type, convert
                   * the passed number to the expected type. Add a distance of 1
                   * for this match as both types are subtypes of Number.
                   */
                  methodConvertedParams[i] = Numbers.convert(num, paramType);
                  methodMatchDistance += 1;

                } else if (Numbers.isReal(paramType)
                        && param instanceof Number num
                        && Numbers.isReal(num)) {
                  /*
                   * If the expected parameter is a real type (float or double)
                   * and the passed number is a different real type, convert
                   * the passed number to the expected type. Add a distance of 1
                   * for this match as both types are subtypes of Number.
                   */
                  methodConvertedParams[i] = Numbers.convert(num, paramType);
                  methodMatchDistance += 1;

                } else {
                  continue matchMethod;
                }
              } else if (!paramType.equals(param.getClass())) {
                /*
                 * Parameter type is compatible to parameter supplied but they
                 * are not the same type: find distance as the number of classes
                 * separating the two types in the inheritance hierarchy. E.g.:
                 *   A <- B <- C is a distance of 0 from C, 1 from B and 2 from A.
                 */
                int distance = 0;
                Class<?> c = param.getClass();
                while (c != null && !c.equals(paramType)) {
                  c = c.getSuperclass();
                  distance++;
                }
                methodMatchDistance += distance;
                methodConvertedParams[i] = param;
              } else {
                /* Same type. */
                methodConvertedParams[i] = param;
              }
            } else {
              methodConvertedParams[i] = param;
            }
          }
          if (methodMatchDistance < matchDistance) {
            match = method;
            matchDistance = methodMatchDistance;
            convertedParams = methodConvertedParams;
          }
        }
        if (matchDistance == 0) {
          /*
           * Perfect match given parameter types (i.e., given that null parameters
           * match any parameter type). No need to search further.
           */
          break;
        }
      }
      if (match != null) {
        try {
          return match.invoke(on, convertedParams);
        } catch (Exception e) {
          throw new ExecutionException("Could not execute method " + methodName
                                     + " on " + on  + " (type: " + on.getClass().getSimpleName()
                                     + "): " + e.getMessage(), e);
        }
      } else {
          throw new ExecutionException(
             "Could not match method " + methodName + " on " + on + " taking parameters "
            + params + " of types " + params.stream()
                                            .map(p -> p == null
                                                    ? "NullType"
                                                    : p.getClass().getSimpleName())
                                            .collect(joining(", ", "[", "]")));
      }
    }
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    on()._toString(st, level, indent);
    st.append('[');
    boolean first = true;
    for (Expression<?, ?> member: members()) {
      if (first) { first = false;   }
      else       { st.append(", "); }
      member._toString(st, level, indent);
    }
    st.append(']');
  }

  public Expression<?, ?> on() {
    return child("on");
  }

  public List<Expression<?, ?>> members() {
    return get("members").children();
  }
}
