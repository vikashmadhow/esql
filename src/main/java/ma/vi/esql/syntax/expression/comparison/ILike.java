/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.comparison;

import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.Translatable;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.expression.NegatableDoubleSubExpressions;

import java.util.Map;

import static ma.vi.esql.syntax.Translatable.Target.SQLSERVER;
import static ma.vi.esql.translator.SqlServerTranslator.requireIif;

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
    super(context, "ilike", not, expr1, expr2);
  }

  public ILike(ILike other) {
    super(other);
  }

  @Override
  public ILike copy() {
    return new ILike(this);
  }

  @Override
  protected String trans(Translatable.Target target,
                         EsqlPath path,
                         Map<String, Object> parameters) {
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

  public boolean not() {
    return childValue("not");
  }
}