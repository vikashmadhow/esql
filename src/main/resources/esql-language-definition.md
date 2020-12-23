# ESQL V2 specification

## <a name='about'>About</a>

ESQL aims to be a database query language similar in syntax to SQL but with
extensions to include metadata into the query language and the ability to be
transparently translated to different implementations of the SQL language 
supported by the main relational databases in use. PostgreSQL, Microsoft SQL Server
and HyperSQL are the currently supported databases with more planned in the near future.

I built a first version of ESQL in 2018 to use for my own projects and have used it 
successfully in several projects in Mauritius, Eswatini and Ethiopia. ESQL greatly 
reduced the effort required to deliver projects through 1) its metadata which can be 
used to automatically generate rich user interfaces, reports, user filters, data imports, 
etc. and 2) its translation to various databases allows for a single language to be used
even if the different projects use different databases.

Experience over the last 2 years has also revealed some flaws in the initial
design of the language, including the lack of a full specification, missing rules on how 
to combine metadata in complex queries and missing rules on how to compute metadata 
in different type of queries such as in the presence of aggregate functions and when
using sub-queries. This is a revised design addressing those issues, enhancing the 
language and building a more robust implementation.

### <a name='overview'>Overview</a>
The core feature of ESQL is that it all metadata to be embedded in table definitions
and queries. Metadata in ESQL is a comma-separated list of attributes surrounded by 
curly braces ({}) with each attribute consisting of a name-expression pair. Metadata 
can be attached to a table and to its columns. For instance this is a `create table`
statement which defines metadata attributes on both the table and its columns:

```postgresql
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
```

When a table is queried, its metadata and those on its queried columns are added
to the query and can be overridden by metadata provided directly in the query. For
example:

```sql
select { a_gt_b: a != b },
       a,
       c { m3: b > 1500 }
  from com.example.S
 where d > 1000
```

will be conceptually expanded to:
```sql
select { 
         max_a: (max(a) from com.example.S),  # from definition of the table
         a_gt_b: a != b                       # override the default attribute with the same name on the table
       },
       a {
         m1: b > 5, m2: 10, m3: a != 0
       },
       c { 
         label: 'Sum of a and b', 
         m1: a > 5, 
         m2: a + b, 
         m3: b > 1500                         # overridden                         
       }
  from com.example.S
 where d > 1000
```

When translated to the SQL supported by the underlying database, the metadata attributes are converted to 
valid columns in the query aliased in a way that they are recognised as metadata and not normal columns. 

### <a name='overview'>What ESQL is not</a>

ESQL is not a full database virtualization layer and does not aim to completely hide the complexity of the
underlying database; it can simplify the development of software that can work transparently different databases
in most cases; however the features of the underlying database still need to be kept in mind when using ESQL.

For instance, modification queries (insert, delete, update) producing a resultset is not supported in all
databases. When it does ESQL will translate to the correct SQL and function as expected. If this is not
supported, the query will fail. Thus, using ESQL only does not guarantee that software can be migrated to
any database; ESQL remains limited by the underlying features of the database.

However, where possible, ESQL emulates some features which are not present in the underlying database (such 
as the boolean datatype on SQL Server) and, together with some care in which feature to use and which to avoid,
allows for the development of software that works on several different databases with minimal development effort. 


## <a name=''>Language specification</a>
The grammar of the language is defined as a set of production rules in ANTLR which is repeated here with comments:

