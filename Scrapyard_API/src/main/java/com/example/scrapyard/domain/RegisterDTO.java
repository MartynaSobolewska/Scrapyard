package com.example.scrapyard.domain;

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
