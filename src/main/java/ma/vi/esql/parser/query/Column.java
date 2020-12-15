/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.esql.parser.query;

import ma.vi.base.string.Strings;
import ma.vi.base.tuple.T2;
import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.Esql;
import ma.vi.esql.parser.MetadataContainer;
import ma.vi.esql.parser.define.Attribute;
import ma.vi.esql.parser.define.ColumnDefinition;
import ma.vi.esql.parser.define.DerivedColumnDefinition;
import ma.vi.esql.parser.define.Metadata;
import ma.vi.esql.parser.expression.BooleanLiteral;
import ma.vi.esql.parser.expression.ColumnRef;
import ma.vi.esql.parser.expression.Expression;
import ma.vi.esql.parser.expression.StringLiteral;
import ma.vi.esql.type.Type;
import ma.vi.esql.type.Types;

import java.util.ArrayList;

import static ma.vi.esql.builder.Attributes.*;

/**
 * A column in a select statement.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Column extends MetadataContainer<Expression<?>, String> {

  public Column(Context context,
                String alias,
                Expression<?> expr,
                Metadata metadata) {
    super(context,
          expr,
          T2.of("alias", new Esql<>(context, alias)),
          T2.of("metadata", metadata));

    /*
     * Alias with referenced column name if not already aliased.
     */
    if (alias == null) {
      if (expr instanceof ColumnRef) {
        alias(((ColumnRef)expr).name());
      } else {
        alias("col_" + Strings.random(10));
      }
    }
  }

  public Column(Column other) {
    super(other);
  }

  public static Column fromDefinition(ColumnDefinition def) {
    boolean derived = def instanceof DerivedColumnDefinition;
    DerivedColumnDefinition derivedDef = derived ? (DerivedColumnDefinition)def : null;

    Type columnType = derived ? derivedDef.expression().type() : def.type();
    Boolean notNull = def.notNull();
    Expression<?> defaultExpr = def.defaultExpression();

    Column col = new Column(def.context,
                            def.name(),
                            new ColumnRef(def.context, null, def.name()),
                            def.metadata());
    if (derived) {
      col.attribute(DERIVED, new BooleanLiteral(def.context, true));
      col.attribute(DERIVED_EXPRESSION, derivedDef.expression());
    }
    col.attribute(TYPE, new StringLiteral(def.context, columnType.translate(Target.ESQL)));
    if (notNull) {
      col.attribute(REQUIRED, new BooleanLiteral(def.context, true));
    }
    if (defaultExpr != null) {
      col.attribute(DEFAULT_VALUE, defaultExpr);
    }
    return col;
  }

  @Override
  public Column copy() {
    if (!copying()) {
      try {
        copying(true);
        return new Column(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public String translate(Target target) {
    StringBuilder st = new StringBuilder(expr().translate(target));
    Metadata metadata = metadata();
    if (target == Target.ESQL) {
      if (alias() != null) {
        st.append(' ').append(alias());
      }
      if (metadata != null && !metadata.attributes().isEmpty()) {
        st.append(" {");
        boolean first = true;
        for (Attribute attr: metadata.attributes().values()) {
          if (first) {
            first = false;
          } else {
            st.append(", ");
          }
          st.append(attr.attributeValue().translate(target));
        }
        st.append('}');
      }
      return st.toString();
    }
    if (alias() != null) {
      st.append(' ').append(alias());
    }
    // basic metadata loading
    if (metadata != null && !metadata.attributes().isEmpty()) {
      for (Attribute attr: metadata.attributes().values()) {
        st.append(", ").append(attr.attributeValue().translate(target));
      }
    }
    return st.toString();
  }

  public boolean derived() {
    if (metadata() != null) {
      Boolean derived = metadata().evaluateAttribute(DERIVED);
      return derived != null && derived;
    }
    return false;
  }

  public boolean notNull() {
    return metadata() != null && (Boolean)metadata().evaluateAttribute(REQUIRED);
  }

  public void notNull(boolean notNull) {
    if (metadata() == null) {
      metadata(new Metadata(context, new ArrayList<>()));
    }
    metadata().attribute(REQUIRED, new BooleanLiteral(context, notNull));
  }

  public String id() {
    if (metadata() != null && metadata().attribute(ID) != null) {
      return metadata().evaluateAttribute(ID);
    } else {
      return null;
    }
  }

  public void id(String id) {
    if (metadata() == null) {
      metadata(new Metadata(context, new ArrayList<>()));
    }
    metadata().attribute(ID, id);
  }

  public Expression<?> defaultExpression() {
    if (metadata() != null
     && metadata().attribute(DEFAULT_VALUE) != null) {
      return metadata().attribute(DEFAULT_VALUE).attributeValue();
    } else {
      return null;
    }
  }

  public void defaultExpression(Expression<?> expr) {
    if (metadata() == null) {
      metadata(new Metadata(context, new ArrayList<>()));
    }
    metadata().attribute(DEFAULT_VALUE, expr);
  }

  @Override
  public Type type() {
    if (metadata() != null
        && metadata().attribute(TYPE) != null) {
      return Types.typeOf(((StringLiteral)metadata().attribute(TYPE).attributeValue()).value);
    } else {
      return expr().type();
    }
  }

  public void type(Type type) {
    if (metadata() == null) {
      metadata(new Metadata(context, new ArrayList<>()));
    }
    metadata().attribute(TYPE, new StringLiteral(context, type.translate(Target.ESQL)));
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    if (alias() != null) {
      st.append(alias()).append(':');
    }
    st.append(expr());
    if (metadata() != null && !metadata().attributes().isEmpty()) {
      st.append(" {");
      boolean first = true;
      for (Attribute attr: metadata().attributes().values()) {
        if (first) {
          first = false;
        } else {
          st.append(", ");
        }
        st.append(attr.attributeValue());
      }
      st.append('}');
    }
  }

  public Expression<?> expr() {
    return value;
  }

  public void expr(Expression<?> expr) {
    value = expr;
  }

  public String alias() {
    return childValue("alias");
  }

  public void alias(String alias) {
    ((Esql<String, ?>)child("alias")).value = alias;
  }
}
