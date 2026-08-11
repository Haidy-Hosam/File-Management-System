package com.ADIB.FileSystem.DataAccess.repository;

import com.ADIB.FileSystem.Business.Enum.FILE_STATUS;
import com.ADIB.FileSystem.Business.Model.FileDepartmentApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileDepartmentApprovalRepo extends JpaRepository<FileDepartmentApproval, Long> {
    List<FileDepartmentApproval> findByFileId(Long fileId);
    Optional<FileDepartmentApproval> findByFileIdAndDepartmentId(Long fileId, Long departmentId);

}
