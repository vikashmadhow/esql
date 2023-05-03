/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression;

import ma.vi.base.tuple.T2;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.cast.Cast;
import ma.vi.esql.translation.TranslationException;
import org.pcollections.PMap;

import java.util.List;

import static ma.vi.esql.semantic.type.Types.TextType;
import static ma.vi.esql.translation.Translatable.Target.JAVASCRIPT;
import static ma.vi.esql.translation.Translatable.Target.SQLSERVER;

/**
 * Concatenation operation in ESQL (||).
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Concatenation extends MultipleSubExpressions {
  public Concatenation(Context context, List<Expression<?, ?>> expressions) {
    super(context, "Concat", expressions);
  }

  public Concatenation(Concatenation other) {
    super(other);
  }

  @SafeVarargs
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
  protected String trans(Target               target,
                         EsqlConnection       esqlCon,
                         EsqlPath             path,
                         PMap<String, Object> parameters,
                         Environment          env) {
    if (target == JAVASCRIPT) {
      StringBuilder st = new StringBuilder("await $exec.concat({}");
      for (Expression<?, ?> e: expressions()) {
        st.append(", ").append(e.translate(target,
                                           esqlCon,
                                           path.add(e),
                                           parameters,
                                           env));
      }
      return st.append(')').toString();
    } else {
      String op = target == SQLSERVER ? " + " : " || ";
      StringBuilder st = new StringBuilder();
      for (Expression<?, ?> e: expressions()) {
        st.append(st.length() == 0 ? "" : op);
        Type type;
        try {
          type = e.computeType(path.add(e));
        } catch (TranslationException te) {
          type = null;
        }
        if (type != Types.StringType
         && type != TextType) {
          e = new Cast(context, e, TextType);
        }
        st.append(e.translate(target,
                              esqlCon,
                              path.add(e),
                              parameters,
                              env));
      }
      return st.toString();
    }
  }
}
