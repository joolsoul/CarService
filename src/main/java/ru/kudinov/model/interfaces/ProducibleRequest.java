package ru.kudinov.model.interfaces;

import ru.kudinov.model.Request;
import ru.kudinov.model.enums.entityEnums.ProductKind;

public interface ProducibleRequest {

    Long getId();

    Request getRequest();

    void setRequest(Request request);

    Producible getProduct();

    void setProduct(Producible producible);

    Double getPrice();

    void setPrice(Double price);

    ProductKind getProductKind();
}
