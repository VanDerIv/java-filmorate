package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@Builder
public class FeedEvent {
    private Long timestamp;
    @NotNull(message = "Введите идентификатор пользователя")
    private long userId;
    @NotNull(message = "Введите тип события")
    private String eventType;
    @NotNull(message = "Введите тип операции")
    private String operation;
    @EqualsAndHashCode.Include
    private long eventId;
    @NotNull(message = "Введите идентификатор события")
    private long entityId;
}
