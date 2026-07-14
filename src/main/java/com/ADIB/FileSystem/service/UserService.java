package com.ADIB.FileSystem.service;

import com.ADIB.FileSystem.Model.Department;
import com.ADIB.FileSystem.Model.Role;
import com.ADIB.FileSystem.Model.User;
import com.ADIB.FileSystem.exception.ResourceAlreadyExistsException;
import com.ADIB.FileSystem.exception.ResourceNotFoundException;
import com.ADIB.FileSystem.mapper.UserMapper;
import com.ADIB.FileSystem.repository.DepartmentRepo;
import com.ADIB.FileSystem.repository.RoleRepo;
import com.ADIB.FileSystem.repository.UserRepo;
import com.ADIB.FileSystem.dto.response.UserResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
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
private final DepartmentRepo departmentRepo;
private final PasswordEncoder passwordEncoder;


    public UserResponse getUser(String name) {
        User user =userRepo.findByname(name);
        if(user == null){
            throw new ResourceNotFoundException("Username not found");
        }
        return userMapper.mapToResponse(user);
    }
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepo.findAll();
        if(users.isEmpty()){
            throw new ResourceNotFoundException("No users yet.");
        }
        return users.stream().map(user -> userMapper.mapToResponse(user)).collect(Collectors.toList());
    }

    public UserResponse createUser(UserRequest  request) {
        User user = userRepo.findByUsername(request.getName());
        if(user!=null){
           throw new ResourceAlreadyExistsException("Username exist");
        }
        Role role = roleRepo.findById(request.getRoleId()).orElseThrow(() -> new ResourceNotFoundException("Role not found"));
        Department department = departmentRepo.findById(request.getDepartmentId()).orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        User newUser = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .username(request.getName())
                .name(request.getName())
                .role(role)
                .department(department)
                .deleted(false)
                .build();

        return userMapper.mapToResponse(userRepo.save(newUser));
    }

    public UserResponse updateUser(Long id,UserRequest  request) {
//        User user = userRepo.findByname(request.getName());
        User user =userRepo.findById(id).orElseThrow(()-> new ResourceNotFoundException("User not found"));

        Role role = roleRepo.findById(request.getRoleId()).orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setUsername(request.getName());
        user.setName(request.getName());
        user.setDeleted(request.isDeleted());
        user.setRole(role);

        return userMapper.mapToResponse(userRepo.save(user));
    }

    public void deleteUser(Long id) {
        User user = userRepo.findById(id).orElseThrow(()-> new ResourceNotFoundException("User not found"));

        userRepo.delete(user);
    }

}


