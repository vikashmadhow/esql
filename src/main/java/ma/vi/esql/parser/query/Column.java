/*
 * Copyright (c) 2020 Vikash Madhow
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
import java.util.UUID;

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
        alias("__auto_col_" + Strings.random(10));
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
    Expression<?> defaultExpr = def.expression();

    Column col = new Column(def.context,
                            def.name(),
                            derived ? derivedDef.expression() : new ColumnRef(def.context, null, def.name()),
                            def.metadata());
    col.id(UUID.randomUUID());

    if (derived) {
      col.attribute(DERIVED, new BooleanLiteral(def.context, true));
    } else if (defaultExpr != null) {
      col.attribute(EXPRESSION, defaultExpr);
    }
    col.attribute(TYPE, new StringLiteral(def.context, "'" + columnType.translate(Target.ESQL) + "'"));
    if (notNull) {
      col.attribute(REQUIRED, new BooleanLiteral(def.context, true));
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
    if (target == Target.ESQL) {
      StringBuilder st = new StringBuilder();
      if (alias() != null) {
        st.append(alias()).append(':');
      }
      st.append(expr().translate(target));
      Metadata metadata = metadata();
      if (metadata != null && !metadata.attributes().isEmpty()) {
        st.append(" {");
        boolean first = true;
        for (Attribute attr: metadata.attributes().values()) {
          if (first) { first = false; }
          else       { st.append(", "); }
          st.append(attr.attributeValue().translate(target));
        }
        st.append('}');
      }
      return st.toString();

    } else {
      StringBuilder st = new StringBuilder(expr().translate(target));
      if (alias() != null) {
        st.append(' ').append(alias());
      }
      return st.toString();
    }
  }

  public boolean derived() {
    if (metadata() != null) {
      Boolean derived = metadata().evaluateAttribute(DERIVED);
      return derived != null && derived;
    }
    return false;
  }

  public boolean notNull() {
    if (metadata() != null) {
      Object notNull = metadata().evaluateAttribute(REQUIRED);
      return notNull instanceof Boolean && (Boolean)notNull;
    }
    return false;
  }

  public void notNull(boolean notNull) {
    if (metadata() == null) {
      metadata(new Metadata(context, new ArrayList<>()));
    }
    metadata().attribute(REQUIRED, new BooleanLiteral(context, notNull));
  }

  public UUID id() {
    if (metadata() != null && metadata().attribute(ID) != null) {
      return metadata().evaluateAttribute(ID);
    } else {
      return null;
    }
  }

  public void id(UUID id) {
    if (metadata() == null) {
      metadata(new Metadata(context, new ArrayList<>()));
    }
    metadata().attribute(ID, id);
  }

  public Expression<?> defaultExpression() {
    if (metadata() != null
     && metadata().attribute(EXPRESSION) != null) {
      return metadata().attribute(EXPRESSION).attributeValue();
    } else {
      return null;
    }
  }

  public void defaultExpression(Expression<?> expr) {
    if (metadata() == null) {
      metadata(new Metadata(context, new ArrayList<>()));
    }
    metadata().attribute(EXPRESSION, expr);
  }

  @Override
  public Type type() {
    if (metadata() != null
        && metadata().attribute(TYPE) != null) {
      return Types.typeOf((String)metadata().evaluateAttribute(TYPE));
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
      metadata()._toString(st, level, indent);
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
