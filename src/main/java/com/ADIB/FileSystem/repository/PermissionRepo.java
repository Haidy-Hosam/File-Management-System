package com.ADIB.FileSystem.repository;

import com.ADIB.FileSystem.Model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepo extends JpaRepository<Permission, Long> {
    Optional<Permission> findByPermissionName(String permissionName);
}
