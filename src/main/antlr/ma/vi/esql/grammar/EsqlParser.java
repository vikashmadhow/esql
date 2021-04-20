// Generated from ma\vi\esql\grammar\Esql.g4 by ANTLR 4.9.2

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
	static { RuntimeMetaData.checkVersion("4.9.2", RuntimeMetaData.VERSION); }

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
		T__87=88, T__88=89, T__89=90, T__90=91, EscapedIdentifier=92, JoinType=93, 
		IntegerLiteral=94, FloatingPointLiteral=95, BooleanLiteral=96, NullLiteral=97, 
		StringLiteral=98, MultiLineStringLiteral=99, UuidLiteral=100, DateLiteral=101, 
		IntervalLiteral=102, Quantifier=103, Not=104, Identifier=105, Comment=106, 
		Whitespace=107;
	public static final int
		RULE_program = 0, RULE_noop = 1, RULE_modify = 2, RULE_queryUpdate = 3, 
		RULE_select = 4, RULE_metadata = 5, RULE_attributeList = 6, RULE_attribute = 7, 
		RULE_distinct = 8, RULE_explicit = 9, RULE_columns = 10, RULE_column = 11, 
		RULE_qualifier = 12, RULE_alias = 13, RULE_aliasPart = 14, RULE_qualifiedName = 15, 
		RULE_tableExpr = 16, RULE_dynamicColumns = 17, RULE_nameWithMetadata = 18, 
		RULE_groupByList = 19, RULE_orderByList = 20, RULE_orderBy = 21, RULE_direction = 22, 
		RULE_setop = 23, RULE_with = 24, RULE_cteList = 25, RULE_cte = 26, RULE_names = 27, 
		RULE_insert = 28, RULE_rows = 29, RULE_row = 30, RULE_defaultValues = 31, 
		RULE_update = 32, RULE_setList = 33, RULE_set = 34, RULE_delete = 35, 
		RULE_expr = 36, RULE_simpleExpr = 37, RULE_columnReference = 38, RULE_selectExpression = 39, 
		RULE_window = 40, RULE_partition = 41, RULE_compare = 42, RULE_ordering = 43, 
		RULE_expressionList = 44, RULE_literal = 45, RULE_baseLiteral = 46, RULE_literalList = 47, 
		RULE_baseLiteralList = 48, RULE_define = 49, RULE_createTable = 50, RULE_dropUndefined = 51, 
		RULE_tableDefinitions = 52, RULE_tableDefinition = 53, RULE_columnDefinition = 54, 
		RULE_derivedColumnDefinition = 55, RULE_constraintDefinition = 56, RULE_constraintName = 57, 
		RULE_onUpdate = 58, RULE_onDelete = 59, RULE_foreignKeyAction = 60, RULE_alterTable = 61, 
		RULE_alterations = 62, RULE_alteration = 63, RULE_alterColumnDefinition = 64, 
		RULE_alterNull = 65, RULE_alterDefault = 66, RULE_dropTable = 67, RULE_type = 68;
	private static String[] makeRuleNames() {
		return new String[] {
			"program", "noop", "modify", "queryUpdate", "select", "metadata", "attributeList", 
			"attribute", "distinct", "explicit", "columns", "column", "qualifier", 
			"alias", "aliasPart", "qualifiedName", "tableExpr", "dynamicColumns", 
			"nameWithMetadata", "groupByList", "orderByList", "orderBy", "direction", 
			"setop", "with", "cteList", "cte", "names", "insert", "rows", "row", 
			"defaultValues", "update", "setList", "set", "delete", "expr", "simpleExpr", 
			"columnReference", "selectExpression", "window", "partition", "compare", 
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
			"'var'", "':='", "'$('", "'<'", "'>'", "'?'", "'||'", "'-'", "'^'", "'%'", 
			"'+'", "'in'", "'between'", "'and'", "'like'", "'ilike'", "'is'", "'or'", 
			"'if'", "'else'", "'over'", "'partition'", "'!='", "'<='", "'>='", "'['", 
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
			null, null, null, null, null, null, null, null, "EscapedIdentifier", 
			"JoinType", "IntegerLiteral", "FloatingPointLiteral", "BooleanLiteral", 
			"NullLiteral", "StringLiteral", "MultiLineStringLiteral", "UuidLiteral", 
			"DateLiteral", "IntervalLiteral", "Quantifier", "Not", "Identifier", 
			"Comment", "Whitespace"
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
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
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
			setState(138);
			expr(0);
			setState(143);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(139);
					match(T__0);
					setState(140);
					expr(0);
					}
					} 
				}
				setState(145);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			}
			setState(147);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__0) {
				{
				setState(146);
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
			setState(149);
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
		enterRule(_localctx, 4, RULE_modify);
		try {
			setState(154);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__39:
				enterOuterAlt(_localctx, 1);
				{
				setState(151);
				update();
				}
				break;
			case T__34:
				enterOuterAlt(_localctx, 2);
				{
				setState(152);
				insert();
				}
				break;
			case T__42:
				enterOuterAlt(_localctx, 3);
				{
				setState(153);
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
		enterRule(_localctx, 6, RULE_queryUpdate);
		try {
			setState(158);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__1:
			case T__32:
				enterOuterAlt(_localctx, 1);
				{
				setState(156);
				select(0);
				}
				break;
			case T__34:
			case T__39:
			case T__42:
				enterOuterAlt(_localctx, 2);
				{
				setState(157);
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
		int _startState = 8;
		enterRecursionRule(_localctx, 8, RULE_select, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(206);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__1:
				{
				_localctx = new BaseSelectionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(161);
				match(T__1);
				setState(166);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
				case 1:
					{
					setState(162);
					metadata();
					setState(164);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==T__2) {
						{
						setState(163);
						match(T__2);
						}
					}

					}
					break;
				}
				setState(169);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__14 || _la==T__15) {
					{
					setState(168);
					distinct();
					}
				}

				setState(172);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__19) {
					{
					setState(171);
					explicit();
					}
				}

				setState(174);
				columns();
				setState(177);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
				case 1:
					{
					setState(175);
					match(T__3);
					setState(176);
					tableExpr(0);
					}
					break;
				}
				setState(181);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
				case 1:
					{
					setState(179);
					match(T__4);
					setState(180);
					((BaseSelectionContext)_localctx).where = expr(0);
					}
					break;
				}
				setState(186);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
				case 1:
					{
					setState(183);
					match(T__5);
					setState(184);
					match(T__6);
					setState(185);
					groupByList();
					}
					break;
				}
				setState(190);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
				case 1:
					{
					setState(188);
					match(T__7);
					setState(189);
					((BaseSelectionContext)_localctx).having = expr(0);
					}
					break;
				}
				setState(195);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
				case 1:
					{
					setState(192);
					match(T__8);
					setState(193);
					match(T__6);
					setState(194);
					orderByList();
					}
					break;
				}
				setState(199);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
				case 1:
					{
					setState(197);
					match(T__9);
					setState(198);
					((BaseSelectionContext)_localctx).offset = expr(0);
					}
					break;
				}
				setState(203);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
				case 1:
					{
					setState(201);
					match(T__10);
					setState(202);
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
				setState(205);
				with();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(214);
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
					setState(208);
					if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
					setState(209);
					setop();
					setState(210);
					select(3);
					}
					} 
				}
				setState(216);
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
		enterRule(_localctx, 10, RULE_metadata);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(217);
			match(T__11);
			setState(218);
			attributeList();
			setState(219);
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
		enterRule(_localctx, 12, RULE_attributeList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(221);
			attribute();
			setState(226);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__2) {
				{
				{
				setState(222);
				match(T__2);
				setState(223);
				attribute();
				}
				}
				setState(228);
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
		enterRule(_localctx, 14, RULE_attribute);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(229);
			match(Identifier);
			setState(230);
			match(T__13);
			setState(231);
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
		enterRule(_localctx, 16, RULE_distinct);
		int _la;
		try {
			setState(242);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__14:
				enterOuterAlt(_localctx, 1);
				{
				setState(233);
				match(T__14);
				}
				break;
			case T__15:
				enterOuterAlt(_localctx, 2);
				{
				setState(234);
				match(T__15);
				setState(240);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__16) {
					{
					setState(235);
					match(T__16);
					setState(236);
					match(T__17);
					setState(237);
					expressionList();
					setState(238);
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
		enterRule(_localctx, 18, RULE_explicit);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(244);
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
		enterRule(_localctx, 20, RULE_columns);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(246);
			column();
			setState(251);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,20,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(247);
					match(T__2);
					setState(248);
					column();
					}
					} 
				}
				setState(253);
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
		enterRule(_localctx, 22, RULE_column);
		int _la;
		try {
			setState(267);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,24,_ctx) ) {
			case 1:
				_localctx = new SingleColumnContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(257);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,21,_ctx) ) {
				case 1:
					{
					setState(254);
					alias();
					setState(255);
					match(T__13);
					}
					break;
				}
				setState(259);
				expr(0);
				setState(261);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
				case 1:
					{
					setState(260);
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
				setState(264);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Identifier) {
					{
					setState(263);
					qualifier();
					}
				}

				setState(266);
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
		enterRule(_localctx, 24, RULE_qualifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(269);
			match(Identifier);
			setState(270);
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
		enterRule(_localctx, 26, RULE_alias);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(273);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__22) {
				{
				setState(272);
				((AliasContext)_localctx).root = match(T__22);
				}
			}

			setState(275);
			aliasPart();
			setState(280);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,26,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(276);
					match(T__22);
					setState(277);
					aliasPart();
					}
					} 
				}
				setState(282);
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
		enterRule(_localctx, 28, RULE_aliasPart);
		try {
			setState(285);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case EscapedIdentifier:
				_localctx = new EscapedAliasPartContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(283);
				match(EscapedIdentifier);
				}
				break;
			case Identifier:
				_localctx = new NormalAliasPartContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(284);
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
			setState(287);
			match(Identifier);
			setState(292);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,28,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(288);
					match(T__21);
					setState(289);
					match(Identifier);
					}
					} 
				}
				setState(294);
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
		int _startState = 32;
		enterRecursionRule(_localctx, 32, RULE_tableExpr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(318);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,31,_ctx) ) {
			case 1:
				{
				_localctx = new SingleTableExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(299);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,29,_ctx) ) {
				case 1:
					{
					setState(296);
					alias();
					setState(297);
					match(T__13);
					}
					break;
				}
				setState(301);
				qualifiedName();
				}
				break;
			case 2:
				{
				_localctx = new SelectTableExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(302);
				alias();
				setState(303);
				match(T__13);
				setState(304);
				match(T__17);
				setState(305);
				select(0);
				setState(306);
				match(T__18);
				}
				break;
			case 3:
				{
				_localctx = new DynamicTableExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(308);
				alias();
				setState(310);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__11) {
					{
					setState(309);
					metadata();
					}
				}

				setState(312);
				dynamicColumns();
				setState(313);
				match(T__13);
				setState(314);
				match(T__17);
				setState(315);
				rows();
				setState(316);
				match(T__18);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(334);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,34,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(332);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,33,_ctx) ) {
					case 1:
						{
						_localctx = new CrossProductTableExprContext(new TableExprContext(_parentctx, _parentState));
						((CrossProductTableExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_tableExpr);
						setState(320);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(321);
						match(T__23);
						setState(322);
						((CrossProductTableExprContext)_localctx).right = tableExpr(3);
						}
						break;
					case 2:
						{
						_localctx = new JoinTableExprContext(new TableExprContext(_parentctx, _parentState));
						((JoinTableExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_tableExpr);
						setState(323);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(325);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==JoinType) {
							{
							setState(324);
							match(JoinType);
							}
						}

						setState(327);
						match(T__24);
						setState(328);
						((JoinTableExprContext)_localctx).right = tableExpr(0);
						setState(329);
						match(T__16);
						setState(330);
						expr(0);
						}
						break;
					}
					} 
				}
				setState(336);
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
		enterRule(_localctx, 34, RULE_dynamicColumns);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(337);
			match(T__17);
			setState(338);
			nameWithMetadata();
			setState(343);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__2) {
				{
				{
				setState(339);
				match(T__2);
				setState(340);
				nameWithMetadata();
				}
				}
				setState(345);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(346);
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
			setState(348);
			match(Identifier);
			setState(350);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__11) {
				{
				setState(349);
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
		enterRule(_localctx, 38, RULE_groupByList);
		try {
			setState(363);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__0:
			case T__1:
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
			case T__45:
			case T__50:
			case T__68:
			case T__70:
			case T__72:
			case T__84:
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
				setState(352);
				expressionList();
				}
				break;
			case T__25:
				_localctx = new RollupGroupContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(353);
				match(T__25);
				setState(354);
				match(T__17);
				setState(355);
				expressionList();
				setState(356);
				match(T__18);
				}
				break;
			case T__26:
				_localctx = new CubeGroupContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(358);
				match(T__26);
				setState(359);
				match(T__17);
				setState(360);
				expressionList();
				setState(361);
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
		enterRule(_localctx, 40, RULE_orderByList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(365);
			orderBy();
			setState(370);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,38,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(366);
					match(T__2);
					setState(367);
					orderBy();
					}
					} 
				}
				setState(372);
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
		enterRule(_localctx, 42, RULE_orderBy);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(373);
			expr(0);
			setState(375);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,39,_ctx) ) {
			case 1:
				{
				setState(374);
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
		enterRule(_localctx, 44, RULE_direction);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(377);
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
		enterRule(_localctx, 46, RULE_setop);
		try {
			setState(384);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,40,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(379);
				match(T__29);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(380);
				match(T__29);
				setState(381);
				match(T__14);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(382);
				match(T__30);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(383);
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
		enterRule(_localctx, 48, RULE_with);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(386);
			match(T__32);
			setState(388);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__33) {
				{
				setState(387);
				((WithContext)_localctx).recursive = match(T__33);
				}
			}

			setState(390);
			cteList();
			setState(391);
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
		enterRule(_localctx, 50, RULE_cteList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(393);
			cte();
			setState(398);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__2) {
				{
				{
				setState(394);
				match(T__2);
				setState(395);
				cte();
				}
				}
				setState(400);
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
		enterRule(_localctx, 52, RULE_cte);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(401);
			match(Identifier);
			setState(403);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,43,_ctx) ) {
			case 1:
				{
				setState(402);
				names();
				}
				break;
			}
			setState(405);
			match(T__17);
			setState(406);
			queryUpdate();
			setState(407);
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
		enterRule(_localctx, 54, RULE_names);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(409);
			match(T__17);
			setState(410);
			match(Identifier);
			setState(415);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__2) {
				{
				{
				setState(411);
				match(T__2);
				setState(412);
				match(Identifier);
				}
				}
				setState(417);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
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
		enterRule(_localctx, 56, RULE_insert);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(420);
			match(T__34);
			setState(421);
			match(T__35);
			setState(425);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,45,_ctx) ) {
			case 1:
				{
				setState(422);
				alias();
				setState(423);
				match(T__13);
				}
				break;
			}
			setState(427);
			qualifiedName();
			setState(429);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__17) {
				{
				setState(428);
				names();
				}
			}

			setState(435);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__36:
				{
				{
				setState(431);
				match(T__36);
				setState(432);
				rows();
				}
				}
				break;
			case T__38:
				{
				setState(433);
				defaultValues();
				}
				break;
			case T__1:
			case T__32:
				{
				setState(434);
				select(0);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(442);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,49,_ctx) ) {
			case 1:
				{
				setState(437);
				match(T__37);
				setState(439);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,48,_ctx) ) {
				case 1:
					{
					setState(438);
					metadata();
					}
					break;
				}
				setState(441);
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
		enterRule(_localctx, 58, RULE_rows);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(444);
			row();
			setState(449);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,50,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(445);
					match(T__2);
					setState(446);
					row();
					}
					} 
				}
				setState(451);
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
		enterRule(_localctx, 60, RULE_row);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(452);
			match(T__17);
			setState(453);
			expressionList();
			setState(454);
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
		enterRule(_localctx, 62, RULE_defaultValues);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(456);
			match(T__38);
			setState(457);
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
		enterRule(_localctx, 64, RULE_update);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(459);
			match(T__39);
			setState(460);
			alias();
			setState(461);
			match(T__3);
			setState(462);
			tableExpr(0);
			setState(463);
			match(T__40);
			setState(464);
			setList();
			setState(467);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,51,_ctx) ) {
			case 1:
				{
				setState(465);
				match(T__4);
				setState(466);
				expr(0);
				}
				break;
			}
			setState(474);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,53,_ctx) ) {
			case 1:
				{
				setState(469);
				match(T__37);
				setState(471);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,52,_ctx) ) {
				case 1:
					{
					setState(470);
					metadata();
					}
					break;
				}
				setState(473);
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
		enterRule(_localctx, 66, RULE_setList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(476);
			set();
			setState(481);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,54,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(477);
					match(T__2);
					setState(478);
					set();
					}
					} 
				}
				setState(483);
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
		enterRule(_localctx, 68, RULE_set);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(484);
			qualifiedName();
			setState(485);
			match(T__41);
			setState(486);
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
		enterRule(_localctx, 70, RULE_delete);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(488);
			match(T__42);
			setState(489);
			alias();
			setState(490);
			match(T__3);
			setState(491);
			tableExpr(0);
			setState(494);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,55,_ctx) ) {
			case 1:
				{
				setState(492);
				match(T__4);
				setState(493);
				expr(0);
				}
				break;
			}
			setState(501);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,57,_ctx) ) {
			case 1:
				{
				setState(496);
				match(T__37);
				setState(498);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,56,_ctx) ) {
				case 1:
					{
					setState(497);
					metadata();
					}
					break;
				}
				setState(500);
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
	public static class AssignmentContext extends ExprContext {
		public TerminalNode Identifier() { return getToken(EsqlParser.Identifier, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public AssignmentContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterAssignment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitAssignment(this);
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
	public static class DeclarationContext extends ExprContext {
		public TerminalNode Identifier() { return getToken(EsqlParser.Identifier, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public DeclarationContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).enterDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsqlListener ) ((EsqlListener)listener).exitDeclaration(this);
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
		int _startState = 72;
		enterRecursionRule(_localctx, 72, RULE_expr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(561);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,63,_ctx) ) {
			case 1:
				{
				_localctx = new SelectStatementContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(504);
				select(0);
				}
				break;
			case 2:
				{
				_localctx = new ModifyStatementContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(505);
				modify();
				}
				break;
			case 3:
				{
				_localctx = new DefineStatementContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(506);
				define();
				}
				break;
			case 4:
				{
				_localctx = new NoopStatementContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(507);
				noop();
				}
				break;
			case 5:
				{
				_localctx = new DeclarationContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(508);
				match(T__43);
				setState(509);
				match(Identifier);
				setState(512);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,58,_ctx) ) {
				case 1:
					{
					setState(510);
					match(T__13);
					setState(511);
					type(0);
					}
					break;
				}
				setState(516);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,59,_ctx) ) {
				case 1:
					{
					setState(514);
					match(T__44);
					setState(515);
					expr(0);
					}
					break;
				}
				}
				break;
			case 6:
				{
				_localctx = new AssignmentContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(518);
				match(Identifier);
				setState(519);
				match(T__44);
				setState(520);
				expr(30);
				}
				break;
			case 7:
				{
				_localctx = new GroupingExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(521);
				match(T__17);
				setState(522);
				expr(0);
				setState(523);
				match(T__18);
				}
				break;
			case 8:
				{
				_localctx = new UncomputedExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(525);
				match(T__45);
				setState(526);
				expr(0);
				setState(527);
				match(T__18);
				}
				break;
			case 9:
				{
				_localctx = new CastExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(529);
				type(0);
				setState(530);
				match(T__46);
				setState(531);
				expr(0);
				setState(532);
				match(T__47);
				}
				break;
			case 10:
				{
				_localctx = new DefaultValueContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(534);
				match(T__38);
				}
				break;
			case 11:
				{
				_localctx = new LiteralExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(535);
				literal();
				}
				break;
			case 12:
				{
				_localctx = new NegationExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(536);
				match(T__50);
				setState(537);
				expr(22);
				}
				break;
			case 13:
				{
				_localctx = new NamedParameterContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(538);
				match(T__13);
				setState(539);
				match(Identifier);
				}
				break;
			case 14:
				{
				_localctx = new NamedArgumentContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(540);
				match(Identifier);
				setState(541);
				match(T__44);
				setState(542);
				expr(17);
				}
				break;
			case 15:
				{
				_localctx = new SelectExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(543);
				selectExpression();
				}
				break;
			case 16:
				{
				_localctx = new NotExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(544);
				match(Not);
				setState(545);
				expr(15);
				}
				break;
			case 17:
				{
				_localctx = new FunctionInvocationContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(546);
				qualifiedName();
				setState(547);
				match(T__17);
				setState(549);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__14 || _la==T__15) {
					{
					setState(548);
					distinct();
					}
				}

				setState(553);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case T__0:
				case T__1:
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
				case T__45:
				case T__50:
				case T__68:
				case T__70:
				case T__72:
				case T__84:
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
					setState(551);
					expressionList();
					}
					break;
				case T__20:
					{
					setState(552);
					((FunctionInvocationContext)_localctx).star = match(T__20);
					}
					break;
				case T__18:
					break;
				default:
					break;
				}
				setState(555);
				match(T__18);
				setState(557);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,62,_ctx) ) {
				case 1:
					{
					setState(556);
					window();
					}
					break;
				}
				}
				break;
			case 18:
				{
				_localctx = new ColumnExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(559);
				columnReference();
				}
				break;
			case 19:
				{
				_localctx = new SimpleExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(560);
				simpleExpr(0);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(657);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,73,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(655);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,72,_ctx) ) {
					case 1:
						{
						_localctx = new ExponentiationExprContext(new ExprContext(_parentctx, _parentState));
						((ExponentiationExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(563);
						if (!(precpred(_ctx, 21))) throw new FailedPredicateException(this, "precpred(_ctx, 21)");
						setState(564);
						match(T__51);
						setState(565);
						((ExponentiationExprContext)_localctx).right = expr(21);
						}
						break;
					case 2:
						{
						_localctx = new MultiplicationExprContext(new ExprContext(_parentctx, _parentState));
						((MultiplicationExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(566);
						if (!(precpred(_ctx, 20))) throw new FailedPredicateException(this, "precpred(_ctx, 20)");
						setState(567);
						((MultiplicationExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__20) | (1L << T__22) | (1L << T__52))) != 0)) ) {
							((MultiplicationExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(568);
						((MultiplicationExprContext)_localctx).right = expr(21);
						}
						break;
					case 3:
						{
						_localctx = new AdditionExprContext(new ExprContext(_parentctx, _parentState));
						((AdditionExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(569);
						if (!(precpred(_ctx, 19))) throw new FailedPredicateException(this, "precpred(_ctx, 19)");
						setState(570);
						((AdditionExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==T__50 || _la==T__53) ) {
							((AdditionExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(571);
						((AdditionExprContext)_localctx).right = expr(20);
						}
						break;
					case 4:
						{
						_localctx = new RangeExprContext(new ExprContext(_parentctx, _parentState));
						((RangeExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(572);
						if (!(precpred(_ctx, 13))) throw new FailedPredicateException(this, "precpred(_ctx, 13)");
						setState(573);
						((RangeExprContext)_localctx).leftCompare = ordering();
						setState(574);
						((RangeExprContext)_localctx).mid = simpleExpr(0);
						setState(575);
						((RangeExprContext)_localctx).rightCompare = ordering();
						setState(576);
						((RangeExprContext)_localctx).right = expr(14);
						}
						break;
					case 5:
						{
						_localctx = new ComparisonContext(new ExprContext(_parentctx, _parentState));
						((ComparisonContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(578);
						if (!(precpred(_ctx, 12))) throw new FailedPredicateException(this, "precpred(_ctx, 12)");
						setState(579);
						compare();
						setState(580);
						((ComparisonContext)_localctx).right = expr(13);
						}
						break;
					case 6:
						{
						_localctx = new BetweenExprContext(new ExprContext(_parentctx, _parentState));
						((BetweenExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(582);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(584);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==Not) {
							{
							setState(583);
							match(Not);
							}
						}

						setState(586);
						match(T__55);
						setState(587);
						((BetweenExprContext)_localctx).mid = expr(0);
						setState(588);
						match(T__56);
						setState(589);
						((BetweenExprContext)_localctx).right = expr(10);
						}
						break;
					case 7:
						{
						_localctx = new LikeExprContext(new ExprContext(_parentctx, _parentState));
						((LikeExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(591);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(593);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==Not) {
							{
							setState(592);
							match(Not);
							}
						}

						setState(595);
						match(T__57);
						setState(596);
						((LikeExprContext)_localctx).right = expr(9);
						}
						break;
					case 8:
						{
						_localctx = new ILikeExprContext(new ExprContext(_parentctx, _parentState));
						((ILikeExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(597);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(599);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==Not) {
							{
							setState(598);
							match(Not);
							}
						}

						setState(601);
						match(T__58);
						setState(602);
						((ILikeExprContext)_localctx).right = expr(8);
						}
						break;
					case 9:
						{
						_localctx = new LogicalAndExprContext(new ExprContext(_parentctx, _parentState));
						((LogicalAndExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(603);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(604);
						match(T__56);
						setState(605);
						((LogicalAndExprContext)_localctx).right = expr(5);
						}
						break;
					case 10:
						{
						_localctx = new LogicalOrExprContext(new ExprContext(_parentctx, _parentState));
						((LogicalOrExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(606);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(607);
						match(T__60);
						setState(608);
						((LogicalOrExprContext)_localctx).right = expr(4);
						}
						break;
					case 11:
						{
						_localctx = new CoalesceExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(609);
						if (!(precpred(_ctx, 24))) throw new FailedPredicateException(this, "precpred(_ctx, 24)");
						setState(612); 
						_errHandler.sync(this);
						_alt = 1;
						do {
							switch (_alt) {
							case 1:
								{
								{
								setState(610);
								match(T__48);
								setState(611);
								expr(0);
								}
								}
								break;
							default:
								throw new NoViableAltException(this);
							}
							setState(614); 
							_errHandler.sync(this);
							_alt = getInterpreter().adaptivePredict(_input,67,_ctx);
						} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
						}
						break;
					case 12:
						{
						_localctx = new ConcatenationExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(616);
						if (!(precpred(_ctx, 23))) throw new FailedPredicateException(this, "precpred(_ctx, 23)");
						setState(619); 
						_errHandler.sync(this);
						_alt = 1;
						do {
							switch (_alt) {
							case 1:
								{
								{
								setState(617);
								match(T__49);
								setState(618);
								expr(0);
								}
								}
								break;
							default:
								throw new NoViableAltException(this);
							}
							setState(621); 
							_errHandler.sync(this);
							_alt = getInterpreter().adaptivePredict(_input,68,_ctx);
						} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
						}
						break;
					case 13:
						{
						_localctx = new QuantifiedComparisonContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(623);
						if (!(precpred(_ctx, 11))) throw new FailedPredicateException(this, "precpred(_ctx, 11)");
						setState(624);
						compare();
						setState(625);
						match(Quantifier);
						setState(626);
						match(T__17);
						setState(627);
						select(0);
						setState(628);
						match(T__18);
						}
						break;
					case 14:
						{
						_localctx = new InExpressionContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(630);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(632);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==Not) {
							{
							setState(631);
							match(Not);
							}
						}

						setState(634);
						match(T__54);
						setState(635);
						match(T__17);
						{
						setState(636);
						expressionList();
						}
						setState(637);
						match(T__18);
						}
						break;
					case 15:
						{
						_localctx = new NullCheckExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(639);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(640);
						match(T__59);
						setState(642);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==Not) {
							{
							setState(641);
							match(Not);
							}
						}

						setState(644);
						match(NullLiteral);
						}
						break;
					case 16:
						{
						_localctx = new CaseExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(645);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(651); 
						_errHandler.sync(this);
						_alt = 1;
						do {
							switch (_alt) {
							case 1:
								{
								{
								setState(646);
								match(T__61);
								setState(647);
								expr(0);
								setState(648);
								match(T__62);
								setState(649);
								expr(0);
								}
								}
								break;
							default:
								throw new NoViableAltException(this);
							}
							setState(653); 
							_errHandler.sync(this);
							_alt = getInterpreter().adaptivePredict(_input,71,_ctx);
						} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
						}
						break;
					}
					} 
				}
				setState(659);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,73,_ctx);
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
		int _startState = 74;
		enterRecursionRule(_localctx, 74, RULE_simpleExpr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(688);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,77,_ctx) ) {
			case 1:
				{
				_localctx = new SimpleGroupingExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(661);
				match(T__17);
				setState(662);
				simpleExpr(0);
				setState(663);
				match(T__18);
				}
				break;
			case 2:
				{
				_localctx = new SimpleCastExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(665);
				type(0);
				setState(666);
				match(T__46);
				setState(667);
				simpleExpr(0);
				setState(668);
				match(T__47);
				}
				break;
			case 3:
				{
				_localctx = new SimpleLiteralExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(670);
				literal();
				}
				break;
			case 4:
				{
				_localctx = new SimpleNegationExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(671);
				match(T__50);
				setState(672);
				simpleExpr(8);
				}
				break;
			case 5:
				{
				_localctx = new SimpleSelectExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(673);
				selectExpression();
				}
				break;
			case 6:
				{
				_localctx = new SimpleFunctionInvocationContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(674);
				qualifiedName();
				setState(675);
				match(T__17);
				setState(677);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__14 || _la==T__15) {
					{
					setState(676);
					distinct();
					}
				}

				setState(681);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case T__0:
				case T__1:
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
				case T__45:
				case T__50:
				case T__68:
				case T__70:
				case T__72:
				case T__84:
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
					setState(679);
					expressionList();
					}
					break;
				case T__20:
					{
					setState(680);
					((SimpleFunctionInvocationContext)_localctx).star = match(T__20);
					}
					break;
				case T__18:
					break;
				default:
					break;
				}
				setState(683);
				match(T__18);
				setState(685);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,76,_ctx) ) {
				case 1:
					{
					setState(684);
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
				setState(687);
				columnReference();
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(725);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,82,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(723);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,81,_ctx) ) {
					case 1:
						{
						_localctx = new SimpleExponentiationExprContext(new SimpleExprContext(_parentctx, _parentState));
						((SimpleExponentiationExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_simpleExpr);
						setState(690);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(691);
						match(T__51);
						setState(692);
						((SimpleExponentiationExprContext)_localctx).right = simpleExpr(7);
						}
						break;
					case 2:
						{
						_localctx = new SimpleMultiplicationExprContext(new SimpleExprContext(_parentctx, _parentState));
						((SimpleMultiplicationExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_simpleExpr);
						setState(693);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(694);
						((SimpleMultiplicationExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__20) | (1L << T__22) | (1L << T__52))) != 0)) ) {
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
					case 3:
						{
						_localctx = new SimpleAdditionExprContext(new SimpleExprContext(_parentctx, _parentState));
						((SimpleAdditionExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_simpleExpr);
						setState(696);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(697);
						((SimpleAdditionExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==T__50 || _la==T__53) ) {
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
					case 4:
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
								match(T__48);
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
							_alt = getInterpreter().adaptivePredict(_input,78,_ctx);
						} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
						}
						break;
					case 5:
						{
						_localctx = new SimpleConcatenationExprContext(new SimpleExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_simpleExpr);
						setState(706);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(709); 
						_errHandler.sync(this);
						_alt = 1;
						do {
							switch (_alt) {
							case 1:
								{
								{
								setState(707);
								match(T__49);
								setState(708);
								simpleExpr(0);
								}
								}
								break;
							default:
								throw new NoViableAltException(this);
							}
							setState(711); 
							_errHandler.sync(this);
							_alt = getInterpreter().adaptivePredict(_input,79,_ctx);
						} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
						}
						break;
					case 6:
						{
						_localctx = new SimpleCaseExprContext(new SimpleExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_simpleExpr);
						setState(713);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(719); 
						_errHandler.sync(this);
						_alt = 1;
						do {
							switch (_alt) {
							case 1:
								{
								{
								setState(714);
								match(T__61);
								setState(715);
								simpleExpr(0);
								setState(716);
								match(T__62);
								setState(717);
								simpleExpr(0);
								}
								}
								break;
							default:
								throw new NoViableAltException(this);
							}
							setState(721); 
							_errHandler.sync(this);
							_alt = getInterpreter().adaptivePredict(_input,80,_ctx);
						} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
						}
						break;
					}
					} 
				}
				setState(727);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,82,_ctx);
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
		enterRule(_localctx, 76, RULE_columnReference);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(729);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,83,_ctx) ) {
			case 1:
				{
				setState(728);
				qualifier();
				}
				break;
			}
			setState(731);
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
		enterRule(_localctx, 78, RULE_selectExpression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(733);
			match(T__17);
			setState(735);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,84,_ctx) ) {
			case 1:
				{
				setState(734);
				match(T__1);
				}
				break;
			}
			setState(738);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__14 || _la==T__15) {
				{
				setState(737);
				distinct();
				}
			}

			setState(743);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,86,_ctx) ) {
			case 1:
				{
				setState(740);
				alias();
				setState(741);
				match(T__13);
				}
				break;
			}
			setState(745);
			((SelectExpressionContext)_localctx).col = expr(0);
			setState(746);
			match(T__3);
			setState(747);
			tableExpr(0);
			setState(750);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__4) {
				{
				setState(748);
				match(T__4);
				setState(749);
				((SelectExpressionContext)_localctx).where = expr(0);
				}
			}

			setState(755);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__8) {
				{
				setState(752);
				match(T__8);
				setState(753);
				match(T__6);
				setState(754);
				orderByList();
				}
			}

			setState(759);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__9) {
				{
				setState(757);
				match(T__9);
				setState(758);
				((SelectExpressionContext)_localctx).offset = expr(0);
				}
			}

			setState(761);
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
			setState(763);
			match(T__63);
			setState(764);
			match(T__17);
			setState(766);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__64) {
				{
				setState(765);
				partition();
				}
			}

			setState(771);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__8) {
				{
				setState(768);
				match(T__8);
				setState(769);
				match(T__6);
				setState(770);
				orderByList();
				}
			}

			setState(773);
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
			setState(775);
			match(T__64);
			setState(776);
			match(T__6);
			setState(777);
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
			setState(779);
			_la = _input.LA(1);
			if ( !(((((_la - 42)) & ~0x3f) == 0 && ((1L << (_la - 42)) & ((1L << (T__41 - 42)) | (1L << (T__46 - 42)) | (1L << (T__47 - 42)) | (1L << (T__65 - 42)) | (1L << (T__66 - 42)) | (1L << (T__67 - 42)))) != 0)) ) {
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
			setState(781);
			_la = _input.LA(1);
			if ( !(((((_la - 47)) & ~0x3f) == 0 && ((1L << (_la - 47)) & ((1L << (T__46 - 47)) | (1L << (T__47 - 47)) | (1L << (T__66 - 47)) | (1L << (T__67 - 47)))) != 0)) ) {
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
			setState(783);
			expr(0);
			setState(788);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,92,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(784);
					match(T__2);
					setState(785);
					expr(0);
					}
					} 
				}
				setState(790);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,92,_ctx);
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
		enterRule(_localctx, 90, RULE_literal);
		int _la;
		try {
			setState(809);
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
				setState(791);
				baseLiteral();
				}
				break;
			case NullLiteral:
				_localctx = new NullContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(792);
				match(NullLiteral);
				}
				break;
			case Identifier:
				_localctx = new BaseArrayLiteralContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(793);
				match(Identifier);
				setState(794);
				match(T__68);
				setState(796);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (((((_la - 94)) & ~0x3f) == 0 && ((1L << (_la - 94)) & ((1L << (IntegerLiteral - 94)) | (1L << (FloatingPointLiteral - 94)) | (1L << (BooleanLiteral - 94)) | (1L << (StringLiteral - 94)) | (1L << (MultiLineStringLiteral - 94)) | (1L << (UuidLiteral - 94)) | (1L << (DateLiteral - 94)) | (1L << (IntervalLiteral - 94)))) != 0)) {
					{
					setState(795);
					baseLiteralList();
					}
				}

				setState(798);
				match(T__69);
				}
				break;
			case T__68:
				_localctx = new JsonArrayLiteralContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(799);
				match(T__68);
				setState(801);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__11 || ((((_la - 69)) & ~0x3f) == 0 && ((1L << (_la - 69)) & ((1L << (T__68 - 69)) | (1L << (IntegerLiteral - 69)) | (1L << (FloatingPointLiteral - 69)) | (1L << (BooleanLiteral - 69)) | (1L << (NullLiteral - 69)) | (1L << (StringLiteral - 69)) | (1L << (MultiLineStringLiteral - 69)) | (1L << (UuidLiteral - 69)) | (1L << (DateLiteral - 69)) | (1L << (IntervalLiteral - 69)) | (1L << (Identifier - 69)))) != 0)) {
					{
					setState(800);
					literalList();
					}
				}

				setState(803);
				match(T__69);
				}
				break;
			case T__11:
				_localctx = new JsonObjectLiteralContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(804);
				match(T__11);
				setState(806);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Identifier) {
					{
					setState(805);
					attributeList();
					}
				}

				setState(808);
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
			setState(819);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IntegerLiteral:
				_localctx = new IntegerContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(811);
				match(IntegerLiteral);
				}
				break;
			case FloatingPointLiteral:
				_localctx = new FloatingPointContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(812);
				match(FloatingPointLiteral);
				}
				break;
			case BooleanLiteral:
				_localctx = new BooleanContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(813);
				match(BooleanLiteral);
				}
				break;
			case MultiLineStringLiteral:
				_localctx = new MultiLineStringContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(814);
				match(MultiLineStringLiteral);
				}
				break;
			case StringLiteral:
				_localctx = new StringContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(815);
				match(StringLiteral);
				}
				break;
			case UuidLiteral:
				_localctx = new UuidContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(816);
				match(UuidLiteral);
				}
				break;
			case DateLiteral:
				_localctx = new DateContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(817);
				match(DateLiteral);
				}
				break;
			case IntervalLiteral:
				_localctx = new IntervalContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(818);
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
			setState(821);
			literal();
			setState(826);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__2) {
				{
				{
				setState(822);
				match(T__2);
				setState(823);
				literal();
				}
				}
				setState(828);
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
			setState(829);
			baseLiteral();
			setState(834);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__2) {
				{
				{
				setState(830);
				match(T__2);
				setState(831);
				baseLiteral();
				}
				}
				setState(836);
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
		enterRule(_localctx, 98, RULE_define);
		try {
			setState(840);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__70:
				enterOuterAlt(_localctx, 1);
				{
				setState(837);
				createTable();
				}
				break;
			case T__84:
				enterOuterAlt(_localctx, 2);
				{
				setState(838);
				alterTable();
				}
				break;
			case T__72:
				enterOuterAlt(_localctx, 3);
				{
				setState(839);
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
		enterRule(_localctx, 100, RULE_createTable);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(842);
			match(T__70);
			setState(843);
			match(T__71);
			setState(844);
			qualifiedName();
			setState(846);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__72) {
				{
				setState(845);
				dropUndefined();
				}
			}

			setState(848);
			match(T__17);
			setState(849);
			tableDefinitions();
			setState(850);
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
		enterRule(_localctx, 102, RULE_dropUndefined);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(852);
			match(T__72);
			setState(853);
			match(T__73);
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
		enterRule(_localctx, 104, RULE_tableDefinitions);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(855);
			tableDefinition();
			setState(858); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(856);
				match(T__2);
				setState(857);
				tableDefinition();
				}
				}
				setState(860); 
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
		enterRule(_localctx, 106, RULE_tableDefinition);
		try {
			setState(866);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,103,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(862);
				columnDefinition();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(863);
				derivedColumnDefinition();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(864);
				constraintDefinition();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(865);
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
		enterRule(_localctx, 108, RULE_columnDefinition);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(868);
			match(Identifier);
			setState(869);
			type(0);
			setState(872);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,104,_ctx) ) {
			case 1:
				{
				setState(870);
				match(Not);
				setState(871);
				match(NullLiteral);
				}
				break;
			}
			setState(876);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,105,_ctx) ) {
			case 1:
				{
				setState(874);
				match(T__38);
				setState(875);
				expr(0);
				}
				break;
			}
			setState(879);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,106,_ctx) ) {
			case 1:
				{
				setState(878);
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
		enterRule(_localctx, 110, RULE_derivedColumnDefinition);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(881);
			match(Identifier);
			setState(882);
			match(T__41);
			setState(883);
			expr(0);
			setState(885);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,107,_ctx) ) {
			case 1:
				{
				setState(884);
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
		enterRule(_localctx, 112, RULE_constraintDefinition);
		int _la;
		try {
			setState(933);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,116,_ctx) ) {
			case 1:
				_localctx = new UniqueConstraintContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(888);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__81) {
					{
					setState(887);
					constraintName();
					}
				}

				setState(890);
				match(T__74);
				setState(891);
				names();
				}
				break;
			case 2:
				_localctx = new PrimaryKeyConstraintContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(893);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__81) {
					{
					setState(892);
					constraintName();
					}
				}

				setState(895);
				match(T__75);
				setState(896);
				match(T__76);
				setState(897);
				names();
				}
				break;
			case 3:
				_localctx = new CheckConstraintContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(899);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__81) {
					{
					setState(898);
					constraintName();
					}
				}

				setState(901);
				match(T__77);
				setState(902);
				match(T__17);
				setState(903);
				expr(0);
				setState(904);
				match(T__18);
				}
				break;
			case 4:
				_localctx = new ForeignKeyConstraintContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(907);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__81) {
					{
					setState(906);
					constraintName();
					}
				}

				setState(909);
				match(T__78);
				setState(910);
				match(T__76);
				setState(911);
				((ForeignKeyConstraintContext)_localctx).from = names();
				setState(912);
				match(T__79);
				setState(913);
				qualifiedName();
				setState(914);
				((ForeignKeyConstraintContext)_localctx).to = names();
				setState(923);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,113,_ctx) ) {
				case 1:
					{
					setState(915);
					match(T__80);
					setState(916);
					match(T__17);
					setState(917);
					((ForeignKeyConstraintContext)_localctx).forwardcost = match(IntegerLiteral);
					setState(920);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==T__2) {
						{
						setState(918);
						match(T__2);
						setState(919);
						((ForeignKeyConstraintContext)_localctx).reversecost = match(IntegerLiteral);
						}
					}

					setState(922);
					match(T__18);
					}
					break;
				}
				setState(927);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,114,_ctx) ) {
				case 1:
					{
					setState(925);
					onUpdate();
					}
					break;
				case 2:
					{
					setState(926);
					onDelete();
					}
					break;
				}
				setState(931);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,115,_ctx) ) {
				case 1:
					{
					setState(929);
					onUpdate();
					}
					break;
				case 2:
					{
					setState(930);
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
		enterRule(_localctx, 114, RULE_constraintName);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(935);
			match(T__81);
			setState(936);
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
		enterRule(_localctx, 116, RULE_onUpdate);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(938);
			match(T__16);
			setState(939);
			match(T__39);
			setState(940);
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
		enterRule(_localctx, 118, RULE_onDelete);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(942);
			match(T__16);
			setState(943);
			match(T__42);
			setState(944);
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
		enterRule(_localctx, 120, RULE_foreignKeyAction);
		try {
			setState(952);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,117,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(946);
				match(T__82);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(947);
				match(T__83);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(948);
				match(T__40);
				setState(949);
				match(NullLiteral);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(950);
				match(T__40);
				setState(951);
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
		enterRule(_localctx, 122, RULE_alterTable);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(954);
			match(T__84);
			setState(955);
			match(T__71);
			setState(956);
			qualifiedName();
			setState(957);
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
		enterRule(_localctx, 124, RULE_alterations);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(959);
			alteration();
			setState(964);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,118,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(960);
					match(T__2);
					setState(961);
					alteration();
					}
					} 
				}
				setState(966);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,118,_ctx);
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
		enterRule(_localctx, 126, RULE_alteration);
		try {
			setState(984);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,119,_ctx) ) {
			case 1:
				_localctx = new RenameTableContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(967);
				match(T__85);
				setState(968);
				match(T__86);
				setState(969);
				match(Identifier);
				}
				break;
			case 2:
				_localctx = new AddTableDefinitionContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(970);
				match(T__87);
				setState(971);
				tableDefinition();
				}
				break;
			case 3:
				_localctx = new AlterColumnContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(972);
				match(T__84);
				setState(973);
				match(T__88);
				setState(974);
				match(Identifier);
				setState(975);
				alterColumnDefinition();
				}
				break;
			case 4:
				_localctx = new DropColumnContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(976);
				match(T__72);
				setState(977);
				match(T__88);
				setState(978);
				match(Identifier);
				}
				break;
			case 5:
				_localctx = new DropConstraintContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(979);
				match(T__72);
				setState(980);
				match(T__81);
				setState(981);
				match(Identifier);
				}
				break;
			case 6:
				_localctx = new DropTableMetadataContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(982);
				match(T__72);
				setState(983);
				match(T__89);
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
		enterRule(_localctx, 128, RULE_alterColumnDefinition);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(987);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,120,_ctx) ) {
			case 1:
				{
				setState(986);
				match(Identifier);
				}
				break;
			}
			setState(990);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,121,_ctx) ) {
			case 1:
				{
				setState(989);
				type(0);
				}
				break;
			}
			setState(993);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,122,_ctx) ) {
			case 1:
				{
				setState(992);
				alterNull();
				}
				break;
			}
			setState(996);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,123,_ctx) ) {
			case 1:
				{
				setState(995);
				alterDefault();
				}
				break;
			}
			setState(999);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,124,_ctx) ) {
			case 1:
				{
				setState(998);
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
		enterRule(_localctx, 130, RULE_alterNull);
		try {
			setState(1004);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Not:
				enterOuterAlt(_localctx, 1);
				{
				setState(1001);
				match(Not);
				setState(1002);
				match(NullLiteral);
				}
				break;
			case NullLiteral:
				enterOuterAlt(_localctx, 2);
				{
				setState(1003);
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
		enterRule(_localctx, 132, RULE_alterDefault);
		try {
			setState(1010);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__38:
				enterOuterAlt(_localctx, 1);
				{
				setState(1006);
				match(T__38);
				setState(1007);
				expr(0);
				}
				break;
			case T__90:
				enterOuterAlt(_localctx, 2);
				{
				setState(1008);
				match(T__90);
				setState(1009);
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
		enterRule(_localctx, 134, RULE_dropTable);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1012);
			match(T__72);
			setState(1013);
			match(T__71);
			setState(1014);
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
		int _startState = 136;
		enterRecursionRule(_localctx, 136, RULE_type, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			_localctx = new BaseContext(_localctx);
			_ctx = _localctx;
			_prevctx = _localctx;

			setState(1017);
			match(Identifier);
			}
			_ctx.stop = _input.LT(-1);
			setState(1027);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,128,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new ArrayContext(new TypeContext(_parentctx, _parentState));
					pushNewRecursionContext(_localctx, _startState, RULE_type);
					setState(1019);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(1020);
					match(T__68);
					setState(1022);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==IntegerLiteral) {
						{
						setState(1021);
						match(IntegerLiteral);
						}
					}

					setState(1024);
					match(T__69);
					}
					} 
				}
				setState(1029);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,128,_ctx);
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
		case 4:
			return select_sempred((SelectContext)_localctx, predIndex);
		case 16:
			return tableExpr_sempred((TableExprContext)_localctx, predIndex);
		case 36:
			return expr_sempred((ExprContext)_localctx, predIndex);
		case 37:
			return simpleExpr_sempred((SimpleExprContext)_localctx, predIndex);
		case 68:
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
			return precpred(_ctx, 24);
		case 14:
			return precpred(_ctx, 23);
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
			return precpred(_ctx, 7);
		case 20:
			return precpred(_ctx, 6);
		case 21:
			return precpred(_ctx, 5);
		case 22:
			return precpred(_ctx, 10);
		case 23:
			return precpred(_ctx, 9);
		case 24:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean type_sempred(TypeContext _localctx, int predIndex) {
		switch (predIndex) {
		case 25:
			return precpred(_ctx, 1);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3m\u0409\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\3\2\3\2\3\2\7\2"+
		"\u0090\n\2\f\2\16\2\u0093\13\2\3\2\5\2\u0096\n\2\3\3\3\3\3\4\3\4\3\4\5"+
		"\4\u009d\n\4\3\5\3\5\5\5\u00a1\n\5\3\6\3\6\3\6\3\6\5\6\u00a7\n\6\5\6\u00a9"+
		"\n\6\3\6\5\6\u00ac\n\6\3\6\5\6\u00af\n\6\3\6\3\6\3\6\5\6\u00b4\n\6\3\6"+
		"\3\6\5\6\u00b8\n\6\3\6\3\6\3\6\5\6\u00bd\n\6\3\6\3\6\5\6\u00c1\n\6\3\6"+
		"\3\6\3\6\5\6\u00c6\n\6\3\6\3\6\5\6\u00ca\n\6\3\6\3\6\5\6\u00ce\n\6\3\6"+
		"\5\6\u00d1\n\6\3\6\3\6\3\6\3\6\7\6\u00d7\n\6\f\6\16\6\u00da\13\6\3\7\3"+
		"\7\3\7\3\7\3\b\3\b\3\b\7\b\u00e3\n\b\f\b\16\b\u00e6\13\b\3\t\3\t\3\t\3"+
		"\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n\5\n\u00f3\n\n\5\n\u00f5\n\n\3\13\3\13\3"+
		"\f\3\f\3\f\7\f\u00fc\n\f\f\f\16\f\u00ff\13\f\3\r\3\r\3\r\5\r\u0104\n\r"+
		"\3\r\3\r\5\r\u0108\n\r\3\r\5\r\u010b\n\r\3\r\5\r\u010e\n\r\3\16\3\16\3"+
		"\16\3\17\5\17\u0114\n\17\3\17\3\17\3\17\7\17\u0119\n\17\f\17\16\17\u011c"+
		"\13\17\3\20\3\20\5\20\u0120\n\20\3\21\3\21\3\21\7\21\u0125\n\21\f\21\16"+
		"\21\u0128\13\21\3\22\3\22\3\22\3\22\5\22\u012e\n\22\3\22\3\22\3\22\3\22"+
		"\3\22\3\22\3\22\3\22\3\22\5\22\u0139\n\22\3\22\3\22\3\22\3\22\3\22\3\22"+
		"\5\22\u0141\n\22\3\22\3\22\3\22\3\22\3\22\5\22\u0148\n\22\3\22\3\22\3"+
		"\22\3\22\3\22\7\22\u014f\n\22\f\22\16\22\u0152\13\22\3\23\3\23\3\23\3"+
		"\23\7\23\u0158\n\23\f\23\16\23\u015b\13\23\3\23\3\23\3\24\3\24\5\24\u0161"+
		"\n\24\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\5\25\u016e"+
		"\n\25\3\26\3\26\3\26\7\26\u0173\n\26\f\26\16\26\u0176\13\26\3\27\3\27"+
		"\5\27\u017a\n\27\3\30\3\30\3\31\3\31\3\31\3\31\3\31\5\31\u0183\n\31\3"+
		"\32\3\32\5\32\u0187\n\32\3\32\3\32\3\32\3\33\3\33\3\33\7\33\u018f\n\33"+
		"\f\33\16\33\u0192\13\33\3\34\3\34\5\34\u0196\n\34\3\34\3\34\3\34\3\34"+
		"\3\35\3\35\3\35\3\35\7\35\u01a0\n\35\f\35\16\35\u01a3\13\35\3\35\3\35"+
		"\3\36\3\36\3\36\3\36\3\36\5\36\u01ac\n\36\3\36\3\36\5\36\u01b0\n\36\3"+
		"\36\3\36\3\36\3\36\5\36\u01b6\n\36\3\36\3\36\5\36\u01ba\n\36\3\36\5\36"+
		"\u01bd\n\36\3\37\3\37\3\37\7\37\u01c2\n\37\f\37\16\37\u01c5\13\37\3 \3"+
		" \3 \3 \3!\3!\3!\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\5\"\u01d6\n\"\3\"\3\""+
		"\5\"\u01da\n\"\3\"\5\"\u01dd\n\"\3#\3#\3#\7#\u01e2\n#\f#\16#\u01e5\13"+
		"#\3$\3$\3$\3$\3%\3%\3%\3%\3%\3%\5%\u01f1\n%\3%\3%\5%\u01f5\n%\3%\5%\u01f8"+
		"\n%\3&\3&\3&\3&\3&\3&\3&\3&\3&\5&\u0203\n&\3&\3&\5&\u0207\n&\3&\3&\3&"+
		"\3&\3&\3&\3&\3&\3&\3&\3&\3&\3&\3&\3&\3&\3&\3&\3&\3&\3&\3&\3&\3&\3&\3&"+
		"\3&\3&\3&\3&\3&\5&\u0228\n&\3&\3&\5&\u022c\n&\3&\3&\5&\u0230\n&\3&\3&"+
		"\5&\u0234\n&\3&\3&\3&\3&\3&\3&\3&\3&\3&\3&\3&\3&\3&\3&\3&\3&\3&\3&\3&"+
		"\3&\3&\5&\u024b\n&\3&\3&\3&\3&\3&\3&\3&\5&\u0254\n&\3&\3&\3&\3&\5&\u025a"+
		"\n&\3&\3&\3&\3&\3&\3&\3&\3&\3&\3&\3&\6&\u0267\n&\r&\16&\u0268\3&\3&\3"+
		"&\6&\u026e\n&\r&\16&\u026f\3&\3&\3&\3&\3&\3&\3&\3&\3&\5&\u027b\n&\3&\3"+
		"&\3&\3&\3&\3&\3&\3&\5&\u0285\n&\3&\3&\3&\3&\3&\3&\3&\6&\u028e\n&\r&\16"+
		"&\u028f\7&\u0292\n&\f&\16&\u0295\13&\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3"+
		"\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\5\'\u02a8\n\'\3\'\3\'\5\'\u02ac\n\'"+
		"\3\'\3\'\5\'\u02b0\n\'\3\'\5\'\u02b3\n\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3"+
		"\'\3\'\3\'\3\'\3\'\6\'\u02c1\n\'\r\'\16\'\u02c2\3\'\3\'\3\'\6\'\u02c8"+
		"\n\'\r\'\16\'\u02c9\3\'\3\'\3\'\3\'\3\'\3\'\6\'\u02d2\n\'\r\'\16\'\u02d3"+
		"\7\'\u02d6\n\'\f\'\16\'\u02d9\13\'\3(\5(\u02dc\n(\3(\3(\3)\3)\5)\u02e2"+
		"\n)\3)\5)\u02e5\n)\3)\3)\3)\5)\u02ea\n)\3)\3)\3)\3)\3)\5)\u02f1\n)\3)"+
		"\3)\3)\5)\u02f6\n)\3)\3)\5)\u02fa\n)\3)\3)\3*\3*\3*\5*\u0301\n*\3*\3*"+
		"\3*\5*\u0306\n*\3*\3*\3+\3+\3+\3+\3,\3,\3-\3-\3.\3.\3.\7.\u0315\n.\f."+
		"\16.\u0318\13.\3/\3/\3/\3/\3/\5/\u031f\n/\3/\3/\3/\5/\u0324\n/\3/\3/\3"+
		"/\5/\u0329\n/\3/\5/\u032c\n/\3\60\3\60\3\60\3\60\3\60\3\60\3\60\3\60\5"+
		"\60\u0336\n\60\3\61\3\61\3\61\7\61\u033b\n\61\f\61\16\61\u033e\13\61\3"+
		"\62\3\62\3\62\7\62\u0343\n\62\f\62\16\62\u0346\13\62\3\63\3\63\3\63\5"+
		"\63\u034b\n\63\3\64\3\64\3\64\3\64\5\64\u0351\n\64\3\64\3\64\3\64\3\64"+
		"\3\65\3\65\3\65\3\66\3\66\3\66\6\66\u035d\n\66\r\66\16\66\u035e\3\67\3"+
		"\67\3\67\3\67\5\67\u0365\n\67\38\38\38\38\58\u036b\n8\38\38\58\u036f\n"+
		"8\38\58\u0372\n8\39\39\39\39\59\u0378\n9\3:\5:\u037b\n:\3:\3:\3:\5:\u0380"+
		"\n:\3:\3:\3:\3:\5:\u0386\n:\3:\3:\3:\3:\3:\3:\5:\u038e\n:\3:\3:\3:\3:"+
		"\3:\3:\3:\3:\3:\3:\3:\5:\u039b\n:\3:\5:\u039e\n:\3:\3:\5:\u03a2\n:\3:"+
		"\3:\5:\u03a6\n:\5:\u03a8\n:\3;\3;\3;\3<\3<\3<\3<\3=\3=\3=\3=\3>\3>\3>"+
		"\3>\3>\3>\5>\u03bb\n>\3?\3?\3?\3?\3?\3@\3@\3@\7@\u03c5\n@\f@\16@\u03c8"+
		"\13@\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\5A\u03db\nA\3"+
		"B\5B\u03de\nB\3B\5B\u03e1\nB\3B\5B\u03e4\nB\3B\5B\u03e7\nB\3B\5B\u03ea"+
		"\nB\3C\3C\3C\5C\u03ef\nC\3D\3D\3D\3D\5D\u03f5\nD\3E\3E\3E\3E\3F\3F\3F"+
		"\3F\3F\3F\5F\u0401\nF\3F\7F\u0404\nF\fF\16F\u0407\13F\3F\2\7\n\"JL\u008a"+
		"G\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62\64\668:<>@BDF"+
		"HJLNPRTVXZ\\^`bdfhjlnprtvxz|~\u0080\u0082\u0084\u0086\u0088\u008a\2\7"+
		"\3\2\36\37\5\2\27\27\31\31\67\67\4\2\65\6588\5\2,,\61\62DF\4\2\61\62E"+
		"F\2\u048a\2\u008c\3\2\2\2\4\u0097\3\2\2\2\6\u009c\3\2\2\2\b\u00a0\3\2"+
		"\2\2\n\u00d0\3\2\2\2\f\u00db\3\2\2\2\16\u00df\3\2\2\2\20\u00e7\3\2\2\2"+
		"\22\u00f4\3\2\2\2\24\u00f6\3\2\2\2\26\u00f8\3\2\2\2\30\u010d\3\2\2\2\32"+
		"\u010f\3\2\2\2\34\u0113\3\2\2\2\36\u011f\3\2\2\2 \u0121\3\2\2\2\"\u0140"+
		"\3\2\2\2$\u0153\3\2\2\2&\u015e\3\2\2\2(\u016d\3\2\2\2*\u016f\3\2\2\2,"+
		"\u0177\3\2\2\2.\u017b\3\2\2\2\60\u0182\3\2\2\2\62\u0184\3\2\2\2\64\u018b"+
		"\3\2\2\2\66\u0193\3\2\2\28\u019b\3\2\2\2:\u01a6\3\2\2\2<\u01be\3\2\2\2"+
		">\u01c6\3\2\2\2@\u01ca\3\2\2\2B\u01cd\3\2\2\2D\u01de\3\2\2\2F\u01e6\3"+
		"\2\2\2H\u01ea\3\2\2\2J\u0233\3\2\2\2L\u02b2\3\2\2\2N\u02db\3\2\2\2P\u02df"+
		"\3\2\2\2R\u02fd\3\2\2\2T\u0309\3\2\2\2V\u030d\3\2\2\2X\u030f\3\2\2\2Z"+
		"\u0311\3\2\2\2\\\u032b\3\2\2\2^\u0335\3\2\2\2`\u0337\3\2\2\2b\u033f\3"+
		"\2\2\2d\u034a\3\2\2\2f\u034c\3\2\2\2h\u0356\3\2\2\2j\u0359\3\2\2\2l\u0364"+
		"\3\2\2\2n\u0366\3\2\2\2p\u0373\3\2\2\2r\u03a7\3\2\2\2t\u03a9\3\2\2\2v"+
		"\u03ac\3\2\2\2x\u03b0\3\2\2\2z\u03ba\3\2\2\2|\u03bc\3\2\2\2~\u03c1\3\2"+
		"\2\2\u0080\u03da\3\2\2\2\u0082\u03dd\3\2\2\2\u0084\u03ee\3\2\2\2\u0086"+
		"\u03f4\3\2\2\2\u0088\u03f6\3\2\2\2\u008a\u03fa\3\2\2\2\u008c\u0091\5J"+
		"&\2\u008d\u008e\7\3\2\2\u008e\u0090\5J&\2\u008f\u008d\3\2\2\2\u0090\u0093"+
		"\3\2\2\2\u0091\u008f\3\2\2\2\u0091\u0092\3\2\2\2\u0092\u0095\3\2\2\2\u0093"+
		"\u0091\3\2\2\2\u0094\u0096\7\3\2\2\u0095\u0094\3\2\2\2\u0095\u0096\3\2"+
		"\2\2\u0096\3\3\2\2\2\u0097\u0098\7\3\2\2\u0098\5\3\2\2\2\u0099\u009d\5"+
		"B\"\2\u009a\u009d\5:\36\2\u009b\u009d\5H%\2\u009c\u0099\3\2\2\2\u009c"+
		"\u009a\3\2\2\2\u009c\u009b\3\2\2\2\u009d\7\3\2\2\2\u009e\u00a1\5\n\6\2"+
		"\u009f\u00a1\5\6\4\2\u00a0\u009e\3\2\2\2\u00a0\u009f\3\2\2\2\u00a1\t\3"+
		"\2\2\2\u00a2\u00a3\b\6\1\2\u00a3\u00a8\7\4\2\2\u00a4\u00a6\5\f\7\2\u00a5"+
		"\u00a7\7\5\2\2\u00a6\u00a5\3\2\2\2\u00a6\u00a7\3\2\2\2\u00a7\u00a9\3\2"+
		"\2\2\u00a8\u00a4\3\2\2\2\u00a8\u00a9\3\2\2\2\u00a9\u00ab\3\2\2\2\u00aa"+
		"\u00ac\5\22\n\2\u00ab\u00aa\3\2\2\2\u00ab\u00ac\3\2\2\2\u00ac\u00ae\3"+
		"\2\2\2\u00ad\u00af\5\24\13\2\u00ae\u00ad\3\2\2\2\u00ae\u00af\3\2\2\2\u00af"+
		"\u00b0\3\2\2\2\u00b0\u00b3\5\26\f\2\u00b1\u00b2\7\6\2\2\u00b2\u00b4\5"+
		"\"\22\2\u00b3\u00b1\3\2\2\2\u00b3\u00b4\3\2\2\2\u00b4\u00b7\3\2\2\2\u00b5"+
		"\u00b6\7\7\2\2\u00b6\u00b8\5J&\2\u00b7\u00b5\3\2\2\2\u00b7\u00b8\3\2\2"+
		"\2\u00b8\u00bc\3\2\2\2\u00b9\u00ba\7\b\2\2\u00ba\u00bb\7\t\2\2\u00bb\u00bd"+
		"\5(\25\2\u00bc\u00b9\3\2\2\2\u00bc\u00bd\3\2\2\2\u00bd\u00c0\3\2\2\2\u00be"+
		"\u00bf\7\n\2\2\u00bf\u00c1\5J&\2\u00c0\u00be\3\2\2\2\u00c0\u00c1\3\2\2"+
		"\2\u00c1\u00c5\3\2\2\2\u00c2\u00c3\7\13\2\2\u00c3\u00c4\7\t\2\2\u00c4"+
		"\u00c6\5*\26\2\u00c5\u00c2\3\2\2\2\u00c5\u00c6\3\2\2\2\u00c6\u00c9\3\2"+
		"\2\2\u00c7\u00c8\7\f\2\2\u00c8\u00ca\5J&\2\u00c9\u00c7\3\2\2\2\u00c9\u00ca"+
		"\3\2\2\2\u00ca\u00cd\3\2\2\2\u00cb\u00cc\7\r\2\2\u00cc\u00ce\5J&\2\u00cd"+
		"\u00cb\3\2\2\2\u00cd\u00ce\3\2\2\2\u00ce\u00d1\3\2\2\2\u00cf\u00d1\5\62"+
		"\32\2\u00d0\u00a2\3\2\2\2\u00d0\u00cf\3\2\2\2\u00d1\u00d8\3\2\2\2\u00d2"+
		"\u00d3\f\4\2\2\u00d3\u00d4\5\60\31\2\u00d4\u00d5\5\n\6\5\u00d5\u00d7\3"+
		"\2\2\2\u00d6\u00d2\3\2\2\2\u00d7\u00da\3\2\2\2\u00d8\u00d6\3\2\2\2\u00d8"+
		"\u00d9\3\2\2\2\u00d9\13\3\2\2\2\u00da\u00d8\3\2\2\2\u00db\u00dc\7\16\2"+
		"\2\u00dc\u00dd\5\16\b\2\u00dd\u00de\7\17\2\2\u00de\r\3\2\2\2\u00df\u00e4"+
		"\5\20\t\2\u00e0\u00e1\7\5\2\2\u00e1\u00e3\5\20\t\2\u00e2\u00e0\3\2\2\2"+
		"\u00e3\u00e6\3\2\2\2\u00e4\u00e2\3\2\2\2\u00e4\u00e5\3\2\2\2\u00e5\17"+
		"\3\2\2\2\u00e6\u00e4\3\2\2\2\u00e7\u00e8\7k\2\2\u00e8\u00e9\7\20\2\2\u00e9"+
		"\u00ea\5J&\2\u00ea\21\3\2\2\2\u00eb\u00f5\7\21\2\2\u00ec\u00f2\7\22\2"+
		"\2\u00ed\u00ee\7\23\2\2\u00ee\u00ef\7\24\2\2\u00ef\u00f0\5Z.\2\u00f0\u00f1"+
		"\7\25\2\2\u00f1\u00f3\3\2\2\2\u00f2\u00ed\3\2\2\2\u00f2\u00f3\3\2\2\2"+
		"\u00f3\u00f5\3\2\2\2\u00f4\u00eb\3\2\2\2\u00f4\u00ec\3\2\2\2\u00f5\23"+
		"\3\2\2\2\u00f6\u00f7\7\26\2\2\u00f7\25\3\2\2\2\u00f8\u00fd\5\30\r\2\u00f9"+
		"\u00fa\7\5\2\2\u00fa\u00fc\5\30\r\2\u00fb\u00f9\3\2\2\2\u00fc\u00ff\3"+
		"\2\2\2\u00fd\u00fb\3\2\2\2\u00fd\u00fe\3\2\2\2\u00fe\27\3\2\2\2\u00ff"+
		"\u00fd\3\2\2\2\u0100\u0101\5\34\17\2\u0101\u0102\7\20\2\2\u0102\u0104"+
		"\3\2\2\2\u0103\u0100\3\2\2\2\u0103\u0104\3\2\2\2\u0104\u0105\3\2\2\2\u0105"+
		"\u0107\5J&\2\u0106\u0108\5\f\7\2\u0107\u0106\3\2\2\2\u0107\u0108\3\2\2"+
		"\2\u0108\u010e\3\2\2\2\u0109\u010b\5\32\16\2\u010a\u0109\3\2\2\2\u010a"+
		"\u010b\3\2\2\2\u010b\u010c\3\2\2\2\u010c\u010e\7\27\2\2\u010d\u0103\3"+
		"\2\2\2\u010d\u010a\3\2\2\2\u010e\31\3\2\2\2\u010f\u0110\7k\2\2\u0110\u0111"+
		"\7\30\2\2\u0111\33\3\2\2\2\u0112\u0114\7\31\2\2\u0113\u0112\3\2\2\2\u0113"+
		"\u0114\3\2\2\2\u0114\u0115\3\2\2\2\u0115\u011a\5\36\20\2\u0116\u0117\7"+
		"\31\2\2\u0117\u0119\5\36\20\2\u0118\u0116\3\2\2\2\u0119\u011c\3\2\2\2"+
		"\u011a\u0118\3\2\2\2\u011a\u011b\3\2\2\2\u011b\35\3\2\2\2\u011c\u011a"+
		"\3\2\2\2\u011d\u0120\7^\2\2\u011e\u0120\5 \21\2\u011f\u011d\3\2\2\2\u011f"+
		"\u011e\3\2\2\2\u0120\37\3\2\2\2\u0121\u0126\7k\2\2\u0122\u0123\7\30\2"+
		"\2\u0123\u0125\7k\2\2\u0124\u0122\3\2\2\2\u0125\u0128\3\2\2\2\u0126\u0124"+
		"\3\2\2\2\u0126\u0127\3\2\2\2\u0127!\3\2\2\2\u0128\u0126\3\2\2\2\u0129"+
		"\u012d\b\22\1\2\u012a\u012b\5\34\17\2\u012b\u012c\7\20\2\2\u012c\u012e"+
		"\3\2\2\2\u012d\u012a\3\2\2\2\u012d\u012e\3\2\2\2\u012e\u012f\3\2\2\2\u012f"+
		"\u0141\5 \21\2\u0130\u0131\5\34\17\2\u0131\u0132\7\20\2\2\u0132\u0133"+
		"\7\24\2\2\u0133\u0134\5\n\6\2\u0134\u0135\7\25\2\2\u0135\u0141\3\2\2\2"+
		"\u0136\u0138\5\34\17\2\u0137\u0139\5\f\7\2\u0138\u0137\3\2\2\2\u0138\u0139"+
		"\3\2\2\2\u0139\u013a\3\2\2\2\u013a\u013b\5$\23\2\u013b\u013c\7\20\2\2"+
		"\u013c\u013d\7\24\2\2\u013d\u013e\5<\37\2\u013e\u013f\7\25\2\2\u013f\u0141"+
		"\3\2\2\2\u0140\u0129\3\2\2\2\u0140\u0130\3\2\2\2\u0140\u0136\3\2\2\2\u0141"+
		"\u0150\3\2\2\2\u0142\u0143\f\4\2\2\u0143\u0144\7\32\2\2\u0144\u014f\5"+
		"\"\22\5\u0145\u0147\f\3\2\2\u0146\u0148\7_\2\2\u0147\u0146\3\2\2\2\u0147"+
		"\u0148\3\2\2\2\u0148\u0149\3\2\2\2\u0149\u014a\7\33\2\2\u014a\u014b\5"+
		"\"\22\2\u014b\u014c\7\23\2\2\u014c\u014d\5J&\2\u014d\u014f\3\2\2\2\u014e"+
		"\u0142\3\2\2\2\u014e\u0145\3\2\2\2\u014f\u0152\3\2\2\2\u0150\u014e\3\2"+
		"\2\2\u0150\u0151\3\2\2\2\u0151#\3\2\2\2\u0152\u0150\3\2\2\2\u0153\u0154"+
		"\7\24\2\2\u0154\u0159\5&\24\2\u0155\u0156\7\5\2\2\u0156\u0158\5&\24\2"+
		"\u0157\u0155\3\2\2\2\u0158\u015b\3\2\2\2\u0159\u0157\3\2\2\2\u0159\u015a"+
		"\3\2\2\2\u015a\u015c\3\2\2\2\u015b\u0159\3\2\2\2\u015c\u015d\7\25\2\2"+
		"\u015d%\3\2\2\2\u015e\u0160\7k\2\2\u015f\u0161\5\f\7\2\u0160\u015f\3\2"+
		"\2\2\u0160\u0161\3\2\2\2\u0161\'\3\2\2\2\u0162\u016e\5Z.\2\u0163\u0164"+
		"\7\34\2\2\u0164\u0165\7\24\2\2\u0165\u0166\5Z.\2\u0166\u0167\7\25\2\2"+
		"\u0167\u016e\3\2\2\2\u0168\u0169\7\35\2\2\u0169\u016a\7\24\2\2\u016a\u016b"+
		"\5Z.\2\u016b\u016c\7\25\2\2\u016c\u016e\3\2\2\2\u016d\u0162\3\2\2\2\u016d"+
		"\u0163\3\2\2\2\u016d\u0168\3\2\2\2\u016e)\3\2\2\2\u016f\u0174\5,\27\2"+
		"\u0170\u0171\7\5\2\2\u0171\u0173\5,\27\2\u0172\u0170\3\2\2\2\u0173\u0176"+
		"\3\2\2\2\u0174\u0172\3\2\2\2\u0174\u0175\3\2\2\2\u0175+\3\2\2\2\u0176"+
		"\u0174\3\2\2\2\u0177\u0179\5J&\2\u0178\u017a\5.\30\2\u0179\u0178\3\2\2"+
		"\2\u0179\u017a\3\2\2\2\u017a-\3\2\2\2\u017b\u017c\t\2\2\2\u017c/\3\2\2"+
		"\2\u017d\u0183\7 \2\2\u017e\u017f\7 \2\2\u017f\u0183\7\21\2\2\u0180\u0183"+
		"\7!\2\2\u0181\u0183\7\"\2\2\u0182\u017d\3\2\2\2\u0182\u017e\3\2\2\2\u0182"+
		"\u0180\3\2\2\2\u0182\u0181\3\2\2\2\u0183\61\3\2\2\2\u0184\u0186\7#\2\2"+
		"\u0185\u0187\7$\2\2\u0186\u0185\3\2\2\2\u0186\u0187\3\2\2\2\u0187\u0188"+
		"\3\2\2\2\u0188\u0189\5\64\33\2\u0189\u018a\5\b\5\2\u018a\63\3\2\2\2\u018b"+
		"\u0190\5\66\34\2\u018c\u018d\7\5\2\2\u018d\u018f\5\66\34\2\u018e\u018c"+
		"\3\2\2\2\u018f\u0192\3\2\2\2\u0190\u018e\3\2\2\2\u0190\u0191\3\2\2\2\u0191"+
		"\65\3\2\2\2\u0192\u0190\3\2\2\2\u0193\u0195\7k\2\2\u0194\u0196\58\35\2"+
		"\u0195\u0194\3\2\2\2\u0195\u0196\3\2\2\2\u0196\u0197\3\2\2\2\u0197\u0198"+
		"\7\24\2\2\u0198\u0199\5\b\5\2\u0199\u019a\7\25\2\2\u019a\67\3\2\2\2\u019b"+
		"\u019c\7\24\2\2\u019c\u01a1\7k\2\2\u019d\u019e\7\5\2\2\u019e\u01a0\7k"+
		"\2\2\u019f\u019d\3\2\2\2\u01a0\u01a3\3\2\2\2\u01a1\u019f\3\2\2\2\u01a1"+
		"\u01a2\3\2\2\2\u01a2\u01a4\3\2\2\2\u01a3\u01a1\3\2\2\2\u01a4\u01a5\7\25"+
		"\2\2\u01a59\3\2\2\2\u01a6\u01a7\7%\2\2\u01a7\u01ab\7&\2\2\u01a8\u01a9"+
		"\5\34\17\2\u01a9\u01aa\7\20\2\2\u01aa\u01ac\3\2\2\2\u01ab\u01a8\3\2\2"+
		"\2\u01ab\u01ac\3\2\2\2\u01ac\u01ad\3\2\2\2\u01ad\u01af\5 \21\2\u01ae\u01b0"+
		"\58\35\2\u01af\u01ae\3\2\2\2\u01af\u01b0\3\2\2\2\u01b0\u01b5\3\2\2\2\u01b1"+
		"\u01b2\7\'\2\2\u01b2\u01b6\5<\37\2\u01b3\u01b6\5@!\2\u01b4\u01b6\5\n\6"+
		"\2\u01b5\u01b1\3\2\2\2\u01b5\u01b3\3\2\2\2\u01b5\u01b4\3\2\2\2\u01b6\u01bc"+
		"\3\2\2\2\u01b7\u01b9\7(\2\2\u01b8\u01ba\5\f\7\2\u01b9\u01b8\3\2\2\2\u01b9"+
		"\u01ba\3\2\2\2\u01ba\u01bb\3\2\2\2\u01bb\u01bd\5\26\f\2\u01bc\u01b7\3"+
		"\2\2\2\u01bc\u01bd\3\2\2\2\u01bd;\3\2\2\2\u01be\u01c3\5> \2\u01bf\u01c0"+
		"\7\5\2\2\u01c0\u01c2\5> \2\u01c1\u01bf\3\2\2\2\u01c2\u01c5\3\2\2\2\u01c3"+
		"\u01c1\3\2\2\2\u01c3\u01c4\3\2\2\2\u01c4=\3\2\2\2\u01c5\u01c3\3\2\2\2"+
		"\u01c6\u01c7\7\24\2\2\u01c7\u01c8\5Z.\2\u01c8\u01c9\7\25\2\2\u01c9?\3"+
		"\2\2\2\u01ca\u01cb\7)\2\2\u01cb\u01cc\7\'\2\2\u01ccA\3\2\2\2\u01cd\u01ce"+
		"\7*\2\2\u01ce\u01cf\5\34\17\2\u01cf\u01d0\7\6\2\2\u01d0\u01d1\5\"\22\2"+
		"\u01d1\u01d2\7+\2\2\u01d2\u01d5\5D#\2\u01d3\u01d4\7\7\2\2\u01d4\u01d6"+
		"\5J&\2\u01d5\u01d3\3\2\2\2\u01d5\u01d6\3\2\2\2\u01d6\u01dc\3\2\2\2\u01d7"+
		"\u01d9\7(\2\2\u01d8\u01da\5\f\7\2\u01d9\u01d8\3\2\2\2\u01d9\u01da\3\2"+
		"\2\2\u01da\u01db\3\2\2\2\u01db\u01dd\5\26\f\2\u01dc\u01d7\3\2\2\2\u01dc"+
		"\u01dd\3\2\2\2\u01ddC\3\2\2\2\u01de\u01e3\5F$\2\u01df\u01e0\7\5\2\2\u01e0"+
		"\u01e2\5F$\2\u01e1\u01df\3\2\2\2\u01e2\u01e5\3\2\2\2\u01e3\u01e1\3\2\2"+
		"\2\u01e3\u01e4\3\2\2\2\u01e4E\3\2\2\2\u01e5\u01e3\3\2\2\2\u01e6\u01e7"+
		"\5 \21\2\u01e7\u01e8\7,\2\2\u01e8\u01e9\5J&\2\u01e9G\3\2\2\2\u01ea\u01eb"+
		"\7-\2\2\u01eb\u01ec\5\34\17\2\u01ec\u01ed\7\6\2\2\u01ed\u01f0\5\"\22\2"+
		"\u01ee\u01ef\7\7\2\2\u01ef\u01f1\5J&\2\u01f0\u01ee\3\2\2\2\u01f0\u01f1"+
		"\3\2\2\2\u01f1\u01f7\3\2\2\2\u01f2\u01f4\7(\2\2\u01f3\u01f5\5\f\7\2\u01f4"+
		"\u01f3\3\2\2\2\u01f4\u01f5\3\2\2\2\u01f5\u01f6\3\2\2\2\u01f6\u01f8\5\26"+
		"\f\2\u01f7\u01f2\3\2\2\2\u01f7\u01f8\3\2\2\2\u01f8I\3\2\2\2\u01f9\u01fa"+
		"\b&\1\2\u01fa\u0234\5\n\6\2\u01fb\u0234\5\6\4\2\u01fc\u0234\5d\63\2\u01fd"+
		"\u0234\5\4\3\2\u01fe\u01ff\7.\2\2\u01ff\u0202\7k\2\2\u0200\u0201\7\20"+
		"\2\2\u0201\u0203\5\u008aF\2\u0202\u0200\3\2\2\2\u0202\u0203\3\2\2\2\u0203"+
		"\u0206\3\2\2\2\u0204\u0205\7/\2\2\u0205\u0207\5J&\2\u0206\u0204\3\2\2"+
		"\2\u0206\u0207\3\2\2\2\u0207\u0234\3\2\2\2\u0208\u0209\7k\2\2\u0209\u020a"+
		"\7/\2\2\u020a\u0234\5J& \u020b\u020c\7\24\2\2\u020c\u020d\5J&\2\u020d"+
		"\u020e\7\25\2\2\u020e\u0234\3\2\2\2\u020f\u0210\7\60\2\2\u0210\u0211\5"+
		"J&\2\u0211\u0212\7\25\2\2\u0212\u0234\3\2\2\2\u0213\u0214\5\u008aF\2\u0214"+
		"\u0215\7\61\2\2\u0215\u0216\5J&\2\u0216\u0217\7\62\2\2\u0217\u0234\3\2"+
		"\2\2\u0218\u0234\7)\2\2\u0219\u0234\5\\/\2\u021a\u021b\7\65\2\2\u021b"+
		"\u0234\5J&\30\u021c\u021d\7\20\2\2\u021d\u0234\7k\2\2\u021e\u021f\7k\2"+
		"\2\u021f\u0220\7/\2\2\u0220\u0234\5J&\23\u0221\u0234\5P)\2\u0222\u0223"+
		"\7j\2\2\u0223\u0234\5J&\21\u0224\u0225\5 \21\2\u0225\u0227\7\24\2\2\u0226"+
		"\u0228\5\22\n\2\u0227\u0226\3\2\2\2\u0227\u0228\3\2\2\2\u0228\u022b\3"+
		"\2\2\2\u0229\u022c\5Z.\2\u022a\u022c\7\27\2\2\u022b\u0229\3\2\2\2\u022b"+
		"\u022a\3\2\2\2\u022b\u022c\3\2\2\2\u022c\u022d\3\2\2\2\u022d\u022f\7\25"+
		"\2\2\u022e\u0230\5R*\2\u022f\u022e\3\2\2\2\u022f\u0230\3\2\2\2\u0230\u0234"+
		"\3\2\2\2\u0231\u0234\5N(\2\u0232\u0234\5L\'\2\u0233\u01f9\3\2\2\2\u0233"+
		"\u01fb\3\2\2\2\u0233\u01fc\3\2\2\2\u0233\u01fd\3\2\2\2\u0233\u01fe\3\2"+
		"\2\2\u0233\u0208\3\2\2\2\u0233\u020b\3\2\2\2\u0233\u020f\3\2\2\2\u0233"+
		"\u0213\3\2\2\2\u0233\u0218\3\2\2\2\u0233\u0219\3\2\2\2\u0233\u021a\3\2"+
		"\2\2\u0233\u021c\3\2\2\2\u0233\u021e\3\2\2\2\u0233\u0221\3\2\2\2\u0233"+
		"\u0222\3\2\2\2\u0233\u0224\3\2\2\2\u0233\u0231\3\2\2\2\u0233\u0232\3\2"+
		"\2\2\u0234\u0293\3\2\2\2\u0235\u0236\f\27\2\2\u0236\u0237\7\66\2\2\u0237"+
		"\u0292\5J&\27\u0238\u0239\f\26\2\2\u0239\u023a\t\3\2\2\u023a\u0292\5J"+
		"&\27\u023b\u023c\f\25\2\2\u023c\u023d\t\4\2\2\u023d\u0292\5J&\26\u023e"+
		"\u023f\f\17\2\2\u023f\u0240\5X-\2\u0240\u0241\5L\'\2\u0241\u0242\5X-\2"+
		"\u0242\u0243\5J&\20\u0243\u0292\3\2\2\2\u0244\u0245\f\16\2\2\u0245\u0246"+
		"\5V,\2\u0246\u0247\5J&\17\u0247\u0292\3\2\2\2\u0248\u024a\f\13\2\2\u0249"+
		"\u024b\7j\2\2\u024a\u0249\3\2\2\2\u024a\u024b\3\2\2\2\u024b\u024c\3\2"+
		"\2\2\u024c\u024d\7:\2\2\u024d\u024e\5J&\2\u024e\u024f\7;\2\2\u024f\u0250"+
		"\5J&\f\u0250\u0292\3\2\2\2\u0251\u0253\f\n\2\2\u0252\u0254\7j\2\2\u0253"+
		"\u0252\3\2\2\2\u0253\u0254\3\2\2\2\u0254\u0255\3\2\2\2\u0255\u0256\7<"+
		"\2\2\u0256\u0292\5J&\13\u0257\u0259\f\t\2\2\u0258\u025a\7j\2\2\u0259\u0258"+
		"\3\2\2\2\u0259\u025a\3\2\2\2\u025a\u025b\3\2\2\2\u025b\u025c\7=\2\2\u025c"+
		"\u0292\5J&\n\u025d\u025e\f\6\2\2\u025e\u025f\7;\2\2\u025f\u0292\5J&\7"+
		"\u0260\u0261\f\5\2\2\u0261\u0262\7?\2\2\u0262\u0292\5J&\6\u0263\u0266"+
		"\f\32\2\2\u0264\u0265\7\63\2\2\u0265\u0267\5J&\2\u0266\u0264\3\2\2\2\u0267"+
		"\u0268\3\2\2\2\u0268\u0266\3\2\2\2\u0268\u0269\3\2\2\2\u0269\u0292\3\2"+
		"\2\2\u026a\u026d\f\31\2\2\u026b\u026c\7\64\2\2\u026c\u026e\5J&\2\u026d"+
		"\u026b\3\2\2\2\u026e\u026f\3\2\2\2\u026f\u026d\3\2\2\2\u026f\u0270\3\2"+
		"\2\2\u0270\u0292\3\2\2\2\u0271\u0272\f\r\2\2\u0272\u0273\5V,\2\u0273\u0274"+
		"\7i\2\2\u0274\u0275\7\24\2\2\u0275\u0276\5\n\6\2\u0276\u0277\7\25\2\2"+
		"\u0277\u0292\3\2\2\2\u0278\u027a\f\f\2\2\u0279\u027b\7j\2\2\u027a\u0279"+
		"\3\2\2\2\u027a\u027b\3\2\2\2\u027b\u027c\3\2\2\2\u027c\u027d\79\2\2\u027d"+
		"\u027e\7\24\2\2\u027e\u027f\5Z.\2\u027f\u0280\7\25\2\2\u0280\u0292\3\2"+
		"\2\2\u0281\u0282\f\b\2\2\u0282\u0284\7>\2\2\u0283\u0285\7j\2\2\u0284\u0283"+
		"\3\2\2\2\u0284\u0285\3\2\2\2\u0285\u0286\3\2\2\2\u0286\u0292\7c\2\2\u0287"+
		"\u028d\f\4\2\2\u0288\u0289\7@\2\2\u0289\u028a\5J&\2\u028a\u028b\7A\2\2"+
		"\u028b\u028c\5J&\2\u028c\u028e\3\2\2\2\u028d\u0288\3\2\2\2\u028e\u028f"+
		"\3\2\2\2\u028f\u028d\3\2\2\2\u028f\u0290\3\2\2\2\u0290\u0292\3\2\2\2\u0291"+
		"\u0235\3\2\2\2\u0291\u0238\3\2\2\2\u0291\u023b\3\2\2\2\u0291\u023e\3\2"+
		"\2\2\u0291\u0244\3\2\2\2\u0291\u0248\3\2\2\2\u0291\u0251\3\2\2\2\u0291"+
		"\u0257\3\2\2\2\u0291\u025d\3\2\2\2\u0291\u0260\3\2\2\2\u0291\u0263\3\2"+
		"\2\2\u0291\u026a\3\2\2\2\u0291\u0271\3\2\2\2\u0291\u0278\3\2\2\2\u0291"+
		"\u0281\3\2\2\2\u0291\u0287\3\2\2\2\u0292\u0295\3\2\2\2\u0293\u0291\3\2"+
		"\2\2\u0293\u0294\3\2\2\2\u0294K\3\2\2\2\u0295\u0293\3\2\2\2\u0296\u0297"+
		"\b\'\1\2\u0297\u0298\7\24\2\2\u0298\u0299\5L\'\2\u0299\u029a\7\25\2\2"+
		"\u029a\u02b3\3\2\2\2\u029b\u029c\5\u008aF\2\u029c\u029d\7\61\2\2\u029d"+
		"\u029e\5L\'\2\u029e\u029f\7\62\2\2\u029f\u02b3\3\2\2\2\u02a0\u02b3\5\\"+
		"/\2\u02a1\u02a2\7\65\2\2\u02a2\u02b3\5L\'\n\u02a3\u02b3\5P)\2\u02a4\u02a5"+
		"\5 \21\2\u02a5\u02a7\7\24\2\2\u02a6\u02a8\5\22\n\2\u02a7\u02a6\3\2\2\2"+
		"\u02a7\u02a8\3\2\2\2\u02a8\u02ab\3\2\2\2\u02a9\u02ac\5Z.\2\u02aa\u02ac"+
		"\7\27\2\2\u02ab\u02a9\3\2\2\2\u02ab\u02aa\3\2\2\2\u02ab\u02ac\3\2\2\2"+
		"\u02ac\u02ad\3\2\2\2\u02ad\u02af\7\25\2\2\u02ae\u02b0\5R*\2\u02af\u02ae"+
		"\3\2\2\2\u02af\u02b0\3\2\2\2\u02b0\u02b3\3\2\2\2\u02b1\u02b3\5N(\2\u02b2"+
		"\u0296\3\2\2\2\u02b2\u029b\3\2\2\2\u02b2\u02a0\3\2\2\2\u02b2\u02a1\3\2"+
		"\2\2\u02b2\u02a3\3\2\2\2\u02b2\u02a4\3\2\2\2\u02b2\u02b1\3\2\2\2\u02b3"+
		"\u02d7\3\2\2\2\u02b4\u02b5\f\t\2\2\u02b5\u02b6\7\66\2\2\u02b6\u02d6\5"+
		"L\'\t\u02b7\u02b8\f\b\2\2\u02b8\u02b9\t\3\2\2\u02b9\u02d6\5L\'\t\u02ba"+
		"\u02bb\f\7\2\2\u02bb\u02bc\t\4\2\2\u02bc\u02d6\5L\'\b\u02bd\u02c0\f\f"+
		"\2\2\u02be\u02bf\7\63\2\2\u02bf\u02c1\5L\'\2\u02c0\u02be\3\2\2\2\u02c1"+
		"\u02c2\3\2\2\2\u02c2\u02c0\3\2\2\2\u02c2\u02c3\3\2\2\2\u02c3\u02d6\3\2"+
		"\2\2\u02c4\u02c7\f\13\2\2\u02c5\u02c6\7\64\2\2\u02c6\u02c8\5L\'\2\u02c7"+
		"\u02c5\3\2\2\2\u02c8\u02c9\3\2\2\2\u02c9\u02c7\3\2\2\2\u02c9\u02ca\3\2"+
		"\2\2\u02ca\u02d6\3\2\2\2\u02cb\u02d1\f\3\2\2\u02cc\u02cd\7@\2\2\u02cd"+
		"\u02ce\5L\'\2\u02ce\u02cf\7A\2\2\u02cf\u02d0\5L\'\2\u02d0\u02d2\3\2\2"+
		"\2\u02d1\u02cc\3\2\2\2\u02d2\u02d3\3\2\2\2\u02d3\u02d1\3\2\2\2\u02d3\u02d4"+
		"\3\2\2\2\u02d4\u02d6\3\2\2\2\u02d5\u02b4\3\2\2\2\u02d5\u02b7\3\2\2\2\u02d5"+
		"\u02ba\3\2\2\2\u02d5\u02bd\3\2\2\2\u02d5\u02c4\3\2\2\2\u02d5\u02cb\3\2"+
		"\2\2\u02d6\u02d9\3\2\2\2\u02d7\u02d5\3\2\2\2\u02d7\u02d8\3\2\2\2\u02d8"+
		"M\3\2\2\2\u02d9\u02d7\3\2\2\2\u02da\u02dc\5\32\16\2\u02db\u02da\3\2\2"+
		"\2\u02db\u02dc\3\2\2\2\u02dc\u02dd\3\2\2\2\u02dd\u02de\5\34\17\2\u02de"+
		"O\3\2\2\2\u02df\u02e1\7\24\2\2\u02e0\u02e2\7\4\2\2\u02e1\u02e0\3\2\2\2"+
		"\u02e1\u02e2\3\2\2\2\u02e2\u02e4\3\2\2\2\u02e3\u02e5\5\22\n\2\u02e4\u02e3"+
		"\3\2\2\2\u02e4\u02e5\3\2\2\2\u02e5\u02e9\3\2\2\2\u02e6\u02e7\5\34\17\2"+
		"\u02e7\u02e8\7\20\2\2\u02e8\u02ea\3\2\2\2\u02e9\u02e6\3\2\2\2\u02e9\u02ea"+
		"\3\2\2\2\u02ea\u02eb\3\2\2\2\u02eb\u02ec\5J&\2\u02ec\u02ed\7\6\2\2\u02ed"+
		"\u02f0\5\"\22\2\u02ee\u02ef\7\7\2\2\u02ef\u02f1\5J&\2\u02f0\u02ee\3\2"+
		"\2\2\u02f0\u02f1\3\2\2\2\u02f1\u02f5\3\2\2\2\u02f2\u02f3\7\13\2\2\u02f3"+
		"\u02f4\7\t\2\2\u02f4\u02f6\5*\26\2\u02f5\u02f2\3\2\2\2\u02f5\u02f6\3\2"+
		"\2\2\u02f6\u02f9\3\2\2\2\u02f7\u02f8\7\f\2\2\u02f8\u02fa\5J&\2\u02f9\u02f7"+
		"\3\2\2\2\u02f9\u02fa\3\2\2\2\u02fa\u02fb\3\2\2\2\u02fb\u02fc\7\25\2\2"+
		"\u02fcQ\3\2\2\2\u02fd\u02fe\7B\2\2\u02fe\u0300\7\24\2\2\u02ff\u0301\5"+
		"T+\2\u0300\u02ff\3\2\2\2\u0300\u0301\3\2\2\2\u0301\u0305\3\2\2\2\u0302"+
		"\u0303\7\13\2\2\u0303\u0304\7\t\2\2\u0304\u0306\5*\26\2\u0305\u0302\3"+
		"\2\2\2\u0305\u0306\3\2\2\2\u0306\u0307\3\2\2\2\u0307\u0308\7\25\2\2\u0308"+
		"S\3\2\2\2\u0309\u030a\7C\2\2\u030a\u030b\7\t\2\2\u030b\u030c\5Z.\2\u030c"+
		"U\3\2\2\2\u030d\u030e\t\5\2\2\u030eW\3\2\2\2\u030f\u0310\t\6\2\2\u0310"+
		"Y\3\2\2\2\u0311\u0316\5J&\2\u0312\u0313\7\5\2\2\u0313\u0315\5J&\2\u0314"+
		"\u0312\3\2\2\2\u0315\u0318\3\2\2\2\u0316\u0314\3\2\2\2\u0316\u0317\3\2"+
		"\2\2\u0317[\3\2\2\2\u0318\u0316\3\2\2\2\u0319\u032c\5^\60\2\u031a\u032c"+
		"\7c\2\2\u031b\u031c\7k\2\2\u031c\u031e\7G\2\2\u031d\u031f\5b\62\2\u031e"+
		"\u031d\3\2\2\2\u031e\u031f\3\2\2\2\u031f\u0320\3\2\2\2\u0320\u032c\7H"+
		"\2\2\u0321\u0323\7G\2\2\u0322\u0324\5`\61\2\u0323\u0322\3\2\2\2\u0323"+
		"\u0324\3\2\2\2\u0324\u0325\3\2\2\2\u0325\u032c\7H\2\2\u0326\u0328\7\16"+
		"\2\2\u0327\u0329\5\16\b\2\u0328\u0327\3\2\2\2\u0328\u0329\3\2\2\2\u0329"+
		"\u032a\3\2\2\2\u032a\u032c\7\17\2\2\u032b\u0319\3\2\2\2\u032b\u031a\3"+
		"\2\2\2\u032b\u031b\3\2\2\2\u032b\u0321\3\2\2\2\u032b\u0326\3\2\2\2\u032c"+
		"]\3\2\2\2\u032d\u0336\7`\2\2\u032e\u0336\7a\2\2\u032f\u0336\7b\2\2\u0330"+
		"\u0336\7e\2\2\u0331\u0336\7d\2\2\u0332\u0336\7f\2\2\u0333\u0336\7g\2\2"+
		"\u0334\u0336\7h\2\2\u0335\u032d\3\2\2\2\u0335\u032e\3\2\2\2\u0335\u032f"+
		"\3\2\2\2\u0335\u0330\3\2\2\2\u0335\u0331\3\2\2\2\u0335\u0332\3\2\2\2\u0335"+
		"\u0333\3\2\2\2\u0335\u0334\3\2\2\2\u0336_\3\2\2\2\u0337\u033c\5\\/\2\u0338"+
		"\u0339\7\5\2\2\u0339\u033b\5\\/\2\u033a\u0338\3\2\2\2\u033b\u033e\3\2"+
		"\2\2\u033c\u033a\3\2\2\2\u033c\u033d\3\2\2\2\u033da\3\2\2\2\u033e\u033c"+
		"\3\2\2\2\u033f\u0344\5^\60\2\u0340\u0341\7\5\2\2\u0341\u0343\5^\60\2\u0342"+
		"\u0340\3\2\2\2\u0343\u0346\3\2\2\2\u0344\u0342\3\2\2\2\u0344\u0345\3\2"+
		"\2\2\u0345c\3\2\2\2\u0346\u0344\3\2\2\2\u0347\u034b\5f\64\2\u0348\u034b"+
		"\5|?\2\u0349\u034b\5\u0088E\2\u034a\u0347\3\2\2\2\u034a\u0348\3\2\2\2"+
		"\u034a\u0349\3\2\2\2\u034be\3\2\2\2\u034c\u034d\7I\2\2\u034d\u034e\7J"+
		"\2\2\u034e\u0350\5 \21\2\u034f\u0351\5h\65\2\u0350\u034f\3\2\2\2\u0350"+
		"\u0351\3\2\2\2\u0351\u0352\3\2\2\2\u0352\u0353\7\24\2\2\u0353\u0354\5"+
		"j\66\2\u0354\u0355\7\25\2\2\u0355g\3\2\2\2\u0356\u0357\7K\2\2\u0357\u0358"+
		"\7L\2\2\u0358i\3\2\2\2\u0359\u035c\5l\67\2\u035a\u035b\7\5\2\2\u035b\u035d"+
		"\5l\67\2\u035c\u035a\3\2\2\2\u035d\u035e\3\2\2\2\u035e\u035c\3\2\2\2\u035e"+
		"\u035f\3\2\2\2\u035fk\3\2\2\2\u0360\u0365\5n8\2\u0361\u0365\5p9\2\u0362"+
		"\u0365\5r:\2\u0363\u0365\5\f\7\2\u0364\u0360\3\2\2\2\u0364\u0361\3\2\2"+
		"\2\u0364\u0362\3\2\2\2\u0364\u0363\3\2\2\2\u0365m\3\2\2\2\u0366\u0367"+
		"\7k\2\2\u0367\u036a\5\u008aF\2\u0368\u0369\7j\2\2\u0369\u036b\7c\2\2\u036a"+
		"\u0368\3\2\2\2\u036a\u036b\3\2\2\2\u036b\u036e\3\2\2\2\u036c\u036d\7)"+
		"\2\2\u036d\u036f\5J&\2\u036e\u036c\3\2\2\2\u036e\u036f\3\2\2\2\u036f\u0371"+
		"\3\2\2\2\u0370\u0372\5\f\7\2\u0371\u0370\3\2\2\2\u0371\u0372\3\2\2\2\u0372"+
		"o\3\2\2\2\u0373\u0374\7k\2\2\u0374\u0375\7,\2\2\u0375\u0377\5J&\2\u0376"+
		"\u0378\5\f\7\2\u0377\u0376\3\2\2\2\u0377\u0378\3\2\2\2\u0378q\3\2\2\2"+
		"\u0379\u037b\5t;\2\u037a\u0379\3\2\2\2\u037a\u037b\3\2\2\2\u037b\u037c"+
		"\3\2\2\2\u037c\u037d\7M\2\2\u037d\u03a8\58\35\2\u037e\u0380\5t;\2\u037f"+
		"\u037e\3\2\2\2\u037f\u0380\3\2\2\2\u0380\u0381\3\2\2\2\u0381\u0382\7N"+
		"\2\2\u0382\u0383\7O\2\2\u0383\u03a8\58\35\2\u0384\u0386\5t;\2\u0385\u0384"+
		"\3\2\2\2\u0385\u0386\3\2\2\2\u0386\u0387\3\2\2\2\u0387\u0388\7P\2\2\u0388"+
		"\u0389\7\24\2\2\u0389\u038a\5J&\2\u038a\u038b\7\25\2\2\u038b\u03a8\3\2"+
		"\2\2\u038c\u038e\5t;\2\u038d\u038c\3\2\2\2\u038d\u038e\3\2\2\2\u038e\u038f"+
		"\3\2\2\2\u038f\u0390\7Q\2\2\u0390\u0391\7O\2\2\u0391\u0392\58\35\2\u0392"+
		"\u0393\7R\2\2\u0393\u0394\5 \21\2\u0394\u039d\58\35\2\u0395\u0396\7S\2"+
		"\2\u0396\u0397\7\24\2\2\u0397\u039a\7`\2\2\u0398\u0399\7\5\2\2\u0399\u039b"+
		"\7`\2\2\u039a\u0398\3\2\2\2\u039a\u039b\3\2\2\2\u039b\u039c\3\2\2\2\u039c"+
		"\u039e\7\25\2\2\u039d\u0395\3\2\2\2\u039d\u039e\3\2\2\2\u039e\u03a1\3"+
		"\2\2\2\u039f\u03a2\5v<\2\u03a0\u03a2\5x=\2\u03a1\u039f\3\2\2\2\u03a1\u03a0"+
		"\3\2\2\2\u03a1\u03a2\3\2\2\2\u03a2\u03a5\3\2\2\2\u03a3\u03a6\5v<\2\u03a4"+
		"\u03a6\5x=\2\u03a5\u03a3\3\2\2\2\u03a5\u03a4\3\2\2\2\u03a5\u03a6\3\2\2"+
		"\2\u03a6\u03a8\3\2\2\2\u03a7\u037a\3\2\2\2\u03a7\u037f\3\2\2\2\u03a7\u0385"+
		"\3\2\2\2\u03a7\u038d\3\2\2\2\u03a8s\3\2\2\2\u03a9\u03aa\7T\2\2\u03aa\u03ab"+
		"\7k\2\2\u03abu\3\2\2\2\u03ac\u03ad\7\23\2\2\u03ad\u03ae\7*\2\2\u03ae\u03af"+
		"\5z>\2\u03afw\3\2\2\2\u03b0\u03b1\7\23\2\2\u03b1\u03b2\7-\2\2\u03b2\u03b3"+
		"\5z>\2\u03b3y\3\2\2\2\u03b4\u03bb\7U\2\2\u03b5\u03bb\7V\2\2\u03b6\u03b7"+
		"\7+\2\2\u03b7\u03bb\7c\2\2\u03b8\u03b9\7+\2\2\u03b9\u03bb\7)\2\2\u03ba"+
		"\u03b4\3\2\2\2\u03ba\u03b5\3\2\2\2\u03ba\u03b6\3\2\2\2\u03ba\u03b8\3\2"+
		"\2\2\u03bb{\3\2\2\2\u03bc\u03bd\7W\2\2\u03bd\u03be\7J\2\2\u03be\u03bf"+
		"\5 \21\2\u03bf\u03c0\5~@\2\u03c0}\3\2\2\2\u03c1\u03c6\5\u0080A\2\u03c2"+
		"\u03c3\7\5\2\2\u03c3\u03c5\5\u0080A\2\u03c4\u03c2\3\2\2\2\u03c5\u03c8"+
		"\3\2\2\2\u03c6\u03c4\3\2\2\2\u03c6\u03c7\3\2\2\2\u03c7\177\3\2\2\2\u03c8"+
		"\u03c6\3\2\2\2\u03c9\u03ca\7X\2\2\u03ca\u03cb\7Y\2\2\u03cb\u03db\7k\2"+
		"\2\u03cc\u03cd\7Z\2\2\u03cd\u03db\5l\67\2\u03ce\u03cf\7W\2\2\u03cf\u03d0"+
		"\7[\2\2\u03d0\u03d1\7k\2\2\u03d1\u03db\5\u0082B\2\u03d2\u03d3\7K\2\2\u03d3"+
		"\u03d4\7[\2\2\u03d4\u03db\7k\2\2\u03d5\u03d6\7K\2\2\u03d6\u03d7\7T\2\2"+
		"\u03d7\u03db\7k\2\2\u03d8\u03d9\7K\2\2\u03d9\u03db\7\\\2\2\u03da\u03c9"+
		"\3\2\2\2\u03da\u03cc\3\2\2\2\u03da\u03ce\3\2\2\2\u03da\u03d2\3\2\2\2\u03da"+
		"\u03d5\3\2\2\2\u03da\u03d8\3\2\2\2\u03db\u0081\3\2\2\2\u03dc\u03de\7k"+
		"\2\2\u03dd\u03dc\3\2\2\2\u03dd\u03de\3\2\2\2\u03de\u03e0\3\2\2\2\u03df"+
		"\u03e1\5\u008aF\2\u03e0\u03df\3\2\2\2\u03e0\u03e1\3\2\2\2\u03e1\u03e3"+
		"\3\2\2\2\u03e2\u03e4\5\u0084C\2\u03e3\u03e2\3\2\2\2\u03e3\u03e4\3\2\2"+
		"\2\u03e4\u03e6\3\2\2\2\u03e5\u03e7\5\u0086D\2\u03e6\u03e5\3\2\2\2\u03e6"+
		"\u03e7\3\2\2\2\u03e7\u03e9\3\2\2\2\u03e8\u03ea\5\f\7\2\u03e9\u03e8\3\2"+
		"\2\2\u03e9\u03ea\3\2\2\2\u03ea\u0083\3\2\2\2\u03eb\u03ec\7j\2\2\u03ec"+
		"\u03ef\7c\2\2\u03ed\u03ef\7c\2\2\u03ee\u03eb\3\2\2\2\u03ee\u03ed\3\2\2"+
		"\2\u03ef\u0085\3\2\2\2\u03f0\u03f1\7)\2\2\u03f1\u03f5\5J&\2\u03f2\u03f3"+
		"\7]\2\2\u03f3\u03f5\7)\2\2\u03f4\u03f0\3\2\2\2\u03f4\u03f2\3\2\2\2\u03f5"+
		"\u0087\3\2\2\2\u03f6\u03f7\7K\2\2\u03f7\u03f8\7J\2\2\u03f8\u03f9\5 \21"+
		"\2\u03f9\u0089\3\2\2\2\u03fa\u03fb\bF\1\2\u03fb\u03fc\7k\2\2\u03fc\u0405"+
		"\3\2\2\2\u03fd\u03fe\f\3\2\2\u03fe\u0400\7G\2\2\u03ff\u0401\7`\2\2\u0400"+
		"\u03ff\3\2\2\2\u0400\u0401\3\2\2\2\u0401\u0402\3\2\2\2\u0402\u0404\7H"+
		"\2\2\u0403\u03fd\3\2\2\2\u0404\u0407\3\2\2\2\u0405\u0403\3\2\2\2\u0405"+
		"\u0406\3\2\2\2\u0406\u008b\3\2\2\2\u0407\u0405\3\2\2\2\u0083\u0091\u0095"+
		"\u009c\u00a0\u00a6\u00a8\u00ab\u00ae\u00b3\u00b7\u00bc\u00c0\u00c5\u00c9"+
		"\u00cd\u00d0\u00d8\u00e4\u00f2\u00f4\u00fd\u0103\u0107\u010a\u010d\u0113"+
		"\u011a\u011f\u0126\u012d\u0138\u0140\u0147\u014e\u0150\u0159\u0160\u016d"+
		"\u0174\u0179\u0182\u0186\u0190\u0195\u01a1\u01ab\u01af\u01b5\u01b9\u01bc"+
		"\u01c3\u01d5\u01d9\u01dc\u01e3\u01f0\u01f4\u01f7\u0202\u0206\u0227\u022b"+
		"\u022f\u0233\u024a\u0253\u0259\u0268\u026f\u027a\u0284\u028f\u0291\u0293"+
		"\u02a7\u02ab\u02af\u02b2\u02c2\u02c9\u02d3\u02d5\u02d7\u02db\u02e1\u02e4"+
		"\u02e9\u02f0\u02f5\u02f9\u0300\u0305\u0316\u031e\u0323\u0328\u032b\u0335"+
		"\u033c\u0344\u034a\u0350\u035e\u0364\u036a\u036e\u0371\u0377\u037a\u037f"+
		"\u0385\u038d\u039a\u039d\u03a1\u03a5\u03a7\u03ba\u03c6\u03da\u03dd\u03e0"+
		"\u03e3\u03e6\u03e9\u03ee\u03f4\u0400\u0405";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}