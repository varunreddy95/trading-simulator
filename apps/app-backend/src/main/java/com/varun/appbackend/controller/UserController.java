package com.varun.appbackend.controller;


import com.varun.appbackend.dto.UserResponseDTO;
import com.varun.appbackend.model.User;
import com.varun.appbackend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Controller for mapping user-related operations
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * POST a new user
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@RequestBody User user) {
        User savedUser = userService.registerUser(user);
        return ResponseEntity.ok(new UserResponseDTO(savedUser.getId(), savedUser.getUsername(), savedUser.getEmail()));
    }

    /**
     * GET a user by username
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponseDTO> getUserByUsername(@PathVariable String username) {
        Optional<User> user = userService.findUserByUsername(username);
        return user.map(u -> ResponseEntity.ok(new UserResponseDTO(u.getId(), u.getUsername(), u.getEmail())))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET a user by email
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponseDTO> getUserByEmail(@PathVariable String email) {
        Optional<User> user = userService.findUserByEmail(email);
        return user.map(u -> ResponseEntity.ok(new UserResponseDTO(u.getId(), u.getUsername(), u.getEmail())))
                .orElse(ResponseEntity.notFound().build());
    }


    /**
     * GET a user by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable long id) {
        Optional<User> user = userService.findById(id);
        return user.map(u -> ResponseEntity.ok(new UserResponseDTO(u.getId(), u.getUsername(), u.getEmail())))
                .orElse(ResponseEntity.notFound().build());
    }
}
