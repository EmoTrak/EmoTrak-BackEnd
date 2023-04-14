package com.example.emotrak.repository;

import com.example.emotrak.entity.User;
import com.example.emotrak.util.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {

    Optional<RefreshToken> findByUser(User user);

    Optional<RefreshToken> findByValue(String refreshTokenValue);
}