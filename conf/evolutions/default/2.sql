# --- !Ups


create table "APP_INSTANCE" ("INSTANCE_ID" varchar(40) NOT NULL PRIMARY KEY,"GROUP_ID" varchar(100) NOT NULL,"JOB_ID" varchar(100) NOT NULL,"TRIGGER_ID" VARCHAR(100),"START_TIME" TIMESTAMP NOT NULL,"END_TIME" TIMESTAMP,"MESSAGE" VARCHAR(200),"RETURN_CODE" INTEGER,"SEQ_ID" BIGINT NOT NULL,"STATUS_ID" INTEGER NOT NULL,"ATTEMPT" INTEGER NOT NULL,"AGENT_NAME" varchar(100) NOT NULL);
create unique index "instance_id_app_instance_unq_key" on "APP_INSTANCE" ("INSTANCE_ID");
create table "APP_STATUS" ("ID" INTEGER NOT NULL PRIMARY KEY,"STATUS" varchar(40) NOT NULL);
create table "APP_INSTANCE_LOG" ("INSTANCE_ID" varchar(40) NOT NULL,"LOG_TYPE" varchar(10) NOT NULL,"LOG_DATA" TEXT);
create index "instance_id_app_instance_log_unq_key" on "APP_INSTANCE_LOG" ("INSTANCE_ID");
create table "APP_GROUP" ("ID" VARCHAR(40) NOT NULL,"GROUP_NAME" VARCHAR(30) NOT NULL,"GROUP_EMAIL" VARCHAR(300) NOT NULL,"DESCRIPTION" VARCHAR(200));
create unique index "group_name_app_group_unq_key" on "APP_GROUP" ("GROUP_NAME");


INSERT INTO "APP_STATUS" VALUES (1, 'running');
INSERT INTO "APP_STATUS" VALUES (2, 'succeeded');
INSERT INTO "APP_STATUS" VALUES (3, 'failed');
INSERT INTO "APP_STATUS" VALUES (4, 'error');
INSERT INTO "APP_STATUS" VALUES (5, 'finished');


INSERT INTO "APP_GROUP" VALUES ('c415ca8db84044f8a43dda999caa1fb6', 'test_group', 'chlr@groupon.com', null);

# --- !Downs

drop table "APP_INSTANCE";
drop table "APP_STATUS";
drop table "APP_INSTANCE_LOG";
drop table "APP_GROUP";