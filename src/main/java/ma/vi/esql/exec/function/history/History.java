/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.exec.function.history;

import ma.vi.esql.builder.SelectBuilder;
import ma.vi.esql.exec.function.Function;
import ma.vi.esql.exec.function.FunctionCall;
import ma.vi.esql.exec.function.FunctionParam;
import ma.vi.esql.exec.function.NamedArgument;
import ma.vi.esql.semantic.type.BaseRelation;
import ma.vi.esql.semantic.type.SimpleColumn;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.Parser;
import ma.vi.esql.syntax.expression.ColumnRef;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.expression.cast.Cast;
import ma.vi.esql.syntax.expression.comparison.Equality;
import ma.vi.esql.syntax.expression.comparison.Inequality;
import ma.vi.esql.syntax.expression.comparison.IsNull;
import ma.vi.esql.syntax.expression.comparison.Range;
import ma.vi.esql.syntax.expression.literal.StringLiteral;
import ma.vi.esql.syntax.macro.UntypedMacro;
import ma.vi.esql.syntax.query.CompositeSelect;
import ma.vi.esql.syntax.query.Select;
import ma.vi.esql.translation.TranslationException;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ma.vi.base.string.Strings.capFirst;
import static ma.vi.base.string.Strings.expandByCase;
import static ma.vi.esql.semantic.type.Types.*;
import static ma.vi.esql.translation.Translatable.Target.ESQL;
import static ma.vi.esql.translation.Translatable.Target.POSTGRESQL;
import static org.apache.commons.lang3.StringUtils.leftPad;

