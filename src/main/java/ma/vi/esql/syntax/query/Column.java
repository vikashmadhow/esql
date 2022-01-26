/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.query;

import ma.vi.base.tuple.T2;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.MetadataContainer;
import ma.vi.esql.syntax.define.Attribute;
import ma.vi.esql.syntax.define.ColumnDefinition;
import ma.vi.esql.syntax.define.DerivedColumnDefinition;
import ma.vi.esql.syntax.define.Metadata;
import ma.vi.esql.syntax.expression.ColumnRef;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.expression.literal.BooleanLiteral;
import org.pcollections.PMap;

import java.util.*;
import java.util.stream.Stream;

import static ma.vi.esql.builder.Attributes.*;
import static ma.vi.esql.semantic.type.Types.UnknownType;
import static ma.vi.esql.translation.Translatable.Target.ESQL;

/**
 * A column in a select statement.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Column extends MetadataContainer<String> { // implements TypedMacro, Symbol {
  @SafeVarargs
  public Column(Context context,
                String name,
                Expression<?, String> expression,
                Type type,
                Metadata metadata,
                T2<String, ? extends Esql<?, ?>>... children) {
    super(context, "Column",
          Stream.concat(
            Stream.of(
              new T2[]{
                T2.of("name",       new Esql<>(context, name != null ? name
                                                         : expression instanceof ColumnRef r ? r.name()
                                                         : null)),
                T2.of("expression", expression),
//                T2.of("type",       new Esql<>(context, type)),
                T2.of("metadata",   addId(metadata))
              }),
            Stream.of(children)).toArray(T2[]::new));
    this.type = type == null ? UnknownType : type;
  }

  public Column(Column other) {
    super(other);
  }

  @SafeVarargs
  public Column(Column other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public Column copy() {
    return new Column(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public Column copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new Column(this, value, children);
  }

  private static Metadata addId(Metadata metadata) {
    if (metadata != null) {
      Map<String, Attribute> attributes = metadata.attributes();
      if (!attributes.containsKey(ID)) {
        attributes.put(ID, Attribute.from(metadata.context, ID, UUID.randomUUID()));
        return new Metadata(metadata.context, new ArrayList<>(attributes.values()));
      }
    }
    return metadata;
  }

  public static Column fromDefinition(ColumnDefinition def, EsqlPath path) {
    boolean derived = def instanceof DerivedColumnDefinition;
    DerivedColumnDefinition derivedDef = derived ? (DerivedColumnDefinition)def : null;

    Type columnType = def.type();
    if (columnType == null) {
      columnType = UnknownType;
    }
    Boolean notNull = def.notNull();
    Expression<?, String> defaultExpr = def.expression();

    Map<String, Attribute> attributes = new HashMap<>();
    if (def.metadata() != null) {
      attributes.putAll(def.metadata().attributes());
    }

    attributes.put(ID, Attribute.from(def.context, ID, UUID.randomUUID()));
    if (derived) {
      attributes.put(DERIVED, Attribute.from(def.context, DERIVED, true));
    } else if (defaultExpr != null) {
      attributes.put(EXPRESSION, new Attribute(def.context, EXPRESSION, defaultExpr));
    }

    // attributes.put(TYPE, Attribute.from(def.context, TYPE, "'" + columnType.translate(ESQL, path) + "'"));
    if (notNull) {
      attributes.put(REQUIRED, Attribute.from(def.context, REQUIRED, true));
    }
    return new Column(def.context,
                      def.name(),
                      derived ? derivedDef.expression() : new ColumnRef(def.context, null, def.name(), columnType),
                      columnType,
                      new Metadata(def.context, new ArrayList<>(attributes.values())));
  }

//  @Override
//  public Esql<?, ?> expand(Esql<?, ?> esql, EsqlPath path) {
//    if (type == UnknownType) {
//      Type t = expression().computeType(path.add(expression()));
//      if (t != UnknownType) {
//        return type(type);
//      }
//    }
//    return esql;
//  }

  @Override
  protected String trans(Target target, EsqlPath path, PMap<String, Object> parameters) {
    StringBuilder st = new StringBuilder();
    if (target == ESQL) {
      if (name() != null) {
        st.append(name()).append(':');
      }
      st.append(expression().translate(target, path.add(expression()), parameters));
      Metadata metadata = metadata();
      if (metadata != null && !metadata.attributes().isEmpty()) {
        st.append(" {");
        boolean first = true;
        for (Attribute attr: metadata.attributes().values()) {
          if (first) { first = false; }
          else       { st.append(", "); }
          st.append(attr.name()).append(':')
            .append(attr.attributeValue().translate(target, path.add(attr.attributeValue()), parameters));
        }
        st.append('}');
      }
    } else {
      st.append(expression().translate(target, path.add(expression()), parameters));
      if (name() != null) {
        st.append(" \"").append(name()).append('"');
      }
    }
    return st.toString();
  }

  public boolean derived() {
    if (name() != null
     && expression() instanceof ColumnRef ref
     && name().equals(ref.name())) {
      return false;

    } else if (metadata() != null) {
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

  public Column notNull(boolean notNull) {
    return setMetadata(REQUIRED, new BooleanLiteral(context, notNull));
  }

  public UUID id() {
    if (metadata() != null && metadata().attribute(ID) != null) {
      return metadata().evaluateAttribute(ID);
    } else {
      return null;
    }
  }

  public Expression<?, String> defaultExpression() {
    if (metadata() != null
     && metadata().attribute(EXPRESSION) != null) {
      return metadata().attribute(EXPRESSION).attributeValue();
    } else {
      return null;
    }
  }

  public Column defaultExpression(Expression<?, String> expr) {
    return setMetadata(EXPRESSION, expr);
  }

  @Override
  public Type computeType(EsqlPath path) {
    if (type == UnknownType) {
      type = expression().computeType(path.add(expression()));
    }
    return type;
  }

  @Override
  public Type type() {
    if (type == UnknownType) {
      type = expression().type();
    }
    return type;
  }

  //  public Column type(Type type) {
//    return set("type", new Esql<>(context, type));
//  }

  private Column setMetadata(String name, Expression<?, String> expr) {
    List<Attribute> attributes = new ArrayList<>();
    if (metadata() != null && metadata().attributes() != null) {
      attributes.addAll(metadata().attributes().values().stream().filter(a -> !a.name().equals(name)).toList());
    }
    attributes.add(new Attribute(context, name, expr));
    return set("metadata", new Metadata(context, attributes));
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    if (name() != null) {
      st.append(name()).append(':');
    }
    expression()._toString(st, level + 1, indent);
    if (metadata() != null && !metadata().attributes().isEmpty()) {
      st.append(' ');
      metadata()._toString(st, level, indent);
    }
    if (type() != UnknownType) {
      st.append(" (").append(type()).append(')');
    }
  }

  public String name() {
    return childValue("name");
  }

  public Expression<?, String> expression() {
    return child("expression");
  }

  public Column expression(Expression<?, String> expression) {
    return set("expression", expression);
  }

  public Column name(String name) {
    return set("name", new Esql<>(context, name));
  }
}
