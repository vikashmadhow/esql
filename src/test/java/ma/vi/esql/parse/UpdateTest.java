package ma.vi.esql.parse;

import ma.vi.esql.DataTest;
import ma.vi.esql.Databases;
import ma.vi.esql.database.Database;
import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.exec.Result;
import ma.vi.esql.parser.Parser;
import ma.vi.esql.parser.Translatable;
import ma.vi.esql.parser.TranslationException;
import ma.vi.esql.parser.modify.Update;
import ma.vi.esql.parser.query.QueryTranslation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.UUID;
import java.util.stream.Stream;

import static ma.vi.esql.parser.Parser.Rules.UPDATE;
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
                              "  from usr:_platform.user.User" +
                              "   set username='xyz', " +
                              "       realname='yxz' " +
                              " where email='xyz@yxz.com'", UPDATE);
      QueryTranslation q = s.translate(db.target());
      Assertions.assertEquals(
          "update \"_platform.user\".\"User\" \"usr\" " +
              "set \"username\"='xyz', \"realname\"='yxz' " +
              "where (\"email\" = 'xyz@yxz.com')",
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
                              "  from usr:_platform.user.User" +
                              "   set username='xyz', " +
                              "       realname='yxz' " +
                              " where email='xyz@yxz.com'", UPDATE);
      QueryTranslation q = s.translate(db.target());
      Assertions.assertEquals(
          "update \"usr\" " +
              "set \"username\"=N'xyz', \"realname\"=N'yxz' " +
              "from \"_platform.user\".\"User\" \"usr\" " +
              "where (\"email\" = N'xyz@yxz.com')",
          q.statement);
      con.exec(s);
    }
  }

  @Test
  void simpleUpdateHSqlDb() {
    Database db = Databases.HSqlDb();
    Parser p = new Parser(db.structure());
    try (EsqlConnection con = db.esql(db.pooledConnection())) {
      Update s = p.parse("update usr " +
                              "  from usr:_platform.user.User" +
                              "   set username='xyz', " +
                              "       realname='yxz' " +
                              " where email='xyz@yxz.com'", UPDATE);
      QueryTranslation q = s.translate(db.target());
      Assertions.assertEquals(
          "update \"_platform.user\".\"User\" \"usr\" " +
              "set \"username\"='xyz', \"realname\"='yxz' " +
              "where (\"email\" = 'xyz@yxz.com')",
          q.statement);
      con.exec(s);
    }
  }

  @Test
  void multiUpdatePostgresql() {
    Database db = Databases.Postgresql();
    Parser p = new Parser(db.structure());
    try (EsqlConnection con = db.esql(db.pooledConnection())) {
      Update s = p.parse("update usr " +
                             "  from usr:_platform.user.User" +
                             "  join usr_role:_platform.user.UserRole on usr_role.user_id=usr._id" +
                             "  join role:_platform.user.Role on usr_role.role_id=role._id" +
                             "   set username='xyz', " +
                             "       realname='yxz' " +
                             " where email='xyz@yxz.com'", UPDATE);

      QueryTranslation q = s.translate(db.target());
      Assertions.assertEquals(
          "with \"!!\"(id, v1, v2) as (" +
                    "select \"usr\".ctid, 'xyz', 'yxz' " +
                    "from \"_platform.user\".\"User\" \"usr\" " +
                    "join \"_platform.user\".\"UserRole\" \"usr_role\" on (\"usr_role\".\"user_id\" = \"usr\".\"_id\") " +
                    "join \"_platform.user\".\"Role\" \"role\" on (\"usr_role\".\"role_id\" = \"role\".\"_id\") " +
                    "where (\"email\" = 'xyz@yxz.com')) " +
                  "update \"_platform.user\".\"User\" \"usr\" " +
                  "set username=\"!!\".v1, realname=\"!!\".v2 " +
                  "from \"!!\" where \"usr\".ctid=\"!!\".id",
          q.statement);
      con.exec(s);

      s = p.parse("update usr_role " +
                  "  from usr:_platform.user.User" +
                  "  join usr_role:_platform.user.UserRole on usr_role.user_id=usr._id" +
                  "  join role:_platform.user.Role on usr_role.role_id=role._id" +
                  "   set role_id=role._id " +
                  " where usr.email='xyz@yxz.com'" +
                  "return usr_role.user_id", UPDATE);
      q = s.translate(db.target());
      Assertions.assertEquals(
          "with \"!!\"(id, v1) as (" +
              "select \"usr_role\".ctid, \"role\".\"_id\" " +
              "from \"_platform.user\".\"User\" \"usr\" " +
              "join \"_platform.user\".\"UserRole\" \"usr_role\" on (\"usr_role\".\"user_id\" = \"usr\".\"_id\") " +
              "join \"_platform.user\".\"Role\" \"role\" on (\"usr_role\".\"role_id\" = \"role\".\"_id\") " +
              "where (\"usr\".\"email\" = 'xyz@yxz.com')) " +
              "update \"_platform.user\".\"UserRole\" \"usr_role\" " +
              "set role_id=\"!!\".v1 " +
              "from \"!!\" " +
              "where \"usr_role\".ctid=\"!!\".id " +
              "returning \"usr_role\".\"user_id\" \"user_id\"",
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
