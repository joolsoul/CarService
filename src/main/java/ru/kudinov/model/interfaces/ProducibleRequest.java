package ru.kudinov.model.interfaces;

import ru.kudinov.model.Request;

public interface ProducibleRequest {

    Long getId();

    Request getRequest();

    void setRequest(Request request);

    Product getProduct();

    void setProduct(Product product);

    Double getPrice();

    void setPrice(Double price);
}
