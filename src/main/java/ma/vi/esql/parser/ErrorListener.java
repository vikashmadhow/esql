/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser;

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
      Analyser.error((ParserRuleContext)e.getCtx(), msg);
    } else {
      Analyser.error(msg, line, charPositionInLine);
    }
  }
}
