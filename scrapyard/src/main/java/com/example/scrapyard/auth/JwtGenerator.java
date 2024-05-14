package com.example.scrapyard.auth;

import com.example.scrapyard.api.exceptions.AuthenticationException;
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
    public String generateBearerToken(String username){
        // check if there is a bearer token for the username in the db. If expired, overwrite with a new one,
        // blank out the corresponding jwt.
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + SecurityConstants.JWT_EXPIRATION);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(currentDate)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.BEARER_SECRET)
                .compact();
    }
//    public String generateJwtToken(String bearerToken){
//        // check if bearer token is in the db. If expired, throw an error
//
//        Date currentDate = new Date();
//        Date expireDate = new Date(currentDate.getTime() + SecurityConstants.JWT_EXPIRATION);
//        String[] authorities = userService.loadUserByUsername(getUsernameFromJwt(bearerToken)).getAuthorities();
//
//        return Jwts.builder()
//                .setSubject(username)
//                .setIssuedAt(currentDate)
//                .setExpiration(expireDate)
//                .claim("authorities", authorities)
//                .signWith(SignatureAlgorithm.HS512, SecurityConstants.JWT_SECRET)
//                .compact();
//    }

    public String createTestToken(String username){
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + SecurityConstants.JWT_EXPIRATION);
        String[] authorities = new String[1];
        authorities[0] = new SimpleGrantedAuthority("USER").toString();

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
    public String getUsernameFromBearerToken(String token){
        return Jwts.parser()
                .setSigningKey(SecurityConstants.BEARER_SECRET)
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

    public boolean jwtTokenIsValid(String token) throws AuthenticationException {
        try {
            Jwts.parser().setSigningKey(SecurityConstants.JWT_SECRET).parseClaimsJws(token);
            return true;
        }catch (Exception ex){
            throw new AuthenticationException("JWT was expired or incorrect");
        }
    }

    public boolean bearerTokenIsValid(String token) throws AuthenticationException {
        try {
            Jwts.parser().setSigningKey(SecurityConstants.BEARER_SECRET).parseClaimsJws(token);
            return true;
        }catch (Exception ex){
            throw new AuthenticationException("JWT was expired or incorrect");
        }
    }
}
