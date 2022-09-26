package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;
    private final JdbcTemplate jdbcTemplate;
    private final static String CREATE_FRIEND = "INSERT INTO USER_FRIEND (USER_ID, FRIEND_ID) VALUES ( ?, ? )";
    private final static String DELETE_FRIEND = "DELETE FROM USER_FRIEND WHERE user_id = ? AND friend_id = ?";
    private final static String GET_FRIENDS_BY_USER = "SELECT friend_id FROM USER_FRIEND WHERE user_id = ?";
    @Autowired
    public UserService(JdbcTemplate jdbcTemplate, @Qualifier("userDbStorage") UserStorage userStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userStorage = userStorage;
    }

    public Set<User> addFriend(long userId, long friendId) {
        if (userId == friendId) {
            throw new IncorrectIdException("Пользователь не может добавить в друзья себя.");
        }
        validateId(userId);
        validateId(friendId);
        jdbcTemplate.update(CREATE_FRIEND, userId, friendId);
        return getAllFriends(userId);
    }

    public Set<User> removeFriend(long userId, long friendId) {
        validateId(userId);
        validateId(friendId);
        jdbcTemplate.update(DELETE_FRIEND, userId, friendId);
        return getAllFriends(userId);
    }

    public Set<User> getAllFriends(long userId) {
        validateId(userId);
        Set<User> friends = new HashSet<>();
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(GET_FRIENDS_BY_USER, userId);
        while (userRows.next()) {
            Optional<User> user = userStorage.getUserById(userRows.getLong("friend_id"));
            user.ifPresent(friends::add);
        }
        return friends;
    }

    public Set<User> getCommonFriends(long userId, long otherUserId) {
        validateId(userId);
        validateId(otherUserId);
        Set<Long> userFriends = getAllFriends(userId).stream()
                .map(User::getId)
                .collect(Collectors.toSet());
        Set<Long> otherUserFriends = getAllFriends(otherUserId).stream()
                .map(User::getId)
                .collect(Collectors.toSet());
        return userFriends.stream()
                .filter(otherUserFriends::contains)
                .map(userStorage::getUserById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
        }

    private void validateId (long id) {
        if (id <= 0) {
            throw new IncorrectIdException(String.format("Некорректный id  - %d", id));
        } else if (userStorage.findAll()
                .stream()
                .noneMatch(x -> x.getId() == id)) {
            throw new UserNotFoundException(String.format("Пользователь с id %d не найден.", id));
        }
    }
}
