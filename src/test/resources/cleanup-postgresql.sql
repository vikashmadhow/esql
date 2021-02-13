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


with s("id", "a", "b", "c") as (select "_id" "id", "a" "a", "b" "b", ("S"."a" + "S"."b") "c"
                                from "DBO"."S" "S"
                                order by "a" offset 0 rows)
select N'T'                                       "/name",
       (iif("t"."a" > "t"."b", 1, 0))             "/tm2",
       '((t.a > t.b))'                            "/tm2/e",
       N'T test table'                            "/description",
       (select max("b") "max" from "a.b"."T" "T") "/tm1",
       '(max:max(b) from T:a.b.T)'                "/tm1/e",
       "t"."a"                                    "a",
       "t"."b"                                    "b",
       "s"."c"                                    "c",
       2                                          "a/sequence",
       (iif("t"."b" > 5, 1, 0))                   "a/m1",
       '((t.b > 5))'                              "a/m1/e",
       10                                         "a/m2",
       (iif("t"."a" != 0, 1, 0))                  "a/m3",
       '((t.a != 0))'                             "a/m3/e",
       '24c1163c-c522-4834-b4f0-1c4007c02e96'     "a/id",
       N'int'                                     "a/type",
       0                                          "a/required",
       3                                          "b/sequence",
       (iif("t"."b" < 0, 1, 0))                   "b/m1",
       '((t.b < 0))'                              "b/m1/e",
       '64647941-e9be-4cd2-85b9-eb638a510e97'     "b/id",
       N'int'                                     "b/type",
       0                                          "b/required"
from "a.b"."T" "t"
         join "s" "s" on ("t"."s_id" = "s"."id")