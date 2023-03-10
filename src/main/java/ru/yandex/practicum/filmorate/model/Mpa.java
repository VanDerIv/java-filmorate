package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@Builder
public class Mpa {
    @EqualsAndHashCode.Include
    private long id;
    @NotNull(message = "Код рейтинга должен быть задан")
    private String name;
    private String description;
}
