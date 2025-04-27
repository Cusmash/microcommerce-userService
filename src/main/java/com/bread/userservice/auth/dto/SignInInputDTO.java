package com.bread.userservice.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SignInInputDTO {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;
}
