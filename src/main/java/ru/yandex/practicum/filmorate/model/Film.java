package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.model.annotation.IsAfter;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@Builder
public class Film {
    @EqualsAndHashCode.Include
    private int id;
    @NotNull(message = "Имя фильма должно быть задано")
    @NotBlank(message = "Имя фильма должно быть задано")
    private String name;
    @Size(max = 200, message = "Максимальная длинна описания 200 символов")
    private String description;
    @IsAfter(message = "Релиз не может быть раньше 28.12.1895", current = "1895-12-28")
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительной")
    private int duration;
    private Set<Integer> likes;
}
