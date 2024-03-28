package com.example.scrapyard.auth;

import com.example.scrapyard.api.GlobalExceptionHandler;
import com.example.scrapyard.api.exceptions.CustomAuthException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.ErrorResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@NoArgsConstructor(force = true)
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private final JwtGenerator jwtGenerator;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException, AuthenticationException {
        String token = getJwtFromRequest(request);
        try {
            if (StringUtils.hasText(token) && jwtGenerator.tokenIsValid(token)) {
                String username = jwtGenerator.getUsernameFromJwt(token);
                List<SimpleGrantedAuthority> authorities =
                        jwtGenerator.getAuthoritiesFromJwt(token).stream()
                        .map(SimpleGrantedAuthority::new).toList();
                UserDetails userDetails = new User(username, "password", authorities);
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, userDetails.getAuthorities(), userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        } catch (CustomAuthException e) {
            ErrorResponse errorResponse =
                    ErrorResponse.create(e, HttpStatus.UNAUTHORIZED, "detail");
            ResponseEntity<ErrorResponse> errorResponseEntity = new ResponseEntity<>(
                    errorResponse, errorResponse.getStatusCode()
            );

            response.setStatus(errorResponse.getStatusCode().value());
            response.setContentType("application/json");
            response.getWriter().write("{\"test\" : \"test value\"}");
            return;
        }
        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return null;
    }
}
