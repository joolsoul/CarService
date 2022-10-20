package ru.kudinov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kudinov.model.CarService;

public interface CarServiceRepository extends JpaRepository<CarService, Long> {
}
