/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.define.table;

import ma.vi.base.collections.Sets;
import ma.vi.base.tuple.T2;
import ma.vi.esql.builder.Attr;
import ma.vi.esql.builder.CreateTableBuilder;
import ma.vi.esql.database.Database;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.ExecutionException;
import ma.vi.esql.exec.Result;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.semantic.type.BaseRelation;
import ma.vi.esql.semantic.type.Column;
import ma.vi.esql.semantic.type.Relation;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.syntax.CircularReferenceException;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.define.Attribute;
import ma.vi.esql.syntax.define.Create;
import ma.vi.esql.syntax.define.Define;
import ma.vi.esql.syntax.define.Metadata;
import ma.vi.esql.syntax.define.index.CreateIndex;
import ma.vi.esql.syntax.expression.*;
import ma.vi.esql.syntax.expression.literal.BooleanLiteral;
import ma.vi.esql.syntax.expression.literal.Literal;
import ma.vi.esql.syntax.query.Select;
import ma.vi.esql.translation.TranslationException;
import org.pcollections.PMap;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.function.Function;

import static java.lang.System.Logger.Level.ERROR;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.*;
import static ma.vi.esql.builder.Attributes.*;
import static ma.vi.esql.database.Database.StructureChange.TABLE_CREATED;
import static ma.vi.esql.semantic.type.Type.dbTableName;
import static ma.vi.esql.translation.Translatable.Target.*;

