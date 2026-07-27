package com.ADIB.FileSystem.controller;

import com.ADIB.FileSystem.dto.request.RegisterRequest;
import com.ADIB.FileSystem.dto.request.UpdateUserRequest;
import com.ADIB.FileSystem.dto.response.AuthResponse;
import com.ADIB.FileSystem.dto.response.PageResponse;
import com.ADIB.FileSystem.dto.response.UserRoleResponse;
import com.ADIB.FileSystem.service.PagePermissionService;
import com.ADIB.FileSystem.service.UserService;
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
    private final PagePermissionService pagePermissionService; // add to constructor


    @PreAuthorize("@pagePermissionService.hasPage('Users')")
    @GetMapping("/{id:[0-9]+}")
    public ResponseEntity<AuthResponse> getUserById(@PathVariable Long id){
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PreAuthorize("@pagePermissionService.hasPage('Users')")
    @GetMapping
    public ResponseEntity<List<AuthResponse>> getAllUsers(){
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PreAuthorize("@pagePermissionService.hasPage('Users')")
    @GetMapping("/search")
    public ResponseEntity<List<AuthResponse>> searchUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long roleId){
        return ResponseEntity.ok(userService.searchUsers(search, roleId));
    }

    @PreAuthorize("@pagePermissionService.hasPage('Users')")
    @PostMapping
    public ResponseEntity<AuthResponse> createUser(@RequestBody RegisterRequest request){
        if(request.getName() == null){
            throw new RuntimeException("Username is required");
        }
        return ResponseEntity.ok(userService.createUser(request));
    }

    @PreAuthorize("@pagePermissionService.hasPage('Users')")
    @PutMapping("/{id}")
    public ResponseEntity<AuthResponse> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest request){
        if(id == null){
            throw new RuntimeException("User id is required");
        }
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @PreAuthorize("@pagePermissionService.hasPage('Users')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<AuthResponse> toggleStatus(@PathVariable Long id){
        if(id == null){
            throw new RuntimeException("User id is required");
        }
        return ResponseEntity.ok(userService.toggleUserStatus(id));
    }

    @PreAuthorize("@pagePermissionService.hasPage('Users')")
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id){
        if(id== null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/profile")
    public ResponseEntity<AuthResponse> getMyProfile() {
        return ResponseEntity.ok(userService.getCurrentUserProfile());
    }

//    @GetMapping("/pages")
//    public ResponseEntity<List<PageResponse>> getMyPages(){
//        return ResponseEntity.ok(userService.getCurrentUserPages());
//    }

    @GetMapping("/pages")
    public ResponseEntity<List<PageResponse>> getMyPages(){
        return ResponseEntity.ok(pagePermissionService.getMyPages());
    }

    @GetMapping("/userRole")
    public ResponseEntity<UserRoleResponse> getUserRole(){
        return ResponseEntity.ok(userService.getUserRole());
    }
}