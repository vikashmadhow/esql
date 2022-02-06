/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.macro;

import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.exec.function.FunctionCall;

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
   *             {@link FunctionCall}) the ESQL node
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

  /**
   * A tagging class set in the path when macro expansion is ongoing. Downstream
   * processing can detect the presence or absence of an instance of this class
   * in the current exploring path and modify their behaviour consequently.
   *
   * One place where this is used is type inference during macro expansion. Types
   * can be requested before macros in the ESQL has been expanded. In that state
   * column names might be null and context information might be incomplete. This
   * pre-typing phase is required to resolve columns in dynamically generated
   * tables, for example. In this state, a best-guess type can be computed by that
   * type should not be cached; instead it should be recomputed when the type is
   * requested next after macro expansion.
   */
  final class OngoingMacroExpansion extends Esql<Void, Void> {
    private OngoingMacroExpansion() {
      super((Context)null);
    }
  }

  /**
   * The only instance of the {@link OngoingMacroExpansion} class that is placed
   * in the exploring path to signify that macro expansion is ongoing.
   */
  OngoingMacroExpansion ONGOING_MACRO_EXPANSION = new OngoingMacroExpansion();
}
