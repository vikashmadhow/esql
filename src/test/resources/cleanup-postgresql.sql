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


select "x"."/name"        "/name",
       "x"."/tm2"         "/tm2",
       "x"."/tm2/e"       "/tm2/e",
       "x"."/description" "/description",
       "x"."/tm1"         "/tm1",
       "x"."/tm1/e"       "/tm1/e",
       "x"."a"            "a",
       "x"."b"            "b",
       "x"."a/sequence"   "a/sequence",
       "x"."a/m1"         "a/m1",
       "x"."a/m1/e"       "a/m1/e",
       "x"."a/m2"         "a/m2",
       "x"."a/m3"         "a/m3",
       "x"."a/m3/e"       "a/m3/e",
       "x"."a/id"         "a/id",
       "x"."a/type"       "a/type",
       "x"."a/required"   "a/required",
       "x"."b/sequence"   "b/sequence",
       "x"."b/m1"         "b/m1",
       "x"."b/m1/e"       "b/m1/e",
       "x"."b/id"         "b/id",
       "x"."b/type"       "b/type",
       "x"."b/required"   "b/required"
from (select "x"."/name"        "/name",
             "x"."/tm2"         "/tm2",
             "x"."/tm2/e"       "/tm2/e",
             "x"."/description" "/description",
             "x"."/tm1"         "/tm1",
             "x"."/tm1/e"       "/tm1/e",
             "x"."a"            "a",
             "x"."b"            "b",
             "x"."a/sequence"   "a/sequence",
             "x"."a/m1"         "a/m1",
             "x"."a/m1/e"       "a/m1/e",
             "x"."a/m2"         "a/m2",
             "x"."a/m3"         "a/m3",
             "x"."a/m3/e"       "a/m3/e",
             "x"."a/id"         "a/id",
             "x"."a/type"       "a/type",
             "x"."a/required"   "a/required",
             "x"."b/sequence"   "b/sequence",
             "x"."b/m1"         "b/m1",
             "x"."b/m1/e"       "b/m1/e",
             "x"."b/id"         "b/id",
             "x"."b/type"       "b/type",
             "x"."b/required"   "b/required"
      from (select N'S'                                       "/name",
                   (iif("s"."a" > "s"."b", 1, 0))             "/tm2",
                   '((s.a > s.b))'                            "/tm2/e",
                   N'S test table'                            "/description",
                   (select max("b") "max" from "DBO"."S" "S") "/tm1",
                   '(max:max(b) from S:S)'                    "/tm1/e",
                   "s"."a"                                    "a",
                   "s"."b"                                    "b",
                   2                                          "a/sequence",
                   (iif("s"."b" > 5, 1, 0))                   "a/m1",
                   '((s.b > 5))'                              "a/m1/e",
                   10                                         "a/m2",
                   (iif("s"."a" != 0, 1, 0))                  "a/m3",
                   '((s.a != 0))'                             "a/m3/e",
                   'ee241f92-86ee-4752-adc1-4b698d9a77b5'     "a/id",
                   N'int'                                     "a/type",
                   0                                          "a/required",
                   3                                          "b/sequence",
                   (iif("s"."b" < 0, 1, 0))                   "b/m1",
                   '((s.b < 0))'                              "b/m1/e",
                   'd408602a-543c-4b9a-b142-d72c38bbad2b'     "b/id",
                   N'int'                                     "b/type",
                   0                                          "b/required"
            from "DBO"."S" "s"
            order by "s"."a" asc
            offset 0 rows) "x"
      order by "x"."a" asc
      offset 0 rows) "x"
