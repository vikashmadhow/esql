/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.semantic.type;

import ma.vi.base.tuple.T2;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.semantic.scope.Symbol;
import ma.vi.esql.syntax.Copy;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.define.Attribute;
import ma.vi.esql.translation.Translatable;
import org.pcollections.PMap;

import java.util.*;

import static ma.vi.esql.translation.Translatable.Target.*;

/**
 * Type information on various structures stored in the database
 * including the types themselves.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public interface Type extends Symbol, Copy<Type>, Translatable<String> {
  /**
   * The name of the type.
   */
  String name();

  @Override
  default Type type() {
    return this;
  }

  /**
   * Returns a list of types which are compatible with this type. Operations
   * on compatible types does not require an explicit cast.
   * @return List of compatible types. Default implementation returns a list
   *         with this type as the only compatible type to itself.
   */
  default Set<Type> compatibleTypes() {
    return Collections.singleton(this);
  }

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
  Map<String, Attribute> attributes();

  default List<Attribute> attributesList() {
    return new ArrayList<>(attributes().values());
  }

  /**
   * Returns a copy of this type which can be modified without impacting
   * core structures.
   */
  Type copy();

  /**
   * Abstract types represent concepts which cannot be directly acted upon;
   * these includes the NullType, VoidType, TopType and so on. In contrast
   * a non-abstract type represents a concrete thing such as an Integer, a
   * Date, etc.
   */
  boolean isAbstract();

  /**
   * Returns the promotion of this type. A promoted type is another type representing
   * a superset of the values of the original type. For example, the 'long' type
   * is the promotion of the 'int' type as it can represent all int values as well
   * as longer bit-size values. By default, the promotion of a type is itself.
   */
  default Type promote() {
    return this;
  }

  @Override
  default String translate(Target target) {
    return name();
  }

  @Override
  default String translate(Target target, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env) {
    return name();
  }

  default Attribute attribute(Attribute attr) {
    return attributes().put(attr.name(), attr);
  }

  default Attribute attribute(String name) {
    return attributes().get(name);
  }

  default void attributes(Collection<Attribute> attributes) {
    for (Attribute a: attributes) attribute(a);
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

  class InternalType implements Type {
    protected InternalType(String name) {
      this.name = name;
    }

    @Override
    public String name() {
      return name;
    }

    @Override
    public Type copy() {
      return this;
    }

    @Override
    public void name(String name) {}

    @Override
    public Kind kind() {
      return Kind.BASE;
    }

    @Override
    public Map<String, Attribute> attributes() {
      return null;
    }

    @Override
    public boolean isAbstract() {
      return true;
    }

    private final String name;
  }

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
    if (target == MARIADB || target == MYSQL) {
      return '"' + (q.a != null ? q.a + '.' : "") + q.b + '"';

    } else {
      StringBuilder st = new StringBuilder();
      if      (q.a != null)          { st.append('"').append(q.a).append("\"."); }
      else if (target == HSQLDB)     { st.append("\"PUBLIC\"."); }
      else if (target == POSTGRESQL) { st.append("\"public\"."); }
      else if (target == SQLSERVER)  { st.append("\"DBO\"."); }
      st.append('"').append(q.b).append('"');
      return st.toString();
    }
  }

  /**
   * Returns the ESQL table name for the database schema and table name supplied.
   */
  static String esqlTableName(String schema, String table, Target target) {
    return schema.equalsIgnoreCase("public") || schema.equalsIgnoreCase("dbo")
         ? table
         : schema + '.' + table;
  }
}