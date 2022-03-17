# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Planned]
### To add
- Array, list, map, set operations.
- Manual transaction control (begin, commit, rollback, set isolation level, 
  savepoints, locking). Currently, transaction boundary is aligned to connection 
  or single-statement only.
- Produce formatted multi-line translation to SQL.
- Assignment to array, list and map subscripts.
- Operations on JSON objects.
- `Truncate table` statement.
- Make all expressions and functions (where possible) executable.
- Support for creating and using sequences.
- Support for creating indices.
- Support for creating and using views (including materialised views).
- Complete documentation of ESQL grammar.
- Fully document purpose and usage.
- Support for Oracle database.
- Support for SQLite.
- Make into Java 9 module.
- Support for 'within group' for ordering in string and array aggregate functions.
- Support for bulk copy manager in postgresql.
- Support for merge queries.
- Implement state-based model where an ESQL program is specified as a target state 
  instead of a sequence of imperative statements. For instance, a table definition
  is specified instead of a `create table` and/or a set `alter table` statements.
  The table definition will then be realised with a sequence of `create table` 
  and `alter table` statements. Currently, the `create table` statement already 
  work in this manner. In the state-based model this is applied to all statements
  where this make senses. For example, a set of records is specified as the target
  content of a table which will then be merged with the existing data through a 
  merge statement and/or a set of insert and update statements.
- Replace visual tests (printResult) with assertions, where possible.
- Implement `explicit` in select (no expanded columns when explicit keyword used).
- `pkey` macro expands to the primary key columns of a table.
- `fkey` macro expands to the columns of a foreign key between two tables.
- Virtual tables implemented as Esql transformers: A virtual table looks and 
  behaves like a normal table but does not need to exist on the database or may
  correspond to multiple tables. Queries and updates to virtual tables are rewritten
  as other queries/updates or whole programs.

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
- Apply result and column metadata overloading in column list expansion (currently,
  the overridden metadata are not being considered). (create tests for metadata 
  overriding)

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
  return String instead of arbitrary objects now (as SelectExpression returns
  QueryTranslation now, being a subclass of Select).
  
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
  is not in the query being executed, the shortest path is looked for between the
  tables in the query and the filter table, based on the foreign key links between
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
- Grouped expressions of grouped expressions are optimized to remove the useless 
  groupings.

## [0.8.9] - 2022-03-05
### Added
- JSON object literals can now only have literal attributes list instead of general
  attributes list (containing expressions which cannot be computed in a JSON 
  literal context).
- Escaped identifiers can be used in attributes which allows reserved words and
  special characters in the identifier names.
- Add `parameters` parameter to `exec` method so that it have the same signature
  as the `translate` method; this is in preparation to the implementation of 
  general-purpose assignment to values of composite types.
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

### Test
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
- Close interface no longer implemented as not required. Will be removed in a 
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