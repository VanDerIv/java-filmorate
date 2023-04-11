package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@Builder
public class Review {
    @EqualsAndHashCode.Include
    private long reviewId;
    @NotNull(message = "Фильм должен быть задан")
    private Long filmId;
    @NotNull(message = "Пользователь должен быть задан")
    private Long userId;
    @NotNull(message = "Ревью не может быть пустым")
    @NotBlank(message = "Ревью не может быть пустым")
    private String content;
    @NotNull(message = "Тип отзыва должен быть задан")
    private Boolean isPositive;
    private int useful;
}
