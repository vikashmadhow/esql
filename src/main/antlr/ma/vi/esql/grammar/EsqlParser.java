// Generated from ma\vi\esql\grammar\Esql.g4 by ANTLR 4.9.3

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
	static { RuntimeMetaData.checkVersion("4.9.3", RuntimeMetaData.VERSION); }

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
		T__94=95, T__95=96, EscapedIdentifier=97, JoinType=98, IntegerLiteral=99, 
		FloatingPointLiteral=100, BooleanLiteral=101, NullLiteral=102, StringLiteral=103, 
		MultiLineStringLiteral=104, UuidLiteral=105, DateLiteral=106, IntervalLiteral=107, 
		Quantifier=108, Not=109, Identifier=110, Comment=111, Whitespace=112;
	public static final int
		RULE_program = 0, RULE_expressions = 1, RULE_noop = 2, RULE_modify = 3, 
		RULE_queryUpdate = 4, RULE_select = 5, RULE_metadata = 6, RULE_attributeList = 7, 
		RULE_attribute = 8, RULE_distinct = 9, RULE_explicit = 10, RULE_columns = 11, 
		RULE_column = 12, RULE_qualifier = 13, RULE_alias = 14, RULE_aliasPart = 15, 
		RULE_qualifiedName = 16, RULE_tableExpr = 17, RULE_dynamicColumns = 18, 
		RULE_nameWithMetadata = 19, RULE_groupByList = 20, RULE_orderByList = 21, 
		RULE_orderBy = 22, RULE_direction = 23, RULE_setop = 24, RULE_with = 25, 
		RULE_cteList = 26, RULE_cte = 27, RULE_names = 28, RULE_insert = 29, RULE_rows = 30, 
		RULE_row = 31, RULE_defaultValues = 32, RULE_update = 33, RULE_setList = 34, 
		RULE_set = 35, RULE_delete = 36, RULE_expr = 37, RULE_simpleExpr = 38, 
		RULE_columnReference = 39, RULE_selectExpression = 40, RULE_window = 41, 
		RULE_partition = 42, RULE_frame = 43, RULE_preceding = 44, RULE_following = 45, 
		RULE_unbounded = 46, RULE_current = 47, RULE_compare = 48, RULE_ordering = 49, 
		RULE_expressionList = 50, RULE_literal = 51, RULE_baseLiteral = 52, RULE_literalList = 53, 
		RULE_baseLiteralList = 54, RULE_define = 55, RULE_createTable = 56, RULE_dropUndefined = 57, 
		RULE_tableDefinitions = 58, RULE_tableDefinition = 59, RULE_columnDefinition = 60, 
		RULE_derivedColumnDefinition = 61, RULE_constraintDefinition = 62, RULE_constraintName = 63, 
		RULE_onUpdate = 64, RULE_onDelete = 65, RULE_foreignKeyAction = 66, RULE_alterTable = 67, 
		RULE_alterations = 68, RULE_alteration = 69, RULE_alterColumnDefinition = 70, 
		RULE_alterNull = 71, RULE_alterDefault = 72, RULE_dropTable = 73, RULE_type = 74;
	private static String[] makeRuleNames() {
		return new String[] {
			"program", "expressions", "noop", "modify", "queryUpdate", "select", 
			"metadata", "attributeList", "attribute", "distinct", "explicit", "columns", 
			"column", "qualifier", "alias", "aliasPart", "qualifiedName", "tableExpr", 
			"dynamicColumns", "nameWithMetadata", "groupByList", "orderByList", "orderBy", 
			"direction", "setop", "with", "cteList", "cte", "names", "insert", "rows", 
			"row", "defaultValues", "update", "setList", "set", "delete", "expr", 
			"simpleExpr", "columnReference", "selectExpression", "window", "partition", 
			"frame", "preceding", "following", "unbounded", "current", "compare", 
			"ordering", "expressionList", "literal", "baseLiteral", "literalList", 
			"baseLiteralList", "define", "createTable", "dropUndefined", "tableDefinitions", 
			"tableDefinition", "columnDefinition", "derivedColumnDefinition", "constraintDefinition", 
			"constraintName", "onUpdate", "onDelete", "foreignKeyAction", "alterTable", 
			"alterations", "alteration", "alterColumnDefinition", "alterNull", "alterDefault", 
			"dropTable", "type"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "';'", "'select'", "','", "'from'", "'where'", "'group'", "'by'", 
			"'having'", "'order'", "'offset'", "'limit'", "'{'", "'}'", "':'", "'all'", 
			"'distinct'", "'on'", "'('", "')'", "'explicit'", "'*'", "'.'", "'/'", 
			"'times'", "'join'", "'rollup'", "'cube'", "'asc'", "'desc'", "'union'", 
			"'intersect'", "'except'", "'with'", "'recursive'", "'insert'", "'into'", 
			"'values'", "'return'", "'default'", "'update'", "'set'", "'='", "'delete'", 
			"'$('", "'<'", "'>'", "'||'", "'-'", "'^'", "'%'", "'+'", "':='", "'in'", 
			"'between'", "'and'", "'like'", "'ilike'", "'is'", "'or'", "'if'", "'else'", 
			"'over'", "'partition'", "'rows'", "'range'", "'preceding'", "'row'", 
			"'following'", "'unbounded'", "'current'", "'!='", "'<='", "'>='", "'['", 
			"']'", "'create'", "'table'", "'drop'", "'undefined'", "'unique'", "'primary'", 
			"'key'", "'check'", "'foreign'", "'references'", "'cost'", "'constraint'", 
			"'restrict'", "'cascade'", "'alter'", "'rename'", "'to'", "'add'", "'column'", 
			"'metadata'", "'no'", null, null, null, null, null, "'null'", null, null, 
			null, null, null, null, "'not'"
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
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, "EscapedIdentifier", "JoinType", "IntegerLiteral", "FloatingPointLiteral", 
			"BooleanLiteral", "NullLiteral", "StringLiteral", "MultiLineStringLiteral", 
			"UuidLiteral", "DateLiteral", "IntervalLiteral", "Quantifier", "Not", 
			"Identifier", "Comment", "Whitespace"
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
		public ExpressionsContext expressions() {
			return getRuleContext(ExpressionsContext.class,0);
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
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(150);
			expressions();
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

	public static class ExpressionsContext extends ParserRuleContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public ExpressionsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expressions; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterExpressions(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitExpressions(this);
		}
	}

	public final ExpressionsContext expressions() throws RecognitionException {
		ExpressionsContext _localctx = new ExpressionsContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_expressions);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(152);
			expr(0);
			setState(157);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(153);
					match(T__0);
					setState(154);
					expr(0);
					}
					} 
				}
				setState(159);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			}
			setState(161);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__0) {
				{
				setState(160);
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
		enterRule(_localctx, 4, RULE_noop);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(163);
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
			setState(168);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__39:
				enterOuterAlt(_localctx, 1);
				{
				setState(165);
				update();
				}
				break;
			case T__34:
				enterOuterAlt(_localctx, 2);
				{
				setState(166);
				insert();
				}
				break;
			case T__42:
				enterOuterAlt(_localctx, 3);
				{
				setState(167);
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
			setState(172);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__1:
			case T__32:
				enterOuterAlt(_localctx, 1);
				{
				setState(170);
				select(0);
				}
				break;
			case T__34:
			case T__39:
			case T__42:
				enterOuterAlt(_localctx, 2);
				{
				setState(171);
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
		public GroupByListContext groupByList() {
			return getRuleContext(GroupByListContext.class,0);
		}
		public OrderByListContext orderByList() {
			return getRuleContext(OrderByListContext.class,0);
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
			setState(220);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__1:
				{
				_localctx = new BaseSelectionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(175);
				match(T__1);
				setState(180);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
				case 1:
					{
					setState(176);
					metadata();
					setState(178);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==T__2) {
						{
						setState(177);
						match(T__2);
						}
					}

					}
					break;
				}
				setState(183);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__14 || _la==T__15) {
					{
					setState(182);
					distinct();
					}
				}

				setState(186);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__19) {
					{
					setState(185);
					explicit();
					}
				}

				setState(188);
				columns();
				setState(191);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
				case 1:
					{
					setState(189);
					match(T__3);
					setState(190);
					tableExpr(0);
					}
					break;
				}
				setState(195);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
				case 1:
					{
					setState(193);
					match(T__4);
					setState(194);
					((BaseSelectionContext)_localctx).where = expr(0);
					}
					break;
				}
				setState(200);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
				case 1:
					{
					setState(197);
					match(T__5);
					setState(198);
					match(T__6);
					setState(199);
					groupByList();
					}
					break;
				}
				setState(204);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
				case 1:
					{
					setState(202);
					match(T__7);
					setState(203);
					((BaseSelectionContext)_localctx).having = expr(0);
					}
					break;
				}
				setState(209);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
				case 1:
					{
					setState(206);
					match(T__8);
					setState(207);
					match(T__6);
					setState(208);
					orderByList();
					}
					break;
				}
				setState(213);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
				case 1:
					{
					setState(211);
					match(T__9);
					setState(212);
					((BaseSelectionContext)_localctx).offset = expr(0);
					}
					break;
				}
				setState(217);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
				case 1:
					{
					setState(215);
					match(T__10);
					setState(216);
					((BaseSelectionContext)_localctx).limit = expr(0);
					}
					break;
				}
				}
				break;
			case T__32:
				{
				_localctx = new WithSelectionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(219);
				with();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(228);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,16,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new CompositeSelectionContext(new SelectContext(_parentctx, _parentState));
					pushNewRecursionContext(_localctx, _startState, RULE_select);
					setState(222);
					if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
					setState(223);
					setop();
					setState(224);
					select(3);
					}
					} 
				}
				setState(230);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,16,_ctx);
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
			setState(231);
			match(T__11);
			setState(232);
			attributeList();
			setState(233);
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
			setState(235);
			attribute();
			setState(240);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__2) {
				{
				{
				setState(236);
				match(T__2);
				setState(237);
				attribute();
				}
				}
				setState(242);
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
			setState(243);
			match(Identifier);
			setState(244);
			match(T__13);
			setState(245);
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
			setState(256);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__14:
				enterOuterAlt(_localctx, 1);
				{
				setState(247);
				match(T__14);
				}
				break;
			case T__15:
				enterOuterAlt(_localctx, 2);
				{
				setState(248);
				match(T__15);
				setState(254);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__16) {
					{
					setState(249);
					match(T__16);
					setState(250);
					match(T__17);
					setState(251);
					expressionList();
					setState(252);
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
			setState(258);
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
			setState(260);
			column();
			setState(265);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,20,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(261);
					match(T__2);
					setState(262);
					column();
					}
					} 
				}
				setState(267);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,20,_ctx);
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
			setState(281);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,24,_ctx) ) {
			case 1:
				_localctx = new SingleColumnContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(271);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,21,_ctx) ) {
				case 1:
					{
					setState(268);
					alias();
					setState(269);
					match(T__13);
					}
					break;
				}
				setState(273);
				expr(0);
				setState(275);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
				case 1:
					{
					setState(274);
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
				setState(278);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Identifier) {
					{
					setState(277);
					qualifier();
					}
				}

				setState(280);
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
		enterRule(_localctx, 26, RULE_qualifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(283);
			match(Identifier);
			setState(284);
			match(T__21);
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
		enterRule(_localctx, 28, RULE_alias);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(287);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__22) {
				{
				setState(286);
				((AliasContext)_localctx).root = match(T__22);
				}
			}

			setState(289);
			aliasPart();
			setState(294);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,26,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(290);
					match(T__22);
					setState(291);
					aliasPart();
					}
					} 
				}
				setState(296);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,26,_ctx);
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
		enterRule(_localctx, 30, RULE_aliasPart);
		try {
			setState(299);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case EscapedIdentifier:
				_localctx = new EscapedAliasPartContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(297);
				match(EscapedIdentifier);
				}
				break;
			case Identifier:
				_localctx = new NormalAliasPartContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(298);
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
		enterRule(_localctx, 32, RULE_qualifiedName);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(301);
			match(Identifier);
			setState(306);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,28,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(302);
					match(T__21);
					setState(303);
					match(Identifier);
					}
					} 
				}
				setState(308);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,28,_ctx);
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
		public TerminalNode JoinType() { return getToken(EsqlParser.JoinType, 0); }
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
		int _startState = 34;
		enterRecursionRule(_localctx, 34, RULE_tableExpr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(332);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,31,_ctx) ) {
			case 1:
				{
				_localctx = new SingleTableExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(313);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,29,_ctx) ) {
				case 1:
					{
					setState(310);
					alias();
					setState(311);
					match(T__13);
					}
					break;
				}
				setState(315);
				qualifiedName();
				}
				break;
			case 2:
				{
				_localctx = new SelectTableExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(316);
				alias();
				setState(317);
				match(T__13);
				setState(318);
				match(T__17);
				setState(319);
				select(0);
				setState(320);
				match(T__18);
				}
				break;
			case 3:
				{
				_localctx = new DynamicTableExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(322);
				alias();
				setState(324);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__11) {
					{
					setState(323);
					metadata();
					}
				}

				setState(326);
				dynamicColumns();
				setState(327);
				match(T__13);
				setState(328);
				match(T__17);
				setState(329);
				rows();
				setState(330);
				match(T__18);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(348);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,34,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(346);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,33,_ctx) ) {
					case 1:
						{
						_localctx = new CrossProductTableExprContext(new TableExprContext(_parentctx, _parentState));
						((CrossProductTableExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_tableExpr);
						setState(334);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(335);
						match(T__23);
						setState(336);
						((CrossProductTableExprContext)_localctx).right = tableExpr(3);
						}
						break;
					case 2:
						{
						_localctx = new JoinTableExprContext(new TableExprContext(_parentctx, _parentState));
						((JoinTableExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_tableExpr);
						setState(337);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(339);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==JoinType) {
							{
							setState(338);
							match(JoinType);
							}
						}

						setState(341);
						match(T__24);
						setState(342);
						((JoinTableExprContext)_localctx).right = tableExpr(0);
						setState(343);
						match(T__16);
						setState(344);
						expr(0);
						}
						break;
					}
					} 
				}
				setState(350);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,34,_ctx);
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
		enterRule(_localctx, 36, RULE_dynamicColumns);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(351);
			match(T__17);
			setState(352);
			nameWithMetadata();
			setState(357);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__2) {
				{
				{
				setState(353);
				match(T__2);
				setState(354);
				nameWithMetadata();
				}
				}
				setState(359);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(360);
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
		enterRule(_localctx, 38, RULE_nameWithMetadata);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(362);
			match(Identifier);
			setState(364);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__11) {
				{
				setState(363);
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
			setState(377);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__0:
			case T__1:
			case T__3:
			case T__11:
			case T__13:
			case T__17:
			case T__22:
			case T__32:
			case T__34:
			case T__38:
			case T__39:
			case T__42:
			case T__43:
			case T__47:
			case T__73:
			case T__75:
			case T__77:
			case T__89:
			case EscapedIdentifier:
			case IntegerLiteral:
			case FloatingPointLiteral:
			case BooleanLiteral:
			case NullLiteral:
			case StringLiteral:
			case MultiLineStringLiteral:
			case UuidLiteral:
			case DateLiteral:
			case IntervalLiteral:
			case Not:
			case Identifier:
				_localctx = new SimpleGroupContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(366);
				expressionList();
				}
				break;
			case T__25:
				_localctx = new RollupGroupContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(367);
				match(T__25);
				setState(368);
				match(T__17);
				setState(369);
				expressionList();
				setState(370);
				match(T__18);
				}
				break;
			case T__26:
				_localctx = new CubeGroupContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(372);
				match(T__26);
				setState(373);
				match(T__17);
				setState(374);
				expressionList();
				setState(375);
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
			setState(379);
			orderBy();
			setState(384);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,38,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(380);
					match(T__2);
					setState(381);
					orderBy();
					}
					} 
				}
				setState(386);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,38,_ctx);
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
			setState(387);
			expr(0);
			setState(389);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,39,_ctx) ) {
			case 1:
				{
				setState(388);
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
			setState(391);
			_la = _input.LA(1);
			if ( !(_la==T__27 || _la==T__28) ) {
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
			setState(398);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,40,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(393);
				match(T__29);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(394);
				match(T__29);
				setState(395);
				match(T__14);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(396);
				match(T__30);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(397);
				match(T__31);
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
			setState(400);
			match(T__32);
			setState(402);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__33) {
				{
				setState(401);
				((WithContext)_localctx).recursive = match(T__33);
				}
			}

			setState(404);
			cteList();
			setState(405);
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
			setState(407);
			cte();
			setState(412);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__2) {
				{
				{
				setState(408);
				match(T__2);
				setState(409);
				cte();
				}
				}
				setState(414);
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
			setState(415);
			match(Identifier);
			setState(417);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,43,_ctx) ) {
			case 1:
				{
				setState(416);
				names();
				}
				break;
			}
			setState(419);
			match(T__17);
			setState(420);
			queryUpdate();
			setState(421);
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
			setState(423);
			match(T__17);
			setState(424);
			match(Identifier);
			setState(429);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__2) {
				{
				{
				setState(425);
				match(T__2);
				setState(426);
				match(Identifier);
				}
				}
				setState(431);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(432);
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
			setState(434);
			match(T__34);
			setState(435);
			match(T__35);
			setState(439);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,45,_ctx) ) {
			case 1:
				{
				setState(436);
				alias();
				setState(437);
				match(T__13);
				}
				break;
			}
			setState(441);
			qualifiedName();
			setState(443);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__17) {
				{
				setState(442);
				names();
				}
			}

			setState(449);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__36:
				{
				{
				setState(445);
				match(T__36);
				setState(446);
				rows();
				}
				}
				break;
			case T__38:
				{
				setState(447);
				defaultValues();
				}
				break;
			case T__1:
			case T__32:
				{
				setState(448);
				select(0);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(456);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,49,_ctx) ) {
			case 1:
				{
				setState(451);
				match(T__37);
				setState(453);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,48,_ctx) ) {
				case 1:
					{
					setState(452);
					metadata();
					}
					break;
				}
				setState(455);
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
			setState(458);
			row();
			setState(463);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,50,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(459);
					match(T__2);
					setState(460);
					row();
					}
					} 
				}
				setState(465);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,50,_ctx);
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
			setState(466);
			match(T__17);
			setState(467);
			expressionList();
			setState(468);
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
			setState(470);
			match(T__38);
			setState(471);
			match(T__36);
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
			setState(473);
			match(T__39);
			setState(474);
			alias();
			setState(475);
			match(T__3);
			setState(476);
			tableExpr(0);
			setState(477);
			match(T__40);
			setState(478);
			setList();
			setState(481);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,51,_ctx) ) {
			case 1:
				{
				setState(479);
				match(T__4);
				setState(480);
				expr(0);
				}
				break;
			}
			setState(488);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,53,_ctx) ) {
			case 1:
				{
				setState(483);
				match(T__37);
				setState(485);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,52,_ctx) ) {
				case 1:
					{
					setState(484);
					metadata();
					}
					break;
				}
				setState(487);
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
			setState(490);
			set();
			setState(495);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,54,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(491);
					match(T__2);
					setState(492);
					set();
					}
					} 
				}
				setState(497);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,54,_ctx);
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
			setState(498);
			qualifiedName();
			setState(499);
			match(T__41);
			setState(500);
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
			setState(502);
			match(T__42);
			setState(503);
			alias();
			setState(504);
			match(T__3);
			setState(505);
			tableExpr(0);
			setState(508);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,55,_ctx) ) {
			case 1:
				{
				setState(506);
				match(T__4);
				setState(507);
				expr(0);
				}
				break;
			}
			setState(515);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,57,_ctx) ) {
			case 1:
				{
				setState(510);
				match(T__37);
				setState(512);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,56,_ctx) ) {
				case 1:
					{
					setState(511);
					metadata();
					}
					break;
				}
				setState(514);
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
	public static class ModifyStatementContext extends ExprContext {
		public ModifyContext modify() {
			return getRuleContext(ModifyContext.class,0);
		}
		public ModifyStatementContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterModifyStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitModifyStatement(this);
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
	public static class SelectStatementContext extends ExprContext {
		public SelectContext select() {
			return getRuleContext(SelectContext.class,0);
		}
		public SelectStatementContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterSelectStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitSelectStatement(this);
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
	public static class DefineStatementContext extends ExprContext {
		public DefineContext define() {
			return getRuleContext(DefineContext.class,0);
		}
		public DefineStatementContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterDefineStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitDefineStatement(this);
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
		public Token star;
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
	public static class NoopStatementContext extends ExprContext {
		public NoopContext noop() {
			return getRuleContext(NoopContext.class,0);
		}
		public NoopStatementContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterNoopStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitNoopStatement(this);
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
			setState(562);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,61,_ctx) ) {
			case 1:
				{
				_localctx = new SelectStatementContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(518);
				select(0);
				}
				break;
			case 2:
				{
				_localctx = new ModifyStatementContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(519);
				modify();
				}
				break;
			case 3:
				{
				_localctx = new DefineStatementContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(520);
				define();
				}
				break;
			case 4:
				{
				_localctx = new NoopStatementContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(521);
				noop();
				}
				break;
			case 5:
				{
				_localctx = new GroupingExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(522);
				match(T__17);
				setState(523);
				expr(0);
				setState(524);
				match(T__18);
				}
				break;
			case 6:
				{
				_localctx = new UncomputedExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(526);
				match(T__43);
				setState(527);
				expr(0);
				setState(528);
				match(T__18);
				}
				break;
			case 7:
				{
				_localctx = new CastExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(530);
				type(0);
				setState(531);
				match(T__44);
				setState(532);
				expr(0);
				setState(533);
				match(T__45);
				}
				break;
			case 8:
				{
				_localctx = new DefaultValueContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(535);
				match(T__38);
				}
				break;
			case 9:
				{
				_localctx = new LiteralExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(536);
				literal();
				}
				break;
			case 10:
				{
				_localctx = new NegationExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(537);
				match(T__47);
				setState(538);
				expr(22);
				}
				break;
			case 11:
				{
				_localctx = new NamedParameterContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(539);
				match(T__13);
				setState(540);
				match(Identifier);
				}
				break;
			case 12:
				{
				_localctx = new NamedArgumentContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(541);
				match(Identifier);
				setState(542);
				match(T__51);
				setState(543);
				expr(17);
				}
				break;
			case 13:
				{
				_localctx = new SelectExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(544);
				selectExpression();
				}
				break;
			case 14:
				{
				_localctx = new NotExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(545);
				match(Not);
				setState(546);
				expr(15);
				}
				break;
			case 15:
				{
				_localctx = new FunctionInvocationContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(547);
				qualifiedName();
				setState(548);
				match(T__17);
				setState(550);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__14 || _la==T__15) {
					{
					setState(549);
					distinct();
					}
				}

				setState(554);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case T__0:
				case T__1:
				case T__3:
				case T__11:
				case T__13:
				case T__17:
				case T__22:
				case T__32:
				case T__34:
				case T__38:
				case T__39:
				case T__42:
				case T__43:
				case T__47:
				case T__73:
				case T__75:
				case T__77:
				case T__89:
				case EscapedIdentifier:
				case IntegerLiteral:
				case FloatingPointLiteral:
				case BooleanLiteral:
				case NullLiteral:
				case StringLiteral:
				case MultiLineStringLiteral:
				case UuidLiteral:
				case DateLiteral:
				case IntervalLiteral:
				case Not:
				case Identifier:
					{
					setState(552);
					expressionList();
					}
					break;
				case T__20:
					{
					setState(553);
					((FunctionInvocationContext)_localctx).star = match(T__20);
					}
					break;
				case T__18:
					break;
				default:
					break;
				}
				setState(556);
				match(T__18);
				setState(558);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,60,_ctx) ) {
				case 1:
					{
					setState(557);
					window();
					}
					break;
				}
				}
				break;
			case 16:
				{
				_localctx = new ColumnExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(560);
				columnReference();
				}
				break;
			case 17:
				{
				_localctx = new SimpleExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(561);
				simpleExpr(0);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(651);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,70,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(649);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,69,_ctx) ) {
					case 1:
						{
						_localctx = new ExponentiationExprContext(new ExprContext(_parentctx, _parentState));
						((ExponentiationExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(564);
						if (!(precpred(_ctx, 21))) throw new FailedPredicateException(this, "precpred(_ctx, 21)");
						setState(565);
						match(T__48);
						setState(566);
						((ExponentiationExprContext)_localctx).right = expr(21);
						}
						break;
					case 2:
						{
						_localctx = new MultiplicationExprContext(new ExprContext(_parentctx, _parentState));
						((MultiplicationExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(567);
						if (!(precpred(_ctx, 20))) throw new FailedPredicateException(this, "precpred(_ctx, 20)");
						setState(568);
						((MultiplicationExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__20) | (1L << T__22) | (1L << T__49))) != 0)) ) {
							((MultiplicationExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(569);
						((MultiplicationExprContext)_localctx).right = expr(21);
						}
						break;
					case 3:
						{
						_localctx = new AdditionExprContext(new ExprContext(_parentctx, _parentState));
						((AdditionExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(570);
						if (!(precpred(_ctx, 19))) throw new FailedPredicateException(this, "precpred(_ctx, 19)");
						setState(571);
						((AdditionExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==T__47 || _la==T__50) ) {
							((AdditionExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(572);
						((AdditionExprContext)_localctx).right = expr(20);
						}
						break;
					case 4:
						{
						_localctx = new RangeExprContext(new ExprContext(_parentctx, _parentState));
						((RangeExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(573);
						if (!(precpred(_ctx, 13))) throw new FailedPredicateException(this, "precpred(_ctx, 13)");
						setState(574);
						((RangeExprContext)_localctx).leftCompare = ordering();
						setState(575);
						((RangeExprContext)_localctx).mid = simpleExpr(0);
						setState(576);
						((RangeExprContext)_localctx).rightCompare = ordering();
						setState(577);
						((RangeExprContext)_localctx).right = expr(14);
						}
						break;
					case 5:
						{
						_localctx = new ComparisonContext(new ExprContext(_parentctx, _parentState));
						((ComparisonContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(579);
						if (!(precpred(_ctx, 12))) throw new FailedPredicateException(this, "precpred(_ctx, 12)");
						setState(580);
						compare();
						setState(581);
						((ComparisonContext)_localctx).right = expr(13);
						}
						break;
					case 6:
						{
						_localctx = new BetweenExprContext(new ExprContext(_parentctx, _parentState));
						((BetweenExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(583);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(585);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==Not) {
							{
							setState(584);
							match(Not);
							}
						}

						setState(587);
						match(T__53);
						setState(588);
						((BetweenExprContext)_localctx).mid = expr(0);
						setState(589);
						match(T__54);
						setState(590);
						((BetweenExprContext)_localctx).right = expr(10);
						}
						break;
					case 7:
						{
						_localctx = new LikeExprContext(new ExprContext(_parentctx, _parentState));
						((LikeExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(592);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(594);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==Not) {
							{
							setState(593);
							match(Not);
							}
						}

						setState(596);
						match(T__55);
						setState(597);
						((LikeExprContext)_localctx).right = expr(9);
						}
						break;
					case 8:
						{
						_localctx = new ILikeExprContext(new ExprContext(_parentctx, _parentState));
						((ILikeExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(598);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(600);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==Not) {
							{
							setState(599);
							match(Not);
							}
						}

						setState(602);
						match(T__56);
						setState(603);
						((ILikeExprContext)_localctx).right = expr(8);
						}
						break;
					case 9:
						{
						_localctx = new LogicalAndExprContext(new ExprContext(_parentctx, _parentState));
						((LogicalAndExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(604);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(605);
						match(T__54);
						setState(606);
						((LogicalAndExprContext)_localctx).right = expr(5);
						}
						break;
					case 10:
						{
						_localctx = new LogicalOrExprContext(new ExprContext(_parentctx, _parentState));
						((LogicalOrExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(607);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(608);
						match(T__58);
						setState(609);
						((LogicalOrExprContext)_localctx).right = expr(4);
						}
						break;
					case 11:
						{
						_localctx = new ConcatenationExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(610);
						if (!(precpred(_ctx, 23))) throw new FailedPredicateException(this, "precpred(_ctx, 23)");
						setState(613); 
						_errHandler.sync(this);
						_alt = 1;
						do {
							switch (_alt) {
							case 1:
								{
								{
								setState(611);
								match(T__46);
								setState(612);
								expr(0);
								}
								}
								break;
							default:
								throw new NoViableAltException(this);
							}
							setState(615); 
							_errHandler.sync(this);
							_alt = getInterpreter().adaptivePredict(_input,65,_ctx);
						} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
						}
						break;
					case 12:
						{
						_localctx = new QuantifiedComparisonContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(617);
						if (!(precpred(_ctx, 11))) throw new FailedPredicateException(this, "precpred(_ctx, 11)");
						setState(618);
						compare();
						setState(619);
						match(Quantifier);
						setState(620);
						match(T__17);
						setState(621);
						select(0);
						setState(622);
						match(T__18);
						}
						break;
					case 13:
						{
						_localctx = new InExpressionContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(624);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(626);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==Not) {
							{
							setState(625);
							match(Not);
							}
						}

						setState(628);
						match(T__52);
						setState(629);
						match(T__17);
						{
						setState(630);
						expressionList();
						}
						setState(631);
						match(T__18);
						}
						break;
					case 14:
						{
						_localctx = new NullCheckExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(633);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(634);
						match(T__57);
						setState(636);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==Not) {
							{
							setState(635);
							match(Not);
							}
						}

						setState(638);
						match(NullLiteral);
						}
						break;
					case 15:
						{
						_localctx = new CaseExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(639);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(645); 
						_errHandler.sync(this);
						_alt = 1;
						do {
							switch (_alt) {
							case 1:
								{
								{
								setState(640);
								match(T__59);
								setState(641);
								expr(0);
								setState(642);
								match(T__60);
								setState(643);
								expr(0);
								}
								}
								break;
							default:
								throw new NoViableAltException(this);
							}
							setState(647); 
							_errHandler.sync(this);
							_alt = getInterpreter().adaptivePredict(_input,68,_ctx);
						} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
						}
						break;
					}
					} 
				}
				setState(653);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,70,_ctx);
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
		public Token star;
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
			setState(682);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,74,_ctx) ) {
			case 1:
				{
				_localctx = new SimpleGroupingExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(655);
				match(T__17);
				setState(656);
				simpleExpr(0);
				setState(657);
				match(T__18);
				}
				break;
			case 2:
				{
				_localctx = new SimpleCastExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(659);
				type(0);
				setState(660);
				match(T__44);
				setState(661);
				simpleExpr(0);
				setState(662);
				match(T__45);
				}
				break;
			case 3:
				{
				_localctx = new SimpleLiteralExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(664);
				literal();
				}
				break;
			case 4:
				{
				_localctx = new SimpleNegationExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(665);
				match(T__47);
				setState(666);
				simpleExpr(8);
				}
				break;
			case 5:
				{
				_localctx = new SimpleSelectExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(667);
				selectExpression();
				}
				break;
			case 6:
				{
				_localctx = new SimpleFunctionInvocationContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(668);
				qualifiedName();
				setState(669);
				match(T__17);
				setState(671);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__14 || _la==T__15) {
					{
					setState(670);
					distinct();
					}
				}

				setState(675);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case T__0:
				case T__1:
				case T__3:
				case T__11:
				case T__13:
				case T__17:
				case T__22:
				case T__32:
				case T__34:
				case T__38:
				case T__39:
				case T__42:
				case T__43:
				case T__47:
				case T__73:
				case T__75:
				case T__77:
				case T__89:
				case EscapedIdentifier:
				case IntegerLiteral:
				case FloatingPointLiteral:
				case BooleanLiteral:
				case NullLiteral:
				case StringLiteral:
				case MultiLineStringLiteral:
				case UuidLiteral:
				case DateLiteral:
				case IntervalLiteral:
				case Not:
				case Identifier:
					{
					setState(673);
					expressionList();
					}
					break;
				case T__20:
					{
					setState(674);
					((SimpleFunctionInvocationContext)_localctx).star = match(T__20);
					}
					break;
				case T__18:
					break;
				default:
					break;
				}
				setState(677);
				match(T__18);
				setState(679);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,73,_ctx) ) {
				case 1:
					{
					setState(678);
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
				setState(681);
				columnReference();
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(712);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,78,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(710);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,77,_ctx) ) {
					case 1:
						{
						_localctx = new SimpleExponentiationExprContext(new SimpleExprContext(_parentctx, _parentState));
						((SimpleExponentiationExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_simpleExpr);
						setState(684);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(685);
						match(T__48);
						setState(686);
						((SimpleExponentiationExprContext)_localctx).right = simpleExpr(7);
						}
						break;
					case 2:
						{
						_localctx = new SimpleMultiplicationExprContext(new SimpleExprContext(_parentctx, _parentState));
						((SimpleMultiplicationExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_simpleExpr);
						setState(687);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(688);
						((SimpleMultiplicationExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__20) | (1L << T__22) | (1L << T__49))) != 0)) ) {
							((SimpleMultiplicationExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(689);
						((SimpleMultiplicationExprContext)_localctx).right = simpleExpr(7);
						}
						break;
					case 3:
						{
						_localctx = new SimpleAdditionExprContext(new SimpleExprContext(_parentctx, _parentState));
						((SimpleAdditionExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_simpleExpr);
						setState(690);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(691);
						((SimpleAdditionExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==T__47 || _la==T__50) ) {
							((SimpleAdditionExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(692);
						((SimpleAdditionExprContext)_localctx).right = simpleExpr(6);
						}
						break;
					case 4:
						{
						_localctx = new SimpleConcatenationExprContext(new SimpleExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_simpleExpr);
						setState(693);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(696); 
						_errHandler.sync(this);
						_alt = 1;
						do {
							switch (_alt) {
							case 1:
								{
								{
								setState(694);
								match(T__46);
								setState(695);
								simpleExpr(0);
								}
								}
								break;
							default:
								throw new NoViableAltException(this);
							}
							setState(698); 
							_errHandler.sync(this);
							_alt = getInterpreter().adaptivePredict(_input,75,_ctx);
						} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
						}
						break;
					case 5:
						{
						_localctx = new SimpleCaseExprContext(new SimpleExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_simpleExpr);
						setState(700);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(706); 
						_errHandler.sync(this);
						_alt = 1;
						do {
							switch (_alt) {
							case 1:
								{
								{
								setState(701);
								match(T__59);
								setState(702);
								simpleExpr(0);
								setState(703);
								match(T__60);
								setState(704);
								simpleExpr(0);
								}
								}
								break;
							default:
								throw new NoViableAltException(this);
							}
							setState(708); 
							_errHandler.sync(this);
							_alt = getInterpreter().adaptivePredict(_input,76,_ctx);
						} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
						}
						break;
					}
					} 
				}
				setState(714);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,78,_ctx);
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
		enterRule(_localctx, 78, RULE_columnReference);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(716);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,79,_ctx) ) {
			case 1:
				{
				setState(715);
				qualifier();
				}
				break;
			}
			setState(718);
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
		enterRule(_localctx, 80, RULE_selectExpression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(720);
			match(T__3);
			setState(721);
			tableExpr(0);
			setState(722);
			match(T__1);
			setState(724);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__14 || _la==T__15) {
				{
				setState(723);
				distinct();
				}
			}

			setState(729);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,81,_ctx) ) {
			case 1:
				{
				setState(726);
				alias();
				setState(727);
				match(T__13);
				}
				break;
			}
			setState(731);
			((SelectExpressionContext)_localctx).col = expr(0);
			setState(734);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,82,_ctx) ) {
			case 1:
				{
				setState(732);
				match(T__4);
				setState(733);
				((SelectExpressionContext)_localctx).where = expr(0);
				}
				break;
			}
			setState(739);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,83,_ctx) ) {
			case 1:
				{
				setState(736);
				match(T__8);
				setState(737);
				match(T__6);
				setState(738);
				orderByList();
				}
				break;
			}
			setState(743);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,84,_ctx) ) {
			case 1:
				{
				setState(741);
				match(T__9);
				setState(742);
				((SelectExpressionContext)_localctx).offset = expr(0);
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

	public static class WindowContext extends ParserRuleContext {
		public PartitionContext partition() {
			return getRuleContext(PartitionContext.class,0);
		}
		public OrderByListContext orderByList() {
			return getRuleContext(OrderByListContext.class,0);
		}
		public FrameContext frame() {
			return getRuleContext(FrameContext.class,0);
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
		enterRule(_localctx, 82, RULE_window);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(745);
			match(T__61);
			setState(746);
			match(T__17);
			setState(748);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__62) {
				{
				setState(747);
				partition();
				}
			}

			setState(753);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__8) {
				{
				setState(750);
				match(T__8);
				setState(751);
				match(T__6);
				setState(752);
				orderByList();
				}
			}

			setState(756);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__63 || _la==T__64) {
				{
				setState(755);
				frame();
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
		enterRule(_localctx, 84, RULE_partition);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(760);
			match(T__62);
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

	public static class FrameContext extends ParserRuleContext {
		public Token frameType;
		public PrecedingContext preceding() {
			return getRuleContext(PrecedingContext.class,0);
		}
		public FollowingContext following() {
			return getRuleContext(FollowingContext.class,0);
		}
		public FrameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_frame; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterFrame(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitFrame(this);
		}
	}

	public final FrameContext frame() throws RecognitionException {
		FrameContext _localctx = new FrameContext(_ctx, getState());
		enterRule(_localctx, 86, RULE_frame);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(764);
			((FrameContext)_localctx).frameType = _input.LT(1);
			_la = _input.LA(1);
			if ( !(_la==T__63 || _la==T__64) ) {
				((FrameContext)_localctx).frameType = (Token)_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(765);
			match(T__53);
			setState(766);
			preceding();
			setState(767);
			match(T__54);
			setState(768);
			following();
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

	public static class PrecedingContext extends ParserRuleContext {
		public UnboundedContext unbounded() {
			return getRuleContext(UnboundedContext.class,0);
		}
		public CurrentContext current() {
			return getRuleContext(CurrentContext.class,0);
		}
		public TerminalNode IntegerLiteral() { return getToken(EsqlParser.IntegerLiteral, 0); }
		public PrecedingContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_preceding; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterPreceding(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitPreceding(this);
		}
	}

	public final PrecedingContext preceding() throws RecognitionException {
		PrecedingContext _localctx = new PrecedingContext(_ctx, getState());
		enterRule(_localctx, 88, RULE_preceding);
		try {
			setState(778);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__68:
				enterOuterAlt(_localctx, 1);
				{
				setState(770);
				unbounded();
				setState(771);
				match(T__65);
				}
				break;
			case T__69:
				enterOuterAlt(_localctx, 2);
				{
				setState(773);
				current();
				setState(774);
				match(T__66);
				}
				break;
			case IntegerLiteral:
				enterOuterAlt(_localctx, 3);
				{
				setState(776);
				match(IntegerLiteral);
				setState(777);
				match(T__65);
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

	public static class FollowingContext extends ParserRuleContext {
		public UnboundedContext unbounded() {
			return getRuleContext(UnboundedContext.class,0);
		}
		public CurrentContext current() {
			return getRuleContext(CurrentContext.class,0);
		}
		public TerminalNode IntegerLiteral() { return getToken(EsqlParser.IntegerLiteral, 0); }
		public FollowingContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_following; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterFollowing(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitFollowing(this);
		}
	}

	public final FollowingContext following() throws RecognitionException {
		FollowingContext _localctx = new FollowingContext(_ctx, getState());
		enterRule(_localctx, 90, RULE_following);
		try {
			setState(788);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__68:
				enterOuterAlt(_localctx, 1);
				{
				setState(780);
				unbounded();
				setState(781);
				match(T__67);
				}
				break;
			case T__69:
				enterOuterAlt(_localctx, 2);
				{
				setState(783);
				current();
				setState(784);
				match(T__66);
				}
				break;
			case IntegerLiteral:
				enterOuterAlt(_localctx, 3);
				{
				setState(786);
				match(IntegerLiteral);
				setState(787);
				match(T__67);
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

	public static class UnboundedContext extends ParserRuleContext {
		public UnboundedContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unbounded; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterUnbounded(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitUnbounded(this);
		}
	}

	public final UnboundedContext unbounded() throws RecognitionException {
		UnboundedContext _localctx = new UnboundedContext(_ctx, getState());
		enterRule(_localctx, 92, RULE_unbounded);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(790);
			match(T__68);
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

	public static class CurrentContext extends ParserRuleContext {
		public CurrentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_current; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterCurrent(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitCurrent(this);
		}
	}

	public final CurrentContext current() throws RecognitionException {
		CurrentContext _localctx = new CurrentContext(_ctx, getState());
		enterRule(_localctx, 94, RULE_current);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(792);
			match(T__69);
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
		enterRule(_localctx, 96, RULE_compare);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(794);
			_la = _input.LA(1);
			if ( !(((((_la - 42)) & ~0x3f) == 0 && ((1L << (_la - 42)) & ((1L << (T__41 - 42)) | (1L << (T__44 - 42)) | (1L << (T__45 - 42)) | (1L << (T__70 - 42)) | (1L << (T__71 - 42)) | (1L << (T__72 - 42)))) != 0)) ) {
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
		enterRule(_localctx, 98, RULE_ordering);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(796);
			_la = _input.LA(1);
			if ( !(((((_la - 45)) & ~0x3f) == 0 && ((1L << (_la - 45)) & ((1L << (T__44 - 45)) | (1L << (T__45 - 45)) | (1L << (T__71 - 45)) | (1L << (T__72 - 45)))) != 0)) ) {
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
		enterRule(_localctx, 100, RULE_expressionList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(798);
			expr(0);
			setState(803);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,90,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(799);
					match(T__2);
					setState(800);
					expr(0);
					}
					} 
				}
				setState(805);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,90,_ctx);
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
		public TerminalNode Identifier() { return getToken(EsqlParser.Identifier, 0); }
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
		enterRule(_localctx, 102, RULE_literal);
		int _la;
		try {
			setState(824);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IntegerLiteral:
			case FloatingPointLiteral:
			case BooleanLiteral:
			case StringLiteral:
			case MultiLineStringLiteral:
			case UuidLiteral:
			case DateLiteral:
			case IntervalLiteral:
				_localctx = new BasicLiteralsContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(806);
				baseLiteral();
				}
				break;
			case NullLiteral:
				_localctx = new NullContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(807);
				match(NullLiteral);
				}
				break;
			case Identifier:
				_localctx = new BaseArrayLiteralContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(808);
				match(Identifier);
				setState(809);
				match(T__73);
				setState(811);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (((((_la - 99)) & ~0x3f) == 0 && ((1L << (_la - 99)) & ((1L << (IntegerLiteral - 99)) | (1L << (FloatingPointLiteral - 99)) | (1L << (BooleanLiteral - 99)) | (1L << (StringLiteral - 99)) | (1L << (MultiLineStringLiteral - 99)) | (1L << (UuidLiteral - 99)) | (1L << (DateLiteral - 99)) | (1L << (IntervalLiteral - 99)))) != 0)) {
					{
					setState(810);
					baseLiteralList();
					}
				}

				setState(813);
				match(T__74);
				}
				break;
			case T__73:
				_localctx = new JsonArrayLiteralContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(814);
				match(T__73);
				setState(816);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__11 || ((((_la - 74)) & ~0x3f) == 0 && ((1L << (_la - 74)) & ((1L << (T__73 - 74)) | (1L << (IntegerLiteral - 74)) | (1L << (FloatingPointLiteral - 74)) | (1L << (BooleanLiteral - 74)) | (1L << (NullLiteral - 74)) | (1L << (StringLiteral - 74)) | (1L << (MultiLineStringLiteral - 74)) | (1L << (UuidLiteral - 74)) | (1L << (DateLiteral - 74)) | (1L << (IntervalLiteral - 74)) | (1L << (Identifier - 74)))) != 0)) {
					{
					setState(815);
					literalList();
					}
				}

				setState(818);
				match(T__74);
				}
				break;
			case T__11:
				_localctx = new JsonObjectLiteralContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(819);
				match(T__11);
				setState(821);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Identifier) {
					{
					setState(820);
					attributeList();
					}
				}

				setState(823);
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
		enterRule(_localctx, 104, RULE_baseLiteral);
		try {
			setState(834);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IntegerLiteral:
				_localctx = new IntegerContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(826);
				match(IntegerLiteral);
				}
				break;
			case FloatingPointLiteral:
				_localctx = new FloatingPointContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(827);
				match(FloatingPointLiteral);
				}
				break;
			case BooleanLiteral:
				_localctx = new BooleanContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(828);
				match(BooleanLiteral);
				}
				break;
			case MultiLineStringLiteral:
				_localctx = new MultiLineStringContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(829);
				match(MultiLineStringLiteral);
				}
				break;
			case StringLiteral:
				_localctx = new StringContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(830);
				match(StringLiteral);
				}
				break;
			case UuidLiteral:
				_localctx = new UuidContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(831);
				match(UuidLiteral);
				}
				break;
			case DateLiteral:
				_localctx = new DateContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(832);
				match(DateLiteral);
				}
				break;
			case IntervalLiteral:
				_localctx = new IntervalContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(833);
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
		enterRule(_localctx, 106, RULE_literalList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(836);
			literal();
			setState(841);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__2) {
				{
				{
				setState(837);
				match(T__2);
				setState(838);
				literal();
				}
				}
				setState(843);
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
		enterRule(_localctx, 108, RULE_baseLiteralList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(844);
			baseLiteral();
			setState(849);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__2) {
				{
				{
				setState(845);
				match(T__2);
				setState(846);
				baseLiteral();
				}
				}
				setState(851);
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
		enterRule(_localctx, 110, RULE_define);
		try {
			setState(855);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__75:
				enterOuterAlt(_localctx, 1);
				{
				setState(852);
				createTable();
				}
				break;
			case T__89:
				enterOuterAlt(_localctx, 2);
				{
				setState(853);
				alterTable();
				}
				break;
			case T__77:
				enterOuterAlt(_localctx, 3);
				{
				setState(854);
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
		enterRule(_localctx, 112, RULE_createTable);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(857);
			match(T__75);
			setState(858);
			match(T__76);
			setState(859);
			qualifiedName();
			setState(861);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__77) {
				{
				setState(860);
				dropUndefined();
				}
			}

			setState(863);
			match(T__17);
			setState(864);
			tableDefinitions();
			setState(865);
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
		enterRule(_localctx, 114, RULE_dropUndefined);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(867);
			match(T__77);
			setState(868);
			match(T__78);
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
			setState(870);
			tableDefinition();
			setState(873); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(871);
				match(T__2);
				setState(872);
				tableDefinition();
				}
				}
				setState(875); 
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
			setState(881);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,101,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(877);
				columnDefinition();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(878);
				derivedColumnDefinition();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(879);
				constraintDefinition();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(880);
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
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(883);
			match(Identifier);
			setState(884);
			type(0);
			setState(887);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,102,_ctx) ) {
			case 1:
				{
				setState(885);
				match(Not);
				setState(886);
				match(NullLiteral);
				}
				break;
			}
			setState(891);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,103,_ctx) ) {
			case 1:
				{
				setState(889);
				match(T__38);
				setState(890);
				expr(0);
				}
				break;
			}
			setState(894);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,104,_ctx) ) {
			case 1:
				{
				setState(893);
				metadata();
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
		enterRule(_localctx, 122, RULE_derivedColumnDefinition);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(896);
			match(Identifier);
			setState(897);
			match(T__41);
			setState(898);
			expr(0);
			setState(900);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,105,_ctx) ) {
			case 1:
				{
				setState(899);
				metadata();
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
		enterRule(_localctx, 124, RULE_constraintDefinition);
		int _la;
		try {
			setState(948);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,114,_ctx) ) {
			case 1:
				_localctx = new UniqueConstraintContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(903);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__86) {
					{
					setState(902);
					constraintName();
					}
				}

				setState(905);
				match(T__79);
				setState(906);
				names();
				}
				break;
			case 2:
				_localctx = new PrimaryKeyConstraintContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(908);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__86) {
					{
					setState(907);
					constraintName();
					}
				}

				setState(910);
				match(T__80);
				setState(911);
				match(T__81);
				setState(912);
				names();
				}
				break;
			case 3:
				_localctx = new CheckConstraintContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(914);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__86) {
					{
					setState(913);
					constraintName();
					}
				}

				setState(916);
				match(T__82);
				setState(917);
				match(T__17);
				setState(918);
				expr(0);
				setState(919);
				match(T__18);
				}
				break;
			case 4:
				_localctx = new ForeignKeyConstraintContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(922);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__86) {
					{
					setState(921);
					constraintName();
					}
				}

				setState(924);
				match(T__83);
				setState(925);
				match(T__81);
				setState(926);
				((ForeignKeyConstraintContext)_localctx).from = names();
				setState(927);
				match(T__84);
				setState(928);
				qualifiedName();
				setState(929);
				((ForeignKeyConstraintContext)_localctx).to = names();
				setState(938);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,111,_ctx) ) {
				case 1:
					{
					setState(930);
					match(T__85);
					setState(931);
					match(T__17);
					setState(932);
					((ForeignKeyConstraintContext)_localctx).forwardcost = match(IntegerLiteral);
					setState(935);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==T__2) {
						{
						setState(933);
						match(T__2);
						setState(934);
						((ForeignKeyConstraintContext)_localctx).reversecost = match(IntegerLiteral);
						}
					}

					setState(937);
					match(T__18);
					}
					break;
				}
				setState(942);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,112,_ctx) ) {
				case 1:
					{
					setState(940);
					onUpdate();
					}
					break;
				case 2:
					{
					setState(941);
					onDelete();
					}
					break;
				}
				setState(946);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,113,_ctx) ) {
				case 1:
					{
					setState(944);
					onUpdate();
					}
					break;
				case 2:
					{
					setState(945);
					onDelete();
					}
					break;
				}
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
		enterRule(_localctx, 126, RULE_constraintName);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(950);
			match(T__86);
			setState(951);
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
		enterRule(_localctx, 128, RULE_onUpdate);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(953);
			match(T__16);
			setState(954);
			match(T__39);
			setState(955);
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
		enterRule(_localctx, 130, RULE_onDelete);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(957);
			match(T__16);
			setState(958);
			match(T__42);
			setState(959);
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
		enterRule(_localctx, 132, RULE_foreignKeyAction);
		try {
			setState(967);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,115,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(961);
				match(T__87);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(962);
				match(T__88);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(963);
				match(T__40);
				setState(964);
				match(NullLiteral);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(965);
				match(T__40);
				setState(966);
				match(T__38);
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

	public static class AlterTableContext extends ParserRuleContext {
		public QualifiedNameContext qualifiedName() {
			return getRuleContext(QualifiedNameContext.class,0);
		}
		public AlterationsContext alterations() {
			return getRuleContext(AlterationsContext.class,0);
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
		enterRule(_localctx, 134, RULE_alterTable);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(969);
			match(T__89);
			setState(970);
			match(T__76);
			setState(971);
			qualifiedName();
			setState(972);
			alterations();
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

	public static class AlterationsContext extends ParserRuleContext {
		public List<AlterationContext> alteration() {
			return getRuleContexts(AlterationContext.class);
		}
		public AlterationContext alteration(int i) {
			return getRuleContext(AlterationContext.class,i);
		}
		public AlterationsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_alterations; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterAlterations(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitAlterations(this);
		}
	}

	public final AlterationsContext alterations() throws RecognitionException {
		AlterationsContext _localctx = new AlterationsContext(_ctx, getState());
		enterRule(_localctx, 136, RULE_alterations);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(974);
			alteration();
			setState(979);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,116,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(975);
					match(T__2);
					setState(976);
					alteration();
					}
					} 
				}
				setState(981);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,116,_ctx);
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

	public static class AlterationContext extends ParserRuleContext {
		public AlterationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_alteration; }
	 
		public AlterationContext() { }
		public void copyFrom(AlterationContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class RenameTableContext extends AlterationContext {
		public TerminalNode Identifier() { return getToken(EsqlParser.Identifier, 0); }
		public RenameTableContext(AlterationContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterRenameTable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitRenameTable(this);
		}
	}
	public static class DropTableMetadataContext extends AlterationContext {
		public DropTableMetadataContext(AlterationContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterDropTableMetadata(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitDropTableMetadata(this);
		}
	}
	public static class AddTableDefinitionContext extends AlterationContext {
		public TableDefinitionContext tableDefinition() {
			return getRuleContext(TableDefinitionContext.class,0);
		}
		public AddTableDefinitionContext(AlterationContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterAddTableDefinition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitAddTableDefinition(this);
		}
	}
	public static class AlterColumnContext extends AlterationContext {
		public TerminalNode Identifier() { return getToken(EsqlParser.Identifier, 0); }
		public AlterColumnDefinitionContext alterColumnDefinition() {
			return getRuleContext(AlterColumnDefinitionContext.class,0);
		}
		public AlterColumnContext(AlterationContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterAlterColumn(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitAlterColumn(this);
		}
	}
	public static class DropConstraintContext extends AlterationContext {
		public TerminalNode Identifier() { return getToken(EsqlParser.Identifier, 0); }
		public DropConstraintContext(AlterationContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterDropConstraint(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitDropConstraint(this);
		}
	}
	public static class DropColumnContext extends AlterationContext {
		public TerminalNode Identifier() { return getToken(EsqlParser.Identifier, 0); }
		public DropColumnContext(AlterationContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterDropColumn(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitDropColumn(this);
		}
	}

	public final AlterationContext alteration() throws RecognitionException {
		AlterationContext _localctx = new AlterationContext(_ctx, getState());
		enterRule(_localctx, 138, RULE_alteration);
		try {
			setState(999);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,117,_ctx) ) {
			case 1:
				_localctx = new RenameTableContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(982);
				match(T__90);
				setState(983);
				match(T__91);
				setState(984);
				match(Identifier);
				}
				break;
			case 2:
				_localctx = new AddTableDefinitionContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(985);
				match(T__92);
				setState(986);
				tableDefinition();
				}
				break;
			case 3:
				_localctx = new AlterColumnContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(987);
				match(T__89);
				setState(988);
				match(T__93);
				setState(989);
				match(Identifier);
				setState(990);
				alterColumnDefinition();
				}
				break;
			case 4:
				_localctx = new DropColumnContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(991);
				match(T__77);
				setState(992);
				match(T__93);
				setState(993);
				match(Identifier);
				}
				break;
			case 5:
				_localctx = new DropConstraintContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(994);
				match(T__77);
				setState(995);
				match(T__86);
				setState(996);
				match(Identifier);
				}
				break;
			case 6:
				_localctx = new DropTableMetadataContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(997);
				match(T__77);
				setState(998);
				match(T__94);
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
		enterRule(_localctx, 140, RULE_alterColumnDefinition);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1002);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,118,_ctx) ) {
			case 1:
				{
				setState(1001);
				match(Identifier);
				}
				break;
			}
			setState(1005);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,119,_ctx) ) {
			case 1:
				{
				setState(1004);
				type(0);
				}
				break;
			}
			setState(1008);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,120,_ctx) ) {
			case 1:
				{
				setState(1007);
				alterNull();
				}
				break;
			}
			setState(1011);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,121,_ctx) ) {
			case 1:
				{
				setState(1010);
				alterDefault();
				}
				break;
			}
			setState(1014);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,122,_ctx) ) {
			case 1:
				{
				setState(1013);
				metadata();
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
		enterRule(_localctx, 142, RULE_alterNull);
		try {
			setState(1019);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Not:
				enterOuterAlt(_localctx, 1);
				{
				setState(1016);
				match(Not);
				setState(1017);
				match(NullLiteral);
				}
				break;
			case NullLiteral:
				enterOuterAlt(_localctx, 2);
				{
				setState(1018);
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
		enterRule(_localctx, 144, RULE_alterDefault);
		try {
			setState(1025);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__38:
				enterOuterAlt(_localctx, 1);
				{
				setState(1021);
				match(T__38);
				setState(1022);
				expr(0);
				}
				break;
			case T__95:
				enterOuterAlt(_localctx, 2);
				{
				setState(1023);
				match(T__95);
				setState(1024);
				match(T__38);
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
		enterRule(_localctx, 146, RULE_dropTable);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1027);
			match(T__77);
			setState(1028);
			match(T__76);
			setState(1029);
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
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode IntegerLiteral() { return getToken(EsqlParser.IntegerLiteral, 0); }
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
		public TerminalNode Identifier() { return getToken(EsqlParser.Identifier, 0); }
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
		return type(0);
	}

	private TypeContext type(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		TypeContext _localctx = new TypeContext(_ctx, _parentState);
		TypeContext _prevctx = _localctx;
		int _startState = 148;
		enterRecursionRule(_localctx, 148, RULE_type, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			_localctx = new BaseContext(_localctx);
			_ctx = _localctx;
			_prevctx = _localctx;

			setState(1032);
			match(Identifier);
			}
			_ctx.stop = _input.LT(-1);
			setState(1042);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,126,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new ArrayContext(new TypeContext(_parentctx, _parentState));
					pushNewRecursionContext(_localctx, _startState, RULE_type);
					setState(1034);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(1035);
					match(T__73);
					setState(1037);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==IntegerLiteral) {
						{
						setState(1036);
						match(IntegerLiteral);
						}
					}

					setState(1039);
					match(T__74);
					}
					} 
				}
				setState(1044);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,126,_ctx);
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

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 5:
			return select_sempred((SelectContext)_localctx, predIndex);
		case 17:
			return tableExpr_sempred((TableExprContext)_localctx, predIndex);
		case 37:
			return expr_sempred((ExprContext)_localctx, predIndex);
		case 38:
			return simpleExpr_sempred((SimpleExprContext)_localctx, predIndex);
		case 74:
			return type_sempred((TypeContext)_localctx, predIndex);
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
			return precpred(_ctx, 21);
		case 4:
			return precpred(_ctx, 20);
		case 5:
			return precpred(_ctx, 19);
		case 6:
			return precpred(_ctx, 13);
		case 7:
			return precpred(_ctx, 12);
		case 8:
			return precpred(_ctx, 9);
		case 9:
			return precpred(_ctx, 8);
		case 10:
			return precpred(_ctx, 7);
		case 11:
			return precpred(_ctx, 4);
		case 12:
			return precpred(_ctx, 3);
		case 13:
			return precpred(_ctx, 23);
		case 14:
			return precpred(_ctx, 11);
		case 15:
			return precpred(_ctx, 10);
		case 16:
			return precpred(_ctx, 6);
		case 17:
			return precpred(_ctx, 2);
		}
		return true;
	}
	private boolean simpleExpr_sempred(SimpleExprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 18:
			return precpred(_ctx, 7);
		case 19:
			return precpred(_ctx, 6);
		case 20:
			return precpred(_ctx, 5);
		case 21:
			return precpred(_ctx, 9);
		case 22:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean type_sempred(TypeContext _localctx, int predIndex) {
		switch (predIndex) {
		case 23:
			return precpred(_ctx, 1);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3r\u0418\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\4I"+
		"\tI\4J\tJ\4K\tK\4L\tL\3\2\3\2\3\3\3\3\3\3\7\3\u009e\n\3\f\3\16\3\u00a1"+
		"\13\3\3\3\5\3\u00a4\n\3\3\4\3\4\3\5\3\5\3\5\5\5\u00ab\n\5\3\6\3\6\5\6"+
		"\u00af\n\6\3\7\3\7\3\7\3\7\5\7\u00b5\n\7\5\7\u00b7\n\7\3\7\5\7\u00ba\n"+
		"\7\3\7\5\7\u00bd\n\7\3\7\3\7\3\7\5\7\u00c2\n\7\3\7\3\7\5\7\u00c6\n\7\3"+
		"\7\3\7\3\7\5\7\u00cb\n\7\3\7\3\7\5\7\u00cf\n\7\3\7\3\7\3\7\5\7\u00d4\n"+
		"\7\3\7\3\7\5\7\u00d8\n\7\3\7\3\7\5\7\u00dc\n\7\3\7\5\7\u00df\n\7\3\7\3"+
		"\7\3\7\3\7\7\7\u00e5\n\7\f\7\16\7\u00e8\13\7\3\b\3\b\3\b\3\b\3\t\3\t\3"+
		"\t\7\t\u00f1\n\t\f\t\16\t\u00f4\13\t\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3"+
		"\13\3\13\3\13\3\13\5\13\u0101\n\13\5\13\u0103\n\13\3\f\3\f\3\r\3\r\3\r"+
		"\7\r\u010a\n\r\f\r\16\r\u010d\13\r\3\16\3\16\3\16\5\16\u0112\n\16\3\16"+
		"\3\16\5\16\u0116\n\16\3\16\5\16\u0119\n\16\3\16\5\16\u011c\n\16\3\17\3"+
		"\17\3\17\3\20\5\20\u0122\n\20\3\20\3\20\3\20\7\20\u0127\n\20\f\20\16\20"+
		"\u012a\13\20\3\21\3\21\5\21\u012e\n\21\3\22\3\22\3\22\7\22\u0133\n\22"+
		"\f\22\16\22\u0136\13\22\3\23\3\23\3\23\3\23\5\23\u013c\n\23\3\23\3\23"+
		"\3\23\3\23\3\23\3\23\3\23\3\23\3\23\5\23\u0147\n\23\3\23\3\23\3\23\3\23"+
		"\3\23\3\23\5\23\u014f\n\23\3\23\3\23\3\23\3\23\3\23\5\23\u0156\n\23\3"+
		"\23\3\23\3\23\3\23\3\23\7\23\u015d\n\23\f\23\16\23\u0160\13\23\3\24\3"+
		"\24\3\24\3\24\7\24\u0166\n\24\f\24\16\24\u0169\13\24\3\24\3\24\3\25\3"+
		"\25\5\25\u016f\n\25\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26"+
		"\3\26\5\26\u017c\n\26\3\27\3\27\3\27\7\27\u0181\n\27\f\27\16\27\u0184"+
		"\13\27\3\30\3\30\5\30\u0188\n\30\3\31\3\31\3\32\3\32\3\32\3\32\3\32\5"+
		"\32\u0191\n\32\3\33\3\33\5\33\u0195\n\33\3\33\3\33\3\33\3\34\3\34\3\34"+
		"\7\34\u019d\n\34\f\34\16\34\u01a0\13\34\3\35\3\35\5\35\u01a4\n\35\3\35"+
		"\3\35\3\35\3\35\3\36\3\36\3\36\3\36\7\36\u01ae\n\36\f\36\16\36\u01b1\13"+
		"\36\3\36\3\36\3\37\3\37\3\37\3\37\3\37\5\37\u01ba\n\37\3\37\3\37\5\37"+
		"\u01be\n\37\3\37\3\37\3\37\3\37\5\37\u01c4\n\37\3\37\3\37\5\37\u01c8\n"+
		"\37\3\37\5\37\u01cb\n\37\3 \3 \3 \7 \u01d0\n \f \16 \u01d3\13 \3!\3!\3"+
		"!\3!\3\"\3\"\3\"\3#\3#\3#\3#\3#\3#\3#\3#\5#\u01e4\n#\3#\3#\5#\u01e8\n"+
		"#\3#\5#\u01eb\n#\3$\3$\3$\7$\u01f0\n$\f$\16$\u01f3\13$\3%\3%\3%\3%\3&"+
		"\3&\3&\3&\3&\3&\5&\u01ff\n&\3&\3&\5&\u0203\n&\3&\5&\u0206\n&\3\'\3\'\3"+
		"\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'"+
		"\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\5\'\u0229\n\'\3\'"+
		"\3\'\5\'\u022d\n\'\3\'\3\'\5\'\u0231\n\'\3\'\3\'\5\'\u0235\n\'\3\'\3\'"+
		"\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3"+
		"\'\3\'\5\'\u024c\n\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\5\'\u0255\n\'\3\'\3\'"+
		"\3\'\3\'\5\'\u025b\n\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\6\'"+
		"\u0268\n\'\r\'\16\'\u0269\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\5\'\u0275"+
		"\n\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\5\'\u027f\n\'\3\'\3\'\3\'\3\'\3\'"+
		"\3\'\3\'\6\'\u0288\n\'\r\'\16\'\u0289\7\'\u028c\n\'\f\'\16\'\u028f\13"+
		"\'\3(\3(\3(\3(\3(\3(\3(\3(\3(\3(\3(\3(\3(\3(\3(\3(\3(\5(\u02a2\n(\3(\3"+
		"(\5(\u02a6\n(\3(\3(\5(\u02aa\n(\3(\5(\u02ad\n(\3(\3(\3(\3(\3(\3(\3(\3"+
		"(\3(\3(\3(\3(\6(\u02bb\n(\r(\16(\u02bc\3(\3(\3(\3(\3(\3(\6(\u02c5\n(\r"+
		"(\16(\u02c6\7(\u02c9\n(\f(\16(\u02cc\13(\3)\5)\u02cf\n)\3)\3)\3*\3*\3"+
		"*\3*\5*\u02d7\n*\3*\3*\3*\5*\u02dc\n*\3*\3*\3*\5*\u02e1\n*\3*\3*\3*\5"+
		"*\u02e6\n*\3*\3*\5*\u02ea\n*\3+\3+\3+\5+\u02ef\n+\3+\3+\3+\5+\u02f4\n"+
		"+\3+\5+\u02f7\n+\3+\3+\3,\3,\3,\3,\3-\3-\3-\3-\3-\3-\3.\3.\3.\3.\3.\3"+
		".\3.\3.\5.\u030d\n.\3/\3/\3/\3/\3/\3/\3/\3/\5/\u0317\n/\3\60\3\60\3\61"+
		"\3\61\3\62\3\62\3\63\3\63\3\64\3\64\3\64\7\64\u0324\n\64\f\64\16\64\u0327"+
		"\13\64\3\65\3\65\3\65\3\65\3\65\5\65\u032e\n\65\3\65\3\65\3\65\5\65\u0333"+
		"\n\65\3\65\3\65\3\65\5\65\u0338\n\65\3\65\5\65\u033b\n\65\3\66\3\66\3"+
		"\66\3\66\3\66\3\66\3\66\3\66\5\66\u0345\n\66\3\67\3\67\3\67\7\67\u034a"+
		"\n\67\f\67\16\67\u034d\13\67\38\38\38\78\u0352\n8\f8\168\u0355\138\39"+
		"\39\39\59\u035a\n9\3:\3:\3:\3:\5:\u0360\n:\3:\3:\3:\3:\3;\3;\3;\3<\3<"+
		"\3<\6<\u036c\n<\r<\16<\u036d\3=\3=\3=\3=\5=\u0374\n=\3>\3>\3>\3>\5>\u037a"+
		"\n>\3>\3>\5>\u037e\n>\3>\5>\u0381\n>\3?\3?\3?\3?\5?\u0387\n?\3@\5@\u038a"+
		"\n@\3@\3@\3@\5@\u038f\n@\3@\3@\3@\3@\5@\u0395\n@\3@\3@\3@\3@\3@\3@\5@"+
		"\u039d\n@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\5@\u03aa\n@\3@\5@\u03ad\n@"+
		"\3@\3@\5@\u03b1\n@\3@\3@\5@\u03b5\n@\5@\u03b7\n@\3A\3A\3A\3B\3B\3B\3B"+
		"\3C\3C\3C\3C\3D\3D\3D\3D\3D\3D\5D\u03ca\nD\3E\3E\3E\3E\3E\3F\3F\3F\7F"+
		"\u03d4\nF\fF\16F\u03d7\13F\3G\3G\3G\3G\3G\3G\3G\3G\3G\3G\3G\3G\3G\3G\3"+
		"G\3G\3G\5G\u03ea\nG\3H\5H\u03ed\nH\3H\5H\u03f0\nH\3H\5H\u03f3\nH\3H\5"+
		"H\u03f6\nH\3H\5H\u03f9\nH\3I\3I\3I\5I\u03fe\nI\3J\3J\3J\3J\5J\u0404\n"+
		"J\3K\3K\3K\3K\3L\3L\3L\3L\3L\3L\5L\u0410\nL\3L\7L\u0413\nL\fL\16L\u0416"+
		"\13L\3L\2\7\f$LN\u0096M\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*"+
		",.\60\62\64\668:<>@BDFHJLNPRTVXZ\\^`bdfhjlnprtvxz|~\u0080\u0082\u0084"+
		"\u0086\u0088\u008a\u008c\u008e\u0090\u0092\u0094\u0096\2\b\3\2\36\37\5"+
		"\2\27\27\31\31\64\64\4\2\62\62\65\65\3\2BC\5\2,,/\60IK\4\2/\60JK\2\u048f"+
		"\2\u0098\3\2\2\2\4\u009a\3\2\2\2\6\u00a5\3\2\2\2\b\u00aa\3\2\2\2\n\u00ae"+
		"\3\2\2\2\f\u00de\3\2\2\2\16\u00e9\3\2\2\2\20\u00ed\3\2\2\2\22\u00f5\3"+
		"\2\2\2\24\u0102\3\2\2\2\26\u0104\3\2\2\2\30\u0106\3\2\2\2\32\u011b\3\2"+
		"\2\2\34\u011d\3\2\2\2\36\u0121\3\2\2\2 \u012d\3\2\2\2\"\u012f\3\2\2\2"+
		"$\u014e\3\2\2\2&\u0161\3\2\2\2(\u016c\3\2\2\2*\u017b\3\2\2\2,\u017d\3"+
		"\2\2\2.\u0185\3\2\2\2\60\u0189\3\2\2\2\62\u0190\3\2\2\2\64\u0192\3\2\2"+
		"\2\66\u0199\3\2\2\28\u01a1\3\2\2\2:\u01a9\3\2\2\2<\u01b4\3\2\2\2>\u01cc"+
		"\3\2\2\2@\u01d4\3\2\2\2B\u01d8\3\2\2\2D\u01db\3\2\2\2F\u01ec\3\2\2\2H"+
		"\u01f4\3\2\2\2J\u01f8\3\2\2\2L\u0234\3\2\2\2N\u02ac\3\2\2\2P\u02ce\3\2"+
		"\2\2R\u02d2\3\2\2\2T\u02eb\3\2\2\2V\u02fa\3\2\2\2X\u02fe\3\2\2\2Z\u030c"+
		"\3\2\2\2\\\u0316\3\2\2\2^\u0318\3\2\2\2`\u031a\3\2\2\2b\u031c\3\2\2\2"+
		"d\u031e\3\2\2\2f\u0320\3\2\2\2h\u033a\3\2\2\2j\u0344\3\2\2\2l\u0346\3"+
		"\2\2\2n\u034e\3\2\2\2p\u0359\3\2\2\2r\u035b\3\2\2\2t\u0365\3\2\2\2v\u0368"+
		"\3\2\2\2x\u0373\3\2\2\2z\u0375\3\2\2\2|\u0382\3\2\2\2~\u03b6\3\2\2\2\u0080"+
		"\u03b8\3\2\2\2\u0082\u03bb\3\2\2\2\u0084\u03bf\3\2\2\2\u0086\u03c9\3\2"+
		"\2\2\u0088\u03cb\3\2\2\2\u008a\u03d0\3\2\2\2\u008c\u03e9\3\2\2\2\u008e"+
		"\u03ec\3\2\2\2\u0090\u03fd\3\2\2\2\u0092\u0403\3\2\2\2\u0094\u0405\3\2"+
		"\2\2\u0096\u0409\3\2\2\2\u0098\u0099\5\4\3\2\u0099\3\3\2\2\2\u009a\u009f"+
		"\5L\'\2\u009b\u009c\7\3\2\2\u009c\u009e\5L\'\2\u009d\u009b\3\2\2\2\u009e"+
		"\u00a1\3\2\2\2\u009f\u009d\3\2\2\2\u009f\u00a0\3\2\2\2\u00a0\u00a3\3\2"+
		"\2\2\u00a1\u009f\3\2\2\2\u00a2\u00a4\7\3\2\2\u00a3\u00a2\3\2\2\2\u00a3"+
		"\u00a4\3\2\2\2\u00a4\5\3\2\2\2\u00a5\u00a6\7\3\2\2\u00a6\7\3\2\2\2\u00a7"+
		"\u00ab\5D#\2\u00a8\u00ab\5<\37\2\u00a9\u00ab\5J&\2\u00aa\u00a7\3\2\2\2"+
		"\u00aa\u00a8\3\2\2\2\u00aa\u00a9\3\2\2\2\u00ab\t\3\2\2\2\u00ac\u00af\5"+
		"\f\7\2\u00ad\u00af\5\b\5\2\u00ae\u00ac\3\2\2\2\u00ae\u00ad\3\2\2\2\u00af"+
		"\13\3\2\2\2\u00b0\u00b1\b\7\1\2\u00b1\u00b6\7\4\2\2\u00b2\u00b4\5\16\b"+
		"\2\u00b3\u00b5\7\5\2\2\u00b4\u00b3\3\2\2\2\u00b4\u00b5\3\2\2\2\u00b5\u00b7"+
		"\3\2\2\2\u00b6\u00b2\3\2\2\2\u00b6\u00b7\3\2\2\2\u00b7\u00b9\3\2\2\2\u00b8"+
		"\u00ba\5\24\13\2\u00b9\u00b8\3\2\2\2\u00b9\u00ba\3\2\2\2\u00ba\u00bc\3"+
		"\2\2\2\u00bb\u00bd\5\26\f\2\u00bc\u00bb\3\2\2\2\u00bc\u00bd\3\2\2\2\u00bd"+
		"\u00be\3\2\2\2\u00be\u00c1\5\30\r\2\u00bf\u00c0\7\6\2\2\u00c0\u00c2\5"+
		"$\23\2\u00c1\u00bf\3\2\2\2\u00c1\u00c2\3\2\2\2\u00c2\u00c5\3\2\2\2\u00c3"+
		"\u00c4\7\7\2\2\u00c4\u00c6\5L\'\2\u00c5\u00c3\3\2\2\2\u00c5\u00c6\3\2"+
		"\2\2\u00c6\u00ca\3\2\2\2\u00c7\u00c8\7\b\2\2\u00c8\u00c9\7\t\2\2\u00c9"+
		"\u00cb\5*\26\2\u00ca\u00c7\3\2\2\2\u00ca\u00cb\3\2\2\2\u00cb\u00ce\3\2"+
		"\2\2\u00cc\u00cd\7\n\2\2\u00cd\u00cf\5L\'\2\u00ce\u00cc\3\2\2\2\u00ce"+
		"\u00cf\3\2\2\2\u00cf\u00d3\3\2\2\2\u00d0\u00d1\7\13\2\2\u00d1\u00d2\7"+
		"\t\2\2\u00d2\u00d4\5,\27\2\u00d3\u00d0\3\2\2\2\u00d3\u00d4\3\2\2\2\u00d4"+
		"\u00d7\3\2\2\2\u00d5\u00d6\7\f\2\2\u00d6\u00d8\5L\'\2\u00d7\u00d5\3\2"+
		"\2\2\u00d7\u00d8\3\2\2\2\u00d8\u00db\3\2\2\2\u00d9\u00da\7\r\2\2\u00da"+
		"\u00dc\5L\'\2\u00db\u00d9\3\2\2\2\u00db\u00dc\3\2\2\2\u00dc\u00df\3\2"+
		"\2\2\u00dd\u00df\5\64\33\2\u00de\u00b0\3\2\2\2\u00de\u00dd\3\2\2\2\u00df"+
		"\u00e6\3\2\2\2\u00e0\u00e1\f\4\2\2\u00e1\u00e2\5\62\32\2\u00e2\u00e3\5"+
		"\f\7\5\u00e3\u00e5\3\2\2\2\u00e4\u00e0\3\2\2\2\u00e5\u00e8\3\2\2\2\u00e6"+
		"\u00e4\3\2\2\2\u00e6\u00e7\3\2\2\2\u00e7\r\3\2\2\2\u00e8\u00e6\3\2\2\2"+
		"\u00e9\u00ea\7\16\2\2\u00ea\u00eb\5\20\t\2\u00eb\u00ec\7\17\2\2\u00ec"+
		"\17\3\2\2\2\u00ed\u00f2\5\22\n\2\u00ee\u00ef\7\5\2\2\u00ef\u00f1\5\22"+
		"\n\2\u00f0\u00ee\3\2\2\2\u00f1\u00f4\3\2\2\2\u00f2\u00f0\3\2\2\2\u00f2"+
		"\u00f3\3\2\2\2\u00f3\21\3\2\2\2\u00f4\u00f2\3\2\2\2\u00f5\u00f6\7p\2\2"+
		"\u00f6\u00f7\7\20\2\2\u00f7\u00f8\5L\'\2\u00f8\23\3\2\2\2\u00f9\u0103"+
		"\7\21\2\2\u00fa\u0100\7\22\2\2\u00fb\u00fc\7\23\2\2\u00fc\u00fd\7\24\2"+
		"\2\u00fd\u00fe\5f\64\2\u00fe\u00ff\7\25\2\2\u00ff\u0101\3\2\2\2\u0100"+
		"\u00fb\3\2\2\2\u0100\u0101\3\2\2\2\u0101\u0103\3\2\2\2\u0102\u00f9\3\2"+
		"\2\2\u0102\u00fa\3\2\2\2\u0103\25\3\2\2\2\u0104\u0105\7\26\2\2\u0105\27"+
		"\3\2\2\2\u0106\u010b\5\32\16\2\u0107\u0108\7\5\2\2\u0108\u010a\5\32\16"+
		"\2\u0109\u0107\3\2\2\2\u010a\u010d\3\2\2\2\u010b\u0109\3\2\2\2\u010b\u010c"+
		"\3\2\2\2\u010c\31\3\2\2\2\u010d\u010b\3\2\2\2\u010e\u010f\5\36\20\2\u010f"+
		"\u0110\7\20\2\2\u0110\u0112\3\2\2\2\u0111\u010e\3\2\2\2\u0111\u0112\3"+
		"\2\2\2\u0112\u0113\3\2\2\2\u0113\u0115\5L\'\2\u0114\u0116\5\16\b\2\u0115"+
		"\u0114\3\2\2\2\u0115\u0116\3\2\2\2\u0116\u011c\3\2\2\2\u0117\u0119\5\34"+
		"\17\2\u0118\u0117\3\2\2\2\u0118\u0119\3\2\2\2\u0119\u011a\3\2\2\2\u011a"+
		"\u011c\7\27\2\2\u011b\u0111\3\2\2\2\u011b\u0118\3\2\2\2\u011c\33\3\2\2"+
		"\2\u011d\u011e\7p\2\2\u011e\u011f\7\30\2\2\u011f\35\3\2\2\2\u0120\u0122"+
		"\7\31\2\2\u0121\u0120\3\2\2\2\u0121\u0122\3\2\2\2\u0122\u0123\3\2\2\2"+
		"\u0123\u0128\5 \21\2\u0124\u0125\7\31\2\2\u0125\u0127\5 \21\2\u0126\u0124"+
		"\3\2\2\2\u0127\u012a\3\2\2\2\u0128\u0126\3\2\2\2\u0128\u0129\3\2\2\2\u0129"+
		"\37\3\2\2\2\u012a\u0128\3\2\2\2\u012b\u012e\7c\2\2\u012c\u012e\5\"\22"+
		"\2\u012d\u012b\3\2\2\2\u012d\u012c\3\2\2\2\u012e!\3\2\2\2\u012f\u0134"+
		"\7p\2\2\u0130\u0131\7\30\2\2\u0131\u0133\7p\2\2\u0132\u0130\3\2\2\2\u0133"+
		"\u0136\3\2\2\2\u0134\u0132\3\2\2\2\u0134\u0135\3\2\2\2\u0135#\3\2\2\2"+
		"\u0136\u0134\3\2\2\2\u0137\u013b\b\23\1\2\u0138\u0139\5\36\20\2\u0139"+
		"\u013a\7\20\2\2\u013a\u013c\3\2\2\2\u013b\u0138\3\2\2\2\u013b\u013c\3"+
		"\2\2\2\u013c\u013d\3\2\2\2\u013d\u014f\5\"\22\2\u013e\u013f\5\36\20\2"+
		"\u013f\u0140\7\20\2\2\u0140\u0141\7\24\2\2\u0141\u0142\5\f\7\2\u0142\u0143"+
		"\7\25\2\2\u0143\u014f\3\2\2\2\u0144\u0146\5\36\20\2\u0145\u0147\5\16\b"+
		"\2\u0146\u0145\3\2\2\2\u0146\u0147\3\2\2\2\u0147\u0148\3\2\2\2\u0148\u0149"+
		"\5&\24\2\u0149\u014a\7\20\2\2\u014a\u014b\7\24\2\2\u014b\u014c\5> \2\u014c"+
		"\u014d\7\25\2\2\u014d\u014f\3\2\2\2\u014e\u0137\3\2\2\2\u014e\u013e\3"+
		"\2\2\2\u014e\u0144\3\2\2\2\u014f\u015e\3\2\2\2\u0150\u0151\f\4\2\2\u0151"+
		"\u0152\7\32\2\2\u0152\u015d\5$\23\5\u0153\u0155\f\3\2\2\u0154\u0156\7"+
		"d\2\2\u0155\u0154\3\2\2\2\u0155\u0156\3\2\2\2\u0156\u0157\3\2\2\2\u0157"+
		"\u0158\7\33\2\2\u0158\u0159\5$\23\2\u0159\u015a\7\23\2\2\u015a\u015b\5"+
		"L\'\2\u015b\u015d\3\2\2\2\u015c\u0150\3\2\2\2\u015c\u0153\3\2\2\2\u015d"+
		"\u0160\3\2\2\2\u015e\u015c\3\2\2\2\u015e\u015f\3\2\2\2\u015f%\3\2\2\2"+
		"\u0160\u015e\3\2\2\2\u0161\u0162\7\24\2\2\u0162\u0167\5(\25\2\u0163\u0164"+
		"\7\5\2\2\u0164\u0166\5(\25\2\u0165\u0163\3\2\2\2\u0166\u0169\3\2\2\2\u0167"+
		"\u0165\3\2\2\2\u0167\u0168\3\2\2\2\u0168\u016a\3\2\2\2\u0169\u0167\3\2"+
		"\2\2\u016a\u016b\7\25\2\2\u016b\'\3\2\2\2\u016c\u016e\7p\2\2\u016d\u016f"+
		"\5\16\b\2\u016e\u016d\3\2\2\2\u016e\u016f\3\2\2\2\u016f)\3\2\2\2\u0170"+
		"\u017c\5f\64\2\u0171\u0172\7\34\2\2\u0172\u0173\7\24\2\2\u0173\u0174\5"+
		"f\64\2\u0174\u0175\7\25\2\2\u0175\u017c\3\2\2\2\u0176\u0177\7\35\2\2\u0177"+
		"\u0178\7\24\2\2\u0178\u0179\5f\64\2\u0179\u017a\7\25\2\2\u017a\u017c\3"+
		"\2\2\2\u017b\u0170\3\2\2\2\u017b\u0171\3\2\2\2\u017b\u0176\3\2\2\2\u017c"+
		"+\3\2\2\2\u017d\u0182\5.\30\2\u017e\u017f\7\5\2\2\u017f\u0181\5.\30\2"+
		"\u0180\u017e\3\2\2\2\u0181\u0184\3\2\2\2\u0182\u0180\3\2\2\2\u0182\u0183"+
		"\3\2\2\2\u0183-\3\2\2\2\u0184\u0182\3\2\2\2\u0185\u0187\5L\'\2\u0186\u0188"+
		"\5\60\31\2\u0187\u0186\3\2\2\2\u0187\u0188\3\2\2\2\u0188/\3\2\2\2\u0189"+
		"\u018a\t\2\2\2\u018a\61\3\2\2\2\u018b\u0191\7 \2\2\u018c\u018d\7 \2\2"+
		"\u018d\u0191\7\21\2\2\u018e\u0191\7!\2\2\u018f\u0191\7\"\2\2\u0190\u018b"+
		"\3\2\2\2\u0190\u018c\3\2\2\2\u0190\u018e\3\2\2\2\u0190\u018f\3\2\2\2\u0191"+
		"\63\3\2\2\2\u0192\u0194\7#\2\2\u0193\u0195\7$\2\2\u0194\u0193\3\2\2\2"+
		"\u0194\u0195\3\2\2\2\u0195\u0196\3\2\2\2\u0196\u0197\5\66\34\2\u0197\u0198"+
		"\5\n\6\2\u0198\65\3\2\2\2\u0199\u019e\58\35\2\u019a\u019b\7\5\2\2\u019b"+
		"\u019d\58\35\2\u019c\u019a\3\2\2\2\u019d\u01a0\3\2\2\2\u019e\u019c\3\2"+
		"\2\2\u019e\u019f\3\2\2\2\u019f\67\3\2\2\2\u01a0\u019e\3\2\2\2\u01a1\u01a3"+
		"\7p\2\2\u01a2\u01a4\5:\36\2\u01a3\u01a2\3\2\2\2\u01a3\u01a4\3\2\2\2\u01a4"+
		"\u01a5\3\2\2\2\u01a5\u01a6\7\24\2\2\u01a6\u01a7\5\n\6\2\u01a7\u01a8\7"+
		"\25\2\2\u01a89\3\2\2\2\u01a9\u01aa\7\24\2\2\u01aa\u01af\7p\2\2\u01ab\u01ac"+
		"\7\5\2\2\u01ac\u01ae\7p\2\2\u01ad\u01ab\3\2\2\2\u01ae\u01b1\3\2\2\2\u01af"+
		"\u01ad\3\2\2\2\u01af\u01b0\3\2\2\2\u01b0\u01b2\3\2\2\2\u01b1\u01af\3\2"+
		"\2\2\u01b2\u01b3\7\25\2\2\u01b3;\3\2\2\2\u01b4\u01b5\7%\2\2\u01b5\u01b9"+
		"\7&\2\2\u01b6\u01b7\5\36\20\2\u01b7\u01b8\7\20\2\2\u01b8\u01ba\3\2\2\2"+
		"\u01b9\u01b6\3\2\2\2\u01b9\u01ba\3\2\2\2\u01ba\u01bb\3\2\2\2\u01bb\u01bd"+
		"\5\"\22\2\u01bc\u01be\5:\36\2\u01bd\u01bc\3\2\2\2\u01bd\u01be\3\2\2\2"+
		"\u01be\u01c3\3\2\2\2\u01bf\u01c0\7\'\2\2\u01c0\u01c4\5> \2\u01c1\u01c4"+
		"\5B\"\2\u01c2\u01c4\5\f\7\2\u01c3\u01bf\3\2\2\2\u01c3\u01c1\3\2\2\2\u01c3"+
		"\u01c2\3\2\2\2\u01c4\u01ca\3\2\2\2\u01c5\u01c7\7(\2\2\u01c6\u01c8\5\16"+
		"\b\2\u01c7\u01c6\3\2\2\2\u01c7\u01c8\3\2\2\2\u01c8\u01c9\3\2\2\2\u01c9"+
		"\u01cb\5\30\r\2\u01ca\u01c5\3\2\2\2\u01ca\u01cb\3\2\2\2\u01cb=\3\2\2\2"+
		"\u01cc\u01d1\5@!\2\u01cd\u01ce\7\5\2\2\u01ce\u01d0\5@!\2\u01cf\u01cd\3"+
		"\2\2\2\u01d0\u01d3\3\2\2\2\u01d1\u01cf\3\2\2\2\u01d1\u01d2\3\2\2\2\u01d2"+
		"?\3\2\2\2\u01d3\u01d1\3\2\2\2\u01d4\u01d5\7\24\2\2\u01d5\u01d6\5f\64\2"+
		"\u01d6\u01d7\7\25\2\2\u01d7A\3\2\2\2\u01d8\u01d9\7)\2\2\u01d9\u01da\7"+
		"\'\2\2\u01daC\3\2\2\2\u01db\u01dc\7*\2\2\u01dc\u01dd\5\36\20\2\u01dd\u01de"+
		"\7\6\2\2\u01de\u01df\5$\23\2\u01df\u01e0\7+\2\2\u01e0\u01e3\5F$\2\u01e1"+
		"\u01e2\7\7\2\2\u01e2\u01e4\5L\'\2\u01e3\u01e1\3\2\2\2\u01e3\u01e4\3\2"+
		"\2\2\u01e4\u01ea\3\2\2\2\u01e5\u01e7\7(\2\2\u01e6\u01e8\5\16\b\2\u01e7"+
		"\u01e6\3\2\2\2\u01e7\u01e8\3\2\2\2\u01e8\u01e9\3\2\2\2\u01e9\u01eb\5\30"+
		"\r\2\u01ea\u01e5\3\2\2\2\u01ea\u01eb\3\2\2\2\u01ebE\3\2\2\2\u01ec\u01f1"+
		"\5H%\2\u01ed\u01ee\7\5\2\2\u01ee\u01f0\5H%\2\u01ef\u01ed\3\2\2\2\u01f0"+
		"\u01f3\3\2\2\2\u01f1\u01ef\3\2\2\2\u01f1\u01f2\3\2\2\2\u01f2G\3\2\2\2"+
		"\u01f3\u01f1\3\2\2\2\u01f4\u01f5\5\"\22\2\u01f5\u01f6\7,\2\2\u01f6\u01f7"+
		"\5L\'\2\u01f7I\3\2\2\2\u01f8\u01f9\7-\2\2\u01f9\u01fa\5\36\20\2\u01fa"+
		"\u01fb\7\6\2\2\u01fb\u01fe\5$\23\2\u01fc\u01fd\7\7\2\2\u01fd\u01ff\5L"+
		"\'\2\u01fe\u01fc\3\2\2\2\u01fe\u01ff\3\2\2\2\u01ff\u0205\3\2\2\2\u0200"+
		"\u0202\7(\2\2\u0201\u0203\5\16\b\2\u0202\u0201\3\2\2\2\u0202\u0203\3\2"+
		"\2\2\u0203\u0204\3\2\2\2\u0204\u0206\5\30\r\2\u0205\u0200\3\2\2\2\u0205"+
		"\u0206\3\2\2\2\u0206K\3\2\2\2\u0207\u0208\b\'\1\2\u0208\u0235\5\f\7\2"+
		"\u0209\u0235\5\b\5\2\u020a\u0235\5p9\2\u020b\u0235\5\6\4\2\u020c\u020d"+
		"\7\24\2\2\u020d\u020e\5L\'\2\u020e\u020f\7\25\2\2\u020f\u0235\3\2\2\2"+
		"\u0210\u0211\7.\2\2\u0211\u0212\5L\'\2\u0212\u0213\7\25\2\2\u0213\u0235"+
		"\3\2\2\2\u0214\u0215\5\u0096L\2\u0215\u0216\7/\2\2\u0216\u0217\5L\'\2"+
		"\u0217\u0218\7\60\2\2\u0218\u0235\3\2\2\2\u0219\u0235\7)\2\2\u021a\u0235"+
		"\5h\65\2\u021b\u021c\7\62\2\2\u021c\u0235\5L\'\30\u021d\u021e\7\20\2\2"+
		"\u021e\u0235\7p\2\2\u021f\u0220\7p\2\2\u0220\u0221\7\66\2\2\u0221\u0235"+
		"\5L\'\23\u0222\u0235\5R*\2\u0223\u0224\7o\2\2\u0224\u0235\5L\'\21\u0225"+
		"\u0226\5\"\22\2\u0226\u0228\7\24\2\2\u0227\u0229\5\24\13\2\u0228\u0227"+
		"\3\2\2\2\u0228\u0229\3\2\2\2\u0229\u022c\3\2\2\2\u022a\u022d\5f\64\2\u022b"+
		"\u022d\7\27\2\2\u022c\u022a\3\2\2\2\u022c\u022b\3\2\2\2\u022c\u022d\3"+
		"\2\2\2\u022d\u022e\3\2\2\2\u022e\u0230\7\25\2\2\u022f\u0231\5T+\2\u0230"+
		"\u022f\3\2\2\2\u0230\u0231\3\2\2\2\u0231\u0235\3\2\2\2\u0232\u0235\5P"+
		")\2\u0233\u0235\5N(\2\u0234\u0207\3\2\2\2\u0234\u0209\3\2\2\2\u0234\u020a"+
		"\3\2\2\2\u0234\u020b\3\2\2\2\u0234\u020c\3\2\2\2\u0234\u0210\3\2\2\2\u0234"+
		"\u0214\3\2\2\2\u0234\u0219\3\2\2\2\u0234\u021a\3\2\2\2\u0234\u021b\3\2"+
		"\2\2\u0234\u021d\3\2\2\2\u0234\u021f\3\2\2\2\u0234\u0222\3\2\2\2\u0234"+
		"\u0223\3\2\2\2\u0234\u0225\3\2\2\2\u0234\u0232\3\2\2\2\u0234\u0233\3\2"+
		"\2\2\u0235\u028d\3\2\2\2\u0236\u0237\f\27\2\2\u0237\u0238\7\63\2\2\u0238"+
		"\u028c\5L\'\27\u0239\u023a\f\26\2\2\u023a\u023b\t\3\2\2\u023b\u028c\5"+
		"L\'\27\u023c\u023d\f\25\2\2\u023d\u023e\t\4\2\2\u023e\u028c\5L\'\26\u023f"+
		"\u0240\f\17\2\2\u0240\u0241\5d\63\2\u0241\u0242\5N(\2\u0242\u0243\5d\63"+
		"\2\u0243\u0244\5L\'\20\u0244\u028c\3\2\2\2\u0245\u0246\f\16\2\2\u0246"+
		"\u0247\5b\62\2\u0247\u0248\5L\'\17\u0248\u028c\3\2\2\2\u0249\u024b\f\13"+
		"\2\2\u024a\u024c\7o\2\2\u024b\u024a\3\2\2\2\u024b\u024c\3\2\2\2\u024c"+
		"\u024d\3\2\2\2\u024d\u024e\78\2\2\u024e\u024f\5L\'\2\u024f\u0250\79\2"+
		"\2\u0250\u0251\5L\'\f\u0251\u028c\3\2\2\2\u0252\u0254\f\n\2\2\u0253\u0255"+
		"\7o\2\2\u0254\u0253\3\2\2\2\u0254\u0255\3\2\2\2\u0255\u0256\3\2\2\2\u0256"+
		"\u0257\7:\2\2\u0257\u028c\5L\'\13\u0258\u025a\f\t\2\2\u0259\u025b\7o\2"+
		"\2\u025a\u0259\3\2\2\2\u025a\u025b\3\2\2\2\u025b\u025c\3\2\2\2\u025c\u025d"+
		"\7;\2\2\u025d\u028c\5L\'\n\u025e\u025f\f\6\2\2\u025f\u0260\79\2\2\u0260"+
		"\u028c\5L\'\7\u0261\u0262\f\5\2\2\u0262\u0263\7=\2\2\u0263\u028c\5L\'"+
		"\6\u0264\u0267\f\31\2\2\u0265\u0266\7\61\2\2\u0266\u0268\5L\'\2\u0267"+
		"\u0265\3\2\2\2\u0268\u0269\3\2\2\2\u0269\u0267\3\2\2\2\u0269\u026a\3\2"+
		"\2\2\u026a\u028c\3\2\2\2\u026b\u026c\f\r\2\2\u026c\u026d\5b\62\2\u026d"+
		"\u026e\7n\2\2\u026e\u026f\7\24\2\2\u026f\u0270\5\f\7\2\u0270\u0271\7\25"+
		"\2\2\u0271\u028c\3\2\2\2\u0272\u0274\f\f\2\2\u0273\u0275\7o\2\2\u0274"+
		"\u0273\3\2\2\2\u0274\u0275\3\2\2\2\u0275\u0276\3\2\2\2\u0276\u0277\7\67"+
		"\2\2\u0277\u0278\7\24\2\2\u0278\u0279\5f\64\2\u0279\u027a\7\25\2\2\u027a"+
		"\u028c\3\2\2\2\u027b\u027c\f\b\2\2\u027c\u027e\7<\2\2\u027d\u027f\7o\2"+
		"\2\u027e\u027d\3\2\2\2\u027e\u027f\3\2\2\2\u027f\u0280\3\2\2\2\u0280\u028c"+
		"\7h\2\2\u0281\u0287\f\4\2\2\u0282\u0283\7>\2\2\u0283\u0284\5L\'\2\u0284"+
		"\u0285\7?\2\2\u0285\u0286\5L\'\2\u0286\u0288\3\2\2\2\u0287\u0282\3\2\2"+
		"\2\u0288\u0289\3\2\2\2\u0289\u0287\3\2\2\2\u0289\u028a\3\2\2\2\u028a\u028c"+
		"\3\2\2\2\u028b\u0236\3\2\2\2\u028b\u0239\3\2\2\2\u028b\u023c\3\2\2\2\u028b"+
		"\u023f\3\2\2\2\u028b\u0245\3\2\2\2\u028b\u0249\3\2\2\2\u028b\u0252\3\2"+
		"\2\2\u028b\u0258\3\2\2\2\u028b\u025e\3\2\2\2\u028b\u0261\3\2\2\2\u028b"+
		"\u0264\3\2\2\2\u028b\u026b\3\2\2\2\u028b\u0272\3\2\2\2\u028b\u027b\3\2"+
		"\2\2\u028b\u0281\3\2\2\2\u028c\u028f\3\2\2\2\u028d\u028b\3\2\2\2\u028d"+
		"\u028e\3\2\2\2\u028eM\3\2\2\2\u028f\u028d\3\2\2\2\u0290\u0291\b(\1\2\u0291"+
		"\u0292\7\24\2\2\u0292\u0293\5N(\2\u0293\u0294\7\25\2\2\u0294\u02ad\3\2"+
		"\2\2\u0295\u0296\5\u0096L\2\u0296\u0297\7/\2\2\u0297\u0298\5N(\2\u0298"+
		"\u0299\7\60\2\2\u0299\u02ad\3\2\2\2\u029a\u02ad\5h\65\2\u029b\u029c\7"+
		"\62\2\2\u029c\u02ad\5N(\n\u029d\u02ad\5R*\2\u029e\u029f\5\"\22\2\u029f"+
		"\u02a1\7\24\2\2\u02a0\u02a2\5\24\13\2\u02a1\u02a0\3\2\2\2\u02a1\u02a2"+
		"\3\2\2\2\u02a2\u02a5\3\2\2\2\u02a3\u02a6\5f\64\2\u02a4\u02a6\7\27\2\2"+
		"\u02a5\u02a3\3\2\2\2\u02a5\u02a4\3\2\2\2\u02a5\u02a6\3\2\2\2\u02a6\u02a7"+
		"\3\2\2\2\u02a7\u02a9\7\25\2\2\u02a8\u02aa\5T+\2\u02a9\u02a8\3\2\2\2\u02a9"+
		"\u02aa\3\2\2\2\u02aa\u02ad\3\2\2\2\u02ab\u02ad\5P)\2\u02ac\u0290\3\2\2"+
		"\2\u02ac\u0295\3\2\2\2\u02ac\u029a\3\2\2\2\u02ac\u029b\3\2\2\2\u02ac\u029d"+
		"\3\2\2\2\u02ac\u029e\3\2\2\2\u02ac\u02ab\3\2\2\2\u02ad\u02ca\3\2\2\2\u02ae"+
		"\u02af\f\t\2\2\u02af\u02b0\7\63\2\2\u02b0\u02c9\5N(\t\u02b1\u02b2\f\b"+
		"\2\2\u02b2\u02b3\t\3\2\2\u02b3\u02c9\5N(\t\u02b4\u02b5\f\7\2\2\u02b5\u02b6"+
		"\t\4\2\2\u02b6\u02c9\5N(\b\u02b7\u02ba\f\13\2\2\u02b8\u02b9\7\61\2\2\u02b9"+
		"\u02bb\5N(\2\u02ba\u02b8\3\2\2\2\u02bb\u02bc\3\2\2\2\u02bc\u02ba\3\2\2"+
		"\2\u02bc\u02bd\3\2\2\2\u02bd\u02c9\3\2\2\2\u02be\u02c4\f\3\2\2\u02bf\u02c0"+
		"\7>\2\2\u02c0\u02c1\5N(\2\u02c1\u02c2\7?\2\2\u02c2\u02c3\5N(\2\u02c3\u02c5"+
		"\3\2\2\2\u02c4\u02bf\3\2\2\2\u02c5\u02c6\3\2\2\2\u02c6\u02c4\3\2\2\2\u02c6"+
		"\u02c7\3\2\2\2\u02c7\u02c9\3\2\2\2\u02c8\u02ae\3\2\2\2\u02c8\u02b1\3\2"+
		"\2\2\u02c8\u02b4\3\2\2\2\u02c8\u02b7\3\2\2\2\u02c8\u02be\3\2\2\2\u02c9"+
		"\u02cc\3\2\2\2\u02ca\u02c8\3\2\2\2\u02ca\u02cb\3\2\2\2\u02cbO\3\2\2\2"+
		"\u02cc\u02ca\3\2\2\2\u02cd\u02cf\5\34\17\2\u02ce\u02cd\3\2\2\2\u02ce\u02cf"+
		"\3\2\2\2\u02cf\u02d0\3\2\2\2\u02d0\u02d1\5\36\20\2\u02d1Q\3\2\2\2\u02d2"+
		"\u02d3\7\6\2\2\u02d3\u02d4\5$\23\2\u02d4\u02d6\7\4\2\2\u02d5\u02d7\5\24"+
		"\13\2\u02d6\u02d5\3\2\2\2\u02d6\u02d7\3\2\2\2\u02d7\u02db\3\2\2\2\u02d8"+
		"\u02d9\5\36\20\2\u02d9\u02da\7\20\2\2\u02da\u02dc\3\2\2\2\u02db\u02d8"+
		"\3\2\2\2\u02db\u02dc\3\2\2\2\u02dc\u02dd\3\2\2\2\u02dd\u02e0\5L\'\2\u02de"+
		"\u02df\7\7\2\2\u02df\u02e1\5L\'\2\u02e0\u02de\3\2\2\2\u02e0\u02e1\3\2"+
		"\2\2\u02e1\u02e5\3\2\2\2\u02e2\u02e3\7\13\2\2\u02e3\u02e4\7\t\2\2\u02e4"+
		"\u02e6\5,\27\2\u02e5\u02e2\3\2\2\2\u02e5\u02e6\3\2\2\2\u02e6\u02e9\3\2"+
		"\2\2\u02e7\u02e8\7\f\2\2\u02e8\u02ea\5L\'\2\u02e9\u02e7\3\2\2\2\u02e9"+
		"\u02ea\3\2\2\2\u02eaS\3\2\2\2\u02eb\u02ec\7@\2\2\u02ec\u02ee\7\24\2\2"+
		"\u02ed\u02ef\5V,\2\u02ee\u02ed\3\2\2\2\u02ee\u02ef\3\2\2\2\u02ef\u02f3"+
		"\3\2\2\2\u02f0\u02f1\7\13\2\2\u02f1\u02f2\7\t\2\2\u02f2\u02f4\5,\27\2"+
		"\u02f3\u02f0\3\2\2\2\u02f3\u02f4\3\2\2\2\u02f4\u02f6\3\2\2\2\u02f5\u02f7"+
		"\5X-\2\u02f6\u02f5\3\2\2\2\u02f6\u02f7\3\2\2\2\u02f7\u02f8\3\2\2\2\u02f8"+
		"\u02f9\7\25\2\2\u02f9U\3\2\2\2\u02fa\u02fb\7A\2\2\u02fb\u02fc\7\t\2\2"+
		"\u02fc\u02fd\5f\64\2\u02fdW\3\2\2\2\u02fe\u02ff\t\5\2\2\u02ff\u0300\7"+
		"8\2\2\u0300\u0301\5Z.\2\u0301\u0302\79\2\2\u0302\u0303\5\\/\2\u0303Y\3"+
		"\2\2\2\u0304\u0305\5^\60\2\u0305\u0306\7D\2\2\u0306\u030d\3\2\2\2\u0307"+
		"\u0308\5`\61\2\u0308\u0309\7E\2\2\u0309\u030d\3\2\2\2\u030a\u030b\7e\2"+
		"\2\u030b\u030d\7D\2\2\u030c\u0304\3\2\2\2\u030c\u0307\3\2\2\2\u030c\u030a"+
		"\3\2\2\2\u030d[\3\2\2\2\u030e\u030f\5^\60\2\u030f\u0310\7F\2\2\u0310\u0317"+
		"\3\2\2\2\u0311\u0312\5`\61\2\u0312\u0313\7E\2\2\u0313\u0317\3\2\2\2\u0314"+
		"\u0315\7e\2\2\u0315\u0317\7F\2\2\u0316\u030e\3\2\2\2\u0316\u0311\3\2\2"+
		"\2\u0316\u0314\3\2\2\2\u0317]\3\2\2\2\u0318\u0319\7G\2\2\u0319_\3\2\2"+
		"\2\u031a\u031b\7H\2\2\u031ba\3\2\2\2\u031c\u031d\t\6\2\2\u031dc\3\2\2"+
		"\2\u031e\u031f\t\7\2\2\u031fe\3\2\2\2\u0320\u0325\5L\'\2\u0321\u0322\7"+
		"\5\2\2\u0322\u0324\5L\'\2\u0323\u0321\3\2\2\2\u0324\u0327\3\2\2\2\u0325"+
		"\u0323\3\2\2\2\u0325\u0326\3\2\2\2\u0326g\3\2\2\2\u0327\u0325\3\2\2\2"+
		"\u0328\u033b\5j\66\2\u0329\u033b\7h\2\2\u032a\u032b\7p\2\2\u032b\u032d"+
		"\7L\2\2\u032c\u032e\5n8\2\u032d\u032c\3\2\2\2\u032d\u032e\3\2\2\2\u032e"+
		"\u032f\3\2\2\2\u032f\u033b\7M\2\2\u0330\u0332\7L\2\2\u0331\u0333\5l\67"+
		"\2\u0332\u0331\3\2\2\2\u0332\u0333\3\2\2\2\u0333\u0334\3\2\2\2\u0334\u033b"+
		"\7M\2\2\u0335\u0337\7\16\2\2\u0336\u0338\5\20\t\2\u0337\u0336\3\2\2\2"+
		"\u0337\u0338\3\2\2\2\u0338\u0339\3\2\2\2\u0339\u033b\7\17\2\2\u033a\u0328"+
		"\3\2\2\2\u033a\u0329\3\2\2\2\u033a\u032a\3\2\2\2\u033a\u0330\3\2\2\2\u033a"+
		"\u0335\3\2\2\2\u033bi\3\2\2\2\u033c\u0345\7e\2\2\u033d\u0345\7f\2\2\u033e"+
		"\u0345\7g\2\2\u033f\u0345\7j\2\2\u0340\u0345\7i\2\2\u0341\u0345\7k\2\2"+
		"\u0342\u0345\7l\2\2\u0343\u0345\7m\2\2\u0344\u033c\3\2\2\2\u0344\u033d"+
		"\3\2\2\2\u0344\u033e\3\2\2\2\u0344\u033f\3\2\2\2\u0344\u0340\3\2\2\2\u0344"+
		"\u0341\3\2\2\2\u0344\u0342\3\2\2\2\u0344\u0343\3\2\2\2\u0345k\3\2\2\2"+
		"\u0346\u034b\5h\65\2\u0347\u0348\7\5\2\2\u0348\u034a\5h\65\2\u0349\u0347"+
		"\3\2\2\2\u034a\u034d\3\2\2\2\u034b\u0349\3\2\2\2\u034b\u034c\3\2\2\2\u034c"+
		"m\3\2\2\2\u034d\u034b\3\2\2\2\u034e\u0353\5j\66\2\u034f\u0350\7\5\2\2"+
		"\u0350\u0352\5j\66\2\u0351\u034f\3\2\2\2\u0352\u0355\3\2\2\2\u0353\u0351"+
		"\3\2\2\2\u0353\u0354\3\2\2\2\u0354o\3\2\2\2\u0355\u0353\3\2\2\2\u0356"+
		"\u035a\5r:\2\u0357\u035a\5\u0088E\2\u0358\u035a\5\u0094K\2\u0359\u0356"+
		"\3\2\2\2\u0359\u0357\3\2\2\2\u0359\u0358\3\2\2\2\u035aq\3\2\2\2\u035b"+
		"\u035c\7N\2\2\u035c\u035d\7O\2\2\u035d\u035f\5\"\22\2\u035e\u0360\5t;"+
		"\2\u035f\u035e\3\2\2\2\u035f\u0360\3\2\2\2\u0360\u0361\3\2\2\2\u0361\u0362"+
		"\7\24\2\2\u0362\u0363\5v<\2\u0363\u0364\7\25\2\2\u0364s\3\2\2\2\u0365"+
		"\u0366\7P\2\2\u0366\u0367\7Q\2\2\u0367u\3\2\2\2\u0368\u036b\5x=\2\u0369"+
		"\u036a\7\5\2\2\u036a\u036c\5x=\2\u036b\u0369\3\2\2\2\u036c\u036d\3\2\2"+
		"\2\u036d\u036b\3\2\2\2\u036d\u036e\3\2\2\2\u036ew\3\2\2\2\u036f\u0374"+
		"\5z>\2\u0370\u0374\5|?\2\u0371\u0374\5~@\2\u0372\u0374\5\16\b\2\u0373"+
		"\u036f\3\2\2\2\u0373\u0370\3\2\2\2\u0373\u0371\3\2\2\2\u0373\u0372\3\2"+
		"\2\2\u0374y\3\2\2\2\u0375\u0376\7p\2\2\u0376\u0379\5\u0096L\2\u0377\u0378"+
		"\7o\2\2\u0378\u037a\7h\2\2\u0379\u0377\3\2\2\2\u0379\u037a\3\2\2\2\u037a"+
		"\u037d\3\2\2\2\u037b\u037c\7)\2\2\u037c\u037e\5L\'\2\u037d\u037b\3\2\2"+
		"\2\u037d\u037e\3\2\2\2\u037e\u0380\3\2\2\2\u037f\u0381\5\16\b\2\u0380"+
		"\u037f\3\2\2\2\u0380\u0381\3\2\2\2\u0381{\3\2\2\2\u0382\u0383\7p\2\2\u0383"+
		"\u0384\7,\2\2\u0384\u0386\5L\'\2\u0385\u0387\5\16\b\2\u0386\u0385\3\2"+
		"\2\2\u0386\u0387\3\2\2\2\u0387}\3\2\2\2\u0388\u038a\5\u0080A\2\u0389\u0388"+
		"\3\2\2\2\u0389\u038a\3\2\2\2\u038a\u038b\3\2\2\2\u038b\u038c\7R\2\2\u038c"+
		"\u03b7\5:\36\2\u038d\u038f\5\u0080A\2\u038e\u038d\3\2\2\2\u038e\u038f"+
		"\3\2\2\2\u038f\u0390\3\2\2\2\u0390\u0391\7S\2\2\u0391\u0392\7T\2\2\u0392"+
		"\u03b7\5:\36\2\u0393\u0395\5\u0080A\2\u0394\u0393\3\2\2\2\u0394\u0395"+
		"\3\2\2\2\u0395\u0396\3\2\2\2\u0396\u0397\7U\2\2\u0397\u0398\7\24\2\2\u0398"+
		"\u0399\5L\'\2\u0399\u039a\7\25\2\2\u039a\u03b7\3\2\2\2\u039b\u039d\5\u0080"+
		"A\2\u039c\u039b\3\2\2\2\u039c\u039d\3\2\2\2\u039d\u039e\3\2\2\2\u039e"+
		"\u039f\7V\2\2\u039f\u03a0\7T\2\2\u03a0\u03a1\5:\36\2\u03a1\u03a2\7W\2"+
		"\2\u03a2\u03a3\5\"\22\2\u03a3\u03ac\5:\36\2\u03a4\u03a5\7X\2\2\u03a5\u03a6"+
		"\7\24\2\2\u03a6\u03a9\7e\2\2\u03a7\u03a8\7\5\2\2\u03a8\u03aa\7e\2\2\u03a9"+
		"\u03a7\3\2\2\2\u03a9\u03aa\3\2\2\2\u03aa\u03ab\3\2\2\2\u03ab\u03ad\7\25"+
		"\2\2\u03ac\u03a4\3\2\2\2\u03ac\u03ad\3\2\2\2\u03ad\u03b0\3\2\2\2\u03ae"+
		"\u03b1\5\u0082B\2\u03af\u03b1\5\u0084C\2\u03b0\u03ae\3\2\2\2\u03b0\u03af"+
		"\3\2\2\2\u03b0\u03b1\3\2\2\2\u03b1\u03b4\3\2\2\2\u03b2\u03b5\5\u0082B"+
		"\2\u03b3\u03b5\5\u0084C\2\u03b4\u03b2\3\2\2\2\u03b4\u03b3\3\2\2\2\u03b4"+
		"\u03b5\3\2\2\2\u03b5\u03b7\3\2\2\2\u03b6\u0389\3\2\2\2\u03b6\u038e\3\2"+
		"\2\2\u03b6\u0394\3\2\2\2\u03b6\u039c\3\2\2\2\u03b7\177\3\2\2\2\u03b8\u03b9"+
		"\7Y\2\2\u03b9\u03ba\7p\2\2\u03ba\u0081\3\2\2\2\u03bb\u03bc\7\23\2\2\u03bc"+
		"\u03bd\7*\2\2\u03bd\u03be\5\u0086D\2\u03be\u0083\3\2\2\2\u03bf\u03c0\7"+
		"\23\2\2\u03c0\u03c1\7-\2\2\u03c1\u03c2\5\u0086D\2\u03c2\u0085\3\2\2\2"+
		"\u03c3\u03ca\7Z\2\2\u03c4\u03ca\7[\2\2\u03c5\u03c6\7+\2\2\u03c6\u03ca"+
		"\7h\2\2\u03c7\u03c8\7+\2\2\u03c8\u03ca\7)\2\2\u03c9\u03c3\3\2\2\2\u03c9"+
		"\u03c4\3\2\2\2\u03c9\u03c5\3\2\2\2\u03c9\u03c7\3\2\2\2\u03ca\u0087\3\2"+
		"\2\2\u03cb\u03cc\7\\\2\2\u03cc\u03cd\7O\2\2\u03cd\u03ce\5\"\22\2\u03ce"+
		"\u03cf\5\u008aF\2\u03cf\u0089\3\2\2\2\u03d0\u03d5\5\u008cG\2\u03d1\u03d2"+
		"\7\5\2\2\u03d2\u03d4\5\u008cG\2\u03d3\u03d1\3\2\2\2\u03d4\u03d7\3\2\2"+
		"\2\u03d5\u03d3\3\2\2\2\u03d5\u03d6\3\2\2\2\u03d6\u008b\3\2\2\2\u03d7\u03d5"+
		"\3\2\2\2\u03d8\u03d9\7]\2\2\u03d9\u03da\7^\2\2\u03da\u03ea\7p\2\2\u03db"+
		"\u03dc\7_\2\2\u03dc\u03ea\5x=\2\u03dd\u03de\7\\\2\2\u03de\u03df\7`\2\2"+
		"\u03df\u03e0\7p\2\2\u03e0\u03ea\5\u008eH\2\u03e1\u03e2\7P\2\2\u03e2\u03e3"+
		"\7`\2\2\u03e3\u03ea\7p\2\2\u03e4\u03e5\7P\2\2\u03e5\u03e6\7Y\2\2\u03e6"+
		"\u03ea\7p\2\2\u03e7\u03e8\7P\2\2\u03e8\u03ea\7a\2\2\u03e9\u03d8\3\2\2"+
		"\2\u03e9\u03db\3\2\2\2\u03e9\u03dd\3\2\2\2\u03e9\u03e1\3\2\2\2\u03e9\u03e4"+
		"\3\2\2\2\u03e9\u03e7\3\2\2\2\u03ea\u008d\3\2\2\2\u03eb\u03ed\7p\2\2\u03ec"+
		"\u03eb\3\2\2\2\u03ec\u03ed\3\2\2\2\u03ed\u03ef\3\2\2\2\u03ee\u03f0\5\u0096"+
		"L\2\u03ef\u03ee\3\2\2\2\u03ef\u03f0\3\2\2\2\u03f0\u03f2\3\2\2\2\u03f1"+
		"\u03f3\5\u0090I\2\u03f2\u03f1\3\2\2\2\u03f2\u03f3\3\2\2\2\u03f3\u03f5"+
		"\3\2\2\2\u03f4\u03f6\5\u0092J\2\u03f5\u03f4\3\2\2\2\u03f5\u03f6\3\2\2"+
		"\2\u03f6\u03f8\3\2\2\2\u03f7\u03f9\5\16\b\2\u03f8\u03f7\3\2\2\2\u03f8"+
		"\u03f9\3\2\2\2\u03f9\u008f\3\2\2\2\u03fa\u03fb\7o\2\2\u03fb\u03fe\7h\2"+
		"\2\u03fc\u03fe\7h\2\2\u03fd\u03fa\3\2\2\2\u03fd\u03fc\3\2\2\2\u03fe\u0091"+
		"\3\2\2\2\u03ff\u0400\7)\2\2\u0400\u0404\5L\'\2\u0401\u0402\7b\2\2\u0402"+
		"\u0404\7)\2\2\u0403\u03ff\3\2\2\2\u0403\u0401\3\2\2\2\u0404\u0093\3\2"+
		"\2\2\u0405\u0406\7P\2\2\u0406\u0407\7O\2\2\u0407\u0408\5\"\22\2\u0408"+
		"\u0095\3\2\2\2\u0409\u040a\bL\1\2\u040a\u040b\7p\2\2\u040b\u0414\3\2\2"+
		"\2\u040c\u040d\f\3\2\2\u040d\u040f\7L\2\2\u040e\u0410\7e\2\2\u040f\u040e"+
		"\3\2\2\2\u040f\u0410\3\2\2\2\u0410\u0411\3\2\2\2\u0411\u0413\7M\2\2\u0412"+
		"\u040c\3\2\2\2\u0413\u0416\3\2\2\2\u0414\u0412\3\2\2\2\u0414\u0415\3\2"+
		"\2\2\u0415\u0097\3\2\2\2\u0416\u0414\3\2\2\2\u0081\u009f\u00a3\u00aa\u00ae"+
		"\u00b4\u00b6\u00b9\u00bc\u00c1\u00c5\u00ca\u00ce\u00d3\u00d7\u00db\u00de"+
		"\u00e6\u00f2\u0100\u0102\u010b\u0111\u0115\u0118\u011b\u0121\u0128\u012d"+
		"\u0134\u013b\u0146\u014e\u0155\u015c\u015e\u0167\u016e\u017b\u0182\u0187"+
		"\u0190\u0194\u019e\u01a3\u01af\u01b9\u01bd\u01c3\u01c7\u01ca\u01d1\u01e3"+
		"\u01e7\u01ea\u01f1\u01fe\u0202\u0205\u0228\u022c\u0230\u0234\u024b\u0254"+
		"\u025a\u0269\u0274\u027e\u0289\u028b\u028d\u02a1\u02a5\u02a9\u02ac\u02bc"+
		"\u02c6\u02c8\u02ca\u02ce\u02d6\u02db\u02e0\u02e5\u02e9\u02ee\u02f3\u02f6"+
		"\u030c\u0316\u0325\u032d\u0332\u0337\u033a\u0344\u034b\u0353\u0359\u035f"+
		"\u036d\u0373\u0379\u037d\u0380\u0386\u0389\u038e\u0394\u039c\u03a9\u03ac"+
		"\u03b0\u03b4\u03b6\u03c9\u03d5\u03e9\u03ec\u03ef\u03f2\u03f5\u03f8\u03fd"+
		"\u0403\u040f\u0414";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}