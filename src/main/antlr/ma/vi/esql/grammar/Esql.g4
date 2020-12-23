/*
 * Copyright (c) 2018-2020 Vikash Madhow
 */

/**
 * ANTLR4 grammar for a SQL-like language which can be translated to
 * SQL commands specific to a physical database (PostgreSql, SQL Server,
 * Oracle, MySQL, etc.)
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
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

/**
 * A `with` query contains comma-separated list of common-table-expression (CTE)
 * followed by select/modify query.
 */
cteList
    : cte (',' cte)*
    ;

/**
 * A common-table-expression (CTE) consists of an identifier which names the CTE
 * followed by an optional list of column names that will be returned by the CTE
 * and the select/modify query that will be executed to produce the result of the
 * CTE.
 */
cte
    : Identifier names? '(' queryUpdate ')'
    ;

/**
 * The column names in a CTE is a comma-separated list of identifiers between
 * parentheses.
 */
names
    : '(' Identifier (',' Identifier)* ')'
    ;


/**
 *
 */
insert
    : 'insert' 'into' (alias ':')? qualifiedName names?
      (('values' rows) | defaultValues | select)
      ('returning' metadata? columns)?
    ;

defaultValues
    : 'default' 'values'
    ;

rows
    : row (',' row)*
    ;

row
    : '(' expressionList ')'
    ;


update
    : 'update'     (alias ':')? qualifiedName
      'set'        setList
      ('using'     tableExpr)?
      ('where'     expr)?
      ('returning' metadata? columns)?
    ;

setList
    : set (',' set)*
    ;

set
    : Identifier '=' expr
    ;


delete
    : 'delete' 'from'? (alias ':')? qualifiedName
      ('using' tableExpr)?
      ('where' expr)?
      ('returning' metadata? columns)?
    ;


/**
 * An expression in ESQL which can be computed to return a single-value.
 */
expr
    /*
     * Parentheses controls the order in which expressions are computed when
     * they are part of larger expressions.
     */
    : '(' expr ')'                                          #GroupingExpr


    | '$(' expr ')'                                         #UncomputedExpr

    | type '<' expr '>'                                     #CastExpr


    /*
     * refers to the default value, only applicable
     * in certain context, such as for inserting values
     * and updating columns (to their defaults)
     */
    | 'default'                                                 #DefaultValue

    // literals
    | literal                                                   #LiteralExpr

    | expr ('?' expr)+                                          #CoalesceExpr   // x?0 == | x if x is not null
                                                                                //        | 0 if x is null
                                                                                //
                                                                                // x?y?z == | x if x is not null
                                                                                //          | y if x is null and y is not null
                                                                                //          | otherwise z

//    | expr '?' expr ('?' expr)*                             #CoalesceExpr   // x?0 == | x if x is not null
//                                                                            //        | 0 if x is null
//                                                                            //
//                                                                            // x?y?z == | x if x is not null
//                                                                            //          | y if x is null and y is not null
//                                                                            //          | otherwise z

    | left=expr '||' right=expr                                 #ConcatenationExpr
    | '-' expr                                                  #NegationExpr
    | <assoc=right> left=expr '^' right=expr                    #ExponentiationExpr
    | left=expr op=('*' | '/' | '%') right=expr                 #MultiplicationExpr
    | left=expr op=('+' | '-') right=expr                       #AdditionExpr

    | ':' Identifier                                            #NamedParameter
    | Identifier ':=' expr                                      #NamedArgument      // A named argument to a function. The name
                                                                                    // is dropped when this is translated to SQL
                                                                                    // as most databases does not support named
                                                                                    // arguments yet. This is however useful for
                                                                                    // macro expansions.

    | selectExpression                                          #SelectExpr         // single-column, single-row only when used
                                                                                    // as an expresion
    | Not expr                                                  #NotExpr
    | qualifiedName '(' distinct? expressionList? ')' window?   #FunctionInvocation
    | 'exists' '(' select ')'                                   #Existence

    | left=expr leftCompare=ordering
      mid=simpleExpr
      rightCompare=ordering right=expr                      #RangeExpr

    | left=expr compare right=expr                          #Comparison
    | expr compare Quantifier '(' select ')'                #QuantifiedComparison
    | expr Not? 'in' '(' (expressionList | select) ')'      #InExpression
    | left=expr Not? 'between' mid=expr 'and' right=expr    #BetweenExpr
    | left=expr Not? 'like' right=expr                      #LikeExpr
    | left=expr Not? 'ilike' right=expr                     #ILikeExpr
    | expr 'is' Not? NullLiteral                            #NullCheckExpr

    | columnReference                                       #ColumnExpr

    | left=expr 'and' right=expr                            #LogicalAndExpr
    | left=expr 'or'  right=expr                            #LogicalOrExpr

    // a -> b : (c -> d : (e -> f : g))
//    | <assoc=right> expr ('?' expr ':' expr)+

    /*
        'a' if x else
        'b' if y else
        'c' if z else 'e'
    */
    | <assoc=right> expr ('if' expr 'else' expr)+           #CaseExpr

    | simpleExpr                                            #SimpleExpression

    ;

