package com.ADIB.FileSystem.service;

import com.ADIB.FileSystem.Model.User;
import com.ADIB.FileSystem.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    UserRepo userRepo;

    /*
    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }*/

    private String getUsername(String name) {
        return userRepo.findByUsername(name ).getName();
    }

}
