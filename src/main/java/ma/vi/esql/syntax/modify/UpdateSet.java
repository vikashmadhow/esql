/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.modify;

import ma.vi.base.tuple.T2;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.define.Attribute;
import org.pcollections.PMap;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.joining;

/**
 * A list of name-expression pairs used as the set clause of an Update.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.mail)
 */
public class UpdateSet extends Esql<String, String> {
  public UpdateSet(Context context, List<Attribute> sets) {
    super(context, "UpdateSet", sets);
  }

  public UpdateSet(UpdateSet other) {
    super(other);
  }

  @SafeVarargs
  public UpdateSet(UpdateSet other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public UpdateSet copy() {
    return new UpdateSet(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public UpdateSet copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new UpdateSet(this, value, children);
  }

  @Override
  protected String trans(Target               target,
                         EsqlConnection       esqlCon,
                         EsqlPath             path,
                         PMap<String, Object> parameters,
                         Environment          env) {
    return sets().values()
                 .stream()
                 .map(a -> '"' + a.name() + "\"="
                         + (a.attributeValue() == null
                         ?  "null"
                         :  a.attributeValue().translate(target,
                                                         esqlCon,
                                                         path.add(a.attributeValue()),
                                                         parameters,
                                                         env)))
                 .collect(joining(", "));
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    boolean first = true;
    for (Attribute a: sets().values()) {
      if (first) { first = false;   }
      else       { st.append(", "); }
      a._toString(st, level, indent);
    }
  }

  public Map<String, Attribute> sets() {
    return childrenMap();
  }

  public Attribute attribute(String name) {
    return sets().get(name);
  }
}