package ru.kudinov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kudinov.model.Car;
import ru.kudinov.model.Request;
import ru.kudinov.model.User;
import ru.kudinov.model.enums.RequestStatus;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    Request findByUserAndRequestStatus(User user, RequestStatus requestStatus);

    List<Request> findByUser(User user);

    List<Request> findByCarAndRequestStatus(Car car, RequestStatus requestStatus);

    List<Request> findByRequestStatus(RequestStatus requestStatus);
}
