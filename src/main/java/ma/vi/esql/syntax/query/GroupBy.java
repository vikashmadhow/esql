/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.query;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;

import java.util.List;
import java.util.Map;

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
                 List<Expression<?, String>> groupBy,
                 Type groupType) {
    super(context, "GroupBy",
          T2.of("groupBy", new Esql<>(context, "groupBy", groupBy)),
          T2.of("type", new Esql<>(context, groupType)));
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
  protected String trans(Target target, EsqlPath path, Map<String, Object> parameters) {
    Type type = groupType();
    return " group by "
         + (type == Type.Rollup ? "rollup(" :
            type == Type.Cube   ?   "cube(" : "")

         + groupBy().stream()
                    .map(a -> a.translate(target, path.add(a), parameters))
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