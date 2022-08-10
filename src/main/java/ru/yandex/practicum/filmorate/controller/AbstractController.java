package ru.yandex.practicum.filmorate.controller;

import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public abstract class AbstractController<T> {
    public abstract List<T> findAll();
    public abstract T create(T value);
    public abstract T update(T value);

    public int generateId(int startId) {
        return ++startId;
    }
}
