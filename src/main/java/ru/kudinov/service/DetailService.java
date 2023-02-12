package ru.kudinov.service;

import org.springframework.stereotype.Service;
import ru.kudinov.model.Detail;
import ru.kudinov.model.enums.entityEnums.DetailType;
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
        return detailRepository.findByDetailTypeAndIsActive(detailType, true);
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

        if (detailFromDb != null && detailFromDb.isActive()) {
            return false;
        }
        if (detailFromDb != null && !detailFromDb.isActive()) {
            detailFromDb.setActive();
            detailFromDb.setDetailType(detail.getDetailType());
            detailFromDb.setImages(detail.getImages());
            detailFromDb.setQuantity(detail.getQuantity());
            detailFromDb.setDescription(detail.getDescription());
            detailFromDb.setPrice(detailFromDb.getPrice());
            detailRepository.save(detailFromDb);
            return true;
        }

        detail.setActive();
        detailRepository.save(detail);
        return true;
    }

    public List<Detail> allDetails() {
        return detailRepository.findAllByIsActive(true);
    }

    public List<Detail> getNonActiveDetails() {
        return detailRepository.findAllByIsActive(false);
    }

    public boolean deleteDetail(Detail detail) {
        if (detailRepository.findById(detail.getId()).isPresent()) {
            detail.setNonActive();
            detailRepository.save(detail);
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
