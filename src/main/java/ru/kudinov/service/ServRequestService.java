package ru.kudinov.service;

import org.springframework.stereotype.Service;
import ru.kudinov.model.Request;
import ru.kudinov.model.ServiceRequest;
import ru.kudinov.repository.ServiceRequestRepository;

import java.util.List;

@Service
public class ServRequestService {

    private final ServiceRequestRepository serviceRequestRepository;

    public ServRequestService(ServiceRequestRepository serviceRequestRepository) {
        this.serviceRequestRepository = serviceRequestRepository;
    }

    public void saveServiceRequest(ServiceRequest serviceRequest) {
        serviceRequestRepository.save(serviceRequest);
    }

    public ServiceRequest getServiceRequest(ru.kudinov.model.Service service, Request request) {
        return serviceRequestRepository.findByRequestAndService(request, service);
    }

    public List<ServiceRequest> getServiceRequestsByRequest(Request request) {
        return serviceRequestRepository.findByRequest(request);
    }
}
