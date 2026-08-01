package com.ADIB.FileSystem.security;
import com.ADIB.FileSystem.Business.Model.User;
import com.ADIB.FileSystem.Business.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAware implements org.springframework.data.domain.AuditorAware<User> {

    private final UserService userService;

    public AuditorAware(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Optional<User> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return Optional.empty();
        }

        Long userId = ((CustomUserDetails) authentication.getPrincipal()).getId();

        return Optional.ofNullable(userService.getUserByid(userId));
    }
}