/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.query;

import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.define.Metadata;
import ma.vi.esql.parser.define.NameWithMetadata;
import ma.vi.esql.parser.expression.ColumnRef;
import ma.vi.esql.parser.expression.Expression;
import ma.vi.esql.parser.modify.InsertRow;
import ma.vi.esql.type.AliasedRelation;
import ma.vi.esql.type.BaseRelation;
import ma.vi.esql.type.Selection;
import ma.vi.esql.type.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static java.util.stream.Collectors.joining;
import static ma.vi.esql.type.Types.NullType;

/**
 * Represents a dynamic table created from a list of rows:
 * <pre>
 *      select t.a, t.b from t(a, b):(('One', 1), ('Two', 2))
 *                           |------------------------------| -&gt; Dynamic table expression
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
      List<Type> columnTypes = new ArrayList<>();
      for (NameWithMetadata ignored: columns()) {
        columnTypes.add(NullType);
      }

      /*
       * Infer column types of the rows by looking at up to 100 rows
       * probabilistically. Using a random sampling of rows ensure that we are
       * not looking at any specific cluster (such as the top 100) and inferring
       * only the type characteristics of that cluster.
       */
      Random random = new Random();
      List<InsertRow> rows = rows();
      for (int i = 0, rowsChecked = 0;
           rowsChecked < Math.min(100, rows.size());
           i = rows.size() <= 100 ? i + 1 : random.nextInt(rows.size()), rowsChecked++) {
        InsertRow row = rows.get(i);
        boolean hasAbstractTypes = false;
        List<Expression<?, String>> values = row.values();
        for (int j = 0; j < values.size(); j++) {
          Type type = values.get(j).type();
          hasAbstractTypes |= columnTypes.get(j).isAbstract() && type.isAbstract();
          if (columnTypes.get(j).isAbstract()) {
            columnTypes.set(j, type);
          }
        }
        if (!hasAbstractTypes) {
          break;
        }
      }

      List<NameWithMetadata> columns = columns();
      List<Column> relationColumns = new ArrayList<>();
      for (int i = 0; i < columnTypes.size(); i++) {
        NameWithMetadata column = columns.get(i);
        Column col = new Column(context,
                                column.name(),
                                new ColumnRef(context, null, column.name()),
                                column.metadata());
        col.type(columnTypes.get(i));
        relationColumns.add(col);
      }
      Selection selection = new Selection(BaseRelation.expandColumns(
                                            metadata() != null
                                              ? new ArrayList<>(metadata().attributes().values())
                                              : null,
                                            relationColumns),this);
//      if (metadata() != null) {
//        for (Attribute attribute: metadata().attributes().values()) {
//          selection.attribute(attribute.name(), attribute.attributeValue());
//        }
//      }
      type = new AliasedRelation(selection, alias());
    }
    return type;
  }

  @Override
  public String translate(Target target, Map<String, Object> parameters) {
    return "(values "
         + rows().stream()
                 .map(r -> r.translate(target, parameters))
                 .collect(joining(", "))
         + ") as \"" + alias() + '"'
         + "(" + columns().stream()
                          .map(c -> c.translate(target, parameters))
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

  private transient volatile AliasedRelation type;
}