package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase
@Sql(scripts = "classpath:testschema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:testdata.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)

public class FilmoRateMpaTests {
    private final MpaStorage mpaStorage;

    public FilmoRateMpaTests(@Autowired MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    @Test
    public void shouldGetMpaById() {
        Mpa mpa = mpaStorage.getMpaById(1);

        assertThat(mpa).hasFieldOrPropertyWithValue("name", "G");
    }

    @Test
    public void shouldGetAllMpas() {
        List<Mpa> mpas = mpaStorage.getMpas();
        assertEquals(5, mpas.size(), "Количество рейтингов MPA в базе неверное");
    }

}
