package com.ADIB.FileSystem.security;

import com.ADIB.FileSystem.Model.User;
import com.ADIB.FileSystem.exception.ResourceNotFoundException;
import com.ADIB.FileSystem.mapper.FileForwardMapper;
import com.ADIB.FileSystem.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrentUserProvider {
    private final UserRepo userRepo;

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated()) {
            throw new ResourceNotFoundException("No authenticated user found");
        }
        String username = authentication.getName();
        User user = userRepo.findByUsername(username);
        if(user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        return user;
    }
}
