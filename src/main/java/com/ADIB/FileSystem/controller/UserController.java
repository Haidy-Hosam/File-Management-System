package com.ADIB.FileSystem.controller;

import com.ADIB.FileSystem.Business.dto.request.RegisterRequest;
import com.ADIB.FileSystem.Business.dto.request.UpdateUserRequest;
import com.ADIB.FileSystem.Business.dto.response.AuthResponse;
import com.ADIB.FileSystem.Business.dto.response.PagePermissionResponse;
import com.ADIB.FileSystem.Business.dto.response.PageResponse;
import com.ADIB.FileSystem.Business.dto.response.UserRoleResponse;
import com.ADIB.FileSystem.Business.service.Permissions.PagePermissionService;
import com.ADIB.FileSystem.Business.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("api/user")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {
    private final UserService userService;
    private final PagePermissionService pagePermissionService;

    @PreAuthorize("@pagePermissionService.hasPermission('Users','CREATE')")
    @PostMapping
    public ResponseEntity<AuthResponse> createUser(@RequestBody RegisterRequest request){
        if(request.getName() == null){
            throw new RuntimeException("Username is required");
        }
        return ResponseEntity.ok(userService.createUser(request));
    }

    @PreAuthorize("@pagePermissionService.hasPermission('Users','UPDATE')")
    @PutMapping("/{id}")
    public ResponseEntity<AuthResponse> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest request){
        if(id == null){
            throw new RuntimeException("User id is required");
        }
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @PreAuthorize("@pagePermissionService.hasPermission('Users','UPDATE')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<AuthResponse> toggleStatus(@PathVariable Long id){
        if(id == null){
            throw new RuntimeException("User id is required");
        }
        return ResponseEntity.ok(userService.toggleUserStatus(id));
    }

    @PreAuthorize("@pagePermissionService.hasPermission('Users','DELETE')")
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id){
        if(id== null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("@pagePermissionService.canRead('Users')")
    @GetMapping("/{id}")
    public ResponseEntity<AuthResponse> getUserById(@PathVariable Long id){
        if(id == null){
            throw new RuntimeException("User id is required");
        }
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PreAuthorize("@pagePermissionService.canRead('Users')")
    @GetMapping
    public ResponseEntity<List<AuthResponse>> getAllUsers(){
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PreAuthorize("@pagePermissionService.canRead('Users')")
    @GetMapping("/search")
    public ResponseEntity<List<AuthResponse>> searchUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long roleId){
        return ResponseEntity.ok(userService.searchUsers(search, roleId));
    }

    @GetMapping("/profile")
    public ResponseEntity<AuthResponse> getMyProfile() {
        return ResponseEntity.ok(userService.getCurrentUserProfile());
    }

    @GetMapping("/pages")
    public ResponseEntity<List<PageResponse>> getMyPages(){
        return ResponseEntity.ok(pagePermissionService.getMyPages());
    }

    @GetMapping("/userRole")
    public ResponseEntity<UserRoleResponse> getUserRole(){
        return ResponseEntity.ok(userService.getUserRole());
    }

    @GetMapping("/permissions")
    public ResponseEntity<List<PagePermissionResponse>> getMyPermissions() {
        return ResponseEntity.ok(pagePermissionService.getMyPermissions());
    }
}