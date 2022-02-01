/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.exec;

import ma.vi.esql.DataTest;
import ma.vi.esql.syntax.Parser;
import ma.vi.esql.syntax.Program;
import ma.vi.esql.syntax.expression.Expression;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

import static ma.vi.esql.syntax.Parser.Rules.EXPRESSION;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class UserDefFunctionTest extends DataTest {
  @TestFactory
  Stream<DynamicTest> noParamFuncParseTest() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   Parser p = new Parser(db.structure());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     Expression<?, ?> expr = p.parse("""
                             function xyz(): int
                               select * from S;
                               insert into a.b.T(a, b) values(1, 2);
                             end
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
                     Expression<?, ?> expr = p.parse("""
                             function _x.y.xyz(a:int, b:double, c:money[]): string[]
                               let x := select * from S where a < :c;
                               insert into a.b.T(a, b) values(1, 2);
                               let z := a + b;
                               z := z + 2 ^ z;
                               _x.y.xyz(x, b, z);
                               return z + x;
                             end
                             """, EXPRESSION);
                     System.out.println(expr);
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> execSimpleFunction() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   Parser p = new Parser(db.structure());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     Program prog = p.parse("""
                             function simp(b: int): int
                               let x := 100;
                               x := x + b;
                               return x;
                             end;
                             let y := simp(5);
                             return y;
                             """);
                     long value = con.exec(prog);
                     Assertions.assertEquals(105L, value);

                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> execRecursiveFunction() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   Parser p = new Parser(db.structure());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     Program prog = p.parse("""
                             function factorial(n: int): int
                               return 1 if n=0 else n * factorial(n-1);
                             end;
                             return factorial(5);
                             """);
                     long value = con.exec(prog);
                     System.out.println(prog);
                     Assertions.assertEquals(120L, value);
                   }
                 }));
  }
}
