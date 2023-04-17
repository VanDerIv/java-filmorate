package ru.yandex.practicum.filmorate.model.enums;

public enum EventType {

    LIKE(1),
    REVIEW(2),
    FRIEND(3);

    private int eventCode;

    EventType(int eventCode) {
        this.eventCode = eventCode;
    }

    public int getEventCode() {
        return eventCode;
    }
}
