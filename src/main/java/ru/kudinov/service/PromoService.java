package ru.kudinov.service;

import org.springframework.stereotype.Service;
import ru.kudinov.model.Promo;
import ru.kudinov.repository.PromoRepository;

@Service
public class PromoService {

    private final PromoRepository promoRepository;

    public PromoService(PromoRepository promoRepository) {
        this.promoRepository = promoRepository;
    }

    public void savePromo(Promo promo) {
        promoRepository.save(promo);
    }
}
