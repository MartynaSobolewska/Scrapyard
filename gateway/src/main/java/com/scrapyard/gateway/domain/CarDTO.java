package com.scrapyard.gateway.domain;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CarDTO {
    private UUID id;
    @NotEmpty
    @Size(min = 2, message = "should have at least 2 characters.")
    private String model;
    @NotEmpty
    @Size(min = 2, message = "should have at least 2 characters.")
    private String brand;
    @Min(value = 1800, message = "should be between 1800 and 2023.")
    @Max(value = 2023, message = "should be between 1800 and 2023.")
    @NotNull
    private Integer yearOfProduction;
    @DecimalMin(value = "1.0", message = "should be larger than 0.")
    @NotNull
    private double price;
}
