// Generated from ma\vi\esql\grammar\Esql.g4 by ANTLR 4.9.3

package ma.vi.esql.grammar;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class EsqlLexer extends Lexer {
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
		T__94=95, T__95=96, T__96=97, T__97=98, T__98=99, T__99=100, T__100=101, 
		T__101=102, T__102=103, T__103=104, T__104=105, T__105=106, T__106=107, 
		T__107=108, T__108=109, T__109=110, EscapedIdentifier=111, IntegerLiteral=112, 
		FloatingPointLiteral=113, BooleanLiteral=114, NullLiteral=115, StringLiteral=116, 
		MultiLineStringLiteral=117, UuidLiteral=118, DateLiteral=119, IntervalLiteral=120, 
		Quantifier=121, Not=122, Identifier=123, Comment=124, Whitespace=125;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
			"T__9", "T__10", "T__11", "T__12", "T__13", "T__14", "T__15", "T__16", 
			"T__17", "T__18", "T__19", "T__20", "T__21", "T__22", "T__23", "T__24", 
			"T__25", "T__26", "T__27", "T__28", "T__29", "T__30", "T__31", "T__32", 
			"T__33", "T__34", "T__35", "T__36", "T__37", "T__38", "T__39", "T__40", 
			"T__41", "T__42", "T__43", "T__44", "T__45", "T__46", "T__47", "T__48", 
			"T__49", "T__50", "T__51", "T__52", "T__53", "T__54", "T__55", "T__56", 
			"T__57", "T__58", "T__59", "T__60", "T__61", "T__62", "T__63", "T__64", 
			"T__65", "T__66", "T__67", "T__68", "T__69", "T__70", "T__71", "T__72", 
			"T__73", "T__74", "T__75", "T__76", "T__77", "T__78", "T__79", "T__80", 
			"T__81", "T__82", "T__83", "T__84", "T__85", "T__86", "T__87", "T__88", 
			"T__89", "T__90", "T__91", "T__92", "T__93", "T__94", "T__95", "T__96", 
			"T__97", "T__98", "T__99", "T__100", "T__101", "T__102", "T__103", "T__104", 
			"T__105", "T__106", "T__107", "T__108", "T__109", "EscapedIdentifier", 
			"IntegerLiteral", "FloatingPointLiteral", "Digits", "BooleanLiteral", 
			"NullLiteral", "StringLiteral", "MultiLineStringLiteral", "UuidLiteral", 
			"DateLiteral", "Date", "Time", "IntervalLiteral", "IntervalPart", "IntervalSuffix", 
			"Quantifier", "Not", "Digit", "ExponentPart", "SignedInteger", "Sign", 
			"StringCharacter", "DoubleSingleQuote", "HexDigit", "Identifier", "Comment", 
			"Whitespace"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "';'", "'select'", "','", "'from'", "'where'", "'group'", "'by'", 
			"'having'", "'order'", "'offset'", "'limit'", "'{'", "'}'", "':'", "'all'", 
			"'distinct'", "'on'", "'('", "')'", "'explicit'", "'*'", "'.'", "'/'", 
			"'times'", "'left'", "'right'", "'full'", "'join'", "'lateral'", "'rollup'", 
			"'cube'", "'asc'", "'desc'", "'union'", "'intersect'", "'except'", "'with'", 
			"'recursive'", "'insert'", "'into'", "'values'", "'return'", "'default'", 
			"'update'", "'set'", "'='", "'delete'", "'$('", "'<'", "'>'", "'||'", 
			"'-'", "'^'", "'%'", "'+'", "'in'", "'between'", "'and'", "'like'", "'ilike'", 
			"'is'", "'or'", "'if'", "'else'", "'function'", "'end'", "'let'", "':='", 
			"'then'", "'elseif'", "'for'", "'do'", "'while'", "'break'", "'continue'", 
			"'over'", "'partition'", "'rows'", "'range'", "'preceding'", "'row'", 
			"'following'", "'unbounded'", "'current'", "'!='", "'<='", "'>='", "'['", 
			"']'", "'create'", "'table'", "'drop'", "'undefined'", "'unique'", "'primary'", 
			"'key'", "'check'", "'foreign'", "'references'", "'cost'", "'constraint'", 
			"'restrict'", "'cascade'", "'alter'", "'rename'", "'to'", "'add'", "'column'", 
			"'metadata'", "'no'", null, null, null, null, "'null'", null, null, null, 
			null, null, null, "'not'"
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
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, "EscapedIdentifier", "IntegerLiteral", "FloatingPointLiteral", 
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


	public EsqlLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Esql.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\177\u046c\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t"+
		" \4!\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t"+
		"+\4,\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64"+
		"\t\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t"+
		"=\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\4"+
		"I\tI\4J\tJ\4K\tK\4L\tL\4M\tM\4N\tN\4O\tO\4P\tP\4Q\tQ\4R\tR\4S\tS\4T\t"+
		"T\4U\tU\4V\tV\4W\tW\4X\tX\4Y\tY\4Z\tZ\4[\t[\4\\\t\\\4]\t]\4^\t^\4_\t_"+
		"\4`\t`\4a\ta\4b\tb\4c\tc\4d\td\4e\te\4f\tf\4g\tg\4h\th\4i\ti\4j\tj\4k"+
		"\tk\4l\tl\4m\tm\4n\tn\4o\to\4p\tp\4q\tq\4r\tr\4s\ts\4t\tt\4u\tu\4v\tv"+
		"\4w\tw\4x\tx\4y\ty\4z\tz\4{\t{\4|\t|\4}\t}\4~\t~\4\177\t\177\4\u0080\t"+
		"\u0080\4\u0081\t\u0081\4\u0082\t\u0082\4\u0083\t\u0083\4\u0084\t\u0084"+
		"\4\u0085\t\u0085\4\u0086\t\u0086\4\u0087\t\u0087\4\u0088\t\u0088\4\u0089"+
		"\t\u0089\4\u008a\t\u008a\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\4\3\4\3"+
		"\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\7\3\b"+
		"\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\13\3\13"+
		"\3\13\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\16\3\16\3"+
		"\17\3\17\3\20\3\20\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3"+
		"\21\3\22\3\22\3\22\3\23\3\23\3\24\3\24\3\25\3\25\3\25\3\25\3\25\3\25\3"+
		"\25\3\25\3\25\3\26\3\26\3\27\3\27\3\30\3\30\3\31\3\31\3\31\3\31\3\31\3"+
		"\31\3\32\3\32\3\32\3\32\3\32\3\33\3\33\3\33\3\33\3\33\3\33\3\34\3\34\3"+
		"\34\3\34\3\34\3\35\3\35\3\35\3\35\3\35\3\36\3\36\3\36\3\36\3\36\3\36\3"+
		"\36\3\36\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3 \3 \3 \3 \3 \3!\3!\3!\3"+
		"!\3\"\3\"\3\"\3\"\3\"\3#\3#\3#\3#\3#\3#\3$\3$\3$\3$\3$\3$\3$\3$\3$\3$"+
		"\3%\3%\3%\3%\3%\3%\3%\3&\3&\3&\3&\3&\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3"+
		"\'\3\'\3(\3(\3(\3(\3(\3(\3(\3)\3)\3)\3)\3)\3*\3*\3*\3*\3*\3*\3*\3+\3+"+
		"\3+\3+\3+\3+\3+\3,\3,\3,\3,\3,\3,\3,\3,\3-\3-\3-\3-\3-\3-\3-\3.\3.\3."+
		"\3.\3/\3/\3\60\3\60\3\60\3\60\3\60\3\60\3\60\3\61\3\61\3\61\3\62\3\62"+
		"\3\63\3\63\3\64\3\64\3\64\3\65\3\65\3\66\3\66\3\67\3\67\38\38\39\39\3"+
		"9\3:\3:\3:\3:\3:\3:\3:\3:\3;\3;\3;\3;\3<\3<\3<\3<\3<\3=\3=\3=\3=\3=\3"+
		"=\3>\3>\3>\3?\3?\3?\3@\3@\3@\3A\3A\3A\3A\3A\3B\3B\3B\3B\3B\3B\3B\3B\3"+
		"B\3C\3C\3C\3C\3D\3D\3D\3D\3E\3E\3E\3F\3F\3F\3F\3F\3G\3G\3G\3G\3G\3G\3"+
		"G\3H\3H\3H\3H\3I\3I\3I\3J\3J\3J\3J\3J\3J\3K\3K\3K\3K\3K\3K\3L\3L\3L\3"+
		"L\3L\3L\3L\3L\3L\3M\3M\3M\3M\3M\3N\3N\3N\3N\3N\3N\3N\3N\3N\3N\3O\3O\3"+
		"O\3O\3O\3P\3P\3P\3P\3P\3P\3Q\3Q\3Q\3Q\3Q\3Q\3Q\3Q\3Q\3Q\3R\3R\3R\3R\3"+
		"S\3S\3S\3S\3S\3S\3S\3S\3S\3S\3T\3T\3T\3T\3T\3T\3T\3T\3T\3T\3U\3U\3U\3"+
		"U\3U\3U\3U\3U\3V\3V\3V\3W\3W\3W\3X\3X\3X\3Y\3Y\3Z\3Z\3[\3[\3[\3[\3[\3"+
		"[\3[\3\\\3\\\3\\\3\\\3\\\3\\\3]\3]\3]\3]\3]\3^\3^\3^\3^\3^\3^\3^\3^\3"+
		"^\3^\3_\3_\3_\3_\3_\3_\3_\3`\3`\3`\3`\3`\3`\3`\3`\3a\3a\3a\3a\3b\3b\3"+
		"b\3b\3b\3b\3c\3c\3c\3c\3c\3c\3c\3c\3d\3d\3d\3d\3d\3d\3d\3d\3d\3d\3d\3"+
		"e\3e\3e\3e\3e\3f\3f\3f\3f\3f\3f\3f\3f\3f\3f\3f\3g\3g\3g\3g\3g\3g\3g\3"+
		"g\3g\3h\3h\3h\3h\3h\3h\3h\3h\3i\3i\3i\3i\3i\3i\3j\3j\3j\3j\3j\3j\3j\3"+
		"k\3k\3k\3l\3l\3l\3l\3m\3m\3m\3m\3m\3m\3m\3n\3n\3n\3n\3n\3n\3n\3n\3n\3"+
		"o\3o\3o\3p\3p\6p\u0365\np\rp\16p\u0366\3p\3p\3q\3q\3r\3r\3r\5r\u0370\n"+
		"r\3r\5r\u0373\nr\3r\3r\3r\5r\u0378\nr\3r\3r\3r\5r\u037d\nr\3s\3s\3s\3"+
		"s\7s\u0383\ns\fs\16s\u0386\13s\3s\3s\5s\u038a\ns\3t\3t\3t\3t\3t\3t\3t"+
		"\3t\3t\5t\u0395\nt\3u\3u\3u\3u\3u\3v\3v\3v\7v\u039f\nv\fv\16v\u03a2\13"+
		"v\3v\3v\3w\3w\7w\u03a8\nw\fw\16w\u03ab\13w\3w\3w\3x\3x\3x\3x\3x\3x\3x"+
		"\3x\3x\3x\3x\3x\3x\3x\3x\3x\3x\3x\3x\3x\3x\3x\3x\3x\3x\3x\3x\3x\3x\3x"+
		"\3x\3x\3x\3x\3x\3x\3x\3x\3x\3x\3x\3y\3y\3y\3y\3y\3y\3y\3y\3y\3y\3y\3y"+
		"\3y\3y\3y\3y\3y\3y\3y\3y\5y\u03ec\ny\3z\5z\u03ef\nz\3z\5z\u03f2\nz\3z"+
		"\3z\3z\3z\5z\u03f8\nz\3z\3z\3z\5z\u03fd\nz\3z\3z\3{\5{\u0402\n{\3{\3{"+
		"\3{\5{\u0407\n{\3{\3{\3{\5{\u040c\n{\3{\3{\3{\3{\5{\u0412\n{\3{\5{\u0415"+
		"\n{\5{\u0417\n{\5{\u0419\n{\3|\3|\3|\3|\3|\3|\7|\u0421\n|\f|\16|\u0424"+
		"\13|\3|\3|\3}\5}\u0429\n}\3}\3}\3}\3~\3~\3~\3~\3~\3~\3~\5~\u0435\n~\3"+
		"\177\3\177\3\177\3\177\3\177\3\177\5\177\u043d\n\177\3\u0080\3\u0080\3"+
		"\u0080\3\u0080\3\u0081\3\u0081\3\u0082\3\u0082\3\u0082\3\u0083\5\u0083"+
		"\u0449\n\u0083\3\u0083\3\u0083\3\u0084\3\u0084\3\u0085\3\u0085\3\u0086"+
		"\3\u0086\3\u0086\3\u0087\3\u0087\3\u0088\3\u0088\7\u0088\u0458\n\u0088"+
		"\f\u0088\16\u0088\u045b\13\u0088\3\u0089\3\u0089\7\u0089\u045f\n\u0089"+
		"\f\u0089\16\u0089\u0462\13\u0089\3\u0089\3\u0089\3\u008a\6\u008a\u0467"+
		"\n\u008a\r\u008a\16\u008a\u0468\3\u008a\3\u008a\3\u03a9\2\u008b\3\3\5"+
		"\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21"+
		"!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\34\67\359\36;\37= ?!"+
		"A\"C#E$G%I&K\'M(O)Q*S+U,W-Y.[/]\60_\61a\62c\63e\64g\65i\66k\67m8o9q:s"+
		";u<w=y>{?}@\177A\u0081B\u0083C\u0085D\u0087E\u0089F\u008bG\u008dH\u008f"+
		"I\u0091J\u0093K\u0095L\u0097M\u0099N\u009bO\u009dP\u009fQ\u00a1R\u00a3"+
		"S\u00a5T\u00a7U\u00a9V\u00abW\u00adX\u00afY\u00b1Z\u00b3[\u00b5\\\u00b7"+
		"]\u00b9^\u00bb_\u00bd`\u00bfa\u00c1b\u00c3c\u00c5d\u00c7e\u00c9f\u00cb"+
		"g\u00cdh\u00cfi\u00d1j\u00d3k\u00d5l\u00d7m\u00d9n\u00dbo\u00ddp\u00df"+
		"q\u00e1r\u00e3s\u00e5\2\u00e7t\u00e9u\u00ebv\u00edw\u00efx\u00f1y\u00f3"+
		"\2\u00f5\2\u00f7z\u00f9\2\u00fb\2\u00fd{\u00ff|\u0101\2\u0103\2\u0105"+
		"\2\u0107\2\u0109\2\u010b\2\u010d\2\u010f}\u0111~\u0113\177\3\2\21\3\2"+
		"$$\3\2\62\63\3\2\62\65\3\2\62\67\4\2ffyy\6\2jjoouu{{\3\2\62;\4\2GGgg\4"+
		"\2--//\3\2))\5\2\62;CHch\6\2&&C\\aac|\7\2&&\62;C\\aac|\4\2\f\f\17\17\5"+
		"\2\13\f\17\17\"\"\2\u0483\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2"+
		"\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2"+
		"\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3"+
		"\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2"+
		"\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67"+
		"\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2"+
		"\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2"+
		"\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]"+
		"\3\2\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2\2g\3\2\2\2\2i\3\2"+
		"\2\2\2k\3\2\2\2\2m\3\2\2\2\2o\3\2\2\2\2q\3\2\2\2\2s\3\2\2\2\2u\3\2\2\2"+
		"\2w\3\2\2\2\2y\3\2\2\2\2{\3\2\2\2\2}\3\2\2\2\2\177\3\2\2\2\2\u0081\3\2"+
		"\2\2\2\u0083\3\2\2\2\2\u0085\3\2\2\2\2\u0087\3\2\2\2\2\u0089\3\2\2\2\2"+
		"\u008b\3\2\2\2\2\u008d\3\2\2\2\2\u008f\3\2\2\2\2\u0091\3\2\2\2\2\u0093"+
		"\3\2\2\2\2\u0095\3\2\2\2\2\u0097\3\2\2\2\2\u0099\3\2\2\2\2\u009b\3\2\2"+
		"\2\2\u009d\3\2\2\2\2\u009f\3\2\2\2\2\u00a1\3\2\2\2\2\u00a3\3\2\2\2\2\u00a5"+
		"\3\2\2\2\2\u00a7\3\2\2\2\2\u00a9\3\2\2\2\2\u00ab\3\2\2\2\2\u00ad\3\2\2"+
		"\2\2\u00af\3\2\2\2\2\u00b1\3\2\2\2\2\u00b3\3\2\2\2\2\u00b5\3\2\2\2\2\u00b7"+
		"\3\2\2\2\2\u00b9\3\2\2\2\2\u00bb\3\2\2\2\2\u00bd\3\2\2\2\2\u00bf\3\2\2"+
		"\2\2\u00c1\3\2\2\2\2\u00c3\3\2\2\2\2\u00c5\3\2\2\2\2\u00c7\3\2\2\2\2\u00c9"+
		"\3\2\2\2\2\u00cb\3\2\2\2\2\u00cd\3\2\2\2\2\u00cf\3\2\2\2\2\u00d1\3\2\2"+
		"\2\2\u00d3\3\2\2\2\2\u00d5\3\2\2\2\2\u00d7\3\2\2\2\2\u00d9\3\2\2\2\2\u00db"+
		"\3\2\2\2\2\u00dd\3\2\2\2\2\u00df\3\2\2\2\2\u00e1\3\2\2\2\2\u00e3\3\2\2"+
		"\2\2\u00e7\3\2\2\2\2\u00e9\3\2\2\2\2\u00eb\3\2\2\2\2\u00ed\3\2\2\2\2\u00ef"+
		"\3\2\2\2\2\u00f1\3\2\2\2\2\u00f7\3\2\2\2\2\u00fd\3\2\2\2\2\u00ff\3\2\2"+
		"\2\2\u010f\3\2\2\2\2\u0111\3\2\2\2\2\u0113\3\2\2\2\3\u0115\3\2\2\2\5\u0117"+
		"\3\2\2\2\7\u011e\3\2\2\2\t\u0120\3\2\2\2\13\u0125\3\2\2\2\r\u012b\3\2"+
		"\2\2\17\u0131\3\2\2\2\21\u0134\3\2\2\2\23\u013b\3\2\2\2\25\u0141\3\2\2"+
		"\2\27\u0148\3\2\2\2\31\u014e\3\2\2\2\33\u0150\3\2\2\2\35\u0152\3\2\2\2"+
		"\37\u0154\3\2\2\2!\u0158\3\2\2\2#\u0161\3\2\2\2%\u0164\3\2\2\2\'\u0166"+
		"\3\2\2\2)\u0168\3\2\2\2+\u0171\3\2\2\2-\u0173\3\2\2\2/\u0175\3\2\2\2\61"+
		"\u0177\3\2\2\2\63\u017d\3\2\2\2\65\u0182\3\2\2\2\67\u0188\3\2\2\29\u018d"+
		"\3\2\2\2;\u0192\3\2\2\2=\u019a\3\2\2\2?\u01a1\3\2\2\2A\u01a6\3\2\2\2C"+
		"\u01aa\3\2\2\2E\u01af\3\2\2\2G\u01b5\3\2\2\2I\u01bf\3\2\2\2K\u01c6\3\2"+
		"\2\2M\u01cb\3\2\2\2O\u01d5\3\2\2\2Q\u01dc\3\2\2\2S\u01e1\3\2\2\2U\u01e8"+
		"\3\2\2\2W\u01ef\3\2\2\2Y\u01f7\3\2\2\2[\u01fe\3\2\2\2]\u0202\3\2\2\2_"+
		"\u0204\3\2\2\2a\u020b\3\2\2\2c\u020e\3\2\2\2e\u0210\3\2\2\2g\u0212\3\2"+
		"\2\2i\u0215\3\2\2\2k\u0217\3\2\2\2m\u0219\3\2\2\2o\u021b\3\2\2\2q\u021d"+
		"\3\2\2\2s\u0220\3\2\2\2u\u0228\3\2\2\2w\u022c\3\2\2\2y\u0231\3\2\2\2{"+
		"\u0237\3\2\2\2}\u023a\3\2\2\2\177\u023d\3\2\2\2\u0081\u0240\3\2\2\2\u0083"+
		"\u0245\3\2\2\2\u0085\u024e\3\2\2\2\u0087\u0252\3\2\2\2\u0089\u0256\3\2"+
		"\2\2\u008b\u0259\3\2\2\2\u008d\u025e\3\2\2\2\u008f\u0265\3\2\2\2\u0091"+
		"\u0269\3\2\2\2\u0093\u026c\3\2\2\2\u0095\u0272\3\2\2\2\u0097\u0278\3\2"+
		"\2\2\u0099\u0281\3\2\2\2\u009b\u0286\3\2\2\2\u009d\u0290\3\2\2\2\u009f"+
		"\u0295\3\2\2\2\u00a1\u029b\3\2\2\2\u00a3\u02a5\3\2\2\2\u00a5\u02a9\3\2"+
		"\2\2\u00a7\u02b3\3\2\2\2\u00a9\u02bd\3\2\2\2\u00ab\u02c5\3\2\2\2\u00ad"+
		"\u02c8\3\2\2\2\u00af\u02cb\3\2\2\2\u00b1\u02ce\3\2\2\2\u00b3\u02d0\3\2"+
		"\2\2\u00b5\u02d2\3\2\2\2\u00b7\u02d9\3\2\2\2\u00b9\u02df\3\2\2\2\u00bb"+
		"\u02e4\3\2\2\2\u00bd\u02ee\3\2\2\2\u00bf\u02f5\3\2\2\2\u00c1\u02fd\3\2"+
		"\2\2\u00c3\u0301\3\2\2\2\u00c5\u0307\3\2\2\2\u00c7\u030f\3\2\2\2\u00c9"+
		"\u031a\3\2\2\2\u00cb\u031f\3\2\2\2\u00cd\u032a\3\2\2\2\u00cf\u0333\3\2"+
		"\2\2\u00d1\u033b\3\2\2\2\u00d3\u0341\3\2\2\2\u00d5\u0348\3\2\2\2\u00d7"+
		"\u034b\3\2\2\2\u00d9\u034f\3\2\2\2\u00db\u0356\3\2\2\2\u00dd\u035f\3\2"+
		"\2\2\u00df\u0362\3\2\2\2\u00e1\u036a\3\2\2\2\u00e3\u037c\3\2\2\2\u00e5"+
		"\u0389\3\2\2\2\u00e7\u0394\3\2\2\2\u00e9\u0396\3\2\2\2\u00eb\u039b\3\2"+
		"\2\2\u00ed\u03a5\3\2\2\2\u00ef\u03ae\3\2\2\2\u00f1\u03eb\3\2\2\2\u00f3"+
		"\u03ee\3\2\2\2\u00f5\u0401\3\2\2\2\u00f7\u041a\3\2\2\2\u00f9\u0428\3\2"+
		"\2\2\u00fb\u0434\3\2\2\2\u00fd\u043c\3\2\2\2\u00ff\u043e\3\2\2\2\u0101"+
		"\u0442\3\2\2\2\u0103\u0444\3\2\2\2\u0105\u0448\3\2\2\2\u0107\u044c\3\2"+
		"\2\2\u0109\u044e\3\2\2\2\u010b\u0450\3\2\2\2\u010d\u0453\3\2\2\2\u010f"+
		"\u0455\3\2\2\2\u0111\u045c\3\2\2\2\u0113\u0466\3\2\2\2\u0115\u0116\7="+
		"\2\2\u0116\4\3\2\2\2\u0117\u0118\7u\2\2\u0118\u0119\7g\2\2\u0119\u011a"+
		"\7n\2\2\u011a\u011b\7g\2\2\u011b\u011c\7e\2\2\u011c\u011d\7v\2\2\u011d"+
		"\6\3\2\2\2\u011e\u011f\7.\2\2\u011f\b\3\2\2\2\u0120\u0121\7h\2\2\u0121"+
		"\u0122\7t\2\2\u0122\u0123\7q\2\2\u0123\u0124\7o\2\2\u0124\n\3\2\2\2\u0125"+
		"\u0126\7y\2\2\u0126\u0127\7j\2\2\u0127\u0128\7g\2\2\u0128\u0129\7t\2\2"+
		"\u0129\u012a\7g\2\2\u012a\f\3\2\2\2\u012b\u012c\7i\2\2\u012c\u012d\7t"+
		"\2\2\u012d\u012e\7q\2\2\u012e\u012f\7w\2\2\u012f\u0130\7r\2\2\u0130\16"+
		"\3\2\2\2\u0131\u0132\7d\2\2\u0132\u0133\7{\2\2\u0133\20\3\2\2\2\u0134"+
		"\u0135\7j\2\2\u0135\u0136\7c\2\2\u0136\u0137\7x\2\2\u0137\u0138\7k\2\2"+
		"\u0138\u0139\7p\2\2\u0139\u013a\7i\2\2\u013a\22\3\2\2\2\u013b\u013c\7"+
		"q\2\2\u013c\u013d\7t\2\2\u013d\u013e\7f\2\2\u013e\u013f\7g\2\2\u013f\u0140"+
		"\7t\2\2\u0140\24\3\2\2\2\u0141\u0142\7q\2\2\u0142\u0143\7h\2\2\u0143\u0144"+
		"\7h\2\2\u0144\u0145\7u\2\2\u0145\u0146\7g\2\2\u0146\u0147\7v\2\2\u0147"+
		"\26\3\2\2\2\u0148\u0149\7n\2\2\u0149\u014a\7k\2\2\u014a\u014b\7o\2\2\u014b"+
		"\u014c\7k\2\2\u014c\u014d\7v\2\2\u014d\30\3\2\2\2\u014e\u014f\7}\2\2\u014f"+
		"\32\3\2\2\2\u0150\u0151\7\177\2\2\u0151\34\3\2\2\2\u0152\u0153\7<\2\2"+
		"\u0153\36\3\2\2\2\u0154\u0155\7c\2\2\u0155\u0156\7n\2\2\u0156\u0157\7"+
		"n\2\2\u0157 \3\2\2\2\u0158\u0159\7f\2\2\u0159\u015a\7k\2\2\u015a\u015b"+
		"\7u\2\2\u015b\u015c\7v\2\2\u015c\u015d\7k\2\2\u015d\u015e\7p\2\2\u015e"+
		"\u015f\7e\2\2\u015f\u0160\7v\2\2\u0160\"\3\2\2\2\u0161\u0162\7q\2\2\u0162"+
		"\u0163\7p\2\2\u0163$\3\2\2\2\u0164\u0165\7*\2\2\u0165&\3\2\2\2\u0166\u0167"+
		"\7+\2\2\u0167(\3\2\2\2\u0168\u0169\7g\2\2\u0169\u016a\7z\2\2\u016a\u016b"+
		"\7r\2\2\u016b\u016c\7n\2\2\u016c\u016d\7k\2\2\u016d\u016e\7e\2\2\u016e"+
		"\u016f\7k\2\2\u016f\u0170\7v\2\2\u0170*\3\2\2\2\u0171\u0172\7,\2\2\u0172"+
		",\3\2\2\2\u0173\u0174\7\60\2\2\u0174.\3\2\2\2\u0175\u0176\7\61\2\2\u0176"+
		"\60\3\2\2\2\u0177\u0178\7v\2\2\u0178\u0179\7k\2\2\u0179\u017a\7o\2\2\u017a"+
		"\u017b\7g\2\2\u017b\u017c\7u\2\2\u017c\62\3\2\2\2\u017d\u017e\7n\2\2\u017e"+
		"\u017f\7g\2\2\u017f\u0180\7h\2\2\u0180\u0181\7v\2\2\u0181\64\3\2\2\2\u0182"+
		"\u0183\7t\2\2\u0183\u0184\7k\2\2\u0184\u0185\7i\2\2\u0185\u0186\7j\2\2"+
		"\u0186\u0187\7v\2\2\u0187\66\3\2\2\2\u0188\u0189\7h\2\2\u0189\u018a\7"+
		"w\2\2\u018a\u018b\7n\2\2\u018b\u018c\7n\2\2\u018c8\3\2\2\2\u018d\u018e"+
		"\7l\2\2\u018e\u018f\7q\2\2\u018f\u0190\7k\2\2\u0190\u0191\7p\2\2\u0191"+
		":\3\2\2\2\u0192\u0193\7n\2\2\u0193\u0194\7c\2\2\u0194\u0195\7v\2\2\u0195"+
		"\u0196\7g\2\2\u0196\u0197\7t\2\2\u0197\u0198\7c\2\2\u0198\u0199\7n\2\2"+
		"\u0199<\3\2\2\2\u019a\u019b\7t\2\2\u019b\u019c\7q\2\2\u019c\u019d\7n\2"+
		"\2\u019d\u019e\7n\2\2\u019e\u019f\7w\2\2\u019f\u01a0\7r\2\2\u01a0>\3\2"+
		"\2\2\u01a1\u01a2\7e\2\2\u01a2\u01a3\7w\2\2\u01a3\u01a4\7d\2\2\u01a4\u01a5"+
		"\7g\2\2\u01a5@\3\2\2\2\u01a6\u01a7\7c\2\2\u01a7\u01a8\7u\2\2\u01a8\u01a9"+
		"\7e\2\2\u01a9B\3\2\2\2\u01aa\u01ab\7f\2\2\u01ab\u01ac\7g\2\2\u01ac\u01ad"+
		"\7u\2\2\u01ad\u01ae\7e\2\2\u01aeD\3\2\2\2\u01af\u01b0\7w\2\2\u01b0\u01b1"+
		"\7p\2\2\u01b1\u01b2\7k\2\2\u01b2\u01b3\7q\2\2\u01b3\u01b4\7p\2\2\u01b4"+
		"F\3\2\2\2\u01b5\u01b6\7k\2\2\u01b6\u01b7\7p\2\2\u01b7\u01b8\7v\2\2\u01b8"+
		"\u01b9\7g\2\2\u01b9\u01ba\7t\2\2\u01ba\u01bb\7u\2\2\u01bb\u01bc\7g\2\2"+
		"\u01bc\u01bd\7e\2\2\u01bd\u01be\7v\2\2\u01beH\3\2\2\2\u01bf\u01c0\7g\2"+
		"\2\u01c0\u01c1\7z\2\2\u01c1\u01c2\7e\2\2\u01c2\u01c3\7g\2\2\u01c3\u01c4"+
		"\7r\2\2\u01c4\u01c5\7v\2\2\u01c5J\3\2\2\2\u01c6\u01c7\7y\2\2\u01c7\u01c8"+
		"\7k\2\2\u01c8\u01c9\7v\2\2\u01c9\u01ca\7j\2\2\u01caL\3\2\2\2\u01cb\u01cc"+
		"\7t\2\2\u01cc\u01cd\7g\2\2\u01cd\u01ce\7e\2\2\u01ce\u01cf\7w\2\2\u01cf"+
		"\u01d0\7t\2\2\u01d0\u01d1\7u\2\2\u01d1\u01d2\7k\2\2\u01d2\u01d3\7x\2\2"+
		"\u01d3\u01d4\7g\2\2\u01d4N\3\2\2\2\u01d5\u01d6\7k\2\2\u01d6\u01d7\7p\2"+
		"\2\u01d7\u01d8\7u\2\2\u01d8\u01d9\7g\2\2\u01d9\u01da\7t\2\2\u01da\u01db"+
		"\7v\2\2\u01dbP\3\2\2\2\u01dc\u01dd\7k\2\2\u01dd\u01de\7p\2\2\u01de\u01df"+
		"\7v\2\2\u01df\u01e0\7q\2\2\u01e0R\3\2\2\2\u01e1\u01e2\7x\2\2\u01e2\u01e3"+
		"\7c\2\2\u01e3\u01e4\7n\2\2\u01e4\u01e5\7w\2\2\u01e5\u01e6\7g\2\2\u01e6"+
		"\u01e7\7u\2\2\u01e7T\3\2\2\2\u01e8\u01e9\7t\2\2\u01e9\u01ea\7g\2\2\u01ea"+
		"\u01eb\7v\2\2\u01eb\u01ec\7w\2\2\u01ec\u01ed\7t\2\2\u01ed\u01ee\7p\2\2"+
		"\u01eeV\3\2\2\2\u01ef\u01f0\7f\2\2\u01f0\u01f1\7g\2\2\u01f1\u01f2\7h\2"+
		"\2\u01f2\u01f3\7c\2\2\u01f3\u01f4\7w\2\2\u01f4\u01f5\7n\2\2\u01f5\u01f6"+
		"\7v\2\2\u01f6X\3\2\2\2\u01f7\u01f8\7w\2\2\u01f8\u01f9\7r\2\2\u01f9\u01fa"+
		"\7f\2\2\u01fa\u01fb\7c\2\2\u01fb\u01fc\7v\2\2\u01fc\u01fd\7g\2\2\u01fd"+
		"Z\3\2\2\2\u01fe\u01ff\7u\2\2\u01ff\u0200\7g\2\2\u0200\u0201\7v\2\2\u0201"+
		"\\\3\2\2\2\u0202\u0203\7?\2\2\u0203^\3\2\2\2\u0204\u0205\7f\2\2\u0205"+
		"\u0206\7g\2\2\u0206\u0207\7n\2\2\u0207\u0208\7g\2\2\u0208\u0209\7v\2\2"+
		"\u0209\u020a\7g\2\2\u020a`\3\2\2\2\u020b\u020c\7&\2\2\u020c\u020d\7*\2"+
		"\2\u020db\3\2\2\2\u020e\u020f\7>\2\2\u020fd\3\2\2\2\u0210\u0211\7@\2\2"+
		"\u0211f\3\2\2\2\u0212\u0213\7~\2\2\u0213\u0214\7~\2\2\u0214h\3\2\2\2\u0215"+
		"\u0216\7/\2\2\u0216j\3\2\2\2\u0217\u0218\7`\2\2\u0218l\3\2\2\2\u0219\u021a"+
		"\7\'\2\2\u021an\3\2\2\2\u021b\u021c\7-\2\2\u021cp\3\2\2\2\u021d\u021e"+
		"\7k\2\2\u021e\u021f\7p\2\2\u021fr\3\2\2\2\u0220\u0221\7d\2\2\u0221\u0222"+
		"\7g\2\2\u0222\u0223\7v\2\2\u0223\u0224\7y\2\2\u0224\u0225\7g\2\2\u0225"+
		"\u0226\7g\2\2\u0226\u0227\7p\2\2\u0227t\3\2\2\2\u0228\u0229\7c\2\2\u0229"+
		"\u022a\7p\2\2\u022a\u022b\7f\2\2\u022bv\3\2\2\2\u022c\u022d\7n\2\2\u022d"+
		"\u022e\7k\2\2\u022e\u022f\7m\2\2\u022f\u0230\7g\2\2\u0230x\3\2\2\2\u0231"+
		"\u0232\7k\2\2\u0232\u0233\7n\2\2\u0233\u0234\7k\2\2\u0234\u0235\7m\2\2"+
		"\u0235\u0236\7g\2\2\u0236z\3\2\2\2\u0237\u0238\7k\2\2\u0238\u0239\7u\2"+
		"\2\u0239|\3\2\2\2\u023a\u023b\7q\2\2\u023b\u023c\7t\2\2\u023c~\3\2\2\2"+
		"\u023d\u023e\7k\2\2\u023e\u023f\7h\2\2\u023f\u0080\3\2\2\2\u0240\u0241"+
		"\7g\2\2\u0241\u0242\7n\2\2\u0242\u0243\7u\2\2\u0243\u0244\7g\2\2\u0244"+
		"\u0082\3\2\2\2\u0245\u0246\7h\2\2\u0246\u0247\7w\2\2\u0247\u0248\7p\2"+
		"\2\u0248\u0249\7e\2\2\u0249\u024a\7v\2\2\u024a\u024b\7k\2\2\u024b\u024c"+
		"\7q\2\2\u024c\u024d\7p\2\2\u024d\u0084\3\2\2\2\u024e\u024f\7g\2\2\u024f"+
		"\u0250\7p\2\2\u0250\u0251\7f\2\2\u0251\u0086\3\2\2\2\u0252\u0253\7n\2"+
		"\2\u0253\u0254\7g\2\2\u0254\u0255\7v\2\2\u0255\u0088\3\2\2\2\u0256\u0257"+
		"\7<\2\2\u0257\u0258\7?\2\2\u0258\u008a\3\2\2\2\u0259\u025a\7v\2\2\u025a"+
		"\u025b\7j\2\2\u025b\u025c\7g\2\2\u025c\u025d\7p\2\2\u025d\u008c\3\2\2"+
		"\2\u025e\u025f\7g\2\2\u025f\u0260\7n\2\2\u0260\u0261\7u\2\2\u0261\u0262"+
		"\7g\2\2\u0262\u0263\7k\2\2\u0263\u0264\7h\2\2\u0264\u008e\3\2\2\2\u0265"+
		"\u0266\7h\2\2\u0266\u0267\7q\2\2\u0267\u0268\7t\2\2\u0268\u0090\3\2\2"+
		"\2\u0269\u026a\7f\2\2\u026a\u026b\7q\2\2\u026b\u0092\3\2\2\2\u026c\u026d"+
		"\7y\2\2\u026d\u026e\7j\2\2\u026e\u026f\7k\2\2\u026f\u0270\7n\2\2\u0270"+
		"\u0271\7g\2\2\u0271\u0094\3\2\2\2\u0272\u0273\7d\2\2\u0273\u0274\7t\2"+
		"\2\u0274\u0275\7g\2\2\u0275\u0276\7c\2\2\u0276\u0277\7m\2\2\u0277\u0096"+
		"\3\2\2\2\u0278\u0279\7e\2\2\u0279\u027a\7q\2\2\u027a\u027b\7p\2\2\u027b"+
		"\u027c\7v\2\2\u027c\u027d\7k\2\2\u027d\u027e\7p\2\2\u027e\u027f\7w\2\2"+
		"\u027f\u0280\7g\2\2\u0280\u0098\3\2\2\2\u0281\u0282\7q\2\2\u0282\u0283"+
		"\7x\2\2\u0283\u0284\7g\2\2\u0284\u0285\7t\2\2\u0285\u009a\3\2\2\2\u0286"+
		"\u0287\7r\2\2\u0287\u0288\7c\2\2\u0288\u0289\7t\2\2\u0289\u028a\7v\2\2"+
		"\u028a\u028b\7k\2\2\u028b\u028c\7v\2\2\u028c\u028d\7k\2\2\u028d\u028e"+
		"\7q\2\2\u028e\u028f\7p\2\2\u028f\u009c\3\2\2\2\u0290\u0291\7t\2\2\u0291"+
		"\u0292\7q\2\2\u0292\u0293\7y\2\2\u0293\u0294\7u\2\2\u0294\u009e\3\2\2"+
		"\2\u0295\u0296\7t\2\2\u0296\u0297\7c\2\2\u0297\u0298\7p\2\2\u0298\u0299"+
		"\7i\2\2\u0299\u029a\7g\2\2\u029a\u00a0\3\2\2\2\u029b\u029c\7r\2\2\u029c"+
		"\u029d\7t\2\2\u029d\u029e\7g\2\2\u029e\u029f\7e\2\2\u029f\u02a0\7g\2\2"+
		"\u02a0\u02a1\7f\2\2\u02a1\u02a2\7k\2\2\u02a2\u02a3\7p\2\2\u02a3\u02a4"+
		"\7i\2\2\u02a4\u00a2\3\2\2\2\u02a5\u02a6\7t\2\2\u02a6\u02a7\7q\2\2\u02a7"+
		"\u02a8\7y\2\2\u02a8\u00a4\3\2\2\2\u02a9\u02aa\7h\2\2\u02aa\u02ab\7q\2"+
		"\2\u02ab\u02ac\7n\2\2\u02ac\u02ad\7n\2\2\u02ad\u02ae\7q\2\2\u02ae\u02af"+
		"\7y\2\2\u02af\u02b0\7k\2\2\u02b0\u02b1\7p\2\2\u02b1\u02b2\7i\2\2\u02b2"+
		"\u00a6\3\2\2\2\u02b3\u02b4\7w\2\2\u02b4\u02b5\7p\2\2\u02b5\u02b6\7d\2"+
		"\2\u02b6\u02b7\7q\2\2\u02b7\u02b8\7w\2\2\u02b8\u02b9\7p\2\2\u02b9\u02ba"+
		"\7f\2\2\u02ba\u02bb\7g\2\2\u02bb\u02bc\7f\2\2\u02bc\u00a8\3\2\2\2\u02bd"+
		"\u02be\7e\2\2\u02be\u02bf\7w\2\2\u02bf\u02c0\7t\2\2\u02c0\u02c1\7t\2\2"+
		"\u02c1\u02c2\7g\2\2\u02c2\u02c3\7p\2\2\u02c3\u02c4\7v\2\2\u02c4\u00aa"+
		"\3\2\2\2\u02c5\u02c6\7#\2\2\u02c6\u02c7\7?\2\2\u02c7\u00ac\3\2\2\2\u02c8"+
		"\u02c9\7>\2\2\u02c9\u02ca\7?\2\2\u02ca\u00ae\3\2\2\2\u02cb\u02cc\7@\2"+
		"\2\u02cc\u02cd\7?\2\2\u02cd\u00b0\3\2\2\2\u02ce\u02cf\7]\2\2\u02cf\u00b2"+
		"\3\2\2\2\u02d0\u02d1\7_\2\2\u02d1\u00b4\3\2\2\2\u02d2\u02d3\7e\2\2\u02d3"+
		"\u02d4\7t\2\2\u02d4\u02d5\7g\2\2\u02d5\u02d6\7c\2\2\u02d6\u02d7\7v\2\2"+
		"\u02d7\u02d8\7g\2\2\u02d8\u00b6\3\2\2\2\u02d9\u02da\7v\2\2\u02da\u02db"+
		"\7c\2\2\u02db\u02dc\7d\2\2\u02dc\u02dd\7n\2\2\u02dd\u02de\7g\2\2\u02de"+
		"\u00b8\3\2\2\2\u02df\u02e0\7f\2\2\u02e0\u02e1\7t\2\2\u02e1\u02e2\7q\2"+
		"\2\u02e2\u02e3\7r\2\2\u02e3\u00ba\3\2\2\2\u02e4\u02e5\7w\2\2\u02e5\u02e6"+
		"\7p\2\2\u02e6\u02e7\7f\2\2\u02e7\u02e8\7g\2\2\u02e8\u02e9\7h\2\2\u02e9"+
		"\u02ea\7k\2\2\u02ea\u02eb\7p\2\2\u02eb\u02ec\7g\2\2\u02ec\u02ed\7f\2\2"+
		"\u02ed\u00bc\3\2\2\2\u02ee\u02ef\7w\2\2\u02ef\u02f0\7p\2\2\u02f0\u02f1"+
		"\7k\2\2\u02f1\u02f2\7s\2\2\u02f2\u02f3\7w\2\2\u02f3\u02f4\7g\2\2\u02f4"+
		"\u00be\3\2\2\2\u02f5\u02f6\7r\2\2\u02f6\u02f7\7t\2\2\u02f7\u02f8\7k\2"+
		"\2\u02f8\u02f9\7o\2\2\u02f9\u02fa\7c\2\2\u02fa\u02fb\7t\2\2\u02fb\u02fc"+
		"\7{\2\2\u02fc\u00c0\3\2\2\2\u02fd\u02fe\7m\2\2\u02fe\u02ff\7g\2\2\u02ff"+
		"\u0300\7{\2\2\u0300\u00c2\3\2\2\2\u0301\u0302\7e\2\2\u0302\u0303\7j\2"+
		"\2\u0303\u0304\7g\2\2\u0304\u0305\7e\2\2\u0305\u0306\7m\2\2\u0306\u00c4"+
		"\3\2\2\2\u0307\u0308\7h\2\2\u0308\u0309\7q\2\2\u0309\u030a\7t\2\2\u030a"+
		"\u030b\7g\2\2\u030b\u030c\7k\2\2\u030c\u030d\7i\2\2\u030d\u030e\7p\2\2"+
		"\u030e\u00c6\3\2\2\2\u030f\u0310\7t\2\2\u0310\u0311\7g\2\2\u0311\u0312"+
		"\7h\2\2\u0312\u0313\7g\2\2\u0313\u0314\7t\2\2\u0314\u0315\7g\2\2\u0315"+
		"\u0316\7p\2\2\u0316\u0317\7e\2\2\u0317\u0318\7g\2\2\u0318\u0319\7u\2\2"+
		"\u0319\u00c8\3\2\2\2\u031a\u031b\7e\2\2\u031b\u031c\7q\2\2\u031c\u031d"+
		"\7u\2\2\u031d\u031e\7v\2\2\u031e\u00ca\3\2\2\2\u031f\u0320\7e\2\2\u0320"+
		"\u0321\7q\2\2\u0321\u0322\7p\2\2\u0322\u0323\7u\2\2\u0323\u0324\7v\2\2"+
		"\u0324\u0325\7t\2\2\u0325\u0326\7c\2\2\u0326\u0327\7k\2\2\u0327\u0328"+
		"\7p\2\2\u0328\u0329\7v\2\2\u0329\u00cc\3\2\2\2\u032a\u032b\7t\2\2\u032b"+
		"\u032c\7g\2\2\u032c\u032d\7u\2\2\u032d\u032e\7v\2\2\u032e\u032f\7t\2\2"+
		"\u032f\u0330\7k\2\2\u0330\u0331\7e\2\2\u0331\u0332\7v\2\2\u0332\u00ce"+
		"\3\2\2\2\u0333\u0334\7e\2\2\u0334\u0335\7c\2\2\u0335\u0336\7u\2\2\u0336"+
		"\u0337\7e\2\2\u0337\u0338\7c\2\2\u0338\u0339\7f\2\2\u0339\u033a\7g\2\2"+
		"\u033a\u00d0\3\2\2\2\u033b\u033c\7c\2\2\u033c\u033d\7n\2\2\u033d\u033e"+
		"\7v\2\2\u033e\u033f\7g\2\2\u033f\u0340\7t\2\2\u0340\u00d2\3\2\2\2\u0341"+
		"\u0342\7t\2\2\u0342\u0343\7g\2\2\u0343\u0344\7p\2\2\u0344\u0345\7c\2\2"+
		"\u0345\u0346\7o\2\2\u0346\u0347\7g\2\2\u0347\u00d4\3\2\2\2\u0348\u0349"+
		"\7v\2\2\u0349\u034a\7q\2\2\u034a\u00d6\3\2\2\2\u034b\u034c\7c\2\2\u034c"+
		"\u034d\7f\2\2\u034d\u034e\7f\2\2\u034e\u00d8\3\2\2\2\u034f\u0350\7e\2"+
		"\2\u0350\u0351\7q\2\2\u0351\u0352\7n\2\2\u0352\u0353\7w\2\2\u0353\u0354"+
		"\7o\2\2\u0354\u0355\7p\2\2\u0355\u00da\3\2\2\2\u0356\u0357\7o\2\2\u0357"+
		"\u0358\7g\2\2\u0358\u0359\7v\2\2\u0359\u035a\7c\2\2\u035a\u035b\7f\2\2"+
		"\u035b\u035c\7c\2\2\u035c\u035d\7v\2\2\u035d\u035e\7c\2\2\u035e\u00dc"+
		"\3\2\2\2\u035f\u0360\7p\2\2\u0360\u0361\7q\2\2\u0361\u00de\3\2\2\2\u0362"+
		"\u0364\7$\2\2\u0363\u0365\n\2\2\2\u0364\u0363\3\2\2\2\u0365\u0366\3\2"+
		"\2\2\u0366\u0364\3\2\2\2\u0366\u0367\3\2\2\2\u0367\u0368\3\2\2\2\u0368"+
		"\u0369\7$\2\2\u0369\u00e0\3\2\2\2\u036a\u036b\5\u00e5s\2\u036b\u00e2\3"+
		"\2\2\2\u036c\u036d\5\u00e5s\2\u036d\u036f\7\60\2\2\u036e\u0370\5\u00e5"+
		"s\2\u036f\u036e\3\2\2\2\u036f\u0370\3\2\2\2\u0370\u0372\3\2\2\2\u0371"+
		"\u0373\5\u0103\u0082\2\u0372\u0371\3\2\2\2\u0372\u0373\3\2\2\2\u0373\u037d"+
		"\3\2\2\2\u0374\u0375\7\60\2\2\u0375\u0377\5\u00e5s\2\u0376\u0378\5\u0103"+
		"\u0082\2\u0377\u0376\3\2\2\2\u0377\u0378\3\2\2\2\u0378\u037d\3\2\2\2\u0379"+
		"\u037a\5\u00e5s\2\u037a\u037b\5\u0103\u0082\2\u037b\u037d\3\2\2\2\u037c"+
		"\u036c\3\2\2\2\u037c\u0374\3\2\2\2\u037c\u0379\3\2\2\2\u037d\u00e4\3\2"+
		"\2\2\u037e\u038a\5\u0101\u0081\2\u037f\u0384\5\u0101\u0081\2\u0380\u0383"+
		"\7a\2\2\u0381\u0383\5\u0101\u0081\2\u0382\u0380\3\2\2\2\u0382\u0381\3"+
		"\2\2\2\u0383\u0386\3\2\2\2\u0384\u0382\3\2\2\2\u0384\u0385\3\2\2\2\u0385"+
		"\u0387\3\2\2\2\u0386\u0384\3\2\2\2\u0387\u0388\5\u0101\u0081\2\u0388\u038a"+
		"\3\2\2\2\u0389\u037e\3\2\2\2\u0389\u037f\3\2\2\2\u038a\u00e6\3\2\2\2\u038b"+
		"\u038c\7v\2\2\u038c\u038d\7t\2\2\u038d\u038e\7w\2\2\u038e\u0395\7g\2\2"+
		"\u038f\u0390\7h\2\2\u0390\u0391\7c\2\2\u0391\u0392\7n\2\2\u0392\u0393"+
		"\7u\2\2\u0393\u0395\7g\2\2\u0394\u038b\3\2\2\2\u0394\u038f\3\2\2\2\u0395"+
		"\u00e8\3\2\2\2\u0396\u0397\7p\2\2\u0397\u0398\7w\2\2\u0398\u0399\7n\2"+
		"\2\u0399\u039a\7n\2\2\u039a\u00ea\3\2\2\2\u039b\u03a0\7)\2\2\u039c\u039f"+
		"\5\u0109\u0085\2\u039d\u039f\5\u010b\u0086\2\u039e\u039c\3\2\2\2\u039e"+
		"\u039d\3\2\2\2\u039f\u03a2\3\2\2\2\u03a0\u039e\3\2\2\2\u03a0\u03a1\3\2"+
		"\2\2\u03a1\u03a3\3\2\2\2\u03a2\u03a0\3\2\2\2\u03a3\u03a4\7)\2\2\u03a4"+
		"\u00ec\3\2\2\2\u03a5\u03a9\7b\2\2\u03a6\u03a8\13\2\2\2\u03a7\u03a6\3\2"+
		"\2\2\u03a8\u03ab\3\2\2\2\u03a9\u03aa\3\2\2\2\u03a9\u03a7\3\2\2\2\u03aa"+
		"\u03ac\3\2\2\2\u03ab\u03a9\3\2\2\2\u03ac\u03ad\7b\2\2\u03ad\u00ee\3\2"+
		"\2\2\u03ae\u03af\7w\2\2\u03af\u03b0\7)\2\2\u03b0\u03b1\3\2\2\2\u03b1\u03b2"+
		"\5\u010d\u0087\2\u03b2\u03b3\5\u010d\u0087\2\u03b3\u03b4\5\u010d\u0087"+
		"\2\u03b4\u03b5\5\u010d\u0087\2\u03b5\u03b6\5\u010d\u0087\2\u03b6\u03b7"+
		"\5\u010d\u0087\2\u03b7\u03b8\5\u010d\u0087\2\u03b8\u03b9\5\u010d\u0087"+
		"\2\u03b9\u03ba\7/\2\2\u03ba\u03bb\5\u010d\u0087\2\u03bb\u03bc\5\u010d"+
		"\u0087\2\u03bc\u03bd\5\u010d\u0087\2\u03bd\u03be\5\u010d\u0087\2\u03be"+
		"\u03bf\7/\2\2\u03bf\u03c0\5\u010d\u0087\2\u03c0\u03c1\5\u010d\u0087\2"+
		"\u03c1\u03c2\5\u010d\u0087\2\u03c2\u03c3\5\u010d\u0087\2\u03c3\u03c4\7"+
		"/\2\2\u03c4\u03c5\5\u010d\u0087\2\u03c5\u03c6\5\u010d\u0087\2\u03c6\u03c7"+
		"\5\u010d\u0087\2\u03c7\u03c8\5\u010d\u0087\2\u03c8\u03c9\7/\2\2\u03c9"+
		"\u03ca\5\u010d\u0087\2\u03ca\u03cb\5\u010d\u0087\2\u03cb\u03cc\5\u010d"+
		"\u0087\2\u03cc\u03cd\5\u010d\u0087\2\u03cd\u03ce\5\u010d\u0087\2\u03ce"+
		"\u03cf\5\u010d\u0087\2\u03cf\u03d0\5\u010d\u0087\2\u03d0\u03d1\5\u010d"+
		"\u0087\2\u03d1\u03d2\5\u010d\u0087\2\u03d2\u03d3\5\u010d\u0087\2\u03d3"+
		"\u03d4\5\u010d\u0087\2\u03d4\u03d5\5\u010d\u0087\2\u03d5\u03d6\7)\2\2"+
		"\u03d6\u00f0\3\2\2\2\u03d7\u03d8\7f\2\2\u03d8\u03d9\7)\2\2\u03d9\u03da"+
		"\3\2\2\2\u03da\u03db\5\u00f3z\2\u03db\u03dc\7)\2\2\u03dc\u03ec\3\2\2\2"+
		"\u03dd\u03de\7f\2\2\u03de\u03df\7)\2\2\u03df\u03e0\3\2\2\2\u03e0\u03e1"+
		"\5\u00f5{\2\u03e1\u03e2\7)\2\2\u03e2\u03ec\3\2\2\2\u03e3\u03e4\7f\2\2"+
		"\u03e4\u03e5\7)\2\2\u03e5\u03e6\3\2\2\2\u03e6\u03e7\5\u00f3z\2\u03e7\u03e8"+
		"\7\"\2\2\u03e8\u03e9\5\u00f5{\2\u03e9\u03ea\7)\2\2\u03ea\u03ec\3\2\2\2"+
		"\u03eb\u03d7\3\2\2\2\u03eb\u03dd\3\2\2\2\u03eb\u03e3\3\2\2\2\u03ec\u00f2"+
		"\3\2\2\2\u03ed\u03ef\5\u0101\u0081\2\u03ee\u03ed\3\2\2\2\u03ee\u03ef\3"+
		"\2\2\2\u03ef\u03f1\3\2\2\2\u03f0\u03f2\5\u0101\u0081\2\u03f1\u03f0\3\2"+
		"\2\2\u03f1\u03f2\3\2\2\2\u03f2\u03f3\3\2\2\2\u03f3\u03f4\5\u0101\u0081"+
		"\2\u03f4\u03f5\5\u0101\u0081\2\u03f5\u03f7\7/\2\2\u03f6\u03f8\t\3\2\2"+
		"\u03f7\u03f6\3\2\2\2\u03f7\u03f8\3\2\2\2\u03f8\u03f9\3\2\2\2\u03f9\u03fa"+
		"\5\u0101\u0081\2\u03fa\u03fc\7/\2\2\u03fb\u03fd\t\4\2\2\u03fc\u03fb\3"+
		"\2\2\2\u03fc\u03fd\3\2\2\2\u03fd\u03fe\3\2\2\2\u03fe\u03ff\5\u0101\u0081"+
		"\2\u03ff\u00f4\3\2\2\2\u0400\u0402\5\u0101\u0081\2\u0401\u0400\3\2\2\2"+
		"\u0401\u0402\3\2\2\2\u0402\u0403\3\2\2\2\u0403\u0404\5\u0101\u0081\2\u0404"+
		"\u0406\7<\2\2\u0405\u0407\t\5\2\2\u0406\u0405\3\2\2\2\u0406\u0407\3\2"+
		"\2\2\u0407\u0408\3\2\2\2\u0408\u0418\5\u0101\u0081\2\u0409\u040b\7<\2"+
		"\2\u040a\u040c\t\5\2\2\u040b\u040a\3\2\2\2\u040b\u040c\3\2\2\2\u040c\u040d"+
		"\3\2\2\2\u040d\u0416\5\u0101\u0081\2\u040e\u040f\7\60\2\2\u040f\u0411"+
		"\5\u0101\u0081\2\u0410\u0412\5\u0101\u0081\2\u0411\u0410\3\2\2\2\u0411"+
		"\u0412\3\2\2\2\u0412\u0414\3\2\2\2\u0413\u0415\5\u0101\u0081\2\u0414\u0413"+
		"\3\2\2\2\u0414\u0415\3\2\2\2\u0415\u0417\3\2\2\2\u0416\u040e\3\2\2\2\u0416"+
		"\u0417\3\2\2\2\u0417\u0419\3\2\2\2\u0418\u0409\3\2\2\2\u0418\u0419\3\2"+
		"\2\2\u0419\u00f6\3\2\2\2\u041a\u041b\7k\2\2\u041b\u041c\7)\2\2\u041c\u041d"+
		"\3\2\2\2\u041d\u0422\5\u00f9}\2\u041e\u041f\7.\2\2\u041f\u0421\5\u00f9"+
		"}\2\u0420\u041e\3\2\2\2\u0421\u0424\3\2\2\2\u0422\u0420\3\2\2\2\u0422"+
		"\u0423\3\2\2\2\u0423\u0425\3\2\2\2\u0424\u0422\3\2\2\2\u0425\u0426\7)"+
		"\2\2\u0426\u00f8\3\2\2\2\u0427\u0429\7/\2\2\u0428\u0427\3\2\2\2\u0428"+
		"\u0429\3\2\2\2\u0429\u042a\3\2\2\2\u042a\u042b\5\u00e5s\2\u042b\u042c"+
		"\5\u00fb~\2\u042c\u00fa\3\2\2\2\u042d\u0435\t\6\2\2\u042e\u042f\7o\2\2"+
		"\u042f\u0430\7q\2\2\u0430\u0435\7p\2\2\u0431\u0435\t\7\2\2\u0432\u0433"+
		"\7o\2\2\u0433\u0435\7u\2\2\u0434\u042d\3\2\2\2\u0434\u042e\3\2\2\2\u0434"+
		"\u0431\3\2\2\2\u0434\u0432\3\2\2\2\u0435\u00fc\3\2\2\2\u0436\u0437\7c"+
		"\2\2\u0437\u0438\7n\2\2\u0438\u043d\7n\2\2\u0439\u043a\7c\2\2\u043a\u043b"+
		"\7p\2\2\u043b\u043d\7{\2\2\u043c\u0436\3\2\2\2\u043c\u0439\3\2\2\2\u043d"+
		"\u00fe\3\2\2\2\u043e\u043f\7p\2\2\u043f\u0440\7q\2\2\u0440\u0441\7v\2"+
		"\2\u0441\u0100\3\2\2\2\u0442\u0443\t\b\2\2\u0443\u0102\3\2\2\2\u0444\u0445"+
		"\t\t\2\2\u0445\u0446\5\u0105\u0083\2\u0446\u0104\3\2\2\2\u0447\u0449\5"+
		"\u0107\u0084\2\u0448\u0447\3\2\2\2\u0448\u0449\3\2\2\2\u0449\u044a\3\2"+
		"\2\2\u044a\u044b\5\u00e5s\2\u044b\u0106\3\2\2\2\u044c\u044d\t\n\2\2\u044d"+
		"\u0108\3\2\2\2\u044e\u044f\n\13\2\2\u044f\u010a\3\2\2\2\u0450\u0451\7"+
		")\2\2\u0451\u0452\7)\2\2\u0452\u010c\3\2\2\2\u0453\u0454\t\f\2\2\u0454"+
		"\u010e\3\2\2\2\u0455\u0459\t\r\2\2\u0456\u0458\t\16\2\2\u0457\u0456\3"+
		"\2\2\2\u0458\u045b\3\2\2\2\u0459\u0457\3\2\2\2\u0459\u045a\3\2\2\2\u045a"+
		"\u0110\3\2\2\2\u045b\u0459\3\2\2\2\u045c\u0460\7%\2\2\u045d\u045f\n\17"+
		"\2\2\u045e\u045d\3\2\2\2\u045f\u0462\3\2\2\2\u0460\u045e\3\2\2\2\u0460"+
		"\u0461\3\2\2\2\u0461\u0463\3\2\2\2\u0462\u0460\3\2\2\2\u0463\u0464\b\u0089"+
		"\2\2\u0464\u0112\3\2\2\2\u0465\u0467\t\20\2\2\u0466\u0465\3\2\2\2\u0467"+
		"\u0468\3\2\2\2\u0468\u0466\3\2\2\2\u0468\u0469\3\2\2\2\u0469\u046a\3\2"+
		"\2\2\u046a\u046b\b\u008a\2\2\u046b\u0114\3\2\2\2#\2\u0366\u036f\u0372"+
		"\u0377\u037c\u0382\u0384\u0389\u0394\u039e\u03a0\u03a9\u03eb\u03ee\u03f1"+
		"\u03f7\u03fc\u0401\u0406\u040b\u0411\u0414\u0416\u0418\u0422\u0428\u0434"+
		"\u043c\u0448\u0459\u0460\u0468\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}