/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parse;

import ma.vi.esql.DataTest;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.database.Structure;
import ma.vi.esql.semantic.type.SimpleColumn;
import ma.vi.esql.semantic.type.Struct;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.Parser;
import ma.vi.esql.syntax.query.Select;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static ma.vi.esql.syntax.Parser.Rules.SELECT;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class ParseTest extends DataTest {
  @TestFactory
  Stream<DynamicTest> basic() {
    return Stream.of(databases)
      .map(db -> dynamicTest(db.target().toString(), () -> {
        Structure structure = db.structure();
        Parser parser = new Parser(structure);

        Esql<?, ?> esql = parser.parse("select * from S");
        assertNotNull(esql);
      }));
  }

  @TestFactory
  Stream<DynamicTest> simpleColumns() {
    return Stream.of(databases)
      .map(db -> dynamicTest(db.target().toString(), () -> {
        Structure structure = db.structure();
        List<SimpleColumn> columns = structure.relation("S").cols();
        for (SimpleColumn c: columns) System.out.println(c);
      }));
  }

  @TestFactory
  Stream<DynamicTest> simpleColumnsFromStruct() {
    return Stream.of(databases)
      .map(db -> dynamicTest(db.target().toString(), () -> {
        try(EsqlConnection con = db.esql()) {
          con.exec("drop struct str.A");
          con.exec("""
                   create struct str.A({
                       name: 'A',
                       description: 'A test structure'
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
                     f=from S select max(a) {
                       m1: from S select min(a)
                     },
                     g=from S select distinct c where d>5 {
                       m1: d
                     },
                     h text {
                       m1: 5
                     },
                     i string
                   )""");
        }
        Struct s = db.structure().struct("str.A");
        List<SimpleColumn> columns = s.cols();
        for (SimpleColumn c: columns) System.out.println(c);

        SimpleColumn c = columns.get(0);
        assertEquals("_id", c.name());
        assertEquals("uuid", c.type());
        assertFalse(c.derived());
        assertTrue(c.notNull());
        assertEquals("uuid", c.attributes().get("_type"));
        assertEquals(true, c.attributes().get("required"));

        c = columns.get(1);
        assertEquals("a", c.name());
        assertEquals("int", c.type());
        assertFalse(c.derived());
        assertFalse(c.notNull());
        assertEquals("int", c.attributes().get("_type"));
        assertEquals("$(row.b.$v > 5)", c.attributes().get("m1"));
        assertEquals(10L, c.attributes().get("m2"));
        assertEquals("$(row.a.$v !== 0)", c.attributes().get("m3"));
      }));
  }

  @TestFactory
  Stream<DynamicTest> rangeAndConditional() {
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
