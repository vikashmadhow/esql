select "x"."_id"              "_id",
       "x"."a"                "a",
       "x"."b"                "b",
       "x"."c"                "c",
       "x"."d"                "d",
       "x"."e"                "e",
       "x"."f"                "f",
       "x"."g"                "g",
       "x"."h"                "h",
       "x"."i"                "i",
       "x"."j"                "j",
       "x"."k"                "k",
       "x"."l"                "l",
       "x"."/name"            "/name",
       "x"."/description"     "/description",
       "x"."/tm1"             "/tm1",
       "x"."/tm1/e"           "/tm1/e",
       "x"."/tm2"             "/tm2",
       "x"."/tm2/e"           "/tm2/e",
       "x"."/validate_unique" "/validate_unique",
       "x"."/dependents"      "/dependents",
       "x"."_id/type"         "_id/type",
       "x"."_id/required"     "_id/required",
       "x"."a/m1"             "a/m1",
       "x"."a/m1/e"           "a/m1/e",
       "x"."a/type"           "a/type",
       "x"."a/m2"             "a/m2",
       "x"."a/m3"             "a/m3",
       "x"."a/m3/e"           "a/m3/e",
       "x"."b/m1"             "b/m1",
       "x"."b/m1/e"           "b/m1/e",
       "x"."b/type"           "b/type",
       "x"."c/e"              "c/e",
       "x"."c/m1"             "c/m1",
       "x"."c/m1/e"           "c/m1/e",
       "x"."c/type"           "c/type",
       "x"."c/m2"             "c/m2",
       "x"."c/m2/e"           "c/m2/e",
       "x"."c/derived"        "c/derived",
       "x"."c/m3"             "c/m3",
       "x"."c/m3/e"           "c/m3/e",
       "x"."d/e"              "d/e",
       "x"."d/m1"             "d/m1",
       "x"."d/type"           "d/type",
       "x"."d/derived"        "d/derived",
       "x"."e/m1"             "e/m1",
       "x"."e/m1/e"           "e/m1/e",
       "x"."e/type"           "e/type",
       "x"."f/e"              "f/e",
       "x"."f/m1"             "f/m1",
       "x"."f/m1/e"           "f/m1/e",
       "x"."f/derived"        "f/derived",
       "x"."g/e"              "g/e",
       "x"."g/m1"             "g/m1",
       "x"."g/m1/e"           "g/m1/e",
       "x"."g/derived"        "g/derived",
       "x"."h/m1"             "h/m1",
       "x"."h/type"           "h/type",
       "x"."i/type"           "i/type",
       "x"."i/expression"     "i/expression",
       "x"."j/type"           "j/type",
       "x"."k/type"           "k/type",
       "x"."l/type"           "l/type"
