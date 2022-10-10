package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase
@Sql(scripts = "classpath:testschema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:testdata.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class FilmorateGenreTests {

    private final GenreStorage genreStorage;

    public FilmorateGenreTests(@Autowired GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    @Test
    public void shouldGetGenreById() {
        Genre genre = genreStorage.getGenreById(1);

        assertThat(genre).hasFieldOrPropertyWithValue("name", "Комедия");
    }

    @Test
    public void shouldGetAllGenres() {
        List<Genre> genres = genreStorage.getGenres();
        assertEquals(6, genres.size(), "Количество жанров в базе неверное");
    }

}
