package ru.yandex.practicum.filmorate.model.enums;

public enum Operation {

    REMOVE(1),
    ADD(2),
    UPDATE(3);

    private int opCode;

    Operation(int opCode) {
        this.opCode = opCode;
    }

    public int getOpCode() {
        return opCode;
    }
}
