/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.variable;

import ma.vi.base.reflect.Dissector;
import ma.vi.base.reflect.Property;
import ma.vi.base.tuple.T2;
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

/**
 * A member selector of the form a[member] where `member` is selected on `a`.
 * Both the member and the object that the selection is performed on can be
 * arbitrary expressions.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Selector extends Expression<String, Selector> {
  public Selector(Context context, Expression<?, ?> on, Expression<?, ?> member) {
    super(context, "Selector",
          T2.of("on",     on),
          T2.of("member", member));
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
    Object member = member().exec(target, esqlCon, path.add(member()), env);

    if (on == null) {
      throw new ExecutionException(this, "Object to select from is null: " + on());
    }
    if (member == null) {
      throw new ExecutionException(this, "Member to select is null: " + member());
    }

    if (member instanceof Number i) {
      if      (on.getClass().isArray())    { return Array.get(on, i.intValue()); }
      else if (on instanceof List       l) { return l.get(i.intValue());         }
      else if (on instanceof Result     r) { return r.value(i.intValue());       }
      else if (on instanceof Result.Row r) { return r.value(i.intValue());       }
      else if (on instanceof Map        m) { return m.get(i.intValue());         }
      else {
        throw new ExecutionException(this,
            "Selection by numeric index is only supported on arrays, lists, maps "
          + "and results. The selection was performed on a " + on.getClass().getSimpleName() + '.');
      }
    } else if (member instanceof String s) {
      switch (on) {
        case Map        m: return m.get(s);
        case Result     r: return r.value(s);
        case Result.Row r: return r.value(s);
        default:
          Optional<Property> p = Dissector.property(on.getClass(), s);
          if (p.isPresent()) {
            return p.get().get(on);
          } else {
            Optional<Method> method = Dissector.method(on.getClass(), s);
            if (method.isPresent()) {
              try {
                return method.get().invoke(on);
              } catch (Exception e) {
                throw new ExecutionException(this,
                                             "Could not execute method " + s
                                           + " on " + on + ": " + e.getMessage(), e);
              }
            } else {
              throw new ExecutionException(this,
                  "Selection by string is supported on maps, results and for property "
                + "selection on arbitrary classes. The selection was performed on "
                + "class " + on.getClass().getSimpleName() + " which does not have "
                + "a field, property or methods named " + member + '.');
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
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    on()._toString(st, level, indent);
    st.append('[');
    member()._toString(st, level, indent);
    st.append(']');
  }

  public Expression<?, ?> on() {
    return child("on");
  }

  public Expression<?, ?> member() {
    return child("member");
  }
}
