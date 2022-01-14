/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression;

import ma.vi.base.tuple.T2;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import org.pcollections.PMap;

/**
 * Keeps a reference to a derived column together with its expansion. This class
 * is used to keep information on the derived column after it has been expanded
 * so that the original reference can be accessed whenever necessary, such as when
 * determining if additional metadata associated with the original reference need
 * to be added to a column.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class ExpandedDerivedColumnRef extends Expression<String, String> {
  public ExpandedDerivedColumnRef(Context context,
                                  String qualifier,
                                  String name,
                                  Expression<?, String> expansion) {
    super(context, "ExpandedDerivedColumnRef",
        T2.of("qualifier", new Esql<>(context, qualifier)),
        T2.of("name", new Esql<>(context, name)),
        T2.of("expansion", expansion));
  }

  public ExpandedDerivedColumnRef(ExpandedDerivedColumnRef other) {
    super(other);
  }

  @SafeVarargs
  public ExpandedDerivedColumnRef(ExpandedDerivedColumnRef other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public ExpandedDerivedColumnRef copy() {
    return new ExpandedDerivedColumnRef(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public ExpandedDerivedColumnRef copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new ExpandedDerivedColumnRef(this, value, children);
  }

  @Override
  public Type computeType(EsqlPath path) {
    return expansion().computeType(path.add(expansion()));
  }

  @Override
  protected String trans(Target target,
                         EsqlPath path,
                         PMap<String, Object> parameters) {
    return expansion().translate(target, path.add(expansion()), parameters);
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    String q = qualifier();
    st.append(q != null ? q + '.' : "")
      .append(name()).append(':');
    expansion()._toString(st, level + 1, indent);
  }

  public String qualifier() {
    return childValue("qualifier");
  }

  public String name() {
    return childValue("name");
  }

  public Expression<?, String> expansion() {
    return child("expansion");
  }
}