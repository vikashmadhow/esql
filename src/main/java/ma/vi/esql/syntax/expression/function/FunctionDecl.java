/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.function;

import ma.vi.base.tuple.T2;
import ma.vi.esql.database.Database;
import ma.vi.esql.exec.Result;
import ma.vi.esql.semantic.scope.Allocator;
import ma.vi.esql.semantic.scope.FunctionScope;
import ma.vi.esql.semantic.scope.Scope;
import ma.vi.esql.semantic.scope.Symbol;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import org.pcollections.PMap;

import java.sql.Connection;
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
    scope.addSymbol(this);
    Scope functionScope = new FunctionScope("Function " + name() + " Scope",
                                            scope, new Allocator());
    super.scope(functionScope, path);
    for (FunctionParameter param: parameters()) {
      functionScope.addSymbol(param);
    }
    for (Expression<?, ?> e: body()) {
      e.scope(functionScope, path.add(this));
    }
    return this;
  }

  @Override
  public FunctionDecl trans(Target target, EsqlPath path, PMap<String, Object> parameters) {
    return this;
  }

  @Override
  public Result execute(Database db, Connection con, EsqlPath path) {
    Result result = Result.Nothing;
    for (Expression<?, ?> st: body()) {
      Result r = st.execute(db, con, path.add(st));
      if (r != Result.Nothing) {
        result = r;
      }
    }
    return result;
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

  public List<? extends Expression<?, ?>> body() {
    return get("body").children();
  }
}
