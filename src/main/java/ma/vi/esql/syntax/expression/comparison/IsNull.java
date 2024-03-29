/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.comparison;

import ma.vi.base.tuple.T2;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.expression.SingleSubExpression;
import org.pcollections.PMap;

import static ma.vi.esql.translation.SqlServerTranslator.requireIif;

/**
 * The is null operator in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class IsNull extends SingleSubExpression {
  public IsNull(Context context,
                boolean not,
                Expression<?, String> expr) {
    super(context, "IsNull", expr, T2.of("not", new Esql<>(context, not)));
  }

  public IsNull(IsNull other) {
    super(other);
  }

  @SafeVarargs
  public IsNull(IsNull other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public IsNull copy() {
    return new IsNull(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public IsNull copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new IsNull(this, value, children);
  }

  @Override
  public Type computeType(EsqlPath path) {
    return Types.BoolType;
  }

  @Override
  protected String trans(Target               target,
                         EsqlConnection       esqlCon,
                         EsqlPath             path,
                         PMap<String, Object> parameters,
                         Environment          env) {
    return switch (target) {
      case JAVASCRIPT -> expr().translate(target,
                                          esqlCon,
                                          path.add(expr()),
                                          parameters,
                                          env)
                       + (not() ? " !== null" : " === null");
      case SQLSERVER -> {
        boolean iif = requireIif(path, parameters);
        yield (iif ? "iif(" : "")
             + String.valueOf(expr().translate(target,
                                               esqlCon,
                                               path.add(expr()),
                                               parameters,
                                               env))
             + " is" + (not() ? " not" : "") + " null"
             + (iif ? ", 1, 0)" : "");
      }
      default -> expr().translate(target,
                                  esqlCon,
                                  path.add(expr()),
                                  parameters,
                                  env)
               + " is" + (not() ? " not" : "") + " null";
    };
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    expr()._toString(st, level, indent);
    st.append(" is");
    if (not()) {
      st.append(" not");
    }
    st.append(" null");
  }

  public boolean not() {
    return childValue("not");
  }
}