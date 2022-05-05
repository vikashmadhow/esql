/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.exec;

import ma.vi.esql.DataTest;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.syntax.Parser;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.UUID;
import java.util.stream.Stream;

import static ma.vi.esql.syntax.expression.literal.DateLiteral.date;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class ExpressionExecTest extends DataTest {
  @TestFactory
  Stream<DynamicTest> literals() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   Parser p = new Parser(db.structure());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     assertEquals(1,     (Long)con.exec(p.parse("1")));
                     assertEquals(-1,    (Long)con.exec(p.parse("-1")));

                     assertEquals(1.5,  con.exec(p.parse("1.5")));
                     assertEquals(-1.5,  con.exec(p.parse("-1.5")));
                     assertEquals(1.5e10,  con.exec(p.parse("1.5e10")));
                     assertEquals(1.5e-10,  con.exec(p.parse("1.5e-10")));
                     assertEquals(-1.5e10,  con.exec(p.parse("-1.5e10")));
                     assertEquals(-1.5e-10,  con.exec(p.parse("-1.5e-10")));

                     assertEquals("abc", con.exec(p.parse("'abc'")));
                     assertEquals("""
                                  function sum(x, y) {
                                    x *= x''s;
                                    y += x;
                                    if (x > y) {
                                      return y;
                                    } else {
                                      return x - y;
                                    }
                                  }""",
                                  con.exec(p.parse("""
                                   `
                                   function sum(x, y) {
                                     x *= x's;
                                     y += x;
                                     if (x > y) {
                                       return y;
                                     } else {
                                       return x - y;
                                     }
                                   }
                                   `
                                   """)));

                     assertEquals(date(2022, 1, 1),
                                  con.exec(p.parse("d'2022-01-01'")));

                     assertEquals(date(2022, 1, 1, 9, 31, 2, 0),
                                  con.exec(p.parse("d'2022-01-01 09:31:02'")));

                     assertEquals(date(2022, 1, 1, 9, 31, 2, 5),
                                  con.exec(p.parse("d'2022-01-01 9:31:2.5'")));

                     assertNull(con.exec(p.parse("null")));

                     assertTrue((Boolean)con.exec(p.parse("true")));
                     assertFalse((Boolean)con.exec(p.parse("false")));

                     assertEquals(UUID.fromString("01234567-89AB-CDEF-0123-456789ABCDEF"),
                                  con.exec(p.parse("u'01234567-89AB-CDEF-0123-456789ABCDEF'")));
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> arithmetic() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   Parser p = new Parser(db.structure());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     assertEquals(2, (Long)con.exec(p.parse("1+1")));
                     assertEquals(0, (Long)con.exec(p.parse("1-1")));
                     assertEquals(0, (Long)con.exec(p.parse("-1+1")));
                     assertEquals(-3, (Long)con.exec(p.parse("-1-1-1")));
                     assertEquals(-1, (Long)con.exec(p.parse("1+-1-1")));

                     assertEquals(0, (Long)con.exec(p.parse("1*0")));
                     assertEquals(1, (Long)con.exec(p.parse("-1*-1")));
                     assertEquals(17, (Long)con.exec(p.parse("2 + 3 * 5")));
                     assertEquals(25, (Long)con.exec(p.parse("(2 + 3) * 5")));
                     assertEquals(11, (Long)con.exec(p.parse("2 * 3 + 5")));

                     assertEquals(2., con.exec(p.parse("10 / 5")));
                     assertEquals(5., con.exec(p.parse("100 / 10 / 2")));
                     assertEquals(5.1, con.exec(p.parse("10.2 / 2")));

                     assertEquals(2, (Long)con.exec(p.parse("7 % 5")));
                     assertThat(con.exec(p.parse("10.2 % 2")),
                                closeTo(0.2, 0.000001));

                     assertEquals(256., con.exec(p.parse("2 ^ 2 ^ 3")));
                     assertEquals(64., con.exec(p.parse("(2 ^ 2) ^ 3")));
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> strings() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   Parser p = new Parser(db.structure());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     assertEquals("11", con.exec(p.parse("1+'1'")));
                     assertEquals("1111", con.exec(p.parse("'1'+'1' + 1 + '1'")));
                     assertEquals("11", con.exec(p.parse("'1'+1")));
                     assertEquals("1-1", con.exec(p.parse("'1' + -1")));
                     
                     assertEquals("11", con.exec(p.parse("1+`1`")));
                     assertEquals("11", con.exec(p.parse("`1`+`1`")));
                     assertEquals("11", con.exec(p.parse("`1`+1")));
                     assertEquals("1-1", con.exec(p.parse("`1` + -1")));

                     assertEquals("11111", con.exec(p.parse("'1'*5")));
                     assertEquals("1212121212", con.exec(p.parse("'12'*5")));
                     assertEquals("312121212123", con.exec(p.parse("3 + '12' * 5 + 3")));
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> comparisons() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   Parser p = new Parser(db.structure());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     assertEquals(true, con.exec(p.parse("1 = 1")));
                     assertEquals(false, con.exec(p.parse("1 = 2")));

                     assertEquals(true, con.exec(p.parse("1 != 2")));

                     assertEquals(true, con.exec(p.parse("1=1 or 1=-1")));
                     assertEquals(false, con.exec(p.parse("1=1 and 1=-1")));

                     assertEquals(true, con.exec(p.parse("1=1 and 2=2")));

                     assertEquals(true, con.exec(p.parse("1>=1")));
                     assertEquals(false, con.exec(p.parse("1>1")));

                     assertEquals(true, con.exec(p.parse("1<=1")));
                     assertEquals(false, con.exec(p.parse("1<1")));

                     assertEquals(true, con.exec(p.parse("not (1<1)")));
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> conditionals() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   Parser p = new Parser(db.structure());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     assertEquals("Three",
                                  con.exec(p.parse("""
                                                   'One'   if 1=2 else
                                                   'Two'   if 2=3 else
                                                   'Three' if 3=3 else
                                                   'Four'
                                                   """)));
                     assertEquals("Four",
                                  con.exec(p.parse("""
                                                   'One'   if 1=2 else
                                                   'Two'   if 2=3 else
                                                   'Three' if 3=4 else
                                                   'Four'
                                                   """)));
                     assertEquals("One",
                                  con.exec(p.parse("""
                                                   'One'   if 1=1 else
                                                   'Two'   if 2=3 else
                                                   'Three' if 3=4 else
                                                   'Four'
                                                   """)));
                     assertEquals("Two",
                                  con.exec(p.parse("""
                                                   'One'   if 1=2 else
                                                   'Two'   if 2=2 else
                                                   'Three' if 3=4 else
                                                   'Four'
                                                   """)));
                   }
                 }));
  }
}
