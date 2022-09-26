INSERT INTO genre (genre_name)
VALUES ( 'Комедия' ), ('Драма'), ( 'Мультфильм' ), ('Триллер'),( 'Документальный' ), ('Боевик');

INSERT INTO mpa (mpa_name)
VALUES ( 'G' ), ('PG'), ( 'PG-13' ), ('R'),( 'NC-17' );

INSERT INTO APP_USER (email, login, name, birthday)
VALUES ( 'olya@olya.ru', 'olya', 'olya', '1989-11-06' ),
       ( 'dima@olya.ru', 'dima', 'dima', '1990-01-01' ),
       ( 'sasha@olya.ru', 'sasha', 'sasha', '2019-09-20' );

INSERT INTO MOVIE (movie_name, description, release_date, duration, rate, mpa_id)
VALUES ( 'movie', 'descr', '2016-05-05', 120, 4, 1 ),
       ( 'movie2', 'descr2', '2016-04-04', 130, 5, 2 ),
       ( 'movie3', 'descr3', '2002-04-04', 65, 1, 3 );