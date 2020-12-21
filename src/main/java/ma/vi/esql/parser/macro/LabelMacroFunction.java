/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.macro;

import ma.vi.esql.parser.*;
import ma.vi.esql.builder.SelectBuilder;
import ma.vi.esql.parser.define.ForeignKeyConstraint;
import ma.vi.esql.parser.expression.*;
import ma.vi.esql.parser.query.*;
import ma.vi.esql.database.Structure;
import ma.vi.esql.function.Function;
import ma.vi.esql.type.BaseRelation;
import ma.vi.esql.type.Relation;
import ma.vi.esql.type.Types;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static ma.vi.esql.builder.Attributes.*;

/**
 * A macro function which produces a label corresponding to an id/code. It can be
 * used as follows:
 *
 * <ul>
 *   <li><b>One-to-one association:</b> To get the label corresponding to an id
 *       referring to another table. For instance, if table A {b_id} refers to
 *       B{id, name} with A.b_id being a foreign key pointing to B.id and B.name
 *       is set as the string_form for the table B then <b>label(b_id, B)</b> will
 *       return the name from B corresponding to b_id.
 *   </li>
 *
 *   <li><b>lookup code resolution:</b> <b>label(code, X)</b> will get the label
 *       corresponding to code from a lookup table named X. For lookups, a variable number
 *       of named links can be supplied to find linked valued. E.g.
 *       <b>label(code, X, Y, Z)</b> will find the code in lookup X, follow its
 *       link to Y and then Z and return the label for the latter. A lookup name
 *       must be a single identifier (does not contain any dots) and this is how
 *       the system distinguishes between tables and lookups.
 *   </li>
 * </ul>
 * <p>
 * label can have the following optional named arguments to control the value displayed:
 * <ul>
 *     <li><b>expression: </b> an expression defining how to produce the label.
 *         For linked tables, this is the string_form expression if defined on the
 *         table, the first non-id column otherwise. For lookups, this is the label
 *         column.</li>
 *     <li><b>show_code:</b> whether to show the code in the label or not. This
 *         applies only when an expression has not been supplied.</li>
 *     <li><b>show_text:</b> whether to show the label text in the label or not. This
 *         applies only when an expression has not been supplied.</li>
 *     <li><b>separator:</b> an expression for the separator between the code and text.</li>
 * </ul>
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class LabelMacroFunction extends Function implements Macro {
  public LabelMacroFunction(Structure structure) {
    super("label", Types.StringType, emptyList());
  }

  @Override
  public boolean expand(String name, Esql<?, ?> esql) {
    FunctionCall call = (FunctionCall)esql;
    Context ctx = call.context;
    List<Expression<?>> arguments = call.arguments();

    if (arguments.isEmpty()) {
      throw new TranslationException("label function need at least one argument (the code to find the label for)");
    }

    /*
     * first argument can contain column references should be
     * qualified to ensure that they do not clash with
     * the lookup/link table to be added to the query.
     */
    Expression<?> code = arguments.get(0);
    QueryUpdate query = call.ancestor(QueryUpdate.class);
    if (query != null) {
      String qualifier = null;
      TableExpr from = query.tables();
      if (from instanceof SingleTableExpr) {
        qualifier = ((SingleTableExpr)from).alias();

      } else if (from instanceof SelectTableExpr) {
        qualifier = ((SelectTableExpr)from).alias();
      }
      if (qualifier != null) {
        ColumnRef.qualify(code, qualifier, null, false);
      }
    }

    /*
     * second argument is optional. If it is provided it is a symbol for a table
     * or lookup. A table will have a dot (.) in its name while a lookup won't.
     * If neither is provided the first argument must be a column reference and
     * this macro must be part of query/update statement in which case the metadata
     * and structure information of the column reference is used to determine the
     * code table attached to the column or link table it refers to.
     */
    String target = null;                       // target table or lookup
    String targetField = null;                  // target field to link code/value to
    Expression<?> labelExpression = null;       // the expression to compute the label
    List<String> links = new ArrayList<>();     // lookup links
    Boolean showCode = null;                    // show the code in the label or not
    Boolean showText = null;                    // show the text in the label or not
    Expression<?> separator = null;             // the separator to use between code and text in the label

    for (int i = 1; i < arguments.size(); i++) {
      Expression<?> arg = arguments.get(i);
      if (arg instanceof UncomputedExpression) {
        if (target == null) {
          target = arg.translate(Translatable.Target.ESQL);
        } else {
          links.add(arg.translate(Translatable.Target.ESQL));
        }
      } else if (arg instanceof NamedArgument) {
        NamedArgument namedArg = (NamedArgument)arg;
        switch (namedArg.name()) {
          case "expression" -> labelExpression = namedArg.arg();
          case "show_text" -> {
            Object value = namedArg.arg().value(null);
            if (value != null && !(value instanceof Boolean)) {
              throw new TranslationException("show_label must be a boolean value (" + namedArg.arg() +
                                                 " was provided)");
            }
            showText = (Boolean)value;
          }
          case "show_code" -> {
            Object value = namedArg.arg().value(null);
            if (value != null && !(value instanceof Boolean)) {
              throw new TranslationException("show_code must be a boolean value (" + namedArg.arg() +
                                                 " was provided)");
            }
            showCode = (Boolean)value;
          }
          case "separator" -> separator = namedArg.arg();
          default -> throw new TranslationException("Invalid named argument in label: " + namedArg.name());
        }
      }
    }

    if (target == null) {
      /*
       * No explicit table or lookup provided: determine from table structure
       */
      if (query == null) {
        throw new TranslationException("label function has not been provided with a table or lookup " +
            "and is not in the context of a query (which could be used " +
            "to determine the table/lookup).");
      }
      if (!(code instanceof ColumnRef)) {
        throw new TranslationException("label function has not been provided with a table or lookup " +
            "and the first argument is not a column reference. Thus " +
            "the table/lookup to use cannot be determined.");
      }

      ColumnRef column = (ColumnRef)code;
      String qualifiedName = column.qualifiedName();

      Relation relation = query.tables().type();
      Column field = relation.column(qualifiedName);
      if (field == null) {
        throw new TranslationException(qualifiedName + " could not be found in " + relation);
      }

      Expression<?> attribute = field.attribute(LINK_TABLE).attributeValue();
      if (attribute != null) {
        /*
         * link table specified: use
         */
        Object eval = attribute.value(null);
        if (!(eval instanceof String)) {
          throw new TranslationException("Invalid link table specified on field " + column + " (" + eval + ").");
        }
        target = (String)eval;
      } else if (relation instanceof BaseRelation) {
        /*
         * use foreign key information to locate target table
         */
        BaseRelation rel = (BaseRelation)relation;
        String columnName = column.name();
        ForeignKeyConstraint foreign = null;
        for (ForeignKeyConstraint fk: rel.dependentConstraints()) {
          if (fk.sourceColumns().contains(columnName)) {
            foreign = fk;
            break;
          }
        }

        if (foreign == null) {
          throw new TranslationException("label function could not find a foreign key from " + columnName +
              " from " + relation + " to some target table to use for " +
              "computing the label.");
        }
        target = foreign.targetTable();
        targetField = foreign.targetColumns().get(0);
      }
    } else {
      throw new TranslationException("Missing link table information");
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

    /*
     * build query and replace function call with it
     */
    if (!lookup) {
      /*
       * Target table
       */
      if (targetField == null) {
        /*
         * If target field not available, use id.
         */
        targetField = "_id";
      }
      /*
       * string form of target table data if label expression is not provided
       */
      BaseRelation relation = ctx.structure.relation(target);
      if (labelExpression == null) {
        if (!showText) {
          /*
           * Show code only
           */
          labelExpression = new ColumnRef(ctx, null, targetField);
        } else {
          Expression<?> expr = relation.attribute(STRING_FORM);
          if (expr != null) {
            labelExpression = expr;
          } else {
            /*
             * no explicit string form: use field marked as main display member
             * or the first non-hidden field
             */
            Column firstNonId = null;
            for (Column field: relation.columns()) {
              if (!field.alias().startsWith("_") && firstNonId == null) {
                firstNonId = field;
              }
              if (field.attribute(MAIN_COLUMN) != null) {
                labelExpression = new ColumnRef(ctx, null, field.alias());
              }
            }
            if (labelExpression == null) {
              if (firstNonId == null) {
                throw new TranslationException("Could not find a column to use for display " +
                    "of the table " + target);
              } else {
                labelExpression = new ColumnRef(ctx, null, firstNonId.alias());
              }
            }
          }
          if (showCode) {
            /*
             * Preprend code and separator
             */
            labelExpression =
                new Concatenation(
                    ctx,
                    new Concatenation(ctx,
                        new ColumnRef(ctx, null, targetField), separator),
                    labelExpression);
          }
        }
      }
      call.parent.replaceWith(name, new SelectExpression(ctx,
          new SelectBuilder(ctx)
              .column(labelExpression, null)
              .from(target, "code")
              .where(new Equality(ctx, new ColumnRef(ctx, "code", targetField), code))
              .orderBy(labelExpression, "asc")
              .limit("1")
              .build()));
    } else {
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
                new Concatenation(
                    ctx,
                    new Concatenation(ctx,
                        new ColumnRef(ctx, null, "code"), separator),
                    labelExpression);
          }
        }
      }
      ColumnRef.qualify(labelExpression, "v" + link, null, true);
      call.parent.replaceWith(name, new SelectExpression(ctx,
          new SelectBuilder(ctx)
              .column(labelExpression, null)
              .from(from)
              .where(new Equality(ctx, new ColumnRef(ctx, "v0", "code"), code))
              .orderBy(labelExpression, "asc")
              .limit("1")
              .build()));
    }
    return true;
  }
}