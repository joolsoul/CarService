package ru.kudinov.model.enums;

import ru.kudinov.model.interfaces.ProductType;

public enum DetailType implements ProductType {
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
