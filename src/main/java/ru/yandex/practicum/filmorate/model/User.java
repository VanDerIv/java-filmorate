package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
public class User {
    @EqualsAndHashCode.Include
    private int id;
    @NotNull(message = "Email пользователя должен быть задан")
    @NotBlank(message = "Email пользователя должен быть задан")
    @Email(message = "Email должен соответствовать формату почты")
    private String email;
    @NotNull(message = "Логин пользователя должен быть задан")
    @NotBlank(message = "Логин пользователя должен быть задан")
    private String login;
    private String name;
    @Past(message = "День рожденье пользователя не может быть больше текущей даты")
    private LocalDate birthday;
}
