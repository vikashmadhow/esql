/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.type;

import ma.vi.base.trie.PathTrie;
import ma.vi.base.tuple.T2;
import ma.vi.esql.parser.CircularReferenceException;
import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.TranslationException;
import ma.vi.esql.parser.define.Attribute;
import ma.vi.esql.parser.define.ConstraintDefinition;
import ma.vi.esql.parser.define.ForeignKeyConstraint;
import ma.vi.esql.parser.expression.*;
import ma.vi.esql.parser.query.Column;
import ma.vi.esql.parser.query.Select;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static ma.vi.base.string.Strings.makeUnique;
import static ma.vi.base.string.Strings.random;
import static ma.vi.esql.builder.Attributes.DERIVED;
import static ma.vi.esql.parser.expression.ColumnRef.qualify;

/**
 * A relation is a composite type with a qualified name (i.e. with a schema name included).
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class BaseRelation extends Relation {
  public BaseRelation(Context context,
                      UUID id,
                      String name,
                      String displayName,
                      String description,
                      List<Attribute> attributes,
                      List<Column> columns,
                      List<ConstraintDefinition> constraints) {
    super(name);
    this.context = context;
    this.id = id;
    this.name = name != null ? name : "__temp__.rel_" + random(10);
    this.displayName = displayName;
    this.description = description;
    if (constraints != null) {
      this.constraints.addAll(constraints);
    }
    if (attributes != null) {
      for (Attribute a: attributes) {
        attribute(a.name(), a.attributeValue());
      }
    }
    this.columns = expandColumns(attributes, columns);

    /*
     * Implicit aliasing of columns.
     */
    Set<String> aliases = new HashSet<>();
    for (Column column: this.columns) {
      if (column.alias() == null) {
        Expression<?> expr = column.expr();
        if (expr instanceof ColumnRef) {
          column.alias(((ColumnRef)expr).name());
        } else {
          column.alias(makeUnique(aliases, "column"));
        }
      }
      aliases.add(column.alias());
      this.columnsByAlias.put(column.alias(), column);
    }

    /*
     * Expand expressions of derived columns until they consist only of
     * base columns. E.g., {a:a, b:a+5, c:a+b, d:c+b}
     *      is expanded to {a:a, b:a+5, c:a+a+5, d:a+a+5+a+5}
     */
    for (Column column: this.columns) {
      if (!(column.expr() instanceof ColumnRef)) {
        column.expr(expandDerived(column.expr(),
                                  columnsByAlias,
                                  column.alias(),
                                  new HashSet<>()));
      }
    }
  }

  public BaseRelation(BaseRelation other) {
    super(other.name);
    this.context = other.context;
    this.id = other.id;
    this.name = other.name;
    this.displayName = other.displayName;
    this.description = other.description;

    this.columns = new ArrayList<>();
    for (Column column: other.columns) {
      Column col = column.copy();
      this.columns.add(col);
    }
    for (ConstraintDefinition c: other.constraints) {
      this.constraints.add(c.copy());
    }
  }

  @Override
  public BaseRelation copy() {
    if (!copying()) {
      try {
        copying(true);
        return new BaseRelation(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public List<Column> columns() {
    return columns;
  }

  public Column findColumn(String relationAlias, String name) {
    return columnsByAlias.get(name);
  }

  public void addColumn(Column column) {
    for (Column c: expandColumn(column, aliasedColumns(columns))) {
      this.columns.add(c);
      this.columnsByAlias.put(c.alias(), c);
    }
  }

  public boolean removeColumn(String columnName) {
    List<T2<String, Column>> cols = columnsByAlias.getPrefixed(columnName);
    columnsByAlias.deletePrefixed(columnName);
    Set<String> colNames = cols.stream().map(T2::a).collect(toSet());
    return columns.removeIf(c -> colNames.contains(c.alias()));
  }

  @Override
  public List<Column> columns(String alias, String prefix) {
    List<T2<String, Column>> cols = columnsByAlias.getPrefixed(prefix);
    return cols.stream()
               .map(t -> alias == null
                            ? t.b().copy()
                            : qualify(t.b().copy(), alias, null, true))
               .collect(toList());
  }

  public static Expression<?> expandDerived(Expression<?> derivedExpression,
                                            PathTrie<Column> columns,
                                            String columnName,
                                            Set<String> seen) {
    try {
      seen.add(columnName);
      return (Expression<?>)derivedExpression.map(e -> {
          if (e instanceof ColumnRef) {
            ColumnRef ref = (ColumnRef)e;
            String colName = ref.name();
            Column column = columns.get(colName);
            if (column == null) {
              throw new TranslationException("Unknown column " + colName
                                           + " in derived expresion " + derivedExpression);
            } else if (seen.contains(colName)) {
              Set<String> otherColumns = seen.stream()
                                             .filter(c -> !c.equals(colName) && !c.contains("/"))
                                             .collect(Collectors.toSet());
              throw new CircularReferenceException(
                "A circular definition was detected in the expression "
                + derivedExpression + " consisting of the column "
                + colName + (otherColumns.isEmpty() ? "" : " and " + otherColumns));
            } else if (column.derived()) {
              return new GroupedExpression(
                  e.context,
                  expandDerived(column.expr(), columns, colName, seen));
            }
          }
          return e;
        },
        e -> !(e instanceof Select));
    } finally {
      seen.remove(columnName);
    }
  }

  /**
   * Returns the columns required to calculate the relation-level attribute.
   *
   * @param attr    The attribute to compute the columns for.
   * @param aliased Maps columns names to their aliases which are used to
   *                replace the column names in expression sent to clients.
   * @return A list of one or two columns which will compute the attribute
   *         value. The first column, when executed, will compute the value
   *         of the attribute while the second one will contain the uncomputed
   *         form of the attribute intended to be sent to clients where the
   *         attribute can be recomputed when its dependencies change. This
   *         2nd column is only returned for non-literal attributes.
   */
  public static List<Column> columnsForAttribute(Attribute attr,
                                                 String prefix,
                                                 Map<String, String> aliased) {
    List<Column> cols = new ArrayList<>();
    String attrPrefix = prefix + attr.name();
    Expression<?> expr = attr.attributeValue().copy();
    cols.add(new Column(
        attr.context,
        attrPrefix,
        expr,
        null
    ));
    if (!(expr instanceof Literal)) {
      /*
       * Rename columns to aliases in uncomputed expression as those will be
       * computed on the client-side where the aliased names will be valid.
       */
      cols.add(new Column(
          attr.context,
          attrPrefix + "/e",
          new UncomputedExpression(attr.context, rename(expr, aliased)),
          null
      ));
    }
    return cols;
  }

  public static List<Column> expandColumns(List<Attribute> attributes, List<Column> columns) {
    List<Column> newCols = new ArrayList<>();
    Map<String, String> aliased = aliasedColumns(columns);

    /*
     * Add relation metadata.
     * For example, `select a from S` becomes
     *     ```
     *     select /tm1:(max:max(S.b) from S:S),
     *            /tm1/e:$(max:max(S.b) from S:S),
     *            /tm2:(S.a>S.b),
     *            /tm2/e:$(S.a>S.b),
     *
     *            a:S.a,
     *            a/m1:(S.b>5),
     *            a/m2:(10),
     *            a/m3:(S.a!=0),
     *            a/m3/e:$(S.a!=0)
     *       from S:S
     *     ```
     */
    if (attributes != null) {
      for (Attribute attr: attributes) {
        newCols.addAll(columnsForAttribute(attr, "/", aliased));
      }
    }

    for (Column column: columns) {
     newCols.addAll(expandColumn(column, aliased));
    }
    return newCols;
  }

  /**
   * Add column metadata as columns to the query according to
   * the various expansion rules detailed in the spec.
   * E.g. a {m1:b, m2:c+5, m3:10} becomes:
   *
   * <pre>
   *    a:a
   *    a/m1:b,
   *    a/m1/e:$(b),
   *    a/m2:c+5,
   *    a/m2/e:$(c+5),
   *    a/m3:10
   * </pre>
   */
  private static List<Column> expandColumn(Column column, Map<String, String> aliased) {
    List<Column> newCols = new ArrayList<>();
    newCols.add(column);
    String colAlias = column.alias();
    if (!colAlias.contains("/")) {
      if (column.metadata() != null) {
        Boolean derived = column.metadata().evaluateAttribute(DERIVED);
        if (derived != null && derived) {
          newCols.add(new Column(
              column.context,
              colAlias + "/e",
              new UncomputedExpression(column.context, rename(column.expr(), aliased)),
              null
          ));
        }
        for (Attribute attr: column.metadata().attributes().values()) {
          newCols.addAll(columnsForAttribute(attr, colAlias + '/', aliased));
        }
      }
    }
    return newCols;
  }

  public static Expression<?> rename(Expression<?> expr, Map<String, String> aliased) {
    return (Expression<?>)expr.map(e -> {
      if (e instanceof ColumnRef) {
        String name = ((ColumnRef)e).name();
        if (aliased.containsKey(name)) {
          ((ColumnRef)e).name(aliased.get(name));
        }
      }
      return e;
    });
  }

  /**
   * Returns column names which have been aliased to a
   * different name. This is used to rename the reference
   * to those column names in expressions.
   */
  private static Map<String, String> aliasedColumns(List<Column> columns) {
    Map<String, String> aliased = new HashMap<>();
    for (Column col: columns) {
      Expression<?> expr = col.expr();
      if (expr instanceof ColumnRef) {
        String name = ((ColumnRef)expr).name();
        String alias = col.alias();
        if (alias != null && !name.equals(alias)) {
          aliased.put(name, alias);
        }
      }
    }
    return aliased;
  }

  public List<ConstraintDefinition> constraints() {
    return unmodifiableList(constraints);
  }

  public void constraint(ConstraintDefinition c) {
    constraints.add(c);
  }

  public ConstraintDefinition constraint(String constraintName) {
    for (ConstraintDefinition c: constraints) {
      if (c.name().equals(constraintName)) {
        return c;
      }
    }
    return null;
  }

  public boolean removeConstraint(String constraintName) {
    return constraints.removeIf(c -> c.name().equals(constraintName));
  }

  public List<ForeignKeyConstraint> dependentConstraints() {
    return unmodifiableList(dependentConstraints);
  }

  public void dependentConstraint(ForeignKeyConstraint c) {
    dependentConstraints.add(c);
  }

  /**
   * Returns the constraint on this table same as the provided definition, or null
   * if none found. When comparing constraints, their names are ignored (i.e. two
   * constraints are equal if they have the same structure even if their names are
   * different) and the source table of the constraint definition is assumed to be
   * this table. The latter is because the constraint definition is attached to a
   * bigger definition (create table, alter table, etc.) which has the table name;
   * the constraint definition itself does not carry the table it is defined against.
   */
  public synchronized ConstraintDefinition sameAs(ConstraintDefinition def) {
    if (constraints != null) {
      for (ConstraintDefinition tableConstraint : constraints) {
        if (tableConstraint.sameAs(def)) {
          return tableConstraint;
        }
      }
    }
    return null;
  }

//    public void dependency(Relation dependency) {
//        if (dependencies == null) {
//            dependencies = new HashSet<>();
//        }
//        dependencies.add(dependency);
//    }
//
//    public List<Column> columns() {
//        return unmodifiableList(columns);
//    }
//
//    /**
//     * Some relations are aliased or formed by several aliased relations
//     * joined together; such relations are specified in the table expressions
//     * for queries and update statements. This method can be used to extract
//     * a specific aliased relation from this relation if it has been formed
//     * for such combinations of relations.
//     */
//    public Relation forAlias(String alias) {
//        if (this instanceof AliasedRelation && ((AliasedRelation)this).alias.equals(alias)) {
//            return this;
//
//        } else if (this instanceof Join) {
//            Join join = (Join)this;
//            for (Relation rel: join.relations) {
//                Relation aliased = rel.forAlias(alias);
//                if (aliased != null) {
//                    return aliased;
//                }
//            }
//        }
//        return null;
//    }
//
//    /**
//     * Find the alias for the relation if this relation is  an aliased relation
//     * for the provided one, or if this is a join which contains the provided
//     * relation as a component.
//     */
//    public String aliasFor(Relation relation) {
//        if (this instanceof AliasedRelation && ((AliasedRelation)this).relation.name().equals(relation.name())) {
//            return ((AliasedRelation)this).alias;
//
//        } else if (this instanceof Join) {
//            Join join = (Join)this;
//            for (Relation rel: join.relations) {
//                String alias = rel.aliasFor(relation);
//                if (alias != null) {
//                    return alias;
//                }
//            }
//        }
//        return null;
//    }

  private static final class Node implements Comparable<Node> {
    private Node(Node previous,
                 ForeignKeyConstraint constraint,
                 boolean reverse) {
      this.previous = previous;
      this.constraint = constraint;
      this.reverse = reverse;

      this.cost = (previous != null ? previous.cost : 0)
          + (reverse ? constraint.reverseCost() : constraint.forwardCost());
    }

    @Override
    public int compareTo(Node o) {
      return cost - o.cost;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Node node = (Node)o;
      return constraint.equals(node.constraint);
    }

    @Override
    public int hashCode() {
      return Objects.hash(constraint);
    }

    @Override
    public String toString() {
      StringBuilder st = new StringBuilder();
      if (previous != null) {
        st.append(previous.toString());
        st.append(" / ");
      }
      if (reverse) {
        st.append(constraint.targetTable())
          .append("<-")
          .append(constraint.table());
      } else {
        st.append(constraint.table())
          .append("->")
          .append(constraint.targetTable());
      }
      return st.toString();
    }

    private final Node previous;
    private final ForeignKeyConstraint constraint;
    public final boolean reverse;
    public final int cost;
  }

  /**
   * Returns the list of foreign-key constraints connecting
   * this relation to the target, if such a path exists.
   */
  public List<T2<ForeignKeyConstraint, Boolean>> path(Relation target, boolean ignoreHiddenFields) {

    Set<Node> explored = new HashSet<>();
    PriorityQueue<Node> toExplore = new PriorityQueue<>();
    Map<Node, Integer> toBeExplored = new HashMap<>();
    for (ConstraintDefinition c: constraints) {
      if (c instanceof ForeignKeyConstraint) {
        ForeignKeyConstraint fk = (ForeignKeyConstraint)c;
        if (fk.forwardCost() >= 0
            && (!ignoreHiddenFields
            || (fk.sourceColumns().stream().noneMatch(f -> !f.equals("_id") && f.charAt(0) == '_')
            && fk.targetColumns().stream().noneMatch(f -> !f.equals("_id") && f.charAt(0) == '_')))) {
          Node node = new Node(null, fk, false);
          toExplore.add(node);
          toBeExplored.put(node, node.cost);
        }
      }
    }
    for (ForeignKeyConstraint c: dependentConstraints) {
      if (c.reverseCost() >= 0
          && (!ignoreHiddenFields
          || (c.sourceColumns().stream().noneMatch(f -> !f.equals("_id") && f.charAt(0) == '_')
          && c.targetColumns().stream().noneMatch(f -> !f.equals("_id") && f.charAt(0) == '_')))) {
        Node node = new Node(null, c, true);
        toExplore.add(node);
        toBeExplored.put(node, node.cost);
      }
    }
    while (!toExplore.isEmpty()) {
      Node node = toExplore.remove();
      toBeExplored.remove(node);
      explored.add(node);

      if ((!node.reverse && node.constraint.targetTable().equals(target.name()))
          || (node.reverse && node.constraint.table().equals(target.name()))) {
        /*
         * Reached target table (goal)
         */
        List<T2<ForeignKeyConstraint, Boolean>> path = new ArrayList<>();
        while (node != null) {
          path.add(T2.of(node.constraint, node.reverse));
          node = node.previous;
        }
        Collections.reverse(path);
        return path;

      } else {
        /*
         * Expansion as per uniform cost search (from 'AI: a modern approach'):
         *      for each action in problem.ACTIONS(node.STATE) do
         *          child <- CHILD-NODE(problem, node, action)
         *          if child.STATE is not in explored or frontier then
         *              frontier <- INSERT(child, frontier)
         *          else if child.STATE is in frontier with higher PATH-COST then
         *              replace that frontier node with child
         */
        BaseRelation rel = node.reverse ? context.structure.relation(node.constraint.table())
                                        : context.structure.relation(node.constraint.targetTable());
        for (ConstraintDefinition c: rel.constraints) {
          if (c instanceof ForeignKeyConstraint) {
            ForeignKeyConstraint fk = (ForeignKeyConstraint)c;
            if (fk.forwardCost() >= 0
                && (!ignoreHiddenFields
                || (fk.sourceColumns().stream().noneMatch(f -> !f.equals("_id") && f.charAt(0) == '_')
                && fk.targetColumns().stream().noneMatch(f -> !f.equals("_id") && f.charAt(0) == '_')))) {

              Node targetNode = new Node(node, fk, false);
              if (!explored.contains(targetNode)
                  && !toBeExplored.containsKey(targetNode)) {
                toExplore.add(targetNode);
                toBeExplored.put(targetNode, targetNode.cost);

              } else if (toBeExplored.containsKey(targetNode) && toBeExplored.get(targetNode) > targetNode.cost) {
                toExplore.remove(targetNode);
                toExplore.add(targetNode);
                toBeExplored.remove(targetNode);
                toBeExplored.put(targetNode, targetNode.cost);
              }
            }
          }
        }

        for (ForeignKeyConstraint c: rel.dependentConstraints) {
          if (c.reverseCost() >= 0
              && (!ignoreHiddenFields
              || (c.sourceColumns().stream().noneMatch(f -> !f.equals("_id") && f.charAt(0) == '_')
              && c.targetColumns().stream().noneMatch(f -> !f.equals("_id") && f.charAt(0) == '_')))) {
            Node sourceNode = new Node(node, c, true);
            if (!explored.contains(sourceNode)
                && !toBeExplored.containsKey(sourceNode)) {
              toExplore.add(sourceNode);
              toBeExplored.put(sourceNode, sourceNode.cost);

            } else if (toBeExplored.containsKey(sourceNode) && toBeExplored.get(sourceNode) > sourceNode.cost) {
              toExplore.remove(sourceNode);
              toExplore.add(sourceNode);
              toBeExplored.remove(sourceNode);
              toBeExplored.put(sourceNode, sourceNode.cost);
            }
          }
        }
      }
    }
    return emptyList();
  }

  public UUID id() {
    return id;
  }

  public final Context context;

  /**
   * Internal unique identifier for table in database system.
   */
  public final UUID id;

  public final String displayName;

  public final String description;

  /**
   * Relation columns.
   */
  private final List<Column> columns;

  private final PathTrie<Column> columnsByAlias = new PathTrie<>();

  /**
   * Constraints set on the table (including field constraints but excluding non-null field's constraints).
   */
  private final List<ConstraintDefinition> constraints = new ArrayList<>();

  /**
   * Foreign key constraints pointing to this relation.
   */
  private final List<ForeignKeyConstraint> dependentConstraints = new ArrayList<>();
}