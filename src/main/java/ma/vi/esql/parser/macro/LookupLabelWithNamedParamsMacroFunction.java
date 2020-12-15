/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.esql.parser.macro;

import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.Parser;
import ma.vi.esql.parser.Translatable;
import ma.vi.esql.parser.Esql;
import ma.vi.esql.parser.Macro;
import ma.vi.esql.parser.QueryUpdate;
import ma.vi.esql.parser.TranslationException;
import ma.vi.esql.builder.SelectBuilder;
import ma.vi.esql.parser.expression.*;
import ma.vi.esql.parser.query.JoinTableExpr;
import ma.vi.esql.parser.query.SingleTableExpr;
import ma.vi.esql.parser.query.TableExpr;
import ma.vi.esql.function.Function;
import ma.vi.esql.type.Types;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.util.Collections.emptyList;

/**
 * A macro function which produces a label corresponding to a lookup code. It can be
 * used as follows:
 *
 * <b>label(code, X)</b> will get the label corresponding to code from a lookup table named X.
 * A variable number of named links can be supplied to find linked valued. E.g.
 * <b>label(code, X, Y, Z)</b> will find the code in lookup X, follow its
 * link to Y and then Z and return the label for the latter.
 * <p>
 * label can have the following optional named arguments to control the value displayed:
 * <ul>
 *     <li><b>label_expression: </b> an expression defining how to produce the label. This is the label
 *                                   column, by default.</li>
 *     <li><b>match_by_id: </b> true if the code provided is the id of the lookup value, instead of the
 *                              default code value.</li>
 *     <li><b>show_code:</b> whether to show the code in the label or not. This
 *                           applies only when an expression has not been supplied.</li>
 *     <li><b>show_text:</b> whether to show the label text in the label or not. This
 *                           applies only when an expression has not been supplied.</li>
 *     <li><b>separator:</b> an expression for the separator between the code and text.</li>
 * </ul>
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class LookupLabelWithNamedParamsMacroFunction extends Function implements Macro {
  public LookupLabelWithNamedParamsMacroFunction() {
    super("lookuplabel", Types.StringType, emptyList());
  }

  @Override
  public boolean expand(String name, Esql<?, ?> esql) {
    FunctionCall call = (FunctionCall)esql;
    Context ctx = call.context;
    List<Expression<?>> arguments = call.arguments();

    if (arguments.size() < 2) {
      throw new TranslationException("label function need at least two arguments (" +
          "the code to find the label for and the lookup containing the code)");
    }

    /*
     * first argument can contain column references should be
     * qualified to ensure that they do not clash with
     * the lookup/link table to be added to the query.
     */
    Iterator<Expression<?>> i = arguments.iterator();
    Expression<?> code = i.next();
    QueryUpdate query = call.ancestor(QueryUpdate.class);
    if (query != null) {
//            String qualifier = null;
//            TableExpr from = query.tables();
//            if (from instanceof SingleTableExpr) {
//                qualifier = ((SingleTableExpr)from).alias();
//
//            } else if (from instanceof SelectTableExpr) {
//                qualifier = ((SelectTableExpr)from).alias();
//
//            } else if (from instanceof JoinTableExpr) {
//
//            }
      String qualifier = LookupLabelMacroFunction.qualifierFromTableExpr(ctx.structure, query.tables(), code);
      if (qualifier != null) {
        ColumnRef.qualify(code, qualifier, null, false);
      }
    }

    /*
     * second argument is the name of the lookup
     */
    Expression<?> e = i.next();
    String target = e instanceof Symbol
                    ? ((Symbol)e).name()
                    : ((StringLiteral)e).value(null);

    /*
     * Load named arguments
     */
    Expression<?> labelExpression = null;       // the expression to compute the label
    List<String> links = new ArrayList<>();     // lookup links
    Boolean showCode = null;                    // show the code in the label or not
    Boolean showText = null;                    // show the text in the label or not
    Boolean matchById = null;                   // search by ID instead of code
    Expression<?> separator = null;             // the separator to use between code and text in the label
    while (i.hasNext()) {
      Expression<?> arg = i.next();
      if (arg instanceof NamedArgument) {
        NamedArgument namedArg = (NamedArgument)arg;
        switch (namedArg.name()) {
          case "label_expression":
            labelExpression = namedArg.arg();
            break;

          case "show_text": {
            Object value = namedArg.arg().value(null);
            if (value != null && !(value instanceof Boolean)) {
              throw new TranslationException("show_label must be a boolean value (" + namedArg.arg() +
                  " was provided)");
            }
            showText = (Boolean)value;
            break;
          }
          case "show_code": {
            Object value = namedArg.arg().value(null);
            if (value != null && !(value instanceof Boolean)) {
              throw new TranslationException("show_code must be a boolean value (" + namedArg.arg() +
                  " was provided)");
            }
            showCode = (Boolean)value;
            break;
          }
          case "match_by_id": {
            Object value = namedArg.arg().value(null);
            if (value != null && !(value instanceof Boolean)) {
              throw new TranslationException("match_by_id must be a boolean value (" + namedArg.arg() +
                  " was provided)");
            }
            matchById = (Boolean)value;
            break;
          }
          case "separator":
            separator = namedArg.arg();
            break;

          default:
            throw new TranslationException("Invalid named argument in lookuplabel: " + namedArg.name());
        }
      } else {
        if (arg instanceof Symbol) {
          links.add(((Symbol)arg).name());
        } else {
          links.add(((StringLiteral)arg).value(Translatable.Target.ESQL));
        }
      }
    }

    /*
     * default value for parameters when not specified
     */
    boolean lookup = target.indexOf('.') == -1;
    if (showCode == null) {
      showCode = lookup;
    }
    if (showText == null) {
      showText = true;
    }
    if (separator == null) {
      separator = new StringLiteral(ctx, "' - '");
    }
    if (matchById == null) {
      matchById = false;
    }

    /*
     * lookup table:
     *
     * label('123', X) is transformed to (pseudo-code):
     *      select v0.code || ' - ' || v0.label
     *      from  Values v0 join Lookup l on v.lookup_id=l.id
     *      where l.name='X' and v0.code='123'
     *
     * label('123', X, Y) is transformed to (pseudo-code):
     *      select v1.code || ' - ' || v1.label
     *      from  Values v1
     *      join  LookupLink lk1 on v1.id=lk1.target_value_id
     *      join  Values v0 on (lk1.source_value_id=v0.id and lk
     *      join Lookup l on v.lookup_id=l.id
     *      where l.name='X' and v0.code='123'
     */
    int link = 0;
    Parser parser = new Parser(ctx.structure);
    TableExpr from = new JoinTableExpr(ctx,
        new SingleTableExpr(ctx, "_platform.lookup.LookupValue", "v0"), null,
        new SingleTableExpr(ctx, "_platform.lookup.Lookup", "lookup"),
        parser.parseExpression("(v0.lookup_id=lookup._id and lookup.name='" + target + "')"));
    for (String linkName: links) {
      from = new JoinTableExpr(ctx, from, null,
          new SingleTableExpr(ctx, "_platform.lookup.LookupValueLink", "lk" + link),
          parser.parseExpression("(v" + link + "._id=lk" + link + ".source_lookup_value_id and " +
              "lk" + link + ".lookup_link='" + linkName + "')"));
      link++;
      from = new JoinTableExpr(ctx, from, null,
          new SingleTableExpr(ctx, "_platform.lookup.LookupValue", "v" + link),
          parser.parseExpression("(v" + link + "._id=lk" + (link - 1) + ".target_lookup_value_id)"));

    }
    if (labelExpression == null) {
      if (!showText) {
        /*
         * Show code only
         */
        labelExpression = new ColumnRef(ctx, null, "code");
      } else {
        labelExpression = new ColumnRef(ctx, null, "label");
        if (showCode) {
          /*
           * Prepend code and separator
           */
          labelExpression =
              new Concatenation(ctx,
                  new Concatenation(ctx,
                      new ColumnRef(ctx, null, "code"),
                      separator),
                  labelExpression);
        }
      }
    }

    ColumnRef.qualify(labelExpression, "v" + link, null, true);
    call.parent.replaceWith(name, new SelectExpression(ctx,
        new SelectBuilder(ctx)
            .column(labelExpression, null)
            .from(from)
            .where(new Equality(ctx,
                matchById ? new ColumnRef(ctx, "v0", "_id")
                          : new ColumnRef(ctx, "v0", "code"),
                code))
            .orderBy(labelExpression, "asc")
            .limit("1")
            .build()));

    return true;
  }
}