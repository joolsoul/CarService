package ru.kudinov.model.enums;

import ru.kudinov.model.interfaces.ProductType;

public enum ServiceType implements ProductType {
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
