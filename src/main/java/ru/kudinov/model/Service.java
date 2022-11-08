package ru.kudinov.model;

import ru.kudinov.model.enums.ProductKind;
import ru.kudinov.model.enums.ServiceType;
import ru.kudinov.model.interfaces.Product;
import ru.kudinov.model.interfaces.ProductType;

import javax.persistence.*;

@Entity
public class Service implements Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private Double price;

    @Enumerated(EnumType.STRING)
    private ServiceType serviceType;

    @Transient
    private final ProductKind PRODUCT_KIND = ProductKind.SERVICE;

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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public ProductKind getPRODUCT_KIND() {
        return PRODUCT_KIND;
    }

    @Override
    public ProductType getProductType() {
        return getServiceType();
    }
}
