/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.query;

import ma.vi.base.tuple.T2;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.composable.Composable;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.semantic.type.BaseRelation;
import ma.vi.esql.semantic.type.Column;
import ma.vi.esql.semantic.type.Selection;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.define.Metadata;
import ma.vi.esql.syntax.define.NameWithMetadata;
import ma.vi.esql.syntax.expression.ColumnRef;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.macro.Macro;
import ma.vi.esql.syntax.modify.InsertRow;
import org.pcollections.PMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;
import static ma.vi.esql.semantic.type.Types.UnknownType;

/**
 * Represents a dynamic table created from a list of rows:
 * <pre>
 *      select t.a, t.b from t{name: 't',
 *                             description: 'Dynamic'}(a, b):(('One', 1),
 *                                                            ('Two', 2))
 *                           |------------------------------------------| -&gt; Dynamic table expression
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
    super(context, "DynamicTable",
          alias,
          T2.of("metadata", metadata),
          T2.of("columns",  new Esql<>(context, "columns", columns, true)),
          T2.of("rows",     new Esql<>(context, "rows", rows, true)));
  }

  public DynamicTableExpr(DynamicTableExpr other) {
    super(other);
  }

  @SafeVarargs
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
  public ShortestPath findShortestPath(Composable composable) {
    return null;
  }

  @Override
  public AppliedShortestPath applyShortestPath(ShortestPath shortest, TableExpr root) {
    return null;
  }

  @Override
  public boolean exists(EsqlPath path) {
    return true;
  }

  @Override
  public TableExpr aliased(String name) {
    return name == null || name.equals(alias()) ? this : null;
  }

  @Override
  public TableExpr table(String name) {
    return null;
  }

  @Override
  public ColumnRef findColumn(String table, String name) {
    return null;
  }

  @Override
  public List<Column> columnList(EsqlPath path) {
    List<Type> columnTypes = guessColumnTypes(path);
    List<NameWithMetadata> columns = columns();
    List<Column> relationColumns = new ArrayList<>();
    for (int i = 0; i < columnTypes.size(); i++) {
      NameWithMetadata column = columns.get(i);
      Column col = new Column(context,
                              column.name(),
                              new ColumnRef(context, alias(), column.name()),
                              columnTypes.get(i),
                              ColumnRef.qualify(column.metadata(), alias()));
      relationColumns.add(col);
    }
    return BaseRelation.expandColumns(
              metadata() == null
           || metadata().attributes() == null
           || metadata().attributes().isEmpty() ? emptyList()
                                                : new ArrayList<>(metadata().attributes().values()),
              relationColumns,
              emptyList());
  }

  private List<Type> guessColumnTypes(EsqlPath path) {
    if (columnTypes == null) {
      columnTypes = new ArrayList<>();
      for (NameWithMetadata ignored : columns()) {
        columnTypes.add(UnknownType);
      }

      /*
       * Infer column types of the rows by looking at up to 100 rows probabilistically.
       * Using a random sampling of rows ensures that we are not looking at any
       * specific cluster (such as the top 100) and inferring only the type
       * characteristics of that cluster.
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
          Type type = values.get(j).computeType(path.add(values.get(j)));
          hasAbstractTypes |= columnTypes.get(j).isAbstract() && type.isAbstract();
          if (columnTypes.get(j).isAbstract()) {
            columnTypes.set(j, type);
          }
        }
        if (!hasAbstractTypes) {
          break;
        }
      }
    }
    return columnTypes;
  }

  @Override
  public Selection computeType(EsqlPath path) {
    if (type == UnknownType) {
      Selection selection = new Selection(columnList(path),
                                          metadata() != null
                                          ? new ArrayList<>(metadata().attributes().values())
                                          : null, this, alias());
      if (path.hasAncestor(Macro.OngoingMacroExpansion.class)) {
        context.type(alias(), selection);
        return selection;
      } else {
        type = selection;
        context.type(alias(), type);
      }
    }
    return (Selection)type;
  }

  @Override
  protected String trans(Target               target,
                         EsqlConnection       esqlCon,
                         EsqlPath             path,
                         PMap<String, Object> parameters,
                         Environment          env) {
    return "(values "
         + rows().stream()
                 .map(r -> r.translate(target, esqlCon, path.add(r), parameters, env))
                 .collect(joining(", "))
         + ") as \"" + alias() + '"'
         + "(" + columns().stream()
                          .map(c -> c.translate(target, esqlCon, path.add(c), parameters, env))
                          .collect(joining(", "))
         + ")";
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    st.append(alias()).append(":(");
    if (metadata() != null
     && metadata().attributes() != null
     && !metadata().attributes().isEmpty()) {
      metadata()._toString(st, level + 1, indent);
      st.append(", ");
    }
    boolean first = true;
    for (NameWithMetadata col: columns()) {
      if (first) { first = false;   }
      else       { st.append(", "); }
      col._toString(st, level + 1, indent);
    }
    st.append(")");
  }

  public String alias() {
    return childValue("alias");
  }

  public Metadata metadata() {
    return child("metadata");
  }

  public List<NameWithMetadata> columns() {
    return child("columns").children();
  }

  public List<InsertRow> rows() {
    return child("rows").children();
  }

  private transient volatile List<Type> columnTypes;
}