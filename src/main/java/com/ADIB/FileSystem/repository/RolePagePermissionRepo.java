package com.ADIB.FileSystem.repository;

import com.ADIB.FileSystem.Model.RolePagePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolePagePermissionRepo extends JpaRepository<RolePagePermission, Long> {
    List<RolePagePermission> findByRoleId(Long roleId);

    @Modifying
    @Query("delete from RolePagePermission rpp where rpp.role.id = :roleId")
    void deleteByRoleId(@Param("roleId") Long roleId);
}
