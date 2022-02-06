/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.variable;

import ma.vi.base.tuple.T2;
import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.exec.Result;
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

import static ma.vi.esql.semantic.type.Types.UnknownType;

/**
 * A variable declaration in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class VariableDecl extends    Expression<String, VariableDecl>
                          implements Symbol {
  public VariableDecl(Context context,
                      String name,
                      Type type,
                      Expression<?, ?> value) {
    super(context, "VariableDecl",
          T2.of("name",  new Esql<>(context, name)),
          T2.of("value", value));
    this.type = type == null ? UnknownType : type;
  }

  public VariableDecl(VariableDecl other) {
    super(other);
  }

  @SafeVarargs
  public VariableDecl(VariableDecl other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public VariableDecl copy() {
    return new VariableDecl(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  public VariableDecl copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new VariableDecl(this, value, children);
  }

  /**
   * Derive type from assigned value if not specified.
   */
  @Override
  public Esql<?, ?> scope(Scope scope, EsqlPath path) {
    super.scope(scope, path);
    if (type() == Types.UnknownType) {
      type = value().computeType(path.add(value()));
    }
    scope.addSymbol(this);
    return this;
  }

  @Override
  public VariableDecl trans(Target target, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env) {
    return this;
  }

  @Override
  protected Object postTransformExec(Target target, EsqlConnection esqlCon,
                                     EsqlPath path,
                                     Environment env) {
    env.add(name(), value().exec(target, esqlCon, path.add(value()), env));
    return Result.Nothing;
  }

  public String name() {
    return childValue("name");
  }

  public Expression<?, ?> value() {
    return child("value");
  }
}