/**
 * An expression which excludes certain combinations of expression
 * and can be used as the middle expression of a range (e.g a > simpleExpr > b)
 */
simpleExpr
    : '(' simpleExpr ')'                                            #SimpleGroupingExpr
    | type '<' simpleExpr '>'                                       #SimpleCastExpr
    | literal                                                       #SimpleLiteralExpr
    | simpleExpr ('?' simpleExpr)+                                  #SimpleCoalesceExpr
    | left=simpleExpr '||' right=simpleExpr                         #SimpleConcatenationExpr
    | '-' simpleExpr                                                #SimpleNegationExpr
    | <assoc=right> left=simpleExpr '^' right=simpleExpr            #SimpleExponentiationExpr
    | left=simpleExpr op=('*' | '/' | '%') right=simpleExpr         #SimpleMultiplicationExpr
    | left=simpleExpr op=('+' | '-') right=simpleExpr               #SimpleAdditionExpr

    | selectExpression                                              #SimpleSelectExpr   // single-column, single-row only when used
                                                                                        // as an expresion
    | qualifiedName '(' distinct? expressionList? ')' window?       #SimpleFunctionInvocation

    | columnReference                                               #SimpleColumnExpr

    // a -> b : c -> d : e -> f : g
    | <assoc=right> simpleExpr ('if' simpleExpr 'else' simpleExpr)+ #SimpleCaseExpr
    ;

selectExpression
    : '(' distinct?
          (alias ':')? col=expr
          'from'   tableExpr
         ('where'  where=expr)?
         ('order' 'by' orderByList)?
         ('offset' offset=expr)?
      ')'
    ;

// y:(c from S), z:x+1 from T

window
    : 'over' '(' partition? ('order' 'by' orderByList)? ')'
    ;

partition
    : 'partition' 'by' expressionList
    ;

compare
    : '='
    | '!='
    | '<'
    | '>'
    | '<='
    | '>='
    ;

ordering
    : '<'
    | '>'
    | '<='
    | '>='
    ;

expressionList
    : expr (',' expr)*
    ;

literal
    : baseLiteral                           #BasicLiterals
    | NullLiteral                           #Null
    | BaseType '[' baseLiteralList? ']'     #BaseArrayLiteral
    | '[' literalList? ']'                  #JsonArrayLiteral       // valid only in metadata expression
    | '{' attributeList? '}'                #JsonObjectLiteral      // valid only in metadata expression
    ;

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

literalList
    : literal (',' literal)*
    ;

baseLiteralList
    : baseLiteral (',' baseLiteral)*
    ;

columnReference
    : qualifier? alias
    ;

qualifier
    : (Identifier '.')
    ;

define
    : createTable
    | alterTable
    | dropTable
//    | createView
//    | dropView
//    | alterView
//    | createIndex
//    | createSequence
//    | createFunction
//    | createTrigger
    ;

// Creates or alters a table.
createTable
    : 'create' 'table' qualifiedName dropUndefined? '(' tableDefinitions ')'
    ;

/**
 * Removes any excess definitions (columns, constraints, etc.)
 * when automatically updating the table
 */
dropUndefined
    : 'drop' 'undefined'
    ;

alterTable
    : 'alter' 'table' qualifiedName alterTableActions
    ;

alterTableActions
    : alterTableAction (',' alterTableAction )*
    ;

alterTableAction
    : 'rename' 'to' Identifier                                  #RenameTable

    | 'add' tableDefinition                                     #AddTableDefinition

    | 'alter' 'column' Identifier alterColumnDefinition         #AlterColumn

    | 'drop' 'column' Identifier                                #DropColumn
    | 'drop' 'constraint' Identifier                            #DropConstraint
    | 'drop' 'metadata'                                         #DropTableMetadata
    ;

dropTable
    : 'drop' 'table' qualifiedName
    ;

tableDefinitions
    : tableDefinition (',' tableDefinition)+
    ;

tableDefinition
    : columnDefinition
    | derivedColumnDefinition
    | constraintDefinition
    | metadata
    ;

columnDefinition
    : Identifier type (Not NullLiteral)? ('default' expr)? metadata?
    ;

alterColumnDefinition
    : Identifier?           // new column name
      type?                 // new column type
      alterNull?            // null state
      alterDefault?         // column default
      metadata?             // column metadata
    ;

alterNull
    : 'not' 'null'
    | 'null'
    ;

alterDefault
    : 'default' expr
    | 'no' 'default'
    ;

/**
 * A table column whose value is derived from an expression.
 * Any expression which can be part of the projection list of
 * a select statement can be used to compute the value of the
 * derived column.
 */
derivedColumnDefinition
    : Identifier '=' expr metadata?
    ;

