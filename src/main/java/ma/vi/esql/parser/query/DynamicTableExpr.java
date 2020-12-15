/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.esql.parser.query;

import ma.vi.esql.parser.Context;
import ma.vi.esql.builder.Attributes;
import ma.vi.esql.parser.define.Attribute;
import ma.vi.esql.parser.define.Metadata;
import ma.vi.esql.parser.define.NameWithMetadata;
import ma.vi.esql.parser.expression.ColumnRef;
import ma.vi.esql.parser.expression.Expression;
import ma.vi.esql.parser.expression.StringLiteral;
import ma.vi.esql.parser.modify.InsertRow;
import ma.vi.esql.type.AliasedRelation;
import ma.vi.esql.type.Selection;
import ma.vi.esql.type.Type;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.util.stream.Collectors.joining;
import static ma.vi.esql.type.Types.NullType;

/**
 * Represents a dynamic table created from as list of rows in a
 * values clause:
 * <pre>
 *      select t.a, t.b from t(a, b):(values ('One', 1), ('Two', 2))
 *                          |---------------------------------------| -&gt; Dynamic table expression
 * </pre>
 *
 * @author vikash.madhow@gmail.com
 */
public class DynamicTableExpr extends AbstractAliasTableExpr {
  public DynamicTableExpr(Context context,
                          String alias,
                          Metadata metadata,
                          List<NameWithMetadata> columns,
                          List<InsertRow> rows) {
    super(context, "DynamicTable", alias);
    childValue("metadata", metadata);
    childValue("columns", columns);
    childValue("rows", rows);
  }

  public DynamicTableExpr(DynamicTableExpr other) {
    super(other);
  }

  @Override
  public DynamicTableExpr copy() {
    if (!copying()) {
      try {
        copying(true);
        return new DynamicTableExpr(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public AliasedRelation type() {
    if (type == null) {
      /*
       * Infer the field type of the row by looking at up to 100 rows.
       */
      List<Type> fieldTypes = new ArrayList<>();
      for (NameWithMetadata ignored: columns()) {
        fieldTypes.add(NullType);
      }

      int rows = 0;
      for (Iterator<InsertRow> i = rows().iterator(); i.hasNext() && rows < 100; ) {
        InsertRow row = i.next();
        List<Expression<?>> values = row.values();
        for (int j = 0; j < values.size(); j++) {
          Type type = values.get(j).type();
          if (fieldTypes.get(j).isAbstract()) {
            fieldTypes.set(j, type);
          }
        }
        rows += 1;
      }

      List<NameWithMetadata> columns = columns();
      List<Column> relationColumns = new ArrayList<>();
      for (int i = 0; i < fieldTypes.size(); i++) {
        NameWithMetadata column = columns.get(i);
        Metadata metadata = column.metadata();
        if (metadata == null) {
          metadata = new Metadata(context, new ArrayList<>());
        }
        if (metadata.attributes() == null) {
          metadata.attributes(new ArrayList<>());
        }

        metadata.attribute(Attributes.TYPE,
                           new StringLiteral(context, fieldTypes.get(i).name()));
        relationColumns.add(new Column(
            context,
            column.name(),
            new ColumnRef(context, null, column.name()),
            metadata
        ));
      }
      Selection selection = new Selection(relationColumns, this);
      if (metadata() != null) {
        for (Attribute attribute: metadata().attributes().values()) {
          selection.attribute(attribute.name(), attribute.attributeValue());
        }
      }
      type = new AliasedRelation(selection, alias());
    }
    return type;
  }

  @Override
  public String translate(Target target) {
    return "(values "
         + rows().stream()
                 .map(r -> r.translate(target))
                 .collect(joining(", "))
         + ") as " + alias()
         + "(" + columns().stream()
                          .map(c -> c.translate(target))
                          .collect(joining(", "))
         + ")";
  }

  @Override
  public String toString() {
    return alias()
        + ":(" + columns().stream()
                          .map(NameWithMetadata::name)
                          .collect(joining(", "))
        + ")";
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    st.append(alias()).append(":(")
      .append(columns().stream()
                       .map(NameWithMetadata::name)
                       .collect(joining(", ")))
      .append(")");
  }

  public List<InsertRow> rows() {
    return childValue("rows");
  }

  public String alias() {
    return childValue("alias");
  }

  public List<NameWithMetadata> columns() {
    return childValue("columns");
  }

  public Metadata metadata() {
    return childValue("metadata");
  }

  private volatile AliasedRelation type;
}