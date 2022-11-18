package ru.kudinov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kudinov.model.Promo;

public interface PromoRepository extends JpaRepository<Promo, Long> {
}