where exists(select N'S'                                                                                                                                                                   "/name",
                    (iif("S"."a" > "S"."b", 1, 0))                                                                                                                                         "/tm2",
                    '((S.a > S.b))'                                                                                                                                                        "/tm2/e",
                    N'S test table'                                                                                                                                                        "/description",
                    (select max("b") "max" from "DBO"."S" "S")                                                                                                                             "/tm1",
                    '(max:max(b) from S:S)'                                                                                                                                                "/tm1/e",
                    "S"."_id"                                                                                                                                                              "_id",
                    "S"."a"                                                                                                                                                                "a",
                    "S"."b"                                                                                                                                                                "b",
                    "S"."a" + "S"."b"                                                                                                                                                      "c",
                    "S"."b" + ("S"."a" + "S"."b")                                                                                                                                          "d",
                    ("S"."e" = 1)                                                                                                                                                          "e",
                    (select max("a") "max" from "DBO"."S" "S")                                                                                                                             "f",
                    (select distinct ("S"."a" + "S"."b") "c"
                     from "DBO"."S" "S"
                     where (("S"."b" + ("S"."a" + "S"."b")) > 5))                                                                                                                          "g",
                    "S"."h"                                                                                                                                                                "h",
                    "S"."i"                                                                                                                                                                "i",
                    "S"."j"                                                                                                                                                                "j",
                    "S"."k"                                                                                                                                                                "k",
                    1                                                                                                                                                                      "_id/sequence",
                    '98beb27c-83d9-4ecb-bc01-a73e076f5f98'                                                                                                                                 "_id/id",
                    N'uuid'                                                                                                                                                                "_id/type",
                    1                                                                                                                                                                      "_id/required",
                    2                                                                                                                                                                      "a/sequence",
                    (iif("S"."b" > 5, 1, 0))                                                                                                                                               "a/m1",
                    '((S.b > 5))'                                                                                                                                                          "a/m1/e",
                    10                                                                                                                                                                     "a/m2",
                    (iif("S"."a" != 0, 1, 0))                                                                                                                                              "a/m3",
                    '((S.a != 0))'                                                                                                                                                         "a/m3/e",
                    'ee241f92-86ee-4752-adc1-4b698d9a77b5'                                                                                                                                 "a/id",
                    N'int'                                                                                                                                                                 "a/type",
                    0                                                                                                                                                                      "a/required",
                    3                                                                                                                                                                      "b/sequence",
                    (iif("S"."b" < 0, 1, 0))                                                                                                                                               "b/m1",
                    '((S.b < 0))'                                                                                                                                                          "b/m1/e",
                    'd408602a-543c-4b9a-b142-d72c38bbad2b'                                                                                                                                 "b/id",
                    N'int'                                                                                                                                                                 "b/type",
                    0                                                                                                                                                                      "b/required",
                    4                                                                                                                                                                      "c/sequence",
                    (iif("S"."a" > 5, 1, 0))                                                                                                                                               "c/m1",
                    '((S.a > 5))'                                                                                                                                                          "c/m1/e",
                    "S"."a" + "S"."b"                                                                                                                                                      "c/m2",
                    'S.a + S.b'                                                                                                                                                            "c/m2/e",
                    (iif("S"."b" > 5, 1, 0))                                                                                                                                               "c/m3",
                    '((S.b > 5))'                                                                                                                                                          "c/m3/e",
                    '4e36309e-89a5-44bc-8ba1-bb02fe9ecbdc'                                                                                                                                 "c/id",
                    N'int'                                                                                                                                                                 "c/type",
                    1                                                                                                                                                                      "c/derived",
                    0                                                                                                                                                                      "c/required",
                    'S.a + S.b'                                                                                                                                                            "c/e",
                    5                                                                                                                                                                      "d/sequence",
                    10                                                                                                                                                                     "d/m1",
                    '4c54c479-848c-4274-ade7-ed174c3dd17f'                                                                                                                                 "d/id",
                    N'int'                                                                                                                                                                 "d/type",
                    1                                                                                                                                                                      "d/derived",
                    0                                                                                                                                                                      "d/required",
                    'S.b + (S.a + S.b)'                                                                                                                                                    "d/e",
                    6                                                                                                                                                                      "e/sequence",
                    ("S"."a" + "S"."b")                                                                                                                                                    "e/m1",
                    '(S.a + S.b)'                                                                                                                                                          "e/m1/e",
                    '1d22f176-9aae-4310-8947-8a2f660f2bc0'                                                                                                                                 "e/id",
                    N'bool'                                                                                                                                                                "e/type",
                    0                                                                                                                                                                      "e/required",
                    7                                                                                                                                                                      "f/sequence",
                    (select min("a") "min" from "DBO"."S" "S")                                                                                                                             "f/m1",
                    '(min:min(a) from S:S)'                                                                                                                                                "f/m1/e",
                    '65e29e20-c975-40f1-8015-e0ef25c21ef8'                                                                                                                                 "f/id",
                    N'int'                                                                                                                                                                 "f/type",
                    1                                                                                                                                                                      "f/derived",
                    0                                                                                                                                                                      "f/required",
                    '(max:max(a) {''int''} from S:S)'                                                                                                                                      "f/e",
                    8                                                                                                                                                                      "g/sequence",
                    (select min("a") "min" from "a.b"."T" "T")                                                                                                                             "g/m1",
                    '(min:min(a) from T:a.b.T)'                                                                                                                                            "g/m1/e",
                    '8b4ada39-388a-4e1f-9392-93ff4896fc14'                                                                                                                                 "g/id",
                    N'int'                                                                                                                                                                 "g/type",
                    1                                                                                                                                                                      "g/derived",
                    0                                                                                                                                                                      "g/required",
                    '(distinct c:(S.a + S.b) {''int''} from S:S where ((S.b + (S.a + S.b)) > 5))'                                                                                          "g/e",
                    9                                                                                                                                                                      "h/sequence",
                    5                                                                                                                                                                      "h/m1",
                    'b3f67975-ca02-4699-b7e5-5b6105d167a8'                                                                                                                                 "h/id",
                    N'text[]'                                                                                                                                                              "h/type",
                    0                                                                                                                                                                      "h/required",
                    10                                                                                                                                                                     "i/sequence",
                    '5036a7d9-589a-473a-a258-6689880bde56'                                                                                                                                 "i/id",
                    (select "lv"."label" "label"
                     from "_platform.lookup"."LookupValue" "lv"
                              join "_platform.lookup"."Lookup" "l"
                                   on (((("lv"."lookup_id" = "l"."_id")) and (("l"."name" = N'City'))))
                     where (("lv"."code" = "i")))                                                                                                                                          "i/label",
                    '(label:lv.label from lv:_platform.lookup.LookupValue join l:_platform.lookup.Lookup on ((((lv.lookup_id = l._id)) and ((l.name = ''City'')))) where ((lv.code = i)))' "i/label/e",
                    N'string'                                                                                                                                                              "i/type",
                    0                                                                                                                                                                      "i/required",
                    11                                                                                                                                                                     "j/sequence",
                    '071a9a85-7503-450e-90da-043f7a1a5de7'                                                                                                                                 "j/id",
                    N'int[]'                                                                                                                                                               "j/type",
                    0                                                                                                                                                                      "j/required",
                    12                                                                                                                                                                     "k/sequence",
                    '84a45ed3-79ea-42f6-bc53-4a7b100a9525'                                                                                                                                 "k/id",
                    N'interval'                                                                                                                                                            "k/type",
                    0                                                                                                                                                                      "k/required"
             from "DBO"."S" "S");



