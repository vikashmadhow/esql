drop table "X";
drop table "Y";
drop schema "_platform.util" cascade;
drop schema "_platform.user" cascade;
drop schema "_platform.external" cascade;
drop schema "_platform.filter" cascade;
drop schema "_platform.import" cascade;
drop schema "_platform.lookup" cascade;
drop schema "_platform.report" cascade;
drop schema "_platform.sampling" cascade;
drop schema "a.b" cascade;
drop schema "b" cascade;
drop table "S";
drop schema _core cascade;


select "a" > "b",
       '"a" > "b"',
       (select max("b") "max" from "public"."S" "S"),
       '(select max("b") "max" from "public"."S" "S")',
       "a" "a",
       "b" "b",
       "b" > 5,
       '"b" > 5',
       "a" != 0, '"a" != 0', "b" < 0, '"b" < 0'
from "public"."S" "S"
order by "S"."a" asc;
