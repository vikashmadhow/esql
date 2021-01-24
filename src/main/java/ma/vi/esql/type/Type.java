/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.type;

import ma.vi.base.tuple.T2;
import ma.vi.esql.parser.Close;
import ma.vi.esql.parser.Copy;
import ma.vi.esql.parser.Translatable;
import ma.vi.esql.parser.expression.Expression;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * Type information on various structures stored in the database
 * including the types themselves.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public interface Type extends Close, Copy<Type>, Translatable<String> {
  /**
   * The name of the type.
   */
  String name();

  /**
   * Allows for the name to be changed; useful in some circumstances when
   * deriving types from other types (as for select projection where the fields
   * in a table expression is projected to the select column expressions).
   */
  void name(String name);

  /**
   * The kind of type: base type (primitives), array types or composites.
   */
  Kind kind();

  /**
   * Type attributes.
   */
  ConcurrentMap<String, Expression<?>> attributes();

  /**
   * Returns a copy of this type which can be modified without impacting
   * core structures.
   */
  Type copy();

  /*
   * Abstract types represent concepts which cannot be directly acted upon;
   * these includes the NullType, VoidType, TopType and so on. In contrast
   * a non-abstract type represents a concrete thing such as an Integer, a
   * Date, etc.
   */
  boolean isAbstract();

  /**
   * The kind of types.
   */
  enum Kind {
    BASE, ARRAY, FIELD, COMPOSITE
  }

  @Override
  default void close() {
  }

  @Override
  default String translate(Target target, Map<String, Object> parameters) {
    return name();
  }

  default Expression<?> attribute(String name, Expression<?> value) {
    return attributes().put(name, value);
  }

  default Expression<?> attribute(String name) {
    return attributes().get(name);
  }

  default void attributes(Map<String, Expression<?>> attributes) {
    for (Map.Entry<String, Expression<?>> a: attributes.entrySet()) {
      attribute(a.getKey(), a.getValue());
    }
  }

  default void clearAttributes() {
    attributes().clear();
  }

  /**
   * Returns an array tableType of this component tableType.
   */
  default ArrayType array() {
    return new ArrayType(this);
  }

  Type Void = new Type() {
    @Override
    public String name() {
      return "Void";
    }

    @Override
    public Type copy() {
      return this;
    }

    @Override
    public boolean copying() {
      return false;
    }

    @Override
    public void copying(boolean copying) {
    }

    @Override
    public void name(String name) {
    }

    @Override
    public Kind kind() {
      return Kind.BASE;
    }

    @Override
    public ConcurrentMap<String, Expression<?>> attributes() {
      return null;
    }

    @Override
    public void close() {
    }

    @Override
    public boolean closing() {
      return false;
    }

    @Override
    public void closing(boolean closing) {
    }

    @Override
    public boolean closed() {
      return false;
    }

    @Override
    public void closed(boolean closed) {
    }

    @Override
    public boolean isAbstract() {
      return true;
    }
  };

  /**
   * Returns the schema from a fully qualified name. A fully qualified name
   * consists of name elements separated by '.'. The schema is all the '.'-separated
   * name elements except for the last one. E.g. the schema for 'a.b.c' is 'a.b'
   * and the schema for 'a' is null.
   */
  static String schema(String name) {
    return splitName(name).a;
  }

  /**
   * Returns the unqualified name from a fully qualified name. A fully qualified name
   * consists of name elements separated by '.'. The unqualified name the last name element
   * in such a name. E.g. the unqualified name for 'a.b.c' is 'c' and for 'a' is 'a'.
   */
  static String unqualifiedName(String name) {
    return splitName(name).b;
  }

  /**
   * Splits a qualified name into a schema and an unqualified name. The schema may be
   * null when the qualified name equals the unqualified name. E.g. 'a.b.c' is split
   * into ['a.b', 'c'] while 'a' is split into [null, 'a'].
   */
  static T2<String, String> splitName(String name) {
    int pos = name.lastIndexOf('.');
    return pos != -1 ? T2.of(name.substring(0, pos), name.substring(pos + 1))
                     : T2.of(null, name);
  }

  /**
   * In Esql, a table name consists of one or more dot (.) separated names,
   * which is then translated into an actual database name with the last
   * dot-separated part of the Esql name used as actual table name and all the
   * previous ones grouped together as the schema. E.g. 'a.b.c' is mapped to
   * the table names "a.b"."c" (a.b is the schema and c is the table in this
   * schema), while 'a' is simply mapped to "a".
   */
  static String dbTableName(String name, Target target) {
    T2<String, String> q = Type.splitName(name);
    StringBuilder st = new StringBuilder();
    if (q.a != null) {
      st.append('"').append(q.a).append("\".");

    } else if (target == Target.HSQLDB) {
      st.append("\"PUBLIC\".");

    } else if (target == Target.POSTGRESQL) {
      st.append("\"public\".");

    } else if (target == Target.SQLSERVER) {
      st.append("\"DBO\".");
    }
    st.append('"').append(q.b).append('"');
    return st.toString();
  }

  /**
   * Returns the ESQL table name for the database schema and table name supplied.
   */
  static String esqlTableName(String schema, String table, Target target) {
    return schema.equalsIgnoreCase("public") || schema.equalsIgnoreCase("dbo")
              ? table : schema + '.' + table;
  }
}