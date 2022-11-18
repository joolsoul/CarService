package ru.kudinov.model.enums;

public enum RequestStatus {
    NOT_CONFIRMED("Не подтверждённый"),
    CONFIRMED("Подтверждённый"),
    IN_PROGRESS("В процессе"),
    COMPLETED("Завершённый"),
    CANCELLED("Отменённый");

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
