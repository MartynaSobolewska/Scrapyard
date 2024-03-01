package com.example.scrapyard.api.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
public class CarNotFoundException extends Exception{
    final UUID id;

    public static CarNotFoundException createWith(UUID id) {
        return new CarNotFoundException(id);
    }

    @Override
    public String getMessage() {
        return "Car with id '" + id + "' not found";
    }
}
