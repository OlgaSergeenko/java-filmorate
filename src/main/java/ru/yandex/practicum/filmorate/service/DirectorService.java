package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class DirectorService {
    private final DirectorStorage directorStorage;

    public List<Director> getDirectors(){
        return directorStorage.getDirectors();
    }

    public Director getDirectorById(long id){
        return directorStorage.getDirectorById(id);
    }

    public Director createDirector(Director director){
        return directorStorage.createDirector(director);
    }

    public Director updateDirector(Director director){
        return directorStorage.updateDirector(director);
    }

    public void removeDirector(long id){
        directorStorage.removeDirector(id);
    }
}
