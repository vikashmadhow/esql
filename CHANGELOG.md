# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Planned]
### To add
- Array, list, map, set operations.
- Manual transaction control (begin, commit, rollback, set isolation level, 
  savepoints, locking). Currently, transaction boundary is aligned to connection.
- Produce formatted multi-line translation to SQL.
- Assignment to array, list and map subscripts.
- Operations on JSON objects.
- Support for merge queries.

- Make all expressions and functions (where possible) executable.

- Support for creating and using views (including materialised views).

- Complete documentation of ESQL grammar.
- Fully document purpose and usage.
- Support Oracle and Firebird.
- Make into Java 9 module.
- Support for `within group` for ordering in string and array aggregate functions.

- Implement state-based model where an ESQL program is specified as a target state 
  instead of a sequence of imperative statements. For instance, a table definition
  is specified instead of a `create table` and/or a set `alter table` statements.
  The table definition will then be realised with a sequence of `create table` 
  and `alter table` statements. Currently, the `create table` statement already 
  work in this manner. In the state-based model this is applied to all statements
  where this make senses. For example, a set of records is specified as the target
  content of a table which will then be merged with the existing data through a 
  merge statement and/or a set of insert and update statements.

- `pkey` macro expands to the primary key columns of a table.
- `fkey` macro expands to the columns of a foreign key between two tables.

- Virtual tables implemented as Esql transformers: A virtual table looks and 
  behaves like a normal table but does not need to exist in the database or may
  correspond to multiple tables. Queries and updates to virtual tables are rewritten
  as other queries/updates or whole programs.

- `Truncate table` statement.

- 'Change notification' and subscription.
- Table time-travel.
- Snapshots.

- Undo and redo maintaining coherent table state (undoing a transaction requires
  undoing on all (linked?) tables touched by the transaction).

- A special keyword (`this`?) to reference the table being implicitly queried. 
  This is useful in expressions defined in attributes which will be executed against
  the containing table.
- Every expression should return a `Result` to normalise the execution of any 
  ESQL expression (need to find way that this does not affect performance unduly).
 
### To optimise
- When filtering a `With`, do not apply filter on a CTE if that CTE inner joins 
  with another CTE, directly or transitively, which has already been filtered.
  (requires breadth-first mapping instead of the depth-first mapping of ESQL.map)

### To test
- Test composition of select statements (union, intersect, except).
- Test query translation and mapping of attributes (special columns such as `a/m1` 
  to attributes of their respective columns).
- Test result (returning clause) from insert, update and delete.
- Test loaded metadata values.
- Test lateral joins.
- Test all normal functions.
- Test translation of grouping by complex expressions (subqueries) in SQL Server.
- Test translation of distinct over multiple columns in SQL Server.
- Test window functions.
- Test performance.

### To fix
- Apply result and column metadata overloading in column list expansion 
  (currently, the overridden metadata are not being considered). (create tests 
  for metadata overriding)
- Modify queries return values don't seem to be supported correctly for SQL 
  Server.

## [1.7.0] (Planned)
### Database stored functions and triggers in ESQL

## [1.6.0] (Planned)
### Mirror tables

## [1.5.32] - 2023-06-29
### Added
- Parsing of negative integers and floating points as literals, instead of negated
  positive integers or floating points. This is useful in contexts where literals
  only are expected (e.g. JSON objects or arrays).

## [1.5.31] - 2023-06-23
### Fixed
- The current environment was not being passed during the translation and  execution 
  process leading to NPEs. This has been fixed.

## [1.5.30] - 2023-06-20
### Added
- Forward and reverse cost of foreign key constraints are added to the metadata
  of base relations. These costs can then be used by clients to inform path search
  between relations.

## [1.5.29] - 2023-06-18
### Fixed
- `required` expressions when creating `struct`s with `StructInitializer` are no
  longer interpreted as `non-null` expressions as these are only valid for tables.
  This fixes issues where required expressions were not being applied to custom 
  structures, such as those defined for report parameters.

## [1.5.28] - 2023-06-07
### Added
- The type of `case` expressions is now properly computed as the type of the first 
  non-null `then` value. Previously the predicates (`when` clause) were included
  in this type computation resulting in a wrong `bool` type for all case expressions.
- Type of `concatenation` is now always `TextType` irrespective of its arguments.

## [1.5.27] - 2023-06-07
### Fixed
- Support for ESQL expressions for boolean attributes in `CreateInitializer`.

## [1.5.26] - 2023-06-02
### Fixed
- Single-quotes inside `StringLiteral`s are now escaped when translating to ESQL.

## [1.5.25] - 2023-06-01
### Added
- `randstr` ESQL function to generate a random string of arbitrary size, implemented
  in PostgreSql as `_core.randomstr`.
