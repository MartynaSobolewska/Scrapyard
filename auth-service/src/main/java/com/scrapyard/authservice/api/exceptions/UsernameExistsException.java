package com.scrapyard.authservice.api.exceptions;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UsernameExistsException extends Exception{
    private final String username;
    public static UsernameExistsException createWith(String email) {
        return new UsernameExistsException(email);
    }

    @Override
    public String getMessage() {
        return "User with username \"" + username + "\" already exists";
    }
}

