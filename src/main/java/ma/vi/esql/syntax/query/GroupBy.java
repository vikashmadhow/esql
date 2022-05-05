/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.query;

import ma.vi.base.tuple.T2;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import org.pcollections.PMap;

import java.util.List;

import static java.util.stream.Collectors.joining;

/**
 * The group by clause of a Select consisting of a list of expressions and the
 * type of group (simple, rollup or cube).
 *
 * @author Vikash Madhow (vikash.madhow@gmail.mail)
 */
public class GroupBy extends Esql<String, String> {

  public enum Type {
    Simple, Rollup, Cube
  }

  public GroupBy(Context context,
                 List<Expression<?, ?>> groupBy,
                 Type groupType) {
    super(context, "GroupBy",
          T2.of("groupBy", new Esql<>(context, "groupBy", groupBy, true)),
          T2.of("type",    new Esql<>(context, groupType)));
  }

  public GroupBy(GroupBy other) {
    super(other);
  }

  @SafeVarargs
  public GroupBy(GroupBy other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public GroupBy copy() {
    return new GroupBy(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  public GroupBy copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new GroupBy(this, value, children);
  }

  @Override
  protected String trans(Target target, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env) {
    Type type = groupType();
    return " group by "
         + (type == Type.Rollup ? "rollup(" :
            type == Type.Cube   ?   "cube(" : "")

         + groupBy().stream()
                    .map(a -> a.translate(target, esqlCon, path.add(a), parameters, env))
                    .collect(joining(", "))

         + (type != Type.Simple ? ")" : "");
  }

  public List<Expression<?, String>> groupBy() {
    return child("groupBy").children();
  }

  public Type groupType() {
    return childValue("type");
  }
}