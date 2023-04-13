package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FilmRecommendationServiceTest {

    @Autowired
    private FilmRecommendationService recommendationService;


    @BeforeEach
    public void initial() {
        Map<Long, Film> films = new HashMap<>();
        Map<Long, User> users = new HashMap<>();

        for (int i = 1; i < 7; i++) {
            User user = User.builder()
                    .email("e" + i + "@yandex.ru")
                    .login("login " + i)
                    .name("name " + i)
                    .birthday(LocalDate.of(1990 + 3 * i, 2 + i, 25 - 2 * i))
                    .build();
            user = recommendationService.getUserService().createUser(user);
            users.put(user.getId(), user);
        }

        for (int i = 1; i < 8; i++) {
            Film film = Film.builder()
                    .name("Фильм " + i)
                    .description("Фильм об " + i)
                    .releaseDate(LocalDate.of(1980 + 3 * i, 3 + i, 13 + 2 * i))
                    .duration(20 * i)
                    .build();
            film = recommendationService.getFilmService().createFilm(film);
            films.put(film.getId(), film);
        }

        recommendationService.getFilmService().setLike(films.get(1L), users.get(2L));
        recommendationService.getFilmService().setLike(films.get(1L), users.get(3L));
        recommendationService.getFilmService().setLike(films.get(1L), users.get(4L));
        recommendationService.getFilmService().setLike(films.get(2L), users.get(4L));
        recommendationService.getFilmService().setLike(films.get(2L), users.get(3L));
        recommendationService.getFilmService().setLike(films.get(2L), users.get(6L));
        recommendationService.getFilmService().setLike(films.get(3L), users.get(4L));
        recommendationService.getFilmService().setLike(films.get(3L), users.get(2L));
        recommendationService.getFilmService().setLike(films.get(3L), users.get(1L));
        recommendationService.getFilmService().setLike(films.get(4L), users.get(1L));
        recommendationService.getFilmService().setLike(films.get(4L), users.get(2L));
        recommendationService.getFilmService().setLike(films.get(4L), users.get(3L));
        recommendationService.getFilmService().setLike(films.get(4L), users.get(5L));
        recommendationService.getFilmService().setLike(films.get(4L), users.get(6L));
        recommendationService.getFilmService().setLike(films.get(5L), users.get(3L));
        recommendationService.getFilmService().setLike(films.get(5L), users.get(2L));
        recommendationService.getFilmService().setLike(films.get(6L), users.get(2L));
        recommendationService.getFilmService().setLike(films.get(6L), users.get(3L));
        recommendationService.getFilmService().setLike(films.get(7L), users.get(1L));
        recommendationService.getFilmService().setLike(films.get(7L), users.get(2L));
        recommendationService.getFilmService().setLike(films.get(7L), users.get(3L));
    }

    @Test
    public void shouldReturnFourFilms() {
        List<Film> films = recommendationService.getFilmsRecommendationsByUserId(4L);
        Set<Long> actualRecommendationFilms = films.stream().map(f -> f.getId()).collect(Collectors.toSet());
        Set<Long> expectedRecommendationFilms = new HashSet<>();
        expectedRecommendationFilms.add(4L);
        expectedRecommendationFilms.add(5L);
        expectedRecommendationFilms.add(6L);
        expectedRecommendationFilms.add(7L);
        Assertions.assertEquals(4, films.size(), "Недопустимое количество рекомендованных фильмов");
        Assertions.assertEquals(expectedRecommendationFilms, actualRecommendationFilms, "Недопустимое состав рекомендованных фильмов");
    }
}