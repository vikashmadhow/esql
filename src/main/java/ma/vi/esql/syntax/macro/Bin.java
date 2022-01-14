/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.macro;

import ma.vi.esql.function.Function;
import ma.vi.esql.function.FunctionParameter;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.TypedMacro;
import ma.vi.esql.syntax.expression.Cast;
import ma.vi.esql.syntax.expression.Concatenation;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.expression.FunctionCall;
import ma.vi.esql.syntax.expression.comparison.Case;
import ma.vi.esql.syntax.expression.comparison.LessThan;
import ma.vi.esql.syntax.expression.comparison.Range;
import ma.vi.esql.syntax.expression.literal.StringLiteral;
import ma.vi.esql.translation.TranslationException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static java.lang.String.valueOf;
import static org.apache.commons.lang3.StringUtils.leftPad;

/**
 * Given a value and a variadic array of intervals, returns the range
 * where the value fits. E.g.
 * <p>
 * bin(15, 'x', 1,5,12,20,35,67) returns '12 &lt;= x &lt; 19'
 * <p>
 * bin is macro which is expanded to a case statement: E.g.
 * <pre>
 *   bin(age, 'age', 1,2,5) expands to:
 *
 *      '01. age &lt; 1'         if age &lt; 1         else
 *      '02. 1 &lt;= age &lt; 2' if 1 &lt;= age &lt; 2 else
 *      '03. 2 &lt;= age &lt; 5' if 2 &lt;= age &lt; 5 else '04. age &gt;= 5'
 *
 *   which is then translated to a database-specific CASE expression.
 * </pre>
 *
 * The interval values can be expressions but they must be in correct order,
 * i.e., from smallest to largest. Otherwise the case expansion will be wrong.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Bin extends Function implements TypedMacro {
  public Bin() {
    super("bin", Types.TextType,
          Arrays.asList(new FunctionParameter("val", Types.TextType),
                        new FunctionParameter("name", Types.TextType),
                        new FunctionParameter("intervals", Types.TextType)));
  }

  @Override
  public Esql<?, ?> expand(Esql<?, ?> esql, EsqlPath path) {
    FunctionCall call = (FunctionCall)esql;
    Context ctx = call.context;
    List<Expression<?, ?>> args = call.arguments();
    if (args.size() < 3) {
      throw new TranslationException("bin requires at least 3 arguments: the value to bin, a human-readable "
                                   + "name of the value to bin, and at least 1 value defining the intervals of "
                                   + "the bin range. E.g: bin(x, 'age', 1, 5, 10) will produce bins: "
                                   + "age < 1, 1 <= age < 5, 5 <= age < 10 and age >= 10" );
    }

    int order = 2;
    List<Expression<?, ?>> cases = new ArrayList<>();
    Iterator<Expression<?, ?>> i = args.iterator();
    Expression<?, ?> binVar = i.next();
    Expression<?, ?> varName = i.next();
    Expression<?, ?> lower = null;
    Expression<?, ?> upper = i.next();
    while (upper != null) {
      if (lower == null) {
        cases.add(new Concatenation(ctx,
                                    Arrays.asList(
                                        new StringLiteral(ctx, "01. "),
                                        varName,
                                        new StringLiteral(ctx, " < "),
                                        new Cast(ctx, upper, Types.StringType))));
        cases.add(new LessThan(ctx, (Expression<?, String>)binVar, (Expression<?, String>)upper));
      }
      lower = upper;
      upper = i.hasNext() ? i.next() : null;
      if (upper == null) {
        cases.add(new Concatenation(ctx,
                                    Arrays.asList(
                                        new StringLiteral(ctx, leftPad(valueOf(order), 2, '0') + ". "),
                                        varName,
                                        new StringLiteral(ctx, " >= "),
                                        new Cast(ctx, lower, Types.StringType))));
      } else {
        cases.add(new Concatenation(ctx,
                                    Arrays.asList(
                                        new StringLiteral(ctx, leftPad(valueOf(order), 2, '0') + ". "),
                                        new Cast(ctx, lower, Types.StringType),
                                        new StringLiteral(ctx," <= "),
                                        varName,
                                        new StringLiteral(ctx," < "),
                                        new Cast(ctx, upper, Types.StringType))));
        cases.add(new Range(ctx,
                            (Expression<?, String>)lower,
                            "<=",
                            (Expression<?, String>)binVar,
                            "<",
                            (Expression<?, String>)upper));
        order += 1;
      }
    }
    return new Case(ctx, cases);
  }
}