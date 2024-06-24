package com.scrapyard.authservice.service.auth;

import com.scrapyard.authservice.DAOs.models.Role;
import com.scrapyard.authservice.DAOs.models.UserEntity;
import com.scrapyard.authservice.api.DTOs.RegisterDTO;
import com.scrapyard.authservice.api.exceptions.CustomInternalServerError;
import com.scrapyard.authservice.api.exceptions.UsernameExistsException;
import com.scrapyard.authservice.service.user.CustomUserDetailsServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class AuthOpsService {
    final CustomUserDetailsServiceImpl userRepository;
    final TokenService tokenService;

    public AuthOpsService(CustomUserDetailsServiceImpl userRepository, TokenService tokenService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    public String register(RegisterDTO registerDTO) throws CustomInternalServerError, UsernameExistsException {
        if (registerDTO == null || registerDTO.getUsername() == null || registerDTO.getPassword() == null ||
                registerDTO.getUsername().trim().isEmpty() || registerDTO.getPassword().trim().isEmpty()) {
            throw CustomInternalServerError.createWith("Incorrect registration data.");
        }
        UserEntity user = userRepository.registerNewUserAccount(registerDTO);
        String[] roles = user.getRoles().stream().map(Role::getName).toArray(String[]::new);
        String bearerToken = ClientTokenHelper.generateToken(user.getUsername());
        String serverToken = ServerTokenHelper.generateToken(user.getUsername(), roles);
        tokenService.saveTokenPair(bearerToken, serverToken);
        return bearerToken;
    }
}