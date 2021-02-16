/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parse;

import ma.vi.esql.Databases;
import ma.vi.esql.TestDatabase;
import ma.vi.esql.parser.Esql;
import ma.vi.esql.parser.Parser;
import ma.vi.esql.parser.SyntaxException;
import ma.vi.esql.parser.query.Select;
import org.junit.jupiter.api.Test;

import static ma.vi.esql.parser.Parser.Rules.SELECT;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ParseTest {
  @Test
  void invalidBaseType() {
    TestDatabase db = Databases.TestDatabase();
    Parser parser = new Parser(db.structure());
    assertThrows(SyntaxException.class, () ->
                 parser.parse("create table A(\n" +
                              "  _id uuid, \n" +
                              "  name str, \n" +
                              "  age int\n" +
                              ")"));
  }

  @Test
  void invalidComponentTypeOfArray() {
    TestDatabase db = Databases.TestDatabase();
    Parser parser = new Parser(db.structure());
    assertThrows(SyntaxException.class, () ->
                 parser.parse("create table A(\n" +
                              "  _id uuid[][], \n" +
                              "  name str[], \n" +
                              "  age int\n" +
                              ")"));
  }

  @Test
  void syntaxErrorInSelect() {
    TestDatabase db = Databases.TestDatabase();
    Parser parser = new Parser(db.structure());
    assertThrows(SyntaxException.class, () -> parser.parse("select * S"));
    assertThrows(SyntaxException.class, () -> parser.parse("select * fro S"));
    assertThrows(SyntaxException.class, () -> parser.parse("select from S"));
  }
}
