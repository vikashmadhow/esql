package ma.vi.esql.translator;

import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.Translatable;
import ma.vi.esql.syntax.TranslationException;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.modify.Delete;
import ma.vi.esql.syntax.modify.Insert;
import ma.vi.esql.syntax.modify.InsertRow;
import ma.vi.esql.syntax.modify.Update;
import ma.vi.esql.syntax.query.QueryTranslation;
import ma.vi.esql.syntax.query.Select;
import ma.vi.esql.syntax.query.SingleTableExpr;
import ma.vi.esql.syntax.query.TableExpr;

import java.util.List;
import java.util.Map;

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
  protected QueryTranslation translate(Select select, EsqlPath path, Map<String, Object> parameters) {
    StringBuilder st = new StringBuilder("select ");
    if (select.distinct()) {
      st.append("distinct ");
      List<Expression<?, String>> distinctOn = select.distinctOn();
      if (distinctOn != null && !distinctOn.isEmpty()) {
        st.append('(')
          .append(distinctOn.stream().map(e -> e.translate(target(), path.add(e), parameters)).collect(joining(", ")))
          .append(") ");
      }
    }

    // add output clause
    QueryTranslation q = select.constructResult(st, target(), path, null, parameters);
    if (select.tables() != null) {
      st.append(" from ").append(select.tables().translate(target(), path.add(select.tables()), parameters));
    }
    if (select.where() != null) {
      st.append(" where ").append(select.where().translate(target(), path.add(select.where()), parameters));
    }
    if (select.groupBy() != null) {
      st.append(select.groupBy().translate(target(), path.add(select.groupBy()), parameters));
    }
    if (select.having() != null) {
      st.append(" having ").append(select.having().translate(target(), path.add(select.having()), parameters));
    }
    if (select.orderBy() != null && !select.orderBy().isEmpty()) {
      st.append(" order by ")
        .append(select.orderBy().stream()
                      .map(e -> e.translate(target(), path.add(e), parameters))
                      .collect(joining(", ")));
    }
    if (select.offset() != null) {
      st.append(" offset ").append(select.offset().translate(target(), path.add(select.offset()), parameters));
    }
    if (select.limit() != null) {
      st.append(" limit ").append(select.limit().translate(target(), path.add(select.limit()), parameters));
    }
    return new QueryTranslation(st.toString(),
                                q.columns(),
                                q.columnToIndex(),
                                q.resultAttributeIndices(),
                                q.resultAttributes());
  }

  @Override
  protected QueryTranslation translate(Update update, EsqlPath path, Map<String, Object> parameters) {
    StringBuilder st = new StringBuilder("update ");

    TableExpr from = update.tables();
    if (!(from instanceof SingleTableExpr)) {
      throw new TranslationException(target() + " does not support multiple tables or joins in updates");
    }
    st.append(from.translate(target(), path.add(from), parameters));
    Update.addSet(st, update.set(), target(), false, path);

    if (update.where() != null) {
      st.append(" where ").append(update.where().translate(target(), path.add(update.where()), parameters));
    }
    if (update.columns() != null) {
      throw new TranslationException(target() + " does not support return values in updates");
    }
    return new QueryTranslation(st.toString(), emptyList(), emptyMap(),
                                emptyList(), emptyMap());
  }

  @Override
  protected QueryTranslation translate(Delete delete, EsqlPath path, Map<String, Object> parameters) {
    StringBuilder st = new StringBuilder("delete ");
    TableExpr from = delete.tables();
    if (!(from instanceof SingleTableExpr)) {
      throw new TranslationException(target() + " does not support multiple tables or joins in updates");
    }
    st.append(" from ").append(from.translate(target(), path.add(from), parameters));
    if (delete.where() != null) {
      st.append(" where ").append(delete.where().translate(target(), path.add(delete.where()), parameters));
    }
    if (delete.columns() != null) {
      throw new TranslationException(target() + " does not support returning rows in deletes");
    }
    return new QueryTranslation(st.toString(), emptyList(), emptyMap(),
                                emptyList(), emptyMap());
  }

  @Override
  protected QueryTranslation translate(Insert insert, EsqlPath path, Map<String, Object> parameters) {
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
                    .map(row -> row.translate(target(), path.add(row), parameters))
                    .collect(joining(", ", " values", "")));

    } else if (insert.defaultValues()) {
      st.append(" default values");

    } else {
      st.append(' ').append(insert.select().translate(target(), path.add(insert.select()), Map.of("addAttributes", false)).statement());
    }

    if (insert.columns() != null && !insert.columns().isEmpty()) {
      throw new TranslationException(target() + " does not support returning rows in inserts");
    }
    return new QueryTranslation(st.toString(), emptyList(), emptyMap(), emptyList(), emptyMap());
  }
}
