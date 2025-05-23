package com.bread.userservice.dto.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDetailsResponseDTO {
    private String firstName;
    private String lastName;
    private String phone;
    private String address;
}