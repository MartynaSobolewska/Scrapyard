package com.example.scrapyard.domain;

import lombok.*;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginDTO {
    String username;
    String password;
}