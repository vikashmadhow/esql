package ma.vi.esql.define;

import ma.vi.base.lang.NotFoundException;
import ma.vi.esql.DataTest;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.Result;
import ma.vi.esql.semantic.type.BaseRelation;
import ma.vi.esql.semantic.type.Column;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.Parser;
import ma.vi.esql.syntax.define.table.ForeignKeyConstraint;
import ma.vi.esql.translation.Translatable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;
import static ma.vi.esql.builder.Attributes.*;
import static ma.vi.esql.translation.Translatable.Target.ESQL;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class ConstraintTest extends DataTest {
  @TestFactory
  Stream<DynamicTest> dependents() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   BaseRelation s = db.structure().relation("S");
                   BaseRelation t = db.structure().relation("a.b.T");
                   BaseRelation x = db.structure().relation("a.b.X");
                   BaseRelation y = db.structure().relation("b.Y");

                   List<ForeignKeyConstraint> sDep = s.dependentConstraints();
                   assertEquals(3, sDep.size());
                   assertTrue(sDep.stream()
                                  .anyMatch(d -> d.sameAs(dep("a.b.T", "s_id", "S", "_id"))));
                   assertTrue(sDep.stream()
                                  .anyMatch(d -> d.sameAs(dep("a.b.X", "s_id", "S", "_id"))));
                   assertTrue(sDep.stream()
                                  .anyMatch(d -> d.sameAs(dep("b.Y", "s_id", "S", "_id"))));
                 }));
  }

  @TestFactory
  Stream<DynamicTest> addDropForeignConstraints() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("drop table test.Y");
                     con.exec("drop table test.X");

                     con.exec("""
                            create table test.X drop undefined(
                              _id uuid not null,
                              a int,
                              b int,
                              primary key(_id)
                            )""");

                     con.exec("""
                            create table test.Y drop undefined(
                              _id uuid not null,
                              a int,
                              b int,
                              x1_id uuid,
                              x2_id uuid,
                              constraint x1_to_y foreign key(x1_id) references test.X(_id),
                              primary key(_id)
                            )""");

                     BaseRelation x = db.structure().relation("test.X");
                     BaseRelation y = db.structure().relation("test.Y");

                     List<ForeignKeyConstraint> yDep = y.dependentConstraints();
                     assertEquals(0, yDep.size());

                     List<ForeignKeyConstraint> xDep = x.dependentConstraints();
                     assertEquals(1, xDep.size());
                     assertTrue(xDep.get(0).sameAs(dep("test.Y", "x1_id", "test.X", "_id")));

                     con.exec("alter table test.Y drop constraint x1_to_y");
                     xDep = x.dependentConstraints();
                     assertEquals(0, xDep.size());

                     con.exec("alter table test.Y add constraint x1_to_y foreign key(x1_id) references test.X(_id)");
                     xDep = x.dependentConstraints();
                     assertEquals(1, xDep.size());
                     assertTrue(xDep.get(0).sameAs(dep("test.Y", "x1_id", "test.X", "_id")));

                     con.exec("alter table test.Y add constraint x2_to_y foreign key(x2_id) references test.X(_id)");
                     xDep = x.dependentConstraints();
                     assertEquals(2, xDep.size());
                     assertTrue(xDep.stream()
                                    .anyMatch(d -> d.sameAs(dep("test.Y", "x1_id", "test.X", "_id"))));
                     assertTrue(xDep.stream()
                                    .anyMatch(d -> d.sameAs(dep("test.Y", "x2_id", "test.X", "_id"))));
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> uniqueAndRequiredConstraints() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("drop table test.X");
                     con.exec("""
                            create table test.X drop undefined(
                              _id uuid not null,
                              a int,
                              b int not null,
                              c int default 5,
                              d int,
                              e = b+c+d,
                              primary key(_id),
                              unique(a),
                              unique(a, b),
                              unique(b, c, d)
                            )""");

                     BaseRelation x = db.structure().relation("test.X");

                     Column col = x.column("a/unique");
                     assertTrue((Boolean)col.expression().exec(Translatable.Target.ESQL, con, new EsqlPath(col), null));
                     assertThrows(NotFoundException.class, () -> x.column("b/unique"));
                     assertThrows(NotFoundException.class, () -> x.column("c/unique"));

                     col = x.column("b/required");
                     assertTrue((Boolean)col.expression().exec(Translatable.Target.ESQL, con, new EsqlPath(col), null));
                     col = x.column("_id/required");
                     assertTrue((Boolean)col.expression().exec(Translatable.Target.ESQL, con, new EsqlPath(col), null));
                     assertThrows(NotFoundException.class, () -> x.column("a/required"));
                     assertThrows(NotFoundException.class, () -> x.column("c/required"));
                     assertThrows(NotFoundException.class, () -> x.column("d/required"));

                     col = x.column("/unique");
                     assertTrue(
                         new JSONArray(
                           new JSONArray[] {
                               new JSONArray(new String[]{"a"}),
                               new JSONArray(new String[]{"a", "b"}),
                               new JSONArray(new String[]{"b", "c", "d"}),
                           }
                         ).similar(col.expression().exec(Translatable.Target.ESQL, con, new EsqlPath(col), null)));

                     System.out.println(x);
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> foreignKeyAttributes() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   Parser p = new Parser(db.structure());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     Result rs = con.exec("select * from a.b.X");

                     assertEquals(rs.resultAttributes().size(), 5);
                     assertEquals(rs.resultAttributes().get("name"), "X");
                     assertEquals(rs.resultAttributes().get("description"), "X test table");
                     assertTrue(new JSONArray(singletonList("_id")).similar(rs.resultAttributes().get(PRIMARY_KEY)));
                     assertEquals(new HashSet<>(new JSONArray(
                       List.of(
                         new JSONObject(Map.of(
                           "from_columns", new JSONArray(singletonList("s_id")),
                           "to_table", "S",
                           "to_columns", new JSONArray(singletonList("_id"))
                         )),
                         new JSONObject(Map.of(
                           "from_columns", new JSONArray(singletonList("t_id")),
                           "to_table", "a.b.T",
                           "to_columns", new JSONArray(singletonList("_id"))
                         ))
                       )
                     ).toList()),
                     new HashSet<>(
                       ((JSONArray)rs.resultAttributes().get(REFERENCES)).toList()));

                     assertTrue(new JSONArray(
                       List.of(
                         new JSONObject(Map.of(
                           "from_table", "b.Y",
                           "from_columns", new JSONArray(singletonList("x_id")),
                           "to_columns", new JSONArray(singletonList("_id"))
                         ))
                       )
                     ).similar(rs.resultAttributes().get(REFERRED_BY)));
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> checkConstraints() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   Parser p = new Parser(db.structure());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("drop table A");
                     con.exec("""
                              create table A drop undefined({
                                  name: 'A',
                                  description: 'A test table'
                                }
                                _id uuid not null,
                                a int {
                                  m1: b > 5,
                                  m2: 10,
                                  m3: a != 0
                                },
                                b int {
                                  m1: b < 0
                                },
                                c=a+b {
                                  m1: a > 5,
                                  m2: a + b,
                                  m3: b > 5
                                },
                                d=b+c {
                                  m1: 10
                                },
                                e int {
                                  m1: c,
                                  "values": {"any": {en: 'Any', fr: 'Une ou plusieurs'}, "all": {en: 'All', fr: 'Toutes'}}
                                },
                                f=from A select max(a) {
                                  m1: from A select min(a)
                                },
                                g=from A select distinct c where d>5 {
                                  m1: d
                                },
                                h text {
                                  m1: 5
                                },
                                i string,
                                check e >= 5,
                                check e <= 100,
                                check length(h) >= 3,
                                primary key(_id)
                              )""");

                     con.exec("""
                                insert into A(_id, a, b, e, h)
                                       values(newid(), 1, 2, 5, 'one'),
                                             (newid(), 2, 3, 7, 'two'),
                                """);

                     Result rs = con.exec("select * from A");
                     assertEquals(rs.resultAttributes().size(), 4);
                     assertEquals(rs.resultAttributes().get("name"), "A");
                     assertEquals(rs.resultAttributes().get("description"), "A test table");
                     assertTrue(new JSONArray(singletonList("_id")).similar(rs.resultAttributes().get(PRIMARY_KEY)));
                     assertTrue(new JSONArray(
                       List.of(
                         p.parseExpression("$(e >= 5)").translate(ESQL),
                         p.parseExpression("$(e <= 100)").translate(ESQL),
                         p.parseExpression("$(length(h) >= 3)").translate(ESQL)
                       )
                     ).similar(rs.resultAttributes().get(CHECK)));
                   }

                   assertThrows(RuntimeException.class, () -> {
                     try (EsqlConnection c = db.esql(db.pooledConnection())) {
                       c.exec("""
                                insert into A(_id, a, b, e, h)
                                values(newid(), 1, 2, 1, 'one')""");
                     }
                   });

                   assertThrows(RuntimeException.class, () -> {
                     try (EsqlConnection c = db.esql(db.pooledConnection())) {
                       c.exec("""
                                insert into A(_id, a, b, e, h)
                                values(newid(), 1, 2, 105, 'one')""");
                     }
                   });

                   assertThrows(RuntimeException.class, () -> {
                     try (EsqlConnection c = db.esql(db.pooledConnection())) {
                       c.exec("""
                                insert into A(_id, a, b, e, h)
                                values(newid(), 1, 2, 10, 'on')""");
                     }
                   });
                 }));
  }

  private static ForeignKeyConstraint dep(String sourceTable, String sourceColumn,
                                          String targetTable, String targetColumn) {
    return new ForeignKeyConstraint(
        null, null, sourceTable,
        singletonList(sourceColumn), targetTable, singletonList(targetColumn),
        1, 1, null, null, false);
  }
}
