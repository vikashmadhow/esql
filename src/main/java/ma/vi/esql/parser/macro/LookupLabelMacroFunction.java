/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.macro;

import ma.vi.esql.builder.SelectBuilder;
import ma.vi.esql.database.Structure;
import ma.vi.esql.function.Function;
import ma.vi.esql.parser.*;
import ma.vi.esql.parser.expression.*;
import ma.vi.esql.parser.query.*;
import ma.vi.esql.type.AliasedRelation;
import ma.vi.esql.type.BaseRelation;
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
public class LookupLabelMacroFunction extends Function implements Macro {
  public LookupLabelMacroFunction() {
    super("label", Types.StringType, emptyList());
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
      String qualifier = qualifierFromTableExpr(ctx.structure, query.tables(), code);
      if (qualifier != null) {
        ColumnRef.qualify(code, qualifier, null, false);
      }
    }

    /*
     * second argument is the name of the lookup
     */
    Expression<?> e = i.next();
    String target = e instanceof UncomputedExpression
                        ? e.translate(Translatable.Target.ESQL)
                        : ((StringLiteral)e).value(null);

    /*
     * 3rd and fourth arguments are whether to show code and text
     * of code, respectively.
     */

    /*
     * Load optional arguments
     */
    List<String> links = new ArrayList<>();     // lookup links
    Boolean showCode = null;                    // show the code in the label or not
    Boolean showText = null;                    // show the text in the label or not
    if (i.hasNext()) {
      showCode = ((BooleanLiteral)i.next()).value;
    }
    if (i.hasNext()) {
      showText = ((BooleanLiteral)i.next()).value;
    }
    while (i.hasNext()) {
      Expression<?> arg = i.next();
      if (arg instanceof UncomputedExpression) {
        links.add(arg.translate(Translatable.Target.ESQL));
      } else {
        links.add(((StringLiteral)arg).value(Translatable.Target.ESQL));
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
    Expression<?> labelExpression;       // the expression to compute the label
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
                    new StringLiteral(ctx, "' - '")),
                labelExpression);
      }
    }

    ColumnRef.qualify(labelExpression, "v" + link, null, true);
    call.parent.replaceWith(name, new SelectExpression(ctx,
        new SelectBuilder(ctx)
            .column(labelExpression, null)
            .from(from)
            .where(new Equality(ctx, new ColumnRef(ctx, "v0", "code"),
                code))
            .orderBy(labelExpression, "asc")
            .limit("1")
            .build()));

    return true;
  }

  /**
   * Finds the table alias which (most probably) contains the code column.
   */
  public static String qualifierFromTableExpr(Structure structure,
                                              TableExpr from,
                                              Expression<?> code) {
    if (from instanceof SingleTableExpr) {
      SingleTableExpr table = (SingleTableExpr)from;
      if (code instanceof ColumnRef) {
        BaseRelation relation = structure.relation(table.tableName());
        if (relation == null) {
          return null;
        } else {
          ColumnRef col = (ColumnRef)code;
          return relation.hasColumn(col.name()) ? table.alias() : null;
        }
      } else {
        return table.alias();
      }

    } else if (from instanceof SelectTableExpr) {
      SelectTableExpr select = (SelectTableExpr)from;
      if (code instanceof ColumnRef) {
        AliasedRelation relation = select.type();
        ColumnRef col = (ColumnRef)code;
        return relation.hasColumn(col.name()) ? select.alias() : null;
      } else {
        return select.alias();
      }

    } else if (from instanceof JoinTableExpr) {
      JoinTableExpr join = (JoinTableExpr)from;
      String q = qualifierFromTableExpr(structure, join.left(), code);
      if (q != null) {
        return q;
      } else {
        return qualifierFromTableExpr(structure, join.right(), code);
      }

    } else if (from instanceof CrossProductTableExpr) {
      CrossProductTableExpr cross = (CrossProductTableExpr)from;
      String q = qualifierFromTableExpr(structure, cross.left(), code);
      if (q != null) {
        return q;
      } else {
        return qualifierFromTableExpr(structure, cross.right(), code);
      }

    } else {
      return null;
    }
  }
}