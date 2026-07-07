package com.ADIB.FileSystem.controller;

import com.ADIB.FileSystem.repository.UserRepo;
import com.ADIB.FileSystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @GetMapping
    public ResponseEntity<UserResponse> getUser(@RequestBody String name){
        return ResponseEntity.ok(userService.getUser(name));
    }
}
