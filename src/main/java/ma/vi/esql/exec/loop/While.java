/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.exec.loop;

import ma.vi.base.tuple.T2;
import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.exec.env.BlockEnvironment;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.semantic.scope.BlockScope;
import ma.vi.esql.semantic.scope.Scope;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import org.pcollections.PMap;

import java.util.List;

import static ma.vi.base.tuple.T2.of;
import static ma.vi.esql.exec.loop.ForEach.LoopState.BREAK;
import static ma.vi.esql.exec.loop.ForEach.execLoopBody;

/**
 * While loop in ESQL of the form
 * <code>
 *    while [condition] do
 *      [expressions...]
 *    end;
 * </code>
 *
 * The `break` keyword exit the loop while the `continue` stops the current iteration
 * and moves to the next.
 *
 * @author vikash.madhow@gmail.com
 */
public class While extends Expression<String, While> {
  public While(Context                context,
               Expression<?, ?>       condition,
               List<Expression<?, ?>> body) {
    super(context, "While",
          of("condition", condition),
          of("body",      new Esql<>(context, "body", body, true)));
  }

  public While(While other) {
    super(other);
  }

  @SafeVarargs
  public While(While other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public While copy() {
    return new While(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public While copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new While(this, value, children);
  }

  @Override
  public Esql<?, ?> scope(Scope scope, EsqlPath path) {
    Scope whileScope = new BlockScope(scope);
    super.scope(whileScope, path);
    condition().scope(whileScope, path.add(condition()));
    for (Expression<?, ?> e: body()) {
      e.scope(whileScope, path.add(e));
    }
    return this;
  }

  @Override
  public Type computeType(EsqlPath path) {
    return Types.VoidType;
  }

  @Override
  protected While trans(Target               target,
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
    Environment whileEnv = new BlockEnvironment("While Loop Environment", env);
    while ((Boolean)condition().exec(target, esqlCon, path, whileEnv)) {
      if (execLoopBody(body(), target, esqlCon, path, whileEnv) == BREAK) {
        break;
      }
    }
    return null;
  }

  public Expression<?, ?> condition() {
    return child("condition");
  }

  public List<Expression<?, ?>> body() {
    return get("body").children();
  }
}