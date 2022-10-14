package ru.yandex.practicum.filmorate.storage.director;

import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.*;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@AllArgsConstructor
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;
    @Override
    public List<Director> getDirectors() {
        String sql = "SELECT * FROM director";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeDirector(rs));
    }

    @Override
    public Director getDirectorById(long id) {
        String sql = "SELECT * FROM director WHERE director_id=?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeDirector(rs), id);
        } catch (DataAccessException e){
            throw new DirectorNotFoundException(String.format("Director with id %d not found", id));
        }
    }

    @Override
    public Director createDirector(Director director) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO director (name) VALUES(?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, director.getName());
            return preparedStatement;
        }, keyHolder);
        director.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        String sql = "UPDATE director SET name=? WHERE director_id=?";
        int status = jdbcTemplate.update(sql,director.getName(),director.getId());
        if (status > 0) {
            return director;
        }
        throw new DirectorNotFoundException("Director with id: " + director.getId() + " not found.");
    }

    @Override
    public void removeDirector(long id) {
        String sql = "DELETE FROM movie_director WHERE director_id=?";
        jdbcTemplate.update(sql,id);
        sql = "DELETE FROM DIRECTOR WHERE director_id=?";
        jdbcTemplate.update(sql,id);
    }

    private Director makeDirector(ResultSet rs) throws SQLException {
        int id = rs.getInt("director_id");
        String name = rs.getString("name");
        return new Director(id,name);
    }
}
