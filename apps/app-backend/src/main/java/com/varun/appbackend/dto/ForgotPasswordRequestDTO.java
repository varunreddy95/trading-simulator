package com.varun.appbackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO representing forgot password request
 */
@Data
public class ForgotPasswordRequestDTO {

    @NotBlank
    @Email
    private String email;
}
