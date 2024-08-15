package com.example.scrapyard.auth;

import com.example.scrapyard.api.exceptions.CustomAuthException;
import com.example.scrapyard.api.exceptions.CustomInternalServerError;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class JwtGenerator {
    int jwtExpiration = 600000;
    @Value(value = "${security.secrets.client:secret}")
    String clientTokenSecret;

    @Value(value = "${security.secrets.server:secret}")
    String serverTokenSecret;
    public String createTestToken(String username){
        return createTestToken(username, false);
    }

    public String createTestToken(String username, boolean forAdmin){
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpiration);
        String[] authorities = forAdmin ? new String[2] : new String[1];
        authorities[0] = new SimpleGrantedAuthority("USER").toString();
        if (forAdmin)
            authorities[1] = new SimpleGrantedAuthority("ADMIN").toString();

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(currentDate)
                .setExpiration(expireDate)
                .claim("authorities", authorities)
                .signWith(SignatureAlgorithm.HS512, serverTokenSecret)
                .compact();
    }

    public String getUsernameFromJwt(String token){
       return Jwts.parser()
                .setSigningKey(serverTokenSecret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
    public ArrayList<String> getAuthoritiesFromServerToken(String token){
        return (ArrayList<String>) Jwts.parser()
                .setSigningKey(serverTokenSecret)
                .parseClaimsJws(token)
                .getBody()
                .get("authorities");
    }

    public boolean serverTokenIsValid(String token) throws AuthenticationException {
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
