package com.example.scrapyard.repository;

import com.example.scrapyard.model.Car;
import com.example.scrapyard.repository.CarRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class CarRepositoryTests {

    Car car1 = Car.builder()
            .price(3000)
            .model("Focus")
            .brand("Ford")
            .yearOfProduction(1990)
            .build();

    Car car2 = Car.builder()
            .price(60000)
            .model("Passat")
            .brand("Volkswagen")
            .yearOfProduction(2001)
            .build();

    @Autowired
    CarRepository carRepository;

    @BeforeEach
    public void setUp() {
        carRepository.save(car1);
        carRepository.save(car2);
    }

    @Test
    void testGetAllCars() {
        List<Car> carList = carRepository.findAll();
        Assertions.assertThat(carList.size()).isEqualTo(2);
        Assertions.assertThat(carList.get(0).getBrand()).isEqualTo(car1.getBrand());
        Assertions.assertThat(carList.get(0).getModel()).isEqualTo(car1.getModel());
        Assertions.assertThat(carList.get(0).getId()).isNotNull();
    }

    @Test
    public void testGetInvalidCar() {
        Exception exception = assertThrows(NoSuchElementException.class, () -> carRepository.findById(UUID.randomUUID()).get());
        Assertions.assertThat(exception).isNotNull();
        Assertions.assertThat(exception.getClass()).isEqualTo(NoSuchElementException.class);
        Assertions.assertThat(exception.getMessage()).isEqualTo("No value present");
    }

    @Test
    public void testGetCreateCar() {
        Car saved = Car.builder()
                .price(200)
                .yearOfProduction(2000)
                .brand("X")
                .model("Y")
                .build();
        Car returned = carRepository.save(saved);
        Assertions.assertThat(returned).isNotNull();
        Assertions.assertThat(returned.getBrand()).isEqualTo(saved.getBrand());
        Assertions.assertThat(returned.getModel()).isEqualTo(saved.getModel());
        Assertions.assertThat(saved.getYearOfProduction()).isEqualTo(returned.getYearOfProduction());
        Assertions.assertThat(returned.getId()).isNotNull();
    }

    @Test
    public void testDeleteCar() {
        Car saved = Car.builder()
                .price(200)
                .yearOfProduction(2000)
                .brand("X")
                .model("Y")
                .build();
        carRepository.save(saved);
        carRepository.delete(saved);
        Exception exception = assertThrows(NoSuchElementException.class, () -> carRepository.findById(UUID.randomUUID()).get());
        Assertions.assertThat(exception).isNotNull();
        Assertions.assertThat(exception.getClass()).isEqualTo(NoSuchElementException.class);
        Assertions.assertThat(exception.getMessage()).isEqualTo("No value present");
    }
    @AfterEach
    public void destroy() {
        carRepository.deleteAll();
    }

}
