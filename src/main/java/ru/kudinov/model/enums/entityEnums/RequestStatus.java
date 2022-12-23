package ru.kudinov.model.enums.entityEnums;

public enum RequestStatus {
    NOT_CONFIRMED("Не подтверждён"),
    CONFIRMED("Подтверждён"),
    IN_PROGRESS("В процессе"),
    COMPLETED("Завершён"),
    CANCELLED("Отменён");

    private String value;

    RequestStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
