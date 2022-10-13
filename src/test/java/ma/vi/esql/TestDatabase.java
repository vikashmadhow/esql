/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql;

import ma.vi.base.config.Configuration;
import ma.vi.base.lang.NotFoundException;
import ma.vi.esql.database.Database;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.database.Extension;
import ma.vi.esql.database.Structure;
import ma.vi.esql.exec.Executor;
import ma.vi.esql.semantic.type.BaseRelation;
import ma.vi.esql.semantic.type.Column;
import ma.vi.esql.semantic.type.Struct;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.EsqlTransformer;
import ma.vi.esql.syntax.Parser;
import ma.vi.esql.syntax.define.Attribute;
import ma.vi.esql.syntax.define.Metadata;
import ma.vi.esql.syntax.define.table.ConstraintDefinition;
import ma.vi.esql.translation.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

import static ma.vi.esql.builder.Attributes.DERIVED;
import static ma.vi.esql.builder.Attributes.TYPE;
import static ma.vi.esql.translation.Translatable.Target.*;

/**
 * A test structure container containing structures used
 * for testing the system.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class TestDatabase implements Database {
  public TestDatabase() {
    init(null);
  }

  @Override public void init(Configuration config) {
    /*
     * Register translators.
     */
    TranslatorFactory.register(ESQL,       new EsqlTranslator());
    TranslatorFactory.register(POSTGRESQL, new PostgresqlTranslator());
    TranslatorFactory.register(SQLSERVER,  new SqlServerTranslator());
    TranslatorFactory.register(MARIADB,    new MariaDbTranslator());
    TranslatorFactory.register(HSQLDB,     new HSqlDbTranslator());

    /*
     * Load database structure.
     */
    structure();
  }

  @Override
  public Configuration config() {
    return null;
  }

  @Override
  public Translatable.Target target() {
    return Translatable.Target.TEST;
  }

  @Override
  public Structure structure() {
    if (structure == null) {
      structure = new Structure(this);
      Context context = new Context(structure);
      Parser parser = new Parser(structure);

      BaseRelation S = new BaseRelation(
          context, UUID.randomUUID(), "S", "S", "S",
          List.of(
              new Attribute(context, "tm2", parser.parseExpression("a > b"))
          ),
          new ArrayList<>(Arrays.asList(
              new Column(
                  context, null,
                  parser.parseExpression("a"),
                  Types.IntType,
                  new Metadata(
                      context,
                      new ArrayList<>(Arrays.asList(
                          new Attribute(context, TYPE, parser.parseExpression("'int'")),
                          new Attribute(context, "m1", parser.parseExpression("b > 5")),
                          new Attribute(context, "m2", parser.parseExpression("10")),
                          new Attribute(context, "m3", parser.parseExpression("a != 0"))
                      ))
                  )
              ),
              new Column(
                  context, null,
                  parser.parseExpression("b"),
                  Types.IntType,
                  new Metadata(
                      context,
                      new ArrayList<>(Arrays.asList(
                          new Attribute(context, TYPE, parser.parseExpression("'int'")),
                          new Attribute(context, "m1", parser.parseExpression("b < 0"))
                      ))
                  )
              ),
              new Column(
                  context, "c",
                  parser.parseExpression("a+b"),
                  Types.IntType,
                  new Metadata(
                      context,
                      new ArrayList<>(Arrays.asList(
                          new Attribute(context, DERIVED, parser.parseExpression("true")),
                          new Attribute(context, "m1", parser.parseExpression("a > 5")),
                          new Attribute(context, "m2", parser.parseExpression("a + b")),
                          new Attribute(context, "m3", parser.parseExpression("b > 5"))
                      ))
                  )
              ),
              new Column(
                  context, "d",
                  parser.parseExpression("b+c"),
                  Types.IntType,
                  new Metadata(
                      context,
                      new ArrayList<>(Arrays.asList(
                          new Attribute(context, DERIVED, parser.parseExpression("true")),
                          new Attribute(context, "m1", parser.parseExpression("10"))
                      ))
                  )
              ),
              new Column(
                  context, null,
                  parser.parseExpression("e"),
                  Types.IntType,
                  new Metadata(
                      context,
                      new ArrayList<>(Arrays.asList(
                          new Attribute(context, TYPE, parser.parseExpression("'int'")),
                          new Attribute(context, "m1", parser.parseExpression("c"))
                      ))
                  )
              ),
              new Column(
                  context, "f",
                  parser.parseExpression("e+d"),
                  Types.IntType,
                  new Metadata(
                      context,
                      new ArrayList<>(List.of(
                          new Attribute(context, DERIVED, parser.parseExpression("true"))
                      ))
                  )
              ),
              new Column(
                  context, "g",
                  parser.parseExpression("f+e"),
                  Types.IntType,
                  new Metadata(
                      context,
                      new ArrayList<>(List.of(
                          new Attribute(context, DERIVED, parser.parseExpression("true"))
                      ))
                  )
              ),
              new Column(
                  context, null,
                  parser.parseExpression("h"),
                  Types.IntType,
                  new Metadata(
                      context,
                      new ArrayList<>(Arrays.asList(
                          new Attribute(context, TYPE, parser.parseExpression("'int'")),
                          new Attribute(context, "m1", parser.parseExpression("5"))
                      ))
                  )
              ),
              new Column(
                  context, null,
                  parser.parseExpression("i"),
                  Types.StringType,
                  new Metadata(
                      context,
                      new ArrayList<>(Collections.singletonList(
                          new Attribute(context, TYPE, parser.parseExpression("'string'"))))
                  )
              )
          )),
          null
      );
      structure.relation(S);

      BaseRelation T = new BaseRelation(
          context, UUID.randomUUID(), "a.b.T","a.b.T","a.b.T",
          List.of(
              new Attribute(context, "tm2", parser.parseExpression("a > b"))
          ),
          new ArrayList<>(Arrays.asList(
              new Column(
                  context, null,
                  parser.parseExpression("a"),
                  Types.IntType,
                  new Metadata(
                      context,
                      new ArrayList<>(Arrays.asList(
                          new Attribute(context, TYPE, parser.parseExpression("'int'")),
                          new Attribute(context, "m1", parser.parseExpression("b > 5")),
                          new Attribute(context, "m1", parser.parseExpression("10")),
                          new Attribute(context, "m1", parser.parseExpression("a != 0"))
                      ))
                  )
              ),
              new Column(
                  context, null,
                  parser.parseExpression("b"),
                  Types.IntType,
                  new Metadata(
                      context,
                      new ArrayList<>(Arrays.asList(
                          new Attribute(context, TYPE, parser.parseExpression("'int'")),
                          new Attribute(context, "m1", parser.parseExpression("b < 0"))
                      ))
                  )
              ),
              new Column(
                  context, "c",
                  parser.parseExpression("a+b"),
                  Types.IntType,
                  new Metadata(
                      context,
                      new ArrayList<>(Arrays.asList(
                          new Attribute(context, DERIVED, parser.parseExpression("true")),
                          new Attribute(context, "m1", parser.parseExpression("a > 5")),
                          new Attribute(context, "m2", parser.parseExpression("a + b")),
                          new Attribute(context, "m3", parser.parseExpression("b > 5"))
                      ))
                  )
              ),
              new Column(
                  context, "d",
                  parser.parseExpression("b+c"),
                  Types.IntType,
                  new Metadata(
                      context,
                      new ArrayList<>(Arrays.asList(
                          new Attribute(context, DERIVED, parser.parseExpression("true")),
                          new Attribute(context, "m1", parser.parseExpression("10"))
                      ))
                  )
              )
          )),
          null
      );
      structure.relation(T);
    }
    return structure;
  }

  @Override
  public <E extends Extension, R extends E> R extension(Class<E> e) throws NotFoundException {
    throw new NotFoundException("Extension " + e + " not loaded");
  }

  @Override
  public void addEsqlTransformer(EsqlTransformer transformer) {}

  @Override
  public boolean removeEsqlTransformer(EsqlTransformer transformer) {
    return false;
  }

  @Override
  public List<EsqlTransformer> esqlTransformers() {
    return null;
  }

  @Override
  public Iterator<Executor> executors() {
    return Collections.emptyIterator();
  }

  @Override
  public void addExecutor(Executor executor) {}

  @Override
  public Subscription subscribe(String table, boolean includeHistory) {
    return null;
  }

  @Override
  public List<Subscription> subscriptions(String table) {
    return null;
  }

  @Override
  public void unsubscribe(Subscription subscription) {}

  @Override
  public Connection pooledConnection(int isolationLevel) {
    return null;
  }

  @Override
  public Connection rawConnection(int isolationLevel,
                                  String username,
                                  String password) {
    return null;
  }

  @Override
  public String transactionId(Connection con) {
    return null;
  }

  @Override
  public EsqlConnection esql(Connection con) {
    return null;
  }

  @Override
  public <T> T[] getArray(ResultSet rs, String index, Class<T> componentType) {
    return null;
  }

  @Override
  public <T> T[] getArray(ResultSet rs, int index, Class<T> componentType) {
    return null;
  }

  @Override
  public void setArray(PreparedStatement ps, int paramIndex, Object array) {}

  @Override
  public void addTable(EsqlConnection con, Struct table) {}

  @Override
  public UUID tableId(EsqlConnection con, String tableName) {
    return null;
  }

  @Override
  public BaseRelation updateTable(EsqlConnection con, BaseRelation table) {
    return null;
  }

  @Override
  public void renameTable(EsqlConnection con, UUID tableId, String name) {}

  @Override
  public void clearTableMetadata(EsqlConnection con, UUID tableId) {}

  @Override
  public void tableMetadata(EsqlConnection con, UUID tableId, Metadata metadata) {}

  @Override
  public void dropTable(EsqlConnection con, UUID tableId) {}

  @Override
  public void column(EsqlConnection con, UUID tableId, Column column, int seq) {}

  @Override
  public void columnName(EsqlConnection con, UUID columnId, String name) {}

  @Override
  public void columnType(EsqlConnection con, UUID columnId, String type) {}

  @Override
  public void defaultValue(EsqlConnection con, UUID columnId, String defaultValue) {}

  @Override
  public void notNull(EsqlConnection con, UUID columnId, String notNull) {}

  @Override
  public void columnMetadata(EsqlConnection con, UUID columnId, Metadata metadata) {}

  @Override
  public void dropColumn(EsqlConnection con, UUID columnId) {}

  @Override
  public void constraint(EsqlConnection con, UUID tableId, ConstraintDefinition constraint) {}

  @Override
  public void dropConstraint(EsqlConnection con, UUID tableId, String constraintName) {}

  private Structure structure;
}
