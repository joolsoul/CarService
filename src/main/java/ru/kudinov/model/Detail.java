package ru.kudinov.model;

import ru.kudinov.model.enums.DetailType;
import ru.kudinov.model.enums.ProductKind;
import ru.kudinov.model.interfaces.Product;
import ru.kudinov.model.interfaces.ProductType;

import javax.persistence.*;

@Entity
public class Detail implements Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private Integer quantity;

    private Double price;

    private String description;

    @Enumerated(EnumType.STRING)
    private DetailType detailType;

    @Transient
    private final ProductKind PRODUCT_KIND = ProductKind.DETAIL;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DetailType getDetailType() {
        return detailType;
    }

    public void setDetailType(DetailType detailType) {
        this.detailType = detailType;
    }

    public ProductKind getPRODUCT_KIND() {
        return PRODUCT_KIND;
    }

    @Override
    public ProductType getProductType() {
        return getDetailType();
    }
}
