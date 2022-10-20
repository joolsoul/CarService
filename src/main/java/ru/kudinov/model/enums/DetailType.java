package ru.kudinov.model.enums;

public enum DetailType {
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
