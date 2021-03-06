/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.query;

import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.QueryUpdate;
import ma.vi.esql.parser.expression.ColumnRef;
import ma.vi.esql.type.AliasedRelation;
import ma.vi.esql.type.Selection;

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
    super(context, "SelectAsTable", alias);
    child("select", select);
  }

  public SelectTableExpr(SelectTableExpr other) {
    super(other);
  }

  @Override
  public SelectTableExpr copy() {
    if (!copying()) {
      try {
        copying(true);
        return new SelectTableExpr(this);
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
      Selection selectType = select().type();
      List<Column> cols = new ArrayList<>();
      for (Column c: selectType.columns()) {
        Column col = new Column(c.context, c.alias(), new ColumnRef(c.context, null, c.alias()), null);
        col.parent = ancestor(QueryUpdate.class);
        cols.add(col);
      }
//      type = new AliasedRelation(select().type(), alias());
      type = new AliasedRelation(new Selection(cols, this), alias());
    }
    return type;
  }

  @Override
  public String translate(Target target, Map<String, Object> parameters) {
    return '(' + select().translate(target, parameters).statement + ") \"" + alias() + '"';
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