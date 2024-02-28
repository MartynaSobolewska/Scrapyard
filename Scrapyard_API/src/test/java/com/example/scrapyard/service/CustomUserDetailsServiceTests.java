package com.example.scrapyard.service;

import com.example.scrapyard.api.exceptions.DatabaseException;
import com.example.scrapyard.api.exceptions.UsernameExistsException;
import com.example.scrapyard.domain.RegisterDTO;
import com.example.scrapyard.model.Role;
import com.example.scrapyard.model.UserEntity;
import com.example.scrapyard.repository.RoleRepository;
import com.example.scrapyard.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTests {

    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private CustomUserDetailsServiceImpl userService;


    RegisterDTO registerDTO1;
    UserEntity userEntity1;

    @BeforeEach
    void setUp() {
        registerDTO1 = RegisterDTO.builder()
                .username("xyz@abc.com")
                .password("xyzabc123")
                .build();
        userEntity1 = UserEntity.builder()
                .id(UUID.randomUUID())
                .username(registerDTO1.getUsername())
                .passwordHash(Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8().encode(registerDTO1.getPassword()))
                .build();
    }

    @Test
    void registerNewUserWhenEmailIsUnique() throws UsernameExistsException, DatabaseException {
        given(roleRepository.findByName("USER")).willReturn(Optional.of(Role.builder().name("USER").build()));
        ArgumentCaptor<UserEntity> argument = ArgumentCaptor.forClass(UserEntity.class);
        ArgumentCaptor<String> passwordArgument = ArgumentCaptor.forClass(String.class);
        when(userRepository.save(any())).thenReturn(userEntity1);
        when(passwordEncoder.encode(registerDTO1.getPassword())).thenReturn(Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8().encode(registerDTO1.getPassword()));

        UserEntity userEntity = userService.registerNewUserAccount(registerDTO1);

        verify(userRepository).save(argument.capture());
        verify(passwordEncoder).encode(passwordArgument.capture());

        UserEntity calledRepoWith = argument.getValue();
        Assertions.assertEquals(calledRepoWith.getUsername(), userEntity1.getUsername());
        Assertions.assertEquals(passwordArgument.getValue(), registerDTO1.getPassword());
        Assertions.assertNotNull(userEntity.getPasswordHash());
    }

    @Test
    void throwErrorWhenRegisteringUserWithNonUniqueEmail(){
        given(userRepository.findByUsername(registerDTO1.getUsername())).willReturn(Optional.of(userEntity1));
        Assertions.assertThrows(UsernameExistsException.class, () -> userService.registerNewUserAccount(registerDTO1));
    }


}