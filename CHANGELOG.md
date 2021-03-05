# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Planned]
- Extensions to add new functionality to ESQL.
- Result transformers and encoders.
- Testing of all normal functions.
- Testing of all macro functions.
- Testing of translation of grouping by complex expressions (subqueries) in SQL Server.
- Testing of translation of distinct over multiple columns in SQL Server.
- Testing of window functions.
- Support for creating and using sequences.
- Support for creating indices.
- Support for creating and using views (including materialised views).
- Complete documentation of ESQL grammar.
- Document purpose and usage. 
- Performance testing.
- Support for Oracle database.

## [0.3.5] - 2021-03-05
### Added
- Recursive CTE is now fully supported in PostgreSQL and SQL Server.
- Reduce code duplication in Analyser for addition and multiplication expressions.

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