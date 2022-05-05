/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.func;

import ma.vi.esql.DataTest;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.Result;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class StringFunctionsTest extends DataTest {
  @TestFactory
  Stream<DynamicTest> caseSensitiveEqual() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("delete t from t:a.b.T");
                     con.exec("delete s from s:S");
                     con.exec("insert into S(_id, i) values "
                                + "(newid(), 'aBcDeF'),"
                                + "(newid(), 'abcdef')");

                     Result rs = con.exec("select i from S where i='aBcDeF'");
                     rs.toNext();
                     assertEquals("aBcDeF", rs.value(1));
                     assertFalse(rs.toNext());

                     rs = con.exec("select i from S where i='abcdef'");
                     rs.toNext();
                     assertEquals("abcdef", rs.value(1));
                     assertFalse(rs.toNext());
                   }
                 }));
  }

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
}