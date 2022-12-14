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
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Mpa> getMpas() {
        List<Mpa> mpas = new ArrayList<>();
        String sql = "SELECT * FROM mpa";
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet(sql);
        while (mpaRows.next()) {
            Mpa mpa = new Mpa(
                    mpaRows.getLong("mpa_id"),
                    mpaRows.getString("mpa_name"));
            mpas.add(mpa);
        }
        return mpas;
    }

    @Override
    public Mpa getMpaById(long id) {
        String sql = "SELECT * FROM MPA WHERE mpa_id = ?";
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet(sql, id);
        if(mpaRows.next()) {
            Mpa mpa = new Mpa (
                    mpaRows.getLong("mpa_id"),
                    mpaRows.getString("mpa_name"));

            log.info("MPA found: {} {}", mpa.getId(), mpa.getName());

            return mpa;
        } else {
            log.error("MPA with id {} not found.", id);
            throw new MpaNotFoundException(String.format("MPA with ID %d not found.", id));
        }
    }
}
