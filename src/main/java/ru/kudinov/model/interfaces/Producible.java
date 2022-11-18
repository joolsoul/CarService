package ru.kudinov.model.interfaces;

import ru.kudinov.model.enums.ProductKind;

public interface Producible {

    Long getId();

    String getName();

    Double getPrice();

    ProductKind getPRODUCT_KIND();

    ProducibleType getProductType();
}
