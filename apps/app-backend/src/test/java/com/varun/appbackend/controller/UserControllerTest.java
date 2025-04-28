package com.varun.appbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.varun.appbackend.model.User;
import com.varun.appbackend.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for UserController endpoints
 */
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(UserController.class)
@SpringJUnitConfig
@ActiveProfiles("test")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("Should register a new user")
    void shouldRegisterNewUser() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("varun");
        user.setPassword("password");

        when(userService.registerUser(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/api/users/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("varun"));
    }

    @Test
    @DisplayName("Should fetch user by username")
    void shouldGetUserByUsername() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("varun");

        when(userService.findUserByUsername("varun")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/username/{username}", "varun"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("varun"));
    }

    @Test
    @DisplayName("Should fetch user by ID")
    void shouldGetUserById() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("varun");

        when(userService.findById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("varun"));
    }
}
