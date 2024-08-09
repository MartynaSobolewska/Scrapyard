package com.scrapyard.authservice.service.auth;

import com.scrapyard.authservice.DAOs.TokenRepository;
import com.scrapyard.authservice.DAOs.models.TokenPair;
import com.scrapyard.authservice.api.exceptions.CustomInternalServerError;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "classpath:application-test.properties")
class TokenServiceTest{
    final AuthTestHelper authTestHelper;
    @Mock
    TokenRepository tokenRepository;

    String testUsername = "test_username";
    String validBearerToken;
    String validServerToken;
    ArgumentCaptor<TokenPair> tokenPairArgumentCaptor = ArgumentCaptor.forClass(TokenPair.class);

    @InjectMocks
    TokenService tokenService;

    TokenServiceTest(AuthTestHelper authTestHelper, TokenRepository tokenRepository) throws CustomInternalServerError {
        this.authTestHelper = authTestHelper;
        this.tokenRepository = tokenRepository;
        validBearerToken = authTestHelper.createTestBearerToken(testUsername);
        validServerToken = authTestHelper.createTestServerToken(testUsername);
    }

    @Nested
    @DisplayName("Save token pair tests")
    class SaveTokenPairTest {
        @Test
        void givenNullClientTokenThrowError() {
            assertThrows(CustomInternalServerError.class, () -> tokenService.saveTokenPair(null, "serverToken"));
        }

        @Test
        void givenNullServerTokenThrowError() {
            assertThrows(CustomInternalServerError.class, () -> tokenService.saveTokenPair("bearerToken", null));
        }

        @Test
        void givenEmptyClientTokenThrowError() {
            assertThrows(CustomInternalServerError.class, () -> tokenService.saveTokenPair("", "serverToken"));
        }

        @Test
        void givenEmptyServerTokenThrowError() {
            assertThrows(CustomInternalServerError.class, () -> tokenService.saveTokenPair("bearerToken", ""));
        }

        @Test
        void givenIncorrectClientTokenThrowError() {
            assertThrows(CustomInternalServerError.class, () -> tokenService.saveTokenPair("bearerToken", "serverToken"));
        }

        @Test
        void givenIncorrectServerTokenThrowError() {
            assertThrows(CustomInternalServerError.class, () -> tokenService.saveTokenPair(validBearerToken, "serverToken"));
        }

        @Test
        void givenIncorrectBearerTokenThrowError() {
            assertThrows(CustomInternalServerError.class, () -> tokenService.saveTokenPair("bearerToken", validServerToken));
        }

        @Test
        void givenCorrectClientTokenAndServerTokenSavePair() throws CustomInternalServerError {
            TokenPair tokenPair = TokenPair.builder().bearerToken(validBearerToken).serverToken(validServerToken).build();
            when(tokenRepository.save(any(TokenPair.class))).thenReturn(tokenPair);

            TokenPair result = tokenService.saveTokenPair(validBearerToken, validServerToken);

            verify(tokenRepository).save(tokenPairArgumentCaptor.capture());
            assertEquals(tokenPairArgumentCaptor.getValue().getBearerToken(), validBearerToken);
            assertEquals(tokenPairArgumentCaptor.getValue().getServerToken(), validServerToken);
            assertEquals(result.getServerToken(), validServerToken);
            assertEquals(result.getBearerToken(), validBearerToken);
        }
    }

    @Nested
    @DisplayName("Get token pair by bearer token tests")
    class GetTokenPairByBearerTokenTest {
        @Test
        void givenNullBearerTokenThrowError() {
            assertThrows(CustomInternalServerError.class, () -> tokenService.getTokenPairByBearerToken(null));
        }

        @Test
        void givenEmptyBearerTokenThrowError() {
            assertThrows(CustomInternalServerError.class, () -> tokenService.getTokenPairByBearerToken(""));
        }

        @Test
        void givenCorrectBearerTokenReturnCorrectPair() throws CustomInternalServerError {
            TokenPair tokenPair = TokenPair.builder().bearerToken(validBearerToken).serverToken(validServerToken).build();
            when(tokenRepository.findById(validBearerToken)).thenReturn(Optional.of(tokenPair));

            Optional<TokenPair> result = tokenService.getTokenPairByBearerToken(validBearerToken);

            assertTrue(result.isPresent());
            assertEquals(result.get().getBearerToken(), validBearerToken);
            assertEquals(result.get().getServerToken(), validServerToken);
        }

    }

}