/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parse;

import ma.vi.esql.Databases;
import ma.vi.esql.TestDatabase;
import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.exec.Result;
import ma.vi.esql.parser.Parser;
import ma.vi.esql.parser.SyntaxException;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class ErrorDetectionTest {
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
    assertThrows(SyntaxException.class, () -> parser.parse("from S"));
  }

  @Test
  void syntaxErrorInCompositeSelect() {
    TestDatabase db = Databases.TestDatabase();
    Parser parser = new Parser(db.structure());
    assertThrows(SyntaxException.class, () -> parser.parse("union all select * from S"));
    assertThrows(SyntaxException.class, () -> parser.parse("except"));
  }

  @Test
  void syntaxErrorInInsert() {
    TestDatabase db = Databases.TestDatabase();
    Parser parser = new Parser(db.structure());
    assertThrows(SyntaxException.class, () -> parser.parse("insert into(a, b, c) values(1,2,3)"));
    assertThrows(SyntaxException.class, () -> parser.parse("insert X(a, b, c) values(1,2,3)"));
    assertThrows(SyntaxException.class, () -> parser.parse("insert X(a b, c) values(1,2,3)"));
    assertThrows(SyntaxException.class, () -> parser.parse("insert X(a b, c) values(1,2 3)"));
    assertThrows(SyntaxException.class, () -> parser.parse("insert X(a b, c) values 1,2 3)"));
    assertThrows(SyntaxException.class, () -> parser.parse("insert X(a b, c) values 1,2 3"));
    assertThrows(SyntaxException.class, () -> parser.parse("insert X(a b, c values(1,2 3)"));
    assertThrows(SyntaxException.class, () -> parser.parse("insert Xa b, c) values(1,2 3)"));
    assertThrows(SyntaxException.class, () -> parser.parse("insert X a b, c values(1,2 3)"));
    assertThrows(SyntaxException.class, () -> parser.parse("insert X(a, b, c) select * fro S"));
    assertThrows(SyntaxException.class, () -> parser.parse("insert X a, b, c) select * fro S"));
    assertThrows(SyntaxException.class, () -> parser.parse("insert X(a, b, c select * fro S"));
    assertThrows(SyntaxException.class, () -> parser.parse("insert X a, b, c select * fro S"));
  }

  @Test
  void syntaxErrorInUpdate() {
    TestDatabase db = Databases.TestDatabase();
    Parser parser = new Parser(db.structure());
    assertThrows(SyntaxException.class, () -> parser.parse("update X set a=b"));
    assertThrows(SyntaxException.class, () -> parser.parse("update from X set a=b"));
    assertThrows(SyntaxException.class, () -> parser.parse("update X from set a=b"));
    assertThrows(SyntaxException.class, () -> parser.parse("update X from X where b>5"));
  }

  @Test
  void syntaxErrorInDelete() {
    TestDatabase db = Databases.TestDatabase();
    Parser parser = new Parser(db.structure());
    assertThrows(SyntaxException.class, () -> parser.parse("delete X set a=b"));
    assertThrows(SyntaxException.class, () -> parser.parse("delete from X set a=b"));
    assertThrows(SyntaxException.class, () -> parser.parse("delete X from set a=b"));
  }
}
