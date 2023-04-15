package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@Builder
public class Director {

    @EqualsAndHashCode.Include
    private long id;
    @NotNull(message = "Имя режисёра должно быть задано")
    @NotBlank(message = "Имя режисёра должно быть задано")
    private String name;
}
