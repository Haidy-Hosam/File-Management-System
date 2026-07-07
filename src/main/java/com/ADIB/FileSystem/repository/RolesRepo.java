package com.ADIB.FileSystem.repository;

import com.ADIB.FileSystem.Model.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolesRepo extends JpaRepository<Roles, Long> {
}
