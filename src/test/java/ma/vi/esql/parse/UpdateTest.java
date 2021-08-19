package ma.vi.esql.parse;

import ma.vi.esql.DataTest;
import ma.vi.esql.Databases;
import ma.vi.esql.database.Database;
import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.exec.Result;
import ma.vi.esql.syntax.Parser;
import ma.vi.esql.syntax.Translatable;
import ma.vi.esql.syntax.TranslationException;
import ma.vi.esql.syntax.modify.Update;
import ma.vi.esql.syntax.query.QueryTranslation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.UUID;
import java.util.stream.Stream;

import static ma.vi.esql.syntax.Parser.Rules.UPDATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class UpdateTest extends DataTest {
  @Test
  void simpleUpdatePostgresql() {
    Database db = Databases.Postgresql();
    Parser p = new Parser(db.structure());
    try (EsqlConnection con = db.esql(db.pooledConnection())) {
      Update s = p.parse("update usr " +
                              "  from usr:S" +
                              "   set a=1, " +
                              "       i='yxz' " +
                              " where i='xyz@yxz.com'", UPDATE);
      QueryTranslation q = s.translate(db.target());
      Assertions.assertEquals(
          "update \"public\".\"S\" \"usr\" " +
              "set \"a\"=1, \"i\"='yxz' " +
              "where (\"i\" = 'xyz@yxz.com')",
          q.statement);
      con.exec(s);
    }
  }

  @Test
  void simpleUpdateSqlServer() {
    Database db = Databases.SqlServer();
    Parser p = new Parser(db.structure());
    try (EsqlConnection con = db.esql(db.pooledConnection())) {
      Update s = p.parse("update usr " +
                              "  from usr:S" +
                              "   set a=1, " +
                              "       i='yxz' " +
                              " where i='xyz@yxz.com'", UPDATE);
      QueryTranslation q = s.translate(db.target());
      Assertions.assertEquals(
          "update \"usr\" " +
              "set \"a\"=1, \"i\"=N'yxz' " +
              "from \"DBO\".\"S\" \"usr\" " +
              "where (\"i\" = N'xyz@yxz.com')",
          q.statement);
      con.exec(s);
    }
  }

//  @Test
//  void simpleUpdateHSqlDb() {
//    Database db = Databases.HSqlDb();
//    Parser p = new Parser(db.structure());
//    try (EsqlConnection con = db.esql(db.pooledConnection())) {
//      Update s = p.parse("update usr " +
//                              "  from usr:_platform.user.User" +
//                              "   set username='xyz', " +
//                              "       realname='yxz' " +
//                              " where email='xyz@yxz.com'", UPDATE);
//      QueryTranslation q = s.translate(db.target());
//      Assertions.assertEquals(
//          "update \"_platform.user\".\"User\" \"usr\" " +
//              "set \"username\"='xyz', \"realname\"='yxz' " +
//              "where (\"email\" = 'xyz@yxz.com')",
//          q.statement);
//      con.exec(s);
//    }
//  }

  @Test
  void multiUpdatePostgresql() {
    Database db = Databases.Postgresql();
    Parser p = new Parser(db.structure());
    try (EsqlConnection con = db.esql(db.pooledConnection())) {
      Update s = p.parse("update usr " +
                             "  from usr:S" +
                             "  join usr_role:a.b.T on usr_role.s_id=usr._id" +
                             "  join role:a.b.X on usr_role._id=role.t_id" +
                             "   set a=1, " +
                             "       i='yxz' " +
                             " where i='xyz@yxz.com'", UPDATE);

      QueryTranslation q = s.translate(db.target());
      Assertions.assertEquals(
          "with \"!!\"(id, v1, v2) as (" +
                    "select \"usr\".ctid, 1, 'yxz' " +
                    "from \"public\".\"S\" \"usr\" " +
                    "join \"a.b\".\"T\" \"usr_role\" on (\"usr_role\".\"s_id\" = \"usr\".\"_id\") " +
                    "join \"a.b\".\"X\" \"role\" on (\"usr_role\".\"_id\" = \"role\".\"t_id\") " +
                    "where (\"i\" = 'xyz@yxz.com')) " +
                  "update \"public\".\"S\" \"usr\" " +
                  "set a=\"!!\".v1, i=\"!!\".v2 " +
                  "from \"!!\" where \"usr\".ctid=\"!!\".id",
          q.statement);
      con.exec(s);

      s = p.parse("update usr_role " +
                      "  from usr:S" +
                      "  join usr_role:a.b.T on usr_role.s_id=usr._id" +
                      "  join role:a.b.X on usr_role._id=role.t_id" +
                      "   set a=usr.b " +
                      " where usr.i='xyz@yxz.com'" +
                  "return usr_role.s_id", UPDATE);
      q = s.translate(db.target());
      Assertions.assertEquals(
          "with \"!!\"(id, v1) as (" +
              "select \"usr_role\".ctid, \"usr\".\"b\" " +
              "from \"public\".\"S\" \"usr\" " +
              "join \"a.b\".\"T\" \"usr_role\" on (\"usr_role\".\"s_id\" = \"usr\".\"_id\") " +
              "join \"a.b\".\"X\" \"role\" on (\"usr_role\".\"_id\" = \"role\".\"t_id\") " +
              "where (\"usr\".\"i\" = 'xyz@yxz.com')) " +
              "update \"a.b\".\"T\" \"usr_role\" " +
              "set a=\"!!\".v1 " +
              "from \"!!\" where \"usr_role\".ctid=\"!!\".id " +
              "returning \"usr_role\".\"s_id\" \"s_id\"",
          q.statement);
      con.exec(s);
    }
  }

  @TestFactory
  Stream<DynamicTest> updateUsingMultipleTables() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("delete T from a.b.T");
                     con.exec("delete s from s:S");

                     UUID id1 = UUID.randomUUID(), id2 = UUID.randomUUID();
                     con.exec("insert into S(_id, a, b, e, h, j) values "
                            + "(u'" + id1 + "', 1, 2, true, text['Four', 'Quatre'], int[1, 2, 3]),"
                            + "(u'" + id2 + "', 6, 7, false, text['Nine', 'Neuf', 'X'], int[5, 6, 7, 8])");

                     con.exec("insert into a.b.T(_id, a, b, s_id) "
                                  + "select newid(), a, 1, u'" + id1 + "' from S union all "
                                  + "select newid(), a, 2, u'" + id2 + "' from S ");

                     Result rs = con.exec("select _id, a, b, s_id from a.b.T order by b, a");
                     rs.next(); assertEquals(rs.get("a").value, 1);
                                assertEquals(rs.get("b").value, 1);
                                assertEquals(rs.get("s_id").value, id1);
                     rs.next(); assertEquals(rs.get("a").value, 6);
                                assertEquals(rs.get("b").value, 1);
                                assertEquals(rs.get("s_id").value , id1);
                     rs.next(); assertEquals(rs.get("a").value, 1);
                                assertEquals(rs.get("b").value, 2);
                                assertEquals(rs.get("s_id").value, id2);
                     rs.next(); assertEquals(rs.get("a").value, 6);
                                assertEquals(rs.get("b").value, 2);
                                assertEquals(rs.get("s_id").value , id2);

                     if (db.target() == Translatable.Target.HSQLDB) {
                       assertThrows(TranslationException.class, () ->
                           con.exec("update s " +
                                    "  from s:S " +
                                    "  join t:a.b.T on t.s_id=s._id " +
                                    "   set a=t.a + t.b" +
                                    " where t.a=6 and t.b=2"));
                     } else {
                       con.exec("update s " +
                                    "  from s:S " +
                                    "  join t:a.b.T on t.s_id=s._id " +
                                    "   set s.a=t.a + t.b" +
                                    " where t.a=6 and t.b=2");
                       rs = con.exec("select sum(a) from S");
                       rs.next();
                       assertEquals(9L, ((Number)rs.get(1).value).longValue());
                     }
                   }
                 }));
  }
}
