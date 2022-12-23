package ru.kudinov.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.kudinov.model.Car;
import ru.kudinov.model.Request;
import ru.kudinov.model.User;
import ru.kudinov.model.enums.entityEnums.RequestStatus;
import ru.kudinov.repository.RequestRepository;

import java.util.List;

@Service
public class RequestService {

    private final RequestRepository requestRepository;

    public RequestService(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    public Request getRequest(User authUser) {
        Request request = findNotConfirmedRequest(authUser);

        if (request == null) {
            request = new Request();
            request.setUser(authUser);
            request.setRequestStatus(RequestStatus.NOT_CONFIRMED);
            saveRequest(request);
        }
        return request;
    }

    public void deleteRequest(Request request) {
        requestRepository.delete(request);
    }

    public List<Request> allRequests(RequestStatus requestStatus) {
        return requestRepository.findByRequestStatus(requestStatus);
    }

    public Request findNotConfirmedRequest(User user) {
        List<Request> requests = requestRepository.findByUserAndRequestStatus(user, RequestStatus.NOT_CONFIRMED);
        if (requests.size() == 0) return null;
        return requests.get(0);
    }

    public Page<Request> findRequestsByStatus(User user, RequestStatus requestStatus, Pageable pageable) {
        return requestRepository.findByUserAndRequestStatus(user, requestStatus, pageable);
    }

    public Page<Request> findAllRequestsWithoutNotConf(User user, Pageable pageable) {
        return requestRepository.findByUserAndRequestStatusNot(user, RequestStatus.NOT_CONFIRMED, pageable);
    }

    public List<Request> findAllRequests(User user) {
        return requestRepository.findByUser(user);
    }

    public List<Request> findAllRequestsByCar(Car car) {
        List<Request> requests = requestRepository.findByCarAndRequestStatus(car, RequestStatus.CONFIRMED);
        requests.addAll(requestRepository.findByCarAndRequestStatus(car, RequestStatus.IN_PROGRESS));
        requests.addAll(requestRepository.findByCarAndRequestStatus(car, RequestStatus.COMPLETED));
        requests.addAll(requestRepository.findByCarAndRequestStatus(car, RequestStatus.CANCELLED));

        return requests;
    }

    public boolean saveRequest(Request request) {

        Request requestFromDb = findNotConfirmedRequest(request.getUser());

        if (requestFromDb != null) {
            return false;
        }

        requestRepository.save(request);
        return true;
    }

    public void updateRequest(Request request) {
        requestRepository.save(request);
    }
}
