package ru.kudinov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kudinov.model.Service;
import ru.kudinov.model.enums.ServiceType;

import java.util.List;

public interface ServiceRepository extends JpaRepository<Service, Long> {

    List<Service> findByServiceType(ServiceType serviceType);

    Service findByName(String name);
}
