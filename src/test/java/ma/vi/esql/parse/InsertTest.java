/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parse;

import ma.vi.esql.DataTest;
import ma.vi.esql.builder.InsertBuilder;
import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.exec.Result;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Parser;
import ma.vi.esql.syntax.modify.Insert;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.*;
import java.util.stream.Stream;

import static ma.vi.esql.syntax.Parser.Rules.INSERT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class InsertTest extends DataTest {
  @TestFactory
  Stream<DynamicTest> simpleInsert() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   Parser p = new Parser(db.structure());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("delete t from t:a.b.T");
                     con.exec("delete s from s:S");
                     Insert insert = p.parse(
                         "insert into S(_id, a, b, e, h, i) values "
                            + "(newid(), 1, 2, true, text['Four', 'Quatre'], 'Five'),"
                            + "(newid(), 6, 7, false, text['Nine', 'Neuf'], 'Ten')",
                         INSERT);
                     Context context = new Context(db.structure());
                     assertEquals(new InsertBuilder(context)
                                        .in("S", null)
                                        .insertColumns("_id", "a", "b", "e", "h", "i")
                                        .insertRow("newid()", "1", "2", "true",  "text['Four', 'Quatre']", "'Five'")
                                        .insertRow("newid()", "6", "7", "false", "text['Nine', 'Neuf']",   "'Ten'")
                                        .build(),
                                  insert);
                     for (int i = 0; i < 10; i++) {
                       con.exec(insert);
                     }
                     Result rs = con.exec("select count(*) from S");
                     rs.next();
                     assertEquals(((Number)rs.get(1).value).intValue(), 20);
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> insertAndReadBool() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("delete t from t:a.b.T");
                     con.exec("delete s from s:S");
                     UUID id1 = UUID.randomUUID(),
                          id2 = UUID.randomUUID(),
                          id3 = UUID.randomUUID();
                     con.exec("insert into S(_id, e) values "
                            + "(u'" + id1 + "', true),"
                            + "(u'" + id2 + "', false),"
                            + "(u'" + id3 + "', null)");
                     Result rs = con.exec("select e from S where _id=u'" + id1 + "'");
                     assertTrue(rs.next());
                     assertEquals(rs.get("e").value, true);

                     rs = con.exec("select e from S where _id=u'" + id2 + "'");
                     assertTrue(rs.next());
                     assertEquals(rs.get("e").value, false);

                     rs = con.exec("select _id, e from S where e");
                     assertTrue(rs.next());
                     assertEquals(rs.get("_id").value, id1);

                     rs = con.exec("select _id, coalesce(e, false) from S where coalesce(e, false)=true");
                     assertTrue(rs.next());
                     assertEquals(rs.get("_id").value, id1);

                     rs = con.exec("select not e from S where _id=u'" + id2 + "' and not e");
                     assertTrue(rs.next());
                     assertEquals(rs.get(1).value, true);
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> insertAndReadArray() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   Parser p = new Parser(db.structure());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("delete t from t:a.b.T");
                     con.exec("delete s from s:S");
                     Insert insert = p.parse(
                         "insert into S(_id, a, b, e, h, j) values "
                             + "(newid(), 1, 2, true, text['Four', 'Quatre'], int[1, 2, 3]),"
                             + "(newid(), 6, 7, false, text['Nine', 'Neuf', 'X'], int[5, 6, 7, 8])",
                         INSERT);
                     con.exec(insert);

                     Result rs = con.exec("select _id, a, b, e, h, j from S");
                     Set<List<String>> stringArray = new HashSet<>();
                     Set<List<Integer>> intArray = new HashSet<>();
                     rs.next(); stringArray.add(Arrays.asList(rs.value("h"))); intArray.add(Arrays.asList(rs.value("j")));
                     rs.next(); stringArray.add(Arrays.asList(rs.value("h"))); intArray.add(Arrays.asList(rs.value("j")));

                     assertEquals(Set.of(Arrays.asList("Four", "Quatre"),
                                         Arrays.asList("Nine", "Neuf", "X")), stringArray);

                     assertEquals(Set.of(Arrays.asList(1, 2, 3),
                                         Arrays.asList(5, 6, 7, 8)), intArray);
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> insertFromSelect() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   Parser p = new Parser(db.structure());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("delete t from t:a.b.T");
                     con.exec("delete s from s:S");
                     con.exec(
                         "insert into S(_id, a, b, e, h, j) values "
                             + "(newid(), 1, 2, true, text['Four', 'Quatre'], int[1, 2, 3]),"
                             + "(newid(), 6, 7, false, text['Nine', 'Neuf', 'X'], int[5, 6, 7, 8])");

                     con.exec("insert into S(_id, a, b, e, h, j) "
                            + "select newid(), 7, b, e, h, j from S");

                     Result rs = con.exec("select _id, a, b, e, h, j from S order by a");

                     rs.next(); assertEquals(rs.get("a").value, 1);
                     rs.next(); assertEquals(rs.get("a").value, 6);
                     rs.next(); assertEquals(rs.get("a").value, 7);
                     rs.next(); assertEquals(rs.get("a").value, 7);
                   }
                 }));
  }
}
