package com.example.scrapyard.repository;

import com.example.scrapyard.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CarRepository
        extends JpaRepository<Car, UUID> {}
