package ru.kudinov.model;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String registrationNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User owner;

    private String carBrand;

    @OneToMany(mappedBy = "car", cascade = CascadeType.REMOVE)
    private Set<Request> requests;

    public Car(String registrationNumber, String carBrand) {
        this.registrationNumber = registrationNumber;
        this.carBrand = carBrand;
    }

    public Car(String registrationNumber, User owner, String carBrand) {
        this.registrationNumber = registrationNumber;
        this.owner = owner;
        this.carBrand = carBrand;
    }

    public Car() {
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getCarBrand() {
        return carBrand;
    }

    public void setCarBrand(String carBrand) {
        this.carBrand = carBrand;
    }
}
