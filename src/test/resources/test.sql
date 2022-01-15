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
order by "t"."a";



select "s"."_id"                                           "_id",
       "s"."a"                                             "a",
       "s"."b"                                             "b",
       "s"."a" + "s"."b"                                   "c",
       "s"."b" + ("s"."a" + "s"."b")                       "d",
       "s"."e"                                             "e",
       ((select max("s"."a") "col" from "public"."S" "S")) "f",
       ((select distinct ("s"."a" + "s"."b") "col"
         from "public"."S" "S"
         where ((("s"."b" + ("s"."a" + "s"."b")) > 5))))   "g",
       "s"."h"                                             "h",
       "s"."i"                                             "i",
       "s"."j"                                             "j",
       "s"."k"                                             "k",
       "s"."l"                                             "l",
       "T"."_id"                                           "_id1",
       "T"."a"                                             "a2",
       "T"."b"                                             "b3",
       "T"."a" + "T"."b"                                   "c4",
       "T"."x"                                             "x",
       "T"."y"                                             "y",
       "T"."s_id"                                          "s_id",
       "x"."a" + "y"."b"                                   "v",
       (select max("S"."b") "col" from "public"."S" "S")   "/tm1",
       ("s"."a" > "s"."b")                                 "/tm2",
       ("s"."b" > 5)                                       "a/m1",
       ("s"."a" != 0)                                      "a/m3",
       ("s"."b" < 0)                                       "b/m1",
       ("s"."a" > 5)                                       "c/m1",
       "s"."a" + "s"."b"                                   "c/m2",
       ("s"."b" > 5)                                       "c/m3",
       ("s"."a" + "s"."b")                                 "e/m1",
       (select min("S"."a") "col" from "public"."S" "S")   "f/m1",
       (select min("T"."a") "col" from "a.b"."T" "T")      "g/m1",
       ("T"."b" > 5)                                       "a2/m1",
       ("T"."a" != 0)                                      "a2/m3",
       ("T"."b" < 0)                                       "b3/m1",
       ("T"."a" > 5)                                       "c4/m1",
       "T"."a" + "T"."b"                                   "c4/m2",
       ("T"."b" > 5)                                       "c4/m3",
       ("T"."b" > 5)                                       "x/x1",
       ("T"."a" != 0)                                      "x/x2",
       ("T"."b" > 5)                                       "y/y1",
       ("T"."a" != 0)                                      "y/y2"
from "public"."S" "s"
       left join "a.b"."T" "T" on ("s"."_id" = "T"."s_id")
       join "a.b"."X" "x" on ("x"."t_id" = "T"."_id")
       cross join "b"."Y" "y"
where (left("s"."i", 2) = 'PI')
order by "s"."a" desc, "y"."b", "T"."b" asc