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
 * A general for loop in ESQL of the form
 * <code>
 *    for [initialise], [condition], [step] do
 *      [expressions...]
 *    end;
 * </code>
 *
 * Any expression can be used for each of the 3 expressions (initialise, condition,
 * and step) in the for loop. The loop start by executing the initialise expression
 * which could create a counter, for instance, executing one iteration of the
 * loop body, then execute the step expression followed by checking if the condition
 * is still true. The for loop repeat while the condition remains true.
 *
 * The `break` keyword exit the loop while the `continue` stops the current iteration
 * and moves to the next.
 *
 * @author vikash.madhow@gmail.com
 */
public class For extends Expression<String, For> {
  public For(Context                context,
             Expression<?, ?>       init,
             Expression<?, ?>       condition,
             Expression<?, ?>       step,
             List<Expression<?, ?>> body) {
    super(context, "For",
          of("init",      init),
          of("condition", condition),
          of("step",      step),
          of("body",      new Esql<>(context, "body", body, true)));
  }

  public For(For other) {
    super(other);
  }

  @SafeVarargs
  public For(For other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public For copy() {
    return new For(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public For copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new For(this, value, children);
  }

  @Override
  public Esql<?, ?> scope(Scope scope, EsqlPath path) {
    Scope forScope = new BlockScope(scope);
    super.scope(forScope, path);
    init().scope(forScope, path.add(init()));
    condition().scope(forScope, path.add(condition()));
    step().scope(forScope, path.add(step()));
    for (Expression<?, ?> e: body()) {
      e.scope(forScope, path.add(e));
    }
    return this;
  }

  @Override
  public Type computeType(EsqlPath path) {
    return Types.VoidType;
  }

  @Override
  protected For trans(Target               target,
                      EsqlConnection       esqlCon,
                      EsqlPath             path,
                      PMap<String, Object> parameters,
                      Environment          env) {
    return this;
  }

  @Override
  public Object exec(Target               target,
                     EsqlConnection       esqlCon,
                     EsqlPath             path,
                     PMap<String, Object> parameters,
                     Environment          env) {
    Environment forEnv = new BlockEnvironment("For Loop Environment", env);
    init().exec(target, esqlCon, path, parameters, forEnv);
    while ((Boolean)condition().exec(target, esqlCon, path, parameters, forEnv)) {
      if (execLoopBody(body(), target, esqlCon, path, parameters, forEnv) == BREAK) {
        break;
      }
      step().exec(target, esqlCon, path, parameters, forEnv);
    }
    return null;
  }

  public Expression<?, ?> init() {
    return child("init");
  }

  public Expression<?, ?> condition() {
    return child("condition");
  }

  public Expression<?, ?> step() {
    return child("step");
  }

  public List<Expression<?, ?>> body() {
    return get("body").children();
  }
}