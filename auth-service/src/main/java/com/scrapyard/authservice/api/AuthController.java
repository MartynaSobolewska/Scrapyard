package com.scrapyard.authservice.api;

import com.scrapyard.authservice.api.DTOs.LoginDTO;
import com.scrapyard.authservice.api.exceptions.CustomInternalServerError;
import com.scrapyard.authservice.service.auth.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@Tag(name = "Authorization Controller", description = "Allows to perform JWT auth operations.")
@RestController
@RequestMapping("/auth")
public class AuthController {
//    private final CustomUserDetailsServiceImpl userService;
//    private final AuthenticationManager authenticationManager;

    private final TokenService tokenService;

    public AuthController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("login")
    public ResponseEntity login(@RequestBody LoginDTO loginDTO) throws CustomInternalServerError {
//        String token = jwtGenerator.generateClientToken(loginDTO.getUsername());
//        return ResponseEntity.ok(new AuthResponseDTO(token));
        return ResponseEntity.ok().build();
    }
}
