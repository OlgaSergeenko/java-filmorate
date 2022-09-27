package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final static String CREATE = "INSERT INTO app_user (email, login, name, birthday) VALUES (?, ?, ?, ?)";
    private final static String UPDATE = "UPDATE APP_USER SET email = ?,login = ?,name = ?,birthday = ? WHERE ID = ?";
    private final static String GET_BY_ID = "select * from APP_USER where id = ?";
    private final static String GET = "select * from APP_USER";
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<User> getUserById(long id) {
        if (id <= 0L) {
            throw new IncorrectIdException(String.format("Некорректный id  - %d", id));
        }
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(GET_BY_ID, id);
        if(userRows.next()) {
            User user = new User(
                    userRows.getLong("id"),
                    userRows.getString("email"),
                    userRows.getString("login"),
                    userRows.getString("name"),
                    userRows.getDate("birthday").toLocalDate());

            log.info("Найден пользователь: {} {}", user.getId(), user.getLogin());

            return Optional.of(user);
        } else {
            log.info("Пользователь с идентификатором {} не найден.", id);
            throw new UserNotFoundException(String.format("Пользователь с идентификатором %d не найден.", id));
        }
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(GET);
        while (userRows.next()) {
            User user = new User(
                    userRows.getLong("id"),
                    userRows.getString("email"),
                    userRows.getString("login"),
                    userRows.getString("name"),
                    userRows.getDate("birthday").toLocalDate());
            users.add(user);
        }
        return users;
    }

    @Override
    public User create(User user) {
        validateUserName(user);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
                    PreparedStatement preparedStatement = connection.prepareStatement(CREATE, Statement.RETURN_GENERATED_KEYS);
                    preparedStatement.setString(1, user.getEmail());
                    preparedStatement.setString(2, user.getLogin());
                    preparedStatement.setString(3, user.getName());
                    preparedStatement.setDate(4, Date.valueOf(user.getBirthday()));
                    return preparedStatement;
                }, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return user;
    }

    @Override
    public User update(User user) {
        validateUserName(user);
        if (user.getId()  <= 0L) {
            throw new IncorrectIdException(String.format("Некорректный id  - %d", user.getId()));
        }
        Optional<User> userToUpdate = getUserById(user.getId());
        if (userToUpdate.isEmpty()){
            log.info("Пользователь с идентификатором {} не найден.", user.getId());
            throw new UserNotFoundException(String.format("Пользователь с идентификатором %d не найден.", user.getId()));
        }
        jdbcTemplate.update(UPDATE,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        return user;
    }

    private void validateUserName(User user) {
        if (user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Логин установлен на имя пользователя.");
        }
    }
}
