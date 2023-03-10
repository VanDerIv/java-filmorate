package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@Builder
public class Genre {
    @EqualsAndHashCode.Include
    private long id;
    @NotNull(message = "Имя жанра должно быть задано")
    private String name;
}
