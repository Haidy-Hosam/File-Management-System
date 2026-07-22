package com.ADIB.FileSystem.security;
import com.ADIB.FileSystem.Model.User;
import com.ADIB.FileSystem.repository.UserRepo;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class SpringSecurityAuditorAware implements AuditorAware<User> {

    private final UserRepo userRepository;

    public SpringSecurityAuditorAware(UserRepo userRepository) {
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

        // authentication.getName() is typically the username/email from your JWT/UserDetails
        return userRepository.findByEmail(authentication.getName());
        // adjust to findByUsername(...) if that's your actual lookup field
    }
}