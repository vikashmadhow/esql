/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.logical;

import ma.vi.base.tuple.T2;
import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.exec.ExecutionException;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.expression.SingleSubExpression;
import org.pcollections.PMap;

import static ma.vi.base.string.Escape.escapeJsonString;
import static ma.vi.esql.translation.Translatable.Target.JSON;

/**
 * The logical inverse (not) operator in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Not extends SingleSubExpression {
  public Not(Context context, Expression<?, String> expr) {
    super(context, "Not", expr);
  }

  public Not(Not other) {
    super(other);
  }

  @SafeVarargs
  public Not(Not other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public Not copy() {
    return new Not(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public Not copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new Not(this, value, children);
  }

  @Override
  protected String trans(Target target, EsqlPath path, PMap<String, Object> parameters) {
    switch (target) {
      case JSON, JAVASCRIPT -> {
        String e = "!" + expr().translate(target, path.add(expr()), parameters);
        return target == JSON ? '"' + escapeJsonString(e) + '"' : e;
      }
      case SQLSERVER -> {
        if (path.ancestor("on") == null
         && path.ancestor("where") == null
         && path.ancestor("having") == null) {
          /*
           * For SQL Server, boolean expressions outside of where and having
           * clauses are not allowed and we simulate it with bitwise operations.
           */
          return "cast(~(" + expr() + ") as bit)";
        } else {
          return "not " + expr().translate(target, path.add(expr()), parameters);
        }
      }
      default -> {
        return "not " + expr().translate(target, path.add(expr()), parameters);
      }
    }
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    st.append("not ");
    expr()._toString(st, level, indent);
  }

  @Override
  public Object postTransformExec(EsqlConnection esqlCon,
                                  EsqlPath       path,
                                  Environment env) {
    Object expr = expr().exec(esqlCon, path.add(expr()), env);
    if (expr instanceof Boolean bool) {
      return !bool;
    } else {
      throw new ExecutionException("Invalid type for logical inverse: " + expr);
    }
  }
}