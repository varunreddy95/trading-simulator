package com.varun.appbackend.service;

import com.varun.appbackend.model.User;
import com.varun.appbackend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service class to manage user operations
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Registers a new user in the system
     *
     * @param user the user to save
     * @return saved user instance
     */
    public User registerUser(User user) {
        return userRepository.save(user);
    }

    /**
     * Finds a user by their username
     *
     * @param username the username to search
     * @return Optional of User
     */
    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Finds a user by their email
     *
     * @param email the username to search
     * @return Optional of User
     */
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Finds a user by their ID
     *
     * @param userId the user ID
     * @return Optional of User
     */
    public Optional<User> findById(long userId) {
        return userRepository.findById(userId);
    }
}
