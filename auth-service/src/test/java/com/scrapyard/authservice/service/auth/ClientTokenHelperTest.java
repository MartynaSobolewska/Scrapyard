package com.scrapyard.authservice.service.auth;

import com.scrapyard.authservice.api.exceptions.CustomInternalServerError;
import com.scrapyard.authservice.config.SecurityConstants;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestPropertySource(locations = "classpath:application-test.properties")
class ClientTokenHelperTest {
    private final Date currentDate = new Date();
    private final Date pastDate = new Date(currentDate.getTime() - SecurityConstants.JWT_EXPIRATION);
    private final Date expireDate = new Date(currentDate.getTime() + SecurityConstants.JWT_EXPIRATION);

    @Nested
    @DisplayName("client token validator tests")
    class ClientValidatorTests{
        @Test
        void givenCorrectTokenReturnTrue(){
            String clientToken = Jwts.builder()
                    .setSubject("username")
                    .setIssuedAt(currentDate)
                    .setExpiration(expireDate)
                    .signWith(SignatureAlgorithm.HS512, SecurityConstants.CLIENT_TOKEN_SECRET)
                    .compact();

            boolean correct = ClientTokenHelper.isValid(clientToken);
            Assertions.assertTrue(correct);
        }

        @Test
        void givenTokenWithoutUsernameReturnFalse() {
            String clientToken = Jwts.builder()
                    .setIssuedAt(currentDate)
                    .setExpiration(expireDate)
                    .signWith(SignatureAlgorithm.HS512, SecurityConstants.CLIENT_TOKEN_SECRET)
                    .compact();

            boolean correct = ClientTokenHelper.isValid(clientToken);
            Assertions.assertFalse(correct);
        }

        @Test
        void givenTokenWithoutExpirationReturnFalse(){
            String clientToken = Jwts.builder()
                    .setSubject("username")
                    .setIssuedAt(currentDate)
                    .signWith(SignatureAlgorithm.HS512, SecurityConstants.CLIENT_TOKEN_SECRET)
                    .compact();

            boolean correct = ClientTokenHelper.isValid(clientToken);
            Assertions.assertFalse(correct);
        }

        @Test
        void givenExpiredTokenReturnFalse(){
            String clientToken = Jwts.builder()
                    .setSubject("username")
                    .setIssuedAt(pastDate)
                    .signWith(SignatureAlgorithm.HS512, SecurityConstants.CLIENT_TOKEN_SECRET)
                    .compact();

            boolean correct = ClientTokenHelper.isValid(clientToken);
            Assertions.assertFalse(correct);
        }

        @Test
        void givenTokenWithoutIssueDateReturnFalse(){
            String clientToken = Jwts.builder()
                    .setSubject("username")
                    .setExpiration(expireDate)
                    .signWith(SignatureAlgorithm.HS512, SecurityConstants.CLIENT_TOKEN_SECRET)
                    .compact();

            boolean correct = ClientTokenHelper.isValid(clientToken);
            Assertions.assertFalse(correct);
        }

        @Test
        void givenTokenSignedWithDifferentSecretReturnFalse(){
            String clientToken = Jwts.builder()
                    .setSubject("username")
                    .setExpiration(expireDate)
                    .signWith(SignatureAlgorithm.HS512, "SECRET")
                    .compact();

            boolean correct = ClientTokenHelper.isValid(clientToken);
            Assertions.assertFalse(correct);
        }

        @Test
        void givenTokenSignedWithDifferentAlgorithmReturnFalse(){
            String clientToken = Jwts.builder()
                    .setSubject("username")
                    .setExpiration(expireDate)
                    .signWith(SignatureAlgorithm.HS256, SecurityConstants.CLIENT_TOKEN_SECRET)
                    .compact();

            boolean correct = ClientTokenHelper.isValid(clientToken);
            Assertions.assertFalse(correct);
        }
    }

    @Nested
    @DisplayName("client token builder tests")
    class ClientTokenBuilderTests{
        @Test
        void givenUsernameReturnsToken() throws CustomInternalServerError {
            // Arrange
            String username = "test";

            // Act
            String clientToken = ClientTokenHelper.generateToken(username);

            // Assert
            Assertions.assertNotNull(clientToken);
            Assertions.assertTrue(ClientTokenHelper.isValid(clientToken));
        }
        @Test
        void givenNullUsernameThrowsError(){
            // Arrange
            String username = null;

            // Act & Assert
            assertThrows(CustomInternalServerError.class, () -> ClientTokenHelper.generateToken(username));
        }
        @Test
        void givenEmptyUsernameThrowsError(){
            // Arrange
            String username = "";

            // Act & Assert
            assertThrows(CustomInternalServerError.class, () -> ClientTokenHelper.generateToken(username));
        }
        @Test
        void givenWhiteSpaceUsernameThrowsError(){
            // Arrange
            String username = " \n";

            // Act & Assert
            assertThrows(CustomInternalServerError.class, () -> ClientTokenHelper.generateToken(username));
        }
    }

}