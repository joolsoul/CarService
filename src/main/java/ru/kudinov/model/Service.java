package ru.kudinov.model;

import ru.kudinov.model.enums.entityEnums.ProductKind;
import ru.kudinov.model.enums.entityEnums.ServiceType;
import ru.kudinov.model.interfaces.Producible;
import ru.kudinov.model.interfaces.ProducibleType;

import javax.persistence.*;

@Entity
public class Service implements Producible {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private Double price;

    @Enumerated(EnumType.STRING)
    private ServiceType serviceType;

    @Transient
    private final ProductKind PRODUCT_KIND = ProductKind.SERVICE;

    private boolean isActive;

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
    public ProducibleType getProductType() {
        return getServiceType();
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive() {
        this.isActive = true;
    }

    public void setNonActive() {
        this.isActive = false;
    }
}
