package com.ADIB.FileSystem.mapper;

import com.ADIB.FileSystem.Model.Role;
import com.ADIB.FileSystem.dto.response.UserResponse;
import com.ADIB.FileSystem.Model.User;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserResponse mapToResponse(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setU_id(user.getU_id());
        userResponse.setName(user.getUsername());
        userResponse.setEmail(user.getEmail());
        userResponse.setRole(
                user.getRoles()
                        .stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet()));

        return userResponse;

    }
}
