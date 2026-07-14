//package com.ADIB.FileSystem.security;
//
//import com.ADIB.FileSystem.Model.User;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.domain.AuditorAware;
//import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
//
//@Configuration
//@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
//public class AuditConfig {
//
//    @Bean
//    public AuditorAware<User> auditorProvider() {
//        return new AuditorAwareImpl();
//    }
//}
