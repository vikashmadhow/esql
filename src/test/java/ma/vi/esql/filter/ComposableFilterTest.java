/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.filter;

import ma.vi.esql.DataTest;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.QueryParams;
import ma.vi.esql.exec.composable.Composable;
import ma.vi.esql.exec.composable.ComposableFilter;
import ma.vi.esql.semantic.type.BaseRelation;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.Parser;
import ma.vi.esql.syntax.Program;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.modify.Delete;
import ma.vi.esql.syntax.modify.Insert;
import ma.vi.esql.syntax.modify.Update;
import ma.vi.esql.syntax.query.*;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class ComposableFilterTest extends DataTest {
  @TestFactory
  Stream<DynamicTest> path() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     setupTables(con);

                     BaseRelation x = db.structure().relation("test.pX");
                     BaseRelation z = db.structure().relation("test.pZ");

                     BaseRelation.Path path = z.path("test.pX");
                     System.out.println(path);
                     pathEquals(path,
                                new Link("test.pZ", "y_id", "test.pY", "_id", false),
                                new Link("test.pY", "b_id", "test.pB", "_id", false),
                                new Link("test.pB", "x_id", "test.pX", "_id", false));

                     path = x.path("test.pZ");
                     System.out.println(path);
                     pathEquals(path,
                                new Link("test.pB", "x_id", "test.pX", "_id", true),
                                new Link("test.pY", "b_id", "test.pB", "_id", true),
                                new Link("test.pZ", "y_id", "test.pY", "_id", true));
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> filter() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     setupTables(con);
                     Esql<?, ?> filtered =
                       con.prepare("select a from test.pZ where a < 2 or a > 5",
                                   new QueryParams()
                                     .and("test.pX", "x", "x.a=3")
                                     .or ("test.pX", "x", "x.a=4")
                                     .and("test.pB", "b", "b.a=5"));
                     System.out.println(filtered);

                     Select select = (Select)((Program)filtered).expressions().get(0);
                     TableExpr from = select.from();
                     Parser p = new Parser(db.structure());
                     fromEquals(from, "test.pZ", "pZ",
                                new Join("test.pY", "pY", null, p.parseExpression("pZ.y_id=pY._id")),
                                new Join("test.pB", "pB", null, p.parseExpression("pY.b_id=pB._id")),
                                new Join("test.pX", "x",  null, p.parseExpression("pB.x_id=x._id")));

                     assertEquals(p.parseExpression("(((pZ.a < 2 or pZ.a > 5) and x.a = 3) or x.a = 4) and pB.a = 5"), select.where());
                     con.execPrepared(filtered);
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> hierarchicalFilters() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     setupTables(con);
                     Esql<?, ?> filtered =
                       con.prepare("select a from test.pZ where a < 2 or a > 5",
                                   new QueryParams()
                                     .filter(new ComposableFilter(Composable.Op.OR,
                                                                  new ComposableFilter("test.pX", "x", "x.a=3"),
                                                                  new ComposableFilter(Composable.Op.AND,
                                                                                       new ComposableFilter("test.pX", "x", "x.a=4"),
                                                                                       new ComposableFilter("test.pY", "y", "y.a=7")),
                                                                  new ComposableFilter("test.pB", "b", "b.a=5"))));
                     System.out.println(filtered);

                     Select select = (Select)((Program)filtered).expressions().get(0);
                     TableExpr from = select.from();
                     Parser p = new Parser(db.structure());
                     fromEquals(from, "test.pZ", "pZ",
                                new Join("test.pY", "pY", null, p.parseExpression("pZ.y_id=pY._id")),
                                new Join("test.pB", "pB", null, p.parseExpression("pY.b_id=pB._id")),
                                new Join("test.pX", "x",  null, p.parseExpression("pB.x_id=x._id")));

                     assertEquals(p.parseExpression("(pZ.a < 2 or pZ.a > 5) and (x.a = 3 or (x.a = 4 and pY.a=7) or pB.a = 5)"), select.where());
                     con.execPrepared(filtered);
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> reverseAndForcedPathFilter() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     setupTables(con);
                     Esql<?, ?> filtered =
                       con.prepare("select * from test.pX where a < 2",
                                   new QueryParams()
                                     .and("test.pA", "a", "a.a=3")  // forcing through pA even if higher cost than B
                                     .and("test.pZ", "z", "z.a<4"));
                     System.out.println(filtered);

                     Select select = (Select)((Program)filtered).expressions().get(0);
                     TableExpr from = select.from();
                     Parser p = new Parser(db.structure());
                     fromEquals(from, "test.pX", "pX",
                                new Join("test.pA", "a",  null, p.parseExpression("pX._id=a.x_id")),
                                new Join("test.pY", "pY", null, p.parseExpression("a._id=pY.a_id")),
                                new Join("test.pZ", "z",  null, p.parseExpression("pY._id=z.y_id")));

                     assertEquals(p.parseExpression("(pX.a < 2) and a.a=3 and z.a<4"), select.where());
                     con.execPrepared(filtered);
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> filterOnLeftJoin() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     setupTables(con);
                     Esql<?, ?> filtered =
                       con.prepare("""
                                   select z.*
                                     from z:test.pZ
                                     join y:test.pY on z.y_id=y._id
                                     join c:test.pC on z.a=c.a
                                    where z.a < 2
                                   """,
                                   new QueryParams()
                                     .and("test.pX", "x", "x.a=3"));
                     System.out.println(filtered);

                     Select select = (Select)((Program)filtered).expressions().get(0);
                     TableExpr from = select.from();
                     Parser p = new Parser(db.structure());
                     fromEquals(from, "test.pZ", "z",
                                new Join("test.pY", "y", null, p.parseExpression("z.y_id=y._id")),
                                new Join("test.pC", "c", null, p.parseExpression("z.a=c.a")),
                                new Join("test.pX", "x", null, p.parseExpression("c.x_id=x._id")));
                     assertEquals(p.parseExpression("(z.a < 2) and x.a = 3"), select.where());

                     filtered =
                       con.prepare("""
                                   select z.*
                                     from z:test.pZ
                                     join y:test.pY on z.y_id=y._id
                                     left join c:test.pC on z.a=c.a
                                    where z.a < 2
                                   """,
                                   new QueryParams()
                                     .and("test.pX", "x", "x.a=3"));
                     System.out.println(filtered);

                     select = (Select)((Program)filtered).expressions().get(0);
                     from = select.from();
                     fromEquals(from, "test.pZ", "z",
                                new Join("test.pY", "y", null, p.parseExpression("z.y_id=y._id")),
                                new Join("test.pB", "pB", null, p.parseExpression("y.b_id=pB._id")),
                                new Join("test.pX", "x", null, p.parseExpression("pB.x_id=x._id")),
                                new Join("test.pC", "c", "left", p.parseExpression("z.a=c.a")));
                     assertEquals(p.parseExpression("(z.a < 2) and x.a = 3"), select.where());

                     con.execPrepared(filtered);
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> filterOnRightJoin() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     setupTables(con);
                     Esql<?, ?> filtered =
                       con.prepare("""
                                   select z.*
                                     from z:test.pZ
                                     join y:test.pY on z.y_id=y._id
                                     right join c:test.pC on z.a=c.a
                                    where z.a < 2
                                   """,
                                   new QueryParams()
                                     .and("test.pX", "x", "x.a=3"));
                     System.out.println(filtered);

                     Select select = (Select)((Program)filtered).expressions().get(0);
                     TableExpr from = select.from();
                     Parser p = new Parser(db.structure());
                     fromEquals(from, "test.pZ", "z",
                                new Join("test.pY", "y", null, p.parseExpression("z.y_id=y._id")),
                                new Join("test.pC", "c", "right", p.parseExpression("z.a=c.a")),
                                new Join("test.pX", "x", null, p.parseExpression("c.x_id=x._id")));
                     assertEquals(p.parseExpression("(z.a < 2) and x.a = 3"), select.where());
                     con.execPrepared(filtered);
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> filterOnFullJoin() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     setupTables(con);
                     Esql<?, ?> filtered =
                       con.prepare("""
                                   select z.*
                                     from z:test.pZ
                                     join y:test.pY on z.y_id=y._id
                                     full join c:test.pC on z.a=c.a
                                    where z.a < 2
                                   """,
                                   new QueryParams()
                                     .and("test.pX", "x", "x.a=3"));
                     System.out.println(filtered);

                     Select select = (Select)((Program)filtered).expressions().get(0);
                     TableExpr from = select.from();
                     Parser p = new Parser(db.structure());
                     fromEquals(from, "test.pZ", "z",
                                new Join("test.pY", "y", null, p.parseExpression("z.y_id=y._id")),
                                new Join("test.pC", "c", "full", p.parseExpression("z.a=c.a")),
                                new Join("test.pX", "x", null, p.parseExpression("c.x_id=x._id")));
                     assertEquals(p.parseExpression("(z.a < 2) and x.a = 3"), select.where());
                     con.execPrepared(filtered);
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> filterOnSubSelects() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     setupTables(con);
                     Esql<?, ?> filtered =
                       con.prepare("""
                                   select z.*,
                                          (select min(a) from test.pA)
                                     from z:test.pZ
                                     times t1(a, b):((1, 2), (3,4), (4,5))
                                     join s1:(select a from z1:test.pZ) on s1.a=z.a
                                     join y:test.pY on z.y_id=y._id
                                     full join c:test.pC on z.a=c.a
                                    where z.a < 2
                                      and exists(select pC._id
                                                   from test.pC
                                                   join test.pY on pC.a=pY.a)
                                   """,
                                   new QueryParams()
                                     .and("test.pX", "x", "x.a=3"));
                     System.out.println(filtered);
//                     Select select = (Select)((Program)filtered).expressions().get(0);
//                     TableExpr from = select.from();
//                     Parser p = new Parser(db.structure());
//                     fromEquals(from, "test.pZ", "z",
//                                new Join("test.pY", "y", null, p.parseExpression("z.y_id=y._id")),
//                                new Join("test.pC", "c", "full", p.parseExpression("z.a=c.a")),
//                                new Join("test.pX", "x", null, p.parseExpression("c.x_id=x._id")));
//                     assertEquals(p.parseExpression("z.a < 2 and x.a = 3"), select.where());
                     con.execPrepared(filtered);
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> filterOnSubSelectsUnfiltered() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     setupTables(con);
                     Esql<?, ?> filtered =
                       con.prepare("""
                                   select distinct explicit unfiltered z.*,
                                          (select unfiltered explicit distinct min(a) from test.pA)
                                     from z:test.pZ
                                     times t1(a, b):((1, 2), (3,4), (4,5))
                                     join s1:(select unfiltered a from z1:test.pZ) on s1.a=z.a
                                     join y:test.pY on z.y_id=y._id
                                     full join c:test.pC on z.a=c.a
                                    where z.a < 2
                                      and exists(select unfiltered pC._id
                                                   from test.pC
                                                   join test.pY on pC.a=pY.a)
                                   """,
                                   new QueryParams()
                                     .and("test.pX", "x", "x.a=3"));
                     System.out.println(filtered);
//                     Select select = (Select)((Program)filtered).expressions().get(0);
//                     TableExpr from = select.from();
//                     Parser p = new Parser(db.structure());
//                     fromEquals(from, "test.pZ", "z",
//                                new Join("test.pY", "y", null, p.parseExpression("z.y_id=y._id")),
//                                new Join("test.pC", "c", "full", p.parseExpression("z.a=c.a")),
//                                new Join("test.pX", "x", null, p.parseExpression("c.x_id=x._id")));
//                     assertEquals(p.parseExpression("z.a < 2 and x.a = 3"), select.where());
                     con.execPrepared(filtered);
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> filterOnWith() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     setupTables(con);
                     Esql<?, ?> filtered =
                       con.prepare("""
                                   with w1(a) (
                                    select min(a) from test.pA
                                   ),
                                   w2(
                                    select a:max(a) from test.pZ
                                   )
                                   select w1.a, w2.a, pY.*
                                     from w1 times test.pY times w2
                                   """,
                                   new QueryParams()
                                     .and("test.pX", "x", "x.a=3"));
                     System.out.println(filtered);
//                     Select select = (Select)((Program)filtered).expressions().get(0);
//                     TableExpr from = select.from();
//                     Parser p = new Parser(db.structure());
//                     fromEquals(from, "test.pZ", "z",
//                                new Join("test.pY", "y", null, p.parseExpression("z.y_id=y._id")),
//                                new Join("test.pC", "c", "full", p.parseExpression("z.a=c.a")),
//                                new Join("test.pX", "x", null, p.parseExpression("c.x_id=x._id")));
//                     assertEquals(p.parseExpression("z.a < 2 and x.a = 3"), select.where());
                     con.execPrepared(filtered);
                   }
                 }));
  }


  @TestFactory
  Stream<DynamicTest> filterOnWithRecursive() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     setupTables(con);
                     Esql<?, ?> filtered =
                       con.prepare("""
                                   with recursive w1(a) (
                                    select a from test.pA where a < 0
                                    union
                                    select pA.a
                                      from w1 join test.pA on w1.a=pA.a
                                     where pA.a > w1.a
                                   ),
                                   w2(
                                    select a:max(a) from test.pZ
                                   )
                                   select w1.a, w2.a, pY.*
                                     from w1 times test.pY times w2
                                   """,
                                   new QueryParams()
                                     .and("test.pX", "x", "x.a=3"));
                     System.out.println(filtered);
