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

    public List<ServiceOrganization> getServiceOrganizations() {
        return serviceOrganizationRepository.findAll();
    }
}
