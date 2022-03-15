/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.filter;

import ma.vi.esql.DataTest;
import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.exec.QueryParams;
import ma.vi.esql.semantic.type.BaseRelation;
import ma.vi.esql.syntax.Esql;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class FilterTest extends DataTest {
  @TestFactory
  Stream<DynamicTest> path() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("drop table test.pX");
                     con.exec("drop table test.pA");
                     con.exec("drop table test.pB");
                     con.exec("drop table test.pY");
                     con.exec("drop table test.pZ");

                     con.exec("""
                            create table test.pX drop undefined(
                              _id uuid not null,
                              a int,
                              primary key(_id)
                            )""");

                     con.exec("""
                            create table test.pA drop undefined(
                              _id uuid not null,
                              a int,
                              x_id uuid,
                              foreign key(x_id) references test.pX(_id) cost (10, 4),
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

                     BaseRelation x = db.structure().relation("test.pX");
                     BaseRelation y = db.structure().relation("test.pY");
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
                                new Link("test.pA", "x_id", "test.pX", "_id", true),
                                new Link("test.pY", "a_id", "test.pA", "_id", true),
                                new Link("test.pZ", "y_id", "test.pY", "_id", true));
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> filter() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("drop table test.pX");
                     con.exec("drop table test.pA");
                     con.exec("drop table test.pB");
                     con.exec("drop table test.pY");
                     con.exec("drop table test.pZ");

                     con.exec("""
                            create table test.pX drop undefined(
                              _id uuid not null,
                              a int,
                              primary key(_id)
                            )""");

                     con.exec("""
                            create table test.pA drop undefined(
                              _id uuid not null,
                              a int,
                              x_id uuid,
                              foreign key(x_id) references test.pX(_id) cost (10, 4),
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

                     Esql<?, ?> filtered =
                       con.prepare("select a from test.pZ where a < 2",
                                   new QueryParams()
                                     .and("test.pX", "x", "x.a=3")
                                     .or("test.pX", "x", "x.a=4")
                                     .and("test.pB", "b", "b.a=5"));
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
}
