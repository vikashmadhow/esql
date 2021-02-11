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



select "x"."/name"        "/name",
       "x"."/tm2"         "/tm2",
       "x"."/tm2/e"       "/tm2/e",
       "x"."/description" "/description",
       "x"."/tm1"         "/tm1",
       "x"."/tm1/e"       "/tm1/e",
       "x"."a"            "a",
       "x"."c"            "c",
       "x"."a/sequence"   "a/sequence",
       "x"."a/m1"         "a/m1",
       "x"."a/m1/e"       "a/m1/e",
       "x"."a/m2"         "a/m2",
       "x"."a/m3"         "a/m3",
       "x"."a/m3/e"       "a/m3/e",
       "x"."a/id"         "a/id",
       "x"."a/type"       "a/type",
       "x"."a/required"   "a/required",
       "x"."c/sequence"   "c/sequence",
       "x"."c/m1"         "c/m1",
       "x"."c/m1/e"       "c/m1/e",
       "x"."c/e"          "c/e",
       "x"."c/m2"         "c/m2",
       "x"."c/m2/e"       "c/m2/e",
       "x"."c/m3"         "c/m3",
       "x"."c/m3/e"       "c/m3/e",
       "x"."c/id"         "c/id",
       "x"."c/type"       "c/type",
       "x"."c/derived"    "c/derived",
       "x"."c/required"   "c/required"
from (select 'S'                                           "/name",
             (("s"."a" > "s"."b"))                         "/tm2",
             '((s.a > s.b))'                               "/tm2/e",
             'S test table'                                "/description",
             (select max("b") "max" from "public"."S" "S") "/tm1",
             '(max:max(b) from S:S)'                       "/tm1/e",
             "s"."a"                                       "a",
             "s"."a" + "s"."b"                             "c",
             2                                             "a/sequence",
             (("s"."b" > 5))                               "a/m1",
             '((s.b > 5))'                                 "a/m1/e",
             10                                            "a/m2",
             (("s"."a" != 0))                              "a/m3",
             '((s.a != 0))'                                "a/m3/e",
             '8d2ee719-ace2-40bd-872f-ee3ce44575c2'::uuid "a/id",
             'int'                                         "a/type",
             false                                         "a/required",
             4                                             "c/sequence",
             (("s"."a" > 5))                               "c/m1",
             '((s.a > 5))'                                 "c/m1/e",
             's.a + s.b'                                   "c/e",
             "s"."a" + "s"."b"                             "c/m2",
             's.a + s.b'                                   "c/m2/e",
             (("s"."b" > 5))                               "c/m3",
             '((s.b > 5))'                                 "c/m3/e",
             '7f49aaed-3fdb-4d94-a523-ed35cd3dcdac'::uuid "c/id",
             'void'                                        "c/type",
             true                                          "c/derived",
             false                                         "c/required"
      from "public"."S" "s"
      order by "s"."a" asc) "x"


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
from (select 'S'                                           "/name",
             (("s"."a" > "s"."b"))                         "/tm2",
             '((s.a > s.b))'                               "/tm2/e",
             'S test table'                                "/description",
             (select max("b") "max" from "PUBLIC"."S" "S") "/tm1",
             '(max:max(b) from S:S)'                       "/tm1/e",
             "s"."a"                                       "a",
             "s"."b"                                       "b",
             2                                             "a/sequence",
             (("s"."b" > 5))                               "a/m1",
             '((s.b > 5))'                                 "a/m1/e",
             10                                            "a/m2",
             (("s"."a" != 0))                              "a/m3",
             '((s.a != 0))'                                "a/m3/e",
             '1d58e5a5-3a48-477c-bad8-ff57c608771d'        "a/id",
             'int'                                         "a/type",
             false                                         "a/required",
             3                                             "b/sequence",
             (("s"."b" < 0))                               "b/m1",
             '((s.b < 0))'                                 "b/m1/e",
             '742711bb-6b54-4e3d-89e5-57830071db19'        "b/id",
             'int'                                         "b/type",
             false                                         "b/required"
      from "PUBLIC"."S" "s"
      order by "s"."a" asc) "x"