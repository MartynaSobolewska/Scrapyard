package com.example.scrapyard.auth;

import com.example.scrapyard.api.exceptions.CustomAuthException;
import com.example.scrapyard.api.exceptions.CustomInternalServerError;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class JwtGenerator {
    //TODO: move jwt secret to config service
    public String generateClientToken(String username) throws CustomInternalServerError {
        if (username == null || username.trim().isEmpty()){
            throw CustomInternalServerError.createWith("Incorrect username data encountered when generating client token.");
        }
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
    public String generateServerToken(String username){
        // check if bearer token is in the db. If expired, throw an error

        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + SecurityConstants.JWT_EXPIRATION);
//        String[] authorities = userService.loadUserByUsername(getUsernameFromJwt(bearerToken)).getAuthorities();

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(currentDate)
                .setExpiration(expireDate)
//                .claim("authorities", authorities)
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

    public boolean clientTokenIsValid(String token) throws CustomAuthException {
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(SecurityConstants.BEARER_SECRET).parseClaimsJws(token);
            // validate token information
            return claimsJws.getBody().getExpiration().after(new Date()) &&
                    claimsJws.getBody().getIssuedAt() != null &&
                    !claimsJws.getBody().getSubject().isEmpty();
        }catch (Exception ex){
            return false;
        }
    }

    public boolean serverTokenIsValid(String token) throws AuthenticationException {
        try {
            Jwts.parser().setSigningKey(SecurityConstants.JWT_SECRET).parseClaimsJws(token);
            return true;
        }catch (Exception ex){
            throw new CustomAuthException("JWT was expired or incorrect");
        }
    }
}
