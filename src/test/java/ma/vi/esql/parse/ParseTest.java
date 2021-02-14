/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parse;

import ma.vi.esql.Databases;
import ma.vi.esql.TestDatabase;
import ma.vi.esql.parser.Parser;
import ma.vi.esql.parser.SyntaxException;
import org.junit.jupiter.api.Test;

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
}
