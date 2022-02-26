/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.query;

import ma.vi.esql.DataTest;
import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.exec.Result;
import ma.vi.esql.exec.ResultTransformer;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class ResultTransformerTest extends DataTest {
  static class TestResultTransformer implements ResultTransformer<S> {
    @Override
    public Iterable<S> transform(Result rs) {
      return new TransIterator(rs);
    }

    private static class TransIterator implements Iterable<S>, Iterator<S> {
      @Override
      public Iterator<S> iterator() {
        return this;
      }

      @Override
      public boolean hasNext() {
        return rs.hasNext();
      }

      @Override
      public S next() {
        if (lastS == null) {
          rs.next();
          List<T> ts = new ArrayList<>();
          lastS = new S(rs.value("s_id"),
                        rs.value("a"),
                        rs.value("b"),
                        rs.value("e"),
                        rs.value("h"),
                        rs.value("j"),
                        ts);
          ts.add(new T(rs.value("ta"),
                       rs.value("tb"),
                       lastS));
        }
        S s = lastS;
        while(rs.hasNext()) {
          rs.next();
          UUID id = rs.value("s_id");
          if (lastS.id().equals(id)) {
            lastS.t().add(new T(rs.value("ta"),
                                rs.value("tb"),
                                lastS));
          } else {
            List<T> ts = new ArrayList<>();
            lastS = new S(rs.value("s_id"),
                          rs.value("a"),
                          rs.value("b"),
                          rs.value("e"),
                          rs.value("h"),
                          rs.value("j"),
                          ts);
            ts.add(new T(rs.value("ta"),
                         rs.value("tb"),
                         lastS));
            break;
          }
        }
        return s;
      }

      private TransIterator(Result rs) {
        this.rs = rs;
      }

      private S lastS;
      private final Result rs;
    }
  }

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

                     for (S s: con.exec("""
                               select ta:t.a, tb:t.b, t.s_id, s.a, s.b, s.e, s.h, s.j
                                 from t:a.b.T
                                 join s:S on t.s_id=s._id
                                order by t.s_id
                               """, new TestResultTransformer())) {
                       System.out.println(s);
                     }
//                     printResult(rs, 20);
                   }
                 }));
  }
}

record T(int a, int b, S s) {
  public T s(S s) {
    return new T(a, b, s);
  }

  @Override public String toString() {
    return "T{ a=" + a + ", b=" + b + " }";
  }
}

record S(UUID id, int a, int b, boolean e, String[] h, Integer[] j, List<T> t) {
  @Override public String toString() {
    return "S{ a=" + a
         +  ", b=" + b
         +  ", e=" + e
         +  ", h=" + Arrays.toString(h)
         +  ", j=" + Arrays.toString(j)
         +  ", t=" + t
         +  " }";
  }
}
