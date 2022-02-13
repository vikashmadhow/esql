package ma.vi.esql.define;

import ma.vi.base.lang.NotFoundException;
import ma.vi.esql.DataTest;
import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.semantic.type.BaseRelation;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.define.ForeignKeyConstraint;
import ma.vi.esql.syntax.query.Column;
import ma.vi.esql.translation.Translatable;
import org.json.JSONArray;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.List;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;
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
                               new JSONArray(new String[]{"a", "b"}),
                               new JSONArray(new String[]{"b", "c", "d"}),
                           }
                         ).similar(col.expression().exec(Translatable.Target.ESQL, con, new EsqlPath(col), null)));

                     System.out.println(x);
                   }
                 }));
  }

  private static ForeignKeyConstraint dep(String sourceTable, String sourceColumn,
                                          String targetTable, String targetColumn) {
    return new ForeignKeyConstraint(
        null, null, sourceTable,
        singletonList(sourceColumn), targetTable, singletonList(targetColumn),
        1, 1, null, null);
  }
}
