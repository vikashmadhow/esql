/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.esql.parser.define;

import ma.vi.base.tuple.T2;
import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.Esql;
import ma.vi.esql.parser.expression.Expression;
import ma.vi.esql.parser.query.Column;
import ma.vi.esql.exec.Result;
import ma.vi.esql.type.BaseRelation;
import ma.vi.esql.type.Type;
import ma.vi.esql.database.Structure;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.System.Logger.Level.ERROR;
import static java.util.Collections.singletonList;
import static ma.vi.esql.parser.Translatable.Target.SQLSERVER;
import static ma.vi.esql.builder.Attributes.DESCRIPTION;
import static ma.vi.esql.builder.Attributes.NAME;
import static ma.vi.esql.type.Type.dbName;

public class CreateTable extends Define<String> {
  public CreateTable(Context context,
                     String name, boolean dropUndefined,
                     List<ColumnDefinition> columns,
                     List<ConstraintDefinition> constraints,
                     Metadata metadata) {
    super(context, name,
          T2.of("dropUndefined", new Esql<>(context, dropUndefined)),
          T2.of("columns", new Esql<>(context, "columns", columns)),
          T2.of("constraints", new Esql<>(context, "constraints", constraints)),
          T2.of("metadata", metadata));
  }

  public CreateTable(CreateTable other) {
    super(other);
  }

