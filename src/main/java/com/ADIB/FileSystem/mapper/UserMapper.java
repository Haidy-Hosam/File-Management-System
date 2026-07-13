package com.ADIB.FileSystem.mapper;

import com.ADIB.FileSystem.Model.Role;
import com.ADIB.FileSystem.dto.response.UserResponse;
import com.ADIB.FileSystem.Model.User;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserMapper {

    public static UserResponse mapToResponse(User user) {
       UserResponse userResponse = UserResponse.builder()
               .u_id(user.getU_id())
               .name(user.getName())
               .email(user.getEmail())
               .role(user.getRole().getName())
               .departmentName(user.getDepartment().getName())
               .build();

        return userResponse ;

    }
}
