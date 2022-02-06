/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.exec;

import ma.vi.esql.DataTest;
import ma.vi.esql.syntax.Parser;
import ma.vi.esql.syntax.Program;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.translation.TranslationException;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

import static ma.vi.esql.syntax.Parser.Rules.EXPRESSION;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class FunctionDefAndExecTest extends DataTest {
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
                             function _x.y.xyz(a:int, b:double, c:[]money): []string
                               let x := select * from S where a < @c;
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
                     assertEquals(105L, value);

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
                     assertEquals(120L, value);
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> readFromSelect() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   Parser p = new Parser(db.structure());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     Program prog = p.parse("""
                                            delete t from t:a.b.T;
                                            delete s from s:S;
                                            insert into S(_id, a, b, e, h, j) values
                                                         (newid(), 1, @b1, true, ['Four', 'Quatre']text, [1, 2, 3]int),
                                                         (newid(), 6, @b2, false, ['Nine', 'Neuf', 'X']text, [5, 6, 7, 8]int);
                                            let i := 1;
                                            for r in select * from S do
                                              print('_id: ' + r['_id']);
                                              print('a: ' + r['a']);
                                              print('b: ' + r['b']);
                                              print('c: ' + r['c']);
                                              print('e: ' + r['e']);
                                              print('h: ' + r['h']);
                                              print('j: ' + r['j']);
                                              
                                              print('Row: ' + @i);
                                              print('Row: ' + @((i * 100) + '%'));
                                              print('Row: ' + i);
                                              i := i + 1;
                                            end;
                                            """);
                     con.exec(prog, Param.of("b1", 5), Param.of("b2", 9));
                      System.out.println(prog);
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> readFromSelectWithComplexSelection() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   Parser p = new Parser(db.structure());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     Program prog = p.parse("""
                                            delete t from t:a.b.T;
                                            delete s from s:S;
                                            insert into S(_id, a, b, e, h, j) values
                                                         (newid(), 1, @b1, true, ['Four', 'Quatre']text, [1, 2, 3]int),
                                                         (newid(), 6, @b2, false, ['Nine', 'Neuf', 'X']text, [5, 6, 7, 8]int);
                                            let i := 1;
                                            for r in select * from S do
                                              for let c := 1,
                                                  c <= r['columnsCount'],
                                                  c := c + 1 do
                                                let col := r['get'][c];
                                                print(col['column']['name'] + ': ' + col['value']);
                                                print('Counter: ' + i);
                                                i := i + 1;
                                              end;
                                            end;
                                            """);
                     con.exec(prog, Param.of("b1", 6), Param.of("b2", 1));
                     System.out.println(prog);
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> whileLoop() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   Parser p = new Parser(db.structure());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     Program prog = p.parse("""
                                            delete t from t:a.b.T;
                                            delete s from s:S;
                                            insert into S(_id, a, b, e, h, j) values
                                                         (newid(), 1, @b1, true, ['Four', 'Quatre']text, [1, 2, 3]int),
                                                         (newid(), 6, @b2, false, ['Nine', 'Neuf', 'X']text, [5, 6, 7, 8]int);
                                            let i := 1;
                                            for r in select * from S do
                                              let c := 1;
                                              while c <= r['columnsCount'] do
                                                let col := r['get'][c];
                                                print(col['column']['name'] + ': ' + col['value'] + ', counter: ' + i);
                                                i := i + 1;
                                                c := c + 1;
                                              end;
                                              print('  ------------------------  ');
                                            end;
                                            """);
                     con.exec(prog, Param.of("b1", 6), Param.of("b2", 1));
                     System.out.println(prog);
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> ifContinueBreak() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   Parser p = new Parser(db.structure());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     Program prog = p.parse("""
                                            delete t from t:a.b.T;
                                            delete s from s:S;
                                            insert into S(_id, a, b, e, h, j) values
                                                         (newid(), 1, @b1, true, ['Four', 'Quatre']text, [1, 2, 3]int),
                                                         (newid(), 6, @b2, false, ['Nine', 'Neuf', 'X']text, [5, 6, 7, 8]int),
                                                         (newid(), 7, @b2, false, ['A', 'B', 'C']text, [2, 7, 9]int),
                                                         (newid(), 7, @b2, true, ['D', 'E']text, [11]int);
                                            let i := 1;
                                            for r in select * from S do
                                              let c := 1;
                                              while c <= r['columnsCount'] do
                                                if i <= 1 then
                                                  print('Aie');
                                                  if c = 1 then
                                                    print('SEE!');
                                                  end;
                                                
                                                elseif i <= 2 then
                                                  print('Aie Aie');
                                                  print('Muchacho');
                                                  if c != 1 then
                                                    print('NO SEE!');
                                                  else
                                                    print('Me SEE!');
                                                  end;
                                                  
                                                elseif i = 3 then
                                                  print('Aie Aie Aie');
                                                  print('Muchacho!');
                                                  
                                                elseif i = 4 then
                                                  print('Aie Aie Aie Aie');
                                                  i := 6;
                                                  continue;
                                                  
                                                elseif i = 5 then
                                                  print('Aie Aie Aie Aie Aie');
                                                  print('Stop Muchacho');
                                                  break;
                                                  
                                                elseif i = 6 then
                                                  print('Aie Aie Aie Aie Aie Aie');
                                                  print('Please stop Muchacho');
                                                  i := i + 1;
                                                  break;
                                                  
                                                else
                                                  print('Megusta, but you''ll get us killed');
                                                  i := 1;
                                                  continue;
                                                end;
                                                
                                                let col := r['get'][c];
                                                print(col['column']['name'] + ': ' + col['value'] + ', counter: ' + i);
                                                  
                                                i := i + 1;
                                                c := c + 1;
                                              end;
                                              print('  ------------------------  ');
                                            end;
                                            """);
                     con.exec(prog, Param.of("b1", 6), Param.of("b2", 1));
                     System.out.println(prog);
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> errorLine() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   Parser p = new Parser(db.structure());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     Program prog = p.parse("""
                                            delete t from t:a.b.T;
                                            delete s from s:S;
                                            insert into S(_id, a, b, e, h, j) values
                                                         (newid(), 1, @b1, true, ['Four', 'Quatre']text, [1, 2, 3]int),
                                                         (newid(), 6, @b2, false, ['Nine', 'Neuf', 'X']text, [5, 6, 7, 8]int);
                                            for r in select * from S do
                                              print('_id: ' + r['_id']);
                                              print('a: ' + r['a']);
                                              print('b: ' + r['b']);
                                              print('c: ' + r['c']);
                                              print('e: ' + d['e']);  # Error line
                                              print('h: ' + r['h']);
                                              print('j: ' + r['j']);
                                            end;
                                            """);
                     TranslationException error = assertThrows(TranslationException.class,
                                                               () -> con.exec(prog,
                                                                              Param.of("b1", 5),
                                                                              Param.of("b2", 9)));
                     assertEquals(error.esql.line, 11);
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> execTrim() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   Parser p = new Parser(db.structure());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     assertEquals("abc", con.exec("trim('   abc  ')"));
                     assertEquals("abc", con.exec("trim(`   abc  `)"));
                     assertNull(con.exec("trim(null)"));
                     assertThrows(ExecutionException.class, () -> con.exec("trim(1)"));

                     assertEquals("   abc", con.exec("rtrim('   abc  ')"));
                     assertEquals("   abc", con.exec("rtrim(`   abc  `)"));
                     assertNull(con.exec("rtrim(null)"));
                     assertThrows(ExecutionException.class, () -> con.exec("rtrim(1)"));

                     assertEquals("abc  ", con.exec("ltrim('   abc  ')"));
                     assertEquals("abc  ", con.exec("ltrim(`   abc  `)"));
                     assertNull(con.exec("ltrim(null)"));
                     assertThrows(ExecutionException.class, () -> con.exec("ltrim(1)"));
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> execPad() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   Parser p = new Parser(db.structure());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     assertEquals("abc", con.exec("lpad('abc', 0)"));
                     assertEquals("   abc", con.exec("lpad('abc', 6)"));
                     assertEquals("%%%abc", con.exec("lpad('abc', 6, '%')"));
                     assertEquals("abc", con.exec("lpad('abc', 0, '#')"));
                     assertEquals("%%%%%%%abc", con.exec("lpad(pad='%', 'abc', 10)"));
                     assertEquals("%%%%%%%abc", con.exec("lpad(pad='%', length=10, 'abc')"));
                     assertEquals("%%%%%%%abc", con.exec("lpad(length=10, pad='%', text='abc')"));

                     assertNull(con.exec("lpad(null)"));

                     assertThrows(ExecutionException.class, () -> con.exec("lpad(1)"));
                     assertThrows(ExecutionException.class, () -> con.exec("lpad('a', 's')"));
                     assertThrows(ExecutionException.class, () -> con.exec("lpad('a', 3, 0)"));

                     assertEquals("abc", con.exec("rpad('abc', 0)"));
                     assertEquals("abc   ", con.exec("rpad('abc', 6)"));
                     assertEquals("abc%%%", con.exec("rpad('abc', 6, '%')"));
                     assertEquals("abc", con.exec("rpad('abc', 0, '#')"));
                     assertEquals("abc%%%%%%%", con.exec("rpad(pad='%', 'abc', 10)"));
                     assertEquals("abc%%%%%%%", con.exec("rpad(pad='%', length=10, 'abc')"));
                     assertEquals("abc%%%%%%%", con.exec("rpad(length=10, pad='%', text='abc')"));

                     assertNull(con.exec("rpad(null)"));

                     assertThrows(ExecutionException.class, () -> con.exec("rpad(1)"));
                     assertThrows(ExecutionException.class, () -> con.exec("rpad('a', 's')"));
                     assertThrows(ExecutionException.class, () -> con.exec("rpad('a', 3, 0)"));
                   }
                 }));
  }
}
