package ru.kudinov.service;


import org.springframework.stereotype.Service;
import ru.kudinov.model.enums.ServiceType;
import ru.kudinov.repository.ServiceRepository;

import java.util.List;

@Service
public class ServService {

    private final ServiceRepository serviceRepository;

    public ServService(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    public ru.kudinov.model.Service findById(Long serviceId) {
        if (serviceRepository.findById(serviceId).isPresent()) {
            return serviceRepository.findById(serviceId).get();
        }
        return null;
    }

    public List<ru.kudinov.model.Service> findByServiceType(ServiceType serviceType) {
        return serviceRepository.findByServiceType(serviceType);
    }
}
