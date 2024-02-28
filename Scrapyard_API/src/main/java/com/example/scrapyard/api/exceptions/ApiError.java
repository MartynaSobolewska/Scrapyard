package com.example.scrapyard.api.exceptions;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApiError {
    private List<String> errors;
    public ApiError(List<String> errors) {
        this.errors = errors;
    }
}