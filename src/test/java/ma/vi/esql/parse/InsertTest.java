/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parse;

import ma.vi.esql.builder.InsertBuilder;
import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.exec.Result;
import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.Parser;
import ma.vi.esql.parser.modify.Insert;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

import static ma.vi.esql.parser.Parser.Rules.INSERT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class InsertTest extends DataTest {
  @TestFactory
  Stream<DynamicTest> simpleInsert() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   Parser p = new Parser(db.structure());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("delete s from s:S");
                     Insert insert = p.parse(
                         "insert into S(_id, a, b, e, h, i) values "
                            + "(newid(), 1, 2, 3, 'Four', 'Five'),"
                            + "(newid(), 6, 7, 8, 'Nine', 'Ten')",
                         INSERT);
                     Context context = new Context(db.structure());
                     assertEquals(new InsertBuilder(context)
                                        .in("S", null)
                                        .insertColumns("_id", "a", "b", "e", "h", "i")
                                        .insertRow("newid()", "1", "2", "3", "'Four'", "'Five'")
                                        .insertRow("newid()", "6", "7", "8", "'Nine'", "'Ten'")
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
}