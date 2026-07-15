package com.ADIB.FileSystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private Long u_id;
    private String name;
    private String email;
    private String role;
    private String departmentName;
    private boolean isDeleted;
}