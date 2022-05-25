/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.define.struct;

import ma.vi.base.tuple.T2;
import ma.vi.esql.database.Database;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.database.Structure;
import ma.vi.esql.exec.Result;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.semantic.type.Column;
import ma.vi.esql.semantic.type.Relation;
import ma.vi.esql.semantic.type.Struct;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.define.Attribute;
import ma.vi.esql.syntax.define.Define;
import ma.vi.esql.syntax.define.Metadata;
import ma.vi.esql.syntax.define.table.*;
import ma.vi.esql.syntax.expression.ColumnRef;
import org.pcollections.PMap;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static ma.vi.esql.semantic.type.Type.dbTableName;
import static ma.vi.esql.translation.Translatable.Target.ESQL;
import static ma.vi.esql.translation.Translatable.Target.SQLSERVER;

/**
 * Alter struct statement.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class AlterStruct extends Define {
  public AlterStruct(Context context, String name, List<Alteration> alterations) {
    super(context, "AlterStruct",
          T2.of("name", new Esql<>(context, name)),
          T2.of("alterations", new Esql<>(context, "alterations", alterations)));
  }

  public AlterStruct(AlterStruct other) {
    super(other);
  }

  @SafeVarargs
  public AlterStruct(AlterStruct other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public AlterStruct copy() {
    return new AlterStruct(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public AlterStruct copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new AlterStruct(this, value, children);
  }

  @Override
  protected String trans(Target               target,
                         EsqlConnection       esqlCon,
                         EsqlPath             path,
                         PMap<String, Object> parameters,
                         Environment          env) {
    StringBuilder st = new StringBuilder("alter ");
    st.append(target == ESQL ? "struct " : "type ");
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
    String name = name();
    Structure s = context.structure;
    Struct struct = s.struct(name);
    if (struct == null) {
      throw new IllegalArgumentException("Struct " + name() + " not found");
    }
    for (Alteration alteration: alterations()) {
      if (alteration instanceof RenameTable rename) {
        /*
         * Rename struct.
         */
        s.remove(struct);
        struct.name(rename.toName());
        s.struct(struct);
        s.database.renameTable(esqlCon, struct.id(), rename.toName());

      } else if (alteration instanceof AddTableDefinition addTable) {
        /*
         * Add struct definitions (columns and metadata)
         */
        TableDefinition definition = addTable.definition();
        if (definition instanceof ColumnDefinition column) {
          /*
           * Add a column to the struct.
           */
          Column col = Column.fromDefinition(column, path);
          s.database.column(esqlCon, struct.id(), col);
          struct.addColumn(col);

        } else if (definition instanceof Metadata metadata) {
          /*
           * Add struct metadata, removing previous ones
           */
          s.database.clearTableMetadata(esqlCon, struct.id());
          s.database.tableMetadata(esqlCon, struct.id(), metadata);

          struct.clearAttributes();
          struct.attributes(metadata.attributes().values());
        }
      } else if (alteration instanceof AlterColumn alterCol) {
        T2<Relation, Column> c = struct.column(ColumnRef.of(null, alterCol.columnName()));
        if (c == null) {
          throw new IllegalArgumentException("Relation " + name() + " does not contain column " + alterCol.columnName());
        }
        Column column = c.b();
        AlterColumnDefinition def = alterCol.definition();
        if (def.toName() != null) {
          /*
           * change column name.
           */
          s.database.columnName(esqlCon, column.id(), def.toName());
          struct.removeColumn(alterCol.columnName());
          column = column.name(def.toName());
          struct.addColumn(column);
        }
        if (def.toType() != null) {
          /*
           * change column type.
           */
          s.database.columnType(esqlCon, column.id(), def.toType().translate(ESQL, esqlCon, path.add(def), env));
          column.type(def.toType());
          struct.addOrReplaceColumn(column);
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
            s.database.defaultValue(esqlCon, column.id(), null);
            struct.addOrReplaceColumn(column.defaultExpression(null));
          }
        }
        if (def.setDefault() != null) {
          /*
           * set default on column.
           */
          if (!column.derived()) {
            s.database.defaultValue(esqlCon, column.id(), def.setDefault().translate(ESQL, esqlCon, path.add(def.setDefault()), env).toString());
            struct.addOrReplaceColumn(column.defaultExpression(def.setDefault()));
          }
        }
        if (def.setNotNull()) {
          /*
           * Modify column so that it cannot take nulls.
           */
          if (!column.derived()) {
            s.database.notNull(esqlCon, column.id(), (db.target() == SQLSERVER ? "1" : "true"));
            struct.addOrReplaceColumn(column.notNull(true));
          }
        }
        if (def.dropNotNull()) {
          /*
           * Modify column so that it can take nulls.
           */
          if (!column.derived()) {
            s.database.notNull(esqlCon, column.id(), db.target() == SQLSERVER ? "0" : "false");
            struct.addOrReplaceColumn(column.notNull(false));
          }
        }

        if (def.metadata() != null
         && def.metadata().attributes() != null
         && !def.metadata().attributes().isEmpty()) {
          /*
           * Add column metadata, removing previous ones.
           */
          s.database.columnMetadata(esqlCon, column.id(), def.metadata());
          Map<String, Attribute> attrs = new LinkedHashMap<>(column.metadata().attributes());
          attrs.putAll(def.metadata().attributes());
          Metadata metadata = new Metadata(def.context, new ArrayList<>(attrs.values()));
          struct.addOrReplaceColumn(column.set("metadata", metadata));
        }
      } else if (alteration instanceof DropColumn drop) {
        /*
         * Drop a column.
         */
        T2<Relation, Column> col = struct.column(ColumnRef.of(null, drop.columnName()));
        Column column = col.b();
        if (column == null) {
          throw new IllegalArgumentException("Struct " + name() + " does not contain column " + drop.columnName());
        }
        s.database.dropColumn(esqlCon, column.id());
        struct.removeColumn(drop.columnName());

      } else if (alteration instanceof DropMetadata) {
        /*
         * Drop all metadata for the struct.
         */
        s.database.clearTableMetadata(esqlCon, struct.id());
        struct.clearAttributes();
      }
    }
    return Result.Nothing;
  }

  public String name() {
    return childValue("name");
  }

  public List<Alteration> alterations() {
    return child("alterations").children();
  }
}
