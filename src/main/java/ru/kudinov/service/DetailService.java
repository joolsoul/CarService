package ru.kudinov.service;

import org.springframework.stereotype.Service;
import ru.kudinov.model.Detail;
import ru.kudinov.model.enums.DetailType;
import ru.kudinov.repository.DetailRepository;

import java.util.List;

@Service
public class DetailService {

    private final DetailRepository detailRepository;

    public DetailService(DetailRepository detailRepository) {
        this.detailRepository = detailRepository;
    }

    public List<Detail> findByDetailType(DetailType detailType) {
        return detailRepository.findByDetailType(detailType);
    }

    public Detail findById(Long detailId) {
        if (detailRepository.findById(detailId).isPresent()) {
            return detailRepository.findById(detailId).get();
        }
        return null;
    }
}
