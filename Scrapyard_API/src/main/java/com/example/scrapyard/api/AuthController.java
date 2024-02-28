package com.example.scrapyard.api;

import com.example.scrapyard.api.exceptions.UsernameExistsException;
import com.example.scrapyard.auth.JwtGenerator;
import com.example.scrapyard.domain.AuthResponseDTO;
import com.example.scrapyard.domain.LoginDTO;
import com.example.scrapyard.domain.RegisterDTO;
import com.example.scrapyard.service.CustomUserDetailsServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authorization Controller", description = "Allows to perform JWT auth operations.")
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final CustomUserDetailsServiceImpl userService;
    private final AuthenticationManager authenticationManager;

    private final JwtGenerator jwtGenerator;

    public AuthController(CustomUserDetailsServiceImpl userService, AuthenticationManager authenticationManager, JwtGenerator jwtGenerator) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtGenerator = jwtGenerator;
    }

    @PostMapping("register")
    public ResponseEntity<String> register(@RequestBody RegisterDTO registerDTO) throws UsernameExistsException {
        userService.registerNewUserAccount(registerDTO);
        return ResponseEntity.ok("Account created.");
    }

    @PostMapping("login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginDTO loginDTO) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getUsername(),
                        loginDTO.getPassword(),
                        userService.loadUserByUsername(loginDTO.getUsername()).getAuthorities()));

        SecurityContextHolder.getContext().setAuthentication(auth);
        String token = jwtGenerator.generateToken(auth);
        return ResponseEntity.ok(new AuthResponseDTO(token));
    }
}
