package ru.kudinov.model;

import ru.kudinov.model.enums.entityEnums.ProductKind;
import ru.kudinov.model.interfaces.Producible;
import ru.kudinov.model.interfaces.ProducibleRequest;

import javax.persistence.*;

@Entity
public class ServiceRequest implements ProducibleRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "service_id")
    private Service service;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "request_id")
    private Request request;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    private Double price;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public ProductKind getProductKind() {
        return getProduct().getPRODUCT_KIND();
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    @Override
    public Producible getProduct() {
        return getService();
    }

    @Override
    public void setProduct(Producible producible) {
        setService((Service) producible);
    }
}
