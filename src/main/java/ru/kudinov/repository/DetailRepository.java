package ru.kudinov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kudinov.model.Detail;
import ru.kudinov.model.enums.DetailType;

import java.util.List;

public interface DetailRepository extends JpaRepository<Detail, Long> {
    List<Detail> findByDetailType(DetailType detailType);

    Detail findByName(String name);
}
