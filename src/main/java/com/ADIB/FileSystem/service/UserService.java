package com.ADIB.FileSystem.service;

import com.ADIB.FileSystem.Model.User;
import com.ADIB.FileSystem.mapper.UserMapper;
import com.ADIB.FileSystem.repository.UserRepo;
import com.ADIB.FileSystem.dto.response.UserResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ADIB.FileSystem.dto.request.*;


@Service
@RequiredArgsConstructor
public class UserService {
private final UserRepo userRepo;
private final UserMapper userMapper;


    public UserResponse getUser(UserRequest  request) {
        User user = userRepo.findByusername(request.getName());
        return userMapper.mapToResponse(user);
    }

    public UserResponse createUser(UserRequest  request) {
        User user = userRepo.findByusername(request.getName());
        if(user==null){
            throw new RuntimeException("Username exist");
        }
        User newUser = User.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .username(request.getName())
                .name(request.getName())
                .build();

        return userMapper.mapToResponse( userRepo.save(newUser));
    }

    public UserResponse updateUser(UserRequest  request) {
        User user = userRepo.findByusername(request.getName());
        if(user==null){
            throw new RuntimeException("Username not exist");
        }
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setUsername(request.getName());
        user.setName(request.getName());

        return userMapper.mapToResponse(userRepo.save(user));
    }

}


