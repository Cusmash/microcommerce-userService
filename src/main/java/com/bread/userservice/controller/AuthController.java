package com.bread.userservice.controller;

import com.bread.userservice.dto.auth.AuthResponseDTO;
import com.bread.userservice.dto.auth.SignInInputDTO;
import com.bread.userservice.dto.auth.SignUpInputDTO;
import com.bread.userservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @MutationMapping
    public AuthResponseDTO signIn(@Argument SignInInputDTO signInInput) {
        return authService.signIn(signInInput);
    }

    @MutationMapping
    public AuthResponseDTO signUp(@Argument SignUpInputDTO signUpInput) {
        return authService.signUp(signUpInput);
    }

    @MutationMapping
    public String logout(@Argument String token) {
        authService.logout(token);
        return "Logged out successfully";
    }

    @MutationMapping
    public Boolean deleteAccount(@Argument String token) {
        return authService.deleteAccount(token);
    }

}
