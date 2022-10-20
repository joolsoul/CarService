package ru.kudinov.service;

import org.springframework.stereotype.Service;
import ru.kudinov.model.Request;
import ru.kudinov.model.User;
import ru.kudinov.model.enums.RequestStatus;
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

    public Request findNotConfirmedRequest(User user) {
        return requestRepository.findByUserAndRequestStatus(user, RequestStatus.NOT_CONFIRMED);
    }

    public List<Request> findAllRequests(User user) {
        return requestRepository.findByUser(user);
    }

    public boolean saveRequest(Request request) {

        Request requestFromDb = findNotConfirmedRequest(request.getUser());

        if (requestFromDb != null) {
            return false;
        }

        requestRepository.save(request);
        return true;
    }
}
