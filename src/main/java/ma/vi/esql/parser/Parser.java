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
      return (T)parse(structure, (ParserRuleContext)Dissector.method(p.getClass(), as).get().invoke(p));
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
                                 ParserRuleContext startFrom) {
    Context context = new Context(structure);
    Analyser analyser = new Analyser(context);
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
    return (Program)parse(structure, parser(esql).program());
  }

  /**
   * Parses a database expression passed in string form.
   */
  public static Expression<?> parseExpression(Structure structure,
                                              String expression) {
    return (Expression<?>)parse(structure, parser(expression).expr());
  }

//  /**
//   * Interprets the expression as an ESQL expression if it is surrounded by parenthesis.
//   * otherwise interprets it as a literal. This is useful when parsing query parameters where,
//   * for instance, an update parameter could be a literal (x=5) as well as an expression (x=y+5).
//   * In the first case, the expression sent for the parameter will be the integer 5; in the second
//   * case the string "`y+5" (without the double-quote) will be sent and processed as the correct
//   * expression.
//   */
//  public static Expression<?> parseLiteralOrExpression(Structure structure,
//                                                       Object expression) {
//    if (expression instanceof String
//     && ((String)expression).startsWith("(")
//     && ((String)expression).endsWith(")")) {
//      /*
//       * Parse as expression
//       */
//      return parseExpression(structure, (String)expression);
//
//    } else {
//      return Literal.makeLiteral(new Context(structure), expression);
//    }
//  }
//
//    /**
//     * Interprets the expression as an ESQL expression if it starts with a single backquote (`),
//     * otherwise interprets it as a literal. This is useful when parsing query parameters where,
//     * for instance, an update parameter could be a literal (x=5) as well as an expression (x=y+5).
//     * In the first case, the expression sent for the parameter will be the integer 5; in the second
//     * case the string "`y+5" (without the double-quote) will be sent and processed as the correct
//     * expression.
//     */
//    default Expression<?> parseLiteralOrExpression(Object expression) {
//        Context context = new Context(this);
//        if (expression instanceof String) {
//            String exp = (String)expression;
//            if (exp.length() > 0 && exp.charAt(0) == '`') {
//                /*
//                 * Back-quoted: process as expression
//                 */
//                return (Expression<?>) parse(this, fromString(exp.substring(1)), false);
//            } else {
//                /*
//                 * No starting backquote: literal string
//                 */
//                return Literal.makeLiteral(context, expression);
//            }
//        } else {
//            /*
//             * Other type than string: literal
//             */
//            return Literal.makeLiteral(context, expression);
//        }
//    }

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