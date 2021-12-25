/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax;

import ma.vi.esql.grammar.EsqlBaseListener;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.define.*;
import ma.vi.esql.syntax.expression.*;
import ma.vi.esql.syntax.expression.arithmetic.*;
import ma.vi.esql.syntax.expression.comparison.*;
import ma.vi.esql.syntax.expression.literal.BooleanLiteral;
import ma.vi.esql.syntax.expression.literal.DateLiteral;
import ma.vi.esql.syntax.expression.literal.FloatingPointLiteral;
import ma.vi.esql.syntax.expression.literal.IntegerLiteral;
import ma.vi.esql.syntax.expression.literal.IntervalLiteral;
import ma.vi.esql.syntax.expression.literal.NullLiteral;
import ma.vi.esql.syntax.expression.literal.StringLiteral;
import ma.vi.esql.syntax.expression.literal.UuidLiteral;
import ma.vi.esql.syntax.expression.literal.*;
import ma.vi.esql.syntax.expression.logical.And;
import ma.vi.esql.syntax.expression.logical.Not;
import ma.vi.esql.syntax.expression.logical.Or;
import ma.vi.esql.syntax.modify.Delete;
import ma.vi.esql.syntax.modify.Insert;
import ma.vi.esql.syntax.modify.InsertRow;
import ma.vi.esql.syntax.modify.Update;
import ma.vi.esql.syntax.query.*;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.*;

import static java.lang.Boolean.parseBoolean;
import static java.lang.Long.parseLong;
import static java.lang.System.Logger.Level.ERROR;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static ma.vi.esql.grammar.EsqlParser.*;
import static ma.vi.esql.syntax.define.ConstraintDefinition.ForeignKeyChangeAction;
import static ma.vi.esql.syntax.define.ConstraintDefinition.ForeignKeyChangeAction.*;
import static ma.vi.esql.syntax.query.GroupBy.Type.*;

