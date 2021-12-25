/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql;

import ma.vi.esql.database.Structure;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.Parser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

// @todo text array testing for both postgres and mssqlserver

public class StructureTest {

  TestDatabase container = new TestDatabase();

  @Test
  public void test() {
    Structure structure = container.structure();
    Parser parser = new Parser(structure);

    Esql<?, ?> esql = parser.parse("select * from S");
    assertNotNull(esql);
  }

  // test array
  // test merge
  // test lookups
  // test bulk insert
  // alter table
  // create, alter and drop index
  // create, alter and drop sequence
  // access sequence
  // window functions
  // grouping sets
  // parsing of parameters (include an ESQL expression as a parameter)
}