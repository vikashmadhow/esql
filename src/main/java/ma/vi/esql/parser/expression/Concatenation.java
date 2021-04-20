/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;

import java.util.List;
import java.util.Map;

import static ma.vi.base.string.Escape.escapeJsonString;
import static ma.vi.esql.parser.Translatable.Target.JSON;

/**
 * Concatenation operation in ESQL (||).
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Concatenation extends MultipleSubExpressions<String> {
  public Concatenation(Context context, List<Expression<?, ?>> expressions) {
    super(context, "concat", expressions);
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
  protected String trans(Target target, Map<String, Object> parameters) {
    switch (target) {
      case JSON, JAVASCRIPT -> {
        StringBuilder st = new StringBuilder();
        for (Expression<?, String> e: expressions()) {
          st.append(st.length() == 0 ? "" : " + ")
            .append('(')
            .append(e.translate(target, parameters))
            .append(" || '')");
        }
        String translation = "(" + st.toString() + ")";
        if (target == JSON) {
          translation = '"' + escapeJsonString(translation) + '"';
        }
        return translation;
      }
      case SQLSERVER -> {
        StringBuilder st = new StringBuilder();
        for (Expression<?, String> e: expressions()) {
          st.append(st.length() == 0 ? "" : " + ")
//            .append("cast(")
            .append(e.translate(target, parameters));
//            .append(" as nvarchar)");
        }
        return st.toString();
      }
      // ESQL, POSTGRESQL
      default -> {
        StringBuilder st = new StringBuilder();
        for (Expression<?, String> e: expressions()) {
          st.append(st.length() == 0 ? "" : " || ")
            .append(e.translate(target, parameters));
        }
        return st.toString();
      }
    }
  }

//  @Override
//  protected String trans(Target target, Map<String, Object> parameters) {
//    switch (target) {
//      case POSTGRESQL:
//      case ESQL:
//        return expr1().translate(target, parameters) + " || " + expr2().translate(target, parameters);
//
////            case SQLSERVER:
////                StringBuilder st = new StringBuilder();
////                Type expr1Type = expr1().type(context.db);
////                Type expr2Type = expr2().type(context.db);
////
////                if (expr1Type == context.db.types().StringType || expr1Type == context.db.types().TextType) {
////                    st.append(expr1().translate(target) + " + " + expr2().translate(target));
////                } else {
////                    st.append("cast(")
////                      .append(expr1().translate(target) + " + " + expr2().translate(target))
////                      .append(" as nvarchar)");
////                }
////
////                st.append(" + ");
////
////                if (expr2Type == context.db.types().StringType || expr2Type == context.db.types().TextType) {
////                    st.append(expr2().translate(target) + " + " + expr2().translate(target));
////                } else {
////                    st.append("cast(")
////                      .append(expr2().translate(target) + " + " + expr2().translate(target))
////                      .append(" as nvarchar)");
////                }
////                return st.toString();
//
//      default:
//        // Sql server, javascript
//        String e = expr1().translate(target, parameters) + " + " + expr2().translate(target, parameters);
//        return target == JSON ? '"' + escapeJsonString(e) + '"' : e;
//    }
//  }
}
