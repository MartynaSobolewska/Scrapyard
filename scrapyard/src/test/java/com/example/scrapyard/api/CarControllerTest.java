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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.UUID;

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

    private CarDTO goodCarDTO = CarDTO.builder()
                .model("Yaris")
                .brand("Toyota")
                .price(1200.2)
                .yearOfProduction(2001)
                .build();
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
    private CarDTO badCarDTO = CarDTO.builder().build();

    private Car badCar = Car.builder().build();

    private Gson gson;

    @BeforeEach
    void setup() {
        gson = new Gson();
    }

    @Test
    @WithMockUser(username = "testadmin", password = "testadminsecret", roles = {"USER", "ADMIN"})
    void whenAuthorisedAndValidRequestBodyCreateCarReturns201() throws Exception {
        Car goodCar = new Car(); // Assuming Car is your entity class
        given(service.saveCar(any(CarDTO.class), any(String.class))).willReturn(goodCar);
        System.out.println(gson.toJson(goodCarDTO));

        // When and Then
        mockMvc.perform(post("/car")
                        .header("Authorization", "Bearer " + jwtGenerator.createTestToken("testadmin"))
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
}