  @Override
  public CreateTable copy() {
    if (!copying()) {
      try {
        copying(true);
        return new CreateTable(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public String translate(Target target) {
    StringBuilder st = new StringBuilder("create table ");
    if (target != SQLSERVER) {
      st.append("if not exists ");
    }
    st.append(dbName(name(), target)).append('(');

    boolean first = true;
    for (ColumnDefinition column: columns()) {
      String def = column.translate(target);
      if (def != null) {
        if (first) {
          first = false;
        } else {
          st.append(", ");
        }
        st.append(def);
      }
    }
    for (ConstraintDefinition constraint: constraints()) {
      st.append(", ").append(constraint.translate(target));
    }
    st.append(')');
    return st.toString();
  }

  @Override
  public Result execute(Connection con,
                        Structure structure,
                        Target target) {
    try {
      // create schema if it does not exist already
      String tableName = name();
      T2<String, String> splitName = Type.splitName(tableName);
      String schema = splitName.a;
      if (target == SQLSERVER) {
        try (ResultSet rs = con.createStatement().executeQuery(
            "select name from sys.schemas where name='" + schema + "'")) {
          if (!rs.next()) {
            con.createStatement().executeUpdate("create schema \"" + schema + '"');
          }
        }
      } else {
        con.createStatement().executeUpdate("create schema if not exists \"" + schema + '"');
      }

      // create or alter table
//      if (system) {
//        /*
//         * For system tables, automatic alterations are not performed as
//         * information on the structure of such tables are not kept.
//         */
//        if (target == SQLSERVER) {
//          try (ResultSet rs = con.createStatement().executeQuery("select OBJECT_ID('" + dbName(tableName) + "')")) {
//            rs.next();
//            Object id = rs.getObject(1);
//            if (id == null) {
//              con.createStatement().executeUpdate(creationSql);
//            }
//          }
//        } else {
//          con.createStatement().executeUpdate(creationSql);
//        }
//
//      } else {

        // update structure if this was not a system table creation and
        // the table did not exist before
        if (!structure.relationExists(tableName)) {
          /*
           * Does not exist: create
           */
          con.createStatement().executeUpdate(translate(target));

          /*
           * Get table name and description from table attributes if specified
           */
          String tableDisplayName = metadata().evaluateAttribute(NAME);
          String tableDescription = metadata().evaluateAttribute(DESCRIPTION);
          BaseRelation table = new BaseRelation(context,
                                                UUID.randomUUID(),
                                                tableName,
                                                tableDisplayName,
                                                tableDescription,
                                                new ArrayList<>(metadata().attributes().values()),
                                                columns().stream()
                                                         .map(Column::fromDefinition)
                                                         .collect(Collectors.toList()),
                                                constraints());
          structure.relation(table);
          structure.database.table(con, table);
        } else {
          /*
           * already exists: alter
           */
          BaseRelation table = structure.relation(tableName);
          AlterTable alter;

          if (metadata() != null
           && metadata().attributes() != null
           && !metadata().attributes().isEmpty()) {
            /*
             * alter table metadata if specified
             */
            alter = new AlterTable(context,
                                   tableName,
                                   singletonList(new AddTableDefinition(context, metadata())));
            alter.execute(con, structure, target);
          }

          // add missing columns and alter existing ones if needed
          Map<String, ColumnDefinition> columns = new HashMap<>();
          for (ColumnDefinition column: columns()) {
            columns.put(column.name(), column);
            Column existingColumn = table.column(column.name());
            if (existingColumn == null) {
              /*
               * No existing field with that name: add
               */
              alter = new AlterTable(context,
                                     tableName,
                                     singletonList(new AddTableDefinition(context, column)));
              alter.execute(con, structure, target);
            } else {
              /*
               * alter existing field
               */
              Type toType = !existingColumn.type().equals(column.type()) ? column.type() : null;

              boolean setNotNull = column.notNull() != null
                                && column.notNull()
                                && !existingColumn.notNull();

              boolean dropNotNull = (column.notNull() == null || !column.notNull())
                                 && existingColumn.notNull();

              Expression<?> setDefault = null;
              if (column.defaultExpression() != null
               && (existingColumn.defaultExpression() == null
                || !column.defaultExpression().equals(existingColumn.defaultExpression()))) {
                setDefault = column.defaultExpression();
//                dropDefault = existingColumn.defaultExpression() != null;
              }

              boolean dropDefault = setDefault == null
                                 && existingColumn.defaultExpression() != null;

              Metadata metadata = column.metadata();
              if (toType != null
               || setNotNull
               || dropNotNull
               || setDefault != null
               || dropDefault
               || (metadata != null && metadata.attributes() != null && !metadata.attributes().isEmpty())) {
                alter = new AlterTable(context, tableName,
                                       singletonList(new AlterColumn(context, column.name(),
                                                                     new AlterColumnDefinition(context,
                                                                                               null,
                                                                                               toType,
                                                                                               setNotNull,
                                                                                               dropNotNull,
                                                                                               setDefault,
                                                                                               dropDefault,
                                                                                               metadata))));
                alter.execute(con, structure, target);
              }
            }
          }

          // add missing constraints and alter existing ones if needed
          for (ConstraintDefinition constraint: constraints()) {
            ConstraintDefinition tableConstraint = table.sameAs(constraint);
            if (tableConstraint == null) {
              /*
               * A constraint same as this one does not exist;: add it
               */
              alter = new AlterTable(context, tableName,
                                     singletonList(new AddTableDefinition(context, constraint)));
              alter.execute(con, structure, target);
            }
          }

          // drop existing definitions (columns, constraints and metadata) from the table
          // which have not been defined in the current create statement if 'drop undefined'
          // clause was explicitly set in command.
          if (dropUndefined()) {
            /*
             * drop excess fields
             */
            for (Column column: table.columns()) {
              if (!columns.containsKey(column.alias())) {
                /*
                 * Drop existing field not specified in create command
                 */
                alter = new AlterTable(context, tableName,
                                       singletonList(new DropColumn(context, column.alias())));
                alter.execute(con, structure, target);
              }
            }

            /*
             * drop excess constraints
             */
            List<String> toDrop = new ArrayList<>();
            for (ConstraintDefinition tableConstraint: table.constraints()) {
              boolean found = false;
              for (ConstraintDefinition c: constraints()) {
                if (tableConstraint.sameAs(c)) {
                  found = true;
                  break;
                }
              }
              if (!found) {
                /*
                 * No definition found for this constraint: drop
                 */
                toDrop.add(tableConstraint.name());
              }
            }
            for (String c: toDrop) {
              new AlterTable(context, tableName, singletonList(new DropConstraint(context, c))).execute(con, structure, target);
            }

            /*
             * Drop table metadata if no metadata specified in create
             */
            if (metadata() == null
             || metadata().attributes() == null
             || metadata().attributes().isEmpty()) {
              alter = new AlterTable(context, tableName, singletonList(new DropMetadata(context)));
              alter.execute(con, structure, target);
            }
          }
        }
//      }
      return Result.Nothing;
    } catch (SQLException e) {
      log.log(ERROR, e.getMessage());
      log.log(ERROR, "Original query: " + this);
      throw new RuntimeException(e);
    }
  }

  public String name() {
    return value;
  }

  public Boolean dropUndefined() {
    return childValue("dropUndefined");
  }

  public List<ColumnDefinition> columns() {
    return child("columns").childrenList();
  }

  public List<ConstraintDefinition> constraints() {
    return child("constraints").childrenList();
  }

  public Metadata metadata() {
    return child("metadata");
  }

  private static final System.Logger log = System.getLogger(CreateTable.class.getName());
}
