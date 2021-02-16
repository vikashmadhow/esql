# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Planned]
- Support for Oracle database.
- Result transformers and encoders.
- Testing of all normal functions.
- Testing of all macro functions.
- Testing of grouping (normal, cube, rollup).
- Testing of window functions.
- Support for creating and using sequences.
- Support for creating indices.
- Support for creating and using views (including materialised views).
- Complete documentation of ESQL grammar.
- Document purpose and usage. 
- Performance testing.

### Fixed
- `exists` should not expand column list (in general, conditions over subqueries
  should not expand the columns in the subquery)
- `exists` fails in SQL Server  

## [Unreleased]
### Added
- Improved error detection: use of reserved keywords in wrong parts and missing/wrong keyword.
- Improved error detection: missing commas and semi-colons.
- Improved error detection: missing closing bracket, square bracket, parenthesis.

## [0.3.2] - 2021-02-16
### Added
- ESQL transformers (prior to execution, to inject specific behaviour by rewriting ESQL).
- Improved detection of general syntax errors, and missing (or wrong) from clause in selects. 

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

