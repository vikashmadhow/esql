package ma.vi.esql.query.define;

import ma.vi.esql.DataTest;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.Result;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.sql.ResultSet;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class SequenceTest extends DataTest {
  @TestFactory
  Stream<DynamicTest> create() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   try (EsqlConnection con = db.esql()) {
                     con.exec("create sequence a.x");
                     ResultSet rs = con.connection().createStatement().executeQuery("select 1 from information_schema.sequences where sequence_schema='a' and sequence_name='x'");
                     assertTrue(rs.next());
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> alter() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   try (EsqlConnection con = db.esql()) {
                     con.exec("create sequence a.x");
                     ResultSet rs = con.connection().createStatement().executeQuery("select 1 from information_schema.sequences where sequence_schema='a' and sequence_name='x'");
                     assertTrue(rs.next());

                     con.exec("alter sequence a.x restart 250 increment 2 minimum 250 maximum 300 cycle");
                     rs = con.connection().createStatement()
                             .executeQuery("""
                                           select start_value, minimum_value, maximum_value, increment, cycle_option
                                             from information_schema.sequences
                                            where sequence_schema='a' and sequence_name='x'""");
                     assertTrue(rs.next());
                     assertEquals(250,   rs.getLong(1));
                     assertEquals(250,   rs.getLong(2));
                     assertEquals(300,   rs.getLong(3));
                     assertEquals(2,     rs.getLong(4));
                     String cycle = rs.getString(5);
                     assertTrue(cycle.equals("YES") || cycle.equals("1"));
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> drop() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("create sequence a.b.y");
                     ResultSet rs = con.connection().createStatement().executeQuery("select 1 from information_schema.sequences where sequence_schema='a.b' and sequence_name='y'");
                     assertTrue(rs.next());

                     con.exec("drop sequence a.b.y");
                     rs = con.connection().createStatement().executeQuery("select 1 from information_schema.sequences where sequence_schema='a.b' and sequence_name='y'");
                     assertFalse(rs.next());
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> nextValue() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   try (EsqlConnection con = db.esql()) {
                     con.exec("drop sequence a.x");
                     con.exec("create sequence a.x start 250 increment 2 minimum 250 maximum 259 cycle");

                     Result rs = con.exec("select nextvalue('a.x')");
                     assertTrue(rs.toNext()); assertEquals(250L, (Long)rs.value(1));
                     rs = con.exec("select nextvalue('a.x')");
                     assertTrue(rs.toNext()); assertEquals(252L, (Long)rs.value(1));
                     rs = con.exec("select nextvalue('a.x')");
                     assertTrue(rs.toNext()); assertEquals(254L, (Long)rs.value(1));
                     rs = con.exec("select nextvalue('a.x')");
                     assertTrue(rs.toNext()); assertEquals(256L, (Long)rs.value(1));
                     rs = con.exec("select nextvalue('a.x')");
                     assertTrue(rs.toNext()); assertEquals(258L, (Long)rs.value(1));
                     rs = con.exec("select nextvalue('a.x')");
                     assertTrue(rs.toNext()); assertEquals(250L, (Long)rs.value(1));
                   }
                 }));
  }

}
