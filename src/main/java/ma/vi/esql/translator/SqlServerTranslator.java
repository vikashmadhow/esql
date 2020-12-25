package ma.vi.esql.translator;

import ma.vi.base.tuple.T2;
import ma.vi.esql.parser.Translatable;
import ma.vi.esql.parser.TranslationException;
import ma.vi.esql.parser.modify.Delete;
import ma.vi.esql.parser.modify.Update;
import ma.vi.esql.parser.query.*;
import ma.vi.esql.type.Type;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class SqlServerTranslator extends AbstractTranslator {
  @Override
  public Translatable.Target target() {
    return Translatable.Target.SQLSERVER;
  }

  @Override
  protected QueryTranslation translate(Update update) {
    StringBuilder st = new StringBuilder("update ");

    TableExpr from = update.tables();
    st.append(update.updateTableAlias());
    Update.addSet(st, update.set(), target(), true);

    // output clause for SQL Server if specified
    QueryTranslation q = null;
    if (update.columns() != null) {
      st.append(" output ");
      q = update.constructResult(st, target(), "inserted", true, true);
    }
    st.append(" from ").append(from.translate(target()));

    if (update.where() != null) {
      st.append(" where ").append(update.where().translate(target()));
    }
    if (q == null) {
      return new QueryTranslation(st.toString(), emptyList(), emptyMap(),
                                     emptyList(), emptyMap());
    } else {
      return new QueryTranslation(st.toString(), q.columns, q.columnToIndex,
                                     q.resultAttributeIndices, q.resultAttributes);
    }
  }

  @Override
  protected QueryTranslation translate(Delete delete) {
    StringBuilder st = new StringBuilder("delete ").append((delete.deleteTableAlias()));
    QueryTranslation q = null;

    TableExpr from = delete.tables();
    if (delete.columns() != null) {
      st.append(" output ");
      q = delete.constructResult(st, target(), "deleted", true, true);
    }
    st.append(" from ").append(from.translate(target()));

    if (delete.where() != null) {
      st.append(" where ").append(delete.where().translate(target()));
    }
    if (q == null) {
      return new QueryTranslation(st.toString(), emptyList(), emptyMap(),
                                  emptyList(), emptyMap());
    } else {
      return new QueryTranslation(st.toString(), q.columns, q.columnToIndex,
                                  q.resultAttributeIndices, q.resultAttributes);
    }
  }
}
