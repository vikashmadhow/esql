/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.query;

import ma.vi.base.tuple.T2;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.composable.Composable;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.exec.function.FunctionCall;
import ma.vi.esql.semantic.type.Column;
import ma.vi.esql.semantic.type.Selection;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.ColumnRef;
import ma.vi.esql.syntax.expression.literal.StringLiteral;
import ma.vi.esql.syntax.macro.Macro;
import ma.vi.esql.syntax.macro.UntypedMacro;
import org.pcollections.PMap;

import java.util.List;

import static java.util.Collections.emptyList;
import static ma.vi.base.tuple.T2.of;
import static ma.vi.esql.semantic.type.Types.UnknownType;

/**
 * Represents a table obtained from a function call:
 * <pre>
 *   select t.value from t:call('a,b,c', ',')
 * </pre>
 * The function call must be a macro that expands to a non-function table
 * expression.
 *
 * @author vikash.madhow@gmail.com
 */
public class FunctionTableExpr extends AbstractAliasTableExpr implements UntypedMacro {
  public FunctionTableExpr(Context      context,
                           String       alias,
                           FunctionCall call) {
    super(context, "FunctionTableExpr", alias, of("call", call));
  }

  public FunctionTableExpr(FunctionTableExpr other) {
    super(other);
  }

  @SafeVarargs
  public FunctionTableExpr(FunctionTableExpr other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public FunctionTableExpr copy() {
    return new FunctionTableExpr(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public FunctionTableExpr copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new FunctionTableExpr(this, value, children);
  }

  @Override
  public Esql<?, ?> expand(Esql<?, ?> esql, EsqlPath path) {
    FunctionCall c = call();
    Esql<?, ?> e = ((Macro)c).expand(c, path.add(c));
    return e instanceof Select           s ? new SelectTableExpr (context, s, alias())
         : e instanceof SelectTableExpr  s ? new SelectTableExpr (context, s.select(), alias())
         : e instanceof StringLiteral    s ? new SingleTableExpr (context, alias(), s.value)
         : e instanceof SingleTableExpr  s ? new SingleTableExpr (context, alias(), s.tableName())
         : e instanceof DynamicTableExpr s ? new DynamicTableExpr(context, alias(), s.metadata(), s.columns(), s.rows())
         :                                   e;
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
    return emptyList();
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
    return alias() + ':' + call().translate(target, esqlCon, path.add(call()), parameters, env);
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    st.append(alias()).append(':');
    call()._toString(st, level, indent);
  }

  public String alias() {
    return childValue("alias");
  }

  public FunctionCall call() {
    return child("call");
  }
}