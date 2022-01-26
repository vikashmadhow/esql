/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.exec;

import ma.vi.esql.DataTest;
import ma.vi.esql.syntax.Parser;
import ma.vi.esql.syntax.expression.Expression;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

import static ma.vi.esql.syntax.Parser.Rules.EXPRESSION;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class FunctionTest extends DataTest {
  @TestFactory
  Stream<DynamicTest> noParamFuncParseTest() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   Parser p = new Parser(db.structure());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     Expression<?,?> expr = p.parse("""
                             function xyz() : int {
                               select * from S;
                               insert into a.b.T(a, b) values(1, 2);
                             }
                             """, EXPRESSION);
                     System.out.println(expr);
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> withParamsFuncParseTest() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   Parser p = new Parser(db.structure());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     Expression<?,?> expr = p.parse("""
                             function _x.y.xyz(a:int, b:double, c:money[]) : string[] {
                               let x := select * from S where y < :c;
                               insert into a.b.T(a, b) values(1, 2);
                               let z := a + b;
                               z := z + 2 ^ z;
                               _x.y.xyz(x, b, z);
                               return z + x;
                             }
                             """, EXPRESSION);
                     System.out.println(expr);
                   }
                 }));
  }
}
