CREATE TABLE IF NOT EXISTS users(
id serial NOT NULL UNIQUE,
name varchar(250) NOT NULL,
email varchar(254) NOT NULL UNIQUE,
PRIMARY KEY (id)
);
