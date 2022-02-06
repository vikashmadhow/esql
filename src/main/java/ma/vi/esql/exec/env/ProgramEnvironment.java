package ma.vi.esql.exec.env;

/**
 * A program environment is used as the global space for an executing programming.
 * It sits just below the read-only system environment.
 *
 * The program environment is initialised with several variables that are used
 * by a running program, including whether break & continue was executed in a
 * sub-expression so that upstream loops can act consequently, etc.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class ProgramEnvironment extends FunctionEnvironment  {
  public ProgramEnvironment(Environment parent) {
    super("Program", parent);
    add("#break", false);
    add("#continue", false);
  }
}
