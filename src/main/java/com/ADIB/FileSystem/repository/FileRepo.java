package com.ADIB.FileSystem.repository;

import com.ADIB.FileSystem.Model.Department;
import com.ADIB.FileSystem.Model.File;
import com.ADIB.FileSystem.dto.response.FileResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FileRepo extends JpaRepository<File, Long> {
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

//    List<File> findByDepartment(Department dept);
    //            WHERE f.isDeleted = true
}