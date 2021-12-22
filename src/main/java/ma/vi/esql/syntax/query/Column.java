/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.query;

import ma.vi.base.string.Strings;
import ma.vi.base.tuple.T2;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.semantic.type.Types;
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
import ma.vi.esql.syntax.expression.literal.StringLiteral;

import java.util.*;
import java.util.stream.Stream;

import static ma.vi.esql.builder.Attributes.*;
import static ma.vi.esql.syntax.Translatable.Target.ESQL;

/**
 * A column in a select statement.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Column extends MetadataContainer<String> {
  public Column(Context context,
                String name,
                Expression<?, String> expression,
                Metadata metadata,
                T2<String, ? extends Esql<?, ?>>... children) {
    super(context, "Column",
          Stream.concat(
            Stream.of(
              new T2[]{
                T2.of("name", new Esql<>(context, name != null ? name
                                                   : expression instanceof ColumnRef r ? r.name()
                                                   : null)),
//                T2.of("name", new Esql<>(context, autoName(expression, name))),
                T2.of("expression", expression),
                T2.of("metadata", addId(metadata))
              }),
            Stream.of(children)).toArray(T2[]::new));
  }

  public Column(Column other) {
    super(other);
  }

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

  /**
   * Compute a unique name with referenced column name if provided name is null.
   */
  private static String autoName(Expression<?, String> expr, String name) {
    if (name == null) {
      if (expr instanceof ColumnRef) {
        return ((ColumnRef)expr).name();
      } else {
        return "__auto_col_" + Strings.random(10);
      }
    }
    return name;
  }

  public static Column fromDefinition(ColumnDefinition def, EsqlPath path) {
    boolean derived = def instanceof DerivedColumnDefinition;
    DerivedColumnDefinition derivedDef = derived ? (DerivedColumnDefinition)def : null;

    Type columnType = null;
    if (!derived) {
      columnType = def.type();
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
    attributes.put(TYPE, Attribute.from(def.context, TYPE,
                                        derived ? "'" + derivedDef.expression().type(path.add(derivedDef.expression())).translate(ESQL, path.add(derivedDef.expression())) + "'" // Types.VoidType.translate(ESQL)
                                                : "'" + columnType.translate(ESQL, path) + "'"));
    if (notNull) {
      attributes.put(REQUIRED, Attribute.from(def.context, REQUIRED, true));
    }

    return new Column(def.context,
                      def.name(),
                      derived ? derivedDef.expression() : new ColumnRef(def.context, null, def.name()),
                      new Metadata(def.context, new ArrayList<>(attributes.values())));
  }

  @Override
  protected String trans(Target target, EsqlPath path, Map<String, Object> parameters) {
    if (target == ESQL) {
      StringBuilder st = new StringBuilder();
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
      return st.toString();

    } else {
      StringBuilder st = new StringBuilder(expression().translate(target, path.add(expression()), parameters));
      if (name() != null) {
        st.append(" \"").append(name()).append('"');
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

  public Column notNull(boolean notNull) {
//    if (metadata() == null) {
//      metadata(new Metadata(context, new ArrayList<>()));
//    }
//    metadata().attribute(REQUIRED, new BooleanLiteral(context, notNull));
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

//  @Override
//  public void attribute(String name, Expression<?, String> value) {
//    if (metadata() == null) {
//      metadata(new Metadata(context, new ArrayList<>()));
//    }
//    if (value != null && parent != null && parent.value instanceof BaseRelation) {
//      value = ((BaseRelation)parent.value).expandDerived(value,name, new HashSet<>());
//    }
//    metadata().attribute(name, value);
//  }

  @Override
  public Type type(EsqlPath path) {
    if (metadata() != null && metadata().attribute(TYPE) != null) {
      Type type = Types.typeOf((String)metadata().evaluateAttribute(TYPE));
      if (type == Types.VoidType && derived()) {
        /*
         * Derived type are set to void on load. Their actual types can
         * be determined at this point.
         */
        type = expression().type(path.add(expression()));
//        if (type != Types.VoidType) {
//          type(type);

          /*
           * Transfer the type information to base table if possible.
           */
//          QueryUpdate query = ancestor(QueryUpdate.class);
//          if (query != null && query.tables() instanceof SingleTableExpr) {
//            SingleTableExpr table = (SingleTableExpr)query.tables();
//            if (context.structure.relationExists(table.tableName())) {
//              BaseRelation rel = context.structure.relation(table.tableName());
//              if (rel.findColumn(null, alias()) != null) {
//                Column column = rel.column(null, alias());
//                column.type(type);
//              }
//            }
//          }
//        }
      }
      return type;
    } else if (derived() || !(expression() instanceof ColumnRef)) {
      return expression().type(path.add(expression()));
    } else {
      return Types.UnknownType;
    }
  }

  public Column type(Type type) {
    return setMetadata(TYPE, new StringLiteral(context, type.translate(ESQL)));
  }

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
    st.append(expression());
    if (metadata() != null && !metadata().attributes().isEmpty()) {
      metadata()._toString(st, level, indent);
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
