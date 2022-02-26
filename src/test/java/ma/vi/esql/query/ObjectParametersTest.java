/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.query;

import ma.vi.esql.DataTest;
import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.exec.Result;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class ObjectParametersTest extends DataTest {
  @TestFactory
  Stream<DynamicTest> query() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("delete t from t:a.b.T");
                     con.exec("delete s from s:S");

                     UUID id1 = UUID.fromString("11111111-1111-1111-1111-111111111111"),
                          id2 = UUID.fromString("22222222-2222-2222-2222-222222222222");
                     con.exec("insert into S(_id, a, b, e, h, j) values "
                                  + "(u'" + id1 + "', 1, 2, true, ['Four', 'Quatre']text, [1, 2, 3]int),"
                                  + "(u'" + id2 + "', 6, 7, false, ['Nine', 'Neuf', 'X']text, [5, 6, 7, 8]int)");

                     con.exec("insert into a.b.T(_id, a, b, s_id) values"
                                  + "(newid(), 1, 2, u'" + id1 + "'), "
                                  + "(newid(), 3, 4, u'" + id1 + "'), "
                                  + "(newid(), 5, 6, u'" + id2 + "'), "
                                  + "(newid(), 7, 8, u'" + id2 + "'), "
                                  + "(newid(), 9, 0, u'" + id2 + "')");

                     Result rs = con.exec("""
                               select ta:t.a, tb:t.b, t.s_id, s.a, s.b, s.e, s.h, s.j
                                 from t:a.b.T
                                 join s:S on t.s_id=s._id
                                where a<=@a""", new A(10, "x"));
                     printResult(rs, 20);

                     rs = con.exec("""
                               select ta:t.a, tb:t.b, t.s_id, s.a, s.b, s.e, s.h, s.j
                                 from t:a.b.T
                                 join s:S on t.s_id=s._id
                                where a<=@a""", new B(10, "x"));
                     printResult(rs, 20);

                     rs = con.exec("""
                               select ta:t.a, tb:t.b, t.s_id, s.a, s.b, s.e, s.h, s.j
                                 from t:a.b.T
                                 join s:S on t.s_id=s._id
                                where a<=@a""", new C(10, "x"));
                     printResult(rs, 20);
                   }
                 }));
  }

  public record A(int a, String b) {}

  public static class B {
    public B(int a, String b) {
      this.a = a;
      this.b = b;
    }

    public final int a;
    public final String b;
  }

  public static class C {
    public C(int a, String b) {
      this.a = a;
      this.b = b;
    }

    public int getA() {
      return a;
    }

    public String getB() {
      return b;
    }

    private final int a;
    private final String b;
  }
}