//                     Select select = (Select)((Program)filtered).expressions().get(0);
//                     TableExpr from = select.from();
//                     Parser p = new Parser(db.structure());
//                     fromEquals(from, "test.pZ", "z",
//                                new Join("test.pY", "y", null, p.parseExpression("z.y_id=y._id")),
//                                new Join("test.pC", "c", "full", p.parseExpression("z.a=c.a")),
//                                new Join("test.pX", "x", null, p.parseExpression("c.x_id=x._id")));
//                     assertEquals(p.parseExpression("z.a < 2 and x.a = 3"), select.where());
                     con.execPrepared(filtered);
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> filterOnUpdate() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     setupTables(con);
                     Esql<?, ?> filtered =
                       con.prepare("""
                                   update z
                                     from z:test.pZ
                                     join y:test.pY on z.y_id=y._id
                                     join c:test.pC on z.a=c.a
                                      set a=c.a,
                                          _id=y._id
                                    where z.a < 2
                                   """,
                                   new QueryParams()
                                     .and("test.pX", "x", "x.a=3"));
                     System.out.println(filtered);

                     Update update = (Update)((Program)filtered).expressions().get(0);
                     TableExpr from = update.from();
                     Parser p = new Parser(db.structure());
                     fromEquals(from, "test.pZ", "z",
                                new Join("test.pY", "y", null, p.parseExpression("z.y_id=y._id")),
                                new Join("test.pC", "c", null, p.parseExpression("z.a=c.a")),
                                new Join("test.pX", "x", null, p.parseExpression("c.x_id=x._id")));
                     assertEquals(p.parseExpression("(z.a < 2) and x.a = 3"), update.where());
                     con.execPrepared(filtered);

                     filtered =
                       con.prepare("""
                                   update z
                                     from z:test.pZ
                                     join y:test.pY on z.y_id=y._id
                                     left join c:test.pC on z.a=c.a
                                      set a=c.a,
                                          _id=y._id
                                    where z.a < 2
                                   """,
                                   new QueryParams()
                                     .and("test.pX", "x", "x.a=3"));
                     System.out.println(filtered);

                     update = (Update)((Program)filtered).expressions().get(0);
                     from = update.from();
                     fromEquals(from, "test.pZ", "z",
                                new Join("test.pY", "y", null, p.parseExpression("z.y_id=y._id")),
                                new Join("test.pB", "pB", null, p.parseExpression("y.b_id=pB._id")),
                                new Join("test.pX", "x", null, p.parseExpression("pB.x_id=x._id")),
                                new Join("test.pC", "c", "left", p.parseExpression("z.a=c.a")));
                     assertEquals(p.parseExpression("(z.a < 2) and x.a = 3"), update.where());
                     con.execPrepared(filtered);
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> filterOnDelete() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     setupTables(con);
                     Esql<?, ?> filtered =
                       con.prepare("""
                                   delete z
                                     from z:test.pZ
                                     join y:test.pY on z.y_id=y._id
                                     join c:test.pC on z.a=c.a
                                    where z.a < 2
                                   """,
                                   new QueryParams()
                                     .and("test.pX", "x", "x.a=3"));
                     System.out.println(filtered);

                     Delete delete = (Delete)((Program)filtered).expressions().get(0);
                     TableExpr from = delete.from();
                     Parser p = new Parser(db.structure());
                     fromEquals(from, "test.pZ", "z",
                                new Join("test.pY", "y", null, p.parseExpression("z.y_id=y._id")),
                                new Join("test.pC", "c", null, p.parseExpression("z.a=c.a")),
                                new Join("test.pX", "x", null, p.parseExpression("c.x_id=x._id")));
                     assertEquals(p.parseExpression("(z.a < 2) and x.a = 3"), delete.where());
                     con.execPrepared(filtered);

                     filtered =
                       con.prepare("""
                                   delete z
                                     from z:test.pZ
                                     join y:test.pY on z.y_id=y._id
                                     left join c:test.pC on z.a=c.a
                                    where z.a < 2
                                   """,
                                   new QueryParams()
                                     .and("test.pX", "x", "x.a=3"));
                     System.out.println(filtered);

                     delete = (Delete)((Program)filtered).expressions().get(0);
                     from = delete.from();
                     fromEquals(from, "test.pZ", "z",
                                new Join("test.pY", "y", null, p.parseExpression("z.y_id=y._id")),
                                new Join("test.pB", "pB", null, p.parseExpression("y.b_id=pB._id")),
                                new Join("test.pX", "x", null, p.parseExpression("pB.x_id=x._id")),
                                new Join("test.pC", "c", "left", p.parseExpression("z.a=c.a")));
                     assertEquals(p.parseExpression("(z.a < 2) and x.a = 3"), delete.where());
                     con.execPrepared(filtered);
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> filterOnInsert() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     setupTables(con);
                     Esql<?, ?> filtered =
                       con.prepare("""
                                   insert into test.pZ(_id, a)
                                   select newid(), c.a
                                     from z:test.pZ
                                     join y:test.pY on z.y_id=y._id
                                     join c:test.pC on z.a=c.a
                                    where z.a < 2
                                   """,
                                   new QueryParams()
                                     .and("test.pX", "x", "x.a=3"));
                     System.out.println(filtered);

                     Select select = ((Insert)((Program)filtered).expressions().get(0)).select();
                     TableExpr from = select.from();
                     Parser p = new Parser(db.structure());
                     fromEquals(from, "test.pZ", "z",
                                new Join("test.pY", "y", null, p.parseExpression("z.y_id=y._id")),
                                new Join("test.pC", "c", null, p.parseExpression("z.a=c.a")),
                                new Join("test.pX", "x", null, p.parseExpression("c.x_id=x._id")));
                     assertEquals(p.parseExpression("(z.a < 2) and x.a = 3"), select.where());
                     con.execPrepared(filtered);

                     filtered =
                       con.prepare("""
                                   insert into test.pZ(_id, a)
                                   select newid(), c.a
                                     from z:test.pZ
                                     join y:test.pY on z.y_id=y._id
                                     left join c:test.pC on z.a=c.a
                                    where z.a < 2
                                   """,
                                   new QueryParams()
                                     .and("test.pX", "x", "x.a=3"));
                     System.out.println(filtered);

                     select = ((Insert)((Program)filtered).expressions().get(0)).select();
                     from = select.from();
                     fromEquals(from, "test.pZ", "z",
                                new Join("test.pY", "y", null, p.parseExpression("z.y_id=y._id")),
                                new Join("test.pB", "pB", null, p.parseExpression("y.b_id=pB._id")),
                                new Join("test.pX", "x", null, p.parseExpression("pB.x_id=x._id")),
                                new Join("test.pC", "c", "left", p.parseExpression("z.a=c.a")));
                     assertEquals(p.parseExpression("(z.a < 2) and x.a = 3"), select.where());
                     con.execPrepared(filtered);
                   }
                 }));
  }

  record Link(String  fromTable,
              String  fromColumn,
              String  toTable,
              String  toColumn,
              boolean reverse) {}

  public static void pathEquals(BaseRelation.Path path, Link... links) {
    assertEquals(path.elements().size(), links.length, "Path has different number of elements ("
               + path.elements().size() + ") than links supplied (" + links.length + ')');
    for (int i = 0; i < links.length; i++) {
      Link link = links[i];
      BaseRelation.Link pathLink = path.elements().get(i);
      assertEquals(link.fromTable,  pathLink.constraint().table());
      assertEquals(link.fromColumn, pathLink.constraint().columns().get(0));
      assertEquals(link.toTable,    pathLink.constraint().targetTable());
      assertEquals(link.toColumn,   pathLink.constraint().targetColumns().get(0));
      assertEquals(link.reverse,    pathLink.reverse());
    }
  }

  record Join(String toTable,
              String toAlias,
              String joinType,
              Expression<?, ?> on) {}

  public static void fromEquals(TableExpr from,
                                String    fromTable,
                                String    fromAlias,
                                Join...   joins) {
    TableExpr t = from;
    int i = joins.length - 1;
    while (true) {
      if (i < 0) {
        SingleTableExpr s = (SingleTableExpr)t;
        assertEquals(fromTable, s.tableName());
        assertEquals(fromAlias, s.alias());
        break;
      } else {
        AbstractJoinTableExpr j = (AbstractJoinTableExpr)t;
        SingleTableExpr s = (SingleTableExpr)j.right();
        Join join = joins[i];
        assertEquals(join.toTable, s.tableName());
        assertEquals(join.toAlias, s.alias());
        assertEquals(join.joinType, j.joinType());
        if (join.on != null) {
          JoinTableExpr je = (JoinTableExpr)j;
          assertEquals(join.on, je.on());
        }
        t = j.left();
        i--;
      }
    }
  }

  public static void setupTables(EsqlConnection con) {
    con.exec("drop table test.pX");
    con.exec("drop table test.pA");
    con.exec("drop table test.pB");
    con.exec("drop table test.pC");
    con.exec("drop table test.pY");
    con.exec("drop table test.pZ");

    con.exec("""
          create table test.pX drop undefined(
            _id uuid not null,
            a   int,
            primary key(_id)
          )""");

    con.exec("""
          create table test.pA drop undefined(
            _id uuid not null,
            a int,
            x_id uuid,
            foreign key(x_id) references test.pX(_id) cost (10, 6),
            primary key(_id)
          )""");

    con.exec("""
          create table test.pB drop undefined(
            _id uuid not null,
            a int,
            x_id uuid,
            foreign key(x_id) references test.pX(_id) cost (5, 5),
            primary key(_id)
          )""");

    con.exec("""
          create table test.pC drop undefined(
            _id uuid not null,
            a int,
            x_id uuid,
            foreign key(x_id) references test.pX(_id),
            primary key(_id)
          )""");

    con.exec("""
          create table test.pY drop undefined(
            _id uuid not null,
            a int,
            a_id uuid,
            b_id uuid,
            foreign key(a_id) references test.pA(_id),
            foreign key(b_id) references test.pB(_id),
            primary key(_id)
          )""");

    con.exec("""
          create table test.pZ drop undefined(
            _id uuid not null,
            a int,
            y_id uuid,
            foreign key(y_id) references test.pY(_id),
            primary key(_id)
          )""");
  }
}
