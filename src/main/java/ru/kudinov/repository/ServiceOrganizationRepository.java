package ru.kudinov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kudinov.model.ServiceOrganization;

import java.util.List;

public interface ServiceOrganizationRepository extends JpaRepository<ServiceOrganization, Long> {

    List<ServiceOrganization> findAllByIsActive(boolean isActive);

    ServiceOrganization findByAddress(String address);

}
