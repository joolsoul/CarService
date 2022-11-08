package ru.kudinov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kudinov.model.Detail;
import ru.kudinov.model.DetailRequest;
import ru.kudinov.model.Request;

import java.util.List;

public interface DetailRequestRepository extends JpaRepository<DetailRequest, Long> {

    DetailRequest findByRequestAndDetail(Request request, Detail detail);

    List<DetailRequest> findByRequest(Request request);
}
