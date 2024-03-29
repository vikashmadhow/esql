/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parse;

import ma.vi.esql.Databases;
import ma.vi.esql.TestDatabase;
import ma.vi.esql.syntax.Parser;
import ma.vi.esql.syntax.SyntaxException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ErrorDetectionTest {
  @Test
  void invalidBaseType() {
    TestDatabase db = Databases.TestDatabase();
    Parser parser = new Parser(db.structure());
    assertThrows(SyntaxException.class, () ->
                 parser.parse("""
                                  create table A(
                                    _id uuid,
                                    name str,
                                    age int
                                  )"""));
  }

  @Test
  void invalidComponentTypeOfArray() {
    TestDatabase db = Databases.TestDatabase();
    Parser parser = new Parser(db.structure());
    assertThrows(SyntaxException.class, () ->
                 parser.parse("""
                                  create table A(
                                    _id uuid[][],\s
                                    name str[],\s
                                    age int
                                  )"""));
  }

  @Test
  void syntaxErrorInSelect() {
    TestDatabase db = Databases.TestDatabase();
    Parser parser = new Parser(db.structure());
//    assertThrows(SyntaxException.class, () -> parser.parse("select * S"));
//    assertThrows(SyntaxException.class, () -> parser.parse("select * fro S"));
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
