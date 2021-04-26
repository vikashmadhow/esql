/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax;

/**
 * A macro is a part of an Esql statement which is expanded after parsing
 * but before translation. Macros can make changes to the whole statement
 * tree, if needed. Macros are used to expand all-columns (*), column references,
 * special functions, etc.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public interface Macro extends Comparable<Macro> {

  /**
   * Determines the order of expansion of macros with
   * lower order values expanded first.
   */
  default int expansionOrder() {
    return Math.abs(System.identityHashCode(this));
  }

  /**
   * Expands or make any changes to an ESQL statement that this macro is
   * part of or for which this macro has been asked to process.
   *
   * @param name The name of this macro in the children map of its parent
   *             syntactic node.
   * @param esql The part of the statement that this macro is to expand. This
   *             will be, in most cases, the macro instance itself. In some
   *             cases (such as for {@link ma.vi.esql.syntax.expression.FunctionCall})
   *             the ESQL part delegates the expansion to another macro class.
   *             In such cases this parameter will be set to the source ESQL
   *             statement delegating to this macro.
   * @return true if changes were made to the statement as part of the expansion.
   * In such cases, the changes themselves should be checked and subjected,
   * if necessary, to further expansions. If false, no expansions were and
   * this branch of the statement tree can be safely ignored during further
   * macro expansion,
   */
  boolean expand(String name, Esql<?, ?> esql);

  /**
   * Macros are naturally ordered by their expansion order
   * from smallest value (highest priority) to largest value
   * (lowest priority).
   */
  @Override
  default int compareTo(Macro other) {
    return expansionOrder() - other.expansionOrder();
  }

  // Some macro expansion orders:
  ////////////////////////////////////

  int HIGHEST = 1000;
  int VERY_HIGH = 2000;
  int HIGH = 3000;
  int MEDIUM = 4000;
  int LOW = 5000;
  int VERY_LOW = 6000;
  int LOWEST = 7000;
}
