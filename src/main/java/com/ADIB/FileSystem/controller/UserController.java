package com.ADIB.FileSystem.controller;

import com.ADIB.FileSystem.dto.request.UserRequest;
import com.ADIB.FileSystem.dto.response.UserResponse;
import com.ADIB.FileSystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{name}")
    public ResponseEntity<UserResponse> getUser(@PathVariable String name){
        if(name == null){
            throw new RuntimeException("Username is required");
        }
        return ResponseEntity.ok(userService.getUser(name));
    }


    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers(){
        return ResponseEntity.ok(userService.getAllUsers());
    }


    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest request){
        if(request.getName() == null){
            throw new RuntimeException("Username is required");
        }
        return ResponseEntity.ok(userService.createUser(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,@RequestBody UserRequest request){
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




}
