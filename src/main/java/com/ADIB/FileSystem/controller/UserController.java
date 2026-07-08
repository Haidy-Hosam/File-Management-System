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

    @GetMapping("/ByParam")
    public ResponseEntity<User> getUser(@RequestParam String reqnameuest){
        if(reqnameuest == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(userService.getUser(reqnameuest));
    }




    @GetMapping("/ByPath/{reqnameuest}")
    public ResponseEntity<User> getUserPath(@PathVariable String reqnameuest){
        if(reqnameuest == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(userService.getUser(reqnameuest));
    }


    @PostMapping
    public ResponseEntity<User> getUser(@RequestBody UserRequest request){
        if(request == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(userService.getUser(request.getName()));
    }


}
