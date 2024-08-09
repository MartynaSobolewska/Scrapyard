package com.scrapyard.authservice.service.auth;

import com.scrapyard.authservice.DAOs.models.Role;
import com.scrapyard.authservice.DAOs.models.TokenPair;
import com.scrapyard.authservice.DAOs.models.UserEntity;
import com.scrapyard.authservice.api.DTOs.LoginDTO;
import com.scrapyard.authservice.api.DTOs.RegisterDTO;
import com.scrapyard.authservice.api.exceptions.CustomAuthException;
import com.scrapyard.authservice.api.exceptions.CustomInternalServerError;
import com.scrapyard.authservice.api.exceptions.UsernameExistsException;
import com.scrapyard.authservice.service.user.CustomUserDetailsServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthOpsService {
    final ClientTokenHelper clientTokenHelper;
    final CustomUserDetailsServiceImpl userRepository;
    final TokenService tokenService;
    final ServerTokenHelper serverTokenHelper;

    public AuthOpsService(ClientTokenHelper clientTokenHelper, CustomUserDetailsServiceImpl userRepository, TokenService tokenService, ServerTokenHelper serverTokenHelper) {
        this.clientTokenHelper = clientTokenHelper;
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.serverTokenHelper = serverTokenHelper;
    }

    public String register(RegisterDTO registerDTO) throws CustomInternalServerError, UsernameExistsException {
        if (registerDTO == null || registerDTO.getUsername() == null || registerDTO.getPassword() == null ||
                registerDTO.getUsername().trim().isEmpty() || registerDTO.getPassword().trim().isEmpty()) {
            throw CustomInternalServerError.createWith("Incorrect registration data.");
        }
        UserEntity user = userRepository.registerNewUserAccount(registerDTO);
        String[] roles = user.getRoles().stream().map(Role::getName).toArray(String[]::new);
        String bearerToken = clientTokenHelper.generateToken(user.getUsername());
        String serverToken = serverTokenHelper.generateToken(user.getUsername(), roles);
        tokenService.saveTokenPair(bearerToken, serverToken);
        return bearerToken;
    }

    public String login(LoginDTO loginDTO) throws CustomAuthException, CustomInternalServerError {
        Optional<TokenPair> optionalTokenPair = tokenService.getTokenPairByBearerToken(loginDTO.getBearerToken());
        if (optionalTokenPair.isEmpty()) {
            throw CustomAuthException.createWith("No such bearer token exists in the database.");
        }
        return optionalTokenPair.get().getServerToken();
    }
}
