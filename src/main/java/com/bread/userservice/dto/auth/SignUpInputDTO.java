package com.bread.userservice.dto.auth;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class SignUpInputDTO {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;
    
    private Boolean acceptedTerms;
    private Boolean acceptedPromotions;

}
