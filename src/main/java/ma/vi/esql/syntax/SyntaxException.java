/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax;

/**
 * An exception thrown if any error is detected during parsing
 * of an ESQL statement or part thereof.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class SyntaxException extends EsqlException {
  public SyntaxException(String message) {
    this(message, null, null, -1, -1, -1, -1, -1, -1);
  }

  public SyntaxException(String message,
                         Throwable cause,
                         String text,
                         int startLine, int startCharPos, int startIndex,
                         int stopLine, int stopCharPos, int stopIndex) {
    super(message, cause);
    this.text = text;
    this.startLine = startLine;
    this.startCharPos = startCharPos;
    this.startIndex = startIndex;
    this.stopLine = stopLine;
    this.stopCharPos = stopCharPos;
    this.stopIndex = stopIndex;
  }

  public final String text;

  public final int startLine;
  public final int startCharPos;
  public final int startIndex;

  public final int stopLine;
  public final int stopCharPos;
  public final int stopIndex;
}