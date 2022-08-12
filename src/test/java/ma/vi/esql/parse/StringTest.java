/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parse;

import ma.vi.esql.Databases;
import ma.vi.esql.database.Structure;
import ma.vi.esql.grammar.EsqlParser;
import ma.vi.esql.syntax.Parser;
import ma.vi.esql.syntax.define.Metadata;
import ma.vi.esql.syntax.expression.literal.StringLiteral;
import org.junit.jupiter.api.Test;

import static ma.vi.esql.translation.Translatable.Target.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StringTest {
  @Test
  void stringWithControlCharacters() {
    Parser parser = new Parser(Databases.Postgresql().structure());
    StringLiteral expr = (StringLiteral)parser.parseExpression("'this is a \n\ttest\n...\tMX3\f\\c'");
    assertEquals("this is a \n\ttest\n...\tMX3\f\\c", expr.value);
    assertEquals("N'this is a \n\ttest\n...\tMX3\f\\c'", expr.translate(SQLSERVER));
    assertEquals("'this is a \n\ttest\n...\tMX3\f\\c'", expr.translate(HSQLDB));
    assertEquals("E'this is a \\n\\ttest\\n...\\tMX3\\f\\\\c'", expr.translate(POSTGRESQL));
  }

  @Test
  void multiLineStrings() {
    String input =
        """
            {
            a: `function sum(x, y) {
                  x *= x's;
                  y += x;
                  if (x > y) {
                    return y;
                  } else {
                    return x - y;
                  }
                }`,
            b: `function sum(x, y) {
                  x *= x''s;
              y += x;
                 if (x > y) {
                    return y;
                  } else {
                    return x - y;
                  }
                }`,
            c: `
               function sum(x, y) {
                 x *= x''s;
                 y += x;
                 if (x > y) {
                   return y;
                 } else {
                   return x - y;
                 }
               }
               `
            }""";
    EsqlParser p = Parser.parser(input);
    Structure structure = Databases.Postgresql().structure();
    Metadata metadata = (Metadata)Parser.parse(structure, p.metadata());
    String value = metadata.evaluateAttribute("a");
    assertEquals(
        """
            function sum(x, y) {
              x *= x's;
              y += x;
              if (x > y) {
                return y;
              } else {
                return x - y;
              }
            }""", value);

    value = metadata.evaluateAttribute("b");
    assertEquals(
        """
            function sum(x, y) {
              x *= x''s;
            y += x;
             if (x > y) {
                return y;
              } else {
                return x - y;
              }
            }""", value);

    value = metadata.evaluateAttribute("c");
    assertEquals(
        """
            function sum(x, y) {
              x *= x''s;
              y += x;
              if (x > y) {
                return y;
              } else {
                return x - y;
              }
            }""", value);
  }

  @Test
  void multiLineStringsWithEscapedBackquote() {
    String input =
        """
            {
            a: `function sum(x, y) {
                  x *= x\\`s;
                  y += x;
                  if (x > y) {
                    return y;
                  } else {
                    return x - y;
                  }
                }`,
            b: `function sum(x, y) {
                  x *= x\\`\\`s;
              y += x;
                 if (x > y) {
                    return y;
                  } else {
                    return x - y;
                  }
                }`
            }""";
    EsqlParser p = Parser.parser(input);
    Structure structure = Databases.Postgresql().structure();
    Metadata metadata = (Metadata)Parser.parse(structure, p.metadata());
    String value = metadata.evaluateAttribute("a");
    assertEquals(
        """
            function sum(x, y) {
              x *= x`s;
              y += x;
              if (x > y) {
                return y;
              } else {
                return x - y;
              }
            }""", value);

    value = metadata.evaluateAttribute("b");
    assertEquals(
        """
            function sum(x, y) {
              x *= x``s;
            y += x;
             if (x > y) {
                return y;
              } else {
                return x - y;
              }
            }""", value);

  }
}
