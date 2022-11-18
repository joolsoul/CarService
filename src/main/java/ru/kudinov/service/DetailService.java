package ru.kudinov.service;

import org.springframework.stereotype.Service;
import ru.kudinov.model.Detail;
import ru.kudinov.model.enums.DetailType;
import ru.kudinov.repository.DetailRepository;

import java.util.List;
import java.util.Objects;

@Service
public class DetailService {

    private final DetailRepository detailRepository;

    public DetailService(DetailRepository detailRepository) {
        this.detailRepository = detailRepository;
    }

    public List<Detail> findByDetailType(DetailType detailType) {
        return detailRepository.findByDetailType(detailType);
    }

    public Detail getByName(String name) {
        return detailRepository.findByName(name);
    }

    public Detail findById(Long detailId) {
        if (detailRepository.findById(detailId).isPresent()) {
            return detailRepository.findById(detailId).get();
        }
        return null;
    }

    public boolean saveDetail(Detail detail) {

        Detail detailFromDb = getByName(detail.getName());

        if (detailFromDb != null) {
            return false;
        }

        detailRepository.save(detail);
        return true;
    }

    public List<Detail> allDetails() {
        return detailRepository.findAll();
    }

    public boolean deleteDetail(Detail detail) {
        if (detailRepository.findById(detail.getId()).isPresent()) {
            detailRepository.deleteById(detail.getId());

            return true;
        }
        return false;
    }

    public void updateDetail(Detail detail) {
        detailRepository.save(detail);
    }

    public boolean isDataCorrectly(Detail detail) {
        return detail.getName() != null && !Objects.equals(detail.getName(), "")
                && detail.getDescription() != null && !Objects.equals(detail.getDescription(), "")
                && detail.getQuantity() != null
                && detail.getPrice() != null
                && detail.getDetailType() != null;
    }
}
