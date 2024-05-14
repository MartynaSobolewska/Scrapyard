package com.example.scrapyard.service;

import com.example.scrapyard.api.exceptions.CustomAuthException;
import com.example.scrapyard.api.exceptions.DatabaseException;
import com.example.scrapyard.api.exceptions.UsernameExistsException;
import com.example.scrapyard.domain.RegisterDTO;
import com.example.scrapyard.model.UserEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface CustomUserDetailsService extends UserDetailsService {
    UserEntity registerNewUserAccount(RegisterDTO accountDto) throws UsernameExistsException, CustomAuthException, DatabaseException;
}
