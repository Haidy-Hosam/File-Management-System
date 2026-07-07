package com.ADIB.FileSystem.mapper;

import com.ADIB.FileSystem.DTO.UserResponse;
import com.ADIB.FileSystem.Model.Roles;
import com.ADIB.FileSystem.Model.User;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getU_id())
                .name(user.getName())
                .email(user.getEmail())
                .status(user.getStatus() != null ? user.getStatus().name() : null)
                .departmentName(user.getDepartment() != null ? user.getDepartment().getName() : null)
                .roles(user.getRoles() != null
                        ? user.getRoles().stream()
                        .map(Roles::getName)
                        .collect(Collectors.toSet())
                        : null)
                .createdAt(user.getCreated_at())
                .updatedAt(user.getUpdated_at())
                .build();
    }
}
