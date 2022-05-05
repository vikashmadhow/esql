/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.exec.conditional;

import ma.vi.base.tuple.T2;
import ma.vi.esql.database.EsqlConnection;
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

/**
 * The condition and body part of an if (and else-if). *
 * @author vikash.madhow@gmail.com
 */
public class Imply extends Expression<String, Imply> {
  public Imply(Context                context,
               Expression<?, ?>       condition,
               List<Expression<?, ?>> body) {
    super(context, "Imply",
          of("condition", condition),
          of("body",      new Esql<>(context, "body", body, true)));
  }

  public Imply(Imply other) {
    super(other);
  }

  @SafeVarargs
  public Imply(Imply other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public Imply copy() {
    return new Imply(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public Imply copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new Imply(this, value, children);
  }

  @Override
  public Esql<?, ?> scope(Scope scope, EsqlPath path) {
    super.scope(scope, path);
    Scope implyScope = new BlockScope(scope);
    for (Expression<?, ?> e: body()) {
      e.scope(implyScope, path.add(e));
    }
    return this;
  }

  @Override
  public Type computeType(EsqlPath path) {
    return Types.VoidType;
  }

  @Override
  protected Imply trans(Target               target,
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
    if ((Boolean)condition().exec(target, esqlCon, path.add(condition()), parameters, env)) {
      If.execBody(body(), target, esqlCon, path, parameters, env);
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