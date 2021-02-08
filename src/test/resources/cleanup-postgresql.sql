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



select (iif("x"."a" > "x"."b", 1, 0)),
       (select max("b") "max" from "DBO"."S" "S"),
       "x"."a" "a",
       "x"."b" "b",
       (iif("x"."b" > 5, 1, 0)),
       (iif("x"."a" != 0, 1, 0)),
       (iif("x"."b" < 0, 1, 0))
from (select (iif("s"."a" > "s"."b", 1, 0)) "c1",
             (select max("b") "max" from "DBO"."S" "S") "c2",
             "s"."a" "a",
             "s"."b" "b",
             (iif("s"."b" > 5, 1, 0)) "c3",
             (iif("s"."a" != 0, 1, 0)) "c4",
             (iif("s"."b" < 0, 1, 0)) "c5"
      from "DBO"."S" "s") "x"


select (iif("x"."a" > "x"."b", 1, 0))             "/tm2",
       (select max("b") "max" from "DBO"."S" "S") "/tm1",
       "x"."a"                                    "a",
       "x"."b"                                    "b",
       (iif("x"."b" > 5, 1, 0))                   "a/m1",
       (iif("x"."a" != 0, 1, 0))                  "a/m3",
       (iif("x"."b" < 0, 1, 0))                   "b/m1"
from (select (iif("s"."a" > "s"."b", 1, 0))             "/tm2",
             (select max("b") "max" from "DBO"."S" "S") "/tm1",
             "s"."a"                                    "a",
             "s"."b"                                    "b",
             (iif("s"."b" > 5, 1, 0))                   "a/m1",
             (iif("s"."a" != 0, 1, 0))                  "a/m3",
             (iif("s"."b" < 0, 1, 0))                   "b/m1"
      from "DBO"."S" "s"
      order by "s"."a" asc
      offset 0 rows) "x"