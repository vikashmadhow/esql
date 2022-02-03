/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.function;

import ma.vi.base.tuple.T2;
import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.exec.Result;
import ma.vi.esql.exec.env.AbstractEnvironment;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.function.FunctionParam;
import ma.vi.esql.semantic.scope.FunctionScope;
import ma.vi.esql.semantic.scope.Scope;
import ma.vi.esql.semantic.scope.Symbol;
import ma.vi.esql.semantic.type.Kind;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import org.pcollections.PMap;

import java.util.List;

/**
 * A user-defined function in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class FunctionDecl extends Expression<String, FunctionDecl> implements Symbol {
  public FunctionDecl(Context context,
                      String name,
                      Type returnType,
                      List<FunctionParameter> parameters,
                      List<? extends Expression<?, ?>> body) {
    super(context, "FunctionDecl",
          T2.of("name",       new Esql<>(context, name)),
          T2.of("returnType", new Esql<>(context, returnType)),
          T2.of("parameters", new Esql<>(context, "parameters", parameters, true)),
          T2.of("body",       new Esql<>(context, "body", body, true)));
  }

  public FunctionDecl(FunctionDecl other) {
    super(other);
  }

  @SafeVarargs
  public FunctionDecl(FunctionDecl other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public FunctionDecl copy() {
    return new FunctionDecl(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  public FunctionDecl copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new FunctionDecl(this, value, children);
  }

  @Override
  public Esql<?, ?> scope(Scope scope, EsqlPath path) {
    scope.addSymbol(Symbol.of(name(), Kind.FUNCTION));
    Scope functionScope = new FunctionScope(scope);
    super.scope(functionScope, path);
    for (FunctionParameter param: parameters()) {
      functionScope.addSymbol(param);
    }
    EsqlPath p = path.add(this);
    for (Expression<?, ?> e: body()) {
      e.scope(functionScope, p);
    }
    return this;
  }

  @Override
  public FunctionDecl trans(Target target, EsqlPath path, PMap<String, Object> parameters) {
    return this;
  }

  @Override
  public Object exec(EsqlConnection esqlCon,
                     EsqlPath       path,
                     Environment env) {
    env.add(name(), set("environment", new Esql<>(context, env)));
    return Result.Nothing;
  }

  @Override
  public FunctionScope scope() {
    return (FunctionScope)scope;
  }

  public String name() {
    return childValue("name");
  }

  public Type returnType() {
    return childValue("returnType");
  }

  public List<FunctionParameter> parameters() {
    return get("parameters").children();
  }

  public List<FunctionParam> params() {
    return parameters().stream()
                       .map(p -> new FunctionParam(p.name(), p.type()))
                       .toList();
  }

  public List<? extends Expression<?, ?>> body() {
    return get("body").children();
  }

  public AbstractEnvironment environment() {
    return childValue("environment");
  }
}
