/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.database;

import ma.vi.base.tuple.T2;
import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.exec.Param;
import ma.vi.esql.exec.Result;
import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.EsqlTransformer;
import ma.vi.esql.parser.Parser;
import ma.vi.esql.parser.define.*;
import ma.vi.esql.parser.expression.*;
import ma.vi.esql.parser.modify.Insert;
import ma.vi.esql.parser.query.Column;
import ma.vi.esql.translator.*;
import ma.vi.esql.type.BaseRelation;
import ma.vi.esql.type.Relation;
import ma.vi.esql.type.Type;
import ma.vi.esql.type.Types;

import java.sql.*;
import java.util.*;

import static java.lang.System.Logger.Level.INFO;
import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static ma.vi.base.string.Escape.escapeSqlString;
import static ma.vi.esql.builder.Attributes.*;
import static ma.vi.esql.parser.Translatable.Target.*;
import static ma.vi.esql.parser.define.ConstraintDefinition.Type.fromMarker;
import static org.apache.commons.lang3.StringUtils.repeat;

/**
 * An abstract implementation of Database that registers base translators and
 * extensions, create core tables, create and load objects structure from the
 * database.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public abstract class AbstractDatabase implements Database {
  @Override public void init(Map<String, Object> config) {
    this.config = config;

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

  /**
   * Loads the structures in the database from the _core tables, if available,
   * or, otherwise, from the information schema.
   */
  @Override
  public synchronized Structure structure() {
    if (structure == null) {
      structure = new Structure(this);
      try (Connection con = pooledConnection(true, -1);
           Statement stmt = con.createStatement()) {
        boolean hasCoreTables = hasCoreTables(con);
        Context context = new Context(structure);
        Parser parser = new Parser(structure);
        if (hasCoreTables) {
          /*
           * Load structure from the _core tables.
           */

          // load bare relations without columns, constraints, etc. as these may refer
          // to other relations which are not loaded yet. After loading the relations
          // their columns, constraints, indices, and so on are loaded and they can
          // refer freely to other relations.
          try (ResultSet rs = stmt.executeQuery("select \"_id\", \"name\", \"display_name\", \"description\", " +
                                                    "       \"type\", \"view_definition\" " +
                                                    "  from \"_core\".\"relations\"");
               PreparedStatement attrStmt = con.prepareStatement(
                   "select \"attribute\", \"value\" " +
                       "  from \"_core\".\"relation_attributes\" " +
                       " where \"relation_id\"=?")) {
            while (rs.next()) {
              loadRelation(con, context, structure, parser, attrStmt, rs);
            }
          }

          // load constraints after all tables are loaded so that foreign key
          // constraints can be properly linked to their target tables.
          Parser p = new Parser(structure);
          EsqlConnection econ = esql(con);
          try (Result rs = econ.exec(p.parse(
              "select _id, name, relation_id, type, check_expr, " +
                  "        source_columns, target_relation_id, target_columns, " +
                  "        forward_cost, reverse_cost, on_update, on_delete " +
                  "   from _core.constraints"))) {
            while (rs.next()) {
              loadConstraints(context, structure, parser, rs);
            }
          }
//          try (ResultSet rs = con.createStatement().executeQuery(
//              "select \"_id\", \"name\", \"relation_id\", \"type\", \"check_expr\", " +
//                  "       \"source_columns\", \"target_relation_id\", \"target_columns\", " +
//                  "       \"forward_cost\", \"reverse_cost\", \"on_update\", \"on_delete\" " +
//                  "  from \"_core\".\"constraints\"")) {
//            while (rs.next()) {
//              loadConstraints(context, structure, parser, rs);
//            }
//          }
//
//          // load indices and link to covered tables
//          try (ResultSet rs = c.createStatement().executeQuery("SELECT _id, name, relation_id, unique_index, " +
//              "columns, expressions, partial_index_condition " +
//              "FROM _core.indices")) {
//            while (rs.next()) {
//              loadIndex(structure, rs);
//            }
//          }
//
//          // load sequences
//          try (ResultSet rs = c.createStatement().executeQuery("SELECT _id, name, start, increment, maximum, cycles " +
//              "FROM _core.sequences")) {
//            while (rs.next()) {
//              loadSequence(structure, rs);
//            }
//          }

          // Todo: parse expressions
          // Todo: load the view sources by interpreting the view definition
        }
        /*
         * Load from the information schemas.
         */

        // load bare relations without columns, constraints, etc. as these may refer
        // to other relations which are not loaded yet. After loading the relations
        // their columns, constraints, indices, and so on are loaded and they can
        // refer freely to other relations.
        Set<BaseRelation> toSave = new HashSet<>();
        Set<String> existingInCore = new HashSet<>();
        try (ResultSet rs = stmt.executeQuery("select table_schema, table_name " +
                                                  "  from information_schema.tables " +
                                                  " where table_type='BASE TABLE'")) {
          while (rs.next()) {
            /*
             * Load table
             */
            UUID tableId = UUID.randomUUID();
            String schema = rs.getString("table_schema");
            String name = rs.getString("table_name");
            String tableName = Type.esqlTableName(schema, name, target());

            if (structure.relationExists(tableName)) {
              existingInCore.add(tableName);
            } else {
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
                    /*
                     * Hack to ensure that all boolean value capitalisations
                     * are mapped to the lowercase esql form. A proper solution
                     * to this would require a parser for each target database
                     * as the default expressions read from the information
                     * schemas are expressed in those.
                     */
                    if (defaultValue.equalsIgnoreCase("true")) {
                      defaultValue = "true";
                    } else if (defaultValue.equalsIgnoreCase("false")) {
                      defaultValue = "false";
                    }
                    if (defaultValue.equalsIgnoreCase("null")) {
                      defaultValue = "null";
                    }
                    attributes.add(new Attribute(context, EXPRESSION, parser.parseExpression(defaultValue)));
                  }
                  attributes.add(new Attribute(context, ID, new UuidLiteral(context, columnId)));
                  attributes.add(new Attribute(context, REQUIRED, new BooleanLiteral(context, notNull)));
                  Metadata metadata = new Metadata(context, attributes);
                  columns.add(new Column(
                      context,
                      columnName,
                      new ColumnRef(context, null, columnName),
                      metadata
                  ));
                }
              }

              BaseRelation relation = new BaseRelation(
                  context,
                  tableId,
                  tableName,
                  tableName,
                  null,
                  null,
                  columns,
                  new ArrayList<>()
              );
              structure.relation(relation);
              toSave.add(relation);
              Types.customType(relation.name(), relation);
            }
          }
        }

        /*
         * Load constraints from information schema for tables which do not
         * already exist in the core tables.
         */
        try (ResultSet crs = con.createStatement().executeQuery(
            "select table_schema, table_name, constraint_schema, " +
                "       constraint_name, constraint_type " +
                "  from information_schema.table_constraints" +
                " order by table_schema, table_name")) {

          while (crs.next()) {
            String schema = crs.getString("table_schema");
            String name = crs.getString("table_name");
            String tableName = Type.esqlTableName(schema, name, target());
            if (!existingInCore.contains(tableName)) {
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
                        parser.parseExpression(r.getString("check_clause")));
                  }
                  break;

                case "UNIQUE":
                  c = new UniqueConstraint(context, constraintName, sourceColumns);
                  break;

                case "PRIMARY KEY":
                  c = new PrimaryKeyConstraint(context, constraintName, sourceColumns);
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
                                                 sourceColumns,
                                                 target.a,
                                                 targetColumns,
                                                 0, 0,
                                                 ConstraintDefinition.ForeignKeyChangeAction.fromInformationSchema(
                                                     updateRule),
                                                 ConstraintDefinition.ForeignKeyChangeAction.fromInformationSchema(
                                                     deleteRule));
                  }
              }

              c.table(tableName);
              relation.constraint(c);

              // dependent relation:
              // E.g., r1[a] -> r2[b]: r1 depends on r2. If r2 is dropped, so should r1.
              if (targetRelation != null) {
                // targetRelation.dependency(relation);
                targetRelation.dependentConstraint((ForeignKeyConstraint)c);
              }

              //            // link referenced field to constraint
              //            // if the field is dropped, so should the constraint
              //            if (columns != null && !columns.isEmpty()) {
              //              for (Field f: columns) {
              //                f.dependentConstraint(c);
              //              }
              //            }
              //
              //            // link referring table for foreign keys to field;
              //            // if this field is dropped, so should the referring table.
              //            //      E.g., r1[a] -> r2[b]: r1 is the dependent relation for field b
              //            if (targetColumns != null && !targetColumns.isEmpty()) {
              //              for (Field f: targetColumns) {
              //                f.dependentForeignKey(relation);
              //              }
              //            }
            }
          }
        }

        /*
         * Save missing relations into core tables, but limit to those
         * having a period in their as those are the ESQL user-defined
         * tables.
         */
        if (hasCoreTables && !toSave.isEmpty()) {
          for (BaseRelation rel: toSave) {
            String schema = Type.schema(rel.name());
            if (schema != null && !ignoredSchemas.contains(schema.toUpperCase())) {
              table(con, rel);
            }
          }
        }

