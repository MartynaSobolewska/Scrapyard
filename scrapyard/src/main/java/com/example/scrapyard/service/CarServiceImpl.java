package com.example.scrapyard.service;

import com.example.scrapyard.api.exceptions.BrandNotFoundException;
import com.example.scrapyard.api.exceptions.CustomAuthException;
import com.example.scrapyard.auth.JwtGenerator;
import com.example.scrapyard.domain.CarDTO;
import com.example.scrapyard.model.Brand;
import com.example.scrapyard.model.Model;
import com.example.scrapyard.model.UserEntity;
import com.example.scrapyard.repository.BrandRepository;
import com.example.scrapyard.repository.CarRepository;
import com.example.scrapyard.model.Car;
import com.example.scrapyard.repository.ModelRepository;
import com.example.scrapyard.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CarServiceImpl implements CarService{
    private final CarRepository carRepository;
    private final BrandRepository brandRepository;
    private final ModelRepository modelRepository;
    private final JwtGenerator jwtGenerator;
    private final UserRepository userRepository;

    public CarServiceImpl(CarRepository carRepository, BrandRepository brandRepository, ModelRepository modelRepository, JwtGenerator jwtGenerator, UserRepository userRepository) {
        this.carRepository = carRepository;
        this.brandRepository = brandRepository;
        this.modelRepository = modelRepository;
        this.jwtGenerator = jwtGenerator;
        this.userRepository = userRepository;
    }

    @Override
    public Car saveCar(CarDTO car, String authToken) throws CustomAuthException, BrandNotFoundException {
        if (brandRepository.findByName(car.getBrand()).isPresent()){
            if (!authToken.startsWith("Bearer ")){
                throw CustomAuthException.createWith("Invalid token format.");
            }
            String username = jwtGenerator.getUsernameFromJwt(authToken.split(" ")[1]);
            if (username == null){
                throw CustomAuthException.createWith("Invalid token format.");
            }
            if (userRepository.findByUsername(username).isEmpty()){
                throw CustomAuthException.createWith("Incorrect user information.");
            }
            UserEntity user = userRepository.findByUsername(username).get();
            Brand brand = brandRepository.findByName(car.getBrand()).get();
            Model model;
            if (modelRepository.findByName(car.getModel()).isPresent())
                model = modelRepository.findByName(car.getModel()).get();
            else
                model = modelRepository.save(Model.builder().name(car.getModel()).build());
            Car carEntity = Car.builder()
                    .price(car.getPrice())
                    .yearOfProduction(car.getYearOfProduction())
                    .model(model)
                    .brand(brand)
                    .user(user).build();
            return carRepository.save(carEntity);
        }
        throw BrandNotFoundException.createWith(car.getBrand());
    }

    @Override
    public Optional<Car> getCarById(UUID id) {
        return carRepository.findById(id);
    }

    @Override
    public List<Car> getCars() {
        return carRepository.findAll();
    }

    @Override
    public Car updateCar(Car car, Long id) {
        return null;
    }

    @Override
    public Optional<Car> deleteCarById(UUID id) {
        Optional<Car> car = carRepository.findById(id);
        carRepository.deleteById(id);
        return car;
    }

    public double getCarPricesSum() {
        return getCars().stream().mapToDouble(Car::getPrice).sum();
    }
}
