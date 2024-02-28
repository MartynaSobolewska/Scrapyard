package com.example.scrapyard;

import com.example.scrapyard.auth.JwtGenerator;
import com.example.scrapyard.domain.CarDTO;
import com.example.scrapyard.model.Car;
import com.example.scrapyard.model.Role;
import com.example.scrapyard.model.UserEntity;
import com.example.scrapyard.repository.CarRepository;
import com.example.scrapyard.repository.RoleRepository;
import com.example.scrapyard.repository.UserRepository;
import com.example.scrapyard.service.CarService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
class ScrapyardAPIIntegrationTests {
        @LocalServerPort
        private int port;

        @Autowired
        private TestRestTemplate restTemplate;

        @Autowired
        private CarRepository carRepository;

        @Autowired
        private CarService carService;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private RoleRepository roleRepository;

        @Autowired
        private JwtGenerator jwtGenerator;

        private static HttpHeaders headers;

        private final ObjectMapper objectMapper = new ObjectMapper();

        @BeforeEach
        public void addUser(){
                Role role = Role.builder().name("USER").build();
                UserEntity entity = UserEntity.builder()
                        .id(UUID.randomUUID()).username("test")
                        .passwordHash("test")
                        .roles(Collections.singletonList(role)).build();
                roleRepository.save(role);
                userRepository.save(entity);
        }


        @BeforeEach
        public void init() {
                String token = jwtGenerator.createTestToken("test");
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("guid", UUID.randomUUID().toString());
            headers.set("Authorization", "Bearer " + token);
        }
        @AfterEach
        public void destroy() {
                carRepository.deleteAll();
        }


        private String createURLWithPort() {
            return "http://localhost:" + port + "/car";
        }

        @Test
        void givenScrapyardAPI_whenCallGetCars_ReturnedListTheSameAsRepository() {
                // When - call get all endpoint
                HttpEntity<String> entity = new HttpEntity<>(null, headers);
                ResponseEntity<List<CarDTO>> response = restTemplate.exchange(
                        createURLWithPort(), HttpMethod.GET, entity, new ParameterizedTypeReference<>(){}
                );
                // Then - returned list shouldn't be null, be of the same size as the one in repo
                List<CarDTO> carList = response.getBody();
                System.out.println(carList);
                assert carList != null;
                Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
                Assertions.assertEquals(carList.size(), carService.getCars().size());
                Assertions.assertEquals(carList.size(), carRepository.findAll().size());
        }

        @Test
        void givenScrapyardAPI_whenAddCar_RepositoryHasIt() throws JsonProcessingException {
                CarDTO carDTO = CarDTO.builder()
                        .yearOfProduction(2000)
                        .price(10000)
                        .brand("Ford")
                        .model("Focus")
                        .build();

                HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(carDTO), headers);
                ResponseEntity<CarDTO> response = restTemplate.exchange(
                        createURLWithPort(), HttpMethod.POST, entity, new ParameterizedTypeReference<>(){}
                );
                // Then - returned car shouldn't be null, be of the same as the one in repo, and the one in request
                assert response.getHeaders().get("Location") != null;
                String location = response.getHeaders().get("Location").get(0);
                System.out.println(location);

                HttpEntity<String> getCar = new HttpEntity<>(null, headers);
                ResponseEntity<CarDTO> carResponse = restTemplate.exchange(
                        location, HttpMethod.GET, getCar, new ParameterizedTypeReference<>(){}
                );
                CarDTO car = carResponse.getBody();

                assert carRepository.findById(car.getId()).isPresent();

                Car repoCar = carRepository.findById(car.getId()).get();
                Assertions.assertEquals(repoCar.getYearOfProduction(), carDTO.getYearOfProduction());
                Assertions.assertEquals(repoCar.getPrice(), carDTO.getPrice());
                Assertions.assertEquals(repoCar.getModel(), carDTO.getModel());
                Assertions.assertEquals(repoCar.getBrand(), carDTO.getBrand());

