package ru.kudinov.model.enums.entityEnums;

public enum EmployeeWorkSchedule {
    FOUR_TWO("4/2");

    private final String value;

    EmployeeWorkSchedule(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}