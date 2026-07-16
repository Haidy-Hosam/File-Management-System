package com.ADIB.FileSystem.service;

import com.ADIB.FileSystem.Model.User;
import com.ADIB.FileSystem.exception.ResourceNotFoundException;
import com.ADIB.FileSystem.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PermissionService {
    private final UserRepo userRepo;

    @Transactional(readOnly = true)
    public boolean hasPage(String pageName){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepo.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));



        return user.getRole()
                .getPages()
                .stream()
                .anyMatch( page -> page.getPageName().equalsIgnoreCase(pageName));
    }
}
