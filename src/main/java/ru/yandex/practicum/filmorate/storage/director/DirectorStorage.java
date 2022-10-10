package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

public interface DirectorStorage {

    List<Director> getDirectors();

    Director getDirectorById(long id);

    Director createDirector(Director director);

    Director updateDirector(Director director);

    void removeDirector(long id);
}
