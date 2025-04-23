package com.varun.appbackend.repository;


import com.varun.appbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for User entity
 * Extends JpaRepository to provide CRUD operations
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /***
     *  Custom finder method to retrieve a user by username
     *
     * @param username the username to search for
     * @return Optional of user
     */
    Optional<User> findByUsername(String username);
}
