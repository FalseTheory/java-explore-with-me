CREATE TABLE IF NOT EXISTS users(
id serial NOT NULL UNIQUE,
name varchar(250) NOT NULL,
email varchar(254) NOT NULL UNIQUE,
PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS categories(
id serial NOT NULL UNIQUE,
name varchar(50) NOT NULL UNIQUE,
PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS events(
	id serial NOT NULL UNIQUE,
	annotation varchar NOT NULL,
	description varchar,
	category_id bigint NOT NULL,
	event_date timestamp without time zone NOT NULL,
	confirmed_requests bigint default 0,
	created_on timestamp without time zone NOT NULL,
	initiator_id bigint NOT NULL,
	lat double precision NOT NULL,
	lon double precision NOT NULL,
	paid boolean NOT NULL,
	participation_limit bigint,
	published_on timestamp without time zone,
	request_moderation boolean,
	state varchar(255),
	title varchar(120) NOT NULL,
	views bigint,
	PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS compilations(
	id serial NOT NULL UNIQUE,
	pinned boolean,
	title varchar(50) NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS compilations_events(
	compilation_id bigint NOT NULL,
	event_id bigint NOT NULL,
	PRIMARY KEY (compilation_id, event_id)
);
CREATE TABLE IF NOT EXISTS participations (
	id serial NOT NULL UNIQUE,
	created timestamp without time zone NOT NULL,
	event_id bigint NOT NULL,
	requester_id bigint NOT NULL,
	status varchar(255) NOT NULL,
	PRIMARY KEY ("id")
);



ALTER TABLE events ADD CONSTRAINT "events_fk3" FOREIGN KEY (category_id) REFERENCES categories(id);

ALTER TABLE events ADD CONSTRAINT "events_fk7" FOREIGN KEY (initiator_id) REFERENCES users(id);

ALTER TABLE compilations_events ADD CONSTRAINT "compilations_events_fk0" FOREIGN KEY (compilation_id) REFERENCES compilations(id);

ALTER TABLE compilations_events ADD CONSTRAINT "compilations_events_fk1" FOREIGN KEY (event_id) REFERENCES events(id);

ALTER TABLE participations ADD CONSTRAINT "participations_fk2" FOREIGN KEY (event_id) REFERENCES events(id);

ALTER TABLE participations ADD CONSTRAINT "participations_fk3" FOREIGN KEY (requester_id) REFERENCES users(id);