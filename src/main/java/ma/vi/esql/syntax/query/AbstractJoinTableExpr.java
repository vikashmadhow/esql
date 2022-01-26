/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.query;

import ma.vi.base.tuple.T2;
import ma.vi.esql.semantic.type.Join;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.macro.Macro;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Abstract parent of cross-products and joins table expressions. Each of these
 * table expressions compose two table expressions, which are accessible through
 * {@link #left()} and {@link #right()}.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public abstract class AbstractJoinTableExpr extends TableExpr {
  @SafeVarargs
  public AbstractJoinTableExpr(Context context,
                               String joinType,
                               TableExpr left,
                               TableExpr right,
                               T2<String, ? extends Esql<?, ?>>... children) {
    super(context, "Join",
          Stream.concat(
            Arrays.stream(
              new T2[]{
                T2.of("left",     left),
                T2.of("right",    right),
                T2.of("joinType", new Esql<>(context, joinType))
              }),
            Arrays.stream(children)).toArray(T2[]::new));
  }

  public AbstractJoinTableExpr(AbstractJoinTableExpr other) {
    super(other);
  }

  @SafeVarargs
  public AbstractJoinTableExpr(AbstractJoinTableExpr other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public abstract AbstractJoinTableExpr copy();

  @Override
  public TableExpr named(String name) {
    if (name == null) {
      return this;
    } else {
      TableExpr expr = left().named(name);
      return expr != null ? expr : right().named(name);
    }
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public abstract AbstractJoinTableExpr copy(String value, T2<String, ? extends Esql<?, ?>>... children);

  @Override
  public boolean exists(EsqlPath path) {
    return left().exists(path) && right().exists(path);
  }

  @Override
  public Join computeType(EsqlPath path) {
    if (type == Types.UnknownType) {
      Join t = new Join(left().computeType(path.add(left())),
                        right().computeType(path.add(right())));
      if (path.hasAncestor(Macro.OngoingMacroExpansion.class)) {
        return t;
      } else {
        type = t;
      }
    }
    return (Join)type;
  }

  @Override
  public List<Column> columnList(EsqlPath path) {
    List<Column> cols = new ArrayList<>(left().columnList(path));
    cols.addAll(right().columnList(path));
    return cols;
  }

  public String joinType() {
    return childValue("joinType");
  }

  public TableExpr left() {
    return child("left");
  }

  public TableExpr right() {
    return child("right");
  }
}