from (select "S"."_id"                                                                                     "_id",
             "S"."a"                                                                                       "a",
             "S"."b"                                                                                       "b",
             "S"."a" + "S"."b"                                                                             "c",
             "S"."b" + ("S"."a" + "S"."b")                                                                 "d",
             "S"."e"                                                                                       "e",
             (select max("a") "col" from "public"."S" "S")                                                 "f",
             (select distinct "c" "col" from "public"."S" "S" where (("S"."b" + ("S"."a" + "S"."b")) > 5)) "g",
             "S"."h"                                                                                       "h",
             "S"."i"                                                                                       "i",
             "S"."j"                                                                                       "j",
             "S"."k"                                                                                       "k",
             "S"."l"                                                                                       "l",
             'S'                                                                                           "/name",
             'S test table'                                                                                "/description",
             (select max("S"."b") "col" from "public"."S" "S")                                             "/tm1",
             '(from S:S select col:max(S.b))'                                                              "/tm1/e",
             ("S"."a" > "S"."b")                                                                           "/tm2",
             '(S.a > S.b)'                                                                                 "/tm2/e",
             '[array[''a'',''b'',''e'']::text[]]'                                                          "/validate_unique",
             '{links: {type: ''a.b.T'',_referred_by: ''s_id'',label: ''S Links''}}'                         "/dependents",
             'uuid'                                                                                        "_id/type",
             true                                                                                          "_id/required",
             ("S"."b" > 5)                                                                                 "a/m1",
             '(S.b > 5)'                                                                                   "a/m1/e",
             'int'                                                                                         "a/type",
             10                                                                                            "a/m2",
             ("S"."a" != 0)                                                                                "a/m3",
             '(S.a != 0)'                                                                                  "a/m3/e",
             ("S"."b" < 0)                                                                                 "b/m1",
             '(S.b < 0)'                                                                                   "b/m1/e",
             'int'                                                                                         "b/type",
             'S.a + S.b'                                                                                   "c/e",
             ("S"."a" > 5)                                                                                 "c/m1",
             '(S.a > 5)'                                                                                   "c/m1/e",
             'int'                                                                                         "c/type",
             "S"."a" + "S"."b"                                                                             "c/m2",
             'S.a + S.b'                                                                                   "c/m2/e",
             true                                                                                          "c/derived",
             ("S"."b" > 5)                                                                                 "c/m3",
             '(S.b > 5)'                                                                                   "c/m3/e",
             'S.b + (S.a + S.b)'                                                                           "d/e",
             10                                                                                            "d/m1",
             'int'                                                                                         "d/type",
             true                                                                                          "d/derived",
             ("S"."a" + "S"."b")                                                                           "e/m1",
             '(S.a + S.b)'                                                                                 "e/m1/e",
             'bool'                                                                                        "e/type",
             '(from S:S select col:max(S.a))'                                                              "f/e",
             (select min("S"."a") "col" from "public"."S" "S")                                             "f/m1",
             '(from S:S select col:min(S.a))'                                                              "f/m1/e",
             true                                                                                          "f/derived",
             '(from S:S select distinct col:(S.a + S.b) where ((S.b + (S.a + S.b)) > 5))'                  "g/e",
             (select min("T"."a") "col" from "a.b"."T" "T")                                                "g/m1",
             '(from T:a.b.T select col:min(T.a))'                                                          "g/m1/e",
             true                                                                                          "g/derived",
             5                                                                                             "h/m1",
             '[]text'                                                                                      "h/type",
             'string'                                                                                      "i/type",
             'Aie'                                                                                         "i/expression",
             '[]int'                                                                                       "j/type",
             'interval'                                                                                    "k/type",
             'int'                                                                                         "l/type"
      from "public"."S" "S") "x"
where ("x"."a" >= 3)
order by "x"."a";



select "S"."_id"                                           "_id",
       "S"."a"                                             "a",
       "S"."b"                                             "b",
       "S"."a" + "S"."b"                                   "c",
       "S"."b" + ("S"."a" + "S"."b")                       "d",
       "S"."e"                                             "e",
       ((select max("S"."a") "col" from "public"."S" "S")) "f",
       ((select distinct ("S"."a" + "S"."b") "col"
         from "public"."S" "S"
         where ((("S"."b" + ("S"."a" + "S"."b")) > 5))))   "g",
       "S"."h"                                             "h",
       "S"."i"                                             "i",
       "S"."j"                                             "j",
       "S"."k"                                             "k",
       "S"."l"                                             "l",
       (select max("S"."b") "col" from "public"."S" "S")   "/tm1",
       ("S"."a" > "S"."b")                                 "/tm2",
       ("S"."b" > 5)                                       "a/m1",
       ("S"."a" != 0)                                      "a/m3",
       ("S"."b" < 0)                                       "b/m1",
       ("S"."a" > 5)                                       "c/m1",
       "S"."a" + "S"."b"                                   "c/m2",
       ("S"."b" > 5)                                       "c/m3",
       ("S"."a" + "S"."b")                                 "e/m1",
       (select min("S"."a") "col" from "public"."S" "S")   "f/m1",
       (select min("T"."a") "col" from "a.b"."T" "T")      "g/m1"
from "public"."S" "S";



