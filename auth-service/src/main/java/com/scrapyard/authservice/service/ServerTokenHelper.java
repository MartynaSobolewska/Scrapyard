package com.scrapyard.authservice.service;

import com.scrapyard.authservice.api.exceptions.CustomInternalServerError;
import com.scrapyard.authservice.config.SecurityConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

@Component
public class ServerTokenHelper {
    public static String generateToken(String username, String[] authorities) throws CustomInternalServerError{
        boolean authoritiesCorrect = authorities == null || Arrays.stream(authorities).anyMatch(a -> a==null || a.isEmpty());
        if (username == null || username.trim().isEmpty() || authoritiesCorrect){
            throw CustomInternalServerError.createWith("Incorrect username data encountered when generating client token.");
        }
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + SecurityConstants.JWT_EXPIRATION);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(currentDate)
                .setExpiration(expireDate)
                .claim("authorities", authorities)
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.SERVER_TOKEN_SECRET)
                .compact();
    }

    public boolean validateToken(String token){
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(SecurityConstants.SERVER_TOKEN_SECRET).parseClaimsJws(token);
            ArrayList<String> authorities = (ArrayList<String>) claimsJws.getBody().get("authorities");
            Date now = new Date();

            // validate token information
            return !authorities.isEmpty()
                    && claimsJws.getBody().getExpiration().after(now)
                    && claimsJws.getBody().getIssuedAt().before(now);
        }catch (Exception ex){
            return false;
        }
    }
}