constraintDefinition
    : constraintName? 'unique' names                    #UniqueConstraint
    | constraintName? 'primary' 'key' names             #PrimaryKeyConstraint
    | constraintName? 'foreign' 'key'
      from=names 'references' qualifiedName to=names

      /*
       * Foreign keys are used in certain cases to find a path between two
       * tables (e.g. to restrict one table based on a criteria on the other
       * for access control and row-level security); a foreign key can be
       * followed from the table where the foreign key is defined to the table
       * being referred (the forward path) or in the reverse direction (the
       * reverse path).
       *
       * The following attribute sets a cost for following this foreign key
       * in the forward and reverse path. If only the forward path cost is
       * specified, twice its value is assigned to the reverse path cost to
       * provide a preference for forward paths over reverse ones.
       *
       * A negative cost prevents the respective path (forward or reverse)
       * to be followed effectively removing that link from any path that
       * a search algorithm will return.
       *
       * A zero or positive value is the cost for following the link and a
       * uniform cost search (such as Djikstra shortest path algorithm) can
       * be used to find shortest path by cost between tables.
       *
       * A forward cost of 1 and a reverse cost of 2 is assumed when not
       * specified, making forward paths preferable to reverse ones.
       */
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
    | constraintName? 'check' '(' expr ')'              #CheckConstraint
    ;

constraintName
    : 'constraint' Identifier
    ;

onUpdate
    : 'on' 'update' foreignKeyAction
    ;

onDelete
    : 'on' 'delete' foreignKeyAction
    ;

foreignKeyAction
    : 'restrict'
    | 'cascade'
    | 'set' 'null'
    | 'set' 'default'
    ;

type
     : BaseType         #Base
     | arrayType        #Array
     ;

arrayType
    : BaseType '[' ']'
    ;


Quantifier
    : 'all' | 'any'
    ;

BaseType            // POSTGRESQL TYPE      SQL SERVER TYPE
    : 'byte'        // tinyint              tinyint
    | 'short'       // smallint             smallint
    | 'int'         // integer              int
    | 'long'        // bigint               bigint
    | 'float'       // real                 real
    | 'double'      // double precision     float
    | 'money'       // money                money
    | 'bool'        // boolean              bit
    | 'char'        // char(1)              char(1)
    | 'string'      // text                 varchar(8000)
    | 'text'        // text                 varchar(max)
    | 'bytes'       // bytea                varbinary(max)
    | 'date'        // date                 date
    | 'time'        // time                 time
    | 'datetime'    // timestamp            datetime2
    | 'interval'    // interval             varchar(200)     -- No interval type in SQL Server, simulated
    | 'uuid'        // uuid                 uniqueidentifier
    | 'json'        // jsonb                varchar(max)
    ;

Not
    : 'not'
    ;

// Integers
IntegerLiteral
    : '-'? Digits
    ;

// Floating-Point Literals
fragment Digits
    : Digit
    | Digit ('_' | Digit)* Digit
    ;

fragment Digit
    : [0-9]
    ;

FloatingPointLiteral
    : '-'? Digits '.' Digits? ExponentPart?
    | '-'? '.' Digits ExponentPart?
    | '-'? Digits ExponentPart
    ;

fragment ExponentPart
    : [eE] SignedInteger
    ;

fragment SignedInteger
    : Sign? Digits
    ;

fragment Sign
    : [+-]
    ;

BooleanLiteral
    : 'true'
    | 'false'
    ;

MultiLineStringLiteral
    : '`' .*? '`'
    ;

StringLiteral
    : '\'' (StringCharacter | DoubleSingleQuote)* '\''
    ;

fragment StringCharacter
    : ~[']
    ;

fragment DoubleSingleQuote
    : '\'\''
    ;

UuidLiteral
    : 'u\'' HexDigit HexDigit HexDigit HexDigit HexDigit HexDigit HexDigit HexDigit
        '-' HexDigit HexDigit HexDigit HexDigit
        '-' HexDigit HexDigit HexDigit HexDigit
        '-' HexDigit HexDigit HexDigit HexDigit
        '-' HexDigit HexDigit HexDigit HexDigit HexDigit HexDigit HexDigit HexDigit HexDigit HexDigit HexDigit HexDigit '\''
    ;

DateLiteral
    : 'd\'' Date '\''
    | 'd\'' Time '\''
    | 'd\'' Date ' ' Time '\''
    ;

IntervalLiteral
    : 'i\'' IntervalPart (',' IntervalPart)* '\''
    ;

// '3mon 2w 1d 1m' + '1w 1h 1m' = '3mon 3w 1d 1h 2m'
fragment IntervalPart
    : '-'? Digits IntervalSuffix
    ;

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

fragment Date
    : Digit? Digit? Digit Digit '-' [01]? Digit '-' [0123]? Digit
    ;

fragment Time
    : Digit? Digit ':' [012345]? Digit (':' [012345]? Digit ('.' Digit Digit? Digit?)?)?
    ;

fragment HexDigit
    : [0-9a-fA-F]
    ;

NullLiteral
    : 'null'
    ;

Identifier
    : [$_a-zA-Z][$_a-zA-Z0-9]*
    ;

// ignore comments
Comment
    : '#' ~[\r\n]* -> skip
    ;

// ignore white-spaces
Whitespace
    : [ \t\r\n]+ -> skip
    ;
