/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.define;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.expression.Expression;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.joining;

/**
 * A list of attributes (name expression pairs) describing
 * certain parts of queries, tables, etc. This is also used as
 * the update set clause as it has the same structure.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.mail)
 */
public class GroupBy extends Esql<String, String> {

  public enum Type {
    Simple, Rollup, Cube;
  }

  public GroupBy(Context context,
                 List<Expression<?, String>> groupBy,
                 Type groupType) {
    super(context, "groupBy",
        T2.of("groupBy", new Esql<>(context, "groupBy", groupBy)),
        T2.of("type", new Esql<>(context, groupType)));
  }

  public GroupBy(GroupBy other) {
    super(other);
  }

  @Override
  public GroupBy copy() {
    if (!copying()) {
      try {
        copying(true);
        return new GroupBy(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  protected String trans(Target target, Map<String, Object> parameters) {
    Type type = groupType();
    return " group by "
        + (type == Type.Rollup ? "rollup(" :
           type == Type.Cube   ? "cube("   : "")

        + groupBy().stream()
                   .map(a -> a.translate(target, parameters))
                   .collect(joining(", "))

        + (type != Type.Simple ? ")" : "");
  }

  public List<Expression<?, String>> groupBy() {
    return child("groupBy").childrenList();
  }

  public Type groupType() {
    return childValue("type");
  }
}