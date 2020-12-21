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

// @todo window functions

program
    : statement (';' statement)* ';'?
    ;

noop
    : ';'
    ;

statement
    : select
    | modify
    | define
    | noop
    ;

queryUpdate
    : select
    | modify
    ;

modify
    : update
    | insert
    | delete
    ;

select
    : with                                  #WithSelection
    | select setop select                   #CompositeSelection
    |'select' metadata? distinct? explicit? columns
      ('from'  tableExpr)?
      ('where' where=expr)?
      ('order' 'by' orderByList)?
      ('group' 'by' groupByList)?
      ('having' having=expr)?
      ('offset' offset=expr)?
      ('limit'  limit=expr)?                #BaseSelection
    ;

groupByList
    : expressionList                    #SimpleGroup
    | 'rollup' '(' expressionList ')'   #RollupGroup
    | 'cube'   '(' expressionList ')'   #CubeGroup
    ;

orderByList
    : orderBy (',' orderBy)*
    ;

orderBy
    : expr direction?
    ;

setop
    : 'union'
    | 'union' 'all'
    | 'intersect'
    | 'except'
    ;

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

with
    : 'with' recursive='recursive'? cteList queryUpdate
    ;

cteList
    : cte (',' cte)*
    ;

cte
    : Identifier names? '(' queryUpdate ')'
    ;

names
    : '(' Identifier (',' Identifier)* ')'
    ;

distinct
    : 'all'
    | 'distinct' ('on' '(' expressionList ')')?
    ;

explicit
    : 'explicit'
    ;

columns
    : column (',' column)*
    ;

column
    : (alias ':')? expr metadata?       #SingleColumn
    | qualifier? '*'                    #StarColumn
    ;

// t{x:4, y:b=0}(a {m1:x}, b {m2:y}):(values (1,2), (3,4))
tableExpr
    : (alias ':')? qualifiedName                                 #SingleTableExpr
    | alias ':' '(' select ')'                                   #SelectTableExpr
    | alias metadata? dynamicColumns ':' '(' 'values' rows ')'   #DynamicTableExpr
    | left=tableExpr 'cross' right=tableExpr                     #CrossProductTableExpr
    | left=tableExpr joinType? 'join' right=tableExpr 'on' expr  #JoinTableExpr
    ;

dynamicColumns
    : '(' nameWithMetadata (',' nameWithMetadata)* ')'
    ;

nameWithMetadata
    : Identifier metadata?
    ;

joinType
    : 'left'
    | 'right'
    | 'full'
    ;

aliasPart
    : EscapedIdentifier     #EscapedAliasPart
    | qualifiedName         #NormalAliasPart
    ;

alias
    : (root='/')? aliasPart ('/' aliasPart )*
    ;

qualifiedName
    : Identifier ('.' Identifier)*
    ;

/**
 * An expression which excludes certain combinations of expression
 * and can be used as the middle expression of a range (e.g a > simpleExpr > b)
 */
simpleExpr
    : '(' simpleExpr ')'                                            #SimpleGroupingExpr
    | simpleExpr '::' type                                          #SimpleCastExpr
    | literal                                                       #SimpleLiteralExpr
    | left=simpleExpr '?' right=simpleExpr                          #SimpleCoalesceExpr
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
    | <assoc=right> simpleExpr ('->' simpleExpr ':' simpleExpr)+   #SimpleCaseExpr
    ;


/**
 * An expr represents an expression in the language. An expression
 * can be computed to return a single-value
 */
expr
    : '(' expr ')'                                          #GroupingExpr
    | '$(' expr ')'                                         #UncomputedExpr
    | type '<' expr '>'                                     #CastExpr
    | 'default'                                             #DefaultValue   // refers to the default value, only applicable
                                                                            // in certain context, such as for inserting values
                                                                            // and updating columns (to their defaults)
    // literals
    | literal                                               #LiteralExpr

    | left=expr '?' right=expr                              #CoalesceExpr   // x?0 == | x if x is not null
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
    | <assoc=right> expr ('->' expr ':' expr)+              #CaseExpr
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

metadata
    : '{' attributeList '}'
    ;

attributeList
    : attribute (',' attribute)*
    ;

attribute
    : Identifier ':' expr
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

direction
    : 'asc'
    | 'desc'
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
    : 'derived' Identifier expr metadata?
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
//     | objectType
     ;

arrayType
    : BaseType '[' ']'
    ;

//returnType
//     : BaseType
//     | arrayType
//     | setofType
//     | VoidType
//     | objectType
//     ;

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

//RecordType
//    : 'record'
//    ;

//VoidType
//    : 'void'
//    ;

//PolymorphicType
//    : 'anyelement'
//    | 'anyarray'
//    | 'anynonarray'
//    ;

//objectType
//    : table
//    ;

//EscapedIdentifier
//    : '"' ~["]+ '"'
//    ;

//setofType
//    : 'setof' '[' (BaseType | objectType) ']'
//    ;

EscapedIdentifier
//    : '"' Identifier '"'
    : '"' ~["]+ '"'
    ;

If
    : 'if'
    ;

Not
    : 'not'
    ;

Exists
    : 'exists'
    ;

// Integers
IntegerLiteral
    : '-'? Digits
    ;

//fragment DecimalNumeral
//    : Zero
//    | GreaterThanZero
//    ;
//
//fragment Zero
//    : '0'
//    ;
//
//fragment GreaterThanZero
//    : [1-9] [0-9]*
//    ;


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

//fragment StringCharacter
//    : NormalStringCharacter
//    | EscapeSequence
//    ;
//
//fragment NormalStringCharacter
//    : ~['%]
//    ;
//
//// Escape Sequences for string Literals
//fragment EscapeSequence
//    : '%' [btnfrq%]
//    | UnicodeEscape
//    ;
//
//fragment UnicodeEscape
//    : '%u' HexDigit HexDigit HexDigit HexDigit
//    ;

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
