/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.query;

import ma.vi.base.tuple.T2;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.composable.Composable;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.semantic.type.Column;
import ma.vi.esql.semantic.type.Selection;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.ColumnRef;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.macro.Macro;
import ma.vi.esql.translation.TranslationException;
import org.pcollections.PMap;

import java.util.List;

import static ma.vi.base.tuple.T2.of;
import static ma.vi.esql.semantic.type.Types.UnknownType;

/**
 * Represents a table created from a `string_split` call:
 * <pre>
 *   select t.value from t:string_split('a,b,c', ',')
 * </pre>
 *
 * @author vikash.madhow@gmail.com
 */
public class StringSplitTableExpr extends AbstractAliasTableExpr {
  public StringSplitTableExpr(Context                     context,
                              String                      alias,
                              boolean                     distinct,
                              List<Expression<?, String>> distinctOn,
                              List<Expression<?, ?>>      arguments) {
    super(context, "StringSplitTable",
          alias,
          of("distinct",   new Esql<>(context, distinct)),
          of("distinctOn", new Esql<>(context, "distinctOn", distinctOn)),
          of("arguments",  new Esql<>(context, "arguments",  arguments)));
    if (arguments.size() < 2) {
      throw new TranslationException("string_split needs 2 arguments: a string to split and the character to split around");
    }
  }

  public StringSplitTableExpr(StringSplitTableExpr other) {
    super(other);
  }

  @SafeVarargs
  public StringSplitTableExpr(StringSplitTableExpr other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public StringSplitTableExpr copy() {
    return new StringSplitTableExpr(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public StringSplitTableExpr copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new StringSplitTableExpr(this, value, children);
  }

  @Override
  public ShortestPath findShortestPath(Composable composable) {
    return null;
  }

  @Override
  public AppliedShortestPath applyShortestPath(ShortestPath shortest, TableExpr root) {
    return null;
  }

  @Override
  public boolean exists(EsqlPath path) {
    return true;
  }

  @Override
  public TableExpr aliased(String name) {
    return name == null || name.equals(alias()) ? this : null;
  }

  @Override
  public TableExpr table(String name) {
    return null;
  }

  @Override
  public ColumnRef findColumn(String table, String name) {
    return null;
  }

  @Override
  public List<Column> columnList(EsqlPath path) {
    return List.of(new Column(context,
                              "value",
                              new ColumnRef(context, alias(), "value"),
                              Types.StringType,
                              null));
  }

  @Override
  public Selection computeType(EsqlPath path) {
    if (type == UnknownType) {
      Selection selection = new Selection(columnList(path), null, this, alias());
      if (path.hasAncestor(Macro.OngoingMacroExpansion.class)) {
        context.type(alias(), selection);
        return selection;
      } else {
        type = selection;
        context.type(alias(), type);
      }
    }
    return (Selection)type;
  }

  @Override
  protected String trans(Target               target,
                         EsqlConnection       esqlCon,
                         EsqlPath             path,
                         PMap<String, Object> parameters,
                         Environment          env) {
    String text = arguments().get(0).translate(target, esqlCon, path, parameters, env).toString();
    String sep  = arguments().get(1).translate(target, esqlCon, path, parameters, env).toString();
    if (target == Target.ESQL){
      return alias() + ":string_split(" + text + ", " + sep + ")";
    } else {
      String split = target == Target.SQLSERVER
                   ? "string_split(" + text + ", " + sep + ") " + alias()
                   : "unnest(string_to_array(" + text + ", " + sep + ")) " + alias() + "(value)";
      if (distinct()) {
        return "(select distinct value from " + split + ") " + alias();
      } else {
        return split;
      }
    }
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    st.append(alias()).append(":string_split(");
    arguments().get(0)._toString(st, level + 1, indent);
    st.append(", ");
    arguments().get(1)._toString(st, level + 1, indent);
    st.append(")");
  }

  public String alias() {
    return childValue("alias");
  }

  public boolean distinct() {
    return childValue("distinct");
  }

  public List<Expression<?, String>> distinctOn() {
    return child("distinctOn").children();
  }

  public List<Expression<?, ?>> arguments() {
    return child("arguments").children();
  }
}