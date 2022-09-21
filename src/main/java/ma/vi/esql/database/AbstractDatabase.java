/*
 * Copyright (c) 2018-2022 Vikash Madhow
 */

package ma.vi.esql.database;

import ma.vi.base.config.Configuration;
import ma.vi.base.lang.NotFoundException;
import ma.vi.base.tuple.T2;
import ma.vi.esql.exec.DefaultExecutor;
import ma.vi.esql.exec.Executor;
import ma.vi.esql.exec.QueryParams;
import ma.vi.esql.exec.Result;
import ma.vi.esql.semantic.type.Struct;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.semantic.type.*;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.EsqlTransformer;
import ma.vi.esql.syntax.Parser;
import ma.vi.esql.syntax.define.Attribute;
import ma.vi.esql.syntax.define.Metadata;
import ma.vi.esql.syntax.define.table.*;
import ma.vi.esql.syntax.expression.ColumnRef;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.expression.literal.BooleanLiteral;
import ma.vi.esql.syntax.expression.literal.StringLiteral;
import ma.vi.esql.syntax.expression.literal.UuidLiteral;
import ma.vi.esql.syntax.modify.Insert;
import ma.vi.esql.translation.*;
import org.pcollections.HashPMap;
import org.pcollections.IntTreePMap;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.lang.System.Logger.Level.INFO;
import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static java.util.stream.Collectors.joining;
import static ma.vi.base.string.Escape.escapeSqlString;
import static ma.vi.esql.builder.Attributes.*;
import static ma.vi.esql.semantic.type.Relation.RelationType.STRUCT;
import static ma.vi.esql.semantic.type.Relation.RelationType.TABLE;
import static ma.vi.esql.syntax.define.table.ConstraintDefinition.ForeignKeyChangeAction.fromInformationSchema;
import static ma.vi.esql.syntax.define.table.ConstraintDefinition.Type.fromMarker;
import static ma.vi.esql.syntax.expression.literal.StringLiteral.escapeEsqlString;
import static ma.vi.esql.translation.Translatable.Target.*;
import static org.apache.commons.lang3.StringUtils.repeat;

