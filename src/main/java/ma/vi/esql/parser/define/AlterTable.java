/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.define;

import ma.vi.base.tuple.T2;
import ma.vi.esql.database.Structure;
import ma.vi.esql.exec.Result;
import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.Esql;
import ma.vi.esql.parser.query.Column;
import ma.vi.esql.type.BaseRelation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static java.util.Collections.singletonList;
import static ma.vi.esql.parser.Translatable.Target.ESQL;
import static ma.vi.esql.parser.Translatable.Target.SQLSERVER;
import static ma.vi.esql.type.Type.dbName;
import static ma.vi.esql.type.Type.splitName;

public class AlterTable extends Define<String> {
  public AlterTable(Context context, String name, List<AlterTableAction> alterations) {
    super(context, name,
          T2.of("alterations", new Esql<>(context, "alterations", alterations)));
  }

  public AlterTable(AlterTable other) {
    super(other);
  }

  @Override
  public AlterTable copy() {
    if (!copying()) {
      try {
        copying(true);
        return new AlterTable(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public String translate(Target target) {
    StringBuilder st = new StringBuilder("alter table ");
    st.append(dbName(name(), target)).append(' ');

    boolean first = true;
    for (AlterTableAction alteration: alterations()) {
      if (first) {
        first = false;
      } else {
        st.append(", ");
      }
      st.append(alteration.translate(target));
    }
    return st.toString();
  }

  @Override
  public Result execute(Connection con, Structure structure, Target target) {
    String name = name();
    String dbName = dbName(name, target);
    Structure s = context.structure;
    BaseRelation relation = s.relation(name);
    if (relation == null) {
      throw new IllegalArgumentException("Relation " + name() + " not found");
    }
    try {
      for (AlterTableAction alteration: alterations()) {
        if (alteration instanceof RenameTable) {
          /*
           * Rename table
           */
          RenameTable rename = (RenameTable)alteration;
          T2<String, String> split = splitName(name);
          String schema = split.a;
          String newFullName = schema + '.' + rename.toName();

          if (target == SQLSERVER) {
            con.createStatement().executeUpdate(
                "sp_rename '" + dbName + "', '" + rename.toName() + "'");
          } else {
            // change the table name only
            con.createStatement().executeUpdate(
                "ALTER TABLE " + dbName + " RENAME TO \"" + rename.toName() + '"');
          }

          s.remove(relation);
          relation.name(newFullName);
          s.relation(relation);
          s.database.tableName(con, relation.id(), newFullName);

        } else if (alteration instanceof AddTableDefinition) {
          /*
           * Add table definitions (columns, constraints and metadata)
           */
          TableDefinition definition = ((AddTableDefinition)alteration).definition();
          if (definition instanceof ColumnDefinition) {
            /*
             * add a column to table
             */
            ColumnDefinition column = (ColumnDefinition)definition;
            if (!(column instanceof DerivedColumnDefinition)) {
              /*
               * Alter table only for non-derived columns. Derived columns
               * are only inserted in the _core.fields table, below.
               */
              if (target == SQLSERVER) {
                con.createStatement().executeUpdate("ALTER TABLE " + dbName + " ADD " + column.translate(target));
              } else {
                con.createStatement().executeUpdate("ALTER TABLE " + dbName +
                                                        " ADD COLUMN " + column.translate(target));
              }
            }
            Column col = Column.fromDefinition(column);
            s.database.column(con, relation.id(), col);
            relation.addColumn(col);

          } else if (definition instanceof ConstraintDefinition) {
            /*
             * add a constraint to table
             */
            ConstraintDefinition constraint = (ConstraintDefinition)definition;
            con.createStatement().executeUpdate("ALTER TABLE " + dbName +
                                                    " ADD " + constraint.translate(target));
            s.database.constraint(con, relation.id(), constraint);
            relation.constraint(constraint);

            if (constraint instanceof ForeignKeyConstraint) {
              ForeignKeyConstraint foreign = (ForeignKeyConstraint)constraint;
              BaseRelation targetTable = structure.relation(foreign.targetTable());
              if (targetTable != null) {
                targetTable.dependentConstraint(foreign);
              }
            }

          } else if (definition instanceof Metadata) {
            /*
             * add table metadata, removing previous ones
             */
            Metadata metadata = (Metadata)definition;
            s.database.clearTableMetadata(con, relation.id());
            s.database.tableMetadata(con, relation.id(), metadata);

            relation.clearAttributes();
            for (Attribute attr: metadata.attributes().values()) {
              relation.attribute(attr.name(), attr.attributeValue());
            }
          }
        } else if (alteration instanceof AlterColumn) {
          AlterColumn alterCol = (AlterColumn)alteration;
          Column column = relation.column(alterCol.columnName());
          if (column == null) {
            throw new IllegalArgumentException("Relation " + name() + " does not contain column " + alterCol.columnName());
          }
          AlterColumnDefinition def = alterCol.definition();
          if (def.toName() != null) {
            /*
             * change column name.
             */
            if (!column.derived()) {
              if (target == SQLSERVER) {
                con.createStatement().executeUpdate(
                    "sp_rename '" + dbName + ".\"" + column.alias() + "\"', '" + def.toName() + "', 'COLUMN'");
              } else {
                con.createStatement().executeUpdate(
                    "ALTER TABLE " + dbName +
                        " ALTER COLUMN \"" + column.alias() +
                        "\" RENAME TO \"" + def.toName() + '"');
              }
              s.database.columnName(con, column.id(), def.toName());
              column.alias(def.toName());
            }
          }
          if (def.toType() != null) {
            /*
             * change column type.
             */
            if (!column.derived()) {
              if (target == SQLSERVER) {
                con.createStatement().executeUpdate(
                    "ALTER TABLE " + dbName +
                        " ALTER COLUMN \"" + column.alias() +
                        "\" " + def.toType().translate(target));
              } else {
                con.createStatement().executeUpdate(
                    "ALTER TABLE " + dbName +
                        " ALTER COLUMN \"" + column.alias() +
                        "\" TYPE " + def.toType().translate(target));
              }
              s.database.columnType(con, column.id(), def.toType().translate(ESQL));
              column.type(def.toType());
            }
          }

          /*
           * drop default when a new default is specified
           */
          if (def.setDefault() != null
           || (def.dropDefault() != null && def.dropDefault())) {
            /*
             * remove default on column.
             */
            if (!column.derived()) {
              if (target == SQLSERVER) {
                // find name of default constraint to drop
                try (ResultSet rs = con.createStatement().executeQuery(
                    "SELECT d.name " +
                        "  FROM sys.default_constraints d " +
                        "  JOIN sys.columns c ON d.parent_column_id=c.column_id " +
                        " WHERE d.parent_object_id=OBJECT_ID(N'" + dbName + "') " +
                        "   AND c.object_id=OBJECT_ID(N'" + dbName + "') " +
                        "   AND c.name='" + column.alias() + "'")) {
                  if (rs.next()) {
                    String defaultConstraint = rs.getString(1);
                    con.createStatement().executeUpdate(
                        "ALTER TABLE " + dbName + " DROP CONSTRAINT " + defaultConstraint);
                  }
                }
              } else {
                // drop default
                con.createStatement().executeUpdate(
                  "ALTER TABLE " + dbName + " ALTER COLUMN \"" + column.alias() + "\" DROP DEFAULT");
              }
              s.database.defaultValue(con, column.id(), null);
              column.defaultExpression(null);
            }
          }
          if (def.setDefault() != null) {
            /*
             * set default on column.
             */
            if (!column.derived()) {
              if (target == SQLSERVER) {
                con.createStatement().executeUpdate(
                    "ALTER TABLE " + dbName +
                        " ADD DEFAULT " + def.setDefault().translate(target) +
                        " FOR \"" + column.alias() + '"');
              } else {
                // change the table name only
                con.createStatement().executeUpdate(
                    "ALTER TABLE " + dbName +
                        " ALTER COLUMN \"" + column.alias() +
                        "\" SET DEFAULT " + def.setDefault().translate(target));
              }
              s.database.defaultValue(con, column.id(), def.setDefault().translate(ESQL));
              column.defaultExpression(def.setDefault());
            }
          }
          if (def.setNotNull()) {
            /*
             * modify field so that it cannot take nulls.
             * this will fail if field already contain some null values.
             */
            if (!column.derived()) {
              if (target == SQLSERVER) {
                con.createStatement().executeUpdate(
                    "ALTER TABLE " + dbName +
                        " ALTER COLUMN \"" + column.alias() +
                        "\" " + column.type().translate(target) + " NOT NULL");
              } else {
                con.createStatement().executeUpdate(
                    "ALTER TABLE " + dbName +
                        " ALTER COLUMN \"" + column.alias() +
                        "\" SET NOT NULL");
              }
              s.database.notNull(con, column.id(), (target == SQLSERVER ? "1" : "true"));
              column.notNull(true);
            }
          }
          if (def.dropNotNull()) {
            /*
             * modify field so that it can take nulls.
             */
            if (!column.derived()) {
              if (target == SQLSERVER) {
                con.createStatement().executeUpdate(
                    "ALTER TABLE " + dbName +
                        " ALTER COLUMN \"" + column.alias() +
                        "\" " + column.type().translate(target) + " NULL");
              } else {
                con.createStatement().executeUpdate(
                    "ALTER TABLE " + dbName +
                        " ALTER COLUMN \"" + column.alias() +
                        "\" DROP NOT NULL");
              }
              s.database.notNull(con, column.id(), target == SQLSERVER ? "0" : "false");
              column.notNull(false);
            }
          }

          if (def.metadata() != null
           && def.metadata().attributes() != null
           && !def.metadata().attributes().isEmpty()) {
            /*
             * add field metadata, removing previous ones
             */
            s.database.columnMetadata(con, column.id(), def.metadata());
            for (Attribute attr: def.metadata().attributes().values()) {
              column.attribute(attr.name(), attr.attributeValue());
            }
          }
        } else if (alteration instanceof DropColumn) {
          /*
           * Drop a column.
           */
          DropColumn drop = (DropColumn)alteration;
          Column column = relation.column(drop.columnName());
          if (column == null) {
            throw new IllegalArgumentException("Relation " + name() + " does not contain column " + drop.columnName());
          }

          // drop constraints referring to column before dropping column
          List<ConstraintDefinition> constraints = relation.constraints();
          if (!constraints.isEmpty()) {
            for (ConstraintDefinition c: constraints) {
              if (c.columns().contains(column.alias())) {
                new AlterTable(context, relation.name(),
                               singletonList(new DropConstraint(context, c.name()))).execute(con, s, target);
              }
            }
          }

          con.createStatement().executeUpdate(
            "ALTER TABLE " + dbName + " DROP COLUMN \"" + drop.columnName() + '"');

//          con.createStatement().executeUpdate(
//            "ALTER TABLE " + dbName + " DROP COLUMN IF EXISTS \"" + drop.columnName() + '"');

          s.database.dropColumn(con, column.id());
          relation.removeColumn(drop.columnName());

        } else if (alteration instanceof DropConstraint) {
          /*
           * Drop a constraint.
           */
          DropConstraint drop = (DropConstraint)alteration;
          ConstraintDefinition c = relation.constraint(drop.constraintName());

          if (c == null) {
            throw new IllegalArgumentException("Relation " + name() + " does not have constraint " + drop.constraintName());
          }
          con.createStatement().executeUpdate(
            "ALTER TABLE " + dbName + " DROP CONSTRAINT \"" + drop.constraintName() + '"');

//          con.createStatement().executeUpdate(
//            "ALTER TABLE " + dbName + " DROP CONSTRAINT IF EXISTS \"" + drop.constraintName() + '"');

          s.database.dropConstraint(con, relation.id(), drop.constraintName());
          relation.removeConstraint(drop.constraintName());

        } else if (alteration instanceof DropMetadata) {
          /*
           * Drop all metadata for the table.
           */
          s.database.clearTableMetadata(con, relation.id());
          relation.clearAttributes();
        }
      }
    } catch(SQLException e) {
      throw new RuntimeException(e);
    }
    return Result.Nothing;
  }

  public String name() {
    return value;
  }

  public List<AlterTableAction> alterations() {
    return child("alterations").childrenList();
  }
}
