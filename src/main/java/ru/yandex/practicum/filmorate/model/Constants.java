package ru.yandex.practicum.filmorate.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;

import java.time.LocalDate;

public class Constants {
    public final static int MAX_DESCRIPTION_LENGTH = 200;
    public final static LocalDate OLDEST_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    public final static Logger LOG_FILM_CONTROLLER = LoggerFactory.getLogger(FilmController.class);

    public final static Logger LOG_USER_CONTROLLER = LoggerFactory.getLogger(UserController.class);
}
