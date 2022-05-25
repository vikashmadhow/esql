# Design of history tables in ESQL

## About
A history table records all changes made to a table, which could be used to revert
a table to a previous state, to track changes, to create snapshots of the data 
at specific point in time, and so on.

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
    _id uuid not null,
    name string not null, 
    address string,
    b_id uuid,
    primary key(_id),
    unique(name),
    foreign key(b_id) references B(_id)
  )
```

A history table is created for every table for which history is enabled, mirroring
its structure and adding some columns to keep information on the event generating
the history record. Following is the history table created for the above table:

```
  create table X.History({
      name: 'History of X' 
    }
    _id uuid not null,
    name string not null, 
    address string,
    b_id uuid,
    
    _hist_trans_id string,
    _hist_user string,
    _hist_event int,
    _hist_time datetime
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
1. **_hist_trans_id**: the unique identifier of the transaction during
   which the event happened.
2. **_hist_user**: the user at the source of this event. This is informational
   only and does not need to be a valid user.
3. **_hist_event**: a code identifying the event. E.g. *1=insert, 2=delete,
   3=update_from and 4=update_to*.
4. **_hist_time**: the date and t

### _core.history
Coarse-grain changes to tables will also be recorded in the system table 
`_core.history` which will hold:

| trans_id | user | table | event | at       |
|----------|------|-------|-------|----------|
| 122323   | xyz  | a     | 1     | 01292102 |
| 122323   | xyz  | b     | 2     | 05453442 |

`_core.history` provides a simple way to get the tables impacted by a transaction,
with the detailed data for those events stored in the respective history tables.

### Trigger
Events are captured and stored in the history tables by a trigger which is added
to the table on creation. This trigger is called after every insert, delete and
update on the table and send the event data to the `_core.history` table and the
corresponding history table.

The trigger (and the history table) is rewritten whenever the table is altered. 

### Optimising coarse-grain history tracking
The event-capture trigger must add minimal overhead to the ongoing query, ideally 
only generating one cheap query for every change captured. The `_core.history` 
table, however, keeps an aggregate for each event on a table per transaction. This
aggregate requires a selection followed by an insert or an update. To optimise
this capture, the change is instead inserted in the append-only table `_core._temp_history`
which has the following structure:

| trans_id | table | event | at       |
|----------|-------|-------|----------|
| 122323   | a     | 1     | 01292102 |
| 122323   | a     | 2     | 05453442 |

Inserts in such a simple table with no indexes is very fast (especially given that
the number of rows in this table are kept to a minimum by deleting all rows for
transactions that have been processed). After the transaction commits, the events 
for the transaction captured in the `core._temp_history` table are aggregated and
copied to the `_core.history` table asynchronously, following which they are deleted
from the `_core._temp_history` table.