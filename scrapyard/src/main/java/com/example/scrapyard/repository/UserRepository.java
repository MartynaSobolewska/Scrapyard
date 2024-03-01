package com.example.scrapyard.repository;

import com.example.scrapyard.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
//    @Query("SELECT a FROM AuthEntity a WHERE a.email = ?1")
    Optional<UserEntity> findByUsername(String username);
}
