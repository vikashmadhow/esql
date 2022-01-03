/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.semantic.type;

import java.util.UUID;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public record Sequence(UUID    id,
                       String  name,
                       long    start,
                       long    increment,
                       long    maximum,
                       boolean cycles) {}
