/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.query;

import ma.vi.esql.DataTest;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.QueryParams;
import ma.vi.esql.exec.Result;
import ma.vi.esql.syntax.Parser;
import ma.vi.esql.syntax.modify.Insert;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.UUID;
import java.util.stream.Stream;

import static java.util.UUID.randomUUID;
import static ma.vi.esql.syntax.Parser.Rules.INSERT;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class WithTest extends DataTest {
  @TestFactory
  Stream<DynamicTest> simpleWithSelect() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("delete T from a.b.T");
                     con.exec("delete s from s:S");

                     UUID id1 = randomUUID(), id2 = randomUUID();
                     con.exec("insert into S(_id, a, b, e, h, j) values "
                                  + "(u'" + id1 + "', 1, 2, true, ['Four', 'Quatre']text, [1, 2, 3]int),"
                                  + "(u'" + id2 + "', 6, 7, false, ['Nine', 'Neuf', 'X']text, [5, 6, 7, 8]int)");

                     con.exec("insert into a.b.T(_id, a, b, s_id) values"
                                  + "(newid(), 1, 2, u'" + id1 + "'), "
                                  + "(newid(), 3, 4, u'" + id2 + "')");

                     Result rs = con.exec("""
                         with s(id, a, b, c) (
                          select _id, a, b, c
                            from S
                           order by a
                         )
                         select t.a, t.b, s.c, x:s.b
                           from t:a.b.T
                           join s on t.s_id=s.id order by t.a""");

//                     printResult(rs, 20);

                     rs.toNext(); assertEquals(1, (Integer)rs.value("a"));
                                assertEquals(2,  (Integer)rs.value("b"));
                                assertEquals(3,  (Integer)rs.value("c"));
                                assertEquals(2,  (Integer)rs.value("x"));
                     rs.toNext(); assertEquals(3, (Integer)rs.value("a"));
                                assertEquals(4,  (Integer)rs.value("b"));
                                assertEquals(13, (Integer)rs.value("c"));
                                assertEquals(7,  (Integer)rs.value("x"));
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> recursiveWithSelect() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("create table x.R(" +
                                  "_id uuid not null," +
                                  "parent_id uuid," +
                                  "name string," +
                                  "primary key(_id))");

                     con.exec("delete R from x.R");

                     UUID root = randomUUID();
                     UUID a = randomUUID();
                     UUID b = randomUUID();
                     UUID c = randomUUID();

                     Parser p = new Parser(db.structure());
                     Insert insert = p.parse("insert into x.R values(@id, @parent, @name)", INSERT);
                     con.exec(insert, new QueryParams().add("id", root).add("parent", null).add("name", "root"));

                     con.exec(insert, new QueryParams().add("id", a).add("parent", root).add("name", "a"));
                     con.exec(insert, new QueryParams().add("id", b).add("parent", root).add("name", "b"));
                     con.exec(insert, new QueryParams().add("id", c).add("parent", root).add("name", "c"));

                     con.exec(insert, new QueryParams().add("id", randomUUID()).add("parent", a).add("name", "a_child1"));
                     con.exec(insert, new QueryParams().add("id", randomUUID()).add("parent", a).add("name", "a_child2"));
                     con.exec(insert, new QueryParams().add("id", randomUUID()).add("parent", a).add("name", "a_child3"));

                     con.exec(insert, new QueryParams().add("id", randomUUID()).add("parent", b).add("name", "b_child1"));
                     con.exec(insert, new QueryParams().add("id", randomUUID()).add("parent", b).add("name", "b_child2"));

                     con.exec(insert, new QueryParams().add("id", randomUUID()).add("parent", c).add("name", "c_child1"));
                     con.exec(insert, new QueryParams().add("id", randomUUID()).add("parent", c).add("name", "c_child2"));
                     con.exec(insert, new QueryParams().add("id", randomUUID()).add("parent", c).add("name", "c_child3"));
                     con.exec(insert, new QueryParams().add("id", randomUUID()).add("parent", c).add("name", "c_child4"));

                     Result rs = con.exec("""             
                       with recursive r(id, parent, name) (
                         select _id, parent_id, name from x.R where parent_id is null
                         union all
                         select xr._id, xr.parent_id, r.name || '/' || xr.name from xr:x.R join r on xr.parent_id=r.id
                       ),
                       b (select * from t:(select * from r order by name))
                       select * from b order by name
                       """);

//                     printResult(rs, 40);

                     rs.toNext(); assertEquals(root, rs.value("id"));
                                assertNull(rs.value("parent"));
                                assertEquals("root", rs.value("name"));
                     rs.toNext(); assertEquals(a, rs.value("id"));
                                assertEquals(root, rs.value("parent"));
                                assertEquals("root/a", rs.value("name"));
                     rs.toNext(); assertNotNull(rs.value("id"));
                                assertEquals(a, rs.value("parent"));
                                assertEquals("root/a/a_child1", rs.value("name"));
                     rs.toNext(); assertNotNull(rs.value("id"));
                                assertEquals(a, rs.value("parent"));
                                assertEquals("root/a/a_child2", rs.value("name"));
                     rs.toNext(); assertNotNull(rs.value("id"));
                                assertEquals(a, rs.value("parent"));
                                assertEquals("root/a/a_child3", rs.value("name"));

                     rs.toNext(); assertEquals(b, rs.value("id"));
                                assertEquals(root, rs.value("parent"));
                                assertEquals("root/b", rs.value("name"));
                     rs.toNext(); assertNotNull(rs.value("id"));
                                assertEquals(b, rs.value("parent"));
                                assertEquals("root/b/b_child1", rs.value("name"));
                     rs.toNext(); assertNotNull(rs.value("id"));
                                assertEquals(b, rs.value("parent"));
                                assertEquals("root/b/b_child2", rs.value("name"));

                     rs.toNext(); assertEquals(c, rs.value("id"));
                                assertEquals(root, rs.value("parent"));
                                assertEquals("root/c", rs.value("name"));
                     rs.toNext(); assertNotNull(rs.value("id"));
                                assertEquals(c, rs.value("parent"));
                                assertEquals("root/c/c_child1", rs.value("name"));
                     rs.toNext(); assertNotNull(rs.value("id"));
                                assertEquals(c, rs.value("parent"));
                                assertEquals("root/c/c_child2", rs.value("name"));
                     rs.toNext(); assertNotNull(rs.value("id"));
                                assertEquals(c, rs.value("parent"));
                                assertEquals("root/c/c_child3", rs.value("name"));
                     rs.toNext(); assertNotNull(rs.value("id"));
                                assertEquals(c, rs.value("parent"));
                                assertEquals("root/c/c_child4", rs.value("name"));
                   }
                 }));
  }
}
