package com.scrapyard.authservice.api.DTOs;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegisterResponse {
    String token;
}
