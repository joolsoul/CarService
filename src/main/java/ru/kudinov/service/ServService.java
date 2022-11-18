package ru.kudinov.service;


import org.springframework.stereotype.Service;
import ru.kudinov.model.enums.ServiceType;
import ru.kudinov.repository.ServiceRepository;

import java.util.List;
import java.util.Objects;

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

    public ru.kudinov.model.Service getByName(String name) {
        return serviceRepository.findByName(name);
    }

    public boolean saveService(ru.kudinov.model.Service service) {

        ru.kudinov.model.Service serviceFromDb = getByName(service.getName());

        if (serviceFromDb != null) {
            return false;
        }

        serviceRepository.save(service);
        return true;
    }

    public boolean deleteService(ru.kudinov.model.Service service) {
        if (serviceRepository.findById(service.getId()).isPresent()) {
            serviceRepository.deleteById(service.getId());

            return true;
        }
        return false;
    }

    public void updateService(ru.kudinov.model.Service service) {
        serviceRepository.save(service);
    }

    public List<ru.kudinov.model.Service> allServices() {
        return serviceRepository.findAll();
    }

    public List<ru.kudinov.model.Service> findByServiceType(ServiceType serviceType) {
        return serviceRepository.findByServiceType(serviceType);
    }

    public boolean isDataCorrectly(ru.kudinov.model.Service service) {
        return service.getName() != null && !Objects.equals(service.getName(), "")
                && service.getServiceType() != null
                && service.getPrice() != null;
    }
}
