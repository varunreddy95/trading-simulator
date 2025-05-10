package com.varun.appbackend.repository;

import com.varun.appbackend.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByEmail(String email);
    // For token cleanup from db
    void deleteByExpiryDateBefore(LocalDateTime dateTime);
}
