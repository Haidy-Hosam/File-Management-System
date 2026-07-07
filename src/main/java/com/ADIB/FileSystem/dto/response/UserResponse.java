package com.ADIB.FileSystem.DTO;

import com.ADIB.FileSystem.Enum.USER_STATUS;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private USER_STATUS status;
    private String departmentName;
    private Set<String> roles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}