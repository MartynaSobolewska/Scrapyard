package com.scrapyard.authservice.service.auth;

import com.scrapyard.authservice.DAOs.models.Role;
import com.scrapyard.authservice.DAOs.models.TokenPair;
import com.scrapyard.authservice.DAOs.models.UserEntity;
import com.scrapyard.authservice.api.DTOs.RegisterDTO;
import com.scrapyard.authservice.api.exceptions.CustomInternalServerError;
import com.scrapyard.authservice.api.exceptions.UsernameExistsException;
import com.scrapyard.authservice.service.user.CustomUserDetailsServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestPropertySource(locations = "classpath:application-test.properties")
class AuthOpsServiceTest {
    @Mock TokenService tokenService;
    @Mock CustomUserDetailsServiceImpl userDetailsService;
    @Mock ClientTokenHelper clientTokenHelper;
    @Mock ServerTokenHelper serverTokenHelper;

    @InjectMocks
    AuthOpsService authOpsService;

    RegisterDTO correctRegisterDTO = RegisterDTO.builder().username("test_username").password("test_password").build();
    List<Role> roles =  List.of(Role.builder().name("USER").id(UUID.randomUUID()).build());
    UserEntity user = UserEntity.builder().username("test_username").passwordHash("test_password_hash").roles(roles).build();


    @Nested
    @DisplayName("Register tests")
    class RegisterTests {
        @Test
        void givenNullRegisterDTOThrowError() {
            assertThrows(CustomInternalServerError.class, () -> authOpsService.register(null));
        }

        @Test
        void givenNullUsernameThrowError() {
            RegisterDTO registerDTO = RegisterDTO.builder().username(null).password("<PASSWORD>").build();
            assertThrows(CustomInternalServerError.class, () -> authOpsService.register(registerDTO));
        }

        @Test
        void givenNullPasswordThrowError() {
            RegisterDTO registerDTO = RegisterDTO.builder().username("test_usrname").password(null).build();
            assertThrows(CustomInternalServerError.class, () -> authOpsService.register(registerDTO));
        }

        @Test
        void givenEmptyUsernameThrowError() {
            RegisterDTO registerDTO = RegisterDTO.builder().username("  ").password(null).build();
            assertThrows(CustomInternalServerError.class, () -> authOpsService.register(registerDTO));
        }

        @Test
        void givenEmptyPasswordThrowError() {
            RegisterDTO registerDTO = RegisterDTO.builder().username(" something ").password("  ").build();
            assertThrows(CustomInternalServerError.class, () -> authOpsService.register(registerDTO));
        }

        @Test
        void givenUserWithUsernameExistsThrowError() throws UsernameExistsException {
            when(userDetailsService.registerNewUserAccount(correctRegisterDTO)).thenThrow(UsernameExistsException.class);
            assertThrows(UsernameExistsException.class, () -> authOpsService.register(correctRegisterDTO));
        }

        @Test
        void givenCorrectRegisterDTOReturnCorrectTokenPair() throws UsernameExistsException, CustomInternalServerError {
            when(userDetailsService.registerNewUserAccount(correctRegisterDTO)).thenReturn(user);
            when(tokenService.saveTokenPair(anyString(), anyString()))
                    .thenReturn(TokenPair.builder().bearerToken("bearerToken").serverToken("serverToken").build());


            String bearerTokenResult = authOpsService.register(correctRegisterDTO);

            assertTrue(ClientTokenHelper.isValid(bearerTokenResult));
            assertEquals(ClientTokenHelper.getUsername(bearerTokenResult), user.getUsername());
            verify(tokenService, times(1)).saveTokenPair(anyString(), anyString());
        }

    }
}