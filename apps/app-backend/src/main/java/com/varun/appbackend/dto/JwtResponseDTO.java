package com.varun.appbackend.dto;

/**
 * DTO representing Jwt response
 */
public class JwtResponseDTO {
    private String token;

    public JwtResponseDTO(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
