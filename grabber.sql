CREATE TABLE post(
    id serial primary key,
    name varchar,
    text varchar,
    link varchar UNIQUE NOT NULL,
    created timestamp
);