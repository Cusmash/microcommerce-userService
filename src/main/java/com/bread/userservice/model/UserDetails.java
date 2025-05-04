package com.bread.userservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDetails {
    @Id
    private String id;

    private String firstName;
    private String lastName;
    private String phone;
    private String address;
}