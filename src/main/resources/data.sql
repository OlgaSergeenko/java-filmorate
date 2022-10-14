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

INSERT INTO EVENT_TYPE (event_type)
VALUES ('LIKE'),
       ('REVIEW'),
       ('FRIEND');

INSERT INTO EVENT_OPERATION (operation_type)
VALUES ('ADD'),
       ('REMOVE'),
       ('UPDATE');