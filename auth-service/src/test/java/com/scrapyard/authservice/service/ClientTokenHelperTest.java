package com.scrapyard.authservice.service;

import com.scrapyard.authservice.api.exceptions.CustomInternalServerError;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestPropertySource(locations = "classpath:application-test.properties")
class ClientTokenHelperTest {
    @Autowired
    private ClientTokenHelper clientTokenHelper;

    @Test
    void testGenerateToken() throws CustomInternalServerError {
        // Arrange
        String username = "test";

        // Act
        String token = clientTokenHelper.generateToken(username);

        // Assert
        assertNotNull(token);
        assertTrue(clientTokenHelper.validateToken(token));
    }

}