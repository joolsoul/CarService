package ru.kudinov.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.kudinov.model.Car;
import ru.kudinov.model.Request;
import ru.kudinov.model.User;
import ru.kudinov.model.enums.entityEnums.RequestStatus;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    Page<Request> findByUserAndRequestStatus(User user, RequestStatus requestStatus, Pageable pageable);

    List<Request> findByUserAndRequestStatus(User user, RequestStatus requestStatus);

    Page<Request> findByUserAndRequestStatusNot(User user, RequestStatus requestStatus, Pageable pageable);

    List<Request> findByUser(User user);

    List<Request> findByCarAndRequestStatus(Car car, RequestStatus requestStatus);

    List<Request> findByRequestStatus(RequestStatus requestStatus);
}
