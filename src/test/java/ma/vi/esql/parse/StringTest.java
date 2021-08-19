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

import static ma.vi.esql.syntax.Translatable.Target.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StringTest {
  @Test
  void stringWithControlCharacters() {
    Parser parser = new Parser(Databases.HSqlDb().structure());
    StringLiteral expr = (StringLiteral)parser.parseExpression("'this is a \n\ttest\n...\tMX3\f\\c'");
    assertEquals("'this is a \n\ttest\n...\tMX3\f\\c'", expr.value);
    assertEquals("N'this is a \n\ttest\n...\tMX3\f\\c'", expr.translate(SQLSERVER));
    assertEquals("'this is a \n\ttest\n...\tMX3\f\\c'", expr.translate(HSQLDB));
    assertEquals("E'this is a \\n\\ttest\\n...\\tMX3\\f\\\\c'", expr.translate(POSTGRESQL));
  }

  @Test
  void multiLineStrings() {
    String input =
        "{\n" +
        "a: `function sum(x, y) {\n" +
        "      x *= x's;\n" +
        "      y += x;\n" +
        "      if (x > y) {\n" +
        "        return y;\n" +
        "      } else {\n" +
        "        return x - y;\n" +
        "      }\n" +
        "    }`,\n" +
        "b: `function sum(x, y) {\n" +
        "      x *= x''s;\n" +
        "  y += x;\n" +
        "     if (x > y) {\n" +
        "        return y;\n" +
        "      } else {\n" +
        "        return x - y;\n" +
        "      }\n" +
        "    }`,\n" +
        "c: `\n" +
        "   function sum(x, y) {\n" +
        "     x *= x''s;\n" +
        "     y += x;\n" +
        "     if (x > y) {\n" +
        "       return y;\n" +
        "     } else {\n" +
        "       return x - y;\n" +
        "     }\n" +
        "   }\n" +
        "   `\n" +
        "}";
    EsqlParser p = Parser.parser(input);
    Structure structure = Databases.HSqlDb().structure();
    Metadata metadata = (Metadata)Parser.parse(structure, p.metadata());
    String value = metadata.evaluateAttribute("a");
    assertEquals(
    "function sum(x, y) {\n" +
            "  x *= x''s;\n" +
            "  y += x;\n" +
            "  if (x > y) {\n" +
            "    return y;\n" +
            "  } else {\n" +
            "    return x - y;\n" +
            "  }\n" +
            "}", value);

    value = metadata.evaluateAttribute("b");
    assertEquals(
    "function sum(x, y) {\n" +
            "  x *= x''''s;\n" +
            "y += x;\n" +
            " if (x > y) {\n" +
            "    return y;\n" +
            "  } else {\n" +
            "    return x - y;\n" +
            "  }\n" +
            "}", value);

    value = metadata.evaluateAttribute("c");
    assertEquals(
        "function sum(x, y) {\n" +
            "  x *= x''''s;\n" +
            "  y += x;\n" +
            "  if (x > y) {\n" +
            "    return y;\n" +
            "  } else {\n" +
            "    return x - y;\n" +
            "  }\n" +
            "}", value);
  }
}
