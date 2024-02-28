package com.example.scrapyard.api.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class BrandNotFoundException extends Exception{
    final String identifier;

    public static BrandNotFoundException createWith(String identifier) {
        return new BrandNotFoundException(identifier);
    }

    @Override
    public String getMessage() {
        try {
            UUID id = UUID.fromString(identifier);
            return "Brand with id " + id + " not found";
        } catch (Exception e){
            return "Brand with name " + identifier + " not found";
        }
    }
}
