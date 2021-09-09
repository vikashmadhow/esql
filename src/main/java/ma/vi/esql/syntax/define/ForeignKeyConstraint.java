/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.define;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static ma.vi.esql.semantic.type.Type.dbTableName;
import static ma.vi.esql.syntax.Translatable.Target.*;

/**
 * Represents a foreign key in ESQL statement
 * and in the structure of a relational database.
 *
 * @author vikash.madhow@gmail.om
 */
public class ForeignKeyConstraint extends ConstraintDefinition {
  public ForeignKeyConstraint(Context context,
                              String name,
                              String sourceTable,
                              List<String> sourceColumns,
                              String targetTable,
                              List<String> targetColumns,
                              int forwardCost,
                              int reverseCost,
                              ForeignKeyChangeAction onUpdate,
                              ForeignKeyChangeAction onDelete) {
    super(context,
          name != null ? name : defaultConstraintName("fk_", sourceColumns, targetColumns),
          sourceTable,
          sourceColumns,
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

  public ForeignKeyConstraint(ForeignKeyConstraint other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public ForeignKeyConstraint copy() {
    return new ForeignKeyConstraint(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public ForeignKeyConstraint copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new ForeignKeyConstraint(this, value, children);
  }

  public boolean sameAs(ConstraintDefinition def) {
    if (def instanceof ForeignKeyConstraint c) {
      return new HashSet<>(sourceColumns()).equals(new HashSet<>(c.sourceColumns()))
          && c.targetTable().equals(targetTable())
          && new HashSet<>(targetColumns()).equals(new HashSet<>(c.targetColumns()))

//          && c.forwardCost() == forwardCost()
//          && c.reverseCost() == reverseCost()

//          && (c.onUpdate() == null || c.onUpdate() == NO_ACTION
//                ? onUpdate() == null || onUpdate() == NO_ACTION
//                : c.onUpdate() == onUpdate())
//          && (c.onDelete() == null || c.onDelete() == NO_ACTION
//                ? onDelete() == null || onDelete() == NO_ACTION
//                : c.onDelete() == onDelete())
          ;
    }
    return false;
  }

  @Override
  protected String trans(Target target, EsqlPath path, Map<String, Object> parameters) {
    String name = name();
    if (name.length() >= 64 && (target == MARIADB || target == MYSQL)) {
      /*
       * Identifiers in MySQL and MariaDB are limited to 64 characters.
       */
      name = name.substring(name.length() - 64);
    }
    return "constraint "
      + '"' + name + '"'
      + " foreign key(" + quotedColumnsList(sourceColumns()) + ") "
      + "references " + dbTableName(targetTable(), target) + '('
      + quotedColumnsList(targetColumns()) + ')'
      + (onUpdate() != null ? " on update " + onUpdate().keyword : "")
      + (onDelete() != null ? " on delete " + onDelete().keyword : "");
  }

  public List<String> sourceColumns() {
    return childValue("columns");
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
