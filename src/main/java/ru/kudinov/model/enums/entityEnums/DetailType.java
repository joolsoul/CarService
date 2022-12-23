package ru.kudinov.model.enums.entityEnums;

import ru.kudinov.model.interfaces.ProducibleType;

public enum DetailType implements ProducibleType {
    ENGINE("Двигатель"),
    TRANSMISSION("Коробка передач");

    private String value;

    DetailType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
