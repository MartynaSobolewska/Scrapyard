package com.scrapyard.authservice.api;

import com.scrapyard.authservice.api.DTOs.LoginDTO;
import com.scrapyard.authservice.api.DTOs.RegisterDTO;
import com.scrapyard.authservice.api.DTOs.AuthResponse;
import com.scrapyard.authservice.api.exceptions.CustomAuthException;
import com.scrapyard.authservice.api.exceptions.CustomInternalServerError;
import com.scrapyard.authservice.api.exceptions.UsernameExistsException;
import com.scrapyard.authservice.service.auth.AuthOpsService;
import com.scrapyard.authservice.service.auth.TokenService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthOpsService authOpsService;
    private final TokenService tokenService;

    public AuthController(AuthOpsService authOpsService, TokenService tokenService) {
        this.authOpsService = authOpsService;
        this.tokenService = tokenService;
    }

    // To be accessed by the gateway only.
    // Returns a server token, corresponding to bearer token.
    // Server token is used internally - only by scrapyard microservices (inaccessible to the user)
    @PostMapping(path ="login", produces = "application/json")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginDTO loginDTO) throws CustomAuthException, CustomInternalServerError {
        String serverToken = authOpsService.login(loginDTO);
        AuthResponse loginResponse = AuthResponse.builder().token(serverToken).build();
        return ResponseEntity.ok(loginResponse);
    }

    // To be accessed by the gateway only.
    // passing bearer token to the user.
    @RequestMapping(value = "register", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterDTO registerDTO) throws CustomInternalServerError, UsernameExistsException {
        String token = authOpsService.register(registerDTO);
        AuthResponse registerResponse = AuthResponse.builder().token(token).build();
        return ResponseEntity.ok(registerResponse);
    }
}
