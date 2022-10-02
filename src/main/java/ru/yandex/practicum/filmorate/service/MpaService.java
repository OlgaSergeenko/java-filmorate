package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class MpaService {

    private final MpaStorage mpaStorage;

    public List<Mpa> getMpas() {
        return mpaStorage.getMpas();
    }

    public Optional<Mpa> getMpaById(long id) {
        return mpaStorage.getMpaById(id);
    }
}
