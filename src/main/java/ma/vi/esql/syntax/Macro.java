/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax;

/**
 * A macro is a part of an Esql statement which is expanded after parsing but
 * before translation. Macros can make changes to the whole statement tree, if
 * needed. They are used to expand all-columns (*), column references, special
 * functions, etc.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public interface Macro {
  /**
   * Expands or make any changes to the ESQL node implementing this interface.
   *
   * @param esql The ESQL node that this macro is to expand. This will be, in most
   *             cases, the macro instance itself. In some cases (such as for
   *             {@link ma.vi.esql.syntax.expression.FunctionCall}) the ESQL node
   *             delegates the expansion to another macro class. In such cases
   *             this parameter will be set to the source ESQL statement delegating
   *             to this macro.
   *
   * @param path The ESQL path from the root node of the ESQL program to this Macro
   *             node.
   *
   * @return The ESQL node to replace this macro node with; this can be the macro
   *         node itself, in which case no replacement is made.
   */
  Esql<?, ?> expand(Esql<?, ?> esql, EsqlPath path);
}
