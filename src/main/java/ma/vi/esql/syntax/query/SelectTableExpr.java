/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.query;

import ma.vi.base.tuple.T2;
import ma.vi.esql.semantic.type.Selection;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.Macro;
import ma.vi.esql.syntax.expression.ColumnRef;
import org.pcollections.PMap;

import java.util.List;

/**
 * Represents a select in a from clause:
 *
 * <pre>
 *     select * from t:(select x, y from T)
 *                  |----------------------| -&gt; Select table expression
 * </pre>
 *
 * @author vikash.madhow@gmail.com
 */
public class SelectTableExpr extends AbstractAliasTableExpr {
  public SelectTableExpr(Context context, Select select, String alias) {
    super(context,
          "SelectAsTable",
          alias,
          T2.of("select", select));
  }

  public SelectTableExpr(SelectTableExpr other) {
    super(other);
  }

  @SafeVarargs
  public SelectTableExpr(SelectTableExpr other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public SelectTableExpr copy() {
     return new SelectTableExpr(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public SelectTableExpr copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new SelectTableExpr(this, value, children);
  }

  @Override
  public boolean exists(EsqlPath path) {
    return select().tables().exists(path);
  }

  @Override
  public TableExpr named(String name) {
    return name == null || name.equals(alias()) ? this : null;
  }

  @Override
  public List<Column> columnList(EsqlPath path) {
    return select().columns().stream()
                   .map(c -> new Column(c.context,
                                        c.name(),
                                        new ColumnRef(c.context, alias(), c.name()),
                                        c.type(),
                                        ColumnRef.qualify(c.metadata(), alias())))
                   .toList();
  }

  @Override
  public Selection computeType(EsqlPath path) {
    if (type == null) {
      Selection sel = select().computeType(path.add(select()));
      Selection t = new Selection(sel.columns().stream()
                                     .map(c -> c.b.expression(new ColumnRef(c.b.context, alias(), c.b.name())))
                                     .toList(),
                                  sel.attributesList(),
                                  this, alias());

      if (path.hasAncestor(Macro.OngoingMacroExpansion.class)) {
        context.type(alias(), t);
        return t;
      } else {
        type = t;
      }
      context.type(alias(), type);
    }
    return type;
  }

  @Override
  protected String trans(Target target, EsqlPath path, PMap<String, Object> parameters) {
    return '(' + select().translate(target, path.add(select()), parameters).translation() + ") \"" + alias() + '"';
  }

  @Override
  public String toString() {
    return alias() + ":(" + select() + ")";
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    st.append(alias()).append(":(");
    select()._toString(st, level, indent);
    st.append(")");
  }

  public Select select() {
    return child("select");
  }

  public String alias() {
    return childValue("alias");
  }

  private transient volatile Selection type;
}