package com.bread.userservice.service.impl;

import com.bread.userservice.config.JwtProperties;
import com.bread.userservice.dto.auth.AuthResponseDTO;
import com.bread.userservice.dto.auth.SignInInputDTO;
import com.bread.userservice.dto.auth.SignUpInputDTO;
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

    @Override
    public AuthResponseDTO signIn(SignInInputDTO input) {
        User user = userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new RuntimeException("worng credentials"));

        System.out.println("Input password: " + input.getPassword());
        System.out.println("Stored password (hash): " + user.getPassword());
        System.out.println("BCrypt result: " + BCrypt.checkpw(input.getPassword(), user.getPassword()));

        boolean passwordMatches = BCrypt.checkpw(input.getPassword(), user.getPassword());
        if (!passwordMatches) {
            throw new RuntimeException("wrong credentials");
        }

        String token = jwtUtils.generateJwtToken(user.getId());

        String redisKey = "auth:" + token;
        try {
            redisTemplate.opsForValue().set(redisKey, user.getId(), Duration.ofMillis(jwtProperties.getExpirationMs()));
        } catch (Exception ex) {
            log.error("No se pudo conectar a Redis", ex);
        }

        return new AuthResponseDTO(token);
    }

    @Override
    public AuthResponseDTO signUp(SignUpInputDTO input) {
        if (userRepository.existsByEmail(input.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        String userId = java.util.UUID.randomUUID().toString();
        String hashedPassword = org.springframework.security.crypto.bcrypt.BCrypt.hashpw(input.getPassword(),
                org.springframework.security.crypto.bcrypt.BCrypt.gensalt());

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
            throw new RuntimeException("Token not found");
        }
    }

    @Override
    public Boolean deleteAccount(String token) {
        String userId = jwtUtils.getUserIdFromJwtToken(token);
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found");
        }

        userRepository.deleteById(userId);
        userDetailsRepository.deleteById(userId);
        redisTemplate.delete("auth:" + token);
        return true;
    }

}
