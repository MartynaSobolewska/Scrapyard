package com.example.scrapyard.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BrandDTO {
    @Size(min = 2, message = "should have at least 2 characters.")
    @Schema(name="Brand name", example = "Ford")
    private String name;
}
