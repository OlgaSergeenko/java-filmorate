package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MpaNotFoundException extends RuntimeException {

    public MpaNotFoundException(final String message) {
        super(message);
    }
}
