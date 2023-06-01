-- Connection and their status
SELECT
  DB_NAME(dbid) as DBName,
  COUNT(dbid) as NumberOfConnections,
  loginame as LoginName,
  blocked, cmd,
  status
FROM
  sys.sysprocesses
WHERE
    dbid > 0 and db_name(dbid)='aletia2'
GROUP BY
  dbid, loginame, blocked, cmd, status;


-- Active queries
SELECT sqltext.TEXT,
       req.session_id,
       req.status,
       req.command,
       req.cpu_time,
       req.total_elapsed_time
  FROM sys.dm_exec_requests req
 CROSS APPLY sys.dm_exec_sql_text(sql_handle) AS sqltext


-- full query text for spid
select  session_id, sql.text
from sys.dm_exec_requests req
       cross apply sys.dm_exec_sql_text(req.sql_handle) sql
where session_id in (87, 90);


-- Check collation of columns
SELECT
  concat('alter table [',  TABLE_SCHEMA, '].[', TABLE_NAME,
         '] alter column [', COLUMN_NAME, '] ', DATA_TYPE,
         iif(CHARACTER_MAXIMUM_LENGTH = -1, '(max)', concat('(', CHARACTER_MAXIMUM_LENGTH, ')')),
         ' COLLATE Latin1_General_CS_AS', iif(IS_NULLABLE = 'NO', ' NOT NULL', ''), ';') correct_collation,
  TABLE_SCHEMA + '.' + TABLE_NAME,
  COLUMN_NAME,
  DATA_TYPE,
  CHARACTER_MAXIMUM_LENGTH,
  COLLATION_NAME,
  IS_NULLABLE
FROM INFORMATION_SCHEMA.COLUMNS
where COLLATION_NAME = 'Latin1_General_CI_AS';
