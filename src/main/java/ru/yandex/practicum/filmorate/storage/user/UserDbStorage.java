package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User create(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO app_user (email, login, name, birthday) " +
                "VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, user.getEmail());
            preparedStatement.setString(2, user.getLogin());
            preparedStatement.setString(3, user.getName());
            preparedStatement.setDate(4, Date.valueOf(user.getBirthday()));
            return preparedStatement;
        }, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return user;
    }

    private User makeUser(SqlRowSet userRows) {
        return new User(
                userRows.getLong("user_id"),
                Objects.requireNonNull(userRows.getString("email")),
                Objects.requireNonNull(userRows.getString("login")),
                userRows.getString("name"),
                Objects.requireNonNull(userRows.getDate("birthday")).toLocalDate());
    }

    @Override
    public User update(User user) {
        String sql = "UPDATE APP_USER " +
                "SET email = ?,login = ?,name = ?,birthday = ? " +
                "WHERE user_id = ?";
        jdbcTemplate.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        return user;
    }

    @Override
    public Optional<User> getUserById(long id) {
        String sqlGetById = "SELECT * " +
                "FROM APP_USER " +
                "WHERE user_id = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlGetById, id);
        if (userRows.next()) {
            User user = makeUser(userRows);
            log.info("Найден пользователь: {} {}", user.getId(), user.getLogin());
            return Optional.of(user);
        } else {
            log.error("Пользователь с идентификатором {} не найден.", id);
            throw new UserNotFoundException(String.format("Пользователь с идентификатором %d не найден.", id));
        }
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * " +
                "FROM APP_USER";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sql);
        while (userRows.next()) {
            User user = makeUser(userRows);
            users.add(user);
        }
        return users;
    }

    @Override
    public Set<User> addFriend(long userId, long friendId) {
        String sql = "INSERT INTO USER_FRIEND (USER_ID, FRIEND_ID) " +
                "VALUES ( ?, ? )";
        jdbcTemplate.update(sql, userId, friendId);
        return getAllFriends(userId);
    }

    public Set<User> getAllFriends(long userId) {
        Set<User> friends = new HashSet<>();
        String sql = "SELECT friend_id " +
                "FROM USER_FRIEND " +
                "WHERE user_id = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sql, userId);
        while (userRows.next()) {
            Optional<User> user = getUserById(userRows.getLong("friend_id"));
            user.ifPresent(friends::add);
        }
        return friends;
    }

    public void removeFriend(long userId, long friendId) {
        String sql = "DELETE FROM USER_FRIEND " +
                "WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    public Set<User> getCommonFriends(long userId, long otherUserId) {
        String sql = "SELECT friend_id\n" +
                "FROM USER_FRIEND\n" +
                "WHERE user_id = ?\n" +
                "INTERSECT\n" +
                "SELECT friend_id\n" +
                "FROM USER_FRIEND\n" +
                "WHERE user_id = ?";
        SqlRowSet friendsRow = jdbcTemplate.queryForRowSet(sql, userId, otherUserId);
        Set<User> commonFriends = new HashSet<>();
        while (friendsRow.next()) {
            Optional<User> user = getUserById(friendsRow.getLong("friend_id"));
            user.ifPresent(commonFriends::add);
        }
        return commonFriends;
    }

    public void removeUser(long id) {
        String sql = "DELETE FROM APP_USER WHERE user_id = ?";
        jdbcTemplate.update(sql, id);
    }
}

