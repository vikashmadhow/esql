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
import ma.vi.esql.syntax.expression.literal.NullLiteral;

import java.util.*;
import java.util.stream.Stream;

import static ma.vi.esql.builder.Attributes.*;
import static ma.vi.esql.syntax.Translatable.Target.ESQL;

/**
 * A column in a select statement.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Column extends MetadataContainer<String, String> {
  public Column(Context context,
                String alias,
                Expression<?, String> expression,
                Metadata metadata,
                T2<String, ? extends Esql<?, ?>>... children) {
    super(context,
          autoAlias(expression, alias),
          Stream.concat(
            Arrays.stream(
              new T2[]{
                T2.of("expression", new Esql<>(context, expression)),
                T2.of("metadata", metadata)
              }),
            Arrays.stream(children)).toArray(T2[]::new));
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

  /**
   * Compute a unique alias with referenced column name if provided alias is null.
   */
  private static String autoAlias(Expression<?, String> expr, String alias) {
    if (alias == null) {
      if (expr instanceof ColumnRef) {
        return ((ColumnRef)expr).name();
      } else {
        return "__auto_col_" + Strings.random(10);
      }
    }
    return alias;
  }

  public static Column fromDefinition(ColumnDefinition def) {
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
                                        derived ? "'" + derivedDef.expression().type().translate(ESQL) + "'" // Types.VoidType.translate(ESQL)
                                                : "'" + columnType.translate(ESQL) + "'"));
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
      if (alias() != null) {
        st.append(alias()).append(':');
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
      if (alias() != null) {
        st.append(" \"").append(alias()).append('"');
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

//  public void notNull(boolean notNull) {
//    if (metadata() == null) {
//      metadata(new Metadata(context, new ArrayList<>()));
//    }
//    metadata().attribute(REQUIRED, new BooleanLiteral(context, notNull));
//  }

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

//  public void defaultExpression(Expression<?, String> expr) {
//    if (metadata() == null) {
//      metadata(new Metadata(context, new ArrayList<>()));
//    }
//    metadata().attribute(EXPRESSION, expr);
//  }
//
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
  public Type type() {
    if (metadata() != null && metadata().attribute(TYPE) != null) {
      Type type = Types.typeOf((String)metadata().evaluateAttribute(TYPE));
      if (type == Types.VoidType && derived()) {
        /*
         * Derived type are set to void on load. Their actual types can
         * be determined at this point.
         */
        type = expression().type();
        if (type != Types.VoidType) {
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
        }
      }
      return type;
    } else {
      Type type = expression().type();
//      type(type);
      return type;
    }
  }

//  public void type(Type type) {
//    if (metadata() == null) {
//      metadata(new Metadata(context, new ArrayList<>()));
//    }
//    metadata().attribute(TYPE, new StringLiteral(context, type.translate(ESQL)));
//  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    if (alias() != null) {
      st.append(alias()).append(':');
    }
    st.append(expression());
    if (metadata() != null && !metadata().attributes().isEmpty()) {
      metadata()._toString(st, level, indent);
    }
  }

  public String alias() {
    return value;
  }

  public Expression<?, String> expression() {
    return childValue("expression");
  }

//  public void expr(Expression<?, String> expr) {
//    value = expr;
//  }


//  public void alias(String alias) {
//    ((Esql<String, ?>)child("alias")).value = alias;
//  }
}
