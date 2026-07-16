package com.ADIB.FileSystem.controller;
import com.ADIB.FileSystem.dto.request.RegisterRequest;
import com.ADIB.FileSystem.dto.response.AuthResponse;
import com.ADIB.FileSystem.dto.response.PageResponse;
import com.ADIB.FileSystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("api/v1/user")
@RequiredArgsConstructor
@PreAuthorize("@permissionService.hasPage('User')")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {
    private final UserService userService;

    @GetMapping("/{name}")
    public ResponseEntity<AuthResponse> getUser(@PathVariable String name){
        if(name == null){
            throw new RuntimeException("Username is required");
        }
        return ResponseEntity.ok(userService.getUser(name));
    }

    @GetMapping
    public ResponseEntity<List<AuthResponse>> getAllUsers(){
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping
    public ResponseEntity<AuthResponse> createUser(@RequestBody RegisterRequest request){
        if(request.getName() == null){
            throw new RuntimeException("Username is required");
        }
        return ResponseEntity.ok(userService.createUser(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthResponse> updateUser(@PathVariable Long id, @RequestBody RegisterRequest request){
        if(id== null){
            throw new RuntimeException("User id is required");
        }
        return ResponseEntity.ok(userService.updateUser(id,request));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id){
        if(id== null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/pages")
    public ResponseEntity<List<PageResponse>> getMyPages(){
        return ResponseEntity.ok(userService.getCurrentUserPages());
    }
}