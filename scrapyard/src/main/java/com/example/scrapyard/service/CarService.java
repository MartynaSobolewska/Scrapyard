package com.example.scrapyard.service;

import com.example.scrapyard.api.exceptions.AuthenticationException;
import com.example.scrapyard.api.exceptions.BrandNotFoundException;
import com.example.scrapyard.domain.CarDTO;
import com.example.scrapyard.model.Car;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CarService {
    // C
    Car saveCar(CarDTO car, String authToken) throws AuthenticationException, BrandNotFoundException;

    // R
    Optional<Car> getCarById(UUID id);
    List<Car> getCars();

    //U
    Car updateCar(Car car, Long id);

    // D
    Optional<Car> deleteCarById(UUID id);

    double getCarPricesSum();
}
