package com.varun.appbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.varun.appbackend.dto.LoginRequestDTO;
import com.varun.appbackend.dto.RegisterRequestDTO;
import com.varun.appbackend.repository.UserRepository;
import com.varun.appbackend.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = AuthController.class)
@ActiveProfiles("test")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

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

}
