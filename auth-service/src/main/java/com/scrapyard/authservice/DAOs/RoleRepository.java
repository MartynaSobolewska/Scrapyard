package com.scrapyard.authservice.DAOs;

import org.springframework.data.jpa.repository.JpaRepository;
import com.scrapyard.authservice.DAOs.models.Role;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByName(String name);
}