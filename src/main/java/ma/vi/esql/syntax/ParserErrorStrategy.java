package ma.vi.esql.syntax;


import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.ParseCancellationException;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class ParserErrorStrategy extends DefaultErrorStrategy {
  /**
   * Instead of recovering from exception {@code e}, re-throw it wrapped
   * in a {@link ParseCancellationException} so it is not caught by the
   * rule function catches.  Use {@link Exception#getCause()} to get the
   * original {@link RecognitionException}.
   */
  @Override
  public void recover(org.antlr.v4.runtime.Parser recognizer, RecognitionException e) {
    SyntaxAnalyser.error(recognizer.getContext(), e.getMessage());
  }

  /**
   * Make sure we don't attempt to recover inline; if the parser
   * successfully recovers, it won't throw an exception.
   */
  @Override
  public Token recoverInline(org.antlr.v4.runtime.Parser recognizer) throws RecognitionException {
    SyntaxAnalyser.error(recognizer.getContext(), "Missing or unknown token");
    return null;
  }
}
