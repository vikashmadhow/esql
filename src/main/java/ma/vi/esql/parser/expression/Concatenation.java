/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;

import static ma.vi.esql.parser.Translatable.Target.JSON;
import static ma.vi.base.string.Escape.escapeJsonString;

public class Concatenation extends DoubleSubExpressions<String> {
  public Concatenation(Context context, Expression<?> expr1, Expression<?> expr2) {
    super(context, "||", expr1, expr2);
  }

  public Concatenation(Concatenation other) {
    super(other);
  }

  @Override
  public Concatenation copy() {
    if (!copying()) {
      try {
        copying(true);
        return new Concatenation(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public String translate(Target target) {
    switch (target) {
      case POSTGRESQL:
      case ESQL:
        return expr1().translate(target) + " || " + expr2().translate(target);

//            case SQLSERVER:
//                StringBuilder st = new StringBuilder();
//                Type expr1Type = expr1().type(context.db);
//                Type expr2Type = expr2().type(context.db);
//
//                if (expr1Type == context.db.types().StringType || expr1Type == context.db.types().TextType) {
//                    st.append(expr1().translate(target) + " + " + expr2().translate(target));
//                } else {
//                    st.append("cast(")
//                      .append(expr1().translate(target) + " + " + expr2().translate(target))
//                      .append(" as nvarchar)");
//                }
//
//                st.append(" + ");
//
//                if (expr2Type == context.db.types().StringType || expr2Type == context.db.types().TextType) {
//                    st.append(expr2().translate(target) + " + " + expr2().translate(target));
//                } else {
//                    st.append("cast(")
//                      .append(expr2().translate(target) + " + " + expr2().translate(target))
//                      .append(" as nvarchar)");
//                }
//                return st.toString();

      default:
        // Sql server, javascript
        String e = expr1().translate(target) + " + " + expr2().translate(target);
        return target == JSON ? '"' + escapeJsonString(e) + '"' : e;
    }
  }
}
