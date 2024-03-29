The grammar of ESQL is described top-down (starting with the largest unit, i.e. a
program, down to each individual tokens) with each construct shown as an ANTLR
production rule. A production rule shows how each element can be constructed from
other elements of the grammar, including itself (i.e., recursion is allowed).

## Program
The largest unit in ESQL is a **program**. An ESQL program is a semi-colon
separated sequence of ESQL [statements](#statement).

```antlr
program
    : statement (';' statement)* ';'?
    ;
```

## No-operation (Noop)
A semi-colon can also be interpreted as a silent no-operation (noop)
statement. Noops can be used to disable certain part of a program dynamically
(by replacing the statement with a noop) without having to remove the
statement which can be harder in some cases.

```antlr
noop
    : ';'
    ;
```

## Statement
ESQL statements can be divided into two groups: statements for defining and
altering database structures (such as tables and columns) and those for
querying and manipulating the data in those structures ([select](#select),
[insert](#insert), [update](#update) and [delete](#delete)).
`select` is the only statement for querying data while there are 3 different
statements for modifying data (insert, update and delete) which are grouped
into [modify](#data-modifying-statements) statements.

```antlr
statement
    : select
    | modify
    | define
    | noop
    ;
```

## Data modifying statements

The 3 data modifying statements are [insert](#insert), [update](#update) and [delete](#delete).

```antlr
modify
    : update
    | insert
    | delete
    ;
```

## Query and modifying statements
[Select](#select) and [modify](#data-modifying-statements) statements can be 
considered together in some statements (such as [with](#with-queries) statements)
where are both allowed as part of those statements (as 
[common table expressions](#common-table-expressions-cte) in `with`).

```antlr
queryUpdate
    : select
    | modify
    ;
```

## Select
A `select` statement extracts rows with a set columns (and metadata) from a
one or more joined tables and optionally subject to filters, groupings,
limits and orderings.

Selects can also be combined with [set operators](#set-operations-on-selects) (union, intersection, etc.)
or through [with](#with-queries) statements which creates temporary selects that can be used
to compose more complex `selects` in the same statement.

```antlr
select
    :  'select' (metadata ','?)? distinct? explicit? columns
      (  'from' tableExpr)?
      ( 'where' where=expr)?
      ( 'group' 'by' groupByList)?
      ('having' having=expr)?
      ( 'order' 'by' orderByList)?
      ('offset' offset=expr)?
      ( 'limit' limit=expr)?                #BaseSelection

    | select setop select                   #CompositeSelection

    | with                                  #WithSelection
    ;
```

## Metadata
Metadata in ESQL is a comma-separated list of [attributes](#attribute-list-in-metadata) surrounded by curly
braces ({}) with each [attribute](#attributes) consisting of a name-expression pair.
Metadata can be attached to a table and to its columns. For instance this is
a `create table` statement which defines metadata attributes on both the
table and its columns:

     create table com.example.S(
       {
         # table metadata (applied to all queries on this table)
         max_a: (max(a) from com.example.S),
         a_gt_b: a > b
       },
       a int {
         # column metadata attached to column a
         m1: b > 5, m2: 10, m3: a != 0
       },
       b int { m1: b < 0 },

       # derived columns (whose value are computed instead of stored) are
       # also supported in ESQL and is defined with a `=` between the name
       # of the column and the expression to compute its value. Metadata can
       # also be attached to derived columns
       ########
       c=a + b { label: 'Sum of a and b', m1: a > 5, m2: a + b, m3: b > 5 },
       d=b + c { m1: 10 },

       e int { m1: c },

       # ESQL also has a simplified select syntax for select expressions where
       # the `select` keyword is dropped and only a single column is specified.
       # Select expressions must be surrounded by parentheses.
       ########
       f=(max(a) from com.example.S) { m1: min(a) from com.example.S },
       g=(distinct c from com.example.S where d>5) { m1: min(a) from T },

       h int { m1: 5 }

       # select expressions can be arbitrarily complex and refer to the columns
       # in the current table as well as columns in other joined tables.
       ########
       i string {
         label: lv.label from lv:LookupValue
                         join  l:Lookup on lv.lookup_id=l._id
                                       and  l.name='City'
                        where lv.code=i
       }
     )

When a table is queried, its metadata and those on its queried columns are
also added to the query and can be overridden by attributes provided in the
query.

```antlr
metadata
    : '{' attributeList '}'
    ;
```

## Attribute list in metadata
[Metadata](#metadata) consists of a comma-separated list of attributes.

```antlr
attributeList
    : attribute (',' attribute)*
    ;
```

## Attributes
Each attribute consists of a simple name mapped to an [expression](#expressions) which must be
valid in the query that will be executed over the table where the [metadata](#metadata)
attribute is specified.

```antlr
attribute
    : Identifier ':' expr
    ;
```

## Distinct selects
The optional distinct clause in [select](#select) can be `all` which is the default
where all matching records are returned or `distinct` which means that only
unique records are returned with duplicates eliminated. `distinct` can
optionally be followed by an [expression list](#expression-lists) which is used to identify
duplicates.

```antlr
distinct
    : 'all'
    | 'distinct' ('on' '(' expressionList ')')?
    ;
```

## Explicit
The optional `explicit` keyword, when specified, disable the automatic
addition and expansion of [metadata](#metadata) in a query and returns a resultset
consisting only of the explicitly specified columns.

```antlr
explicit
    : 'explicit'
    ;
```

## Column list in selects
The `select` keyword is followed by the `columns` clause which is a
comma-separated sequence of columns to select into the result, with each
column being an [expression](#expressions) which can refer to columns in the tables specified
in the `from` clause of the select query.

```antlr
columns
    : column (',' column)*
    ;
```

## Columns
A column in a [select](#select) statement consists of an [expression](#expressions) which will be
executed in the context of the tables in the `from` clause of the select. The
column can be given an [alias](#alias) which will be the name of that column in the
result of the query. [Metadata](#metadata) can also be associated to the column and will
override any metadata defined for that column in the table (this only applies
when the columns refer exactly to a column in a table).

A column can also be the asterisk (*) character, optionally [qualified](#qualifiers), and is
expanded to all the columns in the tables joined for the query, or, if
qualified, all the columns in table referred to by the qualifier.

```antlr
column
    : (alias ':')? expr metadata?       #SingleColumn
    | qualifier? '*'                    #StarColumn
    ;
```

## Qualifiers
In queries where there are multiple tables, the table alias can be used to
qualify columns, when columns with the same name exists in more than one of
the queries tables. A qualifier consists of an identifier followed by '.'.
For example, `a.b` is the column `b` qualified with the table alias `a`.

```antlr
qualifier
  : Identifier '.'
  ;
```

## Alias
An alias consists of one or more alias parts separated by the front slash
('/'), and, optionally, starting with a front slash. An alias part can be a
qualified identifier or an escaped identifier. Qualified identifiers are one
or more identifiers (an identifier is a name starting with `[$_a-zA-Z]` and
optionally followed by zero or more `[$_a-zA-Z0-9]`) joined together with
periods. For instance, these are valid qualified identifiers:

    a
    b.x
    a.b.c

An escaped identifier is a sequence of one or more characters surrounded by
double quotes. These are all valid escaped identifiers:

    "Level 1"
    "!$$#42everything after$$#"
    ".a.b."

All the following are valid aliases:

    a
    age
    man/age
    /country/city/street
    /"level 1"/"level 2"
    x.y.z/"intermediate level"/b.y/"another level"/"yet another level"

```antlr
alias
    : (root='/')? aliasPart ('/' aliasPart)*
    ;
```

## Alias part
An alias part can be a qualified identifier or an escaped identifier.
Qualified identifiers are one or more identifiers (an identifier is a name
starting with `[$_a-zA-Z]` and optionally followed by zero or more
`[$_a-zA-Z0-9]`) joined together with periods. For instance, these are valid
qualified identifiers:

     a
     b.x
     a.b.c

An escaped identifier is a sequence of one or more characters surrounded by
double quotes. These are all valid escaped identifiers:

     "Level 1"
     "!$$#42everything after$$#"
     ".a.b."

```antlr
aliasPart
   : EscapedIdentifier                 #EscapedAliasPart
   | qualifiedName                     #NormalAliasPart
   ;
```

## Escaped identifiers
An escaped identifier is a sequence of one or more characters surrounded by
double quotes, and can be used to create identifiers with non-standard 
characters. These are all valid escaped identifiers:

     "Level 1"
     "!$$#42everything after$$#"
     ".a.b."

```antlr
EscapedIdentifier
    : '"' ~["]+ '"'
    ;
```

## Qualified identifiers
Qualified identifiers are one or more identifiers (an identifier is a name
starting with [$_a-zA-Z] and optionally followed by zero or more
[$_a-zA-Z0-9]) joined together with periods. For instance, these are valid
qualified identifiers:

     a
     b.x
     a.b.c

```antlr
qualifiedName
    : Identifier ('.' Identifier)*
    ;
```

## Table expressions 
The `from` clause of a `select`, `update` and `delete` contains a table 
expression which can be one of these:
1. A single table optionally aliased. If an alias is not provided, a default
   one with the table name (without schema) will be used. I.e. `a.b.X` is
   equivalent to `X:a.b.X`.

2. An aliased select statement: E.g. `select t.x, t.y from
   t:(select x, y, z from T)`.

3. A dynamic table expression which creates a named temporary table with rows
   as part of the query and allow selection from it. E.g.:
         `select a, b from X(a, b):((1, 'One'), (2, 'Two'), ('3, 'Three'))`

4. Cartesian product of any two table expressions: E.g.:
         `select x.a, x.b, y.c from x:X times y:Y`

5. A join (inner, left, right, full) of any two table expressions:
         `select x.a, x.b, y.c, z.d from x:X join y:Y on x.a=y.b left join z:Z on y.c=z.c`

Cartesian products and joins can combine any table expression types (including 
themselves) to form more complex table expressions through composition.

```antlr
tableExpr
    : (alias ':')? qualifiedName                                 #SingleTableExpr
    | alias ':' '(' select ')'                                   #SelectTableExpr
    | alias metadata? dynamicColumns ':' '(' rows ')'            #DynamicTableExpr
    | left=tableExpr 'times' right=tableExpr                     #CrossProductTableExpr
    | left=tableExpr JoinType? 'join' right=tableExpr 'on' expr  #JoinTableExpr
    ;
```

## Dynamic columns
A dynamic table expression is a set of column names with optional metadata
attached to each column.

```antlr
dynamicColumns
    : '(' nameWithMetadata (',' nameWithMetadata)* ')'
    ;
```

## Name with metadata 
A column in the definition of the type of a dynamic table expression consists
of an identifier and followed optionally by metadata for the column.

```antlr
nameWithMetadata
    : Identifier metadata?
    ;
```

## Join types
4 join types are available in ESQL: `left`, `right` and `full` joins are
built with these respective keywords before the `join` keyword in a table
expression; when none of those are provided, an inner join is assumed.

```antlr
JoinType
    : 'left'
    | 'right'
    | 'full'
    ;
```

## Group by list 
The `group by` clause of a `select` consists of a subset of expressions present
in the columns of select. 3 types of groupings are supported: `simple`, `rollup`
and `cube`. The latter two are indicated by surrounding the group list with the
keyword `rollup` and `cube` respectively.

```antlr
groupByList
    : expressionList                        #SimpleGroup
    | 'rollup' '(' expressionList ')'       #RollupGroup
    | 'cube'   '(' expressionList ')'       #CubeGroup
    ;
```

## Order by list
The `order by` clause of a `select` statement is a comma-separated list or
expression by which the select must order its result.

```antlr
orderByList
    : orderBy (',' orderBy)*
    ;
```

## Order by
An order expression in the `order by` list of a `select` is an expression
which can be followed optionally by a direction: `asc` (default) for
ascending or `desc` for descending.

```antlr
orderBy
    : expr direction?
    ;
```

## Order direction
Directions of order by expressions: `asc` (default) for ascending or `desc`
for descending.

```antlr
direction
    : 'asc'
    | 'desc'
    ;
```

## Set operations on selects
`select` can be combined with the following set operations:
1. `union`:     returns the union of two selects removing all duplicate rows;
2. `union all`: returns the union of two selects without removing any duplicate rows;
3. `intersect`: returns the intersection of two selects;
4. `except`:    returns the set difference between two selects
                (i.e. `A except B` returns all rows in A which are not also in B).
```antlr
setop
    : 'union'
    | 'union' 'all'
    | 'intersect'
    | 'except'
    ;
```

## With queries
`with` queries combine `select` and `modify` queries such that the result of
one query can be used in subsequent queries in the same `with` query. Not all
databases support the use of the result of modification queries in subsequent
query in the same `with` query (e.g. Postgresql does, SQL Server does not).
Some databases also do not support return values on modification queries.

In some cases, ESQL will simulate the feature in translated SQL but, in most
cases, the translated SQL will fail when executed on the database; therefore
the supported features of the underlying database must be known and ESQL
commands limited to the supported subset.

`with` queries consist of a list of common-table-expressions (CTE, i.e. a
selection or modification query) followed by a final selection/modification
query. The first CTE can be recursive and refer to itself (check SQL
specification on recursive with queries).

Example with query (an example upsert query):

     with modify(id) (
       update X set a=3, b=4 where c=100 returning id
     ),
     insert into X(a, b)
       select 3, 4
         from X left join modify on X.id=modify.id
        where modify.id is null

```antlr
with
    : 'with' recursive='recursive'? cteList queryUpdate
    ;
```

## List of Common Table Expressions (CTE)
A `with` query contains comma-separated list of common-table-expression (CTE)
followed by select/modify query.

```antlr
cteList
    : cte (',' cte)*
    ;
```

## Common Table Expressions (CTE)
A common-table-expression (CTE) consists of an identifier which names the CTE
followed by an optional list of column names that will be returned by the CTE
and the select/modify query that will be executed to produce the result of
the CTE.

```antlr
cte
    : Identifier names? '(' queryUpdate ')'
    ;
```

## Column names in CTE
The column names in a CTE is a comma-separated list of identifiers between
parentheses.

```antlr
names
    : '(' Identifier (',' Identifier)* ')'
    ;
```

## Insert
Rows can be inserted in a table with the `insert` statement which takes a table 
name, an optional list of column names (if not provided, all the columns in the 
given table are assumed in the same order that they were defined when the table 
was created), definition for the rows to insert and, optionally, a list of 
columns from the affected rows to return as the result of the statement.

Rows to insert are provided as one of:
1. a comma-separated list of row values; or
2. the keyword `default values` which means that one row will be inserted
   with the column values set to their default value or null, if such a
   row is valid; or
3. a `select` query returning a list of conformant rows to insert.

```antlr
insert
    : 'insert' 'into' (alias ':')? qualifiedName names?
      (('values' rows) | defaultValues | select)
      ('return' metadata? columns)?
    ;
```

## List of rows to insert
Rows to insert are a comma-separated list of row definitions.

```antlr
rows
    : row (',' row)*
    ;
```

## Row to insert
A row to insert is defined as a comma-separated list of expressions, surrounded
by parentheses.

```antlr
row
    : '(' expressionList ')'
    ;
```

## Row of default values
The keywords `default` `values` can be provided in an `insert` statement
to insert a row in the table with all columns set to their default values
or null if they do not have any default.

```antlr
defaultValues
    : 'default' 'values'
    ;
```

## Update
A `update` statement modifies matching rows from one or more joined tables.
In ESQL, the table to be updated is identified by its alias in the table
list specified in the `from` clause. This allows any select query to be
converted to an `update` by simply replacing the `select` keyword and
identifying the table to be updated in the `from` clause with its alias.

For example:

     update y
       from x:X join y:Y on x.y_id=y._id and y.status in ('A', 'T')
      where y.min_age <= x.age <= y.max_age

will only update rows in table Y that would be returned by an equivalent
`select` query.

```antlr
update
    :  'update' alias
         'from' tableExpr
          'set' setList
      ( 'where' expr)?
      ('return' metadata? columns)?
    ;
```

## Update set list
The set list of an `update` consists of comma-separated list of set instructions,
each of which consists of the column to update followed by '=' and the value
to update the column to.

```antlr
setList
    : set (',' set)*
    ;
```

## Set instructions
Set instructions in `update` consist of the name of the column to update and
the expression to update it with. Databases which support multi-table updates
(such as mysql) requires a qualified name for the column when multiple tables
are being updated in a single update. In databases where the column name must
be a single identifier, the translator will automatically drop the qualifiers,
if provided.

```antlr
set
    : qualifiedName '=' expr
    ;
```

## Delete
A `delete` statement deletes matching rows from one or more joined tables.
In ESQL, the table from which rows are to be updated is identified by its
alias in the table list specified in the `from` clause. This allows any
select query to be converted to a `delete` by simply replacing the `select`
keyword and identifying the table from which rows are to be deleted in the
`from` clause with its alias.

For example:

     delete y
       from x:X join y:Y on x.y_id=y._id and y.status in ('A', 'T')
      where y.min_age <= x.age <= y.max_age

will only delete rows in table Y that would be returned by an equivalent
`select` query.

```antlr
delete
    :  'delete' alias
         'from' tableExpr
      ( 'where' expr)?
      ('return' metadata? columns)?
    ;
```

## Expressions
An expression in ESQL is a sequence of valid ESQL keywords, symbols and other
tokens which can be computed to return a single-value.

```antlr
expr
      /*
       * Parentheses controls the order in which expressions are computed when
       * they are part of larger expressions.
       */
    : '(' expr ')'                                              #GroupingExpr

      /*
       * An expression surrounded by '$(' and ')' is known as an uncomputed
       * expression in that it is not computed by the interpreter and sent to
       * the database (or client) as a string. Uncomputed expressions are
       * useful when the expressions need to be computed outside of the
       * database (e.g., validation check that can be executed on a client
       * to the check the validity of a value before sending to the database).
       */
    | '$(' expr ')'                                             #UncomputedExpr

      /*
       * Refers to the default value applicable in certain context such as when
       * inserting values and updating columns (to their defaults)
       */
    | type '<' expr '>'                                         #CastExpr

      /*
       * The default value applicable in certain context such as when inserting
       * values and updating columns (to their defaults)
       */
    | 'default'                                                 #DefaultValue

      /*
       * Any literal (such as a number or string) is a valid expression.
       */
    | literal                                                   #LiteralExpr

      /*
       * Coalesce is a '?'-separated list of expressions returning the value of
       * the first non-null expression in the list. E.g. `x?0` is evaluated as
       * `x` if `x` is not null, 0 otherwise. `x?y?z` is evaluated as the `x` if
       * it is not null, `y` if `x` is null and `y` is not null, `z` otherwise.
       */
    | expr ('?' expr)+                                          #CoalesceExpr

      /*
       * `||` is the concatenation operator.
       */
    | expr ('||' expr)+                                         #ConcatenationExpr

      /*
       * Any numeric expression can be negated by prefixing it with `-`.
       */
    | '-' expr                                                  #NegationExpr

      /*
       * `^` is the exponentiation operator in ESQL and it is right-associative.
       */
    | <assoc=right> left=expr '^' right=expr                    #ExponentiationExpr

      /*
       * Multiplicative operators include `*`, `/` and `%` to calculate the
       * multiplication, division and modulus of two operands. They are at the
       * same precedence level.
       */
    | left=expr op=('*' | '/' | '%') right=expr                 #MultiplicationExpr

      /*
       * Additive operators include `+` and `-` to calculate the addition and
       * subtraction of two operands. They are at the same precedence level.
       */
    | left=expr op=('+' | '-') right=expr                       #AdditionExpr

      /*
       * A named parameter consists of a name preceded with a colon (:). Values
       * for named parameters must be provided when a statement containing them
       * is executed.
       */
    | ':' Identifier                                            #NamedParameter

      /*
       * A named argument to a function. The name is dropped when this is
       * translated to SQL as most databases do not support named arguments yet.
       * This is however useful as macro parameters to control the macro
       * expansion. Since macros are expanded before translation of the ESQL
       * statement to SQL, the named parameters can be interpreted by the macro
       * and dropped from the expansion.
       */
    | Identifier ':=' expr                                      #NamedArgument

      /*
       * A single-column, single-row only select used as an expression.
       */
    | selectExpression                                          #SelectExpr

      /*
       * The inverse of a boolean expression.
       */
    | Not expr                                                  #NotExpr

      /*
       * A function call consists of the function name (which can be qualified)
       * followed by an optional comma-separated list of zero or more
       * expressions as arguments to the function (or a single select to cover
       * certain special function such as exists which check whether a select
       * has returned one or more rows), followed optionally by a window
       * characterisation (applicable only to window functions). The start of
       * the argument list may contain a `distinct` definition which is
       * applicable to certain aggregate functions (such as `count`).
       */
    | qualifiedName
      '(' distinct? (expressionList | select | star='*')? ')'
      window?                                                   #FunctionInvocation

      /*
       * Two-sided comparison similar to what is present in the Python language
       * and similar to (but more powerful than) the `between` operator in SQL.
       * E.g., this is a valid such expression:  `min_age <= age < max_age`.
       * The expression consists of three operands and two comparison operators
       * simultaneously comparing the middle operand to the left and right ones.
       * All three operands can be expressions with the middle one limited to
       * certain type of expressions to allow for correct syntactic parsing. All
       * 4 comparison operators (<, <=, >, >=) are allowed in any combination.
       */
    |         left = expr
       leftCompare = ordering
               mid = simpleExpr
      rightCompare = ordering
             right = expr                                       #RangeExpr

      /*
       * Binary comparison of two expressions.
       */
    | left=expr compare right=expr                              #Comparison

      /*
       * Compare an expression to the values returned by a single-column select
       * using either one of two quantifiers: `any` or `all`; for `any` the
       * the comparison must be true between the expression and at least one
       * value in the result of the select. for `all` the comparison must be
       * true for all values for the whole expression to be true.
       */
    | expr compare Quantifier '(' select ')'                    #QuantifiedComparison

      /*
       * True if the expression matches one of the expression in the `in` list
       * or a value returned by the `select`.
       */
    | expr Not? 'in' '(' (expressionList | select) ')'          #InExpression

      /*
       * True if a comparable expression is in the range provided in the
       * `between` expressions (inclusive).
       */
    | left=expr Not? 'between' mid=expr 'and' right=expr        #BetweenExpr

      /*
       * Like comparison operator.
       */
    | left=expr Not? 'like' right=expr                          #LikeExpr

      /*
       * Case-insensitive like comparison operator.
       */
    | left=expr Not? 'ilike' right=expr                         #ILikeExpr

      /*
       * True if the expression is null (or not null).
       */
    | expr 'is' Not? NullLiteral                                #NullCheckExpr

      /*
       * A reference to a column.
       */
    | columnReference                                           #ColumnExpr

      /*
       * Logical and.
       */
    | left=expr 'and' right=expr                                #LogicalAndExpr

      /*
       * Logical or.
       */
    | left=expr 'or'  right=expr                                #LogicalOrExpr

      /*
       * Ternary `if` following the same syntax as in Python:
       *    <true result> if <condition> else <expression if condition is false>
       * E.g.:
       *      'a' if x
       * else 'b' if y
       * else 'c' if z
       * else 'e'
       */
    | <assoc=right> expr ('if' expr 'else' expr)+               #CaseExpr

      /*
       * A restricted expression that can be used as the middle expression in
       * range comparison (limited to avoid ambiguity in the parser).
       */
    | simpleExpr                                                #SimpleExpression
    ;
```

## Simple expressions
A simple expression is a restricted expression which excludes certain combinations 
of expression and can be used as the middle expression of a range (e.g `a > simpleExpr > b`)

```antlr
simpleExpr
    : '(' simpleExpr ')'                                                              #SimpleGroupingExpr
    | type '<' simpleExpr '>'                                                         #SimpleCastExpr
    | literal                                                                         #SimpleLiteralExpr
    | simpleExpr ('?' simpleExpr)+                                                    #SimpleCoalesceExpr
    | simpleExpr ('||' simpleExpr)+                                                   #SimpleConcatenationExpr
    | '-' simpleExpr                                                                  #SimpleNegationExpr
    | <assoc=right> left=simpleExpr '^' right=simpleExpr                              #SimpleExponentiationExpr
    | left=simpleExpr op=('*' | '/' | '%') right=simpleExpr                           #SimpleMultiplicationExpr
    | left=simpleExpr op=('+' | '-') right=simpleExpr                                 #SimpleAdditionExpr
    | selectExpression                                                                #SimpleSelectExpr
    | qualifiedName '(' distinct? (expressionList | select | star='*')? ')' window?   #SimpleFunctionInvocation
    | columnReference                                                                 #SimpleColumnExpr
    | <assoc=right> simpleExpr ('if' simpleExpr 'else' simpleExpr)+                   #SimpleCaseExpr
    ;
```

## Column reference
A column can be referred to by its name in a table or its alias if the
column has been renamed in the query, optionally qualified.

```antlr
columnReference
  : qualifier? alias
  ;
```

## Select as an expression
A select returning zero or one row with a single column and can thus be used
where an expression needs to return a single value such as in metadata
attributes. When used as an expression, the select keyword can be dropped but
the whole expression must be surrounded by parenthesis ('(' and ')').

For example:

    age_max: (select max(age) from People)
    age_min: (min(age) from People)

```antlr
selectExpression
    : '(' 'select'?
          distinct?
          (alias ':')? col=expr
            'from' tableExpr
         ( 'where' where=expr)?
         ( 'order' 'by' orderByList)?
         ('offset' offset=expr)?
      ')'
    ;
```

## Window definition
Certain functions can be followed by a window definition that defines a
window of rows around the current row that the function will operate upon.
A window definition consist of an optional partition definition and an
optional order list.

```antlr
window
  : 'over' '(' partition? ('order' 'by' orderByList)? ')'
  ;
```

## Window partition
A partition is a list of expression by which the result will be sliced and
the window function executed on each slice iteratively.

```antlr
partition
  : 'partition' 'by' expressionList
  ;
```

## Comparison operators
Comparison operators in ESQL include equality (`=`), inequality (`!=`),
and ordering comparisons (`<`, `<=`, `>`, `>=`).

```antlr
compare
  : '='
  | '!='
  | '<'
  | '>'
  | '<='
  | '>='
  ;
```

## Ordering comparisons
Ordering comparisons (`<`, `<=`, `>`, `>=`) are assigned to their own
production rule as they are used exclusively in range comparisons.

```antlr
ordering
  : '<'
  | '>'
  | '<='
  | '>='
  ;
```

## Expression lists
Expression lists, which are comma-separated lists of expressions, are used in
various parts of ESQL statements, such as in ordering list, group by list
and window partition list.

```antlr
expressionList
  : expr (',' expr)*
  ;
```

## Literals
Literals are values of the different types supported by ESQL, and can be
divided into base literals (numbers, strings, booleans, etc.), the special
null literal ('null'), arrays, JSON arrays and JSON objects.

ESQL array literals are defined by their element type (int, string, etc.)
followed by their elements surrounded by square brackets. E.g.:

     int[1, 4, 10, 3]
     string['a', 'b', 'c']

```antlr
literal
   : baseLiteral                           #BasicLiterals
   | NullLiteral                           #Null
   | Identifier '[' baseLiteralList? ']'   #BaseArrayLiteral
   | '[' literalList? ']'                  #JsonArrayLiteral       // valid only in metadata expression
   | '{' attributeList? '}'                #JsonObjectLiteral      // valid only in metadata expression
   ;
```

## Base literals
Base literals include integers, floating point numbers, booleans, single-line
and multi-line strings, UUIDs, dates and times, and intervals.

```antlr
baseLiteral
    : IntegerLiteral            #Integer
    | FloatingPointLiteral      #FloatingPoint
    | BooleanLiteral            #Boolean
    | MultiLineStringLiteral    #MultiLineString
    | StringLiteral             #String
    | UuidLiteral               #Uuid
    | DateLiteral               #Date
    | IntervalLiteral           #Interval
    ;
```

## Literal list
A comma-separated list of literals is used to define the elements of a JSON
array literal.

```antlr
literalList
  : literal (',' literal)*
  ;
```

## Base literal list
A comma-separated list of base literals is used to define the elements of an
ESQL array literal.

```antlr
baseLiteralList
  : baseLiteral (',' baseLiteral)*
  ;
```

## Integer literals
Integer literals in ESQL consist of a sequence of digits preceded optionally
by a minus sign (-) for negative values.

```antlr
IntegerLiteral
  : '-'? Digits
  ;
```

## Floating-point literals
Floating-Point literals in ESQL consist of a mantissa and an exponent part.

```antlr
FloatingPointLiteral
  : '-'? Digits '.' Digits? ExponentPart?
  | '-'? '.' Digits ExponentPart?
  | '-'? Digits ExponentPart
  ;
```

## Underscore in digits
Underscore ('_') can be used to separate digits in large numbers for readability.
E.g. 1_000_000_000

```antlr
  fragment Digits
  : Digit
  | Digit ('_' | Digit)* Digit
  ;
```

## Boolean literals
The boolean literals are `true` and `false`.
```antlr
BooleanLiteral
  : 'true'
  | 'false'
  ;
```

## Null literal
`null` is the null literal in ESQL.
```antlr
NullLiteral
  : 'null'
  ;
```

## String literal
A string literal is a sequence of characters surrounded by single-quotes (').
To add a single-quote character in the string, the single-quote must be
repeated ('').

```antlr
StringLiteral
  : '\'' (StringCharacter | DoubleSingleQuote)* '\''
  ;
```

## Multiline string literals
Multi-line strings in ESQL are surrounded by back-ticks (`) and can span
more than one lines. These strings have special support for stripping the left
margin of spaces.

```
For example the following text:
        `func x(a,b) {
           a = a^b
           return a%b
         }`

will be interpreted as the following, after stripping (all leading spaces removed
from each line but preserving identation):
func x(a,b) {
  a = a^b
  return a%b
}
```
```antlr
MultiLineStringLiteral
  : '`' .*? '`'
  ;
```

## UUID literals
UUIDs are strings of 32 hexadecimal digits surrounded by single-quotes and
preceded with a 'u' character. For instance,

    u'a124bf41-bfeb-aae4-ffee-039fbcde00ee'

```antlr
UuidLiteral
   : 'u\'' HexDigit HexDigit HexDigit HexDigit HexDigit HexDigit HexDigit HexDigit
   '-' HexDigit HexDigit HexDigit HexDigit
   '-' HexDigit HexDigit HexDigit HexDigit
   '-' HexDigit HexDigit HexDigit HexDigit
   '-' HexDigit HexDigit HexDigit HexDigit HexDigit HexDigit HexDigit HexDigit HexDigit HexDigit HexDigit HexDigit '\''
   ;
```

## Date and time literals
Dates and times literals are surrounded by single-quotes and preceded with the
letter `d`. For example, `d'2021-04-12'`, `d'11:25'` and `d'2021-03-19 11:23:54'`

```antlr
DateLiteral
  : 'd\'' Date '\''
  | 'd\'' Time '\''
  | 'd\'' Date ' ' Time '\''
  ;
```

## Date part in date literals
The date part of a date literal consists of a 2-digits or 4-digits year
followed by a single or double digits month and single or double digits day,
each separated by a `-`. E.g., 2002-04-1, 1955-12-01, 99-01-01

```antlr
fragment Date
  : Digit? Digit? Digit Digit '-' [01]? Digit '-' [0123]? Digit
  ;
```

## Time part in date literals
The time part of a date literal consists of a one or two-digit hour
followed by a single or double digits minute and, optionally, a single or
double digits second and finally a one, two or three digits millisecond.
The hour, minute and second must be separated by a `:`, while the millisecond,
if present, must be separated from the second with a `.`.
E.g., `10:45`, `10:43:12`, `15:3:5.34`

```antlr
fragment Time
  : Digit? Digit ':' [012345]? Digit (':' [012345]? Digit ('.' Digit Digit? Digit?)?)?
  ;
```

## Interval literals
An interval literal represents a duration and consists of one or more interval
parts surround by single-quotes and preceded with the letter `i`. Each interval
part consist of an integer followed by a suffix representing a specific duration.
For example: `i'3mon 2w 1d 1m'` represents a duration of 3 months 2 weeks 1 day
and 1 minute.

```antlr
IntervalLiteral
  : 'i\'' IntervalPart (',' IntervalPart)* '\''
  ;
```

## Interval part
An interval part consists of positive or negative integer followed by a suffix
denoting the type of duration that the interval part represents.

```antlr
fragment IntervalPart
  : '-'? Digits IntervalSuffix
  ;
```

## Interval part suffix
The interval part suffixes are:
* 'd' for days
* 'w' for weeks
* 'mon' for months
* 'y' for year
* 'h' for hours
* 'm' for minutes
* 's' for seconds
* 'ms' for milliseconds

```antlr
fragment IntervalSuffix
  : 'd'        // days
  | 'w'        // weeks
  | 'mon'      // months
  | 'y'        // years
  | 'h'        // hours
  | 'm'        // minutes
  | 's'        // seconds
  | 'ms'       // milliseconds
  ;
```

## Data definition
ESQL support several data definition command to create database objects.
Currently, tables can be created, altered and dropped.
```antlr
define
  : createTable
  | alterTable
  | dropTable
  ;
```


## Create table
`create table` is used to create a table or modify an existing table.
In ESQL, `create table` does not fail if the table already exists; instead
the existing table is altered to match the table definition in the `create table`
statement. New columns are added, types are changed , and so on. However some
of these changes might fail if the underlying database cannot safely make
them. For instance, changing the type of a column from string to int will
fail.
As a precaution, columns and constraints are not dropped automatically if
they are not present in the new definition, unless the `drop undefined`
keywords are specified in the command.

```antlr
createTable
  : 'create' 'table' qualifiedName dropUndefined? '(' tableDefinitions ')'
  ;
```

## Drop undefined table definitions
If present in a `create table` statement, removes any excess definitions
(columns, constraints, etc.) when automatically updating the table.

```antlr
dropUndefined
  : 'drop' 'undefined'
  ;
```

## List of table definitions
A `create table` statement consists of a comma-separated list of table
definitions, including the definition of columns, derived columns, constraints
and metadata.

```antlr
tableDefinitions
  : tableDefinition (',' tableDefinition)+
  ;
```


## Table definition
A table definition in a `create table` statement can be one of these:
definition of a column, a derived column, a constraint or metadata.

```antlr
tableDefinition
  : columnDefinition
  | derivedColumnDefinition
  | constraintDefinition
  | metadata
  ;
```

## Column definition
A column definition consists of its name as an identifier, its type and, optionally,
if nulls are prohibited (`not null`), a default value and metadata.

```antlr
columnDefinition
  : Identifier type (Not NullLiteral)? ('default' expr)? metadata?
  ;
```

## Definition of a derive column
A table column whose value is derived from an expression. Any expression
which can be part of the column list of a select statement can be used to
compute the value of a derived column.

```antlr
derivedColumnDefinition
  : Identifier '=' expr metadata?
  ;
```

## Constraints definition
Four types of constraints can be specified when creating a table:
1. `unique` constraints over a set of columns whereupon the combined value
   over these columns is not duplicated over the table.
2. `primary key` constraints is similar to unique constraints with the
   additional requirement that none of the columns can be null.
3. `check` constraints apply a conditional expression on one or more columns
   in the table, with rows allowed only when the conditional expression is true.
4. `foreign key` constraints limits the values that can be inserted in one or
   more columns of the table to values in corresponding columns in another table.

With foreign keys, a database can be considered as a directed graph where the
tables are the vertices and a foreign key between two tables represents an
edge from the corresponding tables in the graph.

This interpretation of a database can be used to find a path between two
tables which can be used for various purposes; currently this is used to
restrict one table based on a criteria on another linked table for
access control and row-level security. A foreign key can be followed from the
table where the foreign key is defined to the table being referred (the forward
path) or in the reverse direction (the reverse path).

To allow for the search of lowest-cost path between tables, cost values
can be associated foreign keys in ESQL using the `cost` keyword in the foreign
key definition, followed by cost of traversing the link in the forward
direction (forward cost) and, optionally, by the cost of traversing the link
in the reverse direction (reverse cost). If only the forward path cost is
specified, twice its value is assigned to the reverse path cost; this is so
as to prefer, by default, forward paths over reverse ones, when searching for
paths between tables.

A negative cost prevents the respective path (forward or reverse) to be
followed effectively removing that link from any path that a search
algorithm will return.

A zero or positive value is the cost for following the link and a uniform
cost search (such as Djikstra shortest path algorithm) can be used to find
shortest path by cost between tables.

A forward cost of 1 and a reverse cost of 2 is assumed when not specified,
making forward paths preferable to reverse ones, as the default behaviour.

```antlr
constraintDefinition
  : constraintName? 'unique' names                    #UniqueConstraint
  | constraintName? 'primary' 'key' names             #PrimaryKeyConstraint
  | constraintName? 'check' '(' expr ')'              #CheckConstraint
  | constraintName? 'foreign' 'key'
  from=names 'references' qualifiedName to=names

  ('cost' '(' forwardcost=IntegerLiteral (',' reversecost=IntegerLiteral)? ')')?

  /*
  * The constraint can have an 'on update' action, an 'on delete' action,
  * neither or both. To implement this, we have to specify the clause
  * twice to ensure that it caters for all 4 cases. This will erroneously
  * allow for a delete or update rule to be specified twice, which is
  * then caught in the Analyser.
  */
  (onUpdate | onDelete)?
  (onUpdate | onDelete)?                            #ForeignKeyConstraint
  ;
```

## Naming constraints
Constraints have an optional name (which must be an identifier); when provided
this must be preceded with the `constraint` keyword. When not provided, a
constraint is named automatically based on its source and target tables and
columns.

```antlr
constraintName
  : 'constraint' Identifier
  ;
```

## On update foreign key action
Foreign keys can have on `on update` action which defines what is to happen
to source columns of the foreign key when the target columns are updated.
The actions can be `restrict` which prevents the update to take place with
an error, `cascade` which propagates the update to the source columns,
`set null` whereupon the source columns are set to null and `set default`
where the source columns are set to their default value.

```antlr
onUpdate
  : 'on' 'update' foreignKeyAction
  ;
```

## On delete foreign key action
Foreign keys can have on `on delete` action which defines what is to happen
to source columns of the foreign key when the target row is deleted.
The actions can be `restrict` which prevents the delete to take place with
an error, `cascade` which deletes the rows containing the source columns
referring to the deleted rows in the target table, `set null` whereupon the
source columns are set to null and `set default` where the source columns are
set to their default value.

```antlr
onDelete
  : 'on' 'delete' foreignKeyAction
  ;
```


## Foreign key actions (on delete and on update)
Foreign keys actions for `on delete` and `on update` clause of foreign keys.

```antlr
foreignKeyAction
  : 'restrict'
  | 'cascade'
  | 'set' 'null'
  | 'set' 'default'
  ;
```

## Alter table
`alter table` is used to apply one or more alterations to an existing table.
Alterations include adding columns, changing column names, types, null status
default value or metadata, or dropping columns, adding or dropping constraints
and adding or dropping metadata.

```antlr
alterTable
  : 'alter' 'table' qualifiedName alterations
  ;
```

## List of alterations
A list of comma-separated alterations can be specified in an `alter table`
statement.
```antlr
alterations
  : alteration (',' alteration )*
  ;
```

## Table alterations
Table alterations include adding columns, changing column names, changing
column types, changing null status, changing default value or metadata,
dropping columns, adding or dropping constraints and adding or dropping
metadata.

```antlr
alteration
  : 'rename' 'to' Identifier                                  #RenameTable

  | 'add' tableDefinition                                     #AddTableDefinition

  | 'alter' 'column' Identifier alterColumnDefinition         #AlterColumn

  | 'drop' 'column' Identifier                                #DropColumn
  | 'drop' 'constraint' Identifier                            #DropConstraint
  | 'drop' 'metadata'                                         #DropTableMetadata
  ;
```

## Alter column
The `alter column` clause in `alter table` is used to change column names,
type, null status, default value and metadata.

```antlr
alterColumnDefinition
  : Identifier?           // new column name
    type?                 // new column type
    alterNull?            // null state
    alterDefault?         // column default
    metadata?             // column metadata
  ;
```

## Alter null status of a column
The null status in the `alter column` clause of an `alter table` can be
specified as either `not null` or `null`.

```antlr
alterNull
  : 'not' 'null'
  | 'null'
  ;
```

## Alter default value of a column
The default value in the `alter column` clause of an `alter table` can be
specified as the keyword `default` followed by an expression for the default
value or the keyword `no default` to signify that the previous default value
for the column is to be dropped.

```antlr
alterDefault
  : 'default' expr
  | 'no' 'default'
  ;
```

## Drop table
A table can be dropped using the `drop table` statement.

```antlr
dropTable
  : 'drop' 'table' qualifiedName
  ;
```

## Data types
ESQL support base types and arrays, where array types are formed from a type
followed by an optional integer size in square brackets.
This table shows the current types that ESQL supports and which types they
are mapped to in PostgreSql and SQL Server:

   | ESQL TYPE  | POSTGRESQL TYPE    | SQL SERVER TYPE  |
   |------------|--------------------|------------------|
   | byte       | tinyint            | tinyint          |
   | short      | smallint           | smallint         |
   | int        | integer            | int              |
   | long       | bigint             | bigint           |
   | float      | real               | real             |
   | double     | double precision   | float            |
   | money      | money              | money            |
   | bool       | boolean            | bit              |
   | char       | char(1)            | char(1)          |
   | string     | text               | varchar(8000)    |
   | text       | text               | varchar(max)     |
   | bytes      | bytea              | varbinary(max)   |
   | date       | date               | date             |
   | time       | time               | time             |
   | datetime   | timestamp          | datetime2        |
   | interval   | interval           | varchar(200)     |
   | uuid       | uuid               | uniqueidentifier |
   | json       | jsonb              | varchar(max)     |

An array type can be formed from any type, including arrays themselves to
create multi-dimensional arrays. E.g. `int[5]`, `string[10][10]`.

```antlr
type
  : Identifier                       #Base       // A base type is simply an identifier
  | type '[' IntegerLiteral? ']'     #Array      // Array of arrays are supported by multiple by following with
                                                 // a type with any number of '['']'
  ;
```

## Identifier
An identifier can start with a letter, an underscore or the dollar sign,
followed by zero or more letters, numbers, underscores and dollar signs. The
dollar sign is reserved for internal use (although ESQL does not enforce this.

```antlr
Identifier
  : [$_a-zA-Z][$_a-zA-Z0-9]*
  ;
```

## Quantifiers
The keywords `any` and `all` are used in quantified comparisons of the form:

     expression compare Quantifier '(' select ')'

A quantified comparison returns true if the expression matches any records in
the select result when `any` is used as the quantifier or if the expression
matches all the records in the select result when `all` is used as the quantifier.

```antlr
Quantifier
  : 'all' | 'any'
  ;
```

## Comments
Comments in ESQL start with '#' and goes to the end of the line.

```antlr
  Comment
  : '#' ~[\r\n]* -> skip
  ;
```
