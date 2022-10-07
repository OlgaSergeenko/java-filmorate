package ru.yandex.practicum.filmorate.service;

import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

@Service
@AllArgsConstructor
public class RecommendationService {

  private final FilmDbStorage filmDbStorage;
  private final UserDbStorage userDbStorage;

  public List<Film> getRecommendedFilms(Long userId) {
    var usersWithSameInterests = userDbStorage.getUsersWithSameInterests(userId);
    return usersWithSameInterests.isEmpty()
        ? Collections.emptyList()
        : filmDbStorage.getRecommendedFilms(userId, usersWithSameInterests);
  }
}
