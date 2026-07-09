package com.ADIB.FileSystem.service;

import com.ADIB.FileSystem.Model.Role;
import com.ADIB.FileSystem.Model.User;
import com.ADIB.FileSystem.mapper.UserMapper;
import com.ADIB.FileSystem.repository.RoleRepo;
import com.ADIB.FileSystem.repository.UserRepo;
import com.ADIB.FileSystem.dto.response.UserResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ADIB.FileSystem.dto.request.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserService {
private final UserRepo userRepo;
private final UserMapper userMapper;
private final RoleRepo roleRepo;


    public UserResponse getUser(String name) {
        User user = userRepo.findByname(name);
        return userMapper.mapToResponse(user);
    }
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepo.findAll();
        return users.stream().map(user -> userMapper.mapToResponse(user)).collect(Collectors.toList());
    }

    public UserResponse createUser(UserRequest  request) {
        User user = userRepo.findByname(request.getName());
        if(user!=null){
            throw new RuntimeException("Username exist");
        }
        Role role = roleRepo.findById(request.getRoleId()).orElseThrow(() -> new RuntimeException("Role not found"));

        User newUser = User.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .username(request.getName())
                .name(request.getName())
                .role(role)
                .build();

        return userMapper.mapToResponse(userRepo.save(newUser));
    }

    public UserResponse updateUser(UserRequest  request) {
        User user = userRepo.findByname(request.getName());
        if(user==null){
            throw new RuntimeException("Username not exist");
        }
        Role role = roleRepo.findById(request.getRoleId()).orElseThrow(() -> new RuntimeException("Role not found"));

        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setUsername(request.getName());
        user.setName(request.getName());
        user.setRole(role);

        return userMapper.mapToResponse(userRepo.save(user));
    }

    public void deleteUser(String username) {
        User user = userRepo.findByname(username);
        if (user == null) {
            throw new RuntimeException("Username not exist");
        }
        userRepo.delete(user);
    }

}