//        // load indices and link to covered tables
//        try (ResultSet rs = stmt.executeQuery("select _id, name, relation_id, unique_index, " +
//                                                  "columns, expressions, partial_index_condition " +
//                                                  "from _core.indices")) {
//          while (rs.next()) {
//            loadIndex(structure, rs);
//          }
//        }
//
//        // load sequences
//        try (ResultSet rs = c.createStatement().executeQuery("SELECT _id, name, start, increment, maximum, cycles " +
//                                                                 "FROM _core.sequences")) {
//          while (rs.next()) {
//            loadSequence(structure, rs);
//          }
//        }

        /*
         * Find the correct type for derived columns (the derived column type are
         * set to void by default).
         */
        for (BaseRelation rel: structure.relations().values()) {
          for (Column column: rel.columns()) {
            if (column.derived()) {
              column.type();
            }
          }
        }

        for (BaseRelation rel: structure.relations().values()) {
          rel.expandColumns();
        }

        // Todo: parse expressions
        // Todo: load the view sources by interpreting the view definition
      } catch (SQLException sqle) {
        throw new RuntimeException(sqle);
      }
    }
    return structure;
  }

  @Override
  public void postInit(Connection con, Structure structure) {
    Parser p = new Parser(structure);
    if (createCoreTables() && !hasCoreTables(con)) {
      // CREATE _core tables
      ///////////////////////////////////////////
      try (EsqlConnection c = esql(pooledConnection())) {
        c.exec(p.parse("create table _core.relations drop undefined(" +
                           "_id             uuid    not null, " +
                           "_can_delete     bool, " +
                           "_can_edit       bool, " +
                           "name            string  not null, " +
                           "display_name    string, " +
                           "description     string, " +
                           "type            char    not null," +
                           "view_definition text," +

                           "constraint _core_relations_pk       primary key (_id)," +
                           "constraint _core_relations_unq_name unique(name))"));

        c.exec(p.parse("create table _core.columns drop undefined(" +
                           "_id             uuid   not null, " +
                           "_can_delete     bool, " +
                           "_can_edit       bool, " +
                           "relation_id     uuid   not null, " +
                           "name            string not null, " +
                           "derived_column  bool, " +
                           "type            string not null, " +
                           "not_null        bool, " +
                           "expression      text, " +   // derived expression or default value
                           "seq             int    not null, " +

                           "constraint _core_columns_pk                primary key(_id), " +
                           "constraint _core_columns_relation_id_fk    foreign key(relation_id) references _core.relations(_id) on delete cascade on update cascade, " +
                           "constraint _core_columns_unq_relation_name unique(relation_id, name), " +
                           "constraint _core_columns_unq_relation_seq  unique(relation_id, seq))"));

//        if (target() != MARIADB && target() != MYSQL) {
          /*
           * create table for holding additional type information on tables.
           */
          c.exec(p.parse("create table _core.relation_attributes drop undefined(" +
                             "_id         uuid    not null, " +
                             "relation_id uuid    not null, " +
                             "attribute   string  not null, " +
                             "value       text, " +

                             "constraint _core_rel_attr_pk           primary key(_id), " +
                             "constraint _core_rel_attr_rel_fk       foreign key(relation_id) references _core.relations(_id) on delete cascade on update cascade, " +
                             "constraint _core_rel_attr_unq_rel_attr unique(relation_id, attribute))"));

          /*
           * create table for holding additional type information on columns.
           */
          c.exec(p.parse("create table _core.column_attributes drop undefined(" +
                             "_id       uuid    not null, " +
                             "column_id uuid    not null, " +
                             "attribute string  not null, " +
                             "value     text, " +

                             "constraint _core_column_attr_pk              primary key(_id), " +
                             "constraint _core_column_attr_column_fk       foreign key(column_id) references _core.columns(_id) on delete cascade on update cascade, " +
                             "constraint _core_column_attr_unq_column_attr unique(column_id, attribute))"));

//        } else {
//          /*
//           * For some reasons, mariadb and mysql cannot create a foreign key from
//           * a field (relation_id) that is also part of a composite unique key
//           */
//          c.exec(p.parse("create table _core.relation_attributes drop undefined(" +
//                             "_id         uuid    not null, " +
//                             "relation_id uuid    not null, " +
//                             "attribute   string  not null, " +
//                             "value       text, " +
//
//                             "constraint _core_rel_attr_pk           primary key(_id), " +
//                             "constraint _core_rel_attr_rel_fk       foreign key(relation_id) references _core.relations(_id) on delete cascade on update cascade)"));
//
//          c.exec(p.parse("create table _core.column_attributes drop undefined(" +
//                             "_id       uuid    not null, " +
//                             "column_id uuid    not null, " +
//                             "attribute string  not null, " +
//                             "value     text, " +
//
//                             "constraint _core_column_attr_pk              primary key(_id), " +
//                             "constraint _core_column_attr_column_fk       foreign key(column_id) references _core.columns(_id) on delete cascade on update cascade)"));
//        }

        c.exec(p.parse("create table _core.constraints drop undefined(" +
                           "_id                 uuid    not null, " +
                           "name                string  not null, " +
                           "relation_id         uuid    not null, " +
                           "type                char    not null, " +
                           "check_expr          text, " +
                           "source_columns      string[]," +
                           "target_relation_id  uuid," +
                           "target_columns      string[]," +
                           "forward_cost        int     not null default 1," +
                           "reverse_cost        int     not null default 2," +
                           "on_update           char," +
                           "on_delete           char," +

                           "constraint _core_constraints_pk        primary key(_id), " +
                           "constraint _core_constraints_rel_id_fk foreign key(relation_id) references _core.relations(_id) on delete cascade on update cascade)"));

        c.exec(p.parse("create table _core.sequences drop undefined(" +
                           "_id         uuid    not null, " +
                           "name        string  not null, " +
                           "start       long    not null default 1, " +
                           "increment   long    not null default 1," +
                           "maximum     long    not null default 9223372036854775807," +
                           "cycles      bool    not null default true," +

                           "constraint _core_seq_pk primary key(_id))"));

        c.exec(p.parse("create table _core.indices drop undefined(" +
                           "_id                     uuid    not null, " +
                           "name                    string  not null, " +
                           "relation_id             uuid    not null, " +
                           "unique_index            bool, " +
                           "columns                 int[]," +
                           "expressions             text[]," +
                           "partial_index_condition text," +

                           "constraint _core_indices_pk        primary key(_id), " +
                           "constraint _core_indices_rel_id_fk foreign key(relation_id) references _core.relations(_id) on delete cascade on update cascade)"));

        /*
         * Manually create self-references for the core tables so that they can be
         * accessed through ESQL.
         */
        BaseRelation relations = structure.relation("_core.relations");
        BaseRelation columns = structure.relation("_core.columns");
        BaseRelation relationAttributes = structure.relation("_core.relation_attributes");
        BaseRelation columnAttributes = structure.relation("_core.column_attributes");
        BaseRelation constraints = structure.relation("_core.constraints");
        BaseRelation sequences = structure.relation("_core.sequences");
        BaseRelation indices = structure.relation("_core.indices");

        UUID relationsId = relations.id();
        UUID columnsId = columns.id();
        UUID relationAttributesId = relationAttributes.id();
        UUID columnAttributesId = columnAttributes.id();
        UUID constraintsId = constraints.id();
        UUID sequencesId = sequences.id();
        UUID indicesId = indices.id();

        c.exec(p.parse(
            "insert into _core.relations(_id, name, display_name, type) values " +
                "('" + relationsId + "',          '_core.relations',           'Relations',           'T'), " +
                "('" + columnsId + "',            '_core.columns',             'Columns',             'T'), " +
                "('" + relationAttributesId + "', '_core.relation_attributes', 'Relation attributes', 'T'), " +
                "('" + columnAttributesId + "',   '_core.column_attributes',   'Column attributes',   'T'), " +
                "('" + constraintsId + "',        '_core.constraints',         'Constraints',         'T'), " +
                "('" + sequencesId + "',          '_core.sequences',           'Sequences',           'T'), " +
                "('" + indicesId + "',            '_core.indices',             'Indices',             'T')"));

        // add columns for all tables
        c.exec(p.parse(
            "insert into _core.columns(_id, relation_id, name, seq, type, not_null) values " +
                "('" + relations.column("_id").id() + "', '" + relationsId + "', '_id',             1, 'uuid',    true), " +
                "('" + relations.column("_can_delete").id() + "', '" + relationsId + "', '_can_delete',     2, 'bool',    false), " +
                "('" + relations.column("_can_edit").id() + "', '" + relationsId + "', '_can_edit',       3, 'bool',    false), " +
                "('" + relations.column("name").id() + "', '" + relationsId + "', 'name',            4, 'string',  true), " +
                "('" + relations.column("display_name").id() + "', '" + relationsId + "', 'display_name',    5, 'string',  false), " +
                "('" + relations.column("description").id() + "', '" + relationsId + "', 'description',     6, 'string',  false), " +
                "('" + relations.column("type").id() + "', '" + relationsId + "', 'type',            7, 'char',    true), " +
                "('" + relations.column("view_definition").id() + "', '" + relationsId + "', 'view_definition', 8, 'text',    false), " +

                "('" + columns.column("_id").id() + "', '" + columnsId + "', '_id',             1, 'uuid',   true), " +
                "('" + columns.column("_can_delete").id() + "', '" + columnsId + "', '_can_delete',     2, 'bool',   false), " +
                "('" + columns.column("_can_edit").id() + "', '" + columnsId + "', '_can_edit',       3, 'bool',   false), " +
                "('" + columns.column("relation_id").id() + "', '" + columnsId + "', 'relation_id',     4, 'uuid',   true), " +
                "('" + columns.column("name").id() + "', '" + columnsId + "', 'name',            5, 'string', true), " +
                "('" + columns.column("derived_column").id() + "', '" + columnsId + "', 'derived_column',  6, 'bool',   false), " +
                "('" + columns.column("type").id() + "', '" + columnsId + "', 'type',            7, 'string', true), " +
                "('" + columns.column("not_null").id() + "', '" + columnsId + "', 'not_null',        8, 'bool',   false), " +
                "('" + columns.column("expression").id() + "', '" + columnsId + "', 'expression',      9, 'text',   false), " +
                "('" + columns.column("seq").id() + "', '" + columnsId + "', 'seq',            10, 'int',    true), " +

                "('" + relationAttributes.column("_id").id() + "', '" + relationAttributesId + "', '_id',         1, 'uuid',   true), " +
                "('" + relationAttributes.column("relation_id")
                                         .id() + "', '" + relationAttributesId + "', 'relation_id', 2, 'uuid',   true), " +
                "('" + relationAttributes.column("attribute")
                                         .id() + "', '" + relationAttributesId + "', 'attribute',   3, 'string', true), " +
                "('" + relationAttributes.column("value").id() + "', '" + relationAttributesId + "', 'value',       4, 'text',   false), " +

                "('" + columnAttributes.column("_id").id() + "', '" + columnAttributesId + "', '_id',       1, 'uuid',   true), " +
                "('" + columnAttributes.column("column_id").id() + "', '" + columnAttributesId + "', 'column_id', 2, 'uuid',   true), " +
                "('" + columnAttributes.column("attribute").id() + "', '" + columnAttributesId + "', 'attribute', 3, 'string', true), " +
                "('" + columnAttributes.column("value").id() + "', '" + columnAttributesId + "', 'value',     4, 'text',   false), " +

                "('" + constraints.column("_id").id() + "', '" + constraintsId + "', '_id',                 1, 'uuid',     true), " +
                "('" + constraints.column("name").id() + "', '" + constraintsId + "', 'name',                2, 'string',   true), " +
                "('" + constraints.column("relation_id")
                                  .id() + "', '" + constraintsId + "', 'relation_id',         3, 'uuid',     true), " +
                "('" + constraints.column("type").id() + "', '" + constraintsId + "', 'type',                4, 'char',     true), " +
                "('" + constraints.column("check_expr")
                                  .id() + "', '" + constraintsId + "', 'check_expr',          5, 'text',     false), " +
                "('" + constraints.column("source_columns")
                                  .id() + "', '" + constraintsId + "', 'source_columns',      6, 'string[]', false), " +
                "('" + constraints.column("target_relation_id")
                                  .id() + "', '" + constraintsId + "', 'target_relation_id',  7, 'uuid',     false), " +
                "('" + constraints.column("target_columns")
                                  .id() + "', '" + constraintsId + "', 'target_columns',      8, 'string[]', false), " +
                "('" + constraints.column("forward_cost")
                                  .id() + "', '" + constraintsId + "', 'forward_cost',        9, 'int',      true), " +
                "('" + constraints.column("reverse_cost")
                                  .id() + "', '" + constraintsId + "', 'reverse_cost',       10, 'int',      true), " +
                "('" + constraints.column("on_update").id() + "', '" + constraintsId + "', 'on_update',          11, 'char',     false), " +
                "('" + constraints.column("on_delete").id() + "', '" + constraintsId + "', 'on_delete',          12, 'char',     false), " +

                "('" + sequences.column("_id").id() + "', '" + sequencesId + "', '_id',       1, 'uuid',   true), " +
                "('" + sequences.column("name").id() + "', '" + sequencesId + "', 'name',      2, 'string', true), " +
                "('" + sequences.column("start").id() + "', '" + sequencesId + "', 'start',     3, 'long',   true), " +
                "('" + sequences.column("increment").id() + "', '" + sequencesId + "', 'increment', 4, 'long',   true), " +
                "('" + sequences.column("maximum").id() + "', '" + sequencesId + "', 'maximum',   5, 'long',   true), " +
                "('" + sequences.column("cycles").id() + "', '" + sequencesId + "', 'cycles',    6, 'bool',   true), " +

                "('" + indices.column("_id").id() + "', '" + indicesId + "', '_id',                     1, 'uuid',   true), " +
                "('" + indices.column("name").id() + "', '" + indicesId + "', 'name',                    2, 'string', true), " +
                "('" + indices.column("relation_id").id() + "', '" + indicesId + "', 'relation_id',             3, 'uuid',   true), " +
                "('" + indices.column("unique_index").id() + "', '" + indicesId + "', 'unique_index',            4, 'bool',   false), " +
                "('" + indices.column("columns").id() + "', '" + indicesId + "', 'columns',                 5, 'int[]',  false), " +
                "('" + indices.column("expressions").id() + "', '" + indicesId + "', 'expressions',             6, 'text[]', false), " +
                "('" + indices.column("partial_index_condition")
                              .id() + "', '" + indicesId + "', 'partial_index_condition', 7, 'text',   false)"));

//      // add columns as children of relations
//      con.createStatement().executeUpdate(
//      "insert into _core.relation_attributes(_id, relation_id, attribute, value) values " +
//              "('" + UUID.randomUUID() + "', '" + relationsId + "', 'children', " +
//              "'{ " +
//              "    columns: {" +
//              "      $m: {" +
//              "        type: ''_core.columns'', " +
//              "        parent_link_column: ''relation_id'', " +
//              "        label: ''Columns''" +
//              "      }," +
//              "      columns: {" +
//              "        _id: {" +
//              "          $m: {" +
//              "            type: ''uuid'', " +
//              "            show: false" +
//              "          }" +
//              "        }," +
//              "        column_id: {" +
//              "          $m: {" +
//              "            type: ''uuid'', " +
//              "            show: false" +
//              "          }" +
//              "        }," +
//              "        attribute: {" +
//              "          $m: {" +
//              "            type: ''string'', " +
//              "            label: ''Attribute''," +
//              "            mask: ''Iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii''," +
//              "            required: true, " +
//              "            link_table: ''_column_attribute''," +
//              "            value_label: ''${lookuplabel(this.row.attribute.$v, \"_column_attribute\")}''" +
//              "          }" +
//              "        }," +
//              "        value: {" +
//              "          $m: {" +
//              "            type: ''string''," +
//              "            label: ''Value''," +
//              "            required: true" +
//              "          }" +
//              "        }" +
//              "      }," +
//              "      columnsOrder: [''_id'', ''column_id'', ''attribute'', ''value'']" +
//              "    }" +
//              "}')");

        // add column attributes as children of column
        c.exec(p.parse(
            "insert into _core.relation_attributes(_id, relation_id, attribute, value) values " +
                "('" + UUID.randomUUID() + "', '" + columnsId + "', 'validate_unique', '[[''relation_id'', ''name'']]'), " +
                "('" + UUID.randomUUID() + "', '" + columnsId + "', 'children', " +
                "'{ " +
                "    column_attributes: {" +
                "      $m: {" +
                "        type: ''_core.column_attributes'', " +
                "        parent_link_column: ''column_id'', " +
                "        label: ''Attributes''" +
                "      }," +
                "      columns: {" +
                "        _id: {" +
                "          $m: {" +
                "            type: ''uuid'', " +
                "            show: false" +
                "          }" +
                "        }," +
                "        column_id: {" +
                "          $m: {" +
                "            type: ''uuid'', " +
                "            show: false" +
                "          }" +
                "        }," +
                "        attribute: {" +
                "          $m: {" +
                "            type: ''string'', " +
                "            label: ''Attribute''," +
                "            mask: ''Iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii''," +
                "            required: true, " +
                "            link_table: ''_column_attribute''," +
                "            value_label: ''${lookuplabel(this.row.attribute.$v, \"_column_attribute\")}''" +
                "          }" +
                "        }," +
                "        value: {" +
                "          $m: {" +
                "            type: ''string''," +
                "            label: ''Value''," +
                "            required: true" +
                "          }" +
                "        }" +
                "      }," +
                "      columnsOrder: [''_id'', ''column_id'', ''attribute'', ''value'']" +
                "    }" +
                "}')"));

      } catch (Exception e) {
        throw e instanceof RuntimeException ? (RuntimeException)e : new RuntimeException(e);
      } finally {
        if (!hasCoreTables) {
          hasCoreTables = true;

          /*
           * Add tables from information schema to _core tables
           */
          for (BaseRelation rel: structure.relations().values()) {
            String schema = Type.schema(rel.name()).toUpperCase();
            if (!ignoredSchemas.contains(schema)) {
              table(con, rel);
            }
          }
        }
      }
    }

    /*
     * Load extensions.
     */
    loadExtensions((Collection<Class<? extends Extension>>)config.getOrDefault(CONFIG_DB_EXTENSIONS, emptySet()),
                   new HashSet<>(), 0);
  }

  private void loadExtensions(Collection<Class<? extends Extension>> toLoad,
                              Set<Class<? extends Extension>> loaded,
                              int level) {
    try {
      for (Class<? extends Extension> extension: toLoad) {
        if (!loaded.contains(extension)) {
          loaded.add(extension);
          log.log(INFO, repeat(' ', level * 2) + extension.getName() + " loaded");

          Extension e = extension.getDeclaredConstructor().newInstance();
          if (e.dependsOn() != null && !e.dependsOn().isEmpty()) {
            loadExtensions(e.dependsOn(), loaded, level + 1);
          }
          e.init(this);
          log.log(INFO, repeat(' ', level * 2) + extension.getName() + " initialized");
        }
      }
    } catch (Exception e) {
      throw e instanceof RuntimeException ? (RuntimeException)e : new RuntimeException(e);
    }
  }

  @Override
  public Map<String, Object> config() {
    return config;
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
   * Removes a ESQL transformer from the list of transformers that was used to
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

  protected void loadRelation(Connection con,
                              Context context,
                              Structure structure,
                              Parser parser,
                              PreparedStatement attrStmt,
                              ResultSet rs) throws SQLException {
    UUID relationId = UUID.fromString(rs.getString("_id"));
    String name = rs.getString("name");
    BaseRelation relation = new BaseRelation(
        context,
        relationId,
        name,
        rs.getString("display_name"),
        rs.getString("description"),
        loadRelationAttributes(context, parser, attrStmt, relationId),
        loadColumns(con, context, parser, relationId),
        new ArrayList<>());
    structure.relation(relation);
    Types.customType(name, relation);
  }

  protected List<Attribute> loadRelationAttributes(Context context,
                                                   Parser parser,
                                                   PreparedStatement attrStmt,
                                                   UUID relationId) throws SQLException {
    // load custom relation attributes
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

//  protected void loadSequence(Structure structure, ResultSet rs) throws SQLException {
//    Sequence s = new Sequence(
//        this,
//        UUID.fromString(rs.getString("_id")),
//        rs.getString("name"),
//        rs.getLong("start"),
//        rs.getLong("increment"),
//        rs.getLong("maximum"),
//        rs.getBoolean("cycles"));
//    structure.sequence(s);
//  }

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
        UUID columnId = UUID.fromString(rs.getString("_id"));
        String columnName = rs.getString("name");
        int columnNumber = rs.getInt("seq");
        String columnType = rs.getString("type");
        boolean notNull = rs.getBoolean("not_null");
        String expression = rs.getString("expression");
        boolean derivedColumn = rs.getBoolean("derived_column");

        // load custom column attributes
        Metadata metadata = new Metadata(context, new ArrayList<>());
        metadata.attribute(TYPE, columnType);
        metadata.attribute(ID, columnId);
        metadata.attribute(REQUIRED, notNull);
        metadata.attribute(SEQUENCE, columnNumber);
        attrStmt.setObject(1, columnId);
        try (ResultSet ars = attrStmt.executeQuery()) {
          while (ars.next()) {
            metadata.attribute(
                ars.getString("attribute"),
                parser.parseExpression(ars.getString("value")));
          }
        }

        Expression<?> expr = expression != null ? parser.parseExpression(expression) : null;
        if (derivedColumn) {
          metadata.attribute(DERIVED, true);
          Column col = new Column(context, columnName, expr, metadata);
          columns.add(col);
        } else {
          Column col = new Column(
              context,
              columnName,
              new ColumnRef(context, null, columnName),
              metadata);
          if (expr != null) {
            col.attribute(EXPRESSION, expr);
          }
          columns.add(col);
        }
      }
      return columns;
    } catch (SQLException sqle) {
      throw new RuntimeException(sqle);
    }
  }

  protected void loadConstraints(Context context,
                                 Structure structure,
                                 Parser parser,
                                 Result rs) throws SQLException {
    /*
     * Load constraints.
     */
    // UUID id = UUID.fromString(rs.getString("_id"));
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
          parser.parseExpression(check));
      case UNIQUE -> new UniqueConstraint(context, name, sourceColumns);
      case PRIMARY_KEY -> new PrimaryKeyConstraint(context, name, sourceColumns);
      case FOREIGN_KEY -> new ForeignKeyConstraint(
          context,
          name,
          sourceColumns,
          targetRelation.name(),
          targetColumns,
          forwardCost, reverseCost,
          onUpdate == null ? null : ConstraintDefinition.ForeignKeyChangeAction.fromMarker(onUpdate.charAt(0)),
          onDelete == null ? null : ConstraintDefinition.ForeignKeyChangeAction.fromMarker(onDelete.charAt(0)));
    };
    c.table(relation.name());
    relation.constraint(c);

    // dependent relation:
    // E.g., r1[a] -> r2[b]: r1 depends on r2. If r2 is dropped, so should r1.
    if (targetRelation != null) {
      // targetRelation.dependency(relation);
      targetRelation.dependentConstraint((ForeignKeyConstraint)c);
    }

