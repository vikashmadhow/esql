/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax;

import ma.vi.base.reflect.Dissector;
import ma.vi.esql.database.Structure;
import ma.vi.esql.grammar.EsqlLexer;
import ma.vi.esql.grammar.EsqlParser;
import ma.vi.esql.syntax.expression.Expression;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import static org.antlr.v4.runtime.CharStreams.fromString;

/**
 * Entry-point to the ESQL parser.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Parser {
  public Parser(Structure structure) {
    this.structure = structure;
  }

  // Esql parsing
  ////////////////////////////////

  public Program parse(String esql) {
    return parse(structure, esql);
  }

  public <T extends Esql<?, ?>> T parse(String esql, String as) {
    try {
      EsqlParser p = parser(esql);
      return (T)parse(structure, (ParserRuleContext)Dissector.method(p.getClass(), as).get().invoke(p));
    } catch (Exception e) {
      throw e instanceof RuntimeException ? (RuntimeException)e : new RuntimeException(e);
    }
  }

  public Expression<?, String> parseExpression(String expression) {
    return parseExpression(structure, expression);
  }

  public static EsqlParser parser(String esql) {
    return parser(fromString(esql));
  }

  public static EsqlParser parser(CharStream esql) {
    EsqlLexer lexer = new EsqlLexer(esql);
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    EsqlParser esqlParser = new EsqlParser(tokens);
    esqlParser.removeErrorListeners();
    esqlParser.addErrorListener(new ErrorListener());
    esqlParser.setErrorHandler(new ParserErrorStrategy());
    return esqlParser;
  }

  public static Esql<?, ?> parse(Structure structure, ParserRuleContext startFrom) {
    Context context = new Context(structure);
    SyntaxAnalyser analyser = new SyntaxAnalyser(context);
    ParseTreeWalker walker = new ParseTreeWalker();
    walker.walk(analyser, startFrom);

    return analyser.lastRecognised;
  }

  /**
   * Parses the Esql definition (queries, updates &amp; other definitions)
   * which can then be executed on the database.
   *
   * @param esql The source program in ESQL.
   * @return The parsed program
   */
  public static Program parse(Structure structure, String esql) {
    return (Program)parse(structure, parser(esql).program());
  }

  /**
   * Parses a database expression passed in string form.
   */
  public static Expression<?, String> parseExpression(Structure structure, String expression) {
    return (Expression<?, String>)parse(structure, parser(expression).expr());
  }

  public interface Rules {
    String PROGRAM = "program";
    String STATEMENT = "statement";

    String SELECT = "select";
    String SELECT_EXPRESSION = "selectExpression";

    String INSERT = "insert";
    String UPDATE = "update";
    String DELETE = "delete";

    String CREATE_TABLE = "createTable";
    String ALTER_TABLE = "alterTable";
    String DROP_TABLE = "dropTable";

    String EXPRESSION = "expr";
    String SIMPLE_EXPRESSION = "simpleExpr";
    String LITERAL = "literal";

    String METADATA = "metadata";
  }

  public final Structure structure;
}
