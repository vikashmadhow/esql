/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.macro;

import ma.vi.esql.function.Function;
import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.Esql;
import ma.vi.esql.parser.Macro;
import ma.vi.esql.parser.TranslationException;
import ma.vi.esql.parser.expression.*;
import ma.vi.esql.type.Types;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static ma.vi.esql.parser.Translatable.Target.ESQL;

/**
 * A macro function which produces a label corresponding to a sequence of ids
 * from linked tables.
 * <p>
 * To get the label corresponding to an id referring to another table. For instance,
 * if table A {b_id} refers to B{id, name} with A.b_id being a foreign key pointing
 * to B.id and B.name is set as the string_form for the table B then <b>joinlabel(b_id, B)</b>
 * will return the name from B corresponding to b_id. joinlabel(b_id, B, c_id, C) will
 * produce c_name / b_name corresponding the b_id and following on to c_id. Any number of
 * links can be specified.
 * <p>
 * joinlabel can have the following optional named arguments to control the value displayed:
 * <ul>
 *     <li><b>show_last_only:</b> Show the last label element in the chain only (a -&gt; b -&gt; c, show c only).</li>
 *     <li><b>join_separator:</b> an expression for the separator between the labels from different tables.</li>
 * </ul>
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class InMonthMacroFunction extends Function implements Macro {
  public InMonthMacroFunction() {
    super("inmonth", Types.StringType, emptyList());
  }

  @Override
  public boolean expand(String name, Esql<?, ?> esql) {
    FunctionCall call = (FunctionCall)esql;
    Context ctx = call.context;
    List<Expression<?>> arguments = call.arguments();

    if (arguments.isEmpty()) {
      throw new TranslationException("inmonth function needs at 3 arguments (the date to check and " +
                                         "the month and year that the date must fall in)");
    }

    /*
     * Load arguments
     */
    Expression<?> date = arguments.get(0);
    Expression<?> month = arguments.get(1);
    Expression<?> year = arguments.get(2);

    if (month instanceof IntegerLiteral && year instanceof IntegerLiteral) {
      String prefix = year.translate(ESQL) + '-' + StringUtils.leftPad(month.translate(ESQL), 2, '0') + '-';
      String startDate = prefix + "01";
      LocalDate end = LocalDate.of(((IntegerLiteral)year).value.intValue(),
                                   ((IntegerLiteral)month).value.intValue(), 1);
      String endDate = prefix + end.lengthOfMonth();
      call.parent.replaceWith(name,
                              new Between(ctx,
                                          false,
                                          date,
                                          new DateLiteral(ctx, startDate),
                                          new DateLiteral(ctx, endDate)));
    } else {
      List<Expression<?>> funcArgs = singletonList(date);
      call.parent.replaceWith(name,
                              new GroupedExpression(
                                  ctx,
                                  new LogicalAnd(
                                      ctx,
                                      new Equality(
                                          ctx,
                                          new FunctionCall(ctx, "month",
                                                           false, null,
                                                           funcArgs, null, false,
                                                           null, null),
                                          month
                                      ),
                                      new Equality(
                                          ctx,
                                          new FunctionCall(ctx, "year",
                                                           false, null,
                                                           funcArgs, null, false,
                                                           null, null),
                                          year
                                      )
                                  )
                              ));
    }
    return true;
  }
}