with "s"("id", "a", "b", "c") as (select "S"."_id" "id", "S"."a" "a", "S"."b" "b", "S"."a" + "S"."b" "c"
                                  from "S" "S"
                                  order by "a")
select 'T'                                                          "/name",
       ("t"."a" > "t"."b")                                          "/tm2",
       '(t.a > t.b)'                                                "/tm2/e",
       'T test table'                                               "/description",
       (select max("b") "__auto_col_ff5nf9j26z" from "a.b"."T" "T") "/tm1",
       '(from T:a.b.T select __auto_col_ff5nf9j26z:max(b))'         "/tm1/e",
       "t"."a"                                                      "a",
       "t"."b"                                                      "b",
       "s"."c"                                                      "c",
       2                                                            "a/sequence",
       ("t"."b" > 5)                                                "a/m1",
       '(t.b > 5)'                                                  "a/m1/e",
       10                                                           "a/m2",
       ("t"."a" != 0)                                               "a/m3",
       '(t.a != 0)'                                                 "a/m3/e",
       '9db8e30c-de80-4b48-b235-ce71c24f8508'::uuid                 "a/id",
       'int'                                                        "a/type",
       false                                                        "a/required",
       3                                                            "b/sequence",
       ("t"."b" < 0)                                                "b/m1",
       '(t.b < 0)'                                                  "b/m1/e",
       '1d2e1000-7ab7-47b2-8d40-c18c0ac2691b'::uuid                 "b/id",
       'int'                                                        "b/type",
       false                                                        "b/required"
from "a.b"."T" "t"
         join "s" "s" on ("t"."s_id" = "s"."id")
order by "t"."a"