package ru.kudinov.model;

import javax.persistence.*;

@Entity
public class DetailRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "detail_id")
    private Detail service;

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

    public Detail getService() {
        return service;
    }

    public void setService(Detail service) {
        this.service = service;
    }

    public Request getOrder() {
        return request;
    }

    public void setOrder(Request request) {
        this.request = request;
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
}
