package com.scrapyard.authservice.service.auth;

import com.scrapyard.authservice.DAOs.TokenRepository;
import com.scrapyard.authservice.DAOs.models.TokenPair;
import com.scrapyard.authservice.api.exceptions.CustomInternalServerError;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TokenService {
    final TokenRepository tokenRepository;

    public TokenService(TokenRepository tokenRepository, ClientTokenHelper clientTokenHelper, ServerTokenHelper serverTokenHelper) {
        this.tokenRepository = tokenRepository;
    }

    public TokenPair saveTokenPair(String bearerToken, String serverToken) throws CustomInternalServerError {
        if (bearerToken == null || bearerToken.trim().isEmpty() ||
            serverToken == null || serverToken.trim().isEmpty()) {
            throw CustomInternalServerError.createWith("Incorrect bearerToken data encountered when saving token pair.");
        }
        if (!ClientTokenHelper.isValid(bearerToken)) {
            throw CustomInternalServerError.createWith("Invalid bearer token data encountered when saving token pair.");
        }
        if (!ServerTokenHelper.isValid(serverToken)) {
            throw CustomInternalServerError.createWith("Invalid server token data encountered when saving token pair.");
        }
        TokenPair tokenPair = TokenPair.builder().bearerToken(bearerToken).serverToken(serverToken).build();
        return tokenRepository.save(tokenPair);
    }

    public Optional<TokenPair> getTokenPairByBearerToken(String bearerToken) throws CustomInternalServerError {
        if (bearerToken == null || bearerToken.trim().isEmpty())
            throw CustomInternalServerError.createWith("Incorrect bearerToken data encountered when getting token pair.");
        return tokenRepository.findById(bearerToken);
    }
}
