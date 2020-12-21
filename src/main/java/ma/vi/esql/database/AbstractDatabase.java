/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.database;

import ma.vi.base.tuple.T2;
import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.exec.Param;
import ma.vi.esql.exec.Result;
import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.Parser;
import ma.vi.esql.parser.define.*;
import ma.vi.esql.parser.expression.BooleanLiteral;
import ma.vi.esql.parser.expression.ColumnRef;
import ma.vi.esql.parser.expression.Expression;
import ma.vi.esql.parser.expression.StringLiteral;
import ma.vi.esql.parser.modify.Insert;
import ma.vi.esql.parser.query.Column;
import ma.vi.esql.type.BaseRelation;
import ma.vi.esql.type.Relation;
import ma.vi.esql.type.Types;

import java.sql.*;
import java.util.*;

import static java.lang.System.Logger.Level.INFO;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static ma.vi.base.string.Escape.escapeEsqlQuote;
import static ma.vi.base.string.Escape.escapeSqlString;
import static ma.vi.esql.builder.Attributes.*;
import static ma.vi.esql.parser.Translatable.Target.ESQL;
import static ma.vi.esql.parser.define.ConstraintDefinition.ForeignKeyChangeAction.NO_ACTION;
import static ma.vi.esql.parser.define.ConstraintDefinition.Type.fromMarker;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public abstract class AbstractDatabase implements Database {
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
            String tableName = schema + '.' + name;

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
                    attributes.add(new Attribute(context, EXPRESSION, parser.parseExpression(defaultValue)));
                  }
                  attributes.add(new Attribute(context, ID, new StringLiteral(context, columnId.toString())));
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
            String tableName = schema + '.' + name;
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

        // Todo: parse expressions
        // Todo: load the view sources by interpreting the view definition
      } catch (SQLException sqle) {
        throw new RuntimeException(sqle);
      }
    }
    return structure;
  }

  @Override
  public void postInit(Connection con,
                       Structure structure,
                       boolean createCoreTables,
                       boolean createPlatformTables) {

    Parser p = new Parser(structure);
    if (createCoreTables && !hasCoreTables(con)) {
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

        // create table for holding additional type information on tables
        c.exec(p.parse("create table _core.relation_attributes drop undefined(" +
                           "_id         uuid    not null, " +
                           "relation_id uuid    not null," +
                           "attribute   string  not null," +
                           "value       text, " +

                           "constraint _core_rel_attr_pk           primary key(_id), " +
                           "constraint _core_rel_attr_rel_fk       foreign key(relation_id) references _core.relations(_id) on delete cascade on update cascade, " +
                           "constraint _core_rel_attr_unq_rel_attr unique(relation_id, attribute))"));

        // create table for holding additional type information on columns
        c.exec(p.parse("create table _core.column_attributes drop undefined(" +
                           "_id       uuid    not null, " +
                           "column_id uuid    not null, " +
                           "attribute string  not null, " +
                           "value     text, " +

                           "constraint _core_column_attr_pk              primary key(_id), " +
                           "constraint _core_column_attr_column_fk       foreign key(column_id) references _core.columns(_id) on delete cascade on update cascade, " +
                           "constraint _core_column_attr_unq_column_attr unique(column_id, attribute))"));

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
        BaseRelation relations          = structure.relation("_core.relations");
        BaseRelation columns            = structure.relation("_core.columns");
        BaseRelation relationAttributes = structure.relation("_core.relation_attributes");
        BaseRelation columnAttributes   = structure.relation("_core.column_attributes");
        BaseRelation constraints        = structure.relation("_core.constraints");
        BaseRelation sequences          = structure.relation("_core.sequences");
        BaseRelation indices            = structure.relation("_core.indices");

        UUID relationsId          = relations.id();
        UUID columnsId            = columns.id();
        UUID relationAttributesId = relationAttributes.id();
        UUID columnAttributesId   = columnAttributes.id();
        UUID constraintsId        = constraints.id();
        UUID sequencesId          = sequences.id();
        UUID indicesId            = indices.id();

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
                "('" + relations.column("_id").id()             + "', '" + relationsId + "', '_id',             1, 'uuid',    true), " +
                "('" + relations.column("_can_delete").id()     + "', '" + relationsId + "', '_can_delete',     2, 'bool',    false), " +
                "('" + relations.column("_can_edit").id()       + "', '" + relationsId + "', '_can_edit',       3, 'bool',    false), " +
                "('" + relations.column("name").id()            + "', '" + relationsId + "', 'name',            4, 'string',  true), " +
                "('" + relations.column("display_name").id()    + "', '" + relationsId + "', 'display_name',    5, 'string',  false), " +
                "('" + relations.column("description").id()     + "', '" + relationsId + "', 'description',     6, 'string',  false), " +
                "('" + relations.column("type").id()            + "', '" + relationsId + "', 'type',            7, 'char',    true), " +
                "('" + relations.column("view_definition").id() + "', '" + relationsId + "', 'view_definition', 8, 'text',    false), " +

                "('" + columns.column("_id").id()            + "', '" + columnsId + "', '_id',             1, 'uuid',   true), " +
                "('" + columns.column("_can_delete").id()    + "', '" + columnsId + "', '_can_delete',     2, 'bool',   false), " +
                "('" + columns.column("_can_edit").id()      + "', '" + columnsId + "', '_can_edit',       3, 'bool',   false), " +
                "('" + columns.column("relation_id").id()    + "', '" + columnsId + "', 'relation_id',     4, 'uuid',   true), " +
                "('" + columns.column("name").id()           + "', '" + columnsId + "', 'name',            5, 'string', true), " +
                "('" + columns.column("derived_column").id() + "', '" + columnsId + "', 'derived_column',  6, 'bool',   false), " +
                "('" + columns.column("type").id()           + "', '" + columnsId + "', 'type',            7, 'string', true), " +
                "('" + columns.column("not_null").id()       + "', '" + columnsId + "', 'not_null',        8, 'bool',   false), " +
                "('" + columns.column("expression").id()     + "', '" + columnsId + "', 'expression',      9, 'text',   false), " +
                "('" + columns.column("seq").id()            + "', '" + columnsId + "', 'seq',            10, 'int',    true), " +

                "('" + relationAttributes.column("_id").id()         + "', '" + relationAttributesId + "', '_id',         1, 'uuid',   true), " +
                "('" + relationAttributes.column("relation_id").id() + "', '" + relationAttributesId + "', 'relation_id', 2, 'uuid',   true), " +
                "('" + relationAttributes.column("attribute").id()   + "', '" + relationAttributesId + "', 'attribute',   3, 'string', true), " +
                "('" + relationAttributes.column("value").id()       + "', '" + relationAttributesId + "', 'value',       4, 'text',   false), " +

                "('" + columnAttributes.column("_id").id()       + "', '" + columnAttributesId + "', '_id',       1, 'uuid',   true), " +
                "('" + columnAttributes.column("column_id").id() + "', '" + columnAttributesId + "', 'column_id', 2, 'uuid',   true), " +
                "('" + columnAttributes.column("attribute").id() + "', '" + columnAttributesId + "', 'attribute', 3, 'string', true), " +
                "('" + columnAttributes.column("value").id()     + "', '" + columnAttributesId + "', 'value',     4, 'text',   false), " +

                "('" + constraints.column("_id").id()                + "', '" + constraintsId + "', '_id',                 1, 'uuid',     true), " +
                "('" + constraints.column("name").id()               + "', '" + constraintsId + "', 'name',                2, 'string',   true), " +
                "('" + constraints.column("relation_id").id()        + "', '" + constraintsId + "', 'relation_id',         3, 'uuid',     true), " +
                "('" + constraints.column("type").id()               + "', '" + constraintsId + "', 'type',                4, 'char',     true), " +
                "('" + constraints.column("check_expr").id()         + "', '" + constraintsId + "', 'check_expr',          5, 'text',     false), " +
                "('" + constraints.column("source_columns").id()     + "', '" + constraintsId + "', 'source_columns',      6, 'string[]', false), " +
                "('" + constraints.column("target_relation_id").id() + "', '" + constraintsId + "', 'target_relation_id',  7, 'uuid',     false), " +
                "('" + constraints.column("target_columns").id()     + "', '" + constraintsId + "', 'target_columns',      8, 'string[]', false), " +
                "('" + constraints.column("forward_cost").id()       + "', '" + constraintsId + "', 'forward_cost',        9, 'int',      true), " +
                "('" + constraints.column("reverse_cost").id()       + "', '" + constraintsId + "', 'reverse_cost',       10, 'int',      true), " +
                "('" + constraints.column("on_update").id()          + "', '" + constraintsId + "', 'on_update',          11, 'char',     false), " +
                "('" + constraints.column("on_delete").id()          + "', '" + constraintsId + "', 'on_delete',          12, 'char',     false), " +

                "('" + sequences.column("_id").id()       + "', '" + sequencesId + "', '_id',       1, 'uuid',   true), " +
                "('" + sequences.column("name").id()      + "', '" + sequencesId + "', 'name',      2, 'string', true), " +
                "('" + sequences.column("start").id()     + "', '" + sequencesId + "', 'start',     3, 'long',   true), " +
                "('" + sequences.column("increment").id() + "', '" + sequencesId + "', 'increment', 4, 'long',   true), " +
                "('" + sequences.column("maximum").id()   + "', '" + sequencesId + "', 'maximum',   5, 'long',   true), " +
                "('" + sequences.column("cycles").id()    + "', '" + sequencesId + "', 'cycles',    6, 'bool',   true), " +

                "('" + indices.column("_id").id()                     + "', '" + indicesId + "', '_id',                     1, 'uuid',   true), " +
                "('" + indices.column("name").id()                    + "', '" + indicesId + "', 'name',                    2, 'string', true), " +
                "('" + indices.column("relation_id").id()             + "', '" + indicesId + "', 'relation_id',             3, 'uuid',   true), " +
                "('" + indices.column("unique_index").id()            + "', '" + indicesId + "', 'unique_index',            4, 'bool',   false), " +
                "('" + indices.column("columns").id()                 + "', '" + indicesId + "', 'columns',                 5, 'int[]',  false), " +
                "('" + indices.column("expressions").id()             + "', '" + indicesId + "', 'expressions',             6, 'text[]', false), " +
                "('" + indices.column("partial_index_condition").id() + "', '" + indicesId + "', 'partial_index_condition', 7, 'text',   false)"));

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
      } finally {
        hasCoreTables = true;
      }
    }

    if (createPlatformTables) {
      try (EsqlConnection c = esql(pooledConnection())) {

        ////////////////////////////////////////
        // Users
        ////////////////////////////////////////

        c.exec(p.parse("create table _platform.user.User drop undefined(" +
                           "{" +
                           "   name: 'User', " +
                           "   description: 'Users having access to the system', " +
                           "   children: {" +
                           "     user_roles: {" +
                           "       $m: {" +
                           "         type: '_platform.user.UserRole'," +
                           "         parent_link_column: 'user_id'," +
                           "         label: 'User roles'" +
                           "       }," +
                           "       columns: {" +
                           "         _id: {" +
                           "           $m: {" +
                           "             type: 'uuid'," +
                           "             show: false" +
                           "           }" +
                           "         }," +
                           "         role_id: {" +
                           "           $m: {" +
                           "             type: 'uuid'," +
                           "             label: 'Role'," +
                           "             link_table: '_platform.user.Role'," +
                           "             link_table_code_label: 'name'," +
                           "             link_table_code_value: '_id'" +
                           "           }" +
                           "         }" +
                           "       }," +
                           "       columnsOrder: ['_id', 'role_id']" +
                           "     }" +
                           "   }" +
                           "}," +
                           "_id uuid not null, " +
                           "_version long not null default 0, " +
                           "_can_delete bool not null default true, " +
                           "_can_edit bool not null default true, " +
                           "_last_user_update string, " +
                           "_last_update_time datetime, " +

                           "username string not null {" +
                           "   validate_unique: true " +
                           "}, " +
                           "realname string not null, " +
                           "password string not null {" +
                           "   password: true " +
                           "}, " +
                           "email string {" +
                           "   required: two_factor='Y'" +
                           "}, " +
                           "phone string { " +
                           "   required: two_factor='Y'" +
                           "}, " +

                           "two_factor string not null default 'N' {" +
                           "   link_table:'_util_yes_no'" +
                           "}, " +
                           "otp string {" +
                           "   show: false " +
                           "}, " +
                           "otp_expires_at datetime {" +
                           "   show: false " +
                           "}," +
                           "send_otp_to string not null default 'phone' {" +
                           "   link_table: '_otp_channel'" +
                           "}, " +
                           "two_factor_auth_valid_until datetime {" +
                           "   show: false " +
                           "}, " +
                           "client_id string {" +
                           "   show: false " +
                           "}, " +

                           "primary key(_id), " +
                           "unique(username))"));

        c.exec(p.parse("create table _platform.user.Role drop undefined(" +
                           "{" +
                           "   name: 'Role', " +
                           "   description: 'A role is a set of access rights which can be assigned to a user', " +
                           "   children: {" +
                           "     rights: {" +
                           "       $m: {" +
                           "         type: '_platform.user.AccessRight'," +
                           "         inline: false, " +
                           "         parent_link_column: 'role_id'," +
                           "         label: 'Rights'" +
                           "       }," +
                           "       columns: {" +
                           "         _id: {" +
                           "           $m: {" +
                           "             type: 'uuid'," +
                           "             show: false" +
                           "           }" +
                           "         }," +
                           "         access_right: {" +
                           "           $m: {" +
                           "             type: 'string'," +
                           "             label: 'Access right', " +
                           "             span: 24, " +
                           "             required: true" +
                           "           }" +
                           "         }" +
                           "       }," +
                           "       columnsOrder: ['_id', 'access_right']" +
                           "     }" +
                           "   }" +
                           "}, " +
                           "_id uuid not null, " +
                           "_version long not null default 0, " +
                           "_can_delete bool not null default true, " +
                           "_can_edit bool not null default true, " +
                           "_last_user_update string, " +
                           "_last_update_time datetime, " +

                           "name string not null {" +
                           "   validate_unique: true " +
                           "}, " +
                           "description text not null {" +
                           "   lines: 5," +
                           "   span: 16 " +
                           "}, " +

                           "primary key(_id), " +
                           "unique(name))"));

        c.exec(p.parse("create table _platform.user.UserRole drop undefined(" +
                           "{" +
                           "   name: 'User Role', " +
                           "   description: 'A role associated to a user' " +
                           "}, " +
                           "_id uuid not null, " +
                           "_version long not null default 0, " +
                           "_can_delete bool not null default true, " +
                           "_can_edit bool not null default true, " +
                           "_last_user_update string, " +
                           "_last_update_time datetime, " +

                           "user_id uuid not null {" +
                           "   show: false, " +
                           "   link_table: '_platform.user.User'," +
                           "   link_table_code_label: 'username'," +
                           "   link_table_code_value: '_id'" +
                           "}, " +
                           "role_id uuid not null {" +
                           "   link_table: '_platform.user.Role'," +
                           "   link_table_code_label: 'name'," +
                           "   link_table_code_value: '_id'" +
                           "}, " +

                           "primary key(_id), " +
                           "foreign key(user_id) references _platform.user.User(_id) on update cascade on delete cascade," +
                           "foreign key(role_id) references _platform.user.Role(_id) on update cascade on delete cascade)"));

        /*
         * A right is the base permission object granting a user access to a resource/function of the system
         */
        c.exec(p.parse("create table _platform.user.AccessRight drop undefined(" +
                           "{" +
                           "   name: 'Access Right', " +
                           "   description: 'An access right is associated to a user through a role to give her access to an area or function of the system'" +
                           "}, " +
                           "_id uuid not null, " +
                           "_version long not null default 0, " +
                           "_can_delete bool not null default true, " +
                           "_can_edit bool not null default true, " +
                           "_last_user_update string, " +
                           "_last_update_time datetime, " +

                           "role_id uuid not null {" +
                           "   show: false " +
                           "}, " +
                           "access_right text not null {" +
                           "   description: 'A right has 3 names separated by / (e.g. ''abc/def/delete'') with each name " +
                           "representing an access to a function or area of the system. Rights are " +
                           "hierarchical with each name representing a sub-function or sub-area of " +
                           "the name before it (E.g. in ''a/b/c'' b is a part of a and c is a part of b). " +
                           "A set of names or the * universe can also be specified in place of a single name. " +
                           "Some examples of rights are ''a/b/c'', ''a/{b,c,d}/c'', ''a/{b,c}/{c,e}'', ''a/*/*'' " +
                           "and ''*/*/*''', " +
                           "   mask: 'jjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj', " +
                           "   span: 24" +
                           "}," +

                           "primary key(_id), " +
                           "foreign key(role_id) references _platform.user.Role(_id) on update cascade on delete cascade)"));

        /*
         * Audit trail
         */
        c.exec(p.parse("create table _platform.user.Audit drop undefined(" +
                           "{" +
                           "  name: 'Audit Trail', " +
                           "  description: 'An audit trail records a user action in the system', " +
                           "  children: {" +
                           "    columns: {" +
                           "      $m: {" +
                           "        type: '_platform.user.AuditField'," +
                           "        parent_link_column: 'audit_id'," +
                           "        label: 'Modified fields'" +
                           "      }," +
                           "      columns: {" +
                           "        _id: {" +
                           "          $m: {" +
                           "            type: 'uuid'," +
                           "            show: false" +
                           "          }" +
                           "        }," +
                           "        field: {" +
                           "          $m: {" +
                           "            type: 'string'," +
                           "            required: true" +
                           "          }" +
                           "        }," +
                           "        from_value: {" +
                           "          $m: {" +
                           "            type: 'text'" +
                           "          }" +
                           "        }," +
                           "        to_value: {" +
                           "          $m: {" +
                           "            type: 'text'" +
                           "          }" +
                           "        }" +
                           "      }," +
                           "      columnsOrder: ['_id', 'field', 'from_value', 'to_value']" +
                           "    }" +
                           "  }" +
                           "}, " +
                           "_id uuid not null, " +
                           "_version long not null default 0, " +
                           "_can_delete bool not null default true, " +
                           "_can_edit bool not null default true, " +
                           "_last_user_update string, " +
                           "_last_update_time datetime, " +

                           "username string not null {" +
                           "   can_insert: false, " +
                           "   can_edit: false, " +
                           "   can_remove: false, " +
                           "   link_table: `_platform.user.User, " +
                           "   link_table_code_value: 'username', " +
                           "   link_table_code_label: 'realname', " +
                           "   value_label: joinlabel(username, `username, `realname, `_platform.user.User)" +
                           "}, " +
                           "action string not null {" +
                           "   can_insert: false, " +
                           "   can_edit: false, " +
                           "   can_remove: false, " +
                           "   link_table: '_audit_action' " +
                           "}, " +
                           "at datetime not null, " +
                           "on_table string not null {" +
                           "   can_insert: false, " +
                           "   can_edit: false, " +
                           "   can_remove: false, " +
                           "   link_table: `_core.relations, " +
                           "   link_table_code_value: 'name', " +
                           "   link_table_code_label: 'display_name', " +
                           "   value_label: joinlabel(on_table, `name, `display_name, `_core.relations)" +
                           "}, " +
                           "record_id uuid, " +
                           "identifier text, " +

                           "description text not null {" +
                           "   lines: 5," +
                           "   span: 24" +
                           "}," +

                           "file_location string {" +
                           "   external_file: true" +
                           "}, " +

                           "primary key(_id)" +
                           ")"));

        c.exec(p.parse("create table _platform.user.AuditField drop undefined(" +
                           "{" +
                           "   name: 'Audit Trail Modified Field', " +
                           "   description: 'Information on a field modified as part of an audit trail record'" +
                           "}, " +
                           "_id uuid not null, " +
                           "_version long not null default 0, " +
                           "_can_delete bool not null default true, " +
                           "_can_edit bool not null default true, " +
                           "_last_user_update string, " +
                           "_last_update_time datetime, " +

                           "audit_id uuid not null, " +
                           "field string not null, " +
                           "from_value text, " +
                           "to_value text, " +

                           "primary key(_id), " +
                           "foreign key(audit_id) references _platform.user.Audit(_id) on update cascade on delete cascade" +
                           ")"));


        // A category is a name that can be used to tag various entity
        c.exec(p.parse("create table _platform.util.Category drop undefined(" +
                           "{" +
                           "   name: 'Category', " +
                           "   description: 'A category is used to group related items such as reports and filters'" +
                           "}, " +
                           "_id uuid not null, " +
                           "_version long not null default 0, " +
                           "_can_delete bool not null default true, " +
                           "_can_edit bool not null default true, " +
                           "_last_user_update string, " +
                           "_last_update_time datetime, " +

                           "name string not null {" +
                           "   validate_unique: true " +
                           "}, " +

                           "primary key(_id), " +
                           "unique(name))"));


        ///////////////////////////////////////////////////////////////////////////
        // Lookups
        ///////////////////////////////////////////////////////////////////////////

        c.exec(p.parse("create table _platform.lookup.Lookup drop undefined(" +
                           "{" +
                           "  name: 'Lookup', " +
                           "  description: 'A named table of values used in various part of the system', " +
                           "  children: {" +
                           "    links: {" +
                           "      $m: {" +
                           "        type: '_platform.lookup.LookupLink'," +
                           "        parent_link_column: 'source_lookup_id'," +
                           "        label: 'Lookup links'" +
                           "      }," +
                           "      columns: {" +
                           "        _id: {" +
                           "          $m: {" +
                           "            type: 'uuid'," +
                           "            show: false" +
                           "          }" +
                           "        }," +
                           "        name: {" +
                           "          $m: {" +
                           "            type: 'string'," +
                           "            label: 'Link name'," +
                           "            mask: 'Iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii'," +
                           "            required: true," +
                           "            validate_unique: true" +
                           "          }" +
                           "        }," +
                           "        name: {" +
                           "          $m: {" +
                           "            type: 'string'," +
                           "            label: 'Link display name'," +
                           "            required: true" +
                           "          }" +
                           "        }," +
                           "        source_lookup_id: {" +
                           "          $m: {" +
                           "            type: 'uuid'," +
                           "            label: 'Source'," +
                           "            link_table: '_platform.lookup.Lookup'," +
                           "            link_table_code_label: 'name'," +
                           "            link_table_code_value: '_id'," +
                           "            show: false" +
                           "          }" +
                           "        }," +
                           "        target_lookup_id: {" +
                           "          $m: {" +
                           "            type: 'uuid'," +
                           "            label: 'Target'," +
                           "            required: true," +
                           "            link_table: '_platform.lookup.Lookup'," +
                           "            link_table_code_label: 'name'," +
                           "            link_table_code_value: '_id'," +
                           "            value_label: '${joinlabel(this.row.target_lookup_id.$v, \"_id\", \"name\", \"_platform.lookup.Lookup\")}'" +
                           "          }" +
                           "        }" +
                           "      }," +
                           "      columnsOrder: ['_id', 'name', 'display_name', 'target_lookup_id']" +
                           "    }" +
                           "  }" +
                           "}," +

                           "_id uuid not null, " +
                           "_version long not null default 0, " +
                           "_can_delete bool not null default true, " +
                           "_can_edit bool not null default true, " +
                           "_last_user_update string, " +
                           "_last_update_time datetime, " +

                           "name string not null {" +
                           "   validate_unique: true, " +
                           "   mask: 'Iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii'" +
                           "}, " +

                           // @todo Who/which_department owns this lookup

                           "description string, " +
                           "primary key(_id), " +
                           "unique(name))"));

        c.exec(p.parse("create table _platform.lookup.LookupLink drop undefined(" +
                           "{" +
                           "   name: 'Lookup Link', " +
                           "   description: 'The definition of links between values of lookup tables which are used for searching data by associations and for aggregating data in reports'" +
                           "}, " +
                           "_id uuid not null, " +
                           "_version long not null default 0, " +
                           "_can_delete bool not null default true, " +
                           "_can_edit bool not null default true, " +
                           "_last_user_update string, " +
                           "_last_update_time datetime, " +

                           "name string not null {" +
                           "   validate_unique: true, " +
                           "   mask: 'Iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii', " +
                           "   description: 'Start with a letter, follow by letters or digits' " +
                           "}, " +

                           "display_name string not null, " +

                           "source_lookup_id uuid not null {" +
                           "   show: false" +
                           "}, " +

                           "target_lookup_id uuid not null {" +
                           "   label: 'Target', " +
                           "   required: true, " +
                           "   link_table: `_platform.lookup.Lookup, " +
                           "   link_table_code_value: `_id, " +
                           "   link_table_code_label: 'name', " +
                           "   value_label: joinlabel(`target_lookup_id, `_id, `name, `_platform.lookup.Lookup)" +
                           "}, " +

                           "primary key(_id), " +
                           "unique(name), " +
                           "foreign key(source_lookup_id) references _platform.lookup.Lookup(_id), " +
                           "foreign key(target_lookup_id) references _platform.lookup.Lookup(_id))"));

        c.exec(p.parse("create table _platform.lookup.LookupValue drop undefined(" +
                           "{" +
                           "   name: 'Lookup Value', " +
                           "   description: 'The values in a lookup table'," +
                           "   validate_unique: [['lookup_id', 'code', 'lang']] " +
                           "}, " +
                           "_id uuid not null, " +
                           "_version long not null default 0, " +
                           "_can_delete bool not null default true, " +
                           "_can_edit bool not null default true, " +
                           "_last_user_update string, " +
                           "_last_update_time datetime, " +

                           "lookup_id uuid not null {" +
                           "   show:false " +
                           "}, " +
                           "code string not null, " +
                           "alt_code1 string, " +
                           "alt_code2 string, " +
                           "label string not null, " +
                           "lang string not null default 'en' {" +
                           "   label: 'Language', " +
                           "   initial_value: 'en' " +
                           "}, " +

                           "primary key(_id), " +
                           "unique(lookup_id, code, lang), " +
                           "foreign key(lookup_id) references _platform.lookup.Lookup(_id) on delete cascade on update cascade)"));

        c.exec(p.parse("create table _platform.lookup.LookupValueLink drop undefined(" +
                           "{" +
                           "   name: 'Lookup Value Link', " +
                           "   description: 'Links between values of lookup tables, used primarily for searching data by associations and for aggregating data in reports'" +
                           "}, " +
                           "_id uuid not null, " +
                           "_version long not null default 0, " +
                           "_can_delete bool not null default true, " +
                           "_can_edit bool not null default true, " +
                           "_last_user_update string, " +
                           "_last_update_time datetime, " +

                           "lookup_link string not null, " +
                           "source_lookup_value_id uuid not null, " +
                           "target_lookup_value_id uuid not null, " +

                           "primary key(_id), " +
                           "foreign key(source_lookup_value_id) references _platform.lookup.LookupValue(_id), " +
                           "foreign key(target_lookup_value_id) references _platform.lookup.LookupValue(_id))"));

        // Saved filters
        ////////////////////////////////////

        // filters
        c.exec(p.parse("create table _platform.filter.Filter drop undefined(" +
                           "{" +
                           "   name: 'Filter', " +
                           "   description: 'Filters are used to search for data in tables', " +
                           "   validate_unique: [['for_table', 'name']] " +
                           "}, " +
                           "_id uuid not null, " +
                           "_version long not null default 0, " +
                           "_can_delete bool not null default true, " +
                           "_can_edit bool not null default true, " +
                           "_last_user_update string, " +
                           "_last_update_time datetime, " +

                           "name string not null, " +

                           "for_table string not null {" +
                           "   can_insert: false, " +
                           "   can_edit: false, " +
                           "   can_remove: false, " +
                           "   link_table: `_core.relations, " +
                           "   link_table_code_value: 'name', " +
                           "   link_table_code_label: 'display_name', " +
                           "   filter_by: 'leftstr(name, 1) != ''_''', " +
                           "   value_label: joinlabel(for_table, `name, `display_name, `_core.relations)" +
                           "}," +

                           "unique(for_table, name), " +
                           "primary key(_id))"));

        // filters conditions
        c.exec(p.parse("create table _platform.filter.FilterCondition drop undefined(" +
                           "{" +
                           "  name: 'Filter Condition'," +
                           "  description: 'A condition one part of a filter and can be combined with other conditions through boolean operators'," +
                           "  sequence_column: 'seq'" +
                           "}, " +
                           "_id uuid not null, " +
                           "_version long not null default 0, " +
                           "_can_delete bool not null default true, " +
                           "_can_edit bool not null default true, " +
                           "_last_user_update string, " +
                           "_last_update_time datetime, " +

                           "filter_id uuid not null {" +
                           "   link_table: `_platform.filter.Filter, " +
                           "   link_table_code_value: `_id, " +
                           "   link_table_code_label: 'name', " +
                           "   value_label: joinlabel(`filter_id, `_id, `name, `_platform.filter.Filter)" +
                           "}, " +
                           "link_op string not null default 'and', " +
                           "expression text not null, " +
                           "operation string not null {" +
                           "   link_table: `_platform.filter.FilterOperation, " +
                           "   link_table_code_value: `op_code, " +
                           "   link_table_code_label: `name, " +
                           "   value_label: joinlabel(operation, `op_code, `name, `_platform.filter.FilterOperation)" +
                           "}, " +
                           "value text, " +
                           "exclude bool default true, " +
                           "seq int not null default 1, " +

                           "primary key(_id)," +
                           "foreign key(filter_id) references _platform.filter.Filter(_id) on delete cascade on update cascade)"));

        // filters operations
        c.exec(p.parse("create table _platform.filter.FilterOperation drop undefined(" +
                           "{" +
                           "  name: 'Filter Operation'," +
                           "  description: 'Operations which can be used in filters'," +
                           "  sequence_column: 'seq'" +
                           "}, " +
                           "_id uuid not null, " +
                           "_version long not null default 0, " +
                           "_can_delete bool not null default true, " +
                           "_can_edit bool not null default true, " +
                           "_last_user_update string, " +
                           "_last_update_time datetime, " +

                           "op_code string not null, " +
                           "name string not null, " +
                           "operator string not null, " +
                           "for_all_types bool not null default false, " +
                           "require_value bool not null default true, " +
                           "value_type string, " +
                           "parse_function text, " +
                           "seq int not null default 1, " +

                           "primary key(_id), " +
                           "unique(op_code), " +
                           "unique(name))"));

        // filters operation supported types
        c.exec(p.parse("create table _platform.filter.FilterOperationType drop undefined(" +
                           "{" +
                           "   name: 'Filter Operation Type', " +
                           "   description: 'The types of data (text, number, etc.) that an operation accepts'" +
                           "}, " +
                           "_id uuid not null, " +
                           "_version long not null default 0, " +
                           "_can_delete bool not null default true, " +
                           "_can_edit bool not null default true, " +
                           "_last_user_update string, " +
                           "_last_update_time datetime, " +

                           "filter_operation_id uuid not null {" +
                           "   link_table: `_platform.filter.FilterOperation, " +
                           "   link_table_code_value: `_id, " +
                           "   link_table_code_label: 'name', " +
                           "   value_label: joinlabel(`filter_operation_id, `_id, `name, `_platform.filter.FilterOperation)" +
                           "}, " +
                           "type string not null, " +

                           "primary key(_id)," +
                           "foreign key(filter_operation_id) references _platform.filter.FilterOperation(_id) on delete cascade on update cascade)"));

        // filters column references (for faster search of conforming filters)
//            c.exec(parse("create table _platform.filter.FilterColumn drop undefined(" +
//                    "{" +
//                    "   name: 'Filter column', " +
//                    "   description: 'Column references in filter conditions, used for faster search of conforming filters'" +
//                    "}, " +
//                    "_id uuid not null, " +
//                    "_version long not null default 0, " +
//                    "_can_delete bool not null default true, " +
//                    "_can_edit bool not null default true, " +
//                    "_last_user_update string, " +
//                    "_last_update_time datetime, " +
//
//                    "filter_condition_id uuid not null {" +
//                    "   link_table: `_platform.filter.FilterCondition, " +
//                    "   link_table_code_value: `_id, " +
//                    "   link_table_code_label: 'expression', " +
//                    "   value_label: joinlabel(`filter_condition_id, `_id, `expression, `_platform.filter.FilterCondition)" +
//                    "}, " +
//                    "name string not null, " +
//
//                    "primary key(_id)," +
//                    "foreign key(filter_condition_id) references _platform.filter.FilterCondition(_id) on delete cascade on update cascade)"));


        // Reports
        ///////////////////////////////

        /*-*******************************************************************************
         * A report splitter is used to create a split report each elements applied over
         * a different subset of the data. The splitter returns a list of filters which
         * are applied to the underlying data to produce one split of the report.
         ********************************************************************************/
        c.exec(p.parse("create table _platform.report.Splitter drop undefined(" +
                           "{" +
                           "   name: 'Report Splitter Function', " +
                           "   description: 'A function returning a list of filters to split a report'" +
                           "}, " +
                           "_id uuid not null, " +
                           "_version long not null default 0, " +
                           "_can_delete bool not null default true, " +
                           "_can_edit bool not null default true, " +
                           "_last_user_update string, " +
                           "_last_update_time datetime, " +

                           "name string not null {" +
                           "   validate_unique: true," +
                           "   span: 8 " +
                           "}, " +

                           "func text not null {" +
                           "   description: 'Function returning a list of filters to split a report', " +
                           "   browse: false, " +
                           "   required: true, " +
                           "   lines: 30, " +
                           "   span: 24, " +
                           "   code_language:'javascript' " +
                           "}, " +

                           "unique(name), " +
                           "primary key(_id))"));

        // Report visualisation functions
        c.exec(p.parse("create table _platform.report.Visualisation drop undefined(" +
                           "{" +
                           "   name: 'Report Visualisation Function', " +
                           "   description: 'The visualisation function transforms loaded data and produces the output of reports' " +
                           "}, " +
                           "_id uuid not null, " +
                           "_version long not null default 0, " +
                           "_can_delete bool not null default true, " +
                           "_can_edit bool not null default true, " +
                           "_last_user_update string, " +
                           "_last_update_time datetime, " +

                           "name string not null {" +
                           "   validate_unique: true," +
                           "   span: 8 " +
                           "}, " +
                           "description string {" +
                           "   span: 16" +
                           "}, " +

                           "group_type string not null default 'normal' { " +
                           "   description: 'Group type used for the query. Normal grouping, rollup or cube are supported.'," +
                           "   span: 8, " +
                           "   link_table: '_group_type' " +
                           "}, " +
                           "output_type string not null default 'excel' {" +
                           "   description: 'Output type: excel, pdf or text'," +
                           "   span: 8, " +
                           "   link_table: '_output_type' " +
                           "}," +

                           "requires_scrollable_format bool not null default false, " +

                           "layout text not null {" +
                           "   description: 'Function to layout the data loaded by the report query', " +
                           "   browse: false, " +
                           "   required: true, " +
                           "   lines: 30, " +
                           "   span: 24, " +
                           "   code_language:'javascript' " +
                           "}, " +

                           "unique(name), " +
                           "primary key(_id))"));

        // report
        c.exec(p.parse("create table _platform.report.Report drop undefined(" +
                           "{" +
                           "   name: 'Report', " +
                           "   description: 'Definition of a report in the system', " +
                           "   children: {" +
                           "     columns: {" +
                           "       $m: {" +
                           "         type: '_platform.report.ReportColumn', " +
                           "         parent_link_column: 'report_id', " +
                           "         sequence_column: 'seq', " +
                           "         inline: false, " +
                           "         section: 'Columns', " +
                           "         label: 'Column' " +
                           "       }," +
                           "       columns: {" +
                           "         _id: {" +
                           "           $m: {" +
                           "             type: 'uuid'," +
                           "             show: false" +
                           "           }" +
                           "         }," +
                           "         expression: {" +
                           "           $m: {" +
                           "             type: 'text', " +
                           "             label: 'Expression', " +
                           "             required: true, " +
                           "             span: 24," +
                           "             lines: 5 " +
                           "           }" +
                           "         }, " +
                           "         name: {" +
                           "           $m: {" +
                           "             type: 'string', " +
                           "             label: 'Name', " +
                           "             mask: 'Iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii'" +
                           "           }" +
                           "         }, " +
                           "         grouped: { " +
                           "           $m: {" +
                           "             type: 'bool', " +
                           "             label: 'Grouped' " +
                           "           }" +
                           "         }, " +
                           "         separate_column: {" +
                           "           $m: {" +
                           "             type: 'bool', " +
                           "             label: 'Show in separate column', " +
                           "             initial_value: true" +
                           "           }" +
                           "         }, " +
                           "         no_repeat: {" +
                           "           $m: {" +
                           "             type: 'bool', " +
                           "             label: 'Hide consecutive repeated values', " +
                           "             initial_value: false" +
                           "           }" +
                           "         }, " +
                           "         show_aggregate_only: {" +
                           "           $m: {" +
                           "             type: 'bool', " +
                           "             label: 'Show aggregates only', " +
                           "             initial_value: false" +
                           "           }" +
                           "         }, " +
                           "         subtotal: {" +
                           "           $m: {" +
                           "             type: 'bool', " +
                           "             label: 'Show subtotals', " +
                           "             initial_value: true" +
                           "           }" +
                           "         }, " +
                           "         seq: {" +
                           "           $m: {" +
                           "             type: 'int'," +
                           "             label: 'Sequence', " +
                           "             show: false" +
                           "           }" +
                           "         } " +
                           "       }," +
                           "       columnsOrder: ['_id', 'expression', 'name', 'grouped', 'separate_column', 'no_repeat', 'show_aggregate_only', 'subtotal', 'seq']" +
                           "     }," +
                           "     joins: {" +
                           "       $m: {" +
                           "         type: '_platform.report.ReportJoin', " +
                           "         parent_link_column: 'report_id', " +
                           "         inline: false, " +
                           "         sequence_column: 'seq', " +
                           "         section: 'Tables', " +
                           "         label: 'Tables' " +
                           "       }," +
                           "       columns: {" +
                           "         _id: {" +
                           "           $m: {" +
                           "             type: 'uuid'," +
                           "             show: false" +
                           "           }" +
                           "         }," +
                           "         join_type: {" +
                           "           $m: {" +
                           "             type: 'string', " +
                           "             label: 'Join type', " +
                           "             can_insert: false, " +
                           "             can_edit: false, " +
                           "             can_remove: false, " +
                           "             required: true, " +
                           "             link_table: '_join_type' " +
                           "           }" +
                           "         }, " +
//                    "         join_table: {" +
//                    "           $m: {" +
//                    "             type: 'text', " +
//                    "             required: true, " +
//                    "             can_insert: false, " +
//                    "             can_edit: false, " +
//                    "             can_remove: false, " +
//                    "             link_table: '_core.relations', " +
//                    "             link_table_code_value: 'name', " +
//                    "             link_table_code_label: 'display_name', " +
//                    "             filter_by: 'leftstr(name, 1) != ''_''', " +
//                    "             value_label: '${joinlabel(this.row.join_table.$v, \"name\", \"display_name\", \"_core.relations\")}'" +
//                    "           }" +
                           "         join_table: {" +
                           "           $m: {" +
                           "             type: 'text', " +
                           "             label: 'Join table', " +
                           "             required: true, " +
                           "             span: 24, " +
                           "             lines: 10" +
                           "           }" +
                           "         }, " +
                           "         table_alias: { " +
                           "           $m: {" +
                           "             type: 'string', " +
                           "             label: 'Table alias', " +
                           "             required: true, " +
                           "             mask: 'Iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii'" +
                           "           }" +
                           "         }, " +
                           "         join_on: {" +
                           "           $m: {" +
                           "             type: 'text', " +
                           "             label: 'Join on' " +
                           "           }" +
                           "         }, " +
                           "         seq: {" +
                           "           $m: {" +
                           "             type: 'int', " +
                           "             label: 'Sequence', " +
                           "             show: false" +
                           "           }" +
                           "         } " +
                           "       }," +
                           "       columnsOrder: ['_id', 'join_type', 'join_table', 'table_alias', 'join_on', 'seq']" +
                           "     }," +
                           "     user_data: {" +
                           "       $m: {" +
                           "         type: '_platform.report.ReportUserData', " +
                           "         parent_link_column: 'report_id', " +
                           "         sequence_column: 'seq', " +
                           "         inline: false," +
                           "         label: 'User data'," +
                           "         section: 'User data' " +
                           "       }," +
                           "       columns: {" +
                           "         _id: {" +
                           "           $m: {" +
                           "             type: 'uuid', " +
                           "             show: false" +
                           "           }" +
                           "         }," +
                           "         report_id: {" +
                           "           $m: {" +
                           "             type: 'uuid', " +
                           "             show: false" +
                           "           }" +
                           "         }," +
                           "         name: {" +
                           "           $m: {" +
                           "             type: 'string', " +
                           "             label: 'Field name'," +
                           "             mask: 'Iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii'," +
                           "             required: true" +
                           "           }" +
                           "         }," +
                           "         type: {" +
                           "           $m: {" +
                           "             type: 'string', " +
                           "             label: 'Type', " +
                           "             required: true, " +
                           "             link_table: '_basetype'," +
                           "             value_label: '${lookuplabel(this.row.type.$v, \"_basetype\")}'" +
                           "           }" +
                           "         }," +
                           "         seq: {" +
                           "           $m: {" +
                           "             type: 'int'," +
                           "             label: 'Sequence', " +
                           "             show: false " +
                           "           }" +
                           "         } " +
                           "       }," +
                           "       columnsOrder: ['_id', 'report_id', 'name', 'type', 'seq']" +
                           "     }" +
                           "   }" +
                           "}, " +
                           "_id uuid not null, " +
                           "_version long not null default 0, " +
                           "_can_delete bool not null default true, " +
                           "_can_edit bool not null default true, " +
                           "_last_user_update string, " +
                           "_last_update_time datetime, " +

                           "name string not null {" +
                           "   section: 'Report', " +
                           "   span: 24, " +
                           "   validate_unique: true " +
                           "}, " +
                           "description string {" +
                           "   section: 'Report', " +
                           "   browse: false, " +
                           "   lines: 5, " +
                           "   span: 24 " +
                           "}, " +
                           "category string not null {" +
                           "   section: 'Report', " +
                           "   browse: false, " +
                           "   link_table: '_platform.util.Category', " +
                           "   link_table_code_value: 'name', " +
                           "   link_table_code_label: 'name' " +
                           "}, " +

                           "hide bool {" +
                           "   section: 'Report', " +
                           "   browse: false" +
                           "}, " +

                           "title string {" +
                           "   section: 'Report', " +
                           "   span: 24, " +
                           "   browse: false, " +
                           "   value_expression: '${this.row._id.$v || this.row.title.$m._changed " +
                           "                                       ? this.row.title.$v " +
                           "                                       : this.row.display_name.$v}'" +
                           "}, " +
                           "subtitle string {" +
                           "   section: 'Report', " +
                           "   span: 24, " +
                           "   browse: false, " +
                           "   value_expression: '${this.row this.row.description.$v}'" +
                           "}, " +

                           "visualisation_id uuid not null {" +
                           "   section: 'Report', " +
                           "   browse: false, " +
                           "   link_table: '_platform.report.Visualisation', " +
                           "   link_table_code_value: '_id', " +
                           "   link_table_code_label: 'name', " +
                           "   value_label: joinlabel(visualisation_id, '_id', 'name', '_platform.report.Visualisation')" +
                           "}, " +

                           "splitter_id uuid {" +
                           "   section: 'Report', " +
                           "   browse: false, " +
                           "   link_table: '_platform.report.Splitter', " +
                           "   link_table_code_value: '_id', " +
                           "   link_table_code_label: 'name', " +
                           "   value_label: joinlabel(splitter_id, '_id', 'name', '_platform.report.Splitter')" +
                           "}, " +

                           "for_table string not null {" +
                           "   section: 'Report', " +
                           "   browse: false, " +
                           "   can_insert: false, " +
                           "   can_edit: false, " +
                           "   can_remove: false, " +
                           "   link_table: `_core.relations, " +
                           "   link_table_code_value: 'name', " +
                           "   link_table_code_label: 'display_name', " +
                           "   filter_by: 'leftstr(name, 1) != ''_''', " +
                           "   value_label: joinlabel(for_table, `name, `display_name, `_core.relations)" +
                           "}," +

                           "show_aggregates_first bool default false {" +
                           "   section: 'Report', " +
                           "   browse: false " +
                           "}, " +

                           "filter_by string { " +
                           "   section: 'Query', " +
                           "   browse: false, " +
                           "   lines: 4, " +
                           "   span: 24 " +
                           "}, " +
                           "group_filter string { " +
                           "   section: 'Query', " +
                           "   browse: false, " +
                           "   lines: 4, " +
                           "   span: 24 " +
                           "}, " +
                           "order_by string { " +
                           "   section: 'Query', " +
                           "   browse: false, " +
                           "   lines: 4, " +
                           "   span: 24 " +
                           "}, " +
                           "group_type string { " +
                           "   section: 'Query', " +
                           "   description: 'Group type used for the query, overriding visualisation group type'," +
                           "   browse: false, " +
                           "   link_table: '_group_type' " +
                           "}, " +

                           "query_transform text {" +
                           "   description: 'Function to transform the query', " +
                           "   section: 'Query', " +
                           "   browse: false, " +
                           "   lines: 20, " +
                           "   span: 24, " +
                           "   code_language:'javascript' " +
                           "}, " +

                           "title_style text {" +
                           "   browse: false, " +
                           "   section: 'Styles', " +
                           "   span: 24, " +
                           "   lines: 8" +
                           "}, " +
                           "subtitle_style text {" +
                           "   browse: false, " +
                           "   section: 'Styles', " +
                           "   span: 24, " +
                           "   lines: 8" +
                           "}, " +
                           "header_style text {" +
                           "   browse: false, " +
                           "   section: 'Styles', " +
                           "   span: 24, " +
                           "   lines: 8" +
                           "}, " +
                           "aggregate_row_style text {" +
                           "   browse: false, " +
                           "   section: 'Styles', " +
                           "   span: 24, " +
                           "   lines: 8" +
                           "}, " +

                           "unique(name), " +
                           "primary key(_id)," +
                           "foreign key(visualisation_id) references _platform.report.Visualisation(_id) on delete cascade on update cascade)"));

        c.exec(p.parse("create table _platform.report.ReportColumn drop undefined(" +
                           "{" +
                           "  name: 'Report Column'," +
                           "  description: 'A column selected for display in a report'," +
                           "  sequence_column: 'seq', " +
                           "  children: {" +
                           "    attributes: {" +
                           "      $m: {" +
                           "        type: '_platform.report.ReportColumnAttribute', " +
                           "        parent_link_column: 'report_column_id', " +
                           "        label: 'Attributes'" +
                           "      }," +
                           "      columns: {" +
                           "        _id: {" +
                           "          $m: {" +
                           "            type: 'uuid', " +
                           "            show: false" +
                           "          }" +
                           "        }," +
                           "        report_column_id: {" +
                           "          $m: {" +
                           "            type: 'uuid', " +
                           "            show: false" +
                           "          }" +
                           "        }," +
                           "        attribute: {" +
                           "          $m: {" +
                           "            type: 'string', " +
                           "            label: 'Attribute'," +
                           "            mask: 'Iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii'," +
                           "            required: true, " +
                           "            link_table: '_report_field_attribute'," +
                           "            value_label: '${lookuplabel(this.row.attribute.$v, \"_report_field_attribute\")}'" +
                           "          }" +
                           "        }," +
                           "        value: {" +
                           "          $m: {" +
                           "            type: 'string'," +
                           "            label: 'Value'" +
                           "          }" +
                           "        }" +
                           "      }," +
                           "      columnsOrder: ['_id', 'report_column_id', 'attribute', 'value']" +
                           "    }" +
                           "  }" +
                           "}, " +
                           "_id uuid not null, " +
                           "_version long not null default 0, " +
                           "_can_delete bool not null default true, " +
                           "_can_edit bool not null default true, " +
                           "_last_user_update string, " +
                           "_last_update_time datetime, " +

                           "report_id uuid not null {" +
                           "   show: false" +
                           "}, " +
                           "expression text not null {" +
                           "   span: 24," +
                           "   lines: 5 " +
                           "}, " +
                           "name string {" +
                           "   mask: 'Iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii'" +
                           "}, " +
                           "grouped bool default true, " +
                           "separate_column bool default true, " +
                           "displayed_as_row bool default true {" +
                           "   read_only: grouped!=true, " +
                           "   active_text: 'Row', " +
                           "   inactive_text: 'Column' " +
                           "}, " +
                           "no_repeat bool default false {" +
                           "   read_only: grouped!=true " +
                           "}," +
                           "subtotal bool default true {" +
                           "   read_only: grouped!=true, " +
                           "   initial_value: true" +
                           "}," +
                           "show_aggregate_only bool default true {" +
                           "   read_only: grouped=true, " +
                           "   initial_value: true" +
                           "}," +
                           "seq int not null default 1 {" +
                           "   show: false" +
                           "}, " +

                           "primary key(_id), " +
                           "foreign key(report_id) references _platform.report.Report(_id) on delete cascade)"));

        c.exec(p.parse("create table _platform.report.ReportJoin drop undefined(" +
                           "{" +
                           "  name: 'Report Join Table'," +
                           "  description: 'A table joined to the primary table to select data for a report'" +
                           "}, " +

                           "_id uuid not null, " +
                           "_version long not null default 0, " +
                           "_can_delete bool not null default true, " +
                           "_can_edit bool not null default true, " +
                           "_last_user_update string, " +
                           "_last_update_time datetime, " +

                           "report_id uuid not null {" +
                           "   show: false" +
                           "}, " +

                           "join_type string not null default 'join' {" +
                           "   link_table: '_join_type', " +
                           "   initial_value: 'join' " +
                           "}, " +
//          "join_table string not null {" +
//          "   link_table: `_core.relations, " +
//          "   link_table_code_value: 'name', " +
//          "   link_table_code_label: 'display_name', " +
//          "   filter_by: 'leftstr(name, 1) != ''_''', " +
//          "   value_label: joinlabel(join_table, `name, `display_name, `_core.relations)" +
//          "}, " +
                           "join_table text not null {" +
                           "   span: 24, " +
                           "   lines: 10, " +
                           "   code_language:'sql' " +

                           "}, " +
                           "table_alias string not null {" +
                           "   mask: 'Iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii'" +
                           "}, " +
                           "join_on text {" +
                           "   span: 24, " +
                           "   lines: 5, " +
                           "   code_language:'sql' " +
                           "}, " +
                           "seq int not null default 1, " +

                           "primary key(_id), " +
                           "foreign key(report_id) references _platform.report.Report(_id) on delete cascade)"));


        c.exec(p.parse("create table _platform.report.ReportColumnAttribute drop undefined(" +
                           "{" +
                           "  name: 'Report Column Attribute'," +
                           "  description: 'Attributes of selected columns in reports controlling their display'" +
                           "}, " +
                           "_id uuid not null, " +
                           "_version long not null default 0, " +
                           "_can_delete bool not null default true, " +
                           "_can_edit bool not null default true, " +
                           "_last_user_update string, " +
                           "_last_update_time datetime, " +

                           "report_column_id uuid not null {" +
                           "   show: false" +
                           "}, " +
                           "attribute string not null { " +
                           "   mask: 'Iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii'," +
                           "   link_table: `_report_field_attribute, " +
                           "   value_label: lookuplabel(attribute, `_report_field_attribute)" +
                           "}," +
                           "value text, " +

                           "primary key(_id), " +
                           "unique(report_column_id, attribute), " +
                           "foreign key(report_column_id) references _platform.report.ReportColumn(_id) on delete cascade on update cascade)"));

        c.exec(p.parse("create table _platform.report.ReportUserData drop undefined(" +
                           "{" +
                           "  name: 'Report User Data'," +
                           "  description: 'User provided data to merge with report definition and query', " +
                           "  sequence_column: 'seq', " +
                           "  children: {" +
                           "    attributes: {" +
                           "      $m: {" +
                           "        type: '_platform.report.ReportUserDataAttribute', " +
                           "        parent_link_column: 'report_user_data_id', " +
                           "        label: 'Attributes'" +
                           "      }," +
                           "      columns: {" +
                           "        _id: {" +
                           "          $m: {" +
                           "            type: 'uuid', " +
                           "            show: false" +
                           "          }" +
                           "        }," +
                           "        report_user_data_id: {" +
                           "          $m: {" +
                           "            type: 'uuid', " +
                           "            show: false" +
                           "          }" +
                           "        }," +
                           "        attribute: {" +
                           "          $m: {" +
                           "            type: 'string', " +
                           "            label: 'Attribute'," +
                           "            mask: 'Iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii'," +
                           "            required: true, " +
                           "            link_table: '_field_attribute'," +
                           "            value_label: '${lookuplabel(this.row.attribute.$v, \"_field_attribute\")}'" +
                           "          }" +
                           "        }," +
                           "        value: {" +
                           "          $m: {" +
                           "            type: 'string'," +
                           "            label: 'Value'," +
                           "            required: true" +
                           "          }" +
                           "        }" +
                           "      }," +
                           "      columnsOrder: ['_id', 'report_user_data_id', 'attribute', 'value']" +
                           "    }" +
                           "  }" +
                           "}, " +
                           "_id uuid not null, " +
                           "_version long not null default 0, " +
                           "_can_delete bool not null default true, " +
                           "_can_edit bool not null default true, " +
                           "_last_user_update string, " +
                           "_last_update_time datetime, " +

                           "report_id uuid not null {" +
                           "   show: false" +
                           "}," +
                           "name string not null {" +
                           "   mask: 'Iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii' " +
                           "}," +
                           "type string not null {" +
                           "   link_table: `_basetype, " +
                           "   value_label: lookuplabel(type, `_basetype)" +
                           "}," +
                           "seq int default 1 {" +
                           "   show: false" +
                           "}," +

                           "primary key(_id)," +
                           "unique(report_id, name)," +
                           "foreign key(report_id) references _platform.report.Report(_id) on update cascade on delete cascade)"));

        c.exec(p.parse("create table _platform.report.ReportUserDataAttribute drop undefined(" +
                           "{" +
                           "  name: 'Report User Data Field Attribute'," +
                           "  description: 'Attributes of fields in user data of report selections'" +
                           "}, " +
                           "_id uuid not null, " +
                           "_version long not null default 0, " +
                           "_can_delete bool not null default true, " +
                           "_can_edit bool not null default true, " +
                           "_last_user_update string, " +
                           "_last_update_time datetime, " +

                           "report_user_data_id uuid not null {" +
                           "   show: false" +
                           "}, " +
                           "attribute string not null { " +
                           "   mask: 'Iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii'," +
                           "   link_table: `_field_attribute, " +
                           "   value_label: lookuplabel(attribute, `_field_attribute)" +
                           "}," +
                           "value text, " +

                           "primary key(_id), " +
                           "unique(report_user_data_id, attribute), " +
                           "foreign key(report_user_data_id) references _platform.report.ReportUserData(_id) on delete cascade on update cascade)"));

        /* ****************************
         *  Report distribution
         * ****************************/
        // Function producing the list of recipients to send the report to
        c.exec(p.parse("create table _platform.report.DistributionListFunction drop undefined(" +
                           "{" +
                           "   name: 'Distribution List Function', " +
                           "   description: 'A function producing the list of recipients to send a report to'" +
                           "}, " +
                           "_id uuid not null, " +
                           "_version long not null default 0, " +
                           "_can_delete bool not null default true, " +
                           "_can_edit bool not null default true, " +
                           "_last_user_update string, " +
                           "_last_update_time datetime, " +

                           "name string not null {" +
                           "   validate_unique: true," +
                           "   span: 8 " +
                           "}, " +

                           "func text not null {" +
                           "   description: 'Function returning list of recipients, and associated information, to send report to', " +
                           "   browse: false, " +
                           "   required: true, " +
                           "   lines: 30, " +
                           "   span: 24, " +
                           "   code_language:'javascript' " +
                           "}, " +

                           "unique(name), " +
                           "primary key(_id))"));

        // Distribution list
        c.exec(p.parse("create table _platform.report.DistributionList drop undefined(" +
                           "{" +
                           "  name: 'Distribution List', " +
                           "  description: 'A named static list of recipients to send a report to', " +
                           "  children: {" +
                           "    attributes: {" +
                           "      $m: {" +
                           "        type: '_platform.report.DistributionListEntry', " +
                           "        parent_link_column: 'distribution_list_id', " +
                           "        inline: false," +
                           "        label: 'Entries'" +
                           "      }," +
                           "      columns: {" +
                           "        _id: {" +
                           "          $m: {" +
                           "            type: 'uuid', " +
                           "            show: false" +
                           "          }" +
                           "        }," +
                           "        distribution_list_id: {" +
                           "          $m: {" +
                           "            type: 'uuid', " +
                           "            show: false" +
                           "          }" +
                           "        }," +
                           "        filter_by: {" +
                           "          $m: {" +
                           "            type: 'text', " +
                           "            label: 'Filter by', " +
                           "            span: 24, " +
                           "            lines: 4, " +
                           "            required: true " +
                           "          }" +
                           "        }," +
                           "        email_to: {" +
                           "          $m: {" +
                           "            type: 'text', " +
                           "            label: 'Email to', " +
                           "            span: 24, " +
                           "            lines: 4, " +
                           "            required: true" +
                           "          }" +
                           "        }, " +
                           "        label: {" +
                           "          $m: {" +
                           "            type: 'string', " +
                           "            label: 'Label', " +
                           "            span: 12 " +
                           "          }" +
                           "        }, " +
                           "        password: {" +
                           "          $m: {" +
                           "            type: 'string'," +
                           "            label: 'Password'" +
                           "          }" +
                           "        } " +
                           "      }," +
                           "      columnsOrder: ['_id', 'distribution_list_id', 'filter_by', 'email_to', 'label', 'password']" +
                           "    }" +
                           "  }" +
                           "}, " +
                           "_id uuid not null, " +
                           "_version long not null default 0, " +
                           "_can_delete bool not null default true, " +
                           "_can_edit bool not null default true, " +
                           "_last_user_update string, " +
                           "_last_update_time datetime, " +

                           "name string not null {" +
                           "   validate_unique: true," +
                           "   span: 8 " +
                           "}, " +

                           "unique(name), " +
                           "primary key(_id))"));

        // Entry in a distribution list
        c.exec(p.parse("create table _platform.report.DistributionListEntry drop undefined(" +
                           "{" +
                           "   name: 'Distribution List Entry', " +
                           "   description: 'An entry in a distribution list'" +
                           "}, " +
                           "_id uuid not null, " +
                           "_version long not null default 0, " +
                           "_can_delete bool not null default true, " +
                           "_can_edit bool not null default true, " +
                           "_last_user_update string, " +
                           "_last_update_time datetime, " +

                           "distribution_list_id uuid not null {" +
                           "   browse: false, " +
                           "   link_table: '_platform.report.DistributionList', " +
                           "   link_table_code_value: '_id', " +
                           "   link_table_code_label: 'name', " +
                           "   value_label: joinlabel(distribution_list_id, '_id', 'name', '_platform.report.DistributionList')" +
                           "}, " +

                           "filter_by text not null {" +
                           "   span: 24, " +
                           "   lines: 4 " +
                           "}, " +
                           "email_to text not null { " +
                           "   span: 24, " +
                           "   lines: 4 " +
                           "}, " +
                           "label string {" +
                           "   span: 12" +
                           "}, " +
                           "password string, " +

                           "primary key(_id))"));

        c.exec(p.parse("create table _platform.report.Distribution drop undefined(" +
                           "{" +
                           "  name: 'Report Distribution'," +
                           "  description: 'Definition of how to distribute a report'" +
                           "}, " +
                           "_id uuid not null, " +
                           "_version long not null default 0, " +
                           "_can_delete bool not null default true, " +
                           "_can_edit bool not null default true, " +
                           "_last_user_update string, " +
                           "_last_update_time datetime, " +

                           "name string not null {" +
                           "   validate_unique: true," +
                           "   span: 8 " +
                           "}, " +

                           "report_id uuid not null {" +
                           "   browse: false, " +
                           "   link_table: '_platform.report.Report', " +
                           "   link_table_code_value: '_id', " +
                           "   link_table_code_label: 'name', " +
                           "   value_label: joinlabel(report_id, '_id', 'name', '_platform.report.Report')" +
                           "}, " +

                           "email_from string not null, " +

                           "subject string not null {" +
                           "   description: 'Function to produce the subject of mail to send to recipient', " +
                           "   lines: 3," +
                           "   span: 24" +
                           "}, " +

                           "message text not null {" +
                           "   description: 'Function to produce the body of message of mail to send to recipient', " +
                           "   browse: false, " +
                           "   lines: 4," +
                           "   span: 24" +
                           "}, " +

                           "filename string {" +
                           "   description: 'Function to produce the name of the report to send to recipient', " +
                           "   lines: 3," +
                           "   span: 24" +
                           "}, " +

                           "list_function_id uuid not null {" +
                           "   browse: false, " +
                           "   link_table: '_platform.report.DistributionListFunction', " +
                           "   link_table_code_value: '_id', " +
                           "   link_table_code_label: 'name', " +
                           "   value_label: joinlabel(list_function_id, '_id', 'name', '_platform.report.DistributionListFunction')" +
                           "}, " +

                           "list_function_parameters text {" +
                           "   description: 'Parameters to the list function', " +
                           "   browse: false, " +
                           "   lines: 4," +
                           "   span: 24" +
                           "}, " +

                           "primary key(_id), " +
                           "unique(name), " +
                           "foreign key(report_id) references _platform.report.Report(_id) on update cascade on delete cascade, " +
                           "foreign key(list_function_id) references _platform.report.DistributionListFunction(_id) on update cascade on delete cascade)"));


        c.exec(p.parse("create table _platform.report.SendLog drop undefined(" +
                           "{" +
                           "  name: 'Report Distribution Log'," +
                           "  description: 'Log of distributed reports'" +
                           "}, " +
                           "_id uuid not null, " +
                           "_version long not null default 0, " +
                           "_can_delete bool not null default true, " +
                           "_can_edit bool not null default true, " +
                           "_last_user_update string, " +
                           "_last_update_time datetime, " +

                           "report_id uuid {" +
                           "   link_table: '_platform.report.Report', " +
                           "   link_table_code_value: '<>._id', " +
                           "   link_table_code_label: '<>.name', " +
                           "   value_label: joinlabel(report_id, `_id, `name, `_platform.report.Report) " +
                           "}," +

                           "sent_at datetime not null, " +
                           "sent_by string not null, " +
                           "sent_to text not null, " +

                           "label string, " +

                           "subject text not null, " +
                           "message text not null, " +

                           "filename string, " +

                           "file_location string {" +
                           "   external_file: true" +
                           "}, " +

                           "password string, " +

                           "primary key(_id), " +
                           "foreign key(report_id) references _platform.report.Report(_id) on update cascade on delete cascade)"));


        // Third-party systems interfacing
        ////////////////////////////////////////////
        c.exec(p.parse("create table _platform.external.Connector drop undefined(" +
                           "{" +
                           "   name: 'External System Connector', " +
                           "   description: 'Defines the connection to an external system'" +
                           "}, " +
                           "_id uuid not null, " +
                           "_version long not null default 0, " +
                           "_can_delete bool not null default true, " +
                           "_can_edit bool not null default true, " +
                           "_last_user_update string, " +
                           "_last_update_time datetime, " +

                           "name string not null {" +
                           "   validate_unique: true," +
                           "   span: 8 " +
                           "}, " +
                           "description string {" +
                           "   span: 16" +
                           "}, " +

                           "product string not null {" +
                           "   link_table: '_external_product'," +
                           "   link_table_show_code: false " +
                           "}, " +
                           "host string not null, " +
                           "port int not null, " +
                           "instance string not null, " +
                           "username string not null, " +
                           "password string not null, " +

                           "unique(name), " +
                           "primary key(_id))"));

        c.exec(p.parse("create table _platform.external.Function drop undefined(" +
                           "{" +
                           "   name: 'External Interface Function', " +
                           "   description: 'A function interfacing with external systems' " +
                           "}, " +
                           "_id uuid not null, " +
                           "_version long not null default 0, " +
                           "_can_delete bool not null default true, " +
                           "_can_edit bool not null default true, " +
                           "_last_user_update string, " +
                           "_last_update_time datetime, " +

                           "name string not null {" +
                           "   validate_unique: true," +
                           "   span: 8 " +
                           "}, " +
                           "description string {" +
                           "   span: 16" +
                           "}, " +

                           "func text not null {" +
                           "   description: 'Function defining the external system interfacing logic', " +
                           "   browse: false, " +
                           "   required: true, " +
                           "   lines: 30, " +
                           "   span: 24, " +
                           "   code_language:'javascript' " +
                           "}, " +

                           "unique(name), " +
                           "primary key(_id))"));

        c.exec(p.parse("create table _platform.external.Interface drop undefined(" +
                           "{" +
                           "   name: 'External Interface', " +
                           "   description: 'Interface with external systems defining the source data, connector and interface function'" +
                           "}, " +
                           "_id uuid not null, " +
                           "_version long not null default 0, " +
                           "_can_delete bool not null default true, " +
                           "_can_edit bool not null default true, " +
                           "_last_user_update string, " +
                           "_last_update_time datetime, " +

                           "name string not null {" +
                           "   validate_unique: true," +
                           "   span: 8 " +
                           "}, " +
                           "description string {" +
                           "   browse: false, " +
                           "   span: 16" +
                           "}, " +

                           "source_data_id uuid not null {" +
                           "   browse: false, " +
                           "   link_table: '_platform.report.Report', " +
                           "   link_table_code_value: '_id', " +
                           "   link_table_code_label: 'name', " +
                           "   value_label: joinlabel(source_data_id, '_id', 'name', '_platform.report.Report')" +
                           "}, " +
                           "connector_id uuid not null {" +
                           "   browse: false, " +
                           "   link_table: '_platform.external.Connector', " +
                           "   link_table_code_value: '_id', " +
                           "   link_table_code_label: 'name', " +
                           "   value_label: joinlabel(connector_id, '_id', 'name', '_platform.external.Connector')" +
                           "}, " +

                           "require_confirmation bool not null default true {" +
                           "   browse: false, " +
                           "   initial_value: true" +
                           "}, " +
                           "confirmation text { " +
                           "   browse: false, " +
                           "   span: 16" +
                           "}, " +

                           "function_id uuid not null {" +
                           "   browse: false, " +
                           "   required: true, " +
                           "   link_table: '_platform.external.Function', " +
                           "   link_table_code_value: '_id', " +
                           "   link_table_code_label: 'name', " +
                           "   value_label: joinlabel(function_id, '_id', 'name', '_platform.external.Function')" +
                           "}, " +

                           "unique(name), " +
                           "primary key(_id), " +
                           "foreign key(source_data_id) references _platform.report .Report(_id) on update cascade on delete cascade," +
                           "foreign key(connector_id) references _platform.external.Connector(_id) on update cascade on delete cascade," +
                           "foreign key(function_id) references _platform.external.Function(_id) on update cascade on delete cascade)"));


        // Table for imports
        ///////////////////////////////

        /*
         * The import system uses several platform tables to store information on the
         * different imports defined in the system and their required data structures:
         *
         * 1. import datasets: an import dataset is a table with a custom structure for
         *    use to temporarily hold imported data where it is processed before transferring
         *    to the final table. Import datasets differ from normal datasets in two
         *    primary ways: 1) they contain a few hidden fields to hold information on
         *    each record such as validation errors and link to information on the imported
         *    file (who imported it, when, what type of data it contained, etc.)
         *
         * 2. data source: a data source describes a specific import file structure and
         *    how it is mapped to an import dataset, as well as, the processing that the
         *    imported data must undergo for cleaning, validation, transformation and
         *    transfer to the final dataset.
         *
         *    A processing instruction takes a row and make one or more changes to it. These
         *    changes can transform a column value, add a new column, remove a column, add
         *    an error, etc. The row may be saved back to the server into any dataset including
         *    the import dataset.
         *
         *    An example import:
         *      1. Define an import dataset for receiving the data
         *      2. Define the data source where a specific imported data structure
         *         can be
         */

        c.exec(p.parse("create table _platform.import.Import drop undefined(" +
                           "{" +
                           "  name: 'Import', " +
                           "  description: 'Data import definition', " +
                           "  children: {" +
                           "    fields: {" +
                           "      $m: {" +
                           "        type: '_platform.import.ImportField', " +
                           "        parent_link_column: 'import_id', " +
                           "        sequence_column: 'seq', " +
                           "        inline: false," +
                           "        label: 'Fields'," +
                           "        section: 'File structure', " +
                           "        sequence: 1" +
                           "      }," +
                           "      columns: {" +
                           "        _id: {" +
                           "          $m: {" +
                           "            type: 'uuid', " +
                           "            show: false" +
                           "          }" +
                           "        }," +
                           "        import_id: {" +
                           "          $m: {" +
                           "            type: 'uuid', " +
                           "            show: false" +
                           "          }" +
                           "        }," +
                           "        name: {" +
                           "          $m: {" +
                           "            type: 'string', " +
                           "            label: 'Field name'," +
                           "            mask: 'Iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii'," +
                           "            required: true" +
                           "          }" +
                           "        }," +
                           "        type: {" +
                           "          $m: {" +
                           "            type: 'string', " +
                           "            label: 'Type', " +
                           "            required: true, " +
                           "            link_table: '_basetype'," +
                           "            value_label: '${lookuplabel(this.row.type.$v, \"_basetype\")}'" +
                           "          }" +
                           "        }," +
                           "        column_index: {" +
                           "          $m: {" +
                           "            type: 'int'," +
                           "            label: 'Column index'," +
                           "            required: true" +
                           "          }" +
                           "        }," +
                           "        default_value: {" +
                           "          $m: {" +
                           "            type: 'string', " +
                           "            label: 'Default value'" +
                           "          }" +
                           "        }," +
                           "        seq: {" +
                           "          $m: {" +
                           "            type: 'int'," +
                           "            label: 'Sequence', " +
                           "            show: false " +
                           "          }" +
                           "        } " +
                           "      }," +
                           "      columnsOrder: ['_id', 'import_id', 'name', 'column_index', 'default_value', 'seq']" +
                           "    }, " +
                           "    process_functions: {" +
                           "      $m: {" +
                           "        type: '_platform.import.ProcessFunction', " +
                           "        parent_link_column: 'import_id', " +
                           "        sequence_column: 'seq', " +
                           "        inline: false," +
                           "        label: 'Processing functions', " +
                           "        section: 'Processing', " +
                           "        sequence: 2" +
                           "      }," +
                           "      columns: {" +
                           "        _id: {" +
                           "          $m: {" +
                           "            type: 'uuid', " +
                           "            show: false" +
                           "          }" +
                           "        }," +
                           "        import_id: {" +
                           "          $m: {" +
                           "            type: 'uuid', " +
                           "            show: false" +
                           "          }" +
                           "        }," +
                           "        function_id: {" +
                           "          $m: {" +
                           "            type: 'uuid', " +
                           "            label: 'Function', " +
                           "            required: true, " +
                           "            link_table: '_platform.import.Function'," +
                           "            link_table_code_label: 'name'," +
                           "            link_table_code_value: '_id'," +
                           "            value_label: '${joinlabel(this.row.function_id.$v, \"_id\", \"name\", \"_platform.import.Function\")}'" +
                           "          }" +
                           "        }," +
                           "        pass: {" +
                           "          $m: {" +
                           "            type: 'int', " +
                           "            label: 'Pass', " +
                           "            description: 'Complex data can be imported in several passes', " +
                           "            minimum: 1, " +
                           "            initial_value: 1 " +
                           "          }" +
                           "        }," +
                           "        seq: {" +
                           "          $m: {" +
                           "            type: 'int'," +
                           "            label: 'Sequence'," +
                           "            required: true" +
                           "          }" +
                           "        }" +
                           "      }," +
                           "      columnsOrder: ['_id', 'import_id', 'function_id', 'seq']" +
                           "    }" +
                           "  }" +
                           "}, " +
                           "_id uuid not null, " +
                           "_version long not null default 0, " +
                           "_can_delete bool not null default true, " +
                           "_can_edit bool not null default true, " +
                           "_last_user_update string, " +
                           "_last_update_time datetime, " +

                           "name string not null {" +
                           "   section: 'Import definition', " +
                           "   mask: 'Aiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii' " +
                           "}," +
                           "display_name string not null {" +
                           "   section: 'Import definition'" +
                           "}," +
                           "description string {" +
                           "   section: 'Import definition'," +
                           "   lines: 5" +
                           "}, " +
                           "target_table string {" +
                           "   section: 'Import definition', " +
                           "   can_insert: false, " +
                           "   can_edit: false, " +
                           "   can_remove: false, " +
                           "   link_table: '_core.relations', " +
                           "   link_table_code_value: 'name', " +
                           "   link_table_code_label: 'display_name?name', " +
                           "   filter_by: 'name=''_platform.lookup.Lookup'' or leftstr(name, 1) != ''_''', " +
                           "   value_label: joinlabel(target_table, 'name', 'display_name', '_core.relations')" +
                           "}," +
                           "start_of_columns text {" +
                           "   section: 'Import definition'," +
                           "   description: 'Comma-separated start positions of each column for fixed-length text files (first character at 0)', " +
                           "   lines: 5" +
                           "}, " +
                           "column_separator char default ',' {" +
                           "   section: 'Import definition'," +
                           "   initial_value: ',', " +
                           "   description: 'Character separating columns, default is comma', " +
                           "   max_length: 1" +
                           "}, " +
                           "header_lines int not null default 1 {" +
                           "   section: 'Import definition'," +
                           "   initial_value: 1" +
                           "}, " +
                           "footer_lines int default 0 {" +
                           "   section: 'Import definition'," +
                           "   initial_value: 0" +
                           "}, " +
                           "skip_blank_lines bool default false {" +
                           "   description: 'Skip or stop on blank lines', " +
                           "   section: 'Import definition'," +
                           "   initial_value: false" +
                           "}, " +
                           "load_all_sheets bool default false {" +
                           "   description: 'Import from all sheets or only first sheet (default) for Excel files', " +
                           "   section: 'Import definition'," +
                           "   initial_value: false" +
                           "}, " +

                           "primary key(_id)," +
                           "unique(name)," +
                           "foreign key(target_table) references _core.relations(name))"));

        c.exec(p.parse("create table _platform.import.ImportField drop undefined(" +
                           "{" +
                           "  name: 'Import field'," +
                           "  description: 'An imported field from a source file or derived from such'," +
                           "  sequence_column: 'seq', " +
                           "  children: {" +
                           "    attributes: {" +
                           "      $m: {" +
                           "        type: '_platform.import.ImportFieldAttribute', " +
                           "        parent_link_column: 'import_field_id', " +
                           "        label: 'Attributes'" +
                           "      }," +
                           "      columns: {" +
                           "        _id: {" +
                           "          $m: {" +
                           "            type: 'uuid', " +
                           "            show: false" +
                           "          }" +
                           "        }," +
                           "        import_field_id: {" +
                           "          $m: {" +
                           "            type: 'uuid', " +
                           "            show: false" +
                           "          }" +
                           "        }," +
                           "        attribute: {" +
                           "          $m: {" +
                           "            type: 'string', " +
                           "            label: 'Attribute'," +
                           "            mask: 'Iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii'," +
                           "            required: true, " +
                           "            link_table: '_field_attribute'," +
                           "            value_label: '${lookuplabel(this.row.attribute.$v, \"_field_attribute\")}'" +
                           "          }" +
                           "        }," +
                           "        value: {" +
                           "          $m: {" +
                           "            type: 'string'," +
                           "            label: 'Value'," +
                           "            required: true" +
                           "          }" +
                           "        }" +
                           "      }," +
                           "      columnsOrder: ['_id', 'import_field_id', 'attribute', 'value']" +
                           "    }" +
                           "  }" +
                           "}, " +
                           "_id uuid not null, " +
                           "_version long not null default 0, " +
                           "_can_delete bool not null default true, " +
                           "_can_edit bool not null default true, " +
                           "_last_user_update string, " +
                           "_last_update_time datetime, " +

                           "import_id uuid not null {" +
                           "   show: false" +
                           "}," +
                           "name string not null {" +
                           "   mask: 'Iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii' " +
                           "}," +
                           "type string not null {" +
                           "   link_table: `_basetype, " +
                           "   value_label: lookuplabel(type, `_basetype)" +
                           "}," +
                           "column_index int not null {" +
                           "   description: 'The index of the column in the source file to import into this field'" +
                           "}," +
                           "default_value text," +
                           "seq int default 1," +

                           "primary key(_id)," +
                           "unique(import_id, name)," +
                           "foreign key(import_id) references _platform.import.Import(_id) on update cascade on delete cascade)"));

        // create table for holding additional type information on fields
        c.exec(p.parse("create table _platform.import.ImportFieldAttribute drop undefined(" +
                           "{" +
                           "  name: 'Import field attribute'," +
                           "  description: 'Attributes of imported fields constrain their values'," +
                           "  sequence_column: 'seq'" +
                           "}, " +
                           "_id uuid not null, " +
                           "_version long not null default 0, " +
                           "_can_delete bool not null default true, " +
                           "_can_edit bool not null default true, " +
                           "_last_user_update string, " +
                           "_last_update_time datetime, " +

                           "import_field_id uuid not null {" +
                           "   show: false" +
                           "}, " +
                           "attribute string not null { " +
                           "   mask: 'Iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii'," +
                           "   link_table: `_field_attribute, " +
                           "   value_label: lookuplabel(attribute, `_field_attribute)" +
                           "}," +
                           "value text, " +

                           "primary key(_id), " +
                           "unique(import_field_id, attribute), " +
                           "foreign key(import_field_id) references _platform.import.ImportField(_id) on delete cascade on update cascade)"));

        c.exec(p.parse("create table _platform.import.File drop undefined(" +
                           "{" +
                           "   name: 'Import file', " +
                           "   description: 'Information on a specific file imported in the system'" +
                           "}, " +
                           "_id uuid not null, " +
                           "_version long not null default 0, " +
                           "_can_delete bool not null default true, " +
                           "_can_edit bool not null default true, " +
                           "_last_user_update string, " +
                           "_last_update_time datetime, " +

                           "import_id uuid not null," +
                           "received_on datetime not null," +
                           "description string {" +
                           "   lines: 5" +
                           "}, " +
                           "error text, " +
                           "records_have_errors bool default false, " +
                           "client_file_name string," +
                           "user_data text, " +

                           "primary key(_id)," +
                           "foreign key(import_id) references _platform.import.Import(_id) on update cascade on delete cascade)"));

        c.exec(p.parse("create table _platform.import.Function drop undefined(" +
                           "{" +
                           "   name: 'Import function', " +
                           "   description: 'Import functions transforms imported data prior to saving into the database'" +
                           "}, " +
                           "_id uuid not null, " +
                           "_version long not null default 0, " +
                           "_can_delete bool not null default true {" +
                           "    initial_value: true" +
                           "}, " +
                           "_can_edit bool not null default true {" +
                           "    initial_value: true" +
                           "}, " +
                           "_last_user_update string, " +
                           "_last_update_time datetime, " +

                           "name string not null {" +
                           "   span: 12, " +
                           "   mask: 'Iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii', " +
                           "   description: 'Start with a letter or underscore, follow by letters, digits or underscore' " +
                           "}, " +

                           "iterative bool not null default true {" +
                           "   span: 12, " +
                           "   description: 'An iterative function (default) will be executed on each row, " +
                           "while a non-iterative one will be executed once on all rows'" +
                           "}, " +

                           "func text not null {" +
                           "   label: 'Function', " +
                           "   browse: false, " +
                           "   span: 24, " +
                           "   lines: 20," +
                           "   code_language:'javascript' " +
                           "}, " +

                           "primary key(_id)," +
                           "unique(name))"));

        c.exec(p.parse("create table _platform.import.ProcessFunction drop undefined(" +
                           "{" +
                           "  name: 'Process function'," +
                           "  description: 'Associates import functions to a specific import as part its processing'," +
                           "  sequence_column: 'seq', " +
                           "  children: {" +
                           "    parameters: {" +
                           "      $m: {" +
                           "        type: '_platform.import.ProcessFunctionParameter', " +
                           "        parent_link_column: 'process_function_id', " +
                           "        label: 'Parameters'" +
                           "      }," +
                           "      columns: {" +
                           "        _id: {" +
                           "          $m: {" +
                           "            type: 'uuid', " +
                           "            show: false" +
                           "          }" +
                           "        }," +
                           "        process_function_id: {" +
                           "          $m: {" +
                           "            type: 'uuid', " +
                           "            show: false" +
                           "          }" +
                           "        }," +
                           "        name: {" +
                           "          $m: {" +
                           "            type: 'string', " +
                           "            label: 'Name'," +
                           "            mask: 'Iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii'," +
                           "            required: true" +
                           "          }" +
                           "        }," +
                           "        value1: {" +
                           "          $m: {" +
                           "            type: 'text'," +
                           "            label: 'Value 1'" +
                           "          }" +
                           "        }," +
                           "        value2: {" +
                           "          $m: {" +
                           "            type: 'text', " +
                           "            label: 'Value 2'" +
                           "          }" +
                           "        }," +
                           "        value3: {" +
                           "          $m: {" +
                           "            type: 'text', " +
                           "            label: 'Value 3'" +
                           "          }" +
                           "        }" +
                           "      }," +
                           "      columnsOrder: ['_id', 'process_function_id', 'name', 'value1', 'value2', 'value3']" +
                           "    }" +
                           "  }" +
                           "}, " +
                           "_id uuid not null, " +
                           "_version long not null default 0, " +
                           "_can_delete bool not null default true {" +
                           "    initial_value: true" +
                           "}, " +
                           "_can_edit bool not null default true {" +
                           "    initial_value: true" +
                           "}, " +
                           "_last_user_update string, " +
                           "_last_update_time datetime, " +

                           "import_id uuid not null {" +
                           "   show: false" +
                           "}, " +
                           "description string {" +
                           "   description: 'A description of this process step'" +
                           "}, " +
                           "function_id uuid not null {" +
                           "   link_table: '_platform.import.Function'," +
                           "   link_table_code_label: 'name'," +
                           "   link_table_code_value: '_id'," +
                           "   value_label: joinlabel(function_id, '_id', 'name', '_platform.import.Function')" +
                           "}, " +
                           "condition text {" +
                           "   description: 'Process step is only executed if this condition is true', " +
                           "   browse: false, " +
                           "   span: 24, " +
                           "   lines: 10," +
                           "   code_language:'javascript' " +
                           "}," +
                           "pass int not null default 1 {" +
                           "   description: 'Complex data are imported in several passes', " +
                           "   minimum: 1, " +
                           "   initial_value: 1 " +
                           "}," +
                           "seq int not null default 1 {" +
                           "   description: 'Execution order of function, in the pass' " +
                           "}, " +
//                    "seq int not null default 1 {" +
//                    "   sequencer: true, " +
//                    "   description: 'Execution order of function, in the pass' " +
//                    "}, " +

                           "primary key(_id)," +
                           "foreign key(import_id) references _platform.import.Import(_id) on update cascade on delete cascade," +
                           "foreign key(function_id) references _platform.import.Function(_id) on update cascade on delete cascade)"));

        c.exec(p.parse("create table _platform.import.ProcessFunctionParameter drop undefined(" +
                           "{" +
                           "   name: 'Process function parameter', " +
                           "   description: 'Parameter values for functions used in the processing of data for a specific import'" +
                           "}, " +
                           "_id uuid not null, " +
                           "_version long not null default 0, " +
                           "_can_delete bool not null default true {" +
                           "    initial_value: true" +
                           "}, " +
                           "_can_edit bool not null default true {" +
                           "    initial_value: true" +
                           "}, " +
                           "_last_user_update string, " +
                           "_last_update_time datetime, " +

                           "process_function_id uuid not null, " +
                           "name string not null {" +
                           "    mask: 'Iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii'" +
                           "}, " +

                           "value1 text, " +
                           "value2 text, " +
                           "value3 text, " +

                           "primary key(_id)," +
                           "foreign key(process_function_id) references _platform.import.ProcessFunction(_id) on update cascade on delete cascade)"));

        c.exec(p.parse("create table _platform.import.TargetField drop undefined(" +
                           "{" +
                           "   name: 'Import target field', " +
                           "   description: 'Import information such as precedence rules list on fields of target tables', " +
                           "   children: {" +
                           "     precedence: {" +
                           "       $m: {" +
                           "         type: '_platform.import.PrecedenceList', " +
                           "         parent_link_column: 'target_field_id', " +
                           "         sequence_column: 'seq', " +
                           "         label: 'Precedence'" +
                           "       }," +
                           "       columns: {" +
                           "         _id: {" +
                           "           $m: {" +
                           "             type: 'uuid', " +
                           "             show: false" +
                           "           }" +
                           "         }," +
                           "         target_field_id: {" +
                           "           $m: {" +
                           "             type: 'uuid', " +
                           "             show: false" +
                           "           }" +
                           "         }," +
                           "         import_id: {" +
                           "           $m: {" +
                           "             type: 'uuid', " +
                           "             label: 'Import'," +
                           "             can_insert: false," +
                           "             can_remove: false," +
                           "             can_edit: false," +
                           "             required: true, " +
                           "             link_table: '_platform.import.Import'," +
                           "             link_table_code_label: 'display_name?name'," +
                           "             link_table_code_value: '_id'" +
                           "           }" +
                           "         }," +
                           "         seq: {" +
                           "           $m: {" +
                           "             type: 'int', " +
                           "             label: 'Sequence' " +
                           "           }" +
                           "         } " +
                           "       }," +
                           "       columnsOrder: ['_id', 'target_field_id', 'import_id', 'seq']" +
                           "     }" +
                           "  }" +
                           "}, " +
                           "_id uuid not null, " +
                           "_version long not null default 0, " +
                           "_can_delete bool not null default true {" +
                           "    initial_value: true" +
                           "}, " +
                           "_can_edit bool not null default true {" +
                           "    initial_value: true" +
                           "}, " +
                           "_last_user_update string, " +
                           "_last_update_time datetime, " +

//          "target_table string not null {" +
//          "   can_insert: false, " +
//          "   can_edit: false, " +
//          "   can_remove: false, " +
//          "   link_table: '_core.relations', " +
//          "   link_table_code_value: 'name', " +
//          "   link_table_code_label: 'display_name?name', " +
//          "   filter_by: 'leftstr(name, 1) != ''_''', " +
//          "   value_label: joinlabel(target_table, 'name', 'display_name', '_core.relations')" +
//          "}, " +

                           "field_id uuid not null {" +
                           "   span: 24, " +
                           "   adaptive: true," +
                           "   can_insert: false," +
                           "   can_remove: false," +
                           "   can_edit: false," +
                           "   link_table: '_core.columns'," +
                           "   link_table_code_value: `_id, " +
                           "   link_table_code_label: 'joinlabel(`relation_id, `_id, `display_name, `_core.relations) || '' / '' || name', " +
                           "   filter_by: 'leftstr(name, 1) != ''_''', " +
                           "   value_label: joinlabel(`field_id, `_id, `name, `_core.columns, " +
                           "                                  `relation_id, `_id, `display_name, `_core.relations)" +
                           "}, " +

                           "primary key(_id)," +
                           "unique(field_id), " +
                           "foreign key(field_id) references _core.columns(_id) on update cascade on delete cascade)"));

        c.exec(p.parse("create table _platform.import.PrecedenceList drop undefined(" +
                           "{" +
                           "   name: 'Import precedence', " +
                           "   description: 'The list of imports in order of precedence for updating fields'" +
                           "}, " +
                           "_id uuid not null, " +
                           "_version long not null default 0, " +
                           "_can_delete bool not null default true {" +
                           "    initial_value: true" +
                           "}, " +
                           "_can_edit bool not null default true {" +
                           "    initial_value: true" +
                           "}, " +
                           "_last_user_update string, " +
                           "_last_update_time datetime, " +

                           "target_field_id uuid not null, " +
                           "import_id uuid not null {" +
                           "   link_table: '_platform.import.Import'," +
                           "   link_table_code_label: 'display_name?name'," +
                           "   link_table_code_value: '_id'," +
                           "   can_insert: false," +
                           "   can_remove: false," +
                           "   can_edit: false," +
                           "   value_label: joinlabel(import_id, '_id', 'display_name', '_platform.import.Import')" +
                           "}, " +
                           "seq int not null default 0," +

                           "primary key(_id)," +
                           "foreign key(target_field_id) references _platform.import.TargetField(_id) on update cascade on delete cascade, " +
                           "foreign key(import_id) references _platform.import.Import(_id) on update cascade on delete cascade)"));


        // Stratified sampling
        ///////////////////////////////

        c.exec(p.parse("create table _platform.sampling.Stratified drop undefined(" +
                           "{" +
                           "  name: 'Stratified sampling', " +
                           "  description: 'Stratified sampling definition', " +
                           "  children: {" +
                           "    dimensions: {" +
                           "      $m: {" +
                           "        type: '_platform.sampling.StratifiedDimension', " +
                           "        parent_link_column: 'stratified_id', " +
                           "        sequence_column: 'seq', " +
//                    "        inline: false, " +
                           "        label: 'Dimension' " +
                           "      }," +
                           "      columns: {" +
                           "        _id: {" +
                           "          $m: {" +
                           "            type: 'uuid'," +
                           "            show: false" +
                           "          }" +
                           "        }," +
                           "        expression: {" +
                           "          $m: {" +
                           "            type: 'text', " +
                           "            label: 'Expression', " +
                           "            required: true" +
                           "          }" +
                           "        }, " +
                           "        seq: {" +
                           "          $m: {" +
                           "            type: 'int', " +
                           "            label: 'Sequence', " +
                           "            show: false" +
                           "          }" +
                           "        } " +
                           "      }," +
                           "      columnsOrder: ['_id', 'expression', 'seq']" +
                           "    }" +
                           "  }" +
                           "}," +

                           "_id uuid not null, " +
                           "_version long not null default 0, " +
                           "_can_delete bool not null default true, " +
                           "_can_edit bool not null default true, " +
                           "_last_user_update string, " +
                           "_last_update_time datetime, " +

                           "name string not null {" +
                           "   span: 12, " +
                           "   validate_unique: true " +
                           "}, " +
                           "description text {" +
                           "   span: 12 " +
                           "}, " +
                           "on_table string not null {" +
                           "   description: 'The table containing the data to be stratified', " +
                           "   browse: false, " +
                           "   can_insert: false, " +
                           "   can_edit: false, " +
                           "   can_remove: false, " +
                           "   link_table: `_core.relations, " +
                           "   link_table_code_value: 'name', " +
                           "   link_table_code_label: 'display_name', " +
                           "   filter_by: 'leftstr(name, 1) != ''_''', " +
                           "   value_label: joinlabel(on_table, `name, `display_name, `_core.relations)" +
                           "}," +
                           "weight_expression string not null default 'count(*)' {" +
                           "   description: 'Expression to use for weighing a group in the population. " +
                           "E.g. count(*) uses the number of records in the group as its weight (proportional selection) " +
                           "while sum(revenue) to use the total revenue of the group (disproportional selection).', " +
                           "   browse: false, " +
                           "   span: 24, " +
                           "   required: true, " +
                           "   initial_Value: 'count(*)'" +
                           "}," +

                           "unique(name), " +
                           "primary key(_id))"));


        c.exec(p.parse("create table _platform.sampling.StratifiedDimension drop undefined(" +
                           "{" +
                           "  name: 'Stratified sampling dimension'," +
                           "  description: 'A dimension in a stratified sampling defined as a value to extracted from the database'," +
                           "  sequence_column: 'seq' " +
                           "}, " +
                           "_id uuid not null, " +
                           "_version long not null default 0, " +
                           "_can_delete bool not null default true, " +
                           "_can_edit bool not null default true, " +
                           "_last_user_update string, " +
                           "_last_update_time datetime, " +

                           "stratified_id uuid not null {" +
                           "   show: false" +
                           "}, " +
                           "expression text not null, " +
                           "seq int not null default 1 {" +
                           "   show: false" +
                           "}, " +

                           "primary key(_id), " +
                           "foreign key(stratified_id) references _platform.sampling.Stratified(_id) on delete cascade)"));

        // filter operations
        try (Result rs = c.exec(p.parse("select _id from _platform.filter.FilterOperation"))) {
          if (!rs.next()) {
            log.log(INFO, "Creating filter operations with their supported types");

            class Operation {
              public Operation(String opCode,
                               String name,
                               String operator,
                               Boolean forAllTypes,
                               Boolean requireValue,
                               String valueType,
                               String parseFunction,
                               int seq,
                               String[] types) {
                this.id = UUID.randomUUID().toString();
                this.opCode = opCode;
                this.name = name;
                this.operator = operator;
                this.forAllTypes = forAllTypes;
                this.requireValue = requireValue;
                this.valueType = valueType;
                this.parseFunction = parseFunction;
                this.seq = seq;
                this.types = types;
              }

              public final String id;
              public final String opCode;
              public final String name;
              public final String operator;
              public final Boolean forAllTypes;
              public final Boolean requireValue;
              public final String valueType;
              public final String parseFunction;
              public final int seq;
              public final String[] types;
            }
            Operation[] operations = new Operation[]{
                new Operation("Equal", "Equal", "=",
                              true, true, null,
                              escapeEsqlQuote("if (typeof(value) === \"string\") { \n" +
                                                  "    return \"lower(\" + (alias ? alias + '.' : '') + expression + \")='\" + value.toLowerCase() + \"'\"; \n" +
                                                  "} else { \n" +
                                                  "    return (alias ? alias + '.' : '') + expression + \"=\" + convert.toEsqlLiteral(value); \n" +
                                                  "}"), 10,
                              new String[]{"byte", "short", "int", "long", "float", "double",
                                  "money", "bool", "char", "string", "text", "date",
                                  "time", "datetime", "uuid",}),

                new Operation("NotEqual", "Not equal", "!=",
                              true, true, null,
                              escapeEsqlQuote(
                                  "if (typeof(value) === \"string\") { \n" +
                                      "    return \"lower(\" + (alias ? alias + '.' : '') + expression + \")!='\" + value.toLowerCase() + \"'\"; \n" +
                                      "} else { \n" +
                                      "    return (alias ? alias + '.' : '') + expression + \"!=\" + convert.toEsqlLiteral(value); \n" +
                                      "}"), 20,
                              new String[]{"byte", "short", "int", "long", "float", "double",
                                  "money", "bool", "char", "string", "text", "date",
                                  "time", "datetime", "uuid",}),

                new Operation("Greater", "Greater", ">",
                              false, true, null, null, 30,
                              new String[]{"byte", "short", "int", "long", "float", "double",
                                  "money", "char", "string", "text", "date", "time",
                                  "datetime",}),

                new Operation("GreaterOrEqual", "Greater or equal", ">=",
                              false, true, null, null, 40,
                              new String[]{"byte", "short", "int", "long", "float", "double",
                                  "money", "char", "string", "text", "date", "time",
                                  "datetime",}),

                new Operation("Less", "Less", "<",
                              false, true, null, null, 50,
                              new String[]{"byte", "short", "int", "long", "float", "double",
                                  "money", "char", "string", "text", "date", "time",
                                  "datetime",}),

                new Operation("LessOrEqual", "Less or equal", "<=",
                              false, true, null, null, 60,
                              new String[]{"byte", "short", "int", "long", "float", "double",
                                  "money", "char", "string", "text", "date", "time",
                                  "datetime",}),

                new Operation("Between", "Between", "between",
                              false, true, null, null, 70,
                              new String[]{"byte", "short", "int", "long", "float", "double",
                                  "money", "char", "string", "text", "date", "time",
                                  "datetime",}),

                new Operation("InList", "In list", "in",
                              true, true, "text",
                              escapeEsqlQuote(
                                  "const items = (value.split(/[,;\r\n]+/)" +
                                      ".filter(i => i.trim().length > 0)" +
                                      ".map(i => convert.toEsqlLiteral(convert.toType(i.trim(), column.type)))" +
                                      ".join(', '));\n" +
                                      "return (alias ? alias + '.' : '') + expression + ' in (' + items + ')';"
                                             ), 75,
                              new String[]{"byte", "short", "int", "long", "float", "double",
                                  "money", "char", "string", "text", "date", "time",
                                  "datetime",}),

                new Operation("IsBlank", "Is blank", "is null",
                              true, false, null, null, 80,
                              new String[]{"byte", "short", "int", "long", "float", "double",
                                  "money", "bool", "char", "string", "text", "date",
                                  "time", "datetime", "uuid",}),

                new Operation("IsNotBlank", "Is not blank", "is not null",
                              true, false, null, null, 90,
                              new String[]{"byte", "short", "int", "long", "float", "double",
                                  "money", "bool", "char", "string", "text", "date",
                                  "time", "datetime", "uuid",}),

                new Operation("Contains", "Contains",
                              "ilike", false, true, null,
                              escapeEsqlQuote(
                                  "const keywords = value.split(/\\W+/).filter(w => w.length > 0);\n" +
                                      "return (alias ? alias + '.' : '') + expression + \" ilike '%\" + keywords.join('%') + \"%'\";"
                                             ),

                              100, new String[]{"string", "text"}),

                new Operation("IsTrue", "Is true", "=true",
                              false, false, null,
                              null, 110, new String[]{"bool"}),

                new Operation("IsFalse", "Is false", "=false",
                              false, false, null
                    , null, 120, new String[]{"bool"}),
            };

            for (Operation op: operations) {
              c.exec(p.parse("insert into _platform.filter.FilterOperation" +
                                 "(_id, _can_delete, op_code, name, operator, for_all_types, " +
                                 " require_value, value_type, parse_function, seq) values" +
                                 "   (u'" + op.id + "', false, '" + op.opCode + "', '" + op.name + "', '" + op.operator + "', " +
                                 op.forAllTypes + ", " + op.requireValue + ", '" + op.valueType + "', " +
                                 (op.parseFunction == null ? "null" : "'" + op.parseFunction + "'") +
                                 ", " + op.seq + ")"));

              for (String type: op.types) {
                c.exec(p.parse("insert into _platform.filter.FilterOperationType" +
                                   "     (_id, _can_delete, filter_operation_id, type) values" +
                                   "     (newid(), false, '" + op.id + "', '" + type + "')"));
              }
            }
          }
        }

        /*
         * Load import functions
         */
        /*
        Yaml yaml = new Yaml();
        Reader in = new InputStreamReader(AbstractDatabase.class.getResourceAsStream("/init/import_functions.yml"));
        Iterable<Object> contents = yaml.loadAll(in);
        for (Object content: contents) {
          Map<String, String> functions = (Map<String, String>)content;
          for (Map.Entry<String, String> function: functions.entrySet()) {
            String functionName = function.getKey();
            String functionBody = function.getValue();
            boolean iterative = true;
            if (functionName.charAt(0) == '<') {
              iterative = false;
              functionName = functionName.substring(1, functionName.length() - 1);
            }
            try (Result rs = c.exec(p.parse("select _id from _platform.import.Function where name='" + functionName + "'"))) {
              if (rs.next()) {
                log.log(INFO, "Updating import function " + functionName);
                c.exec(p.parse("update _platform.import.Function f " +
                                 "set func=:f, iterative=:i where name=:n"),
                       Param.of("n", functionName),
                       Param.of("i", iterative),
                       Param.of("f", functionBody));
              } else {
                log.log(INFO, "Creating import function " + functionName);
                c.exec(p.parse("insert into _platform.import.Function(_id, _can_delete, name, iterative, func) values" +
                                 "(newid(), false, :n, :i, :f)"),
                       Param.of("n", functionName),
                       Param.of("i", iterative),
                       Param.of("f", functionBody));
              }
            }
          }
        }
        */

        // base lookups
        // Join type
        try (Result rs = c.exec(p.parse("select _id from _platform.lookup.Lookup where name='_join_type'"))) {
          if (!rs.next()) {
            log.log(INFO, "Creating _join_type lookup");

            String id = UUID.randomUUID().toString();
            c.exec(p.parse("insert into _platform.lookup.Lookup(_id, _can_delete, name, description) " +
                               "values(:id, false, :name, :description)"),
                   Param.of("id", id),
                   Param.of("name", "_join_type"),
                   Param.of("description", "Internal lookup containing table join types used in filters and reports"));

            c.exec(p.parse(
                "insert into _platform.lookup.LookupValue(_id, _can_delete, lookup_id, code, lang, label) values" +
                    "(newid(), false, '" + id + "', 'from',       'en', 'from'), " +
                    "(newid(), false, '" + id + "', 'cross',      'en', 'cross'), " +
                    "(newid(), false, '" + id + "', 'join',       'en', 'inner join'), " +
                    "(newid(), false, '" + id + "', 'left join',  'en', 'left join'), " +
                    "(newid(), false, '" + id + "', 'right join', 'en', 'right join'), " +
                    "(newid(), false, '" + id + "', 'full join',  'en', 'full join')"));
          }
        }

        // Report grouping type (normal, rollup or cube).
        try (Result rs = c.exec(p.parse("select _id from _platform.lookup.Lookup where name='_group_type'"))) {
          if (!rs.next()) {
            log.log(INFO, "Creating _group_type lookup");

            UUID id = UUID.randomUUID();
            c.exec(p.parse("insert into _platform.lookup.Lookup(_id, _can_delete, name, description) " +
                               "values(:id, false, :name, :description)"),
                   Param.of("id", id.toString()),
                   Param.of("name", "_group_type"),
                   Param.of("description",
                            "Internal lookup defining the different group types used in reporting queries"));

            c.exec(p.parse(
                "insert into _platform.lookup.LookupValue(_id, _can_delete, lookup_id, code, lang, label) values" +
                    "(newid(), false, '" + id + "', 'normal', 'en', 'Normal'), " +
                    "(newid(), false, '" + id + "', 'rollup', 'en', 'Rollup'), " +
                    "(newid(), false, '" + id + "', 'cube',   'en', 'Cube')"));
          }
        }

        // Base audit actions.
        try (Result rs = c.exec(p.parse("select _id from _platform.lookup.Lookup where name='_audit_action'"))) {
          if (!rs.next()) {
            log.log(INFO, "Creating _audit_action lookup");

            UUID id = UUID.randomUUID();
            c.exec(p.parse("insert into _platform.lookup.Lookup(_id, _can_delete, name, description) " +
                               "values(:id, false, :name, :description)"),
                   Param.of("id", id.toString()),
                   Param.of("name", "_audit_action"),
                   Param.of("description",
                            "Internal lookup defining the different base audit actions kept in the audit trail"));

            c.exec(p.parse(
                "insert into _platform.lookup.LookupValue(_id, _can_delete, lookup_id, code, lang, label) values" +
                    "(newid(), false, '" + id + "', 'signin',       'en', 'Sign In'), " +
                    "(newid(), false, '" + id + "', 'signout',      'en', 'Sign Out'), " +
                    "(newid(), false, '" + id + "', 'failedsignin', 'en', 'Failed sign in'), " +
                    "(newid(), false, '" + id + "', 'import',       'en', 'Import'), " +
                    "(newid(), false, '" + id + "', 'create',       'en', 'Create'), " +
                    "(newid(), false, '" + id + "', 'modify',       'en', 'Modify'), " +
                    "(newid(), false, '" + id + "', 'delete',       'en', 'Delete')"));
          }
        }

        // Report output type (excel, pdf and text).
        try (Result rs = c.exec(p.parse("select _id from _platform.lookup.Lookup where name='_output_type'"))) {
          if (!rs.next()) {
            log.log(INFO, "Creating _output_type lookup");

            UUID id = UUID.randomUUID();
            c.exec(p.parse("insert into _platform.lookup.Lookup(_id, _can_delete, name, description) " +
                               "values(:id, false, :name, :description)"),
                   Param.of("id", id.toString()),
                   Param.of("name", "_output_type"),
                   Param.of("description", "Internal lookup defining the output types of reports"));

            c.exec(p.parse(
                "insert into _platform.lookup.LookupValue(_id, _can_delete, lookup_id, code, lang, label) values" +
                    "(newid(), false, '" + id + "', 'excel', 'en', 'Excel'), " +
                    "(newid(), false, '" + id + "', 'pdf',   'en', 'PDF'), " +
                    "(newid(), false, '" + id + "', 'text',  'en', 'Text')"));
          }
        }

        try (Result rs = c.exec(p.parse("select _id from _platform.util.Category where name='Main'"))) {
          if (!rs.next()) {
            log.log(INFO, "Adding Main category");
            c.exec(p.parse("insert into _platform.util.Category(_id, _can_delete, name) " +
                               "values(newid(), false, 'Main')"));
          }
        }

//            // User rights, resources
//            try (Result rs = c.exec(parse("select _id from _platform.lookup.Lookup where name='_user_resource'"))) {
//                if (!rs.next()) {
//                    log.log(INFO, "Creating _user_resource lookup");
//
//                    UUID id = UUID.randomUUID();
//                    c.exec(parse("insert into _platform.lookup.Lookup(_id, _can_delete, name, description) " +
//                                    "values(:id, false, :name, :description)"),
//                            Param.of("id", id.toString()),
//                            Param.of("name", "_user_resource"),
//                            Param.of("description", "Internal lookup containing resources which can be assigned as rights to users"));
//
//                    c.exec(parse("insert into _platform.lookup.LookupValue(_id, _can_delete, lookup_id, code, lang, label) values" +
//                            "(newid(), false, '" + id + "', '*',                       'en', 'All (*)'), " +
//                            "(newid(), false, '" + id + "', '-',                       'en', 'None'), " +
//                            "(newid(), false, '" + id + "', '_platform.lookup.Lookup', 'en', 'Lookup'), " +
//                            "(newid(), false, '" + id + "', '_platform.user.User',     'en', 'User')"));
//                }
//            }
//
//            // User rights, entities
//            try (Result rs = c.exec(parse("select _id from _platform.lookup.Lookup where name='_user_entity'"))) {
//                if (!rs.next()) {
//                    log.log(INFO, "Creating _user_entity lookup");
//
//                    UUID id = UUID.randomUUID();
//                    c.exec(parse("insert into _platform.lookup.Lookup(_id, _can_delete, name, description) " +
//                                    "values(:id, false, :name, :description)"),
//                            Param.of("id", id.toString()),
//                            Param.of("name", "_user_entity"),
//                            Param.of("description", "Internal lookup containing entities which can be assigned as rights to users"));
//
//                    c.exec(parse("insert into _platform.lookup.LookupValue(_id, _can_delete, lookup_id, code, lang, label) values" +
//                            "(newid(), false, '" + id + "', '*',                'en', 'All (*)'), " +
//                            "(newid(), false, '" + id + "', '-',                'en', 'None'), " +
//                            "(newid(), false, '" + id + "', '_basetype',        'en', 'Base types lookup'), " +
//                            "(newid(), false, '" + id + "', '_user_resource',   'en', 'User resource lookup'), " +
//                            "(newid(), false, '" + id + "', '_user_entity',     'en', 'User entity lookup'), " +
//                            "(newid(), false, '" + id + "', '_user_function',   'en', 'User function lookup')"));
//                }
//            }
//
//            // User rights, functions
//            try (Result rs = c.exec(parse("select _id from _platform.lookup.Lookup where name='_user_function'"))) {
//                if (!rs.next()) {
//                    log.log(INFO, "Creating _user_function lookup");
//
//                    UUID id = UUID.randomUUID();
//                    c.exec(parse("insert into _platform.lookup.Lookup(_id, _can_delete, name, description) " +
//                                    "values(:id, false, :name, :description)"),
//                            Param.of("id", id.toString()),
//                            Param.of("name", "_user_function"),
//                            Param.of("description", "Internal lookup containing functions which can be assigned as rights to users"));
//
//                    c.exec(parse("insert into _platform.lookup.LookupValue(_id, _can_delete, lookup_id, code, lang, label) values" +
//                            "(newid(), false, '" + id + "', '*',      'en', 'All (*)'), " +
//                            "(newid(), false, '" + id + "', '-',      'en', 'None'), " +
//                            "(newid(), false, '" + id + "', 'read',   'en', 'View'), " +
//                            "(newid(), false, '" + id + "', 'modify', 'en', 'Modify'), " +
//                            "(newid(), false, '" + id + "', 'delete', 'en', 'Delete'), " +
//                            "(newid(), false, '" + id + "', 'create', 'en', 'Create'), " +
//                            "(newid(), false, '" + id + "', 'list',   'en', 'List')"));
//                }
//            }

        // Field base types for creation of new fields
        try (Result rs = c.exec(p.parse("select _id from _platform.lookup.Lookup where name='_basetype'"))) {
          if (!rs.next()) {
            log.log(INFO, "Creating _basetype lookup");

            UUID id = UUID.randomUUID();
            c.exec(p.parse("insert into _platform.lookup.Lookup(_id, _can_delete, name, description) " +
                               "values(:id, false, :name, :description)"),
                   Param.of("id", id.toString()),
                   Param.of("name", "_basetype"),
                   Param.of("description", "Internal lookup containing information on base types"));

            c.exec(p.parse(
                "insert into _platform.lookup.LookupValue(_id, _can_delete, lookup_id, code, lang, label) values" +
                    "(newid(), false, '" + id + "', 'uuid',     'en', 'Unique identifier'), " +
                    "(newid(), false, '" + id + "', 'variable', 'en', 'Variable type (undefined)'), " +
                    "(newid(), false, '" + id + "', 'int',      'en', 'Integer (32 bits)'), " +
                    "(newid(), false, '" + id + "', 'long',     'en', 'Long integer (64 bits)'), " +
                    "(newid(), false, '" + id + "', 'float',    'en', 'Real number (32 bits)'), " +
                    "(newid(), false, '" + id + "', 'double',   'en', 'Real number with increased precision (64 bits)'), " +
                    "(newid(), false, '" + id + "', 'bool',     'en', 'Boolean (yes/no)'), " +
                    "(newid(), false, '" + id + "', 'string',   'en', 'Text'), " +
                    "(newid(), false, '" + id + "', 'text',     'en', 'Large text (unlimited length)'), " +
                    "(newid(), false, '" + id + "', 'date',     'en', 'Date'), " +
                    "(newid(), false, '" + id + "', 'time',     'en', 'Time'), " +
                    "(newid(), false, '" + id + "', 'datetime', 'en', 'Date and time')"));
          }
        }

        // Months
        try (Result rs = c.exec(p.parse("select _id from _platform.lookup.Lookup where name='_otp_channel'"))) {
          if (!rs.next()) {
            log.log(INFO, "Creating _otp_channel lookup");

            UUID id = UUID.randomUUID();
            c.exec(p.parse("insert into _platform.lookup.Lookup(_id, _can_delete, name, description) " +
                               "values(:id, false, :name, :description)"),
                   Param.of("id", id.toString()),
                   Param.of("name", "_otp_channel"),
                   Param.of("description", "OTP channel lookup"));

            c.exec(p.parse(
                "insert into _platform.lookup.LookupValue(_id, _can_delete, lookup_id, code, lang, label) values" +
                    "(newid(), false, '" + id + "', 'phone', 'en', 'Phone'), " +
                    "(newid(), false, '" + id + "', 'email', 'en', 'Email')"));
          }
        }

        // Months
        try (Result rs = c.exec(p.parse("select _id from _platform.lookup.Lookup where name='_util_month'"))) {
          if (!rs.next()) {
            log.log(INFO, "Creating _util_month lookup");

            UUID id = UUID.randomUUID();
            c.exec(p.parse("insert into _platform.lookup.Lookup(_id, _can_delete, name, description) " +
                               "values(:id, false, :name, :description)"),
                   Param.of("id", id.toString()),
                   Param.of("name", "_util_month"),
                   Param.of("description", "Month utility lookup"));

            c.exec(p.parse(
                "insert into _platform.lookup.LookupValue(_id, _can_delete, lookup_id, code, lang, label) values" +
                    "(newid(), false, '" + id + "', '01', 'en', 'January'), " +
                    "(newid(), false, '" + id + "', '02', 'en', 'February'), " +
                    "(newid(), false, '" + id + "', '03', 'en', 'March'), " +
                    "(newid(), false, '" + id + "', '04', 'en', 'April'), " +
                    "(newid(), false, '" + id + "', '05', 'en', 'May'), " +
                    "(newid(), false, '" + id + "', '06', 'en', 'June'), " +
                    "(newid(), false, '" + id + "', '07', 'en', 'July'), " +
                    "(newid(), false, '" + id + "', '08', 'en', 'August'), " +
                    "(newid(), false, '" + id + "', '09', 'en', 'September'), " +
                    "(newid(), false, '" + id + "', '10', 'en', 'October'), " +
                    "(newid(), false, '" + id + "', '11', 'en', 'November'), " +
                    "(newid(), false, '" + id + "', '12', 'en', 'December')"));
          }
        }

        // Yes/No
        try (Result rs = c.exec(p.parse("select _id from _platform.lookup.Lookup where name='_util_yes_no'"))) {
          if (!rs.next()) {
            log.log(INFO, "Creating _util_yes_no lookup");

            UUID id = UUID.randomUUID();
            c.exec(p.parse("insert into _platform.lookup.Lookup(_id, _can_delete, name, description) " +
                               "values(:id, false, :name, :description)"),
                   Param.of("id", id.toString()),
                   Param.of("name", "_util_yes_no"),
                   Param.of("description", "Yes/No utility lookup"));

            c.exec(p.parse(
                "insert into _platform.lookup.LookupValue(_id, _can_delete, lookup_id, code, lang, label) values" +
                    "(newid(), false, '" + id + "', 'Y', 'en', 'Yes'), " +
                    "(newid(), false, '" + id + "', 'N', 'en', 'No')"));
          }
        }

        // Years
        try (Result rs = c.exec(p.parse("select _id from _platform.lookup.Lookup  where name='_util_year'"))) {
          if (!rs.next()) {
            log.log(INFO, "Creating _util_year lookup");

            UUID id = UUID.randomUUID();
            c.exec(p.parse("insert into _platform.lookup.Lookup(_id, _can_delete, name, description) " +
                               "values(:id, false, :name, :description)"),
                   Param.of("id", id.toString()),
                   Param.of("name", "_util_year"),
                   Param.of("description", "Year utility lookup"));

            StringBuilder q = new StringBuilder(
                "insert into _platform.lookup.LookupValue(_id, _can_delete, lookup_id, code, lang, label) values");
            for (int y = 1900; y <= 2100; y++) {
              if (y > 1900) {
                q.append(", ");
              }
              q.append("(newid(), false, '")
               .append(id)
               .append("', '")
               .append(y)
               .append("', 'en', '")
               .append(y)
               .append("')");
            }
            c.exec(p.parse(q.toString()));
          }
        }

        // External products
        try (Result rs = c.exec(p.parse("select _id from _platform.lookup.Lookup where name='_external_product'"))) {
          if (!rs.next()) {
            log.log(INFO, "Creating _external_product lookup");

            UUID id = UUID.randomUUID();
            c.exec(p.parse("insert into _platform.lookup.Lookup(_id, _can_delete, name, description) " +
                               "values(:id, false, :name, :description)"),
                   Param.of("id", id.toString()),
                   Param.of("name", "_external_product"),
                   Param.of("description", "External products"));

            c.exec(p.parse(
                "insert into _platform.lookup.LookupValue(_id, _can_delete, lookup_id, code, lang, label) values" +
                    "(newid(), false, '" + id + "', 'ms_sql_server_2017', 'en', 'SQL Server'), " +
                    "(newid(), false, '" + id + "', 'postgresql_10',      'en', 'Postgresql')"));
          }
        }

        // Field attributes
        try (Result rs = c.exec(p.parse("select _id from _platform.lookup.Lookup where name='_field_attribute'"))) {
          if (!rs.next()) {
            log.log(INFO, "Creating _field_attribute lookup");

            UUID id = UUID.randomUUID();
            c.exec(p.parse("insert into _platform.lookup.Lookup(_id, _can_delete, name, description) " +
                               "values(:id, false, :name, :description)"),
                   Param.of("id", id.toString()),
                   Param.of("name", "_field_attribute"),
                   Param.of("description", "Internal lookup containing information on default attributes for fields"));

            c.exec(p.parse(
                "insert into _platform.lookup.LookupValue(_id, _can_delete, lookup_id, code, lang, label) values" +
                    "(newid(), false, '" + id + "', 'label',                    'en', 'Label'), " +
                    "(newid(), false, '" + id + "', 'short_label',              'en', 'Short label'), " +
                    "(newid(), false, '" + id + "', 'description',              'en', 'Description'), " +
                    "(newid(), false, '" + id + "', 'read_only',                'en', 'Read-ony'), " +
                    "(newid(), false, '" + id + "', 'show',                     'en', 'Show'), " +
                    "(newid(), false, '" + id + "', 'edit',                     'en', 'Show on edit'), " +
                    "(newid(), false, '" + id + "', 'sequence',                 'en', 'Sequence number of field'), " +
                    "(newid(), false, '" + id + "', 'main_column',              'en', 'Main column'), " +

                    "(newid(), false, '" + id + "', 'format',                   'en', 'Format'), " +
                    "(newid(), false, '" + id + "', 'money',                    'en', 'Money'), " +
                    "(newid(), false, '" + id + "', 'width',                    'en', 'Width (in pixels)'), " +
                    "(newid(), false, '" + id + "', 'span',                     'en', 'Span (in grid cells, 1 to 24)'), " +
                    "(newid(), false, '" + id + "', 'offset',                   'en', 'Offset (in grid cells, 1 to 24)'), " +
                    "(newid(), false, '" + id + "', 'group',                    'en', 'Group'), " +
                    "(newid(), false, '" + id + "', 'subgroup',                 'en', 'Subgroup'), " +
                    "(newid(), false, '" + id + "', 'password',                 'en', 'Password (hidden content on edit)'), " +
                    "(newid(), false, '" + id + "', 'lines',                    'en', 'Number of lines for multi-line text edit'), " +

                    "(newid(), false, '" + id + "', 'initial_value',            'en', 'Initial value'), " +

                    "(newid(), false, '" + id + "', 'link_table',               'en', 'Linked table'), " +
                    "(newid(), false, '" + id + "', 'multi_select',             'en', 'Linked table - Multiple selection'), " +
                    "(newid(), false, '" + id + "', 'link_table_code_value',    'en', 'Linked table - code field'), " +
                    "(newid(), false, '" + id + "', 'link_table_code_label',    'en', 'Linked table - label field'), " +
                    "(newid(), false, '" + id + "', 'adaptive',                 'en', 'Linked table - adaptive loading of codes'), " +
                    "(newid(), false, '" + id + "', 'value_label',              'en', 'Linked table - value label'), " +
                    "(newid(), false, '" + id + "', 'can_insert',               'en', 'Linked table - allow insert of new codes'), " +
                    "(newid(), false, '" + id + "', 'can_edit',                 'en', 'Linked table - allow editing of codes'), " +
                    "(newid(), false, '" + id + "', 'can_remove',               'en', 'Linked table - allow deletion of codes'), " +
                    "(newid(), false, '" + id + "', 'filter_by',                'en', 'Linked table - filter by'), " +
                    "(newid(), false, '" + id + "', 'order_by',                 'en', 'Linked table - order by'), " +

                    "(newid(), false, '" + id + "', 'mask',                     'en', 'Edit mask'), " +
                    "(newid(), false, '" + id + "', 'required',                 'en', 'Required'), " +
                    "(newid(), false, '" + id + "', 'validate_unique',          'en', 'Unique'), " +
                    "(newid(), false, '" + id + "', 'minimum',                  'en', 'Minimum value'), " +
                    "(newid(), false, '" + id + "', 'maximum',                  'en', 'Maximum value'), " +
                    "(newid(), false, '" + id + "', 'min_length',               'en', 'Minimum length'), " +
                    "(newid(), false, '" + id + "', 'max_length',               'en', 'Maximum length'), " +
                    "(newid(), false, '" + id + "', 'email',                    'en', 'Email'), " +
                    "(newid(), false, '" + id + "', 'phone',                    'en', 'Phone'), " +
                    "(newid(), false, '" + id + "', 'pattern',                  'en', 'Pattern'), " +
                    "(newid(), false, '" + id + "', 'value_type',               'en', 'Value type (date, number, text or boolean)'), " +
                    "(newid(), false, '" + id + "', 'target_type',              'en', 'Target type (date, number, text or boolean)'), " +

                    "(newid(), false, '" + id + "', 'date_resolution',          'en', 'Date resolution (day, week, month or year)')"));
          }
        }

        // Report field attributes
        try (Result rs = c.exec(p.parse("select _id from _platform.lookup.Lookup where name='_report_type'"))) {
          if (!rs.next()) {
            log.log(INFO, "Creating _report_type lookup");

            UUID id = UUID.randomUUID();
            c.exec(p.parse("insert into _platform.lookup.Lookup(_id, _can_delete, name, description) " +
                               "values(:id, false, :name, :description)"),
                   Param.of("id", id.toString()),
                   Param.of("name", "_report_type"),
                   Param.of("description", "Internal lookup of types of report"));

            c.exec(p.parse(
                "insert into _platform.lookup.LookupValue(_id, _can_delete, lookup_id, code, lang, label) values" +
                    "(newid(), false, '" + id + "', 'Tabular',  'en', 'Tabular'), " +
                    "(newid(), false, '" + id + "', 'Crosstab', 'en', 'Crosstab')"
                          ));
//                    c.exec(parse("insert into _platform.lookup.LookupValue(_id, _can_delete, lookup_id, code, lang, label) values" +
//                            "(newid(), false, '" + id + "', 'Tabular',  'en', 'Tabular'), " +
//                            "(newid(), false, '" + id + "', 'Crosstab', 'en', 'Crosstab'), " +
//                            "(newid(), false, '" + id + "', 'Chart',    'en', 'Chart'), " +
//                            "(newid(), false, '" + id + "', 'Document', 'en', 'Document')"
//                    ));
          }
        }

        try (Result rs = c.exec(p.parse("select _id from _platform.lookup.Lookup where name='_report_field_attribute'"))) {
          if (!rs.next()) {
            log.log(INFO, "Creating _report_field_attribute lookup");

            UUID id = UUID.randomUUID();
            c.exec(p.parse("insert into _platform.lookup.Lookup(_id, _can_delete, name, description) " +
                               "values(:id, false, :name, :description)"),
                   Param.of("id", id.toString()),
                   Param.of("name", "_report_field_attribute"),
                   Param.of("description", "Internal lookup containing information on attributes for report fields"));

            c.exec(p.parse(
                "insert into _platform.lookup.LookupValue(_id, _can_delete, lookup_id, code, lang, label) values" +
                    "(newid(), false, '" + id + "', 'label',           'en', 'Label'), " +
                    "(newid(), false, '" + id + "', 'short_label',     'en', 'Short label'), " +
                    "(newid(), false, '" + id + "', 'description',     'en', 'Description'), " +
                    "(newid(), false, '" + id + "', 'show',            'en', 'Show'), " +

                    "(newid(), false, '" + id + "', 'format',          'en', 'Format'), " +
                    "(newid(), false, '" + id + "', 'money',           'en', 'Money'), " +
                    "(newid(), false, '" + id + "', 'width',           'en', 'Width (in pixels)'), " +
                    "(newid(), false, '" + id + "', 'span',            'en', 'Span (in grid cells, 1 to 24)'), " +
                    "(newid(), false, '" + id + "', 'offset',          'en', 'Offset (in grid cells, 1 to 24)'), " +
                    "(newid(), false, '" + id + "', 'group',           'en', 'Group'), " +
                    "(newid(), false, '" + id + "', 'subgroup',        'en', 'Subgroup'), " +
                    "(newid(), false, '" + id + "', 'lines',           'en', 'Number of lines for multi-line text edit'), " +

                    "(newid(), false, '" + id + "', 'excel_font',      'en', 'Font'), " +
                    "(newid(), false, '" + id + "', 'excel_alignment', 'en', 'Alignment'), " +
                    "(newid(), false, '" + id + "', 'excel_border',    'en', 'Border'), " +
                    "(newid(), false, '" + id + "', 'excel_fill',      'en', 'Fill'), " +

                    "(newid(), false, '" + id + "', 'dynamic',         'en', 'Dynamic attributes')"));
          }
        }
      }
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
          table = r.getString("table_schema") + '.' + r.getString("table_name");
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

//    protected void loadSequence(Structure structure, ResultSet rs) throws SQLException {
//      Sequence s = new Sequence(
//          this,
//          UUID.fromString(rs.getString("_id")),
//          rs.getString("name"),
//          rs.getLong("start"),
//          rs.getLong("increment"),
//          rs.getLong("maximum"),
//          rs.getBoolean("cycles"));
//      structure.sequence(s);
//    }

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
          onUpdate == null ? NO_ACTION : ConstraintDefinition.ForeignKeyChangeAction.fromMarker(onUpdate.charAt(0)),
          onDelete == null ? NO_ACTION : ConstraintDefinition.ForeignKeyChangeAction.fromMarker(onDelete.charAt(0)));
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

  @Override
  public void tableName(Connection con, UUID tableId, String name) {
    if (hasCoreTables(con)) {
      EsqlConnection econ = esql(con);
      Parser p = new Parser(structure());
      econ.exec(p.parse(
          "update _core.relations SET name='" + name +
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
      econ.exec(p.parse("delete from _core.relations where _id='" + tableId + "'"));
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
      econ.exec(p.parse("update _core.columns set name='" + name + "' where _id='" + columnId + "'"));
    }
  }

  @Override
  public void columnType(Connection con, UUID columnId, String type) {
    if (hasCoreTables(con)) {
      EsqlConnection econ = esql(con);
      Parser p = new Parser(structure());
      econ.exec(p.parse("update _core.columns set type='" + type + "' where _id='" + columnId + "'"));
    }
  }

  @Override
  public void defaultValue(Connection con, UUID columnId, String defaultValue) {
    if (hasCoreTables(con)) {
      EsqlConnection econ = esql(con);
      Parser p = new Parser(structure());
      econ.exec(p.parse(
        "update _core.columns " +
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
            "update _core.columns " +
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
    econ.exec(p.parse("delete from _core.column_attributes where column_id='" + columnId + "'"));
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
      econ.exec(p.parse("delete from _core.columns where _id='" + columnId + "'"));
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
          "delete from _core.constraints " +
              " where relation_id='" + tableId + "' " +
              "   and name='" + constraintName + "'"));
    }
  }

  @Override
  public void clearTableMetadata(Connection con, UUID tableId) {
    if (hasCoreTables(con)) {
      EsqlConnection econ = esql(con);
      Parser p = new Parser(structure());
      econ.exec(p.parse("delete from _core.relation_attributes "
                            + " where relation_id='" + tableId + '\''));
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
              "update _core.relations " +
                  "   set display_name='" + escapeSqlString(tableDisplayName) + "', " +
                  "        description='" + escapeSqlString(tableDescription) + "'" +
                  " where _id='" + tableId + "'"));
        } else if (tableDisplayName != null) {
          econ.exec(p.parse(
              "update _core.relations " +
                  "   set display_name='" + escapeSqlString(tableDisplayName) + "'" +
                  " where _id='" + tableId + "'"));
        } else if (tableDescription != null) {
          econ.exec(p.parse(
              "update _core.relations " +
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
}
