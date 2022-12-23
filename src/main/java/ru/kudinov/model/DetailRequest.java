package ru.kudinov.model;

import ru.kudinov.model.enums.entityEnums.ProductKind;
import ru.kudinov.model.interfaces.Producible;
import ru.kudinov.model.interfaces.ProducibleRequest;

import javax.persistence.*;

@Entity
public class DetailRequest implements ProducibleRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "detail_id")
    private Detail detail;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "request_id")
    private Request request;

    private Integer quantity;

    private Double price;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Detail getDetail() {
        return detail;
    }

    public void setDetail(Detail detail) {
        this.detail = detail;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    @Override
    public ProductKind getProductKind() {
        return getProduct().getPRODUCT_KIND();
    }

    @Override
    public Producible getProduct() {
        return getDetail();
    }

    @Override
    public void setProduct(Producible producible) {
        setDetail((Detail) producible);
    }


}
