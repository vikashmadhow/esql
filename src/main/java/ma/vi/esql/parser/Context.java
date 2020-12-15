/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser;

import ma.vi.esql.type.Type;
import ma.vi.esql.type.Types;
import ma.vi.esql.database.Structure;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Holds information which is passed around to the various Esql objects
 * in an Esql program/statement/expression during macro expansion and
 * translation.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Context {
  public Context(Structure structure) {
    this.structure = structure;
  }

  /**
   * Returns the context-specific (local) type with the specified name, or
   * null if no such type has been added to the context type registry (through
   * the {@link #type(Type)} method).
   */
  public Type type(String name) {
    if (types.containsKey(name)) {
      return types.get(name);
    } else if (structure != null && structure.relationExists(name)) {
      return structure.relation(name);
    } else {
      return Types.typeOf(name);
    }
  }

  /**
   * Adds a context-specific (local) type replacing any existing local type
   * with the same name. Returns the previous known type with that name or
   * null if none.
   */
  public Type type(Type type) {
    return types.put(type.name(), type);
  }

  /**
   * Context-specific types.
   */
  private final ConcurrentMap<String, Type> types = new ConcurrentHashMap<>();

  public final Structure structure;
}