with recursive r("id", "parent", "name") as (select "_id" "_id", "parent_id" "parent_id", "name" "name"
                                             from "x"."R" "R"
                                             where "parent_id" is not null
                                             union all
                                             select "xr"."_id"                       "_id",
                                                    "xr"."parent_id"                 "parent_id",
                                                    "r"."name" || '/' || "xr"."name" "column"
                                             from "x"."R" "xr"
                                                      join "r" "r" on ("xr"."parent_id" = "r"."id"))
select "r"."id" "id", "r"."parent" "parent", "r"."name" "name"
from "r" "r";



select (iif("S"."a" > "S"."b", 1, 0))                                              "/tm2",
       (select max("b") "max" from "DBO"."S" "S")                                  "/tm1",
       "S"."a"                                                                     "a",
       "S"."b"                                                                     "b",
       "S"."a" + "S"."b"                                                           "c",
       "S"."a" * "S"."b"                                                           "column",
       case
           when ("S"."a" * ("S"."a" + "S"."b") < 10) then N'01. ' + N'a times c' + N' < ' + cast(10 as nvarchar(4000))
           when (10 <= "S"."a" * ("S"."a" + "S"."b") and "S"."a" * ("S"."a" + "S"."b") < 25) then N'02. ' +
                                                                                                  cast(10 as nvarchar(4000)) +
                                                                                                  N' <= ' +
                                                                                                  N'a times c' +
                                                                                                  N' < ' +
                                                                                                  cast(25 as nvarchar(4000))
           when (25 <= "S"."a" * ("S"."a" + "S"."b") and "S"."a" * ("S"."a" + "S"."b") < 40) then N'03. ' +
                                                                                                  cast(25 as nvarchar(4000)) +
                                                                                                  N' <= ' +
                                                                                                  N'a times c' +
                                                                                                  N' < ' +
                                                                                                  cast(40 as nvarchar(4000))
           when (40 <= "S"."a" * ("S"."a" + "S"."b") and "S"."a" * ("S"."a" + "S"."b") < 75) then N'04. ' +
                                                                                                  cast(40 as nvarchar(4000)) +
                                                                                                  N' <= ' +
                                                                                                  N'a times c' +
                                                                                                  N' < ' +
                                                                                                  cast(75 as nvarchar(4000))
           when (75 <= "S"."a" * ("S"."a" + "S"."b") and "S"."a" * ("S"."a" + "S"."b") < 100) then N'05. ' +
                                                                                                   cast(75 as nvarchar(4000)) +
                                                                                                   N' <= ' +
                                                                                                   N'a times c' +
                                                                                                   N' < ' +
                                                                                                   cast(100 as nvarchar(4000))
           when (100 <= "S"."a" * ("S"."a" + "S"."b") and "S"."a" * ("S"."a" + "S"."b") < 200) then N'06. ' +
                                                                                                    cast(100 as nvarchar(4000)) +
                                                                                                    N' <= ' +
                                                                                                    N'a times c' +
                                                                                                    N' < ' +
                                                                                                    cast(200 as nvarchar(4000))
           else N'07. ' + N'a times c' + N' >= ' + cast(200 as nvarchar(4000)) end "bin",
       (iif("S"."b" > 5, 1, 0))                                                    "a/m1",
       (iif("S"."a" != 0, 1, 0))                                                   "a/m3",
       (iif("S"."b" < 0, 1, 0))                                                    "b/m1"
from "DBO"."S" "S"
order by "a"





create table if not exists "_core"."constraints"("_id" uuid not null, "name" text not null, "relation_id" uuid not null, "type" char(1) not null, "check_expr" text, "source_columns" text[], "target_relation_id" uuid, "target_columns" text[], "forward_cost" integer not null default select 1, "reverse_cost" integer not null default select 2, "on_update" char(1), "on_delete" char(1), constraint "_core_constraints_pk" primary key("_id"), constraint "_core_constraints_rel_id_fk" foreign key("relation_id") references "_core"."relations"("_id") on update cascade on delete cascade)