/**
 * Returns a list of history events on a table with each row showing a change
 * to one column. For example, `history('test.Table', from_date, to_date, [column_position= true | false])`
 * could return:
 *
 * <pre>
 * | at | action | by | _id | identifier | column | from | to |
 * |----|--------|----|-----|------------|--------|------|----|
 * | t1 | I      | u1 | id1 | e1         | name   |      | a  |
 * | t2 | D      | u1 | id1 | e1         | id     | i    |    |
 * | t2 | T      | u2 | id2 | e2         | name   | x    | y  |
 * </pre>
 *
 * This function is a macro that works by expanding to:
 * <pre>
 * select change_at, 'I', change_by, _id, identifier, '001. x', null, x
 *   from test.Table$history
 *  where from &le; change_at &le; to
 *    and action = 'I'
 *    and x is not null
 *
 * union all
 *
 * select change_at, 'I', change_by, _id, identifier, '002. y', null, y
 *   from test.Table$history
 *  where from &le; change_at &le; to
 *    and action = 'I'
 *    and y is not null
 *
 * union all
 *
 * select change_at, 'D', change_by, _id, identifier, '001. x', x, null
 *   from test.Table$history
 *  where from &le; change_at &le; to
 *    and action = 'D'
 *    and x is not null
 *
 * union all
 *
 * select change_at, 'U', change_by, _id, identifier, '001. x', cf.x, ct.x
 *   from cf:test.Table$history
 *   join ct:test.Table$history on cf.trans_id = ct.trans_id
 *  where from &le; cf.change_at &le; to
 *    and cf.action = 'F'
 *    and ct.action = 'T'
 *    and cf.x != ct.x
 * ...
 * </pre>
 *
 * `history` also takes an optional set of named arguments to control its output:
 * <ul>
 *   <li><b>column_position</b>: When set to true (default false) the column name
 *       is prefixed with an index value that can be used for ordering (especially
 *       in crosstabs).</li>
 *   <li><b>show_insert</b>: When set to true (default true) inserts are included
 *       in the history output.</li>
 *   <li><b>show_delete</b>: When set to true (default true) deletes are included
 *       in the history output.</li>
 *   <li><b>show_update</b>: When set to true (default true) updates are included
 *       in the history output.</li>
 * </ul>
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class History extends Function implements UntypedMacro {
  public History() {
    super("history", TextType,
          Arrays.asList(new FunctionParam("table", StringType),
                        new FunctionParam("from",  DatetimeType),
                        new FunctionParam("to",    DatetimeType)));
  }

  @Override
  public Esql<?, ?> expand(Esql<?, ?> esql, EsqlPath path) {
    FunctionCall call = (FunctionCall)esql;
    Context ctx = call.context;
    List<Expression<?, ?>> args = call.arguments();
    if (args.size() < 3) {
      throw new TranslationException(esql, "history requires at least 3 arguments: the table to get history for, "
                                         + "the starting date and the end date for the period to get history for.");
    }

    /*
     * Load arguments included named arguments and links.
     */
    String                table      = null;    // Table to load history for.
    Expression<?, String> from       = null;    // Start date to include in history.
    Expression<?, String> to         = null;    // End date of history to include.
    boolean               columnPos  = false;   // show the column position or not.
    boolean               showInsert = true;    // Show inserts in history.
    boolean               showDelete = true;    // Show deletes in history.
    boolean               showUpdate = true;    // Show updates in history.

    for (Expression<?, ?> arg: args) {
      if (arg instanceof NamedArgument namedArg) {
        switch (namedArg.name()) {
          case "column_position" -> columnPos  = getBooleanParam(namedArg, "column_position", path);
          case "show_insert"     -> showInsert = getBooleanParam(namedArg, "show_insert",     path);
          case "show_delete"     -> showDelete = getBooleanParam(namedArg, "show_delete",     path);
          case "show_update"     -> showUpdate = getBooleanParam(namedArg, "show_update",     path);
          default ->
          throw new TranslationException("""
            Invalid named argument in history: %1s
            history only recognises the following named arguments:
            column_position: whether to show the column position in the column label. Default is false.
            show_insert: When set to true (default true) inserts are included in the history output.
            show_delete: When set to true (default true) deletes are included in the history output.
            show_update: When set to true (default true) updates are included in the history output.
            """.formatted(namedArg.name()));
        }
      } else if (table == null) {
        table = getStringParam(args.get(0), "table", path);
      } else if (from == null) {
        from = (Expression<?, String>)arg;
      } else {
        to = (Expression<?, String>)arg;
      }
    }
    if (table == null) {
      throw new IllegalArgumentException("Table is null");
    }
    BaseRelation relation = esql.context.structure.relation(table);

    if (relation.attribute("history") == null
     || !((Boolean)relation.evaluateAttribute("history"))) {
      throw new IllegalArgumentException("History is not enabled on " + table);
    }
    String historyTable = table + "$history";

    List<SimpleColumn> columns = relation.cols(ESQL).stream()
                                         .filter(c -> !c.derived()
                                                   && !c.name().contains("/")
                                                   && !c.name().startsWith("_")
                                                   && !c.is("hide")
                                                   && !c.is("edit_hide"))
                                         .toList();
    String idColumn = null;
    if (relation.attribute("code_column") != null) {
      idColumn = relation.evaluateAttribute("code_column");
    }
    if (idColumn == null) {
      idColumn = columns.stream   ()
                        .filter   (c -> c.is("identifier"))
                        .findFirst()
                        .map      (SimpleColumn::name)
                        .orElse   (null);
    }
    if (idColumn == null) {
      idColumn = "_id";
    }
    idColumn = "cf." + idColumn;

    Target target = ctx.structure.database.target();
    List<Select> selects = new ArrayList<>();
    Range range = new Range(ctx,
                            new FunctionCall(ctx,
                                             "startofday",
                                             List.of(target == POSTGRESQL ? new Cast(ctx, from, DatetimeType) : from)),
                            "<=",
                            new ColumnRef(ctx, "cf", "history_change_time"),
                            "<=",
                            new FunctionCall(ctx,
                                             "endofday",
                                             List.of(target == POSTGRESQL ? new Cast(ctx, to, DatetimeType) : to)));
    for (int i = 0; i < columns.size(); i++) {
      SimpleColumn col = columns.get(i);
      String label = col.has("label")       ? col.get("label")
                   : col.has("short_label") ? col.get("short_label")
                   : capFirst(expandByCase(col.name()));
      String group = col.get("group");
      String subgroup = col.get("subgroup");
      if      (subgroup != null) label = subgroup + ": " + label;
      else if (group    != null) label = group    + ": " + label;
      if (columnPos) {
        label = leftPad(String.valueOf(i), 3, '0') + ". " + label;
      }
      label = "'" + label.replace("'", "''") + "'";

      /* Insert events */
      if (showInsert) {
        selects.add(new SelectBuilder(ctx)
                          .explicit(true)
                          .column("cf.history_change_time", "change_at")
                          .column("'Created'",              "change")
                          .column("cf.history_change_user", "change_by")
                          .column("cf._id",                 "_id")
                          .column(idColumn,                 "identifier")
                          .column("'" + col.name() + "'",   "column_name")
                          .column(label,                    "column_label")
                          .column("null",                   "from_value")
                          .column(format(col, "cf", ctx),   "to_value")
                          .from  (historyTable,             "cf")
                          .and   (range)
                          .and   (new Equality(ctx,
                                               new ColumnRef(ctx, "cf", "history_change_event"),
                                               new StringLiteral(ctx, "I")))
                          .and   (new IsNull(ctx, true, new ColumnRef(ctx, "cf", col.name())))
                          .build());
      }

      /* Delete events */
      if (showDelete) {
        selects.add(new SelectBuilder(ctx)
                          .explicit(true)
                          .column("cf.history_change_time", "change_at")
                          .column("'Deleted'",              "change")
                          .column("cf.history_change_user", "change_by")
                          .column("cf._id",                 "_id")
                          .column(idColumn,                 "identifier")
                          .column("'" + col.name() + "'",   "column_name")
                          .column(label,                    "column_label")
                          .column(format(col, "cf", ctx),   "from_value")
                          .column("null",                   "to_value")
                          .from  (historyTable,             "cf")
                          .and   (range)
                          .and   (new Equality(ctx,
                                               new ColumnRef(ctx, "cf", "history_change_event"),
                                               new StringLiteral(ctx, "D")))
                          .and   (new IsNull(ctx, true, new ColumnRef(ctx, "cf", col.name())))
                          .build());
      }

      /* Update events */
      if (showUpdate) {
        selects.add(new SelectBuilder(ctx)
                          .explicit(true)
                          .column("cf.history_change_time", "change_at")
                          .column("'Modified'",             "change")
                          .column("cf.history_change_user", "change_by")
                          .column("cf._id",                 "_id")
                          .column(idColumn,                 "identifier")
                          .column("'" + col.name() + "'",   "column_name")
                          .column(label,                    "column_label")
                          .column(format(col, "cf", ctx),   "from_value")
                          .column(format(col, "ct", ctx),   "to_value")
                          .from  (historyTable,             "cf")
                          .join  (historyTable,             "ct", "cf._id = ct._id and cf.history_change_trans_id = ct.history_change_trans_id", false)
                          .and   (range)
                          .and   (new Equality(ctx,
                                               new ColumnRef(ctx, "cf", "history_change_event"),
                                               new StringLiteral(ctx, "F")))
                          .and   (new Equality(ctx,
                                               new ColumnRef(ctx, "ct", "history_change_event"),
                                               new StringLiteral(ctx, "T")))
                          .and   (new Inequality(ctx,
                                                 new ColumnRef(ctx, "cf", col.name()),
                                                 new ColumnRef(ctx, "ct", col.name())))
                          .build());
      }
    }
    return new CompositeSelect(ctx, "union all", selects);
  }

  private static String format(SimpleColumn col, String alias, Context context) {
    if (col.has("value_label")) {
      String valueLabel = col.get("value_label");
      if (valueLabel.startsWith("$(")) {
        valueLabel = valueLabel.substring(2, valueLabel.length() - 1);
      }
      Parser parser = new Parser(context.structure);
      Expression<?, String> expr = ColumnRef.qualify(parser.parseExpression(valueLabel), alias);
      return expr.translate(ESQL, null, new EsqlPath(expr), context.structure);

    } else if (col.has("lookup")) {
      return "lookuplabel(" + alias + '.' + col.name() + ", '" + col.get("lookup") + "', show_code=true))";

    } else if (col.has("link_table")) {
      if (col.get("link_table") instanceof JSONArray linkTable) {
        StringBuilder st = new StringBuilder("joinlabel(" + alias + '.' + col.name());
        JSONArray linkCode = col.get("link_code");
        JSONArray linkLabel = col.get("link_label");
        for (int i = 0; i < linkCode.length(); i++) {
          String code  = linkCode.getString(i);
          String label = linkLabel.getString(i);
          String table = linkTable.getString(i);
          st.append(", '").append(code) .append("'")
            .append(", '").append(label).append("'")
            .append(", '").append(table).append("'");
        }
        return st.append(")").toString();

      } else {
        return "joinlabel(" + alias + '.' + col.name()
             + ", '" + col.get("link_code")  + "'"
             + ", '" + col.get("link_label") + "'"
             + ", '" + col.get("link_table") + "')";
      }
    } else if (col.is("obfuscate")) {
      return "unobfuscate(" + alias + "." + col.name() + ")";

    } else {
      return "format(" + alias + "." + col.name() + ")";
    }
  }

  static String getStringParam(Expression<?, ?> arg,
                               String           argName,
                               EsqlPath         path) {
    Object value = arg.exec(ESQL, null, path, arg.context.structure);
    if (value != null && !(value instanceof String)) {
      throw new TranslationException(argName + " must be a string value (" + arg + " was provided)");
    }
    return (String)value;
  }

  static boolean getBooleanParam(NamedArgument namedArg,
                                 String        argName,
                                 EsqlPath      path) {
    Object value = namedArg.arg().exec(ESQL, null, path, namedArg.context.structure);
    if (value != null && !(value instanceof Boolean)) {
      throw new TranslationException(argName + " must be a boolean value (" + namedArg.arg() + " was provided)");
    }
    return value != null && (Boolean)value;
  }
}