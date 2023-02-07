package ru.yandex.practicum.filmorate.error;

public class ValidationException extends Throwable {
    public ValidationException(String message) {
        super(message);
    }
}
