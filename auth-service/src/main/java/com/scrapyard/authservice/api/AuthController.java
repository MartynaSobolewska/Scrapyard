package com.scrapyard.authservice.api;

import com.scrapyard.authservice.api.DTOs.LoginDTO;
import com.scrapyard.authservice.api.DTOs.RegisterDTO;
import com.scrapyard.authservice.api.DTOs.RegisterResponse;
import com.scrapyard.authservice.api.exceptions.CustomInternalServerError;
import com.scrapyard.authservice.api.exceptions.UsernameExistsException;
import com.scrapyard.authservice.service.auth.AuthOpsService;
import com.scrapyard.authservice.service.auth.TokenService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//@Tag(name = "Authorization Controller", description = "Allows to perform JWT auth operations.")
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthOpsService authOpsService;
    private final TokenService tokenService;

    public AuthController(AuthOpsService authOpsService, TokenService tokenService) {
        this.authOpsService = authOpsService;
        this.tokenService = tokenService;
    }

    @PostMapping(path ="login", produces = "application/json")
    public ResponseEntity login(@RequestBody LoginDTO loginDTO) throws CustomInternalServerError {
//        String token = jwtGenerator.generateClientToken(loginDTO.getUsername());
//        return ResponseEntity.ok(new AuthResponseDTO(token));
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "register", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterDTO registerDTO) throws CustomInternalServerError, UsernameExistsException {
        String token = authOpsService.register(registerDTO);
        RegisterResponse registerResponse = RegisterResponse.builder().token(token).build();
        return ResponseEntity.ok(registerResponse);
    }
}
