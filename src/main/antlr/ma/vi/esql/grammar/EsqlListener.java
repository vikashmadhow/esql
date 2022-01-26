// Generated from ma\vi\esql\grammar\Esql.g4 by ANTLR 4.9.3

package ma.vi.esql.grammar;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link EsqlParser}.
 */
public interface EsqlListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link EsqlParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(EsqlParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(EsqlParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#expressions}.
	 * @param ctx the parse tree
	 */
	void enterExpressions(EsqlParser.ExpressionsContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#expressions}.
	 * @param ctx the parse tree
	 */
	void exitExpressions(EsqlParser.ExpressionsContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#noop}.
	 * @param ctx the parse tree
	 */
	void enterNoop(EsqlParser.NoopContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#noop}.
	 * @param ctx the parse tree
	 */
	void exitNoop(EsqlParser.NoopContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#modify}.
	 * @param ctx the parse tree
	 */
	void enterModify(EsqlParser.ModifyContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#modify}.
	 * @param ctx the parse tree
	 */
	void exitModify(EsqlParser.ModifyContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#queryUpdate}.
	 * @param ctx the parse tree
	 */
	void enterQueryUpdate(EsqlParser.QueryUpdateContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#queryUpdate}.
	 * @param ctx the parse tree
	 */
	void exitQueryUpdate(EsqlParser.QueryUpdateContext ctx);
	/**
	 * Enter a parse tree produced by the {@code CompositeSelection}
	 * labeled alternative in {@link EsqlParser#select}.
	 * @param ctx the parse tree
	 */
	void enterCompositeSelection(EsqlParser.CompositeSelectionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code CompositeSelection}
	 * labeled alternative in {@link EsqlParser#select}.
	 * @param ctx the parse tree
	 */
	void exitCompositeSelection(EsqlParser.CompositeSelectionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code WithSelection}
	 * labeled alternative in {@link EsqlParser#select}.
	 * @param ctx the parse tree
	 */
	void enterWithSelection(EsqlParser.WithSelectionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code WithSelection}
	 * labeled alternative in {@link EsqlParser#select}.
	 * @param ctx the parse tree
	 */
	void exitWithSelection(EsqlParser.WithSelectionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code BaseSelection}
	 * labeled alternative in {@link EsqlParser#select}.
	 * @param ctx the parse tree
	 */
	void enterBaseSelection(EsqlParser.BaseSelectionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code BaseSelection}
	 * labeled alternative in {@link EsqlParser#select}.
	 * @param ctx the parse tree
	 */
	void exitBaseSelection(EsqlParser.BaseSelectionContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#metadata}.
	 * @param ctx the parse tree
	 */
	void enterMetadata(EsqlParser.MetadataContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#metadata}.
	 * @param ctx the parse tree
	 */
	void exitMetadata(EsqlParser.MetadataContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#attributeList}.
	 * @param ctx the parse tree
	 */
	void enterAttributeList(EsqlParser.AttributeListContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#attributeList}.
	 * @param ctx the parse tree
	 */
	void exitAttributeList(EsqlParser.AttributeListContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#attribute}.
	 * @param ctx the parse tree
	 */
	void enterAttribute(EsqlParser.AttributeContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#attribute}.
	 * @param ctx the parse tree
	 */
	void exitAttribute(EsqlParser.AttributeContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#distinct}.
	 * @param ctx the parse tree
	 */
	void enterDistinct(EsqlParser.DistinctContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#distinct}.
	 * @param ctx the parse tree
	 */
	void exitDistinct(EsqlParser.DistinctContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#explicit}.
	 * @param ctx the parse tree
	 */
	void enterExplicit(EsqlParser.ExplicitContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#explicit}.
	 * @param ctx the parse tree
	 */
	void exitExplicit(EsqlParser.ExplicitContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#columns}.
	 * @param ctx the parse tree
	 */
	void enterColumns(EsqlParser.ColumnsContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#columns}.
	 * @param ctx the parse tree
	 */
	void exitColumns(EsqlParser.ColumnsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SingleColumn}
	 * labeled alternative in {@link EsqlParser#column}.
	 * @param ctx the parse tree
	 */
	void enterSingleColumn(EsqlParser.SingleColumnContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SingleColumn}
	 * labeled alternative in {@link EsqlParser#column}.
	 * @param ctx the parse tree
	 */
	void exitSingleColumn(EsqlParser.SingleColumnContext ctx);
	/**
	 * Enter a parse tree produced by the {@code StarColumn}
	 * labeled alternative in {@link EsqlParser#column}.
	 * @param ctx the parse tree
	 */
	void enterStarColumn(EsqlParser.StarColumnContext ctx);
	/**
	 * Exit a parse tree produced by the {@code StarColumn}
	 * labeled alternative in {@link EsqlParser#column}.
	 * @param ctx the parse tree
	 */
	void exitStarColumn(EsqlParser.StarColumnContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#qualifier}.
	 * @param ctx the parse tree
	 */
	void enterQualifier(EsqlParser.QualifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#qualifier}.
	 * @param ctx the parse tree
	 */
	void exitQualifier(EsqlParser.QualifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#alias}.
	 * @param ctx the parse tree
	 */
	void enterAlias(EsqlParser.AliasContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#alias}.
	 * @param ctx the parse tree
	 */
	void exitAlias(EsqlParser.AliasContext ctx);
	/**
	 * Enter a parse tree produced by the {@code EscapedAliasPart}
	 * labeled alternative in {@link EsqlParser#aliasPart}.
	 * @param ctx the parse tree
	 */
	void enterEscapedAliasPart(EsqlParser.EscapedAliasPartContext ctx);
	/**
	 * Exit a parse tree produced by the {@code EscapedAliasPart}
	 * labeled alternative in {@link EsqlParser#aliasPart}.
	 * @param ctx the parse tree
	 */
	void exitEscapedAliasPart(EsqlParser.EscapedAliasPartContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NormalAliasPart}
	 * labeled alternative in {@link EsqlParser#aliasPart}.
	 * @param ctx the parse tree
	 */
	void enterNormalAliasPart(EsqlParser.NormalAliasPartContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NormalAliasPart}
	 * labeled alternative in {@link EsqlParser#aliasPart}.
	 * @param ctx the parse tree
	 */
	void exitNormalAliasPart(EsqlParser.NormalAliasPartContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#qualifiedName}.
	 * @param ctx the parse tree
	 */
	void enterQualifiedName(EsqlParser.QualifiedNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#qualifiedName}.
	 * @param ctx the parse tree
	 */
	void exitQualifiedName(EsqlParser.QualifiedNameContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SingleTableExpr}
	 * labeled alternative in {@link EsqlParser#tableExpr}.
	 * @param ctx the parse tree
	 */
	void enterSingleTableExpr(EsqlParser.SingleTableExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SingleTableExpr}
	 * labeled alternative in {@link EsqlParser#tableExpr}.
	 * @param ctx the parse tree
	 */
	void exitSingleTableExpr(EsqlParser.SingleTableExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code DynamicTableExpr}
	 * labeled alternative in {@link EsqlParser#tableExpr}.
	 * @param ctx the parse tree
	 */
	void enterDynamicTableExpr(EsqlParser.DynamicTableExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code DynamicTableExpr}
	 * labeled alternative in {@link EsqlParser#tableExpr}.
	 * @param ctx the parse tree
	 */
	void exitDynamicTableExpr(EsqlParser.DynamicTableExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code CrossProductTableExpr}
	 * labeled alternative in {@link EsqlParser#tableExpr}.
	 * @param ctx the parse tree
	 */
	void enterCrossProductTableExpr(EsqlParser.CrossProductTableExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code CrossProductTableExpr}
	 * labeled alternative in {@link EsqlParser#tableExpr}.
	 * @param ctx the parse tree
	 */
	void exitCrossProductTableExpr(EsqlParser.CrossProductTableExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SelectTableExpr}
	 * labeled alternative in {@link EsqlParser#tableExpr}.
	 * @param ctx the parse tree
	 */
	void enterSelectTableExpr(EsqlParser.SelectTableExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SelectTableExpr}
	 * labeled alternative in {@link EsqlParser#tableExpr}.
	 * @param ctx the parse tree
	 */
	void exitSelectTableExpr(EsqlParser.SelectTableExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JoinTableExpr}
	 * labeled alternative in {@link EsqlParser#tableExpr}.
	 * @param ctx the parse tree
	 */
	void enterJoinTableExpr(EsqlParser.JoinTableExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JoinTableExpr}
	 * labeled alternative in {@link EsqlParser#tableExpr}.
	 * @param ctx the parse tree
	 */
	void exitJoinTableExpr(EsqlParser.JoinTableExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#dynamicColumns}.
	 * @param ctx the parse tree
	 */
	void enterDynamicColumns(EsqlParser.DynamicColumnsContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#dynamicColumns}.
	 * @param ctx the parse tree
	 */
	void exitDynamicColumns(EsqlParser.DynamicColumnsContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#nameWithMetadata}.
	 * @param ctx the parse tree
	 */
	void enterNameWithMetadata(EsqlParser.NameWithMetadataContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#nameWithMetadata}.
	 * @param ctx the parse tree
	 */
	void exitNameWithMetadata(EsqlParser.NameWithMetadataContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#lateral}.
	 * @param ctx the parse tree
	 */
	void enterLateral(EsqlParser.LateralContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#lateral}.
	 * @param ctx the parse tree
	 */
	void exitLateral(EsqlParser.LateralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SimpleGroup}
	 * labeled alternative in {@link EsqlParser#groupByList}.
	 * @param ctx the parse tree
	 */
	void enterSimpleGroup(EsqlParser.SimpleGroupContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SimpleGroup}
	 * labeled alternative in {@link EsqlParser#groupByList}.
	 * @param ctx the parse tree
	 */
	void exitSimpleGroup(EsqlParser.SimpleGroupContext ctx);
	/**
	 * Enter a parse tree produced by the {@code RollupGroup}
	 * labeled alternative in {@link EsqlParser#groupByList}.
	 * @param ctx the parse tree
	 */
	void enterRollupGroup(EsqlParser.RollupGroupContext ctx);
	/**
	 * Exit a parse tree produced by the {@code RollupGroup}
	 * labeled alternative in {@link EsqlParser#groupByList}.
	 * @param ctx the parse tree
	 */
	void exitRollupGroup(EsqlParser.RollupGroupContext ctx);
	/**
	 * Enter a parse tree produced by the {@code CubeGroup}
	 * labeled alternative in {@link EsqlParser#groupByList}.
	 * @param ctx the parse tree
	 */
	void enterCubeGroup(EsqlParser.CubeGroupContext ctx);
	/**
	 * Exit a parse tree produced by the {@code CubeGroup}
	 * labeled alternative in {@link EsqlParser#groupByList}.
	 * @param ctx the parse tree
	 */
	void exitCubeGroup(EsqlParser.CubeGroupContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#orderByList}.
	 * @param ctx the parse tree
	 */
	void enterOrderByList(EsqlParser.OrderByListContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#orderByList}.
	 * @param ctx the parse tree
	 */
	void exitOrderByList(EsqlParser.OrderByListContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#orderBy}.
	 * @param ctx the parse tree
	 */
	void enterOrderBy(EsqlParser.OrderByContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#orderBy}.
	 * @param ctx the parse tree
	 */
	void exitOrderBy(EsqlParser.OrderByContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#direction}.
	 * @param ctx the parse tree
	 */
	void enterDirection(EsqlParser.DirectionContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#direction}.
	 * @param ctx the parse tree
	 */
	void exitDirection(EsqlParser.DirectionContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#setop}.
	 * @param ctx the parse tree
	 */
	void enterSetop(EsqlParser.SetopContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#setop}.
	 * @param ctx the parse tree
	 */
	void exitSetop(EsqlParser.SetopContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#with}.
	 * @param ctx the parse tree
	 */
	void enterWith(EsqlParser.WithContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#with}.
	 * @param ctx the parse tree
	 */
	void exitWith(EsqlParser.WithContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#cteList}.
	 * @param ctx the parse tree
	 */
	void enterCteList(EsqlParser.CteListContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#cteList}.
	 * @param ctx the parse tree
	 */
	void exitCteList(EsqlParser.CteListContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#cte}.
	 * @param ctx the parse tree
	 */
	void enterCte(EsqlParser.CteContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#cte}.
	 * @param ctx the parse tree
	 */
	void exitCte(EsqlParser.CteContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#names}.
	 * @param ctx the parse tree
	 */
	void enterNames(EsqlParser.NamesContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#names}.
	 * @param ctx the parse tree
	 */
	void exitNames(EsqlParser.NamesContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#insert}.
	 * @param ctx the parse tree
	 */
	void enterInsert(EsqlParser.InsertContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#insert}.
	 * @param ctx the parse tree
	 */
	void exitInsert(EsqlParser.InsertContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#rows}.
	 * @param ctx the parse tree
	 */
	void enterRows(EsqlParser.RowsContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#rows}.
	 * @param ctx the parse tree
	 */
	void exitRows(EsqlParser.RowsContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#row}.
	 * @param ctx the parse tree
	 */
	void enterRow(EsqlParser.RowContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#row}.
	 * @param ctx the parse tree
	 */
	void exitRow(EsqlParser.RowContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#defaultValues}.
	 * @param ctx the parse tree
	 */
	void enterDefaultValues(EsqlParser.DefaultValuesContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#defaultValues}.
	 * @param ctx the parse tree
	 */
	void exitDefaultValues(EsqlParser.DefaultValuesContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#update}.
	 * @param ctx the parse tree
	 */
	void enterUpdate(EsqlParser.UpdateContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#update}.
	 * @param ctx the parse tree
	 */
	void exitUpdate(EsqlParser.UpdateContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#setList}.
	 * @param ctx the parse tree
	 */
	void enterSetList(EsqlParser.SetListContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#setList}.
	 * @param ctx the parse tree
	 */
	void exitSetList(EsqlParser.SetListContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#set}.
	 * @param ctx the parse tree
	 */
	void enterSet(EsqlParser.SetContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#set}.
	 * @param ctx the parse tree
	 */
	void exitSet(EsqlParser.SetContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#delete}.
	 * @param ctx the parse tree
	 */
	void enterDelete(EsqlParser.DeleteContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#delete}.
	 * @param ctx the parse tree
	 */
	void exitDelete(EsqlParser.DeleteContext ctx);
	/**
	 * Enter a parse tree produced by the {@code LikeExpr}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterLikeExpr(EsqlParser.LikeExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code LikeExpr}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitLikeExpr(EsqlParser.LikeExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code QuantifiedComparison}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterQuantifiedComparison(EsqlParser.QuantifiedComparisonContext ctx);
	/**
	 * Exit a parse tree produced by the {@code QuantifiedComparison}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitQuantifiedComparison(EsqlParser.QuantifiedComparisonContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ModifyStatement}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterModifyStatement(EsqlParser.ModifyStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ModifyStatement}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitModifyStatement(EsqlParser.ModifyStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code BetweenExpr}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterBetweenExpr(EsqlParser.BetweenExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code BetweenExpr}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitBetweenExpr(EsqlParser.BetweenExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ILikeExpr}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterILikeExpr(EsqlParser.ILikeExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ILikeExpr}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitILikeExpr(EsqlParser.ILikeExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code InExpression}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterInExpression(EsqlParser.InExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code InExpression}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitInExpression(EsqlParser.InExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code LogicalAndExpr}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterLogicalAndExpr(EsqlParser.LogicalAndExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code LogicalAndExpr}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitLogicalAndExpr(EsqlParser.LogicalAndExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SimpleExpression}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterSimpleExpression(EsqlParser.SimpleExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SimpleExpression}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitSimpleExpression(EsqlParser.SimpleExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Assignment}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterAssignment(EsqlParser.AssignmentContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Assignment}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitAssignment(EsqlParser.AssignmentContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SelectStatement}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterSelectStatement(EsqlParser.SelectStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SelectStatement}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitSelectStatement(EsqlParser.SelectStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code DefaultValue}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterDefaultValue(EsqlParser.DefaultValueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code DefaultValue}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitDefaultValue(EsqlParser.DefaultValueContext ctx);
	/**
	 * Enter a parse tree produced by the {@code DefineStatement}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterDefineStatement(EsqlParser.DefineStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code DefineStatement}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitDefineStatement(EsqlParser.DefineStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code VarDecl}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterVarDecl(EsqlParser.VarDeclContext ctx);
	/**
	 * Exit a parse tree produced by the {@code VarDecl}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitVarDecl(EsqlParser.VarDeclContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Comparison}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterComparison(EsqlParser.ComparisonContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Comparison}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitComparison(EsqlParser.ComparisonContext ctx);
	/**
	 * Enter a parse tree produced by the {@code GroupingExpr}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterGroupingExpr(EsqlParser.GroupingExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code GroupingExpr}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitGroupingExpr(EsqlParser.GroupingExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code CastExpr}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterCastExpr(EsqlParser.CastExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code CastExpr}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitCastExpr(EsqlParser.CastExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code LiteralExpr}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterLiteralExpr(EsqlParser.LiteralExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code LiteralExpr}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitLiteralExpr(EsqlParser.LiteralExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NotExpr}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterNotExpr(EsqlParser.NotExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NotExpr}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitNotExpr(EsqlParser.NotExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code MultiplicationExpr}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterMultiplicationExpr(EsqlParser.MultiplicationExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code MultiplicationExpr}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitMultiplicationExpr(EsqlParser.MultiplicationExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code UncomputedExpr}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterUncomputedExpr(EsqlParser.UncomputedExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code UncomputedExpr}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitUncomputedExpr(EsqlParser.UncomputedExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ConcatenationExpr}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterConcatenationExpr(EsqlParser.ConcatenationExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ConcatenationExpr}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitConcatenationExpr(EsqlParser.ConcatenationExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FunctionInvocation}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterFunctionInvocation(EsqlParser.FunctionInvocationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FunctionInvocation}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitFunctionInvocation(EsqlParser.FunctionInvocationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Return}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterReturn(EsqlParser.ReturnContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Return}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitReturn(EsqlParser.ReturnContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SelectExpr}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterSelectExpr(EsqlParser.SelectExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SelectExpr}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitSelectExpr(EsqlParser.SelectExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NamedParameter}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterNamedParameter(EsqlParser.NamedParameterContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NamedParameter}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitNamedParameter(EsqlParser.NamedParameterContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ExponentiationExpr}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExponentiationExpr(EsqlParser.ExponentiationExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExponentiationExpr}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExponentiationExpr(EsqlParser.ExponentiationExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NoopStatement}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterNoopStatement(EsqlParser.NoopStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NoopStatement}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitNoopStatement(EsqlParser.NoopStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code RangeExpr}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterRangeExpr(EsqlParser.RangeExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code RangeExpr}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitRangeExpr(EsqlParser.RangeExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FunctionDecl}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterFunctionDecl(EsqlParser.FunctionDeclContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FunctionDecl}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitFunctionDecl(EsqlParser.FunctionDeclContext ctx);
	/**
	 * Enter a parse tree produced by the {@code LogicalOrExpr}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterLogicalOrExpr(EsqlParser.LogicalOrExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code LogicalOrExpr}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitLogicalOrExpr(EsqlParser.LogicalOrExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AdditionExpr}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterAdditionExpr(EsqlParser.AdditionExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AdditionExpr}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitAdditionExpr(EsqlParser.AdditionExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NullCheckExpr}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterNullCheckExpr(EsqlParser.NullCheckExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NullCheckExpr}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitNullCheckExpr(EsqlParser.NullCheckExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ColumnRef}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterColumnRef(EsqlParser.ColumnRefContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ColumnRef}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitColumnRef(EsqlParser.ColumnRefContext ctx);
	/**
	 * Enter a parse tree produced by the {@code CaseExpr}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterCaseExpr(EsqlParser.CaseExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code CaseExpr}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitCaseExpr(EsqlParser.CaseExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NegationExpr}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterNegationExpr(EsqlParser.NegationExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NegationExpr}
	 * labeled alternative in {@link EsqlParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitNegationExpr(EsqlParser.NegationExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SimpleConcatenationExpr}
	 * labeled alternative in {@link EsqlParser#simpleExpr}.
	 * @param ctx the parse tree
	 */
	void enterSimpleConcatenationExpr(EsqlParser.SimpleConcatenationExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SimpleConcatenationExpr}
	 * labeled alternative in {@link EsqlParser#simpleExpr}.
	 * @param ctx the parse tree
	 */
	void exitSimpleConcatenationExpr(EsqlParser.SimpleConcatenationExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SimpleFunctionInvocation}
	 * labeled alternative in {@link EsqlParser#simpleExpr}.
	 * @param ctx the parse tree
	 */
	void enterSimpleFunctionInvocation(EsqlParser.SimpleFunctionInvocationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SimpleFunctionInvocation}
	 * labeled alternative in {@link EsqlParser#simpleExpr}.
	 * @param ctx the parse tree
	 */
	void exitSimpleFunctionInvocation(EsqlParser.SimpleFunctionInvocationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SimpleAdditionExpr}
	 * labeled alternative in {@link EsqlParser#simpleExpr}.
	 * @param ctx the parse tree
	 */
	void enterSimpleAdditionExpr(EsqlParser.SimpleAdditionExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SimpleAdditionExpr}
	 * labeled alternative in {@link EsqlParser#simpleExpr}.
	 * @param ctx the parse tree
	 */
	void exitSimpleAdditionExpr(EsqlParser.SimpleAdditionExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SimpleNegationExpr}
	 * labeled alternative in {@link EsqlParser#simpleExpr}.
	 * @param ctx the parse tree
	 */
	void enterSimpleNegationExpr(EsqlParser.SimpleNegationExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SimpleNegationExpr}
	 * labeled alternative in {@link EsqlParser#simpleExpr}.
	 * @param ctx the parse tree
	 */
	void exitSimpleNegationExpr(EsqlParser.SimpleNegationExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SimpleMultiplicationExpr}
	 * labeled alternative in {@link EsqlParser#simpleExpr}.
	 * @param ctx the parse tree
	 */
	void enterSimpleMultiplicationExpr(EsqlParser.SimpleMultiplicationExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SimpleMultiplicationExpr}
	 * labeled alternative in {@link EsqlParser#simpleExpr}.
	 * @param ctx the parse tree
	 */
	void exitSimpleMultiplicationExpr(EsqlParser.SimpleMultiplicationExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SimpleLiteralExpr}
	 * labeled alternative in {@link EsqlParser#simpleExpr}.
	 * @param ctx the parse tree
	 */
	void enterSimpleLiteralExpr(EsqlParser.SimpleLiteralExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SimpleLiteralExpr}
	 * labeled alternative in {@link EsqlParser#simpleExpr}.
	 * @param ctx the parse tree
	 */
	void exitSimpleLiteralExpr(EsqlParser.SimpleLiteralExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SimpleGroupingExpr}
	 * labeled alternative in {@link EsqlParser#simpleExpr}.
	 * @param ctx the parse tree
	 */
	void enterSimpleGroupingExpr(EsqlParser.SimpleGroupingExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SimpleGroupingExpr}
	 * labeled alternative in {@link EsqlParser#simpleExpr}.
	 * @param ctx the parse tree
	 */
	void exitSimpleGroupingExpr(EsqlParser.SimpleGroupingExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SimpleCastExpr}
	 * labeled alternative in {@link EsqlParser#simpleExpr}.
	 * @param ctx the parse tree
	 */
	void enterSimpleCastExpr(EsqlParser.SimpleCastExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SimpleCastExpr}
	 * labeled alternative in {@link EsqlParser#simpleExpr}.
	 * @param ctx the parse tree
	 */
	void exitSimpleCastExpr(EsqlParser.SimpleCastExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SimpleExponentiationExpr}
	 * labeled alternative in {@link EsqlParser#simpleExpr}.
	 * @param ctx the parse tree
	 */
	void enterSimpleExponentiationExpr(EsqlParser.SimpleExponentiationExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SimpleExponentiationExpr}
	 * labeled alternative in {@link EsqlParser#simpleExpr}.
	 * @param ctx the parse tree
	 */
	void exitSimpleExponentiationExpr(EsqlParser.SimpleExponentiationExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SimpleCaseExpr}
	 * labeled alternative in {@link EsqlParser#simpleExpr}.
	 * @param ctx the parse tree
	 */
	void enterSimpleCaseExpr(EsqlParser.SimpleCaseExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SimpleCaseExpr}
	 * labeled alternative in {@link EsqlParser#simpleExpr}.
	 * @param ctx the parse tree
	 */
	void exitSimpleCaseExpr(EsqlParser.SimpleCaseExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SimpleSelectExpr}
	 * labeled alternative in {@link EsqlParser#simpleExpr}.
	 * @param ctx the parse tree
	 */
	void enterSimpleSelectExpr(EsqlParser.SimpleSelectExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SimpleSelectExpr}
	 * labeled alternative in {@link EsqlParser#simpleExpr}.
	 * @param ctx the parse tree
	 */
	void exitSimpleSelectExpr(EsqlParser.SimpleSelectExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SimpleColumnExpr}
	 * labeled alternative in {@link EsqlParser#simpleExpr}.
	 * @param ctx the parse tree
	 */
	void enterSimpleColumnExpr(EsqlParser.SimpleColumnExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SimpleColumnExpr}
	 * labeled alternative in {@link EsqlParser#simpleExpr}.
	 * @param ctx the parse tree
	 */
	void exitSimpleColumnExpr(EsqlParser.SimpleColumnExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#parameter}.
	 * @param ctx the parse tree
	 */
	void enterParameter(EsqlParser.ParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#parameter}.
	 * @param ctx the parse tree
	 */
	void exitParameter(EsqlParser.ParameterContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#parameters}.
	 * @param ctx the parse tree
	 */
	void enterParameters(EsqlParser.ParametersContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#parameters}.
	 * @param ctx the parse tree
	 */
	void exitParameters(EsqlParser.ParametersContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#columnReference}.
	 * @param ctx the parse tree
	 */
	void enterColumnReference(EsqlParser.ColumnReferenceContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#columnReference}.
	 * @param ctx the parse tree
	 */
	void exitColumnReference(EsqlParser.ColumnReferenceContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#selectExpression}.
	 * @param ctx the parse tree
	 */
	void enterSelectExpression(EsqlParser.SelectExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#selectExpression}.
	 * @param ctx the parse tree
	 */
	void exitSelectExpression(EsqlParser.SelectExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#window}.
	 * @param ctx the parse tree
	 */
	void enterWindow(EsqlParser.WindowContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#window}.
	 * @param ctx the parse tree
	 */
	void exitWindow(EsqlParser.WindowContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#partition}.
	 * @param ctx the parse tree
	 */
	void enterPartition(EsqlParser.PartitionContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#partition}.
	 * @param ctx the parse tree
	 */
	void exitPartition(EsqlParser.PartitionContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#frame}.
	 * @param ctx the parse tree
	 */
	void enterFrame(EsqlParser.FrameContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#frame}.
	 * @param ctx the parse tree
	 */
	void exitFrame(EsqlParser.FrameContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#preceding}.
	 * @param ctx the parse tree
	 */
	void enterPreceding(EsqlParser.PrecedingContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#preceding}.
	 * @param ctx the parse tree
	 */
	void exitPreceding(EsqlParser.PrecedingContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#following}.
	 * @param ctx the parse tree
	 */
	void enterFollowing(EsqlParser.FollowingContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#following}.
	 * @param ctx the parse tree
	 */
	void exitFollowing(EsqlParser.FollowingContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#unbounded}.
	 * @param ctx the parse tree
	 */
	void enterUnbounded(EsqlParser.UnboundedContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#unbounded}.
	 * @param ctx the parse tree
	 */
	void exitUnbounded(EsqlParser.UnboundedContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#current}.
	 * @param ctx the parse tree
	 */
	void enterCurrent(EsqlParser.CurrentContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#current}.
	 * @param ctx the parse tree
	 */
	void exitCurrent(EsqlParser.CurrentContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#compare}.
	 * @param ctx the parse tree
	 */
	void enterCompare(EsqlParser.CompareContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#compare}.
	 * @param ctx the parse tree
	 */
	void exitCompare(EsqlParser.CompareContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#ordering}.
	 * @param ctx the parse tree
	 */
	void enterOrdering(EsqlParser.OrderingContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#ordering}.
	 * @param ctx the parse tree
	 */
	void exitOrdering(EsqlParser.OrderingContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#expressionList}.
	 * @param ctx the parse tree
	 */
	void enterExpressionList(EsqlParser.ExpressionListContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#expressionList}.
	 * @param ctx the parse tree
	 */
	void exitExpressionList(EsqlParser.ExpressionListContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#arguments}.
	 * @param ctx the parse tree
	 */
	void enterArguments(EsqlParser.ArgumentsContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#arguments}.
	 * @param ctx the parse tree
	 */
	void exitArguments(EsqlParser.ArgumentsContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#argument}.
	 * @param ctx the parse tree
	 */
	void enterArgument(EsqlParser.ArgumentContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#argument}.
	 * @param ctx the parse tree
	 */
	void exitArgument(EsqlParser.ArgumentContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#namedArgument}.
	 * @param ctx the parse tree
	 */
	void enterNamedArgument(EsqlParser.NamedArgumentContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#namedArgument}.
	 * @param ctx the parse tree
	 */
	void exitNamedArgument(EsqlParser.NamedArgumentContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#positionalArgument}.
	 * @param ctx the parse tree
	 */
	void enterPositionalArgument(EsqlParser.PositionalArgumentContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#positionalArgument}.
	 * @param ctx the parse tree
	 */
	void exitPositionalArgument(EsqlParser.PositionalArgumentContext ctx);
	/**
	 * Enter a parse tree produced by the {@code BasicLiterals}
	 * labeled alternative in {@link EsqlParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterBasicLiterals(EsqlParser.BasicLiteralsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code BasicLiterals}
	 * labeled alternative in {@link EsqlParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitBasicLiterals(EsqlParser.BasicLiteralsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Null}
	 * labeled alternative in {@link EsqlParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterNull(EsqlParser.NullContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Null}
	 * labeled alternative in {@link EsqlParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitNull(EsqlParser.NullContext ctx);
	/**
	 * Enter a parse tree produced by the {@code BaseArrayLiteral}
	 * labeled alternative in {@link EsqlParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterBaseArrayLiteral(EsqlParser.BaseArrayLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code BaseArrayLiteral}
	 * labeled alternative in {@link EsqlParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitBaseArrayLiteral(EsqlParser.BaseArrayLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JsonArrayLiteral}
	 * labeled alternative in {@link EsqlParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterJsonArrayLiteral(EsqlParser.JsonArrayLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JsonArrayLiteral}
	 * labeled alternative in {@link EsqlParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitJsonArrayLiteral(EsqlParser.JsonArrayLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JsonObjectLiteral}
	 * labeled alternative in {@link EsqlParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterJsonObjectLiteral(EsqlParser.JsonObjectLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JsonObjectLiteral}
	 * labeled alternative in {@link EsqlParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitJsonObjectLiteral(EsqlParser.JsonObjectLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Integer}
	 * labeled alternative in {@link EsqlParser#baseLiteral}.
	 * @param ctx the parse tree
	 */
	void enterInteger(EsqlParser.IntegerContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Integer}
	 * labeled alternative in {@link EsqlParser#baseLiteral}.
	 * @param ctx the parse tree
	 */
	void exitInteger(EsqlParser.IntegerContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FloatingPoint}
	 * labeled alternative in {@link EsqlParser#baseLiteral}.
	 * @param ctx the parse tree
	 */
	void enterFloatingPoint(EsqlParser.FloatingPointContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FloatingPoint}
	 * labeled alternative in {@link EsqlParser#baseLiteral}.
	 * @param ctx the parse tree
	 */
	void exitFloatingPoint(EsqlParser.FloatingPointContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Boolean}
	 * labeled alternative in {@link EsqlParser#baseLiteral}.
	 * @param ctx the parse tree
	 */
	void enterBoolean(EsqlParser.BooleanContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Boolean}
	 * labeled alternative in {@link EsqlParser#baseLiteral}.
	 * @param ctx the parse tree
	 */
	void exitBoolean(EsqlParser.BooleanContext ctx);
	/**
	 * Enter a parse tree produced by the {@code MultiLineString}
	 * labeled alternative in {@link EsqlParser#baseLiteral}.
	 * @param ctx the parse tree
	 */
	void enterMultiLineString(EsqlParser.MultiLineStringContext ctx);
	/**
	 * Exit a parse tree produced by the {@code MultiLineString}
	 * labeled alternative in {@link EsqlParser#baseLiteral}.
	 * @param ctx the parse tree
	 */
	void exitMultiLineString(EsqlParser.MultiLineStringContext ctx);
	/**
	 * Enter a parse tree produced by the {@code String}
	 * labeled alternative in {@link EsqlParser#baseLiteral}.
	 * @param ctx the parse tree
	 */
	void enterString(EsqlParser.StringContext ctx);
	/**
	 * Exit a parse tree produced by the {@code String}
	 * labeled alternative in {@link EsqlParser#baseLiteral}.
	 * @param ctx the parse tree
	 */
	void exitString(EsqlParser.StringContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Uuid}
	 * labeled alternative in {@link EsqlParser#baseLiteral}.
	 * @param ctx the parse tree
	 */
	void enterUuid(EsqlParser.UuidContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Uuid}
	 * labeled alternative in {@link EsqlParser#baseLiteral}.
	 * @param ctx the parse tree
	 */
	void exitUuid(EsqlParser.UuidContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Date}
	 * labeled alternative in {@link EsqlParser#baseLiteral}.
	 * @param ctx the parse tree
	 */
	void enterDate(EsqlParser.DateContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Date}
	 * labeled alternative in {@link EsqlParser#baseLiteral}.
	 * @param ctx the parse tree
	 */
	void exitDate(EsqlParser.DateContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Interval}
	 * labeled alternative in {@link EsqlParser#baseLiteral}.
	 * @param ctx the parse tree
	 */
	void enterInterval(EsqlParser.IntervalContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Interval}
	 * labeled alternative in {@link EsqlParser#baseLiteral}.
	 * @param ctx the parse tree
	 */
	void exitInterval(EsqlParser.IntervalContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#literalList}.
	 * @param ctx the parse tree
	 */
	void enterLiteralList(EsqlParser.LiteralListContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#literalList}.
	 * @param ctx the parse tree
	 */
	void exitLiteralList(EsqlParser.LiteralListContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#baseLiteralList}.
	 * @param ctx the parse tree
	 */
	void enterBaseLiteralList(EsqlParser.BaseLiteralListContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#baseLiteralList}.
	 * @param ctx the parse tree
	 */
	void exitBaseLiteralList(EsqlParser.BaseLiteralListContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#define}.
	 * @param ctx the parse tree
	 */
	void enterDefine(EsqlParser.DefineContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#define}.
	 * @param ctx the parse tree
	 */
	void exitDefine(EsqlParser.DefineContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#createTable}.
	 * @param ctx the parse tree
	 */
	void enterCreateTable(EsqlParser.CreateTableContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#createTable}.
	 * @param ctx the parse tree
	 */
	void exitCreateTable(EsqlParser.CreateTableContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#dropUndefined}.
	 * @param ctx the parse tree
	 */
	void enterDropUndefined(EsqlParser.DropUndefinedContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#dropUndefined}.
	 * @param ctx the parse tree
	 */
	void exitDropUndefined(EsqlParser.DropUndefinedContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#tableDefinitions}.
	 * @param ctx the parse tree
	 */
	void enterTableDefinitions(EsqlParser.TableDefinitionsContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#tableDefinitions}.
	 * @param ctx the parse tree
	 */
	void exitTableDefinitions(EsqlParser.TableDefinitionsContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#tableDefinition}.
	 * @param ctx the parse tree
	 */
	void enterTableDefinition(EsqlParser.TableDefinitionContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#tableDefinition}.
	 * @param ctx the parse tree
	 */
	void exitTableDefinition(EsqlParser.TableDefinitionContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#columnDefinition}.
	 * @param ctx the parse tree
	 */
	void enterColumnDefinition(EsqlParser.ColumnDefinitionContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#columnDefinition}.
	 * @param ctx the parse tree
	 */
	void exitColumnDefinition(EsqlParser.ColumnDefinitionContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#derivedColumnDefinition}.
	 * @param ctx the parse tree
	 */
	void enterDerivedColumnDefinition(EsqlParser.DerivedColumnDefinitionContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#derivedColumnDefinition}.
	 * @param ctx the parse tree
	 */
	void exitDerivedColumnDefinition(EsqlParser.DerivedColumnDefinitionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code UniqueConstraint}
	 * labeled alternative in {@link EsqlParser#constraintDefinition}.
	 * @param ctx the parse tree
	 */
	void enterUniqueConstraint(EsqlParser.UniqueConstraintContext ctx);
	/**
	 * Exit a parse tree produced by the {@code UniqueConstraint}
	 * labeled alternative in {@link EsqlParser#constraintDefinition}.
	 * @param ctx the parse tree
	 */
	void exitUniqueConstraint(EsqlParser.UniqueConstraintContext ctx);
	/**
	 * Enter a parse tree produced by the {@code PrimaryKeyConstraint}
	 * labeled alternative in {@link EsqlParser#constraintDefinition}.
	 * @param ctx the parse tree
	 */
	void enterPrimaryKeyConstraint(EsqlParser.PrimaryKeyConstraintContext ctx);
	/**
	 * Exit a parse tree produced by the {@code PrimaryKeyConstraint}
	 * labeled alternative in {@link EsqlParser#constraintDefinition}.
	 * @param ctx the parse tree
	 */
	void exitPrimaryKeyConstraint(EsqlParser.PrimaryKeyConstraintContext ctx);
	/**
	 * Enter a parse tree produced by the {@code CheckConstraint}
	 * labeled alternative in {@link EsqlParser#constraintDefinition}.
	 * @param ctx the parse tree
	 */
	void enterCheckConstraint(EsqlParser.CheckConstraintContext ctx);
	/**
	 * Exit a parse tree produced by the {@code CheckConstraint}
	 * labeled alternative in {@link EsqlParser#constraintDefinition}.
	 * @param ctx the parse tree
	 */
	void exitCheckConstraint(EsqlParser.CheckConstraintContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ForeignKeyConstraint}
	 * labeled alternative in {@link EsqlParser#constraintDefinition}.
	 * @param ctx the parse tree
	 */
	void enterForeignKeyConstraint(EsqlParser.ForeignKeyConstraintContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ForeignKeyConstraint}
	 * labeled alternative in {@link EsqlParser#constraintDefinition}.
	 * @param ctx the parse tree
	 */
	void exitForeignKeyConstraint(EsqlParser.ForeignKeyConstraintContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#constraintName}.
	 * @param ctx the parse tree
	 */
	void enterConstraintName(EsqlParser.ConstraintNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#constraintName}.
	 * @param ctx the parse tree
	 */
	void exitConstraintName(EsqlParser.ConstraintNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#onUpdate}.
	 * @param ctx the parse tree
	 */
	void enterOnUpdate(EsqlParser.OnUpdateContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#onUpdate}.
	 * @param ctx the parse tree
	 */
	void exitOnUpdate(EsqlParser.OnUpdateContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#onDelete}.
	 * @param ctx the parse tree
	 */
	void enterOnDelete(EsqlParser.OnDeleteContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#onDelete}.
	 * @param ctx the parse tree
	 */
	void exitOnDelete(EsqlParser.OnDeleteContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#foreignKeyAction}.
	 * @param ctx the parse tree
	 */
	void enterForeignKeyAction(EsqlParser.ForeignKeyActionContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#foreignKeyAction}.
	 * @param ctx the parse tree
	 */
	void exitForeignKeyAction(EsqlParser.ForeignKeyActionContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#alterTable}.
	 * @param ctx the parse tree
	 */
	void enterAlterTable(EsqlParser.AlterTableContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#alterTable}.
	 * @param ctx the parse tree
	 */
	void exitAlterTable(EsqlParser.AlterTableContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#alterations}.
	 * @param ctx the parse tree
	 */
	void enterAlterations(EsqlParser.AlterationsContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#alterations}.
	 * @param ctx the parse tree
	 */
	void exitAlterations(EsqlParser.AlterationsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code RenameTable}
	 * labeled alternative in {@link EsqlParser#alteration}.
	 * @param ctx the parse tree
	 */
	void enterRenameTable(EsqlParser.RenameTableContext ctx);
	/**
	 * Exit a parse tree produced by the {@code RenameTable}
	 * labeled alternative in {@link EsqlParser#alteration}.
	 * @param ctx the parse tree
	 */
	void exitRenameTable(EsqlParser.RenameTableContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AddTableDefinition}
	 * labeled alternative in {@link EsqlParser#alteration}.
	 * @param ctx the parse tree
	 */
	void enterAddTableDefinition(EsqlParser.AddTableDefinitionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AddTableDefinition}
	 * labeled alternative in {@link EsqlParser#alteration}.
	 * @param ctx the parse tree
	 */
	void exitAddTableDefinition(EsqlParser.AddTableDefinitionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AlterColumn}
	 * labeled alternative in {@link EsqlParser#alteration}.
	 * @param ctx the parse tree
	 */
	void enterAlterColumn(EsqlParser.AlterColumnContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AlterColumn}
	 * labeled alternative in {@link EsqlParser#alteration}.
	 * @param ctx the parse tree
	 */
	void exitAlterColumn(EsqlParser.AlterColumnContext ctx);
	/**
	 * Enter a parse tree produced by the {@code DropColumn}
	 * labeled alternative in {@link EsqlParser#alteration}.
	 * @param ctx the parse tree
	 */
	void enterDropColumn(EsqlParser.DropColumnContext ctx);
	/**
	 * Exit a parse tree produced by the {@code DropColumn}
	 * labeled alternative in {@link EsqlParser#alteration}.
	 * @param ctx the parse tree
	 */
	void exitDropColumn(EsqlParser.DropColumnContext ctx);
	/**
	 * Enter a parse tree produced by the {@code DropConstraint}
	 * labeled alternative in {@link EsqlParser#alteration}.
	 * @param ctx the parse tree
	 */
	void enterDropConstraint(EsqlParser.DropConstraintContext ctx);
	/**
	 * Exit a parse tree produced by the {@code DropConstraint}
	 * labeled alternative in {@link EsqlParser#alteration}.
	 * @param ctx the parse tree
	 */
	void exitDropConstraint(EsqlParser.DropConstraintContext ctx);
	/**
	 * Enter a parse tree produced by the {@code DropTableMetadata}
	 * labeled alternative in {@link EsqlParser#alteration}.
	 * @param ctx the parse tree
	 */
	void enterDropTableMetadata(EsqlParser.DropTableMetadataContext ctx);
	/**
	 * Exit a parse tree produced by the {@code DropTableMetadata}
	 * labeled alternative in {@link EsqlParser#alteration}.
	 * @param ctx the parse tree
	 */
	void exitDropTableMetadata(EsqlParser.DropTableMetadataContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#alterColumnDefinition}.
	 * @param ctx the parse tree
	 */
	void enterAlterColumnDefinition(EsqlParser.AlterColumnDefinitionContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#alterColumnDefinition}.
	 * @param ctx the parse tree
	 */
	void exitAlterColumnDefinition(EsqlParser.AlterColumnDefinitionContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#alterNull}.
	 * @param ctx the parse tree
	 */
	void enterAlterNull(EsqlParser.AlterNullContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#alterNull}.
	 * @param ctx the parse tree
	 */
	void exitAlterNull(EsqlParser.AlterNullContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#alterDefault}.
	 * @param ctx the parse tree
	 */
	void enterAlterDefault(EsqlParser.AlterDefaultContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#alterDefault}.
	 * @param ctx the parse tree
	 */
	void exitAlterDefault(EsqlParser.AlterDefaultContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsqlParser#dropTable}.
	 * @param ctx the parse tree
	 */
	void enterDropTable(EsqlParser.DropTableContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsqlParser#dropTable}.
	 * @param ctx the parse tree
	 */
	void exitDropTable(EsqlParser.DropTableContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Array}
	 * labeled alternative in {@link EsqlParser#type}.
	 * @param ctx the parse tree
	 */
	void enterArray(EsqlParser.ArrayContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Array}
	 * labeled alternative in {@link EsqlParser#type}.
	 * @param ctx the parse tree
	 */
	void exitArray(EsqlParser.ArrayContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Base}
	 * labeled alternative in {@link EsqlParser#type}.
	 * @param ctx the parse tree
	 */
	void enterBase(EsqlParser.BaseContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Base}
	 * labeled alternative in {@link EsqlParser#type}.
	 * @param ctx the parse tree
	 */
	void exitBase(EsqlParser.BaseContext ctx);
}