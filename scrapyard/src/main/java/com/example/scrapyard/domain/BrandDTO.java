package com.example.scrapyard.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BrandDTO implements Serializable {
    @NotEmpty
    @Size(min = 2, message = "should have at least 2 characters.")
    @Schema(name="name", example = "Ford")
    private String name;
}
