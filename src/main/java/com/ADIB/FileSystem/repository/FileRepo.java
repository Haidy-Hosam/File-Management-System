package com.ADIB.FileSystem.repository;

import com.ADIB.FileSystem.Enum.FILE_STATUS;
import com.ADIB.FileSystem.Model.Department;
import com.ADIB.FileSystem.Model.File;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FileRepo extends JpaRepository<File, Long>, JpaSpecificationExecutor<File> {
    long countByCreatedByIdAndIsDeletedFalse(Long userId);

    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM files WHERE id = :fileId")
    void deleteById(@Param("fileId") Long fileId);

    @Query("""
            SELECT f
            FROM File f
            JOIN f.departments d
            WHERE d.id = :deptId
            AND f.isDeleted = false
            """)
    Page<File> findByDepartmentId(
            @Param("deptId") Long deptId,
            Pageable pageable
    );

    @Query("""
            SELECT f
            FROM File f 
            WHERE f.isDeleted = true
            
            """)
    Page<File> findDeletedFiles(
            Pageable pageable
    );


    @Query("SELECT COUNT(f) FROM File f JOIN f.departments d WHERE d.id = :deptId")
    long countFilesByDepartment(@Param("deptId") Long deptId);

    @Query("SELECT COALESCE(SUM(f.size),0) FROM File f JOIN f.departments d WHERE d.id = :deptId")
    Long getTotalDepartmentStorage(@Param("deptId") Long deptId);

    Page<File> findByCreatedByIdAndIsDeletedFalse(Long userId, Pageable pageable);
    //    List<File> findByDepartment(Department dept);
    //            WHERE f.isDeleted = true


    // Admin
    long count();

    long countByStatus(FILE_STATUS status);

    // Employee
    long countByDepartmentsContains(Department department);

    long countByDepartmentsContainsAndStatus(
            Department department,
            FILE_STATUS status
    );
}