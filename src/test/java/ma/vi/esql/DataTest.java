/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql;

import ma.vi.esql.database.Database;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.ColumnMapping;
import ma.vi.esql.exec.Result;
import ma.vi.esql.exec.ResultColumn;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.Parser;
import ma.vi.esql.syntax.Program;
import ma.vi.esql.syntax.expression.literal.DateLiteral;
import ma.vi.esql.syntax.expression.literal.Literal;
import ma.vi.esql.translation.Translatable;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.pcollections.HashPMap;
import org.pcollections.IntTreePMap;

import java.util.*;
import java.util.stream.Collectors;

import static ma.vi.esql.database.Database.NULL_DB;
import static ma.vi.esql.database.EsqlConnection.NULL_CONNECTION;
import static org.apache.commons.lang3.StringUtils.repeat;
import static org.apache.commons.lang3.StringUtils.rightPad;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataTest {
  public static Database[] databases;

  public static class IsEqualToJsonArray extends BaseMatcher<JSONArray> {
    public IsEqualToJsonArray(JSONArray expected) {
      this.expected = expected;
    }

    @Override
    public boolean matches(Object actual) {
      if (actual instanceof JSONArray arr) {
        return arr.similar(expected);
      } else {
        return false;
      }
    }

    @Override
    public void describeTo(Description description) {
      description.appendValue(expected);
    }

    private final JSONArray expected;
  }

  public static class IsEqualToJsonObject extends BaseMatcher<JSONArray> {
    public IsEqualToJsonObject(JSONObject expected) {
      this.expected = expected;
    }

    @Override
    public boolean matches(Object actual) {
      if (actual instanceof JSONObject obj) {
        return obj.similar(expected);
      } else {
        return false;
      }
    }

    @Override
    public void describeTo(Description description) {
      description.appendValue(expected);
    }

    private final JSONObject expected;
  }

  @BeforeAll
  static void setup() {
    databases = new Database[] {
        Databases.Postgresql(),
        Databases.SqlServer(),
//        Databases.HSqlDb(),
//        Databases.MariaDb(),
    };

    for (Database db: databases) {
      System.out.println(db.target());
      Parser p = new Parser(db.structure());
      try (EsqlConnection con = db.esql(db.pooledConnection())) {
        Program s = p.parse("""
                              create table S drop undefined({
                                name: 'S',
                                description: 'S test table',
                                validate_unique: [['a', 'b', 'e']],
                                history: true,
                                dependents: {
                                  links: {
                                    "type": 'a.b.T',
                                    _referred_by: 's_id',
                                    label: 'S Links'
                                  }
                                }
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
                              e bool {
                                m1: c
                              },
                              f=from S select max(a) {
                                m1: from S select min(a)
                              },
                              g=from S select distinct a+b where d>5 {
                                m1: from a.b.T select min(a)
                              },
                              h []text {
                                m1: 5
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
        con.exec(s);

        s = p.parse("create table a.b.T drop undefined(" +
                    "  {" +
                    "    name: 'T'," +
                    "    description: 'T test table', " +
                    "    history: true " +
                    "  } " +
                    "  _id uuid not null," +
                    "  a int {" +
                    "    m1: b > 5," +
                    "    m2: 10," +
                    "    m3: a != 0" +
                    "  }," +
                    "  b int {" +
                    "    m1: b < 0" +
                    "  }," +
                    "  c=a+b {" +
                    "    m1: a > 5," +
                    "    m2: a + b," +
                    "    m3: b > 5" +
                    "  }," +
                    "  x int {" +
                    "    x1: b > 5," +
                    "    x2: a != 0" +
                    "  }," +
                    "  y int {" +
                    "    y1: b > 5," +
                    "    y2: a != 0" +
                    "  }," +
                    "  s_id uuid {" +
                    "    link_table: 'S', " +
                    "    link_table_code: '_id', " +
                    "    link_table_label: 'a' " +
                    "  }," +
                    "  foreign key(s_id) references S(_id)," +
                    "  primary key(_id)" +
                    ")");
        con.exec(s);

        s = p.parse("create table a.b.X drop undefined(" +
                    "  {" +
                    "    name: 'X'," +
                    "    description: 'X test table'" +
                    "  } " +
                    "  _id uuid not null," +
                    "  a int {" +
                    "    m1: b > 5," +
                    "    m2: 10," +
                    "    m3: a != 0" +
                    "  }," +
                    "  b int {" +
                    "    m1: b < 0" +
                    "  }," +
                    "  c=a+b {" +
                    "    m1: a > 5," +
                    "    m2: a + b," +
                    "    m3: b > 5" +
                    "  }," +
                    "  s_id uuid {" +
                    "    link_table: 'S', " +
                    "    link_table_code: '_id', " +
                    "    link_table_label: 'a' " +
                    "  }," +
                    "  t_id uuid {" +
                    "    link_table: 'a.b.T', " +
                    "    link_table_code: '_id', " +
                    "    link_table_label: 'b' " +
                    "  }," +
                    "  foreign key(s_id) references S(_id)," +
                    "  foreign key(t_id) references a.b.T(_id) on update cascade on delete cascade," +
                    "  primary key(_id)" +
                    ")");
        con.exec(s);

        s = p.parse("create table b.Y drop undefined(" +
                    "  {" +
                    "    name: 'Y'," +
                    "    description: 'Y test table'" +
                    "  } " +
                    "  _id uuid not null," +
                    "  a int {" +
                    "    m1: b > 5," +
                    "    m2: 10," +
                    "    m3: a != 0" +
                    "  }," +
                    "  b int {" +
                    "    m1: b < 0" +
                    "  }," +
                    "  c=a+b {" +
                    "    m1: a > 5," +
                    "    m2: a + b," +
                    "    m3: b > 5" +
                    "  }," +
                    "  s_id uuid {" +
                    "    link_table: 'S', " +
                    "    link_table_code: '_id', " +
                    "    link_table_label: 'a' " +
                    "  }," +
                    "  t_id uuid {" +
                    "    link_table: 'a.b.T', " +
                    "    link_table_code: '_id', " +
                    "    link_table_label: 'b' " +
                    "  }," +
                    "  x_id uuid {" +
                    "    link_table: 'a.b.X', " +
                    "    link_table_code: '_id', " +
                    "    link_table_label: 'b' " +
                    "  }," +
                    "  foreign key(s_id) references S(_id)," +
                    "  foreign key(t_id) references a.b.T(_id) on update cascade on delete cascade," +
                    "  foreign key(x_id) references a.b.X(_id)," +
                    "  primary key(_id)" +
                    ")");
        con.exec(s);
      }
    }
  }

  public static void printResult(Result rs, int columnWidth) {
    printResult(rs, columnWidth, false);
  }

  public static void printResult(Result rs, int columnWidth, boolean showMetadata) {
    boolean first = true;
    while(rs.toNext()) {
      if (first) {
        System.out.println('+' + repeat(repeat('-', columnWidth) + '+', rs.columnsCount()));
        System.out.print('|');
        for (ColumnMapping col: rs.columns()) {
          System.out.print(rightPad(col.column().name(), columnWidth) + '|');
        }
        System.out.println();
        System.out.println('+' + repeat(repeat('-', columnWidth) + '+', rs.columnsCount()));
        first = false;
      }
      System.out.print('|');
      for (int i = 0; i < rs.columnsCount(); i++) {
        ResultColumn<Object> col = rs.get(i + 1);
        Object value = col.value();
        int spaceLeft = columnWidth;
        if (value != null) {
          spaceLeft -= value.toString().length();
          if (value.getClass().isArray()) {
            System.out.print(Arrays.deepToString((Object[])value));
          } else {
            System.out.print(value);
          }
        }
        if (showMetadata) {
          if (col.metadata() != null && !col.metadata().isEmpty()) {
            for (Map.Entry<String, Object> e: col.metadata().entrySet()) {
              if (e.getValue() != null) {
                spaceLeft -= e.getKey().length() + e.getValue().toString().length() - 2;
                System.out.print(',' + e.getKey() + ':' + e.getValue().toString());
              }
            }
          }
        }
        if (spaceLeft > 0) {
          System.out.print(repeat(' ', spaceLeft));
        }
        System.out.print('|');
      }
      System.out.println();
    }
    if (!first) {
      System.out.println('+' + repeat(repeat('-', columnWidth) + '+', rs.columnsCount()));
    }
  }

  public static void matchResult(Result rs, List<Map<String, Object>> expected) {
    int row = 1;
    for (Map<String, Object> expectedRow: expected) {
      rs.toNext();
      for (Map.Entry<String, Object> col: expectedRow.entrySet()) {
        String c = col.getKey();
        Object expectedVal = col.getValue();
        Object actualVal = rs.value(c);
        if (expectedVal instanceof Date && actualVal instanceof Date) {
          assertEquals(DateLiteral.DateFormat.format(expectedVal),
                       DateLiteral.DateFormat.format(actualVal),
                       "Row " + row + ", column " + c + ", expected " + expectedVal + ", got " + actualVal);
        } else {
          assertEquals(expectedVal, actualVal,
                       "Row " + row + ", column " + c + ", expected " + expectedVal + ", got " + actualVal);
        }
      }
      row++;
    }
  }

  private static String lengthen(String val, int length) {
    return rightPad(val == null ? " " : val, length, " ").substring(0, length);
  }

  public static Object literalValue(Literal<?> literal) {
    return literal.exec(Translatable.Target.JAVASCRIPT,
                        NULL_CONNECTION,
                        new EsqlPath(literal),
                        HashPMap.empty(IntTreePMap.empty()),
                        NULL_DB.structure());
  }

  public static Map<String, Object> literalValueMap(Map<String, Object> attributes) {
    return attributes.entrySet().stream().collect(
      Collectors.toMap(Map.Entry::getKey,
                       e -> literalValue((Literal<?>)e.getValue()),
                       (k1, k2) -> k1,
                       LinkedHashMap::new));
  }
}
