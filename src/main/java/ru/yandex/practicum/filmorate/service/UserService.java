package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Set;

@Service
public class UserService implements UserStorage {

    private final UserStorage inMemoryUserStorage;

    @Autowired
    public UserService(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    @Override
    public List<User> findAll() {
       return inMemoryUserStorage.findAll();
    }

    @Override
    public User create(User value) {
        return inMemoryUserStorage.create(value);
    }

    @Override
    public User update(User value) {
        return inMemoryUserStorage.update(value);
    }

    @Override
    public User getById(long id) {
        return inMemoryUserStorage.getById(id);
    }

    public Set<Long> addFriend(long userId, long friendId) {
        return inMemoryUserStorage.addFriend(userId, friendId);
    }

    public Set<Long> removeFriend(long userId, long friendId) {
        return inMemoryUserStorage.removeFriend(userId, friendId);
    }


    public List<User> getAllFriends(long userId) {
        return inMemoryUserStorage.getAllFriends(userId);
    }

    public List<User> getFriends(long id) {
        return inMemoryUserStorage.getAllFriends(id);
    }

    public List<User> getCommonFriends(long userId, long otherUserId) {
        return inMemoryUserStorage.getCommonFriends(userId,otherUserId);
    }
}
