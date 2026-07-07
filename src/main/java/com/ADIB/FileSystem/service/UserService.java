package com.ADIB.FileSystem.service;

import com.ADIB.FileSystem.Model.User;
import com.ADIB.FileSystem.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    UserRepo userRepo;

    /*
    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }*/

    private UserResponse getUser(String name) {
        return Mapper(userRepo.findByUsername(name));
    }

}
