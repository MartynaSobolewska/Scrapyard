package com.scrapyard.authservice.DAOs;

import com.scrapyard.authservice.DAOs.models.TokenPair;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<TokenPair, String> {
}
