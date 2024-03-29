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
import ma.vi.esql.syntax.query.*;
import org.pcollections.PMap;

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
  protected QueryTranslation translate(Select select, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env) {
    StringBuilder st = new StringBuilder("select ");
    if (select.distinct()) {
      st.append("distinct ");
      List<Expression<?, ?>> distinctOn = select.distinctOn();
      if (distinctOn != null && !distinctOn.isEmpty()) {
        throw new TranslationException(select, target() + " does not support distinct limited to columns");
      }
    }

    // add output clause
    QueryTranslation q = select.constructResult(st, target(), path, null, parameters, env);
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
    if (select.limit() != null) {
      st.append(" limit ").append(select.limit().translate(target(), esqlCon, path.add(select.limit()), parameters, env));
    }
    if (select.offset() != null) {
      st.append(" offset ").append(select.offset().translate(target(), esqlCon, path.add(select.offset()), parameters, env));
    }
    return new QueryTranslation(select,
                                st.toString(),
                                q.columns(),
                                q.resultAttributes());
  }

  @Override
  protected QueryTranslation translate(Update               update,
                                       EsqlConnection       esqlCon,
                                       EsqlPath             path,
                                       PMap<String, Object> parameters,
                                       Environment          env) {
    StringBuilder st = new StringBuilder("update ");

    TableExpr from = update.tables();
    st.append(from.translate(target(), esqlCon, path.add(from), parameters, env));
    Util.addSet(st, update.set(), target(), false, path, env);

    if (update.where() != null) {
      st.append(" where ").append(update.where().translate(target(), esqlCon, path.add(update.where()), parameters, env));
    }
    if (update.columns() != null) {
      throw new TranslationException(update, target() + " does not support return values in updates");
    }
    return new QueryTranslation(update, st.toString(), emptyList(), emptyMap());
  }

  @Override
  protected QueryTranslation translate(Delete delete, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env) {
    StringBuilder st = new StringBuilder("delete ");
    QueryTranslation q = null;

    TableExpr from = delete.tables();
    if (from instanceof SingleTableExpr deleteTable) {
      st.append(" from ").append(Type.dbTableName(deleteTable.tableName(), target()));

    } else if (from instanceof AbstractJoinTableExpr) {
      /*
       * Maria DB allows a form of delete using joins which is close to ESQL
       * but uses the table name instead of the alias.
       */
      SingleTableExpr deleteTable = (SingleTableExpr)from.aliased(delete.deleteTableAlias());
      if (deleteTable == null) {
        throw new TranslationException(delete, "Could not find table with alias " + delete.deleteTableAlias());
      }
      st.append(Type.dbTableName(deleteTable.tableName(), target()));
      st.append(" from ").append(from.translate(target(), esqlCon, path.add(from), parameters, env));

    } else {
      throw new TranslationException(delete, "Wrong table type to delete: " + from);
    }

    if (delete.where() != null) {
      st.append(" where ").append(delete.where().translate(target(), esqlCon, path.add(delete.where()), parameters, env));
    }
    if (delete.columns() != null) {
      st.append(" returning ");
      q = delete.constructResult(st, target(), path, null, parameters, env);
    }
    if (q == null) {
      return new QueryTranslation(delete, st.toString(), emptyList(), emptyMap());
    } else {
      return new QueryTranslation(delete,
                                  st.toString(),
                                  q.columns(),
                                  q.resultAttributes());
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
      st.append(' ').append(insert.select().translate(target(),
                                                      esqlCon, path.add(insert.select()),
                                                      parameters.plus("addAttributes", false), env).translation());
    }

    QueryTranslation q = null;
    if (insert.columns() != null && !insert.columns().isEmpty()) {
      st.append(" returning ");
      q = insert.constructResult(st, target(), path, null, parameters, env);
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
