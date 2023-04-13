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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FilmRecommendationServiceTest {

    @Autowired
    private FilmRecommendationService recommendationService;


    @BeforeEach
    public void initial() {
        User user;
        for (int i = 1; i < 7; i++) {
            user = User.builder()
                    .email("e" + i + "@yandex.ru")
                    .login("login " + i)
                    .name("name " + i)
                    .birthday(LocalDate.of(1990 + 3 * i, 2 + i, 25 - 2 * i))
                    .build();
            recommendationService.getUserService().createUser(user);
        }
        Film film;
        for (int i = 1; i < 8; i++) {
            film = Film.builder()
                    .name("Фильм " + i)
                    .description("Фильм об " + i)
                    .releaseDate(LocalDate.of(1980 + 3 * i, 3 + i, 13 + 2 * i))
                    .duration(20 * i)
                    .build();
            recommendationService.getFilmService().createFilm(film);
        }

        recommendationService.setLike(1, 2);
        recommendationService.setLike(1, 3);
        recommendationService.setLike(1, 4);
        recommendationService.setLike(2, 4);
        recommendationService.setLike(2, 3);
        recommendationService.setLike(2, 6);
        recommendationService.setLike(3, 4);
        recommendationService.setLike(3, 2);
        recommendationService.setLike(3, 1);
        recommendationService.setLike(4, 1);
        recommendationService.setLike(4, 2);
        recommendationService.setLike(4, 3);
        recommendationService.setLike(4, 5);
        recommendationService.setLike(4, 6);
        recommendationService.setLike(5, 3);
        recommendationService.setLike(5, 2);
        recommendationService.setLike(6, 2);
        recommendationService.setLike(6, 3);
        recommendationService.setLike(7, 1);
        recommendationService.setLike(7, 2);
        recommendationService.setLike(7, 3);
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