/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.history;

import ma.vi.esql.DataTest;
import ma.vi.esql.database.Database;
import ma.vi.esql.database.EsqlConnection;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class StructureChangeTest extends DataTest {
  @TestFactory
  Stream<DynamicTest> structureChange() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   Database.StructureSubscription subscription = db.structureSubscribe();
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("create table subs.test.X(a int)");
                     con.exec("create table subs.test.Y(b int)");
                   }
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("alter table subs.test.X rename to Z");
                     con.exec("alter table subs.test.Z add column b int");
                     con.exec("alter table subs.test.Z drop column a");
                     con.exec("alter table subs.test.Z alter column b rename to c");
                     con.exec("alter table subs.test.Z alter column c type string");
                     con.exec("drop table subs.test.Y");
                     con.exec("drop table subs.test.Z");
                   }

                   Database.StructureChangeEvent e = subscription.events().take();
                   assertEquals(e.change(), Database.StructureChange.TABLE_CREATED);
                   assertEquals(e.tableName(), "subs.test.X");

                   e = subscription.events().take();
                   assertEquals(e.change(), Database.StructureChange.TABLE_CREATED);
                   assertEquals(e.tableName(), "subs.test.Y");

                   e = subscription.events().take();
                   assertEquals(e.change(), Database.StructureChange.TABLE_RENAMED);
                   assertEquals(e.tableName(), "subs.test.X");
                   assertEquals(e.tableNewName(), "subs.test.Z");

                   e = subscription.events().take();
                   assertEquals(e.change(), Database.StructureChange.COLUMN_ADDED);
                   assertEquals(e.tableName(), "subs.test.Z");
                   assertEquals(e.column().name(), "b");
                   assertEquals(e.column().type().name(), "int");

                   e = subscription.events().take();
                   assertEquals(e.change(), Database.StructureChange.COLUMN_DROPPED);
                   assertEquals(e.tableName(), "subs.test.Z");
                   assertEquals(e.column().name(), "a");

                   e = subscription.events().take();
                   assertEquals(e.change(), Database.StructureChange.COLUMN_RENAMED);
                   assertEquals(e.tableName(), "subs.test.Z");
                   assertEquals(e.column().name(), "b");
                   assertEquals(e.newColumn().name(), "c");

                   e = subscription.events().take();
                   assertEquals(e.change(), Database.StructureChange.COLUMN_TYPE_CHANGED);
                   assertEquals(e.tableName(), "subs.test.Z");
                   assertEquals(e.column().name(), "c");
                   assertEquals(e.column().type().name(), "int");
                   assertEquals(e.newColumn().name(), "c");
                   assertEquals(e.newColumn().type().name(), "string");

                   e = subscription.events().take();
                   assertEquals(e.change(), Database.StructureChange.TABLE_DROPPED);
                   assertEquals(e.tableName(), "subs.test.Y");

                   e = subscription.events().take();
                   assertEquals(e.change(), Database.StructureChange.TABLE_DROPPED);
                   assertEquals(e.tableName(), "subs.test.Z");
                 }));
  }
}
