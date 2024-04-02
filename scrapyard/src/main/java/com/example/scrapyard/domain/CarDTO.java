package com.example.scrapyard.domain;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Size(min = 2, message = "should have at least 2 characters")
    @Schema(name="Car model", example = "Focus")
    private String model;
    @NotEmpty
    @Size(min = 2, message = "should have at least 2 characters")
    @Schema(name="Car brand", example = "Ford")
    private String brand;
    @Min(value = 1800, message = "should be between 1800 and 2024")
    @Max(value = 2024, message = "should be between 1800 and 2024")
    @NotNull
    @Schema(name="Car year of production", example = "2001")
    private Integer yearOfProduction;
    @DecimalMin(value = "1.0", message = "should be larger than 0")
    @NotNull
    @Schema(name = "Car price", example = "2000")
    private double price;
}
