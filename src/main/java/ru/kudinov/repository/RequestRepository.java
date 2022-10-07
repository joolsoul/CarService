package ru.kudinov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kudinov.model.Request;

public interface RequestRepository extends JpaRepository<Request, Long> {
}
