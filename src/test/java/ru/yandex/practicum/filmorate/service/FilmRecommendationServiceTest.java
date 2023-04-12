package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FilmRecommendationServiceTest {

    @Autowired
    private  FilmRecommendationService recommendationService;


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
        for (int i = 1; i < 6; i++) {
            film = Film.builder()
                    .name("Фильм " + i)
                    .description("Фильм об " + i)
                    .releaseDate(LocalDate.of(1980 + 3 * i, 3 + i, 13 + 2 * i))
                    .duration(20 * i)
                    .build();
            recommendationService.getFilmService().createFilm(film);
        }

        recommendationService.setLike(2,6);
        recommendationService.setLike(3,5);
        recommendationService.setLike(5,1);
        recommendationService.setLike(3,1);
        recommendationService.setLike(1,2);

    }

    @Test
    public void checkGetFilmsRecommendationsForUser() {
        List<Film> films = recommendationService.getFilmsRecommendationsForUser(1L);
        //assertEquals(5, filmService.getAllFilms().size(), "Недопустимое количество фильмов");
    }
}