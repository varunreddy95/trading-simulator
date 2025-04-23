package com.varun.appbackend.repository;


import com.varun.appbackend.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for Account entity
 * Provides access to CRUD operations on accounts
 */
public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * Finds an account by the associated user's ID
     *
     * @param userId the ID of the user
     * @return Optional of Account
     */
    Optional<Account> findByUserId(Long userId);
}
