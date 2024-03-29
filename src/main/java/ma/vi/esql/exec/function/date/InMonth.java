/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.exec.function.date;

import ma.vi.esql.exec.function.Function;
import ma.vi.esql.exec.function.FunctionCall;
import ma.vi.esql.exec.function.FunctionParam;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.expression.GroupedExpression;
import ma.vi.esql.syntax.expression.comparison.Between;
import ma.vi.esql.syntax.expression.comparison.Equality;
import ma.vi.esql.syntax.expression.literal.DateLiteral;
import ma.vi.esql.syntax.expression.literal.IntegerLiteral;
import ma.vi.esql.syntax.expression.logical.And;
import ma.vi.esql.syntax.macro.TypedMacro;
import ma.vi.esql.translation.TranslationException;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.List;

import static java.util.Collections.singletonList;
import static ma.vi.esql.database.Database.NULL_DB;
import static ma.vi.esql.database.EsqlConnection.NULL_CONNECTION;
import static ma.vi.esql.semantic.type.Types.*;
import static ma.vi.esql.translation.Translatable.Target.ESQL;

/**
 * A macro function to check whether a date value falls in a specific month of a
 * year. This macro expands to a `between` expression and result in faster query
 * execution than extracting and comparing the year and month from the date.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class InMonth extends Function implements TypedMacro {
  public InMonth() {
    super("inmonth", BoolType,
          List.of(new FunctionParam("date",  DateType),
                  new FunctionParam("month", IntType),
                  new FunctionParam("year",  IntType)));
  }

  @Override
  public Esql<?, ?> expand(Esql<?, ?> esql, EsqlPath path) {
    FunctionCall call = (FunctionCall)esql;
    Context ctx = call.context;
    List<Expression<?, ?>> arguments = call.arguments();

    if (arguments.isEmpty()) {
      throw new TranslationException(esql, "inmonth function needs 3 arguments: the date followed "
                                         + "by the month and year to check that the date is in");
    }

    /*
     * Load arguments
     */
    Expression<?, ?> date = arguments.get(0);
    Expression<?, ?> month = arguments.get(1);
    Expression<?, ?> year = arguments.get(2);

    if (month instanceof IntegerLiteral && year instanceof IntegerLiteral) {
      String prefix = year.translate(ESQL, NULL_CONNECTION, path.add(year), NULL_DB.structure()).toString() + '-'
                    + StringUtils.leftPad(month.translate(ESQL, NULL_CONNECTION, path.add(month), NULL_DB.structure()).toString(), 2, '0') + '-';
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
        new And(ctx,
                new Equality(ctx,
                             new FunctionCall(ctx, "month", funcArgs),
                             month),
                new Equality(ctx,
                             new FunctionCall(ctx, "year", funcArgs),
                             year)
        )
      );
    }
  }
}