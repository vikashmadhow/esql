/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.define;

import ma.vi.base.string.Strings;
import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static ma.vi.base.lang.Errors.checkArgument;

/**
 * Represent a constraint (primary key, foreign key, etc.) on a table.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public abstract class ConstraintDefinition extends TableDefinition {
  public ConstraintDefinition(Context context,
                              String name,
                              String table,
                              List<String> columns,
                              T2<String, ? extends Esql<?, ?>>... children) {
    super(context,
          name,
          Stream.concat(
              Stream.of(
                  T2.of("table", new Esql<>(context, table)),
                  T2.of("columns", new Esql<>(context, columns))),
              Stream.of(children)).toArray(T2[]::new));
  }

  public ConstraintDefinition(ConstraintDefinition other) {
    super(other);
  }

  public ConstraintDefinition(ConstraintDefinition other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public abstract ConstraintDefinition copy();

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public abstract ConstraintDefinition copy(String value, T2<String, ? extends Esql<?, ?>>... children);

  /**
   * Returns true if this constraint is the same as the provided definition,
   * ignoring the names of the constraints and assuming that the constraint
   * definition are on the same relation as this constraint.
   */
  public abstract boolean sameAs(ConstraintDefinition def);

  @Override
  public String name() {
    return value;
  }

  public <T extends ConstraintDefinition> T table(String table) {
    return (T)set("table", new Esql<>(context, table));
  }

  public String table() {
    return childValue("table");
  }

  public List<String> columns() {
    return childValue("columns");
  }

  protected static String defaultConstraintName(String prefix, List<String>... columns) {
    return (prefix != null ? prefix : "")
          + Stream.of(columns)
                  .map(cols -> cols.stream()
                                   .map(String::toLowerCase)
                                   .collect(joining("_")))
                  .collect(joining("__"))
          + '_' + Strings.random(4);
  }

  /**
   * Supported types of constraints.
   */
  public enum Type {
    CHECK       ('C', "CHECK"),
    UNIQUE      ('U', "UNIQUE"),
    PRIMARY_KEY ('P', "PRIMARY KEY"),
    FOREIGN_KEY ('F', "FOREIGN KEY");

    Type(char marker, String keyword) {
      this.marker = marker;
      this.keyword = keyword;
    }

    public static Type fromMarker(char marker) {
      checkArgument(marker == 'C' || marker == 'U' || marker == 'P' || marker == 'F',
          "Constraint type must be C (CHECK) U (UNIQUE) P (PRIMARY KEY) " +
              "or F (FOREIGN KEY); '" + marker + "' is not recognised.");
      return marker == 'C' ? CHECK       :
             marker == 'U' ? UNIQUE      :
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
      return marker == 'A' ? NO_ACTION :
             marker == 'R' ? RESTRICT  :
             marker == 'C' ? CASCADE   :
             marker == 'N' ? SET_NULL  : SET_DEFAULT;
    }

    public static ForeignKeyChangeAction fromInformationSchema(String rule) {
      return switch (rule) {
        case "NO ACTION" -> NO_ACTION;
        case "RESTRICT"  -> RESTRICT;
        case "CASCADE"   -> CASCADE;
        case "SET NULL"  -> SET_NULL;
        default          -> SET_DEFAULT;
      };
    }

    public final char marker;
    public final String keyword;
  }
}