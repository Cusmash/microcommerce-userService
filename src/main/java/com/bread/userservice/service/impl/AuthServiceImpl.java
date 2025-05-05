package com.bread.userservice.service.impl;

import com.bread.userservice.config.JwtProperties;
import com.bread.userservice.dto.auth.AuthResponseDTO;
import com.bread.userservice.dto.auth.SignInInputDTO;
import com.bread.userservice.dto.auth.SignUpInputDTO;
import com.bread.userservice.exception.CustomGraphQLException;
import com.bread.userservice.model.User;
import com.bread.userservice.repository.UserDetailsRepository;
import com.bread.userservice.repository.UserRepository;
import com.bread.userservice.service.AuthService;
import com.bread.userservice.util.JwtUtils;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final JwtUtils jwtUtils;
    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtProperties jwtProperties;
    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponseDTO signIn(SignInInputDTO input) {
        User user = userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> {
                    log.warn("user no encontrado: {}", input.getEmail());
                    return CustomGraphQLException.invalidCredentials();
                });

        boolean passwordMatches = BCrypt.checkpw(input.getPassword(), user.getPassword());
        if (!passwordMatches) {
            log.warn("wrongPass email {}", input.getEmail());
            throw CustomGraphQLException.invalidCredentials();
        }

        String token = jwtUtils.generateJwtToken(user.getId());
        String redisKey = "auth:" + token;

        try {
            redisTemplate.opsForValue().set(redisKey, user.getId(), Duration.ofMillis(jwtProperties.getExpirationMs()));
            log.info("token guardado userId: {}", user.getId());
        } catch (Exception ex) {
            log.error("no se pudo guardar userId: {}", user.getId(), ex);
        }

        return new AuthResponseDTO(token);
    }

    @Override
    public AuthResponseDTO signUp(SignUpInputDTO input) {
        if (userRepository.existsByEmail(input.getEmail())) {
            log.warn("el email {} ya esta registrado", input.getEmail());
            throw CustomGraphQLException.userAlreadyExists();
        }

        String userId = java.util.UUID.randomUUID().toString();
        String hashedPassword = passwordEncoder.encode(input.getPassword());

        User user = User.builder()
                .id(userId)
                .email(input.getEmail())
                .password(hashedPassword)
                .acceptedTerms(Boolean.TRUE.equals(input.getAcceptedTerms()))
                .acceptedPromotions(Boolean.TRUE.equals(input.getAcceptedPromotions()))
                .build();

        userRepository.save(user);

        com.bread.userservice.model.UserDetails userDetails = com.bread.userservice.model.UserDetails.builder()
                .id(userId)
                .firstName(input.getFirstName())
                .lastName(input.getLastName())
                .build();

        userDetailsRepository.save(userDetails);

        String token = jwtUtils.generateJwtToken(userId);
        try {
            redisTemplate.opsForValue().set("auth:" + token, userId,
                java.time.Duration.ofMillis(jwtProperties.getExpirationMs()));
        } catch (Exception ex) {
            log.error("No se pudo conectar a Redis", ex);
        }

        return new AuthResponseDTO(token);
    }

    @Override
    public void logout(String token) {
        String key = "auth:" + token;
        Boolean deleted = redisTemplate.delete(key);
        if (deleted == null || !deleted) {
            log.warn("token no encontrado {}", token);
            throw CustomGraphQLException.tokenInvalid();
        }
        log.info("logout exitoso {}", token);
    }

    @Override
    public Boolean deleteAccount(String token) {
        String userId = jwtUtils.getUserIdFromJwtToken(token);
        log.info("intentando eliminar cuenta {}", userId);
        if (!userRepository.existsById(userId)) {
            throw CustomGraphQLException.notFound(userId);
        }

        userRepository.deleteById(userId);
        userDetailsRepository.deleteById(userId);
        redisTemplate.delete("auth:" + token);
        log.info("cuenta eliminada exitosamente {}", userId);
        return true;
    }

    @Override
    public boolean forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() ->{
                log.warn("recuperación de contraseña: {}", email);
                return CustomGraphQLException.invalidCredentials();
            });

        String token = jwtUtils.generateToken(user.getId(), Duration.ofMinutes(15));
        redisTemplate.opsForValue().set("reset:" + token, user.getId(), Duration.ofMinutes(15));

        log.info("Mock: http://localhost:3000/reset-password?token={}", token);

        return true;
    }

    @Override
    public boolean resetPassword(String token, String newPassword) {
        String key = "reset:" + token;
        String userId = (String) redisTemplate.opsForValue().get(key);

        if (userId == null) {
            log.warn("token inválido {}", token);
            throw CustomGraphQLException.tokenInvalid();
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> CustomGraphQLException.notFound("User"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        redisTemplate.delete(key);
        log.info("contraseña restablecida exitosamente {}", userId);
        return true;
    }
}
