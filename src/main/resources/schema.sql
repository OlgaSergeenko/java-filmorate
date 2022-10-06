DROP TABLE IF EXISTS MOVIE cascade;
DROP TABLE IF EXISTS MPA cascade;
DROP TABLE IF EXISTS GENRE cascade;
DROP TABLE IF EXISTS GENRE_MOVIE cascade;
DROP TABLE IF EXISTS APP_USER cascade;
DROP TABLE IF EXISTS USER_FRIEND cascade;
DROP TABLE IF EXISTS MOVIE_LIKES cascade;
DROP TABLE IF EXISTS DIRECTOR cascade;
DROP TABLE IF EXISTS MOVIE_DIRECTOR cascade;
DROP TABLE IF EXISTS REVIEW cascade;
DROP TABLE IF EXISTS REVIEW_USER cascade;

CREATE TABLE IF NOT EXISTS MPA
(
    mpa_id   bigint generated by default as identity primary key,
    mpa_name varchar UNIQUE
);

CREATE TABLE IF NOT EXISTS MOVIE
(
    movie_id     BIGINT generated by default as identity primary key,
    movie_name   VARCHAR(255) NOT NULL,
    description  VARCHAR(200) NOT NULL,
    release_date DATE         NOT NULL,
    duration     INTEGER      NOT NULL,
    mpa_id       BIGINT      NOT NULL REFERENCES mpa (mpa_id)
);

CREATE TABLE IF NOT EXISTS APP_USER
(
    user_id  bigint generated by default as identity primary key,
    email    varchar NOT NULL,
    login    varchar NOT NULL,
    name     varchar,
    birthday date
);

CREATE TABLE IF NOT EXISTS GENRE
(
    genre_id   bigint generated by default as identity primary key,
    genre_name varchar
);

CREATE TABLE IF NOT EXISTS GENRE_MOVIE
(
    movie_id BIGINT REFERENCES movie (movie_id) ON DELETE CASCADE,
    genre_id BIGINT REFERENCES genre (genre_id),
    CONSTRAINT movie_genre_pk primary key (movie_id, genre_id)
);

CREATE TABLE IF NOT EXISTS USER_FRIEND
(
    user_id   BIGINT REFERENCES app_user (user_id) ON DELETE CASCADE,
    friend_id BIGINT REFERENCES app_user (user_id) ON DELETE CASCADE,
    CONSTRAINT user_friend_pk primary key (user_id, friend_id)
);

CREATE TABLE IF NOT EXISTS MOVIE_LIKES
(
    movie_id BIGINT REFERENCES MOVIE (movie_id) ON DELETE CASCADE,
    user_id  BIGINT REFERENCES app_user (user_id) ON DELETE CASCADE,
    CONSTRAINT movie_user_pk primary key (movie_id, user_id)
);

CREATE TABLE IF NOT EXISTS DIRECTOR
(
    director_id bigint generated by default as identity primary key,
    name varchar(50)
);

CREATE TABLE IF NOT EXISTS MOVIE_DIRECTOR
(
    movie_id BIGINT REFERENCES movie (movie_id),
    director_id BIGINT REFERENCES director(director_id),
    CONSTRAINT movie_director_pk primary key (movie_id, director_id)
);

CREATE TABLE IF NOT EXISTS REVIEW
(
    review_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    content VARCHAR(255),
    is_positive BIT NOT NULL,
    user_id BIGINT NOT NULL REFERENCES app_user (user_id) ON DELETE CASCADE,
    movie_id BIGINT NOT NULL REFERENCES movie (movie_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS REVIEW_USER
(
    user_id BIGINT NOT NULL REFERENCES app_user (user_id) ON DELETE CASCADE,
    review_id BIGINT NOT NULL REFERENCES review (review_id) ON DELETE CASCADE,
    is_like TINYINT NOT NULL
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
