/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.literal.NullLiteral;

import java.util.List;
import java.util.Map;

import static ma.vi.base.string.Escape.escapeJsonString;
import static ma.vi.esql.syntax.Translatable.Target.JSON;

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

  public Concatenation(Concatenation other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public Concatenation copy() {
    return new Concatenation(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public Concatenation copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new Concatenation(this, value, children);
  }

  @Override
  protected String trans(Target target, EsqlPath path, Map<String, Object> parameters) {
    switch (target) {
      case JSON, JAVASCRIPT -> {
        StringBuilder st = new StringBuilder();
        for (Expression<?, String> e: expressions()) {
          st.append(st.length() == 0 ? "" : " + ")
            .append('(')
            .append(e.translate(target, path.add(e), parameters))
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
            .append(e.translate(target, path.add(e), parameters));
//            .append(" as nvarchar)");
        }
        return st.toString();
      }
      // ESQL, POSTGRESQL
      default -> {
        StringBuilder st = new StringBuilder();
        for (Expression<?, String> e: expressions()) {
          st.append(st.length() == 0 ? "" : " || ")
            .append(e.translate(target, path.add(e), parameters));
        }
        return st.toString();
      }
    }
  }

//  @Override
//  protected String trans(Target target, EsqlPath path, Map<String, Object> parameters) {
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
