/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.exec.conditional;

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

/**
 * If-elseif(s)-else block in Esql.
 *
 * @author vikash.madhow@gmail.com
 */
public class If extends Expression<String, If> {
  public If(Context                context,
            Imply                  ifImply,
            List<Imply>            elseIfImplies,
            List<Expression<?, ?>> elseImply) {
    super(context, "If",
          of("ifImply",       ifImply),
          of("elseIfImplies", new Esql<>(context, "elseIfImplies", elseIfImplies, true)),
          of("elseImply",     new Esql<>(context, "elseImply", elseImply, true)));
  }

  public If(If other) {
    super(other);
  }

  @SafeVarargs
  public If(If other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public If copy() {
    return new If(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public If copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new If(this, value, children);
  }

  @Override
  public Esql<?, ?> scope(Scope scope, EsqlPath path) {
    super.scope(scope, path);
    ifImply().scope(scope, path.add(ifImply()));
    if (elseIfImplies() != null && !elseIfImplies().isEmpty()) {
      for (Imply elseIf: elseIfImplies()) {
        elseIf.scope(scope, path.add(elseIf));
      }
    }
    if (elseImply() != null) {
      Scope elseScope = new BlockScope(scope);
      for (Expression<?, ?> e: elseImply()) {
        e.scope(elseScope, path.add(e));
      }
    }
    return this;
  }

  @Override
  public Type computeType(EsqlPath path) {
    return Types.VoidType;
  }

  @Override
  protected If trans(Target               target,
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
    Imply ifImply = ifImply();
    if ((Boolean)ifImply.condition().exec(target, esqlCon, path, parameters, env)) {
      execBody(ifImply.body(), target, esqlCon, path, parameters, env);
    } else {
      boolean conditionMet = false;
      List<Imply> elseIfs = elseIfImplies();
      if (elseIfs != null && !elseIfs.isEmpty()) {
        for (Imply elseIf: elseIfs) {
          if ((Boolean)elseIf.condition().exec(target, esqlCon, path, parameters, env)) {
            execBody(elseIf.body(), target, esqlCon, path, parameters, env);
            conditionMet = true;
            break;
          }
        }
      }
      if (!conditionMet && elseImply() != null) {
        execBody(elseImply(), target, esqlCon, path, parameters, env);
      }
    }
    return null;
  }

  static void execBody(List<Expression<?, ?>> body,
                       Target                 target,
                       EsqlConnection         esqlCon,
                       EsqlPath               path,
                       PMap<String, Object>   parameters,
                       Environment            parentEnv) {
    Environment bodyEnv = new BlockEnvironment(parentEnv);
    for (Expression<?, ?> e: body) {
      e.exec(target, esqlCon, path.add(e), parameters, bodyEnv);
    }
  }

  public Imply ifImply() {
    return child("ifImply");
  }

  public List<Imply> elseIfImplies() {
    return get("elseIfImplies").children();
  }

  public List<Expression<?, ?>> elseImply() {
    return get("elseImply").children();
  }
}