package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@Builder
public class User {
    @EqualsAndHashCode.Include
    private long id;
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
    private Set<Long> friends;

    public void compute() {
        if (this.getName() == null || this.getName().isBlank()) {
            this.setName(this.getLogin());
        }
    }
}
