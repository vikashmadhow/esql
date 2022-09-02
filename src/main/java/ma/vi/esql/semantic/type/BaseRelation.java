/*
 * Copyright (c) 2018-2021 Vikash Madhow
 */

package ma.vi.esql.semantic.type;

import ma.vi.base.lang.NotFoundException;
import ma.vi.base.trie.PathTrie;
import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.CircularReferenceException;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.define.Attribute;
import ma.vi.esql.syntax.define.Metadata;
import ma.vi.esql.syntax.define.table.*;
import ma.vi.esql.syntax.expression.*;
import ma.vi.esql.syntax.expression.literal.*;
import ma.vi.esql.syntax.expression.logical.And;
import ma.vi.esql.syntax.query.Select;
import ma.vi.esql.translation.TranslationException;

import java.util.*;

import static java.util.Collections.*;
import static java.util.stream.Collectors.toSet;
import static ma.vi.base.string.Strings.random;
import static ma.vi.esql.builder.Attributes.*;
import static ma.vi.esql.semantic.type.Types.UnknownType;
import static ma.vi.esql.syntax.expression.ColumnRef.unqualify;

/**
 * A relation is a composite type with a qualified name (i.e. with a schema name included).
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class BaseRelation extends Struct {
  public BaseRelation(Context                    context,
                      UUID                       id,
                      String                     name,
                      String                     displayName,
                      String                     description,
                      List<Attribute>            attributes,
                      List<Column>               columns,
                      List<ConstraintDefinition> constraints) {
    super(context,
          id,
          name != null ? name : "__temp__.rel_" + random(10),
          displayName,
          description,
          attributes,
          columns);

    this.columns.clear();
    this.columnsByAlias.clear();

    /*
     * Set default names of columns when not specified.
     */
    PathTrie<Column> aliasedColumns = new PathTrie<>();
    List<Column> renamedCols = new ArrayList<>();
    for (Column column: columns) {
      Column col = column;
      if (col.name() == null) {
        if (col.expression() instanceof ColumnRef ref) {
          col = col.name(ref.columnName());
        } else {
          throw new TranslationException(col, "The derived column " + col + " in the base relation " + name + " requires a name");
        }
      }
      aliasedColumns.put(col.name(), col);
      col = col.set("relation", new Esql<>(context, this));
      renamedCols.add(col);
    }

    for (Column column: renamedCols) {
      /*
       * Expand expressions of derived columns until they consist only of
       * base columns. E.g., {a:a, b:a+5, c:a+b, d:c+b}
       *      is expanded to {a:a, b:a+5, c:a+a+5, d:a+a+5+a+5}
       */
      if (column.derived()
      && !(column.expression() instanceof Literal)
      && (!(column.expression() instanceof ColumnRef)
       || !((ColumnRef)column.expression()).columnName().equals(column.name()))) {
        column = column.expression(expandDerived(column.expression(),
                                                 aliasedColumns,
                                                 column.name(),
                                                 new HashSet<>()));
      }
      if (column.metadata() != null && !column.metadata().attributes().isEmpty()) {
        List<Attribute> attrs = new ArrayList<>();
        for (Attribute attr: column.metadata().attributes().values()) {
          attrs.add(attr.attributeValue(expandDerived(attr.attributeValue(),
                                                      aliasedColumns,
                                                      column.name() + '/' + attr.name(),
                                                      new HashSet<>())));
        }
        column = column.metadata(new Metadata(column.context, attrs));
      }
      this.columns.add(column);
      this.columnsByAlias.put(column.name(), column);
    }

    expandColumns();

    if (constraints != null) {
      for (ConstraintDefinition c: constraints) {
        constraint(c);
      }
    }
  }

  public BaseRelation(BaseRelation other) {
    super(other);
    for (ConstraintDefinition c: other.constraints) {
      this.constraints.add(c.copy());
    }
  }

  @Override
  public BaseRelation copy() {
    return new BaseRelation(this);
  }

  @Override
  public String alias() {
    int pos = name.lastIndexOf('.');
    return pos == -1 ? name : name.substring(pos + 1);
  }

  @Override
  public Set<String> aliases() {
    return emptySet();
  }

  @Override
  public List<T2<Relation, Column>> columns() {
    return columns.stream().map(c -> new T2<Relation, Column>(this, c)).toList();
  }

  @Override
  public T2<Relation, Column> findColumn(ColumnRef ref) {
    Column column = columnsByAlias.get(ref.columnName());
    return column == null ? null : T2.of(this, column);
  }

  public void addColumn(Column column) {
    for (Column c: expandColumn(column, aliasedColumns(columns))) {
      this.columns.add(c);
      this.columnsByAlias.put(c.name(), c);
    }
  }

  public void addOrReplaceColumn(Column column) {
    String columnName = column.name();
    if (columnsByAlias.get(columnName) != null) {
      removeColumn(columnName);
    }
    addColumn(column);
  }

  public boolean removeColumn(String columnName) {
    List<T2<String, Column>> cols = columnsByAlias.getPrefixed(columnName);
    columnsByAlias.deletePrefixed(columnName);
    Set<String> colNames = cols.stream().map(T2::a).collect(toSet());
    return columns.removeIf(c -> colNames.contains(c.name()));
  }

  @Override
  public List<T2<Relation, Column>> columns(String prefix) {
    List<T2<String, Column>> cols = columnsByAlias.getPrefixed(prefix);
    return cols.stream()
               .map(t -> new T2<Relation, Column>(this, t.b().copy()))
               .toList();
  }

  public Attribute attribute(String name, Expression<?, String> expr) {
    return attribute(new Attribute(context, name, expr));
  }

  @Override
  public Attribute attribute(Attribute att) {
    Expression<?, ?> expr = att.attributeValue();
    String name = att.name();
    if (expr != null && !(expr instanceof Literal)) {
      expr = expandDerived(expr, columnsByAlias, name, new HashSet<>());
    }
    removeColumn('/' + name);
    for (Column col: columnsForAttribute(new Attribute(context, name, expr), "/",
                                         aliasedColumns(columns))) {
      addColumn(col);
    }
    return attributes().put(name, att);
  }

  private static Expression<?, ?> expandDerived(Expression<?, ?> derivedExpression,
                                                PathTrie<Column> columns,
                                                String           columnName,
                                                Set<String>      seen) {
    try {
      seen.add(columnName);
      return (Expression<?, String>)derivedExpression.map((e, path) -> {
        if (e instanceof ColumnRef ref
         && !path.hasAncestor(UncomputedExpression.class)) {
          String colName = ref.columnName();
          Column column = columns.get(colName);
          if (column == null) {
            throw new TranslationException(derivedExpression, "Unknown column " + colName
                                        + " in derived expression " + derivedExpression);
          } else if (seen.contains(colName)) {
            Set<String> otherColumns = seen.stream()
                                           .filter(c -> !c.equals(colName) && !c.contains("/"))
                                           .collect(toSet());
            throw new CircularReferenceException(
              "A circular definition was detected in the expression "
              + derivedExpression + " consisting of the column "
              + colName + (otherColumns.isEmpty() ? "" : " and " + otherColumns));
          } else if (column.derived()) {
            return new GroupedExpression(e.context, expandDerived(column.expression(), columns, colName, seen));
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
  public static List<Column> columnsForAttribute(Attribute           attr,
                                                 String              prefix,
                                                 Map<String, String> aliased) {
    List<Column> cols = new ArrayList<>();
    if (!attr.name().equals("_id")) {
      String attrPrefix = prefix + attr.name();
      Expression<?, ?> expr = attr.attributeValue().copy();
      ColumnRef ref = expr.find(ColumnRef.class);
      cols.add(new Column(attr.context, attrPrefix, expr,
                          ref == null || expr instanceof SelectExpression
                            ? expr.computeType(new EsqlPath(expr))
                            : UnknownType,
                          null));
      if (!(expr instanceof Literal)) {
        /*
         * Rename columns to aliases in uncomputed expression as those will be
         * computed on the client-side where the aliased names will be valid.
         */
        cols.add(new Column(attr.context,
                            attrPrefix + "/$e",
                            new UncomputedExpression(attr.context, unqualify(expr)),
                            Types.TextType,
                            null));
      }
    }
    return cols;
  }

  private void expandColumns() {
    List<Column> cols = expandColumns(new ArrayList<>(attributes().values()),
                                      this.columns,
                                      this.constraints);
    /*
     * Implicit naming of columns.
     */
    this.columns.clear();
    for (Column column: cols) {
      column = column.set("relation", new Esql<>(context, this));
      if (column.name() == null) {
        Expression<?, ?> expr = column.expression();
        if (expr instanceof ColumnRef ref) {
          column = column.name(ref.columnName());
        } else {
          throw new TranslationException(column, "The derived column " + column + " in the base relation " + name + " requires a name");
        }
      }
      this.columns.add(column);
      this.columnsByAlias.put(column.name(), column);
    }
  }

  /**
   * Add relation and column metadata to the column list.
   * For example, <pre>select a from S</pre> becomes
   *     <pre>
   *     select /tm1:(max:max(S.b) from S:S),
   *            /tm1/$e:$(max:max(S.b) from S:S),
   *            /tm2:(S.a>S.b),
   *            /tm2/$e:$(S.a>S.b),
   *
   *            a:S.a,
   *            a/m1:(S.b>5),
   *            a/m2:(10),
   *            a/m3:(S.a!=0),
   *            a/m3/$e:$(S.a!=0)
   *       from S:S
   *     </pre>
   */
  public static List<Column> expandColumns(List<Attribute>            attributes,
                                           List<Column>               columns,
                                           List<ConstraintDefinition> constraints) {
    Context context = !attributes .isEmpty() ? attributes .get(0).context
                    : !columns    .isEmpty() ? columns    .get(0).context
                    : !constraints.isEmpty() ? constraints.get(0).context
                    :  null;
    if (context == null) {
      throw new NotFoundException("Could not get a valid context");
    }
    List<Column> newCols = new ArrayList<>();
    Map<String, String> aliased = aliasedColumns(columns);
    for (Attribute attr: attributes) {
      newCols.addAll(columnsForAttribute(attr, "/", aliased));
    }

//    /*
//     * Add relation level attribute columns for multi columns unique constraint.
//     */
//    List<UniqueConstraint> uniqueCons = constraints.stream()
//                                                   .filter(c -> c instanceof UniqueConstraint)
//                                                   .map(c -> (UniqueConstraint)c)
//                                                   .toList();
//    List<List<String>> multiUnique = new ArrayList<>();
//    for (UniqueConstraint cons: uniqueCons) {
//      if (cons.columns().size() > 1) {
//        multiUnique.add(cons.columns());
//      }
//    }
//    if (!multiUnique.isEmpty()) {
//      newCols.add(new Column(context,
//                             '/' + UNIQUE,
//                             new JsonArrayLiteral(
//                                   context,
//                                   multiUnique.stream()
//                                              .map(u -> new JsonArrayLiteral(
//                                                              context,
//                                                              u.stream()
//                                                               .map(s -> new StringLiteral(context, s))
//                                                               .toList()))
//                                              .toList()),
//                             Types.JsonType,
//                             null));
//    }
//
//    /*
//     * Expand columns, adding unique attribute to unique columns.
//     */
//    Set<String> uniqueColumns = uniqueCons.stream()
//                                          .filter(u -> u.columns().size() == 1)
//                                          .map(u -> u.columns().get(0))
//                                          .collect(toSet());
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
   *    a/m1/$e:$(b),
   *    a/m2:c+5,
   *    a/m2/$e:$(c+5),
   *    a/m3:10
   * </pre>
   */
  public static List<Column> expandColumn(Column column,
                                          Map<String, String> aliased) {
    List<Column> newCols = new ArrayList<>();
    newCols.add(column);
    String colAlias = column.name();
    if (!colAlias.contains("/")) {
      if (column.metadata() != null) {
        Boolean derived = column.metadata().evaluateAttribute(DERIVED);
        if (derived != null && derived) {
          newCols.add(new Column(column.context,
                                 colAlias + "/$e",
                                 new UncomputedExpression(column.context,
                                                          unqualify(column.expression())),
                                 Types.TextType,
                                 null));
        }
        Map<String, Attribute> attributes = column.metadata().attributes();
//        if (unique && !attributes.containsKey(UNIQUE)) {
//          newCols.add(new Column(column.context,
//                                 colAlias + '/' + UNIQUE,
//                                 new BooleanLiteral(column.context, true),
//                                 Types.BoolType,
//                                 null));
//        }
        if (column.notNull() && !attributes.containsKey(REQUIRED)) {
          newCols.add(new Column(column.context,
                                 colAlias + '/' + REQUIRED,
                                 new BooleanLiteral(column.context, true),
                                 Types.BoolType,
                                 null));
        }
        for (Attribute attr: attributes.values()) {
          newCols.addAll(columnsForAttribute(attr, colAlias + '/', aliased));
        }
      }
    }
    return newCols;
  }

  /**
   * Returns column names which have been aliased to a different name. This is
   * used to rename the reference to those column names in expressions.
   */
  private static Map<String, String> aliasedColumns(Collection<Column> columns) {
    Map<String, String> aliased = new HashMap<>();
    for (Column col: columns) {
      Expression<?, ?> expr = col.expression();
      if (expr instanceof ColumnRef ref) {
        String name = ref.columnName();
        String alias = col.name();
        if (alias != null && !name.equals(alias)) {
          aliased.put(name, alias);
        }
      }
    }
    return aliased;
  }

  public PrimaryKeyConstraint primaryKey() {
    for (ConstraintDefinition c: constraints) {
      if (c instanceof PrimaryKeyConstraint) {
        return (PrimaryKeyConstraint)c;
      }
    }
    return null;
  }

  public List<ConstraintDefinition> constraints() {
    return unmodifiableList(constraints);
  }

  public void constraint(ConstraintDefinition c) {
    constraints.add(c);
    addAttributesForConstraints(this, c);
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
    Optional<ConstraintDefinition> opt = constraints.stream()
                                                    .filter(c -> c.name().equals(constraintName))
                                                    .findFirst();
    if (opt.isPresent()) {
      ConstraintDefinition c = opt.get();
      constraints.remove(c);
      if (c instanceof ForeignKeyConstraint fk
       && fk.context.structure.relationExists(fk.targetTable()) ) {
        Relation rel = fk.context.structure.relation(fk.targetTable());
        if (rel instanceof BaseRelation br) {
          br.removeDependentConstraint(fk);
        }
      }
//      return constraints.removeIf(c -> c.name().equals(constraintName));
      return true;
    }
    return false;
  }

  public List<ForeignKeyConstraint> dependentConstraints() {
    return unmodifiableList(dependentConstraints);
  }

  public void dependentConstraint(ForeignKeyConstraint c) {
    if (dependentConstraints.stream().noneMatch(c::sameAs)) {
      dependentConstraints.add(c);
    }
  }

  public void removeDependentConstraint(ForeignKeyConstraint c) {
    dependentConstraints.removeIf(c::sameAs);
  }

  /**
   * Returns the constraint on this table similar to the provided definition, or
   * null if none found. When comparing constraints, their names are ignored (i.e.
   * two constraints are equal if they have the same structure even if their names
   * are different) and the source table of the constraint definition is assumed
   * to be this table. The latter is because the constraint definition is attached
   * to a bigger definition (create table, alter table, etc.) which has the table
   * name; the constraint definition itself does not carry the table it is defined on.
   */
  public synchronized ConstraintDefinition sameAs(ConstraintDefinition def) {
    if (constraints != null) {
      for (ConstraintDefinition tableConstraint: constraints) {
        if (tableConstraint.sameAs(def)) {
          return tableConstraint;
        }
      }
    }
    return null;
  }

  public Map<String, Index> indices() {
    return Collections.unmodifiableMap(indices);
  }

  public Optional<Index> index(String name) {
    return indices.containsKey(name)
         ? Optional.of(indices.get(name))
         : Optional.empty();
  }

  public Optional<Index> index(List<String> columns) {
    return indices.values().stream()
                  .filter(i -> new HashSet<>(i.columns()).equals(new HashSet<>(columns)))
                  .findFirst();
  }

  public void index(Index index) {
    if (indices.containsKey(index.name())) {
      throw new IllegalArgumentException("Index named " + index.name()
                                       + " already exists on table " + name);
    }
    indices.put(index.name(), index);
  }

  public void removeIndex(String name) {
    index(name).ifPresentOrElse(
      this::removeIndex,
      () -> {
        throw new IllegalArgumentException("Could not find index named " + name
                                         + " on table " + this.name());
      });
  }

  public void removeIndex(Index index) {
    indices.remove(index.name());
  }

  private static void addAttributesForConstraints(BaseRelation rel, ConstraintDefinition constraint) {
    if      (constraint instanceof UniqueConstraint u) addUniqueConstraint(rel, u);
    else if (constraint instanceof CheckConstraint c) addCheckConstraint(rel, c);
    else if (constraint instanceof PrimaryKeyConstraint p) addPrimaryConstraint(rel, p);
    else if (constraint instanceof ForeignKeyConstraint f) addForeignConstraint(rel, f);
  }

  private static void addUniqueConstraint(BaseRelation rel, UniqueConstraint unique) {
    if (unique.columns().size() == 1) {
      /*
       * For unique constraints on single columns add a unique metadata attribute
       * to the column.
       */
      String colName = unique.columns().get(0);
      Column existingCol = rel.findColumn(colName);
      if (existingCol == null) {
        throw new TranslationException(colName + " not found in table " + rel.name);
      }
      Attribute attr = existingCol._attribute(UNIQUE, new BooleanLiteral(unique.context, true));
      rel.addColumnsForAttribute(attr, colName);
    }

    /*
     * For multi-column unique constraints add to the unique metadata attribute
     * of the table.
     */
    Attribute attr = rel.attribute(UNIQUE);
    List<JsonArrayLiteral> items = new ArrayList<>();
    if (attr != null) {
      items.addAll((List<JsonArrayLiteral>)(((JsonArrayLiteral)attr.attributeValue()).items()));
    }
    items.add(new JsonArrayLiteral(unique.context, unique.columns().stream()
                                                         .map(c -> new StringLiteral(unique.context, c))
                                                         .toList()));
    rel.attribute(UNIQUE, new JsonArrayLiteral(unique.context, items));
  }

  private static void addPrimaryConstraint(BaseRelation rel, PrimaryKeyConstraint pri) {
    if (pri.columns().size() == 1) {
      /*
       * Add primary key constraint to single .
       */
      String colName = pri.columns().get(0);
      Column existingCol = rel.findColumn(colName);
      if (existingCol == null) {
        throw new TranslationException(colName + " not found in table " + rel.name);
      }
      Attribute attr = existingCol._attribute(PRIMARY_KEY, new BooleanLiteral(pri.context, true));
      rel.addColumnsForAttribute(attr, colName);
    }

    /*
     * For multi-column primary constraints add to the unique metadata attribute
     * of the table.
     */
    rel.attribute(PRIMARY_KEY, new JsonArrayLiteral(pri.context,
                                                    pri.columns().stream()
                                                       .map(c -> new StringLiteral(pri.context, c))
                                                       .toList()));
  }

  private static void addForeignConstraint(BaseRelation rel, ForeignKeyConstraint f) {
    if (f.columns().size() == 1) {
      /*
       * Add foreign key references expressions to single columns.
       */
      String colName = f.columns().get(0);
      Column existingCol = rel.findColumn(colName);
      if (existingCol == null) {
        throw new TranslationException(colName + " not found in table " + rel.name);
      }
      Attribute keys = existingCol.attribute(REFERENCES);
      List<Literal<?>> array = new ArrayList<>();
      if (keys != null) {
        JsonArrayLiteral json = (JsonArrayLiteral)keys.attributeValue();
        array.addAll(json.items());
      }
      array.add(new JsonObjectLiteral(f.context,
                                      Arrays.asList(Attribute.from(f.context, "to_table", f.targetTable()),
                                                    Attribute.from(f.context, "to_columns", f.targetColumns().get(0)))));
      Attribute attr = existingCol._attribute(REFERENCES, new JsonArrayLiteral(f.context, array));
      rel.addColumnsForAttribute(attr, colName);
    }

    /*
     * Add as table attribute.
     */
    Attribute attr = rel.attribute(REFERENCES);
    List<JsonObjectLiteral> items = new ArrayList<>();
    if (attr != null) {
      items.addAll((List<JsonObjectLiteral>)(((JsonArrayLiteral)attr.attributeValue()).items()));
    }
    items.add(new JsonObjectLiteral(f.context,
                                    Arrays.asList(new Attribute(f.context,
                                                                "from_columns",
                                                                new JsonArrayLiteral(f.context,
                                                                                     f.columns().stream()
                                                                                      .map(c -> new StringLiteral(f.context, c))
                                                                                      .toList())),
                                                  Attribute.from(f.context, "to_table", f.targetTable()),
                                                  new Attribute(f.context,
                                                                "to_columns",
                                                                new JsonArrayLiteral(f.context,
                                                                                     f.targetColumns().stream()
                                                                                      .map(c -> new StringLiteral(f.context, c))
                                                                                      .toList())))));
    rel.attribute(REFERENCES, new JsonArrayLiteral(f.context, items));

    if (rel.context.structure.relationExists(f.targetTable())) {
      Relation relation = rel.context.structure.relation(f.targetTable());
      if (relation instanceof BaseRelation br) {
        br.dependentConstraint(f);
        if (f.targetColumns().size() == 1) {
          /*
           * Add foreign key referred by expressions to single target columns.
           */
          String colName = f.targetColumns().get(0);
          Column existingCol = br.findColumn(colName);
          if (existingCol == null) {
            throw new TranslationException(colName + " not found in table " + br.name);
          }
          Attribute keys = existingCol.attribute(REFERRED_BY);
          List<Literal<?>> array = new ArrayList<>();
          if (keys != null) {
            JsonArrayLiteral json = (JsonArrayLiteral)keys.attributeValue();
            array.addAll(json.items());
          }
          array.add(new JsonObjectLiteral(f.context,
                                          Arrays.asList(Attribute.from(f.context, "from_table", f.table()),
                                                        Attribute.from(f.context, "from_columns", f.columns().get(0)))));
          attr = existingCol._attribute(REFERRED_BY, new JsonArrayLiteral(f.context, array));
          br.addColumnsForAttribute(attr, colName);
        }

        /*
         * Add as table attribute.
         */
        attr = br.attribute(REFERRED_BY);
        items = new ArrayList<>();
        if (attr != null) {
          items.addAll((List<JsonObjectLiteral>)(((JsonArrayLiteral)attr.attributeValue()).items()));
        }
        items.add(new JsonObjectLiteral(f.context,
                                        Arrays.asList(new Attribute(f.context,
                                                                    "from_columns",
                                                                    new JsonArrayLiteral(f.context,
                                                                                         f.columns().stream()
                                                                                          .map(c -> new StringLiteral(f.context, c))
                                                                                          .toList())),
                                                      Attribute.from(f.context, "from_table", f.table()),
                                                      new Attribute(f.context,
                                                                    "to_columns",
                                                                    new JsonArrayLiteral(f.context,
                                                                                         f.targetColumns().stream()
                                                                                          .map(c -> new StringLiteral(f.context, c))
                                                                                          .toList())))));
        br.attribute(REFERRED_BY, new JsonArrayLiteral(f.context, items));
      }
    }
  }

  private static void addCheckConstraint(BaseRelation rel, CheckConstraint chk) {
    if (chk.columns().size() == 1) {
      /*
       * Add check constraint expressions to single columns
       */
      String colName = chk.columns().get(0);
      Column existingCol = rel.findColumn(colName);
      if (existingCol == null) {
        throw new TranslationException(colName + " not found in table " + rel.name);
      }
      Attribute checkAttr = existingCol.attribute(CHECK);
      Expression<?, ?> check = checkAttr == null ? null : checkAttr.attributeValue();
      check = check == null || check.equals(chk.expr())
            ? chk.expr()
            : new And(chk.context,
                      new GroupedExpression(chk.context, check),
                      new GroupedExpression(chk.context, chk.expr()));

      rel.addColumnsForAttribute(existingCol._attribute(CHECK, check), colName);
      rel.addColumnsForAttribute(existingCol._attribute(CHECK + "/$e",
                                                        new UncomputedExpression(chk.context,
                                                                                 unqualify(check))),
                                                        colName);
    }

    /*
     * For multi-column check constraints add to the check metadata attribute of
     * the table.
     */
    Attribute attr = rel.attribute(CHECK);
    List<UncomputedExpression> items = new ArrayList<>();
    if (attr != null) {
      items.addAll((List<UncomputedExpression>)((JsonArrayLiteral)attr.attributeValue()).items());
    }
    items.add(new UncomputedExpression(chk.context, chk.expr()));
    rel.attribute(new Attribute(chk.context, CHECK, new JsonArrayLiteral(chk.context, items)));
  }

  private void addColumnsForAttribute(Attribute attr, String colName) {
    for (Column col: columnsForAttribute(attr, colName + '/', aliasedColumns(columns))) {
      addOrReplaceColumn(col);
    }
  }

//  public void dependency(Relation dependency) {
//    if (dependencies == null) {
//      dependencies = new HashSet<>();
//    }
//    dependencies.add(dependency);
//  }

  private static final class Node implements Comparable<Node> {
    private Node(Node                 previous,
                 ForeignKeyConstraint constraint,
                 boolean              reverse) {
      this.previous   = previous;
      this.constraint = constraint;
      this.reverse    = reverse;
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
        st.append(previous);
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
    public  final boolean reverse;
    public  final int cost;
  }

  public record Link(ForeignKeyConstraint constraint,
                     boolean              reverse) {}

  public record Path(List<Link> elements, int cost) {
    public boolean hasReverseLink() {
      return elements.stream().anyMatch(Link::reverse);
    }
  }

  /**
   * Returns the list of foreign-key constraints connecting this relation to the
   * target, if such a path exists.
   */
  public Path path(String targetTable) {
    if (targetTable.equals(name())) {
      return new Path(emptyList(), 0);
    }
    Set<Node> explored = new HashSet<>();
    PriorityQueue<Node> toExplore = new PriorityQueue<>();
    Map<Node, Integer> toBeExplored = new HashMap<>();
    for (ConstraintDefinition c: constraints) {
      if (c instanceof ForeignKeyConstraint fk) {
        if (fk.forwardCost() >= 0) {
          Node node = new Node(null, fk, false);
          toExplore.add(node);
          toBeExplored.put(node, node.cost);
        }
      }
    }
    for (ForeignKeyConstraint c: dependentConstraints) {
      if (c.reverseCost() >= 0) {
        Node node = new Node(null, c, true);
        toExplore.add(node);
        toBeExplored.put(node, node.cost);
      }
    }
    while (!toExplore.isEmpty()) {
      Node node = toExplore.remove();
      toBeExplored.remove(node);
      explored.add(node);

      if ((!node.reverse && node.constraint.targetTable().equals(targetTable))
       || ( node.reverse && node.constraint.table().equals(targetTable))) {
        /*
         * Reached target table (goal)
         */
        List<Link> path = new ArrayList<>();
        int cost = node.cost;
        while (node != null) {
          path.add(new Link(node.constraint, node.reverse));
          node = node.previous;
        }
        Collections.reverse(path);
        return new Path(path, cost);

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
          if (c instanceof ForeignKeyConstraint fk) {
            if (fk.forwardCost() >= 0) {
              Node targetNode = new Node(node, fk, false);
              if (!explored.contains(targetNode)
               && !toBeExplored.containsKey(targetNode)) {
                toExplore.add(targetNode);
                toBeExplored.put(targetNode, targetNode.cost);

              } else if (toBeExplored.containsKey(targetNode)
                      && toBeExplored.get(targetNode) > targetNode.cost) {
                toExplore.remove(targetNode);
                toExplore.add(targetNode);
                toBeExplored.remove(targetNode);
                toBeExplored.put(targetNode, targetNode.cost);
              }
            }
          }
        }

        for (ForeignKeyConstraint c: rel.dependentConstraints) {
          if (c.reverseCost() >= 0) {
            Node sourceNode = new Node(node, c, true);
            if (!explored.contains(sourceNode)
             && !toBeExplored.containsKey(sourceNode)) {
              toExplore.add(sourceNode);
              toBeExplored.put(sourceNode, sourceNode.cost);

            } else if (toBeExplored.containsKey(sourceNode)
                    && toBeExplored.get(sourceNode) > sourceNode.cost) {
              toExplore.remove(sourceNode);
              toExplore.add(sourceNode);
              toBeExplored.remove(sourceNode);
              toBeExplored.put(sourceNode, sourceNode.cost);
            }
          }
        }
      }
    }
    return null;
  }

//  /**
//   * Returns the list of foreign-key constraints connecting this relation to the
//   * target, if such a path exists.
//   */
//  public List<Link> path(Relation target, boolean ignoreHiddenFields) {
//    Set<Node> explored = new HashSet<>();
//    PriorityQueue<Node> toExplore = new PriorityQueue<>();
//    Map<Node, Integer> toBeExplored = new HashMap<>();
//    for (ConstraintDefinition c: constraints) {
//      if (c instanceof ForeignKeyConstraint fk) {
//        if (fk.forwardCost() >= 0
//         && (!ignoreHiddenFields
//          || (fk.sourceColumns().stream().noneMatch(f -> !f.equals("_id") && f.charAt(0) == '_')
//           && fk.targetColumns().stream().noneMatch(f -> !f.equals("_id") && f.charAt(0) == '_')))) {
//          Node node = new Node(null, fk, false);
//          toExplore.add(node);
//          toBeExplored.put(node, node.cost);
//        }
//      }
//    }
//    for (ForeignKeyConstraint c: dependentConstraints) {
//      if (c.reverseCost() >= 0
//       && (!ignoreHiddenFields
//        || (c.sourceColumns().stream().noneMatch(f -> !f.equals("_id") && f.charAt(0) == '_')
//         && c.targetColumns().stream().noneMatch(f -> !f.equals("_id") && f.charAt(0) == '_')))) {
//        Node node = new Node(null, c, true);
//        toExplore.add(node);
//        toBeExplored.put(node, node.cost);
//      }
//    }
//    while (!toExplore.isEmpty()) {
//      Node node = toExplore.remove();
//      toBeExplored.remove(node);
//      explored.add(node);
//
//      if ((!node.reverse && node.constraint.targetTable().equals(target.name()))
//       || ( node.reverse && node.constraint.table().equals(target.name()))) {
//        /*
//         * Reached target table (goal)
//         */
//        List<Link> path = new ArrayList<>();
//        while (node != null) {
//          path.add(new Link(node.constraint, node.reverse));
//          node = node.previous;
//        }
//        Collections.reverse(path);
//        return path;
//
//      } else {
//        /*
//         * Expansion as per uniform cost search (from 'AI: a modern approach'):
//         *      for each action in problem.ACTIONS(node.STATE) do
//         *          child <- CHILD-NODE(problem, node, action)
//         *          if child.STATE is not in explored or frontier then
//         *              frontier <- INSERT(child, frontier)
//         *          else if child.STATE is in frontier with higher PATH-COST then
//         *              replace that frontier node with child
//         */
//        BaseRelation rel = node.reverse ? context.structure.relation(node.constraint.table())
//                                        : context.structure.relation(node.constraint.targetTable());
//        for (ConstraintDefinition c: rel.constraints) {
//          if (c instanceof ForeignKeyConstraint fk) {
//            if (fk.forwardCost() >= 0
//            && (!ignoreHiddenFields
//            || (fk.sourceColumns().stream().noneMatch(f -> !f.equals("_id") && f.charAt(0) == '_')
//            &&  fk.targetColumns().stream().noneMatch(f -> !f.equals("_id") && f.charAt(0) == '_')))) {
//
//              Node targetNode = new Node(node, fk, false);
//              if (!explored.contains(targetNode)
//               && !toBeExplored.containsKey(targetNode)) {
//                toExplore.add(targetNode);
//                toBeExplored.put(targetNode, targetNode.cost);
//
//              } else if (toBeExplored.containsKey(targetNode)
//                      && toBeExplored.get(targetNode) > targetNode.cost) {
//                toExplore.remove(targetNode);
//                toExplore.add(targetNode);
//                toBeExplored.remove(targetNode);
//                toBeExplored.put(targetNode, targetNode.cost);
//              }
//            }
//          }
//        }
//
//        for (ForeignKeyConstraint c: rel.dependentConstraints) {
//          if (c.reverseCost() >= 0
//          && (!ignoreHiddenFields
//          || (c.sourceColumns().stream().noneMatch(f -> !f.equals("_id") && f.charAt(0) == '_')
//          &&  c.targetColumns().stream().noneMatch(f -> !f.equals("_id") && f.charAt(0) == '_')))) {
//            Node sourceNode = new Node(node, c, true);
//            if (!explored.contains(sourceNode)
//             && !toBeExplored.containsKey(sourceNode)) {
//              toExplore.add(sourceNode);
//              toBeExplored.put(sourceNode, sourceNode.cost);
//
//            } else if (toBeExplored.containsKey(sourceNode)
//                    && toBeExplored.get(sourceNode) > sourceNode.cost) {
//              toExplore.remove(sourceNode);
//              toExplore.add(sourceNode);
//              toBeExplored.remove(sourceNode);
//              toBeExplored.put(sourceNode, sourceNode.cost);
//            }
//          }
//        }
//      }
//    }
//    return emptyList();
//  }

  public UUID id() {
    return id;
  }

  public BaseRelation id(UUID id) {
    return new BaseRelation(context,
                            id,
                            name,
                            displayName,
                            description,
                            attributesList(),
                            columns,
                            constraints);
  }

  /**
   * Constraints set on the table (including field constraints but excluding non-null field's constraints).
   */
  private final List<ConstraintDefinition> constraints = new ArrayList<>();

  private final Map<String, Index> indices = new HashMap<>();

  /**
   * Foreign key constraints pointing to this relation.
   */
  private final List<ForeignKeyConstraint> dependentConstraints = new ArrayList<>();
}