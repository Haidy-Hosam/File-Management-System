package com.ADIB.FileSystem.mapper;

import com.ADIB.FileSystem.Business.Model.User;
import com.ADIB.FileSystem.Business.dto.response.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthMapper {

    public AuthResponse MapToResponse(User user, String accessToken, String refreshToken) {
        AuthResponse authResponse =  AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .name(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().getName())
                .departmentName(user.getDepartment() != null ? user.getDepartment().getName() : null)
                .isDeleted(false)
                .build();

        return authResponse;
    }
}
