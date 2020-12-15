/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.define;

import ma.vi.base.string.Strings;
import ma.vi.base.tuple.T2;
import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.Esql;

import java.util.HashSet;
import java.util.List;

import static java.util.stream.Collectors.joining;
import static ma.vi.esql.parser.define.ConstraintDefinition.ForeignKeyChangeAction.NO_ACTION;
import static ma.vi.esql.type.Type.dbName;
import static ma.vi.esql.type.Type.splitName;

/**
 * Represents a foreign key in ESQL statement
 * and in the structure of a relational database.
 *
 * @author vikash.madhow@gmail.om
 */
public class ForeignKeyConstraint extends ConstraintDefinition {
  public ForeignKeyConstraint(Context context,
                              String name,
                              List<String> sourceColumns,
                              String targetTable,
                              List<String> targetColumns,
                              int forwardCost,
                              int reverseCost,
                              ForeignKeyChangeAction onUpdate,
                              ForeignKeyChangeAction onDelete) {
    super(context, name,
        T2.of("sourceColumns", new Esql<>(context, sourceColumns)),
        T2.of("targetTable", new Esql<>(context, targetTable)),
        T2.of("targetColumns", new Esql<>(context, targetColumns)),
        T2.of("forwardCost", new Esql<>(context, forwardCost)),
        T2.of("reverseCost", new Esql<>(context, reverseCost)),
        T2.of("onUpdate", new Esql<>(context, onUpdate)),
        T2.of("onDelete", new Esql<>(context, onDelete)));
  }

  public ForeignKeyConstraint(ForeignKeyConstraint other) {
    super(other);
  }

  @Override
  public ForeignKeyConstraint copy() {
    if (!copying()) {
      try {
        copying(true);
        return new ForeignKeyConstraint(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  public boolean sameAs(ConstraintDefinition def) {
    if (def instanceof ForeignKeyConstraint) {
      ForeignKeyConstraint c = (ForeignKeyConstraint)def;
      return new HashSet<>(c.sourceColumns()).equals(new HashSet<>(sourceColumns()))

          && c.targetTable().equals(targetTable())

          && new HashSet<>(c.targetColumns()).equals(new HashSet<>(targetColumns()))

          && c.forwardCost() == forwardCost()
          && c.reverseCost() == reverseCost()

          && (c.onUpdate() == null
                ? onUpdate() == null || onUpdate() == NO_ACTION
                : c.onUpdate() == onUpdate())
          && (c.onDelete() == null
                ? onDelete() == null || onDelete() == NO_ACTION
                : c.onDelete() == onDelete());
    }
    return false;
  }

  @Override
  public String translate(Target target) {
    return "constraint \"" + (name() != null ? name() : defaultConstraintName())
        + "\" foreign key(" + quotedColumnsList(sourceColumns()) + ") "
        + "references " + dbName(targetTable(), target) + '('
        + quotedColumnsList(targetColumns()) + ')'
        + (onUpdate() != null ? " on update " + onUpdate().keyword : "")
        + (onDelete() != null ? " on delete " + onDelete().keyword : "");
  }

  @Override
  protected String defaultConstraintName() {
    return "foreign_from_"
        + sourceColumns().stream()
                         .map(String::toLowerCase)
                         .collect(joining("_"))
        + "_to_" + splitName(targetTable()).b + '_'
        + targetColumns().stream()
                         .map(String::toLowerCase)
                         .collect(joining("_"))
        + '_' + Strings.random(4);
  }

  public List<String> sourceColumns() {
    return childValue("sourceColumns");
  }

  public String targetTable() {
    return childValue("targetTable");
  }

  public List<String> targetColumns() {
    return childValue("targetColumns");
  }

  public int forwardCost() {
    return childValue("forwardCost");
  }

  public int reverseCost() {
    return childValue("reverseCost");
  }

  public ForeignKeyChangeAction onUpdate() {
    return childValue("onUpdate");
  }

  public ForeignKeyChangeAction onDelete() {
    return childValue("onDelete");
  }
}
