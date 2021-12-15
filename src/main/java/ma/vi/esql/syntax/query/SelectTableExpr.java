/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.query;

import ma.vi.base.tuple.T2;
import ma.vi.esql.semantic.type.AliasedRelation;
import ma.vi.esql.semantic.type.Selection;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.ColumnRef;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents a select in a from clause:
 *
 * <pre>
 *     select * from t:(select x, y from T)
 *                  |----------------------| -&gt; Select table expression
 * </pre>
 *
 * @author vikash.madhow@gmail.com
 */
public class SelectTableExpr extends AbstractAliasTableExpr {
  public SelectTableExpr(Context context, Select select, String alias) {
    super(context,
          "SelectAsTable",
          alias,
          T2.of("select", select));
  }

  public SelectTableExpr(SelectTableExpr other) {
    super(other);
  }

  public SelectTableExpr(SelectTableExpr other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public SelectTableExpr copy() {
     return new SelectTableExpr(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public SelectTableExpr copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new SelectTableExpr(this, value, children);
  }

  @Override
  public AliasedRelation type(EsqlPath path) {
//    if (type == null) {
//      Selection selectType = select().type(path);
//      List<Column> cols = new ArrayList<>();
//      for (Column c: selectType.columns()) {
//        Column col = new Column(c.context, c.alias(), new ColumnRef(c.context, null, c.alias()), null);
//        col.parent = path.ancestor(QueryUpdate.class);
//        cols.add(col);
//      }
//      type = new AliasedRelation(select().type(), alias());
//      type = new AliasedRelation(new Selection(cols, this), alias());
      type = new AliasedRelation(new Selection(select().type(path.add(select())).columns().stream()
                                                       .map(c -> c.expression(new ColumnRef(c.context, null, c.alias())))
                                                       .toList() , this), alias());
      context.type(alias(), type);
//    }
    return type;
  }

  @Override
  protected String trans(Target target, EsqlPath path, Map<String, Object> parameters) {
    return '(' + select().translate(target, path.add(select()), parameters).statement + ") \"" + alias() + '"';
  }

  @Override
  public String toString() {
    return alias() + ":(" + select() + ")";
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    st.append(alias()).append(":(");
    select()._toString(st, level, indent);
    st.append(")");
  }

  public Select select() {
    return child("select");
  }

  public String alias() {
    return childValue("alias");
  }

  private transient volatile AliasedRelation type;
}