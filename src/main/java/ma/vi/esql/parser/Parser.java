/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser;

import ma.vi.base.reflect.Dissector;
import ma.vi.esql.database.Structure;
import ma.vi.esql.grammar.EsqlLexer;
import ma.vi.esql.grammar.EsqlParser;
import ma.vi.esql.parser.expression.Expression;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import static org.antlr.v4.runtime.CharStreams.fromString;

/**
 * Knows how to parse ESQL statements and expressions
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
      return (T)parse(structure, (ParserRuleContext)Dissector.method(p.getClass(), as).get().invoke(p), esql);
    } catch (Exception e) {
      throw e instanceof RuntimeException ? (RuntimeException)e : new RuntimeException(e);
    }
  }

  public Expression<?> parseExpression(String expression) {
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
    return esqlParser;
  }

  public static Esql<?, ?> parse(Structure structure,
                                 ParserRuleContext startFrom,
                                 String input) {
    Context context = new Context(structure);
    Analyser analyser = new Analyser(context, input);
    ParseTreeWalker walker = new ParseTreeWalker();
    walker.walk(analyser, startFrom);

    /*
     * Compute the type of all elements in the program (depth-first post-order).
     * During the typing operation, the esql may be changed (expanded or contracted,
     * for example).
     */
//    esql.forEach(e -> {
//      e.type();
//      return true;
//    });
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
    return (Program)parse(structure, parser(esql).program(), esql);
  }

  /**
   * Parses a database expression passed in string form.
   */
  public static Expression<?> parseExpression(Structure structure,
                                              String expression) {
    return (Expression<?>)parse(structure, parser(expression).expr(), expression);
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
