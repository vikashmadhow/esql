package ma.vi.esql.database.init;

import ma.vi.esql.database.Database;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Initializers creates database structures from a hierarchical representation
 * (such as encoded in a YAML file).
 *
 * @param <T> The type of structures created by this initializer.
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public interface Initializer<T> {
  /**
   * Create or update the structure with the specified name and contents in the
   * database. If overwrite is true the structure is updated if already exists.
   *
   * @param db         The database to create the structure in.
   * @param overwrite  Whether to overwrite the structure if it already exists.
   * @param name       Name of the structure.
   * @param existing   Existing structure with the same name in the database
   *                   or null, if no such structure exists.
   * @param definition Contents of the structure.
   * @return The created structure.
   */
  T add(Database db,
        boolean  overwrite,
        String   name,
        T        existing,
        Map<String, Object> definition);

  default T add(Database     db,
                boolean      overwrite,
                String       name,
                T            existing,
                List<Object> definition) {
    Map<String, Object> def = IntStream.range(0, definition.size())
                                       .boxed()
                                       .collect(Collectors.toMap(String::valueOf,
                                                                 definition::get,
                                                                 (d1, d2) -> d1,
                                                                 LinkedHashMap::new));
    return add(db, overwrite, name, existing, def);
  }

  /**
   * Create or update the structure with the specified name and contents in the
   * database. If overwrite is true the structure is updated if already exists.
   *
   * @param db         The database to create the structure in.
   * @param overwrite  Whether to overwrite the structure if it already exists.
   * @param name       Name of the structure.
   * @param definition Contents of the structure.
   * @return The created structure.
   */
  default T add(Database db,
                boolean  overwrite,
                String   name,
                Map<String, Object> definition) {
    T existing = get(db, name);
    if (overwrite || existing == null) {
      if (definition.containsKey(NAME)) {
        name = (String)definition.get(NAME);
        definition.remove(NAME);
      }
      return add(db, overwrite, name, existing, definition);
    }
    return existing;
  }

  default T add(Database     db,
                boolean      overwrite,
                String       name,
                List<Object> definition) {
    T existing = get(db, name);
    if (overwrite || existing == null) {
      return add(db, overwrite, name, existing, definition);
    }
    return existing;
  }

  /**
   * Returns the structure with the specified name from the database or null if
   * it does not exist.
   *
   * @param db   The database the structure is stored in.
   * @param name The name of the structure to get.
   * @return The structure by the name or null if no such structure exists.
   */
  T get(Database db, String name);

  /**
   * If the definition parameter is of type string, loads the structure from the
   * database with that name. Otherwise, create the structure using the defaultName
   * provided.
   *
   * @param db          The database containing the structures.
   * @param overwrite   Overwrite the structure if already existing.
   * @param definition  The name of the structure to load or the definition of
   *                    the structure to create.
   * @param defaultName A default name for the structure to create.
   * @return The loaded or created structure, or null if the provided definition
   *         is null.
   */
  default T getOrAdd(Database db,
                     boolean  overwrite,
                     Object   definition,
                     String   defaultName) {
    return definition instanceof String name ? get(db, name)
         : definition instanceof Map    map  ? add(db, overwrite, defaultName, (Map<String, Object>)map)
         : definition instanceof List   list ? add(db, overwrite, defaultName, (List<Object>)list)
         : null;
  }

  /**
   * Add a list of objects defined in the provided map.
   *
   * @param db          The database to create the structures in.
   * @param overwrite   Overwrite the structure if already existing.
   * @param definitions The structures to create.
   * @return The list of structures created.
   */
  default List<T> add(Database db,
                      boolean  overwrite,
                      Map<String, Object> definitions) {
    return definitions.entrySet().stream()
                      .map(e -> {
                        if (e.getValue() instanceof Map)
                          return add(db, overwrite, e.getKey(), new LinkedHashMap<>((Map<String, Object>)e.getValue()));
                        else
                          return add(db, overwrite, e.getKey(), new ArrayList<>((List<Object>)e.getValue()));
                      })
                      .toList();
  }

  /**
   * Interprets the data from the input stream as YAML and creates the
   * structures that it defines in the database.
   *
   * @param db The database to create the structures in.
   * @param in Input stream of the YAML data defining the structures to create.
   * @return The list of structures created.
   */
  default List<T> add(Database db, InputStream in) {
    if (in == null) {
      /*
       * No input initialization: all initialization input is already contained
       * in the method. E.g., creation of base tables.
       */
      return Collections.emptyList();
    } else {
      /*
       * Initialize database from YAML input.
       */
      Yaml yaml = new Yaml();
      Iterable<Object> contents = yaml.loadAll(in);
      List<T> created = new ArrayList<>();
      for (Object content : contents) {
        Map<String, Object> entries = new LinkedHashMap<>((Map<String, Object>)content);
        boolean overwrite = entries.containsKey(OVERWRITE)
                         && (Boolean)entries.get(OVERWRITE);
        entries.remove(OVERWRITE);
        created.addAll(add(db, overwrite, entries));
      }
      return created;
    }
  }

  static String param(Map<String, Object> definition,
                      String              name,
                      String              defaultValue) {
    return definition.containsKey(name)
         ? definition.get(name).toString()
         : defaultValue;
  }

  static int intParam(Map<String, Object> definition,
                      String              name,
                      int                 defaultValue) {
    if (definition.containsKey(name)) {
      Object v = definition.get(name);
      try                { return Integer.parseInt(v.toString()); }
      catch(Exception e) { return defaultValue; }
    }
    return defaultValue;
  }

  static boolean boolParam(Map<String, Object> definition,
                           String              name,
                           boolean             defaultValue) {
    if (definition.containsKey(name)) {
      Object v = definition.get(name);
      try                { return Boolean.parseBoolean(v.toString()); }
      catch(Exception e) { return defaultValue; }
    }
    return defaultValue;
  }

  /**
   * The special configuration key which indicates that the context is in
   * overwrite mode; in this mode, the system is allowed to overwrite data
   * during the configuration phase of application launch (e.g. overwrite the
   * password of certain special user account). If absent in the configuration,
   * overwrite of existing data should be prohibited to prevent accidental or
   * malicious changes to system data.
   */
  String OVERWRITE = "$overwrite$";

  /**
   * A special configuration key specifying a name for the current object being
   * created or updated.
   */
  String NAME = "$name";

  /**
   * A special configuration key specifying a display name for the current object
   * being created or updated.
   */
  String DISPLAY_NAME = "$displayName";

  /**
   * A special configuration key specifying a description for the current object
   * being created or updated.
   */
  String DESCRIPTION = "$description";

  /**
   * A special configuration key specifying a set of name-value pairs as metadata
   * for the current object being created or updated.
   */
  String METADATA = "$metadata";

  /**
   * Configuration key to provide a source file to the initializer.
   */
  String SOURCE_FILE = "SOURCE_FILE";
}
