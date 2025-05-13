package com.varun.appbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.varun.appbackend.config.TestMockBeans;
import com.varun.appbackend.dto.LoginRequestDTO;
import com.varun.appbackend.dto.RegisterRequestDTO;
import com.varun.appbackend.dto.ResetPasswordDTO;
import com.varun.appbackend.model.PasswordResetToken;
import com.varun.appbackend.repository.PasswordResetTokenRepository;
import com.varun.appbackend.repository.UserRepository;
import com.varun.appbackend.model.User;
import com.varun.appbackend.service.MailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = AuthController.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = {AuthController.class, TestMockBeans.class})
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private MailService mailService;

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Test
    @DisplayName("Should register user successfully with valid input")
    void shouldRegisterUser() throws Exception {
        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setUsername("varun123");
        dto.setEmail("varun@example.com");
        dto.setPassword("Password@123");
        dto.setConfirmPassword("Password@123");

        when(userRepository.findByEmail("varun@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("varun123")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON).
                content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(content().string("User registered successfully"));
    }

    @Test
    @DisplayName("Should fail if passwords do not match")
    void shouldFailIfPasswordsMismatch() throws Exception {
        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setUsername("varun123");
        dto.setEmail("varun@example.com");
        dto.setPassword("Password@123");
        dto.setConfirmPassword("Password@124");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Passwords do not match"));
    }

    @Test
    @DisplayName("Should fail if email already exists")
    void shouldFailIfEmailExists() throws Exception {
        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setUsername("varun123");
        dto.setEmail("varun@example.com");
        dto.setPassword("Password@123");
        dto.setConfirmPassword("Password@123");

        when(userRepository.findByEmail("varun@example.com")).thenReturn(Optional.of(new User()));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email already registered"));
    }

    @Test
    @DisplayName("Should fail if username is taken")
    void shouldFailIfUsernameTaken() throws Exception {
        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setUsername("varun123");
        dto.setEmail("unique@example.com");
        dto.setPassword("Password@123");
        dto.setConfirmPassword("Password@123");

        when(userRepository.findByEmail("unique@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("varun123")).thenReturn(Optional.of(new User()));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Username is already taken"));
    }

    @Test
    @DisplayName("Should login successfully with valid username and password")
    void shouldLoginWithUsername() throws Exception {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setIdentifier("varun123");
        dto.setPassword("Password@123");

        User user = new User();
        user.setUsername("varun123");
        user.setEmail("varun@example.com");
        user.setPassword("$2a$10$mockedHash"); // encoded password

        when(userRepository.findByUsername("varun123")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Password@123", user.getPassword())).thenReturn(true);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Login successful"));
    }

    @Test
    @DisplayName("Should login successfully with valid email and password")
    void shouldLoginWithEmail() throws Exception {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setIdentifier("varun@example.com");
        dto.setPassword("Password@123");

        User user = new User();
        user.setUsername("varun123");
        user.setEmail("varun@example.com");
        user.setPassword("$2a$10$mockedHash");

        when(userRepository.findByEmail("varun@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Password@123", user.getPassword())).thenReturn(true);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Login successful"));
    }

    @Test
    @DisplayName("Should fail login for incorrect password")
    void shouldFailLoginWithWrongPassword() throws Exception {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setIdentifier("varun123");
        dto.setPassword("WrongPassword");

        User user = new User();
        user.setUsername("varun123");
        user.setPassword("$2a$10$mockedHash");

        when(userRepository.findByUsername("varun123")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("WrongPassword", user.getPassword())).thenReturn(false);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid credentials"));
    }

    @Test
    @DisplayName("Should fail login for unknown username/email")
    void shouldFailLoginUserNotFound() throws Exception {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setIdentifier("unknown@example.com");
        dto.setPassword("Password@123");

        when(userRepository.findByUsername("unknown@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid credentials"));
    }

    @Test
    @DisplayName("Should send reset link for existing email")
    void shouldSendResetLinkForExistingEmail() throws Exception {
        String email = "user@example.com";
        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        doNothing().when(passwordResetTokenRepository).deleteByEmail(email);
        when(passwordResetTokenRepository.save(any())).thenReturn(new PasswordResetToken());

        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("email", email))))
                .andExpect(status().isOk())
                .andExpect(content().string("Password reset link sent"));
    }

    @Test
    @DisplayName("Should return 404 if email not found in forgot-password")
    void shouldFailIfEmailNotFound() throws Exception {
        String email = "notfound@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("email", email))))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User with this email does not exist"));
    }

    @Test
    @DisplayName("Should reset password successfully with valid token")
    void shouldResetPasswordSuccessfully() throws Exception {
        String token = UUID.randomUUID().toString();
        ResetPasswordDTO dto = new ResetPasswordDTO();
        dto.setToken(token);
        dto.setNewPassword("Password@123");
        dto.setConfirmPassword("Password@123");

        PasswordResetToken tokenEntity = new PasswordResetToken();
        tokenEntity.setToken(token);
        tokenEntity.setEmail("user@example.com");
        tokenEntity.setExpiryDate(LocalDateTime.now().plusMinutes(10));

        User user = new User();
        user.setEmail("user@example.com");

        when(passwordResetTokenRepository.findByToken(token)).thenReturn(Optional.of(tokenEntity));
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Password has been reset successfully"));
    }

    @Test
    @DisplayName("Should fail if token is invalid")
    void shouldFailIfTokenInvalid() throws Exception {
        ResetPasswordDTO dto = new ResetPasswordDTO();
        dto.setToken("invalid");
        dto.setNewPassword("Password@123");
        dto.setConfirmPassword("Password@123");

        when(passwordResetTokenRepository.findByToken("invalid")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid or expired token"));
    }

    @Test
    @DisplayName("Should fail if token is expired")
    void shouldFailIfTokenExpired() throws Exception {
        String token = "expired";
        ResetPasswordDTO dto = new ResetPasswordDTO();
        dto.setToken(token);
        dto.setNewPassword("Password@123");
        dto.setConfirmPassword("Password@123");

        PasswordResetToken tokenEntity = new PasswordResetToken();
        tokenEntity.setToken(token);
        tokenEntity.setEmail("user@example.com");
        tokenEntity.setExpiryDate(LocalDateTime.now().minusMinutes(1));

        when(passwordResetTokenRepository.findByToken(token)).thenReturn(Optional.of(tokenEntity));

        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Token has expired"));
    }

    @Test
    @DisplayName("Should fail if user with token's email is not found")
    void shouldFailIfUserMissingForToken() throws Exception {
        String token = "valid-token";
        String email = "missing@example.com";
        PasswordResetToken resetToken = new PasswordResetToken(1L, token, email, LocalDateTime.now().plusMinutes(15));

        when(passwordResetTokenRepository.findByToken(token)).thenReturn(Optional.of(resetToken));
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        ResetPasswordDTO dto = new ResetPasswordDTO();
        dto.setToken(token);
        dto.setNewPassword("Password@123");
        dto.setConfirmPassword("Password@123");

        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User not found"));
    }

    @Test
    @DisplayName("Should fail if passwords don't match")
    void shouldFailIfPasswordsDoNotMatch() throws Exception {
        ResetPasswordDTO dto = new ResetPasswordDTO();
        dto.setToken("abc");
        dto.setNewPassword("Password@123");
        dto.setConfirmPassword("Mismatch");

        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Passwords do not match"));
    }

    @Test
    @DisplayName("Should fail if user not found")
    void shouldFailIfUserNotFound() throws Exception {
        String token = "validtoken";
        ResetPasswordDTO dto = new ResetPasswordDTO();
        dto.setToken(token);
        dto.setNewPassword("Password@123");
        dto.setConfirmPassword("Password@123");

        PasswordResetToken tokenEntity = new PasswordResetToken();
        tokenEntity.setToken(token);
        tokenEntity.setEmail("ghost@example.com");
        tokenEntity.setExpiryDate(LocalDateTime.now().plusMinutes(10));

        when(passwordResetTokenRepository.findByToken(token)).thenReturn(Optional.of(tokenEntity));
        when(userRepository.findByEmail("ghost@example.com")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User not found"));
    }


}
