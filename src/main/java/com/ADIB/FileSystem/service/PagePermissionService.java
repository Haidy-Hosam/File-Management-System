package com.ADIB.FileSystem.service;

import com.ADIB.FileSystem.Model.RolePagePermission;
import com.ADIB.FileSystem.Model.User;
import com.ADIB.FileSystem.dto.response.PageResponse;
import com.ADIB.FileSystem.exception.ResourceNotFoundException;
import com.ADIB.FileSystem.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PagePermissionService {
    private final UserRepo userRepo;

    @Transactional(readOnly = true)
    public boolean hasPage(String pageName){
        User auth = currentUser();
        String email = auth.getName();
        User user = userRepo.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return user.getRole()
                .getRolePagePermissions()
                .stream()
                .anyMatch( rpp -> rpp.getPage().getPageName().equalsIgnoreCase(pageName));
    }
    @Transactional(readOnly = true)
    public List<PageResponse> getMyPages() {
        User user = currentUser();
        return user.getRole()
                .getRolePagePermissions()
                .stream()
                .map(RolePagePermission::getPage)
                .distinct()
                .map(p -> new PageResponse(p.getId(), p.getPageName(), p.getRoute()))
                .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public boolean hasPermission(String pageName,String permissionName){
        User user = currentUser();
        return user.getRole()
                .getRolePagePermissions()
                .stream()
                .anyMatch(rpp -> rpp.getPage().getPageName().equalsIgnoreCase(pageName)
                        && rpp.getPermission().getPermissionName().equalsIgnoreCase(permissionName));
    }

    private User currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
