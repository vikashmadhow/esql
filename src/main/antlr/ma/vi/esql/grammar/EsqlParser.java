// Generated from ma\vi\esql\grammar\Esql.g4 by ANTLR 4.9

    package ma.vi.esql.grammar;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class EsqlParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.9", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, T__20=21, T__21=22, T__22=23, T__23=24, 
		T__24=25, T__25=26, T__26=27, T__27=28, T__28=29, T__29=30, T__30=31, 
		T__31=32, T__32=33, T__33=34, T__34=35, T__35=36, T__36=37, T__37=38, 
		T__38=39, T__39=40, T__40=41, T__41=42, T__42=43, T__43=44, T__44=45, 
		T__45=46, T__46=47, T__47=48, T__48=49, T__49=50, T__50=51, T__51=52, 
		T__52=53, T__53=54, T__54=55, T__55=56, T__56=57, T__57=58, T__58=59, 
		T__59=60, T__60=61, T__61=62, T__62=63, T__63=64, T__64=65, T__65=66, 
		T__66=67, T__67=68, T__68=69, T__69=70, T__70=71, T__71=72, T__72=73, 
		T__73=74, T__74=75, T__75=76, T__76=77, T__77=78, T__78=79, T__79=80, 
		T__80=81, T__81=82, T__82=83, T__83=84, T__84=85, T__85=86, T__86=87, 
		T__87=88, T__88=89, T__89=90, T__90=91, T__91=92, T__92=93, T__93=94, 
		EscapedIdentifier=95, Quantifier=96, BaseType=97, Not=98, IntegerLiteral=99, 
		FloatingPointLiteral=100, BooleanLiteral=101, MultiLineStringLiteral=102, 
		StringLiteral=103, UuidLiteral=104, DateLiteral=105, IntervalLiteral=106, 
		NullLiteral=107, Identifier=108, Comment=109, Whitespace=110;
	public static final int
		RULE_program = 0, RULE_noop = 1, RULE_statement = 2, RULE_modify = 3, 
		RULE_queryUpdate = 4, RULE_select = 5, RULE_metadata = 6, RULE_attributeList = 7, 
		RULE_attribute = 8, RULE_distinct = 9, RULE_explicit = 10, RULE_columns = 11, 
		RULE_column = 12, RULE_alias = 13, RULE_aliasPart = 14, RULE_qualifiedName = 15, 
		RULE_tableExpr = 16, RULE_dynamicColumns = 17, RULE_nameWithMetadata = 18, 
		RULE_joinType = 19, RULE_groupByList = 20, RULE_orderByList = 21, RULE_orderBy = 22, 
		RULE_direction = 23, RULE_setop = 24, RULE_with = 25, RULE_cteList = 26, 
		RULE_cte = 27, RULE_names = 28, RULE_insert = 29, RULE_rows = 30, RULE_row = 31, 
		RULE_defaultValues = 32, RULE_update = 33, RULE_setList = 34, RULE_set = 35, 
		RULE_delete = 36, RULE_expr = 37, RULE_simpleExpr = 38, RULE_selectExpression = 39, 
		RULE_window = 40, RULE_partition = 41, RULE_compare = 42, RULE_ordering = 43, 
		RULE_expressionList = 44, RULE_literal = 45, RULE_baseLiteral = 46, RULE_literalList = 47, 
		RULE_baseLiteralList = 48, RULE_columnReference = 49, RULE_qualifier = 50, 
		RULE_define = 51, RULE_createTable = 52, RULE_dropUndefined = 53, RULE_alterTable = 54, 
		RULE_alterTableActions = 55, RULE_alterTableAction = 56, RULE_dropTable = 57, 
		RULE_tableDefinitions = 58, RULE_tableDefinition = 59, RULE_columnDefinition = 60, 
		RULE_alterColumnDefinition = 61, RULE_alterNull = 62, RULE_alterDefault = 63, 
		RULE_derivedColumnDefinition = 64, RULE_constraintDefinition = 65, RULE_constraintName = 66, 
		RULE_onUpdate = 67, RULE_onDelete = 68, RULE_foreignKeyAction = 69, RULE_type = 70, 
		RULE_arrayType = 71;
	private static String[] makeRuleNames() {
		return new String[] {
			"program", "noop", "statement", "modify", "queryUpdate", "select", "metadata", 
			"attributeList", "attribute", "distinct", "explicit", "columns", "column", 
			"alias", "aliasPart", "qualifiedName", "tableExpr", "dynamicColumns", 
			"nameWithMetadata", "joinType", "groupByList", "orderByList", "orderBy", 
			"direction", "setop", "with", "cteList", "cte", "names", "insert", "rows", 
			"row", "defaultValues", "update", "setList", "set", "delete", "expr", 
			"simpleExpr", "selectExpression", "window", "partition", "compare", "ordering", 
			"expressionList", "literal", "baseLiteral", "literalList", "baseLiteralList", 
			"columnReference", "qualifier", "define", "createTable", "dropUndefined", 
			"alterTable", "alterTableActions", "alterTableAction", "dropTable", "tableDefinitions", 
			"tableDefinition", "columnDefinition", "alterColumnDefinition", "alterNull", 
			"alterDefault", "derivedColumnDefinition", "constraintDefinition", "constraintName", 
			"onUpdate", "onDelete", "foreignKeyAction", "type", "arrayType"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "';'", "'select'", "','", "'from'", "'where'", "'order'", "'by'", 
			"'group'", "'having'", "'offset'", "'limit'", "'{'", "'}'", "':'", "'all'", 
			"'distinct'", "'on'", "'('", "')'", "'explicit'", "'*'", "'/'", "'.'", 
			"'times'", "'join'", "'left'", "'right'", "'full'", "'rollup'", "'cube'", 
			"'asc'", "'desc'", "'union'", "'intersect'", "'except'", "'with'", "'recursive'", 
			"'insert'", "'into'", "'values'", "'return'", "'default'", "'update'", 
			"'set'", "'='", "'delete'", "'$('", "'<'", "'>'", "'?'", "'||'", "'-'", 
			"'^'", "'%'", "'+'", "':='", "'exists'", "'in'", "'between'", "'and'", 
			"'like'", "'ilike'", "'is'", "'or'", "'if'", "'else'", "'over'", "'partition'", 
			"'!='", "'<='", "'>='", "'['", "']'", "'create'", "'table'", "'drop'", 
			"'undefined'", "'alter'", "'rename'", "'to'", "'add'", "'column'", "'constraint'", 
			"'metadata'", "'no'", "'unique'", "'primary'", "'key'", "'foreign'", 
			"'references'", "'cost'", "'check'", "'restrict'", "'cascade'", null, 
			null, null, "'not'", null, null, null, null, null, null, null, null, 
			"'null'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, "EscapedIdentifier", 
			"Quantifier", "BaseType", "Not", "IntegerLiteral", "FloatingPointLiteral", 
			"BooleanLiteral", "MultiLineStringLiteral", "StringLiteral", "UuidLiteral", 
			"DateLiteral", "IntervalLiteral", "NullLiteral", "Identifier", "Comment", 
			"Whitespace"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "Esql.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public EsqlParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class ProgramContext extends ParserRuleContext {
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public ProgramContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_program; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterProgram(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitProgram(this);
		}
	}

	public final ProgramContext program() throws RecognitionException {
		ProgramContext _localctx = new ProgramContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_program);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(144);
			statement();
			setState(149);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(145);
					match(T__0);
					setState(146);
					statement();
					}
					} 
				}
				setState(151);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			}
			setState(153);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__0) {
				{
				setState(152);
				match(T__0);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NoopContext extends ParserRuleContext {
		public NoopContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_noop; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterNoop(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitNoop(this);
		}
	}

	public final NoopContext noop() throws RecognitionException {
		NoopContext _localctx = new NoopContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_noop);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(155);
			match(T__0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StatementContext extends ParserRuleContext {
		public SelectContext select() {
			return getRuleContext(SelectContext.class,0);
		}
		public ModifyContext modify() {
			return getRuleContext(ModifyContext.class,0);
		}
		public DefineContext define() {
			return getRuleContext(DefineContext.class,0);
		}
		public NoopContext noop() {
			return getRuleContext(NoopContext.class,0);
		}
		public StatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitStatement(this);
		}
	}

	public final StatementContext statement() throws RecognitionException {
		StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_statement);
		try {
			setState(161);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__1:
			case T__35:
				enterOuterAlt(_localctx, 1);
				{
				setState(157);
				select(0);
				}
				break;
			case T__37:
			case T__42:
			case T__45:
				enterOuterAlt(_localctx, 2);
				{
				setState(158);
				modify();
				}
				break;
			case T__73:
			case T__75:
			case T__77:
				enterOuterAlt(_localctx, 3);
				{
				setState(159);
				define();
				}
				break;
			case T__0:
				enterOuterAlt(_localctx, 4);
				{
				setState(160);
				noop();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ModifyContext extends ParserRuleContext {
		public UpdateContext update() {
			return getRuleContext(UpdateContext.class,0);
		}
		public InsertContext insert() {
			return getRuleContext(InsertContext.class,0);
		}
		public DeleteContext delete() {
			return getRuleContext(DeleteContext.class,0);
		}
		public ModifyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_modify; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterModify(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitModify(this);
		}
	}

	public final ModifyContext modify() throws RecognitionException {
		ModifyContext _localctx = new ModifyContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_modify);
		try {
			setState(166);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__42:
				enterOuterAlt(_localctx, 1);
				{
				setState(163);
				update();
				}
				break;
			case T__37:
				enterOuterAlt(_localctx, 2);
				{
				setState(164);
				insert();
				}
				break;
			case T__45:
				enterOuterAlt(_localctx, 3);
				{
				setState(165);
				delete();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class QueryUpdateContext extends ParserRuleContext {
		public SelectContext select() {
			return getRuleContext(SelectContext.class,0);
		}
		public ModifyContext modify() {
			return getRuleContext(ModifyContext.class,0);
		}
		public QueryUpdateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_queryUpdate; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterQueryUpdate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitQueryUpdate(this);
		}
	}

	public final QueryUpdateContext queryUpdate() throws RecognitionException {
		QueryUpdateContext _localctx = new QueryUpdateContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_queryUpdate);
		try {
			setState(170);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__1:
			case T__35:
				enterOuterAlt(_localctx, 1);
				{
				setState(168);
				select(0);
				}
				break;
			case T__37:
			case T__42:
			case T__45:
				enterOuterAlt(_localctx, 2);
				{
				setState(169);
				modify();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SelectContext extends ParserRuleContext {
		public SelectContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_select; }
	 
		public SelectContext() { }
		public void copyFrom(SelectContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class CompositeSelectionContext extends SelectContext {
		public List<SelectContext> select() {
			return getRuleContexts(SelectContext.class);
		}
		public SelectContext select(int i) {
			return getRuleContext(SelectContext.class,i);
		}
		public SetopContext setop() {
			return getRuleContext(SetopContext.class,0);
		}
		public CompositeSelectionContext(SelectContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterCompositeSelection(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitCompositeSelection(this);
		}
	}
	public static class WithSelectionContext extends SelectContext {
		public WithContext with() {
			return getRuleContext(WithContext.class,0);
		}
		public WithSelectionContext(SelectContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterWithSelection(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitWithSelection(this);
		}
	}
	public static class BaseSelectionContext extends SelectContext {
		public ExprContext where;
		public ExprContext having;
		public ExprContext offset;
		public ExprContext limit;
		public ColumnsContext columns() {
			return getRuleContext(ColumnsContext.class,0);
		}
		public MetadataContext metadata() {
			return getRuleContext(MetadataContext.class,0);
		}
		public DistinctContext distinct() {
			return getRuleContext(DistinctContext.class,0);
		}
		public ExplicitContext explicit() {
			return getRuleContext(ExplicitContext.class,0);
		}
		public TableExprContext tableExpr() {
			return getRuleContext(TableExprContext.class,0);
		}
		public OrderByListContext orderByList() {
			return getRuleContext(OrderByListContext.class,0);
		}
		public GroupByListContext groupByList() {
			return getRuleContext(GroupByListContext.class,0);
		}
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public BaseSelectionContext(SelectContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterBaseSelection(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitBaseSelection(this);
		}
	}

	public final SelectContext select() throws RecognitionException {
		return select(0);
	}

	private SelectContext select(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		SelectContext _localctx = new SelectContext(_ctx, _parentState);
		SelectContext _prevctx = _localctx;
		int _startState = 10;
		enterRecursionRule(_localctx, 10, RULE_select, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(218);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__1:
				{
				_localctx = new BaseSelectionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(173);
				match(T__1);
				setState(178);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
				case 1:
					{
					setState(174);
					metadata();
					setState(176);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==T__2) {
						{
						setState(175);
						match(T__2);
						}
					}

					}
					break;
				}
				setState(181);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__14 || _la==T__15) {
					{
					setState(180);
					distinct();
					}
				}

				setState(184);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__19) {
					{
					setState(183);
					explicit();
					}
				}

				setState(186);
				columns();
				setState(189);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
				case 1:
					{
					setState(187);
					match(T__3);
					setState(188);
					tableExpr(0);
					}
					break;
				}
				setState(193);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
				case 1:
					{
					setState(191);
					match(T__4);
					setState(192);
					((BaseSelectionContext)_localctx).where = expr(0);
					}
					break;
				}
				setState(198);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
				case 1:
					{
					setState(195);
					match(T__5);
					setState(196);
					match(T__6);
					setState(197);
					orderByList();
					}
					break;
				}
				setState(203);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
				case 1:
					{
					setState(200);
					match(T__7);
					setState(201);
					match(T__6);
					setState(202);
					groupByList();
					}
					break;
				}
				setState(207);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
				case 1:
					{
					setState(205);
					match(T__8);
					setState(206);
					((BaseSelectionContext)_localctx).having = expr(0);
					}
					break;
				}
				setState(211);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
				case 1:
					{
					setState(209);
					match(T__9);
					setState(210);
					((BaseSelectionContext)_localctx).offset = expr(0);
					}
					break;
				}
				setState(215);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,15,_ctx) ) {
				case 1:
					{
					setState(213);
					match(T__10);
					setState(214);
					((BaseSelectionContext)_localctx).limit = expr(0);
					}
					break;
				}
				}
				break;
			case T__35:
				{
				_localctx = new WithSelectionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(217);
				with();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(226);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,17,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new CompositeSelectionContext(new SelectContext(_parentctx, _parentState));
					pushNewRecursionContext(_localctx, _startState, RULE_select);
					setState(220);
					if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
					setState(221);
					setop();
					setState(222);
					select(3);
					}
					} 
				}
				setState(228);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,17,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class MetadataContext extends ParserRuleContext {
		public AttributeListContext attributeList() {
			return getRuleContext(AttributeListContext.class,0);
		}
		public MetadataContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_metadata; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterMetadata(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitMetadata(this);
		}
	}

	public final MetadataContext metadata() throws RecognitionException {
		MetadataContext _localctx = new MetadataContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_metadata);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(229);
			match(T__11);
			setState(230);
			attributeList();
			setState(231);
			match(T__12);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AttributeListContext extends ParserRuleContext {
		public List<AttributeContext> attribute() {
			return getRuleContexts(AttributeContext.class);
		}
		public AttributeContext attribute(int i) {
			return getRuleContext(AttributeContext.class,i);
		}
		public AttributeListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_attributeList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterAttributeList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitAttributeList(this);
		}
	}

	public final AttributeListContext attributeList() throws RecognitionException {
		AttributeListContext _localctx = new AttributeListContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_attributeList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(233);
			attribute();
			setState(238);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__2) {
				{
				{
				setState(234);
				match(T__2);
				setState(235);
				attribute();
				}
				}
				setState(240);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AttributeContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(EsqlParser.Identifier, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public AttributeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_attribute; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterAttribute(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitAttribute(this);
		}
	}

	public final AttributeContext attribute() throws RecognitionException {
		AttributeContext _localctx = new AttributeContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_attribute);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(241);
			match(Identifier);
			setState(242);
			match(T__13);
			setState(243);
			expr(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DistinctContext extends ParserRuleContext {
		public ExpressionListContext expressionList() {
			return getRuleContext(ExpressionListContext.class,0);
		}
		public DistinctContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_distinct; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterDistinct(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitDistinct(this);
		}
	}

	public final DistinctContext distinct() throws RecognitionException {
		DistinctContext _localctx = new DistinctContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_distinct);
		int _la;
		try {
			setState(254);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__14:
				enterOuterAlt(_localctx, 1);
				{
				setState(245);
				match(T__14);
				}
				break;
			case T__15:
				enterOuterAlt(_localctx, 2);
				{
				setState(246);
				match(T__15);
				setState(252);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__16) {
					{
					setState(247);
					match(T__16);
					setState(248);
					match(T__17);
					setState(249);
					expressionList();
					setState(250);
					match(T__18);
					}
				}

				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExplicitContext extends ParserRuleContext {
		public ExplicitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_explicit; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterExplicit(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitExplicit(this);
		}
	}

	public final ExplicitContext explicit() throws RecognitionException {
		ExplicitContext _localctx = new ExplicitContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_explicit);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(256);
			match(T__19);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ColumnsContext extends ParserRuleContext {
		public List<ColumnContext> column() {
			return getRuleContexts(ColumnContext.class);
		}
		public ColumnContext column(int i) {
			return getRuleContext(ColumnContext.class,i);
		}
		public ColumnsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_columns; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterColumns(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitColumns(this);
		}
	}

	public final ColumnsContext columns() throws RecognitionException {
		ColumnsContext _localctx = new ColumnsContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_columns);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(258);
			column();
			setState(263);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,21,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(259);
					match(T__2);
					setState(260);
					column();
					}
					} 
				}
				setState(265);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,21,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ColumnContext extends ParserRuleContext {
		public ColumnContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_column; }
	 
		public ColumnContext() { }
		public void copyFrom(ColumnContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class StarColumnContext extends ColumnContext {
		public QualifierContext qualifier() {
			return getRuleContext(QualifierContext.class,0);
		}
		public StarColumnContext(ColumnContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterStarColumn(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitStarColumn(this);
		}
	}
	public static class SingleColumnContext extends ColumnContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public AliasContext alias() {
			return getRuleContext(AliasContext.class,0);
		}
		public MetadataContext metadata() {
			return getRuleContext(MetadataContext.class,0);
		}
		public SingleColumnContext(ColumnContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterSingleColumn(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitSingleColumn(this);
		}
	}

	public final ColumnContext column() throws RecognitionException {
		ColumnContext _localctx = new ColumnContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_column);
		int _la;
		try {
			setState(279);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,25,_ctx) ) {
			case 1:
				_localctx = new SingleColumnContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(269);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
				case 1:
					{
					setState(266);
					alias();
					setState(267);
					match(T__13);
					}
					break;
				}
				setState(271);
				expr(0);
				setState(273);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,23,_ctx) ) {
				case 1:
					{
					setState(272);
					metadata();
					}
					break;
				}
				}
				break;
			case 2:
				_localctx = new StarColumnContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(276);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Identifier) {
					{
					setState(275);
					qualifier();
					}
				}

				setState(278);
				match(T__20);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AliasContext extends ParserRuleContext {
		public Token root;
		public List<AliasPartContext> aliasPart() {
			return getRuleContexts(AliasPartContext.class);
		}
		public AliasPartContext aliasPart(int i) {
			return getRuleContext(AliasPartContext.class,i);
		}
		public AliasContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_alias; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterAlias(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitAlias(this);
		}
	}

	public final AliasContext alias() throws RecognitionException {
		AliasContext _localctx = new AliasContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_alias);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(282);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__21) {
				{
				setState(281);
				((AliasContext)_localctx).root = match(T__21);
				}
			}

			setState(284);
			aliasPart();
			setState(289);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,27,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(285);
					match(T__21);
					setState(286);
					aliasPart();
					}
					} 
				}
				setState(291);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,27,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AliasPartContext extends ParserRuleContext {
		public AliasPartContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_aliasPart; }
	 
		public AliasPartContext() { }
		public void copyFrom(AliasPartContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class EscapedAliasPartContext extends AliasPartContext {
		public TerminalNode EscapedIdentifier() { return getToken(EsqlParser.EscapedIdentifier, 0); }
		public EscapedAliasPartContext(AliasPartContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterEscapedAliasPart(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitEscapedAliasPart(this);
		}
	}
	public static class NormalAliasPartContext extends AliasPartContext {
		public QualifiedNameContext qualifiedName() {
			return getRuleContext(QualifiedNameContext.class,0);
		}
		public NormalAliasPartContext(AliasPartContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterNormalAliasPart(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitNormalAliasPart(this);
		}
	}

	public final AliasPartContext aliasPart() throws RecognitionException {
		AliasPartContext _localctx = new AliasPartContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_aliasPart);
		try {
			setState(294);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case EscapedIdentifier:
				_localctx = new EscapedAliasPartContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(292);
				match(EscapedIdentifier);
				}
				break;
			case Identifier:
				_localctx = new NormalAliasPartContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(293);
				qualifiedName();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class QualifiedNameContext extends ParserRuleContext {
		public List<TerminalNode> Identifier() { return getTokens(EsqlParser.Identifier); }
		public TerminalNode Identifier(int i) {
			return getToken(EsqlParser.Identifier, i);
		}
		public QualifiedNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_qualifiedName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterQualifiedName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitQualifiedName(this);
		}
	}

	public final QualifiedNameContext qualifiedName() throws RecognitionException {
		QualifiedNameContext _localctx = new QualifiedNameContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_qualifiedName);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(296);
			match(Identifier);
			setState(301);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,29,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(297);
					match(T__22);
					setState(298);
					match(Identifier);
					}
					} 
				}
				setState(303);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,29,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TableExprContext extends ParserRuleContext {
		public TableExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tableExpr; }
	 
		public TableExprContext() { }
		public void copyFrom(TableExprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class SingleTableExprContext extends TableExprContext {
		public QualifiedNameContext qualifiedName() {
			return getRuleContext(QualifiedNameContext.class,0);
		}
		public AliasContext alias() {
			return getRuleContext(AliasContext.class,0);
		}
		public SingleTableExprContext(TableExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterSingleTableExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitSingleTableExpr(this);
		}
	}
	public static class DynamicTableExprContext extends TableExprContext {
		public AliasContext alias() {
			return getRuleContext(AliasContext.class,0);
		}
		public DynamicColumnsContext dynamicColumns() {
			return getRuleContext(DynamicColumnsContext.class,0);
		}
		public RowsContext rows() {
			return getRuleContext(RowsContext.class,0);
		}
		public MetadataContext metadata() {
			return getRuleContext(MetadataContext.class,0);
		}
		public DynamicTableExprContext(TableExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterDynamicTableExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitDynamicTableExpr(this);
		}
	}
	public static class CrossProductTableExprContext extends TableExprContext {
		public TableExprContext left;
		public TableExprContext right;
		public List<TableExprContext> tableExpr() {
			return getRuleContexts(TableExprContext.class);
		}
		public TableExprContext tableExpr(int i) {
			return getRuleContext(TableExprContext.class,i);
		}
		public CrossProductTableExprContext(TableExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterCrossProductTableExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitCrossProductTableExpr(this);
		}
	}
	public static class SelectTableExprContext extends TableExprContext {
		public AliasContext alias() {
			return getRuleContext(AliasContext.class,0);
		}
		public SelectContext select() {
			return getRuleContext(SelectContext.class,0);
		}
		public SelectTableExprContext(TableExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterSelectTableExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitSelectTableExpr(this);
		}
	}
	public static class JoinTableExprContext extends TableExprContext {
		public TableExprContext left;
		public TableExprContext right;
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public List<TableExprContext> tableExpr() {
			return getRuleContexts(TableExprContext.class);
		}
		public TableExprContext tableExpr(int i) {
			return getRuleContext(TableExprContext.class,i);
		}
		public JoinTypeContext joinType() {
			return getRuleContext(JoinTypeContext.class,0);
		}
		public JoinTableExprContext(TableExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterJoinTableExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitJoinTableExpr(this);
		}
	}

	public final TableExprContext tableExpr() throws RecognitionException {
		return tableExpr(0);
	}

	private TableExprContext tableExpr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		TableExprContext _localctx = new TableExprContext(_ctx, _parentState);
		TableExprContext _prevctx = _localctx;
		int _startState = 32;
		enterRecursionRule(_localctx, 32, RULE_tableExpr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(327);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,32,_ctx) ) {
			case 1:
				{
				_localctx = new SingleTableExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(308);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,30,_ctx) ) {
				case 1:
					{
					setState(305);
					alias();
					setState(306);
					match(T__13);
					}
					break;
				}
				setState(310);
				qualifiedName();
				}
				break;
			case 2:
				{
				_localctx = new SelectTableExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(311);
				alias();
				setState(312);
				match(T__13);
				setState(313);
				match(T__17);
				setState(314);
				select(0);
				setState(315);
				match(T__18);
				}
				break;
			case 3:
				{
				_localctx = new DynamicTableExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(317);
				alias();
				setState(319);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__11) {
					{
					setState(318);
					metadata();
					}
				}

				setState(321);
				dynamicColumns();
				setState(322);
				match(T__13);
				setState(323);
				match(T__17);
				setState(324);
				rows();
				setState(325);
				match(T__18);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(343);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,35,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(341);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,34,_ctx) ) {
					case 1:
						{
						_localctx = new CrossProductTableExprContext(new TableExprContext(_parentctx, _parentState));
						((CrossProductTableExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_tableExpr);
						setState(329);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(330);
						match(T__23);
						setState(331);
						((CrossProductTableExprContext)_localctx).right = tableExpr(3);
						}
						break;
					case 2:
						{
						_localctx = new JoinTableExprContext(new TableExprContext(_parentctx, _parentState));
						((JoinTableExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_tableExpr);
						setState(332);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(334);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__25) | (1L << T__26) | (1L << T__27))) != 0)) {
							{
							setState(333);
							joinType();
							}
						}

						setState(336);
						match(T__24);
						setState(337);
						((JoinTableExprContext)_localctx).right = tableExpr(0);
						setState(338);
						match(T__16);
						setState(339);
						expr(0);
						}
						break;
					}
					} 
				}
				setState(345);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,35,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class DynamicColumnsContext extends ParserRuleContext {
		public List<NameWithMetadataContext> nameWithMetadata() {
			return getRuleContexts(NameWithMetadataContext.class);
		}
		public NameWithMetadataContext nameWithMetadata(int i) {
			return getRuleContext(NameWithMetadataContext.class,i);
		}
		public DynamicColumnsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dynamicColumns; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterDynamicColumns(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitDynamicColumns(this);
		}
	}

	public final DynamicColumnsContext dynamicColumns() throws RecognitionException {
		DynamicColumnsContext _localctx = new DynamicColumnsContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_dynamicColumns);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(346);
			match(T__17);
			setState(347);
			nameWithMetadata();
			setState(352);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__2) {
				{
				{
				setState(348);
				match(T__2);
				setState(349);
				nameWithMetadata();
				}
				}
				setState(354);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(355);
			match(T__18);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NameWithMetadataContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(EsqlParser.Identifier, 0); }
		public MetadataContext metadata() {
			return getRuleContext(MetadataContext.class,0);
		}
		public NameWithMetadataContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_nameWithMetadata; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterNameWithMetadata(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitNameWithMetadata(this);
		}
	}

	public final NameWithMetadataContext nameWithMetadata() throws RecognitionException {
		NameWithMetadataContext _localctx = new NameWithMetadataContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_nameWithMetadata);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(357);
			match(Identifier);
			setState(359);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__11) {
				{
				setState(358);
				metadata();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class JoinTypeContext extends ParserRuleContext {
		public JoinTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_joinType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterJoinType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitJoinType(this);
		}
	}

	public final JoinTypeContext joinType() throws RecognitionException {
		JoinTypeContext _localctx = new JoinTypeContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_joinType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(361);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__25) | (1L << T__26) | (1L << T__27))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class GroupByListContext extends ParserRuleContext {
		public GroupByListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_groupByList; }
	 
		public GroupByListContext() { }
		public void copyFrom(GroupByListContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class CubeGroupContext extends GroupByListContext {
		public ExpressionListContext expressionList() {
			return getRuleContext(ExpressionListContext.class,0);
		}
		public CubeGroupContext(GroupByListContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterCubeGroup(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitCubeGroup(this);
		}
	}
	public static class SimpleGroupContext extends GroupByListContext {
		public ExpressionListContext expressionList() {
			return getRuleContext(ExpressionListContext.class,0);
		}
		public SimpleGroupContext(GroupByListContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterSimpleGroup(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitSimpleGroup(this);
		}
	}
	public static class RollupGroupContext extends GroupByListContext {
		public ExpressionListContext expressionList() {
			return getRuleContext(ExpressionListContext.class,0);
		}
		public RollupGroupContext(GroupByListContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterRollupGroup(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitRollupGroup(this);
		}
	}

	public final GroupByListContext groupByList() throws RecognitionException {
		GroupByListContext _localctx = new GroupByListContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_groupByList);
		try {
			setState(374);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__11:
			case T__13:
			case T__17:
			case T__20:
			case T__21:
			case T__41:
			case T__46:
			case T__51:
			case T__56:
			case T__71:
			case EscapedIdentifier:
			case BaseType:
			case Not:
			case IntegerLiteral:
			case FloatingPointLiteral:
			case BooleanLiteral:
			case MultiLineStringLiteral:
			case StringLiteral:
			case UuidLiteral:
			case DateLiteral:
			case IntervalLiteral:
			case NullLiteral:
			case Identifier:
				_localctx = new SimpleGroupContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(363);
				expressionList();
				}
				break;
			case T__28:
				_localctx = new RollupGroupContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(364);
				match(T__28);
				setState(365);
				match(T__17);
				setState(366);
				expressionList();
				setState(367);
				match(T__18);
				}
				break;
			case T__29:
				_localctx = new CubeGroupContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(369);
				match(T__29);
				setState(370);
				match(T__17);
				setState(371);
				expressionList();
				setState(372);
				match(T__18);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class OrderByListContext extends ParserRuleContext {
		public List<OrderByContext> orderBy() {
			return getRuleContexts(OrderByContext.class);
		}
		public OrderByContext orderBy(int i) {
			return getRuleContext(OrderByContext.class,i);
		}
		public OrderByListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_orderByList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterOrderByList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitOrderByList(this);
		}
	}

	public final OrderByListContext orderByList() throws RecognitionException {
		OrderByListContext _localctx = new OrderByListContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_orderByList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(376);
			orderBy();
			setState(381);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,39,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(377);
					match(T__2);
					setState(378);
					orderBy();
					}
					} 
				}
				setState(383);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,39,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class OrderByContext extends ParserRuleContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public DirectionContext direction() {
			return getRuleContext(DirectionContext.class,0);
		}
		public OrderByContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_orderBy; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterOrderBy(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitOrderBy(this);
		}
	}

	public final OrderByContext orderBy() throws RecognitionException {
		OrderByContext _localctx = new OrderByContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_orderBy);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(384);
			expr(0);
			setState(386);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,40,_ctx) ) {
			case 1:
				{
				setState(385);
				direction();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DirectionContext extends ParserRuleContext {
		public DirectionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_direction; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterDirection(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitDirection(this);
		}
	}

	public final DirectionContext direction() throws RecognitionException {
		DirectionContext _localctx = new DirectionContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_direction);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(388);
			_la = _input.LA(1);
			if ( !(_la==T__30 || _la==T__31) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SetopContext extends ParserRuleContext {
		public SetopContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_setop; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterSetop(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitSetop(this);
		}
	}

	public final SetopContext setop() throws RecognitionException {
		SetopContext _localctx = new SetopContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_setop);
		try {
			setState(395);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,41,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(390);
				match(T__32);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(391);
				match(T__32);
				setState(392);
				match(T__14);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(393);
				match(T__33);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(394);
				match(T__34);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class WithContext extends ParserRuleContext {
		public Token recursive;
		public CteListContext cteList() {
			return getRuleContext(CteListContext.class,0);
		}
		public QueryUpdateContext queryUpdate() {
			return getRuleContext(QueryUpdateContext.class,0);
		}
		public WithContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_with; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterWith(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitWith(this);
		}
	}

	public final WithContext with() throws RecognitionException {
		WithContext _localctx = new WithContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_with);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(397);
			match(T__35);
			setState(399);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__36) {
				{
				setState(398);
				((WithContext)_localctx).recursive = match(T__36);
				}
			}

			setState(401);
			cteList();
			setState(402);
			queryUpdate();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CteListContext extends ParserRuleContext {
		public List<CteContext> cte() {
			return getRuleContexts(CteContext.class);
		}
		public CteContext cte(int i) {
			return getRuleContext(CteContext.class,i);
		}
		public CteListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cteList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterCteList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitCteList(this);
		}
	}

	public final CteListContext cteList() throws RecognitionException {
		CteListContext _localctx = new CteListContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_cteList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(404);
			cte();
			setState(409);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__2) {
				{
				{
				setState(405);
				match(T__2);
				setState(406);
				cte();
				}
				}
				setState(411);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CteContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(EsqlParser.Identifier, 0); }
		public QueryUpdateContext queryUpdate() {
			return getRuleContext(QueryUpdateContext.class,0);
		}
		public NamesContext names() {
			return getRuleContext(NamesContext.class,0);
		}
		public CteContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cte; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterCte(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitCte(this);
		}
	}

	public final CteContext cte() throws RecognitionException {
		CteContext _localctx = new CteContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_cte);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(412);
			match(Identifier);
			setState(414);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,44,_ctx) ) {
			case 1:
				{
				setState(413);
				names();
				}
				break;
			}
			setState(416);
			match(T__17);
			setState(417);
			queryUpdate();
			setState(418);
			match(T__18);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NamesContext extends ParserRuleContext {
		public List<TerminalNode> Identifier() { return getTokens(EsqlParser.Identifier); }
		public TerminalNode Identifier(int i) {
			return getToken(EsqlParser.Identifier, i);
		}
		public NamesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_names; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterNames(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitNames(this);
		}
	}

	public final NamesContext names() throws RecognitionException {
		NamesContext _localctx = new NamesContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_names);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(420);
			match(T__17);
			setState(421);
			match(Identifier);
			setState(426);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__2) {
				{
				{
				setState(422);
				match(T__2);
				setState(423);
				match(Identifier);
				}
				}
				setState(428);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(429);
			match(T__18);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class InsertContext extends ParserRuleContext {
		public QualifiedNameContext qualifiedName() {
			return getRuleContext(QualifiedNameContext.class,0);
		}
		public DefaultValuesContext defaultValues() {
			return getRuleContext(DefaultValuesContext.class,0);
		}
		public SelectContext select() {
			return getRuleContext(SelectContext.class,0);
		}
		public AliasContext alias() {
			return getRuleContext(AliasContext.class,0);
		}
		public NamesContext names() {
			return getRuleContext(NamesContext.class,0);
		}
		public ColumnsContext columns() {
			return getRuleContext(ColumnsContext.class,0);
		}
		public RowsContext rows() {
			return getRuleContext(RowsContext.class,0);
		}
		public MetadataContext metadata() {
			return getRuleContext(MetadataContext.class,0);
		}
		public InsertContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_insert; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterInsert(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitInsert(this);
		}
	}

	public final InsertContext insert() throws RecognitionException {
		InsertContext _localctx = new InsertContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_insert);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(431);
			match(T__37);
			setState(432);
			match(T__38);
			setState(436);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,46,_ctx) ) {
			case 1:
				{
				setState(433);
				alias();
				setState(434);
				match(T__13);
				}
				break;
			}
			setState(438);
			qualifiedName();
			setState(440);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__17) {
				{
				setState(439);
				names();
				}
			}

			setState(446);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__39:
				{
				{
				setState(442);
				match(T__39);
				setState(443);
				rows();
				}
				}
				break;
			case T__41:
				{
				setState(444);
				defaultValues();
				}
				break;
			case T__1:
			case T__35:
				{
				setState(445);
				select(0);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(453);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,50,_ctx) ) {
			case 1:
				{
				setState(448);
				match(T__40);
				setState(450);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,49,_ctx) ) {
				case 1:
					{
					setState(449);
					metadata();
					}
					break;
				}
				setState(452);
				columns();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RowsContext extends ParserRuleContext {
		public List<RowContext> row() {
			return getRuleContexts(RowContext.class);
		}
		public RowContext row(int i) {
			return getRuleContext(RowContext.class,i);
		}
		public RowsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rows; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterRows(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitRows(this);
		}
	}

	public final RowsContext rows() throws RecognitionException {
		RowsContext _localctx = new RowsContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_rows);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(455);
			row();
			setState(460);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,51,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(456);
					match(T__2);
					setState(457);
					row();
					}
					} 
				}
				setState(462);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,51,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RowContext extends ParserRuleContext {
		public ExpressionListContext expressionList() {
			return getRuleContext(ExpressionListContext.class,0);
		}
		public RowContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_row; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterRow(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitRow(this);
		}
	}

	public final RowContext row() throws RecognitionException {
		RowContext _localctx = new RowContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_row);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(463);
			match(T__17);
			setState(464);
			expressionList();
			setState(465);
			match(T__18);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DefaultValuesContext extends ParserRuleContext {
		public DefaultValuesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_defaultValues; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterDefaultValues(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitDefaultValues(this);
		}
	}

	public final DefaultValuesContext defaultValues() throws RecognitionException {
		DefaultValuesContext _localctx = new DefaultValuesContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_defaultValues);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(467);
			match(T__41);
			setState(468);
			match(T__39);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class UpdateContext extends ParserRuleContext {
		public AliasContext alias() {
			return getRuleContext(AliasContext.class,0);
		}
		public TableExprContext tableExpr() {
			return getRuleContext(TableExprContext.class,0);
		}
		public SetListContext setList() {
			return getRuleContext(SetListContext.class,0);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public ColumnsContext columns() {
			return getRuleContext(ColumnsContext.class,0);
		}
		public MetadataContext metadata() {
			return getRuleContext(MetadataContext.class,0);
		}
		public UpdateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_update; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterUpdate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitUpdate(this);
		}
	}

	public final UpdateContext update() throws RecognitionException {
		UpdateContext _localctx = new UpdateContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_update);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(470);
			match(T__42);
			setState(471);
			alias();
			setState(472);
			match(T__3);
			setState(473);
			tableExpr(0);
			setState(474);
			match(T__43);
			setState(475);
			setList();
			setState(478);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,52,_ctx) ) {
			case 1:
				{
				setState(476);
				match(T__4);
				setState(477);
				expr(0);
				}
				break;
			}
			setState(485);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,54,_ctx) ) {
			case 1:
				{
				setState(480);
				match(T__40);
				setState(482);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,53,_ctx) ) {
				case 1:
					{
					setState(481);
					metadata();
					}
					break;
				}
				setState(484);
				columns();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SetListContext extends ParserRuleContext {
		public List<SetContext> set() {
			return getRuleContexts(SetContext.class);
		}
		public SetContext set(int i) {
			return getRuleContext(SetContext.class,i);
		}
		public SetListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_setList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterSetList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitSetList(this);
		}
	}

	public final SetListContext setList() throws RecognitionException {
		SetListContext _localctx = new SetListContext(_ctx, getState());
		enterRule(_localctx, 68, RULE_setList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(487);
			set();
			setState(492);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,55,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(488);
					match(T__2);
					setState(489);
					set();
					}
					} 
				}
				setState(494);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,55,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SetContext extends ParserRuleContext {
		public QualifiedNameContext qualifiedName() {
			return getRuleContext(QualifiedNameContext.class,0);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public SetContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_set; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterSet(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitSet(this);
		}
	}

	public final SetContext set() throws RecognitionException {
		SetContext _localctx = new SetContext(_ctx, getState());
		enterRule(_localctx, 70, RULE_set);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(495);
			qualifiedName();
			setState(496);
			match(T__44);
			setState(497);
			expr(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DeleteContext extends ParserRuleContext {
		public AliasContext alias() {
			return getRuleContext(AliasContext.class,0);
		}
		public TableExprContext tableExpr() {
			return getRuleContext(TableExprContext.class,0);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public ColumnsContext columns() {
			return getRuleContext(ColumnsContext.class,0);
		}
		public MetadataContext metadata() {
			return getRuleContext(MetadataContext.class,0);
		}
		public DeleteContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_delete; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterDelete(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitDelete(this);
		}
	}

	public final DeleteContext delete() throws RecognitionException {
		DeleteContext _localctx = new DeleteContext(_ctx, getState());
		enterRule(_localctx, 72, RULE_delete);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(499);
			match(T__45);
			setState(500);
			alias();
			setState(501);
			match(T__3);
			setState(502);
			tableExpr(0);
			setState(505);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,56,_ctx) ) {
			case 1:
				{
				setState(503);
				match(T__4);
				setState(504);
				expr(0);
				}
				break;
			}
			setState(512);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,58,_ctx) ) {
			case 1:
				{
				setState(507);
				match(T__40);
				setState(509);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,57,_ctx) ) {
				case 1:
					{
					setState(508);
					metadata();
					}
					break;
				}
				setState(511);
				columns();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExprContext extends ParserRuleContext {
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }
	 
		public ExprContext() { }
		public void copyFrom(ExprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class LikeExprContext extends ExprContext {
		public ExprContext left;
		public ExprContext right;
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode Not() { return getToken(EsqlParser.Not, 0); }
		public LikeExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterLikeExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitLikeExpr(this);
		}
	}
	public static class QuantifiedComparisonContext extends ExprContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public CompareContext compare() {
			return getRuleContext(CompareContext.class,0);
		}
		public TerminalNode Quantifier() { return getToken(EsqlParser.Quantifier, 0); }
		public SelectContext select() {
			return getRuleContext(SelectContext.class,0);
		}
		public QuantifiedComparisonContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterQuantifiedComparison(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitQuantifiedComparison(this);
		}
	}
	public static class StarExprContext extends ExprContext {
		public StarExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterStarExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitStarExpr(this);
		}
	}
	public static class BetweenExprContext extends ExprContext {
		public ExprContext left;
		public ExprContext mid;
		public ExprContext right;
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode Not() { return getToken(EsqlParser.Not, 0); }
		public BetweenExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterBetweenExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitBetweenExpr(this);
		}
	}
	public static class ColumnExprContext extends ExprContext {
		public ColumnReferenceContext columnReference() {
			return getRuleContext(ColumnReferenceContext.class,0);
		}
		public ColumnExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterColumnExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitColumnExpr(this);
		}
	}
	public static class ILikeExprContext extends ExprContext {
		public ExprContext left;
		public ExprContext right;
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode Not() { return getToken(EsqlParser.Not, 0); }
		public ILikeExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterILikeExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitILikeExpr(this);
		}
	}
	public static class InExpressionContext extends ExprContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public ExpressionListContext expressionList() {
			return getRuleContext(ExpressionListContext.class,0);
		}
		public SelectContext select() {
			return getRuleContext(SelectContext.class,0);
		}
		public TerminalNode Not() { return getToken(EsqlParser.Not, 0); }
		public InExpressionContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterInExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitInExpression(this);
		}
	}
	public static class LogicalAndExprContext extends ExprContext {
		public ExprContext left;
		public ExprContext right;
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public LogicalAndExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterLogicalAndExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitLogicalAndExpr(this);
		}
	}
	public static class SimpleExpressionContext extends ExprContext {
		public SimpleExprContext simpleExpr() {
			return getRuleContext(SimpleExprContext.class,0);
		}
		public SimpleExpressionContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterSimpleExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitSimpleExpression(this);
		}
	}
	public static class DefaultValueContext extends ExprContext {
		public DefaultValueContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterDefaultValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitDefaultValue(this);
		}
	}
	public static class ComparisonContext extends ExprContext {
		public ExprContext left;
		public ExprContext right;
		public CompareContext compare() {
			return getRuleContext(CompareContext.class,0);
		}
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public ComparisonContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterComparison(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitComparison(this);
		}
	}
	public static class GroupingExprContext extends ExprContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public GroupingExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterGroupingExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitGroupingExpr(this);
		}
	}
	public static class CastExprContext extends ExprContext {
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public CastExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterCastExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitCastExpr(this);
		}
	}
	public static class LiteralExprContext extends ExprContext {
		public LiteralContext literal() {
			return getRuleContext(LiteralContext.class,0);
		}
		public LiteralExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterLiteralExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitLiteralExpr(this);
		}
	}
	public static class NotExprContext extends ExprContext {
		public TerminalNode Not() { return getToken(EsqlParser.Not, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public NotExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterNotExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitNotExpr(this);
		}
	}
	public static class NamedArgumentContext extends ExprContext {
		public TerminalNode Identifier() { return getToken(EsqlParser.Identifier, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public NamedArgumentContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterNamedArgument(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitNamedArgument(this);
		}
	}
	public static class MultiplicationExprContext extends ExprContext {
		public ExprContext left;
		public Token op;
		public ExprContext right;
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public MultiplicationExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterMultiplicationExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitMultiplicationExpr(this);
		}
	}
	public static class ExistenceContext extends ExprContext {
		public SelectContext select() {
			return getRuleContext(SelectContext.class,0);
		}
		public ExistenceContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterExistence(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitExistence(this);
		}
	}
	public static class UncomputedExprContext extends ExprContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public UncomputedExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterUncomputedExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitUncomputedExpr(this);
		}
	}
	public static class ConcatenationExprContext extends ExprContext {
		public ExprContext left;
		public ExprContext right;
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public ConcatenationExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterConcatenationExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitConcatenationExpr(this);
		}
	}
	public static class FunctionInvocationContext extends ExprContext {
		public QualifiedNameContext qualifiedName() {
			return getRuleContext(QualifiedNameContext.class,0);
		}
		public DistinctContext distinct() {
			return getRuleContext(DistinctContext.class,0);
		}
		public ExpressionListContext expressionList() {
			return getRuleContext(ExpressionListContext.class,0);
		}
		public WindowContext window() {
			return getRuleContext(WindowContext.class,0);
		}
		public FunctionInvocationContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterFunctionInvocation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitFunctionInvocation(this);
		}
	}
	public static class CoalesceExprContext extends ExprContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public CoalesceExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterCoalesceExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitCoalesceExpr(this);
		}
	}
	public static class SelectExprContext extends ExprContext {
		public SelectExpressionContext selectExpression() {
			return getRuleContext(SelectExpressionContext.class,0);
		}
		public SelectExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterSelectExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitSelectExpr(this);
		}
	}
	public static class NamedParameterContext extends ExprContext {
		public TerminalNode Identifier() { return getToken(EsqlParser.Identifier, 0); }
		public NamedParameterContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterNamedParameter(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitNamedParameter(this);
		}
	}
	public static class ExponentiationExprContext extends ExprContext {
		public ExprContext left;
		public ExprContext right;
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public ExponentiationExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterExponentiationExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitExponentiationExpr(this);
		}
	}
	public static class RangeExprContext extends ExprContext {
		public ExprContext left;
		public OrderingContext leftCompare;
		public SimpleExprContext mid;
		public OrderingContext rightCompare;
		public ExprContext right;
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public List<OrderingContext> ordering() {
			return getRuleContexts(OrderingContext.class);
		}
		public OrderingContext ordering(int i) {
			return getRuleContext(OrderingContext.class,i);
		}
		public SimpleExprContext simpleExpr() {
			return getRuleContext(SimpleExprContext.class,0);
		}
		public RangeExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterRangeExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitRangeExpr(this);
		}
	}
	public static class LogicalOrExprContext extends ExprContext {
		public ExprContext left;
		public ExprContext right;
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public LogicalOrExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterLogicalOrExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitLogicalOrExpr(this);
		}
	}
	public static class AdditionExprContext extends ExprContext {
		public ExprContext left;
		public Token op;
		public ExprContext right;
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public AdditionExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterAdditionExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitAdditionExpr(this);
		}
	}
	public static class NullCheckExprContext extends ExprContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode NullLiteral() { return getToken(EsqlParser.NullLiteral, 0); }
		public TerminalNode Not() { return getToken(EsqlParser.Not, 0); }
		public NullCheckExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterNullCheckExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitNullCheckExpr(this);
		}
	}
	public static class CaseExprContext extends ExprContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public CaseExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterCaseExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitCaseExpr(this);
		}
	}
	public static class NegationExprContext extends ExprContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public NegationExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterNegationExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitNegationExpr(this);
		}
	}

	public final ExprContext expr() throws RecognitionException {
		return expr(0);
	}

	private ExprContext expr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExprContext _localctx = new ExprContext(_ctx, _parentState);
		ExprContext _prevctx = _localctx;
		int _startState = 74;
		enterRecursionRule(_localctx, 74, RULE_expr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(560);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,62,_ctx) ) {
			case 1:
				{
				_localctx = new GroupingExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(515);
				match(T__17);
				setState(516);
				expr(0);
				setState(517);
				match(T__18);
				}
				break;
			case 2:
				{
				_localctx = new UncomputedExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(519);
				match(T__46);
				setState(520);
				expr(0);
				setState(521);
				match(T__18);
				}
				break;
			case 3:
				{
				_localctx = new CastExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(523);
				type();
				setState(524);
				match(T__47);
				setState(525);
				expr(0);
				setState(526);
				match(T__48);
				}
				break;
			case 4:
				{
				_localctx = new DefaultValueContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(528);
				match(T__41);
				}
				break;
			case 5:
				{
				_localctx = new LiteralExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(529);
				literal();
				}
				break;
			case 6:
				{
				_localctx = new NegationExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(530);
				match(T__51);
				setState(531);
				expr(24);
				}
				break;
			case 7:
				{
				_localctx = new StarExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(532);
				match(T__20);
				}
				break;
			case 8:
				{
				_localctx = new NamedParameterContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(533);
				match(T__13);
				setState(534);
				match(Identifier);
				}
				break;
			case 9:
				{
				_localctx = new NamedArgumentContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(535);
				match(Identifier);
				setState(536);
				match(T__55);
				setState(537);
				expr(18);
				}
				break;
			case 10:
				{
				_localctx = new SelectExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(538);
				selectExpression();
				}
				break;
			case 11:
				{
				_localctx = new NotExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(539);
				match(Not);
				setState(540);
				expr(16);
				}
				break;
			case 12:
				{
				_localctx = new FunctionInvocationContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(541);
				qualifiedName();
				setState(542);
				match(T__17);
				setState(544);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__14 || _la==T__15) {
					{
					setState(543);
					distinct();
					}
				}

				setState(547);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__11) | (1L << T__13) | (1L << T__17) | (1L << T__20) | (1L << T__21) | (1L << T__41) | (1L << T__46) | (1L << T__51) | (1L << T__56))) != 0) || ((((_la - 72)) & ~0x3f) == 0 && ((1L << (_la - 72)) & ((1L << (T__71 - 72)) | (1L << (EscapedIdentifier - 72)) | (1L << (BaseType - 72)) | (1L << (Not - 72)) | (1L << (IntegerLiteral - 72)) | (1L << (FloatingPointLiteral - 72)) | (1L << (BooleanLiteral - 72)) | (1L << (MultiLineStringLiteral - 72)) | (1L << (StringLiteral - 72)) | (1L << (UuidLiteral - 72)) | (1L << (DateLiteral - 72)) | (1L << (IntervalLiteral - 72)) | (1L << (NullLiteral - 72)) | (1L << (Identifier - 72)))) != 0)) {
					{
					setState(546);
					expressionList();
					}
				}

				setState(549);
				match(T__18);
				setState(551);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,61,_ctx) ) {
				case 1:
					{
					setState(550);
					window();
					}
					break;
				}
				}
				break;
			case 13:
				{
				_localctx = new ExistenceContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(553);
				match(T__56);
				setState(554);
				match(T__17);
				setState(555);
				select(0);
				setState(556);
				match(T__18);
				}
				break;
			case 14:
				{
				_localctx = new ColumnExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(558);
				columnReference();
				}
				break;
			case 15:
				{
				_localctx = new SimpleExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(559);
				simpleExpr(0);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(655);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,72,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(653);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,71,_ctx) ) {
					case 1:
						{
						_localctx = new ConcatenationExprContext(new ExprContext(_parentctx, _parentState));
						((ConcatenationExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(562);
						if (!(precpred(_ctx, 25))) throw new FailedPredicateException(this, "precpred(_ctx, 25)");
						setState(563);
						match(T__50);
						setState(564);
						((ConcatenationExprContext)_localctx).right = expr(26);
						}
						break;
					case 2:
						{
						_localctx = new ExponentiationExprContext(new ExprContext(_parentctx, _parentState));
						((ExponentiationExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(565);
						if (!(precpred(_ctx, 23))) throw new FailedPredicateException(this, "precpred(_ctx, 23)");
						setState(566);
						match(T__52);
						setState(567);
						((ExponentiationExprContext)_localctx).right = expr(23);
						}
						break;
					case 3:
						{
						_localctx = new MultiplicationExprContext(new ExprContext(_parentctx, _parentState));
						((MultiplicationExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(568);
						if (!(precpred(_ctx, 22))) throw new FailedPredicateException(this, "precpred(_ctx, 22)");
						setState(569);
						((MultiplicationExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__20) | (1L << T__21) | (1L << T__53))) != 0)) ) {
							((MultiplicationExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(570);
						((MultiplicationExprContext)_localctx).right = expr(23);
						}
						break;
					case 4:
						{
						_localctx = new AdditionExprContext(new ExprContext(_parentctx, _parentState));
						((AdditionExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(571);
						if (!(precpred(_ctx, 21))) throw new FailedPredicateException(this, "precpred(_ctx, 21)");
						setState(572);
						((AdditionExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==T__51 || _la==T__54) ) {
							((AdditionExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(573);
						((AdditionExprContext)_localctx).right = expr(22);
						}
						break;
					case 5:
						{
						_localctx = new RangeExprContext(new ExprContext(_parentctx, _parentState));
						((RangeExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(574);
						if (!(precpred(_ctx, 13))) throw new FailedPredicateException(this, "precpred(_ctx, 13)");
						setState(575);
						((RangeExprContext)_localctx).leftCompare = ordering();
						setState(576);
						((RangeExprContext)_localctx).mid = simpleExpr(0);
						setState(577);
						((RangeExprContext)_localctx).rightCompare = ordering();
						setState(578);
						((RangeExprContext)_localctx).right = expr(14);
						}
						break;
					case 6:
						{
						_localctx = new ComparisonContext(new ExprContext(_parentctx, _parentState));
						((ComparisonContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(580);
						if (!(precpred(_ctx, 12))) throw new FailedPredicateException(this, "precpred(_ctx, 12)");
						setState(581);
						compare();
						setState(582);
						((ComparisonContext)_localctx).right = expr(13);
						}
						break;
					case 7:
						{
						_localctx = new BetweenExprContext(new ExprContext(_parentctx, _parentState));
						((BetweenExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(584);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(586);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==Not) {
							{
							setState(585);
							match(Not);
							}
						}

						setState(588);
						match(T__58);
						setState(589);
						((BetweenExprContext)_localctx).mid = expr(0);
						setState(590);
						match(T__59);
						setState(591);
						((BetweenExprContext)_localctx).right = expr(10);
						}
						break;
					case 8:
						{
						_localctx = new LikeExprContext(new ExprContext(_parentctx, _parentState));
						((LikeExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(593);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(595);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==Not) {
							{
							setState(594);
							match(Not);
							}
						}

						setState(597);
						match(T__60);
						setState(598);
						((LikeExprContext)_localctx).right = expr(9);
						}
						break;
					case 9:
						{
						_localctx = new ILikeExprContext(new ExprContext(_parentctx, _parentState));
						((ILikeExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(599);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(601);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==Not) {
							{
							setState(600);
							match(Not);
							}
						}

						setState(603);
						match(T__61);
						setState(604);
						((ILikeExprContext)_localctx).right = expr(8);
						}
						break;
					case 10:
						{
						_localctx = new LogicalAndExprContext(new ExprContext(_parentctx, _parentState));
						((LogicalAndExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(605);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(606);
						match(T__59);
						setState(607);
						((LogicalAndExprContext)_localctx).right = expr(5);
						}
						break;
					case 11:
						{
						_localctx = new LogicalOrExprContext(new ExprContext(_parentctx, _parentState));
						((LogicalOrExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(608);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(609);
						match(T__63);
						setState(610);
						((LogicalOrExprContext)_localctx).right = expr(4);
						}
						break;
					case 12:
						{
						_localctx = new CoalesceExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(611);
						if (!(precpred(_ctx, 26))) throw new FailedPredicateException(this, "precpred(_ctx, 26)");
						setState(614); 
						_errHandler.sync(this);
						_alt = 1;
						do {
							switch (_alt) {
							case 1:
								{
								{
								setState(612);
								match(T__49);
								setState(613);
								expr(0);
								}
								}
								break;
							default:
								throw new NoViableAltException(this);
							}
							setState(616); 
							_errHandler.sync(this);
							_alt = getInterpreter().adaptivePredict(_input,66,_ctx);
						} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
						}
						break;
					case 13:
						{
						_localctx = new QuantifiedComparisonContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(618);
						if (!(precpred(_ctx, 11))) throw new FailedPredicateException(this, "precpred(_ctx, 11)");
						setState(619);
						compare();
						setState(620);
						match(Quantifier);
						setState(621);
						match(T__17);
						setState(622);
						select(0);
						setState(623);
						match(T__18);
						}
						break;
					case 14:
						{
						_localctx = new InExpressionContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(625);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(627);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==Not) {
							{
							setState(626);
							match(Not);
							}
						}

						setState(629);
						match(T__57);
						setState(630);
						match(T__17);
						setState(633);
						_errHandler.sync(this);
						switch (_input.LA(1)) {
						case T__11:
						case T__13:
						case T__17:
						case T__20:
						case T__21:
						case T__41:
						case T__46:
						case T__51:
						case T__56:
						case T__71:
						case EscapedIdentifier:
						case BaseType:
						case Not:
						case IntegerLiteral:
						case FloatingPointLiteral:
						case BooleanLiteral:
						case MultiLineStringLiteral:
						case StringLiteral:
						case UuidLiteral:
						case DateLiteral:
						case IntervalLiteral:
						case NullLiteral:
						case Identifier:
							{
							setState(631);
							expressionList();
							}
							break;
						case T__1:
						case T__35:
							{
							setState(632);
							select(0);
							}
							break;
						default:
							throw new NoViableAltException(this);
						}
						setState(635);
						match(T__18);
						}
						break;
					case 15:
						{
						_localctx = new NullCheckExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(637);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(638);
						match(T__62);
						setState(640);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==Not) {
							{
							setState(639);
							match(Not);
							}
						}

						setState(642);
						match(NullLiteral);
						}
						break;
					case 16:
						{
						_localctx = new CaseExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(643);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(649); 
						_errHandler.sync(this);
						_alt = 1;
						do {
							switch (_alt) {
							case 1:
								{
								{
								setState(644);
								match(T__64);
								setState(645);
								expr(0);
								setState(646);
								match(T__65);
								setState(647);
								expr(0);
								}
								}
								break;
							default:
								throw new NoViableAltException(this);
							}
							setState(651); 
							_errHandler.sync(this);
							_alt = getInterpreter().adaptivePredict(_input,70,_ctx);
						} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
						}
						break;
					}
					} 
				}
				setState(657);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,72,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class SimpleExprContext extends ParserRuleContext {
		public SimpleExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_simpleExpr; }
	 
		public SimpleExprContext() { }
		public void copyFrom(SimpleExprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class SimpleConcatenationExprContext extends SimpleExprContext {
		public SimpleExprContext left;
		public SimpleExprContext right;
		public List<SimpleExprContext> simpleExpr() {
			return getRuleContexts(SimpleExprContext.class);
		}
		public SimpleExprContext simpleExpr(int i) {
			return getRuleContext(SimpleExprContext.class,i);
		}
		public SimpleConcatenationExprContext(SimpleExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterSimpleConcatenationExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitSimpleConcatenationExpr(this);
		}
	}
	public static class SimpleFunctionInvocationContext extends SimpleExprContext {
		public QualifiedNameContext qualifiedName() {
			return getRuleContext(QualifiedNameContext.class,0);
		}
		public DistinctContext distinct() {
			return getRuleContext(DistinctContext.class,0);
		}
		public ExpressionListContext expressionList() {
			return getRuleContext(ExpressionListContext.class,0);
		}
		public WindowContext window() {
			return getRuleContext(WindowContext.class,0);
		}
		public SimpleFunctionInvocationContext(SimpleExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterSimpleFunctionInvocation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitSimpleFunctionInvocation(this);
		}
	}
	public static class SimpleLiteralExprContext extends SimpleExprContext {
		public LiteralContext literal() {
			return getRuleContext(LiteralContext.class,0);
		}
		public SimpleLiteralExprContext(SimpleExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterSimpleLiteralExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitSimpleLiteralExpr(this);
		}
	}
	public static class SimpleGroupingExprContext extends SimpleExprContext {
		public SimpleExprContext simpleExpr() {
			return getRuleContext(SimpleExprContext.class,0);
		}
		public SimpleGroupingExprContext(SimpleExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterSimpleGroupingExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitSimpleGroupingExpr(this);
		}
	}
	public static class SimpleExponentiationExprContext extends SimpleExprContext {
		public SimpleExprContext left;
		public SimpleExprContext right;
		public List<SimpleExprContext> simpleExpr() {
			return getRuleContexts(SimpleExprContext.class);
		}
		public SimpleExprContext simpleExpr(int i) {
			return getRuleContext(SimpleExprContext.class,i);
		}
		public SimpleExponentiationExprContext(SimpleExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterSimpleExponentiationExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitSimpleExponentiationExpr(this);
		}
	}
	public static class SimpleSelectExprContext extends SimpleExprContext {
		public SelectExpressionContext selectExpression() {
			return getRuleContext(SelectExpressionContext.class,0);
		}
		public SimpleSelectExprContext(SimpleExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterSimpleSelectExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitSimpleSelectExpr(this);
		}
	}
	public static class SimpleAdditionExprContext extends SimpleExprContext {
		public SimpleExprContext left;
		public Token op;
		public SimpleExprContext right;
		public List<SimpleExprContext> simpleExpr() {
			return getRuleContexts(SimpleExprContext.class);
		}
		public SimpleExprContext simpleExpr(int i) {
			return getRuleContext(SimpleExprContext.class,i);
		}
		public SimpleAdditionExprContext(SimpleExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterSimpleAdditionExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitSimpleAdditionExpr(this);
		}
	}
	public static class SimpleNegationExprContext extends SimpleExprContext {
		public SimpleExprContext simpleExpr() {
			return getRuleContext(SimpleExprContext.class,0);
		}
		public SimpleNegationExprContext(SimpleExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterSimpleNegationExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitSimpleNegationExpr(this);
		}
	}
	public static class SimpleMultiplicationExprContext extends SimpleExprContext {
		public SimpleExprContext left;
		public Token op;
		public SimpleExprContext right;
		public List<SimpleExprContext> simpleExpr() {
			return getRuleContexts(SimpleExprContext.class);
		}
		public SimpleExprContext simpleExpr(int i) {
			return getRuleContext(SimpleExprContext.class,i);
		}
		public SimpleMultiplicationExprContext(SimpleExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterSimpleMultiplicationExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitSimpleMultiplicationExpr(this);
		}
	}
	public static class SimpleCastExprContext extends SimpleExprContext {
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public SimpleExprContext simpleExpr() {
			return getRuleContext(SimpleExprContext.class,0);
		}
		public SimpleCastExprContext(SimpleExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterSimpleCastExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitSimpleCastExpr(this);
		}
	}
	public static class SimpleCaseExprContext extends SimpleExprContext {
		public List<SimpleExprContext> simpleExpr() {
			return getRuleContexts(SimpleExprContext.class);
		}
		public SimpleExprContext simpleExpr(int i) {
			return getRuleContext(SimpleExprContext.class,i);
		}
		public SimpleCaseExprContext(SimpleExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterSimpleCaseExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitSimpleCaseExpr(this);
		}
	}
	public static class SimpleCoalesceExprContext extends SimpleExprContext {
		public List<SimpleExprContext> simpleExpr() {
			return getRuleContexts(SimpleExprContext.class);
		}
		public SimpleExprContext simpleExpr(int i) {
			return getRuleContext(SimpleExprContext.class,i);
		}
		public SimpleCoalesceExprContext(SimpleExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterSimpleCoalesceExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitSimpleCoalesceExpr(this);
		}
	}
	public static class SimpleColumnExprContext extends SimpleExprContext {
		public ColumnReferenceContext columnReference() {
			return getRuleContext(ColumnReferenceContext.class,0);
		}
		public SimpleColumnExprContext(SimpleExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterSimpleColumnExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitSimpleColumnExpr(this);
		}
	}

	public final SimpleExprContext simpleExpr() throws RecognitionException {
		return simpleExpr(0);
	}

	private SimpleExprContext simpleExpr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		SimpleExprContext _localctx = new SimpleExprContext(_ctx, _parentState);
		SimpleExprContext _prevctx = _localctx;
		int _startState = 76;
		enterRecursionRule(_localctx, 76, RULE_simpleExpr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(685);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,76,_ctx) ) {
			case 1:
				{
				_localctx = new SimpleGroupingExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(659);
				match(T__17);
				setState(660);
				simpleExpr(0);
				setState(661);
				match(T__18);
				}
				break;
			case 2:
				{
				_localctx = new SimpleCastExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(663);
				type();
				setState(664);
				match(T__47);
				setState(665);
				simpleExpr(0);
				setState(666);
				match(T__48);
				}
				break;
			case 3:
				{
				_localctx = new SimpleLiteralExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(668);
				literal();
				}
				break;
			case 4:
				{
				_localctx = new SimpleNegationExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(669);
				match(T__51);
				setState(670);
				simpleExpr(8);
				}
				break;
			case 5:
				{
				_localctx = new SimpleSelectExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(671);
				selectExpression();
				}
				break;
			case 6:
				{
				_localctx = new SimpleFunctionInvocationContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(672);
				qualifiedName();
				setState(673);
				match(T__17);
				setState(675);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__14 || _la==T__15) {
					{
					setState(674);
					distinct();
					}
				}

				setState(678);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__11) | (1L << T__13) | (1L << T__17) | (1L << T__20) | (1L << T__21) | (1L << T__41) | (1L << T__46) | (1L << T__51) | (1L << T__56))) != 0) || ((((_la - 72)) & ~0x3f) == 0 && ((1L << (_la - 72)) & ((1L << (T__71 - 72)) | (1L << (EscapedIdentifier - 72)) | (1L << (BaseType - 72)) | (1L << (Not - 72)) | (1L << (IntegerLiteral - 72)) | (1L << (FloatingPointLiteral - 72)) | (1L << (BooleanLiteral - 72)) | (1L << (MultiLineStringLiteral - 72)) | (1L << (StringLiteral - 72)) | (1L << (UuidLiteral - 72)) | (1L << (DateLiteral - 72)) | (1L << (IntervalLiteral - 72)) | (1L << (NullLiteral - 72)) | (1L << (Identifier - 72)))) != 0)) {
					{
					setState(677);
					expressionList();
					}
				}

				setState(680);
				match(T__18);
				setState(682);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,75,_ctx) ) {
				case 1:
					{
					setState(681);
					window();
					}
					break;
				}
				}
				break;
			case 7:
				{
				_localctx = new SimpleColumnExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(684);
				columnReference();
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(718);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,80,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(716);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,79,_ctx) ) {
					case 1:
						{
						_localctx = new SimpleConcatenationExprContext(new SimpleExprContext(_parentctx, _parentState));
						((SimpleConcatenationExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_simpleExpr);
						setState(687);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(688);
						match(T__50);
						setState(689);
						((SimpleConcatenationExprContext)_localctx).right = simpleExpr(10);
						}
						break;
					case 2:
						{
						_localctx = new SimpleExponentiationExprContext(new SimpleExprContext(_parentctx, _parentState));
						((SimpleExponentiationExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_simpleExpr);
						setState(690);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(691);
						match(T__52);
						setState(692);
						((SimpleExponentiationExprContext)_localctx).right = simpleExpr(7);
						}
						break;
					case 3:
						{
						_localctx = new SimpleMultiplicationExprContext(new SimpleExprContext(_parentctx, _parentState));
						((SimpleMultiplicationExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_simpleExpr);
						setState(693);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(694);
						((SimpleMultiplicationExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__20) | (1L << T__21) | (1L << T__53))) != 0)) ) {
							((SimpleMultiplicationExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(695);
						((SimpleMultiplicationExprContext)_localctx).right = simpleExpr(7);
						}
						break;
					case 4:
						{
						_localctx = new SimpleAdditionExprContext(new SimpleExprContext(_parentctx, _parentState));
						((SimpleAdditionExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_simpleExpr);
						setState(696);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(697);
						((SimpleAdditionExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==T__51 || _la==T__54) ) {
							((SimpleAdditionExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(698);
						((SimpleAdditionExprContext)_localctx).right = simpleExpr(6);
						}
						break;
					case 5:
						{
						_localctx = new SimpleCoalesceExprContext(new SimpleExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_simpleExpr);
						setState(699);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(702); 
						_errHandler.sync(this);
						_alt = 1;
						do {
							switch (_alt) {
							case 1:
								{
								{
								setState(700);
								match(T__49);
								setState(701);
								simpleExpr(0);
								}
								}
								break;
							default:
								throw new NoViableAltException(this);
							}
							setState(704); 
							_errHandler.sync(this);
							_alt = getInterpreter().adaptivePredict(_input,77,_ctx);
						} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
						}
						break;
					case 6:
						{
						_localctx = new SimpleCaseExprContext(new SimpleExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_simpleExpr);
						setState(706);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(712); 
						_errHandler.sync(this);
						_alt = 1;
						do {
							switch (_alt) {
							case 1:
								{
								{
								setState(707);
								match(T__64);
								setState(708);
								simpleExpr(0);
								setState(709);
								match(T__65);
								setState(710);
								simpleExpr(0);
								}
								}
								break;
							default:
								throw new NoViableAltException(this);
							}
							setState(714); 
							_errHandler.sync(this);
							_alt = getInterpreter().adaptivePredict(_input,78,_ctx);
						} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
						}
						break;
					}
					} 
				}
				setState(720);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,80,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class SelectExpressionContext extends ParserRuleContext {
		public ExprContext col;
		public ExprContext where;
		public ExprContext offset;
		public TableExprContext tableExpr() {
			return getRuleContext(TableExprContext.class,0);
		}
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public DistinctContext distinct() {
			return getRuleContext(DistinctContext.class,0);
		}
		public AliasContext alias() {
			return getRuleContext(AliasContext.class,0);
		}
		public OrderByListContext orderByList() {
			return getRuleContext(OrderByListContext.class,0);
		}
		public SelectExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_selectExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterSelectExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitSelectExpression(this);
		}
	}

	public final SelectExpressionContext selectExpression() throws RecognitionException {
		SelectExpressionContext _localctx = new SelectExpressionContext(_ctx, getState());
		enterRule(_localctx, 78, RULE_selectExpression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(721);
			match(T__17);
			setState(723);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__14 || _la==T__15) {
				{
				setState(722);
				distinct();
				}
			}

			setState(728);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,82,_ctx) ) {
			case 1:
				{
				setState(725);
				alias();
				setState(726);
				match(T__13);
				}
				break;
			}
			setState(730);
			((SelectExpressionContext)_localctx).col = expr(0);
			setState(731);
			match(T__3);
			setState(732);
			tableExpr(0);
			setState(735);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__4) {
				{
				setState(733);
				match(T__4);
				setState(734);
				((SelectExpressionContext)_localctx).where = expr(0);
				}
			}

			setState(740);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__5) {
				{
				setState(737);
				match(T__5);
				setState(738);
				match(T__6);
				setState(739);
				orderByList();
				}
			}

			setState(744);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__9) {
				{
				setState(742);
				match(T__9);
				setState(743);
				((SelectExpressionContext)_localctx).offset = expr(0);
				}
			}

			setState(746);
			match(T__18);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class WindowContext extends ParserRuleContext {
		public PartitionContext partition() {
			return getRuleContext(PartitionContext.class,0);
		}
		public OrderByListContext orderByList() {
			return getRuleContext(OrderByListContext.class,0);
		}
		public WindowContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_window; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterWindow(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitWindow(this);
		}
	}

	public final WindowContext window() throws RecognitionException {
		WindowContext _localctx = new WindowContext(_ctx, getState());
		enterRule(_localctx, 80, RULE_window);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(748);
			match(T__66);
			setState(749);
			match(T__17);
			setState(751);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__67) {
				{
				setState(750);
				partition();
				}
			}

			setState(756);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__5) {
				{
				setState(753);
				match(T__5);
				setState(754);
				match(T__6);
				setState(755);
				orderByList();
				}
			}

			setState(758);
			match(T__18);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PartitionContext extends ParserRuleContext {
		public ExpressionListContext expressionList() {
			return getRuleContext(ExpressionListContext.class,0);
		}
		public PartitionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_partition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterPartition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitPartition(this);
		}
	}

	public final PartitionContext partition() throws RecognitionException {
		PartitionContext _localctx = new PartitionContext(_ctx, getState());
		enterRule(_localctx, 82, RULE_partition);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(760);
			match(T__67);
			setState(761);
			match(T__6);
			setState(762);
			expressionList();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CompareContext extends ParserRuleContext {
		public CompareContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_compare; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterCompare(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitCompare(this);
		}
	}

	public final CompareContext compare() throws RecognitionException {
		CompareContext _localctx = new CompareContext(_ctx, getState());
		enterRule(_localctx, 84, RULE_compare);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(764);
			_la = _input.LA(1);
			if ( !(((((_la - 45)) & ~0x3f) == 0 && ((1L << (_la - 45)) & ((1L << (T__44 - 45)) | (1L << (T__47 - 45)) | (1L << (T__48 - 45)) | (1L << (T__68 - 45)) | (1L << (T__69 - 45)) | (1L << (T__70 - 45)))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class OrderingContext extends ParserRuleContext {
		public OrderingContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ordering; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterOrdering(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitOrdering(this);
		}
	}

	public final OrderingContext ordering() throws RecognitionException {
		OrderingContext _localctx = new OrderingContext(_ctx, getState());
		enterRule(_localctx, 86, RULE_ordering);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(766);
			_la = _input.LA(1);
			if ( !(((((_la - 48)) & ~0x3f) == 0 && ((1L << (_la - 48)) & ((1L << (T__47 - 48)) | (1L << (T__48 - 48)) | (1L << (T__69 - 48)) | (1L << (T__70 - 48)))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExpressionListContext extends ParserRuleContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public ExpressionListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expressionList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterExpressionList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitExpressionList(this);
		}
	}

	public final ExpressionListContext expressionList() throws RecognitionException {
		ExpressionListContext _localctx = new ExpressionListContext(_ctx, getState());
		enterRule(_localctx, 88, RULE_expressionList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(768);
			expr(0);
			setState(773);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,88,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(769);
					match(T__2);
					setState(770);
					expr(0);
					}
					} 
				}
				setState(775);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,88,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LiteralContext extends ParserRuleContext {
		public LiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_literal; }
	 
		public LiteralContext() { }
		public void copyFrom(LiteralContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class JsonArrayLiteralContext extends LiteralContext {
		public LiteralListContext literalList() {
			return getRuleContext(LiteralListContext.class,0);
		}
		public JsonArrayLiteralContext(LiteralContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterJsonArrayLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitJsonArrayLiteral(this);
		}
	}
	public static class NullContext extends LiteralContext {
		public TerminalNode NullLiteral() { return getToken(EsqlParser.NullLiteral, 0); }
		public NullContext(LiteralContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterNull(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitNull(this);
		}
	}
	public static class BasicLiteralsContext extends LiteralContext {
		public BaseLiteralContext baseLiteral() {
			return getRuleContext(BaseLiteralContext.class,0);
		}
		public BasicLiteralsContext(LiteralContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterBasicLiterals(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitBasicLiterals(this);
		}
	}
	public static class BaseArrayLiteralContext extends LiteralContext {
		public TerminalNode BaseType() { return getToken(EsqlParser.BaseType, 0); }
		public BaseLiteralListContext baseLiteralList() {
			return getRuleContext(BaseLiteralListContext.class,0);
		}
		public BaseArrayLiteralContext(LiteralContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterBaseArrayLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitBaseArrayLiteral(this);
		}
	}
	public static class JsonObjectLiteralContext extends LiteralContext {
		public AttributeListContext attributeList() {
			return getRuleContext(AttributeListContext.class,0);
		}
		public JsonObjectLiteralContext(LiteralContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterJsonObjectLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitJsonObjectLiteral(this);
		}
	}

	public final LiteralContext literal() throws RecognitionException {
		LiteralContext _localctx = new LiteralContext(_ctx, getState());
		enterRule(_localctx, 90, RULE_literal);
		int _la;
		try {
			setState(794);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IntegerLiteral:
			case FloatingPointLiteral:
			case BooleanLiteral:
			case MultiLineStringLiteral:
			case StringLiteral:
			case UuidLiteral:
			case DateLiteral:
			case IntervalLiteral:
				_localctx = new BasicLiteralsContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(776);
				baseLiteral();
				}
				break;
			case NullLiteral:
				_localctx = new NullContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(777);
				match(NullLiteral);
				}
				break;
			case BaseType:
				_localctx = new BaseArrayLiteralContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(778);
				match(BaseType);
				setState(779);
				match(T__71);
				setState(781);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (((((_la - 99)) & ~0x3f) == 0 && ((1L << (_la - 99)) & ((1L << (IntegerLiteral - 99)) | (1L << (FloatingPointLiteral - 99)) | (1L << (BooleanLiteral - 99)) | (1L << (MultiLineStringLiteral - 99)) | (1L << (StringLiteral - 99)) | (1L << (UuidLiteral - 99)) | (1L << (DateLiteral - 99)) | (1L << (IntervalLiteral - 99)))) != 0)) {
					{
					setState(780);
					baseLiteralList();
					}
				}

				setState(783);
				match(T__72);
				}
				break;
			case T__71:
				_localctx = new JsonArrayLiteralContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(784);
				match(T__71);
				setState(786);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__11 || ((((_la - 72)) & ~0x3f) == 0 && ((1L << (_la - 72)) & ((1L << (T__71 - 72)) | (1L << (BaseType - 72)) | (1L << (IntegerLiteral - 72)) | (1L << (FloatingPointLiteral - 72)) | (1L << (BooleanLiteral - 72)) | (1L << (MultiLineStringLiteral - 72)) | (1L << (StringLiteral - 72)) | (1L << (UuidLiteral - 72)) | (1L << (DateLiteral - 72)) | (1L << (IntervalLiteral - 72)) | (1L << (NullLiteral - 72)))) != 0)) {
					{
					setState(785);
					literalList();
					}
				}

				setState(788);
				match(T__72);
				}
				break;
			case T__11:
				_localctx = new JsonObjectLiteralContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(789);
				match(T__11);
				setState(791);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Identifier) {
					{
					setState(790);
					attributeList();
					}
				}

				setState(793);
				match(T__12);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BaseLiteralContext extends ParserRuleContext {
		public BaseLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_baseLiteral; }
	 
		public BaseLiteralContext() { }
		public void copyFrom(BaseLiteralContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class IntegerContext extends BaseLiteralContext {
		public TerminalNode IntegerLiteral() { return getToken(EsqlParser.IntegerLiteral, 0); }
		public IntegerContext(BaseLiteralContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterInteger(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitInteger(this);
		}
	}
	public static class MultiLineStringContext extends BaseLiteralContext {
		public TerminalNode MultiLineStringLiteral() { return getToken(EsqlParser.MultiLineStringLiteral, 0); }
		public MultiLineStringContext(BaseLiteralContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterMultiLineString(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitMultiLineString(this);
		}
	}
	public static class UuidContext extends BaseLiteralContext {
		public TerminalNode UuidLiteral() { return getToken(EsqlParser.UuidLiteral, 0); }
		public UuidContext(BaseLiteralContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterUuid(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitUuid(this);
		}
	}
	public static class FloatingPointContext extends BaseLiteralContext {
		public TerminalNode FloatingPointLiteral() { return getToken(EsqlParser.FloatingPointLiteral, 0); }
		public FloatingPointContext(BaseLiteralContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterFloatingPoint(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitFloatingPoint(this);
		}
	}
	public static class StringContext extends BaseLiteralContext {
		public TerminalNode StringLiteral() { return getToken(EsqlParser.StringLiteral, 0); }
		public StringContext(BaseLiteralContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterString(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitString(this);
		}
	}
	public static class BooleanContext extends BaseLiteralContext {
		public TerminalNode BooleanLiteral() { return getToken(EsqlParser.BooleanLiteral, 0); }
		public BooleanContext(BaseLiteralContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterBoolean(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitBoolean(this);
		}
	}
	public static class DateContext extends BaseLiteralContext {
		public TerminalNode DateLiteral() { return getToken(EsqlParser.DateLiteral, 0); }
		public DateContext(BaseLiteralContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterDate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitDate(this);
		}
	}
	public static class IntervalContext extends BaseLiteralContext {
		public TerminalNode IntervalLiteral() { return getToken(EsqlParser.IntervalLiteral, 0); }
		public IntervalContext(BaseLiteralContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterInterval(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitInterval(this);
		}
	}

	public final BaseLiteralContext baseLiteral() throws RecognitionException {
		BaseLiteralContext _localctx = new BaseLiteralContext(_ctx, getState());
		enterRule(_localctx, 92, RULE_baseLiteral);
		try {
			setState(804);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IntegerLiteral:
				_localctx = new IntegerContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(796);
				match(IntegerLiteral);
				}
				break;
			case FloatingPointLiteral:
				_localctx = new FloatingPointContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(797);
				match(FloatingPointLiteral);
				}
				break;
			case BooleanLiteral:
				_localctx = new BooleanContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(798);
				match(BooleanLiteral);
				}
				break;
			case MultiLineStringLiteral:
				_localctx = new MultiLineStringContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(799);
				match(MultiLineStringLiteral);
				}
				break;
			case StringLiteral:
				_localctx = new StringContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(800);
				match(StringLiteral);
				}
				break;
			case UuidLiteral:
				_localctx = new UuidContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(801);
				match(UuidLiteral);
				}
				break;
			case DateLiteral:
				_localctx = new DateContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(802);
				match(DateLiteral);
				}
				break;
			case IntervalLiteral:
				_localctx = new IntervalContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(803);
				match(IntervalLiteral);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LiteralListContext extends ParserRuleContext {
		public List<LiteralContext> literal() {
			return getRuleContexts(LiteralContext.class);
		}
		public LiteralContext literal(int i) {
			return getRuleContext(LiteralContext.class,i);
		}
		public LiteralListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_literalList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterLiteralList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitLiteralList(this);
		}
	}

	public final LiteralListContext literalList() throws RecognitionException {
		LiteralListContext _localctx = new LiteralListContext(_ctx, getState());
		enterRule(_localctx, 94, RULE_literalList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(806);
			literal();
			setState(811);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__2) {
				{
				{
				setState(807);
				match(T__2);
				setState(808);
				literal();
				}
				}
				setState(813);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BaseLiteralListContext extends ParserRuleContext {
		public List<BaseLiteralContext> baseLiteral() {
			return getRuleContexts(BaseLiteralContext.class);
		}
		public BaseLiteralContext baseLiteral(int i) {
			return getRuleContext(BaseLiteralContext.class,i);
		}
		public BaseLiteralListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_baseLiteralList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterBaseLiteralList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitBaseLiteralList(this);
		}
	}

	public final BaseLiteralListContext baseLiteralList() throws RecognitionException {
		BaseLiteralListContext _localctx = new BaseLiteralListContext(_ctx, getState());
		enterRule(_localctx, 96, RULE_baseLiteralList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(814);
			baseLiteral();
			setState(819);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__2) {
				{
				{
				setState(815);
				match(T__2);
				setState(816);
				baseLiteral();
				}
				}
				setState(821);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ColumnReferenceContext extends ParserRuleContext {
		public AliasContext alias() {
			return getRuleContext(AliasContext.class,0);
		}
		public QualifierContext qualifier() {
			return getRuleContext(QualifierContext.class,0);
		}
		public ColumnReferenceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_columnReference; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterColumnReference(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitColumnReference(this);
		}
	}

	public final ColumnReferenceContext columnReference() throws RecognitionException {
		ColumnReferenceContext _localctx = new ColumnReferenceContext(_ctx, getState());
		enterRule(_localctx, 98, RULE_columnReference);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(823);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,96,_ctx) ) {
			case 1:
				{
				setState(822);
				qualifier();
				}
				break;
			}
			setState(825);
			alias();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class QualifierContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(EsqlParser.Identifier, 0); }
		public QualifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_qualifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterQualifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitQualifier(this);
		}
	}

	public final QualifierContext qualifier() throws RecognitionException {
		QualifierContext _localctx = new QualifierContext(_ctx, getState());
		enterRule(_localctx, 100, RULE_qualifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(827);
			match(Identifier);
			setState(828);
			match(T__22);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DefineContext extends ParserRuleContext {
		public CreateTableContext createTable() {
			return getRuleContext(CreateTableContext.class,0);
		}
		public AlterTableContext alterTable() {
			return getRuleContext(AlterTableContext.class,0);
		}
		public DropTableContext dropTable() {
			return getRuleContext(DropTableContext.class,0);
		}
		public DefineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_define; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterDefine(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitDefine(this);
		}
	}

	public final DefineContext define() throws RecognitionException {
		DefineContext _localctx = new DefineContext(_ctx, getState());
		enterRule(_localctx, 102, RULE_define);
		try {
			setState(833);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__73:
				enterOuterAlt(_localctx, 1);
				{
				setState(830);
				createTable();
				}
				break;
			case T__77:
				enterOuterAlt(_localctx, 2);
				{
				setState(831);
				alterTable();
				}
				break;
			case T__75:
				enterOuterAlt(_localctx, 3);
				{
				setState(832);
				dropTable();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CreateTableContext extends ParserRuleContext {
		public QualifiedNameContext qualifiedName() {
			return getRuleContext(QualifiedNameContext.class,0);
		}
		public TableDefinitionsContext tableDefinitions() {
			return getRuleContext(TableDefinitionsContext.class,0);
		}
		public DropUndefinedContext dropUndefined() {
			return getRuleContext(DropUndefinedContext.class,0);
		}
		public CreateTableContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_createTable; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterCreateTable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitCreateTable(this);
		}
	}

	public final CreateTableContext createTable() throws RecognitionException {
		CreateTableContext _localctx = new CreateTableContext(_ctx, getState());
		enterRule(_localctx, 104, RULE_createTable);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(835);
			match(T__73);
			setState(836);
			match(T__74);
			setState(837);
			qualifiedName();
			setState(839);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__75) {
				{
				setState(838);
				dropUndefined();
				}
			}

			setState(841);
			match(T__17);
			setState(842);
			tableDefinitions();
			setState(843);
			match(T__18);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DropUndefinedContext extends ParserRuleContext {
		public DropUndefinedContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dropUndefined; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterDropUndefined(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitDropUndefined(this);
		}
	}

	public final DropUndefinedContext dropUndefined() throws RecognitionException {
		DropUndefinedContext _localctx = new DropUndefinedContext(_ctx, getState());
		enterRule(_localctx, 106, RULE_dropUndefined);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(845);
			match(T__75);
			setState(846);
			match(T__76);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AlterTableContext extends ParserRuleContext {
		public QualifiedNameContext qualifiedName() {
			return getRuleContext(QualifiedNameContext.class,0);
		}
		public AlterTableActionsContext alterTableActions() {
			return getRuleContext(AlterTableActionsContext.class,0);
		}
		public AlterTableContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_alterTable; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterAlterTable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitAlterTable(this);
		}
	}

	public final AlterTableContext alterTable() throws RecognitionException {
		AlterTableContext _localctx = new AlterTableContext(_ctx, getState());
		enterRule(_localctx, 108, RULE_alterTable);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(848);
			match(T__77);
			setState(849);
			match(T__74);
			setState(850);
			qualifiedName();
			setState(851);
			alterTableActions();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AlterTableActionsContext extends ParserRuleContext {
		public List<AlterTableActionContext> alterTableAction() {
			return getRuleContexts(AlterTableActionContext.class);
		}
		public AlterTableActionContext alterTableAction(int i) {
			return getRuleContext(AlterTableActionContext.class,i);
		}
		public AlterTableActionsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_alterTableActions; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterAlterTableActions(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitAlterTableActions(this);
		}
	}

	public final AlterTableActionsContext alterTableActions() throws RecognitionException {
		AlterTableActionsContext _localctx = new AlterTableActionsContext(_ctx, getState());
		enterRule(_localctx, 110, RULE_alterTableActions);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(853);
			alterTableAction();
			setState(858);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__2) {
				{
				{
				setState(854);
				match(T__2);
				setState(855);
				alterTableAction();
				}
				}
				setState(860);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AlterTableActionContext extends ParserRuleContext {
		public AlterTableActionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_alterTableAction; }
	 
		public AlterTableActionContext() { }
		public void copyFrom(AlterTableActionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class RenameTableContext extends AlterTableActionContext {
		public TerminalNode Identifier() { return getToken(EsqlParser.Identifier, 0); }
		public RenameTableContext(AlterTableActionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterRenameTable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitRenameTable(this);
		}
	}
	public static class DropTableMetadataContext extends AlterTableActionContext {
		public DropTableMetadataContext(AlterTableActionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterDropTableMetadata(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitDropTableMetadata(this);
		}
	}
	public static class AddTableDefinitionContext extends AlterTableActionContext {
		public TableDefinitionContext tableDefinition() {
			return getRuleContext(TableDefinitionContext.class,0);
		}
		public AddTableDefinitionContext(AlterTableActionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterAddTableDefinition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitAddTableDefinition(this);
		}
	}
	public static class AlterColumnContext extends AlterTableActionContext {
		public TerminalNode Identifier() { return getToken(EsqlParser.Identifier, 0); }
		public AlterColumnDefinitionContext alterColumnDefinition() {
			return getRuleContext(AlterColumnDefinitionContext.class,0);
		}
		public AlterColumnContext(AlterTableActionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterAlterColumn(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitAlterColumn(this);
		}
	}
	public static class DropConstraintContext extends AlterTableActionContext {
		public TerminalNode Identifier() { return getToken(EsqlParser.Identifier, 0); }
		public DropConstraintContext(AlterTableActionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterDropConstraint(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitDropConstraint(this);
		}
	}
	public static class DropColumnContext extends AlterTableActionContext {
		public TerminalNode Identifier() { return getToken(EsqlParser.Identifier, 0); }
		public DropColumnContext(AlterTableActionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterDropColumn(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitDropColumn(this);
		}
	}

	public final AlterTableActionContext alterTableAction() throws RecognitionException {
		AlterTableActionContext _localctx = new AlterTableActionContext(_ctx, getState());
		enterRule(_localctx, 112, RULE_alterTableAction);
		try {
			setState(878);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,100,_ctx) ) {
			case 1:
				_localctx = new RenameTableContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(861);
				match(T__78);
				setState(862);
				match(T__79);
				setState(863);
				match(Identifier);
				}
				break;
			case 2:
				_localctx = new AddTableDefinitionContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(864);
				match(T__80);
				setState(865);
				tableDefinition();
				}
				break;
			case 3:
				_localctx = new AlterColumnContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(866);
				match(T__77);
				setState(867);
				match(T__81);
				setState(868);
				match(Identifier);
				setState(869);
				alterColumnDefinition();
				}
				break;
			case 4:
				_localctx = new DropColumnContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(870);
				match(T__75);
				setState(871);
				match(T__81);
				setState(872);
				match(Identifier);
				}
				break;
			case 5:
				_localctx = new DropConstraintContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(873);
				match(T__75);
				setState(874);
				match(T__82);
				setState(875);
				match(Identifier);
				}
				break;
			case 6:
				_localctx = new DropTableMetadataContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(876);
				match(T__75);
				setState(877);
				match(T__83);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DropTableContext extends ParserRuleContext {
		public QualifiedNameContext qualifiedName() {
			return getRuleContext(QualifiedNameContext.class,0);
		}
		public DropTableContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dropTable; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterDropTable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitDropTable(this);
		}
	}

	public final DropTableContext dropTable() throws RecognitionException {
		DropTableContext _localctx = new DropTableContext(_ctx, getState());
		enterRule(_localctx, 114, RULE_dropTable);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(880);
			match(T__75);
			setState(881);
			match(T__74);
			setState(882);
			qualifiedName();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TableDefinitionsContext extends ParserRuleContext {
		public List<TableDefinitionContext> tableDefinition() {
			return getRuleContexts(TableDefinitionContext.class);
		}
		public TableDefinitionContext tableDefinition(int i) {
			return getRuleContext(TableDefinitionContext.class,i);
		}
		public TableDefinitionsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tableDefinitions; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterTableDefinitions(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitTableDefinitions(this);
		}
	}

	public final TableDefinitionsContext tableDefinitions() throws RecognitionException {
		TableDefinitionsContext _localctx = new TableDefinitionsContext(_ctx, getState());
		enterRule(_localctx, 116, RULE_tableDefinitions);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(884);
			tableDefinition();
			setState(887); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(885);
				match(T__2);
				setState(886);
				tableDefinition();
				}
				}
				setState(889); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==T__2 );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TableDefinitionContext extends ParserRuleContext {
		public ColumnDefinitionContext columnDefinition() {
			return getRuleContext(ColumnDefinitionContext.class,0);
		}
		public DerivedColumnDefinitionContext derivedColumnDefinition() {
			return getRuleContext(DerivedColumnDefinitionContext.class,0);
		}
		public ConstraintDefinitionContext constraintDefinition() {
			return getRuleContext(ConstraintDefinitionContext.class,0);
		}
		public MetadataContext metadata() {
			return getRuleContext(MetadataContext.class,0);
		}
		public TableDefinitionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tableDefinition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterTableDefinition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitTableDefinition(this);
		}
	}

	public final TableDefinitionContext tableDefinition() throws RecognitionException {
		TableDefinitionContext _localctx = new TableDefinitionContext(_ctx, getState());
		enterRule(_localctx, 118, RULE_tableDefinition);
		try {
			setState(895);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,102,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(891);
				columnDefinition();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(892);
				derivedColumnDefinition();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(893);
				constraintDefinition();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(894);
				metadata();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ColumnDefinitionContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(EsqlParser.Identifier, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode Not() { return getToken(EsqlParser.Not, 0); }
		public TerminalNode NullLiteral() { return getToken(EsqlParser.NullLiteral, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public MetadataContext metadata() {
			return getRuleContext(MetadataContext.class,0);
		}
		public ColumnDefinitionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_columnDefinition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterColumnDefinition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitColumnDefinition(this);
		}
	}

	public final ColumnDefinitionContext columnDefinition() throws RecognitionException {
		ColumnDefinitionContext _localctx = new ColumnDefinitionContext(_ctx, getState());
		enterRule(_localctx, 120, RULE_columnDefinition);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(897);
			match(Identifier);
			setState(898);
			type();
			setState(901);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Not) {
				{
				setState(899);
				match(Not);
				setState(900);
				match(NullLiteral);
				}
			}

			setState(905);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__41) {
				{
				setState(903);
				match(T__41);
				setState(904);
				expr(0);
				}
			}

			setState(908);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__11) {
				{
				setState(907);
				metadata();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AlterColumnDefinitionContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(EsqlParser.Identifier, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public AlterNullContext alterNull() {
			return getRuleContext(AlterNullContext.class,0);
		}
		public AlterDefaultContext alterDefault() {
			return getRuleContext(AlterDefaultContext.class,0);
		}
		public MetadataContext metadata() {
			return getRuleContext(MetadataContext.class,0);
		}
		public AlterColumnDefinitionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_alterColumnDefinition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterAlterColumnDefinition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitAlterColumnDefinition(this);
		}
	}

	public final AlterColumnDefinitionContext alterColumnDefinition() throws RecognitionException {
		AlterColumnDefinitionContext _localctx = new AlterColumnDefinitionContext(_ctx, getState());
		enterRule(_localctx, 122, RULE_alterColumnDefinition);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(911);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Identifier) {
				{
				setState(910);
				match(Identifier);
				}
			}

			setState(914);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==BaseType) {
				{
				setState(913);
				type();
				}
			}

			setState(917);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Not || _la==NullLiteral) {
				{
				setState(916);
				alterNull();
				}
			}

			setState(920);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__41 || _la==T__84) {
				{
				setState(919);
				alterDefault();
				}
			}

			setState(923);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__11) {
				{
				setState(922);
				metadata();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AlterNullContext extends ParserRuleContext {
		public TerminalNode Not() { return getToken(EsqlParser.Not, 0); }
		public TerminalNode NullLiteral() { return getToken(EsqlParser.NullLiteral, 0); }
		public AlterNullContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_alterNull; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterAlterNull(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitAlterNull(this);
		}
	}

	public final AlterNullContext alterNull() throws RecognitionException {
		AlterNullContext _localctx = new AlterNullContext(_ctx, getState());
		enterRule(_localctx, 124, RULE_alterNull);
		try {
			setState(928);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Not:
				enterOuterAlt(_localctx, 1);
				{
				setState(925);
				match(Not);
				setState(926);
				match(NullLiteral);
				}
				break;
			case NullLiteral:
				enterOuterAlt(_localctx, 2);
				{
				setState(927);
				match(NullLiteral);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AlterDefaultContext extends ParserRuleContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public AlterDefaultContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_alterDefault; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterAlterDefault(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitAlterDefault(this);
		}
	}

	public final AlterDefaultContext alterDefault() throws RecognitionException {
		AlterDefaultContext _localctx = new AlterDefaultContext(_ctx, getState());
		enterRule(_localctx, 126, RULE_alterDefault);
		try {
			setState(934);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__41:
				enterOuterAlt(_localctx, 1);
				{
				setState(930);
				match(T__41);
				setState(931);
				expr(0);
				}
				break;
			case T__84:
				enterOuterAlt(_localctx, 2);
				{
				setState(932);
				match(T__84);
				setState(933);
				match(T__41);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DerivedColumnDefinitionContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(EsqlParser.Identifier, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public MetadataContext metadata() {
			return getRuleContext(MetadataContext.class,0);
		}
		public DerivedColumnDefinitionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_derivedColumnDefinition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterDerivedColumnDefinition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitDerivedColumnDefinition(this);
		}
	}

	public final DerivedColumnDefinitionContext derivedColumnDefinition() throws RecognitionException {
		DerivedColumnDefinitionContext _localctx = new DerivedColumnDefinitionContext(_ctx, getState());
		enterRule(_localctx, 128, RULE_derivedColumnDefinition);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(936);
			match(Identifier);
			setState(937);
			match(T__44);
			setState(938);
			expr(0);
			setState(940);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__11) {
				{
				setState(939);
				metadata();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ConstraintDefinitionContext extends ParserRuleContext {
		public ConstraintDefinitionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constraintDefinition; }
	 
		public ConstraintDefinitionContext() { }
		public void copyFrom(ConstraintDefinitionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ForeignKeyConstraintContext extends ConstraintDefinitionContext {
		public NamesContext from;
		public NamesContext to;
		public Token forwardcost;
		public Token reversecost;
		public QualifiedNameContext qualifiedName() {
			return getRuleContext(QualifiedNameContext.class,0);
		}
		public List<NamesContext> names() {
			return getRuleContexts(NamesContext.class);
		}
		public NamesContext names(int i) {
			return getRuleContext(NamesContext.class,i);
		}
		public ConstraintNameContext constraintName() {
			return getRuleContext(ConstraintNameContext.class,0);
		}
		public List<OnUpdateContext> onUpdate() {
			return getRuleContexts(OnUpdateContext.class);
		}
		public OnUpdateContext onUpdate(int i) {
			return getRuleContext(OnUpdateContext.class,i);
		}
		public List<OnDeleteContext> onDelete() {
			return getRuleContexts(OnDeleteContext.class);
		}
		public OnDeleteContext onDelete(int i) {
			return getRuleContext(OnDeleteContext.class,i);
		}
		public List<TerminalNode> IntegerLiteral() { return getTokens(EsqlParser.IntegerLiteral); }
		public TerminalNode IntegerLiteral(int i) {
			return getToken(EsqlParser.IntegerLiteral, i);
		}
		public ForeignKeyConstraintContext(ConstraintDefinitionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterForeignKeyConstraint(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitForeignKeyConstraint(this);
		}
	}
	public static class UniqueConstraintContext extends ConstraintDefinitionContext {
		public NamesContext names() {
			return getRuleContext(NamesContext.class,0);
		}
		public ConstraintNameContext constraintName() {
			return getRuleContext(ConstraintNameContext.class,0);
		}
		public UniqueConstraintContext(ConstraintDefinitionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterUniqueConstraint(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitUniqueConstraint(this);
		}
	}
	public static class PrimaryKeyConstraintContext extends ConstraintDefinitionContext {
		public NamesContext names() {
			return getRuleContext(NamesContext.class,0);
		}
		public ConstraintNameContext constraintName() {
			return getRuleContext(ConstraintNameContext.class,0);
		}
		public PrimaryKeyConstraintContext(ConstraintDefinitionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterPrimaryKeyConstraint(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitPrimaryKeyConstraint(this);
		}
	}
	public static class CheckConstraintContext extends ConstraintDefinitionContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public ConstraintNameContext constraintName() {
			return getRuleContext(ConstraintNameContext.class,0);
		}
		public CheckConstraintContext(ConstraintDefinitionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterCheckConstraint(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitCheckConstraint(this);
		}
	}

	public final ConstraintDefinitionContext constraintDefinition() throws RecognitionException {
		ConstraintDefinitionContext _localctx = new ConstraintDefinitionContext(_ctx, getState());
		enterRule(_localctx, 130, RULE_constraintDefinition);
		int _la;
		try {
			setState(988);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,122,_ctx) ) {
			case 1:
				_localctx = new UniqueConstraintContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(943);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__82) {
					{
					setState(942);
					constraintName();
					}
				}

				setState(945);
				match(T__85);
				setState(946);
				names();
				}
				break;
			case 2:
				_localctx = new PrimaryKeyConstraintContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(948);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__82) {
					{
					setState(947);
					constraintName();
					}
				}

				setState(950);
				match(T__86);
				setState(951);
				match(T__87);
				setState(952);
				names();
				}
				break;
			case 3:
				_localctx = new ForeignKeyConstraintContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(954);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__82) {
					{
					setState(953);
					constraintName();
					}
				}

				setState(956);
				match(T__88);
				setState(957);
				match(T__87);
				setState(958);
				((ForeignKeyConstraintContext)_localctx).from = names();
				setState(959);
				match(T__89);
				setState(960);
				qualifiedName();
				setState(961);
				((ForeignKeyConstraintContext)_localctx).to = names();
				setState(970);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__90) {
					{
					setState(962);
					match(T__90);
					setState(963);
					match(T__17);
					setState(964);
					((ForeignKeyConstraintContext)_localctx).forwardcost = match(IntegerLiteral);
					setState(967);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==T__2) {
						{
						setState(965);
						match(T__2);
						setState(966);
						((ForeignKeyConstraintContext)_localctx).reversecost = match(IntegerLiteral);
						}
					}

					setState(969);
					match(T__18);
					}
				}

				setState(974);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,119,_ctx) ) {
				case 1:
					{
					setState(972);
					onUpdate();
					}
					break;
				case 2:
					{
					setState(973);
					onDelete();
					}
					break;
				}
				setState(978);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,120,_ctx) ) {
				case 1:
					{
					setState(976);
					onUpdate();
					}
					break;
				case 2:
					{
					setState(977);
					onDelete();
					}
					break;
				}
				}
				break;
			case 4:
				_localctx = new CheckConstraintContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(981);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__82) {
					{
					setState(980);
					constraintName();
					}
				}

				setState(983);
				match(T__91);
				setState(984);
				match(T__17);
				setState(985);
				expr(0);
				setState(986);
				match(T__18);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ConstraintNameContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(EsqlParser.Identifier, 0); }
		public ConstraintNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constraintName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterConstraintName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitConstraintName(this);
		}
	}

	public final ConstraintNameContext constraintName() throws RecognitionException {
		ConstraintNameContext _localctx = new ConstraintNameContext(_ctx, getState());
		enterRule(_localctx, 132, RULE_constraintName);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(990);
			match(T__82);
			setState(991);
			match(Identifier);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class OnUpdateContext extends ParserRuleContext {
		public ForeignKeyActionContext foreignKeyAction() {
			return getRuleContext(ForeignKeyActionContext.class,0);
		}
		public OnUpdateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_onUpdate; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterOnUpdate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitOnUpdate(this);
		}
	}

	public final OnUpdateContext onUpdate() throws RecognitionException {
		OnUpdateContext _localctx = new OnUpdateContext(_ctx, getState());
		enterRule(_localctx, 134, RULE_onUpdate);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(993);
			match(T__16);
			setState(994);
			match(T__42);
			setState(995);
			foreignKeyAction();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class OnDeleteContext extends ParserRuleContext {
		public ForeignKeyActionContext foreignKeyAction() {
			return getRuleContext(ForeignKeyActionContext.class,0);
		}
		public OnDeleteContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_onDelete; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterOnDelete(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitOnDelete(this);
		}
	}

	public final OnDeleteContext onDelete() throws RecognitionException {
		OnDeleteContext _localctx = new OnDeleteContext(_ctx, getState());
		enterRule(_localctx, 136, RULE_onDelete);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(997);
			match(T__16);
			setState(998);
			match(T__45);
			setState(999);
			foreignKeyAction();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ForeignKeyActionContext extends ParserRuleContext {
		public TerminalNode NullLiteral() { return getToken(EsqlParser.NullLiteral, 0); }
		public ForeignKeyActionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_foreignKeyAction; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterForeignKeyAction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitForeignKeyAction(this);
		}
	}

	public final ForeignKeyActionContext foreignKeyAction() throws RecognitionException {
		ForeignKeyActionContext _localctx = new ForeignKeyActionContext(_ctx, getState());
		enterRule(_localctx, 138, RULE_foreignKeyAction);
		try {
			setState(1007);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,123,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1001);
				match(T__92);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1002);
				match(T__93);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1003);
				match(T__43);
				setState(1004);
				match(NullLiteral);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1005);
				match(T__43);
				setState(1006);
				match(T__41);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TypeContext extends ParserRuleContext {
		public TypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type; }
	 
		public TypeContext() { }
		public void copyFrom(TypeContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ArrayContext extends TypeContext {
		public ArrayTypeContext arrayType() {
			return getRuleContext(ArrayTypeContext.class,0);
		}
		public ArrayContext(TypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterArray(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitArray(this);
		}
	}
	public static class BaseContext extends TypeContext {
		public TerminalNode BaseType() { return getToken(EsqlParser.BaseType, 0); }
		public BaseContext(TypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterBase(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitBase(this);
		}
	}

	public final TypeContext type() throws RecognitionException {
		TypeContext _localctx = new TypeContext(_ctx, getState());
		enterRule(_localctx, 140, RULE_type);
		try {
			setState(1011);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,124,_ctx) ) {
			case 1:
				_localctx = new BaseContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(1009);
				match(BaseType);
				}
				break;
			case 2:
				_localctx = new ArrayContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(1010);
				arrayType();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ArrayTypeContext extends ParserRuleContext {
		public TerminalNode BaseType() { return getToken(EsqlParser.BaseType, 0); }
		public ArrayTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arrayType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterArrayType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitArrayType(this);
		}
	}

	public final ArrayTypeContext arrayType() throws RecognitionException {
		ArrayTypeContext _localctx = new ArrayTypeContext(_ctx, getState());
		enterRule(_localctx, 142, RULE_arrayType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1013);
			match(BaseType);
			setState(1014);
			match(T__71);
			setState(1015);
			match(T__72);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 5:
			return select_sempred((SelectContext)_localctx, predIndex);
		case 16:
			return tableExpr_sempred((TableExprContext)_localctx, predIndex);
		case 37:
			return expr_sempred((ExprContext)_localctx, predIndex);
		case 38:
			return simpleExpr_sempred((SimpleExprContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean select_sempred(SelectContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 2);
		}
		return true;
	}
	private boolean tableExpr_sempred(TableExprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 1:
			return precpred(_ctx, 2);
		case 2:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean expr_sempred(ExprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 3:
			return precpred(_ctx, 25);
		case 4:
			return precpred(_ctx, 23);
		case 5:
			return precpred(_ctx, 22);
		case 6:
			return precpred(_ctx, 21);
		case 7:
			return precpred(_ctx, 13);
		case 8:
			return precpred(_ctx, 12);
		case 9:
			return precpred(_ctx, 9);
		case 10:
			return precpred(_ctx, 8);
		case 11:
			return precpred(_ctx, 7);
		case 12:
			return precpred(_ctx, 4);
		case 13:
			return precpred(_ctx, 3);
		case 14:
			return precpred(_ctx, 26);
		case 15:
			return precpred(_ctx, 11);
		case 16:
			return precpred(_ctx, 10);
		case 17:
			return precpred(_ctx, 6);
		case 18:
			return precpred(_ctx, 2);
		}
		return true;
	}
	private boolean simpleExpr_sempred(SimpleExprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 19:
			return precpred(_ctx, 9);
		case 20:
			return precpred(_ctx, 7);
		case 21:
			return precpred(_ctx, 6);
		case 22:
			return precpred(_ctx, 5);
		case 23:
			return precpred(_ctx, 10);
		case 24:
			return precpred(_ctx, 1);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3p\u03fc\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\4I"+
		"\tI\3\2\3\2\3\2\7\2\u0096\n\2\f\2\16\2\u0099\13\2\3\2\5\2\u009c\n\2\3"+
		"\3\3\3\3\4\3\4\3\4\3\4\5\4\u00a4\n\4\3\5\3\5\3\5\5\5\u00a9\n\5\3\6\3\6"+
		"\5\6\u00ad\n\6\3\7\3\7\3\7\3\7\5\7\u00b3\n\7\5\7\u00b5\n\7\3\7\5\7\u00b8"+
		"\n\7\3\7\5\7\u00bb\n\7\3\7\3\7\3\7\5\7\u00c0\n\7\3\7\3\7\5\7\u00c4\n\7"+
		"\3\7\3\7\3\7\5\7\u00c9\n\7\3\7\3\7\3\7\5\7\u00ce\n\7\3\7\3\7\5\7\u00d2"+
		"\n\7\3\7\3\7\5\7\u00d6\n\7\3\7\3\7\5\7\u00da\n\7\3\7\5\7\u00dd\n\7\3\7"+
		"\3\7\3\7\3\7\7\7\u00e3\n\7\f\7\16\7\u00e6\13\7\3\b\3\b\3\b\3\b\3\t\3\t"+
		"\3\t\7\t\u00ef\n\t\f\t\16\t\u00f2\13\t\3\n\3\n\3\n\3\n\3\13\3\13\3\13"+
		"\3\13\3\13\3\13\3\13\5\13\u00ff\n\13\5\13\u0101\n\13\3\f\3\f\3\r\3\r\3"+
		"\r\7\r\u0108\n\r\f\r\16\r\u010b\13\r\3\16\3\16\3\16\5\16\u0110\n\16\3"+
		"\16\3\16\5\16\u0114\n\16\3\16\5\16\u0117\n\16\3\16\5\16\u011a\n\16\3\17"+
		"\5\17\u011d\n\17\3\17\3\17\3\17\7\17\u0122\n\17\f\17\16\17\u0125\13\17"+
		"\3\20\3\20\5\20\u0129\n\20\3\21\3\21\3\21\7\21\u012e\n\21\f\21\16\21\u0131"+
		"\13\21\3\22\3\22\3\22\3\22\5\22\u0137\n\22\3\22\3\22\3\22\3\22\3\22\3"+
		"\22\3\22\3\22\3\22\5\22\u0142\n\22\3\22\3\22\3\22\3\22\3\22\3\22\5\22"+
		"\u014a\n\22\3\22\3\22\3\22\3\22\3\22\5\22\u0151\n\22\3\22\3\22\3\22\3"+
		"\22\3\22\7\22\u0158\n\22\f\22\16\22\u015b\13\22\3\23\3\23\3\23\3\23\7"+
		"\23\u0161\n\23\f\23\16\23\u0164\13\23\3\23\3\23\3\24\3\24\5\24\u016a\n"+
		"\24\3\25\3\25\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\5"+
		"\26\u0179\n\26\3\27\3\27\3\27\7\27\u017e\n\27\f\27\16\27\u0181\13\27\3"+
		"\30\3\30\5\30\u0185\n\30\3\31\3\31\3\32\3\32\3\32\3\32\3\32\5\32\u018e"+
		"\n\32\3\33\3\33\5\33\u0192\n\33\3\33\3\33\3\33\3\34\3\34\3\34\7\34\u019a"+
		"\n\34\f\34\16\34\u019d\13\34\3\35\3\35\5\35\u01a1\n\35\3\35\3\35\3\35"+
		"\3\35\3\36\3\36\3\36\3\36\7\36\u01ab\n\36\f\36\16\36\u01ae\13\36\3\36"+
		"\3\36\3\37\3\37\3\37\3\37\3\37\5\37\u01b7\n\37\3\37\3\37\5\37\u01bb\n"+
		"\37\3\37\3\37\3\37\3\37\5\37\u01c1\n\37\3\37\3\37\5\37\u01c5\n\37\3\37"+
		"\5\37\u01c8\n\37\3 \3 \3 \7 \u01cd\n \f \16 \u01d0\13 \3!\3!\3!\3!\3\""+
		"\3\"\3\"\3#\3#\3#\3#\3#\3#\3#\3#\5#\u01e1\n#\3#\3#\5#\u01e5\n#\3#\5#\u01e8"+
		"\n#\3$\3$\3$\7$\u01ed\n$\f$\16$\u01f0\13$\3%\3%\3%\3%\3&\3&\3&\3&\3&\3"+
		"&\5&\u01fc\n&\3&\3&\5&\u0200\n&\3&\5&\u0203\n&\3\'\3\'\3\'\3\'\3\'\3\'"+
		"\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3"+
		"\'\3\'\3\'\3\'\3\'\3\'\3\'\5\'\u0223\n\'\3\'\5\'\u0226\n\'\3\'\3\'\5\'"+
		"\u022a\n\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\5\'\u0233\n\'\3\'\3\'\3\'\3\'\3"+
		"\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'"+
		"\3\'\3\'\5\'\u024d\n\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\5\'\u0256\n\'\3\'\3"+
		"\'\3\'\3\'\5\'\u025c\n\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\6"+
		"\'\u0269\n\'\r\'\16\'\u026a\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\5\'\u0276"+
		"\n\'\3\'\3\'\3\'\3\'\5\'\u027c\n\'\3\'\3\'\3\'\3\'\3\'\5\'\u0283\n\'\3"+
		"\'\3\'\3\'\3\'\3\'\3\'\3\'\6\'\u028c\n\'\r\'\16\'\u028d\7\'\u0290\n\'"+
		"\f\'\16\'\u0293\13\'\3(\3(\3(\3(\3(\3(\3(\3(\3(\3(\3(\3(\3(\3(\3(\3(\3"+
		"(\5(\u02a6\n(\3(\5(\u02a9\n(\3(\3(\5(\u02ad\n(\3(\5(\u02b0\n(\3(\3(\3"+
		"(\3(\3(\3(\3(\3(\3(\3(\3(\3(\3(\3(\3(\6(\u02c1\n(\r(\16(\u02c2\3(\3(\3"+
		"(\3(\3(\3(\6(\u02cb\n(\r(\16(\u02cc\7(\u02cf\n(\f(\16(\u02d2\13(\3)\3"+
		")\5)\u02d6\n)\3)\3)\3)\5)\u02db\n)\3)\3)\3)\3)\3)\5)\u02e2\n)\3)\3)\3"+
		")\5)\u02e7\n)\3)\3)\5)\u02eb\n)\3)\3)\3*\3*\3*\5*\u02f2\n*\3*\3*\3*\5"+
		"*\u02f7\n*\3*\3*\3+\3+\3+\3+\3,\3,\3-\3-\3.\3.\3.\7.\u0306\n.\f.\16.\u0309"+
		"\13.\3/\3/\3/\3/\3/\5/\u0310\n/\3/\3/\3/\5/\u0315\n/\3/\3/\3/\5/\u031a"+
		"\n/\3/\5/\u031d\n/\3\60\3\60\3\60\3\60\3\60\3\60\3\60\3\60\5\60\u0327"+
		"\n\60\3\61\3\61\3\61\7\61\u032c\n\61\f\61\16\61\u032f\13\61\3\62\3\62"+
		"\3\62\7\62\u0334\n\62\f\62\16\62\u0337\13\62\3\63\5\63\u033a\n\63\3\63"+
		"\3\63\3\64\3\64\3\64\3\65\3\65\3\65\5\65\u0344\n\65\3\66\3\66\3\66\3\66"+
		"\5\66\u034a\n\66\3\66\3\66\3\66\3\66\3\67\3\67\3\67\38\38\38\38\38\39"+
		"\39\39\79\u035b\n9\f9\169\u035e\139\3:\3:\3:\3:\3:\3:\3:\3:\3:\3:\3:\3"+
		":\3:\3:\3:\3:\3:\5:\u0371\n:\3;\3;\3;\3;\3<\3<\3<\6<\u037a\n<\r<\16<\u037b"+
		"\3=\3=\3=\3=\5=\u0382\n=\3>\3>\3>\3>\5>\u0388\n>\3>\3>\5>\u038c\n>\3>"+
		"\5>\u038f\n>\3?\5?\u0392\n?\3?\5?\u0395\n?\3?\5?\u0398\n?\3?\5?\u039b"+
		"\n?\3?\5?\u039e\n?\3@\3@\3@\5@\u03a3\n@\3A\3A\3A\3A\5A\u03a9\nA\3B\3B"+
		"\3B\3B\5B\u03af\nB\3C\5C\u03b2\nC\3C\3C\3C\5C\u03b7\nC\3C\3C\3C\3C\5C"+
		"\u03bd\nC\3C\3C\3C\3C\3C\3C\3C\3C\3C\3C\3C\5C\u03ca\nC\3C\5C\u03cd\nC"+
		"\3C\3C\5C\u03d1\nC\3C\3C\5C\u03d5\nC\3C\5C\u03d8\nC\3C\3C\3C\3C\3C\5C"+
		"\u03df\nC\3D\3D\3D\3E\3E\3E\3E\3F\3F\3F\3F\3G\3G\3G\3G\3G\3G\5G\u03f2"+
		"\nG\3H\3H\5H\u03f6\nH\3I\3I\3I\3I\3I\2\6\f\"LNJ\2\4\6\b\n\f\16\20\22\24"+
		"\26\30\32\34\36 \"$&(*,.\60\62\64\668:<>@BDFHJLNPRTVXZ\\^`bdfhjlnprtv"+
		"xz|~\u0080\u0082\u0084\u0086\u0088\u008a\u008c\u008e\u0090\2\b\3\2\34"+
		"\36\3\2!\"\4\2\27\3088\4\2\66\6699\5\2//\62\63GI\4\2\62\63HI\2\u0472\2"+
		"\u0092\3\2\2\2\4\u009d\3\2\2\2\6\u00a3\3\2\2\2\b\u00a8\3\2\2\2\n\u00ac"+
		"\3\2\2\2\f\u00dc\3\2\2\2\16\u00e7\3\2\2\2\20\u00eb\3\2\2\2\22\u00f3\3"+
		"\2\2\2\24\u0100\3\2\2\2\26\u0102\3\2\2\2\30\u0104\3\2\2\2\32\u0119\3\2"+
		"\2\2\34\u011c\3\2\2\2\36\u0128\3\2\2\2 \u012a\3\2\2\2\"\u0149\3\2\2\2"+
		"$\u015c\3\2\2\2&\u0167\3\2\2\2(\u016b\3\2\2\2*\u0178\3\2\2\2,\u017a\3"+
		"\2\2\2.\u0182\3\2\2\2\60\u0186\3\2\2\2\62\u018d\3\2\2\2\64\u018f\3\2\2"+
		"\2\66\u0196\3\2\2\28\u019e\3\2\2\2:\u01a6\3\2\2\2<\u01b1\3\2\2\2>\u01c9"+
		"\3\2\2\2@\u01d1\3\2\2\2B\u01d5\3\2\2\2D\u01d8\3\2\2\2F\u01e9\3\2\2\2H"+
		"\u01f1\3\2\2\2J\u01f5\3\2\2\2L\u0232\3\2\2\2N\u02af\3\2\2\2P\u02d3\3\2"+
		"\2\2R\u02ee\3\2\2\2T\u02fa\3\2\2\2V\u02fe\3\2\2\2X\u0300\3\2\2\2Z\u0302"+
		"\3\2\2\2\\\u031c\3\2\2\2^\u0326\3\2\2\2`\u0328\3\2\2\2b\u0330\3\2\2\2"+
		"d\u0339\3\2\2\2f\u033d\3\2\2\2h\u0343\3\2\2\2j\u0345\3\2\2\2l\u034f\3"+
		"\2\2\2n\u0352\3\2\2\2p\u0357\3\2\2\2r\u0370\3\2\2\2t\u0372\3\2\2\2v\u0376"+
		"\3\2\2\2x\u0381\3\2\2\2z\u0383\3\2\2\2|\u0391\3\2\2\2~\u03a2\3\2\2\2\u0080"+
		"\u03a8\3\2\2\2\u0082\u03aa\3\2\2\2\u0084\u03de\3\2\2\2\u0086\u03e0\3\2"+
		"\2\2\u0088\u03e3\3\2\2\2\u008a\u03e7\3\2\2\2\u008c\u03f1\3\2\2\2\u008e"+
		"\u03f5\3\2\2\2\u0090\u03f7\3\2\2\2\u0092\u0097\5\6\4\2\u0093\u0094\7\3"+
		"\2\2\u0094\u0096\5\6\4\2\u0095\u0093\3\2\2\2\u0096\u0099\3\2\2\2\u0097"+
		"\u0095\3\2\2\2\u0097\u0098\3\2\2\2\u0098\u009b\3\2\2\2\u0099\u0097\3\2"+
		"\2\2\u009a\u009c\7\3\2\2\u009b\u009a\3\2\2\2\u009b\u009c\3\2\2\2\u009c"+
		"\3\3\2\2\2\u009d\u009e\7\3\2\2\u009e\5\3\2\2\2\u009f\u00a4\5\f\7\2\u00a0"+
		"\u00a4\5\b\5\2\u00a1\u00a4\5h\65\2\u00a2\u00a4\5\4\3\2\u00a3\u009f\3\2"+
		"\2\2\u00a3\u00a0\3\2\2\2\u00a3\u00a1\3\2\2\2\u00a3\u00a2\3\2\2\2\u00a4"+
		"\7\3\2\2\2\u00a5\u00a9\5D#\2\u00a6\u00a9\5<\37\2\u00a7\u00a9\5J&\2\u00a8"+
		"\u00a5\3\2\2\2\u00a8\u00a6\3\2\2\2\u00a8\u00a7\3\2\2\2\u00a9\t\3\2\2\2"+
		"\u00aa\u00ad\5\f\7\2\u00ab\u00ad\5\b\5\2\u00ac\u00aa\3\2\2\2\u00ac\u00ab"+
		"\3\2\2\2\u00ad\13\3\2\2\2\u00ae\u00af\b\7\1\2\u00af\u00b4\7\4\2\2\u00b0"+
		"\u00b2\5\16\b\2\u00b1\u00b3\7\5\2\2\u00b2\u00b1\3\2\2\2\u00b2\u00b3\3"+
		"\2\2\2\u00b3\u00b5\3\2\2\2\u00b4\u00b0\3\2\2\2\u00b4\u00b5\3\2\2\2\u00b5"+
		"\u00b7\3\2\2\2\u00b6\u00b8\5\24\13\2\u00b7\u00b6\3\2\2\2\u00b7\u00b8\3"+
		"\2\2\2\u00b8\u00ba\3\2\2\2\u00b9\u00bb\5\26\f\2\u00ba\u00b9\3\2\2\2\u00ba"+
		"\u00bb\3\2\2\2\u00bb\u00bc\3\2\2\2\u00bc\u00bf\5\30\r\2\u00bd\u00be\7"+
		"\6\2\2\u00be\u00c0\5\"\22\2\u00bf\u00bd\3\2\2\2\u00bf\u00c0\3\2\2\2\u00c0"+
		"\u00c3\3\2\2\2\u00c1\u00c2\7\7\2\2\u00c2\u00c4\5L\'\2\u00c3\u00c1\3\2"+
		"\2\2\u00c3\u00c4\3\2\2\2\u00c4\u00c8\3\2\2\2\u00c5\u00c6\7\b\2\2\u00c6"+
		"\u00c7\7\t\2\2\u00c7\u00c9\5,\27\2\u00c8\u00c5\3\2\2\2\u00c8\u00c9\3\2"+
		"\2\2\u00c9\u00cd\3\2\2\2\u00ca\u00cb\7\n\2\2\u00cb\u00cc\7\t\2\2\u00cc"+
		"\u00ce\5*\26\2\u00cd\u00ca\3\2\2\2\u00cd\u00ce\3\2\2\2\u00ce\u00d1\3\2"+
		"\2\2\u00cf\u00d0\7\13\2\2\u00d0\u00d2\5L\'\2\u00d1\u00cf\3\2\2\2\u00d1"+
		"\u00d2\3\2\2\2\u00d2\u00d5\3\2\2\2\u00d3\u00d4\7\f\2\2\u00d4\u00d6\5L"+
		"\'\2\u00d5\u00d3\3\2\2\2\u00d5\u00d6\3\2\2\2\u00d6\u00d9\3\2\2\2\u00d7"+
		"\u00d8\7\r\2\2\u00d8\u00da\5L\'\2\u00d9\u00d7\3\2\2\2\u00d9\u00da\3\2"+
		"\2\2\u00da\u00dd\3\2\2\2\u00db\u00dd\5\64\33\2\u00dc\u00ae\3\2\2\2\u00dc"+
		"\u00db\3\2\2\2\u00dd\u00e4\3\2\2\2\u00de\u00df\f\4\2\2\u00df\u00e0\5\62"+
		"\32\2\u00e0\u00e1\5\f\7\5\u00e1\u00e3\3\2\2\2\u00e2\u00de\3\2\2\2\u00e3"+
		"\u00e6\3\2\2\2\u00e4\u00e2\3\2\2\2\u00e4\u00e5\3\2\2\2\u00e5\r\3\2\2\2"+
		"\u00e6\u00e4\3\2\2\2\u00e7\u00e8\7\16\2\2\u00e8\u00e9\5\20\t\2\u00e9\u00ea"+
		"\7\17\2\2\u00ea\17\3\2\2\2\u00eb\u00f0\5\22\n\2\u00ec\u00ed\7\5\2\2\u00ed"+
		"\u00ef\5\22\n\2\u00ee\u00ec\3\2\2\2\u00ef\u00f2\3\2\2\2\u00f0\u00ee\3"+
		"\2\2\2\u00f0\u00f1\3\2\2\2\u00f1\21\3\2\2\2\u00f2\u00f0\3\2\2\2\u00f3"+
		"\u00f4\7n\2\2\u00f4\u00f5\7\20\2\2\u00f5\u00f6\5L\'\2\u00f6\23\3\2\2\2"+
		"\u00f7\u0101\7\21\2\2\u00f8\u00fe\7\22\2\2\u00f9\u00fa\7\23\2\2\u00fa"+
		"\u00fb\7\24\2\2\u00fb\u00fc\5Z.\2\u00fc\u00fd\7\25\2\2\u00fd\u00ff\3\2"+
		"\2\2\u00fe\u00f9\3\2\2\2\u00fe\u00ff\3\2\2\2\u00ff\u0101\3\2\2\2\u0100"+
		"\u00f7\3\2\2\2\u0100\u00f8\3\2\2\2\u0101\25\3\2\2\2\u0102\u0103\7\26\2"+
		"\2\u0103\27\3\2\2\2\u0104\u0109\5\32\16\2\u0105\u0106\7\5\2\2\u0106\u0108"+
		"\5\32\16\2\u0107\u0105\3\2\2\2\u0108\u010b\3\2\2\2\u0109\u0107\3\2\2\2"+
		"\u0109\u010a\3\2\2\2\u010a\31\3\2\2\2\u010b\u0109\3\2\2\2\u010c\u010d"+
		"\5\34\17\2\u010d\u010e\7\20\2\2\u010e\u0110\3\2\2\2\u010f\u010c\3\2\2"+
		"\2\u010f\u0110\3\2\2\2\u0110\u0111\3\2\2\2\u0111\u0113\5L\'\2\u0112\u0114"+
		"\5\16\b\2\u0113\u0112\3\2\2\2\u0113\u0114\3\2\2\2\u0114\u011a\3\2\2\2"+
		"\u0115\u0117\5f\64\2\u0116\u0115\3\2\2\2\u0116\u0117\3\2\2\2\u0117\u0118"+
		"\3\2\2\2\u0118\u011a\7\27\2\2\u0119\u010f\3\2\2\2\u0119\u0116\3\2\2\2"+
		"\u011a\33\3\2\2\2\u011b\u011d\7\30\2\2\u011c\u011b\3\2\2\2\u011c\u011d"+
		"\3\2\2\2\u011d\u011e\3\2\2\2\u011e\u0123\5\36\20\2\u011f\u0120\7\30\2"+
		"\2\u0120\u0122\5\36\20\2\u0121\u011f\3\2\2\2\u0122\u0125\3\2\2\2\u0123"+
		"\u0121\3\2\2\2\u0123\u0124\3\2\2\2\u0124\35\3\2\2\2\u0125\u0123\3\2\2"+
		"\2\u0126\u0129\7a\2\2\u0127\u0129\5 \21\2\u0128\u0126\3\2\2\2\u0128\u0127"+
		"\3\2\2\2\u0129\37\3\2\2\2\u012a\u012f\7n\2\2\u012b\u012c\7\31\2\2\u012c"+
		"\u012e\7n\2\2\u012d\u012b\3\2\2\2\u012e\u0131\3\2\2\2\u012f\u012d\3\2"+
		"\2\2\u012f\u0130\3\2\2\2\u0130!\3\2\2\2\u0131\u012f\3\2\2\2\u0132\u0136"+
		"\b\22\1\2\u0133\u0134\5\34\17\2\u0134\u0135\7\20\2\2\u0135\u0137\3\2\2"+
		"\2\u0136\u0133\3\2\2\2\u0136\u0137\3\2\2\2\u0137\u0138\3\2\2\2\u0138\u014a"+
		"\5 \21\2\u0139\u013a\5\34\17\2\u013a\u013b\7\20\2\2\u013b\u013c\7\24\2"+
		"\2\u013c\u013d\5\f\7\2\u013d\u013e\7\25\2\2\u013e\u014a\3\2\2\2\u013f"+
		"\u0141\5\34\17\2\u0140\u0142\5\16\b\2\u0141\u0140\3\2\2\2\u0141\u0142"+
		"\3\2\2\2\u0142\u0143\3\2\2\2\u0143\u0144\5$\23\2\u0144\u0145\7\20\2\2"+
		"\u0145\u0146\7\24\2\2\u0146\u0147\5> \2\u0147\u0148\7\25\2\2\u0148\u014a"+
		"\3\2\2\2\u0149\u0132\3\2\2\2\u0149\u0139\3\2\2\2\u0149\u013f\3\2\2\2\u014a"+
		"\u0159\3\2\2\2\u014b\u014c\f\4\2\2\u014c\u014d\7\32\2\2\u014d\u0158\5"+
		"\"\22\5\u014e\u0150\f\3\2\2\u014f\u0151\5(\25\2\u0150\u014f\3\2\2\2\u0150"+
		"\u0151\3\2\2\2\u0151\u0152\3\2\2\2\u0152\u0153\7\33\2\2\u0153\u0154\5"+
		"\"\22\2\u0154\u0155\7\23\2\2\u0155\u0156\5L\'\2\u0156\u0158\3\2\2\2\u0157"+
		"\u014b\3\2\2\2\u0157\u014e\3\2\2\2\u0158\u015b\3\2\2\2\u0159\u0157\3\2"+
		"\2\2\u0159\u015a\3\2\2\2\u015a#\3\2\2\2\u015b\u0159\3\2\2\2\u015c\u015d"+
		"\7\24\2\2\u015d\u0162\5&\24\2\u015e\u015f\7\5\2\2\u015f\u0161\5&\24\2"+
		"\u0160\u015e\3\2\2\2\u0161\u0164\3\2\2\2\u0162\u0160\3\2\2\2\u0162\u0163"+
		"\3\2\2\2\u0163\u0165\3\2\2\2\u0164\u0162\3\2\2\2\u0165\u0166\7\25\2\2"+
		"\u0166%\3\2\2\2\u0167\u0169\7n\2\2\u0168\u016a\5\16\b\2\u0169\u0168\3"+
		"\2\2\2\u0169\u016a\3\2\2\2\u016a\'\3\2\2\2\u016b\u016c\t\2\2\2\u016c)"+
		"\3\2\2\2\u016d\u0179\5Z.\2\u016e\u016f\7\37\2\2\u016f\u0170\7\24\2\2\u0170"+
		"\u0171\5Z.\2\u0171\u0172\7\25\2\2\u0172\u0179\3\2\2\2\u0173\u0174\7 \2"+
		"\2\u0174\u0175\7\24\2\2\u0175\u0176\5Z.\2\u0176\u0177\7\25\2\2\u0177\u0179"+
		"\3\2\2\2\u0178\u016d\3\2\2\2\u0178\u016e\3\2\2\2\u0178\u0173\3\2\2\2\u0179"+
		"+\3\2\2\2\u017a\u017f\5.\30\2\u017b\u017c\7\5\2\2\u017c\u017e\5.\30\2"+
		"\u017d\u017b\3\2\2\2\u017e\u0181\3\2\2\2\u017f\u017d\3\2\2\2\u017f\u0180"+
		"\3\2\2\2\u0180-\3\2\2\2\u0181\u017f\3\2\2\2\u0182\u0184\5L\'\2\u0183\u0185"+
		"\5\60\31\2\u0184\u0183\3\2\2\2\u0184\u0185\3\2\2\2\u0185/\3\2\2\2\u0186"+
		"\u0187\t\3\2\2\u0187\61\3\2\2\2\u0188\u018e\7#\2\2\u0189\u018a\7#\2\2"+
		"\u018a\u018e\7\21\2\2\u018b\u018e\7$\2\2\u018c\u018e\7%\2\2\u018d\u0188"+
		"\3\2\2\2\u018d\u0189\3\2\2\2\u018d\u018b\3\2\2\2\u018d\u018c\3\2\2\2\u018e"+
		"\63\3\2\2\2\u018f\u0191\7&\2\2\u0190\u0192\7\'\2\2\u0191\u0190\3\2\2\2"+
		"\u0191\u0192\3\2\2\2\u0192\u0193\3\2\2\2\u0193\u0194\5\66\34\2\u0194\u0195"+
		"\5\n\6\2\u0195\65\3\2\2\2\u0196\u019b\58\35\2\u0197\u0198\7\5\2\2\u0198"+
		"\u019a\58\35\2\u0199\u0197\3\2\2\2\u019a\u019d\3\2\2\2\u019b\u0199\3\2"+
		"\2\2\u019b\u019c\3\2\2\2\u019c\67\3\2\2\2\u019d\u019b\3\2\2\2\u019e\u01a0"+
		"\7n\2\2\u019f\u01a1\5:\36\2\u01a0\u019f\3\2\2\2\u01a0\u01a1\3\2\2\2\u01a1"+
		"\u01a2\3\2\2\2\u01a2\u01a3\7\24\2\2\u01a3\u01a4\5\n\6\2\u01a4\u01a5\7"+
		"\25\2\2\u01a59\3\2\2\2\u01a6\u01a7\7\24\2\2\u01a7\u01ac\7n\2\2\u01a8\u01a9"+
		"\7\5\2\2\u01a9\u01ab\7n\2\2\u01aa\u01a8\3\2\2\2\u01ab\u01ae\3\2\2\2\u01ac"+
		"\u01aa\3\2\2\2\u01ac\u01ad\3\2\2\2\u01ad\u01af\3\2\2\2\u01ae\u01ac\3\2"+
		"\2\2\u01af\u01b0\7\25\2\2\u01b0;\3\2\2\2\u01b1\u01b2\7(\2\2\u01b2\u01b6"+
		"\7)\2\2\u01b3\u01b4\5\34\17\2\u01b4\u01b5\7\20\2\2\u01b5\u01b7\3\2\2\2"+
		"\u01b6\u01b3\3\2\2\2\u01b6\u01b7\3\2\2\2\u01b7\u01b8\3\2\2\2\u01b8\u01ba"+
		"\5 \21\2\u01b9\u01bb\5:\36\2\u01ba\u01b9\3\2\2\2\u01ba\u01bb\3\2\2\2\u01bb"+
		"\u01c0\3\2\2\2\u01bc\u01bd\7*\2\2\u01bd\u01c1\5> \2\u01be\u01c1\5B\"\2"+
		"\u01bf\u01c1\5\f\7\2\u01c0\u01bc\3\2\2\2\u01c0\u01be\3\2\2\2\u01c0\u01bf"+
		"\3\2\2\2\u01c1\u01c7\3\2\2\2\u01c2\u01c4\7+\2\2\u01c3\u01c5\5\16\b\2\u01c4"+
		"\u01c3\3\2\2\2\u01c4\u01c5\3\2\2\2\u01c5\u01c6\3\2\2\2\u01c6\u01c8\5\30"+
		"\r\2\u01c7\u01c2\3\2\2\2\u01c7\u01c8\3\2\2\2\u01c8=\3\2\2\2\u01c9\u01ce"+
		"\5@!\2\u01ca\u01cb\7\5\2\2\u01cb\u01cd\5@!\2\u01cc\u01ca\3\2\2\2\u01cd"+
		"\u01d0\3\2\2\2\u01ce\u01cc\3\2\2\2\u01ce\u01cf\3\2\2\2\u01cf?\3\2\2\2"+
		"\u01d0\u01ce\3\2\2\2\u01d1\u01d2\7\24\2\2\u01d2\u01d3\5Z.\2\u01d3\u01d4"+
		"\7\25\2\2\u01d4A\3\2\2\2\u01d5\u01d6\7,\2\2\u01d6\u01d7\7*\2\2\u01d7C"+
		"\3\2\2\2\u01d8\u01d9\7-\2\2\u01d9\u01da\5\34\17\2\u01da\u01db\7\6\2\2"+
		"\u01db\u01dc\5\"\22\2\u01dc\u01dd\7.\2\2\u01dd\u01e0\5F$\2\u01de\u01df"+
		"\7\7\2\2\u01df\u01e1\5L\'\2\u01e0\u01de\3\2\2\2\u01e0\u01e1\3\2\2\2\u01e1"+
		"\u01e7\3\2\2\2\u01e2\u01e4\7+\2\2\u01e3\u01e5\5\16\b\2\u01e4\u01e3\3\2"+
		"\2\2\u01e4\u01e5\3\2\2\2\u01e5\u01e6\3\2\2\2\u01e6\u01e8\5\30\r\2\u01e7"+
		"\u01e2\3\2\2\2\u01e7\u01e8\3\2\2\2\u01e8E\3\2\2\2\u01e9\u01ee\5H%\2\u01ea"+
		"\u01eb\7\5\2\2\u01eb\u01ed\5H%\2\u01ec\u01ea\3\2\2\2\u01ed\u01f0\3\2\2"+
		"\2\u01ee\u01ec\3\2\2\2\u01ee\u01ef\3\2\2\2\u01efG\3\2\2\2\u01f0\u01ee"+
		"\3\2\2\2\u01f1\u01f2\5 \21\2\u01f2\u01f3\7/\2\2\u01f3\u01f4\5L\'\2\u01f4"+
		"I\3\2\2\2\u01f5\u01f6\7\60\2\2\u01f6\u01f7\5\34\17\2\u01f7\u01f8\7\6\2"+
		"\2\u01f8\u01fb\5\"\22\2\u01f9\u01fa\7\7\2\2\u01fa\u01fc\5L\'\2\u01fb\u01f9"+
		"\3\2\2\2\u01fb\u01fc\3\2\2\2\u01fc\u0202\3\2\2\2\u01fd\u01ff\7+\2\2\u01fe"+
		"\u0200\5\16\b\2\u01ff\u01fe\3\2\2\2\u01ff\u0200\3\2\2\2\u0200\u0201\3"+
		"\2\2\2\u0201\u0203\5\30\r\2\u0202\u01fd\3\2\2\2\u0202\u0203\3\2\2\2\u0203"+
		"K\3\2\2\2\u0204\u0205\b\'\1\2\u0205\u0206\7\24\2\2\u0206\u0207\5L\'\2"+
		"\u0207\u0208\7\25\2\2\u0208\u0233\3\2\2\2\u0209\u020a\7\61\2\2\u020a\u020b"+
		"\5L\'\2\u020b\u020c\7\25\2\2\u020c\u0233\3\2\2\2\u020d\u020e\5\u008eH"+
		"\2\u020e\u020f\7\62\2\2\u020f\u0210\5L\'\2\u0210\u0211\7\63\2\2\u0211"+
		"\u0233\3\2\2\2\u0212\u0233\7,\2\2\u0213\u0233\5\\/\2\u0214\u0215\7\66"+
		"\2\2\u0215\u0233\5L\'\32\u0216\u0233\7\27\2\2\u0217\u0218\7\20\2\2\u0218"+
		"\u0233\7n\2\2\u0219\u021a\7n\2\2\u021a\u021b\7:\2\2\u021b\u0233\5L\'\24"+
		"\u021c\u0233\5P)\2\u021d\u021e\7d\2\2\u021e\u0233\5L\'\22\u021f\u0220"+
		"\5 \21\2\u0220\u0222\7\24\2\2\u0221\u0223\5\24\13\2\u0222\u0221\3\2\2"+
		"\2\u0222\u0223\3\2\2\2\u0223\u0225\3\2\2\2\u0224\u0226\5Z.\2\u0225\u0224"+
		"\3\2\2\2\u0225\u0226\3\2\2\2\u0226\u0227\3\2\2\2\u0227\u0229\7\25\2\2"+
		"\u0228\u022a\5R*\2\u0229\u0228\3\2\2\2\u0229\u022a\3\2\2\2\u022a\u0233"+
		"\3\2\2\2\u022b\u022c\7;\2\2\u022c\u022d\7\24\2\2\u022d\u022e\5\f\7\2\u022e"+
		"\u022f\7\25\2\2\u022f\u0233\3\2\2\2\u0230\u0233\5d\63\2\u0231\u0233\5"+
		"N(\2\u0232\u0204\3\2\2\2\u0232\u0209\3\2\2\2\u0232\u020d\3\2\2\2\u0232"+
		"\u0212\3\2\2\2\u0232\u0213\3\2\2\2\u0232\u0214\3\2\2\2\u0232\u0216\3\2"+
		"\2\2\u0232\u0217\3\2\2\2\u0232\u0219\3\2\2\2\u0232\u021c\3\2\2\2\u0232"+
		"\u021d\3\2\2\2\u0232\u021f\3\2\2\2\u0232\u022b\3\2\2\2\u0232\u0230\3\2"+
		"\2\2\u0232\u0231\3\2\2\2\u0233\u0291\3\2\2\2\u0234\u0235\f\33\2\2\u0235"+
		"\u0236\7\65\2\2\u0236\u0290\5L\'\34\u0237\u0238\f\31\2\2\u0238\u0239\7"+
		"\67\2\2\u0239\u0290\5L\'\31\u023a\u023b\f\30\2\2\u023b\u023c\t\4\2\2\u023c"+
		"\u0290\5L\'\31\u023d\u023e\f\27\2\2\u023e\u023f\t\5\2\2\u023f\u0290\5"+
		"L\'\30\u0240\u0241\f\17\2\2\u0241\u0242\5X-\2\u0242\u0243\5N(\2\u0243"+
		"\u0244\5X-\2\u0244\u0245\5L\'\20\u0245\u0290\3\2\2\2\u0246\u0247\f\16"+
		"\2\2\u0247\u0248\5V,\2\u0248\u0249\5L\'\17\u0249\u0290\3\2\2\2\u024a\u024c"+
		"\f\13\2\2\u024b\u024d\7d\2\2\u024c\u024b\3\2\2\2\u024c\u024d\3\2\2\2\u024d"+
		"\u024e\3\2\2\2\u024e\u024f\7=\2\2\u024f\u0250\5L\'\2\u0250\u0251\7>\2"+
		"\2\u0251\u0252\5L\'\f\u0252\u0290\3\2\2\2\u0253\u0255\f\n\2\2\u0254\u0256"+
		"\7d\2\2\u0255\u0254\3\2\2\2\u0255\u0256\3\2\2\2\u0256\u0257\3\2\2\2\u0257"+
		"\u0258\7?\2\2\u0258\u0290\5L\'\13\u0259\u025b\f\t\2\2\u025a\u025c\7d\2"+
		"\2\u025b\u025a\3\2\2\2\u025b\u025c\3\2\2\2\u025c\u025d\3\2\2\2\u025d\u025e"+
		"\7@\2\2\u025e\u0290\5L\'\n\u025f\u0260\f\6\2\2\u0260\u0261\7>\2\2\u0261"+
		"\u0290\5L\'\7\u0262\u0263\f\5\2\2\u0263\u0264\7B\2\2\u0264\u0290\5L\'"+
		"\6\u0265\u0268\f\34\2\2\u0266\u0267\7\64\2\2\u0267\u0269\5L\'\2\u0268"+
		"\u0266\3\2\2\2\u0269\u026a\3\2\2\2\u026a\u0268\3\2\2\2\u026a\u026b\3\2"+
		"\2\2\u026b\u0290\3\2\2\2\u026c\u026d\f\r\2\2\u026d\u026e\5V,\2\u026e\u026f"+
		"\7b\2\2\u026f\u0270\7\24\2\2\u0270\u0271\5\f\7\2\u0271\u0272\7\25\2\2"+
		"\u0272\u0290\3\2\2\2\u0273\u0275\f\f\2\2\u0274\u0276\7d\2\2\u0275\u0274"+
		"\3\2\2\2\u0275\u0276\3\2\2\2\u0276\u0277\3\2\2\2\u0277\u0278\7<\2\2\u0278"+
		"\u027b\7\24\2\2\u0279\u027c\5Z.\2\u027a\u027c\5\f\7\2\u027b\u0279\3\2"+
		"\2\2\u027b\u027a\3\2\2\2\u027c\u027d\3\2\2\2\u027d\u027e\7\25\2\2\u027e"+
		"\u0290\3\2\2\2\u027f\u0280\f\b\2\2\u0280\u0282\7A\2\2\u0281\u0283\7d\2"+
		"\2\u0282\u0281\3\2\2\2\u0282\u0283\3\2\2\2\u0283\u0284\3\2\2\2\u0284\u0290"+
		"\7m\2\2\u0285\u028b\f\4\2\2\u0286\u0287\7C\2\2\u0287\u0288\5L\'\2\u0288"+
		"\u0289\7D\2\2\u0289\u028a\5L\'\2\u028a\u028c\3\2\2\2\u028b\u0286\3\2\2"+
		"\2\u028c\u028d\3\2\2\2\u028d\u028b\3\2\2\2\u028d\u028e\3\2\2\2\u028e\u0290"+
		"\3\2\2\2\u028f\u0234\3\2\2\2\u028f\u0237\3\2\2\2\u028f\u023a\3\2\2\2\u028f"+
		"\u023d\3\2\2\2\u028f\u0240\3\2\2\2\u028f\u0246\3\2\2\2\u028f\u024a\3\2"+
		"\2\2\u028f\u0253\3\2\2\2\u028f\u0259\3\2\2\2\u028f\u025f\3\2\2\2\u028f"+
		"\u0262\3\2\2\2\u028f\u0265\3\2\2\2\u028f\u026c\3\2\2\2\u028f\u0273\3\2"+
		"\2\2\u028f\u027f\3\2\2\2\u028f\u0285\3\2\2\2\u0290\u0293\3\2\2\2\u0291"+
		"\u028f\3\2\2\2\u0291\u0292\3\2\2\2\u0292M\3\2\2\2\u0293\u0291\3\2\2\2"+
		"\u0294\u0295\b(\1\2\u0295\u0296\7\24\2\2\u0296\u0297\5N(\2\u0297\u0298"+
		"\7\25\2\2\u0298\u02b0\3\2\2\2\u0299\u029a\5\u008eH\2\u029a\u029b\7\62"+
		"\2\2\u029b\u029c\5N(\2\u029c\u029d\7\63\2\2\u029d\u02b0\3\2\2\2\u029e"+
		"\u02b0\5\\/\2\u029f\u02a0\7\66\2\2\u02a0\u02b0\5N(\n\u02a1\u02b0\5P)\2"+
		"\u02a2\u02a3\5 \21\2\u02a3\u02a5\7\24\2\2\u02a4\u02a6\5\24\13\2\u02a5"+
		"\u02a4\3\2\2\2\u02a5\u02a6\3\2\2\2\u02a6\u02a8\3\2\2\2\u02a7\u02a9\5Z"+
		".\2\u02a8\u02a7\3\2\2\2\u02a8\u02a9\3\2\2\2\u02a9\u02aa\3\2\2\2\u02aa"+
		"\u02ac\7\25\2\2\u02ab\u02ad\5R*\2\u02ac\u02ab\3\2\2\2\u02ac\u02ad\3\2"+
		"\2\2\u02ad\u02b0\3\2\2\2\u02ae\u02b0\5d\63\2\u02af\u0294\3\2\2\2\u02af"+
		"\u0299\3\2\2\2\u02af\u029e\3\2\2\2\u02af\u029f\3\2\2\2\u02af\u02a1\3\2"+
		"\2\2\u02af\u02a2\3\2\2\2\u02af\u02ae\3\2\2\2\u02b0\u02d0\3\2\2\2\u02b1"+
		"\u02b2\f\13\2\2\u02b2\u02b3\7\65\2\2\u02b3\u02cf\5N(\f\u02b4\u02b5\f\t"+
		"\2\2\u02b5\u02b6\7\67\2\2\u02b6\u02cf\5N(\t\u02b7\u02b8\f\b\2\2\u02b8"+
		"\u02b9\t\4\2\2\u02b9\u02cf\5N(\t\u02ba\u02bb\f\7\2\2\u02bb\u02bc\t\5\2"+
		"\2\u02bc\u02cf\5N(\b\u02bd\u02c0\f\f\2\2\u02be\u02bf\7\64\2\2\u02bf\u02c1"+
		"\5N(\2\u02c0\u02be\3\2\2\2\u02c1\u02c2\3\2\2\2\u02c2\u02c0\3\2\2\2\u02c2"+
		"\u02c3\3\2\2\2\u02c3\u02cf\3\2\2\2\u02c4\u02ca\f\3\2\2\u02c5\u02c6\7C"+
		"\2\2\u02c6\u02c7\5N(\2\u02c7\u02c8\7D\2\2\u02c8\u02c9\5N(\2\u02c9\u02cb"+
		"\3\2\2\2\u02ca\u02c5\3\2\2\2\u02cb\u02cc\3\2\2\2\u02cc\u02ca\3\2\2\2\u02cc"+
		"\u02cd\3\2\2\2\u02cd\u02cf\3\2\2\2\u02ce\u02b1\3\2\2\2\u02ce\u02b4\3\2"+
		"\2\2\u02ce\u02b7\3\2\2\2\u02ce\u02ba\3\2\2\2\u02ce\u02bd\3\2\2\2\u02ce"+
		"\u02c4\3\2\2\2\u02cf\u02d2\3\2\2\2\u02d0\u02ce\3\2\2\2\u02d0\u02d1\3\2"+
		"\2\2\u02d1O\3\2\2\2\u02d2\u02d0\3\2\2\2\u02d3\u02d5\7\24\2\2\u02d4\u02d6"+
		"\5\24\13\2\u02d5\u02d4\3\2\2\2\u02d5\u02d6\3\2\2\2\u02d6\u02da\3\2\2\2"+
		"\u02d7\u02d8\5\34\17\2\u02d8\u02d9\7\20\2\2\u02d9\u02db\3\2\2\2\u02da"+
		"\u02d7\3\2\2\2\u02da\u02db\3\2\2\2\u02db\u02dc\3\2\2\2\u02dc\u02dd\5L"+
		"\'\2\u02dd\u02de\7\6\2\2\u02de\u02e1\5\"\22\2\u02df\u02e0\7\7\2\2\u02e0"+
		"\u02e2\5L\'\2\u02e1\u02df\3\2\2\2\u02e1\u02e2\3\2\2\2\u02e2\u02e6\3\2"+
		"\2\2\u02e3\u02e4\7\b\2\2\u02e4\u02e5\7\t\2\2\u02e5\u02e7\5,\27\2\u02e6"+
		"\u02e3\3\2\2\2\u02e6\u02e7\3\2\2\2\u02e7\u02ea\3\2\2\2\u02e8\u02e9\7\f"+
		"\2\2\u02e9\u02eb\5L\'\2\u02ea\u02e8\3\2\2\2\u02ea\u02eb\3\2\2\2\u02eb"+
		"\u02ec\3\2\2\2\u02ec\u02ed\7\25\2\2\u02edQ\3\2\2\2\u02ee\u02ef\7E\2\2"+
		"\u02ef\u02f1\7\24\2\2\u02f0\u02f2\5T+\2\u02f1\u02f0\3\2\2\2\u02f1\u02f2"+
		"\3\2\2\2\u02f2\u02f6\3\2\2\2\u02f3\u02f4\7\b\2\2\u02f4\u02f5\7\t\2\2\u02f5"+
		"\u02f7\5,\27\2\u02f6\u02f3\3\2\2\2\u02f6\u02f7\3\2\2\2\u02f7\u02f8\3\2"+
		"\2\2\u02f8\u02f9\7\25\2\2\u02f9S\3\2\2\2\u02fa\u02fb\7F\2\2\u02fb\u02fc"+
		"\7\t\2\2\u02fc\u02fd\5Z.\2\u02fdU\3\2\2\2\u02fe\u02ff\t\6\2\2\u02ffW\3"+
		"\2\2\2\u0300\u0301\t\7\2\2\u0301Y\3\2\2\2\u0302\u0307\5L\'\2\u0303\u0304"+
		"\7\5\2\2\u0304\u0306\5L\'\2\u0305\u0303\3\2\2\2\u0306\u0309\3\2\2\2\u0307"+
		"\u0305\3\2\2\2\u0307\u0308\3\2\2\2\u0308[\3\2\2\2\u0309\u0307\3\2\2\2"+
		"\u030a\u031d\5^\60\2\u030b\u031d\7m\2\2\u030c\u030d\7c\2\2\u030d\u030f"+
		"\7J\2\2\u030e\u0310\5b\62\2\u030f\u030e\3\2\2\2\u030f\u0310\3\2\2\2\u0310"+
		"\u0311\3\2\2\2\u0311\u031d\7K\2\2\u0312\u0314\7J\2\2\u0313\u0315\5`\61"+
		"\2\u0314\u0313\3\2\2\2\u0314\u0315\3\2\2\2\u0315\u0316\3\2\2\2\u0316\u031d"+
		"\7K\2\2\u0317\u0319\7\16\2\2\u0318\u031a\5\20\t\2\u0319\u0318\3\2\2\2"+
		"\u0319\u031a\3\2\2\2\u031a\u031b\3\2\2\2\u031b\u031d\7\17\2\2\u031c\u030a"+
		"\3\2\2\2\u031c\u030b\3\2\2\2\u031c\u030c\3\2\2\2\u031c\u0312\3\2\2\2\u031c"+
		"\u0317\3\2\2\2\u031d]\3\2\2\2\u031e\u0327\7e\2\2\u031f\u0327\7f\2\2\u0320"+
		"\u0327\7g\2\2\u0321\u0327\7h\2\2\u0322\u0327\7i\2\2\u0323\u0327\7j\2\2"+
		"\u0324\u0327\7k\2\2\u0325\u0327\7l\2\2\u0326\u031e\3\2\2\2\u0326\u031f"+
		"\3\2\2\2\u0326\u0320\3\2\2\2\u0326\u0321\3\2\2\2\u0326\u0322\3\2\2\2\u0326"+
		"\u0323\3\2\2\2\u0326\u0324\3\2\2\2\u0326\u0325\3\2\2\2\u0327_\3\2\2\2"+
		"\u0328\u032d\5\\/\2\u0329\u032a\7\5\2\2\u032a\u032c\5\\/\2\u032b\u0329"+
		"\3\2\2\2\u032c\u032f\3\2\2\2\u032d\u032b\3\2\2\2\u032d\u032e\3\2\2\2\u032e"+
		"a\3\2\2\2\u032f\u032d\3\2\2\2\u0330\u0335\5^\60\2\u0331\u0332\7\5\2\2"+
		"\u0332\u0334\5^\60\2\u0333\u0331\3\2\2\2\u0334\u0337\3\2\2\2\u0335\u0333"+
		"\3\2\2\2\u0335\u0336\3\2\2\2\u0336c\3\2\2\2\u0337\u0335\3\2\2\2\u0338"+
		"\u033a\5f\64\2\u0339\u0338\3\2\2\2\u0339\u033a\3\2\2\2\u033a\u033b\3\2"+
		"\2\2\u033b\u033c\5\34\17\2\u033ce\3\2\2\2\u033d\u033e\7n\2\2\u033e\u033f"+
		"\7\31\2\2\u033fg\3\2\2\2\u0340\u0344\5j\66\2\u0341\u0344\5n8\2\u0342\u0344"+
		"\5t;\2\u0343\u0340\3\2\2\2\u0343\u0341\3\2\2\2\u0343\u0342\3\2\2\2\u0344"+
		"i\3\2\2\2\u0345\u0346\7L\2\2\u0346\u0347\7M\2\2\u0347\u0349\5 \21\2\u0348"+
		"\u034a\5l\67\2\u0349\u0348\3\2\2\2\u0349\u034a\3\2\2\2\u034a\u034b\3\2"+
		"\2\2\u034b\u034c\7\24\2\2\u034c\u034d\5v<\2\u034d\u034e\7\25\2\2\u034e"+
		"k\3\2\2\2\u034f\u0350\7N\2\2\u0350\u0351\7O\2\2\u0351m\3\2\2\2\u0352\u0353"+
		"\7P\2\2\u0353\u0354\7M\2\2\u0354\u0355\5 \21\2\u0355\u0356\5p9\2\u0356"+
		"o\3\2\2\2\u0357\u035c\5r:\2\u0358\u0359\7\5\2\2\u0359\u035b\5r:\2\u035a"+
		"\u0358\3\2\2\2\u035b\u035e\3\2\2\2\u035c\u035a\3\2\2\2\u035c\u035d\3\2"+
		"\2\2\u035dq\3\2\2\2\u035e\u035c\3\2\2\2\u035f\u0360\7Q\2\2\u0360\u0361"+
		"\7R\2\2\u0361\u0371\7n\2\2\u0362\u0363\7S\2\2\u0363\u0371\5x=\2\u0364"+
		"\u0365\7P\2\2\u0365\u0366\7T\2\2\u0366\u0367\7n\2\2\u0367\u0371\5|?\2"+
		"\u0368\u0369\7N\2\2\u0369\u036a\7T\2\2\u036a\u0371\7n\2\2\u036b\u036c"+
		"\7N\2\2\u036c\u036d\7U\2\2\u036d\u0371\7n\2\2\u036e\u036f\7N\2\2\u036f"+
		"\u0371\7V\2\2\u0370\u035f\3\2\2\2\u0370\u0362\3\2\2\2\u0370\u0364\3\2"+
		"\2\2\u0370\u0368\3\2\2\2\u0370\u036b\3\2\2\2\u0370\u036e\3\2\2\2\u0371"+
		"s\3\2\2\2\u0372\u0373\7N\2\2\u0373\u0374\7M\2\2\u0374\u0375\5 \21\2\u0375"+
		"u\3\2\2\2\u0376\u0379\5x=\2\u0377\u0378\7\5\2\2\u0378\u037a\5x=\2\u0379"+
		"\u0377\3\2\2\2\u037a\u037b\3\2\2\2\u037b\u0379\3\2\2\2\u037b\u037c\3\2"+
		"\2\2\u037cw\3\2\2\2\u037d\u0382\5z>\2\u037e\u0382\5\u0082B\2\u037f\u0382"+
		"\5\u0084C\2\u0380\u0382\5\16\b\2\u0381\u037d\3\2\2\2\u0381\u037e\3\2\2"+
		"\2\u0381\u037f\3\2\2\2\u0381\u0380\3\2\2\2\u0382y\3\2\2\2\u0383\u0384"+
		"\7n\2\2\u0384\u0387\5\u008eH\2\u0385\u0386\7d\2\2\u0386\u0388\7m\2\2\u0387"+
		"\u0385\3\2\2\2\u0387\u0388\3\2\2\2\u0388\u038b\3\2\2\2\u0389\u038a\7,"+
		"\2\2\u038a\u038c\5L\'\2\u038b\u0389\3\2\2\2\u038b\u038c\3\2\2\2\u038c"+
		"\u038e\3\2\2\2\u038d\u038f\5\16\b\2\u038e\u038d\3\2\2\2\u038e\u038f\3"+
		"\2\2\2\u038f{\3\2\2\2\u0390\u0392\7n\2\2\u0391\u0390\3\2\2\2\u0391\u0392"+
		"\3\2\2\2\u0392\u0394\3\2\2\2\u0393\u0395\5\u008eH\2\u0394\u0393\3\2\2"+
		"\2\u0394\u0395\3\2\2\2\u0395\u0397\3\2\2\2\u0396\u0398\5~@\2\u0397\u0396"+
		"\3\2\2\2\u0397\u0398\3\2\2\2\u0398\u039a\3\2\2\2\u0399\u039b\5\u0080A"+
		"\2\u039a\u0399\3\2\2\2\u039a\u039b\3\2\2\2\u039b\u039d\3\2\2\2\u039c\u039e"+
		"\5\16\b\2\u039d\u039c\3\2\2\2\u039d\u039e\3\2\2\2\u039e}\3\2\2\2\u039f"+
		"\u03a0\7d\2\2\u03a0\u03a3\7m\2\2\u03a1\u03a3\7m\2\2\u03a2\u039f\3\2\2"+
		"\2\u03a2\u03a1\3\2\2\2\u03a3\177\3\2\2\2\u03a4\u03a5\7,\2\2\u03a5\u03a9"+
		"\5L\'\2\u03a6\u03a7\7W\2\2\u03a7\u03a9\7,\2\2\u03a8\u03a4\3\2\2\2\u03a8"+
		"\u03a6\3\2\2\2\u03a9\u0081\3\2\2\2\u03aa\u03ab\7n\2\2\u03ab\u03ac\7/\2"+
		"\2\u03ac\u03ae\5L\'\2\u03ad\u03af\5\16\b\2\u03ae\u03ad\3\2\2\2\u03ae\u03af"+
		"\3\2\2\2\u03af\u0083\3\2\2\2\u03b0\u03b2\5\u0086D\2\u03b1\u03b0\3\2\2"+
		"\2\u03b1\u03b2\3\2\2\2\u03b2\u03b3\3\2\2\2\u03b3\u03b4\7X\2\2\u03b4\u03df"+
		"\5:\36\2\u03b5\u03b7\5\u0086D\2\u03b6\u03b5\3\2\2\2\u03b6\u03b7\3\2\2"+
		"\2\u03b7\u03b8\3\2\2\2\u03b8\u03b9\7Y\2\2\u03b9\u03ba\7Z\2\2\u03ba\u03df"+
		"\5:\36\2\u03bb\u03bd\5\u0086D\2\u03bc\u03bb\3\2\2\2\u03bc\u03bd\3\2\2"+
		"\2\u03bd\u03be\3\2\2\2\u03be\u03bf\7[\2\2\u03bf\u03c0\7Z\2\2\u03c0\u03c1"+
		"\5:\36\2\u03c1\u03c2\7\\\2\2\u03c2\u03c3\5 \21\2\u03c3\u03cc\5:\36\2\u03c4"+
		"\u03c5\7]\2\2\u03c5\u03c6\7\24\2\2\u03c6\u03c9\7e\2\2\u03c7\u03c8\7\5"+
		"\2\2\u03c8\u03ca\7e\2\2\u03c9\u03c7\3\2\2\2\u03c9\u03ca\3\2\2\2\u03ca"+
		"\u03cb\3\2\2\2\u03cb\u03cd\7\25\2\2\u03cc\u03c4\3\2\2\2\u03cc\u03cd\3"+
		"\2\2\2\u03cd\u03d0\3\2\2\2\u03ce\u03d1\5\u0088E\2\u03cf\u03d1\5\u008a"+
		"F\2\u03d0\u03ce\3\2\2\2\u03d0\u03cf\3\2\2\2\u03d0\u03d1\3\2\2\2\u03d1"+
		"\u03d4\3\2\2\2\u03d2\u03d5\5\u0088E\2\u03d3\u03d5\5\u008aF\2\u03d4\u03d2"+
		"\3\2\2\2\u03d4\u03d3\3\2\2\2\u03d4\u03d5\3\2\2\2\u03d5\u03df\3\2\2\2\u03d6"+
		"\u03d8\5\u0086D\2\u03d7\u03d6\3\2\2\2\u03d7\u03d8\3\2\2\2\u03d8\u03d9"+
		"\3\2\2\2\u03d9\u03da\7^\2\2\u03da\u03db\7\24\2\2\u03db\u03dc\5L\'\2\u03dc"+
		"\u03dd\7\25\2\2\u03dd\u03df\3\2\2\2\u03de\u03b1\3\2\2\2\u03de\u03b6\3"+
		"\2\2\2\u03de\u03bc\3\2\2\2\u03de\u03d7\3\2\2\2\u03df\u0085\3\2\2\2\u03e0"+
		"\u03e1\7U\2\2\u03e1\u03e2\7n\2\2\u03e2\u0087\3\2\2\2\u03e3\u03e4\7\23"+
		"\2\2\u03e4\u03e5\7-\2\2\u03e5\u03e6\5\u008cG\2\u03e6\u0089\3\2\2\2\u03e7"+
		"\u03e8\7\23\2\2\u03e8\u03e9\7\60\2\2\u03e9\u03ea\5\u008cG\2\u03ea\u008b"+
		"\3\2\2\2\u03eb\u03f2\7_\2\2\u03ec\u03f2\7`\2\2\u03ed\u03ee\7.\2\2\u03ee"+
		"\u03f2\7m\2\2\u03ef\u03f0\7.\2\2\u03f0\u03f2\7,\2\2\u03f1\u03eb\3\2\2"+
		"\2\u03f1\u03ec\3\2\2\2\u03f1\u03ed\3\2\2\2\u03f1\u03ef\3\2\2\2\u03f2\u008d"+
		"\3\2\2\2\u03f3\u03f6\7c\2\2\u03f4\u03f6\5\u0090I\2\u03f5\u03f3\3\2\2\2"+
		"\u03f5\u03f4\3\2\2\2\u03f6\u008f\3\2\2\2\u03f7\u03f8\7c\2\2\u03f8\u03f9"+
		"\7J\2\2\u03f9\u03fa\7K\2\2\u03fa\u0091\3\2\2\2\177\u0097\u009b\u00a3\u00a8"+
		"\u00ac\u00b2\u00b4\u00b7\u00ba\u00bf\u00c3\u00c8\u00cd\u00d1\u00d5\u00d9"+
		"\u00dc\u00e4\u00f0\u00fe\u0100\u0109\u010f\u0113\u0116\u0119\u011c\u0123"+
		"\u0128\u012f\u0136\u0141\u0149\u0150\u0157\u0159\u0162\u0169\u0178\u017f"+
		"\u0184\u018d\u0191\u019b\u01a0\u01ac\u01b6\u01ba\u01c0\u01c4\u01c7\u01ce"+
		"\u01e0\u01e4\u01e7\u01ee\u01fb\u01ff\u0202\u0222\u0225\u0229\u0232\u024c"+
		"\u0255\u025b\u026a\u0275\u027b\u0282\u028d\u028f\u0291\u02a5\u02a8\u02ac"+
		"\u02af\u02c2\u02cc\u02ce\u02d0\u02d5\u02da\u02e1\u02e6\u02ea\u02f1\u02f6"+
		"\u0307\u030f\u0314\u0319\u031c\u0326\u032d\u0335\u0339\u0343\u0349\u035c"+
		"\u0370\u037b\u0381\u0387\u038b\u038e\u0391\u0394\u0397\u039a\u039d\u03a2"+
		"\u03a8\u03ae\u03b1\u03b6\u03bc\u03c9\u03cc\u03d0\u03d4\u03d7\u03de\u03f1"+
		"\u03f5";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}