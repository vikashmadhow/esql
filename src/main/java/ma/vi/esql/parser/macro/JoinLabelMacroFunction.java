/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.macro;

import ma.vi.base.tuple.T4;
import ma.vi.esql.builder.SelectBuilder;
import ma.vi.esql.database.Structure;
import ma.vi.esql.function.Function;
import ma.vi.esql.parser.*;
import ma.vi.esql.parser.expression.*;
import ma.vi.esql.parser.query.*;
import ma.vi.esql.type.Types;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.util.Collections.emptyList;
import static ma.vi.esql.parser.expression.ColumnRef.qualify;

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
public class JoinLabelMacroFunction extends Function implements Macro {
  public JoinLabelMacroFunction(Structure structure) {
    super("joinlabel", Types.StringType, emptyList());
  }

  @Override
  public boolean expand(String name, Esql<?, ?> esql) {
    FunctionCall call = (FunctionCall)esql;
    Context ctx = call.context;
    List<Expression<?>> arguments = call.arguments();

    if (arguments.isEmpty()) {
      throw new TranslationException("joinlabel function need at least two arguments (the id to find the label " +
          "for and the table to join to)");
    }

    /*
     * Load arguments
     */

    /*
     * Each link is a triple: id expression, link_id expression, name expression and target table
     */
    List<T4<Expression<?>, Expression<?>, Expression<?>, String>> links = new ArrayList<>();
    Boolean showLastOnly = null;                // show only the last element (last linked foreign table) of the join
    Expression<?> joinSeparator = null;         // the separator to use between labels from different table (joins)

    Iterator<Expression<?>> i = arguments.iterator();
    while (i.hasNext()) {
      Expression<?> arg = i.next();
      if (arg instanceof NamedArgument) {
        NamedArgument namedArg = (NamedArgument)arg;
        switch (namedArg.name()) {
          case "show_last_only": {
            Object value = namedArg.arg().value(null);
            if (value != null && !(value instanceof Boolean)) {
              throw new TranslationException("show_last_only must be a boolean value (" + namedArg.arg() +
                  " was provided)");
            }
            showLastOnly = (Boolean)value;
            break;
          }
          case "join_separator":
            joinSeparator = namedArg.arg();
            break;
          default:
            throw new TranslationException("Invalid named argument in label: " + namedArg.name());
        }
      } else {
        /*
         * link arguments consist of 3 parts: the id expression, the name expression and the target table
         */
        if (!i.hasNext()) {
          throw new TranslationException("joinlabel needs an id expression, a link id expression, " +
              "a name expression and a table symbol for each link. Only the id expression was " +
              "provided for one link.");
        }
        Expression<?> linkIdExpr = i.next();

        if (!i.hasNext()) {
          throw new TranslationException("joinlabel needs an id expression, a link id expression, " +
              "a name expression and a table symbol for each link. Only the id and link id " +
              "expression was provided for one link.");
        }
        Expression<?> nameExpr = i.next();

        if (!i.hasNext()) {
          throw new TranslationException("joinlabel needs an id expression, a link id expression, " +
              "a name expression and a table symbol for each link. Only the id, link id and name " +
              "expression was provided for one link.");
        }
        Expression<?> tableExpr = i.next();

        String table;
        if (tableExpr instanceof UncomputedExpression) {
          table = tableExpr.translate(Translatable.Target.ESQL);

        } else if (tableExpr instanceof StringLiteral) {
          table = ((StringLiteral)tableExpr).value(null);

        } else {
          throw new TranslationException("Table in joinlabel function must be provided " +
              "as a symbol or a string. " + tableExpr + " was provided.");
        }
        links.add(T4.of(arg, linkIdExpr, nameExpr, table));
      }
    }

    if (links.isEmpty()) {
      throw new TranslationException("No links supplied to joinlabel function");
    }

    /*
     * default value for parameters when not specified
     */
    if (showLastOnly == null) {
      showLastOnly = false;
    }
    if (joinSeparator == null) {
      joinSeparator = new StringLiteral(ctx, "' / '");
    }

    /*
     * joinlabel(linked_id, target_link_id, name, table) is transformed to (pseudo-code):
     *
     *      select t0.name from table t0 where t0.target_link_id=linked_id
     *
     * joinlabel(bu_id, _id, bu_name, BusinessUnit,
     *           company_id, _id, company_name, Company
     *           legal_company_id, _id, legal_company_name, LegalCompany) is transformed to (pseudo-code):
     *
     *      select t3.legal_company_name || ' / ' || t2.company_name || ' / ' || t1.bu_name
     *
     *      from   LegalCompany t3
     *      join   Company      t2 on t2.legal_company_id=t3._id
     *      join   BusinessUnit t1 on t1.company_id=t2._id
     *
     *      where  t1._id=bu_id
     */
    int alias = 0;
    Iterator<T4<Expression<?>, Expression<?>, Expression<?>, String>> linkIter = links.iterator();
    T4<Expression<?>, Expression<?>, Expression<?>, String> link = linkIter.next();

    Expression<?> linkId = toColumnRef(ctx, link.a, null);
    Expression<?> targetLinkId = toColumnRef(ctx, link.b, null);
    Expression<?> linkLabel = toColumnRef(ctx, link.c, "t" + alias);

    // value to show
    Expression<?> value = qualify(linkLabel, "t" + alias, null, true);
    TableExpr from = new SingleTableExpr(ctx, link.d, "t0");
    while (linkIter.hasNext()) {
      alias++;
      link = linkIter.next();
      from = new JoinTableExpr(ctx,
          from, null,
          new SingleTableExpr(ctx, link.d, "t" + alias),
          new Equality(ctx,
              qualify(toColumnRef(ctx, link.a, null), "t" + (alias - 1), null, true),
              qualify(toColumnRef(ctx, link.b, null), "t" + alias, null, true)));
      if (showLastOnly) {
        value = qualify(toColumnRef(ctx, link.c, "t" + alias), "t" + alias, null, true);
      } else {
        value = new Concatenation(ctx, qualify(toColumnRef(ctx, link.c, "t" + alias), "t" + alias, null, true),
            new Concatenation(ctx, joinSeparator, value));
      }
    }

    /*
     * first argument can contain column references should be
     * qualified to ensure that they do not clash with
     * the link table to be added to the query.
     */
    QueryUpdate query = call.ancestor(QueryUpdate.class);
    if (query != null) {
//            String qualifier = null;
      TableExpr queryFrom = query.tables();
      String qualifier = findTableQualifier(queryFrom, linkId);

//            if (queryFrom instanceof SingleTableExpr) {
//                qualifier = ((SingleTableExpr)queryFrom).alias();
//
//            } else if (queryFrom instanceof SelectTableExpr) {
//                qualifier = ((SelectTableExpr)queryFrom).alias();
//
//            } else if (queryFrom instanceof JoinTableExpr) {
//                JoinTableExpr join = (JoinTableExpr)queryFrom;
//                join.left().
//
//            }
      if (qualifier != null) {
        ColumnRef.qualify(linkId, qualifier, null, false);
      }
    }

    Expression<?> firstLinkedId = qualify(toColumnRef(ctx, targetLinkId, "t0"), "t0", null, true);
    call.parent.replaceWith(name, new SelectExpression(ctx,
        new SelectBuilder(ctx)
            .column(value, null)
            .from(from)
            .where(new Equality(ctx, firstLinkedId, linkId))
            .orderBy(firstLinkedId, "asc")
            .limit("1")
            .build()));
    return true;
  }

