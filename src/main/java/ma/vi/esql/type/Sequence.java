/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.esql.type;

import java.util.UUID;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Sequence {
  public Sequence(UUID id, String name, long start,
                  long increment, long maximum, boolean cycles) {
    this.id = id;
    this.name = name;
    this.start = start;
    this.increment = increment;
    this.maximum = maximum;
    this.cycles = cycles;
  }

  public final UUID id;
  public final String name;
  public final long start;
  public final long increment;
  public final long maximum;
  public final boolean cycles;
}
