package ma.vi.esql.translation;

import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.modify.Delete;
import ma.vi.esql.syntax.modify.Insert;
import ma.vi.esql.syntax.modify.InsertRow;
import ma.vi.esql.syntax.modify.Update;
import ma.vi.esql.syntax.query.QueryTranslation;
import ma.vi.esql.syntax.query.Select;
import ma.vi.esql.syntax.query.SingleTableExpr;
import ma.vi.esql.syntax.query.TableExpr;
import org.pcollections.PMap;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.joining;
import static ma.vi.esql.translation.SqlServerTranslator.DONT_ADD_IIF;
import static ma.vi.esql.translation.Translatable.Target.ESQL;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class EsqlTranslator extends AbstractTranslator {
  @Override
  public Translatable.Target target() {
    return ESQL;
  }

  @Override
  protected QueryTranslation translate(Select select, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env) {
    StringBuilder st = new StringBuilder("select ");
    if (select.distinct()) {
      st.append("distinct ");
      List<Expression<?, ?>> distinctOn = select.distinctOn();
      if (distinctOn != null && !distinctOn.isEmpty()) {
        st.append("on (")
          .append(distinctOn.stream()
                            .map(e -> e.translate(target(), esqlCon, path.add(e), parameters, env).toString())
                            .collect(joining(", ")))
          .append(") ");
      }
    }

    if (select.explicit() != null && select.explicit()) {
      st.append("explicit ");
    }

    st.append(select.columns().stream()
                    .map(c -> c.translate(target(), esqlCon, path.add(c), parameters, env))
                    .collect(joining(", ")));

    if (select.tables() != null) {
      st.append(" from ").append(select.tables().translate(target(), esqlCon, path.add(select.tables()), parameters, env));
    }
    if (select.where() != null) {
      st.append(" where ").append(select.where().translate(target(), esqlCon, path.add(select.where()), parameters, env));
    }
    if (select.groupBy() != null) {
      st.append(select.groupBy().translate(target(), esqlCon, path.add(select.groupBy()), parameters, env));
    }
    if (select.having() != null) {
      st.append(" having ").append(select.having().translate(target(), esqlCon, path.add(select.having()), parameters, env));
    }
    if (select.orderBy() != null && !select.orderBy().isEmpty()) {
      st.append(" order by ")
        .append(select.orderBy().stream()
                      .map(e -> e.translate(target(), esqlCon, path.add(e), parameters, env))
                      .collect(joining(", ")));
    }
    if (select.offset() != null) {
      st.append(" offset ").append(select.offset().translate(target(), esqlCon, path.add(select.offset()), parameters, env));
    }
    if (select.limit() != null) {
      st.append(" limit ").append(select.limit().translate(target(), esqlCon, path.add(select.limit()), parameters, env));
    }
    return new QueryTranslation(select, st.toString(),null, null);
  }

  @Override
  protected QueryTranslation translate(Update               update,
                                       EsqlConnection       esqlCon,
                                       EsqlPath             path,
                                       PMap<String, Object> parameters,
                                       Environment          env) {
    TableExpr from = update.tables();
    StringBuilder st = new StringBuilder();
    st.append("update ").append(update.updateTableAlias())
      .append(" from ") .append(from.translate(target(), esqlCon, path.add(from), parameters, env));
    Util.addSet(st, update.set(), target(), false, path, env);
    if (update.where() != null) {
      st.append(" where ").append(update.where().translate(target(), esqlCon, path.add(update.where()), parameters, env));
    }
    QueryTranslation q = null;
    if (update.columns() != null) {
      st.append(" returning ");
      q = update.constructResult(st, target(), path, null, parameters);
    }
    if (q == null) {
      return new QueryTranslation(update, st.toString(), emptyList(), emptyMap());
    } else {
      return new QueryTranslation(update,
                                  st.toString(),
                                  q.columns(),
                                  q.resultAttributes());
    }
  }

  @Override
  protected QueryTranslation translate(Delete               delete,
                                       EsqlConnection       esqlCon,
                                       EsqlPath             path,
                                       PMap<String, Object> parameters,
                                       Environment          env) {
    TableExpr from = delete.tables();
    StringBuilder st = new StringBuilder();
    st.append("delete ").append(delete.deleteTableAlias())
      .append(" from ") .append(from.translate(target(), esqlCon, path.add(from), parameters, env));

    if (delete.where() != null) {
      st.append(" where ").append(delete.where().translate(target(), esqlCon, path.add(delete.where()), parameters, env));
    }
    if (delete.columns() != null && !delete.columns().isEmpty()) {
      st.append(" returning ");
      QueryTranslation q = delete.constructResult(st, target(), path,null, parameters);
      return new QueryTranslation(delete,
                                  st.toString(),
                                  q.columns(),
                                  q.resultAttributes());
    } else {
      return new QueryTranslation(delete, st.toString(), emptyList(), emptyMap());
    }
  }

  @Override
  protected QueryTranslation translate(Insert insert, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env) {
    StringBuilder st = new StringBuilder("insert into ");
    TableExpr table = insert.tables();
    if (!(table instanceof SingleTableExpr)) {
      throw new TranslationException(insert, "Insert only works with single tables. A " + table.getClass().getSimpleName()
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
                    .map(row -> row.translate(target(), esqlCon, path.add(row), parameters, env))
                    .collect(joining(", ", " values", "")));

    } else if (insert.defaultValues()) {
      st.append(" default values");

    } else {
      st.append(' ').append(insert.select().translate(target(), null, path.add(insert.select()), DONT_ADD_IIF, null).translation());
    }

    QueryTranslation q = null;
    if (insert.columns() != null && !insert.columns().isEmpty()) {
      st.append(" return ");
      q = insert.constructResult(st, target(), path, null, parameters);
    }

    if (q == null) {
      return new QueryTranslation(insert, st.toString(), emptyList(), emptyMap());
    } else {
      return new QueryTranslation(insert,
                                  st.toString(),
                                  q.columns(),
                                  q.resultAttributes());
    }
  }
}
