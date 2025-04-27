package com.bread.userservice.user.service;

import com.bread.userservice.model.User;
import com.bread.userservice.user.dto.UserInputDTO;

import java.util.List;

public interface UserService {
    User createUser(UserInputDTO userInputDTO);
    User getUserById(String id);
    List<User> getAllUsers();
    void deleteUser(String id);
}
