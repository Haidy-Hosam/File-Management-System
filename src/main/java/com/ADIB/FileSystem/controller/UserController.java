package com.ADIB.FileSystem.controller;

import com.ADIB.FileSystem.Model.User;
import com.ADIB.FileSystem.dto.request.UserRequest;
import com.ADIB.FileSystem.dto.response.UserResponse;
import com.ADIB.FileSystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserResponse> getUser(@RequestParam UserRequest request){
        if(request.getName() == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(userService.getUser(request));
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest request){
        if(request.getName() == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(userService.createUser(request));
    }

    @PutMapping
    public ResponseEntity<UserResponse> updateUser(@RequestBody UserRequest request){
        if(request.getName() == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(userService.updateUser(request));
    }




}
