/*
 * Copyright (c) 2022 Vikash Madhow
 */

package ma.vi.esql.database;

import ma.vi.base.config.Configuration;

import static ma.vi.esql.database.Database.*;

/**
 * Creates and returns a {@link Database} instance of the correct type based
 * on the value {@link Database#CONFIG_DB_SYSTEM} configuration parameter.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class DatabaseFactory {
  /**
   * Creates and returns a {@link Database} instance of the correct type based
   * on the value {@link Database#CONFIG_DB_SYSTEM} configuration parameter. The
   * configuration supplied is passed to the constructor of the Database implementation
   * and must contain all necessary initialization parameters.
   */
  public static Database get(Configuration config) {
    if (config.has(CONFIG_DB_SYSTEM)) {
        return switch((String)config.get(CONFIG_DB_SYSTEM)) {
          case DB_SYSTEM_POSTGRESQL -> new Postgresql(config);
          case DB_SYSTEM_SQLSERVER  -> new SqlServer(config);
          default -> throw new DataException("Unsupported database system: "
                                            + config.get(CONFIG_DB_SYSTEM));
        };
    } else {
      throw new DataException("Targeted database system must be specified in the "
                            + "configuration using the " + CONFIG_DB_SYSTEM + " parameter");
    }
  }
}
