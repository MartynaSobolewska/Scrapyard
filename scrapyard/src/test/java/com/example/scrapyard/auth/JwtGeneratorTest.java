package com.example.scrapyard.auth;

import com.example.scrapyard.api.exceptions.CustomInternalServerError;
import com.example.scrapyard.service.CustomUserDetailsServiceImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.util.Date;

import static org.junit.Assert.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestPropertySource(locations = "classpath:application-test.properties")
class JwtGeneratorTest {
    @Autowired
    private JwtGenerator jwtGenerator;

    @MockBean
    private CustomUserDetailsServiceImpl userDetailsService;

    private final Date currentDate = new Date();
    private final Date pastDate = new Date(currentDate.getTime() - SecurityConstants.JWT_EXPIRATION);
    private final Date expireDate = new Date(currentDate.getTime() + SecurityConstants.JWT_EXPIRATION);

    private final String[] authorities = new String[]{"USER"};

    // Moved to the clienttokenhelper in the auth service
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

            boolean correct = jwtGenerator.clientTokenIsValid(clientToken);
            Assertions.assertTrue(correct);
        }

        @Test
        void givenTokenWithoutUsernameReturnFalse() {
            String clientToken = Jwts.builder()
                    .setIssuedAt(currentDate)
                    .setExpiration(expireDate)
                    .signWith(SignatureAlgorithm.HS512, SecurityConstants.CLIENT_TOKEN_SECRET)
                    .compact();

            boolean correct = jwtGenerator.clientTokenIsValid(clientToken);
            Assertions.assertFalse(correct);
        }

        @Test
        void givenTokenWithoutExpirationReturnFalse(){
            String clientToken = Jwts.builder()
                    .setSubject("username")
                    .setIssuedAt(currentDate)
                    .signWith(SignatureAlgorithm.HS512, SecurityConstants.CLIENT_TOKEN_SECRET)
                    .compact();

            boolean correct = jwtGenerator.clientTokenIsValid(clientToken);
            Assertions.assertFalse(correct);
        }

        @Test
        void givenExpiredTokenReturnFalse(){
            String clientToken = Jwts.builder()
                    .setSubject("username")
                    .setIssuedAt(pastDate)
                    .signWith(SignatureAlgorithm.HS512, SecurityConstants.CLIENT_TOKEN_SECRET)
                    .compact();

            boolean correct = jwtGenerator.clientTokenIsValid(clientToken);
            Assertions.assertFalse(correct);
        }

        @Test
        void givenTokenWithoutIssueDateReturnFalse(){
            String clientToken = Jwts.builder()
                    .setSubject("username")
                    .setExpiration(expireDate)
                    .signWith(SignatureAlgorithm.HS512, SecurityConstants.CLIENT_TOKEN_SECRET)
                    .compact();

            boolean correct = jwtGenerator.clientTokenIsValid(clientToken);
            Assertions.assertFalse(correct);
        }

        @Test
        void givenTokenSignedWithDifferentSecretReturnFalse(){
            String clientToken = Jwts.builder()
                    .setSubject("username")
                    .setExpiration(expireDate)
                    .signWith(SignatureAlgorithm.HS512, "SECRET")
                    .compact();

            boolean correct = jwtGenerator.clientTokenIsValid(clientToken);
            Assertions.assertFalse(correct);
        }

        @Test
        void givenTokenSignedWithDifferentAlgorithmReturnFalse(){
            String clientToken = Jwts.builder()
                    .setSubject("username")
                    .setExpiration(expireDate)
                    .signWith(SignatureAlgorithm.HS256, SecurityConstants.CLIENT_TOKEN_SECRET)
                    .compact();

            boolean correct = jwtGenerator.clientTokenIsValid(clientToken);
            Assertions.assertFalse(correct);
        }
    }

    // Moved to the clienttokenhelper in the auth service
    @Nested
    @DisplayName("client token builder tests")
    class ClientTokenBuilderTests{
        @Test
        void givenUsernameReturnsToken() throws CustomInternalServerError {
            // Arrange
            String username = "test";

            // Act
            String clientToken = jwtGenerator.generateClientToken(username);

            // Assert
            Assertions.assertNotNull(clientToken);
            Assertions.assertTrue(jwtGenerator.clientTokenIsValid(clientToken));
        }
        @Test
        void givenNullUsernameThrowsError(){
            // Arrange
            String username = null;

            // Act & Assert
            assertThrows(CustomInternalServerError.class, () -> jwtGenerator.generateClientToken(username));
        }
        @Test
        void givenEmptyUsernameThrowsError(){
            // Arrange
            String username = "";

            // Act & Assert
            assertThrows(CustomInternalServerError.class, () -> jwtGenerator.generateClientToken(username));
        }
        @Test
        void givenWhiteSpaceUsernameThrowsError(){
            // Arrange
            String username = " \n";

            // Act & Assert
            assertThrows(CustomInternalServerError.class, () -> jwtGenerator.generateClientToken(username));
        }
    }

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
                    .signWith(SignatureAlgorithm.HS512, SecurityConstants.SERVER_TOKEN_SECRET)
                    .compact();

            boolean correct = jwtGenerator.serverTokenIsValid(serverToken);
            Assertions.assertTrue(correct);
        }
        @Test
        void givenTokenMissingRolesReturnFalse(){
            String serverToken = Jwts.builder()
                    .setSubject("username")
                    .setIssuedAt(currentDate)
                    .setExpiration(expireDate)
                    .signWith(SignatureAlgorithm.HS512, SecurityConstants.SERVER_TOKEN_SECRET)
                    .compact();

            boolean correct = jwtGenerator.serverTokenIsValid(serverToken);
            Assertions.assertFalse(correct);
        }
        @Test
        void givenTokenMissingIssueDateReturnFalse(){
            String serverToken = Jwts.builder()
                    .claim("authorities", authorities)
                    .setSubject("username")
                    .setExpiration(expireDate)
                    .signWith(SignatureAlgorithm.HS512, SecurityConstants.SERVER_TOKEN_SECRET)
                    .compact();

            boolean correct = jwtGenerator.serverTokenIsValid(serverToken);
            Assertions.assertFalse(correct);
        }
        @Test
        void givenTokenMissingExpirationDateReturnFalse(){
            String serverToken = Jwts.builder()
                    .claim("authorities", authorities)
                    .setSubject("username")
                    .setIssuedAt(currentDate)
                    .signWith(SignatureAlgorithm.HS512, SecurityConstants.SERVER_TOKEN_SECRET)
                    .compact();

            boolean correct = jwtGenerator.serverTokenIsValid(serverToken);
            Assertions.assertFalse(correct);
        }

        @Test
        void givenTokenFutureIssueDateReturnFalse(){
            String serverToken = Jwts.builder()
                    .claim("authorities", authorities)
                    .setSubject("username")
                    .setExpiration(expireDate)
                    .setIssuedAt(expireDate)
                    .signWith(SignatureAlgorithm.HS512, SecurityConstants.SERVER_TOKEN_SECRET)
                    .compact();

            boolean correct = jwtGenerator.serverTokenIsValid(serverToken);
            Assertions.assertFalse(correct);
        }

        @Test
        void givenTokenPastExpireDateReturnFalse(){
            String serverToken = Jwts.builder()
                    .claim("authorities", authorities)
                    .setSubject("username")
                    .setExpiration(expireDate)
                    .setIssuedAt(expireDate)
                    .signWith(SignatureAlgorithm.HS512, SecurityConstants.SERVER_TOKEN_SECRET)
                    .compact();

            boolean correct = jwtGenerator.serverTokenIsValid(serverToken);
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
            String serverToken = jwtGenerator.generateServerToken(username, authorities);

            // Assert
            Assertions.assertNotNull(serverToken);
            Assertions.assertTrue(jwtGenerator.serverTokenIsValid(serverToken));
        }
        @Test
        void givenNullUsernameBuildsThrowsError(){
            // Arrange
            String username = null;

            // Act & assert
            assertThrows(CustomInternalServerError.class, () -> jwtGenerator.generateServerToken(username, authorities));
        }

        @Test
        void givenWhiteSpaceUsernameBuildsThrowsError(){
            // Arrange
            String username = " \n\t";

            // Act & assert
            assertThrows(CustomInternalServerError.class, () -> jwtGenerator.generateServerToken(username, authorities));
        }
        @Test
        void givenNullRolesBuildsThrowsError(){
            // Arrange
            String username = "test";

            // Act & assert
            assertThrows(CustomInternalServerError.class, () -> jwtGenerator.generateServerToken(username, null));
        }
        @Test
        void givenEmptyRolesBuildsThrowsError(){
            // Arrange
            String username = "test";
            String[] wrongAuthorities = new String[2];

            // Act & assert
            assertThrows(CustomInternalServerError.class, () -> jwtGenerator.generateServerToken(username, wrongAuthorities));
        }
        @Test
        void givenEmptyStringRoleBuildsThrowsError(){
            // Arrange
            String username = "test";
            String[] wrongAuthorities = new String[]{"USER", ""};

            // Act & assert
            assertThrows(CustomInternalServerError.class, () -> jwtGenerator.generateServerToken(username, wrongAuthorities));
        }

    }

}