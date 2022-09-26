package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private final static String GET = "SELECT * FROM mpa";
    private final static String GET_BY_ID = "SELECT * FROM MPA WHERE ID = ?";
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Mpa> getMpas() {
        List<Mpa> mpas = new ArrayList<>();
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet(GET);
        while (mpaRows.next()) {
            Mpa mpa = new Mpa(
                    mpaRows.getInt("id"),
                    mpaRows.getString("mpa_name"));
            mpas.add(mpa);
        }
        return mpas;
    }

    @Override
    public Optional<Mpa> getMpaById(int id) {
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet(GET_BY_ID, id);
        if(mpaRows.next()) {
            Mpa mpa = new Mpa (
                    mpaRows.getInt("id"),
                    mpaRows.getString("mpa_name"));

            log.info("Найден рейтинг: {} {}", mpa.getId(), mpa.getName());

            return Optional.of(mpa);
        } else {
            log.info("Рейтинг с идентификатором {} не найден.", id);
            throw new MpaNotFoundException(String.format("Рейтинг с идентификатором %d не найден.", id));
        }
    }
}
