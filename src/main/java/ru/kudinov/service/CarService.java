package ru.kudinov.service;

import org.springframework.stereotype.Service;
import ru.kudinov.model.Car;
import ru.kudinov.model.User;
import ru.kudinov.repository.CarRepository;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class CarService {

    private final CarRepository carRepository;

    public CarService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public List<Car> findByOwner(User owner) {
        return carRepository.findByOwner(owner);
    }

    public Car findById(Long carId) {
        if (carRepository.findById(carId).isPresent()) {
            return carRepository.findById(carId).get();
        }
        return null;
    }

    public Car findByRegistrationNumber(String registrationNumber) {
        return carRepository.findByRegistrationNumber(registrationNumber);
    }

    public boolean saveCar(Car car) {

        Car carFromDb = findByRegistrationNumber(car.getRegistrationNumber());

        if (carFromDb != null) {
            return false;
        }

        carRepository.save(car);
        return true;
    }

    public boolean editCar(Car car) {

        if (!isCarDataCorrectly(car)) {
            return false;
        }

        carRepository.save(car);
        return true;
    }

    private final String REGEX = "^[АВЕКМНОРСТУХ]\\d{3}[АВЕКМНОРСТУХ]{2}\\d{2,3}";

    public boolean isCarDataCorrectly(Car car) {

        return Pattern.matches(REGEX, car.getRegistrationNumber());
    }

    public boolean deleteCar(Car car) {
        if (carRepository.findById(car.getId()).isPresent()) {
            carRepository.deleteById(car.getId());
            return true;
        }
        return false;
    }

    public boolean deleteCar(Long carId) {
        if (carRepository.findById(carId).isPresent()) {
            carRepository.deleteById(carId);
            return true;
        }
        return false;
    }


    public abstract static class CarNotFoundException extends RuntimeException {
        public CarNotFoundException(String message) {
            super(message);
        }

        public CarNotFoundException(String message, Throwable cause) {
            super(message, cause);
        }
    }

}
