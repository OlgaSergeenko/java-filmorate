package ru.yandex.practicum.filmorate.controller;

import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public interface AbstractController<T> {
    List<T> findAll();
    T create(T value);
    T update(T value);
    T getById(long id);

    default Long generateId(long startId) {
        return ++startId;
    }
}
