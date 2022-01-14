/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.comparison;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.expression.NegatableDoubleSubExpressions;
import ma.vi.esql.translation.Translatable;
import org.pcollections.PMap;

import static ma.vi.esql.translation.SqlServerTranslator.requireIif;
import static ma.vi.esql.translation.Translatable.Target.SQLSERVER;

/**
 * The case-insensitive like operator (ilike) in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class ILike extends NegatableDoubleSubExpressions<String> {
  public ILike(Context context,
               boolean not,
               Expression<?, String> expr1,
               Expression<?, String> expr2) {
    super(context, "ILike", not, expr1, expr2);
  }

  public ILike(ILike other) {
    super(other);
  }

  @SafeVarargs
  public ILike(ILike other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public ILike copy() {
    return new ILike(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public ILike copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new ILike(this, value, children);
  }

  @Override
  protected String trans(Translatable.Target target,
                         EsqlPath path,
                         PMap<String, Object> parameters) {
    if (target == SQLSERVER) {
      /*
       * SQL Server requires collation for case-insensitive like.
       */
      String e = '(' + expr1().translate(target, path.add(expr1()), parameters) + ") collate Latin1_General_CI_AS"
               + (not() ? " not" : "")
               + " like (" + expr2().translate(target, path.add(expr2()), parameters) + ") collate Latin1_General_CI_AS";
      if (requireIif(path, parameters)) {
        e = "iif(" + e + ", 1, 0)";
      }
      return e;
    } else {
      return super.trans(target, path, parameters);
    }
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    expr1()._toString(st, level, indent);
    if (not()) {
      st.append(" not");
    }
    st.append(" ilike ");
    expr2()._toString(st, level, indent);
  }

  public boolean not() {
    return childValue("not");
  }
}