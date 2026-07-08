package com.ADIB.FileSystem.mapper;

import com.ADIB.FileSystem.dto.response.UserResponse;
import com.ADIB.FileSystem.Model.Roles;
import com.ADIB.FileSystem.Model.User;
import org.springframework.stereotype.Component;



@Component
public class UserMapper {

    public UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getU_id())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
