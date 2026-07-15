package com.ADIB.FileSystem.mapper;

import com.ADIB.FileSystem.dto.response.AuthResponse;
import com.ADIB.FileSystem.Model.User;
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
}
