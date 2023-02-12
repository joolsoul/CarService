package ru.kudinov.model;

import ru.kudinov.model.enums.entityEnums.ServOrgWorkSchedule;

import javax.persistence.*;

@Entity
public class ServiceOrganization {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String address;

    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private ServOrgWorkSchedule servOrgWorkSchedule;

    private boolean isActive;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public ServOrgWorkSchedule getServOrgWorkSchedule() {
        return servOrgWorkSchedule;
    }

    public void setServOrgWorkSchedule(ServOrgWorkSchedule servOrgWorkSchedule) {
        this.servOrgWorkSchedule = servOrgWorkSchedule;
    }
}
