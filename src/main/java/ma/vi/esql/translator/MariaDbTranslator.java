package ma.vi.esql.translator;

import ma.vi.esql.parser.Translatable;
import ma.vi.esql.parser.TranslationException;
import ma.vi.esql.parser.expression.Expression;
import ma.vi.esql.parser.modify.Delete;
import ma.vi.esql.parser.modify.Insert;
import ma.vi.esql.parser.modify.InsertRow;
import ma.vi.esql.parser.modify.Update;
import ma.vi.esql.parser.query.*;
import ma.vi.esql.type.Type;

import java.util.List;
import java.util.Map;

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
  protected QueryTranslation translate(Select select, Map<String, Object> parameters) {
    StringBuilder st = new StringBuilder("select ");
    if (select.distinct()) {
      st.append("distinct ");
      List<Expression<?, String>> distinctOn = select.distinctOn();
      if (distinctOn != null && !distinctOn.isEmpty()) {
        throw new TranslationException(target() + " does not support distinct limited to columns");
      }
    }

    // add output clause
    QueryTranslation q = select.constructResult(st, target(), null, parameters);
    if (select.tables() != null) {
      st.append(" from ").append(select.tables().translate(target(), parameters));
    }
    if (select.where() != null) {
      st.append(" where ").append(select.where().translate(target(), parameters));
    }
    if (select.groupBy() != null) {
      st.append(select.groupBy().translate(target(), parameters));
    }
    if (select.having() != null) {
      st.append(" having ").append(select.having().translate(target(), parameters));
    }
    if (select.orderBy() != null && !select.orderBy().isEmpty()) {
      st.append(" order by ")
        .append(select.orderBy().stream()
                      .map(e -> e.translate(target(), parameters))
                      .collect(joining(", ")));
    }
    if (select.limit() != null) {
      st.append(" limit ").append(select.limit().translate(target(), parameters));
    }
    if (select.offset() != null) {
      st.append(" offset ").append(select.offset().translate(target(), parameters));
    }
    return new QueryTranslation(st.toString(),
                                q.columns,
                                q.columnToIndex,
                                q.resultAttributeIndices,
                                q.resultAttributes);
  }

  @Override
  protected QueryTranslation translate(Update update, Map<String, Object> parameters) {
    StringBuilder st = new StringBuilder("update ");

    TableExpr from = update.tables();
    st.append(from.translate(target(), parameters));
    Update.addSet(st, update.set(), target(), false);

    if (update.where() != null) {
      st.append(" where ").append(update.where().translate(target(), parameters));
    }
    if (update.columns() != null) {
      throw new TranslationException(target() + " does not support return values in updates");
    }
    return new QueryTranslation(st.toString(), emptyList(), emptyMap(),
                                   emptyList(), emptyMap());
  }

  @Override
  protected QueryTranslation translate(Delete delete, Map<String, Object> parameters) {
    StringBuilder st = new StringBuilder("delete ");
    QueryTranslation q = null;

    TableExpr from = delete.tables();
    if (from instanceof SingleTableExpr) {
      SingleTableExpr deleteTable = (SingleTableExpr)from;
      st.append(" from ").append(Type.dbTableName(deleteTable.tableName(), target()));

    } else if (from instanceof AbstractJoinTableExpr) {
      /*
       * Maria DB allows a form of delete using joins which is close to ESQL
       * but uses the table name instead of the alias.
       */
      SingleTableExpr deleteTable = Delete.findSingleTable((AbstractJoinTableExpr)from, delete.deleteTableAlias());
      if (deleteTable == null) {
        throw new TranslationException("Could not find table with alias " + delete.deleteTableAlias());
      }
      st.append(Type.dbTableName(deleteTable.tableName(), target()));
      st.append(" from ").append(from.translate(target(), parameters));

    } else {
      throw new TranslationException("Wrong table type to delete: " + from);
    }

    if (delete.where() != null) {
      st.append(" where ").append(delete.where().translate(target(), parameters));
    }
    if (delete.columns() != null) {
      st.append(" returning ");
      q = delete.constructResult(st, target(), null, parameters);
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
  protected QueryTranslation translate(Insert insert, Map<String, Object> parameters) {
    StringBuilder st = new StringBuilder("insert into ");
    TableExpr table = insert.tables();
    if (!(table instanceof SingleTableExpr)) {
      throw new TranslationException("Insert only works with single tables. A " + table.getClass().getSimpleName()
                                         + " was found instead.");
    }
    st.append(Type.dbTableName(((SingleTableExpr)table).tableName(), target()));

    List<String> fields = insert.fields();
    if (fields != null && !fields.isEmpty()) {
      st.append(fields.stream()
                      .map(f -> '"' + f + '"')
                      .collect(joining(", ", "(", ")")));
    }

    List<InsertRow> rows = insert.rows();
    if (rows != null && !rows.isEmpty()) {
      st.append(rows.stream()
                    .map(row -> row.translate(target(), parameters))
                    .collect(joining(", ", " values", "")));

    } else if (insert.defaultValues()) {
      st.append(" default values");

    } else {
      st.append(' ').append(insert.select().translate(target(), Map.of("addAttributes", false)).statement);
    }

    QueryTranslation q = null;
    if (insert.columns() != null && !insert.columns().isEmpty()) {
      st.append(" returning ");
      q = insert.constructResult(st, target(), null, parameters);
    }

    if (q == null) {
      return new QueryTranslation(st.toString(), emptyList(), emptyMap(), emptyList(), emptyMap());
    } else {
      return new QueryTranslation(st.toString(), q.columns, q.columnToIndex,
                                  q.resultAttributeIndices, q.resultAttributes);
    }
  }
}
