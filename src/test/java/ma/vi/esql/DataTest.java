/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql;

import ma.vi.esql.database.Database;
import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.exec.Result;
import ma.vi.esql.parser.Parser;
import ma.vi.esql.parser.Program;
import org.junit.jupiter.api.BeforeAll;

import static org.apache.commons.lang3.StringUtils.*;

public class DataTest {
  public static Database[] databases;

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
        Program s = p.parse("create table S drop undefined(" +
                                "  {" +
                                "    name: 'S'," +
                                "    description: 'S test table'," +
                                "    tm1: (max(b) from S)," +
                                "    tm2: a > b" +
                                "  }, " +
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
                                "  d=b+c {" +
                                "    m1: 10" +
                                "  }," +
                                "  e bool {" +
                                "    m1: c" +
                                "  }," +
                                "  f=(max(a) from S) {" +
                                "    m1: (min(a) from S)" +
                                "  }," +
                                "  g=(distinct c from S where d>5) {" +
                                "    m1: (min(a) from a.b.T)" +
                                "  }," +
                                "  h text[] {" +
                                "    m1: 5" +
                                "  }," +
                                "  i string," +
                                "  j int[], " +
                                "  k interval, " +
                                "  l int, " +
                                "  primary key(_id)" +
                                ")");
        con.exec(s);

        s = p.parse("create table a.b.T drop undefined(" +
                    "  {" +
                    "    name: 'T'," +
                    "    description: 'T test table'," +
                    "    tm1: (max(b) from a.b.T)," +
                    "    tm2: a > b" +
                    "  }, " +
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
                    "    description: 'X test table'," +
                    "    tm1: (max(b) from a.b.X)," +
                    "    tm2: a > b" +
                    "  }, " +
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
                    "    description: 'Y test table'," +
                    "    tm1: (max(b) from b.Y)," +
                    "    tm2: a > b" +
                    "  }, " +
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
    boolean first = true;
    while(rs.next()) {
      if (first) {
        System.out.println('+' + repeat(repeat('-', columnWidth) + '+', rs.columns()));
        System.out.print('|');
        for (int i = 0; i < rs.columns(); i++) {
          System.out.print(rightPad(rs.column(i + 1).alias(), columnWidth) + '|');
        }
        System.out.println();
        System.out.println('+' + repeat(repeat('-', columnWidth) + '+', rs.columns()));
        first = false;
      }
      System.out.print('|');
      for (int i = 0; i < rs.columns(); i++) {
        Object value = rs.value(i + 1);
        if (value == null) {
          System.out.print(repeat(' ', columnWidth) + '|');
        } else if (value instanceof Number) {
          System.out.print(leftPad(value.toString(), columnWidth) + '|');
        } else {
          System.out.print(rightPad(value.toString(), columnWidth) + '|');
        }
      }
      System.out.println();
    }
    if (!first) {
      System.out.println('+' + repeat(repeat('-', columnWidth) + '+', rs.columns()));
    }
  }
}
