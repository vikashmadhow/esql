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
      from (select 'S'                                           "/name",
                   (("s"."a" > "s"."b"))                         "/tm2",
                   '((s.a > s.b))'                               "/tm2/e",
                   'S test table'                                "/description",
                   (select max("b") "max" from "public"."S" "S") "/tm1",
                   '(max:max(b) from S:S)'                       "/tm1/e",
                   "s"."a"                                       "a",
                   "s"."b"                                       "b",
                   2                                             "a/sequence",
                   (("s"."b" > 5))                               "a/m1",
                   '((s.b > 5))'                                 "a/m1/e",
                   10                                            "a/m2",
                   (("s"."a" != 0))                              "a/m3",
                   '((s.a != 0))'                                "a/m3/e",
                   '8d2ee719-ace2-40bd-872f-ee3ce44575c2'::uuid "a/id",
                   'int'                                         "a/type",
                   false                                         "a/required",
                   3                                             "b/sequence",
                   (("s"."b" < 0))                               "b/m1",
                   '((s.b < 0))'                                 "b/m1/e",
                   'b7e94cee-be70-4430-adf8-252c579fed93'::uuid "b/id",
                   'int'                                         "b/type",
                   false                                         "b/required"
            from "public"."S" "s"
            order by "s"."a" asc) "x"
      order by "x"."a" asc) "x"


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
from (select "x"."/name"        "/name",
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
      order by "x"."a" asc) "x"


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
from (select "x"."/name"        "/name",
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
      from (select N'S'                                       "/name",
                   (iif("s"."a" > "s"."b", 1, 0))             "/tm2",
                   '((s.a > s.b))'                            "/tm2/e",
                   N'S test table'                            "/description",
                   (select max("b") "max" from "DBO"."S" "S") "/tm1",
                   '(max:max(b) from S:S)'                    "/tm1/e",
                   "s"."a"                                    "a",
                   "s"."a" + "s"."b"                          "c",
                   2                                          "a/sequence",
                   (iif("s"."b" > 5, 1, 0))                   "a/m1",
                   '((s.b > 5))'                              "a/m1/e",
                   10                                         "a/m2",
                   (iif("s"."a" != 0, 1, 0))                  "a/m3",
                   '((s.a != 0))'                             "a/m3/e",
                   'ee241f92-86ee-4752-adc1-4b698d9a77b5'     "a/id",
                   N'int'                                     "a/type",
                   0                                          "a/required",
                   4                                          "c/sequence",
                   (iif("s"."a" > 5, 1, 0))                   "c/m1",
                   '((s.a > 5))'                              "c/m1/e",
                   's.a + s.b'                                "c/e",
                   "s"."a" + "s"."b"                          "c/m2",
                   's.a + s.b'                                "c/m2/e",
                   (iif("s"."b" > 5, 1, 0))                   "c/m3",
                   '((s.b > 5))'                              "c/m3/e",
                   '4e36309e-89a5-44bc-8ba1-bb02fe9ecbdc'     "c/id",
                   N'void'                                    "c/type",
                   1                                          "c/derived",
                   0                                          "c/required"
            from "DBO"."S" "s"
            order by "s"."a" asc
            offset 0 rows) "x"
      order by "x"."a" asc
      offset 0 rows) "x"


