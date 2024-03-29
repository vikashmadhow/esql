/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.query;

import ma.vi.base.tuple.T2;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.semantic.type.Column;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import org.pcollections.PMap;

/**
 * The wild-card (*) representing all columns in a query.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class StarColumn extends Column {
  public StarColumn(Context context, String qualifier) {
    super(context, "_", null, null, null,
          T2.of("qualifier", new Esql<>(context, qualifier)));
  }

  public StarColumn(StarColumn other) {
    super(other);
  }

  @SafeVarargs
  public StarColumn(StarColumn other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public StarColumn copy() {
    return new StarColumn(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public StarColumn copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new StarColumn(this, value, children);
  }

  @Override
  protected String trans(Target target, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env) {
    return switch (target) {
      case JAVASCRIPT, ESQL -> (qualifier() != null ? qualifier() + '.' : "") + "*";
      default               -> (qualifier() != null ? '"' + qualifier() + "\"." : "") + "*";
    };
  }

  @Override
  public String toString() {
    return (qualifier() == null ? "" : qualifier() + '.') + '*';
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    st.append(qualifier() == null ? "" : qualifier() + ".").append('*');
  }

  public String qualifier() {
    return childValue("qualifier");
  }
}