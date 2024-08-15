package com.scrapyard.authservice.service.auth;

import com.scrapyard.authservice.api.exceptions.CustomInternalServerError;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestPropertySource(locations = "classpath:application-test.properties")
class ServerTokenHelperTest {
//    final ClientTokenHelper clientTokenHelper;
    int jwtExpiration = 600000;
    @Value(value = "${security.secrets.client:secret}")
    String clientTokenSecret;

    @Value(value = "${security.secrets.server:secret}")
    String serverTokenSecret;
    @Autowired
    private ServerTokenHelper serverTokenHelper;

    private final Date currentDate = new Date();
    private final Date pastDate = new Date(currentDate.getTime() - jwtExpiration);
    private final Date expireDate = new Date(currentDate.getTime() + jwtExpiration);

    private final String[] authorities = new String[]{"USER"};


    @Nested
    @DisplayName("server token validator tests")
    class ServerValidatorTests{
        @Test
        void givenCorrectTokenReturnTrue(){
            String serverToken = Jwts.builder()
                    .claim("authorities", authorities)
                    .setSubject("username")
                    .setIssuedAt(currentDate)
                    .setExpiration(expireDate)
                    .signWith(SignatureAlgorithm.HS512, serverTokenSecret)
                    .compact();

            boolean correct = serverTokenHelper.isValid(serverToken);
            Assertions.assertTrue(correct);
        }
        @Test
        void givenTokenMissingRolesReturnFalse(){
            String serverToken = Jwts.builder()
                    .setSubject("username")
                    .setIssuedAt(currentDate)
                    .setExpiration(expireDate)
                    .signWith(SignatureAlgorithm.HS512, serverTokenSecret)
                    .compact();

            boolean correct = serverTokenHelper.isValid(serverToken);
            Assertions.assertFalse(correct);
        }
        @Test
        void givenTokenMissingIssueDateReturnFalse(){
            String serverToken = Jwts.builder()
                    .claim("authorities", authorities)
                    .setSubject("username")
                    .setExpiration(expireDate)
                    .signWith(SignatureAlgorithm.HS512, serverTokenSecret)
                    .compact();

            boolean correct = serverTokenHelper.isValid(serverToken);
            Assertions.assertFalse(correct);
        }
        @Test
        void givenTokenMissingExpirationDateReturnFalse(){
            String serverToken = Jwts.builder()
                    .claim("authorities", authorities)
                    .setSubject("username")
                    .setIssuedAt(currentDate)
                    .signWith(SignatureAlgorithm.HS512, serverTokenSecret)
                    .compact();

            boolean correct = serverTokenHelper.isValid(serverToken);
            Assertions.assertFalse(correct);
        }

        @Test
        void givenTokenFutureIssueDateReturnFalse(){
            String serverToken = Jwts.builder()
                    .claim("authorities", authorities)
                    .setSubject("username")
                    .setExpiration(expireDate)
                    .setIssuedAt(expireDate)
                    .signWith(SignatureAlgorithm.HS512, serverTokenSecret)
                    .compact();

            boolean correct = serverTokenHelper.isValid(serverToken);
            Assertions.assertFalse(correct);
        }

        @Test
        void givenTokenPastExpireDateReturnFalse(){
            String serverToken = Jwts.builder()
                    .claim("authorities", authorities)
                    .setSubject("username")
                    .setExpiration(expireDate)
                    .setIssuedAt(expireDate)
                    .signWith(SignatureAlgorithm.HS512, serverTokenSecret)
                    .compact();

            boolean correct = serverTokenHelper.isValid(serverToken);
            Assertions.assertFalse(correct);
        }
    }

    @Nested
    @DisplayName("server token builder tests")
    class ServerTokenBuilderTests{
        @Test
        void givenCorrectUsernameBuildsCorrectToken() throws CustomInternalServerError {
            // Arrange
            String username = "test";

            // Act
            String serverToken = serverTokenHelper.generateToken(username, authorities);

            // Assert
            Assertions.assertNotNull(serverToken);
            Assertions.assertTrue(serverTokenHelper.isValid(serverToken));
        }
        @Test
        void givenNullUsernameBuildsThrowsError(){
            // Arrange
            String username = null;

            // Act & assert
            assertThrows(CustomInternalServerError.class, () -> serverTokenHelper.generateToken(username, authorities));
        }

        @Test
        void givenWhiteSpaceUsernameBuildsThrowsError(){
            // Arrange
            String username = " \n\t";

            // Act & assert
            assertThrows(CustomInternalServerError.class, () -> serverTokenHelper.generateToken(username, authorities));
        }
        @Test
        void givenNullRolesBuildsThrowsError(){
            // Arrange
            String username = "test";

            // Act & assert
            assertThrows(CustomInternalServerError.class, () -> serverTokenHelper.generateToken(username, null));
        }
        @Test
        void givenEmptyRolesBuildsThrowsError(){
            // Arrange
            String username = "test";
            String[] wrongAuthorities = new String[2];

            // Act & assert
            assertThrows(CustomInternalServerError.class, () -> serverTokenHelper.generateToken(username, wrongAuthorities));
        }
        @Test
        void givenEmptyStringRoleBuildsThrowsError(){
            // Arrange
            String username = "test";
            String[] wrongAuthorities = new String[]{"USER", ""};

            // Act & assert
            assertThrows(CustomInternalServerError.class, () -> serverTokenHelper.generateToken(username, wrongAuthorities));
        }

    }

}