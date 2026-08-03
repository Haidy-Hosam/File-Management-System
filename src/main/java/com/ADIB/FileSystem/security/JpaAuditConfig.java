package com.ADIB.FileSystem.security;

import com.ADIB.FileSystem.Business.Model.User;
import com.ADIB.FileSystem.Business.service.UserService;
import com.ADIB.FileSystem.DataAccess.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@RequiredArgsConstructor
public class JpaAuditConfig {
    @Bean
    public org.springframework.data.domain.AuditorAware<User> auditorAware(UserService userService) {
        return new AuditorAware(userService);
    }
}
