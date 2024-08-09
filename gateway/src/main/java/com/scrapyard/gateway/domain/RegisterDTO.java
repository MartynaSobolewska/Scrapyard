package com.scrapyard.gateway.domain;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class RegisterDTO {
    @NotEmpty
    @NotNull
    @Size(min = 3, message = "Should have at least 3 characters")
    String username;
    @NotEmpty
    @NotNull
    @Size(min = 8, message = "Should have at least 8 characters")
    String password;
}
