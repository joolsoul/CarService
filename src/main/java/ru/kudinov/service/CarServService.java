package ru.kudinov.service;

import org.springframework.stereotype.Service;
import ru.kudinov.repository.CarServiceRepository;

@Service
public class CarServService {

    private final CarServiceRepository carServiceRepository;

    public CarServService(CarServiceRepository carServiceRepository) {
        this.carServiceRepository = carServiceRepository;
    }
}
