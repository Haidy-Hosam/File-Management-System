package com.ADIB.FileSystem.mapper;

import com.ADIB.FileSystem.dto.response.UserResponse;
import com.ADIB.FileSystem.Model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse mapToResponse(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setName(user.getUsername());
        userResponse.setEmail(user.getEmail());

        return userResponse;

    }
}
