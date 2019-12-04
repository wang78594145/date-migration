-- datasource_item
CREATE TABLE "public"."datasource_item" (
"id" int4 DEFAULT nextval('datasource_item_id_seq'::regclass) NOT NULL,
"ds_id" int4 NOT NULL,
"name" text COLLATE "default",
"comment" text COLLATE "default",
CONSTRAINT "datasource_item_pkey" PRIMARY KEY ("id"),
CONSTRAINT "datasource_item_unique_name" UNIQUE ("name")
)
WITH (OIDS=FALSE)
;

ALTER TABLE "public"."datasource_item" OWNER TO "postgres";


-- tasks 表

CREATE TABLE "public"."task" (
"id" serial PRIMARY KEY,
"name" varchar(20) COLLATE "default",
"source_type" smallint ,
"source_address" varchar(50),
"source_tablename" varchar(50),
"create_time" timestamp DEFAULT now(),
"finish_time" timestamp,
"output_format" smallint ,
"output_path" varchar(50) COLLATE "default",
"lat_begin" float8,
"lat_end" float8,
"lon_begin" float8,
"lon_end" float8,
"layer_min" int2,
"layer_max" int2,
"task_total" int4,
"task_done" int4,
"progress" int2,
"status" varchar(20) COLLATE "default",
"comments" varchar(100) COLLATE "default"
)
WITH (OIDS=FALSE)
;

ALTER TABLE "public"."task" OWNER TO "postgres";



-- task item表

CREATE TABLE "public"."task_item" (
"id" serial PRIMARY KEY,
"task_id" int4,
"name" varchar(500) COLLATE "default",
"status" varchar(20) COLLATE "default",
UNIQUE(name)
)
WITH (OIDS=FALSE)
;

ALTER TABLE "public"."task_item" OWNER TO "postgres";

