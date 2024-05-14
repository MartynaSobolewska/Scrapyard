package com.example.scrapyard.auth;

import com.example.scrapyard.repository.UserRepository;
import com.example.scrapyard.service.CustomUserDetailsServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.TestPropertySource;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestPropertySource(locations = "classpath:application-test.properties")
class JwtGeneratorTest {

//    @Autowired
//    private CustomUserDetailsServiceImpl userDetailsService;
    @Autowired
    private JwtGenerator jwtGenerator;

    @Nested
    @DisplayName("register tests")
    class RegisterTests{
        @Test
        void givenCorrectAuthReturnsJwt(){
            // Arrange
            String username = "test";

            // Act
            String jwt = jwtGenerator.generateBearerToken(username);

            // Assert
            Assertions.assertNotNull(jwt);
            Assertions.assertTrue(jwt.contains(username));
        }
    }

}