//    // link referenced field to constraint
//    // if the field is dropped, so should the constraint
//    if (fields != null && !fields.isEmpty()) {
//      for (Field f: fields) {
//        f.dependentConstraint(c);
//      }
//    }
//
//    // link referring table for foreign keys to field;
//    // if this field is dropped, so should the referring table.
//    //      E.g., r1[a] -> r2[b]: r1 is the dependent relation for field b
//    if (targetFields != null && !targetFields.isEmpty()) {
//      for (Field f: targetFields) {
//        f.dependentForeignKey(relation);
//      }
//    }
  }

//    private int[] intArrayFrom(String array) {
//        if (array == null || array.trim().length() == 0) {
//            return new int[0];
//        } else {
//            String a[] = array.substring(1, array.length() - 1).split(",");
//            int[] values = new int[a.length];
//            for (int i=0; i < a.length; i++) {
//                values[i] = Integer.parseInt(a[i]);
//            }
//            return values;
//        }
//    }

//    protected void loadIndex(Structure structure, ResultSet rs) throws SQLException {
//        Index index = new Index(
//                this,
//                UUID.fromString(rs.getString("_id")),
//                rs.getString("name"),
//                structure.relation(UUID.fromString(rs.getString("relation_id"))),
//                rs.getBoolean("unique_index"),
//                rs.getString("partial_index_condition"));
//
//        Array fieldsArray = rs.getArray("fields");
//        if (fieldsArray != null) {
//            Integer[] fields = (Integer[])fieldsArray.getArray();
//            Map<Integer, Field> fieldsByNumber = index.indexedRelation.fieldsByNumber();
//            for (Integer field: fields) {
//                if (field != 0) {
//                    index.indexFields.add(fieldsByNumber.get(field));
//                }
//            }
//        }
//
//        Array expressionArray = rs.getArray("expressions");
//        if (expressionArray  != null) {
//            String[] exprs = (String[])expressionArray.getArray();
//            for (String expr: exprs) {
//                index.indexExpressions.add(new DbExpression(this, expr));
//            }
//        }
//        index.indexedRelation.addIndex(index);
//    }

  public boolean hasCoreTables(Connection con) {
    if (hasCoreTables == null) {
      try (Statement stmt = con.createStatement();
           ResultSet rs = stmt.executeQuery(
               "select table_name " +
                   "  from information_schema.tables " +
                   " where table_schema='_core' " +
                   "   and table_name='relations'")) {
        hasCoreTables = rs.next();
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    }
    return hasCoreTables;
  }

  @Override
  public void table(Connection con, BaseRelation table) {
    if (hasCoreTables(con)) {
      Parser p = new Parser(structure());
      EsqlConnection econ = esql(con);
      try (Result rs = econ.exec(p.parse("select _id from _core.relations where name='" + table.name() + "'"))) {
        if (!rs.next()) {
          econ.exec(p.parse(
              "insert into _core.relations(_id, name, display_name, description, type) values(" +
                  "'" + table.id().toString() + "', " +
                  "'" + table.name() + "', " +
                  (table.displayName == null ? "'" + table.name() + "'" : "'" + escapeSqlString(table.displayName) + "'") + ", " +
                  (table.description == null ? "null" : "'" + escapeSqlString(table.description) + "'") + ", " +
                  "'" + Relation.RelationType.TABLE.marker + "')"));

          if (table.attributes() != null) {
            Insert insertRelAttr = p.parse(INSERT_TABLE_ATTRIBUTE, "insert");
            for (Map.Entry<String, Expression<?>> a: table.attributes().entrySet()) {
              econ.exec(insertRelAttr,
                        Param.of("tableId", table.id()),
                        Param.of("name", a.getKey()),
                        Param.of("value", a.getValue().translate(ESQL)));
            }
          }

          // add columns
          Insert insertCol = p.parse(INSERT_COLUMN, "insert");
          Insert insertColAttr = p.parse(INSERT_COLUMN_ATTRIBUTE, "insert");
          for (Column column: table.columns()) {
            addColumn(econ, table.id(), column, insertCol, insertColAttr);
          }

          // add constraints
          Insert insertConstraint = p.parse(INSERT_CONSTRAINT, "insert");
          for (ConstraintDefinition c: table.constraints()) {
            addConstraint(econ, table.id(), c, insertConstraint);
          }
        }
      }
    }
  }

  @Override
  public void tableName(Connection con, UUID tableId, String name) {
    if (hasCoreTables(con)) {
      EsqlConnection econ = esql(con);
      Parser p = new Parser(structure());
      econ.exec(p.parse(
          "update rel " +
              "   from rel:_core.relations " +
              "    set name='" + name +
              "' where _id='" + tableId + "'"));
    }
  }

  @Override
  public void dropTable(Connection con, UUID tableId) {
    if (hasCoreTables(con)) {
      EsqlConnection econ = esql(con);
//      // delete constraints
//      con.createStatement().executeUpdate("DELETE FROM _core.constraints WHERE relation_id='" + tableId + "'");
//
//      // delete columns attributes
//      con.createStatement().executeUpdate("DELETE FROM _core.column_attributes " +
//                                              "WHERE field_id IN (" +
//                                              "      SELECT _id FROM _core.columns " +
//                                              "      WHERE relation_id='" + tableId + "')");
//
//      // delete columns
//      con.createStatement().executeUpdate("DELETE FROM _core.columns WHERE relation_id='" + tableId + "'");
//
//      // delete relation attributes
//      con.createStatement().executeUpdate("DELETE FROM _core.relation_attributes WHERE relation_id='" + tableId + "'");

      // delete from in _core.relations
      Parser p = new Parser(structure());
      econ.exec(p.parse("delete rel from rel:_core.relations where _id='" + tableId + "'"));
    }
  }

  @Override
  public void column(Connection con, UUID tableId, Column column) {
    if (hasCoreTables(con)) {
      EsqlConnection econ = esql(con);
      Parser p = new Parser(structure());
      Insert insertCol = p.parse(INSERT_COLUMN, "insert");
      Insert insertColAttr = p.parse(INSERT_COLUMN_ATTRIBUTE, "insert");
      addColumn(econ, tableId, column, insertCol, insertColAttr);
    }
  }

  private void addColumn(EsqlConnection econ,
                         UUID tableId,
                         Column column,
                         Insert insertCol,
                         Insert insertColAttr) {
    String columnName = column.alias();
    if (columnName.indexOf('/') == -1) {
      /*
       * Check if the CAN_DELETE attribute is set on this field and save
       * its value, which controls whether this field can later be dropped.
       */
      Boolean canDelete = column.metadata() == null ? null : column.metadata().evaluateAttribute(CAN_DELETE);
      UUID columnId = column.id();
      if (columnId == null) {
        columnId = UUID.randomUUID();
        column.id(columnId);
      }
      econ.exec(insertCol,
                Param.of("id",            columnId),
                Param.of("canDelete",     canDelete),
                Param.of("relation",      tableId),
                Param.of("name",          column.alias()),
                Param.of("derivedColumn", column.derived()),
                Param.of("type",          column.type().translate(ESQL)),
                Param.of("nonNull",       column.notNull()),
                Param.of("expression",
                         column.derived()                   ? column.expr().translate(ESQL) :
                         column.defaultExpression() != null ? column.defaultExpression().translate(ESQL) : null));

      addColumnMetadata(econ, column, insertColAttr);
    }
  }

  private void addColumnMetadata(EsqlConnection econ,
                                 Column column,
                                 Insert insertColAttr) {
    if (column.metadata() != null && column.metadata().attributes() != null) {
      for (Attribute attr: column.metadata().attributes().values()) {
        Expression<?> value = attr.attributeValue();
        econ.exec(insertColAttr,
                  Param.of("columnId", column.id()),
                  Param.of("name", attr.name()),
                  Param.of("value", value == null ? null : value.translate(ESQL)));
      }
    }
  }

  @Override
  public void columnName(Connection con, UUID columnId, String name) {
    if (hasCoreTables(con)) {
      EsqlConnection econ = esql(con);
      Parser p = new Parser(structure());
      econ.exec(p.parse("update col from col:_core.columns set name='" + name + "' where _id='" + columnId + "'"));
    }
  }

  @Override
  public void columnType(Connection con, UUID columnId, String type) {
    if (hasCoreTables(con)) {
      EsqlConnection econ = esql(con);
      Parser p = new Parser(structure());
      econ.exec(p.parse("update col from col:_core.columns set type='" + type + "' where _id='" + columnId + "'"));
    }
  }

  @Override
  public void defaultValue(Connection con, UUID columnId, String defaultValue) {
    if (hasCoreTables(con)) {
      EsqlConnection econ = esql(con);
      Parser p = new Parser(structure());
      econ.exec(p.parse(
        "update col " +
            "  from col:_core.columns " +
            "   set expression=" + (defaultValue == null ? "null"
                                                           : "'" + StringLiteral.escapeEsqlString(defaultValue) + "'") +
            " where _id=u'" + columnId + "'"));
    }
  }

  @Override
  public void notNull(Connection con, UUID columnId, String notNull) {
    if (hasCoreTables(con)) {
      EsqlConnection econ = esql(con);
      Parser p = new Parser(structure());
      econ.exec(p.parse(
            "update col" +
                "  from col:_core.columns " +
                "   set not_null=" + notNull +
                " where _id=u'" + columnId + "'"));
    }
  }

  @Override
  public void columnMetadata(Connection con,
                             UUID columnId,
                             Metadata metadata) {
    if (hasCoreTables(con)) {
      EsqlConnection econ = esql(con);
      Parser p = new Parser(structure());
      Insert insertColAttr = p.parse(INSERT_COLUMN_ATTRIBUTE, "insert");
      columnMetadata(econ, p, columnId, metadata, insertColAttr);
    }
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
                  Param.of("columnId", columnId),
                  Param.of("name", attr.name()),
                  Param.of("value", attr.attributeValue().translate(ESQL)));
      }
    }
  }

  @Override
  public void dropColumn(Connection con, UUID columnId) {
    if (hasCoreTables(con)) {
      EsqlConnection econ = esql(con);
      Parser p = new Parser(structure());
      econ.exec(p.parse("delete col from col:_core.columns where _id='" + columnId + "'"));
    }
  }

  @Override
  public void constraint(Connection con,
                         UUID tableId,
                         ConstraintDefinition constraint) {
    if (hasCoreTables(con)) {
      Parser p = new Parser(structure());
      Insert insertConstraint = p.parse(INSERT_CONSTRAINT, "insert");
      addConstraint(esql(con), tableId, constraint, insertConstraint);
    }
  }

  private void addConstraint(EsqlConnection econ,
                             UUID tableId,
                             ConstraintDefinition constraint,
                             Insert insertConstraint) {
    if (constraint instanceof UniqueConstraint) {
      UniqueConstraint unique = (UniqueConstraint)constraint;
      econ.exec(insertConstraint,
                Param.of("name", constraint.name()),
                Param.of("relation", tableId),
                Param.of("type", String.valueOf(ConstraintDefinition.Type.UNIQUE.marker)),
                Param.of("checkExpr", null),
                Param.of("sourceColumns", unique.columns().toArray(new String[0])),
                Param.of("targetRelation", null),
                Param.of("targetColumns", null),
                Param.of("forwardCost", 1),
                Param.of("reverseCost", 2),
                Param.of("onUpdate", null),
                Param.of("onDelete", null));

    } else if (constraint instanceof PrimaryKeyConstraint) {
      PrimaryKeyConstraint primary = (PrimaryKeyConstraint)constraint;
      econ.exec(insertConstraint,
                Param.of("name", constraint.name()),
                Param.of("relation", tableId),
                Param.of("type", String.valueOf(ConstraintDefinition.Type.PRIMARY_KEY.marker)),
                Param.of("checkExpr", null),
                Param.of("sourceColumns", primary.columns().toArray(new String[0])),
                Param.of("targetRelation", null),
                Param.of("targetColumns", null),
                Param.of("forwardCost", 1),
                Param.of("reverseCost", 2),
                Param.of("onUpdate", null),
                Param.of("onDelete", null));

    } else if (constraint instanceof ForeignKeyConstraint) {
      Structure s = constraint.context.structure;
      ForeignKeyConstraint foreign = (ForeignKeyConstraint)constraint;
      BaseRelation target = s.relation(foreign.targetTable());
      econ.exec(insertConstraint,
                Param.of("name", constraint.name()),
                Param.of("relation", tableId),
                Param.of("type", String.valueOf(ConstraintDefinition.Type.FOREIGN_KEY.marker)),
                Param.of("checkExpr", null),
                Param.of("sourceColumns", foreign.sourceColumns().toArray(new String[0])),
                Param.of("targetRelation", target.id()),
                Param.of("targetColumns", foreign.targetColumns().toArray(new String[0])),
                Param.of("forwardCost", foreign.forwardCost()),
                Param.of("reverseCost", foreign.reverseCost()),
                Param.of("onUpdate", foreign.onUpdate() == null ? null : String.valueOf(foreign.onUpdate().marker)),
                Param.of("onDelete", foreign.onDelete() == null ? null : String.valueOf(foreign.onDelete().marker)));

    } else if (constraint instanceof CheckConstraint) {
      CheckConstraint check = (CheckConstraint)constraint;
      String checkExpression = check.expr().translate(ESQL);
      econ.exec(insertConstraint,
                Param.of("name", constraint.name()),
                Param.of("relation", tableId),
                Param.of("type", String.valueOf(ConstraintDefinition.Type.CHECK.marker)),
                Param.of("checkExpr", checkExpression),
                Param.of("sourceColumns", check.expr().columns().toArray(new String[0])),
                Param.of("targetRelation", null),
                Param.of("targetColumns", null),
                Param.of("forwardCost", 1),
                Param.of("reverseCost", 2),
                Param.of("onUpdate", null),
                Param.of("onDelete", null));

    } else {
      throw new UnsupportedOperationException("Unrecognised constraint type: " + constraint.getClass());
    }
  }

  @Override
  public void dropConstraint(Connection con,
                             UUID tableId,
                             String constraintName) {
    if (hasCoreTables(con)) {
      EsqlConnection econ = esql(con);
      Parser p = new Parser(structure());
      econ.exec(p.parse(
          "delete con " +
              "  from con:_core.constraints " +
              " where relation_id='" + tableId + "' " +
              "   and name='" + constraintName + "'"));
    }
  }

  @Override
  public void clearTableMetadata(Connection con, UUID tableId) {
    if (hasCoreTables(con)) {
      EsqlConnection econ = esql(con);
      Parser p = new Parser(structure());
      econ.exec(p.parse("delete att " +
                            "  from att:_core.relation_attributes " +
                            " where relation_id='" + tableId + '\''));
    }
  }

  @Override
  public void tableMetadata(Connection con, UUID tableId, Metadata metadata) {
    if (hasCoreTables(con)) {
      EsqlConnection econ = esql(con);
      if (metadata != null && metadata.attributes() != null) {
        Parser p = new Parser(structure());
        Insert insert = p.parse("insert into _core.relation_attributes" +
                                    "(_id, relation_id, attribute, value) values" +
                                    "(newid(), :relation, :name, :value)", "insert");
        for (Attribute attr: metadata.attributes().values()) {
          Expression<?> value = attr.attributeValue();
          econ.exec(insert,
                    Param.of("relation", tableId),
                    Param.of("name", attr.name()),
                    Param.of("value", value.translate(ESQL)));
        }

        /*
         * Update table display name and description if specified.
         */
        String tableDisplayName = metadata.evaluateAttribute(NAME);
        String tableDescription = metadata.evaluateAttribute(DESCRIPTION);
        if (tableDisplayName != null && tableDescription != null) {
          econ.exec(p.parse(
              "update rel " +
              "  from rel:_core.relations " +
              "   set display_name='" + escapeSqlString(tableDisplayName) + "', " +
              "       description='" + escapeSqlString(tableDescription) + "'" +
              " where _id='" + tableId + "'"));
        } else if (tableDisplayName != null) {
          econ.exec(p.parse(
              "update rel " +
                  "  from rel:_core.relations " +
                  "   set display_name='" + escapeSqlString(tableDisplayName) + "'" +
                  " where _id='" + tableId + "'"));
        } else if (tableDescription != null) {
          econ.exec(p.parse(
              "update rel " +
                  "  from reL:_core.relations " +
                  "   set description='" + escapeSqlString(tableDescription) + "'" +
                  " where _id='" + tableId + "'"));
        }
      }
    }
  }

  protected static String valueOf(Object value) {
    return value == null ? null : value.toString();
  }

  protected Structure structure;

  private Boolean hasCoreTables = null;

  private final List<EsqlTransformer> transformers = new ArrayList<>();

  private static final String INSERT_COLUMN =
      "insert into _core.columns("
          + "  _id, _can_delete, relation_id, name, "
          + "  derived_column, type, not_null, expression, seq) "
          + "values(:id, :canDelete, :relation, :name, "
          + "       :derivedColumn, :type, :nonNull, :expression, "
          + "       coalesce((max(seq) from _core.columns where relation_id=:relation), 0) + 1)";

  private static final String INSERT_COLUMN_ATTRIBUTE =
      "insert into _core.column_attributes("
          + "  _id, column_id, attribute, value) "
          + "values(newid(), :columnId, :name, :value)";

  private static final String INSERT_TABLE_ATTRIBUTE =
      "insert into _core.relation_attributes"
          + "(_id, relation_id, attribute, value) "
          + "values(newid(), :tableId, :name, :value)";

  private static final String INSERT_CONSTRAINT =
      "insert into _core.constraints"
          + "(_id, name, relation_id, type, check_expr, source_columns, "
          + "target_relation_id, target_columns, forward_cost, reverse_cost, "
          + "on_update, on_delete) values"
          + "(newid(), :name, :relation, :type, :checkExpr, :sourceColumns, "
          + " :targetRelation, :targetColumns, :forwardCost, :reverseCost, "
          + " :onUpdate, :onDelete)";

  private static final System.Logger log = System.getLogger(AbstractDatabase.class.getName());

  private static final Set<String> ignoredSchemas = Set.of("INFORMATION_SCHEMA",
                                                           "DBO",
                                                           "PUBLIC",
                                                           "PG_CATALOG",
                                                           "SYSTEM_LOBS",
                                                           "MYSQL",
                                                           "PERFORMANCE_SCHEMA");
  private Map<String, Object> config;
}
