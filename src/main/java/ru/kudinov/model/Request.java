package ru.kudinov.model;

import ru.kudinov.model.enums.RequestStatus;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Table
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "car_id")
    private Car car;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
    @JoinColumn(name = "service_organization_id")
    private ServiceOrganization serviceOrganization;

    @Temporal(TemporalType.TIMESTAMP)
    private Date orderDate;

    private Double cost;

    @Enumerated(EnumType.STRING)
    private RequestStatus requestStatus;

    @OneToMany(mappedBy = "request", cascade = CascadeType.REMOVE)
    private Set<DetailRequest> detailRequests;

    @OneToMany(mappedBy = "request", cascade = CascadeType.REMOVE)
    private Set<ServiceRequest> serviceRequests;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public ServiceOrganization getServiceOrganization() {
        return serviceOrganization;
    }

    public void setServiceOrganization(ServiceOrganization serviceOrganization) {
        this.serviceOrganization = serviceOrganization;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public RequestStatus getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(RequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }
}
