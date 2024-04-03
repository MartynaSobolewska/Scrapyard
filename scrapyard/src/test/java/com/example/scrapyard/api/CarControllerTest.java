package com.example.scrapyard.api;

import com.example.scrapyard.auth.JwtGenerator;
import com.example.scrapyard.domain.CarDTO;
import com.example.scrapyard.model.*;
import com.example.scrapyard.service.CarService;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureMockMvc
class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtGenerator jwtGenerator;

    @MockBean
    private CarService service;

    private String invalidToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6ImludmFsaWQgand0IHRva2VuIiwiaWF0IjoxNTE2MjM5MDIyfQ.EbNcDTfTgPJn5LsfLUlba_Tdzmhmd4L4b0tmqUnH3BM";

    private CarDTO goodCarDTO;
    private Car goodCar = Car.builder()
            .id(UUID.randomUUID())
            .model(
                    Model.builder()
                            .id(UUID.randomUUID())
                            .name("Yaris").build()
            )
            .brand(
                    Brand.builder()
                            .name("Toyota")
                            .id(UUID.randomUUID())
                            .build()
            )
            .price(1200.2)
            .yearOfProduction(2001)
            .user(
                    UserEntity.builder()
                            .id(UUID.randomUUID())
                            .roles(Collections.singletonList(Role.builder().name("USER").id(UUID.randomUUID()).build()))
                            .username("xyz").passwordHash("xyz").build())
            .build();
    private CarDTO badCarDTO;

    private Car badCar = Car.builder().build();

    private Gson gson;

    @BeforeEach
    void setup() {
        goodCarDTO = CarDTO.builder()
                .model("Yaris")
                .brand("Toyota")
                .price(1200.2)
                .yearOfProduction(2001)
                .build();
        badCarDTO = CarDTO.builder().build();
        gson = new Gson();
    }

    @Nested
    @DisplayName("POST /car tests")
    class CreateCarTests {
        @DisplayName("USER auth, valid car -> 201")
        @Test
        void whenAuthorisedAndValidRequestBodyCreateCarReturns201() throws Exception {
            Car goodCar = new Car(); // Assuming Car is your entity class
            given(service.saveCar(any(CarDTO.class), any(String.class))).willReturn(goodCar);

            // When and Then
            mockMvc.perform(post("/car")
                            .header("Authorization", "Bearer " + jwtGenerator.createTestToken("testuser"))
                            .header("guid", UUID.randomUUID())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(goodCarDTO))
                            .characterEncoding("utf-8")
                    )
                    .andExpect(status().isCreated()); // Assuming you expect a 201 status code
        }

        @DisplayName("no auth, valid car -> 401")
        @Test
        void whenNotAuthorisedAndValidRequestBodyCreateCarReturns401() throws Exception {
            Car goodCar = new Car(); // Assuming Car is your entity class
            given(service.saveCar(any(CarDTO.class), any(String.class))).willReturn(goodCar);
            System.out.println(gson.toJson(goodCarDTO));

            // response we expect - the same as from global exception handler's would be despite it being handled in a filter

            // When and Then
            mockMvc.perform(post("/car")
                            .header("Authorization", "Bearer " + invalidToken)
                            .header("guid", UUID.randomUUID())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(goodCarDTO))
                            .characterEncoding("utf-8")
                    )
                    .andExpect(status().isUnauthorized())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.errors.[0]").value("Authentication unsuccessful: incorrect token"));
        }

        @DisplayName("USER auth, no brand field -> 400")
        @Test
        void whenAuthorisedAndCarDTOLacksBrandCreateCarReturns400() throws Exception {
            goodCarDTO.setBrand(null);

            given(service.saveCar(any(CarDTO.class), any(String.class))).willReturn(goodCar);

            // When and Then
            mockMvc.perform(post("/car")
                            .header("Authorization", "Bearer " + jwtGenerator.createTestToken("testuser"))
                            .header("guid", UUID.randomUUID())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(goodCarDTO))
                            .characterEncoding("utf-8")
                    )
                    .andExpect(status().is4xxClientError())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasSize(1))) // Assuming you expect a 201 status code
                    .andExpect(MockMvcResultMatchers.jsonPath("$.errors.[0]").value("brand: must not be empty")); // Assuming you expect a 201 status code
        }

        @DisplayName("USER auth, brand field too short -> 400")
        @Test
        void whenAuthorisedAndCarDTOShortBrandCreateCarReturns400() throws Exception {
            goodCarDTO.setBrand("X");

            given(service.saveCar(any(CarDTO.class), any(String.class))).willReturn(goodCar);

            // When and Then
            mockMvc.perform(post("/car")
                            .header("Authorization", "Bearer " + jwtGenerator.createTestToken("testuser"))
                            .header("guid", UUID.randomUUID())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(goodCarDTO))
                            .characterEncoding("utf-8")
                    )
                    .andExpect(status().is4xxClientError())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasSize(1))) // Assuming you expect a 201 status code
                    .andExpect(MockMvcResultMatchers.jsonPath("$.errors.[0]").value("brand: should have at least 2 characters")); // Assuming you expect a 201 status code
        }

        @DisplayName("USER auth, no model field -> 400")
        @Test
        void whenAuthorisedAndCarDTOLacksModelCreateCarReturns400() throws Exception {
            goodCarDTO.setModel(null);

            given(service.saveCar(any(CarDTO.class), any(String.class))).willReturn(goodCar);

            // When and Then
            mockMvc.perform(post("/car")
                            .header("Authorization", "Bearer " + jwtGenerator.createTestToken("testuser"))
                            .header("guid", UUID.randomUUID())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(goodCarDTO))
                            .characterEncoding("utf-8")
                    )
                    .andExpect(status().is4xxClientError())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasSize(1))) // Assuming you expect a 201 status code
                    .andExpect(MockMvcResultMatchers.jsonPath("$.errors.[0]").value("model: must not be empty")); // Assuming you expect a 201 status code
        }

        @DisplayName("USER auth, model field too short -> 400")
        @Test
        void whenAuthorisedAndCarDTOShortModelCreateCarReturns400() throws Exception {
            goodCarDTO.setModel("X");

            given(service.saveCar(any(CarDTO.class), any(String.class))).willReturn(goodCar);

            // When and Then
            mockMvc.perform(post("/car")
                            .header("Authorization", "Bearer " + jwtGenerator.createTestToken("testuser"))
                            .header("guid", UUID.randomUUID())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(goodCarDTO))
                            .characterEncoding("utf-8")
                    )
                    .andExpect(status().is4xxClientError())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasSize(1))) // Assuming you expect a 201 status code
                    .andExpect(MockMvcResultMatchers.jsonPath("$.errors.[0]").value("model: should have at least 2 characters")); // Assuming you expect a 201 status code
        }

        @DisplayName("USER auth, year of production field < 1800 -> 400")
        @Test
        void whenAuthorisedAndCarDTOTooOldCreateCarReturns400() throws Exception {
            goodCarDTO.setYearOfProduction(10);

            given(service.saveCar(any(CarDTO.class), any(String.class))).willReturn(goodCar);

            // When and Then
            mockMvc.perform(post("/car")
                            .header("Authorization", "Bearer " + jwtGenerator.createTestToken("testuser"))
                            .header("guid", UUID.randomUUID())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(goodCarDTO))
                            .characterEncoding("utf-8")
                    )
                    .andExpect(status().is4xxClientError())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasSize(1))) // Assuming you expect a 201 status code
                    .andExpect(MockMvcResultMatchers.jsonPath("$.errors.[0]").value("yearOfProduction: should be between 1800 and 2024")); // Assuming you expect a 201 status code
        }

        @DisplayName("USER auth, year of production field > now -> 400")
        @Test
        void whenAuthorisedAndCarDTOTooYoungCreateCarReturns400() throws Exception {
            goodCarDTO.setYearOfProduction(2050);

            given(service.saveCar(any(CarDTO.class), any(String.class))).willReturn(goodCar);

            // When and Then
            mockMvc.perform(post("/car")
                            .header("Authorization", "Bearer " + jwtGenerator.createTestToken("testuser"))
                            .header("guid", UUID.randomUUID())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(goodCarDTO))
                            .characterEncoding("utf-8")
                    )
                    .andExpect(status().is4xxClientError())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasSize(1))) // Assuming you expect a 201 status code
                    .andExpect(MockMvcResultMatchers.jsonPath("$.errors.[0]").value("yearOfProduction: should be between 1800 and 2024")); // Assuming you expect a 201 status code
        }

        @DisplayName("USER auth, price field < 0 -> 400")
        @Test
        void whenAuthorisedAndCarDTONegativePriceCreateCarReturns400() throws Exception {
            goodCarDTO.setPrice(-1.5);

            given(service.saveCar(any(CarDTO.class), any(String.class))).willReturn(goodCar);

            // When and Then
            mockMvc.perform(post("/car")
                            .header("Authorization", "Bearer " + jwtGenerator.createTestToken("testuser"))
                            .header("guid", UUID.randomUUID())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(goodCarDTO))
                            .characterEncoding("utf-8")
                    )
                    .andExpect(status().is4xxClientError())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasSize(1))) // Assuming you expect a 201 status code
                    .andExpect(MockMvcResultMatchers.jsonPath("$.errors.[0]").value("price: should be larger than 0")); // Assuming you expect a 201 status code
        }

    }

    @Nested
    @DisplayName("DELETE /car tests")
    class DeleteCarTests {
        @DisplayName("USER auth, valid id -> 403")
        @Test
        void givenAuthorisedAsUserOnlyWhenDeleteCarReturns403() throws Exception {
            mockMvc.perform(delete("/car/{id}", UUID.randomUUID())
                            .header("Authorization", "Bearer " + jwtGenerator.createTestToken("testadmin"))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());
        }

        @DisplayName("ADMIN auth, valid id -> 2xx")
        @Test
        void givenAuthorisedAsAdminWhenDeleteCarReturns200() throws Exception {
            given(service.deleteCarById(any(UUID.class))).willReturn(Optional.of(goodCar));


            mockMvc.perform(delete("/car/{id}", UUID.randomUUID())
                            .header("guid", UUID.randomUUID())
                            .header("Authorization", "Bearer " + jwtGenerator.createTestToken("testadmin", true))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().is2xxSuccessful());
        }

        @DisplayName("ADMIN auth, nonexistent car id -> 404")
        @Test
        void givenAuthorisedAndCarNonexistentAsAdminWhenDeleteCarReturns404() throws Exception {
            UUID uuid = UUID.randomUUID();
            given(service.deleteCarById(uuid)).willReturn(Optional.empty());


            mockMvc.perform(delete("/car/{id}", uuid)
                            .header("guid", UUID.randomUUID())
                            .header("Authorization", "Bearer " + jwtGenerator.createTestToken("testadmin", true))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasSize(1)))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.errors.[0]").value("Car with id '" + uuid + "' not found"));
        }

    }

    @Nested
    @DisplayName("GET /car/sum tests")
    class SumCarPricesTests {
        @DisplayName("ADMIN auth -> 200, sum in the response body")
        @Test
        void givenAuthorisedWhenSumCalledReturnsCorrectFormat() throws Exception {
            given(service.getCarPricesSum()).willReturn(12345.67);
            mockMvc.perform(get("/car/sum")
                            .header("guid", UUID.randomUUID())
                            .header("Authorization", "Bearer " + jwtGenerator.createTestToken("testadmin", true))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().is2xxSuccessful())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.sum").value(12345.67));
        }

        @DisplayName("USER auth -> 403")
        @Test
        void givenAuthorisedAsUserWhenSumCalledReturns403() throws Exception {
            given(service.getCarPricesSum()).willReturn(12345.67);
            mockMvc.perform(get("/car/sum")
                            .header("guid", UUID.randomUUID())
                            .header("Authorization", "Bearer " + jwtGenerator.createTestToken("testuser"))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("GET /car tests")
    class GetAllCarsTests {
        @DisplayName("USER auth -> 200, list with one car")
        @Test
        void givenAuthorisedWhenGetAllCarsReturnsCorrectFormat() throws Exception {
            List<Car> carsSingletonList = Collections.singletonList(goodCar);
            given(service.getCars()).willReturn(carsSingletonList);

            mockMvc.perform(get("/car")
                            .header("guid", UUID.randomUUID())
                            .header("Authorization", "Bearer " + jwtGenerator.createTestToken("testuser"))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().is2xxSuccessful())
                    .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id").value(goodCar.getId().toString()));
        }

        @DisplayName("USER auth -> 200, list with many cars")
        @Test
        void givenAuthorisedWhenGetAllCarsReturnsMultipleCarsInCorrectFormat() throws Exception {
            List<Car> carsSingletonList = List.of(goodCar, goodCar, goodCar);
            given(service.getCars()).willReturn(carsSingletonList);

            mockMvc.perform(get("/car")
                            .header("guid", UUID.randomUUID())
                            .header("Authorization", "Bearer " + jwtGenerator.createTestToken("testuser"))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().is2xxSuccessful())
                    .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(3)))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id").value(goodCar.getId().toString()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.[1].id").value(goodCar.getId().toString()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.[2].id").value(goodCar.getId().toString()));
        }

        @DisplayName("no auth -> 401")
        @Test
        void givenUnauthorisedWhenGetAllCarsReturns403() throws Exception {
            List<Car> carsSingletonList = Collections.singletonList(goodCar);
            given(service.getCars()).willReturn(carsSingletonList);

            mockMvc.perform(get("/car")
                            .header("guid", UUID.randomUUID())
                            .header("Authorization", "Bearer " + invalidToken)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasSize(1)));
        }
    }

    @Nested
    @DisplayName("GET /car/{id} tests")
    class GetCarByIdTests {
        @DisplayName("USER auth, valid id -> 200 OK with car")
        @Test
        void givenAuthorisedWhenGetCarByIdReturnsCorrectFormat() throws Exception {
            given(service.getCarById(any(UUID.class))).willReturn(Optional.of(goodCar));

            mockMvc.perform(get("/car/{id}", goodCar.getId())
                            .header("guid", UUID.randomUUID())
                            .header("Authorization", "Bearer " + jwtGenerator.createTestToken("testuser"))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().is2xxSuccessful())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(goodCar.getId().toString()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.model.name").value(goodCar.getModel().getName()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.model.id").value(goodCar.getModel().getId().toString()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.price").value(goodCar.getPrice()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.yearOfProduction").value(goodCar.getYearOfProduction()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.brand.name").value(goodCar.getBrand().getName()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.brand.id").value(goodCar.getBrand().getId().toString()));
        }
        @DisplayName("USER auth, invalid id -> 404")
        @Test
        void givenAuthorisedAndInvalidIdWhenGetCarByIdReturns404() throws Exception {
            given(service.getCarById(any(UUID.class))).willReturn(Optional.empty());

            mockMvc.perform(get("/car/{id}", goodCar.getId())
                            .header("guid", UUID.randomUUID())
                            .header("Authorization", "Bearer " + jwtGenerator.createTestToken("testuser"))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }
        @DisplayName("no auth, invalid id -> 401")
        @Test
        void givenUnauthorisedAndInvalidIdWhenGetCarByIdReturns401() throws Exception {
            given(service.getCarById(any(UUID.class))).willReturn(Optional.empty());

            mockMvc.perform(get("/car/{id}", goodCar.getId())
                            .header("guid", UUID.randomUUID())
                            .header("Authorization", "Bearer " + invalidToken)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());
        }
    }

}