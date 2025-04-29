package com.varun.appbackend.repository;

import com.varun.appbackend.model.Trade;
import com.varun.appbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository for accessing Trade data
 */
public interface TradeRepository extends JpaRepository<Trade, Long> {

    /**
     * Finds all trades for a specific user
     *
     * @param user the user
     * @return list of trades
     */
    List<Trade> findByUser(User user);
}
