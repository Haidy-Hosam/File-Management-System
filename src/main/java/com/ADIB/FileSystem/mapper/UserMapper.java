package com.ADIB.FileSystem.mapper;

import com.ADIB.FileSystem.dto.response.AuthResponse;
import com.ADIB.FileSystem.Model.User;
import com.ADIB.FileSystem.dto.response.UserResponse;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public static AuthResponse mapToResponse(User user) {
       AuthResponse userResponse = AuthResponse.builder()
               .u_id(user.getId())
               .name(user.getName())
               .email(user.getEmail())
               .role(user.getRole() != null ? user.getRole().getName() : null)
               .departmentName(user.getDepartment() != null ? user.getDepartment().getName() : null)
               .isDeleted(Boolean.TRUE.equals(user.getDeleted()))
               .build();

        return userResponse ;

    }

    public static UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole() != null ? user.getRole().getName() : null)
                .isManager(user.getRole() != null && user.getRole().getId()==2)
                .build();

    }
}
