package ru.yandex.practicum.filmorate.storage.feed;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeedDbStorage implements FeedStorage {

    private final JdbcTemplate jdbcTemplate;

    public List<Event> getFeed(long userId) {
        String sql = "SELECT * FROM FEED WHERE USER_ID = ?";
        log.info("Searching for feed (user_id={})", userId);
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeEvent(rs), userId);
    }

    public void addEvent(long userId, String eventType,
                         String operation, long entityId) {
        String sql = "INSERT INTO FEED (now_stamp, user_id, event_type, " +
                "operation, entity_id) VALUES (?, ?, ?, ?, ?)";
        log.info("Adding event: type={}, operation={}, user={}, entity={}", eventType, operation, userId, entityId);
        jdbcTemplate.update(sql, Timestamp.from(Instant.now()).getTime(), userId, eventType, operation, entityId);
    }
    private Event makeEvent(ResultSet rs) throws SQLException {
        return new Event(
                rs.getLong("now_stamp"),
                rs.getLong("user_id"),
                rs.getString("event_type"),
                rs.getString("operation"),
                rs.getLong("event_id"),
                rs.getLong("entity_id")
        );
    }
}
