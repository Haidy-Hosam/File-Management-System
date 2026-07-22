package com.ADIB.FileSystem.service;

import com.ADIB.FileSystem.Model.Department;
import com.ADIB.FileSystem.Model.Role;
import com.ADIB.FileSystem.Model.User;
import com.ADIB.FileSystem.dto.response.PageResponse;
import com.ADIB.FileSystem.dto.response.UserRoleResponse;
import com.ADIB.FileSystem.exception.ResourceAlreadyExistsException;
import com.ADIB.FileSystem.exception.ResourceNotFoundException;
import com.ADIB.FileSystem.mapper.UserMapper;
import com.ADIB.FileSystem.repository.DepartmentRepo;
import com.ADIB.FileSystem.repository.RoleRepo;
import com.ADIB.FileSystem.repository.UserRepo;
import com.ADIB.FileSystem.dto.response.AuthResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.ADIB.FileSystem.dto.request.*;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserService {
private final UserRepo userRepo;
private final UserMapper userMapper;
private final RoleRepo roleRepo;
private final DepartmentRepo departmentRepo;
private final PasswordEncoder passwordEncoder;


    public AuthResponse getUser(String name) {
        User user =userRepo.findByname(name);
        if(user == null){
            throw new ResourceNotFoundException("Username not found");
        }
        return userMapper.mapToResponse(user);
    }
    public List<AuthResponse> getAllUsers() {
        List<User> users = userRepo.findAll();
        if(users.isEmpty()){
            throw new ResourceNotFoundException("No users yet.");
        }
        return users.stream().map(user -> userMapper.mapToResponse(user)).collect(Collectors.toList());
    }

    public AuthResponse createUser(RegisterRequest request) {
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

    public AuthResponse updateUser(Long id, RegisterRequest request) {
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

    public List<PageResponse> getCurrentUserPages(){
        Authentication auth =  SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authentication = " + auth);
        System.out.println("Principal = " + auth.getPrincipal());
        System.out.println("Name = " + auth.getName());
        System.out.println("Authorities = " + auth.getAuthorities());

        String email = auth.getName();
        User user = userRepo.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
        Role role = user.getRole();

        return role.getPages()
                .stream()
                .map(page -> new PageResponse(
                        page.getPageName(),
                        page.getRoute()
                ))
                .toList();
    }

    public UserRoleResponse getUserRole() {
        Authentication auth =  SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepo.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
        String userInitials = this.getInitials(user.getUsername());
        return UserRoleResponse.builder()
                 .name(user.getUsername())
                 .role(user.getRole().getName())
                .initials(userInitials.toUpperCase())
                 .build();
    }

    private String getInitials(String name){
        StringBuilder sb = new StringBuilder();
        String trimmedName = name.trim();
        for (int i = 0; i < trimmedName.length(); i++) {
            if(i==0 || name.charAt(i-1)==' '){
                sb.append(name.charAt(i));
            }
        }
        return sb.toString();
    }

}


