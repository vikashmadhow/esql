/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.esql.parser.query;

import ma.vi.esql.parser.Context;
import ma.vi.esql.type.AliasedRelation;

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
      type = new AliasedRelation(select().type(), alias());
    }
    return type;
  }

  @Override
  public String translate(Target target) {
    return '(' + select().translate(target).statement + ") " + alias();
  }

  @Override
  public String toString() {
    return alias() + ":(" + select() + ")";
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    st.append(alias()).append(":(").append(select()).append(")");
  }

  public Select select() {
    return child("select");
  }

  public String alias() {
    return childValue("alias");
  }

  private volatile AliasedRelation type;
}