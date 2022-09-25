/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.exec;

import ma.vi.esql.semantic.type.Column;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.syntax.query.AttributeIndex;

import java.util.List;
import java.util.Map;

/**
 * Maps a query column (in a select, e.g.) to the column index in the resultset,
 * its type, and the indices of its metadata attributes in the resultset. Any
 * attributes which be pre-calculated is also added to the column mapping.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public record ColumnMapping(int                  valueIndex,
                            Column               column,
                            Type                 valueType,
                            List<AttributeIndex> attributeIndices,
                            Map<String, Object>  attributes) {}
