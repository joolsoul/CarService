package ru.kudinov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kudinov.model.Service;
import ru.kudinov.model.enums.entityEnums.ServiceType;

import java.util.List;

public interface ServiceRepository extends JpaRepository<Service, Long> {

    List<Service> findByServiceTypeAndIsActive(ServiceType serviceType, boolean isActive);

    List<Service> findAllByIsActive(boolean isActive);

    Service findByName(String name);
}
