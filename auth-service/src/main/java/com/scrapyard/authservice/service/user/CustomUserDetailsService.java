package com.scrapyard.authservice.service.user;

import com.scrapyard.authservice.DAOs.models.UserEntity;
import com.scrapyard.authservice.api.DTOs.RegisterDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface CustomUserDetailsService extends UserDetailsService {
    UserEntity registerNewUserAccount(RegisterDTO accountDto) throws UsernameExistsException, CustomAuthException, DatabaseException;
}

