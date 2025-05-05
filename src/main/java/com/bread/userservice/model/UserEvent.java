package com.bread.userservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEvent {
    private String eventType;
    private String userId;
    private String email;
    private String firstName;
    private String lastName;
    private String token;
}
