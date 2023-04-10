/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.define.table;

import ma.vi.base.tuple.T2;
import ma.vi.esql.database.Database;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.database.Structure;
import ma.vi.esql.exec.Result;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.semantic.type.BaseRelation;
import ma.vi.esql.semantic.type.Column;
import ma.vi.esql.semantic.type.Relation;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.define.Attribute;
import ma.vi.esql.syntax.define.Define;
import ma.vi.esql.syntax.define.Metadata;
import ma.vi.esql.syntax.expression.ColumnRef;
import org.pcollections.PMap;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;
import static ma.vi.esql.builder.Attributes.HISTORY;
import static ma.vi.esql.database.Database.StructureChange.*;
import static ma.vi.esql.semantic.type.Type.dbTableName;
import static ma.vi.esql.semantic.type.Type.splitName;
import static ma.vi.esql.translation.Translatable.Target.*;

/**
 * Alter table statement.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class AlterTable extends Define {
  public AlterTable(Context context, String name, List<Alteration> alterations) {
    super(context, "AlterTable",
          T2.of("name", new Esql<>(context, name)),
          T2.of("alterations", new Esql<>(context, "alterations", alterations)));
  }

  public AlterTable(AlterTable other) {
    super(other);
  }

  @SafeVarargs
  public AlterTable(AlterTable other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public AlterTable copy() {
    return new AlterTable(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public AlterTable copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new AlterTable(this, value, children);
  }

  @Override
  protected String trans(Target target, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env) {
    StringBuilder st = new StringBuilder("alter table ");
    st.append(dbTableName(name(), target)).append(' ');

    boolean first = true;
    for (Alteration alteration: alterations()) {
      if (first) {
        first = false;
      } else {
        st.append(", ");
      }
      st.append(alteration.translate(target, esqlCon, path.add(alteration), parameters, env));
    }
    return st.toString();
  }

  @Override
  protected Object postTransformExec(Target               target,
                                     EsqlConnection       esqlCon,
                                     EsqlPath             path,
                                     PMap<String, Object> parameters,
                                     Environment          env) {
    Database db = esqlCon.database();
    Connection con = esqlCon.connection();
    String name = name();
    String dbName = dbTableName(name, db.target());
    Structure s = context.structure;
    BaseRelation relation = s.relation(name);
    if (relation == null) {
      throw new IllegalArgumentException("Relation " + name() + " not found");
    }
    try {
      for (Alteration alteration: alterations()) {
        if (alteration instanceof RenameTable rename) {
          /*
           * Rename table
           */
          T2<String, String> split = splitName(name);
          String schema = split.a;
          String newFullName = schema + '.' + rename.toName();

          if (db.target() == SQLSERVER) {
            con.createStatement().executeUpdate(
                "sp_rename '" + dbName + "', '" + rename.toName() + "'");
          } else {
            /*
             * Change the table name only.
             */
            con.createStatement().executeUpdate(
                "ALTER TABLE " + dbName + " RENAME TO \"" + rename.toName() + '"');
          }

          s.remove(relation);
          relation.name(newFullName);
          s.relation(relation);
          s.database.renameTable(esqlCon, relation.id(), newFullName);

          /*
           * Apply change to corresponding history table, if present.
           */
          Boolean history = relation.evaluateAttribute(HISTORY);
          if (history != null && history) {
            AlterTable alter = new AlterTable(context,
                                              name + "$history",
                                              List.of(new RenameTable(context, rename.toName() + "$history")));
            alter.exec(target, esqlCon, path.add(alter), parameters, env);
          }

          /*
           * Add structure change event for table rename event that will be sent
           * to registered subscribers.
           */
          esqlCon.addStructureChange(new Database.StructureChangeEvent(
            TABLE_RENAMED, name, newFullName, null, null));

        } else if (alteration instanceof AddTableDefinition addTable) {
          /*
           * Add table definitions (columns, constraints and metadata)
           */
          TableDefinition definition = addTable.definition();
          if (definition instanceof ColumnDefinition column) {
            T2<Relation, Column> existing = relation.findColumn(ColumnRef.of(null, column.name()));
            if (existing == null) {
              /*
               * add a column to table
               */
              if (!(column instanceof DerivedColumnDefinition)) {
                /*
                 * Alter table only for non-derived columns. Derived columns are
                 * only inserted in the _core.fields table, below.
                 */
                if (db.target() == SQLSERVER) {
                  con.createStatement().executeUpdate("ALTER TABLE " + dbName
                                                    + " ADD " + column.translate(db.target(), esqlCon, path.add(column), env));
                } else {
                  con.createStatement().executeUpdate("ALTER TABLE " + dbName
                                                    + " ADD COLUMN " + column.translate(db.target(), esqlCon, path.add(column), env));
                }
              }

              Column col = Column.fromDefinition(column);
  //            if (parent instanceof CreateTable) {
  //              col.parent = parent;
  //            } else {
  //              col.parent = new Esql<>(column.context, relation);
  //            }
              s.database.column(esqlCon, relation.id(), col, column.seq);
              relation.addColumn(col);

              applyChangeToHistoryTable(relation, context, name(), alteration,
                                        target, esqlCon, path, parameters, env);

              /*
               * Add structure change event for table add column event that will
               * be sent to registered subscribers.
               */
              esqlCon.addStructureChange(new Database.StructureChangeEvent(
                COLUMN_ADDED, name, null, column, column));
            }
          } else if (definition instanceof ConstraintDefinition constraint) {
            /*
             * add a constraint to table
             */
            if (!(constraint instanceof ForeignKeyConstraint)
             || !((ForeignKeyConstraint)constraint).ignore()) {
              con.createStatement().executeUpdate("ALTER TABLE " + dbName +
                                                  " ADD " + constraint.translate(db.target(), esqlCon, path.add(constraint), env));
            }
            s.database.constraint(esqlCon, relation.id(), constraint);
            relation.constraint(constraint);

            if (constraint instanceof ForeignKeyConstraint foreign) {
              BaseRelation targetTable = db.structure().relation(foreign.targetTable());
              if (targetTable != null) {
                targetTable.dependentConstraint(foreign);
              }
            }

          } else if (definition instanceof Metadata metadata) {
            /*
             * add table metadata, removing previous ones
             */
            s.database.clearTableMetadata(esqlCon, relation.id());
            s.database.tableMetadata(esqlCon, relation.id(), metadata);

            relation.clearAttributes();
            relation.attributes(metadata.attributes().values());
//            for (Attribute attr: metadata.attributes().values()) {
//              relation.attribute(attr.name(), attr.attributeValue());
//            }
          }
        } else if (alteration instanceof AlterColumn alterCol) {
          T2<Relation, Column> c = relation.column(ColumnRef.of(null, alterCol.columnName()));
          if (c == null) {
            throw new IllegalArgumentException("Relation " + name() + " does not contain column " + alterCol.columnName());
          }
          Column column = c.b();
          AlterColumnDefinition def = alterCol.definition();
          if (def.toName() != null) {
            /*
             * change column name.
             */
            if (!column.derived()) {
              if (db.target() == SQLSERVER) {
                con.createStatement().executeUpdate(
                    "sp_rename '" + dbName + ".\"" + column.name() + "\"', '" + def.toName() + "', 'COLUMN'");
              } else {
                con.createStatement().executeUpdate(
                    "ALTER TABLE " + dbName
                  + " RENAME COLUMN \"" + column.name()
                  + "\"  TO \"" + def.toName() + '"');
              }
            }

            applyChangeToHistoryTable(relation, context, name(), alteration,
                                      target, esqlCon, path, parameters, env);

            s.database.columnName(esqlCon, column.id(), def.toName());
            relation.removeColumn(alterCol.columnName());
            Column newColumn = column.name(def.toName());
            relation.addColumn(newColumn);

            /*
             * Add structure change event for column renamed event that will be
             * sent to registered subscribers.
             */
            esqlCon.addStructureChange(new Database.StructureChangeEvent(
              COLUMN_RENAMED, name, null,
              Column.toDefinition(column),
              Column.toDefinition(newColumn)));
          }
          if (def.toType() != null) {
            /*
             * change column type.
             */
            if (!column.derived()) {
              if (db.target() == POSTGRESQL) {
                String type = def.toType().translate(db.target(), esqlCon, path.add(def), env);
                con.createStatement().executeUpdate(
                  "ALTER TABLE " + dbName
                + " ALTER COLUMN \"" + column.name() + '"'
                + " TYPE " + type
                + " USING \"" + column.name() + "\"::" + type);
              } else {
                con.createStatement().executeUpdate(
                    "ALTER TABLE " + dbName +
                        " ALTER COLUMN \"" + column.name() +
                        "\" " + def.toType().translate(db.target(), esqlCon, path.add(def), env));
              }
            }

            applyChangeToHistoryTable(relation, context, name(), alteration,
                                      target, esqlCon, path, parameters, env);

            ColumnDefinition fromDefinition = Column.toDefinition(column);
            s.database.columnType(esqlCon, column.id(), def.toType().translate(ESQL, esqlCon, path.add(def), env));
//            column = column.type(def.toType());
            column.type(def.toType());
            relation.addOrReplaceColumn(column);

            /*
             * Add structure change event for column change type event that will
             * be sent to registered subscribers.
             */
            esqlCon.addStructureChange(new Database.StructureChangeEvent(
              COLUMN_TYPE_CHANGED, name, null,
              fromDefinition,
              Column.toDefinition(column)));
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
              if (db.target() == SQLSERVER) {
                // find name of default constraint to drop
                try (ResultSet rs = con.createStatement().executeQuery(
                    "SELECT d.name " +
                        "  FROM sys.default_constraints d " +
                        "  JOIN sys.columns c ON d.parent_column_id=c.column_id " +
                        " WHERE d.parent_object_id=OBJECT_ID(N'" + dbName + "') " +
                        "   AND c.object_id=OBJECT_ID(N'" + dbName + "') " +
                        "   AND c.name='" + column.name() + "'")) {
                  if (rs.next()) {
                    String defaultConstraint = rs.getString(1);
                    con.createStatement().executeUpdate(
                        "ALTER TABLE " + dbName + " DROP CONSTRAINT " + defaultConstraint);
                  }
                }
              } else {
                // drop default
                con.createStatement().executeUpdate(
                  "ALTER TABLE " + dbName + " ALTER COLUMN \"" + column.name() + "\" DROP DEFAULT");
              }
              s.database.defaultValue(esqlCon, column.id(), null);
              relation.addOrReplaceColumn(column.defaultExpression(null));
            }
          }
          if (def.setDefault() != null) {
            /*
             * set default on column.
             */
            if (!column.derived()) {
              if (db.target() == SQLSERVER) {
                con.createStatement().executeUpdate(
                    "ALTER TABLE " + dbName +
                        " ADD DEFAULT " + def.setDefault().translate(db.target(), esqlCon, path.add(def.setDefault()), env) +
                        " FOR \"" + column.name() + '"');
              } else {
                // change the table name only
                con.createStatement().executeUpdate(
                    "ALTER TABLE " + dbName +
                        " ALTER COLUMN \"" + column.name() +
                        "\" SET DEFAULT " + def.setDefault().translate(db.target(), esqlCon, path.add(def.setDefault()), env));
              }
              s.database.defaultValue(esqlCon, column.id(), def.setDefault().translate(ESQL, esqlCon, path.add(def.setDefault()), env).toString());
              relation.addOrReplaceColumn(column.defaultExpression(def.setDefault()));
            }
          }
          if (def.setNotNull()) {
            /*
             * modify field so that it cannot take nulls.
             * this will fail if field already contain some null values.
             */
            if (!column.derived()) {
              if (db.target() == SQLSERVER) {
                con.createStatement().executeUpdate(
                    "ALTER TABLE " + dbName +
                        " ALTER COLUMN \"" + column.name() +
                        "\" " + column.computeType(path.add(column)).translate(db.target(), esqlCon, path.add(column), env) + " NOT NULL");
              } else {
                con.createStatement().executeUpdate(
                    "ALTER TABLE " + dbName +
                        " ALTER COLUMN \"" + column.name() +
                        "\" SET NOT NULL");
              }
              s.database.notNull(esqlCon, column.id(), (db.target() == SQLSERVER ? "1" : "true"));
              relation.addOrReplaceColumn(column.notNull(true));
            }
          }
          if (def.dropNotNull()) {
            /*
             * modify field so that it can take nulls.
             */
            if (!column.derived()) {
              if (db.target() == SQLSERVER) {
                con.createStatement().executeUpdate(
                    "ALTER TABLE " + dbName +
                        " ALTER COLUMN \"" + column.name() +
                        "\" " + column.computeType(path.add(column)).translate(db.target(), esqlCon, path.add(column), env) + " NULL");
              } else {
                con.createStatement().executeUpdate(
                    "ALTER TABLE " + dbName +
                        " ALTER COLUMN \"" + column.name() +
                        "\" DROP NOT NULL");
              }
              s.database.notNull(esqlCon, column.id(), db.target() == SQLSERVER ? "0" : "false");
              relation.addOrReplaceColumn(column.notNull(false));
            }
          }

          if (def.metadata() != null
           && def.metadata().attributes() != null
           && !def.metadata().attributes().isEmpty()) {
            /*
             * add field metadata, removing previous ones
             */
            s.database.columnMetadata(esqlCon, column.id(), def.metadata());
            Map<String, Attribute> attrs = new LinkedHashMap<>(column.metadata().attributes());
            attrs.putAll(def.metadata().attributes());
            Metadata metadata = new Metadata(def.context, new ArrayList<>(attrs.values()));
            relation.addOrReplaceColumn(column.set("metadata", metadata));
          }
        } else if (alteration instanceof DropColumn drop) {
          /*
           * Drop a column.
           */
          T2<Relation, Column> col = relation.column(ColumnRef.of(null, drop.columnName()));
          Column column = col.b();
          if (column == null) {
            throw new IllegalArgumentException("Relation " + name() + " does not contain column " + drop.columnName());
          }

          // drop constraints referring to column before dropping column
          List<ConstraintDefinition> constraints = relation.constraints();
          if (!constraints.isEmpty()) {
            for (ConstraintDefinition c: new ArrayList<>(constraints)) {
              if (c.columns().contains(column.name())) {
                new AlterTable(context, relation.name(),
                               singletonList(new DropConstraint(context, c.name()))).exec(target, esqlCon, path, parameters, env);
              }
            }
          }

          if (!column.derived()) {
            con.createStatement().executeUpdate(
                "ALTER TABLE " + dbName + " DROP COLUMN \"" + drop.columnName() + '"');
          }

          applyChangeToHistoryTable(relation, context, name(), alteration,
                                    target, esqlCon, path, parameters, env);

          s.database.dropColumn(esqlCon, column.id());
          relation.removeColumn(drop.columnName());

          /*
           * Add structure change event for table drop column event that will
           * be sent to registered subscribers.
           */
          ColumnDefinition colDef = Column.toDefinition(column);
          esqlCon.addStructureChange(new Database.StructureChangeEvent(
            COLUMN_DROPPED, name, null, colDef, colDef));

        } else if (alteration instanceof DropConstraint drop) {
          /*
           * Drop a constraint.
           */
          ConstraintDefinition c = relation.constraint(drop.constraintName());

          if (c == null) {
            throw new IllegalArgumentException("Relation " + name() + " does not have constraint " + drop.constraintName());
          }
          con.createStatement().executeUpdate(
            "ALTER TABLE " + dbName + " DROP CONSTRAINT IF EXISTS \"" + drop.constraintName() + '"');

          s.database.dropConstraint(esqlCon, relation.id(), drop.constraintName());
          relation.removeConstraint(drop.constraintName());

        } else if (alteration instanceof DropMetadata) {
          /*
           * Drop all metadata for the table.
           */
          s.database.clearTableMetadata(esqlCon, relation.id());
          relation.clearAttributes();
        }
      }
    } catch(SQLException e) {
      throw new RuntimeException(e);
    }
    return Result.Nothing;
  }

  /**
   * Apply change to a table to its corresponding history table, if present.
   */
  private static void applyChangeToHistoryTable(BaseRelation         relation,
                                                Context              context,
                                                String               name,
                                                Alteration           alteration,
                                                Target               target,
                                                EsqlConnection       esqlCon,
                                                EsqlPath             path,
                                                PMap<String, Object> parameters,
                                                Environment          env) {
    Boolean history = relation.evaluateAttribute(HISTORY);
    if (history != null && history) {
      AlterTable alter = new AlterTable(context,
                                        name + "$history",
                                        List.of(alteration));
      alter.exec(target, esqlCon, path.add(alter), parameters, env);
    }
  }

  public String name() {
    return childValue("name");
  }

  public List<Alteration> alterations() {
    return child("alterations").children();
  }
}
