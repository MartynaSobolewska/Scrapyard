package com.example.scrapyard.auth;

import com.example.scrapyard.api.exceptions.CustomAuthException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class JwtGenerator {
    //TODO: move jwt secret to config service
    public String generateToken(Authentication authentication){
        String username = authentication.getName();
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + SecurityConstants.JWT_EXPIRATION);
       String[] authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toArray(String[]::new);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(currentDate)
                .setExpiration(expireDate)
                .claim("authorities", authorities)
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.JWT_SECRET)
                .compact();
    }
    public String createTestToken(String username){
        return createTestToken(username, false);
    }

    public String createTestToken(String username, boolean forAdmin){
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + SecurityConstants.JWT_EXPIRATION);
        String[] authorities = forAdmin ? new String[2] : new String[1];
        authorities[0] = new SimpleGrantedAuthority("USER").toString();
        if (forAdmin)
            authorities[1] = new SimpleGrantedAuthority("ADMIN").toString();

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(currentDate)
                .setExpiration(expireDate)
                .claim("authorities", authorities)
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.JWT_SECRET)
                .compact();
    }

    public String getUsernameFromJwt(String token){
       return Jwts.parser()
                .setSigningKey(SecurityConstants.JWT_SECRET)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
    public ArrayList<String> getAuthoritiesFromJwt(String token){
        return (ArrayList<String>) Jwts.parser()
                .setSigningKey(SecurityConstants.JWT_SECRET)
                .parseClaimsJws(token)
                .getBody()
                .get("authorities");
    }

    public boolean tokenIsValid(String token) throws CustomAuthException {
        try {
            Jwts.parser().setSigningKey(SecurityConstants.JWT_SECRET).parseClaimsJws(token);
            return true;
        }catch (Exception ex){
            throw CustomAuthException.createWith("JWT was expired or incorrect");
        }
    }
}
