# java-filmorate
Template repository for Filmorate project.

[Link to diagram in project docs](drawSQL-filmorate.v3.png)

To see the diagram online use this [link](https://drawsql.app/teams/new-13/diagrams/filmorate)

![Database](drawSQL-filmorate.v3.png)

#### Пример запроса на получение списка юзеров:
SELECT * </br>
FROM user

#### Пример запроса на получение юзера по id:
SELECT * </br>
FROM user AS u </br>
WHERE u.user_id = id

#### Пример запроса на получение таблицы друзей конкретного юзера (id = 1):
SELECT * </br>
FROM friend_user AS f </br>
WHERE f.user_id = id

#### Пример запроса на получение рейтинга фильма по названию:
SELECT m.movie_name, </br>
        r.rating_name </br>
FROM movie AS m </br>
LEFT OUTER JOIN rating AS r ON m.rating_id = r.rating_id

#### Пример запроса на получение списка всех фильмов с рейтингом PG:
SELECT m.movie_name, </br>
g.genre_name </br>
FROM movie AS m </br>
LEFT OUTER JOIN genre_movie AS gm ON m.movie_id = gm.movie_id </br>
LEFT OUTER JOIN genre AS g ON gm.genre_id = g.genre_id