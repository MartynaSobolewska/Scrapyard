package com.example.scrapyard.domain;

import com.example.scrapyard.model.Brand;
import com.example.scrapyard.model.Model;
import lombok.*;

import java.util.UUID;

@Setter
@Getter
public class CarResponse {
    private UUID id;
    private Model model;
    private Brand brand;
    private Integer yearOfProduction;
    private double price;
}
