/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql;

import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.EsqlTransformer;
import ma.vi.esql.parser.Parser;
import ma.vi.esql.parser.Translatable;
import ma.vi.esql.parser.define.Attribute;
import ma.vi.esql.parser.define.ConstraintDefinition;
import ma.vi.esql.parser.define.Metadata;
import ma.vi.esql.parser.query.Column;
import ma.vi.esql.database.Database;
import ma.vi.esql.database.Structure;
import ma.vi.esql.type.BaseRelation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

import static ma.vi.esql.builder.Attributes.DERIVED;
import static ma.vi.esql.builder.Attributes.TYPE;

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

  @Override
  public Map<String, Object> config() {
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
          Arrays.asList(
              new Attribute(context, "tm1", parser.parseExpression("(max(b) from S)")),
              new Attribute(context, "tm2", parser.parseExpression("a > b"))
          ),
          new ArrayList<>(Arrays.asList(
              new Column(
                  context, null,
                  parser.parseExpression("a"),
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
                  parser.parseExpression("(max(a) from S)"),
                  new Metadata(
                      context,
                      new ArrayList<>(Arrays.asList(
                          new Attribute(context, DERIVED, parser.parseExpression("true")),
                          new Attribute(context, "m1", parser.parseExpression("(min(a) from S)"))
                      ))
                  )
              ),
              new Column(
                  context, "g",
                  parser.parseExpression("(distinct c from S where d>5)"),
                  new Metadata(
                      context,
                      new ArrayList<>(Arrays.asList(
                          new Attribute(context, DERIVED, parser.parseExpression("true")),
                          new Attribute(context, "m1", parser.parseExpression("(min(a) from a.b.T)"))
                      ))
                  )
              ),
              new Column(
                  context, null,
                  parser.parseExpression("h"),
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
                  new Metadata(
                      context,
                      new ArrayList<>(Arrays.asList(
                          new Attribute(context, TYPE, parser.parseExpression("'string'")),
                          new Attribute(context, "label", parser.parseExpression(
                              "(lv.label from lv:LookupValue" +
                              "          join  l:Lookup on lv.lookup_id=l._id" +
                              "                        and  l.name='City'" +
                              "         where lv.code=i)"
                          ))
                      ))
                  )
              )
          )),
          null
      );
      structure.relation(S);

      BaseRelation T = new BaseRelation(
          context, UUID.randomUUID(), "a.b.T","a.b.T","a.b.T",
          Arrays.asList(
              new Attribute(context, "tm1", parser.parseExpression("max(b) from S")),
              new Attribute(context, "tm2", parser.parseExpression("a > b"))
          ),
          new ArrayList<>(Arrays.asList(
              new Column(
                  context, null,
                  parser.parseExpression("a"),
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
  public void postInit(Connection con,
                       Structure structure,
                       boolean createCoreTables) {
  }

  @Override
  public void addEsqlTransformer(EsqlTransformer transformer) {
  }

  @Override
  public boolean removeEsqlTransformer(EsqlTransformer transformer) {
    return false;
  }

  @Override
  public List<EsqlTransformer> esqlTransformers() {
    return null;
  }

  @Override
  public Connection pooledConnection(boolean autoCommit,
                                     int isolationLevel,
                                     String username,
                                     String password) {
    return null;
  }

  @Override
  public Connection rawConnection(boolean autoCommit,
                                  int isolationLevel,
                                  String username,
                                  String password) {
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
  public void setArray(PreparedStatement ps, int paramIndex, Object array) {
  }

  @Override
  public void table(Connection con, BaseRelation table) {
  }

  @Override
  public void tableName(Connection con, UUID tableId, String name) {
  }

  @Override
  public void clearTableMetadata(Connection con, UUID tableId) {
  }

  @Override
  public void tableMetadata(Connection con, UUID tableId, Metadata metadata) {
  }

  @Override
  public void dropTable(Connection con, UUID tableId) {
  }

  @Override
  public void column(Connection con, UUID tableId, Column column) {
  }

  @Override
  public void columnName(Connection con, UUID columnId, String name) {
  }

  @Override
  public void columnType(Connection con, UUID columnId, String type) {
  }

  @Override
  public void defaultValue(Connection con, UUID columnId, String defaultValue) {
  }

  @Override
  public void notNull(Connection con, UUID columnId, String notNull) {
  }

  @Override
  public void columnMetadata(Connection con, UUID columnId, Metadata metadata) {
  }

  @Override
  public void dropColumn(Connection con, UUID columnId) {
  }

  @Override
  public void constraint(Connection con, UUID tableId, ConstraintDefinition constraint) {
  }

  @Override
  public void dropConstraint(Connection con, UUID tableId, String constraintName) {
  }

  private Structure structure;
}