                Assertions.assertEquals(repoCar.getYearOfProduction(), car.getYearOfProduction());
                Assertions.assertEquals(repoCar.getPrice(), car.getPrice());
                Assertions.assertEquals(repoCar.getModel(), car.getModel());
                Assertions.assertEquals(repoCar.getBrand(), car.getBrand());
        }

        @Test
        void givenScrapyardHasCar_whenDeleteCar_CarNotThere(){
                // given Car
                Car car = Car.builder()
                        .yearOfProduction(2000)
                        .price(10000)
                        .brand("Ford")
                        .model("Focus")
                        .build();

                Car savedCar = carService.saveCar(car);

                // when request delete
                HttpEntity<String> entity = new HttpEntity<>(null, headers);
                ResponseEntity<Car> response = restTemplate.exchange(
                        createURLWithPort()+"/"+ savedCar.getId(), HttpMethod.DELETE, entity, new ParameterizedTypeReference<>(){}
                );
                // Then - returned car shouldn't be null, be of the same as the one in repo, and the one in request
                Assertions.assertTrue(response.getStatusCode().is2xxSuccessful());
                Assertions.assertTrue(carService.getCarById(car.getId()).isEmpty());
        }

        @Test
        void givenScrapyardHasNoCar_whenDeleteCar_Return404(){
                // when request delete
                HttpEntity<String> entity = new HttpEntity<>(null, headers);
                ResponseEntity<Car> response = restTemplate.exchange(
                        createURLWithPort()+"/1", HttpMethod.DELETE, entity, new ParameterizedTypeReference<>(){}
                );
                // Then - returned car shouldn't be null, be of the same as the one in repo, and the one in request
                Assertions.assertTrue(response.getStatusCode().is4xxClientError());

                Car responseBody = response.getBody();
                System.out.println(responseBody);
        }

        @Test
        void givenScrapyardHasCarX_whenGetCarX_CarXReturned(){
                // given Car
                Car car = Car.builder()
                        .yearOfProduction(2000)
                        .price(10000)
                        .brand("Ford")
                        .model("Focus")
                        .build();

                Car savedCar = carService.saveCar(car);

                // when request delete
                HttpEntity<String> entity = new HttpEntity<>(null, headers);
                ResponseEntity<Car> response = restTemplate.exchange(
                        createURLWithPort()+"/"+ savedCar.getId(), HttpMethod.GET, entity, new ParameterizedTypeReference<>(){}
                );
                // Then - returned car shouldn't be null, be of the same as the one in repo, and the one in request
                assert response.getStatusCode().is2xxSuccessful();

                Car responseCar = response.getBody();
                System.out.println(responseCar);

                assert responseCar != null;

                Assertions.assertEquals(car.getYearOfProduction(), responseCar.getYearOfProduction());
                Assertions.assertEquals(car.getPrice(), responseCar.getPrice());
                Assertions.assertEquals(car.getModel(), responseCar.getModel());
                Assertions.assertEquals(car.getBrand(), responseCar.getBrand());
        }

        @Test
        void givenScrapyardHasNoCar_whenGetCar_Return404(){
                // when request delete
                HttpEntity<String> entity = new HttpEntity<>(null, headers);
                ResponseEntity<Car> response = restTemplate.exchange(
                        createURLWithPort()+"/1", HttpMethod.GET, entity, new ParameterizedTypeReference<>(){}
                );
                // Then - returned car shouldn't be null, be of the same as the one in repo, and the one in request
                Assertions.assertTrue(response.getStatusCode().is4xxClientError());

                Car responseBody = response.getBody();
                System.out.println(responseBody);
        }

        @Test
        void givenNoCars_WhenSum_Return0(){
                // when sum
                HttpEntity<String> entity = new HttpEntity<>(null, headers);
                ResponseEntity<Map<String,Double>> response = restTemplate.exchange(
                        createURLWithPort()+"/sum", HttpMethod.GET, entity, new ParameterizedTypeReference<>(){}
                );
                // Then - returned car shouldn't be null, be of the same as the one in repo, and the one in request
                Assertions.assertTrue(response.getStatusCode().is2xxSuccessful());

                Double responseBody = response.getBody().get("sum");
                System.out.println(responseBody);
                assert responseBody == 0.0;
        }

        @Test
        void givenCars_WhenSum_ReturnSumOfPrices(){
                // given cars
                Car car0 = Car.builder()
                        .yearOfProduction(2000)
                        .price(10000)
                        .brand("Ford")
                        .model("Focus")
                        .build();
                Car car1 = Car.builder()
                        .yearOfProduction(2000)
                        .price(1999.99)
                        .brand("X")
                        .model("Y")
                        .build();
                carService.saveCar(car0);
                carService.saveCar(car1);

                // when sum
                HttpEntity<String> entity = new HttpEntity<>(null, headers);
                ResponseEntity<Map<String, Double>> response = restTemplate.exchange(
                        createURLWithPort()+"/sum", HttpMethod.GET, entity, new ParameterizedTypeReference<>(){}
                );
                System.out.println(response);

                // Then - sum of prices should be returned
                Assertions.assertTrue(response.getStatusCode().is2xxSuccessful());

                Double responseBody = response.getBody().get("sum");
                System.out.println(responseBody);
                Assertions.assertEquals(responseBody, (car0.getPrice() + car1.getPrice()));
        }

}
