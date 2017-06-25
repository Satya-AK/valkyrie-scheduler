# --- !Ups

create table "APP_INSTANCE" ("INSTANCE_ID" varchar(40) NOT NULL,"GROUP_NAME" varchar(100) NOT NULL,"JOB_NAME" varchar(100) NOT NULL,"TRIGGER_NAME" VARCHAR(100),"START_TIME" TIMESTAMP NOT NULL,"END_TIME" TIMESTAMP,"MESSAGE" VARCHAR(200),"RETURN_CODE" INTEGER,"SEQ_ID" BIGINT NOT NULL,"STATUS_ID" INTEGER NOT NULL,"ATTEMPT" INTEGER NOT NULL,"AGENT_NAME" varchar(100) NOT NULL);
create table "APP_STATUS" ("ID" INTEGER NOT NULL,"STATUS" varchar(40) NOT NULL);
create table "APP_INSTANCE_LOG" ("INSTANCE_ID" varchar(40) NOT NULL,"LOG_TYPE" varchar(10) NOT NULL,"LOG_DATA" TEXT);
create table "APP_GROUP" ("ID" VARCHAR(40) NOT NULL,"GROUP_NAME" VARCHAR(30) NOT NULL,"GROUP_EMAIL" VARCHAR(300) NOT NULL,"DESCRIPTION" VARCHAR(200) NOT NULL);


INSERT INTO "APP_STATUS" VALUES (1, 'running');
INSERT INTO "APP_STATUS" VALUES (2, 'succeeded');
INSERT INTO "APP_STATUS" VALUES (3, 'failed');
INSERT INTO "APP_STATUS" VALUES (4, 'error');


# --- !Downs

drop table "APP_INSTANCE";
drop table "APP_STATUS";
drop table "APP_INSTANCE_LOG";
drop table "APP_GROUP";