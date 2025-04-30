package com.bread.userservice.dto.auth;

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
