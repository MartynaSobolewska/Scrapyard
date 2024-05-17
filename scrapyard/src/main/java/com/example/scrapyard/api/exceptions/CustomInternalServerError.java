package com.example.scrapyard.api.exceptions;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CustomInternalServerError extends Exception{
    final String message;

    public static CustomInternalServerError createWith(String message) {
        return new CustomInternalServerError(message);
    }

    @Override
    public String getMessage() {
        return message;
    }
}
