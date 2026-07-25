package com.ADIB.FileSystem.security;

import com.ADIB.FileSystem.Model.User;
import com.ADIB.FileSystem.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@RequiredArgsConstructor
public class JpaAuditConfig {
private final UserRepo userRepo;
    @Bean
    public org.springframework.data.domain.AuditorAware<User> auditorAware(UserRepo userRepository) {
        return new AuditorAware(userRepository);
    }
}
