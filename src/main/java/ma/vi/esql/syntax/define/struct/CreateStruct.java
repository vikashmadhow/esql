/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.define.struct;

import ma.vi.base.tuple.T2;
import ma.vi.esql.database.Database;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.ExecutionException;
import ma.vi.esql.exec.Result;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.semantic.type.Column;
import ma.vi.esql.semantic.type.Relation;
import ma.vi.esql.semantic.type.Struct;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.define.Define;
import ma.vi.esql.syntax.define.Metadata;
import ma.vi.esql.syntax.define.table.*;
import ma.vi.esql.syntax.expression.ColumnRef;
import ma.vi.esql.syntax.expression.Expression;
import org.pcollections.PMap;

import java.util.*;
import java.util.function.Function;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static ma.vi.esql.builder.Attributes.DESCRIPTION;
import static ma.vi.esql.builder.Attributes.NAME;
import static ma.vi.esql.semantic.type.Type.dbTableName;
import static ma.vi.esql.translation.Translatable.Target.ESQL;
import static ma.vi.esql.translation.Translatable.Target.SQLSERVER;

/**
 * Create struct statement.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class CreateStruct extends Define {
  public CreateStruct(Context                context,
                      String                 name,
                      List<ColumnDefinition> columns,
                      Metadata               metadata) {
    super(context, "CreateStruct",
          T2.of("name",     new Esql<>(context, name)),
          T2.of("columns",  new Esql<>(context, "columns", CreateTable.expand(columns))),
          T2.of("metadata", metadata));
  }

  public CreateStruct(CreateStruct other) {
    super(other);
  }

  @SafeVarargs
  public CreateStruct(CreateStruct other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public CreateStruct copy() {
    return new CreateStruct(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public CreateStruct copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new CreateStruct(this, value, children);
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

  @Override
  protected String trans(Target               target,
                         EsqlConnection       esqlCon,
                         EsqlPath             path,
                         PMap<String, Object> parameters,
                         Environment          env) {
    StringBuilder st = new StringBuilder("create");
    st.append(target == ESQL ? " struct" : " type");
    st.append(dbTableName(name(), target));
    if (target != ESQL) {
      st.append(" as");
    }
    if (target == SQLSERVER) {
      st.append(" table");
    }
    st.append("(\n");

    if (target == ESQL && metadata() != null) {
      st.append(metadata().translate(target, esqlCon, path.add(metadata()), parameters, env))
        .append('\n');
    }

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
    st.append(')');
    return st.toString();
  }

  @Override
  protected Object postTransformExec(Target               target,
                                     EsqlConnection       esqlCon,
                                     EsqlPath             path,
                                     PMap<String, Object> parameters,
                                     Environment          env) {
    Database db = esqlCon.database();
    String structName = name();
    if (!db.structure().structExists(structName)) {
      if (db.structure().relationExists(structName)) {
        throw new ExecutionException("A table named " + structName + " exists; a "
                                   + "struct with the same name cannot be created");
      }

      /*
       * Get struct name and description from attributes if specified, and create
       * struct in core tables.
       */
      String structDisplayName = metadata().evaluateAttribute(NAME, esqlCon, path, parameters, env);
      String structDescription = metadata().evaluateAttribute(DESCRIPTION, esqlCon, path, parameters, env);
      Struct struct = new Struct(context,
                                 UUID.randomUUID(),
                                 structName,
                                 structDisplayName,
                                 structDescription,
                                 new ArrayList<>(metadata().attributes().values()),
                                 columns().stream()
                                          .map(Column::fromDefinition)
                                          .collect(toList()));
      db.structure().struct(struct);
      db.structure().database.addTable(esqlCon, struct);

    } else {
      /*
       * Already exists: alter
       */
      Struct struct = db.structure().struct(structName);
      AlterStruct alter;

      if (metadata() != null
       && metadata().attributes() != null
       && !metadata().attributes().isEmpty()) {
        /*
         * alter struct metadata if specified
         */
        alter = new AlterStruct(context,
                                structName,
                                singletonList(new AddTableDefinition(context, metadata())));
        alter.exec(target, esqlCon, path.add(alter), parameters, env);
      }

      /*
       * Add missing columns and alter existing ones if needed.
       */
      int seq = 1;
      Map<String, ColumnDefinition> columns = new HashMap<>();
      for (ColumnDefinition column: columns()) {
        columns.put(column.name(), column);
        T2<Relation, Column> existing = struct.findColumn(ColumnRef.of(null, column.name()));
        if (existing == null) {
          /*
           * No existing column with that name: add
           */
          column.seq = seq;
          alter = new AlterStruct(context,
                                  structName,
                                  singletonList(new AddTableDefinition(context, column)));
          alter.exec(target, esqlCon, path.add(alter), parameters, env);
        } else {
          /*
           * Alter existing column.
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
            alter = new AlterStruct(context, structName,
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
        seq++;
      }

      /*
       * Drop excess columns.
       */
      for (T2<Relation, Column> column: new ArrayList<>(struct.columns())) {
        String columnName = column.b().name();
        if (columnName != null
         && columnName.indexOf('/') == -1
         && !columns.containsKey(columnName)) {
          /*
           * Drop existing column not specified in create command
           */
          alter = new AlterStruct(context, structName, singletonList(new DropColumn(context, columnName)));
          alter.exec(target, esqlCon, path.add(alter), parameters, env);
        }
      }

      /*
       * Drop table metadata if no metadata specified in create
       */
      if (metadata() == null
       || metadata().attributes() == null
       || metadata().attributes().isEmpty()) {
        alter = new AlterStruct(context, structName, singletonList(new DropMetadata(context)));
        alter.exec(target, esqlCon, path.add(alter), parameters, env);
      }
    }
    return Result.Nothing;
  }

  public String name() {
    return childValue("name");
  }

  public List<ColumnDefinition> columns() {
    return child("columns").children();
  }

  public Metadata metadata() {
    return child("metadata");
  }
}
