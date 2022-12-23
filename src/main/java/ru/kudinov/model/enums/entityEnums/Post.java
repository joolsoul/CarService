package ru.kudinov.model.enums.entityEnums;

public enum Post {
    EMPLOYEE("Работник"),
    INTERN("Стажёр");

    private final String value;

    Post(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}