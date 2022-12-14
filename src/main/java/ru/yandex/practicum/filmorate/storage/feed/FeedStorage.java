package ru.yandex.practicum.filmorate.storage.feed;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface FeedStorage {
    List<Event> getFeed(long userId);

    void addEvent(long userId, String eventType, String operation, long entityId);
}
