package com.varun.appbackend.controller;

import com.varun.appbackend.dto.*;
import com.varun.appbackend.model.PasswordResetToken;
import com.varun.appbackend.model.Role;
import com.varun.appbackend.model.User;
import com.varun.appbackend.repository.PasswordResetTokenRepository;
import com.varun.appbackend.repository.UserRepository;
import com.varun.appbackend.service.JwtService;
import com.varun.appbackend.service.MailService;
import com.varun.appbackend.util.ForgotPasswordRateLimiter;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * REST controller for authentication related endpoints
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final ForgotPasswordRateLimiter rateLimiter;
    private final MailService mailService;
    private final JwtService jwtService;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          PasswordResetTokenRepository passwordResetTokenRepository,
                          MailService mailService,
                          ForgotPasswordRateLimiter rateLimiter,
                          JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.mailService = mailService;
        this.rateLimiter = rateLimiter;
        this.jwtService = jwtService;
    }

    /**
     * POST register user
     * @param dto user register request
     * @return user registered successfully
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequestDTO dto) {
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("Passwords do not match");
        }

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already registered");
        }

        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username is already taken");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }

    /**
     * POST user login
     * @param dto user login request
     * @return user login successfully
     */
    @PostMapping("/login")
    public ResponseEntity<JwtResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        Optional<User> userOpt = userRepository.findByEmail(dto.getIdentifier());

        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByUsername(dto.getIdentifier());
        }

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = jwtService.generateToken(user.getUsername());

        return ResponseEntity.ok(new JwtResponseDTO(token));
    }


    /**
     * POST user request for forgot password
     * @param dto forgot password request from the user
     * @return Generates token that is sent to the registered email of the user
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO dto) {
        if (!rateLimiter.isAllowed(dto.getEmail())) {
            long wait = rateLimiter.getRemainingCooldownSeconds(dto.getEmail());
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Please wait " + wait + " seconds before requesting another reset.");
        }

        Optional<User> userOpt = userRepository.findByEmail(dto.getEmail());

        // Return generic response
        if (userOpt.isEmpty()) {
            return ResponseEntity.ok("If this email exists, a reset link has been sent");
        }

        passwordResetTokenRepository.deleteByEmail(dto.getEmail());

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setEmail(dto.getEmail());
        resetToken.setToken(token);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(15));

        passwordResetTokenRepository.save(resetToken);

        try {
            mailService.sendPasswordResetEmail(dto.getEmail(), token);
        } catch (Exception e) {
            System.err.println("Error while sending password reset email: " + e.getMessage());
        }

        return ResponseEntity.ok("If this email exists, a reset link has been sent");
    }



    /**
     * POST user reset password request
     * @param dto reset password request by the user
     * @return password is reset successfully
     */
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordDTO dto) {
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("Passwords do not match");
        }

        Optional<PasswordResetToken> tokenOpt = passwordResetTokenRepository.findByToken(dto.getToken());

        if (tokenOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid or expired token");
        }

        PasswordResetToken token = tokenOpt.get();

        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Token has expired");
        }

        Optional<User> userOpt = userRepository.findByEmail(token.getEmail());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        User user = userOpt.get();
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);

        // Token is one time use only
        passwordResetTokenRepository.delete(token);

        return ResponseEntity.ok("Password has been reset successfully");
    }

    @GetMapping
    public ResponseEntity<UserResponseDTO> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = (User) authentication.getPrincipal();

        UserResponseDTO dto = new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );

        return ResponseEntity.ok(dto);
    }
}
