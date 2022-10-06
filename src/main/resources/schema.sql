DROP TABLE IF EXISTS MOVIE cascade;
DROP TABLE IF EXISTS MPA cascade;
DROP TABLE IF EXISTS GENRE cascade;
DROP TABLE IF EXISTS GENRE_MOVIE cascade;
DROP TABLE IF EXISTS APP_USER cascade;
DROP TABLE IF EXISTS USER_FRIEND cascade;
DROP TABLE IF EXISTS MOVIE_LIKES cascade;
DROP TABLE IF EXISTS DIRECTOR cascade;
DROP TABLE IF EXISTS MOVIE_DIRECTOR cascade;

CREATE TABLE IF NOT EXISTS MPA
(
    mpa_id   integer generated by default as identity primary key,
    mpa_name varchar UNIQUE
);

CREATE TABLE IF NOT EXISTS MOVIE
(
    movie_id     INTEGER generated by default as identity primary key,
    movie_name   VARCHAR(255) NOT NULL,
    description  VARCHAR(200) NOT NULL,
    release_date DATE         NOT NULL,
    duration     INTEGER      NOT NULL,
    rate         INTEGER,
    mpa_id       INTEGER      NOT NULL REFERENCES mpa (mpa_id)
);

CREATE TABLE IF NOT EXISTS APP_USER
(
    user_id  integer generated by default as identity primary key,
    email    varchar NOT NULL,
    login    varchar NOT NULL,
    name     varchar,
    birthday date
);

CREATE TABLE IF NOT EXISTS GENRE
(
    genre_id   integer generated by default as identity primary key,
    genre_name varchar
);

CREATE TABLE IF NOT EXISTS GENRE_MOVIE
(
    movie_id integer REFERENCES movie (movie_id) ON DELETE CASCADE,
    genre_id integer REFERENCES genre (genre_id),
    CONSTRAINT movie_genre_pk primary key (movie_id, genre_id)
);

CREATE TABLE IF NOT EXISTS USER_FRIEND
(
    user_id   integer REFERENCES app_user (user_id) ON DELETE CASCADE,
    friend_id integer REFERENCES app_user (user_id) ON DELETE CASCADE,
    CONSTRAINT user_friend_pk primary key (user_id, friend_id)
);

CREATE TABLE IF NOT EXISTS MOVIE_LIKES
(
    movie_id integer REFERENCES MOVIE (movie_id) ON DELETE CASCADE,
    user_id  integer REFERENCES app_user (user_id) ON DELETE CASCADE,
    CONSTRAINT movie_user_pk primary key (movie_id, user_id)
);

CREATE TABLE IF NOT EXISTS DIRECTOR
(
    director_id integer generated by default as identity primary key,
    name varchar(50)
);

CREATE TABLE IF NOT EXISTS MOVIE_DIRECTOR
(
    movie_id integer REFERENCES movie (movie_id),
    director_id integer REFERENCES director(director_id),
    CONSTRAINT movie_director_pk primary key (movie_id, director_id)
);

INSERT INTO GENRE (genre_name)
VALUES ('Комедия'),
       ('Драма'),
       ('Мультфильм'),
       ('Триллер'),
       ('Документальный'),
       ('Боевик');

INSERT INTO MPA (mpa_name)
VALUES ('G'),
       ('PG'),
       ('PG-13'),
       ('R'),
       ('NC-17');
