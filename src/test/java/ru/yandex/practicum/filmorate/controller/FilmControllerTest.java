package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }


    @Test
    void validateFilm() {
        Film film1 = Film.builder().id(1).name("Титаник").description("Красивый фильм о любви")
                .releaseDate(LocalDate.of(1997, 11, 1)).duration(194).build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film1);
        assertTrue(violations.isEmpty(), "Фильм Титаник должен быть добавлен успешно");

        Film film2 = Film.builder().id(2).name("").description("Тестовый фильм")
                .releaseDate(LocalDate.of(2023, 01, 1)).duration(1).build();
        violations = validator.validate(film2);
        Optional<ConstraintViolation<Film>> constraintViolation = violations.stream().findFirst();
        assertFalse(constraintViolation.isEmpty(), "Валидация не должна проходить по имени файла");
        assertEquals(constraintViolation.get().getMessage(), "Имя фильма должно быть задано", "Должна вернуться ошибка имени файла");

        Film film3 = Film.builder().id(3).name("Назад в будущее").description("Так много слов было сказано об этом фильме, что уже и добавить нечего, но я все пишу и пишу, пишу и пишу. Так много слов было сказано об этом фильме, что уже и добавить нечего, но я все пишу и пишу, пишу и пишу.")
                .releaseDate(LocalDate.of(1980, 10, 3)).duration(180).build();
        violations = validator.validate(film3);
        constraintViolation = violations.stream().findFirst();
        assertFalse(constraintViolation.isEmpty(), "Валидация не должна проходить по длине описания");
        assertEquals(constraintViolation.get().getMessage(), "Максимальная длинна описания 200 символов", "Должна вернуться ошибка длинны описания");

        Film film4 = Film.builder().id(4).name("Назад в будущее 2").description("Так много слов было сказано об этом фильме, что уже и добавить нечего, но я все пишу и пишу, пишу и пишу.")
                .releaseDate(LocalDate.of(1880, 10, 3)).duration(180).build();
        violations = validator.validate(film4);
        constraintViolation = violations.stream().findFirst();
        assertFalse(constraintViolation.isEmpty(), "Валидация не должна проходить по дате релиза");
        assertEquals(constraintViolation.get().getMessage(), "Релиз не может быть раньше 28.12.1895", "Должна вернуться ошибка даты релиза");

        Film film5 = Film.builder().id(5).name("Назад в будущее 3").description("Так много слов было сказано об этом фильме, что уже и добавить нечего, но я все пишу и пишу, пишу и пишу.")
                .releaseDate(LocalDate.of(1980, 10, 3)).duration(-180).build();
        violations = validator.validate(film5);
        constraintViolation = violations.stream().findFirst();
        assertFalse(constraintViolation.isEmpty(), "Валидация не должна проходить по продолжительности");
        assertEquals(constraintViolation.get().getMessage(), "Продолжительность фильма должна быть положительной", "Должна вернуться ошибка продолжительности");
    }
}