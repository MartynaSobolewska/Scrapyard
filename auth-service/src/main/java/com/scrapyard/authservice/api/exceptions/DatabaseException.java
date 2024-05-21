package com.scrapyard.authservice.api.exceptions;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DatabaseException extends Exception{
    final String message;

    public static com.example.scrapyard.api.exceptions.DatabaseException createWith(String message) {
        return new com.example.scrapyard.api.exceptions.DatabaseException(message);
    }

    @Override
    public String getMessage() {
        return message;
    }
}
