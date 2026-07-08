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

    @Autowired
    UserRepo userRepo;

    UserMapper userMapper;


    public UserService(UserRepo userRepo, UserMapper userMapper) {
        this.userRepo = userRepo;
        this.userMapper = userMapper;}



    public User getUser(String  nn) {
        User user = userRepo.findByusername(nn);
        System.out.println(user.getEmail());
        return user;
    }

}


