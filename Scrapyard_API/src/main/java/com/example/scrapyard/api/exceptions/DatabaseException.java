package com.example.scrapyard.api.exceptions;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DatabaseException extends Exception{
    final String message;

    public static DatabaseException createWith(String message) {
        return new DatabaseException(message);
    }

    @Override
    public String getMessage() {
        return message;
    }
}
