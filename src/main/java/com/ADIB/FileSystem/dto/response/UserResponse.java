package com.ADIB.FileSystem.dto.response;

import com.ADIB.FileSystem.Model.User;
import com.ADIB.FileSystem.dto.request.UserRequest;
import com.ADIB.FileSystem.exception.ResourceAlreadyExistsException;
import com.ADIB.FileSystem.exception.ResourceNotFoundException;
import com.ADIB.FileSystem.mapper.UserMapper;
import com.ADIB.FileSystem.repository.UserRepo;
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

    private Long u_id;
    private String name;
    private String email;
    private String role;
    private String departmentName;
}