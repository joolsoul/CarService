package ru.kudinov.service;

import org.springframework.stereotype.Service;
import ru.kudinov.model.ServiceOrganization;
import ru.kudinov.repository.ServiceOrganizationRepository;

import java.util.List;

@Service
public class ServOrganizationService {

    private final ServiceOrganizationRepository serviceOrganizationRepository;

    public ServOrganizationService(ServiceOrganizationRepository serviceOrganizationRepository) {
        this.serviceOrganizationRepository = serviceOrganizationRepository;
    }

    public ServiceOrganization findById(Long serviceOrganizationId) {
        if (serviceOrganizationRepository.findById(serviceOrganizationId).isPresent()) {
            return serviceOrganizationRepository.findById(serviceOrganizationId).get();
        }
        return null;
    }

    public List<ServiceOrganization> allServiceOrganizations() {
        return serviceOrganizationRepository.findAllByIsActive(true);
    }

    public List<ServiceOrganization> getNonActiveServiceOrganizations() {
        return serviceOrganizationRepository.findAllByIsActive(false);
    }

    public boolean saveServiceOrganization(ServiceOrganization serviceOrganization) {
        ServiceOrganization serviceOrganizationFromDb = serviceOrganizationRepository.findByAddress(serviceOrganization.getAddress());

        if (serviceOrganizationFromDb != null && serviceOrganizationFromDb.isActive()) {
            return false;
        }
        if (serviceOrganizationFromDb != null && !serviceOrganizationFromDb.isActive()) {
            serviceOrganizationFromDb.setActive();
            serviceOrganizationRepository.save(serviceOrganizationFromDb);
            return true;
        }

        serviceOrganization.setActive();
        serviceOrganizationRepository.save(serviceOrganization);
        return true;
    }

    public void updateServiceOrganization(ServiceOrganization serviceOrganization) {
        ServiceOrganization serviceOrganizationFromDb = serviceOrganizationRepository.findByAddress(serviceOrganization.getAddress());

        if (serviceOrganizationFromDb != null && !serviceOrganizationFromDb.isActive()) {
            serviceOrganizationFromDb.setActive();
            serviceOrganizationFromDb.setServOrgWorkSchedule(serviceOrganization.getServOrgWorkSchedule());
            serviceOrganizationFromDb.setPhoneNumber(serviceOrganization.getPhoneNumber());
            serviceOrganizationRepository.save(serviceOrganizationFromDb);
            return;
        }

        serviceOrganization.setActive();
        serviceOrganizationRepository.save(serviceOrganization);
    }

    public boolean deleteServiceOrganization(ServiceOrganization serviceOrganization) {
        if (serviceOrganizationRepository.findById(serviceOrganization.getId()).isPresent()) {
            serviceOrganization.setNonActive();
            serviceOrganizationRepository.save(serviceOrganization);
            return true;
        }
        return false;
    }

}
