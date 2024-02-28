package com.example.scrapyard.api;

import com.example.scrapyard.api.CarController;
import com.example.scrapyard.auth.JwtGenerator;
import com.example.scrapyard.domain.CarDTO;
import com.example.scrapyard.model.Car;
import com.example.scrapyard.service.CarServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(CarController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class CarControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarServiceImpl carService;

    @Autowired
    private ObjectMapper objectMapper;

    private Car car;
    private CarDTO carDTO;

    private static HttpHeaders headers;

    @BeforeAll
    static void init(){

        headers = new HttpHeaders();
        headers.set("guid", UUID.randomUUID().toString());
    }

    @BeforeEach
    void setup() {
        car = Car.builder()
                .brand("Ford")
                .model("Focus")
                .price(1200)
                .yearOfProduction(1998)
                .build();

        carDTO = CarDTO.builder()
                .brand("Ford")
                .model("Focus")
                .price(1200)
                .yearOfProduction(1998)
                .build();

    }
    @Test
    @DisplayName("Add and return valid car object")
    public void givenCar_whenCreateCar_thenReturnSavedCar() throws Exception{
        when(carService.saveCar(any(Car.class))).thenReturn(car);

        ResultActions response = mockMvc.perform(post("/car")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(carDTO))
        );

        response.andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Adding a car object with missing model")
    public void givenCarWithNoModel_whenCreateCar_thenReturn400() throws Exception {
        // GIVEN - a valid car, mock service response
        carDTO.setModel(null);
        when(carService.saveCar(any(Car.class))).thenReturn(car);

        // WHEN - we call the add car endpoint with car object
        ResultActions response = mockMvc.perform(post("/car")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers)
                .content(objectMapper.writeValueAsString(carDTO))
        );

        // THEN - verify that the returned car is correct
        response.andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.errors.[0]", is("model: must not be empty")));
    }

    @Test
    @DisplayName("Adding a car object with one letter model")
    public void givenCarWithShortModel_whenCreateCar_thenReturn400() throws Exception {
        // GIVEN - a valid car, mock service response
        carDTO.setModel("a");
        when(carService.saveCar(any(Car.class))).thenReturn(car);

        // WHEN - we call the add car endpoint with car object
        ResultActions response = mockMvc.perform(post("/car")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers)
                .content(objectMapper.writeValueAsString(carDTO))
        );

        // THEN - verify that the returned car is correct
        response.andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.errors.[0]", is("model: should have at least 2 characters.")));
    }

    @Test
    @DisplayName("Adding a car object with missing brand")
    public void givenCarWithNoBrand_whenCreateCar_thenReturn400() throws Exception {
        // GIVEN - a valid car, mock service response
        carDTO.setBrand(null);
        when(carService.saveCar(any(Car.class))).thenReturn(car);

        // WHEN - we call the add car endpoint with car object
        ResultActions response = mockMvc.perform(post("/car")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers)
                .content(objectMapper.writeValueAsString(carDTO))
        );

        // THEN - verify that the returned car is correct
        response.andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.errors.[0]", is("brand: must not be empty")));
    }

    @Test
    @DisplayName("Adding a car object with 1 letter brand")
    public void givenCarWithEmptyBrand_whenCreateCar_thenReturn400() throws Exception {
        // GIVEN - a valid car, mock service response
        carDTO.setBrand("a");
        when(carService.saveCar(any(Car.class))).thenReturn(car);

        // WHEN - we call the add car endpoint with car object
        ResultActions response = mockMvc.perform(post("/car")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers)
                .content(objectMapper.writeValueAsString(carDTO))
        );

        // THEN - verify that the returned car is correct
        response.andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.errors.[0]", is("brand: should have at least 2 characters.")));
    }

    @Test
    @DisplayName("Adding a car object with too small year of production")
    public void givenCarWithSmallYearOfProduction_whenCreateCar_thenReturn400() throws Exception {
        // GIVEN - a valid car, mock service response
        carDTO.setYearOfProduction(-10);
        when(carService.saveCar(any(Car.class))).thenReturn(car);

        // WHEN - we call the add car endpoint with car object
        ResultActions response = mockMvc.perform(post("/car")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers)
                .content(objectMapper.writeValueAsString(carDTO))
        );

        // THEN - verify that the returned car is correct
        response.andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.errors.[0]", is("yearOfProduction: should be between 1800 and 2023.")));
    }

    @Test
    @DisplayName("Adding a car object with too large year of production")
    public void givenCarWithLargeYearOfProduction_whenCreateCar_thenReturn400() throws Exception {
        // GIVEN - a valid car, mock service response
        carDTO.setYearOfProduction(4000);
        when(carService.saveCar(any(Car.class))).thenReturn(car);

        // WHEN - we call the add car endpoint with car object
        ResultActions response = mockMvc.perform(post("/car")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers)
                .content(objectMapper.writeValueAsString(carDTO))
        );

        // THEN - verify that the returned car is correct
        response.andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.errors.[0]", is("yearOfProduction: should be between 1800 and 2023.")));
    }

    @Test
    @DisplayName("Adding a car object with negative price")
    public void givenCarWithNegativePrice_whenCreateCar_thenReturn400() throws Exception {
        // GIVEN - a valid car, mock service response
        carDTO.setPrice(-10);
        when(carService.saveCar(any(Car.class))).thenReturn(car);

        // WHEN - we call the add car endpoint with car object
        ResultActions response = mockMvc.perform(post("/car")
                .headers(headers)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(carDTO))
        );

        // THEN - verify that the returned car is correct
        response.andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.errors.[0]", is("price: should be larger than 0.")));
    }

    @Test
    @DisplayName("Get all cars with no cars")
    public void givenNoCars_whenGetAllCars_thenReturnEmptyList() throws Exception {
        // GIVEN - no cars

        // WHEN - we call the add car endpoint with car object
        ResultActions response = mockMvc.perform(get("/car")
                        .headers(headers)
                .contentType(MediaType.APPLICATION_JSON));

        // THEN - verify that response is 200 and empty list
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(List.of())));
    }

    @Test
    @DisplayName("Get all cars with 2 cars saved")
    public void givenTwoSavedCars_whenGetAllCars_thenReturnListWithThoseCars() throws Exception {
        Car car1 = Car.builder()
                .brand("Kia")
                .model("Rio")
                .price(1300)
                .yearOfProduction(2008)
                .build();

        when(carService.getCars()).thenReturn(List.of(car, car1));

        // WHEN - we call the add car endpoint with car object
        ResultActions response = mockMvc.perform(get("/car")
                .headers(headers)
                .contentType(MediaType.APPLICATION_JSON));

        // THEN - verify that response is 200 and empty list
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$.[*].brand", containsInAnyOrder("Kia", "Ford")))
                .andExpect(jsonPath("$.[*].model", containsInAnyOrder("Rio", "Focus")));
    }

    @Test
    @DisplayName("Get sum of car prices when no cars.")
    public void givenNoCars_whenSumAllCars_thenReturn0() throws Exception {
        ResultActions response = mockMvc.perform(get("/car/sum")
                .headers(headers)
                .contentType(MediaType.APPLICATION_JSON));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sum", is(0.0)));
    }

    @Test
    @DisplayName("Get sum of car prices when car prices' sum is not 0.")
    public void givenTwoCars_whenSumAllCars_thenReturnSumOfPrices() throws Exception {
        // GIVEN - car sum is 2000.1
        when(carService.getCarPricesSum()).thenReturn(2000.1);


        // WHEN - we call the add car endpoint with car object
        ResultActions response = mockMvc.perform(get("/car/sum")
                .headers(headers)
                .contentType(MediaType.APPLICATION_JSON));

        // THEN - verify that response is 200 and empty list
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sum", is(2000.1)));
    }

    @Test
    @DisplayName("Get nonexistent car by id.")
    public void givenNonexistentId_whenGetCarById_thenReturnErrorMessage() throws Exception {
        UUID id =  UUID.randomUUID();
        ResultActions response = mockMvc.perform(get("/car/" + id)
                .headers(headers));

        response.andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.errors.[0]" , is("Car with id '"+ id + "' not found")));
    }

    @Test
    @DisplayName("Get existent car by id.")
    public void givenExistentId_whenGetCarById_thenReturnErrorMessage() throws Exception {
        UUID id = UUID.randomUUID();
        when(carService.getCarById(id)).thenReturn(Optional.of(car));

        // WHEN - we call the add car endpoint with car object
        ResultActions response = mockMvc.perform(get("/car/" + id)
                .headers(headers));

        // THEN - verify that response is 200 and empty list
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brand", is(car.getBrand())))
                .andExpect(jsonPath("$.model", is(car.getModel())))
                .andExpect(jsonPath("$.yearOfProduction", is(car.getYearOfProduction())))
                .andExpect(jsonPath("$.price", is(car.getPrice())));
    }

    @Test
    @DisplayName("Delete nonexistent car by id.")
    public void givenNonexistentId_whenDeleteCar_thenReturnErrorMessage() throws Exception {
        UUID id = UUID.randomUUID();
        when(carService.deleteCarById(car.getId())).thenReturn(Optional.empty());

        ResultActions response = mockMvc.perform(delete("/car/" + id)
                .headers(headers));

        response.andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.errors.[0]",
                        is("Car with id '"+ id + "' not found")));
    }

    @Test
    @DisplayName("Delete existing car by id.")
    public void givenExistentId_whenDeleteCar_thenReturnDeletedCar() throws Exception {
        UUID id = UUID.randomUUID();
        when(carService.deleteCarById(id)).thenReturn(Optional.of(car));

        ResultActions response = mockMvc.perform(delete("/car/" + id)
                .headers(headers));

        response.andDo(print())
                .andExpect(status().is2xxSuccessful());
    }
}
