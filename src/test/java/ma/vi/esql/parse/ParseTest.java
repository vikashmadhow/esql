/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parse;

import ma.vi.esql.DataTest;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.syntax.Parser;
import ma.vi.esql.syntax.Program;
import ma.vi.esql.syntax.query.Select;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.UUID;
import java.util.stream.Stream;

import static ma.vi.esql.syntax.Parser.Rules.SELECT;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class ParseTest extends DataTest {

  @TestFactory
  Stream<DynamicTest> rangeAndConditionalTest() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("delete T from a.b.T");
                     con.exec("delete s from s:S");

                     UUID id1 = UUID.randomUUID(), id2 = UUID.randomUUID();
                     con.exec("insert into S(_id, a, b, e, h, j) values "
                                  + "(u'" + id1 + "', 1, 2, true, ['Four', 'Quatre']text, [1, 2, 3]int),"
                                  + "(u'" + id2 + "', 6, 7, false, ['Nine', 'Neuf', 'X']text, [5, 6, 7, 8]int)");

                     con.exec("insert into a.b.T(_id, a, b, s_id) values"
                                  + "(newid(), 1, 2, u'" + id1 + "'), "
                                  + "(newid(), 3, 4, u'" + id2 + "')");

                     Parser p = new Parser(db.structure());
                     Select select = p.parse(
                         "select s.c if 0 < t.b < 5 else s.a, "
                             + "  'A' if t.b < 0 else "
                             + "  'B' if 0 <= t.b < 5 else "
                             + "  'C' if 5 <= t.b < 10 else 'D' "
                             + "  from t:a.b.T join s:S on t.s_id=s._id", SELECT);

                     con.exec(select);

//                     rs.next(); assertEquals(1,  (Integer)rs.value("a"));
//                                assertEquals(2,  (Integer)rs.value("b"));
//                                assertEquals(3,  (Integer)rs.value("c"));
//                     rs.next(); assertEquals(3,  (Integer)rs.value("a"));
//                                assertEquals(4,  (Integer)rs.value("b"));
//                                assertEquals(13, (Integer)rs.value("c"));
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> createTableWithSpuriousComma() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("drop table parse.test.X");

                     con.exec("""
                              create table parse.test.X drop undefined({
                                name: 'S',
                                description: 'S test table',
                                validate_unique: [['a', 'b', 'e']],
                                dependents: {
                                  links: {
                                    "type": 'a.b.T',
                                    _referred_by: 's_id',
                                    label: 'S Links',
                                  }
                                }
                              }
                              _id uuid not null,
                              a int {
                                m1: b > 5,
                                m2: 10,
                                m3: a != 0,
                              },
                              b int {
                                m1: b < 0,
                              },
                              c=a+b {
                                m1: a > 5,
                                m2: a + b,
                                m3: b > 5,
                              },
                              d=b+c {
                                m1: 10,
                              },
                              e bool {
                                m1: c,
                              },
                              f=from S select max(a) {
                                m1: from S select min(a),
                              },
                              g=from S select distinct a+b where d>5 {
                                m1: from a.b.T select min(a),
                              },
                              h []text {
                                m1: 5,
                              },
                              i string default 'Aie',
                              j []int,
                              k interval,
                              l int,
                              m []uuid,
                              n date,
                              o time,
                              p datetime,
                              primary key(_id)
                            )""");
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> changeColumnDerivation() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("drop table parse.test.X");

                     con.exec("""
                              create table parse.test.X drop undefined({
                                name: 'S',
                                description: 'S test table',
                                validate_unique: [['a', 'b', 'e']],
                                dependents: {
                                  links: {
                                    "type": 'a.b.T',
                                    _referred_by: 's_id',
                                    label: 'S Links',
                                  }
                                }
                              }
                              _id uuid not null,
                              a int {
                                m1: b > 5,
                                m2: 10,
                                m3: a != 0,
                              },
                              b int {
                                m1: b < 0,
                              },
                              c=a+b {
                                m1: a > 5,
                                m2: a + b,
                                m3: b > 5,
                              },
                              d=b+c {
                                m1: 10,
                              },
                              e bool {
                                m1: c,
                              },
                              f=from S select max(a) {
                                m1: from S select min(a),
                              },
                              g=from S select distinct a+b where d>5 {
                                m1: from a.b.T select min(a),
                              },
                              h []text {
                                m1: 5,
                              },
                              i string default 'Aie',
                              j []int,
                              k interval,
                              l int,
                              m []uuid,
                              n date,
                              o time,
                              p datetime,
                              primary key(_id)
                            )""");

                     con.exec("""
                              create table parse.test.X drop undefined({
                                name: 'S',
                                description: 'S test table',
                                validate_unique: [['a', 'b', 'e']],
                                dependents: {
                                  links: {
                                    "type": 'a.b.T',
                                    _referred_by: 's_id',
                                    label: 'S Links',
                                  }
                                }
                              }
                              _id uuid not null,
                              a int {
                                m1: b > 5,
                                m2: 10,
                                m3: a != 0,
                              },
                              b = a * 2 {
                                m1: b < 0,
                              },
                              c int {
                                m1: a > 5,
                                m2: a + b,
                                m3: b > 5,
                              },
                              d=b+c {
                                m1: 10,
                              },
                              e bool {
                                m1: c,
                              },
                              f=from S select max(a) {
                                m1: from S select min(a),
                              },
                              g=from S select distinct a+b where d>5 {
                                m1: from a.b.T select min(a),
                              },
                              h []text {
                                m1: 5,
                              },
                              i string default 'Aie',
                              j []int,
                              k interval,
                              l int,
                              m []uuid,
                              n date,
                              o time,
                              p datetime,
                              primary key(_id)
                            )""");
                   }
                 }));
  }
}
