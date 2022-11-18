package ru.kudinov.service;

import org.springframework.stereotype.Service;
import ru.kudinov.model.Detail;
import ru.kudinov.model.DetailRequest;
import ru.kudinov.model.Request;
import ru.kudinov.repository.DetailRequestRepository;

import java.util.List;

@Service
public class DetailRequestService {

    private final DetailRequestRepository detailRequestRepository;

    public DetailRequestService(DetailRequestRepository detailRequestRepository) {
        this.detailRequestRepository = detailRequestRepository;
    }

    public DetailRequest findById(Long detailRequestId) {
        if (detailRequestRepository.findById(detailRequestId).isPresent()) {
            return detailRequestRepository.findById(detailRequestId).get();
        }
        return null;
    }

    public boolean saveDetailRequest(DetailRequest detailRequest) {
        detailRequestRepository.save(detailRequest);
        return true;
    }


    public DetailRequest getDetailRequest(Detail detail, Request request) {
        return detailRequestRepository.findByRequestAndDetail(request, detail);
    }

    public List<DetailRequest> getDetailRequestsByRequest(Request request) {
        return detailRequestRepository.findByRequest(request);
    }

    public void removeDetailRequest(DetailRequest detailRequest) {
        detailRequestRepository.delete(detailRequest);
    }
}
