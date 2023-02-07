package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }


    @Test
    void validateFilm() {
        User user1 = User.builder().id(1).name("Петя").login("Peta").email("peta@yandex.ru")
                .birthday(LocalDate.of(2003, 02, 1)).build();
        Set<ConstraintViolation<User>> violations = validator.validate(user1);
        assertTrue(violations.isEmpty(), "Пользователь создан без ошибок");

        User user2 = User.builder().id(2).name("Петя").login("Peta")
                .birthday(LocalDate.of(2003, 02, 1)).build();
        violations = validator.validate(user2);
        Optional<ConstraintViolation<User>> constraintViolation = violations.stream().findFirst();
        assertFalse(constraintViolation.isEmpty(), "Валидация не должна проходить по Email");
        assertEquals(constraintViolation.get().getMessage(), "Email пользователя должен быть задан", "Должна вернуться ошибка пустого Email");

        User user3 = User.builder().id(3).name("Петя").login("Peta").email("yandex.ru")
                .birthday(LocalDate.of(2003, 02, 1)).build();
        violations = validator.validate(user3);
        constraintViolation = violations.stream().findFirst();
        assertFalse(constraintViolation.isEmpty(), "Валидация не должна проходить по Email");
        assertEquals(constraintViolation.get().getMessage(), "Email должен соответствовать формату почты", "Должна вернуться ошибка не корректного Email");

        User user4 = User.builder().id(4).name("Петя").login("").email("peta@yandex.ru")
                .birthday(LocalDate.of(2003, 02, 1)).build();
        violations = validator.validate(user4);
        constraintViolation = violations.stream().findFirst();
        assertFalse(constraintViolation.isEmpty(), "Валидация не должна проходить по логину");
        assertEquals(constraintViolation.get().getMessage(), "Логин пользователя должен быть задан", "Должна вернуться ошибка пустого логина");

        User user5 = User.builder().id(5).name("Петя").login("Peta").email("peta@yandex.ru")
                .birthday(LocalDate.of(2223, 02, 1)).build();
        violations = validator.validate(user5);
        constraintViolation = violations.stream().findFirst();
        assertFalse(constraintViolation.isEmpty(), "Валидация не должна проходить по дате рождения");
        assertEquals(constraintViolation.get().getMessage(), "День рожденье пользователя не может быть больше текущей даты", "Должна вернуться ошибка даты рождения");
    }
}