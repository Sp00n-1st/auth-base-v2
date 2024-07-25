package com.tujuhsembilan.example.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tujuhsembilan.example.model.TokenStore;

import java.util.Optional;

public interface TokenStoreRepo extends JpaRepository<TokenStore, UUID> {
    Optional<TokenStore> findByAccessToken(String accessToken);

    Optional<TokenStore> findByRefreshToken(String refreshToken);

    Optional<TokenStore> findByUsername(String username);
}