/**
 * The analyser interprets the syntactic tree produced by the parser for an ESQL
 * program and statement and produces a {@link Esql} or list of Esql statements
 * that can be executed.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class SyntaxAnalyser extends EsqlBaseListener {
  public SyntaxAnalyser(Context context) {
    this.context = context;
  }

  @Override
  public void visitErrorNode(ErrorNode node) {
    Token token = node.getSymbol();
    String text = token.getTokenSource().getInputStream().toString();
    log.log(ERROR, "ERROR in " + text + " at " + token.getLine() + ':' + token.getCharPositionInLine());
    throw new SyntaxException("ERROR in " + text + " (" + token.getText() + ") "
                            + "at " + token.getLine() + ':' + token.getCharPositionInLine(), null, text,
                              token.getLine(), token.getCharPositionInLine(), token.getStartIndex(),
                              token.getLine(), token.getCharPositionInLine() + token.getText().length(), token.getStopIndex());
  }

  public static void error(ParserRuleContext ctx, String message) throws SyntaxException {
    int startLine = ctx.getStart().getLine();
    int startPos = ctx.getStart().getCharPositionInLine();
    int startIndex = ctx.getStart().getStartIndex();

    Token stop = ctx.getStop();
    int stopLine = stop == null ? 0 : stop.getLine();
    int stopPos = stop == null ? 0 : stop.getCharPositionInLine();
    int stopIndex = stop == null ? 0 : stop.getStartIndex();

    String text = ctx.start.getInputStream().toString();
    log.log(ERROR, "ERROR " + message + " in '" + text + "' at [" + startLine + ":"
                + startPos + "]");

    throw new SyntaxException(message, null, text, startLine, startPos,
                              startIndex, stopLine, stopPos, stopIndex);
  }

  public static void error(String message, int startLine, int startPos) throws SyntaxException {
    log.log(ERROR, "ERROR " + message + "' at [" + startLine + ":" + startPos + "]");
    throw new SyntaxException(message, null, null, startLine, startPos, 0, 0, 0, 0);
  }

  @Override
  public void exitEveryRule(ParserRuleContext ctx) {
    lastRecognised = get(ctx);
  }

  @Override
  public void exitProgram(ProgramContext ctx) {
    put(ctx, new Program(context, value(ctx.expressions())));
  }

  @Override
  public void exitExpressions(ExpressionsContext ctx) {
    put(ctx, new Esql<>(context, ctx.expr().stream().map(e -> (Expression<?, ?>)get(e)).toList()));
  }

//  @Override
//  public void exitStatement(StatementContext ctx) {
//    if (ctx.select() != null) {
//      put(ctx, get(ctx.select()));
//
//    } else if (ctx.modify() != null) {
//      if (ctx.modify().insert() != null) {
//        put(ctx, get(ctx.modify().insert()));
//
//      } else if (ctx.modify().delete() != null) {
//        put(ctx, get(ctx.modify().delete()));
//
//      } else if (ctx.modify().update() != null) {
//        put(ctx, get(ctx.modify().update()));
//      }
//    } else if (ctx.define() != null) {
//      put(ctx, get(ctx.define()));
//    }
//  }

  @Override
  public void exitNoopStatement(NoopStatementContext ctx) {
    put(ctx, get(ctx.noop()));
  }

  @Override
  public void exitNoop(NoopContext ctx) {
    put(ctx, new NoOp(context));
  }

  // select
  ////////////////////////////////////////////////

  @Override
  public void exitSelectStatement(SelectStatementContext ctx) {
    put(ctx, get(ctx.select()));
  }

  @Override
  public void exitModifyStatement(ModifyStatementContext ctx) {
    put(ctx, get(ctx.modify()));
  }

  @Override
  public void exitDefineStatement(DefineStatementContext ctx) {
    put(ctx, get(ctx.define()));
  }

  @Override
  public void exitBaseSelection(BaseSelectionContext ctx) {
    DistinctContext distinct = ctx.distinct();
    if (ctx.tableExpr() == null) {
      error(ctx,"Missing or wrong from clause in Select");
    }
    if (ctx.columns() == null) {
      error(ctx,"No columns specified in Select");
    }
    put(ctx, new Select(context,
                        ctx.metadata() == null ? null : get(ctx.metadata()),
                        distinct != null && distinct.getText().startsWith("distinct"),
                        distinct != null && distinct.expressionList() != null ? value(distinct.expressionList()) : null,
                        ctx.explicit() != null,
                        value(ctx.columns()),
                        ctx.tableExpr() == null ? null : get(ctx.tableExpr()),
                        ctx.where == null ? null : get(ctx.where),
                        get(ctx.groupByList()),
                        ctx.having == null ? null : get(ctx.having),
                        ctx.orderByList() == null ? null : value(ctx.orderByList()),
                        ctx.offset == null ? null : get(ctx.offset),
                        ctx.limit == null ? null : get(ctx.limit)));
  }

  @Override
  public void exitWithSelection(WithSelectionContext ctx) {
    put(ctx, get(ctx.with()));
  }

  @Override
  public void exitCompositeSelection(CompositeSelectionContext ctx) {
    String operator = value(ctx.setop());
    put(ctx, new CompositeSelects(context, operator.equals("unionall") ? "union all" : operator,
                                  ctx.select().stream()
                                     .map(s -> (Select)get(s))
                                     .toList()));
  }

  @Override
  public void exitSetop(SetopContext ctx) {
    put(ctx, new Esql<>(context, ctx.getText()));
  }


  @Override
  public void exitColumns(ColumnsContext ctx) {
    /*
     * columns are mapped to list of Column objects
     */
    put(ctx, new Esql<>(context, ctx.column().stream()
                                    .map(this::get)
                                    .toList()));
  }

  @Override
  public void exitSimpleGroup(SimpleGroupContext ctx) {
    put(ctx, new GroupBy(context, value(ctx.expressionList()), Simple));
  }

  @Override
  public void exitRollupGroup(RollupGroupContext ctx) {
    put(ctx, new GroupBy(context, value(ctx.expressionList()), Rollup));
  }

  @Override
  public void exitCubeGroup(CubeGroupContext ctx) {
    put(ctx, new GroupBy(context, value(ctx.expressionList()), Cube));
  }

  @Override
  public void exitSingleColumn(SingleColumnContext ctx) {
    Metadata metadata = get(ctx.metadata());
    put(ctx, new Column(context, value(ctx.alias()), get(ctx.expr()), metadata));
  }

  @Override
  public void exitStarColumn(StarColumnContext ctx) {
    put(ctx, new StarColumn(context, ctx.qualifier() == null
                                       ? null
                                       : ctx.qualifier().Identifier().getText()));
  }

  @Override
  public void exitSingleTableExpr(SingleTableExprContext ctx) {
    put(ctx, new SingleTableExpr(context, value(ctx.qualifiedName()), value(ctx.alias())));
  }

  @Override
  public void exitDynamicTableExpr(DynamicTableExprContext ctx) {
    put(ctx, new DynamicTableExpr(context,
                                  value(ctx.alias()),
                                  get(ctx.metadata()),
                                  value(ctx.dynamicColumns()),
                                  ctx.rows() == null ? null : value(ctx.rows())));
  }

  @Override
  public void exitNameWithMetadata(NameWithMetadataContext ctx) {
    put(ctx, new NameWithMetadata(context,
                                  ctx.Identifier().getText(),
                                  get(ctx.metadata())));
  }

  @Override
  public void exitDynamicColumns(DynamicColumnsContext ctx) {
    /*
     * map list of names (identifiers) to a list of string
     */
    put(ctx, new Esql<>(context,  ctx.nameWithMetadata().stream()
                                     .map(this::get).toList()));
  }

  @Override
  public void exitSelectTableExpr(SelectTableExprContext ctx) {
    put(ctx, new SelectTableExpr(context, get(ctx.select()), value(ctx.alias())));
  }

  @Override
  public void exitJoinTableExpr(JoinTableExprContext ctx) {
    put(ctx, new JoinTableExpr(context,
                               ctx.JoinType() == null ? null : ctx.JoinType().getText(),
                               get(ctx.left),
                               get(ctx.right),
                               get(ctx.expr())));
  }

  @Override
  public void exitCrossProductTableExpr(CrossProductTableExprContext ctx) {
    put(ctx, new CrossProductTableExpr(context, get(ctx.left), get(ctx.right)));
  }

  @Override
  public void exitOrderByList(OrderByListContext ctx) {
    put(ctx, new Esql<>(context, ctx.orderBy().stream()
                                    .map(c -> (Order)get(c))
                                    .toList()));
  }

  @Override
  public void exitOrderBy(OrderByContext ctx) {
    put(ctx, new Order(context, get(ctx.expr()), ctx.direction() == null ? null : ctx.direction().getText()));
  }

  @Override
  public void exitGroupingExpr(GroupingExprContext ctx) {
    put(ctx, new GroupedExpression(context, get(ctx.expr())));
  }

  @Override
  public void exitUncomputedExpr(UncomputedExprContext ctx) {
    put(ctx, new UncomputedExpression(context, get(ctx.expr())));
  }

  // With and CTE
  //////////////////////////////////////////////////

  @Override
  public void exitWith(WithContext ctx) {
    put(ctx, new With(context,
                      ctx.recursive != null,
                      value(ctx.cteList()),
                      get(ctx.queryUpdate())));
  }

  @Override
  public void exitCteList(CteListContext ctx) {
    put(ctx, new Esql<>(context, ctx.cte().stream()
                                    .map(e -> (Cte)get(e))
                                    .toList()));
  }

  @Override
  public void exitCte(CteContext ctx) {
    put(ctx, new Cte(context, ctx.Identifier().getText(),
                     ctx.names() == null ? null : value(ctx.names()),
                     get(ctx.queryUpdate())));
  }

  // @todo Window operations

  // modification queries (insert, delete, update)
  ////////////////////////////////////////////////////////////////

  @Override
  public void exitQueryUpdate(QueryUpdateContext ctx) {
    put(ctx, ctx.select() != null ? get(ctx.select()) : get(ctx.modify()));
  }

  @Override
  public void exitModify(ModifyContext ctx) {
    put(ctx, ctx.insert() != null ? get(ctx.insert()) :
             ctx.update() != null ? get(ctx.update()) :
             get(ctx.delete()));
  }

  // insert
  ////////////////////////////////////////////////

  @Override
  public void exitInsert(InsertContext ctx) {
    put(ctx, new Insert(context,
                        new SingleTableExpr(context, value(ctx.qualifiedName()), null),
                        ctx.names() == null ? null : value(ctx.names()),
                        ctx.rows() == null ? null : value(ctx.rows()),
                        ctx.defaultValues() != null,
                        ctx.select() == null ? null : get(ctx.select()),
                        ctx.metadata() == null ? null : get(ctx.metadata()),
                        ctx.columns() == null ? null : value(ctx.columns())));
  }

  @Override
  public void exitRows(RowsContext ctx) {
    /*
     * rows becomes list of InsertRow where each row is a list of expressions
     */
    put(ctx, new Esql<>(context, ctx.row().stream()
                                    .map(this::get)
                                    .toList()));
  }

  @Override
  public void exitRow(RowContext ctx) {
    /*
     * a row is mapped to an InsertRow (a list of expression)
     */
    put(ctx, new InsertRow(context, value(ctx.expressionList())));
  }

  @Override
  public void exitDefaultValue(DefaultValueContext ctx) {
    put(ctx, new DefaultValue(context));
  }

  // delete
  ///////////////////////////////////////////

  @Override
  public void exitDelete(DeleteContext ctx) {
    put(ctx, new Delete(context,
                        value(ctx.alias()),
                        get(ctx.tableExpr()),
                        ctx.expr() == null ? null : get(ctx.expr()),
                        ctx.metadata() == null ? null : get(ctx.metadata()),
                        ctx.columns() == null ? null : value(ctx.columns())));
  }

  // update
  ///////////////////////////////////////////

  @Override
  public void exitUpdate(UpdateContext ctx) {
    put(ctx, new Update(context,
                        value(ctx.alias()),
                        get(ctx.tableExpr()),
                        get(ctx.setList()),
                        ctx.expr() == null ? null : get(ctx.expr()),
                        ctx.metadata() == null ? null : get(ctx.metadata()),
                        ctx.columns() == null ? null : value(ctx.columns())));
  }

  @Override
  public void exitSetList(SetListContext ctx) {
    put(ctx, new Metadata(context, ctx.set().stream()
                                      .map(a -> (Attribute)get(a))
                                      .toList()));
  }

  @Override
  public void exitSet(SetContext ctx) {
    put(ctx, new Attribute(context, ctx.qualifiedName().getText(), get(ctx.expr())));
  }

  // literals
  ////////////////////////////////////////////////

  @Override
  public void exitLiteralExpr(LiteralExprContext ctx) {
    put(ctx, get(ctx.literal()));
  }

  @Override
  public void exitSimpleLiteralExpr(SimpleLiteralExprContext ctx) {
    put(ctx, get(ctx.literal()));
  }

  @Override
  public void exitInteger(IntegerContext ctx) {
    put(ctx, new IntegerLiteral(context, parseLong(ctx.getText())));
  }

  @Override
  public void exitFloatingPoint(FloatingPointContext ctx) {
    put(ctx, new FloatingPointLiteral(context, ctx.getText()));
  }

  @Override
  public void exitBoolean(BooleanContext ctx) {
    put(ctx, new BooleanLiteral(context, parseBoolean(ctx.getText())));
  }

  @Override
  public void exitString(StringContext ctx) {
    put(ctx, new StringLiteral(context, ctx.getText()));
  }

  @Override
  public void exitUuid(UuidContext ctx) {
    String text = ctx.getText();
    put(ctx, new UuidLiteral(context, UUID.fromString(text.substring(2, text.length() - 1))));
  }

  @Override
  public void exitMultiLineString(MultiLineStringContext ctx) {
    /*
     * Strip left margin from multi-line string:
     *  text: `func x(a,b) {
     *           a = a^b
     *           return a%b
     *         }`
     * or
     *  text: `func x(a,b) {
     *           a = a^b
     *           return a%b
     *         }`
     *
     * will produce the following string after stripping:
     * func x(a,b) {
     *   a = a^b
     *   return a%b
     * }
     */
    String text = ctx.getText();
    int margin = ctx.getStart().getCharPositionInLine() + 1;
    StringBuilder stripped = new StringBuilder("'");
    boolean first = true;
    String[] lines = text.substring(1, text.length() - 1).split("\n");
    if (lines.length > 0) {
      if (lines[0].trim().length() == 0) {
        margin -= 1;
      } else {
        stripped.append(lines[0].replace("'", "''"));
        first = false;
      }
      for (int i = 1; i < lines.length; i++) {
        int j = 0;
        for (; j < Math.min(margin, lines[i].length())
            && Character.isSpaceChar(lines[i].charAt(j)); j++);
        if (i < lines.length - 1
         || lines[i].trim().length() > 0) {
          stripped.append(first ? "" : "\n")
                  .append(lines[i].substring(j).replace("'", "''"));
          first = false;
        }
      }
    }
    stripped.append('\'');
    put(ctx, new StringLiteral(context, stripped.toString()));
  }

  @Override
  public void exitDate(DateContext ctx) {
    String text = ctx.getText();
    put(ctx, new DateLiteral(context, text.substring(2, text.length() - 1)));
  }

  @Override
  public void exitInterval(IntervalContext ctx) {
    String text = ctx.getText();
    put(ctx, new IntervalLiteral(context, text.substring(2, text.length() - 1)));
  }

  @Override
  public void exitNull(NullContext ctx) {
    put(ctx, new NullLiteral(context));
  }

  @Override
  public void exitBasicLiterals(BasicLiteralsContext ctx) {
    put(ctx, get(ctx.baseLiteral()));
  }

  @Override
  public void exitBaseArrayLiteral(BaseArrayLiteralContext ctx) {
    String componentType = ctx.Identifier().getText();
    Type type = Types.findTypeOf(componentType);
    if (type == null) {
      error(ctx, "Unknown type " + componentType + " for array literal");
    }
    put(ctx, new BaseArrayLiteral(context, type, value(ctx.baseLiteralList())));
  }

  @Override
  public void exitJsonArrayLiteral(JsonArrayLiteralContext ctx) {
    put(ctx, new JsonArrayLiteral(context, value(ctx.literalList())));
  }

  @Override
  public void exitJsonObjectLiteral(JsonObjectLiteralContext ctx) {
    put(ctx, new JsonObjectLiteral(context, value(ctx.attributeList())));
  }

  @Override
  public void exitBaseLiteralList(BaseLiteralListContext ctx) {
    put(ctx, new Esql<>(context, ctx.baseLiteral().stream()
                                    .map(e -> (BaseLiteral<?>)get(e))
                                    .toList()));
  }

  @Override
  public void exitLiteralList(LiteralListContext ctx) {
    put(ctx, new Esql<>(context, ctx.literal().stream()
                                    .map(e -> (Literal<?>)get(e))
                                    .toList()));
  }

  // expressions
  ///////////////////////////////////////////////////

  @Override
  public void exitConcatenationExpr(ConcatenationExprContext ctx) {
    createConcatenation(ctx, ctx.expr());
  }

  @Override
  public void exitSimpleConcatenationExpr(SimpleConcatenationExprContext ctx) {
    createConcatenation(ctx, ctx.simpleExpr());
  }

  private void createConcatenation(ParserRuleContext ctx,
                                   List<? extends ParserRuleContext> expressions) {
    boolean optimised = false;
    if (expressions.size() == 2) {
      /*
       * Chained concatenation statements are broken in 2-parts corresponding to
       * (expr '||' expr). If the first expr is a concatenation expression, we can
       * optimise the whole concatenation function by combining it into a single one.
       *
       * Thus (concatenation || e3) where concatenation is (e1 || e2) is combined into
       * (e1 || e2 || e3)
       */
      Expression<?, String> first = get(expressions.get(0));
      Expression<?, String> second = get(expressions.get(1));
      if (first instanceof Concatenation) {
        Concatenation concat = (Concatenation)first;
        List<Expression<?, ?>> concatExprs = new ArrayList<>(concat.expressions());
        concatExprs.add(second);
        put(ctx, new Concatenation(context, concatExprs));
        optimised = true;

      } if (second instanceof Concatenation) {
        Concatenation concat = (Concatenation)second;
        List<Expression<?, ?>> concatExprs = new ArrayList<>(concat.expressions());
        concatExprs.add(0, first);
        put(ctx, new Concatenation(context, concatExprs));
        optimised = true;
      }
    }
    if (!optimised) {
      put(ctx, new Concatenation(context, expressions.stream()
                                                     .map(e -> (Expression<?, String>)get(e))
                                                     .collect(toList())));
    }
  }


  @Override
  public void exitCastExpr(CastExprContext ctx) {
    put(ctx, new Cast(context, get(ctx.expr()), value(ctx.type())));
  }

  @Override
  public void exitSimpleCastExpr(SimpleCastExprContext ctx) {
    put(ctx, new Cast(context, get(ctx.simpleExpr()), value(ctx.type())));
  }

  @Override
  public void exitNegationExpr(NegationExprContext ctx) {
    put(ctx, new Negation(context, get(ctx.expr())));
  }

  @Override
  public void exitSimpleNegationExpr(SimpleNegationExprContext ctx) {
    put(ctx, new Negation(context, get(ctx.simpleExpr())));
  }

  @Override
  public void exitExponentiationExpr(ExponentiationExprContext ctx) {
    put(ctx, new Exponentiation(context, get(ctx.left), get(ctx.right)));
  }

  @Override
  public void exitSimpleExponentiationExpr(SimpleExponentiationExprContext ctx) {
    put(ctx, new Exponentiation(context, get(ctx.left), get(ctx.right)));
  }

  @Override
  public void exitMultiplicationExpr(MultiplicationExprContext ctx) {
    createBinaryExpr(ctx, ctx.left, ctx.right, ctx.op.getText().charAt(0));
  }

  @Override
  public void exitSimpleMultiplicationExpr(SimpleMultiplicationExprContext ctx) {
    createBinaryExpr(ctx, ctx.left, ctx.right, ctx.op.getText().charAt(0));
  }

  @Override
  public void exitAdditionExpr(AdditionExprContext ctx) {
    createBinaryExpr(ctx, ctx.left, ctx.right, ctx.op.getText().charAt(0));
  }

  @Override
  public void exitSimpleAdditionExpr(SimpleAdditionExprContext ctx) {
    createBinaryExpr(ctx, ctx.left, ctx.right, ctx.op.getText().charAt(0));
  }

  private void createBinaryExpr(ParserRuleContext ctx,
                                ParserRuleContext left,
                                ParserRuleContext right,
                                char op) {
    switch (op) {
      case '*' -> put(ctx, new Multiplication(context, get(left), get(right)));
      case '/' -> put(ctx, new Division(context, get(left), get(right)));
      case '%' -> put(ctx, new Modulus(context, get(left), get(right)));
      case '+' -> put(ctx, new Addition(context, get(left), get(right)));
      case '-' -> put(ctx, new Subtraction(context, get(left), get(right)));
      default  -> throw new TranslationException("Unknown operator: " + op);
    }
  }

  @Override
  public void exitColumnExpr(ColumnExprContext ctx) {
    ColumnReferenceContext ref = ctx.columnReference();
    String qualifier = null;
    if (ref.qualifier() != null) {
      qualifier = ref.qualifier().Identifier().getText();
    }
    put(ctx, new ColumnRef(context, qualifier, value(ref.alias())));
  }

  @Override
  public void exitSimpleColumnExpr(SimpleColumnExprContext ctx) {
    ColumnReferenceContext ref = ctx.columnReference();
    String qualifier = null;
    if (ref.qualifier() != null) {
      qualifier = ref.qualifier().Identifier().getText();
    }
    put(ctx, new ColumnRef(context, qualifier, value(ref.alias())));
  }

  @Override
  public void exitSelectExpr(SelectExprContext ctx) {
    put(ctx, get(ctx.selectExpression()));
  }

  @Override
  public void exitSimpleSelectExpr(SimpleSelectExprContext ctx) {
    put(ctx, get(ctx.selectExpression()));
  }

  @Override
  public void exitSelectExpression(SelectExpressionContext ctx) {
    DistinctContext distinct = ctx.distinct();
    Select s = new Select(context,
                          null,
                          distinct != null && distinct.getText().startsWith("distinct"),
                          distinct != null && distinct.expressionList() != null ? value(distinct.expressionList()) : null,
                          true,
                          singletonList(new Column(context, value(ctx.alias()), get(ctx.col), null)),
                          get(ctx.tableExpr()),
                          ctx.where == null ? null : get(ctx.where),
                          null,
                          null,
                          ctx.orderByList() == null ? null : value(ctx.orderByList()),
                          ctx.offset == null ? null : get(ctx.offset),
                          null);
    put(ctx, new SelectExpression(context, s));
  }

  @Override
  public void exitNotExpr(NotExprContext ctx) {
    put(ctx, new Not(context, get(ctx.expr())));
  }

  @Override
  public void exitFunctionInvocation(FunctionInvocationContext ctx) {
    createFunctionInvocation(ctx, ctx.distinct(), ctx.window(),
                             ctx.qualifiedName(), ctx.expressionList(),
                             ctx.star);
  }

  @Override
  public void exitSimpleFunctionInvocation(SimpleFunctionInvocationContext ctx) {
    createFunctionInvocation(ctx, ctx.distinct(), ctx.window(),
                             ctx.qualifiedName(), ctx.expressionList(),
                             ctx.star);
  }

  public void createFunctionInvocation(ParserRuleContext ctx,
                                       DistinctContext distinct,
                                       WindowContext window,
                                       ParserRuleContext qualifiedName,
                                       ParserRuleContext expressionList,
                                       Token star) {
    put(ctx,
        new FunctionCall(context,
                         value(qualifiedName),
                         distinct != null && distinct.getText().startsWith("distinct"),
                         distinct != null && distinct.expressionList() != null ? value(distinct.expressionList()) : null,
                         value(expressionList),
                         star != null,
                         window != null ? value(window.partition() != null ? window.partition().expressionList() : null) : null,
                         window != null ? value(window.orderByList()) : null));
  }

  @Override
  public void exitExpressionList(ExpressionListContext ctx) {
    put(ctx, new Esql<>(context, ctx.expr().stream()
                                    .map(e -> (Expression<?, ?>)get(e))
                                    .toList()));
  }

  @Override
  public void exitComparison(ComparisonContext ctx) {
    String compare = ctx.compare().getText();
    switch (compare) {
      case "="  -> put(ctx, new Equality(context, get(ctx.left), get(ctx.right)));
      case "!=" -> put(ctx, new Inequality(context, get(ctx.left), get(ctx.right)));
      case "<"  -> put(ctx, new LessThan(context, get(ctx.left), get(ctx.right)));
      case "<=" -> put(ctx, new LessThanOrEqual(context, get(ctx.left), get(ctx.right)));
      case ">"  -> put(ctx, new GreaterThan(context, get(ctx.left), get(ctx.right)));
      case ">=" -> put(ctx, new GreaterThanOrEqual(context, get(ctx.left), get(ctx.right)));
      default   -> throw new TranslationException("Unknown comparison operator: " + compare);
    }
   }

  @Override
  public void exitQuantifiedComparison(QuantifiedComparisonContext ctx) {
    put(ctx, new QuantifiedComparison(context,
                                      get(ctx.expr()),
                                      ctx.compare().getText(),
                                      ctx.Quantifier().getText(),
                                      get(ctx.select())));
   }

  @Override
  public void exitRangeExpr(RangeExprContext ctx) {
    put(ctx, new Range(context,
                       get(ctx.left),
                       ctx.leftCompare.getText(),
                       get(ctx.mid),
                       ctx.rightCompare.getText(),
                       get(ctx.right)));
   }

  @Override
  public void exitInExpression(InExpressionContext ctx) {
    put(ctx, new In(context,
                    get(ctx.expr()),
                    ctx.Not() != null,
                    value(ctx.expressionList())));
   }

  @Override
  public void exitBetweenExpr(BetweenExprContext ctx) {
    put(ctx, new Between(context,
                         ctx.Not() != null,
                         get(ctx.left),
                         get(ctx.mid),
                         get(ctx.right)));
  }

  @Override
  public void exitLogicalAndExpr(LogicalAndExprContext ctx) {
    put(ctx, new And(context, get(ctx.left), get(ctx.right)));
  }

  @Override
  public void exitLogicalOrExpr(LogicalOrExprContext ctx) {
    put(ctx, new Or(context, get(ctx.left), get(ctx.right)));
  }

  @Override
  public void exitLikeExpr(LikeExprContext ctx) {
    put(ctx, new Like(context,
                      ctx.Not() != null,
                      get(ctx.left),
                      get(ctx.right)));
   }

  @Override
  public void exitILikeExpr(ILikeExprContext ctx) {
    put(ctx, new ILike(context,
                       ctx.Not() != null,
                       get(ctx.left),
                       get(ctx.right)));
  }

  @Override
  public void exitNullCheckExpr(NullCheckExprContext ctx) {
    put(ctx, new IsNull(context, ctx.Not() != null, get(ctx.expr())));
  }

  @Override
  public void exitCaseExpr(CaseExprContext ctx) {
    createCase(ctx, ctx.expr());
  }

  @Override
  public void exitSimpleCaseExpr(SimpleCaseExprContext ctx) {
    createCase(ctx, ctx.simpleExpr());
  }

  private void createCase(ParserRuleContext ctx,
                          List<? extends ParserRuleContext> expressions) {
    boolean optimised = false;
    if (expressions.size() == 3) {
      /*
       * Multi-select case statements are broken in 3-parts
       * corresponding to (expr -> expr : expr), associating to
       * the right (starting at the end of the whole case expression).
       * If the last expr is a case expression, we can optimise
       * the whole case statement by combining it into a single one.
       *
       * Thus (e1 -> e2 : case) where case is (e3 -> e4 : e5)
       * is combined into (e1 -> e2 : e3 -> e4 : e5)
       */
      Esql<?, ?> last = get(expressions.get(2));
      if (last instanceof Case) {
        Case lastCase = (Case)last;
        List<Expression<?, ?>> caseExprs = new ArrayList<>();
        caseExprs.add(get(expressions.get(0)));
        caseExprs.add(get(expressions.get(1)));
        caseExprs.addAll(lastCase.expressions());
        put(ctx, new Case(context, caseExprs));
        optimised = true;
      }
    }
    if (!optimised) {
      put(ctx, new Case(context, expressions.stream()
                                            .map(e -> (Expression<?, String>)get(e))
                                            .collect(toList())));
    }
  }

  // create table
  /////////////////////////////////////////////////////////

  @Override
  public void exitCreateTable(CreateTableContext ctx) {
    List<TableDefinition> tableDefinitions = value(ctx.tableDefinitions());
    List<ColumnDefinition> columns =
        tableDefinitions.stream()
                        .filter(t -> t instanceof ColumnDefinition)
                        .map(t -> (ColumnDefinition)t)
                        .collect(toList());

    List<ConstraintDefinition> constraints =
        tableDefinitions.stream()
                        .filter(t -> t instanceof ConstraintDefinition)
                        .map(t -> (ConstraintDefinition)t).toList();

    String tableName = value(ctx.qualifiedName());
    List<ConstraintDefinition> withAddedTable = new ArrayList<>();
    for (ConstraintDefinition c: constraints) {
      withAddedTable.add(c.table(tableName));
    }

    List<Metadata> metadata =
        tableDefinitions.stream()
                        .filter(t -> t instanceof Metadata)
                        .map(t -> (Metadata)t).toList();

    List<Attribute> attributes = new ArrayList<>();
    for (Metadata m: metadata) {
      attributes.addAll(m.attributes().values());
    }
    put(ctx.parent, new CreateTable(context,
                                    tableName,
                                    ctx.dropUndefined() != null,
                                    columns,
                                    withAddedTable,
                                    new Metadata(context, attributes)));
  }

  @Override
  public void exitTableDefinitions(TableDefinitionsContext ctx) {
    put(ctx, new Esql<>(context, ctx.tableDefinition().stream()
                                    .map(t -> (TableDefinition)get(
                                                t.columnDefinition()        != null ? t.columnDefinition() :
                                                t.derivedColumnDefinition() != null ? t.derivedColumnDefinition() :
                                                t.constraintDefinition()    != null ? t.constraintDefinition()
                                                                                    : t.metadata()))
                                    .toList()));
  }

  @Override
  public void exitColumnDefinition(ColumnDefinitionContext ctx) {
    put(ctx, new ColumnDefinition(
        context,
        ctx.Identifier().getText(),
        value(ctx.type()),
        ctx.Not() != null,
        get(ctx.expr()),
        get(ctx.metadata())));
  }

  @Override
  public void exitDerivedColumnDefinition(DerivedColumnDefinitionContext ctx) {
    put(ctx, new DerivedColumnDefinition(
        context,
        ctx.Identifier().getText(),
        get(ctx.expr()),
        get(ctx.metadata())));
  }

  // drop table
  //////////////////////////////////////////////////

  @Override
  public void exitDropTable(DropTableContext ctx) {
    put(ctx.parent, new DropTable(context, value(ctx.qualifiedName())));
  }

  // alter table
  ///////////////////////////////////////////////////////////////

  @Override
  public void exitAlterTable(AlterTableContext ctx) {
    String tableName = value(ctx.qualifiedName());
    List<Alteration> alterations = value(ctx.alterations());
    List<Alteration> withAddedTable = new ArrayList<>();
    for (Alteration alteration: alterations) {
      if (alteration instanceof AddTableDefinition def
       && def.definition() instanceof ConstraintDefinition constraint) {
        withAddedTable.add(new AddTableDefinition(context, constraint.table(tableName)));
      } else {
        withAddedTable.add(alteration);
      }
    }
    put(ctx.alterations(), new Esql<>(context, withAddedTable));
    put(ctx.parent, new AlterTable(context, tableName, withAddedTable));
  }

  @Override
  public void exitAlterations(AlterationsContext ctx) {
    put(ctx, new Esql<>(context, ctx.alteration().stream()
                                    .map(a -> (Alteration)get(a))
                                    .toList()));
  }

  @Override
  public void exitRenameTable(RenameTableContext ctx) {
    put(ctx, new RenameTable(context, ctx.Identifier().getText()));
  }

  @Override
  public void exitAddTableDefinition(AddTableDefinitionContext ctx) {
    TableDefinitionContext tableDef = ctx.tableDefinition();
    if (tableDef.columnDefinition() != null) {
      ColumnDefinitionContext childCtx = tableDef.columnDefinition();
      put(ctx, new AddTableDefinition(context, get(childCtx)));

    } else if (tableDef.derivedColumnDefinition() != null) {
      DerivedColumnDefinitionContext childCtx = tableDef.derivedColumnDefinition();
      put(ctx, new AddTableDefinition(context, get(childCtx)));

    } else if (tableDef.constraintDefinition() != null) {
      ConstraintDefinitionContext childCtx = tableDef.constraintDefinition();
      put(ctx, new AddTableDefinition(context, get(childCtx)));

    } else if (tableDef.metadata() != null) {
      MetadataContext childCtx = tableDef.metadata();
      put(ctx, new AddTableDefinition(context, get(childCtx)));
    }
  }

  @Override
  public void exitAlterColumnDefinition(AlterColumnDefinitionContext ctx) {
    put(ctx, new AlterColumnDefinition(
                  context,
                  ctx.Identifier() == null ? null : ctx.Identifier().getText(),
                  ctx.type() == null ? null : value(ctx.type()),
                  ctx.alterNull() != null && ctx.alterNull().getText().contains("not"),
                  ctx.alterNull() != null && !ctx.alterNull().getText().contains("not"),
                  ctx.alterDefault() != null && ctx.alterDefault().expr() != null
                  ? get(ctx.alterDefault().expr()) : null,
                  ctx.alterDefault() != null && ctx.alterDefault().expr() == null,
                  ctx.metadata() == null ? null : get(ctx.metadata())));
  }

  @Override
  public void exitDropColumn(DropColumnContext ctx) {
    put(ctx, new DropColumn(context, ctx.Identifier().getText()));
  }

  @Override
  public void exitDropConstraint(DropConstraintContext ctx) {
    put(ctx, new DropConstraint(context, ctx.Identifier().getText()));
  }

  @Override
  public void exitDropTableMetadata(DropTableMetadataContext ctx) {
    put(ctx, new DropMetadata(context));
  }

  // types
  ///////////////////////////////////////

  @Override
  public void exitBase(BaseContext ctx) {
    String baseType = ctx.Identifier().getText();
    Type type = Types.findTypeOf(baseType);
    if (type == null) {
      error(ctx, "Unknown base type " + baseType);
    }
    put(ctx, new Esql<>(context, type));
  }

  @Override
  public void exitArray(ArrayContext ctx) {
    String componentType = ctx.type().getText();
    Type type = Types.findTypeOf(componentType);
    if (type == null) {
      error(ctx, "Unknown component type " + componentType + " for array");
    }
    put(ctx, new Esql<>(context, type.array()));
  }

  // Constraints
  ///////////////////////////////////////////

  @Override
  public void exitUniqueConstraint(UniqueConstraintContext ctx) {
    put(ctx, new UniqueConstraint(context, value(ctx.constraintName()), "unknown_table", value(ctx.names())));
  }

  @Override
  public void exitPrimaryKeyConstraint(PrimaryKeyConstraintContext ctx) {
    put(ctx, new PrimaryKeyConstraint(context, value(ctx.constraintName()), "unknown_table", value(ctx.names())));
  }

  @Override
  public void exitForeignKeyConstraint(ForeignKeyConstraintContext ctx) {
    List<OnUpdateContext> onUpdates = ctx.onUpdate();
    List<OnDeleteContext> onDeletes = ctx.onDelete();
    if (onUpdates != null && onUpdates.size() > 1) {
      throw new TranslationException("on update specified more than once in a constraint");
    }
    if (onDeletes != null && onDeletes.size() > 1) {
      throw new TranslationException("on delete specified more than once in a constraint");
    }

    int forwardCost = 1;
    int reverseCost = 2;
    if (ctx.forwardcost != null) {
      forwardCost = Integer.parseInt(ctx.forwardcost.getText());
      reverseCost = ctx.reversecost != null
                    ? Integer.parseInt(ctx.reversecost.getText())
                    : forwardCost * 2;
    }

    put(ctx, new ForeignKeyConstraint(
        context,
        value(ctx.constraintName()),
        "unknown_table",
        value(ctx.from),
        value(ctx.qualifiedName()),
        value(ctx.to),
        forwardCost,
        reverseCost,
        value(onUpdates != null && !onUpdates.isEmpty() ? onUpdates.get(0) : null),
        value(onDeletes != null && !onDeletes.isEmpty() ? onDeletes.get(0) : null)));
  }

  @Override
  public void exitOnUpdate(OnUpdateContext ctx) {
    String text = ctx.foreignKeyAction().getText();
    ForeignKeyChangeAction action = text.equals("restrict") ? RESTRICT :
                                    text.equals("cascade") ? CASCADE :
                                    text.contains("null") ? SET_NULL :
                                    SET_DEFAULT;
    put(ctx, new Esql<>(context, action));
  }

  @Override
  public void exitOnDelete(OnDeleteContext ctx) {
    String text = ctx.foreignKeyAction().getText();
    ForeignKeyChangeAction action = text.equals("restrict") ? RESTRICT :
                                    text.equals("cascade") ? CASCADE :
                                    text.contains("null") ? SET_NULL :
                                    SET_DEFAULT;
    put(ctx, new Esql<>(context, action));
  }

  @Override
  public void exitCheckConstraint(CheckConstraintContext ctx) {
    put(ctx, new CheckConstraint(context, value(ctx.constraintName()), "unknown_table", get(ctx.expr())));
  }

  @Override
  public void exitConstraintName(ConstraintNameContext ctx) {
    put(ctx, new Esql<>(context, ctx.Identifier().getText()));
  }


  // names, parameters and metadata
  /////////////////////////////////////////////////////////

  @Override
  public void exitEscapedAliasPart(EscapedAliasPartContext ctx) {
    String text = ctx.EscapedIdentifier().getText();
    put(ctx, new Esql<>(context, text.substring(1, text.length() - 1)));
  }

  @Override
  public void exitNormalAliasPart(NormalAliasPartContext ctx) {
    put(ctx, new Esql<>(context, ctx.qualifiedName().getText()));
  }

  @Override
  public void exitAlias(AliasContext ctx) {
    StringBuilder alias = new StringBuilder();
    if (ctx.root != null) {
      alias.append('/');
    }
    Iterator<AliasPartContext> i = ctx.aliasPart().iterator();
    alias.append(this.<String>value(i.next()));
    while (i.hasNext()) {
      alias.append('/').append(this.<String>value(i.next()));
    }
    put(ctx, new Esql<>(context, alias.toString()));
  }

  @Override
  public void exitQualifiedName(QualifiedNameContext ctx) {
    put(ctx, new Esql<>(context, ctx.getText()));
  }

  @Override
  public void exitNames(NamesContext ctx) {
    /*
     * map list of names (identifiers) to a list of string
     */
    put(ctx, new Esql<>(context, ctx.Identifier().stream()
                                    .map(TerminalNode::getText)
                                    .toList()));
  }

  @Override
  public void exitNamedParameter(NamedParameterContext ctx) {
    put(ctx, new NamedParameter(context, ctx.Identifier().getText()));
  }

  @Override
  public void exitNamedArgument(NamedArgumentContext ctx) {
    put(ctx, new NamedArgument(context, ctx.Identifier().getText(), get(ctx.expr())));
  }

  @Override
  public void exitMetadata(MetadataContext ctx) {
    put(ctx, new Metadata(context, value(ctx.attributeList())));
  }

  @Override
  public void exitAttributeList(AttributeListContext ctx) {
    put(ctx, new Esql<>(context, ctx.attribute().stream()
                                    .map(e -> (Attribute)get(e))
                                    .toList()));
  }

  @Override
  public void exitAttribute(AttributeContext ctx) {
    if (ctx.Identifier() != null) {
      put(ctx, new Attribute(context, ctx.Identifier().getText(), get(ctx.expr())));
    }
  }

  // internal state methods
  ////////////////////////////////////////////////////////////////

  /**
   * Returns the Esql object assigned previously to the context
   * if any. Otherwise returns null.
   */
  private <T extends Esql<?, ?>> T get(RuleContext ctx) {
    return ctx == null ? null : (T)nodes.get(ctx);
  }

  /**
   * Returns the object inside the Esql object assigned previously
   * to the context if any. Otherwise returns null.
   */
  private <T> T value(RuleContext ctx) {
    Esql<T, ?> esql = get(ctx);
    return esql == null ? null : esql.value;
  }

  private void put(RuleContext ctx, Esql<?, ?> esql) {
    nodes.put(ctx, esql);
  }

  public final IdentityHashMap<RuleContext, Esql<?, ?>> nodes = new IdentityHashMap<>();

  private final Context context;

  public Esql<?, ?> lastRecognised;

  public static final System.Logger log = System.getLogger(SyntaxAnalyser.class.getName());
}