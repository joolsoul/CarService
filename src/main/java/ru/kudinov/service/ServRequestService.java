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

    public ServiceRequest findById(Long serviceRequestId) {
        if (serviceRequestRepository.findById(serviceRequestId).isPresent()) {
            return serviceRequestRepository.findById(serviceRequestId).get();
        }
        return null;
    }

    public boolean saveServiceRequest(ServiceRequest serviceRequest) {

        serviceRequestRepository.save(serviceRequest);
        return true;
    }

    public ServiceRequest getServiceRequest(ru.kudinov.model.Service service, Request request) {
        return serviceRequestRepository.findByRequestAndService(request, service);
    }

    public List<ServiceRequest> getServiceRequestsByRequest(Request request) {
        return serviceRequestRepository.findByRequest(request);
    }

    public void removeServiceRequest(ServiceRequest serviceRequest) {
        serviceRequestRepository.delete(serviceRequest);
    }
}
