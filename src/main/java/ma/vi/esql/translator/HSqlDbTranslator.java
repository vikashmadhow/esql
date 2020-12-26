package ma.vi.esql.translator;

import ma.vi.esql.parser.Translatable;
import ma.vi.esql.parser.TranslationException;
import ma.vi.esql.parser.expression.Expression;
import ma.vi.esql.parser.modify.Delete;
import ma.vi.esql.parser.modify.Update;
import ma.vi.esql.parser.query.QueryTranslation;
import ma.vi.esql.parser.query.Select;
import ma.vi.esql.parser.query.SingleTableExpr;
import ma.vi.esql.parser.query.TableExpr;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.joining;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class HSqlDbTranslator extends AbstractTranslator {
  @Override
  public Translatable.Target target() {
    return Translatable.Target.HSQLDB;
  }

  @Override
  protected QueryTranslation translate(Select select) {
    StringBuilder st = new StringBuilder("select ");
    if (select.distinct()) {
      st.append("distinct ");
      List<Expression<?>> distinctOn = select.distinctOn();
      if (distinctOn != null && !distinctOn.isEmpty()) {
        st.append('(')
          .append(distinctOn.stream().map(e -> e.translate(target())).collect(joining(", ")))
          .append(") ");
      }
    }

    // add output clause
    QueryTranslation q = select.constructResult(st, target(), null,
                                                true, true);
    if (select.tables() != null) {
      st.append(" from ").append(select.tables().translate(target()));
    }
    if (select.where() != null) {
      st.append(" where ").append(select.where().translate(target()));
    }
    if (select.groupBy() != null) {
      st.append(select.groupBy().translate(target()));
    }
    if (select.having() != null) {
      st.append(" having ").append(select.having().translate(target()));
    }
    if (select.orderBy() != null && !select.orderBy().isEmpty()) {
      st.append(" order by ")
        .append(select.orderBy().stream()
                      .map(e -> e.translate(target()))
                      .collect(joining(", ")));
    }
    if (select.offset() != null) {
      st.append(" offset ").append(select.offset().translate(target()));
    }
    if (select.limit() != null) {
      st.append(" limit ").append(select.limit().translate(target()));
    }
    return new QueryTranslation(st.toString(),
                                q.columns,
                                q.columnToIndex,
                                q.resultAttributeIndices,
                                q.resultAttributes);
  }

  @Override
  protected QueryTranslation translate(Update update) {
    StringBuilder st = new StringBuilder("update ");

    TableExpr from = update.tables();
    if (!(from instanceof SingleTableExpr)) {
      throw new TranslationException(target() + " does not support multiple tables or joins in updates");
    }
    st.append(from.translate(target()));
    Update.addSet(st, update.set(), target(), false);

    if (update.where() != null) {
      st.append(" where ").append(update.where().translate(target()));
    }
    if (update.columns() != null) {
      throw new TranslationException(target() + " does not support return values in updates");
    }
    return new QueryTranslation(st.toString(), emptyList(), emptyMap(),
                                emptyList(), emptyMap());
  }

  @Override
  protected QueryTranslation translate(Delete delete) {
    StringBuilder st = new StringBuilder("delete ");
    TableExpr from = delete.tables();
    if (!(from instanceof SingleTableExpr)) {
      throw new TranslationException(target() + " does not support multiple tables or joins in updates");
    }
    st.append(" from ").append(from.translate(target()));
    if (delete.where() != null) {
      st.append(" where ").append(delete.where().translate(target()));
    }
    if (delete.columns() != null) {
      throw new TranslationException(target() + " does not support returning rows in deletes");
    }
    return new QueryTranslation(st.toString(), emptyList(), emptyMap(),
                                emptyList(), emptyMap());
  }
}