/**
 * An abstract implementation of Database that registers base translators and
 * extensions, create core tables, create and load objects structure from the
 * database.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public abstract class AbstractDatabase implements Database {
  @Override
  public void init(Configuration config) {
    this.config = config;
    structure = new Structure(this);

    /*
     * Register translators.
     */
    TranslatorFactory.register(ESQL,       new EsqlTranslator());
    TranslatorFactory.register(POSTGRESQL, new PostgresqlTranslator());
    TranslatorFactory.register(SQLSERVER,  new SqlServerTranslator());
    TranslatorFactory.register(MARIADB,    new MariaDbTranslator());
    TranslatorFactory.register(HSQLDB,     new HSqlDbTranslator());

    /*
     * Create core tables.
     */
    try (EsqlConnection con = esql()) {
      createCoreRelations(con);
      createCoreRelationAttributes(con);
      createCoreColumns(con);
      createCoreColumnAttributes(con);
      createCoreConstraints(con);
      createCoreHistory(con);
    }

    /*
     * Load database structure from information schemas, update core tables with
     * information from information schemas and then load database structure from
     * core tables, which will include information not present in the information
     * schemas, such as relation and column attributes.
     */
    loadInformationSchemas();
    updateCoreTables();
    loadCoreTables();
  }

  /**
   * Loads the structures in the database from the _core tables, if available,
   * or, otherwise, from the information schema.
   */
  @Override
  public synchronized Structure structure() {
    return structure;
  }

  @Override
  public EsqlConnection esql(Connection con) {
    try {
      /*
       * Clean up closed connections from the thread-local connection stack.
       */
      Stack<EsqlConnectionImpl> cons = current.get();
      while (!cons.isEmpty() && cons.peek().isClosed()) cons.pop();

      EsqlConnectionImpl esql;
      if (con != null) {
        esql = new EsqlConnectionImpl(this, con);
      } else {
        if (cons.isEmpty()) {
          esql = new EsqlConnectionImpl(this, pooledConnection());
        } else {
          esql = cons.pop();
          esql.incOpenCount();
        }
      }
      cons.push(esql);
      return esql;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Configuration config() {
    return config;
  }

  @Override
  public <E extends Extension, R extends E> R extension(Class<E> e) throws NotFoundException {
    if (extensions.containsKey(e)) {
      return (R)extensions.get(e);
    } else {
      throw new NotFoundException("Extension " + e + " not loaded");
    }
  }

  /**
   * Adds an ESQL transformer to the list of transformers that are used to
   * transform ESQL statements prior to their executions.
   */
  @Override
  public void addEsqlTransformer(EsqlTransformer transformer) {
    transformers.add(transformer);
  }

  /**
   * Removes an ESQL transformer from the list of transformers that was used to
   * transform ESQL statements prior to their executions, if it exists. Returns
   * true if the transformer was successfully removed.
   */
  @Override
  public boolean removeEsqlTransformer(EsqlTransformer transformer) {
    return transformers.remove(transformer);
  }

  /**
   * Returns the list of active ESQL transformers used to transform ESQL
   * statements prior to their executions.
   */
  @Override
  public List<EsqlTransformer> esqlTransformers() {
    return unmodifiableList(transformers);
  }

  @Override
  public Iterator<Executor> executors() {
    return executors.iterator();
  }

  @Override
  public void addExecutor(Executor executor) {
    executors.addFirst(executor);
  }

  @Override
  public Subscription subscribe(String table, boolean includeHistory) {
    Subscription sub = new Subscription(table, includeHistory, new LinkedBlockingDeque<>());
    subscriptions.computeIfAbsent(table, t -> new ArrayList<>()).add(sub);
    return sub;
  }

  @Override
  public List<Subscription> subscriptions(String table) {
    return subscriptions.getOrDefault(table, emptyList());
  }

  @Override
  public void unsubscribe(Subscription subscription) {
    subscriptions.get(subscription.table()).remove(subscription);
  }

  private void loadInformationSchemas() {
    try (Connection con = pooledConnection();
         Statement stmt = con.createStatement()) {

      Context context = new Context(structure);
      Parser parser = new Parser(structure);

      /*
       * Load table information from information schemas.
       */
      try (ResultSet rs = stmt.executeQuery("select table_schema, table_name "
                                              + "  from information_schema.tables "
                                              + " where table_type='BASE TABLE'"
                                              + "   and upper(table_schema) not in ("
                                              + ignoredSchemas.stream()
                                                              .map(s -> "'" + s + "'")
                                                              .collect(Collectors.joining(","))
                                              + ")")) {
        while (rs.next()) {
          /*
           * Load table
           */
          UUID tableId = UUID.randomUUID();
          String schema = rs.getString("table_schema");
          String name = rs.getString("table_name");
          String tableName = Type.esqlTableName(schema, name, target());

          /*
           * Load columns.
           */
          List<Column> columns = new ArrayList<>();
          try (ResultSet crs = con.createStatement().executeQuery(
            "select column_name, ordinal_position, data_type, " +
              "       column_default, is_nullable " +
              "  from information_schema.columns " +
              " where table_schema='" + schema + "' " +
              "   and table_name='" + name + "'" +
              " order by ordinal_position")) {

            while (crs.next()) {
              UUID columnId = UUID.randomUUID();
              String columnName = crs.getString("column_name");
              // int fieldNumber = frs.getInt("ordinal_position");
              String colType = crs.getString("data_type");
              String columnType = Types.esqlType(colType, target());
              if (columnType == null) {
                throw new IllegalArgumentException("ESQL type equivalent for " + target() + " type "
                                                     + colType + " is not known");
              }
              boolean notNull = crs.getString("is_nullable").equals("NO");
              String defaultValue = crs.getString("column_default");

              /*
               * Load custom columns attributes
               */
              List<Attribute> attributes = new ArrayList<>();
              attributes.add(new Attribute(context, TYPE, new StringLiteral(context, columnType)));
              if (defaultValue != null) {
                attributes.add(new Attribute(context, EXPRESSION, dbExpressionToEsql(defaultValue, parser)));
              }
              attributes.add(new Attribute(context, ID, new UuidLiteral(context, columnId)));
              if (notNull) {
                attributes.add(new Attribute(context, REQUIRED, new BooleanLiteral(context, notNull)));
              }
              Metadata metadata = new Metadata(context, attributes);
              Type type = Types.typeOf(columnType);
              columns.add(new Column(context,
                                     columnName,
                                     new ColumnRef(context, null, columnName, type),
                                     type,
                                     metadata));
            }
          }
          BaseRelation relation = new BaseRelation(context,
                                                   tableId,
                                                   tableName,
                                                   tableName,
                                                   null,
                                                   null,
                                                   columns,
                                                   new ArrayList<>());
          structure.relation(relation);
          Types.customType(relation.name(), relation);
        }
      }

      /*
       * Load constraints from information schema for tables which do not
       * already exist in the core tables.
       */
      try (ResultSet crs = con.createStatement().executeQuery(
        "select table_schema, table_name, constraint_schema, "
          + "       constraint_name, constraint_type "
          + "  from information_schema.table_constraints"
          + " where upper(table_schema) not in "
          + "(" + ignoredSchemas.stream()
                                .map(s -> "'" + s + "'")
                                .collect(Collectors.joining(",")) + ")"
          + "   and upper(constraint_name) not like '%_NOT_NULL'"
          + " order by table_schema, table_name")) {

        while (crs.next()) {
          String schema = crs.getString("table_schema");
          String name = crs.getString("table_name");
          String tableName = Type.esqlTableName(schema, name, target());

          BaseRelation relation = structure.relation(tableName);
          String constraintSchema = crs.getString("constraint_schema");
          String constraintName = crs.getString("constraint_name");
          String constraintType = crs.getString("constraint_type");

          List<String> sourceColumns = keyColumns(con.createStatement(), constraintSchema, constraintName).b;
          List<String> targetColumns;
          BaseRelation targetRelation = null;
          ConstraintDefinition c = null;
          switch (constraintType) {
            case "CHECK":
              try (ResultSet r = con.createStatement().executeQuery(
                "select check_clause " +
                  "  from information_schema.check_constraints " +
                  " where constraint_schema='" + constraintSchema + "' " +
                  "   and constraint_name='" + constraintName + "'")) {
                r.next();
                c = new CheckConstraint(
                  context,
                  constraintName,
                  tableName,
                  dbExpressionToEsql(r.getString("check_clause"), parser));
              }
              break;

            case "UNIQUE":
              c = new UniqueConstraint(context, constraintName, tableName, sourceColumns);
              break;

            case "PRIMARY KEY":
              c = new PrimaryKeyConstraint(context, constraintName, tableName, sourceColumns);
              break;

            case "FOREIGN KEY":
              try (ResultSet r = con.createStatement().executeQuery(
                "select unique_constraint_schema," +
                  "       unique_constraint_name," +
                  "       update_rule, " +
                  "       delete_rule " +
                  "  from information_schema.referential_constraints " +
                  " where constraint_schema='" + constraintSchema + "' " +
                  "   and constraint_name='" + constraintName + "'")) {
                r.next();
                String uniqueConstraintSchema = r.getString("unique_constraint_schema");
                String uniqueConstraintName = r.getString("unique_constraint_name");
                String updateRule = r.getString("update_rule");
                String deleteRule = r.getString("delete_rule");

                T2<String, List<String>> target = keyColumns(con.createStatement(),
                                                             uniqueConstraintSchema,
                                                             uniqueConstraintName);
                targetRelation = structure.relation(target.a);
                targetColumns = target.b;

                c = new ForeignKeyConstraint(context,
                                             constraintName,
                                             tableName,
                                             sourceColumns,
                                             target.a,
                                             targetColumns,
                                             0, 0,
                                             fromInformationSchema(updateRule),
                                             fromInformationSchema(deleteRule),
                                             false);
              }
          }
          relation.constraint(c);

          /*
           * Dependent relation: E.g., r1[a] -> r2[b]: r1 depends on r2. If r2
           * is dropped, so should r1.
           */
          if (targetRelation != null) {
            targetRelation.dependentConstraint((ForeignKeyConstraint)c);
          }
        }
      }
    } catch (SQLException sqle) {
      throw new RuntimeException(sqle);
    }
  }

  /**
   * Create tables from data in information schema and drop columns and constraints
   * that no longer exist in the information schema.
   */
  private void updateCoreTables() {
    Parser p = new Parser(structure);
    try (EsqlConnection econ = esql()) {
      List<BaseRelation> updatedTables = new ArrayList<>();
      for (BaseRelation rel : structure.relations().values()) {
        String schema = Type.schema(rel.name());
        if (schema == null
          || !schema.equals("_core")
          || !ignoredSchemas.contains(schema.toUpperCase())) {
          UUID tableId = tableId(econ, rel.name());
          if (tableId == null) {
            addTable(econ, rel);
          } else {
            econ.exec(p.parse(
              "update r from r:_core.relations "
                + "   set display_name=" + (rel.displayName == null ? "'" + rel.name() + "'" : "'" + escapeSqlString(rel.displayName) + "'") + ", "
                + "       description=" + (rel.description == null ? "null" : "'" + escapeSqlString(rel.description) + "'")
                + " where _id='" + tableId + "'"));

            /*
             * Delete non-derived columns which are not in information schema anymore.
             */
            if (rel.columns().isEmpty()) {
              econ.exec(p.parse("delete c from c:_core.columns "
                                  + "  where relation_id='" + tableId + "'"
                                  + "    and coalesce(derived_column, false)=false"));
            } else {
              econ.exec(p.parse("delete c from c:_core.columns "
                                  + "  where relation_id='" + tableId + "'"
                                  + "    and coalesce(derived_column, false)=false "
                                  + "    and name not in ("
                                  + rel.columns().stream()
                                       .map(c -> "'" + c.b.name() + "'")
                                       .collect(joining(","))
                                  + ")"));
            }

            /*
             * Delete constraints which are not in information schema anymore.
             */
            if (rel.constraints().isEmpty()) {
              econ.exec(p.parse("delete c from c:_core.constraints "
                                  + "  where relation_id='" + tableId + "'"));
            } else {
              econ.exec(p.parse("delete c from c:_core.constraints "
                                  + "  where relation_id='" + tableId + "'"
                                  + "    and name not in ("
                                  + rel.constraints().stream()
                                       .map(c -> "'" + c.name() + "'")
                                       .collect(joining(","))
                                  + ")"));
            }
            if (!tableId.equals(rel.id())) {
              updatedTables.add(rel.id(tableId));
            }
          }
        }
      }
      if (!updatedTables.isEmpty()) {
        for (BaseRelation rel : updatedTables) {
          structure.relation(rel);
        }
      }
    }
  }

  private void loadCoreTables() {
    Context context = new Context(structure);
    Parser p = new Parser(structure);

    try (EsqlConnection econ = esql();
         Statement stmt = econ.connection().createStatement()) {
      /*
       * Load bare relations without columns, constraints, etc. as these may refer
       * to other relations which are not loaded yet. After loading the relations
       * their columns, constraints, indices, and so on are loaded as they can
       * then refer freely to other relations.
       */
      try (ResultSet rs = stmt.executeQuery("""
                            select "_id", "name", "display_name", "description",
                                   "type", "view_definition"
                              from "_core"."relations"
                            """);
           PreparedStatement attrStmt = econ.connection().prepareStatement("""
                            select "attribute", "value"
                              from "_core"."relation_attributes"
                             where "relation_id"=?""")) {
        while (rs.next()) {
          loadRelation(econ.connection(), context, structure, p, attrStmt, rs);
        }
      }

      /*
       * load constraints after all tables are loaded so that foreign key
       * constraints can be properly linked to their target tables.
       */
      try (Result rs = econ.exec(p.parse("""
        select _id, name, relation_id, "type", check_expr,
               source_columns, target_relation_id, target_columns,
               forward_cost, reverse_cost, on_update, on_delete
          from _core.constraints"""))) {
        while (rs.toNext()) {
          loadConstraints(context, structure, p, rs);
        }
      }

      /*
       * Find the correct type for derived columns (the derived column type are
       * set to void by default).
       */
      for (BaseRelation rel: structure.relations().values()) {
        for (T2<Relation, Column> column: rel.columns()) {
          if (column.b.derived()) {
            column.b.computeType(new EsqlPath(column.b));
          }
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Load extensions. Should be called from subclasses after full initialisation
   * as some extensions depend on database-specific functions and structures. E.g.
   * function to work with intervals and capture of change events.
   *
   * @param toLoad Extension classes mapped to the configuration to load. This is
   *               usually supplied in a configuration parameter (see {@link Database#CONFIG_DB_EXTENSIONS}).
   * @param loaded This is the set of extensions that have already been loaded and
   *               is used to prevent circular dependencies to cause infinite loops;
   *               initially this should be empty.
   * @param level The extension loading depths: when an extension depends on another
   *              extension, the latter has a level that is one higher that the
   *              one depending on it. This is used for printing informational
   *              messages during loading.
   */
  protected void loadExtensions(Map<Class<? extends Extension>, Configuration> toLoad,
                                Set<Class<? extends Extension>> loaded,
                                int level) {
    try {
      for (Class<? extends Extension> extension: toLoad.keySet()) {
        if (!loaded.contains(extension)) {
          loaded.add(extension);
          log.log(INFO, repeat(' ', level * 2) + extension.getName() + " loaded");

          Extension e = extension.getDeclaredConstructor().newInstance();
          if (e.dependsOn() != null && !e.dependsOn().isEmpty()) {
            loadExtensions(e.dependsOn(), loaded, level + 1);
          }
          e.init(this, toLoad.get(extension));
          extensions.put(extension, e);
          log.log(INFO, repeat(' ', level * 2) + extension.getName() + " initialized");
        }
      }
    } catch (Exception e) {
      throw e instanceof RuntimeException ? (RuntimeException)e : new RuntimeException(e);
    }
  }

  private T2<String, List<String>> keyColumns(Statement stmt,
                                              String constraintSchema,
                                              String constraintName) {
    try (ResultSet r = stmt.executeQuery(
        "select table_schema, table_name, column_name " +
            "  from information_schema.key_column_usage " +
            " where constraint_schema='" + constraintSchema + "' " +
            "   and constraint_name='" + constraintName + "' " +
            " order by ordinal_position")) {
      String table = null;
      List<String> columns = new ArrayList<>();
      while (r.next()) {
        if (table == null) {
          table = Type.esqlTableName(r.getString("table_schema"), r.getString("table_name"), target());
        }
        columns.add(r.getString("column_name"));
      }
      return T2.of(table, columns);
    } catch (SQLException sqle) {
      throw new RuntimeException(sqle);
    }
  }

  protected void loadRelation(Connection        con,
                              Context           context,
                              Structure         structure,
                              Parser            parser,
                              PreparedStatement attrStmt,
                              ResultSet         rs) throws SQLException {
    UUID relationId = UUID.fromString(rs.getString("_id"));
    String name = rs.getString("name");
    String type = rs.getString("type");
    if (type.charAt(0) == TABLE.marker) {
      BaseRelation relation = new BaseRelation(context,
                                               relationId,
                                               name,
                                               rs.getString("display_name"),
                                               rs.getString("description"),
                                               loadRelationAttributes(context, parser, attrStmt, relationId),
                                               loadColumns(con, context, parser, relationId),
                                               new ArrayList<>());
      structure.relation(relation);
      Types.customType(name, relation);
    } else if (type.charAt(0) == STRUCT.marker) {
      Struct struct = new Struct(context,
                                 relationId,
                                 name,
                                 rs.getString("display_name"),
                                 rs.getString("description"),
                                 loadRelationAttributes(context, parser, attrStmt, relationId),
                                 loadColumns(con, context, parser, relationId));
      structure.struct(struct);
      Types.customType(name, struct);
    }
  }

  /**
   * Load custom relation attributes.
   */
  protected List<Attribute> loadRelationAttributes(Context context,
                                                   Parser parser,
                                                   PreparedStatement attrStmt,
                                                   UUID relationId) throws SQLException {
    List<Attribute> attributes = new ArrayList<>();
    attrStmt.setObject(1, relationId);
    try (ResultSet ars = attrStmt.executeQuery()) {
      while (ars.next()) {
        attributes.add(
            new Attribute(context,
                          ars.getString("attribute"),
                          parser.parseExpression(ars.getString("value"))));
      }
    }
    return attributes;
  }

  protected List<Column> loadColumns(Connection con,
                                     Context context,
                                     Parser parser,
                                     UUID relationId) {
    List<Column> columns = new ArrayList<>();
    try (ResultSet rs = con.createStatement().executeQuery(
        "select \"_id\", \"name\", \"relation_id\", \"seq\", \"type\", " +
            "       \"derived_column\", \"not_null\", \"expression\" " +
            "  from \"_core\".\"columns\" " +
            " where \"relation_id\"='" + relationId + "' " +
            " order by \"seq\"");
         PreparedStatement attrStmt = con.prepareStatement(
             "select \"attribute\", \"value\" " +
                 "  from \"_core\".\"column_attributes\" " +
                 " where \"column_id\"=?")) {

      while (rs.next()) {
        UUID    columnId      = UUID.fromString(rs.getString("_id"));
        String  columnName    = rs.getString   ("name");
        int     columnNumber  = rs.getInt      ("seq");
        String  columnType    = rs.getString   ("type");
        Type    type          = Types.typeOf   (columnType);
        boolean notNull       = rs.getBoolean  ("not_null");
        String  expression    = rs.getString   ("expression");
        boolean derivedColumn = rs.getBoolean  ("derived_column");

        /*
         * Load custom column attributes.
         */
        Map<String, Attribute> attributes = new HashMap<>();
        attributes.put(TYPE, Attribute.from(context, TYPE, columnType));
        attributes.put(ID,   Attribute.from(context, ID,   columnId));
        if (notNull) {
          attributes.put(REQUIRED, Attribute.from(context, REQUIRED, notNull));
        }

        attrStmt.setObject(1, columnId);
        try (ResultSet ars = attrStmt.executeQuery()) {
          while (ars.next()) {
            String name = ars.getString("attribute");
            attributes.put(name, new Attribute(context, name,
                                               parser.parseExpression(ars.getString("value"))));
          }
        }

        Column col;
        Expression<?, String> expr = expression != null
                                   ? parser.parseExpression(expression)
                                   : null;
        if (derivedColumn) {
          attributes.put(DERIVED, Attribute.from(context, DERIVED, true));
          col = new Column(context,
                           columnName,
                           expr,
                           type,
                           new Metadata(context, new ArrayList<>(attributes.values())));
        } else {
          if (expr != null) {
            attributes.put(EXPRESSION, new Attribute(context, EXPRESSION, expr));
          }
          col = new Column(context,
                           columnName,
                           new ColumnRef(context, null, columnName, type),
                           type,
                           new Metadata(context, new ArrayList<>(attributes.values())));
        }
        columns.add(col);
      }
      return columns;
    } catch (SQLException sqle) {
      throw new RuntimeException(sqle);
    }
  }

  /**
   * Load constraints.
   */
  protected void loadConstraints(Context   context,
                                 Structure structure,
                                 Parser    parser,
                                 Result    rs) {
    String name = rs.value("name");
    BaseRelation relation = structure.relation((UUID)rs.value("relation_id"));
    ConstraintDefinition.Type type = fromMarker(((String)rs.value("type")).charAt(0));

    UUID targetRelationId = rs.value("target_relation_id");
    BaseRelation targetRelation = null;
    if (targetRelationId != null) {
      targetRelation = structure.relation(targetRelationId);
    }
    String[] sourceCols = rs.value("source_columns");
    List<String> sourceColumns = sourceCols == null ? emptyList() : asList(sourceCols);

    String[] targetCols = rs.value("target_columns");
    List<String> targetColumns = targetCols == null ? emptyList() : asList(targetCols);

    String check = rs.value("check_expr");

    int forwardCost = rs.value("forward_cost");
    int reverseCost = rs.value("reverse_cost");

    String onUpdate = rs.value("on_update");
    String onDelete = rs.value("on_delete");

    ConstraintDefinition c = switch (type) {
      case CHECK -> new CheckConstraint(
          context,
          name,
          relation.name(),
          parser.parseExpression(check));
      case UNIQUE -> new UniqueConstraint(context, name, relation.name(), sourceColumns);
      case PRIMARY_KEY -> new PrimaryKeyConstraint(context, name, relation.name(), sourceColumns);
      case FOREIGN_KEY -> new ForeignKeyConstraint(
          context,
          name,
          relation.name(),
          sourceColumns,
          targetRelation.name(),
          targetColumns,
          forwardCost, reverseCost,
          onUpdate == null ? null : ConstraintDefinition.ForeignKeyChangeAction.fromMarker(onUpdate.charAt(0)),
          onDelete == null ? null : ConstraintDefinition.ForeignKeyChangeAction.fromMarker(onDelete.charAt(0)),
          false);
    };
    relation.constraint(c);

    /*
     * dependent relation: E.g., r1[a] -> r2[b]: r1 depends on r2. If r2 is
     * dropped, so should r1.
     */
    if (targetRelation != null) {
      targetRelation.dependentConstraint((ForeignKeyConstraint)c);
    }
  }

  /**
   * Returns the table id of the table with the specified name in the core
   * information table if it exists, or returns null otherwise.
   */
  @Override
  public UUID tableId(EsqlConnection con, String tableName) {
    try (ResultSet rs = con.connection().createStatement().executeQuery(
        "select _id"
      + "  from _core.relations "
      + " where name='" + tableName + "'")) {
      return rs.next() ? UUID.fromString(rs.getString(1)) : null;
    } catch (SQLException sqle) {
      throw new RuntimeException(sqle);
    }
  }

  @Override
  public void addTable(EsqlConnection con, Struct table) {
    createCoreRelations(con);
    Parser p = new Parser(structure());
    try (Result rs = con.exec(p.parse("select _id"
                                    + "  from _core.relations"
                                    + " where name='" + table.name() + "'"))) {
      if (!rs.toNext()) {
        /*
         * Add table, its columns and constraints
         */
        con.exec(p.parse(
            "insert into _core.relations(_id, name, display_name, description, \"type\") values("
          + "u'" + table.id() + "', "
          + "'" + table.name() + "', "
          + (table.displayName == null ? "'" + table.name() + "'" : "'" + escapeSqlString(table.displayName) + "'") + ", "
          + (table.description == null ? "null" : "'" + escapeSqlString(table.description) + "'") + ", "
          + "'" + (table instanceof BaseRelation ? TABLE.marker : STRUCT.marker) + "')"));

        if (table.attributes() != null) {
          createCoreRelationAttributes(con);
          Insert insertRelAttr = p.parse(INSERT_TABLE_ATTRIBUTE, "insert");
          for (Map.Entry<String, Attribute> a: table.attributes().entrySet()) {
            con.exec(insertRelAttr,
                      new QueryParams()
                        .add("tableId", table.id())
                        .add("name", a.getKey())
                        .add("value", a.getValue().attributeValue().translate(ESQL)));
          }
        }

        /*
         * Add columns.
         */
        createCoreColumns(con);
        createCoreColumnAttributes(con);
        Insert insertCol = p.parse(INSERT_COLUMN, "insert");
        Insert insertColAttr = p.parse(INSERT_COLUMN_ATTRIBUTE, "insert");
        for (T2<Relation, Column> column: table.columns()) {
          addColumn(con, table.id(), column.b, insertCol, insertColAttr);
        }

        if (table instanceof BaseRelation br) {
          /*
           * Add constraints.
           */
          createCoreConstraints(con);
          Insert insertConstraint = p.parse(INSERT_CONSTRAINT, "insert");
          for (ConstraintDefinition c: br.constraints()) {
            addConstraint(con, table.id(), c, insertConstraint);
          }
        }
      }
    }
  }

  /**
   * Updates the table data in the core information tables; returns a new
   * BaseRelation table representing the updated table.
   */
  @Override
  public BaseRelation updateTable(EsqlConnection con, BaseRelation table) {
    Parser p = new Parser(structure());
    try (Result rs = con.exec(p.parse("select _id from _core.relations where name='" + table.name() + "'"))) {
      if (rs.toNext()) {
        /*
         * Update table, its columns and constraints
         */
        UUID tableId = rs.value(1);
        con.exec(p.parse(
            "update r from r:_core.relations "
                + "set display_name=" + (table.displayName == null ? "'" + table.name() + "'" : "'" + escapeSqlString(table.displayName) + "'") + ", "
                + "    description=" +(table.description == null ? "null" : "'" + escapeSqlString(table.description) + "'") + ", "
                + "    type='" + TABLE.marker + "' "
                + "where _id='" + tableId + "'"));

        clearTableMetadata(con, tableId);
        if (table.attributes() != null) {
          tableMetadata(con, tableId, new Metadata(table.context, table.attributesList()));
        }

        /*
         * Clear and re-add columns
         */
        con.exec(p.parse("delete c from c:_core.columns where relation_id='" + tableId + "'"));
        Insert insertCol = p.parse(INSERT_COLUMN, "insert");
        Insert insertColAttr = p.parse(INSERT_COLUMN_ATTRIBUTE, "insert");
        for (T2<Relation, Column> column: table.columns()) {
          addColumn(con, tableId, column.b, insertCol, insertColAttr);
        }

        /*
         * Clear and re-add constraints
         */
        con.exec(p.parse("delete c from c:_core.constraints where relation_id='" + tableId + "'"));
        Insert insertConstraint = p.parse(INSERT_CONSTRAINT, "insert");
        for (ConstraintDefinition c: table.constraints()) {
          addConstraint(con, tableId, c, insertConstraint);
        }

        return tableId.equals(table.id()) ? table : table.id(tableId);
      }
    }
    return table;
  }

  @Override
  public void renameTable(EsqlConnection con, UUID tableId, String name) {
    createCoreRelations(con);
    con.exec("update rel "
           + "   from rel:_core.relations "
           + "    set name='" + name
           + "' where _id='" + tableId + "'");
  }

  @Override
  public void dropTable(EsqlConnection con, UUID tableId) {
    createCoreRelations(con);
    con.exec("delete rel"
           + "  from rel:_core.relations"
           + " where _id='" + tableId + "'");
  }

  @Override
  public void column(EsqlConnection con, UUID tableId, Column column) {
    createCoreColumns(con);
    Parser p = new Parser(structure());
    Insert insertCol = p.parse(INSERT_COLUMN, "insert");
    Insert insertColAttr = p.parse(INSERT_COLUMN_ATTRIBUTE, "insert");
    addColumn(con, tableId, column, insertCol, insertColAttr);
  }

  private void addColumn(EsqlConnection econ,
                         UUID           tableId,
                         Column         column,
                         Insert         insertCol,
                         Insert         insertColAttr) {
    String columnName = column.name();
    if (columnName.indexOf('/') == -1) {
      /*
       * Check if the CAN_DELETE attribute is set on this field and save
       * its value, which controls whether this field can later be dropped.
       */
      Boolean noDelete = column.metadata() == null
                       ? false
                       : column.metadata().evaluateAttribute(NO_DELETE,
                                                             econ,
                                                             new EsqlPath(column.metadata()),
                                                             HashPMap.empty(IntTreePMap.empty()),
                                                             econ.database().structure());
      if (noDelete == null) noDelete = false;
      UUID columnId = column.id();
      if (columnId == null) {
        Map<String, Attribute> attributes = new LinkedHashMap<>();
        if (column.metadata() != null && column.metadata().attributes() != null) {
          attributes.putAll(column.metadata().attributes());
        }
        columnId = UUID.randomUUID();
        // attributes.put(ID, Attribute.from(column.context, ID, columnId));
        // attributes.remove(ID);
        column = new Column(column.context,
                            column.name(),
                            column.expression(),
                            column.type(),
                            new Metadata(column.context, new ArrayList<>(attributes.values())));
      }
      econ.exec(insertCol,
                new QueryParams()
                  .add("id",            columnId)
                  .add("noDelete",      noDelete)
                  .add("relation",      tableId)
                  .add("name",          column.name())
                  .add("derivedColumn", column.derived())
                  .add("type",          column.computeType(new EsqlPath(column)).translate(ESQL))
                  .add("nonNull",       column.notNull())
                  .add("expression",
                           column.derived()                   ? column.expression().translate(ESQL) :
                           column.defaultExpression() != null ? column.defaultExpression().translate(ESQL) : null));
      addColumnMetadata(econ, column, insertColAttr);
    }
  }

  private void addColumnMetadata(EsqlConnection econ,
                                 Column column,
                                 Insert insertColAttr) {
    if (column.metadata() != null && column.metadata().attributes() != null) {
      for (Attribute attr: column.metadata().attributes().values()) {
        if (!attr.name().equals(ID)) {
          Expression<?, ?> value = attr.attributeValue();
          econ.exec(insertColAttr,
                    new QueryParams()
                      .add("columnId", column.id())
                      .add("name", attr.name())
                      .add("value", value == null ? null : value.translate(ESQL)));
        }
      }
    }
  }

  @Override
  public void columnName(EsqlConnection con, UUID columnId, String name) {
    createCoreColumns(con);
    con.exec("update col"
           + "  from col:_core.columns"
           + "   set name='" + name + "'"
           + " where _id='" + columnId + "'");
  }

  @Override
  public void columnType(EsqlConnection con, UUID columnId, String type) {
    createCoreColumns(con);
    con.exec("update col"
           + "  from col:_core.columns"
           + "   set type='" + type + "'"
           + " where _id='" + columnId + "'");
  }

  @Override
  public void defaultValue(EsqlConnection con, UUID columnId, String defaultValue) {
    createCoreColumns(con);
    con.exec("update col "
           + "  from col:_core.columns "
           + "   set expression=" + (defaultValue == null
                                   ? "null"
                                   : "'" + escapeEsqlString(defaultValue) + "'")
           + " where _id=u'" + columnId + "'");
  }

  @Override
  public void notNull(EsqlConnection con, UUID columnId, String notNull) {
    createCoreColumns(con);
    con.exec("update col"
           + "  from col:_core.columns"
           + "   set not_null=" + notNull
           + " where _id=u'" + columnId + "'");
  }

  @Override
  public void columnMetadata(EsqlConnection con,
                             UUID columnId,
                             Metadata metadata) {
    createCoreColumnAttributes(con);
    Parser p = new Parser(structure());
    Insert insertColAttr = p.parse(INSERT_COLUMN_ATTRIBUTE, "insert");
    columnMetadata(con, p, columnId, metadata, insertColAttr);
  }

  private void columnMetadata(EsqlConnection econ,
                              Parser p,
                              UUID columnId,
                              Metadata metadata,
                              Insert insertColAttr) {
    econ.exec(p.parse("delete att from att:_core.column_attributes where column_id='" + columnId + "'"));
    if (metadata != null && metadata.attributes() != null) {
      for (Attribute attr: metadata.attributes().values()) {
        econ.exec(insertColAttr,
                  new QueryParams()
                    .add("columnId", columnId)
                    .add("name",     attr.name())
                    .add("value",    attr.attributeValue().translate(ESQL)));
      }
    }
  }

  @Override
  public void dropColumn(EsqlConnection con, UUID columnId) {
    createCoreColumns(con);
    con.exec("delete col from col:_core.columns where _id='" + columnId + "'");
  }

  @Override
  public void constraint(EsqlConnection con,
                         UUID tableId,
                         ConstraintDefinition constraint) {
    createCoreConstraints(con);
    Parser p = new Parser(structure());
    Insert insertConstraint = p.parse(INSERT_CONSTRAINT, "insert");
    addConstraint(con, tableId, constraint, insertConstraint);
  }

  private void addConstraint(EsqlConnection econ,
                             UUID tableId,
                             ConstraintDefinition constraint,
                             Insert insertConstraint) {
    if (constraint instanceof UniqueConstraint unique) {
      econ.exec(insertConstraint,
                new QueryParams()
                  .add("name", constraint.name())
                  .add("relation", tableId)
                  .add("type", String.valueOf(ConstraintDefinition.Type.UNIQUE.marker))
                  .add("checkExpr", null)
                  .add("sourceColumns", unique.columns().toArray(new String[0]))
                  .add("targetRelation", null)
                  .add("targetColumns", null)
                  .add("forwardCost", 1)
                  .add("reverseCost", 2)
                  .add("onUpdate", null)
                  .add("onDelete", null));

    } else if (constraint instanceof PrimaryKeyConstraint primary) {
      econ.exec(insertConstraint,
                new QueryParams()
                  .add("name", constraint.name())
                  .add("relation", tableId)
                  .add("type", String.valueOf(ConstraintDefinition.Type.PRIMARY_KEY.marker))
                  .add("checkExpr", null)
                  .add("sourceColumns", primary.columns().toArray(new String[0]))
                  .add("targetRelation", null)
                  .add("targetColumns", null)
                  .add("forwardCost", 1)
                  .add("reverseCost", 2)
                  .add("onUpdate", null)
                  .add("onDelete", null));

    } else if (constraint instanceof ForeignKeyConstraint foreign) {
      Structure s = constraint.context.structure;
      BaseRelation target = s.relation(foreign.targetTable());
      econ.exec(insertConstraint,
                new QueryParams()
                  .add("name", constraint.name())
                  .add("relation", tableId)
                  .add("type", String.valueOf(ConstraintDefinition.Type.FOREIGN_KEY.marker))
                  .add("checkExpr", null)
                  .add("sourceColumns", foreign.sourceColumns().toArray(new String[0]))
                  .add("targetRelation", target.id())
                  .add("targetColumns", foreign.targetColumns().toArray(new String[0]))
                  .add("forwardCost", foreign.forwardCost())
                  .add("reverseCost", foreign.reverseCost())
                  .add("onUpdate", foreign.onUpdate() == null ? null : String.valueOf(foreign.onUpdate().marker))
                  .add("onDelete", foreign.onDelete() == null ? null : String.valueOf(foreign.onDelete().marker)));

    } else if (constraint instanceof CheckConstraint check) {
      String checkExpression = check.expr().translate(ESQL);
      econ.exec(insertConstraint,
                new QueryParams()
                  .add("name", constraint.name())
                  .add("relation", tableId)
                  .add("type", String.valueOf(ConstraintDefinition.Type.CHECK.marker))
                  .add("checkExpr", checkExpression)
                  .add("sourceColumns", check.expr().referredColumns().toArray(new String[0]))
                  .add("targetRelation", null)
                  .add("targetColumns", null)
                  .add("forwardCost", 1)
                  .add("reverseCost", 2)
                  .add("onUpdate", null)
                  .add("onDelete", null));

    } else {
      throw new UnsupportedOperationException("Unrecognised constraint type: " + constraint.getClass());
    }
  }

  @Override
  public void dropConstraint(EsqlConnection con,
                             UUID tableId,
                             String constraintName) {
    createCoreConstraints(con);
    con.exec("delete con "
          + "  from con:_core.constraints "
          + " where relation_id='" + tableId + "' "
          + "   and name='" + constraintName + "'");
  }

  @Override
  public void clearTableMetadata(EsqlConnection con, UUID tableId) {
    createCoreRelationAttributes(con);
    con.exec("delete att"
           + "  from att:_core.relation_attributes"
           + " where relation_id='" + tableId + "'");
  }

  @Override
  public void tableMetadata(EsqlConnection con, UUID tableId, Metadata metadata) {
    if (metadata != null && metadata.attributes() != null) {
      createCoreRelations(con);
      createCoreRelationAttributes(con);

      Parser p = new Parser(structure());
      Insert insert = p.parse("insert into _core.relation_attributes"
                            + "      (_id, relation_id, attribute, value)"
                            + "values(newid(), @relation, @name, @value)", "insert");
      for (Attribute attr: metadata.attributes().values()) {
        Expression<?, ?> value = attr.attributeValue();
        con.exec(insert,
                 new QueryParams()
                    .add("relation", tableId)
                    .add("name", attr.name())
                    .add("value", value.translate(ESQL)));
      }

      /*
       * Update table display name and description if specified.
       */
      String tableDisplayName = metadata.evaluateAttribute(NAME);
      String tableDescription = metadata.evaluateAttribute(DESCRIPTION);
      if (tableDisplayName != null && tableDescription != null) {
        con.exec(p.parse(
            "update rel"
          + "  from rel:_core.relations"
          + "   set display_name='" + escapeSqlString(tableDisplayName) + "', "
          + "       description='" + escapeSqlString(tableDescription) + "'"
          + " where _id='" + tableId + "'"));
      } else if (tableDisplayName != null) {
        con.exec(p.parse(
            "update rel "
          + "  from rel:_core.relations "
          + "   set display_name='" + escapeSqlString(tableDisplayName) + "'"
          + " where _id='" + tableId + "'"));
      } else if (tableDescription != null) {
        con.exec(p.parse(
            "update rel "
          + "  from reL:_core.relations "
          + "   set description='" + escapeSqlString(tableDescription) + "'"
          + " where _id='" + tableId + "'"));
      }
    }
  }

  protected static String valueOf(Object value) {
    return value == null ? null : value.toString();
  }

  /**
   * Hack to ensure that native database expressions are mapped to ESQL form (A
   * better solution to this would require a parser for each target database as
   * the default expressions read from the information schemas are expressed in
   * those):
   * 1. that all boolean value capitalisations are mapped to the lowercase esql
   *    form.
   * 2. String prefixes such as N'' and E'' are removed (this is done using a
   *    regex which caters for most cases but will not catch all of them; for that
   *    a parser is required).
   * 3. Remove square brackets surrounding variables which are used in SQL Server.
   */
  private static Expression<?, String> dbExpressionToEsql(String expr, Parser parser) {
    if (expr == null) {
      return null;
    } else {
      expr = expr.trim();
      if (expr.equalsIgnoreCase("true")) {
        expr = "true";
      } else if (expr.equalsIgnoreCase("false")) {
        expr = "false";
      }
      if (expr.equalsIgnoreCase("null")) {
        expr = "null";
      }
      expr = STRING_PREFIX.matcher(expr).replaceAll("$1");
      expr = SQL_SERVER_SURROUNDED_VAR.matcher(expr).replaceAll("$1");
      expr = SQL_SERVER_LEN.matcher(expr).replaceAll("length($1)");
      expr = SQL_KEYWORDS.matcher(expr).replaceAll(m -> m.group().toLowerCase());
      expr = expr.replace("::bpchar", "::char");
      return parser.parseExpression(expr);
    }
  }

  /**
   * The cached database structure is loaded on initialisation and kept up-to-date
   * on every change to the database through object creation, deletion and alteration
   * statements.
   */
  protected Structure structure;

  /**
   * List of registered ESQL transformers which can change an ESQL program before
   * it is executed.
   */
  private final List<EsqlTransformer> transformers = new ArrayList<>();

  /**
   * The executors chain which executes the ESQL program. New executors are added
   * at the front of the queue and the first one is invoked with the ESQL to execute.
   * The executor can complete the execution or perform some action before
   * delegating to the next one in the chain. At the bottom of the chain is the
   * {@link DefaultExecutor} which is the base executor for ESQL programs.
   */
  private final Deque<Executor> executors = new ArrayDeque<>(singleton(new DefaultExecutor()));

  /**
   * Loaded extensions.
   */
  private final Map<Class<? extends Extension>, Extension> extensions = new HashMap<>();

  /**
   * Database configuration.
   */
  private Configuration config;

  /**
   * Simple transaction boundary management using a thread local to hold the
   * current active connection (and transaction).
   */
  private final ThreadLocal<Stack<EsqlConnectionImpl>> current = ThreadLocal.withInitial(Stack::new);

  /**
   * Subscriptions to table events.
   */
  private final Map<String, List<Subscription>> subscriptions = new ConcurrentHashMap<>();

  private static final String INSERT_COLUMN = """
    insert into _core.columns(_id, _no_delete, relation_id, name, derived_column,
                              "type", not_null, expression, seq)
    values(@id, @noDelete, @relation, @name,
           @derivedColumn, @type, @nonNull, @expression,
           coalesce(from _core.columns select max(seq) where relation_id=@relation, 0) + 1)""";

  private static final String INSERT_COLUMN_ATTRIBUTE = """
    insert into _core.column_attributes(_id, column_id, attribute, value)
                                 values(newid(), @columnId, @name, @value)""";

  private static final String INSERT_TABLE_ATTRIBUTE = """
    insert into _core.relation_attributes(_id, relation_id, attribute, value)
                                   values(newid(), @tableId, @name, @value)""";

  private static final String INSERT_CONSTRAINT = """
    insert into _core.constraints(_id, name, relation_id, type, check_expr, source_columns,
                                  target_relation_id, target_columns, forward_cost, reverse_cost,
                                  on_update, on_delete)
    values(newid(), @name, @relation, @type, @checkExpr, @sourceColumns,
           @targetRelation, @targetColumns, @forwardCost, @reverseCost,
           @onUpdate, @onDelete)""";

  private static final System.Logger log = System.getLogger(AbstractDatabase.class.getName());

  private static final Set<String> ignoredSchemas = Set.of("INFORMATION_SCHEMA",
//                                                           "DBO",
//                                                           "PUBLIC",
                                                           "PG_CATALOG",
                                                           "SYSTEM_LOBS",
                                                           "MYSQL",
                                                           "PERFORMANCE_SCHEMA");

  private static final Pattern STRING_PREFIX = Pattern.compile("\\b[A-Z]('[^']*')");

  /**
   * Regex to identify special variables that SQL Server surround with square
   * brackets. To parse as ESQL the square brackets must be removed.
   */
  private static final Pattern SQL_SERVER_SURROUNDED_VAR = Pattern.compile("\\[([a-zA-Z_][a-zA-Z_0-9]*)\\]");

  /**
   * Replace len() function with length in SQL Server.
   */
  private static final Pattern SQL_SERVER_LEN = Pattern.compile("len\\s*\\((.+)\\)");

  /**
   * Keywords to change to lowercase.
   */
  private static final Pattern SQL_KEYWORDS = Pattern.compile(
    "and|or|not|left|right|trim|len|length|null|in|all|any|true|false",
    Pattern.CASE_INSENSITIVE);
}