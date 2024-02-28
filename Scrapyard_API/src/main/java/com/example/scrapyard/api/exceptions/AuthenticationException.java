package com.example.scrapyard.api.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
@AllArgsConstructor
public class AuthenticationException extends Exception{
    final String message;

    public static AuthenticationException createWith(String message) {
        return new AuthenticationException(message);
    }

    @Override
    public String getMessage() {
        return message;
    }
}
