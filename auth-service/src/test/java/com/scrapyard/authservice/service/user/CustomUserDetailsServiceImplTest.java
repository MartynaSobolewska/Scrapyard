package com.scrapyard.authservice.service.user;

import com.scrapyard.authservice.DAOs.RoleRepository;
import com.scrapyard.authservice.DAOs.UserRepository;
import com.scrapyard.authservice.DAOs.models.Role;
import com.scrapyard.authservice.DAOs.models.UserEntity;
import com.scrapyard.authservice.api.DTOs.RegisterDTO;
import com.scrapyard.authservice.api.exceptions.DatabaseException;
import com.scrapyard.authservice.api.exceptions.UsernameExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;


import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "classpath:application-test.properties")
class CustomUserDetailsServiceImplTest {
    @Mock UserRepository userRepository;
    @Mock RoleRepository roleRepository;

    @Mock PasswordEncoder passwordEncoder;
    @Captor ArgumentCaptor<UserEntity> userEntityCaptor;
    @Captor ArgumentCaptor<Role> roleArgumentCaptor;

    @InjectMocks CustomUserDetailsServiceImpl userDetailsService;

    private final Role userRole = Role.builder().name("USER").id(UUID.randomUUID()).build();
    private final RegisterDTO validRegisterDto = new RegisterDTO("test_username", "test_password");
    private final String encodedTestPassword = "encoded_test_password";


    @Nested
    @DisplayName("Register user tests")
    class RegisterUserTest {
        @Test
        void givenValidDtoSaveToDb() throws UsernameExistsException {
            // Arrange
            when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
            when(userRepository.findByUsername("test_username")).thenReturn(Optional.empty());
            when(passwordEncoder.encode("test_password")).thenReturn(encodedTestPassword);

            // Act
            userDetailsService.registerNewUserAccount(validRegisterDto);

            // Assert
            verify(roleRepository, times(1)).findByName("USER");
            verify(userRepository).save(userEntityCaptor.capture());
            UserEntity capturedUser = userEntityCaptor.getValue();
            assertEquals(validRegisterDto.getUsername(), capturedUser.getUsername());
            assertEquals(encodedTestPassword, capturedUser.getPasswordHash());
            assertEquals(userRole.getName(), capturedUser.getRoles().get(0).getName());
        }

        @Test
        void givenExistingUsernameThrowError(){
            // Arrange
            when(userRepository.findByUsername("test_username")).thenReturn(Optional.of(UserEntity.builder().build()));

            // Act & Assert
            assertThrows(UsernameExistsException.class, () -> userDetailsService.registerNewUserAccount(validRegisterDto));
        }

        @Test
        void givenUserRoleDoesntExistCreateIt() throws UsernameExistsException {
            // Arrange
            when(roleRepository.findByName("USER")).thenReturn(Optional.empty());
            when(roleRepository.save(any(Role.class))).thenReturn(userRole);
            when(userRepository.findByUsername("test_username")).thenReturn(Optional.empty());
            when(passwordEncoder.encode("test_password")).thenReturn(encodedTestPassword);

            // Act
            userDetailsService.registerNewUserAccount(validRegisterDto);

            // Assert that user role was created
            verify(roleRepository, times(1)).save(any(Role.class));
            verify(roleRepository, times(1)).findByName("USER");
            verify(roleRepository).save(roleArgumentCaptor.capture());
            assertEquals(roleArgumentCaptor.getValue().getName(), userRole.getName());

            // Assert saved user is correct
            verify(userRepository).save(userEntityCaptor.capture());
            UserEntity capturedUser = userEntityCaptor.getValue();
            assertEquals(validRegisterDto.getUsername(), capturedUser.getUsername());
            assertEquals(encodedTestPassword, capturedUser.getPasswordHash());
            assertEquals(userRole.getName(), capturedUser.getRoles().get(0).getName());
        }
    }
}