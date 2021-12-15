/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.query;

import ma.vi.base.tuple.T2;
import ma.vi.esql.semantic.type.AliasedRelation;
import ma.vi.esql.semantic.type.BaseRelation;
import ma.vi.esql.semantic.type.Selection;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.define.Metadata;
import ma.vi.esql.syntax.define.NameWithMetadata;
import ma.vi.esql.syntax.expression.ColumnRef;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.modify.InsertRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static java.util.stream.Collectors.joining;
import static ma.vi.esql.semantic.type.Types.NullType;

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
    super(context,
          "DynamicTable",
          alias,
          T2.of("metadata", metadata),
          T2.of("columns", new Esql<>(context, "columns", columns)),
          T2.of("rows", new Esql<>(context, "rows", rows)));
  }

  public DynamicTableExpr(DynamicTableExpr other) {
    super(other);
  }

  public DynamicTableExpr(DynamicTableExpr other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public DynamicTableExpr copy() {
    return new DynamicTableExpr(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public DynamicTableExpr copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new DynamicTableExpr(this, value, children);
  }

  @Override
  public AliasedRelation type(EsqlPath path) {
    if (type == null) {
      List<Type> columnTypes = new ArrayList<>();
      for (NameWithMetadata ignored: columns()) {
        columnTypes.add(NullType);
      }

      /*
       * Infer column types of the rows by looking at up to 100 rows probabilistically.
       * Using a random sampling of rows ensure that we are not looking at any specific
       * cluster (such as the top 100) and inferring only the type characteristics of
       * that cluster.
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
          Type type = values.get(j).type(path.add(values.get(j)));
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
        col = col.type(columnTypes.get(i));
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
      context.type(alias(), type);
    }
    return type;
  }

  @Override
  protected String trans(Target target, EsqlPath path, Map<String, Object> parameters) {
    return "(values "
         + rows().stream()
                 .map(r -> r.translate(target, path.add(r), parameters))
                 .collect(joining(", "))
         + ") as \"" + alias() + '"'
         + "(" + columns().stream()
                          .map(c -> c.translate(target, path.add(c), parameters))
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