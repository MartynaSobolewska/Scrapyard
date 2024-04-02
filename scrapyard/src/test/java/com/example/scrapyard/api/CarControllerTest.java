package com.example.scrapyard.api;

import com.example.scrapyard.auth.JwtGenerator;
import com.example.scrapyard.domain.CarDTO;
import com.example.scrapyard.model.Brand;
import com.example.scrapyard.model.Car;
import com.example.scrapyard.model.Model;
import com.example.scrapyard.service.CarService;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

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

    @Test
    void whenNotAuthorisedAndValidRequestBodyCreateCarReturns403() throws Exception {
        Car goodCar = new Car(); // Assuming Car is your entity class
        given(service.saveCar(any(CarDTO.class), any(String.class))).willReturn(goodCar);
        System.out.println(gson.toJson(goodCarDTO));

        // response we expect - the same as from global exception handler's would be despite it being handled in a filter

        // When and Then
        mockMvc.perform(post("/car")
                        .header("Authorization", "Bearer "  + invalidToken)
                        .header("guid", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(goodCarDTO))
                        .characterEncoding("utf-8")
                )
                .andExpect(status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.[0]").value("Authentication unsuccessful: incorrect token"));
    }

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
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors",  hasSize(1))) // Assuming you expect a 201 status code
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.[0]").value("brand: must not be empty")); // Assuming you expect a 201 status code
    }

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
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors",  hasSize(1))) // Assuming you expect a 201 status code
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.[0]").value("brand: should have at least 2 characters")); // Assuming you expect a 201 status code
    }

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
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors",  hasSize(1))) // Assuming you expect a 201 status code
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.[0]").value("model: must not be empty")); // Assuming you expect a 201 status code
    }

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
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors",  hasSize(1))) // Assuming you expect a 201 status code
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.[0]").value("model: should have at least 2 characters")); // Assuming you expect a 201 status code
    }

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
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors",  hasSize(1))) // Assuming you expect a 201 status code
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.[0]").value("yearOfProduction: should be between 1800 and 2024")); // Assuming you expect a 201 status code
    }

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
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors",  hasSize(1))) // Assuming you expect a 201 status code
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.[0]").value("yearOfProduction: should be between 1800 and 2024")); // Assuming you expect a 201 status code
    }

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
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors",  hasSize(1))) // Assuming you expect a 201 status code
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.[0]").value("price: should be larger than 0")); // Assuming you expect a 201 status code
    }

    @Test
    void givenAuthorisedAsUserOnlyWhenDeleteCarReturns403() throws Exception {
        mockMvc.perform(delete("/car/{id}", UUID.randomUUID())
                        .header("Authorization", "Bearer " + jwtGenerator.createTestToken("testadmin"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenAuthorisedAsAdminWhenDeleteCarReturns200() throws Exception {
        given(service.deleteCarById(any(UUID.class))).willReturn(Optional.of(goodCar));


        mockMvc.perform(delete("/car/{id}", UUID.randomUUID())
                        .header("guid", UUID.randomUUID())
                        .header("Authorization", "Bearer " + jwtGenerator.createTestToken("testadmin", true))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void givenAuthorisedAndCarNonexistentAsAdminWhenDeleteCarReturns404() throws Exception {
        UUID uuid = UUID.randomUUID();
        given(service.deleteCarById(uuid)).willReturn(Optional.empty());


        mockMvc.perform(delete("/car/{id}", uuid)
                        .header("guid", UUID.randomUUID())
                        .header("Authorization", "Bearer " + jwtGenerator.createTestToken("testadmin", true))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors",  hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.[0]").value("Car with id '" + uuid + "' not found"));
    }

    // TODO: test sum, get car, fetch all cars
}