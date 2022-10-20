package ru.kudinov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kudinov.model.Request;
import ru.kudinov.model.Service;
import ru.kudinov.model.ServiceRequest;

import java.util.List;

public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {

    ServiceRequest findByRequestAndService(Request request, Service service);

    List<ServiceRequest> findByRequest(Request request);
}
