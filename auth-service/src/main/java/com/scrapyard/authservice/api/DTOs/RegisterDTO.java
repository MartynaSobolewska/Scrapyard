package com.scrapyard.authservice.api.DTOs;


import lombok.*;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterDTO {
    String username;
    String password;
}
