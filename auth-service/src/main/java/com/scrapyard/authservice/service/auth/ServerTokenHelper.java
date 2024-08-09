package com.scrapyard.authservice.service.auth;

import com.scrapyard.authservice.api.exceptions.CustomInternalServerError;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

@Service
public class ServerTokenHelper {
    int jwtExpiration = 600000;
    @Value(value = "${security.secrets.client:secret}")
    String clientTokenSecret;

    @Value(value = "${security.secrets.server:secret}")
    String serverTokenSecret;
    

    public String generateToken(String username, String[] authorities) throws CustomInternalServerError{
        System.out.println("SERVER TOKEN S: " + serverTokenSecret);
        boolean authoritiesCorrect = authorities == null || Arrays.stream(authorities).anyMatch(a -> a==null || a.isEmpty());
        if (username == null || username.trim().isEmpty() || authoritiesCorrect){
            throw CustomInternalServerError.createWith("Incorrect username data encountered when generating client token.");
        }
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpiration);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(currentDate)
                .setExpiration(expireDate)
                .claim("authorities", authorities)
                .signWith(SignatureAlgorithm.HS512, serverTokenSecret)
                .compact();
    }

    public boolean isValid(String token){
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(serverTokenSecret).parseClaimsJws(token);
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
