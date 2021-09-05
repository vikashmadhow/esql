/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.define;

import ma.vi.base.collections.Sets;
import ma.vi.base.tuple.T2;
import ma.vi.esql.database.Database;
import ma.vi.esql.exec.Result;
import ma.vi.esql.syntax.*;
import ma.vi.esql.syntax.expression.ColumnRef;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.query.Column;
import ma.vi.esql.syntax.query.Select;
import ma.vi.esql.semantic.type.BaseRelation;
import ma.vi.esql.semantic.type.Type;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.System.Logger.Level.ERROR;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static ma.vi.esql.builder.Attributes.DESCRIPTION;
import static ma.vi.esql.builder.Attributes.NAME;
import static ma.vi.esql.syntax.Translatable.Target.*;
import static ma.vi.esql.semantic.type.Type.dbTableName;

/**
 * Create table statement.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class CreateTable extends Define<String> {
  public CreateTable(Context                    context,
                     String                     name,
                     boolean                    dropUndefined,
                     List<ColumnDefinition>     columns,
                     List<ConstraintDefinition> constraints,
                     Metadata                   metadata) {
    super(context, name,
          T2.of("dropUndefined", new Esql<>(context, dropUndefined)),
          T2.of("columns",       new Esql<>(context, "columns", columns)),
          T2.of("constraints",   new Esql<>(context, "constraints", constraints)),
          T2.of("metadata",      metadata));

    /*
     * verify that there are no circular references in derived columns
     */
    Set<String> persistentCols = columns.stream()
                                        .filter(c -> !(c instanceof DerivedColumnDefinition))
                                        .map(ColumnDefinition::name)
                                        .collect(Collectors.toSet());
    Map<String, Expression<?, String>> derivedCols = columns.stream()
                                                    .filter(c -> c instanceof DerivedColumnDefinition)
                                                    .collect(Collectors.toMap(ColumnDefinition::name,
                                                                              ColumnDefinition::expression));

    for (ColumnDefinition column: columns) {
      if (column instanceof DerivedColumnDefinition) {
        circular(column.name(), column.expression(), persistentCols, derivedCols, new ArrayList<>());
      }
    }
  }

  public static void circular(String column,
                              Expression<?, String> expression,
                              Set<String> persistentCols,
                              Map<String, Expression<?, String>> derivedCols,
                              List<String> circularPath) {
    try {
      circularPath.add(column);
      expression.forEach(e -> {
        if (e instanceof ColumnRef) {
          ColumnRef ref = (ColumnRef)e;
          String colName = ref.name();
          if (!persistentCols.contains(colName) && !derivedCols.containsKey(colName)) {
            throw new TranslationException("Unknown column " + colName
                                         + " in derived expression " + expression);
          } else if (circularPath.contains(colName)) {
            circularPath.add(colName);
            throw new CircularReferenceException("A circular reference was detected in the expression "
                                               + expression + " consisting of the column path " + circularPath);
          } else if (derivedCols.containsKey(colName)) {
            circular(colName, derivedCols.get(colName), persistentCols, derivedCols, circularPath);
          }
        }
        return true;
      },
      e -> !(e instanceof Select));
    } finally {
      circularPath.remove(column);
    }
  }

  public CreateTable(CreateTable other) {
    super(other);
  }

  @Override
  public CreateTable copy() {
    return new CreateTable(this);
  }

  @Override
  protected String trans(Target target, EsqlPath path, Map<String, Object> parameters) {
    StringBuilder st = new StringBuilder("create table ");
    if (target != SQLSERVER) {
      st.append("if not exists ");
    }
    st.append(dbTableName(name(), target)).append('(');

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
      st.append(", ").append(constraint.translate(target, parameters));
    }
    st.append(')');
    return st.toString();
  }

  @Override
  public Result execute(Database db, Connection con) {
    try {
      // create schema if it does not exist already
      String tableName = name();
      T2<String, String> splitName = Type.splitName(tableName);
      String schema = splitName.a;
      if (schema != null) {
        if (db.target() == SQLSERVER) {
          try (ResultSet rs = con.createStatement().executeQuery(
              "select name from sys.schemas where name='" + schema + "'")) {
            if (!rs.next()) {
              con.createStatement().executeUpdate("create schema \"" + schema + '"');
            }
          }
        } else if (db.target() != MARIADB && db.target() != MYSQL) {
          con.createStatement().executeUpdate("create schema if not exists \"" + schema + '"');
        }
      }

      // create or alter table
      // update structure if this was not a system table creation and
      // the table did not exist before
      if (!db.structure().relationExists(tableName)) {
        List<ConstraintDefinition> constraints = constraints();
        if (db.target() == MARIADB || db.target() == MYSQL) {
          /*
           * For some reasons, mariadb and mysql cannot create a foreign key
           * from a field (relation_id) that is also part of a composite unique
           * key. As a workaround, we simply drop such unique constraints.
           */
          Set<String> columnsInForeignKey =
              constraints.stream()
                         .filter(c -> c instanceof ForeignKeyConstraint)
                         .flatMap(c -> ((ForeignKeyConstraint)c).sourceColumns().stream())
                         .collect(toSet());

          int size = constraints.size();
          constraints.removeIf(c -> c instanceof UniqueConstraint
              && !Sets.intersect(new HashSet<>(c.columns()),
                                 columnsInForeignKey).isEmpty());
          if (constraints.size() != size) {
            constraints(constraints);
          }
        }

        /*
         * Get table name and description from table attributes if specified.
         * Errors in the table structure are detected when creating the BaseRelation
         * instance and will prevent the creation to go through if the structure
         * is not valid.
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
                                                       .collect(toList()),
                                              constraints);
        /*
         * Does not exist and valid: create
         */
        con.createStatement().executeUpdate(translate(db.target()));
        db.structure().relation(table);
        table.expandColumns();
        db.structure().database.table(con, table);
      } else {
        /*
         * already exists: alter
         */
        BaseRelation table = db.structure().relation(tableName);
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
          alter.parent = this;
          alter.execute(db, con);
        }

        // add missing columns and alter existing ones if needed
        Map<String, ColumnDefinition> columns = new HashMap<>();
        for (ColumnDefinition column: columns()) {
          columns.put(column.name(), column);
          Column existingColumn = table.findColumn(null, column.name());
          if (existingColumn == null) {
            /*
             * No existing field with that name: add
             */
            alter = new AlterTable(context,
                                   tableName,
                                   singletonList(new AddTableDefinition(context, column)));
            alter.parent = this;
            alter.execute(db, con);
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

            Expression<?, String> setDefault = null;
            if (column.expression() != null
             && !column.expression().equals(existingColumn.defaultExpression())) {
              setDefault = column.expression();
            }
            boolean dropDefault = column.expression() == null
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
              alter.parent = this;
              alter.execute(db, con);
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
            new AlterTable(context, tableName,
                           singletonList(new AddTableDefinition(context, constraint))).execute(db, con);
          } else {
            /*
             * There is a similar constraint. For foreign keys check that the cascading
             * parameters have not changed. If so, update the constraint by dropping
             * the existing one and recreating it with the new definition.
             */
            if (constraint instanceof ForeignKeyConstraint) {
              ForeignKeyConstraint fk = (ForeignKeyConstraint)constraint;
              ForeignKeyConstraint existing = (ForeignKeyConstraint)tableConstraint;
              if (!Objects.equals(fk.onDelete(), existing.onDelete())
                  || !Objects.equals(fk.onUpdate(), existing.onUpdate())) {

                new AlterTable(context, tableName,
                               singletonList(new DropConstraint(context, tableConstraint.name()))).execute(db, con);
                new AlterTable(context, tableName,
                               singletonList(new AddTableDefinition(context, constraint))).execute(db, con);
              }
            }
          }
        }

        // drop existing definitions (columns, constraints and metadata) from the table
        // which have not been defined in the current create statement if 'drop undefined'
        // clause was explicitly set in command.
        if (dropUndefined()) {
          /*
           * drop excess fields
           */
          for (Column column: new ArrayList<>(table.columns())) {
            String columnName = column.alias();
            if (columnName != null
                && columnName.indexOf('/') == -1
                && !columns.containsKey(columnName)) {
              /*
               * Drop existing field not specified in create command
               */
              alter = new AlterTable(context, tableName,
                                     singletonList(new DropColumn(context, columnName)));
              alter.parent = this;
              alter.execute(db, con);
            }
          }

          /*
           * drop excess constraints
           */
          List<String> toDrop = new ArrayList<>();
          for (ConstraintDefinition tableConstraint: new ArrayList<>(table.constraints())) {
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
            new AlterTable(context, tableName, singletonList(new DropConstraint(context, c))).execute(db, con);
          }

          /*
           * Drop table metadata if no metadata specified in create
           */
          if (metadata() == null
              || metadata().attributes() == null
              || metadata().attributes().isEmpty()) {
            alter = new AlterTable(context, tableName, singletonList(new DropMetadata(context)));
            alter.parent = this;
            alter.execute(db, con);
          }
        }
      }
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
    return child("columns").children();
  }

  public void constraints(List<ConstraintDefinition> constraints) {
    child("constraints", new Esql<>(context, "constraints", constraints));
  }

  public List<ConstraintDefinition> constraints() {
    return child("constraints").children();
  }

  public Metadata metadata() {
    return child("metadata");
  }

  private static final System.Logger log = System.getLogger(CreateTable.class.getName());
}
