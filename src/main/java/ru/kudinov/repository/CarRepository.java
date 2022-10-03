package ru.kudinov.repository;

import org.springframework.data.repository.CrudRepository;
import ru.kudinov.model.Car;
import ru.kudinov.model.User;

import java.util.List;

public interface CarRepository extends CrudRepository<Car, Long> {

    Car findByRegistrationNumber(String registrationNumber);
    List<Car> findByOwner(User owner);
}