```antlrv4
grammar Esql;

@header {
    package ma.vi.esql.grammar;
}

/**
 * An ESQL program is a semi-colon separated sequence of ESQL statements.
 */
program
    : statement (';' statement)* ';'?
    ;

/**
 * A semi-colon can also be interpreted as a silent no-operation (noop)
 * statement. Noops can be used to disable certain part of a program
 * dynamically (by replacing the statement with a noop) without having
 * to remove the statement which can be harder in some cases.
 */
noop
    : ';'
    ;

/**
 * ESQL statements can be divided into two groups: statements for defining
 * and altering database structures (such as tables and columns) and those
 * for querying and manipulating the data in those structures (select, insert, etc.).
 * select is the only statement for querying data while there are 3 different
 * statements for modifying data (insert, update and delete) which are grouped
 * into modify statements.
 */
statement
    : select
    | modify
    | define
    | noop
    ;

/**
 * The 3 data modifying statements are update, insert and delete.
 */
modify
    : update
    | insert
    | delete
    ;

/**
 * select and modify are the data manipulation group of statements in ESQL.
 */
queryUpdate
    : select
    | modify
    ;

/**
 * A `select` statement extracts rows with a set columns (and metadata)
 * from a one or more joined tables and optionally subject to filters,
 * groupings, limits and orderings.
 *
 * Selects can also be combined with set operators (union, intersection, etc.)
 * or through `with` statements which creates temporary selects that can
 * be used to compose more complex `selects` in the same statement.
 */
select
    : 'select'  (metadata ','?)? distinct? explicit? columns
      ('from'   tableExpr)?
      ('where'  where=expr)?
      ('order'  'by' orderByList)?
      ('group'  'by' groupByList)?
      ('having' having=expr)?
      ('offset' offset=expr)?
      ('limit'  limit=expr)?                #BaseSelection

    | select setop select                   #CompositeSelection

    | with                                  #WithSelection
    ;

/**
 * Metadata in ESQL is a comma-separated list of attributes surrounded by curly
 * parentheses ({}) with each attribute consisting of a name-expression pair.
 * Metadata can beattached to a table and to its columns. For instance this is
 * a `create table` statement which defines metadata attributes on both the table
 * and its columns:
 *
 *      create table com.example.S(
 *        {
 *          # table metadata (applied to all queries on this table)
 *          max_a: (max(a) from com.example.S),
 *          a_gt_b: a > b
 *        },
 *        a int {
 *          # column metadata attached to column a
 *          m1: b > 5, m2: 10, m3: a != 0
 *        },
 *        b int { m1: b < 0 },
 *
 *        # derived columns (whose value are computed instead of stored) are
 *        # also supported in ESQL and is defined with a `=` between the name
 *        # of the column and the expression to compute its value. Metadata can
 *        # also be attached to derived columns
 *        ########
 *        c=a + b { label: 'Sum of a and b', m1: a > 5, m2: a + b, m3: b > 5 },
 *        d=b + c { m1: 10 },
 *
 *        e int { m1: c },
 *
 *        # ESQL also has a simplified select syntax for select expressions where
 *        # the `select` keyword is dropped and only a single column is specified.
 *        # Select expressions must be surrounded by parentheses.
 *        ########
 *        f=(max(a) from com.example.S) { m1: min(a) from com.example.S },
 *        g=(distinct c from com.example.S where d>5) { m1: min(a) from T },
 *
 *        h int { m1: 5 }
 *
 *        # select expressions can be arbitrarily complex and refer to the columns
 *        # in the current table as well as columns in other joined tables.
 *        ########
 *        i string {
 *          label: lv.label from lv:LookupValue
 *                          join  l:Lookup on lv.lookup_id=l._id
 *                                        and  l.name='City'
 *                         where lv.code=i
 *        }
 *      )
 *
 * When a table is queried, its metadata and those on its queried columns are also added
 * to the query and can be overridden by attributes provided in the query.
 */
metadata
    : '{' attributeList '}'
    ;

/**
 * Metadata consists of a comma-separated list of attributes.
 */
attributeList
    : attribute (',' attribute)*
    ;

/**
 * Each attribute consists of a simple name and an expression which must be valid
 * in the query that will be executed over the table where the metadata attribute
 * is specified.
 */
attribute
    : Identifier ':' expr
    ;

/**
 * The optional distinct clause in `select` can be `all` which is the default where
 * all matching records are returned or `distinct` which means that only unique records
 * are returned with duplicates eliminated. `distinct` can optionally be followed by
 * by an expression list which is used to identify duplicates.
 */
distinct
    : 'all'
    | 'distinct' ('on' '(' expressionList ')')?
    ;

/**
 * The optional `explicit` keyword, when specified, disable the automatic addition and
 * expansion of metadata in a query and returns a resultset consisting only of the
 * explicitly specified columns.
 */
explicit
    : 'explicit'
    ;

/**
 * The `select` keyword is followed by the `columns` clause which is a comma-separated
 * sequence of columns to select into the result, with each column being an expression
 * which can refer to columns in the tables specified in the `from` clause of the select
 * query.
 */
columns
    : column (',' column)*
    ;

/**
 * A column in a select statement consists of a expression which will be executed
 * in the context of the tables in the `from` clause of the select. The column can
 * be given an alias which will be the name of that column in the result of the query.
 * Metadata can also be associated to the column and will override any metadata
 * defined for that column in the table (this only applies when the columns refers
 * exactly to a column in a table).
 *
 * A column can also be the asterisk (*) character, optionally qualified, and is
 * expanded to all the columns in the tables joined for the query, or, if qualified,
 * all the columns in table referred to by the qualifier.
 */
column
    : (alias ':')? expr metadata?       #SingleColumn
    | qualifier? '*'                    #StarColumn
    ;

/**
 * An alias consists of one or more alias parts separated by the front slash ('/'), and,
 * optionally, starting with a front slash. An alias part can be a qualified identifier
 * or an escaped identifier. Qualified identifiers are one or more identifiers (an identifier
 * is a name starting with [$_a-zA-Z] and optionally followed by zero or more [$_a-zA-Z0-9])
 * joined together with periods. For instance, these are valid qualified identifiers:
 *
 *    a
 *    b.x
 *    a.b.c
 *
 * An escaped identifier is a sequence of one or more characters surrounded by double quotes.
 * These are all valid escaped identifiers:
 *
 *    "Level 1"
 *    "!$$#42everything after$$#"
 *    ".a.b."
 *
 * All the following are valid aliases:
 *
 *    a
 *    age
 *    man/age
 *    /country/city/street
 *    /"level 1"/"level 2"
 *    x.y.z/"intermediate level"/b.y/"another level"/"yet another level"
 *
 */
alias
    : (root='/')? aliasPart ('/' aliasPart)*
    ;

/**
 * An alias part can be a qualified identifier or an escaped identifier. Qualified identifiers
 * are one or more identifiers (an identifier is a name starting with [$_a-zA-Z] and optionally
 * followed by zero or more [$_a-zA-Z0-9]) joined together with periods. For instance, these
 * are valid qualified identifiers:
 *
 *    a
 *    b.x
 *    a.b.c
 *
 * An escaped identifier is a sequence of one or more characters surrounded by double quotes.
 * These are all valid escaped identifiers:
 *
 *    "Level 1"
 *    "!$$#42everything after$$#"
 *    ".a.b."
 *
 */
aliasPart
    : EscapedIdentifier                 #EscapedAliasPart
    | qualifiedName                     #NormalAliasPart
    ;

/**
 * An escaped identifier is a sequence of one or more characters surrounded by double quotes.
 * These are all valid escaped identifiers:
 *
 *    "Level 1"
 *    "!$$#42everything after$$#"
 *    ".a.b."
 *
 */
EscapedIdentifier
    : '"' ~["]+ '"'
    ;

/**
 * Qualified identifiers are one or more identifiers (an identifier is a name starting with
 * [$_a-zA-Z] and optionally followed by zero or more [$_a-zA-Z0-9]) joined together with
 * periods. For instance, these are valid qualified identifiers:
 *
 *    a
 *    b.x
 *    a.b.c
 *
 */
qualifiedName
    : Identifier ('.' Identifier)*
    ;

/**
 * The `from` clause of a `select` contains a table expression which can be one of these:
 * 1. A single table optionally aliased. If an alias is not provided, a default one with the
 *    the table name (without schema) will be used. I.e. `a.b.X` is equivalent to `X:a.b.X`.
 *
 * 2. An aliased select statement: E.g. `select t.x, t.y from t:(select x, y, z from T)`.
 *
 * 3. A dynamic table expression which creates a named temporary table with rows as part of
 *    the query and allow selection from it. E.g.:
 *          `select a, b from X(a, b):((1, 'One'), (2, 'Two'), ('3, 'Three'))
 *
 * 4. Cartesian product of any two table expressions: E.g.:
 *          `select x.a, x.b, y.c from x:X times y:Y`
 *
 * 5. A join (inner, left, right, full) of any two table expressions:
 *          `select x.a, x.b, y.c, z.d from x:X join y:Y on x.a=y.b left join z:Z on y.c=z.c`
 *
 * Cartesian products and joins can combine any table expression types (including themselves)
 * to form more complex table expressions through composition.
 */
tableExpr
    : (alias ':')? qualifiedName                                 #SingleTableExpr
    | alias ':' '(' select ')'                                   #SelectTableExpr
    | alias metadata? dynamicColumns ':' '(' rows ')'            #DynamicTableExpr
    | left=tableExpr 'times' right=tableExpr                     #CrossProductTableExpr
    | left=tableExpr joinType? 'join' right=tableExpr 'on' expr  #JoinTableExpr
    ;

/**
 * The definition of a dynamic table expression is a set of column names with optional
 * metadata attached to each column.
 */
dynamicColumns
    : '(' nameWithMetadata (',' nameWithMetadata)* ')'
    ;

/**
 * A column in the definition of the type of a dynamic table expression consists of an
 * identifier and followed optionally by metadata for the column.
 */
nameWithMetadata
    : Identifier metadata?
    ;

/**
 * 4 join types are available in ESQL: `left`, `right` and `full` joins are built with these
 * respective keywords before the `join` keyword in a table expression; when none of those is
 * provided, an inner join is assumed.
 */
joinType
    : 'left'
    | 'right'
    | 'full'
    ;

/**
 * The `group by` clause of a select consists of a subset of expressions
 * present in the columns of select. 3 types of groupings are supported:
 * simple, rollup and cube. The latter two are indicated by surrounding
 * the group list with the keword `rollup` and `cube` respectively.
 */
groupByList
    : expressionList                        #SimpleGroup
    | 'rollup' '(' expressionList ')'       #RollupGroup
    | 'cube'   '(' expressionList ')'       #CubeGroup
    ;

/**
 * The `order by` clause of a `select` statement is a comma-separated list
 * or expression by which the select must order its result.
 */
orderByList
    : orderBy (',' orderBy)*
    ;

/**
 * An order expression in the `order by` list of a `select` is an expression
 * which can be followed optionally by a direction: `asc` (default) for ascending
 * or `desc` for descending.
 */
orderBy
    : expr direction?
    ;

/**
 * Directions of order by expressions: `asc` (default) for ascending
 * or `desc` for descending.
 */
direction
    : 'asc'
    | 'desc'
    ;

/**
 * `select`s can be combined with the following set operations:
 *  1. `union`: returns the union of two selects removing all duplicate rows;
 *  2. `union all`: returns the union of two selects without removing any duplicate rows;
 *  3. `intersect`: returns the intersection of two selects;
 *  4. `except`: returns the set difference between two selects (i.e. `A except B` returns all rows in A which are not also in B).
 */
setop
    : 'union'
    | 'union' 'all'
    | 'intersect'
    | 'except'
    ;

/**
 * `with` queries combine `select` and `modify` queries such that the result of one query
 * can be used in subsequent queries in the same `with` query. Not all databases support the
 * use of the result of modification queries in subsequent query in the same `with` query
 * (e.g. Postgresql does, SQL Server does not). Some databases also do not support return values
 * on modification queries. In some cases, ESQL will simulate the feature in translated SQL
 * but, in most cases, the translated SQL will fail when executed on the database; therefore
 * the supported features of the underlying database must be known and ESQL commands limited
 * to the supported subset.
 *
 * `with` queries consist of a list of common-table-expressions (CTE, i.e. a selection or
 * modification query) followed by a final selection/modification query. The first CTE can
 * be recursive and refer to itself (check SQL specification on recursive with queries).
 *
 * Example with query (an example upsert query):
 *
 *      with modify(id) (
 *        update X set a=3, b=4 where c=100 returning id
 *      ),
 *      insert into X(a, b)
 *        select 3, 4
 *          from X left join modify on X.id=modify.id
 *         where modify.id is null
 *
 */
with
    : 'with' recursive='recursive'? cteList queryUpdate
    ;
```


## <a name="expansion_rules">3. Expansion Rules</a>

The following tables are used to illustrate the expansion rules below:
```
S {
    {
        # table metadata (applied to all queries on this table)
        tm1: max(b) from S,
        tm2: a > b
    }
    a int {m1: b > 5, m2: 10, m3: a != 0},
    b int {m1: b < 0},
    c = a + b {m1: a > 5, m2: a + b, m3: b > 5},
    d = b + c {m1: 10},
    e int {m1: c},
    f=(max(a) from S) {m1: min(a) from S},
    g=(distinct c from S where d>5) {m1: min(a) from T},
    h int {m1: 5}
    
    i string {
        label: lv.label from lv:LookupValue 
                        join  l:Lookup on lv.lookup_id=l._id
                                      and  l.name='City'
                        where lv.code=i
    }
}

a.b.T (

)
```

### <a name="expansion_rules">Expansion Rules</a>

#### <a name="metadata_expansion_rules">Columns and tables aliasing</a>

Columns and tables are always aliased, explicitly or implicitly. Implicit aliasing
rules are as follows:

1. Named columns and tables are aliased by their names; columns are qualified with
   the table alias: 
   `select a from S` **=>** `select a:S.a from S:S`
    
2. `select b+1 from A <=> select c1:A.b+1 from A:A`
   column expressions are given arbitrary names which do not clash with existing
   names in the statement.

3. Tables with composite names are aliased with the last part of their names:
   ```
   select b+1 from s.t.A <=> select c1:A.b+1 from A:s.t.A
   select b+1, a+b from s.t.A, s.t.B <=> select c1:B.b+1, c2:A.a+B.b from A:s.t.A, B:s.t.B
   ```

4. Implicit aliasing for columns and tables is always set to their names, even if 
   that alias has already been assigned to another column or table:
   ```
   select a, a:b from s.A           # ERROR as b is aliased to a
   select b:a, a:b from s.A         # This works

   select a from s.t.A, A:s.t.B     # ERROR: table s.t.B aliased to A which is
                                    # the implicit alias of table s.t.A
   select a from B:s.t.A, A:s.t.B   # This works
   ```

5. Renaming a column changes all the generated subscripted names (see section on 
   metadata expansion) to the new name (`b/m1` instead of `a[m1]` when `a` is 
   renamed to `b`, for instance). Columns in computed expressions are not renamed 
   as in the query scope the new names are not valid yet. I.e. a column in the 
   column list of a select statement does not know about the new names, it executes 
   solely in the namespace created by its table list. The new names are part of the
   result of executing the query and is not valid before. Columns in the uncomputed 
   expressions are however renamed to use the new names as those expressions are 
   executed on the result of the query where the new names will be valid. The column
   names in the uncomputed form is still qualified with the table alias that the 
   original column came from even though this produces an expression that is no 
   longer to be executed on the database; however, the additional table alias 
   qualifier acts a placeholder which can easily be replaced by another alias on the 
   client-side where the uncomputed expression is intended to be run.
   ```
   select b:S.a, 
          b/m1:(S.a+1),  b/m1/e:$(S.b+1),     # column renamed in uncomputed form
          b/m2:(10), 
          b/m3:(S.a!=0), b/m3/e:$(S.b!=0)     # column renamed in uncomputed form 
           
          a:S.b,
          a/m0:(S.a+S.b), a/m1/e:$(S.b+S.a),  # column renamed in uncomputed form
          a/m1:(S.b<0),   a/m1/e:$(S.a<0),    # column renamed in uncomputed form
          a/m1:(S.b<0),   a/m1/e:$(S.a<0),    # column renamed in uncomputed form
   
     from S:S
   ```

#### <a name="metadata_expansion_rules">Columns and metadata expansion</a>

1. `select a from S` **=>** 
   ```
   select /tm1:(max:max(S.b) from S:S), /tm1/e:$(max:max(S.b) from S:S),
          /tm2:(S.a>S.b),               /tm2/e:$(S.a>S.b),
   
          a:S.a,
          a/m1:(S.b>5),
          a/m2:(10),
          a/m3:(S.a!=0), a/m3/e:$(S.a!=0)
   from S:S
   ```
   The metadata attributes are expanded into the query and assigned to a subscripted 
   name of the column. Names in the form `c[m]` refers to the metadata attribute m of 
   column c. When a metadata expression refers to a column that is present in the query 
   (such as metadata attribute `m3` of column `a` which refers to `a` in this query) 
   the metadata expression is expanded into a computed form and an uncomputed form 
   (`a/m3:(S.a!=0)` is the computed form and `a/m3/e:$(S.a!=0)` is the uncomputed 
   form in this query). The computed form is evaluated as part of the query and 
   produces a value while the uncomputed form can be translated to a target such as 
   Javascript where it can be used for computing the values of this metadata attribute 
   dynamically (when the value of `a` changes locally while being edited, for instance). 
   The uncomputed form is not included for metadata attributes with literal values (such 
   as for `m2` above) as its value never changes.

2. `select a {m0: b, m1:a+1} from S` **=>** 
   ```
   select a:S.a, 
          a/m0:(S.b), 
          a/m1:(S.a+1),  a/m1/e:$(S.a+1), 
          a/m2:(10), 
          a/m3:(S.a!=0), a/m3/e:$(S.a!=0)
     from S:S
   ```
   metadata can be added to and overridden in the query with `a[m0]` added and `a[m1]` 
   overriding the existing definition.

3. `select c from S` **=>** 
   ```
   select c:(S.a+S.b), 
          c/m1/v:(S.a>5), 
          c/m2/v:(S.a+S.b), 
          c/m3/v:(S.b>5) 
     from S:S
   ```
   derived fields are expanded into their expressions and given the name of the
   derived field, along with their metadata.
    
4. `select a, b, c from S` **=>** 
    ```
    select a:S.a, 
           a/m0/v:(S.b), 
           a/m1/v:(S.a+1),  a/m1/e:$(S.a+1), 
           a/m2/v:(10), 
           a/m3/v:(S.a!=0), a/m3/e:$(S.a!=0) 
           
           b:S.b,
           b/m1/v:(S.b<0),  b/m1/e:$(S.b<0),
   
           c:(S.a+S.b),      c/e:$(S.a+S.b) 
           c/m1/v:(S.a>5),   c/m1/v:$(S.a>5), 
           c/m2/v:(S.a+S.b), c/m2/v:$(S.a+S.b), 
           c/m3/v:(S.b>5),   c/m3/v:$(S.b>5) 
      from S:S
    ```
    if all dependent fields of a derived field are in the columns list, the
    derived expression is included in its uncomputed form.
    
5. `select a {m1:10}, a[m1]:11 from S` **=>** `select a:S.a, a[m1]:(11) from S:S`: 
   the named metadata column takes precedence over both preceding forms (implicit in the table
   definition of the column or explicit in the query).
   
6. `select d from S` **=>** `select d:(S.b+(S.a+S.b)) from S:S`: 
   derived columns are repeatedly expanded until they contain only base columns.

7. `select e from S` **=>** `select e:S.e, e[m1]:(S.a+S.b) from S:S`: 
   derived columns are expanded wherever referenced, including metadata.

8. `select a+b {m1:10} from S` **=>** `select c1:(S.a+S.b), c1[m1]:(10) from S:S` 
   computed columns are assigned a default name which is used to refer to their metadata. 
   Only metadata specified in the query are computed for such columns, metadata from base 
   columns in the expression (`a` and `b`) are not included.
   
9. `select c+d from S` **=>** `select c1:((S.a+S.b)+(S.b+(S.a+S.b))) from S:S`: 
   expansion of derived columns can be arbitrarily long; the expansion is halted with an 
   error as soon as a circular definition is detected.

10. `select f from S` **=>** 
    ```
    select f:    (max:max(S.a) from S:S), f[e]:    $(max:max(S.a) from S:S),
           f[m1]:(min:min(S.a) from S:S), f[m1][e]:$(min:min(S.a) from S:S),
      from S:S
    ```
    expressions of derived columns and metadata can be single result queries (i.e., 
    queries returning a single row and column). The syntax of single result query 
    expressions omits the `select` keyword and allows only one column. Further a 
    limit of 1 row is implicitly added to the query expression if needed to guarantee
    that it will return a single row. 
    
    Query expressions are also expanded into a computed and uncomputed form, with the 
    latter intended to be computed on the client-side.
    
    Query expressions are always expanded to their uncomputed form as they are self-
    contained and do not depend on other columns being loaded in the query.

11. `select g from S` **=>** 
    ```
    select g:       (S.a+S.b from S:S where S.d>5), g[e]:    $(S.a+S.b from S:S where S.d>5), 
           g[m1][v]:(min(S.a) from S:S),            g[m1][e]:$(min(S.a) from S:S),
           g[m2][v]:(S.a+S.b),
           g[m3][v]:(S.b>5)
      from S:S
    ```
    when a single-result query is the expression of a derived column, the metadata, 
    if any, of the column in the single-result query (`g=(c from S where d>5 limit 1)` 
    in this example and the metadata for `c` is `{m1: a > 5, m2: a + b, m3: b > 5}`) 
    cannot be expanded inside the single-result query (as this will violate the rules 
    for such queries); instead the metadata is moved to the immediate outer context 
    (***for metadata attributes of the column in the query referring to other columns
        in the inner query, the attributes are moved to the outer context if those
        same tables also exist in the outer context***), and merged with the metadata 
    of the derived column with the metadata of the latter (`g` in the example) taking 
    precedence. Thus the metadata of column `c` is moved into the outer query and merged 
    with the metadata of column `g` resulting in the above expanded query, with `g[m1]` 
    coming from the definition of `g` and `g[m2]` and `g[m3]` coming from the definition 
    of `c`. The uncomputed forms of `g[m2]` and `g[m3]` are not included as their 
    expressions refer to columns `a` and `b` which are not included in this query. 
    
#### <a name="metadata_expansion_rules">Expansion with groups and aggregates</a>

Column and metadata expansion in queries with grouping must result in the exact same 
number of groups that would have been produced without expansion.

Consider a query `select a, b, count() from X group by a, b`. Metadata attributes for
the columns in the column list of this are included in the expansion of this query only 
in the following cases:
 
1. The metadata expression does not depend on any columns (e.g. is a literal or some
   operation over literals). For instance,
   
   ```
   select a:    X.a, 
          b:    X.b, 
          a[m1]:(10), 
          b[m1]:(5^2+1), 
          count:count()
     from X:X
    group by X.a, X.b, (10), (5^2+1)
   ```
   will produce the same groups as the original query given that the metadata 
   attributes are always fixed.
   
2. The metadata expression depends only on columns being grouped by. For example, 
   
   ```
   select a:S.a, 
          b:S.b, 
          a[m1]:(S.a!=5), 
          b[m1]:(S.b!=5), 
          count:count() 
     from S:S 
    group by S.a, S.b, (S.a!=5), (S.b!=5)
   ```
      
    | a | b | a !=5  | b!=5  |
    |---|---|--------|-------|
    | 1 | 1 | true   | true  |
    | 4 | 5 | true   | false |
    | 5 | 5 | false  | false |
    | 5 | 4 | false  | true  |
    | 5 | 6 | false  | true  |

   ```
   select a:    S.a, 
          b:    S.b, 
          a[m1]:(S.a+S.b),
          count() 
     from S:S
    group by S.a, S.b, (S.a+S.b)
   ```
      
   | a | b | a + b |
   |---|---|-------|
   | 1 | 1 | 2     |
   | 4 | 5 | 9     |
   | 5 | 4 | 9     |

   The following might create additional groups as `c` is not part of the group by list and 
   thus this metadata attribute would not be expanded in this query:
    
   ```
    select a:    S.a, 
           b:    S.b, 
           a[m1]:(S.c),         # Wrong, will not be included
           count:count() 
      from S:S 
     group by S.a, S.b, (S.c)   # Wrong, will not be included
   ```
   
   For instance:  
   
   | a | b | a[m1]:c |
   |---|---|---------|
   | 1 | 1 | 1       |
   | 1 | 1 | 2       |

### <a name="all_columns_expansion_rules">All columns</a>

`*` denotes all columns from the tables or sub-queries being queried

1. `select * from S` **=>**
   ```
   select a:       S.a, 
          a[m1][v]:(S.b>5),  a[m1][e]:$(S.b>5), 
          a[m2][v]:(10),
          a[m3][v]:(S.a!=0), a[m3][e]:$(S.a!=0),
   
          b: S.b,
          b[m1][v]:(S.b<0), b[m1][e]:$(S.b<0),
          
          c:(S.a+S.b),        c[e]:$(S.a+S.b),
          c[m1][v]:(S.a>5),   c[m1][e]:$(S.a>5), 
          c[m2][v]:(S.a+S.b), c[m2][e]:$(S.a+S.b), 
          c[m3][v]:(S.b>5),   c[m3][e]:$(S.b>5),
          
          d:(S.b+(S.a+S.b)), d[e]:$(S.b+(S.a+S.b)), 
          d[m1][v]:(10),
   
          e,
          e[m1][v]:(S.a+S.b), e[m1][e]:$(S.a+S.b),
   
          f:       (max:max(S.a) from S:S), f[e]:    $(max:max(S.a) from S:S),
          f[m1][v]:(max:min(S.a) from S:S), f[m1][e]:$(max:min(S.a) from S:S), 
          
          g:       (distinct c:(S.a+S.b) from S:S where (S.b+(S.a+S.b))>5), 
          g[e]:    $(distinct c:(S.a+S.b) from S:S where (S.b+(S.a+S.b))>5), 
          g[m1][v]:(min:min(T.a) from T:T), g[m1][e]:$(min:min(T.a) from T:T), 
          g[m2][v]:(S.a+S.b), g[m2][e]:$(S.a+S.b),
          g[m3][v]:(S.b>5),   g[m3][e]:$(S.b>5),

          h:       S.h,
          h[m1][v]:5,
         
          i:          S.i,
          i[label][v]:(label:lv.label from lv:LookupValue
                                      join  l:Lookup on lv.lookup_id=l._id
                                                    and  l.name='City'
                                     where lv.code=S.i)
          i[label][e]:$(label:lv.label from lv:LookupValue
                                       join  l:Lookup on lv.lookup_id=l._id
                                                     and  l.name='City'
                                      where lv.code=S.i)
     from S:S
   ```:
   Expansion of column `a` leaves out metadata attribute `m1` as it depends on column
   `b` which is unrelated to `a` and thus might result in more groups than produced by
   the original query. A group `{(1)}` might be broken into two groups 
   `{(1, true), (1, false}` if `m1:b < 5` is included in the expansion.

   
#### <a name="metadata_expansion_rules">Expansion over sub-queries</a>

1. `select a from t:(select a from S)` **=>**
   ```
   select a:t.a,
          a[m1][v]:t.a[m1][v], a[m1][e]:t.a[m1][e],
          a[m2][v]:t.a[m2][v],
          a[m3][v]:t.a[m3][v], a[m3][e]:t.a[m3][e]

     from t:(select a:       S.a,
                    a[m1][v]:(S.b>5),  a[m1][e]:$(S.b>5),
                    a[m2][v]:(10),
                    a[m3][v]:(S.a!=0), a[m3][e]:$(S.a!=0)
               from S:S)
   ```
   
 
#### <a name="joins_expansion_rules">Expansion of joins</a>
1. Joins:
   ```
   select * 
     from a:x.A 
     join x.B on a.b_id=B._id
     left join c:(select d from S)
   ```
   **=>**
   ```
   select a:a.a,
          a[m1][v]:(a.b>5), a[m1][e]:$(a.b>5),
          b:a.b, 
          b[m1][v]:(10),
   
          c:b.c,
          c[m1][v]:(b.c!=0), c[m1][v]:$(b.c!=0),
         
          d:c.d,
          d[m1][v]:c.d[m1][v], d[m1][e]:c.d[m1][e] 

     from a:x.A 
     join x.B on a.b_id=B._id  
     left join c:(select d:S.d,
                         d[m1][v]:(S.d>5), 
                         d[m1][e]:$(S.d>5)
                    from S:S)
   ```

#### <a name="joins_expansion_rules">Expansion of with queries</a>



### <a name='part2.1'>2.1. Modules</a>

Each module is described in a single file with the first line consisting of a
module name followed by zero or more import statements, and then the module
definitions itself which can consist of constants, variables, functions and
class declarations, as such:

    module: module_modifier?
            module_declaration
            (import_statement)*
            (propertyDecl | functionDecl | classDecl)* ;

The optional `module_modifier` can be `client` (default), `service` or
`application` with the following meanings:
1. `client`: This is the default module type with the module being translated
   into Javascript code to be executed inside a web browser. The module can also
   contain the definitiion of server and database functions which will be called
   through automatically-generated remote invocation code;
2. `application`: This is a special module containing information on the whole
   application. There must be a single such module for every application.
3. `service`: The module contains functions which will be invoked through a REST
   protocol. The module is compiled into a Java or Javascript service to be
   deployed on the server.

The module declaration must be the first non-empty and non-comment line in the
form:

    module_declaration: attributes? 'module' module_name
    module_name: identifier

where an identifier consists of a sequence of alphanumeric and underscore
characters but always starting with an alphabetic and underscore character:
    
    identifier: [a-zA-Z_][a-zA-Z0-9_]* ;
    
For instance a valid module definition would be `module x` or
`module climate_model`.

### <a name='part2.2'>2.2. The application module</a>

Every application must have one (and only one) application module. This module
can define several hookup functions which are called at different points in the
lifecycle of the application. The hookup functions are:

1. `install`: called when the application is installed or upgraded to ensure
   that the necessary database and other structures are created/amended as
   required by the current version of the application;
2. `uninstall`: called to clean up when the application is being uninstalled;
3. `start`: called when the application is launched; this function should load
   the last known state of the application (if any) and direct the user to
   either the homepage of the application or the last used page loaed with the
   appropriate state;
4. `stop`: called when the application is being switched off; this is generally
   the case when the user is logging out of or shutting down the system. In this
   function, the current state of the application can be saved for reloading on
   the next launch.

Application modules must be annotated the following attributes:
1. `name`: this is the name of the application in the form of a qualified
   identifier. A qualified identifier consists of one or more identifiers
   separated by the dot character (`.`):

   `qualified_identifier: identifier ('.' identifier)*;`

   `explore` is a special internal module which is imported automatically in all
   modules. It contains the definitions of several structures and functions
   which are assumed to be always present in the execution environment of a Bind
   program.

   To reduce the possibility of name clashes between independently developed
   module in a single Bind  environment, Bind module names must follow the Java
   reverse domain name convention (such `com.google`, `edu.gatech.tsquare`,
   etc.).
2. `version`: the semantic version of the application;
3. `display_name`: a descriptive name for the application shown to users.
4. `icon`: an icon for the application;


### <a name='part2.3'>2.3. Names</a>

All entities in a module (variable, constants, functions and classes) are
identified by a name which must be unique in the module. A name follows the same
pattern as an identifier, consisting of a sequence of alphanumeric and
underscore characters but always starting with an alphabetic or underscore
character.

Names starting with an underscore (`_`) represent entities which are private to
the module and not available for import from other modules.


### <a name='part2.4'>2.4. Attributes</a>

Attributes are an optional set of names or name-value pairs with the following
syntactic form:

    attributes: attribute+ 
    attribute: '[' nameValuePair (',' nameValuePair)* ']'
    nameValuePair: Identifier (':' expression)?
    
They can be set on a module and on its internal definitions (constants,
variables, functions and classes) and are made accessible as part of the runtime
information for that particular entity through a set of `Descriptor` objects
(more details in relevant sections later). For instance, the following is an
example of a module declaration with associated attributes:

    [description: 'Various mathematical functions']
    [author: 'Snoopy']
    [version: '1.2.3']
    [sequential_version: 1053]
    [internal_module]
    module math
    
Any valid qualified identifier can be used as an attribute name and any valid
Bind expression, including function definitions and function calls, can be used
as the value.

Function calls defined on attributes are called at the time of loading of the
entity on which the attribute is set.


### 1.3. Comments and whitespace

Comments in Bind are any text following the hash symbol (#) up to the end of the
line. Whitespace is defined as ` [ \t\r\n]+`. Comments and whitespace are
ignored in Bind and silently skipped.


### 1.4. Statements separator

Bind does not require any separator between statements and instead rely on their
syntactic structures to separate them automatically. For readability, the
semicolon character (`;`) can be used for separating statements, especially when
several are placed on the same line.


### 1.5. Imports

A module can import other modules using an `import` statement. It can also
import existing Javascript code and supports a certain level of interoperability
since Bind code is translated to Javascript on the client-side. **On the
server-side Bind imports can be used to rename server-side existing definitions
such as tables and functions.**

The first form of the `import` statement is:

    import: 'import' module_name ('as' identifier)

where `module_name` is the name of an existing module. The alias is an
identifier through which the definitions in the module can be accessed. If not
specified, the last part of the module name will become the alias. For instance:

    import a.b.c                        # import 1
    import x.y.z as d                   # import 2
    import util.collections as col      # import 3
    
Imports start with the `import` reserved word followed the module name and
optionally an alias. When an alias is specified as `d` and `col` in imports 2
and 3 above, the definitions in the corresponding import can be accessed through
that alias. I.e. `d.ref` is used to access the object named `ref` in module
`x.y.z`. Similarly, `col.abc.dy` is used to refer to the member `dy` of object
`abc` in module `util.collections`. When an alias is not specified, as in first
import above, the last part of the imported module name (`c` in that case) is
used as the alias. Therefore object `x` in module `a.b.c` is accessed as `c.x`.

A second form of import allows objects in modules to be imported without
aliasing. It has the following syntax:

    import_from: 'import' alias (',' alias)* 'from' fullyQualifiedIdentifier
    alias: identifier ('as' identifier)?
    
For instance:
    
    import x, y as a, z as d from a.b.c
    
In this import `x` refers directly to `a.b.c.x`, `a` refers to `a.b.c.y` and `d`
to `a.b.c.z`.

A third import allows for some Javascript interoperability by allowing existing
Javascript code to be imported into the Bind execution environment and one or
more definitions in the Javascript to be made available through an optionally
aliased name:
  
    js_interop: 'from' path 'as' identifier 'import' alias (',' alias)* ;
    path: string_literal ;
    
In this form, the Javascript code to import is specified as a path to a .js file
containing the Javascript code. The path to the Javascript file is any path that
is allowed by the browser to be loaded from the current page (which is the
current Bind execution environment). This includes URL from other domains if
this is allowed by the current security policy. For instance:

    from '/lib/js/test.js' as test import a, b, trans as xy, pi, log as log10;
    
This will load the code in the file `/lib/js/test.js` and wrap the code in an
IIFE (Immediately-Invoked-Function-Expression) returning a Javascript object
with references to the specified imported list. E.g.

    test = (function() {
        // CONTENTS OF /lib/js/test.js
        
        return {
            a: a,
            b: b,
            xy: trans,
            pi: pi,
            log10: log
        }
    })();
    
As such the properties of the `test` object will refer to the definitions in the
original file with the names changed if so specified in the alias list. This
technique should provide some interoperability with existing Javascript code but
it requires that all the definitions to be imported be explicitly specified.

Finally, if a javascript module is already in a form that it can be imported
without need for embedding into an IIFE, the following special form will include
it directly into the Bind runtime envrinment:

    import_js: 'import' StringLiteral 'as' Identifier

This form is used internally to import the base Javascript definitions and
functions used by all Bind programs. E.g.:

    import 'a/b/c.js' as x

will be translated into:

    x = load_module('a/b/c.js')

This will make the entities in `a/b/c.js` accessible through the variable `x`.

All Bind programs have the following import implicitly added:

    import 'core/lang/bind.js' as lang


### 1.6. Properties

Modules and classes (explained later) can contain properties which are values
associated to the module which can be read and, optionally, written into. The
read/write logic can be specialised to suit different needs. Properties are
created using the following syntax:

    property: attributes? 'property' Identifier '=' expression ('get' blockWithReturn)? ('set' block)?

For each property, a hidden variable with the same name as the property name
prefixed with an 'underscore' is created in the object corresponding to the
module (or class instance if this property is part of a class as described
later). This hidden variable is initialised with the expression provided in the
property definition as part of the module's initialisation.

If neither a getter nor a setter is specified for the property default ones are
generated as such:
    
    property x = y 
             get { return this._x  }
             set { this._x = value }

In the above, `x` is the property name, `y` is its initial value, `this`
corresponds to the module object, `_x` corresponds to the hidden variable for
the property and `value` is a reserved word corresponding to the value passed to
the setter.

Read-only properties have getters only.

Attributes can be attached to properties. Following is an example of a simple
property with attributes:
    
    [min:0, max:150, required]
    property age = 0
             get { return this._age }
             set { this._age = value }


### 1.7. Module function declarations

Functions can be defined at the module level using the `fn` keyword using the
following syntax:

    function_decl: attributes? modifier? 'fn' function_name '(' parameter_list? ')' ':' return_type statement
    modifier:      ('server'|'database'|'client')
    function_name: identifier

The optional `server`, `database` or `client` (default) modifier, if specified,
controls what the function will be translated into: a Java method to be executed
on the web server, a pl/sql function in the database, or a Javascript function
for execution on the client-side only, respectively.

These function types can be arranged into the following levels, with functions
at certain level limited to invoking other functions at the same level or in
the levels below:

        +---------------------+
        |   client functions  |
        +---------+-----------+
                  | can call
        +---------v-----------+
        |   server functions  |
        +---------+-----------+
                  | can call
        +---------v-----------+
        | database functions  |
        +---------------------+

Inter-level calls are compiled into special remote invocation calls of the
proper types.

The parameter list consists of zero or more parameter declaration, each with a
unique name in the list.

    parameter_list:  parameter_decl (',' parameter_decl)*
    parameter_decl: ('...')? Identifier ':' parameter_type) ('=' expression)?

Parameter can have default values specified as an expression. Presently, only
literals and simple operations on literals are valid as default values. A
parameter with a default value can only be followed by parameters with default
values.

If the last parameter name is prefixed with 3 dots `...`, the function can take
a variable number of parameters with all parameters at the position of the
variadic parameter and beyond combined into an array of the proper type and
supplied as the value of the variadic parameter.

Types can be divided into base types, array types and composite types with the
following table showing which types they are compiled to on the database, 
server and the client:

| Bind type   | Database type (pl/sql) | Server type (Java) | Client type (Javascript) |
|-------------|------------------------|--------------------|--------------------------|
| byte        | smallint               | byte               | number                   |
| short       | smallint               | short              | number                   |
| int         | int                    | int                | number                   |
| long        | bigint                 | long               | number                   |
| verylong    | numeric(1000)          | BigInteger         | number                   |
| float       | real                   | float              | number                   |
| double      | double precision       | double             | number                   |
| real        | numeric                | BigDecimal         | number                   |
| bool        | boolean                | boolean            | boolean                  |
| text        | text                   | string             | string                   |
| bytes       | bytea                  | byte[]             | ByteBuffer               |
| date        | date                   | Date               | Date                     |
| time        | time                   | Date               | Date                     |
| datetime    | timestamp              | Date               | Date                     |
| uuid        | uuid                   | Uuid               | string                   |
| json        | jsonb                  | Json               | object                   |
| array types | array                  | array              | array                    |
| object      | jsonb                  | Object             | object                   |

Some translations:

    fn x(): int

could be translated to:
    
    CREATE OR REPLACE FUNCTION x()

The parameter and return types are necessary for server functions only as the
function declaration on the database are typed. If not specified, the
polymorphic type `anyelement` is used on the server-side; on the client-side
types are ignored. Those types are thus modeled in relation to what is supported
by the database:

    parameterType
         : BaseType
         | arrayType
         | RecordType
         | compositeType
         | PolymorphicType
         ;
         
A base type is one of several supported primitive types on the database (the
commented text is the name of the corresponding database type):
    
    BaseType
        : 'string'          // text
        | 'short'           // smallint
        | 'int'             // integer
        | 'long'            // bigint
        | 'float'           // real
        | 'double'          // double precision
        | 'numeric'         // numeric
        | 'boolean'         // boolean
        | 'uuid'            // UUID
        | 'hstore'          // hstore
        | 'json'            // jsonb
        | 'bytes'           // bytea
        ;
        
A composite type, on the other hand, is a user-defined type constructed by combining or 
restricting base types. Such a composite type will be named arbitrarily during definition 
and referred to by a qualifier identifier:

    compositeType: qualifiedIdentifier ;
    
A parameter can also be of type `record` meaning that its value can be an arbitrary row:
    
    RecordType: 'record' ;
    
Array-types can then be formed from either base, record or composite types by suffixing 
with one or more `[]`, one for each dimension of the array type. Only regular arrays 
(non-jagged) are supported by Postgresql:

    arrayType: (BaseType | RecordType | compositeType) ('[' ']')+ ;
    
Finally, a parameter can have a polymorphic type whereupon its acceptable value is defined
by one of the following constraints:
    
    PolymorphicType
        : 'anyelement'
        | 'anyarray'
        | 'anynonarray'
        ;

A parameter with polymorphic type `anyelement` will accept a value of any type. `anyarray`
and `anynonarray` will only accept array values or non-arrays, respectively. PostgreSql has
one important restriction when a polymorphic type is used in a function declaration: at
function call time all similar polymorphic types must be passed the same concrete type. 
For instance, `fn test(a: any, b:any, c: anynonarray): anynonarray` can only accept the
same type for the parameters `a` and `b` while the type of the parameter `c` must be the
same as the return type of the function. The exact types can vary from call to call but 
those constraints must be respected. 

In addition to the parameter types, a return type can be:

1. a set of values of other types, usually as the result of query:
```
    setofType: 'setof' '[' (BaseType | RecordType | compositeType) ']' ;
```        

2. The `void` type when the function does not return any value.
```
    VoidType: 'void' ;
```

The syntactic form of the return type is thus:

    returnType
         : BaseType
         | arrayType
         | setofType
         | RecordType
         | VoidType
         | compositeType
         | PolymorphicType
         ;
         
         
### 1.8. Classes

Classes can be defined inside modules with the following syntax:
 
    classDecl: attributes? 'class' class_name ('extends' qualifiedIdentifier) classBody ;

The class name, as for all module entities, must be unique within the module and can start
with an underscore (`_`), in which case the class is private to the module. A class can
extends a single other class specified after the `extends` keyword. The class being extended
is referred to as the superclass of the defined class. If no superclass is specified, the
class implicitly extends the `lang.Any` class (described in the [Execution environment](#part2) 
section).

The class to be extended may be described in another module and has to be imported. If so, 
the superclass may need to be referred to through a qualified identifier (e.g. `util.List`).

Attributes can be attached to a class and will be available through the class descriptor
at runtime.

The body of a class may contain one or more variable, constants, constructors and methods
declarations surrounded by parenthesis as follows:

    classBody: '{' (constructor | property | method)* '}' ;
    method: functionDecl ;
    
Constructor are special functions which are invoked to construct an instance of the class. 
Methods are functions defined inside classes and follow the same form as module-level functions.
Properties are instance variables which can be read-only or read-write.

Inside the constructors and methods of the class the `super` keyword refers to the superclass
while the `this` keyword refers to the current instance of the class.

#### 1.8.1. Class constructors

A class may specify one or more constructors with the following syntactic form:
 
    constructor: 'constructor' '(' parameterList ')' statement ;

The keyword `constructor` starts the definition of a constructor and is followed by
a list of zero or more parameters surrounded by brackets. The parameter list follows
the same rules as those for normal functions allowing for default values as well as
variadic parameters. The constructor declaration is completed with a statement (or 
block of statements) for its operation.

A constructor should always invoke a super constructor and it is an error if such a 
call is not made in the constructor.

#### 1.8.2. Properties

Class properties are similar to module properties in both syntax and semantics with
property state stored in the class instance instead of the module object.

#### 1.8.3. Methods
 
Methods are simply functions defined inside a class and in which statements have access 
to the current instance through the `this` reference and to the superclass through the
`super` reference.


### 1.9. Expressions

An expression is a syntactic unit (such as a literal or identifier) which produces a value
or any valid combinations of smaller expressions producing a value. Expressions have the following
syntactic form:

    expression
        : '(' expression ')'                                    #ExpressionGrouping
        | 'this'                                                #ThisReference
        | 'super'                                               #SuperReference
        | 'value'                                               #ValueReference                     // The value assigned to a setter
        | 'error'                                               #ErrorReference                     // The error caught in a catch block
        | literal                                               #LiteralExpression
        | Identifier                                            #VariableReference
    
        | expression '.' Identifier                             #MemberReference
    
        | expression '(' expressionList? ')'                    #FunctionInvocation
        | 'new' qualifiedIdentifier '(' expressionList? ')'     #ConstructorInvocation
        | '[' expressionList? ']'                               #ArrayConstruction
    
        | lambda                                                #LambdaExpression
    
        | expression '[' expression ']'                         #ArrayIndexing
        
        | expression 'instanceof' qualifiedIdentifier           #TypeCheck
    
        // arithmetic in order of precedence
        | ('+' | '-' | '~'| '!') expression                     #Negation
        | <assoc=right> expression '**' expression              #Exponentiation
        | expression ('*' | '/' | '%') expression               #Multiplication
        | expression ('+' | '-') expression                     #Addition
        | expression ('<<' | '>>')                              #Shift
        | expression ('<' | '>' | '<=' | '>=') expression       #Relational
        | expression ('==' | '!=') expression                   #Equality
        | expression '&' expression                             #BitwiseAnd
        | expression '^' expression                             #BitwiseXor
        | expression '|' expression                             #BitwiseOr
        | expression '&&' expression                            #LogicalAnd
        | expression '||' expression                            #LogicalOr
        | expression 'if' expression 'else' expression          #Conditional
        ;


#### 1.9.1. Base expression units

The base expression units are expressions which are not constructed themselves from other 
sub-expressions. As such they are units which can be composed together to form larger expressions.
The base expression units consists only of literals and references to variables, constants, 
properties and a few contextual references.

##### 1.9.1.1. Literals

A literal can be a number (integer or floating-point), a string, a boolean value, an array of
values or the special null value literal:

    literal
        : IntegerLiteral
        | FloatingPointLiteral
        | StringLiteral
        | BooleanLiteral
        | NullLiteral
        ;

An **integer** can be specified in decimal as a sequence of digits. It can also be specified in
hexadecimal format starting with `0x` and followed by a sequence of hexadecimal digits (`[0-9a-fA-F]`),
in octal format starting with `0o` and followed by a sequence of octal digits (`[0-7]`) or
in binary starting with `0b` and followed by a sequence of binary digits (`[01]`).

A **floating-point number** may contain a fractional portion specified after the decimal point (`.`)
and an exponent part specified after the letter `e`.

A **string** literal is specified as a sequence of characters between single-quotes. String characters
may include escape sequences which is a sequence of characters starting with a backslash and
denoting a special (usually non-printable) character. The following escape character sequences 
are supported:

| Escape sequence      | Insert character                |
|----------------------|---------------------------------|
| \b                   | Backspace character             |
| \t                   | Tab character                   |
| \r                   | Carriage return character       |
| \n                   | Newline character               |
| \f                   | Form feed character             |
| \'                   | ma.vi.tuple.Single-quote character          |
| \\                   | Backslash character             |
| \uHHHH               | Inserts the unicode character with the specified unicode hexadecimal codepoint (4 digits) |

A **boolean** literal can be only one of these two values: `true`, `false`.

The **null** literal is a special value which is part of the set of possible values of any types.
It can thus take the place of any parameter value being passed to a function or assigned to any
variable. The `null` value typically means that the actual value is unknown.

##### 1.9.1.2. Variables, constants and properties references

Variables and constants can be referenced by their names in an expression and produces their
current value. Similarly properties from other modules which have been aliased in an import
to a name can be referenced by that name.

##### 1.9.1.3. Contextual references

There are four contextual references which are valid in certain context only and whose values
depend on that context. The first one is the **`this`** contextual reference which refers to
the current instance of a class when used inside a getter/setter/method/constructor of a class. 
When used outside of class, it refers to the module object and can be used to access module-level
properties, functions and classes.

The second contextual reference is **`super`** which refers to the superclass of the current class
when used inside a class. When used outside a class (in the module), it refers to the `lang.Module`
superclass of the current module. `lang.Module` is the implicit superclass of all modules.

**`value`** is only valid when used in a property setter where it refers to the value being assigned
to the property.
 
**`error`** is valid only inside a `catch` block and it refers to the caught error.


### 1.9.2. Algebraic operators

Various arithmetic, boolean and bitwise operators can be used to construct operations. Most of these
operators are binary and their syntactic form, in order of precedence is as follows:

    algebraicExpression
        : ('-' | '~'| '!') expression                
        | <assoc=right> expression '**' expression         
        | expression ('*' | '/' | '%') expression          
        | expression ('+' | '-') expression                
        | expression ('<<' | '>>')                         
        | expression ('<' | '>' | '<=' | '>=') expression  
        | expression ('==' | '!=') expression              
        | expression '&' expression                        
        | expression '^' expression                        
        | expression '|' expression                        
        | expression '&&' expression                       
        | expression '||' expression                       
        | expression 'if' expression 'else' expression     
        ;
        
#### 1.9.2.1. Unary algebraic operators

There are three unary algebraic operators:

1. **`-`**: When applied to a numeric expression, this negates its value.
2. **`~`**: This performs a bitwise negation of the numeric expression it is applied to. For instance
   `~0b10010 == 0b01101`.
3. **`!`**: This performs a boolean negation of the boolean expression it is applied to: `~true == false` 
   and `~false == true`.
   
#### 1.9.2.2. Exponentiation

**`**`** is used as the exponentiation operator. It is the only right-associative operator and has
the highest precedence after the unary operators.

#### 1.9.2.3. Multiplicative

**`*`**, **`/`** and **`%`** are the multiplication, division and modulus (remainder) operators, 
respectively. They have the next highest precedence after exponentiation.

#### 1.9.2.4. Additive

**`+`** and **`-`** are the addition and subtraction operators, respectively. They have the next 
highest precedence after the multiplicative operators.

#### 1.9.2.5. Shift

**`<<`** and **`>>`** are the bitwise left and right shift operators, respectively. They have the next 
highest precedence after the additive operators.

#### 1.9.2.6. Relational

**`<`**, **`>`**, **`<=`**, **`>=`** are the less than, greater than, less than or equal to and greater
than or equal to operators, respectively. They are used to compare numeric expressions and return a boolean
value as their result; they have the next highest precedence after the shift operators.

#### 1.9.2.7. Equality

**`==`** and **`!=`** are the equal and not equal operators, respectively. They are used to compare 
expressions for equality and return a boolean value as their result; they have the next highest
precedence after the relational operators.

#### 1.9.2.8. Bitwise

**`&`**, **`^`** and **`|`**  are the bitwise and, xor and or operators, respectively. They have the
next highest precedence after the equality operators with bitwise and (`&`) having the highest precedence
and bitwise or (`|`) having the lowest precedence among the three.

#### 1.9.2.9. Boolean comparison

**`&&`** and **`||`** are the boolean AND and OR operators, respectively. They have the
next highest precedence after the bitwise operators with boolean AND (`&&`) having higher precedence
than boolean OR (`||`). Both of these operators take boolean operands and returns an boolean
value representing the AND or OR combination of the operands; they are useful for decision-making
in conditional (`if`) and looping (`while`) statements.

#### 1.9.2.10. Conditional

The value of the conditional expression depends on the value of a test expression. It has the
following form:
 
    conditional: expression-when-true 'if' test-expression 'else' expression-when-false ;     

The value of the whole conditional expression is equal to the `expression-when-true` when the
`test-expression` is true; otherwise it has the value of `expression-when-false`. Both of these
value expressions must be of the same type.

This operator has the lowest precedence.


### 1.9.3. Expression grouping

Expressions can be grouped by surrounding them with brackets as follows:
   
   expressionGroup: '(' expression ')'

When grouped, the expression inside the brackets are evaluated before expressions outside it
thereby allowing for default evaluation precedence to be overridden.


### 1.9.4. Member referencing

Given a value from a complex type (i.e. one which has underlying structure), its members,
including tis methods and properties, can be referenced using the dot (`.`) operator using 
the following syntactic form:

    member_reference: expression '.' Identifier
    
where `Identifier` is the name of the member being referred.

### 1.9.5. Function call

A function can be invoked by following the function expression with a list of zero or
parameter values enclosed in brackets as follows:

        function_call: expression '(' expressionList? ')'                    

The value of this expression is the returned value of the function.

### 1.9.6. Object creation

An object is created by invoking a constructor on a class. This is done by applying 
the `new` operator to a constructor of the class to instantiate using this form: 

    constructor_call: 'new' qualifiedIdentifier '(' expressionList? ')' 
    
The qualified identifier in this case refers to the class name and the expression 
list to the parameters to pass to the constructor. The result of such a call is a
new instance of the class.

### 1.9.7. Array construction

Arrays can be constructed by surrounding zero or more expressions by square brackets
(`[]`). An empty array is created when if no expressions are given within the brackets.
Multi-dimensional arrays can be constructed by nesting array constructors, such as for
instance: `[[1, 2, 3], [4, 5, 6]]`.

### 1.9.8. Array access

Arrays can be accessed with the following form:
    
    array_access: array_expression '[' index_expression ']' 

where `array_expression` must evaluate to an array and `index_expression` must evaluate
to a positive integer. This expression may be repeated for multi-dimensional array access.
For instance: `a[2][3]`. Array indices start at 0 in Bind.

### 1.9.9. Type check



expression 'instanceof' qualifiedIdentifier

### 1.9.10. Lambda expression

        | 
    
        | lambda                                                #LambdaExpression
    
        
        |            #TypeCheck



### 1.10. Statements

The body of a function, method and property getter/setter consists of one or more statements. 
If there are more than one statements, they must be enclosed between parenthesis (a block).

Statements can be one of the following:

    statement
        : variableDecl Sep?
        | constantDecl Sep?
        | assignment Sep?
        | functionCall Sep?
        | ret Sep?
        | 'for' Identifier 'in' expression statement
        | 'while' expression statement
        | 'do' statement 'while' expression
        | 'if' expression 'then' statement ('else' statement)
        | 'break' Sep?
        | 'continue' Sep?
        | 'throw' expression Sep?
        | 'try' block ('catch' block)? ('finally' block)?
        | block
        ;

#### 1.10.1. Variables, constants, blocks and scoping

Variables and constants can be declared using the following syntax:
    
    'var' Identifier = expression
    'const' Identifier = expression
    
Variables can be re-assigned to after declaration while constants can't.
 
A block is a group of statements surrounded by parenthesis (`{}`) defined as follows:
 
    block: '{' statement* '}'
 
All variables and constants are scoped to the block within which they are 
defined and can only be referenced after declaration. 

## <a name='part2'>Part II - Execution environment</a> 

### 2.1. Prerequisites

Both on the client and server sides, system assumes that a certain set of structures and 
functions to be already present. These prerequisites are satisfied in a system-dependent
manner, currently as follows:

### 2.2. __BIND__ namespace

This is a special namespace inside which all modules, classes

### 2.3. Base class hierarchy

### 2.4. Libraries



## <a name='part3'>Part III - Translations</a>

### 3.1. Modules
 
On the client-side, the whole module is translated into a Javascript object extending the 
`Module` class (more information about this class and the Bind execution environment is found 
in the relevant sections below). This class has certain default constants, variables, functions 
and classes which can be overridden as required. 

For instance the following trivial module:

    module a.b.c
    
will be translated to:
 
    __BIND__.a = 
    {
        b: 
        {
            c: new class extends Module 
            {
                // translated module definitions go here
            }
        }
    }

All module definitions will then be translated and inserted in the `c` class. These module
definitions and translations are defined in the following sections.

On the server-side, to each module corresponds a schema which is created if it does not 
already exists:

    CREATE SCHEMA IF NOT EXISTS "__BIND__.a.b.c";
    
The contents of the module is stored in the `lang.module` table which has the 
following structure(and some samples):

| id | module_name  | source_code          |  client_side_compiled   |  server_side_compiled  |
|----|--------------|----------------------|-------------------------|------------------------|
| 1  | x.y.z        | module x.y.z ...     | ...                     | ...                    |
| 2  | a.b.c        | module a.b.c ...     | ...                     | ...                    |


### 1.3. Module definitions - constants and variables

Module can contains constants declarations in the following syntax:

    constant_declaration ::= attributes? 'const' identifier '=' expression
    
Variable declarations have similar syntax with the `const` keyword replaced by `var`.

This constant declaration becomes a read-only property inside the module class while 
a variable becomes a read-write property, with their initial value set in the constructor
of the module. For instance

    module a.b.c
    
    const x = 10
    const y = 50.67
    var jail = 'test'
    var m = 1000
    
will be translated to:

    __BIND__.a = 
    {
        b: 
        {
            c: new class extends Module 
            {
                constructor() {
                    this.__x__ = 10
                    this.__y__ = 50.67
                    this.__jail__ = 'test'
                    this.__m__ = 1000
                }
                
                get x() { return this.__x__ }
                
                get y() { return this.__y__ }
                
                get jail() { return this.__jail__ }
                set jail(__jail__) { this.__jail__ = __jail__ }
                
                get m() { return this.__m__ }
                set m(__m__) { this.__m__ = __m__ }
            }
        }
    }
    
### 1.3. Module definitions - functions

Module can contains constants declarations in the following syntax:

    constant_declaration ::= 'const' identifier '=' expression
    
Variable declarations have similar syntax with the `const` keyword replaced by `var`.

This constant declaration becomes a read-only property inside the module class while 
a variable becomes a read-write property, with their initial b set in the constructor
of the module. For instance

    module a.b.c
    
    const x = 10
    const y = 50.67
    var jail = 'test'
    var m = 1000
    
will be translated to:

    __BIND__.a = 
    {
        b: 
        {
            c: new class extends Module 
            {
                constructor() {
                    this.$x = 10
                    this.$y = 50.67
                    this.$jail = 'test'
                    this.$m = 1000
                }
                
                get x() { return this.$x }
                
                get y() { return this.$y }
                
                get jail() { return this.$jail }
                set jail($jail) { this.$jail = $jail }
                
                get m() { return this.$m }
                set m($m) { this.$m = $m }
            }
        }
    }


1. Module definition
### 1.1. `module a.b.c`

#### On client-side (JS)

In the file containing the definition of the module
~~~    
__BIND__ = typeof(__BIND__) === "undefined" ? {} : __BIND__
__BIND__.a = typeof(__BIND__.a) === "undefined" ? {} : __BIND__.a
__BIND__.a.b = typeof(__BIND__.a.b) === "undefined" ? {} : __BIND__.a.b
__BIND__.a.b.c = typeof(__BIND__.a.b.c) === "undefined" ? {} : __BIND__.a.b.c

// module definitions consisting of __BIND__.a.b.c.<x> = <translated definition for x>
~~~

#### On server-side (plpython3u)

To each module corresponds a schema which is created if it does not already exists:

    CREATE SCHEMA IF NOT EXISTS "__BIND__.a.b.c";
    
The contents of the module is stored in the `lang.module` table which has the 
following structure(and some samples):

| id | module_name  | source_code          |  client_side_compiled   |  server_side_compiled  |
|----|--------------|----------------------|-------------------------|------------------------|
| 1  | x.y.z        | module x.y.z ...     | ...                     | ...                    |
| 2  | a.b.c        | module a.b.c ...     | ...                     | ...                    |


## 2. Imports
### 2.1. `import a.b.c`
#### Client-side

    // link to code corresponding to module a.b.c 
    c = __BIND__.a.b.c
    
#### Server-side

All constant members and functions referenced through `c` is rewritten to reference the object.
Constant members are realised as functions on the server-side. For example:

~~~
server fn sqrt_plus_pi(a: int): int {
  return c.sqrt(a) + c.PI
}
~~~    

will be translated to:
~~~   
create or replace function "__BIND__.current_module_name".sqrt_plus_pi(a int) returns int as $$
  return "__BIND__.a.b.c".sqrt(a) + "__BIND__.a.b.c".PI() 
$$ language plpython3u
~~~
    
### 2.2. `import a.b.c as d`
#### Client-side

    // link to code corresponding to module a.b.c 
    d = __BIND__.a.b.c
    
#### Server-side
Similar to the previous case (2.) except that members and functions referenced through `d` are the ones
which are rewritten.
    
    
### 2.3. `from a.b.c import a, b as c, c as b`
#### Client-side

    // link to code corresponding to module a.b.c 
    a = __BIND__.a.b.c.a
    c = __BIND__.a.b.c.b
    b = __BIND__.a.b.c.c

#### Server-side

All reference to the imported names (a, b and c) are bound to the specific functions:
~~~
a -> "__BIND__.a.b.c".a()
c -> "__BIND__.a.b.c".b()
b -> "__BIND__.a.b.c".c()
~~~
    

### 2.4. JS interop: `from 'lib/test.js' as test import a, b as c, d as e`

This form allows for some interoperability with existing Javascript code.
First it re-packages the code as follows:
```
test = (function() {
// existing library code goes here

// exports
return {
   a: a,
   c: b,
   e: d
}
})()
```
The repackaged code is then dynamically loaded by appending a script element 
with the code to the document body.

This is only applicable to the client-side.


## 3. Module definitions
 
### 3.1. Constants: `const a = 5`
#### Client-side

    // Inside the module object the constant becomes a read-only property
    get a() { return 5 }
    
#### Server-side

    // constant members are realised as functions on the server-side.
    create or replace function "__BIND__.current_module_name".a() returns int as $$
        return 5;
    $$ language sql immutable parallel safe;

### 3.2. Functions

##### Client-side translation
`fn test() <statement>` is translated to:
```javascript
test: function() {
    // <translated statement>
}
```

`fn test(a, b) <statement>` is translated to:
```javascript
test: function(a, b) {
    // <translated statement>
}
```

`fn test(a, b=10, c='test') <statement>` is translated to:
```javascript
test: function(a, b=10, c='test') {
    // <translated statement>
}
```

`fn test(a, b=10, c='test', ...p) <statement>` is translated to:
```javascript
test: function(a, b=10, c='test', ...p) {
    // <translated statement>
}
```

##### Server-side translation
`server fn test() <statement>` is translated to:
```python
create or replace function "<module name>"."test"() return anyelement as $$
    // <python translated statement>
$$ language plpython3u;
```

`server fn test(a, b) <statement>` is translated to:
```python
create or replace function "<module name>"."test"(a anyelement, b anyelement) return anyelement as $$
    // <python translated statement>
$$ language plpython3u;
```

`server fn test(a, b=10, c='test') <statement>` is translated to:
```python
create or replace function "<module name>"."test"(a anyelement, b int = 10, c text = 'test') return anyelement as $$
    // <python translated statement>
$$ language plpython3u;
```


`server fn test(a, b=10, c='test', ...p) <statement>` is translated to:
```python
create or replace function "<module name>"."test"(a anyelement, b int = 10, c text = 'test', variadic p) return anyelement as $$
    // <python translated statement>
$$ language plpython3u;
```

The server-side code will use anyelement as the parameter and return 
type if a more specific type (based on the default parameter values) 
cannot be determined. For more control explicit type information could 
be provided (and this is always recommended). The explicit types are only 
used for the definition of the server function and can be any postgresql 
types which can appear in parameter list definitions, including pseudo-type 
such as anyelement and anyarray. 

It should be noted that there are specific rules when using pseudo-types (e.g. 
all similar pseudo-type in a function are realised as the same concrete 
type at call-time) and this must be kept in mind when defining such functions. 
Bind is not always able to enforce that such constraints are respected in all 
function calls.

The optional `server` keyword means that the function is realised purely 
as a database function (plpython3u). Such a function can be called by 
client-side functions as well as other server-side ones but the call 
semantics is different: for client-side the call to a server function gets 
wrapped into a database call while, from another server function, it's a 
direct database call. *A server function cannot call a client-side function.* 

---
A trigger function produces a server side function returning a trigger; such a 
function can be used as a trigger on a database table. Trigger functions have 
access to some special variables (old record, new record, trigger action, etc.)
which can be acessed through specially name variable (`_trigger_old`, 
`_trigger_new`, etc.).

To set a trigger on a database table, the `create or update trigger` (described 
later in the sections on embedding SQL commands) should be used.


### 3.3. Function statements

The body of a function consists of one or more statements. If there are more than
one statements, they must be enclosed between parenthesis (in a block of statement):


## Part III - Server-side translations

Considering application x.y:

    | Bind                                 | Client                                     | Server                                            | Database                                         |
    |--------------------------------------|--------------------------------------------|---------------------------------------------------|--------------------------------------------------|
    | module a { // }                      | __BIND__.x.y.a = new class { // }          |                                                   |                                                  |
    |                                      |                                            |                                                   |                                                  |
    |                                      |                                            |                                                   |                                                  |
    |                                      |                                            |                                                   |                                                  |
    |                                      |                                            |                                                   |                                                  |
    |                                      |                                            |                                                   |                                                  |
    |                                      |                                            |                                                   |                                                  |
    |                                      |                                            |                                                   |                                                  |
    |                                      |                                            |                                                   |                                                  |
    |                                      |                                            |                                                   |                                                  |
    |                                      |                                            |                                                   |                                                  |
    |                                      |                                            |                                                   |                                                  |
    |                                      |                                            |                                                   |                                                  |
    |                                      |                                            |                                                   |                                                  |
    |                                      |                                            |                                                   |                                                  |
    |                                      |                                            |                                                   |                                                  |
    |                                      |                                            |                                                   |                                                  |
    |                                      |                                            |                                                   |                                                  |
    |                                      |                                            |                                                   |                                                  |
