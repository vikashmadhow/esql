package ma.vi.esql.database.init;

import ma.vi.base.lang.NotFoundException;
import ma.vi.esql.database.Database;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.QueryParams;
import ma.vi.esql.exec.Result;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.CRC32C;

import static java.lang.System.Logger.Level.INFO;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.IntStream.range;

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
    return add(db, overwrite, name, (UUID)null, definition);
  }

  default T add(Database db,
                boolean  overwrite,
                String   name,
                UUID     resourceId,
                Map<String, Object> definition) {
    if (changed(db, name, resourceId, definition)) {
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
    return null;
  }

  default T add(Database     db,
                boolean      overwrite,
                String       name,
                T            existing,
                List<Object> definition) {
    Map<String, Object> def = range(0, definition.size())
                                .boxed()
                                .collect(toMap(String::valueOf,
                                               definition::get,
                                               (d1, d2) -> d1,
                                               LinkedHashMap::new));
    return add(db, overwrite, name, existing, def);
  }

  default T add(Database     db,
                boolean      overwrite,
                String       name,
                List<Object> definition) {
    return add(db, overwrite, name, (UUID)null, definition);
  }

  default T add(Database     db,
                boolean      overwrite,
                String       name,
                UUID         resourceId,
                List<Object> definition) {
    if (changed(db, name, resourceId, definition)) {
      T existing = get(db, name);
      if (overwrite || existing == null) {
        return add(db, overwrite, name, existing, definition);
      }
      return existing;
    }
    return null;
  }

  private boolean changed(Database db,
                          String   name,
                          UUID     resourceId,
                          Object   definition) {
    boolean changed = true;
    if (resourceId != null) {
      String fingerprint = String.valueOf(definition.hashCode());
      try (EsqlConnection con = db.esql();
           Result rs = con.exec("""
                                select _id, fingerprint
                                  from _core.resource_entry
                                 where resource_id = @resourceId
                                   and name        = @name""",
                                new QueryParams().add("resourceId", resourceId)
                                                 .add("name",       name))) {
        if (rs.toNext()) {
          /*
           * Existing entry: check fingerprint
           */
          String previousFingerprint = rs.value("fingerprint");
          if (!fingerprint.equals(previousFingerprint)) {
            /*
             * Change
             */
            log.log(INFO, "Change detected in " + name);
            con.exec("""
                     update e
                       from e:_core.resource_entry
                        set fingerprint = @fingerprint
                      where _id = @entryId""",
                     new QueryParams().add("entryId",     rs.value("_id"))
                                      .add("fingerprint", fingerprint));
          } else {
            /*
             * No change: skip.
             */
            changed = false;
            log.log(INFO, "Skipping " + name + ": no change");
          }
        } else {
          /*
           * New entry
           */
          log.log(INFO, "New entry " + name);
          con.exec("""
                   insert into _core.resource_entry(_id,     resource_id, name,  fingerprint)
                                             values(newid(), @resourceId, @name, @fingerprint)""",
                   new QueryParams().add("resourceId",  resourceId)
                                    .add("name",        name)
                                    .add("fingerprint", fingerprint));
        }
      }
    }
    return changed;
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
                      UUID     resourceId,
                      Map<String, Object> definitions) {
    return definitions.entrySet().stream()
                      .map(e -> e.getValue() instanceof Map m
                              ? add(db, overwrite, String.valueOf(e.getKey()), resourceId, new LinkedHashMap<String, Object>(m))
                              : add(db, overwrite, String.valueOf(e.getKey()), resourceId, new ArrayList<>((List<Object>)e.getValue())))
                      .toList();
  }

  /**
   * Initialise using data in the YAML resource file and creates the  structures
   * that it defines in the database.
   *
   * @param db The database to create the structures in.
   * @param resource YAML resource file defining the structures to create.
   * @return The list of structures created.
   */
  default List<T> add(Database db, String resource) {
    URL url = getClass().getResource(resource);
    if (url == null) {
      throw new NotFoundException("Could not open " + resource + " from " + getClass());
    }
    try (InputStream in = url.openStream()) {
      return add(db, resource, in);
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
  }

  /**
   * Interprets the data from the input stream as YAML and creates the structures
   * that it defines in the database.
   *
   * @param db The database to create the structures in.
   * @param in Input stream of the YAML data defining the structures to create.
   * @return The list of structures created.
   */
  default List<T> add(Database    db,
                      String      resource,
                      InputStream in) {
    if (in == null) {
      /*
       * No input initialization: all initialization input is already contained
       * in the method. E.g., creation of base tables.
       */
      init(db);
      return emptyList();

    } else if (resource == null) {
      /*
       * Initialize database from YAML input stream.
       */
      Yaml yaml = new Yaml();
      Iterable<Object> contents = yaml.loadAll(in);
      List<T> created = new ArrayList<>();
      for (Object content: contents) {
        Map<String, Object> entries = new LinkedHashMap<>((Map<String, Object>)content);
        boolean overwrite = entries.containsKey(OVERWRITE)
                         && (Boolean)entries.get(OVERWRITE);
        entries.remove(OVERWRITE);
        created.addAll(add(db, overwrite, (UUID)null, entries));
      }
      return created;

    } else {
      /*
       * Both a resource name and input stream was provided: compare with previous
       * changes and initialise incrementally.
       */
      try (EsqlConnection con = db.esql();
           Result rs = con.exec("""
                                select _id, fingerprint
                                  from _core.resource
                                 where name = @name""",
                                new QueryParams().add("name", resource))) {
        UUID resourceId;
        String fingerprint;
        boolean changed = false;
        URL url = getClass().getResource(resource);
        if (rs.toNext()) {
          /*
           * Resource was seen before, check fingerprint.
           */
          resourceId = rs.value("_id");
          fingerprint = getFingerprint(resource, url);

          if (!fingerprint.equals(rs.value("fingerprint"))) {
            /*
             * Resource has changed, update fingerprint.
             */
            log.log(INFO, "Change detected in " + resource);
            changed = true;
            con.exec("""
                     update r
                       from r:_core.resource
                        set fingerprint = @fingerprint
                      where _id = @id""",
                        new QueryParams().add("id",          resourceId)
                                         .add("fingerprint", fingerprint));
          }
        } else {
          /*
           * First time seeing resource.
           */
          changed = true;
          resourceId = UUID.randomUUID();
          fingerprint = getFingerprint(resource, url);
          con.exec("""
                     insert into _core.resource(_id, name,  fingerprint)
                                         values(@id, @name, @fingerprint)""",
                   new QueryParams().add("id",          resourceId)
                                    .add("name",        resource)
                                    .add("fingerprint", fingerprint));
        }
        if (changed) {
          /*
           * Initialize database from YAML input stream.
           */
          Yaml yaml = new Yaml();
          Iterable<Object> contents = yaml.loadAll(in);
          List<T> created = new ArrayList<>();
          for (Object content: contents) {
            Map<String, Object> entries = new LinkedHashMap<>((Map<String, Object>)content);
            boolean overwrite = entries.containsKey(OVERWRITE)
                             && (Boolean)entries.get(OVERWRITE);
            entries.remove(OVERWRITE);
            created.addAll(add(db, overwrite, resourceId, entries));
          }
          return created;

        } else {
          log.log(INFO, "No change detected in " + resource);
          return emptyList();
        }
      }
    }
  }

  private String getFingerprint(String resource, URL url) {
    if (url == null) {
      throw new NotFoundException("Could not open " + resource + " from " + getClass());
    }
    String location = url.getFile();
    String fingerprint;
    int pos = location.indexOf('!');
    if (pos != -1) {
      /*
       * The resource is part of a jar file. Use jar entries information to
       * detect changes.
       */
      location = location.substring(0, pos);
      pos = location.indexOf("file:/");
      if (pos != -1) {
        location = location.substring(pos + 6);
      }
      try (JarFile jar = new JarFile(new File(location).getAbsolutePath())) {
        JarEntry entry = jar.getJarEntry(resource.startsWith("/") ? resource.substring(1) : resource);
        fingerprint = String.valueOf(entry.getCrc());
      } catch (IOException ioe) {
        throw new RuntimeException(ioe);
      }
    } else {
      /*
       * The resource is part of a jar file. Use jar entries information to
       * detect changes.
       */
      try (FileInputStream urlIn = new FileInputStream(location)) {
        CRC32C crc = new CRC32C();
        byte[] data = new byte[16384];
        int read;
        while ((read = urlIn.read(data)) != -1) {
          crc.update(data, 0, read);
        }
        fingerprint = String.valueOf(crc.getValue());
      } catch(IOException ioe) {
        throw new RuntimeException(ioe);
      }
    }
    return fingerprint;
  }

  /**
   * No input initialization: all initialization input is already contained
   * in the method. E.g., creation of base tables.
   * @param db Database to initialise.
   */
  default void init(Database db) {}

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
  String OVERWRITE = "$overwrite";

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

  System.Logger log = System.getLogger(Initializer.class.getName());
}
