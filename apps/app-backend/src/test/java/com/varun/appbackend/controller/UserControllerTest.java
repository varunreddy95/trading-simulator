package com.varun.appbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.varun.appbackend.config.TestMockBeans;
import com.varun.appbackend.model.User;
import com.varun.appbackend.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;


import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for UserController endpoints
 */
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@ContextConfiguration(classes = {UserController.class, TestMockBeans.class})
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("Should register a new user successfully")
    void shouldRegisterNewUser() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("varun");
        user.setEmail("xyz@gmail.com");
        user.setPassword("password");

        when(userService.registerUser(user)).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("varun"));
    }

    @Test
    @DisplayName("Should fetch user by username successfully")
    void shouldGetUserByUsername() throws Exception {
        User user = new User();
        user.setId(2L);
        user.setUsername("john");
        user.setEmail("xyz@gmail.com");
        user.setPassword("pass123");

        when(userService.findUserByUsername("john")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/username/{username}", "john"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.username").value("john"));
    }

    @Test
    @DisplayName("Should fetch user by email successfully")
    void shouldGetUserByEmail() throws Exception {
        User user = new User();
        user.setId(2L);
        user.setUsername("john");
        user.setEmail("xyz@gmail.com");
        user.setPassword("pass123");

        when(userService.findUserByEmail("xyz@gmail.com")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/email/{email}", "xyz@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.email").value("xyz@gmail.com"));
    }

    @Test
    @DisplayName("Should fetch user by ID successfully")
    void shouldGetUserById() throws Exception {
        User user = new User();
        user.setId(3L);
        user.setUsername("alice");
        user.setEmail("xyz@gmail.com");
        user.setPassword("alice123");

        when(userService.findById(3L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/{id}", 3L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.username").value("alice"));
    }
}