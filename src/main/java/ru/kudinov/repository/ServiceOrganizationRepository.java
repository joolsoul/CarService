package ru.kudinov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kudinov.model.ServiceOrganization;

public interface ServiceOrganizationRepository extends JpaRepository<ServiceOrganization, Long> {
}
