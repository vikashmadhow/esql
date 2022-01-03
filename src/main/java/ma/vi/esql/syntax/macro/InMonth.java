/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.macro;

import ma.vi.esql.function.Function;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.*;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.expression.FunctionCall;
import ma.vi.esql.syntax.expression.GroupedExpression;
import ma.vi.esql.syntax.expression.comparison.Between;
import ma.vi.esql.syntax.expression.comparison.Equality;
import ma.vi.esql.syntax.expression.literal.DateLiteral;
import ma.vi.esql.syntax.expression.literal.IntegerLiteral;
import ma.vi.esql.syntax.expression.logical.And;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static ma.vi.esql.syntax.Translatable.Target.ESQL;

/**
 * A macro function to check whether a date value falls in a specific month of a
 * year. This macro expands to a `between` expression and result in faster query
 * execution than extracting and comparing the year and month from the date.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class InMonth extends Function implements Macro {
  public InMonth() {
    super("inmonth", Types.StringType, emptyList());
  }

  @Override
  public Esql<?, ?> expand(Esql<?, ?> esql, EsqlPath path) {
    FunctionCall call = (FunctionCall)esql;
    Context ctx = call.context;
    List<Expression<?, ?>> arguments = call.arguments();

    if (arguments.isEmpty()) {
      throw new TranslationException("inmonth function needs at 3 arguments (the date to check "
                                   + "and the month and year that the date must fall in)");
    }

    /*
     * Load arguments
     */
    Expression<?, ?> date = arguments.get(0);
    Expression<?, ?> month = arguments.get(1);
    Expression<?, ?> year = arguments.get(2);

    if (month instanceof IntegerLiteral && year instanceof IntegerLiteral) {
      String prefix = year.translate(ESQL, path.add(year)).toString() + '-'
                    + StringUtils.leftPad(month.translate(ESQL, path.add(month)).toString(), 2, '0') + '-';
      String startDate = prefix + "01";
      LocalDate end = LocalDate.of(((IntegerLiteral)year).value.intValue(),
                                   ((IntegerLiteral)month).value.intValue(), 1);
      String endDate = prefix + end.lengthOfMonth();
      return new Between(ctx,
                        false,
                        date,
                        new DateLiteral(ctx, startDate),
                        new DateLiteral(ctx, endDate));
    } else {
      List<Expression<?, ?>> funcArgs = singletonList(date);
      return new GroupedExpression(
        ctx,
        new And(
            ctx,
            new Equality(
                ctx,
                new FunctionCall(ctx, "month",
                                 false, null,
                                 funcArgs, false,
                                 null, null, null),
                month
            ),
            new Equality(
                ctx,
                new FunctionCall(ctx, "year",
                                 false, null,
                                 funcArgs, false,
                                 null, null, null),
                year
            )
        )
      );
    }
  }
}