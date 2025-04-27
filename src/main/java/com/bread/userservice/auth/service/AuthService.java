package com.bread.userservice.auth.service;

import com.bread.userservice.auth.dto.AuthResponseDTO;
import com.bread.userservice.auth.dto.SignInInputDTO;
import com.bread.userservice.auth.dto.SignUpInputDTO;

public interface AuthService {
    AuthResponseDTO signUp(SignUpInputDTO signUpInputDTO);
    AuthResponseDTO signIn(SignInInputDTO signInInputDTO);
    void logout(String token);
}

