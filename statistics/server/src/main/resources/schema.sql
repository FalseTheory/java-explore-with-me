CREATE TABLE IF NOT EXISTS "stats" (
	"id" serial NOT NULL UNIQUE,
	"uri" varchar(256) NOT NULL,
	"ip" varchar(50) NOT NULL,
	"app" varchar(256) NOT NULL,
	"timestamp" timestamp without time zone NOT NULL,
	PRIMARY KEY ("id")
);