/**
 * Create table statement.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class CreateTable extends Define implements Create {
  public CreateTable(Context                    context,
                     String                     name,
                     boolean                    dropUndefined,
                     List<ColumnDefinition>     columns,
                     List<ConstraintDefinition> constraints,
                     Metadata                   metadata) {
    super(context, "CreateTable",
          T2.of("name",          new Esql<>(context, name)),
          T2.of("dropUndefined", new Esql<>(context, dropUndefined)),
          T2.of("columns",       new Esql<>(context, "columns", expand(columns))),
          T2.of("constraints",   new Esql<>(context, "constraints", constraints)),
          T2.of("metadata",      metadata));
  }

  public CreateTable(CreateTable other) {
    super(other);
  }

  @SafeVarargs
  public CreateTable(CreateTable other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public CreateTable copy() {
    return new CreateTable(this);
  }

  /**
   * This constructs a CreateTable node to carry only the column information which
   * is used to find the type of derived columns in the statement. The type inference
   * in ColumnDef looks for the columns of CreateTable to infer the type of derived
   * columns and this is done in the super-constructor call of this object (from
   * the static {@link #expand(List)} method). Since the CreateTable is not available
   * at that point, a temporary proxy for it containing only the columns is created
   * and used for the type inference purpose.
   *
   * @param columns The columns of the Create Table statement.
   */
  private CreateTable(List<ColumnDefinition> columns) {
    super((Context)null,
          "Temp CreateTable For Type Inference Of Derived Cols",
          T2.of("columns", new Esql<>(null, "columns", columns)));
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public CreateTable copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new CreateTable(this, value, children);
  }

  public Map<String, ColumnDefinition> columnsByName() {
    return colsByName(columns());
  }

  private static Map<String, ColumnDefinition> colsByName(List<ColumnDefinition> cols) {
    return cols.stream().collect(toMap(ColumnDefinition::name,
                                       Function.identity(),
                                       (c1, c2) -> c1,
                                       LinkedHashMap::new));
  }

  public static List<ColumnDefinition> expand(List<ColumnDefinition> cols) {
    Map<String, ColumnDefinition> columnsMap = colsByName(cols);
    boolean hasDerived = false;
    List<ColumnDefinition> columns = new ArrayList<>();
    for (ColumnDefinition column: cols) {
      hasDerived |= column instanceof DerivedColumnDefinition;

      /*
       * Expand expressions of derived columns until they consist only of
       * base columns. E.g., {a:a, b:a+5, c:a+b, d:c+b}
       *      is expanded to {a:a, b:a+5, c:a+(a+5), d:(a+(a+5))+(a+5)}
       */
      if (column.expression() != null
      && !(column.expression() instanceof Literal)) {
        column = column.expression(expandDerived(column.expression(),
                                                 columnsMap,
                                                 column.name(),
                                                 new HashSet<>()));
      }
      if (column.metadata() != null
      && !column.metadata().attributes().isEmpty()) {
        List<Attribute> attrs = new ArrayList<>();
        for (Attribute attr: column.metadata().attributes().values()) {
          attrs.add(attr.attributeValue(expandDerived(attr.attributeValue(),
                                                      columnsMap,
                                                      column.name() + '/' + attr.name(),
                                                      new HashSet<>())));
        }
        column = column.metadata(new Metadata(column.context, attrs));
      }
      columns.add(column);
    }

    if (hasDerived) {
      List<ColumnDefinition> typedCols = new ArrayList<>();
      EsqlPath typeProxy = new EsqlPath(new CreateTable(columns));
      for (ColumnDefinition column: columns) {
        if (column instanceof DerivedColumnDefinition derived) {
          if (derived.type() == null && !(derived.expression() instanceof SelectExpression)) {
            Type type = derived.expression().computeType(typeProxy.add(derived.expression()));
            typedCols.add(derived.type(type));
          } else {
            typedCols.add(derived);
          }
        } else {
          typedCols.add(column);
        }
      }
      return typedCols;
    } else {
      return columns;
    }
  }

  private static Expression<?, String> expandDerived(Expression<?, ?> derivedExpression,
                                                     Map<String, ColumnDefinition> columns,
                                                     String columnName,
                                                     Set<String> seen) {
    try {
      seen.add(columnName);
      return (Expression<?, String>)derivedExpression.map((e, path) -> {
        if (e instanceof ColumnRef ref
         && !path.hasAncestor(UncomputedExpression.class)) {
          String colName = ref.columnName();
          ColumnDefinition column = columns.get(colName);
          if (column == null) {
            throw new TranslationException(derivedExpression, "Unknown column " + colName
                                         + " in derived expression " + derivedExpression);
          } else if (seen.contains(colName)) {
            Set<String> otherColumns = seen.stream()
                                           .filter(c -> !c.equals(colName) && !c.contains("/"))
                                           .collect(toSet());
            throw new CircularReferenceException(
                "A circular definition was detected in the expression "
              + derivedExpression + " consisting of the column "
              + colName + (otherColumns.isEmpty() ? "" : " and " + otherColumns));
          } else if (column instanceof DerivedColumnDefinition) {
            return new GroupedExpression(e.context, expandDerived(column.expression(), columns, colName, seen));
          }
        }
        return e;
      },
      e -> !(e instanceof Select));
    } finally {
      seen.remove(columnName);
    }
  }

  @Override
  protected String trans(Target target, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env) {
    StringBuilder st = new StringBuilder("create table ");
    if (target != SQLSERVER) {
      st.append("if not exists ");
    }
    st.append(dbTableName(name(), target)).append('(');

    boolean first = true;
    for (ColumnDefinition column: columns()) {
      String def = column.translate(target, esqlCon, path.add(column), env);
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
      if (!(constraint instanceof ForeignKeyConstraint)
       || !((ForeignKeyConstraint)constraint).ignore()) {
        st.append(", ").append(constraint.translate(target, esqlCon, path.add(constraint), parameters, env));
      }
    }
    st.append(')');
    return st.toString();
  }

  @Override
  protected Object postTransformExec(Target               target,
                                     EsqlConnection       esqlCon,
                                     EsqlPath             path,
                                     PMap<String, Object> parameters,
                                     Environment          env) {
    try {
      Database db = esqlCon.database();
      Connection con = esqlCon.connection();

      /*
       * Create schema if it does not exist already.
       */
      String tableName = name();
      T2<String, String> splitName = Type.splitName(tableName);
      String schema = splitName.a;
      db.createSchema(con, schema);

      /*
       * Create or alter table:  update structure if this was not a system table
       * creation and the table did not exist before.
       */
      String tableDisplayName = metadata().evaluateAttribute(NAME, esqlCon, path, parameters, env);
      String tableDescription = metadata().evaluateAttribute(DESCRIPTION, esqlCon, path, parameters, env);

      if (!db.structure().relationExists(tableName)) {
        if (db.structure().structExists(tableName)) {
          throw new ExecutionException("A struct named " + tableName + " exists; a "
                                     + "table with the same name cannot be created");
        }
        CreateTable modified = this;
        List<ConstraintDefinition> constraints = constraints();
        if (db.target() == MARIADB || db.target() == MYSQL) {
          /*
           * For some reasons, mariadb and mysql cannot create a foreign key from
           * a column (relation_id) that is also part of a composite unique key.
           * As a workaround, we simply drop such unique constraints.
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
            modified = constraints(constraints);
          }
        }

        /*
         * Get table name and description from table attributes if specified.
         * Errors in the table structure are detected when creating the BaseRelation
         * instance and will prevent the creation to go through if the structure
         * is not valid.
         */
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
        con.createStatement().executeUpdate(modified.translate(db.target(), esqlCon, path.add(modified), env));
        if (!tableName.startsWith("_core.")
         && !tableName.endsWith("$history")) {
          addCoarseEventCaptureTriggers(target, con, tableName);
        }
        db.structure().relation(table);
        db.structure().database.addTable(esqlCon, table);

        createHistoryTable(target, tableName, tableDisplayName, esqlCon, path, parameters, env);

        /*
         * Add structure change event for table creation that will be sent to
         * registered subscribers.
         */
        esqlCon.addStructureChange(new Database.StructureChangeEvent(
          TABLE_CREATED, tableName, null, null, null));
      } else {
        /*
         * Already exists: alter
         */
        BaseRelation table = db.structure().relation(tableName);
        AlterTable alter;

        if (metadata() != null
         && metadata().attributes() != null
         && !metadata().attributes().isEmpty()) {
          /*
           * Alter table metadata if specified
           */
          alter = new AlterTable(context,
                                 tableName,
                                 singletonList(new AddTableDefinition(context, metadata())));
          alter.exec(target, esqlCon, path.add(alter), parameters, env);
        }

        /*
         * Add missing columns and alter existing ones if needed.
         */
        Map<String, ColumnDefinition> columns = new HashMap<>();
        int seq = 1;
        for (ColumnDefinition column: columns()) {
          columns.put(column.name(), column);
          T2<Relation, Column> existing = table.findColumn(ColumnRef.of(null, column.name()));
          if (existing == null) {
            /*
             * No existing column with that name: add.
             */
            column.seq = seq;
            alter = new AlterTable(context,
                                   tableName,
                                   singletonList(new AddTableDefinition(context, column)));
            alter.exec(target, esqlCon, path.add(alter), parameters, env);
          } else {
            /*
             * Alter existing column.
             */
            Column existingColumn = existing.b();
            if (existingColumn.derived() != column instanceof DerivedColumnDefinition) {
              /*
               * Derived status of column has changed. Need to drop existing column
               * and recreate.
               */
              alter = new AlterTable(context, tableName, singletonList(new DropColumn(context, column.name())));
              alter.exec(target, esqlCon, path.add(alter), parameters, env);

              alter = new AlterTable(context,
                                     tableName,
                                     singletonList(new AddTableDefinition(context, column)));
              alter.exec(target, esqlCon, path.add(alter), parameters, env);

            } else {
              Type toType = !existingColumn.computeType(path.add(existingColumn)).equals(column.type()) ? column.type() : null;

              boolean setNotNull =  column.notNull() != null
                                &&  column.notNull()
                                && !existingColumn.notNull();

              boolean dropNotNull = (column.notNull() == null || !column.notNull())
                                 && existingColumn.notNull();

              Expression<?, ?> setDefault = null;
              if (!(column instanceof DerivedColumnDefinition)
               &&  column.expression() != null
               && !column.expression().equals(existingColumn.defaultExpression())) {
                setDefault = column.expression();
              }
              boolean dropDefault = !(column instanceof DerivedColumnDefinition)
                                 &&  column.expression() == null
                                 &&  existingColumn.defaultExpression() != null;

              Metadata metadata = column.metadata();
              boolean changedMetadata = (metadata == null && existingColumn.metadata() != null)
                                     || (metadata != null && !metadata.equals(existingColumn.metadata()));

              if (toType != null
               || setNotNull
               || dropNotNull
               || setDefault != null
               || dropDefault
               || changedMetadata) {
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
                alter.exec(target, esqlCon, path.add(alter), parameters, env);
              }
            }
          }
          seq++;
        }

        /*
         * Add missing constraints and alter existing ones if needed.
         */
        for (ConstraintDefinition constraint: constraints()) {
          ConstraintDefinition tableConstraint = table.sameAs(constraint);
          if (tableConstraint == null) {
            /*
             * A constraint same as this one does not exist;: add it
             */
            alter = new AlterTable(context, tableName, singletonList(new AddTableDefinition(context, constraint)));
            alter.exec(target, esqlCon, path.add(alter), parameters, env);
          } else {
            /*
             * There is a similar constraint. For foreign keys check that the cascading
             * parameters have not changed. If so, update the constraint by dropping
             * the existing one and recreating it with the new definition.
             */
            if (constraint instanceof ForeignKeyConstraint fk) {
              ForeignKeyConstraint existing = (ForeignKeyConstraint)tableConstraint;
              if (!Objects.equals(fk.onDelete(), existing.onDelete())
               || !Objects.equals(fk.onUpdate(), existing.onUpdate())
               ||  fk.forwardCost() != existing.forwardCost()
               ||  fk.reverseCost() != existing.reverseCost()) {

                alter = new AlterTable(context, tableName, singletonList(new DropConstraint(context, tableConstraint.name())));
                alter.exec(target, esqlCon, path.add(alter), parameters, env);

                alter = new AlterTable(context, tableName, singletonList(new AddTableDefinition(context, constraint)));
                alter.exec(target, esqlCon, path.add(alter), parameters, env);
              }
            }
          }
        }

        /*
         * Drop existing definitions (columns, constraints and metadata) from the
         * table which have not been defined in the current create statement if
         * 'drop undefined' clause was explicitly set in command.
         */
        if (dropUndefined()) {
          /*
           * Drop excess columns.
           */
          for (T2<Relation, Column> column: new ArrayList<>(table.columns())) {
            String columnName = column.b().name();
            if (columnName != null
                && columnName.indexOf('/') == -1
                && !columns.containsKey(columnName)) {
              /*
               * Drop existing column not specified in create command.
               */
              alter = new AlterTable(context, tableName, singletonList(new DropColumn(context, columnName)));
              alter.exec(target, esqlCon, path.add(alter), parameters, env);
            }
          }

          /*
           * Drop excess constraints.
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
               * No definition found for this constraint: drop.
               */
              toDrop.add(tableConstraint.name());
            }
          }
          for (String c: toDrop) {
            alter = new AlterTable(context, tableName, singletonList(new DropConstraint(context, c)));
            alter.exec(target, esqlCon, path.add(alter), parameters, env);
          }

          /*
           * Drop table metadata if no metadata specified in create.
           */
          if (metadata() == null
           || metadata().attributes() == null
           || metadata().attributes().isEmpty()) {
            alter = new AlterTable(context, tableName, singletonList(new DropMetadata(context)));
            alter.exec(target, esqlCon, path.add(alter), parameters, env);
          }
        }

        /*
         * Alter history tables.
         */
        createHistoryTable(target, tableName, tableDisplayName, esqlCon, path, parameters, env);
      }
      return Result.Nothing;
    } catch (SQLException e) {
      log.log(ERROR, e.getMessage());
      log.log(ERROR, "Original query: " + this);
      throw new RuntimeException(e);
    }
  }

  /**
   * Create history table if history attribute set to true and no history
   * table exists.
   */
  private void createHistoryTable(Target               target,
                                  String               tableName,
                                  String               tableDisplayName,
                                  EsqlConnection       esqlCon,
                                  EsqlPath             path,
                                  PMap<String, Object> parameters,
                                  Environment          env) throws SQLException {
    Boolean history = metadata().evaluateAttribute(HISTORY);
    if (history != null && history) {
//       && !db.structure().relationExists(tableName + "$history")) {
      CreateTableBuilder builder = new CreateTableBuilder(context);
      String historyTable = tableName + "$history";
      builder.name(historyTable);
      builder.metadata(NAME,        "'History of " + (tableDisplayName == null ? Type.unqualifiedName(tableName) : tableDisplayName + "'"));
      builder.metadata(DESCRIPTION, "'History of " + (tableDisplayName == null ? Type.unqualifiedName(tableName) : tableDisplayName + "'"));
      if (metadata().attribute("group") != null) {
        Expression<?, ?> group = metadata().attribute("group").attributeValue();
        builder.metadata("group", group);
      }
      builder.column("history_change_trans_id", "string",   true,  Attr.of(GROUP, "'History'"), Attr.of(READONLY, "true"));
      builder.column("history_change_event",    "string",   true,  Attr.of(GROUP, "'History'"), Attr.of(READONLY, "true"), Attr.of("values", "{I: 'Insert', D: 'Delete', U: 'Update', F: 'Update from', T: 'Update to'}"));
      builder.column("history_change_time",     "datetime", true,  Attr.of(GROUP, "'History'"), Attr.of(READONLY, "true"));
      builder.column("history_change_user",     "string",   false, Attr.of(GROUP, "'History'"), Attr.of(READONLY, "true"));
      for (ColumnDefinition col: columns()) {
        if (col instanceof DerivedColumnDefinition d) {
          builder.derivedColumn(d.name(), d.expression(), d.metadata());
        } else {
          Metadata metadata = col.metadata();
          Map<String, Attribute> attr = metadata == null
                                      ? new HashMap<>()
                                      : new LinkedHashMap<>(metadata.attributes());
          attr.put(READONLY, new Attribute(context, READONLY, new BooleanLiteral(context, true)));
          builder.column(col.name(), col.type(), false, null,
                         new Metadata(context, new ArrayList<>(attr.values())));
        }
      }
      CreateTable create = builder.build();
      create.exec(target, esqlCon, path.add(create), parameters, env);

      /*
       * Index transaction_id column in history table.
       */
      var index = new CreateIndex(context, false, "idx_trans_id_on_" + historyTable, historyTable,
                                  List.of(new ColumnRef(context, null, "history_change_trans_id")));
      index.exec(target, esqlCon, path.add(index), parameters, env);

      index = new CreateIndex(context, false, "idx_user_on_" + historyTable, historyTable,
                                  List.of(new ColumnRef(context, null, "history_change_user")));
      index.exec(target, esqlCon, path.add(index), parameters, env);

      index = new CreateIndex(context, false, "idx_time_on_" + historyTable, historyTable,
                                  List.of(new ColumnRef(context, null, "history_change_time")));
      index.exec(target, esqlCon, path.add(index), parameters, env);

      addFineEventCaptureTriggers(target, esqlCon.connection(), tableName);
    }
  }

  /**
   * Add coarse event-capture triggers which inserts a row into _core.history
   * if any changes is detected on the table, without capturing any of the changed
   * column values. These triggers are added to all tables except the ones in the
   * _core schema and History tables.
   *
   * @param target The target database on which the table is being created. This
   *               is used to create the triggers in the correct SQL dialect.
   * @param con Connection to the database.
   * @param table The table for which the triggers are being created.
   * @throws SQLException On any error when creating the triggers.
   */
  private void addCoarseEventCaptureTriggers(Target     target,
                                             Connection con,
                                             String     table) throws SQLException {
    T2<String, String> split = Type.splitName(table);
    String schema = split.a;
    String tableName = split.b;
    String dbTableName = Type.dbTableName(table, target);

    if (target == SQLSERVER) {
      String insertTrigger = Type.dbTableName((schema == null ? "" : schema + '.') + "capture_coarse_insert_event_on_" + tableName, target);
      String deleteTrigger = Type.dbTableName((schema == null ? "" : schema + '.') + "capture_coarse_delete_event_on_" + tableName, target);
      String updateTrigger = Type.dbTableName((schema == null ? "" : schema + '.') + "capture_coarse_update_event_on_" + tableName, target);
      try(Statement stmt = con.createStatement()) {
        stmt.executeUpdate(
          """
          create or alter trigger %1$s on %2$s after insert as
          begin
            if (rowcount_big() = 0) return;
            insert into _core._temp_history(trans_id, table_name, event, at_time)
                               values(cast(current_transaction_id() as varchar),
                                      '%3$s', 'I', getdate());
          end""".formatted(insertTrigger, dbTableName, table));

        stmt.executeUpdate(
          """
          create or alter trigger %1$s on %2$s after delete as
          begin
            if (rowcount_big() = 0) return;
            insert into _core._temp_history(trans_id, table_name, event, at_time)
                               values(cast(current_transaction_id() as varchar),
                                      '%3$s', 'D', getdate());
          end""".formatted(deleteTrigger, dbTableName, table));

        stmt.executeUpdate(
          """
          create or alter trigger %1$s on %2$s after update as
          begin
            if (rowcount_big() = 0) return;
            insert into _core._temp_history(trans_id, table_name, event, at_time)
                               values(cast(current_transaction_id() as varchar),
                                      '%3$s', 'U', getdate());
          end""".formatted(updateTrigger, dbTableName, table));
      }
    } else if (target == POSTGRESQL) {
      String insertTrigger = "capture_coarse_insert_event_on_" + tableName;
      String deleteTrigger = "capture_coarse_delete_event_on_" + tableName;
      String updateTrigger = "capture_coarse_update_event_on_" + tableName;
      try(Statement stmt = con.createStatement()) {
        stmt.executeUpdate(
          """
          create or replace trigger %1$s
          after       insert on %2$s
          referencing new table as inserted
          for each    statement
          execute     function _core.capture_insert_event()
          """.formatted(insertTrigger, dbTableName));

        stmt.executeUpdate(
          """
          create or replace trigger %1$s
          after       delete on %2$s
          referencing old table as deleted
          for each    statement
          execute     function _core.capture_delete_event()
          """.formatted(deleteTrigger, dbTableName));

        stmt.executeUpdate(
          """
          create or replace trigger %1$s
          after       update on %2$s
          referencing new table as inserted
          for each    statement
          execute     function _core.capture_update_event()
          """.formatted(updateTrigger, dbTableName));
      }
    } else {
      throw new IllegalArgumentException(target + " is not supported yet.");
    }
  }

  private void addFineEventCaptureTriggers(Target     target,
                                           Connection con,
                                           String     table) throws SQLException {
    T2<String, String> split = Type.splitName(table);
    String schema = split.a;
    String tableName = split.b;
    String dbTableName = Type.dbTableName(table, target);
    String dbHistoryTableName = Type.dbTableName(table + "$history", target);

    if (target == SQLSERVER) {
      try(Statement stmt = con.createStatement()) {
        String insertTrigger = Type.dbTableName((schema == null ? "" : schema + '.') + "capture_fine_insert_history_on_" + tableName, target);
        String deleteTrigger = Type.dbTableName((schema == null ? "" : schema + '.') + "capture_fine_delete_history_on_" + tableName, target);
        String updateTrigger = Type.dbTableName((schema == null ? "" : schema + '.') + "capture_fine_update_history_on_" + tableName, target);

        stmt.executeUpdate(
          """
          create or alter trigger %1$s on %2$s after insert as
          begin
            if (rowcount_big() = 0) return;
            insert into %3$s
            select cast(current_transaction_id() as varchar),
                   'I', getdate(), null, *
              from inserted;
          end""".formatted(insertTrigger,
                           dbTableName,
                           dbHistoryTableName));

        stmt.executeUpdate(
          """
          create or alter trigger %1$s on %2$s after delete as
          begin
            if (rowcount_big() = 0) return;
            insert into %3$s
            select cast(current_transaction_id() as varchar),
                   'D', getdate(), null, *
              from deleted;
          end""".formatted(deleteTrigger,
                           dbTableName,
                           dbHistoryTableName));

        stmt.executeUpdate(
          """
          create or alter trigger %1$s on %2$s after update as
          begin
            if (rowcount_big() = 0) return;
            insert into %3$s
            select cast(current_transaction_id() as varchar),
                   'F', getdate(), null, *
              from deleted;
              
            insert into %3$s
            select cast(current_transaction_id() as varchar),
                   'T', getdate(), null, *
              from inserted;
          end""".formatted(updateTrigger,
                           dbTableName,
                           dbHistoryTableName));
      }
    } else if (target == POSTGRESQL) {
      try(Statement stmt = con.createStatement()) {
        String insertTriggerFunc = Type.dbTableName((schema == null ? "" : schema + '.') + "capture_fine_insert_history_func_on_" + tableName, target);
        String deleteTriggerFunc = Type.dbTableName((schema == null ? "" : schema + '.') + "capture_fine_delete_history_func_on_" + tableName, target);
        String updateTriggerFunc = Type.dbTableName((schema == null ? "" : schema + '.') + "capture_fine_update_history_func_on_" + tableName, target);

        /*
        * fine-grain event capture trigger functions
        */
       stmt.executeUpdate("""
         create or replace function %1$s() returns trigger as $$
         begin
           if exists(select 1 from inserted) then
             insert into %2$s
             select pg_current_xact_id()::text,
                    'I', now(), null, *
               from inserted;
           end if;
           return null;
         end;
         $$ language plpgsql""".formatted(insertTriggerFunc,
                                          dbHistoryTableName));

       stmt.executeUpdate("""
         create or replace function %1$s() returns trigger as $$
         begin
           if exists(select 1 from deleted) then
             insert into %2$s
             select pg_current_xact_id()::text,
                    'D', now(), null, *
               from deleted;
           end if;
           return null;
         end;
         $$ language plpgsql""".formatted(deleteTriggerFunc,
                                          dbHistoryTableName));

       stmt.executeUpdate("""
         create or replace function %1$s() returns trigger as $$
         begin
           if exists(select 1 from inserted) then
             insert into %2$s
             select pg_current_xact_id()::text,
                    'F', now(), null, *
               from deleted;
               
             insert into %2$s
             select pg_current_xact_id()::text,
                    'T', now(), null, *
               from inserted;
           end if;
           return null;
         end;
         $$ language plpgsql""".formatted(updateTriggerFunc,
                                          dbHistoryTableName));

        String insertTriggerName = "capture_fine_insert_event_on_" + tableName;
        String deleteTriggerName = "capture_fine_delete_event_on_" + tableName;
        String updateTriggerName = "capture_fine_update_event_on_" + tableName;
        stmt.executeUpdate(
          """
          create or replace trigger %1$s
          after       insert on %2$s
          referencing new table as inserted
          for each    statement
          execute     function %3$s()
          """.formatted(insertTriggerName, dbTableName, insertTriggerFunc));

        stmt.executeUpdate(
          """
          create or replace trigger %1$s
          after       delete on %2$s
          referencing old table as deleted
          for each    statement
          execute     function %3$s()
          """.formatted(deleteTriggerName, dbTableName, deleteTriggerFunc));

        stmt.executeUpdate(
          """
          create or replace trigger %1$s
          after       update on %2$s
          referencing old table as deleted
                      new table as inserted
          for each    statement
          execute     function %3$s()
          """.formatted(updateTriggerName, dbTableName, updateTriggerFunc));
      }
    } else {
      throw new IllegalArgumentException(target + " is not supported yet.");
    }
  }

  public String name() {
    return childValue("name");
  }

  public Boolean dropUndefined() {
    return childValue("dropUndefined");
  }

  public List<ColumnDefinition> columns() {
    return child("columns").children();
  }

  public CreateTable constraints(List<ConstraintDefinition> constraints) {
    return set("constraints", new Esql<>(context, "constraints", constraints));
  }

  public List<ConstraintDefinition> constraints() {
    return child("constraints").children();
  }

  public Metadata metadata() {
    return child("metadata");
  }

  private static final System.Logger log = System.getLogger(CreateTable.class.getName());
}
