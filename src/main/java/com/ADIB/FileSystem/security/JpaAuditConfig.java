package com.ADIB.FileSystem.security;

import com.ADIB.FileSystem.Model.User;
import com.ADIB.FileSystem.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@RequiredArgsConstructor
public class JpaAuditConfig {
private final UserRepo userRepo;
    @Bean
    public AuditorAware<User> auditorAware(UserRepo userRepository) {
        return new SpringSecurityAuditorAware(userRepository);
    }
}
