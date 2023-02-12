package ru.kudinov.model.enums.entityEnums;

import ru.kudinov.model.interfaces.ProducibleType;

public enum ServiceType implements ProducibleType {
    DIAGNOSTICS("Диагностика"),
    MAINTENANCE("Обслуживание"),
    REPAIR("Ремонт");

    private String value;

    ServiceType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
