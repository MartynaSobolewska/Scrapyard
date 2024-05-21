package com.scrapyard.authservice.api.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.AuthenticationException;


@Getter
@Setter
public class CustomAuthException extends AuthenticationException {
    private final String message;

    public CustomAuthException(String msg) {
        super(msg);
        message = msg;
    }

    public static com.example.scrapyard.api.exceptions.CustomAuthException createWith(String message) {
        return new com.example.scrapyard.api.exceptions.CustomAuthException(message);
    }

    @Override
    public String getMessage() {
        return "Authentication unsuccessful: " + message;
    }
}