- `obfuscate` and `unobfuscate` ESQL function implemented in Postgresql.
- `checkdigit` ESQL function takes a number and appends a check digit to it computed 
  using the GTIN-13 method (https://www.gs1.org/services/how-calculate-check-digit-manually).
- `cast` and `trycast` on dates on SQL Server uses `try_parse` to attempt parsing
  the date through different cultures until one is found or the cast fails (with
  an exception in case of `cast` and `null` in case of `trycast`).

## [1.5.24] - 2023-05-31
### Added
- `randstr` ESQL function to generate a random string of arbitrary size, implemented
  in SQL Server as `_core.randomstr`.

## [1.5.23] - 2023-05-29
### Added
- Remove `COLLATE` from translation of equality to SQL Server as it breaks 'group 
  by rollup'. SQL Server are now assumed to be in the correct collation which may
  result in some unpredictable behaviour on that database (i.e. the behaviour now
  depends on which collation is in use).

## [1.5.22] - 2023-05-25
### Added
- Acquiring locks on `_core._temp_history` when reading and transferring coarse-
  grained history at end of transaction commit now times out after a very short
  period (< 100ms) to not block access to this table. When a lock acquisition 
  fails, it is retried up to 10 times. This resolves most blocking queries on this
  table.
- `explicit` keyword in `select` implemented. When this keyword is present in a
  `select`, the columns in its column list are not expanded to include their 
  metadata.

## [1.5.21] - 2023-05-25
### Added
- Method `replaceTable` in `ComposableFilter` which returns a new `ComposableFilter`
  with its table replaced if it is contained as a key in a given replacements map. 
  The replacement is performed recursively on all children of this filter.

## [1.5.20] - 2023-05-23
### Added
- Simplified and increased compatibility of grammar and parsing for short-hand 
  forms of coalesce (?), ternary conditionals ( -> | ) and concatenation ( || ).

## [1.5.19] - 2023-05-22
### Added
- Ternary expressions of the form `e1 -> e2 | e3` compatible with ESQL v1 is now
  supported (the syntax in ESQL v1 was `e1 -> e2 : e3` while it is `e1 -> e2 | e3`
  in v2 as the `:` character is now used for aliasing). This is in addition to 
  the support of the new form `e2 if e1 else e3`.
- Coalesce expressions of the form `e1 ? e2 (?...)` compatible with ESQL v1 is 
  now supported.

## [1.5.18] - 2023-05-19
### Added
- `toMap` method added to `QueryParams` returning a map view of the parameters 
  with the parameter values in their raw form (not as ESQL expressions). This 
  view provides for a simpler access to the parameter values.

## [1.5.17] - 2023-05-17
### Added
- Fractional part of seconds in ESQL time literals now allows more than 3 digits
  to cater for nanoseconds time resolution.

## [1.5.16] - 2023-05-07
### Added
- Methods in `Result` catches exception and sets the `rollbackOnly` flag of its
  underlying `EsqlConnection` so that the connection is not committed on close.
- `Cast` and `TryCast` surrounds ESQL casting expression in brackets. Without this
  casting on compound expressions produces wrong translations. E.g. casting `x + 1`
  to type `T` will now result in `(x + 1)::T` instead of the erroneous `x + 1::T`.

## [1.5.15] - 2023-05-03
### Added
- Translation of string literals to Javascript uses backquotes (\`) when the 
  string contain carriage returns (`\n`).

### Fixed
- `is null` translation to SQL Server fixed to use the appropriate boolean 
  translation based on the position where it is specified in the query.

## [1.5.14] - 2023-05-02
### Added
- Arguments of `concat` function is cast to `text` type before concatenation. 
  This removes certain error cases from certain databases such as when concatenating
  a `string` to `uuid` on SQL Server.
- Extends detection of where a boolean expression is allowed on SQL Server. This
  resolves an issue where `bool` columns where being wrongly converted to an 
  equality expression in the `set` clause of an `update`
- Extends `cast` and `trycast` on SQL Server to properly cast value of `bool` 
  type.

## [1.5.13] - 2023-05-02
### Fixed
- Fixed parsing of `convert` expression in SQL Server.

## [1.5.12] - 2023-04-27
### Added
- A column can be composed without specifying any target table; in such cases, 
  it is assumed that the required tables are already included in the `from` 
  clause of the select. All column references must now be fully and correctly 
  qualified for this composition to work. If a column is not qualified, the 
  composition fails with an exception. If it is qualified with a wrong qualifier 
  (one that is not associated to a table in the table list of the query), the 
  composition is silently ignored. This is because the composition may succeed 
  in another part of the query (such as when composing on a select containing an
  inner select or is part of a set-composed select).

## [1.5.11] - 2023-04-27
### Added
- `ComposableColumn` can now have a `null` table whereby no table link is 
  attempted. Instead the expression of the composable column is simply added
  to the column list of the `select`.
- Column expression is identified as part of an aggregate if it calls any aggregate
  function without a window clause; previously only expressions that were a single
  function call to an aggregate function would be identified as an aggregate. This
  change allows aggregate function to be nested into another expression such as
  a concatenation or bracket-grouping.
- New `round` function with correct translation for Postgresql, Sql Server, 
  Javascript and Esql. Execution of that function in ESQL uses the `Numbers`
  class for more accurate rounding.
- `number` ESQL abstract type mapped to `Double` Java class.
- Return and parameter types of `inmonth` macro function specified to help with
  type inference.

## [1.5.10] - 2023-04-24
### Added
- `DefaultExecutor` catches exception and sets the `rollbackOnly` flag of its 
   underlying `EsqlConnection` so that the connection is not committed on close. 

## [1.5.9] - 2023-04-20
### Added
- `Result` stores the connection that created it in a public final field.
- The `db` member of the `Result` class has been removed as it can now be accessed
  through the connection that produced the result.

## [1.5.8] - 2023-04-13
### Added
- `addColumn` method in `Database` changed to check whether column already exists
  in `_core.columns` before inserting; in case of prior existence, the column 
  definition is updated.
- `addColumnMetadata` method in `Database` changed to check whether column attribute
  already exists in `_core.column_attributes` before inserting; in case of prior 
  existence, the attribute value is updated.

## [1.5.7] - 2023-04-11
### Added
- `TableInitializer` creates primary key for `_id` column or `_primary_key` 
   metadata. 
- `columnExists` method in `Database` which returns true if a column exists in a
  table as set in the `information_schema.columns` view.

### Fixed
- Corrected an error when a column in table was changed from derived to non-derived
  or vice-versa. The table structure in the database and the in the _core tables
  would be out-of-sync in some cases, which would then cause an exception on the
  next run.

## [1.5.6] - 2023-04-10
### Added
- `TableInitializer` creates primary key for `_id` column or `_primary_key` 
   metadata. 

## [1.5.5] - 2023-04-06
### Added
- `makeLiteral` builds `JsonObjectLiteral` from `JSONObject` or `Map`.
- `makeLiteral` now builds `JsonArrayLiteral` from `JSONArray` instead of 
  `BaseArrayLiteral`.
- `CreateInitializer` can now work with metadata expressions of different types
  (instead of coercing them as an expression string).

## [1.5.4] - 2023-03-17
### Added
- `qualify` method in `ColumnRef` now takes a new `replaceExisting` argument 
  which, when true (default), sets all qualifiers in the ESQL statement with the
  provided one. When false, the provided qualifier is set only column references
  without a qualifier.

## [1.5.3] - 2023-03-15
### Added
- Utility `value` method in `Result` taking a default value to return when the
  column value is null.

### Fixed
- History tables are created before the tables if the target table already exists,
  otherwise they are created after.

## [1.5.2] - 2023-03-10
### Added
- Translation of `select`, `update`, `delete` and `insert` to Javascript for 
  execution on the client-side. This is primarily for translating select expressions
  in metadata.

## [1.5.1] - 2023-02-25
### Added
- Keep metadata of the source columns in the history table and add READONLY=true.
  This allows the history data to have the same look and feel as the table data.
- History data is set in a separate group for layout purposes. They also have 
  metadata to make them read-only and the change event is not linked to a list 
  of values. 
- Transaction id, user and time columns in history table are now indexed.

## [1.5.0] - 2023-02-24
### Fine-grain history and notification
A history table records all changes made to a table, which could be used to revert
a table to a previous state, track changes, create snapshots of the data at specific 
points in time, and so on.

## Design
History of a table is recorded as the sequence of significant events on the table. 
Recording of history is opt-in through the presence of a metadata attribute in 
the table at creation time. For example, history is enabled for the following table:

```
  create table X({
      name: 'X', 
      description: 'X table',
      history: true
    }
    _id     uuid not null,
    name    string not null, 
    address string,
    b_id    uuid,
    primary key(_id),
    unique  (name),
    foreign key(b_id) references B(_id)
  )
```

A history table is created for every table for which history is enabled, mirroring
its structure and adding some columns to keep information on the event generating
the history record. Following is the history table created for the above table:

```
  create table X$history({
      name: 'History of X' 
    }
    _id uuid not null,
    name string not null, 
    address string,
    b_id uuid,
    
    history_change_trans_id string,
    history_change_event int,
    history_change_time datetime,
    history_change_user string
  )
```

Some constraints such as primary key, uniqueness and foreign keys are not replicated
in the history as they would not hold: the history table contains a record for 
each change made to the table and thus would not maintain uniqueness of any column.
History table entries are also permanent and thus cannot maintain foreign key 
relationships which may change with time. Non-null constraints, however, can still
be maintained in the history.

4 columns are added to the history table to capture information on the specific 
event leading to that history entry:
1. **history_change_trans_id**: the unique identifier of the transaction during which the
   event happened.
2. **history_change_user**: the user at the source of this event. This is informational
   only and does not need to be a valid user.
3. **history_change_event**: a code identifying the event. E.g. *I=insert, D=delete,
   F=update_from and T=update_to*.
4. **history_change_time**: the date and time when the event happened.

## [1.4.4] - 2023-02-20
### Fixed
- `find` method in `QueryParams` fixed to not create an `Optional` around a null
  parameter value which is illegal. Instead `Optional.empty` will be returned and
  the `get` method has been modified to properly handle parameters with `null` 
  values independently of the `find` method.

## [1.4.3] - 2023-02-16
### Added
- `fractional` ESQL super type mapped to `Double` Java class. 
- `integral` ESQL super type mapped to `Long` Java class. 

## [1.4.2] - 2023-02-16
### Added
- Support for `select` sub-query in `in` operator. 

## [1.4.1] - 2023-02-16
### Added
- Support for `distinct` keywords in table returning functions which eliminates
  duplicated rows from the returned table.

## [1.4.0] - 2023-02-15
### Select from a function (acting as a dynamic table, e.g. `select label from t:joinlabel(null, '_id', 'a', 'S')`
- ESQL syntax rules have been updated to accept a function call in the `from`
  clause of a query. In this form, the function is expected to return a table
  that can be queried and/or combined using relational join operators with other
  tables.
- `string_split` has been implemented as a table-returning function that uses
  this new syntax to allow its result to be queried and combined with other tables.

## [1.3.12.2] - 2023-02-09
### Fixed
- Fixed NPE in `CompositeSelect` passing a `null` instead of the environment. 

## [1.3.12.1] - 2023-02-05
### Fixed
- Fixed ESQL translation of `trycast`.

## [1.3.12] - 2023-02-04
### Added
- `unfiltered` keyword in `select`, `update` and `delete` which, when present,
  disables filter composition on the query (or part of the query if it is part
  of a complex composition or common-table-expression `with` query).
- `CreateInitializer` can now optionally treat required as non-null (default)
  but preserved in metadata for client-size validation without being enforced at 
  the database level.

## [1.3.11.1] - 2023-01-31
### Fixed
- Fixed `AddMonths` translation on Postgresql. 

## [1.3.11] - 2023-01-31
### Added
- try-cast operator `?:` attempts to cast a value to a type and returns null 
  instead of failing if the value cannot be converted to the type.
- Support for standard cast syntax (`cast(expr as type)`).
- Long form try-cast syntax (`trycast(expr as type)`).

## [1.3.10] - 2023-01-28
### Added
- `TableInitializer` creates derived column when the type is set to `#computed`.
- `Create` interface to tag subclasses of `Define` that creates database objects.
- Common code in `CreateStructBuilder` and `CreateTableBuilder` refactored into
  `CreateBuilder` abstract class.
- Common code in `StructInitializer` and `TableInitializer` refactored into
  `CreateInitializer` abstract class.
- Translation of `year` and `month` function to Javascript uses methods in 
  Javascript `Date` object instead of those in the `moment` library as the latter
  is deprecated and no longer expected to be used on the client-side.

## [1.3.9] - 2023-01-25
### Added
- `TableInitializer` creates unique constraints from `unique` attribute in definition.
- `TableInitializer` creates foreign key constraints from `link_table` and `link_code`
  attributes in definition.

## [1.3.8] - 2023-01-24
### Added
- `TableInitializer` for creating `table` defined in a YAML file or from other
  hierarchical formats.

## [1.3.7] - 2023-01-06
### Added
- Implement `order` when composing `ComposableColumn` in `select`s.

### Fixed:
- Translation of `SelectExpression` in `Order` forcibly translated to strings.

## [1.3.6] - 2023-01-05
### Added
- `get` method in `SimpleColumn` now takes an optional default value to return 
  when the attribute is not present in the column.
- `is` method in `SimpleColumn` now takes an optional default value to return 
  when the attribute is not present in the column.
- `metadata` and `context` included in `SimpleColumn` which is needed in cases 
  where the original column needs to be reconstructed (or a similar structure
  needs to be built).

### Fixed
- Fixed automatic grouping of composable columns which was not grouping some
  columns not already subsumed by existing groupings.

## [1.3.5] - 2022-12-28
### Added
- `and` and `or` added to `SelectBuilder`, combines expression with the `where` 
  clause, if any, of the `select` being built. If there are no `where` clause, 
  the added expression becomes the `where` clause.

## [1.3.4] - 2022-12-27
### Fixed
- Do not apply filter to a select query without a from clause (e.g. `select 
  nextvalue('numgen')`) as this would cause a NPE. 

## [1.3.3] - 2022-12-27
- Common metadata attributes names updated in `Attributes` class.

## [1.3.2] - 2022-12-26
- `offset` and `limit` in `SelectBuilder` can now take an expression as their
  argument (instead of a raw string that will be parsed into an expression).

## [1.3.1] - 2022-12-25
- `UncomputedExpression` is translated to Javascript when sent to client. Previously
  it was being translated to ESQL.
- Result attributes are returned as expressions in the `Result` instead of text 
  literals. This allows for them to be computed dynamically on the client side.

## [1.3.0] - 2022-12-18
### Composing `select` with same-table or linked-table column expressions.
- ESQL already allows composing filter expressions with queries to create queries
  where a table is filtered by a condition containing columns belonging to the 
  table or to tables that can be reached through one or more joins. For example,
  composing the filter `{condition: x=5, table: Z}` with the query 
  `select a, b from X` and given that there is a relationship `X` from `X` to `Z`
  through `Y`, a resulting query such as the following could be produced:
  ```
    select a, b 
      from x:X 
      join y:Y on x.y_id=y._id
      join z:Z on y.z_id=z._id
    where z.x=5
  ```
  This query filters table `X` by a condition defined on a column defined on an
  indirectly linked table.

  A related problem would be to compose a set of columns as the projection of a 
  `select` query. For example composing `{column: label(x), table: Z}` onto
  `select a, b from X where b < 3` given the same relationships as above would
  result in the following query:
  ```
    select a, b, label(z.x) 
      from x:X 
      join y:Y on x.y_id=y._id
      join z:Z on y.z_id=z._id
     where b < 3
  ```
  In the case where the whole projection is defined as composable columns, the 
  query to compose with would be invalid under current ESQL syntax. I.e., a query
  such as `select from x:X` would not parse. A simple solution would be to have
  a special column that would be eliminated during composition. E.g., a candidate 
  for such a special column could be `___`. This would parse as a valid column 
  name in ESQL and is an unlikely column in a SQL database. Further, this column
  looks like a blank space that needs to be filled and is thus a rather intuitive  
  representation of a placeholder column that will be replaced with composed 
  columns. In the event that the intent of the query is to target a column named
  `___` in the table, column composition should not be used.

  Columns are also specified in `group by` and `order by`. Thus,
  composable columns must specify whether, in addition to the projection list, 
  they must appear in any of those clauses. For example, composing columns
  ```
  [
   {column: b, table: X, group: 'rollup'},
   {column: x, table: Z, alias:z, group: 'normal', order: 'desc'}, 
   {column: sum(x), table: Z},
   {column: avg(b if z.a < 5 else c), table: Y}
  ]
  ```
  onto the query `select a from X where b < 3 order by a`
  ```
    select x.a, x.b, z.x, sum(z.x), avg(y.b if z.a < 5 else y.c)
      from x:X 
      join y:Y on x.y_id=y._id
      join z:Z on y.z_id=z._id
     where x.b < 3
     group by rollup(x.a, x.b, z.x) 
     order by x.a, z.x desc
  ```
  Further, in this version, the composable filters will be extended to take a 
  `grouped` parameter. When set, the filter is on an aggregrate expression which
  is set in the `having` clause of the query (instead of the `where` clause as 
  for normal filters). For example, additionally composing the filter 
  `{condition: sum(x) < 1000, table: Z, grouped: true}` on the previous query 
  results in the following:
  ```
    select x.a, x.b, z.x, sum(z.x), avg(y.b if z.a < 5 else y.c)
      from x:X 
      join y:Y on x.y_id=y._id
      join z:Z on y.z_id=z._id
     where x.b < 3
     group by rollup(x.a, x.b, z.x) 
    having sum(z.x) < 1000
     order by x.a, z.x desc
  ```

## [1.2.14] - 2022-12-06
### Fixed
- `like` and `ilike` translation on databases other than SQL Server.

## [1.2.13] - 2022-12-04
### Added
- `StructInitializer` accepts types as both the existing `type` or the newer
  `_type` attribute.

## [1.2.12] - 2022-11-25
### Added
- Do not expand column list when select appears in the `set` clause of an `update`.

## [1.2.11] - 2022-11-24
### Added
- `postInit` method in `Extension` which is called after initialisation and can
  contain code that requires the extension to have been installed. E.g., an 
  initializer for reports that requires the report extension to be available.

## [1.2.10] - 2022-11-24
### Added
- Utility methods for simpler access to attribute values in `SimpleColumn`. 

## [1.2.9] - 2022-11-22
### Added
- Method `cols` to relation which returns its list of columns as `SimpleColumn`s
  which is a Java record form of columns. This structure is easier to access and
  work with by clients.

## [1.2.8] - 2022-11-22
### Added
- Add method `get` in `QueryParams` to get the value of a parameter or throw a
  `NotFoundException` (the `find` method does the same thing but returns an 
  `Optional`).

## [1.2.7] - 2022-11-21
### Fixed
- `RESTRICT` is translated to `NO ACTION` on SQL Server as this is the equivalent
  syntax on that database.

## [1.2.6] - 2022-11-11
### Added
- A spurious ending comma (`,`) is now allowed at the end of any literal list 
  including at the end of JSON array literals.

## [1.2.5] - 2022-10-31
### Added
- Unique columns added to table metadata so that constraints can be applied on 
  the client side when editing.

## [1.2.4] - 2022-10-27
### Fixed
- The sequence name in `nextvalue` is now properly converted to a valid table name 
  for PostgreSQL (including double-quoting schema and table name) and then inserted
  between single quotes.

## [1.2.3] - 2022-10-27
### Added
- A spurious comma (`,`) is now allowed at the end of an attribute list (in metadata
  and JSON object definitions). Such commas are a common source of syntax errors
  which will now parse correctly.
- Derived columns which are redefined as non-derived and vice-versa in a `CreateTable`
  statement are not supported by dropping the existing column and re-adding with
  the new status.

## [1.2.2] - 2022-10-17
### Fix schema creation
#### Fixed:
- Fix creation of schema which was not aligned with ongoing transaction and could
  fail in some circumstances

## [1.2.1] - 2022-10-17
### Sequence support; finalise index support with `drop index` implementation
#### Added:
- Parsing and execution of `drop index` statement in ESQL.
- Parsing and execution of `create sequence` statement in ESQL.
- Parsing and execution of `drop sequence` statement in ESQL.
- Parsing and execution of `alter sequence` statement in ESQL.
- `createSchema` method added to `Database` to generically create a schema in 
  the target database as needed.
- `nextvalue` function to get the next value from a sequence.

## [1.2.0] - 2022-10-15
### Structure change and coarse history notifications
#### Added:
- Structure change detection and notifications (on creation, deletion and 
  modification of tables).
- Coarse grain change events include flags for the presence of insertions, 
  updates and deletions in the change.

#### Changed:
- Syntax of the `alter column` part of `alter table` statement changed to 
  disambiguate between renaming a column name and changing its type.
- Adding a column to an existing table using `alter column` now requires the
  `column` keyword before the column definition; this is similar to the form
  of this statement in most databases and is also more intuitive.

#### Fixed:
- Statement to change name of table in `_core.relations` was missing the closing
  single-quote around the new name.
- `alter column` part of `alter table` statement was not being parsed by the 
  syntax analyser.
- Syntax for renaming column in Postgresql corrected.

## [1.1.3] - 2022-10-13
### Added:
- Keep a sequence number in `_core.columns` to represent the order of the column 
  definition in `create table` or `create struct` and order by it when loading 
  columns. This makes for a more predictable behaviour when changing the structure 
  of tables.

### Fixed:
- Column list of subqueries are no longer expanded where the subquery is part of
  the `where` clause of the outer query.

## [1.1.2] - 2022-10-12
### Fixed:
- `await` added to call to `$exec.concat()` in the Javascript translation of 
  the concatenation operation.

## [1.1.1] - 2022-10-06
### Added:
- `READONLY` attribute added to `Attributes` class.

## [1.1.0] - 2022-09-27
### Added:
- Component type must now be specified in base array literal; without the explicit
  type, the grammar is ambiguous and the base array literal can be interpreted as
  a JSON array literal leading to erroneous translation.
- `$changed` special metadata attribute which is added to records and columns (on
  the client side) when their values are changed. This is used for tracking
  elements of records which need to be inserted/updated in the database.
- `inarray` function returns true if an element is in an array and is translated
  using array operations on database that has such support or `string_split` on
  SQL Server. 
  - To ease support for `string_split` the internal format for emulated string 
    arrays (in databases that do not have intrinsic support for arrays such as 
    SQL Server) has changed: the prefix and suffix (`[]`) has been dropped and 
    the individual values are no longer quoted if they are strings. The separator 
    has been changed from `,` to `|` which, as a less frequent character in strings,
    is less likely to already exist in an element.  

## [1.0.18] - 2022-09-26
### Added:
- Translation of select expression to Javascript using the support function
  `$exec.select`.

## [1.0.17] - 2022-09-26
### Changed:
- The set clause of an `Update` was previously kept in a `Metadata` object as the
  latter's structure of a map of names to expressions is the same as expected for
  the set clause. `Metadata` however is a subclass of `Define` which prevents
  macro expansion in its descendents. This is the correct behaviour for metadata
  as it allows for metadata attributes to be sent in its unexpanded form to the
  client where it might be expanded differently than on the server. However, this
  behaviour is incorrect for the update set clause. As such a new structure called
  `UpdateSet` has been created for this purpose; it is similar in structure to
  `Metadata` except that it does not descend from `Define`.

## [1.0.16] - 2022-09-25
### Fixed:
- Translation of logical operators in SQL server was not taking into account the
  context in all cases (logical operators can only appear in certain part of SQL
  Server dialect as the latter does not have a boolean type). This is now fixed
  with the generated translation changed based on translation parameters set by
  ancestor nodes in the parsed tree of the statement.

### Removed:
- Deprecated target JSON has been removed.

## [1.0.15] - 2022-09-23
### Changed:
- Filters are applied  prior to typed macro expansion in the execution pipeline
  allowing filters to contain typed macros. The previous pipeline assumed that
  filters do not contain any typed macros.

### Fixed:
- Translation of select expression in binary operators was failing because of 
  casting issue: select expressions are translated to a `QueryTranslation` while
  the translation of a binary operator is a string. The select expression is now
  cast to a string in the translation method of `BinaryOperator`. 

## [1.0.13] - 2022-09-22
### Changed:
- Overwrite key name changed from `$overwrite$` to `$overwrite` to align with the
  naming convention of the other initializer keys.

### Removed:
- Removed initializer key SOURCE_FILE as this is no longer used.

## [1.0.12] - 2022-09-20
### Changed:
- Casting syntax changed from `type`<`expr`> to `expr`::`type` as the former 
  can be misinterpreted as relational operation (because of the `<` and `>`). 

### Fixed:
- `bpchar` type in PostgreSQL mapped to `char` type to fix errors in default 
  expressions read from the information_schema of that database. 

## [1.0.11] - 2022-09-19
### Added:
- New method `compatibleType` on interface `Type` returning a set of types 
  compatible with the current type. Compatible types allow operations between
  them without any explicit casting. By default, the set of compatible types for
  a type is a singleton set containing the type itself.

## [1.0.10] - 2022-09-12
### Changed:
- Some attributes which take a default value of false have been renamed accordingly.

## [1.0.9] - 2022-09-07
### Added:
- `init(Database)` method restored in `Initializer` interface as it allows for
  initializers to create objects that do not necessarily fit in a hierarchical
  model. E.g., creation of base tables.

## [1.0.8] - 2022-09-06
### Added:
- Column names in the `set` clause of an `update` statement can now be escaped to 
  refer to columns whose names are reserved words (or using special characters).

## [1.0.7] - 2022-09-03
### Added:
- Automatically added attributes `check` and `expression` have been renamed to 
  `_check` and `_expression`, respectively, to be consistent with other automatically 
  added attributes.

## [1.0.6] - 2022-09-02
### Added:
- Add `using` clause to `alter type` statement in Postgresql to support additional
  column type conversions (e.g. string to array of strings). Not all type conversions
  are supported and some will result in an error.
- Automatically added attributes to metadata (`type`, `references`, `referred_by`
  and `primary_key`) are not prefixed with an underscore (`_`) to signify that
  they are for system use and should be hidden from the user during normal 
  operations.

### Changed:
- `Required` attribute is now added only when the column is non-null; it was being
  added in all cases previously.
- Attributes and columns whose values are by default true must be explicitly
  provided only to set them to false. This can be counter-intuitive as the 
  presence of something is required to disable it. An example of this is the 
  `show` attribute. By default, this is `true`, meaning that the column is shown 
  in all context (viewing, editing, etc.). To hide the column, the attribute 
  `show` has to be specified and set to `false`. All such variables and column 
  names have been deprecated and replaced with their opposites (`show -> hide`, 
  `browse -> browse_hide`, `can_delete -> no_delete`, etc.).
- The spurious default implementations of the `add` and `get` methods (returning 
  null) of the `Initializer` interface has been removed to force subclasses to 
  provide a working concrete implementation.
 
### Removed:
- `init(Database)` method removed from `Initializer` interface as it is no longer
  used. This is a remnant of the previous simpler initializer model where this 
  was the only method containing all the logic of the initializer.

### Fixed:
- Added named-arguments block to translation of the concatenation operation to 
  Javascript as expected by the client-side executor (the `$exec` object).

## [1.0.5] - 2022-08-12
### Added:
- Backquote can be added to multi-line strings by escaping them (\`).
- Single-quotes are not escaped automatically in multi-line strings as this is
  not necessary and probably unexpected behaviour in such strings which should
  generally allow as much raw input as possible.
- Special key names for object's `display_name`, `description` and `metadata` 
  added to `Initializer`.

### Fixed:
- A double single-quote ('') in a string literal is now properly parsed as the
  escaped form of a single-quote.
- Change `_toString` of `NamedArgument` to display its new correct syntax (using
  `=` instead of `:=`).
- `add` method in `Initializer` now checks type of definitions (list or map) and
  correctly route call to the appropriate method. 

## [1.0.4] - 2022-08-08
### Changed:
- `StringLiteral` no longer includes the surrounding single-quotes in its internal
  value. This makes it simpler to work with the single-quotes inside strings which
  need to be escaped.

## [1.0.3] - 2022-08-07
### Added:
- Utility `columnMap` method added to `Relation` to return a map of column names
  to columns. This is useful for direct access of columns by name but will miss
  columns with the same names (this can happen for joins, e.g.). When there are
  more than one column with the same name, only one, selected arbitrarily, will 
  be in the map.
- New `add` method in `Initializer` taking a list as the definition of an object
  (instead of a map). This is useful for creating objects which are defined as
  a sequence of items. E.g., a program consisting of a sequence of function calls.
- Added `init` method in `Initializer` for no-input initialization.
- The `context` and `parser` final fields in builders are now public 
  and thus can be used during creation of ESQL statements.

## [1.0.2] - 2022-07-29
### Added:
- `create struct` builder.
- A new `Initializer` model for creating arbitrary database structures from a
  hierarchical representation.
- `StructInitializer` for creating `struct` defined in a YAML file or from other
  hierarchical formats.

## [1.0.1] - 2022-07-26
### Fixed:
- Fixed error in translation of `NamedArgument` to ESQL which was still using the
  previous syntax (`:=` instead of the new `=`).

## [1.0.0] - 2022-07-06
### Added:
- `ColumnRef` translation to Javascript adds a special prefix and suffix to help 
  with execution on the client-side. A column `a` for instance will be translated
  to `row.a.$v` as `a` becomes a member of an object (named `row` conventionally)
  on the client-side and it is itself an object with a member named `$v` containing
  its value and another member named `$m` containing its metadata. Support for 
  referencing metadata on the client-side reinterprets the qualified column-name 
  `a.b` as accessing the value of metadata attribute `b` of `a`. `a.b` is thus 
  translated as `row.a.$m.b`.
- Function calls of the form `$x('y')` are treated specially when being translated 
  to Javascript: this form is used to target the metadata attribute value of a 
  column. E.g., `$a('m1')` refers to the value of the metadata attribute `m1` of 
  the column `a`. This is translated to Javascript as `row.a.$m.m1`.
- `coalesce` function translation to Javascript using `||` (or) operator.
- When targeting Javascript, function calls are translated to a form that can be
  executed on the client-side provided there is an executor object present (assumed 
  to be named `$exec`) available in the execution context and containing the 
  functions invoked as its members.
- When translating function calls to Javascript, named arguments are packaged into
  an object and provided as the first argument to the Javascript function. The 
  javascript function must have its first parameter as an object that will contain
  the list of named arguments.
- `UncomputedExpression` in attributes are not translated in query result so that
  they can be correctly translated during result encoding based on where the result
  is being sent to.
- `UncomputedExpression` are not automatically aliased as they will be executed 
  in a context where the alias might no longer be valid.
- Allow invalid column references in uncomputed expressions as they may refer to
  variables that will be valid on the client side.
- Concatenation (||) is translated to `$exec.concat` function for Javascript target,
  which has the same null behaviour as SQL concatenation, i.e., any null values
  concatenated to a non-null value result in null. In other words, nulls infect
  the whole concatenation.

### Fixed:
- The logic for determining if a QueryUpdate statement produces a result (thus 
  requiring the use of JDBC `executeQuery` instead of `executeUpdate`) was based 
  on whether the type of the statement was a `Selection`. This was excluding 
  `SelectExpression` which returns the type of its single column result. This has
  been changed to allow a `SelectExpression` to be executed as a normal `Select`
  and produce a resultset.
  
### Deprecated:
- The JSON target is deprecated. This target is essentially the Javascript 
  translation wrapped in a double-quoted string. This is however problematic as 
  only the whole translated expression should be wrapped as such, and not the 
  internal sub-expressions. It's thus better to translate to Javascript and then 
  delegate to the caller to add the wrapping.

## [0.9.9] - 2022-06-22
### Added:
- Simple `Initializer` interface to standardise initializing the database.  
- The default OVERWRITE key to `Initializer`, which is a common key used to set
  the system in `overwrite` mode where system data can be overwritten during the
  configuration phase of application launch.
- `expandname` function to expand a column name, such as `first_name` to a more 
  readable name (e.g. `First name`).
- Dates moved to Java 8 time (`LocalDate`, `LocalTime` and `LocalDateTime`).
- `DateLiteral` now returns a `LocalDate`, `LocalTime` or `LocalDateTime` if it 
  has a date component only, a time component only or both components, respectively.
- ESQL date-time literal can now have 'T' character as a separator between the 
  date and time components to conform with ISO date time format (in addition to
  simply using a single space character as the separator).
- Date, time and date-time normalised to `LocalDate`, `LocalTime` and `LocalDateTime`, 
  respectively, when read from database.
- Times read from database as Timestamp to conserve millisecond values before 
  being converted to `LocalTime`.
- `Literal.makeLiteral` can now make a string literal for a character instead of
  throwing an exception.
- `Column.setMetadata` now removes the supplied attribute if its provided value
  is to be null.
- Column metadata attribute `_id` containing the UUID value for the column in the
  `_core.columns` table is no longer saved to the `_core.column_attributes` table
  as it may replace the column id with an incorrect value in case the id was 
  changed through some table `alter` operation.

### Fixed:
- Raw Postgresql JDBC connection strings are now built correctly for cases where 
  the host and/or port are not provided.
- Raw SQL JDBC connection strings are now built correctly for cases where the host 
  and/or port are not provided.
- Structs were not being loaded from _core.relations table on init.

## [0.9.8] - 2022-06-08
### Changed:
- `extension` method in `Database` now returns the correct type of `Extension` 
  class as passed to the method (instead of any arbitrary subclass of `Extension`).
- PostgreSQL and Sql Server JDBC driver made into an API dependency so that they
  are available to applications depending on ESQL. This will allow dependent 
  applications to use non-standard apis in those JDBC drivers; for instance, the
  bulk copy manager in PostgreSQL. 

### Fixed:
- Arrays containing strings and UUIDs are now correctly translated to a string
  which is the format used to simulate arrays on SQL Server (and other databases)
  which do not natively support array types. A translation parameter (`inArray`)
  is set during array translation so that downstream translators can produce the 
  correct translation in that context.
- Fixed conversion of empty arrays of UUIDs in SQL Server (was producing an error
  when trying to convert an empty string to a UUID).
- Fixed support for C-style escaping of special characters in PostgreSQL strings.

## [0.9.7] - 2022-05-25
### Added:
- Structs: user-defined composite data types:
  - `create struct` statement to create a user-defined composite datatype which 
    has a similar syntax to `create table`. This creates a type with a table-like 
    structure which can have columns and metadata. This structure is stored in
    the _core tables; the only difference from a normal table is that an actual
    table is not created in the database.
  - `drop struct` drops a user-defined composite type.
  - `alter struct` alters a user-defined composite type.
- `insert`, `CTE`, `create table` and several other expression can now take an
  escaped identifier (identifier surrounded by quotes allowing reserved words and
  special characters) wherever a normal identifier was previously required. This
  allows reserved words to be as columns names in tables, for example.

### Changed
- `create table` syntax has been changed to be closer to SQL: 
  - the sequence of definitions is no longer arbitrary and must be table metadata
    first, if any, then column definitions and finally constraints.
  - A comma must no longer appear after the table metadata definition.

### Fixed
- Extensions are loaded after database-specific initialisations as some extensions
  depend on structures that have been created during the latter. E.g. functions
  to work with interval data types and to capture change events.
- `insert` can now have escaped identifiers in its column list.

## [0.9.6] - 2022-05-17
### Added
- History tracking
  - auto-commit support disabled as this prevents the tracking of all changes made
    in a transaction.
  - Coarse-grain history (only detect that a change has occurred in a table without
    any other details:
    - `_core.history` for coarse-grain history tracking.
    - Creation of history table for tables opting into the history tracking system.
    - Creation of trigger to capture changes on table and send to `_core.history`
      and corresponding history table.
  - See history-tables.md for more info on this feature.
- Optimisation of coarse-grain history tables (create indices).
- `count` aggregate function is translated to `count_big` on SQL server which
  returns a `bigint` instead of an `int`.
- `create index` is recognised in ESQL and translated to target database.
- Creation of core tables are now self-describing, i.e., they automatically capture
  and save metadata on themselves. This considerably reduces the amount of code
  required to create them.

### Removed
- The option of using only the information schemas has been removed as it is too
  limited and increases the complexity of initialisation and queries. The core 
  tables are now always created.

## [0.9.5] - 2022-05-11
### Added
- Executors are added to a chain of executors for the database and an iterator 
  over the chain is provided to the first executor; this iterator can then be used,
  if needed, to obtain the next executor in the chain to delegate execution to.
  Executor chain allows for multiple executors to be set up on the database in a  
  specific order and execution of ESQL programs can make use of all of them, if 
  needed. 

## [0.9.4] - 2022-05-05
### Added
- `=` and `!=` on strings force case-sensitivity on SQL Server which, by default, 
  uses a case-insensitive collation for the database and for string comparison.
- ESQL parser rebuilt on latest Antlr version 4.10.
- `Executor` interface to abstract query execution and allows different implementation
  of executors to be plugged into the ESQL execution engine. This will be used,
  e.g., by the `esql-user` extension to limit query execution based on user 
  permissions.
- Transaction and connection boundaries now uses a counter which tracks the number
  of times that a connection has been used by functions in the same thread and
  automatically commits/rollbacks the transaction and closes the connections when
  the count reaches 0 (the count is decremented when the close() method is called;
  this method is automatically invoked, as EsqlConnection is an AutoCloseable, at
  the end of the try-with-resource block which opened/accessed the connection).
  This simplifies transaction and connection boundary management as the opening 
  of connection uses the same pattern irrespective of whether the connection is
  inherited from the caller or a new connection is created. In both cases, the 
  connection can be opened in a try-with-resources block and it will be closed 
  automatically when the correct boundary is exited.
- `find` method in `QueryParams` class to look for parameters by name.
- New parameter `firstFilter` added to `filter` method and set to true when it is
  invoked on the first filter in the set of active filters for the query, false
  otherwise. This allows the filter application to be handled differently for the
  first filter, such as grouping existing conditions properly so that they don't
  interfere with the filter conditions in indeterminate ways.
- Hierarchical filters where filters can have sub-filters and so on, creating an
  and/or hierarchy of filters; application of hierarchical filters result in a 
  condition clause (where clause) with the same structure as the hierarchy.
  
## [0.9.3] - 2022-03-29
### Added
- `StringForm` interface to standardise the use of the `_toString` method to 
  construct a string representation of an object in a StringBuilder with support 
  for hierarchies and indentation.

### Fixed
- Added `Environment` parameter to `addSet` method in `Translator.Util` which is 
  used to produce the `set` clause of an `Update`. This parameter allows the 
  function to receive the environment in which the update is being translated 
  allowing named parameters to be translated correctly.

## [0.9.2] - 2022-03-22
### Added
- Added EsqlPath parameter to `filter` method which can be used to determine the
  context of the current statement (i.e., which surrounding ESQL statement it is
  part of). This is used to ensure that changes made to the query is compatible
  with the surrounding expressions.
- Add several filters to QueryParams using `filters` single method call.
- QueryParams check if single or multiple filters are non-null before adding to
  further simplify calling interface.

### Fixed
- For recursive `WITH` query on SQL Server, `distinct` is not supported in the 
  first CTE. If this is the case, excludes putting distinct even when there are 
  reverse links on the path.
- Filtering of subclasses of `Select` was delegating to the Select superclass and
  this was erroneous as the specific subclass was being replaced with an instance
  of `Select`; the `filter` method in `Select` now returns `this` when it detects
  that it is being run on a subclass; the filtering still works as the subclasses
  are composed of other `Selects` which are filtered correctly.
- SQL Server only support `union all` as the operator in the first CTE of a 
  recursive WITH query. Other composite operators are changed to `union all` when
  the first CTE of a recursive WITH query is being translated for SQL Server.

## [0.9.1] - 2022-03-17
### Added
- Filtering on Deletes.
- Filtering on Updates.

### Tested
- Test finding minimal cost paths between tables.
- Test dynamic filtering.

### Fixed
- Fixed ESQL grammar to correctly parse `right` and `full` joins.
- Table expression trees created through filtering are now automatically left-rotated 
  to translate correctly.
- CTE and WITH `tables()` methods now return the tables of the query they contain,
  which now reflects any changes made to that query (through filtering, e.g.). 
  Previously CTE would return a copy of the tables that was made when the CTE was 
  constructed and would thus be obsolete if the tables of its inner query changed.
- Translation of delete on multiple tables for PostgreSQL has been rewritten to
  be similar as the translation for updates as this works better for all types of
  complex joins (the existing translation was using the `using` clause which limits
  the type of table expressions that can be added to it).
- Fixed concatenation of select expressions which was expecting expressions to 
  return String instead of arbitrary objects now (as `SelectExpression`, being a 
  subclass of `Select`, returns `QueryTranslation` now).
  
## [0.9.0] - 2022-03-15
### Added
- Simple transaction boundary management using a thread local variable to hold 
  a stack of current active connections (and transactions). `Database.esql` method 
  can now take a null `Connection` signaling that the first active connection at 
  the top of the stack must be used (or a new current active connection is added 
  to the top of the stack and used thereon until it is closed).
- `SelectExpression` is now a subclass of `Select` instead of just `Expression`
  and thus participates in all query-type operations.
- Dynamic filtering where a set of filters can be composed into a query prior to 
  its execution. Filters are specified against a table and, if the filter table
  is not in the query being executed, the shortest path between the tables in the 
  query and the filter table is searched for, based on foreign key links between
  them. If such a path exists, the query is rewritten to join all the tables in 
  the path in its from clause.
- EsqlConnection `prepare` method prepares a query for execution by substituting
  parameter values, expanding macros, applying filters, etc. After preparation,
  the query can be executed without any other processing.
- EsqlConnection `execPrepared` method to execute a prepared query. The existing
  `exec` method is now implemented by calling `prepare` followed by `execPrepared`.
- Query parameters and filters have been grouped into the `QueryParams` class 
  which uses methods call chaining to simplify construction of parameters and
  filters groups to pass to the EsqlConnection execution methods.
- Combining `where` conditions with filters (with AND & OR operator) implemented.
- Grouped expressions (`()`) of grouped expressions are optimized to remove the 
  unnecessary groupings.

## [0.8.9] - 2022-03-05
### Added
- JSON object literals can now only have literal attributes list instead of general
  attributes list (containing expressions which cannot be computed in a JSON 
  literal context).
- Escaped identifiers can be used in attributes which allows reserved words and
  special characters in the identifier names.
- Add `parameters` parameter to `exec` method so that it has the same signature
  as the `translate` method; this is in preparation for the implementation of 
  general-purpose assignment to members of composite types.
- `evaluateAttribute` method added to `Attribute` class to compute the value of
  the attribute's expression. This is useful for attributes with literal values.
- Added auto-generated metadata columns for check, primary and foreign key constraints
  on single columns. For foreign keys, metadata are added to both sides of the 
  reference.
- Check constraints expressions no longer need to be surrounded in brackets.
- `JsonArrayLiteral` now has higher precedence than base array literals which
  allows attributes of JsonArrayLiterals to parse correctly.
- All 4 constraint types (unique, primary, check and foreign) now add attributes 
  describing themselves to the containing (and target for foreign keys) relation 
  and, if they are set on a single column, the corresponding column.

### Tested
- Test check constraints.

### Fixed
- `notNull` method in `Column` now only evaluates its value if it is set to a 
  boolean literal. Evaluating it when it is set to an expression will likely cause
  an error in that context; in such cases, `notNull` is assumed to be false.
- `exec` methods in `EsqlConnection` check type of parameters and dispatch to
  correct method at runtime; this corrects for the inability of Java static dispatch
  to distinguish between an object and a variadic parameter.
- Expansion of column list of CTE now returns a list of column names aliased with
  the alias associated to the CTE name instead of the CTE name only; this fixes 
  a problem where the column references to columns inside a CTE was wrongly using 
  the CTE name as their qualifier.
- Fixed parsing of SQL Server expressions containing special variables surrounded
  by square brackets when loading check expression for SQL Server information 
  schemas (on initial load).
- Fixed parsing of SQL expressions containing keywords in uppercase (ESQL is 
  case-sensitive and all keywords are in lowercase).
- Column reference in column definition of `create table` statements where the 
  column expression is a select expressions is now handled correctly.  
- Grammar changed to include `UncomputedExpression` as an actual base literal which
  allows `JsonArrayLiteral` containing `UncomputedExpression` to parse correctly.

## [0.8.8] - 2022-02-26
### Added
- `ResultTransformer` interface to transform `Result` to sequence of arbitrary 
  objects.
- Loaded extensions can be obtained from database which allows the development of
  arbitrary custom methods for logic that cannot be easily implemented as ESQL 
  macros or functions. 
- An object or a record can now be passed as a parameter when executing a query;
  the properties/fields of the object is used to create an array of `Param` objects
  which is then passed to the query executioner.
- Use Configuration class instead of Map of strings to objects to pass configuration
  information to the database for consistency.
- DatabaseFactory to create database instance of the correct type based on passed 
  configuration.

## [0.8.7] - 2022-02-14
### Added
- Define coalesce function so that its return type is properly mapped to its 
  parameter type.
- Foreign keys which are not enforced by the database (only saved in the _core 
  tables to use for linkage information in restriction, main-details CRUD ops, etc.).

## [0.8.6] - 2022-02-13
### Added
- Add default column metadata for unique and non-null columns.
- Add default result metadata for unique set of columns.
- Add default result metadata for child relations (relations pointing to 
  current relation through a foreign key).
- Several additional checks and fixes on foreign key and dependency constraints 
  (a dependency constraint is the reverse of a foreign key. I.e. if there is an
   fk pointing from A to B there is a dependency constraint pointing from B to A).

## [0.8.5] - 2022-02-12
### Added
- Extensions can now be configured (e.g. to set a specific lookup schema) through 
  parameters on initialisation.
- Result and table metadata are now restricted to literal values only as they are
  associated to the whole result/table. Metadata attribute expressions are computed 
  for every row in a result/table and would not have the same value for the whole 
  result/table.
- Dynamic table expressions now supports table-level literal metadata.
- Uncomputed expression surround the textual translation of the expression with
  `$(` and `)` on all targets. This was previously done only for ESQL output. The
  delimiters simplify the detection of an uncomputed expression in a result.

### Fixed
- Fixed propagation of table-level metadata from inner query to outer query which
  was failing for select table expressions and dynamic expressions  
- Uncomputed expressions is now a subclass of `BaseLiteral` and behaves as a
  literal in all circumstances, fixing several issues with how they were treated.

### Changed
- The uncomputed expressions of derived columns and attributes now used the suffix
  `/$e` instead of `/e` as the latter could conflict (especially for derived columns)
  with a metadata attribute of that name (e). `$e` also follows the naming pattern 
  of special attributes in ESQL (other special attributes include `$m` and `$v`). 

## [0.8.4] - 2022-02-11
### Added
- Do not expand column list of arbitrary selects (not just select expressions)
  in column list of other queries.
- Correlated query test.
- The type of a select with a single column and part of a column list of an 
  enclosing query is taken to be the type of its single column (as for select 
  expressions), when reading the value for that column from the database. This 
  allows proper normalisation of such values.
 
### Fixed
- Fixed resolving of qualified column references in correlated queries (where a 
  table is referenced in a subquery) where the reference belongs to a table in an
  outer query.

## [0.8.3] - 2022-02-06
### Added
- `print` function to print debug messages to console.
- Named argument now uses `@` instead of `:` prefix as the ':' conflicts in some
  cases with naming of columns and tables.
- Eval construct added to the grammar: `@(expression)` evaluates the expression
  and inserts it into the containing expression.
- Signature of `translate` and `exec` methods changed to allow them to work 
  together, as this is required to properly implement the eval construct.
- Show line number where error occurred in ESQL program.
- General-purpose language:
  - Selector construct added to grammar: `a[b]...` selects member `b` in `a`.
  - For-each loop over list, iterables, maps, arrays and results, with binding
    for both key and value where applicable.
  - General for loop (for init, condition, step do body end).
  - While loop.
  - If-elseif(s)-else conditional. 
  - Break keyword to break from the innermost loop.
  - Continue keyword to continue to the next iteration of the innermost loop.
- Selectors now take a list of members instead of just one, allowing for more
  complex selection.
- Selectors can now select and invoke arbitrary methods on an object with dynamic
  dispatch selecting the most specialised method to invoke based the actual types
  of the passed parameters.
- Automatic numeric conversion to match method signatures in selectors.
- 1-based array access, as for databases (and unlike Java) in list and arrays
  in Esql.
- Package restructured to improve clarity and coherence.

## [0.8.2] - 2022-02-03
### Added
- Support for passing named arguments to functions.
- Support for default values in function parameters.
- Pass null or the default value to parameters of functions for which an argument
  was not provided.
- Computation and tests for `ltrim` and `rtrim`.
- Computation and tests for `lpad` and `rpad`.

## [0.8.1] - 2022-02-03
### Added
- Grammar modified to parse `left` and `right` as function calls (in addition to
  their use in join types) when they are followed by an opening brace, two arguments
  (string and an integer number of characters) and a closing brace.
- Support for calling predefined functions (such as trim) in an ESQL program.
- `Result` now implements `Iterator` and `Iterable` interfaces. The `next` method
  in `Result` was renamed to `toNext` as its signature conflicted with that of 
  the `next` method in `Iterator`. Result can now be iterated using the Java 
  for-each loop. The existing result scrolling mechanism (based on Java ResultSet)
  is still available and the iterator mechanism is a thin wrapper around it; this
  allows the result to be read with either mechanism separately or even combined.

### Tested
- Computing value of expressions:
  - literals.
  - arithmetic expressions and sub-expressions groupings with braces.
  - strings repeat and concatenation.
  - comparisons.
  - logical operators.
  - conditionals (`a if c1 else b if c2 else...`).
  - some pre-defined functions (trim).
- Function parsing.
- Simple function execution.
- Recursive function execution (factorial).

## [0.8.0] - 2022-02-01
### Added
- Function and variable scoping.
- A global environment is created as a child of the System environment (structure) 
  for every program that is executed. This global scope hold all top-level variable 
  and function declarations.
- General-purpose language support:
  - Variable definitions and assignments.
  - Symbol table and scoping rules.
  - General typing.
  - Functions.
- Literals parsing tests.
- Expressions parsing tests.
- Execution now uses environment bindings which is a foundation for the support 
  of lambda expressions.
- Custom date and time literals parsing is faster and fix cases where milliseconds
  were being ignored.

### Fixed
- Fixed `ancestorDistance` method in `EsqlPath`.
- Negative numbers are now parsed as the negation operation on a number (or 
  expression) instead of being parsed as token in the lexer. When parsed by the
  lexer `n-1` produces two tokens: `n` and `-1` which is obviously wrong. Now 
  only the pattern for positive integers is defined as a lexical rule with negative
  numbers treated as the negation operator applied on a positive number.

## [0.7.4] - 2022-01-26
### Added
- Type inference system improved with type assigned and computed for every ESQL
  node in a syntax tree. All nodes are assigned the `UnknownType` initially.
- Scope rules and mechanism added with `Structure` being the top-level `SystemScope`,
  a program forming the `GlobalScope`, functions introducing `FunctionScope` and
  blocks creating local block scopes.
- Syntax and implementation of user-defined functions syntax, variable declaration 
  and assignments for general purpose ESQL.

### Fixed
- `money` ESQL type mapped to `Double` Java class, allows correct numeric conversion 
  of  values of this type read from the database.
- Sub-select detection corrected in Sql-Server translator to allow translation to
  automatically correct use of `order` clause in sub-selects (which is prohibited
  in SQL Server without an `offset` clause). 

## [0.7.3] - 2022-01-16
### Changed
- Changed the grammar of function call to allow a slightly shorter syntax for 
  named arguments where the ':=' is replaced with '='. E.g., `sum(0, values:=[1,2,3])` 
  can now be written `sum(0, values=[1,2,3])`. This also removes a potential 
  conflict with assignment to variables in the extension of ESQL with general
  purpose features.

## [0.7.2] - 2022-01-15
### Fixed
- Fixed an error where columns in select expressions were being wrongly aliased  
  with table alias from surrounding context.

## [0.7.1] - 2022-01-14
### Added
- Translation now use a (functional) persistent map (PCollections.org) for the 
  parameters map to allow for fast addition and deletion of parameters without
  compromising the existing parameters.

### Fixed
- Columns are given a unique user-friendly name by the syntax analyser and the 
  select builder to resolve errors in code which assumes columns always have a 
  non-null name.

## [0.7.0] - 2022-01-13
### Added
- Macro expansion have been split into two phases:
  - UntypedMacro are expanded first: during this expansion, types could be 
    unavailable and expansion must not assume their presence. In this phase 
    column lists are expanded (star columns and column metadata);
  - TypedMacro are then expanded: types are not available (or can be computed).
    All previous macros (except for ColumnList) are of this kind.
- Column and ColumnRef now carries type information which could simplify type 
  inference and propagation.
- The general structure of variable scopes and symbols has been added (continuing
  semantic analysis work in preparation to adding general purpose language features).
- Original query is now kept in the QueryTranslation to improve error management.
- `find` method in Esql to find first child, if any, satisfying a predicate function.
- `columnList` method to get column list from a TableExpr.
- `named` method to get a sub-TableExpr from a TableExpr with the specified alias.

### Improved
- Type inference has been improved to work in many more cases, properly propagating
  type information from inner queries to outer ones.
- Column list expansion improved to not require type information during its expansion
  and works in more scenarios.
- Simplified Result class which reuses the QueryTranslation instead of duplicating
  its fields.
- Simplified unknown function translation by reusing translation code in Function 
  class.

### Fixed
- Overloading of column metadata in column list expansion.
- ___unknown mapped to UnknownType instead of (erroneously) Json.

## [0.6.6] - 2022-01-05
### Added
- ColumnList macro expansion now add columns for relation metadata where appropriate.
- Support for lateral joins (using outer apply in SQL Server).

### Fixed
- Metadata expansion into columns in ColumnList macro expansion considers whether
  query is grouped and is a modifying query.
- `range` function (for building histogram of values similar to the `bin` macro) 
  has been renamed to `binf` to not clash with the `range` keyword used in specifying
  the window frame of window functions.

### Improved
- Attributes which are literal are normally optimised away (removed from the column-list
  of the query and calculated statically). The optimisation was not carried out 
  in subqueries as the attributes could be referenced in an outer query. An 
  exception to the subqueries rule has been added which is when the query is the 
  last query in a `With` query; in that case, attributes loading can be optimised 
  as the columns will not be referred to outside the `With` query.
- Simplified the `Result` classes by removing redundant fields, replacing generic
  pairs and triples with new record types and removing unused methods.
- Document `Result` classes.
- Optimize loading of literal attribute values set as columns (as columns named
  as '<col>/<attribute>').

## [0.6.5] - 2022-01-04
### Added
- Add explicit date part add functions (that do not use the general interval add  
  function) as those can be much faster for SQL Server (using underlying `dateadd` 
  function instead of the custom _core.add_interval function).
- Months ceiling (`monthsceil`) function which returns the number of months between 
  2 dates, adding one month for any remaining days in the period, analogous to 
  computing the ceiling of a fractional number.
- Support for frame definition (rows and range) in window functions.
- `AsPromotedNumericParameterType` is a special return type for functions returning
  a value that is the promoted numeric type of their first parameter. For instance, 
  the `sum` aggregate function has a dynamic promoted return type based on its 
  parameter: if its parameter is integer, its return type is long, while if its 
  parameter is single-precision floating-point, its return type is double-precision  
  floating-point.
- Optimize `case` to `iif` when there is a single condition on SQL Server.
- `Selection` type can now carry an alias reducing the need for `AliasedRelation`
  which is now used only as the type of `SingleTableExpr`.

### Changed
- Code cleanup: add @SafeVargs for safe parameterised variable arguments, use Java 
  records where possible, use instanceof patterns, eliminate unnecessary toString(),
  use text blocks for some multi-line strings, clean certain javadocs.

## [0.6.4] - 2021-12-25
### Added
- Types of derived columns are now inferred in CreateTable statements.
- Macros are now expanded for all ESQL statement, not just QueryUpdates. Since 
  the expansion can be invalid in some contexts (such as expansion of a select
  expression referring to a table in the CreateTable statement creating that table)
  care must be taken in the macro to detect the correct context when expanding.
- ColumnRef are now properly qualified in SelectExpression.
- New method 'exists' added to TableExpr which returns true if all the tables
  referred to in the table expression exists. This is useful to prevent errors
  during macro expansion when tables referred to by some statement have not been
  created yet. With this check, the macro expansion can skip over parts which are
  missing at that point.

### Fixed
- Bool support in SQL Server using IIF has been fixed for select expressions in
  column list (IIF is used in column list while not used in places where boolean
  expressions are expected - e.g. the where clause. Select expressions in column
  list conflicts with that as it is in a column list, thus requires an IIF, but 
  also, it has certain parts, where using an IIF is an error). This can be resolved
  by carefully setting a parameter at different points in the translation to 
  control the output of the IIF.

### Changed
- Remove 'replaceExistingQualifier' parameter from the 'qualify' method as it is
  normally always true. Qualifier is replaced automatically in column references
  in non-select-expression or those in select-expression without a qualifier.

## [0.6.3] - 2021-12-22
### Added
- JsonObjectLiteral and JsonArrayLiteral are now outputted as strings in queries.
- 'hasAncestor' method which can check if an ESQL is a descendant of one of several 
  ESQL classes in one call.
- Selects are now treated as select-expressions (columns are not expanded) when
  they appear in insert row values and as part of the column list of another query.
- Random naming of columns when a name is not provided or to disambiguate duplicate
  columns has been replaced with more friendly names in ColumnList expansion which
  disambiguate column names using an index in the context of the other columns in
  the list.
- Automatic BaseRelation columns use friendly names instead of random ones. 

### Changed
- QueryTranslation is now a Java record.
- Columns are no longer automatically expanded in base relations; this decreases
  the number of synthetic columns with names containing '/' being added to the 
  relations. The only synthetic columns added now are the uncomputed forms for
  derived columns (e.g. c=a+b result in c/e=$(a+b) being added to the column list).
- Attributes access interface in types and in metadata have been normalised.
- Table type inference no longer add synthetic columns.
- Macro expansion adds a tagging class in the expansion path to signal to downstream
  processes that expansion is ongoing to allow for different behaviour during 
  expansion.
- Type inference is allowed during macro expansion but is restricted to a pre-typing
  phase where a best-effort type is produced; since this type may not be correct,
  it is not cached. The correct type is then produced after the macro expansion
  when the type method is next called.

## [0.6.1] - 2021-12-19
### Added
- ColumnRef expansion finds and includes the proper qualifier (based on the relation 
  that the referred column is coming from) resulting in a single method for handling
  this expansion and increased stability.
- ColumnList expansion of `*` column has been improved.

### Fixed
- Parsing of default value expressions from SQL Server has been fixed to properly
  convert prefixed strings (e.g. N'yy', E'xx') to equivalent ESQL expressions.
- Type inference for recursive with and multi-level subqueries containing *.

### Refactored
- Column alias renamed to 'name'.
- Type inference and column finding simplified by removing the need to explicitly
  work with relation aliases. All aliasing is handled internally with the column
  finding methods returns both the column found and the internal relation that they
  belong to. E.g. looking for a column over a join will return both the column and
  the deepest joined relation that the column belongs to.

## [0.6.0] - 2021-12-15
### Redesigned
- ESQL node changed:
  - To be immutable, creating copies on mutating operations, with use of path 
    persistence for faster copies and lower memory use;
  - To use a list for children (instead of a map) to preserve order of children;
  - To implement a path-seeking api for selecting part of the tree and replacing 
    it with other nodes
- New simpler map and forEach function in ESQL.
- New simpler macro interface.
- New copy constructor and method allowing changes to be applied to the value and
  children of the copy.
- EsqlPath added to type() and execute() methods to allow for searching for
  ancestors in those methods.
- Default map method uses DFS to force leaves of AST to be mapped before parents.
- bfsMap method to map using BFS strategy.
- All tests passing (except for comparison difference due to different 
  dynamically-generated UUIDs).
- Esql now raise an error when its value is of type ESQL. The value is a placeholder
  for the value of the ESQL node in the AST. If it is also an ESQL, mapping 
  functions does not follow through it during processing. Thus, such values are 
  best stored as children of a tree node.

## [0.5.2] - 2021-08-19
### Refactored
- Package restructure: 
  - split expression package into subpackages by type of expressions; 
  - split function package into subpackages by type of data that the functions 
    operate upon; 
  - type package moved to semantic package; 
  - QueryUpdate class moved to ma.vi.esql.syntax.query package
  
### Added
- The type of types (kinds) are now a proper sub-interface of Type, for consistency.
- Functions are now types with their own specific kind.
- New syntax for select expressions which starts with the `from` keyword. 
  E.g. `from X select y`. This removes any ambiguity between `select` expressions
  and `select` statements and the need for surrounding select expressions with '()'.

## [0.5.1] - 2021-04-25
### Deprecated
- `Close` interface no longer implemented as not required. Will be removed in a 
  future release.

## [0.5.0] - 2021-04-22
### Added
- Statements are now expressions in ESQL which allows for some grammatically 
  wrong statements (such as putting an insert into a function call) to parse 
  correctly, but also allows for more complex constructions, such as assigning 
  a select to a variable.
- `translate` method delegate to the protected `trans` method in ESQL to allow 
  for subclasses to provide richer translation behaviour, such as adding prefixes
  and suffixes to the translation.

### Fixed
- Set the parent of substituted parameter values to the parent of the NamedParameter
  being substituted. This conserve the hierarchy of the ESQL object and allows 
  traversal search, such as for looking for ancestors, to continue working as 
  prior to the substitution.
  
## [0.4.4] - 2021-04-16
### Added 
- Completed documentation .g4 antlr grammar and github wiki page on ESQL grammar.
- Rename AlterTableAction to Alteration

## [0.4.3] - 2021-04-13
### Added
- Extensions can be provided as any collection instead of just a set on initialisation.
  Using a List, e.g., will allow the client to provide a preferred order in which to 
  load and initialise the extensions.
  
### Fixed
- Dropping derived columns was sending an `alter table ... drop column` command to
  the database which resulted in an error. Derived columns only need to be removed
  from the `_core.columns` table.
- Do not throw NotfoundException in findColumn in Join as this breaks column search 
  in SQL Server in complex queries where the referred column might be in an outer 
  query.

## [0.4.2] - 2021-04-12
### Added
- Testing of all base macro functions (bin, inmonth).

### Fixed
- Corrected an error where column defaults were being dropped when altering a 
  table on execution of a `create table` statement, as they were erroneously found 
  to be missing.
- Do not throw NotfoundException in findColumn in AliasedRelation as this breaks
  aliased column search in SQL Server in complex queries where the referred column
  might be in an outer query.

## [0.4.1] - 2021-04-11
### Fixed
- Extensions are loaded in all cases; previously they were loaded only when
  core tables were being created.
- Source columns in foreign key constraint assigned to the child named `columns`
  to be coherent with other constraints and resolve an error which happened when
  a foreign key constraint is to be dropped.
- Fixed ConcurrentModificationException when dropping columns which are no 
  longer defined in a table.

## [0.4.0] - 2021-04-10
### Added
- Result now keeps the Column info alongside each column mapping for access to 
  the column metadata when accessing the result.
- Add database configuration parameters as constants to the Database interface.

## [0.3.9] - 2021-04-08
### Added
- `bin` macro function which return the interval that a value belongs to and 
  expanded to a CASE expression; it is similar to the `range` function but the 
  latter uses a stored database function whereas `bin` expansion to a CASE can 
  be optimised by the database and result in faster queries.
- Concatenation of multiple expressions is now optimised to generate more 
  compact code.

### Fixed
- Typing of CTE now returns the relation type of the CTE instead of the type of
  the internal query of the CTE, thereby fixing various bugs in `with` queries.
- Renamed functions and macro functions to be shorter and consistent.  
  
### Removed
- All lookup tables, functions and macros moved to an Extension.

## [0.3.8] - 2021-04-06
### Added
- Extensions can define their dependencies on other extensions which determines
  the loading and initialization order of extensions. 
- `database.createCoreTables` parameter instead of method parameter to postInit 
  simplifies instantiation of database object.
- Extension `init` function no longer takes a `Structure` parameter as it can 
  be obtained from the `Database` parameter.
- Simplified function registration in Structure.  
  
### Fixed
- Self-translation of cross-joins to ESQL uses proper form (`times` instead of 
  `cross join`).
- Corrected order of `rtrim` and `ltrim` function which were reversed during
  registration in the constructor of `Structure`.

## [0.3.7] - 2021-03-06
### Added
- Extensions to add new functionality to ESQL.
- Extensions now declare dependencies on other extensions which are loaded first.

## [0.3.6] - 2021-03-05
### Added
- Columns of CTE are renamed to match the CTE field list when latter is provided.

## [0.3.5] - 2021-03-05
### Added
- Recursive CTE is now fully supported in PostgreSQL and SQL Server.
- Reduce code duplication in Analyser for addition and multiplication expressions.
- Columns of CTE are renamed to match the CTE field list when latter is provided.

### Fixed
- `is null` was being parsed (and translated) as `is not null`.
- `with` query types are now taken as the type of their last CTE. 

## [0.3.4] - 2021-03-04
### Added
- Reduce code duplication in Analyser for expressions and simple expressions.
  
### Fixed
- Corrected error where derived expressions were not being linked to their parent 
  leading to type resolution errors (in SQL Server). 
- Translation of select expressions in ESQL now does not output column metadata 
  even when they are present (which is invalid in select expressions).

## [0.3.3] - 2021-02-18
### Added
- Improved error detection: use of reserved keywords in wrong parts and missing/wrong keyword.
- Improved error detection: missing commas and semi-colons.
- Improved error detection: missing closing bracket, square bracket, parenthesis.
- Group by clause can now refer to columns by position (e.g group by 1, 2 will
  group by the 1st and 2nd column expressions in the query). This is simulated 
  on SQL Server which does not directly support this construct.
- Base testing of grouping (normal, cube, rollup).

### Fixed
- Improved bool support in nested queries on SQL Server using `ancestorDistance`
  and consequently solving the error in nested `exists` queries.
- `order by` is now specified after `group by` in ESQL select, in line with the 
  order of their execution.

## [0.3.2] - 2021-02-16
### Added
- ESQL transformers (prior to execution, to inject specific behaviour by rewriting 
  ESQL).
- Improved detection of general syntax errors, and missing (or wrong) `from` 
  clause in selects. 

## [0.3.1] - 2021-02-14
### Added
- Reduce the number of keywords in the grammar by not explicitly naming all
  acceptable types (replace with identifier pattern).
- Grammar support for multi-dimensional arrays and sized arrays.
- Improved error detection and reporting of wrong types in ESQL commands.

## [0.3.0] - 2021-02-13
### Added
- (Non-recursive) Common-Table-Expressions (CTE) (With queries) queries are now 
  fully supported and tested in both PostgreSql and Sql Server.
- Columns selected in an inner query, including metadata and derived columns, are
  now properly propagated to outer levels allowing for nested queries of any 
  number of levels (e.g `select a from x:(select * from y:(select a from A))`)

### Fixed
- Multi-tables updates and deletes in PostgreSql are now fully functional.

### Deprecated
- Support for HSQLDB is being deprecated as complex names are not supported
  when reading from sub-queries.
- Support for MariaDB (and MySQL) is being deprecated as it fails to create 
  constraints on tables in certain conditions.