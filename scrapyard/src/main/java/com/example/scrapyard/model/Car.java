package com.example.scrapyard.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    // if doesn't exist, will be created
    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "model_id")
    private Model model;
    // can only be created by admin
    @OneToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;
    private Integer yearOfProduction;
    private double price;
    // owner who created object
    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
