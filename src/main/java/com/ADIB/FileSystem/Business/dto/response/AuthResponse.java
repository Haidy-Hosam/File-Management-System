package com.ADIB.FileSystem.Business.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String name;
    private String email;
    private String role;
    private String departmentName;

    private Long filesCount;
    private Long u_id;
    private boolean isDeleted;

    private LocalDateTime lastLogin;
}