/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.func;

import ma.vi.esql.DataTest;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.Result;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class StringFunctionsTest extends DataTest {
//  @TestFactory
//  Stream<DynamicTest> caseSensitiveEqual() {
//    return Stream.of(databases)
//                 .map(db -> dynamicTest(db.target().toString(), () -> {
//                   System.out.println(db.target());
//                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
//                     con.exec("delete t from t:a.b.T");
//                     con.exec("delete s from s:S");
//                     con.exec("insert into S(_id, i) values "
//                                + "(newid(), 'aBcDeF'),"
//                                + "(newid(), 'abcdef')");
//
//                     Result rs = con.exec("select i from S where i='aBcDeF'");
//                     rs.toNext();
//                     assertEquals("aBcDeF", rs.value(1));
//                     assertFalse(rs.toNext());
//
//                     rs = con.exec("select i from S where i='abcdef'");
//                     rs.toNext();
//                     assertEquals("abcdef", rs.value(1));
//                     assertFalse(rs.toNext());
//                   }
//                 }));
//  }

  @TestFactory
  Stream<DynamicTest> caseSensitiveNotEqual() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("delete t from t:a.b.T");
                     con.exec("delete s from s:S");
                     con.exec("insert into S(_id, i) values "
                                + "(newid(), 'aBcDeF'),"
                                + "(newid(), 'abcdef')");

                     Result rs = con.exec("select i from S where i != 'aBcDeF'");
                     rs.toNext();
                     assertEquals("abcdef", rs.value(1));
                     assertFalse(rs.toNext());

                     rs = con.exec("select i from S where i != 'abcdef'");
                     rs.toNext();
                     assertEquals("aBcDeF", rs.value(1));
                     assertFalse(rs.toNext());
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> concat() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("delete t from t:a.b.T");
                     con.exec("delete s from s:S");
                     con.exec("insert into S(_id, a, i) values "
                                + "(newid(), 2021, 'X'),"
                                + "(newid(), 2022, 'Y')");

                     con.exec("""
                              update s
                                from s:S
                                 set n=concat(s.a + 1, '-03-31')::date""");

                     Result rs = con.exec("select a, n from S order by a");
                     rs.toNext();
                     assertEquals((Integer)2021, rs.value("a"));
                     assertEquals(LocalDate.of(2022, 3, 31), rs.value("n"));
                     rs.toNext();
                     assertEquals((Integer)2022, rs.value("a"));
                     assertEquals(LocalDate.of(2023, 3, 31), rs.value("n"));
                     assertFalse(rs.toNext());
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> obfuscate() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     String o = "abcdefg1234@!$%12";
                     Result rs = con.exec("select obfuscate('" + o + "')");
                     rs.toNext();
                     String v = rs.value(1);
                     System.out.println(v);
                     assertFalse(rs.toNext());
                     rs = con.exec("select unobfuscate('" + v + "')");
                     rs.toNext();
                     v = rs.value(1);
                     assertEquals(o, v);
                     System.out.println(v);
                   }
                 }));
  }
}