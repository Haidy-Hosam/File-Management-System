package com.ADIB.FileSystem.Business.service;

import com.ADIB.FileSystem.Business.dto.request.RegisterRequest;
import com.ADIB.FileSystem.Business.dto.request.UpdateUserRequest;
import com.ADIB.FileSystem.Business.Model.Department;
import com.ADIB.FileSystem.Business.Model.Role;
import com.ADIB.FileSystem.Business.Model.User;
import com.ADIB.FileSystem.Business.dto.response.UserRoleResponse;
import com.ADIB.FileSystem.Business.Exceptions.ResourceAlreadyExistsException;
import com.ADIB.FileSystem.Business.Exceptions.ResourceNotFoundException;
import com.ADIB.FileSystem.Business.service.Permissions.PagePermissionService;
import com.ADIB.FileSystem.DataAccess.repository.UserRepo;
import com.ADIB.FileSystem.mapper.UserMapper;
import com.ADIB.FileSystem.DataAccess.repository.DepartmentRepo;
import com.ADIB.FileSystem.DataAccess.repository.RoleRepo;
import com.ADIB.FileSystem.Business.dto.response.AuthResponse;

import com.ADIB.FileSystem.security.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final DepartmentRepo departmentRepo;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUserProvider currentUserProvider;
    private final PagePermissionService pagePermissionService;


    private static final Long MANAGER_ROLE_ID = 2L;

    public AuthResponse getUserById(Long id) {
        User target = getUserByIdOrThrow(id);
        if (!pagePermissionService.hasFullReadAccess("Users") && !inMyDepartment(target)) {
            throw new ResourceNotFoundException("User not found");
        }
        return userMapper.mapToResponse(target);
    }

    public User getUserByid(Long id) { return userRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));}

    public List<AuthResponse> getAllUsers() {
        return toResponseList(getVisibleUsers());
    }

    public UserRoleResponse getUserRole() {
        User user = currentUserProvider.getCurrentUser();
        return UserRoleResponse.builder()
                .name(user.getUsername())
                .role(user.getRole().getName())
                .initials(getInitials(user.getUsername()).toUpperCase())
                .build();
    }

    public AuthResponse getCurrentUserProfile() {
        User user = currentUserProvider.getCurrentUser();
        return AuthResponse.builder()
                .u_id(user.getId())
                .name(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().getName())
                .departmentName(user.getDepartment() != null ? user.getDepartment().getName() : null)
                .isDeleted(user.getDeleted())
                .build();
    }

    public List<AuthResponse> searchUsers(String search, Long roleId) {
        return getVisibleUsers().stream()
                .filter(u -> roleId == null || (u.getRole() != null && u.getRole().getId().equals(roleId)))
                .filter(u -> search == null || search.isBlank()
                        || u.getName().toLowerCase().contains(search.toLowerCase())
                        || u.getEmail().toLowerCase().contains(search.toLowerCase()))
                .map(userMapper::mapToResponse)
                .collect(Collectors.toList());
    }

    public AuthResponse createUser(RegisterRequest request) {
        if (userRepo.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("Email already exists");
        }

        if(userRepo.existsByUsername(request.getUsername())) {
            throw new ResourceAlreadyExistsException("Username already exists");
        }

        Role role = getRoleOrThrow(request.getRoleId());
        Department department = getDepartmentOrThrow(request.getDepartmentId());

        if (MANAGER_ROLE_ID.equals(role.getId())) {
            int managerExists = userRepo.ManagerExistsInDepartment(request.getDepartmentId());
            if (managerExists > 0) {
                throw new ResourceAlreadyExistsException(
                        "Department '" + department.getName() + "' already has a manager");
            }
        }

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

    public AuthResponse updateUser(Long id, UpdateUserRequest request) {
        User user = getUserByIdOrThrow(id);
        Role role = getRoleOrThrow(request.getRoleId());
        Department department = getDepartmentOrThrow(request.getDepartmentId());

        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setName(request.getName());
        user.setDepartment(department);
        user.setRole(role);
        if (request.getIsDeleted() != null) {
            user.setDeleted(request.getIsDeleted());
        }

        return userMapper.mapToResponse(userRepo.save(user));
    }

    public AuthResponse toggleUserStatus(Long id) {
        User user = getUserByIdOrThrow(id);
        user.setDeleted(!Boolean.TRUE.equals(user.getDeleted()));
        return userMapper.mapToResponse(userRepo.save(user));
    }

    public void deleteUser(Long id) {
        userRepo.delete(getUserByIdOrThrow(id));
    }


    private boolean inMyDepartment(User target) {
        Department myDept = currentUserProvider.getCurrentUser().getDepartment();
        return myDept != null && target.getDepartment() != null && myDept.getId().equals(target.getDepartment().getId());
    }

    private List<User> getVisibleUsers() {
        if (pagePermissionService.hasFullReadAccess("Users")) {
            return userRepo.findAll();
        }
        Department dept = currentUserProvider.getCurrentUser().getDepartment();
        return dept == null ? List.of() : userRepo.findByDepartmentId(dept.getId());
    }

    private User getUserByIdOrThrow(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private Role getRoleOrThrow(Long roleId) {
        return roleRepo.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
    }

    private Department getDepartmentOrThrow(Long departmentId) {
        return departmentRepo.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
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

    private List<AuthResponse> toResponseList(List<User> users) {
        return users.stream()
                .map(userMapper::mapToResponse)
                .collect(Collectors.toList());
    }


}