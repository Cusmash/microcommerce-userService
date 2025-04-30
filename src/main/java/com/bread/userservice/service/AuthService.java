package com.bread.userservice.service;

import com.bread.userservice.dto.auth.AuthResponseDTO;
import com.bread.userservice.dto.auth.SignInInputDTO;
import com.bread.userservice.dto.auth.SignUpInputDTO;

public interface AuthService {
    AuthResponseDTO signUp(SignUpInputDTO signUpInputDTO);
    AuthResponseDTO signIn(SignInInputDTO signInInputDTO);
    void logout(String token);
}

