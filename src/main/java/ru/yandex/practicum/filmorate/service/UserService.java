package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserStorage inMemoryUserStorage;

    public Set<Long> addFriend(long userId, long friendId) {
        if (userId == friendId) {
            throw new IncorrectIdException("Пользователь не может добавить в друзья себя.");
        }
        validateId(userId);
        validateId(friendId);
        User user = inMemoryUserStorage.getById(userId);
        user.addFriend(friendId);
        User friend = inMemoryUserStorage.getById(friendId);
        friend.addFriend(userId);
        return user.getFriends();
    }

    public Set<Long> removeFriend(long userId, long friendId) {
        validateId(userId);
        validateId(friendId);
        User user = inMemoryUserStorage.getById(userId);
        user.getFriends().remove(friendId);
        User friend = inMemoryUserStorage.getById(friendId);
        friend.getFriends().remove(userId);
        return user.getFriends();
    }


    public List<User> getAllFriends(long userId) {
        validateId(userId);
        Set<Long> friends = inMemoryUserStorage.getById(userId).getFriends();
        List<User> userFriends = new ArrayList<>();
        for (long id : friends) {
            userFriends.add(inMemoryUserStorage.getById(id));
        }
        return userFriends;
    }

    public List<User> getCommonFriends(long userId, long otherUserId) {
        validateId(userId);
        validateId(otherUserId);
        Set<Long> userFriends = inMemoryUserStorage.getById(userId).getFriends();
        Set<Long> otherUserFriends = inMemoryUserStorage.getById(otherUserId).getFriends();
        Set<Long> common = userFriends.stream()
                .filter(otherUserFriends::contains)
                .collect(Collectors.toSet());
        List<User> commonFriends = new ArrayList<>();
        for (long id : common) {
            commonFriends.add(inMemoryUserStorage.getById(id));
        }
        return commonFriends;
    }

    public void validateId (long id) {
        if (id <= 0) {
            throw new IncorrectIdException(String.format("Некорректный id  - %d", id));
        } else if (inMemoryUserStorage.findAll()
                .stream()
                .noneMatch(x -> x.getId() == id)) {
            throw new UserNotFoundException(String.format("Пользователь с id %d не найден.", id));
        }
    }
}
