select "his"."change_at"  "change_at",
       "his"."change"     "change",
       "his"."change_by"  "change_by",
       "his"."_id"        "_id",
       "his"."identifier" "identifier",
       "his"."column"     "column",
       "his"."from_value" "from_value",
       "his"."to_value"   "to_value"
from (select "cf"."history_change_time"  "change_at",
             "cf"."history_change_event" "change",
             "cf"."history_change_user"  "change_by",
             "cf"."_id"                  "_id",
             "cf"."_id"                  "identifier",
             '000. A'                    "column",
             null                        "from_value",
             "cf"."a"                    "to_value"
      from "public"."S$history" "cf"
      where ('2023-01-01' <= "cf"."history_change_time" and "cf"."history_change_time" <= '2030-01-01')
        and "cf"."history_change_event" = 'I'
        and "cf"."a" is not null
      union all
      select "cf"."history_change_time"  "change_at",
             "cf"."history_change_event" "change",
             "cf"."history_change_user"  "change_by",
             "cf"."_id"                  "_id",
             "cf"."_id"                  "identifier",
             '000. A'                    "column",
             "cf"."a"                    "from_value",
             null                        "to_value"
      from "public"."S$history" "cf"
      where ('2023-01-01' <= "cf"."history_change_time" and "cf"."history_change_time" <= '2030-01-01')
        and "cf"."history_change_event" = 'D'
        and "cf"."a" is not null
      union all
      select "cf"."history_change_time"  "change_at",
             "cf"."history_change_event" "change",
             "cf"."history_change_user"  "change_by",
             "cf"."_id"                  "_id",
             "cf"."_id"                  "identifier",
             '000. A'                    "column",
             "cf"."a"                    "from_value",
             "ct"."a"                    "to_value"
      from "public"."S$history" "cf"
             join "public"."S$history" "ct"
                  on "cf"."_id" = "ct"."_id" and "cf"."history_change_trans_id" = "ct"."history_change_trans_id"
      where ('2023-01-01' <= "cf"."history_change_time" and "cf"."history_change_time" <= '2030-01-01')
        and "cf"."history_change_event" = 'F'
        and "ct"."history_change_event" = 'T'
        and "cf"."a" != "ct"."a"
      union all
      select "cf"."history_change_time"  "change_at",
             "cf"."history_change_event" "change",
             "cf"."history_change_user"  "change_by",
             "cf"."_id"                  "_id",
             "cf"."_id"                  "identifier",
             '001. B'                    "column",
             null                        "from_value",
             "cf"."b"                    "to_value"
      from "public"."S$history" "cf"
      where ('2023-01-01' <= "cf"."history_change_time" and "cf"."history_change_time" <= '2030-01-01')
        and "cf"."history_change_event" = 'I'
        and "cf"."b" is not null
      union all
      select "cf"."history_change_time"  "change_at",
             "cf"."history_change_event" "change",
             "cf"."history_change_user"  "change_by",
             "cf"."_id"                  "_id",
             "cf"."_id"                  "identifier",
             '001. B'                    "column",
             "cf"."b"                    "from_value",
             null                        "to_value"
      from "public"."S$history" "cf"
      where ('2023-01-01' <= "cf"."history_change_time" and "cf"."history_change_time" <= '2030-01-01')
        and "cf"."history_change_event" = 'D'
        and "cf"."b" is not null
      union all
      select "cf"."history_change_time"  "change_at",
             "cf"."history_change_event" "change",
             "cf"."history_change_user"  "change_by",
             "cf"."_id"                  "_id",
             "cf"."_id"                  "identifier",
             '001. B'                    "column",
             "cf"."b"                    "from_value",
             "ct"."b"                    "to_value"
      from "public"."S$history" "cf"
             join "public"."S$history" "ct"
                  on "cf"."_id" = "ct"."_id" and "cf"."history_change_trans_id" = "ct"."history_change_trans_id"
      where ('2023-01-01' <= "cf"."history_change_time" and "cf"."history_change_time" <= '2030-01-01')
        and "cf"."history_change_event" = 'F'
        and "ct"."history_change_event" = 'T'
        and "cf"."b" != "ct"."b"
      union all
      select "cf"."history_change_time"  "change_at",
             "cf"."history_change_event" "change",
             "cf"."history_change_user"  "change_by",
             "cf"."_id"                  "_id",
             "cf"."_id"                  "identifier",
             '002. E'                    "column",
             null                        "from_value",
             "cf"."e"                    "to_value"
      from "public"."S$history" "cf"
      where ('2023-01-01' <= "cf"."history_change_time" and "cf"."history_change_time" <= '2030-01-01')
        and "cf"."history_change_event" = 'I'
        and "cf"."e" is not null
      union all
      select "cf"."history_change_time"  "change_at",
             "cf"."history_change_event" "change",
             "cf"."history_change_user"  "change_by",
             "cf"."_id"                  "_id",
             "cf"."_id"                  "identifier",
             '002. E'                    "column",
             "cf"."e"                    "from_value",
             null                        "to_value"
      from "public"."S$history" "cf"
      where ('2023-01-01' <= "cf"."history_change_time" and "cf"."history_change_time" <= '2030-01-01')
        and "cf"."history_change_event" = 'D'
        and "cf"."e" is not null
      union all
      select "cf"."history_change_time"  "change_at",
             "cf"."history_change_event" "change",
             "cf"."history_change_user"  "change_by",
             "cf"."_id"                  "_id",
             "cf"."_id"                  "identifier",
             '002. E'                    "column",
             "cf"."e"                    "from_value",
             "ct"."e"                    "to_value"
      from "public"."S$history" "cf"
             join "public"."S$history" "ct"
                  on "cf"."_id" = "ct"."_id" and "cf"."history_change_trans_id" = "ct"."history_change_trans_id"
      where ('2023-01-01' <= "cf"."history_change_time" and "cf"."history_change_time" <= '2030-01-01')
        and "cf"."history_change_event" = 'F'
        and "ct"."history_change_event" = 'T'
        and "cf"."e" != "ct"."e"
      union all
      select "cf"."history_change_time"  "change_at",
             "cf"."history_change_event" "change",
             "cf"."history_change_user"  "change_by",
             "cf"."_id"                  "_id",
             "cf"."_id"                  "identifier",
             '003. H'                    "column",
             null                        "from_value",
             "cf"."h"                    "to_value"
      from "public"."S$history" "cf"
      where ('2023-01-01' <= "cf"."history_change_time" and "cf"."history_change_time" <= '2030-01-01')
        and "cf"."history_change_event" = 'I'
        and "cf"."h" is not null
      union all
      select "cf"."history_change_time"  "change_at",
             "cf"."history_change_event" "change",
             "cf"."history_change_user"  "change_by",
             "cf"."_id"                  "_id",
             "cf"."_id"                  "identifier",
             '003. H'                    "column",
             "cf"."h"                    "from_value",
             null                        "to_value"
      from "public"."S$history" "cf"
      where ('2023-01-01' <= "cf"."history_change_time" and "cf"."history_change_time" <= '2030-01-01')
        and "cf"."history_change_event" = 'D'
        and "cf"."h" is not null
      union all
      select "cf"."history_change_time"  "change_at",
             "cf"."history_change_event" "change",
             "cf"."history_change_user"  "change_by",
             "cf"."_id"                  "_id",
             "cf"."_id"                  "identifier",
             '003. H'                    "column",
             "cf"."h"                    "from_value",
             "ct"."h"                    "to_value"
      from "public"."S$history" "cf"
             join "public"."S$history" "ct"
                  on "cf"."_id" = "ct"."_id" and "cf"."history_change_trans_id" = "ct"."history_change_trans_id"
      where ('2023-01-01' <= "cf"."history_change_time" and "cf"."history_change_time" <= '2030-01-01')
        and "cf"."history_change_event" = 'F'
        and "ct"."history_change_event" = 'T'
        and "cf"."h" != "ct"."h"
      union all
      select "cf"."history_change_time"  "change_at",
             "cf"."history_change_event" "change",
             "cf"."history_change_user"  "change_by",
             "cf"."_id"                  "_id",
             "cf"."_id"                  "identifier",
             '004. I'                    "column",
             null                        "from_value",
             "cf"."i"                    "to_value"
      from "public"."S$history" "cf"
      where ('2023-01-01' <= "cf"."history_change_time" and "cf"."history_change_time" <= '2030-01-01')
        and "cf"."history_change_event" = 'I'
        and "cf"."i" is not null
      union all
      select "cf"."history_change_time"  "change_at",
             "cf"."history_change_event" "change",
             "cf"."history_change_user"  "change_by",
             "cf"."_id"                  "_id",
             "cf"."_id"                  "identifier",
             '004. I'                    "column",
             "cf"."i"                    "from_value",
             null                        "to_value"
      from "public"."S$history" "cf"
      where ('2023-01-01' <= "cf"."history_change_time" and "cf"."history_change_time" <= '2030-01-01')
        and "cf"."history_change_event" = 'D'
        and "cf"."i" is not null
      union all
      select "cf"."history_change_time"  "change_at",
             "cf"."history_change_event" "change",
             "cf"."history_change_user"  "change_by",
             "cf"."_id"                  "_id",
             "cf"."_id"                  "identifier",
             '004. I'                    "column",
             "cf"."i"                    "from_value",
             "ct"."i"                    "to_value"
      from "public"."S$history" "cf"
             join "public"."S$history" "ct"
                  on "cf"."_id" = "ct"."_id" and "cf"."history_change_trans_id" = "ct"."history_change_trans_id"
      where ('2023-01-01' <= "cf"."history_change_time" and "cf"."history_change_time" <= '2030-01-01')
        and "cf"."history_change_event" = 'F'
        and "ct"."history_change_event" = 'T'
        and "cf"."i" != "ct"."i"
      union all
      select "cf"."history_change_time"  "change_at",
             "cf"."history_change_event" "change",
             "cf"."history_change_user"  "change_by",
             "cf"."_id"                  "_id",
             "cf"."_id"                  "identifier",
             '005. J'                    "column",
             null                        "from_value",
             "cf"."j"                    "to_value"
      from "public"."S$history" "cf"
      where ('2023-01-01' <= "cf"."history_change_time" and "cf"."history_change_time" <= '2030-01-01')
        and "cf"."history_change_event" = 'I'
        and "cf"."j" is not null
      union all
      select "cf"."history_change_time"  "change_at",
             "cf"."history_change_event" "change",
             "cf"."history_change_user"  "change_by",
             "cf"."_id"                  "_id",
             "cf"."_id"                  "identifier",
             '005. J'                    "column",
             "cf"."j"                    "from_value",
             null                        "to_value"
      from "public"."S$history" "cf"
      where ('2023-01-01' <= "cf"."history_change_time" and "cf"."history_change_time" <= '2030-01-01')
        and "cf"."history_change_event" = 'D'
        and "cf"."j" is not null
      union all
      select "cf"."history_change_time"  "change_at",
             "cf"."history_change_event" "change",
             "cf"."history_change_user"  "change_by",
             "cf"."_id"                  "_id",
             "cf"."_id"                  "identifier",
             '005. J'                    "column",
             "cf"."j"                    "from_value",
             "ct"."j"                    "to_value"
      from "public"."S$history" "cf"
             join "public"."S$history" "ct"
                  on "cf"."_id" = "ct"."_id" and "cf"."history_change_trans_id" = "ct"."history_change_trans_id"
      where ('2023-01-01' <= "cf"."history_change_time" and "cf"."history_change_time" <= '2030-01-01')
        and "cf"."history_change_event" = 'F'
        and "ct"."history_change_event" = 'T'
        and "cf"."j" != "ct"."j"
      union all
      select "cf"."history_change_time"  "change_at",
             "cf"."history_change_event" "change",
             "cf"."history_change_user"  "change_by",
             "cf"."_id"                  "_id",
             "cf"."_id"                  "identifier",
             '006. K'                    "column",
             null                        "from_value",
             "cf"."k"                    "to_value"
      from "public"."S$history" "cf"
      where ('2023-01-01' <= "cf"."history_change_time" and "cf"."history_change_time" <= '2030-01-01')
        and "cf"."history_change_event" = 'I'
        and "cf"."k" is not null
      union all
      select "cf"."history_change_time"  "change_at",
             "cf"."history_change_event" "change",
             "cf"."history_change_user"  "change_by",
             "cf"."_id"                  "_id",
             "cf"."_id"                  "identifier",
             '006. K'                    "column",
             "cf"."k"                    "from_value",
             null                        "to_value"
      from "public"."S$history" "cf"
      where ('2023-01-01' <= "cf"."history_change_time" and "cf"."history_change_time" <= '2030-01-01')
        and "cf"."history_change_event" = 'D'
        and "cf"."k" is not null
      union all
      select "cf"."history_change_time"  "change_at",
             "cf"."history_change_event" "change",
             "cf"."history_change_user"  "change_by",
             "cf"."_id"                  "_id",
             "cf"."_id"                  "identifier",
             '006. K'                    "column",
             "cf"."k"                    "from_value",
             "ct"."k"                    "to_value"
      from "public"."S$history" "cf"
             join "public"."S$history" "ct"
                  on "cf"."_id" = "ct"."_id" and "cf"."history_change_trans_id" = "ct"."history_change_trans_id"
      where ('2023-01-01' <= "cf"."history_change_time" and "cf"."history_change_time" <= '2030-01-01')
        and "cf"."history_change_event" = 'F'
        and "ct"."history_change_event" = 'T'
        and "cf"."k" != "ct"."k"
      union all
      select "cf"."history_change_time"  "change_at",
             "cf"."history_change_event" "change",
             "cf"."history_change_user"  "change_by",
             "cf"."_id"                  "_id",
             "cf"."_id"                  "identifier",
             '007. L'                    "column",
             null                        "from_value",
             "cf"."l"                    "to_value"
      from "public"."S$history" "cf"
      where ('2023-01-01' <= "cf"."history_change_time" and "cf"."history_change_time" <= '2030-01-01')
        and "cf"."history_change_event" = 'I'
        and "cf"."l" is not null
      union all
      select "cf"."history_change_time"  "change_at",
             "cf"."history_change_event" "change",
             "cf"."history_change_user"  "change_by",
             "cf"."_id"                  "_id",
             "cf"."_id"                  "identifier",
             '007. L'                    "column",
             "cf"."l"                    "from_value",
             null                        "to_value"
      from "public"."S$history" "cf"
      where ('2023-01-01' <= "cf"."history_change_time" and "cf"."history_change_time" <= '2030-01-01')
        and "cf"."history_change_event" = 'D'
        and "cf"."l" is not null
      union all
      select "cf"."history_change_time"  "change_at",
             "cf"."history_change_event" "change",
             "cf"."history_change_user"  "change_by",
             "cf"."_id"                  "_id",
             "cf"."_id"                  "identifier",
             '007. L'                    "column",
             "cf"."l"                    "from_value",
             "ct"."l"                    "to_value"
      from "public"."S$history" "cf"
             join "public"."S$history" "ct"
                  on "cf"."_id" = "ct"."_id" and "cf"."history_change_trans_id" = "ct"."history_change_trans_id"
      where ('2023-01-01' <= "cf"."history_change_time" and "cf"."history_change_time" <= '2030-01-01')
        and "cf"."history_change_event" = 'F'
        and "ct"."history_change_event" = 'T'
        and "cf"."l" != "ct"."l"
      union all
      select "cf"."history_change_time"  "change_at",
             "cf"."history_change_event" "change",
             "cf"."history_change_user"  "change_by",
             "cf"."_id"                  "_id",
             "cf"."_id"                  "identifier",
             '008. M'                    "column",
             null                        "from_value",
             "cf"."m"                    "to_value"
      from "public"."S$history" "cf"
      where ('2023-01-01' <= "cf"."history_change_time" and "cf"."history_change_time" <= '2030-01-01')
        and "cf"."history_change_event" = 'I'
        and "cf"."m" is not null
      union all
      select "cf"."history_change_time"  "change_at",
             "cf"."history_change_event" "change",
             "cf"."history_change_user"  "change_by",
             "cf"."_id"                  "_id",
             "cf"."_id"                  "identifier",
             '008. M'                    "column",
             "cf"."m"                    "from_value",
             null                        "to_value"
      from "public"."S$history" "cf"
      where ('2023-01-01' <= "cf"."history_change_time" and "cf"."history_change_time" <= '2030-01-01')
        and "cf"."history_change_event" = 'D'
        and "cf"."m" is not null
      union all
      select "cf"."history_change_time"  "change_at",
             "cf"."history_change_event" "change",
             "cf"."history_change_user"  "change_by",
             "cf"."_id"                  "_id",
             "cf"."_id"                  "identifier",
             '008. M'                    "column",
             "cf"."m"                    "from_value",
             "ct"."m"                    "to_value"
      from "public"."S$history" "cf"
             join "public"."S$history" "ct"
                  on "cf"."_id" = "ct"."_id" and "cf"."history_change_trans_id" = "ct"."history_change_trans_id"
      where ('2023-01-01' <= "cf"."history_change_time" and "cf"."history_change_time" <= '2030-01-01')
        and "cf"."history_change_event" = 'F'
        and "ct"."history_change_event" = 'T'
        and "cf"."m" != "ct"."m"
      union all
      select "cf"."history_change_time"  "change_at",
             "cf"."history_change_event" "change",
             "cf"."history_change_user"  "change_by",
             "cf"."_id"                  "_id",
             "cf"."_id"                  "identifier",
             '009. N'                    "column",
             null                        "from_value",
             "cf"."n"                    "to_value"
      from "public"."S$history" "cf"
      where ('2023-01-01' <= "cf"."history_change_time" and "cf"."history_change_time" <= '2030-01-01')
        and "cf"."history_change_event" = 'I'
        and "cf"."n" is not null
      union all
      select "cf"."history_change_time"  "change_at",
             "cf"."history_change_event" "change",
             "cf"."history_change_user"  "change_by",
             "cf"."_id"                  "_id",
             "cf"."_id"                  "identifier",
             '009. N'                    "column",
             "cf"."n"                    "from_value",
             null                        "to_value"
      from "public"."S$history" "cf"
      where ('2023-01-01' <= "cf"."history_change_time" and "cf"."history_change_time" <= '2030-01-01')
        and "cf"."history_change_event" = 'D'
        and "cf"."n" is not null
      union all
      select "cf"."history_change_time"  "change_at",
             "cf"."history_change_event" "change",
             "cf"."history_change_user"  "change_by",
             "cf"."_id"                  "_id",
             "cf"."_id"                  "identifier",
             '009. N'                    "column",
             "cf"."n"                    "from_value",
             "ct"."n"                    "to_value"
      from "public"."S$history" "cf"
             join "public"."S$history" "ct"
                  on "cf"."_id" = "ct"."_id" and "cf"."history_change_trans_id" = "ct"."history_change_trans_id"
      where ('2023-01-01' <= "cf"."history_change_time" and "cf"."history_change_time" <= '2030-01-01')
        and "cf"."history_change_event" = 'F'
        and "ct"."history_change_event" = 'T'
        and "cf"."n" != "ct"."n"
      union all
      select "cf"."history_change_time"  "change_at",
             "cf"."history_change_event" "change",
             "cf"."history_change_user"  "change_by",
             "cf"."_id"                  "_id",
             "cf"."_id"                  "identifier",
             '010. O'                    "column",
             null                        "from_value",
             "cf"."o"                    "to_value"
      from "public"."S$history" "cf"
      where ('2023-01-01' <= "cf"."history_change_time" and "cf"."history_change_time" <= '2030-01-01')
        and "cf"."history_change_event" = 'I'
        and "cf"."o" is not null
      union all
      select "cf"."history_change_time"  "change_at",
             "cf"."history_change_event" "change",
             "cf"."history_change_user"  "change_by",
             "cf"."_id"                  "_id",
             "cf"."_id"                  "identifier",
             '010. O'                    "column",
             "cf"."o"                    "from_value",
             null                        "to_value"
      from "public"."S$history" "cf"
      where ('2023-01-01' <= "cf"."history_change_time" and "cf"."history_change_time" <= '2030-01-01')
        and "cf"."history_change_event" = 'D'
        and "cf"."o" is not null
      union all
      select "cf"."history_change_time"  "change_at",
             "cf"."history_change_event" "change",
             "cf"."history_change_user"  "change_by",
             "cf"."_id"                  "_id",
             "cf"."_id"                  "identifier",
             '010. O'                    "column",
             "cf"."o"                    "from_value",
             "ct"."o"                    "to_value"
      from "public"."S$history" "cf"
             join "public"."S$history" "ct"
                  on "cf"."_id" = "ct"."_id" and "cf"."history_change_trans_id" = "ct"."history_change_trans_id"
      where ('2023-01-01' <= "cf"."history_change_time" and "cf"."history_change_time" <= '2030-01-01')
        and "cf"."history_change_event" = 'F'
        and "ct"."history_change_event" = 'T'
        and "cf"."o" != "ct"."o"
      union all
      select "cf"."history_change_time"  "change_at",
             "cf"."history_change_event" "change",
             "cf"."history_change_user"  "change_by",
             "cf"."_id"                  "_id",
             "cf"."_id"                  "identifier",
             '011. P'                    "column",
             null                        "from_value",
             "cf"."p"                    "to_value"
      from "public"."S$history" "cf"
      where ('2023-01-01' <= "cf"."history_change_time" and "cf"."history_change_time" <= '2030-01-01')
        and "cf"."history_change_event" = 'I'
        and "cf"."p" is not null
      union all
      select "cf"."history_change_time"  "change_at",
             "cf"."history_change_event" "change",
             "cf"."history_change_user"  "change_by",
             "cf"."_id"                  "_id",
             "cf"."_id"                  "identifier",
             '011. P'                    "column",
             "cf"."p"                    "from_value",
             null                        "to_value"
      from "public"."S$history" "cf"
      where ('2023-01-01' <= "cf"."history_change_time" and "cf"."history_change_time" <= '2030-01-01')
        and "cf"."history_change_event" = 'D'
        and "cf"."p" is not null
      union all
      select "cf"."history_change_time"  "change_at",
             "cf"."history_change_event" "change",
             "cf"."history_change_user"  "change_by",
             "cf"."_id"                  "_id",
             "cf"."_id"                  "identifier",
             '011. P'                    "column",
             "cf"."p"                    "from_value",
             "ct"."p"                    "to_value"
      from "public"."S$history" "cf"
             join "public"."S$history" "ct"
                  on "cf"."_id" = "ct"."_id" and "cf"."history_change_trans_id" = "ct"."history_change_trans_id"
      where ('2023-01-01' <= "cf"."history_change_time" and "cf"."history_change_time" <= '2030-01-01')
        and "cf"."history_change_event" = 'F'
        and "ct"."history_change_event" = 'T'
        and "cf"."p" != "ct"."p") "his"