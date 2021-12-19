/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.modify;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.define.Attribute;
import ma.vi.esql.syntax.define.Metadata;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.query.Column;
import ma.vi.esql.syntax.query.ColumnList;
import ma.vi.esql.syntax.query.QueryUpdate;
import ma.vi.esql.syntax.query.TableExpr;

import java.util.List;

import static ma.vi.base.tuple.T2.of;

/**
 * Update statement.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Update extends QueryUpdate {
  public Update(Context       context,
                String        updateTableAlias,
                TableExpr     from,
                Metadata      set,
                Expression<?, String> where,
                Metadata      returnMetadata,
                List<Column>  returnColumns) {
    super(context, "Update",
          of("updateTableAlias", new Esql<>(context, updateTableAlias)),
          of("set",              set),
          of("tables",           from),
          of("where",            where),
          of("metadata",         returnMetadata),
          of("columns",          new ColumnList(context, returnColumns)));
  }

  public Update(Update other) {
    super(other);
  }

  public Update(Update other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public Update copy() {
    return new Update(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public Update copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new Update(this, value, children);
  }

  @Override
  public boolean modifying() {
    return true;
  }

  public static void addSet(StringBuilder st,
                            Metadata sets,
                            Target target,
                            boolean removeQualifier,
                            EsqlPath path) {
    boolean first = true;
    st.append(" set ");
    for (Attribute set: sets.attributes().values()) {
      if (first) {
        first = false;
      } else {
        st.append(", ");
      }
      String columnName = set.name();
      if (removeQualifier) {
        int pos = columnName.lastIndexOf('.');
        if (pos != -1) {
          columnName = columnName.substring(pos + 1);
        }
      }
      st.append('"').append(columnName).append("\"=")
        .append(set.attributeValue().translate(target, path));
    }
  }

  public Metadata set() {
    return child("set");
  }

  public String updateTableAlias() {
    return childValue("updateTableAlias");
  }
}