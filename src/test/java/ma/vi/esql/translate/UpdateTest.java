package ma.vi.esql.translate;

import ma.vi.esql.Databases;
import ma.vi.esql.database.Database;
import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.parser.Parser;
import ma.vi.esql.parser.modify.Update;
import ma.vi.esql.parser.query.QueryTranslation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static ma.vi.esql.parser.Parser.Rules.UPDATE;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class UpdateTest {
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
}
