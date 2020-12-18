/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.modify;

import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.Esql;
import ma.vi.esql.parser.QueryUpdate;
import ma.vi.esql.parser.define.Attribute;
import ma.vi.esql.parser.define.Metadata;
import ma.vi.esql.parser.expression.Expression;
import ma.vi.esql.parser.query.*;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static ma.vi.base.tuple.T2.of;

/**
 * Update statement.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Update extends QueryUpdate {
  public Update(Context context,
                SingleTableExpr from,
                Metadata set,
                TableExpr using,
                Expression<?> where,
                Metadata metadata,
                List<Column> returning) {
    super(context, "Update",
        of("set", set),
        of("where", where),
        of("metadata", metadata),
        of("columns", new Esql<>(context, returning)));

    // combine using and from as the usage tables if both are specified
    if (using != null) {
      child("tables", new CrossProductTableExpr(context, from, using));
      child("from", from);
      child("using", using);
    } else {
      child("tables", from);
      child("from", from);
    }
  }

  public Update(Update other) {
    super(other);
  }

  @Override
  public Update copy() {
    if (!copying()) {
      try {
        copying(true);
        return new Update(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public QueryTranslation translate(Target target) {
    StringBuilder st = new StringBuilder("update ");
    QueryTranslation q = null;

    SingleTableExpr from = from();
    if (target == Target.SQLSERVER) {
      // SQL Server supports multiple tables in updates but, in those cases,
      // it is simpler and clearer to refer to the table being updated using
      // its alias instead of a double from clause.
      st.append(from.alias());
    } else {
      st.append(from.translate(target));
    }

    boolean first = true;
    st.append(" set ");
    for (Attribute set: set().attributes().values()) {
      if (first) {
        first = false;
      } else {
        st.append(", ");
      }
      st.append('"').append(set.name()).append("\"=")
        .append(set.attributeValue().translate(target));
    }

    if (target == Target.SQLSERVER) {
      // output clause for SQL Server if specified
      if (columns() != null) {
        st.append(" output ");
        q = constructResult(st, target, "inserted", true, true);
      }
      st.append(" from ").append(tables().translate(target));

    } else {
      if (using() != null) {
        st.append(" from ").append(using().translate(target));
      }
    }

    if (where() != null) {
      st.append(" where ").append(where().translate(target));
    }

    if (target == Target.POSTGRESQL && columns() != null) {
      st.append(" returning ");
      q = constructResult(st, target, null, true, true);
    }

    if (q == null) {
      return new QueryTranslation(st.toString(), emptyList(), emptyMap(),
          emptyList(), emptyMap());
    } else {
      return new QueryTranslation(st.toString(), q.columns, q.columnToIndex,
          q.resultAttributeIndices, q.resultAttributes);
    }
  }

  public Metadata set() {
    return child("set");
  }

  public SingleTableExpr from() {
    return child("from");
  }

  public TableExpr using() {
    return child("using");
  }
}