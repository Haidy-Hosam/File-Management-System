package com.ADIB.FileSystem.security;
import com.ADIB.FileSystem.Model.User;
import com.ADIB.FileSystem.repository.UserRepo;
import com.ADIB.FileSystem.service.CustomUserDetailsService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAware implements org.springframework.data.domain.AuditorAware<User> {

    private final UserRepo userRepository;

    public AuditorAware(UserRepo userRepository) {
        this.userRepository = userRepository;
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
        // getReferenceById never hits the DB immediately - it's a lazy proxy, no query, no auto-flush
        return Optional.of(userRepository.getReferenceById(userId));   // adjust to findByUsername(...) if that's your actual lookup field
    }
}