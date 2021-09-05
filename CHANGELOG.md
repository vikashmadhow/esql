# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Planned]
- General-purpose language:
  - Variable definitions and assignments
  - Symbol table and scoping rules
  - General typing  
  - Conditional statements  
  - Iteration and loops 
  - Functions
- Testing of all normal functions.
- Testing of translation of grouping by complex expressions (subqueries) in SQL Server.
- Testing of translation of distinct over multiple columns in SQL Server.
- Testing of window functions.
- Array operations.
- JSON operations.
- Support for creating and using sequences.
- Support for creating indices.
- Support for creating and using views (including materialised views).
- Complete documentation of ESQL grammar.
- Document purpose and usage.
- Performance testing.
- Result transformers and encoders.
- Support for Oracle database.
- Make into Java 9 module.
- Support for frame definition (rows and range) in window functions.
- Support for 'within group' for ordering in string and array aggregate functions.

## [Unreleased]
### Redesigned
- Change ESQL node:
  - To be immutable, creating copies on mutating operations, with use of path persistence
    for faster copies and lower memory use;
  - To use a list for children (instead of a map) to preserve order of children;
  - To implement a path-seeking api for selecting part of the tree and replacing it
    with other nodes
- New simpler map and forEach function in ESQL.
- New simpler macro interface.

## [0.5.2] - 2021-08-19
### Refactored
- Package restructure: 
  - split expression package into subpackages by type of expressions; 
  - split function package into subpackages by type of data that the functions operate upon; 
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
  in SQL Server in complex queries where the referred column might be in an outer query.

## [0.4.2] - 2021-04-12
### Added
- Testing of all base macro functions (bin, inmonth).

### Fixed
- Corrected an error where column defaults were being dropped when altering a table
  on execution of a `create table` statement, as they were erroneously found to be
  missing.
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
- Self-translation of cross-joins to ESQL uses proper form (`times` instead of `cross join`).
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
- Group by clause can now refer to columns by position (e.g group by 1, 2 will group by the 1st and 2nd column 
  expressions in the query). This is simulated on SQL Server which does not directly
  support this construct.
- Base testing of grouping (normal, cube, rollup).

### Fixed
- Improved bool support in nested queries on SQL Server using `ancestorDistance`
  and consequently solving the error in nested `exists` queries.
- `order by` is now specified after `group by` in ESQL select, in line with the 
  order of their execution.

## [0.3.2] - 2021-02-16
### Added
- ESQL transformers (prior to execution, to inject specific behaviour by rewriting ESQL).
- Improved detection of general syntax errors, and missing (or wrong) `from` clause in selects. 

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