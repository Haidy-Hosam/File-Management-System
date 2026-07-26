package com.ADIB.FileSystem.mapper;

import com.ADIB.FileSystem.dto.response.AuthResponse;
import com.ADIB.FileSystem.Model.User;
import com.ADIB.FileSystem.dto.response.UserResponse;
import com.ADIB.FileSystem.repository.FileRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final FileRepo fileRepo;

    public AuthResponse mapToResponse(User user) {
        long filesCount = fileRepo.countByCreatedByIdAndIsDeletedFalse(user.getId());

        return AuthResponse.builder()
                .u_id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole() != null ? user.getRole().getName() : null)
                .departmentName(user.getDepartment() != null ? user.getDepartment().getName() : null)
                .isDeleted(Boolean.TRUE.equals(user.getDeleted()))
                .filesCount(filesCount)
                .lastLogin(user.getLastLogin())
                .build();
    }

    public UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole() != null ? user.getRole().getName() : null)
                .isManager(user.getRole() != null && user.getRole().getId() == 2)
                .build();
    }
}