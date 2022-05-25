# ESQL
A language similar in form to SQL that includes metadata (as a list of name-expression 
pairs) which can be attached to tables and columns. The language is transparently 
translated for execution to the underlying database (Postgresql and MS SQL Server 
currently).

For example, this creates a table (metadata are between {}):
```
  create table com.example.S(
    {
      # table metadata (applied to all queries on this table)
      max_a: (max(a) from com.example.S),
      a_gt_b: a > b
    }
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

When a table is queried, metadata attached to the table and columns are automatically
included in the result, and can be overridden at the query level:
```
  select x.a, x.b, y.c, 
         z.d {m1: 'test'}, # metadata override: if the z.d already contains a 
                           # metadata attribute m1, its value will be replaced
                           # by 'test'
                            
         # ESQL equivalent of SQL case statement, based on Python conditional expressions: 
         #    <true-expression> if <condition> else <false-expression>
         # The conditional expression associates to the right and can be chained.
         # ESQL also supports two-sided comparison, like Python.      
                         
         'A' if  0 <= x.a < 5  else
         'B' if  5 <= x.a < 10 else
         'C' if 10 <= x.a < 20 else 'D'
    from x:X 
    join y:Y on x.a=y.b 
    left join z:Z on y.c=z.c
   where 5 <= z.s <= 10  
```
