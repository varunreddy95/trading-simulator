package com.varun.appbackend.repository;

import com.varun.appbackend.model.Position;
import com.varun.appbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for trade positions by a user
 */
public interface PositionRepository extends JpaRepository<Position, Long> {
    Optional<Position> findByUserAndStockSymbol(User user, String stockSymbol);
    List<Position> findByUser(User user);
}
