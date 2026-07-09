package com.ADIB.FileSystem.repository;

import com.ADIB.FileSystem.Model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepo extends JpaRepository<Role, Long> {
    Optional<Role> findByid(Long id);
}