package ma.vi.esql.query.define;

import ma.vi.esql.DataTest;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.Result;
import ma.vi.esql.syntax.Parser;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class CreateTableTest extends DataTest {
  @TestFactory
  Stream<DynamicTest> create() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   Parser p = new Parser(db.structure());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("drop table A");
                     con.exec("""
                                   create table A drop undefined(
                                     {
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
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> alterTableAddColumn() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   Parser p = new Parser(db.structure());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("drop table test.CreateA");
                     con.exec("""
                              create table test.CreateA drop undefined(
                                _id uuid not null,
                                a int {
                                  m1: b > 5,
                                  m2: 10,
                                  m3: a != 0
                                },
                                b int {
                                  m1: b < 0
                                },
                                d=a+b {
                                  m1: 10
                                },
                                e int {
                                  m1: b,
                                  "values": {"any": {en: 'Any', fr: 'Une ou plusieurs'}, "all": {en: 'All', fr: 'Toutes'}}
                                },
                                h text {
                                  m1: 5
                                },
                                i string,
                                primary key(_id)
                              )""");

                     Result rs = con.exec("""
                                          select name, seq
                                            from _core.columns
                                           where relation_id=(select _id from _core.relations where name='test.CreateA')
                                           order by seq""");
                     matchResult(rs, List.of(Map.of("name", "_id", "seq", 1),
                                             Map.of("name", "a", "seq", 2),
                                             Map.of("name", "b", "seq", 3),
                                             Map.of("name", "d", "seq", 4),
                                             Map.of("name", "e", "seq", 5),
                                             Map.of("name", "h", "seq", 6),
                                             Map.of("name", "i", "seq", 7)));

                     con.exec("""
                               create table test.CreateA drop undefined(
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
                                 primary key(_id)
                               )""");

                     rs = con.exec("""
                                   select name, seq
                                     from _core.columns
                                    where relation_id=(select _id from _core.relations where name='test.CreateA')
                                    order by seq""");
                     matchResult(rs, List.of(Map.of("name", "_id", "seq", 1),
                                             Map.of("name", "a", "seq", 2),
                                             Map.of("name", "b", "seq", 3),
                                             Map.of("name", "c", "seq", 4),
                                             Map.of("name", "d", "seq", 5),
                                             Map.of("name", "e", "seq", 6),
                                             Map.of("name", "f", "seq", 7),
                                             Map.of("name", "g", "seq", 8),
                                             Map.of("name", "h", "seq", 9),
                                             Map.of("name", "i", "seq", 10)));
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> alterType() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   Parser p = new Parser(db.structure());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("drop table A");
                     con.exec("""
                              create table A drop undefined(
                              {
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
                              primary key(_id))""");
                   }
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("drop table A");
                     con.exec("""
                              create table A drop undefined(
                              {
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
                              h []text {
                                m1: 5
                              },
                              i []string,
                              check e >= 5,
                              check e <= 100,
                              primary key(_id))""");
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> allowInvalidUncomputedExpression() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     // con.exec("drop table test.X");
                     con.exec("""
                            create table test.X drop undefined({
                                xc: 'Result Metadata'
                              }
                              _id uuid not null,
                              a int {
                                m1: 1
                              },
                              b int not null {
                                m1: 'abc',
                                m2: b + c,
                                m3: 2 * b,
                                m4: $(a.m1 * 5)
                              },
                              c int default 5,
                              d int,
                              e = b+c+d {
                                m3: 10,
                                "values": {"any": {en: 'Any', fr: 'Une ou plusieurs'}, "all": {en: 'All', fr: 'Toutes'}}
                              },
                              primary key(_id),
                              unique(a),
                              unique(a, b),
                              unique(b, c, d)
                            )""");

                     con.exec("delete x from test.X");
                     con.exec("""
                              insert into test.X(_id, a, b, c, d)
                              values (newid(), 1, 2, 3, 4),
                                     (newid(), 5, 6, 7, 8),
                                     (newid(), 3, 4, default, 5)
                              """);

                     Result rs = con.exec("select * from t:test.X order by a");
                     printResult(rs, 20, true);
                   }
                 }));
  }

}
