/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.filter;

import ma.vi.esql.DataTest;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.QueryParams;
import ma.vi.esql.exec.composable.ComposableColumn;
import ma.vi.esql.semantic.type.BaseRelation;
import ma.vi.esql.semantic.type.Column;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.Parser;
import ma.vi.esql.syntax.Program;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.query.*;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class ComposableColumnTest extends DataTest {
  @TestFactory
  Stream<DynamicTest> addSingleColumn() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     setupTables(con);
                     Esql<?, ?> filtered =
                       con.prepare("select ___ from test.pZ where a < 2 or a > 5",
                                   new QueryParams()
                                        .column("test.pX", "x", "a"));
                     System.out.println(filtered);

                     Select select = (Select)((Program)filtered).expressions().get(0);
                     TableExpr from = select.from();
                     Parser p = new Parser(db.structure());
                     fromEquals(from, "test.pZ", "pZ",
                                new Join("test.pY", "pY", null, p.parseExpression("pZ.y_id=pY._id")),
                                new Join("test.pB", "pB", null, p.parseExpression("pY.b_id=pB._id")),
                                new Join("test.pX", "x",  null, p.parseExpression("pB.x_id=x._id")));

                     assertEquals(p.parseExpression("pZ.a < 2 or pZ.a > 5"), select.where());
                     assertEquals(List.of(p.parseExpression("x.a")),
                                  select.columns().stream()
                                        .map(Column::expression)
                                        .toList());
                     con.execPrepared(filtered);
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> addSingleColumnToExistingColumns() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     setupTables(con);
                     Esql<?, ?> filtered =
                       con.prepare("select a from test.pZ where a < 2 or a > 5",
                                   new QueryParams()
                                        .column("test.pX", "x", "b"));
                     System.out.println(filtered);

                     Select select = (Select)((Program)filtered).expressions().get(0);
                     TableExpr from = select.from();
                     Parser p = new Parser(db.structure());
                     fromEquals(from, "test.pZ", "pZ",
                                new Join("test.pY", "pY", null, p.parseExpression("pZ.y_id=pY._id")),
                                new Join("test.pB", "pB", null, p.parseExpression("pY.b_id=pB._id")),
                                new Join("test.pX", "x",  null, p.parseExpression("pB.x_id=x._id")));

                     assertEquals(p.parseExpression("pZ.a < 2 or pZ.a > 5"), select.where());
                     assertEquals(List.of(p.parseExpression("pZ.a"),
                                          p.parseExpression("x.b")),
                                  select.columns().stream()
                                        .filter (c -> !c.name().contains("/"))
                                        .map    (Column::expression)
                                        .toList ());
                     con.execPrepared(filtered);
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> addGroupedColumn() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     setupTables(con);
                     Esql<?, ?> filtered =
                       con.prepare("select ___ from test.pZ where a < 2 or a > 5",
                                   new QueryParams()
                                        .column("test.pX", "x", null, "a", GroupBy.Type.Simple)
                                        .column("test.pY", "y", null, "sum(a)"));
                     System.out.println(filtered);

                     Select select = (Select)((Program)filtered).expressions().get(0);
                     TableExpr from = select.from();
                     Parser p = new Parser(db.structure());
                     fromEquals(from, "test.pZ", "pZ",
                                new Join("test.pY", "pY", null, p.parseExpression("pZ.y_id=pY._id")),
                                new Join("test.pB", "pB", null, p.parseExpression("pY.b_id=pB._id")),
                                new Join("test.pX", "x",  null, p.parseExpression("pB.x_id=x._id")));

                     assertEquals(p.parseExpression("pZ.a < 2 or pZ.a > 5"), select.where());
                     assertEquals(List.of(p.parseExpression("x.a"),
                                          p.parseExpression("sum(pY.a)")),
                                  select.columns().stream()
                                        .filter (c -> !c.name().contains("/"))
                                        .map    (Column::expression)
                                        .toList ());
                     con.execPrepared(filtered);
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> addComplexGroupedColumn() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     setupTables(con);
                     Esql<?, ?> filtered =
                       con.prepare("select ___ from test.pZ where a < 2 or a > 5",
                                   new QueryParams()
                                        .column("test.pX", "x", null, "a", GroupBy.Type.Cube)
                                        .column("test.pX", "x", null, "b", GroupBy.Type.Simple)
                                        .column("test.pZ", "z", null, "a", GroupBy.Type.Rollup)
                                        .column("test.pY", "y", null, "sum(a)")
                                        .column("test.pX", "x", null, "count(b)"));
                     System.out.println(filtered);

                     Select select = (Select)((Program)filtered).expressions().get(0);
                     TableExpr from = select.from();
                     Parser p = new Parser(db.structure());
                     fromEquals(from, "test.pZ", "pZ",
                                new Join("test.pY", "pY", null, p.parseExpression("pZ.y_id=pY._id")),
                                new Join("test.pB", "pB", null, p.parseExpression("pY.b_id=pB._id")),
                                new Join("test.pX", "x",  null, p.parseExpression("pB.x_id=x._id")));

                     assertEquals(p.parseExpression("pZ.a < 2 or pZ.a > 5"), select.where());
                     assertEquals(List.of(p.parseExpression("x.a"),
                                          p.parseExpression("x.b"),
                                          p.parseExpression("pZ.a"),
                                          p.parseExpression("sum(pY.a)"),
                                          p.parseExpression("count(x.b)")),
                                  select.columns().stream()
                                        .filter (c -> !c.name().contains("/"))
                                        .map    (Column::expression)
                                        .toList ());

                     assertEquals(List.of(p.parseExpression("x.a"),
                                          p.parseExpression("x.b"),
                                          p.parseExpression("pZ.a")),
                                  select.groupBy().groupBy());

                     assertEquals(GroupBy.Type.Cube, select.groupBy().groupType());
                     con.execPrepared(filtered);
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> addComplexGroupedColumnWithoutAggregates() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     setupTables(con);
                     Esql<?, ?> filtered =
                       con.prepare("select ___ from test.pZ where a < 2 or a > 5",
                                   new QueryParams()
                                        .column("test.pX", "x", null, "a", GroupBy.Type.Cube)
                                        .column("test.pX", "x", null, "b", GroupBy.Type.Simple)
                                        .column("test.pZ", "z", null, "a", GroupBy.Type.Rollup));
                     System.out.println(filtered);

                     Select select = (Select)((Program)filtered).expressions().get(0);
                     TableExpr from = select.from();
                     Parser p = new Parser(db.structure());
                     fromEquals(from, "test.pZ", "pZ",
                                new Join("test.pY", "pY", null, p.parseExpression("pZ.y_id=pY._id")),
                                new Join("test.pB", "pB", null, p.parseExpression("pY.b_id=pB._id")),
                                new Join("test.pX", "x",  null, p.parseExpression("pB.x_id=x._id")));

                     assertEquals(p.parseExpression("pZ.a < 2 or pZ.a > 5"), select.where());
                     assertEquals(List.of(p.parseExpression("x.a"),
                                          p.parseExpression("x.b"),
                                          p.parseExpression("pZ.a")),
                                  select.columns().stream()
                                        .filter (c -> !c.name().contains("/"))
                                        .map    (Column::expression)
                                        .toList ());

                     assertEquals(List.of(p.parseExpression("x.a"),
                                          p.parseExpression("x.b"),
                                          p.parseExpression("pZ.a")),
                                  select.groupBy().groupBy());

                     assertEquals(GroupBy.Type.Cube, select.groupBy().groupType());
                     con.execPrepared(filtered);
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> autoGroup() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     setupTables(con);
                     Esql<?, ?> filtered =
                       con.prepare("select ___ from test.pZ where a < 2 or a > 5",
                                   new QueryParams()
                                     .column("test.pX", "x", null, "a")
                                     .column("test.pX", "x", null, "b")
                                     .column("test.pZ", "z", null, "a")
                                     .column("test.pY", "y", null, "sum(a)")
                                     .column("test.pX", "x", null, "count(b)"));
                     System.out.println(filtered);

                     Select select = (Select)((Program)filtered).expressions().get(0);
                     TableExpr from = select.from();
                     Parser p = new Parser(db.structure());
                     fromEquals(from, "test.pZ", "pZ",
                                new Join("test.pY", "pY", null, p.parseExpression("pZ.y_id=pY._id")),
                                new Join("test.pB", "pB", null, p.parseExpression("pY.b_id=pB._id")),
                                new Join("test.pX", "x",  null, p.parseExpression("pB.x_id=x._id")));

                     assertEquals(p.parseExpression("pZ.a < 2 or pZ.a > 5"), select.where());
                     assertEquals(List.of(p.parseExpression("x.a"),
                                          p.parseExpression("x.b"),
                                          p.parseExpression("pZ.a"),
                                          p.parseExpression("sum(pY.a)"),
                                          p.parseExpression("count(x.b)")),
                                  select.columns().stream()
                                        .filter (c -> !c.name().contains("/"))
                                        .map    (Column::expression)
                                        .toList ());

                     assertEquals(List.of(p.parseExpression("x.a"),
                                          p.parseExpression("x.b"),
                                          p.parseExpression("pZ.a")),
                                  select.groupBy().groupBy());

                     assertEquals(GroupBy.Type.Simple, select.groupBy().groupType());
                     con.execPrepared(filtered);
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> autoGroupWithWindow() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     setupTables(con);
                     Esql<?, ?> filtered =
                       con.prepare("select ___ from test.pZ where a < 2 or a > 5",
                                   new QueryParams()
                                     .column("test.pX", "x", null, "a")
                                     .column("test.pX", "x", null, "b")
                                     .column("test.pZ", "z", null, "a")
                                     .column("test.pY", "y", null, "sum(a) over(partition by b)")
                                     .column("test.pX", "x", null, "count(b)"));
                     System.out.println(filtered);

                     Select select = (Select)((Program)filtered).expressions().get(0);
                     TableExpr from = select.from();
                     Parser p = new Parser(db.structure());
                     fromEquals(from, "test.pZ", "pZ",
                                new Join("test.pY", "pY", null, p.parseExpression("pZ.y_id=pY._id")),
                                new Join("test.pB", "pB", null, p.parseExpression("pY.b_id=pB._id")),
                                new Join("test.pX", "x",  null, p.parseExpression("pB.x_id=x._id")));

                     assertEquals(p.parseExpression("pZ.a < 2 or pZ.a > 5"), select.where());
                     assertEquals(List.of(p.parseExpression("x.a"),
                                          p.parseExpression("x.b"),
                                          p.parseExpression("pZ.a"),
                                          p.parseExpression("sum(pY.a) over(partition by pY.b)"),
                                          p.parseExpression("count(x.b)")),
                                  select.columns().stream()
                                        .filter (c -> !c.name().contains("/"))
                                        .map    (Column::expression)
                                        .toList ());

                     assertEquals(List.of(p.parseExpression("x.a"),
                                          p.parseExpression("x.b"),
                                          p.parseExpression("pZ.a"),
                                          p.parseExpression("pY.a"),
                                          p.parseExpression("pY.b")),
                                  select.groupBy().groupBy());

                     assertEquals(GroupBy.Type.Simple, select.groupBy().groupType());
                     con.execPrepared(filtered);
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> addColumnToSingleSubSelect() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     setupTables(con);
                     Esql<?, ?> filtered =
                       con.prepare("""
                                   select *
                                     from s1:(select a from z1:test.pZ)
                                   """,
                                   new QueryParams()
                                        .column("test.pX", "x", "b"));
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
  Stream<DynamicTest> addColumnToSubSelects() {
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
                                        .column("test.pX", "x", "a"));
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
  Stream<DynamicTest> addColumnWithMissingTable() {
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
                                     join s1:(select z1.a from z1:test.pZ) on s1.a=z.a
                                     join y:test.pY on z.y_id=y._id
                                     full join c:test.pC on z.a=c.a
                                    where z.a < 2
                                      and exists(select pC._id
                                                   from test.pC
                                                   join test.pY on pC.a=pY.a)
                                   """,
                                   new QueryParams()
                                        .column(new ComposableColumn(null, "a", "z1.a", null)));
                     System.out.println(filtered);
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
    con.exec("drop table test.pZ");
    con.exec("drop table test.pY");
    con.exec("drop table test.pA");
    con.exec("drop table test.pB");
    con.exec("drop table test.pC");
    con.exec("drop table test.pX");

    con.exec("""
          create table test.pX drop undefined(
            _id uuid not null,
            a int,
            b int,
            primary key(_id)
          )""");

    con.exec("""
          create table test.pA drop undefined(
            _id uuid not null,
            a int,
            b int,
            x_id uuid,
            foreign key(x_id) references test.pX(_id) cost (10, 6),
            primary key(_id)
          )""");

    con.exec("""
          create table test.pB drop undefined(
            _id uuid not null,
            a int,
            b int,
            x_id uuid,
            foreign key(x_id) references test.pX(_id) cost (5, 5),
            primary key(_id)
          )""");

    con.exec("""
          create table test.pC drop undefined(
            _id uuid not null,
            a int,
            b int,
            x_id uuid,
            foreign key(x_id) references test.pX(_id),
            primary key(_id)
          )""");

    con.exec("""
          create table test.pY drop undefined(
            _id uuid not null,
            a int,
            b int,
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
            b int,
            y_id uuid,
            foreign key(y_id) references test.pY(_id),
            primary key(_id)
          )""");
  }
}
