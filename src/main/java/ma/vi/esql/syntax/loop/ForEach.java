/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.loop;

import ma.vi.base.tuple.T2;
import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.exec.ExecutionException;
import ma.vi.esql.exec.Result;
import ma.vi.esql.exec.env.BlockEnvironment;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.semantic.scope.Scope;
import ma.vi.esql.semantic.scope.Symbol;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import org.pcollections.PMap;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;

import static ma.vi.base.tuple.T2.of;
import static ma.vi.esql.semantic.type.Types.TopType;

/**
 * For each loop to iterate over lists, arrays, maps, results and iterables in general.
 *
 * @author vikash.madhow@gmail.com
 */
public class ForEach extends Expression<String, ForEach> {
  public ForEach(Context                context,
                 String                 keyVar,
                 String                 valueVar,
                 Expression<?, ?>       iterateOn,
                 List<Expression<?, ?>> body) {
    super(context, "ForEach",
          of("keyVar",    new Esql<>(context, keyVar)),
          of("valueVar",  new Esql<>(context, valueVar)),
          of("iterateOn", iterateOn),
          of("body",      new Esql<>(context, "body", body, true)));
  }

  public ForEach(ForEach other) {
    super(other);
  }

  @SafeVarargs
  public ForEach(ForEach other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public ForEach copy() {
    return new ForEach(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public ForEach copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new ForEach(this, value, children);
  }

  @Override
  public Esql<?, ?> scope(Scope scope, EsqlPath path) {
    super.scope(scope, path);
    if (keyVar() != null) {
      scope.addSymbol(Symbol.of(keyVar(), TopType));
    }
    scope.addSymbol(Symbol.of(valueVar(), TopType));
    return this;
  }

  @Override
  public Type computeType(EsqlPath path) {
    return Types.VoidType;
  }

  @Override
  protected ForEach trans(Target               target,
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
    Environment forEnv = new BlockEnvironment("For Each Loop Environment", env);
    String keyVar = keyVar();
    if (keyVar != null) {
      forEnv.add(keyVar, null);
    }
    String valueVar = valueVar();
    forEnv.add(valueVar, null);

    Object iterateOn = iterateOn().exec(target, esqlCon, path.add(iterateOn()), forEnv);
    if (iterateOn == null) {
      throw new ExecutionException(this, "Iteration over null object: " + iterateOn());
    } else {
      List<? extends Expression<?, ?>> body = body();
      if (iterateOn.getClass().isArray()) {
        for (int i = 0; i < Array.getLength(iterateOn); i++) {
          if (keyVar != null) {
            forEnv.set(keyVar, i);
          }
          forEnv.set(valueVar, Array.get(iterateOn, i));
          for (Expression<?, ?> e: body) {
            e.exec(target, esqlCon, path.add(e), forEnv);
          }
        }
      } else if (iterateOn instanceof List<?> list) {
        for (int i = 0; i < list.size(); i++) {
          if (keyVar != null) {
            forEnv.set(keyVar, i);
          }
          forEnv.set(valueVar, list.get(i));
          for (Expression<?, ?> e: body) {
            e.exec(target, esqlCon, path.add(e), forEnv);
          }
        }
      } else if (iterateOn instanceof Map<?, ?> map) {
        for (Object key: map.keySet()) {
          if (keyVar != null) {
            forEnv.set(keyVar, key);
          }
          forEnv.set(valueVar, map.get(key));
          for (Expression<?, ?> e: body) {
            e.exec(target, esqlCon, path.add(e), forEnv);
          }
        }
      } else if (iterateOn instanceof Result rs) {
        for (Result.Row row: rs) {
          forEnv.set(valueVar, row);
          for (Expression<?, ?> e: body) {
            e.exec(target, esqlCon, path.add(e), forEnv);
          }
        }
      } else if (iterateOn instanceof Iterable<?> it) {
        for (Object row: it) {
          forEnv.set(valueVar, row);
          for (Expression<?, ?> e: body) {
            e.exec(target, esqlCon, path.add(e), forEnv);
          }
        }
      } else {
        throw new ExecutionException(this, "Iteration over objects of type "
                                   + iterateOn.getClass().getSimpleName()
                                   + " is not supported");
      }
    }
    return null;
  }

  public String keyVar() {
    return childValue("keyVar");
  }

  public String valueVar() {
    return childValue("valueVar");
  }

  public Expression<?, ?> iterateOn() {
    return child("iterateOn");
  }

  public List<Expression<?, ?>> body() {
    return get("body").children();
  }
}