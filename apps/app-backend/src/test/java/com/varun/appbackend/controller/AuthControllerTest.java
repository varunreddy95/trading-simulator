package com.varun.appbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.varun.appbackend.dto.RegisterRequestDTO;
import com.varun.appbackend.repository.UserRepository;
import com.varun.appbackend.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepository;

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

}
