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
    private Set<String> role;

//    private String departmentName;

    public UserResponse getUser(UserRequest request) {
        User user = UserRepo.findByusername(request.getName());
        if (user == null) {
            throw new ResourceNotFoundException("User with username '" + request.getName() + "' not found");
        }
        return UserMapper.mapToResponse(user);
    }

    public UserResponse createUser(UserRequest request) {
        User user = UserRepo.findByusername(request.getName());
        if (user != null) {
            throw new ResourceAlreadyExistsException("Username '" + request.getName() + "' already exists");
        }
        User newUser = User.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .username(request.getName())
                .name(request.getName())
                .build();
        return UserMapper.mapToResponse(UserRepo.save(newUser));
    }

    public UserResponse updateUser(UserRequest request) {
        User user = UserRepo.findByusername(request.getName());
        if (user == null) {
            throw new ResourceNotFoundException("Username '" + request.getName() + "' not found");
        }
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setUsername(request.getName());
        user.setName(request.getName());
        return UserMapper.mapToResponse(UserRepo.save(user));
    }

    public UserResponse deleteUser(UserRequest request) {
        User user = UserRepo.findByname(request.getName());
        if (user == null) {
            throw new ResourceNotFoundException("Username '" + request.getName() + "' not found");
        }
        UserResponse response = UserMapper.mapToResponse(user);
        UserRepo.delete(user);
        return response;
    }
}