/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.define;

import ma.vi.base.tuple.T2;
import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.Esql;

import java.util.List;

import static ma.vi.base.lang.Errors.checkArgument;

/**
 * Represent a constraint (primary key, foreign key, etc.) on a table.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public abstract class ConstraintDefinition extends TableDefinition {
  public ConstraintDefinition(Context context,
                              String name,
                              T2<String, ? extends Esql<?, ?>>... children) {
    super(context, name, children);
  }

  public ConstraintDefinition(ConstraintDefinition other) {
    super(other);
  }

  @Override
  public abstract ConstraintDefinition copy();

  /**
   * Returns true if this constraint is the same as the provided definition,
   * ignoring the names of the constraints and assuming that the constraint
   * definition are on the same relation as this constraint.
   */
  public abstract boolean sameAs(ConstraintDefinition def);

  @Override
  public String name() {
    if (value == null) {
      value = defaultConstraintName();
    }
    return value;
  }

  public void table(String table) {
    childValue("table", table);
  }

  public String table() {
    return childValue("table");
  }

  public void columns(List<String> columns) {
    childValue("columns", columns);
  }

  public List<String> columns() {
    return childValue("columns");
  }

  protected abstract String defaultConstraintName();

  /**
   * Supported types of constraints.
   */
  public enum Type {
    CHECK('C', "CHECK"),
    UNIQUE('U', "UNIQUE"),
    PRIMARY_KEY('P', "PRIMARY KEY"),
    FOREIGN_KEY('F', "FOREIGN KEY");

    Type(char marker, String keyword) {
      this.marker = marker;
      this.keyword = keyword;
    }

    public static Type fromMarker(char marker) {
      checkArgument(marker == 'C' || marker == 'U' || marker == 'P' || marker == 'F',
          "Constraint type must be C (CHECK) U (UNIQUE) P (PRIMARY KEY) " +
              "or F (FOREIGN KEY); '" + marker + "' is not recognised.");
      return marker == 'C' ? CHECK :
             marker == 'U' ? UNIQUE :
             marker == 'P' ? PRIMARY_KEY :
             FOREIGN_KEY;
    }

    public final String keyword;

    public final char marker;
  }

  public enum ForeignKeyChangeAction {
    NO_ACTION   ('A', "no action"),
    RESTRICT    ('R', "restrict"),
    CASCADE     ('C', "cascade"),
    SET_NULL    ('N', "set null"),
    SET_DEFAULT ('D', "set default");

    ForeignKeyChangeAction(char marker, String keyword) {
      this.marker = marker;
      this.keyword = keyword;
    }

    public static ForeignKeyChangeAction fromMarker(char marker) {
      checkArgument(marker == 'A' || marker == 'R' || marker == 'C' || marker == 'N' || marker == 'D',
          "Foreign key action marker must be A = no action, R = restrict, C = cascade, " +
              "N = set null or D = set default; '" + marker + "' is not recognised.");
      return marker == 'A' ? NO_ACTION  :
             marker == 'R' ? RESTRICT   :
             marker == 'C' ? CASCADE    :
             marker == 'N' ? SET_NULL   : SET_DEFAULT;
    }

    public static ForeignKeyChangeAction fromInformationSchema(String rule) {
      return switch (rule) {
        case "NO ACTION"  -> NO_ACTION;
        case "RESTRICT"   -> RESTRICT;
        case "CASCADE"    -> CASCADE;
        case "SET NULL"   -> SET_NULL;
        default           -> SET_DEFAULT;
      };
    }

    public final char marker;
    public final String keyword;
  }
}