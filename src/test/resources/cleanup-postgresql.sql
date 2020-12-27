drop table "S";
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
drop schema _core cascade;


select "_id" _id, "name" name, "relation_id" relation_id, "type" type, "check_expr" check_expr, "source_columns" source_columns, "target_relation_id" target_relation_id, "target_columns" target_columns, "forward_cost" forward_cost, "reverse_cost" reverse_cost, "on_update" on_update, "on_delete" on_delete from "_core"."constraints" "constraints"