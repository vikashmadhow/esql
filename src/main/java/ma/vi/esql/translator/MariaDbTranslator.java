package ma.vi.esql.translator;

import ma.vi.esql.parser.Translatable;
import ma.vi.esql.parser.TranslationException;
import ma.vi.esql.parser.expression.Expression;
import ma.vi.esql.parser.modify.Delete;
import ma.vi.esql.parser.modify.Update;
import ma.vi.esql.parser.query.*;
import ma.vi.esql.type.Type;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.joining;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class MariaDbTranslator extends AbstractTranslator {
  @Override
  public Translatable.Target target() {
    return Translatable.Target.MARIADB;
  }

  @Override
  protected QueryTranslation translate(Select select) {
    StringBuilder st = new StringBuilder("select ");
    if (select.distinct()) {
      st.append("distinct ");
      List<Expression<?>> distinctOn = select.distinctOn();
      if (distinctOn != null && !distinctOn.isEmpty()) {
        throw new TranslationException(target() + " does not support distinct limited to columns");
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
    if (select.limit() != null) {
      st.append(" limit ").append(select.limit().translate(target()));
    }
    if (select.offset() != null) {
      st.append(" offset ").append(select.offset().translate(target()));
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
    QueryTranslation q = null;

    TableExpr from = delete.tables();
    if (from instanceof SingleTableExpr) {
      st.append(" from ").append(from.translate(target()));

    } else if (from instanceof AbstractJoinTableExpr) {
      /*
       * Maria DB allows a form of delete using joins which is close to ESQL
       * but uses the table name instead of the alias.
       */
      SingleTableExpr deleteTable = Delete.findSingleTable((AbstractJoinTableExpr)from, delete.deleteTableAlias());
      if (deleteTable == null) {
        throw new TranslationException("Could not find table with alias " + delete.deleteTableAlias());
      }
      st.append(Type.dbName(deleteTable.tableName(), target()));
      st.append(" from ").append(from.translate(target()));

    } else {
      throw new TranslationException("Wrong table type to delete: " + from);
    }

    if (delete.where() != null) {
      st.append(" where ").append(delete.where().translate(target()));
    }
    if (delete.columns() != null) {
      st.append(" returning ");
      q = delete.constructResult(st, target(), null, true, true);
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
