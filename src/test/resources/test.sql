with "s"("id", "a", "b", "c") as (select "S"."_id" "id", "S"."a" "a", "S"."b" "b", ("S"."a" + "S"."b") "c"
                                  from "public"."S" "S"
                                  order by "S"."a")
select "t"."a"        "a",
       "t"."b"        "b",
       "s"."c"        "c",
       "s"."b"        "x",
       2              "a/sequence",
       ("t"."b" > 5)  "a/m1",
       '(t.b > 5)'    "a/m1/e",
       10             "a/m2",
       ("t"."a" != 0) "a/m3",
       '(t.a != 0)'   "a/m3/e",
       'int'          "a/type",
       false          "a/required",
       3              "b/sequence",
       ("t"."b" < 0)  "b/m1",
       '(t.b < 0)'    "b/m1/e",
       'int'          "b/type",
       false          "b/required"
from "a.b"."T" "t"
       join "s" "s" on ("t"."s_id" = "s"."id")
order by "t"."a";



with "s"("id", "a", "b", "c") as (select "S"."_id" "id", "S"."a" "a", "S"."b" "b", ("S"."a" + "S"."b") "c"
                                  from "public"."S" "S"
                                  order by "S"."a")
select "t"."a"                                            "a",
       "t"."b"                                            "b",
       "s"."c"                                            "c",
       "s"."b"                                            "x",
       (select max("T"."b") "column1" from "a.b"."T" "T") "/tm1",
       ("t"."a" > "t"."b")                                "/tm2",
       ("t"."b" > 5)                                      "a/m1",
       ("t"."a" != 0)                                     "a/m3",
       ("t"."b" < 0)                                      "b/m1"
from "a.b"."T" "t"
       join "s" "s" on ("t"."s_id" = "s"."id")
order by "t"."a";


with "s"("id", "a", "b", "c") as (select "S"."_id" "id", "S"."a" "a", "S"."b" "b", ("S"."a" + "S"."b") "c"
                                  from "DBO"."S" "S"
                                  order by "S"."a"
                                  offset 0 rows)
select "t"."a"                                            "a",
       "t"."b"                                            "b",
       "s"."c"                                            "c",
       "s"."b"                                            "x",
       (select max("T"."b") "column1" from "a.b"."T" "T") "/tm1",
       iif("t"."a" > "t"."b", 1, 0)                       "/tm2",
       iif("t"."b" > 5, 1, 0)                             "a/m1",
       iif("t"."a" != 0, 1, 0)                            "a/m3",
       iif("t"."b" < 0, 1, 0)                             "b/m1"
from "a.b"."T" "t"
       join "s" "s" on ("t"."s_id" = "s"."id")
order by "t"."a"