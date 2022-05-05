/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.define;

import ma.vi.base.collections.Sets;
import ma.vi.base.tuple.T2;
import ma.vi.esql.database.Database;
import ma.vi.esql.database.EsqlConnection;
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
import ma.vi.esql.syntax.expression.ColumnRef;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.expression.GroupedExpression;
import ma.vi.esql.syntax.expression.SelectExpression;
import ma.vi.esql.syntax.expression.literal.Literal;
import ma.vi.esql.syntax.query.Select;
import ma.vi.esql.translation.TranslationException;
import org.pcollections.PMap;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;

import static java.lang.System.Logger.Level.ERROR;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.*;
import static ma.vi.esql.builder.Attributes.DESCRIPTION;
import static ma.vi.esql.builder.Attributes.NAME;
import static ma.vi.esql.semantic.type.Type.dbTableName;
import static ma.vi.esql.translation.Translatable.Target.*;

/**
 * Create table statement.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class CreateTable extends Define {
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

  private static List<ColumnDefinition> expand(List<ColumnDefinition> cols) {
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
        if (e instanceof ColumnRef ref) {
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

      /*
       * Create or alter table:  update structure if this was not a system table
       * creation and the table did not exist before.
       */
      if (!db.structure().relationExists(tableName)) {
        CreateTable modified = this;
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
            modified = constraints(constraints);
          }
        }

        /*
         * Get table name and description from table attributes if specified.
         * Errors in the table structure are detected when creating the BaseRelation
         * instance and will prevent the creation to go through if the structure
         * is not valid.
         */
        String tableDisplayName = metadata().evaluateAttribute(NAME, esqlCon, path, parameters, env);
        String tableDescription = metadata().evaluateAttribute(DESCRIPTION, esqlCon, path, parameters, env);
        BaseRelation table = new BaseRelation(context,
                                              UUID.randomUUID(),
                                              tableName,
                                              tableDisplayName,
                                              tableDescription,
                                              new ArrayList<>(metadata().attributes().values()),
                                              columns().stream()
                                                       .map(c -> Column.fromDefinition(c, path))
                                                       .collect(toList()),
                                              constraints);
        /*
         * Does not exist and valid: create
         */
        con.createStatement().executeUpdate(modified.translate(db.target(), esqlCon, path.add(modified), env));
        db.structure().relation(table);
//        table.expandColumns();
        db.structure().database.addTable(esqlCon, table);
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
          alter.exec(target, esqlCon, path.add(alter), parameters, env);
        }

        // add missing columns and alter existing ones if needed
        Map<String, ColumnDefinition> columns = new HashMap<>();
        for (ColumnDefinition column: columns()) {
          columns.put(column.name(), column);
          T2<Relation, Column> existing = table.findColumn(ColumnRef.of(null, column.name()));
          if (existing == null) {
            /*
             * No existing field with that name: add
             */
            alter = new AlterTable(context,
                                   tableName,
                                   singletonList(new AddTableDefinition(context, column)));
            alter.exec(target, esqlCon, path.add(alter), parameters, env);
          } else {
            /*
             * alter existing field
             */
            Column existingColumn = existing.b();
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

        // add missing constraints and alter existing ones if needed
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
                  || !Objects.equals(fk.onUpdate(), existing.onUpdate())) {

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
           * drop excess fields
           */
          for (T2<Relation, Column> column: new ArrayList<>(table.columns())) {
            String columnName = column.b().name();
            if (columnName != null
                && columnName.indexOf('/') == -1
                && !columns.containsKey(columnName)) {
              /*
               * Drop existing field not specified in create command
               */
              alter = new AlterTable(context, tableName, singletonList(new DropColumn(context, columnName)));
              alter.exec(target, esqlCon, path.add(alter), parameters, env);
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
            alter = new AlterTable(context, tableName, singletonList(new DropConstraint(context, c)));
            alter.exec(target, esqlCon, path.add(alter), parameters, env);
          }

          /*
           * Drop table metadata if no metadata specified in create
           */
          if (metadata() == null
           || metadata().attributes() == null
           || metadata().attributes().isEmpty()) {
            alter = new AlterTable(context, tableName, singletonList(new DropMetadata(context)));
            alter.exec(target, esqlCon, path.add(alter), parameters, env);
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