select "s"."_id"                                                                                                "_id",
       "s"."a"                                                                                                  "a",
       "s"."b"                                                                                                  "b",
       "s"."a" + "s"."b"                                                                                        "c",
       "s"."b" + ("s"."a" + "s"."b")                                                                            "d",
       "s"."e"                                                                                                  "e",
       (select max("a") "max" from "DBO"."S" "S")                                                               "f",
       (select distinct ("S"."a" + "S"."b") "c" from "DBO"."S" "S" where (("S"."b" + ("S"."a" + "S"."b")) > 5)) "g",
       "s"."h"                                                                                                  "h",
       "s"."i"                                                                                                  "i",
       "s"."j"                                                                                                  "j",
       "s"."k"                                                                                                  "k",
       "T"."_id"                                                                                                "_id_4rup",
       "T"."a"                                                                                                  "a_tlry",
       "T"."b"                                                                                                  "b_pfpe",
       "T"."a" + "T"."b"                                                                                        "c_nc6t",
       "T"."s_id"                                                                                               "s_id",
       "x"."a" + "y"."b"                                                                                        "v",
       iif("s"."b" > 5, 1, 0)                                                                                   "a/m1",
       iif("s"."a" != 0, 1, 0)                                                                                  "a/m3",
       iif("s"."b" < 0, 1, 0)                                                                                   "b/m1",
       iif("s"."a" > 5, 1, 0)                                                                                   "c/m1",
       "s"."a" + "s"."b"                                                                                        "c/m2",
       iif("s"."b" > 5, 1, 0)                                                                                   "c/m3",
       "s"."c"                                                                                                  "e/m1",
       (select min("a") "min" from "DBO"."S" "S")                                                               "f/m1",
       (select min("a") "min" from "a.b"."T" "T")                                                               "g/m1",
       (select "lv"."label" "label"
        from "_platform.lookup"."LookupValue" "lv"
                 join "_platform.lookup"."Lookup" "l" on (("lv"."lookup_id" = "l"."_id") and ("l"."name" = N'City'))
        where ("lv"."code" = "i"))                                                                              "i/label",
       iif("T"."b" > 5, 1, 0)                                                                                   "a_tlry/m1",
       iif("T"."a" != 0, 1, 0)                                                                                  "a_tlry/m3",
       iif("T"."b" < 0, 1, 0)                                                                                   "b_pfpe/m1",
       iif("T"."a" > 5, 1, 0)                                                                                   "c_nc6t/m1",
       "T"."a" + "T"."b"                                                                                        "c_nc6t/m2",
       iif("T"."b" > 5, 1, 0)                                                                                   "c_nc6t/m3",
       "x"."a"                                                                                                  "v/m1",
       iif("y"."b" >= 5, 1, 0)                                                                                  "v/m2"
from "DBO"."S" "s"
         left join "a.b"."T" "T" on ("s"."_id" = "T"."s_id")
         join "a.b"."X" "x" on ("x"."t_id" = "T"."_id")
         cross join "b"."Y" "y"
order by "s"."a" desc, "y"."b", "T"."b" asc


select "/name"        "/name",
       "/tm2"         "/tm2",
       "/tm2/e"       "/tm2/e",
       "/description" "/description",
       "/tm1"         "/tm1",
       "/tm1/e"       "/tm1/e",
       "x"."a"        "a",
       "x"."c"        "c",
       "a/sequence"   "a/sequence",
       "a/m1"         "a/m1",
       "a/m1/e"       "a/m1/e",
       "a/m2"         "a/m2",
       "a/m3"         "a/m3",
       "a/m3/e"       "a/m3/e",
       "a/id"         "a/id",
       "a/type"       "a/type",
       "a/required"   "a/required",
       "c/sequence"   "c/sequence",
       "c/m1"         "c/m1",
       "c/m1/e"       "c/m1/e",
       "c/e"          "c/e",
       "c/m2"         "c/m2",
       "c/m2/e"       "c/m2/e",
       "c/m3"         "c/m3",
       "c/m3/e"       "c/m3/e",
       "c/id"         "c/id",
       "c/type"       "c/type",
       "c/derived"    "c/derived",
       "c/required"   "c/required"
from (select "/name"        "/name",
             "/tm2"         "/tm2",
             "/tm2/e"       "/tm2/e",
             "/description" "/description",
             "/tm1"         "/tm1",
             "/tm1/e"       "/tm1/e",
             "x"."a"        "a",
             "x"."c"        "c",
             "a/sequence"   "a/sequence",
             "a/m1"         "a/m1",
             "a/m1/e"       "a/m1/e",
             "a/m2"         "a/m2",
             "a/m3"         "a/m3",
             "a/m3/e"       "a/m3/e",
             "a/id"         "a/id",
             "a/type"       "a/type",
             "a/required"   "a/required",
             "c/sequence"   "c/sequence",
             "c/m1"         "c/m1",
             "c/m1/e"       "c/m1/e",
             "c/e"          "c/e",
             "c/m2"         "c/m2",
             "c/m2/e"       "c/m2/e",
             "c/m3"         "c/m3",
             "c/m3/e"       "c/m3/e",
             "c/id"         "c/id",
             "c/type"       "c/type",
             "c/derived"    "c/derived",
             "c/required"   "c/required"
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
                   '8d2ee719-ace2-40bd-872f-ee3ce44575c2'::uuid  "a/id",
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
                   '7f49aaed-3fdb-4d94-a523-ed35cd3dcdac'::uuid  "c/id",
                   'int'                                         "c/type",
                   true                                          "c/derived",
                   false                                         "c/required"
            from "public"."S" "s"
            order by "s"."a" asc) "x"
      order by "x"."a" asc) "x"