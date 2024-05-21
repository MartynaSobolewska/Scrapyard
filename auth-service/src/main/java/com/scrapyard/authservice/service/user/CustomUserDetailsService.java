package com.scrapyard.authservice.service.user;

import com.scrapyard.authservice.DAOs.models.UserEntity;
import com.scrapyard.authservice.api.DTOs.RegisterDTO;
import com.scrapyard.authservice.api.exceptions.CustomAuthException;
import com.scrapyard.authservice.api.exceptions.DatabaseException;
import com.scrapyard.authservice.api.exceptions.UsernameExistsException;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface CustomUserDetailsService extends UserDetailsService {
    UserEntity registerNewUserAccount(RegisterDTO accountDto) throws UsernameExistsException, CustomAuthException, DatabaseException;
}

