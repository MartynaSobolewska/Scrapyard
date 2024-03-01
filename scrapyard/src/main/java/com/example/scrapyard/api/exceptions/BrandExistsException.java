package com.example.scrapyard.api.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BrandExistsException extends Exception{
    private final String brandName;
    public static BrandExistsException createWith(String brandName) {
        return new BrandExistsException(brandName);
    }
    @Override
    public String getMessage() {
        return "Brand with name \"" + brandName + "\" already exists";
    }
}
