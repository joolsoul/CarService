package ru.kudinov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kudinov.model.Detail;
import ru.kudinov.model.enums.entityEnums.DetailType;

import java.util.List;

public interface DetailRepository extends JpaRepository<Detail, Long> {
    List<Detail> findByDetailTypeAndIsActive(DetailType detailType, boolean isActive);

    Detail findByName(String name);

    List<Detail> findAllByIsActive(boolean isActive);
}