  public static Expression<?> toColumnRef(Context ctx, Expression<?> expr, String qualifier) {
    if (expr instanceof UncomputedExpression) {
      return new ColumnRef(ctx, qualifier, expr.translate(Translatable.Target.ESQL));

    } else if (expr instanceof StringLiteral) {
      return new ColumnRef(ctx, qualifier, ((StringLiteral)expr).value(null));
    }
    return expr;
  }

  public static String findTableQualifier(TableExpr from, Expression<?> colExpr) {
    ColumnRef col = colExpr.firstChild(ColumnRef.class);
    if (col == null) {
      return null;
    } else {

      if (from instanceof SingleTableExpr && from.type().hasColumn(col.name())) {
        return ((SingleTableExpr)from).alias();

      } else if (from instanceof SelectTableExpr && from.type().hasColumn(col.name())) {
        return ((SelectTableExpr)from).alias();

      } else if (from instanceof JoinTableExpr) {
        JoinTableExpr join = (JoinTableExpr)from;
        String q = findTableQualifier(join.left(), colExpr);
        if (q != null) {
          return q;
        }
        q = findTableQualifier(join.right(), colExpr);
        if (q != null) {
          return q;
        }

      } else if (from instanceof CrossProductTableExpr) {
        CrossProductTableExpr cross = (CrossProductTableExpr)from;
        String q = findTableQualifier(cross.left(), colExpr);
        if (q != null) {
          return q;
        }
        q = findTableQualifier(cross.right(), colExpr);
        if (q != null) {
          return q;
        }
      }
      return null;
    }
  }
}