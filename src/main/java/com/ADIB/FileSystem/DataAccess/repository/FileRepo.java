package com.ADIB.FileSystem.DataAccess.repository;

import com.ADIB.FileSystem.Business.Enum.FILE_STATUS;
import com.ADIB.FileSystem.Business.Model.Department;
import com.ADIB.FileSystem.Business.Model.File;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FileRepo extends JpaRepository<File, Long>, JpaSpecificationExecutor<File> {
    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM files WHERE id = :fileId")
    void deleteById(@Param("fileId") Long fileId);

    @Query("SELECT DISTINCT f FROM File f JOIN f.departments d WHERE d.id = :deptId AND f.isDeleted = false")
    Page<File> findByDepartmentIdAndNotDeleted(@Param("deptId") Long deptId, Pageable pageable);
    @Query(" SELECT DISTINCT f FROM File f JOIN f.departments d WHERE d.id = :deptId AND f.isDeleted = true")
    Page<File> findDeletedByDepartmentId(@Param("deptId") Long deptId, Pageable pageable);
    @Query("SELECT f FROM File f JOIN f.departments d WHERE d.id = :deptId AND f.isDeleted = false")
    Page<File> findByDepartmentId(@Param("deptId") Long deptId, Pageable pageable);
    @Query("SELECT f FROM File f WHERE f.isDeleted = true")
    Page<File> findDeletedFiles(Pageable pageable);
    Page<File> findByCreatedByIdAndIsDeletedFalse(Long userId, Pageable pageable);
    Page<File> findByIsDeletedFalse(Pageable pageable);

    @Query("SELECT COALESCE(SUM(f.size),0) FROM File f JOIN f.departments d WHERE d.id = :deptId")
    Long getTotalDepartmentStorage(@Param("deptId") Long deptId);
    @Query("SELECT COUNT(f) FROM File f JOIN f.departments d WHERE d.id = :deptId")
    long countFilesByDepartment(@Param("deptId") Long deptId);
    long countByCreatedByIdAndIsDeletedFalse(Long userId);
    long count();
    long countByStatus(FILE_STATUS status);
    long countByDepartmentsContains(Department department);
    long countByDepartmentsContainsAndStatus(Department department, FILE_STATUS status);

    @Query("SELECT DISTINCT f FROM File f JOIN f.departments d WHERE d.id = :deptId AND f.status = :status AND f.isDeleted = false")
    Page<File> findByDepartmentIdAndStatusAndNotDeleted(@Param("deptId") Long deptId,
                                                        @Param("status") FILE_STATUS status,
                                                        Pageable pageable);

    @Query("""
            SELECT DISTINCT f FROM File f
            JOIN FileDepartmentApproval a ON a.file = f
            WHERE a.department.id = :deptId
              AND a.currentApprovalOrder = f.currentApprovalOrder
              AND f.status = com.ADIB.FileSystem.Business.Enum.FILE_STATUS.PENDING
              AND f.isDeleted = false
    """)
    Page<File> findPendingApprovalForDepartment(@Param("deptId") Long deptId, Pageable pageable);
    @Query("""
SELECT DISTINCT f FROM File f
JOIN f.departments d
LEFT JOIN FileDepartmentApproval a ON a.file = f AND a.department.id = :deptId
WHERE f.isDeleted = false
  AND d.id = :deptId
  AND (
       f.status = com.ADIB.FileSystem.Business.Enum.FILE_STATUS.APPROVED
    OR (f.status = com.ADIB.FileSystem.Business.Enum.FILE_STATUS.PENDING
        AND a.currentApprovalOrder = f.currentApprovalOrder)
  )
""")
    Page<File> findVisibleToManager(@Param("deptId") Long deptId, Pageable pageable);

}