select "z"."_id"           "_id",
       "z"."a"             "a",
       "z"."y_id"          "y_id",
       (select distinct min("pA"."a") "column1"
        from "test"."pA" "pA"
               join "test"."pY" "pY" on "pA"."_id" = "pY"."a_id"
               join "test"."pB" "pB" on "pY"."b_id" = "pB"."_id"
               join "test"."pX" "x" on "pB"."x_id" = "x"."_id"
        where "x"."a" = 3) "column1"
from "test"."pZ" "z"
       cross join (values (1, 2), (3, 4), (4, 5)) as "t1"("a", "b")
       join (select "z1"."a" "a", 'int' "a/type"
             from "test"."pZ" "z1"
                    join "test"."pY" "pY" on "z1"."y_id" = "pY"."_id"
                    join "test"."pB" "pB" on "pY"."b_id" = "pB"."_id"
                    join "test"."pX" "x" on "pB"."x_id" = "x"."_id"
             where "x"."a" = 3) "s1" on "s1"."a" = "z"."a"
       join "test"."pY" "y" on "z"."y_id" = "y"."_id"
       full join "test"."pC" "c" on "z"."a" = "c"."a"
       join "test"."pX" "x" on "c"."x_id" = "x"."_id"
where "z"."a" < 2
  and exists(select "pC"."_id" "_id", 'uuid' "_id/type", true "_id/required", true "_id/_primary_key"
             from "test"."pC" "pC"
                    join "test"."pX" "x" on "pC"."x_id" = "x"."_id"
                    join "test"."pY" "pY" on "pC"."a" = "pY"."a"
             where "x"."a" = 3)
  and "x"."a" = 3



with "!!"(id, v1, v2) as (select "z".ctid, "c"."a", "y"."_id"
                          from "test"."pZ" "z"
                                 join "test"."pY" "y" on "z"."y_id" = "y"."_id"
                                 join "test"."pC" "c" on "z"."a" = "c"."a"
                                 join "test"."pX" "x" on "c"."x_id" = "x"."_id"
                          where "z"."a" < 2
                            and "x"."a" = 3)
update "test"."pZ" "z"
set a="!!".v1,
    _id="!!".v2
from "!!"
where "z".ctid = "!!".id

with "!!"(id, v1, v2) as (select "z".ctid, "c"."a", "y"."_id"
                          from "test"."pZ" "z"
                                 join "test"."pY" "y" on "z"."y_id" = "y"."_id"
                                 join "test"."pB" "pB" on "y"."b_id" = "pB"."_id"
                                 join "test"."pX" "x" on "pB"."x_id" = "x"."_id"
                                 left join "test"."pC" "c" on "z"."a" = "c"."a"
                          where "z"."a" < 2
                            and "x"."a" = 3)
update "test"."pZ" "z"
set a="!!".v1,
    _id="!!".v2
from "!!"
where "z".ctid = "!!".id;



delete
  from "test"."pZ" "z"
 using "test"."pY" "y",
       "test"."pC" "c",
       "test"."pX" "x"
 where ("z"."a" < 2 and "x"."a" = 3)
   and ("z"."y_id" = "y"."_id")
   and "z"."a" = "c"."a"
   and "c"."x_id" = "x"."_id";


with "!!"(id) as (select "z".ctid
                  from "test"."pZ" "z"
                         join "test"."pY" "y" on "z"."y_id" = "y"."_id"
                         join "test"."pC" "c" on "z"."a" = "c"."a"
                         join "test"."pX" "x" on "c"."x_id" = "x"."_id"
                  where "z"."a" < 2
                    and "x"."a" = 3)
delete
from "test"."pZ" "z" using "!!"
where "z".ctid = "!!".id;


with "!!"(id) as (select "z".ctid
                  from "test"."pZ" "z"
                         join "test"."pY" "y" on "z"."y_id" = "y"."_id"
                         join "test"."pB" "pB" on "y"."b_id" = "pB"."_id"
                         join "test"."pX" "x" on "pB"."x_id" = "x"."_id"
                         left join "test"."pC" "c" on "z"."a" = "c"."a"
                  where "z"."a" < 2
                    and "x"."a" = 3)
delete
from "test"."pZ" "z" using "!!"
where "z".ctid = "!!".id;