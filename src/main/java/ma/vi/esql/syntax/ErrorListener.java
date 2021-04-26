/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

/**
 * ESQL parser error listener.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class ErrorListener extends BaseErrorListener {
  @Override
  public void syntaxError(Recognizer<?, ?> recognizer,
                          Object offendingSymbol,
                          int line,
                          int charPositionInLine,
                          String msg,
                          RecognitionException e) {
    if (e != null) {
      SyntaxAnalyser.error((ParserRuleContext)e.getCtx(), msg);
    } else {
      SyntaxAnalyser.error(msg, line, charPositionInLine);
    }
  }
}
