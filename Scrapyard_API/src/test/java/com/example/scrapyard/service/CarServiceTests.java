package com.example.scrapyard.service;

import com.example.scrapyard.repository.CarRepository;
import com.example.scrapyard.model.Car;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CarServiceTests {
    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private CarServiceImpl carService;

    private Car car;

    @BeforeEach
    public void setup() {

        car = Car.builder()
                .brand("Ford")
                .model("Focus")
                .price(1200)
                .yearOfProduction(1998)
                .build();
    }

    @Test
    @DisplayName("Saving a correct car")
    void givenCar_whenSaveCar_thenReturnCar(){
        given(carRepository.save(car)).willReturn(car);

        // WHEN we save the car
        Car savedCar = carService.saveCar(car);
        System.out.println(savedCar);

        // THEN saved car should be the same as the car we passed
        assertThat(savedCar).isEqualTo(car);
    }

    @Test
    @DisplayName("Getting nonexistent car by id")
    void givenNonexistentCar_whenGetCar_thenReturnCar(){
        // WHEN we get preexisting car
        Optional<Car> foundCar = carService.getCarById(car.getId());


        // THEN saved car should be the same as the car we passed
        assertThat(foundCar).isEqualTo(Optional.empty());
    }

    @Test
    @DisplayName("Getting existent car by id")
    void givenExistingCar_whenGetCar_thenReturnCar(){
        // GIVEN car exists in the repository
//        given(carRepository.existsById(car.getId())).willReturn(true);
        given(carRepository.findById(car.getId())).willReturn(Optional.ofNullable(car));

        // WHEN we get preexisting car
        Optional<Car> foundCar = carService.getCarById(car.getId());
        System.out.println(foundCar);

        // THEN saved car should be the same as the car we passed
        assertThat(foundCar.get()).isEqualTo(car);
    }

    @Test
    @DisplayName("Getting a list of all cars (len. 2)")
    void given2SavedCars_whenGetAll_thenReturnListOf2(){
        // GIVEN 2 cars in scrapyard
        Car car2 = Car.builder()
                .brand("ABC")
                .model("EFG")
                .yearOfProduction(1999)
                .price(200F)
                .build();
        List<Car> cars = List.of(car, car2);
        given(carRepository.findAll()).willReturn(cars);

        // WHEN we find all
        List<Car> foundCars = carService.getCars();

        // THEN we should have a list of two cars
        assertThat(foundCars).isEqualTo(cars);
    }

    @Test
    @DisplayName("Getting a list of all cars (len. 0)")
    void givenNoSavedCars_whenGetAll_thenReturnEmptyList(){
        // GIVEN 0 cars in scrapyard

        // WHEN we find all
        List<Car> foundCars = carService.getCars();

        // THEN we should have a list of two cars
        assertThat(foundCars).isEmpty();
    }

    @Test
    @DisplayName("Getting the price sum when no cars")
    void givenNoSavedCars_whenGetSum_thenReturn0(){
        // GIVEN 0 cars in scrapyard

        // WHEN we sum all
        double carsSum = carService.getCarPricesSum();

        // Then we should get 0
        assertThat(carsSum).isEqualTo(0);
    }

    @Test
    @DisplayName("Getting the price sum when one car")
    void givenSavedCar_whenGetSum_thenReturnItsPrice(){
        // GIVEN car in scrapyard
        given(carRepository.findAll()).willReturn(List.of(car));

        // WHEN we sum all
        double carsSum = carService.getCarPricesSum();

        // Then we should car's price
        assertThat(carsSum).isEqualTo(car.getPrice());
    }

    @Test
    @DisplayName("Getting the price sum when multiple cars")
    void givenSavedCars_whenGetSum_thenReturnSumOfPrices(){
        // GIVEN car in scrapyard
        Car car1 = Car.builder()
                .brand("X")
                .model("Y")
                .yearOfProduction(1999)
                .price(200)
                .build();
        Car car2 = Car.builder()
                .brand("X")
                .model("Y")
                .yearOfProduction(1999)
                .price(1.99)
                .build();

        List<Car> cars = List.of(car, car1, car2);
        given(carRepository.findAll()).willReturn(cars);

        double sum = car.getPrice() + car1.getPrice() + car2.getPrice();

        // WHEN we sum all
        double carsSum = carService.getCarPricesSum();

        // Then we should car's price
        assertThat(carsSum).isEqualTo(sum);
    }

    @Test
    @DisplayName("Deleting the car when car doesn't exist")
    void givenNoCar_whenDeleteCar_thenReturn(){
        // GIVEN no car in scrapyard

        // WHEN we delete nonexistent car
        Optional<Car> deletedCar = carService.deleteCarById(UUID.randomUUID());

        // Then we should get an empty optional
        assertThat(deletedCar).isEqualTo(Optional.empty());
    }

    @Test
    @DisplayName("Deleting the car when car exists")
    void givenExistentCarId_whenDeleteCar_thenReturn(){
        // GIVEN car with this id exists in scrapyard
        given(carRepository.findById(car.getId())).willReturn(Optional.of(car));

        // WHEN we delete nonexistent car
        Optional<Car> deletedCar = carService.deleteCarById(car.getId());

        // Then we should get an empty optional
        assertThat(deletedCar).isEqualTo(Optional.of(car));
    }
}
