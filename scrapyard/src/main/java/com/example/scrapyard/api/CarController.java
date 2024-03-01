package com.example.scrapyard.api;

import com.example.scrapyard.api.exceptions.AuthenticationException;
import com.example.scrapyard.api.exceptions.BrandNotFoundException;
import com.example.scrapyard.api.exceptions.CarNotFoundException;
import com.example.scrapyard.domain.CarDTO;
import com.example.scrapyard.domain.CarResponse;
import com.example.scrapyard.model.Car;
import com.example.scrapyard.service.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.*;

@Tag(name = "Scrapyard", description = "the Scrapyard API.")
@RestController
@RequestMapping(path = "/car")
public class CarController {
    private final ModelMapper modelMapper;
    private final CarService carService;
    public CarController(CarService carService, ModelMapper modelMapper) {
        this.carService = carService;
        this.modelMapper = modelMapper;
    }

    @Operation(
            summary = "Create new car.",
            description = "Adds a new car object to the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "successful operation"),
            @ApiResponse(responseCode = "400", description = "incorrect request")
    })
    @PostMapping("")
    public ResponseEntity<URI> addNewCar(
            @Valid @RequestBody CarDTO carDTO,
            @RequestHeader("Authorization") String token) throws AuthenticationException, BrandNotFoundException {
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").
                buildAndExpand(carService.saveCar(carDTO, token).getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @Operation(
            summary = "Fetch all cars.",
            description = "fetches all car entities and their data from data source.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation")
    })
    @GetMapping(path = "", produces = "application/json")
    public ResponseEntity<List<CarResponse>> getAllCars(@RequestHeader("guid") UUID guid) {
        // map Car to Car DTO
        List<CarResponse> carResponse = carService.getCars().stream()
                .map(car -> modelMapper.map(car, CarResponse.class))
                .toList();
        return ResponseEntity.ok(carResponse);
    }

    @Operation(
            summary = "Get a car by id.",
            description = "Returns car with given id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "400", description = "Bad request. Incorrect id format."),
            @ApiResponse(responseCode = "404", description = "Car with provided id not found."),
    })
    @GetMapping(path = "{id}")
    public ResponseEntity<CarResponse> getCar(
            @RequestHeader("guid") UUID guid,
            @PathVariable("id")
            @Parameter(name = "id", description = "Car uuid", example = "dc84f2ca-5f63-46ec-8c62-7f59fbab5db7") UUID carId)
            throws CarNotFoundException {
        Optional<Car> retrievedCar = carService.getCarById(carId);

        if (retrievedCar.isPresent())
            return ResponseEntity.ok(modelMapper.map(retrievedCar.get(), CarResponse.class));
        else throw CarNotFoundException.createWith(carId);
    }

    @Operation(
            summary = "Get a sum of all cars' prices.",
            description = "Returns a number which is a sum of all cars' prices.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "400", description = "Bad request."),
    })
    @GetMapping(path = "/sum")
    public ResponseEntity<Map<String,Double>> sumAllCars(@RequestHeader("guid") UUID guid) {
        double carResponse = carService.getCarPricesSum();
        Map<String,Double> response = new HashMap<>();
        response.put("sum", carResponse);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Delete a car by id.",
            description = "Deletes and returns a car with given id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No content"),
            @ApiResponse(responseCode = "400", description = "Bad request. Incorrect id format."),
            @ApiResponse(responseCode = "404", description = "Car with provided id not found."),
    })
    @DeleteMapping(path = "{id}")
    public ResponseEntity<?> deleteCar(
            @RequestHeader("guid") UUID guid,
            @PathVariable("id")
            @Parameter(name = "id", description = "Car uuid", example = "dc84f2ca-5f63-46ec-8c62-7f59fbab5db7") UUID carId)
            throws CarNotFoundException {

        Optional<Car> deletedCar = carService.deleteCarById(carId);

        if (deletedCar.isPresent())
            return ResponseEntity.noContent().build();
        else throw CarNotFoundException.createWith(carId);

    }
}

