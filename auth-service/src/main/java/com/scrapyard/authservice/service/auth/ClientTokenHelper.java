package com.scrapyard.authservice.service.auth;

import com.scrapyard.authservice.api.exceptions.CustomInternalServerError;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ClientTokenHelper {
    
    int jwtExpiration = 600000;
    @Value(value = "${security.secrets.client:secret}")
    String clientTokenSecret;

    @Value(value = "${security.secrets.server:secret}")
    String serverTokenSecret;

    public String generateToken(String username) throws CustomInternalServerError {
        if (username == null || username.trim().isEmpty()){
            throw CustomInternalServerError.createWith("Incorrect username data encountered when generating client token.");
        }
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpiration);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(currentDate)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, clientTokenSecret)
                .compact();
    }

    public boolean isValid(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(clientTokenSecret).parseClaimsJws(token);
            Date now = new Date();

            // validate token information
            return claimsJws.getBody().getExpiration().after(now) &&
                    claimsJws.getBody().getIssuedAt().before(now) &&
                    !claimsJws.getBody().getSubject().isEmpty();
        }catch (Exception ex){
            return false;
        }
    }

    public String getUsername(String token){
        return Jwts.parser()
                .setSigningKey(clientTokenSecret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
