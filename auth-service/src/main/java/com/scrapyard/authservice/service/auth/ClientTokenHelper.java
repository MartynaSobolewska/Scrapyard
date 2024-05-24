package com.scrapyard.authservice.service.auth;

import com.scrapyard.authservice.api.exceptions.CustomInternalServerError;
import com.scrapyard.authservice.config.SecurityConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ClientTokenHelper {
    public static String generateToken(String username) throws CustomInternalServerError {
        if (username == null || username.trim().isEmpty()){
            throw CustomInternalServerError.createWith("Incorrect username data encountered when generating client token.");
        }
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + SecurityConstants.JWT_EXPIRATION);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(currentDate)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.CLIENT_TOKEN_SECRET)
                .compact();
    }

    public static boolean isValid(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(SecurityConstants.CLIENT_TOKEN_SECRET).parseClaimsJws(token);
            Date now = new Date();

            // validate token information
            return claimsJws.getBody().getExpiration().after(now) &&
                    claimsJws.getBody().getIssuedAt().before(now) &&
                    !claimsJws.getBody().getSubject().isEmpty();
        }catch (Exception ex){
            return false;
        }
    }
}
