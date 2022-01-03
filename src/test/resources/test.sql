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
order by "t"."a"