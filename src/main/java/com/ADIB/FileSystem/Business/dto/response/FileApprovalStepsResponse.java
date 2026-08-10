package com.ADIB.FileSystem.Business.dto.response;

import com.ADIB.FileSystem.Business.Enum.FILE_STATUS;
import com.ADIB.FileSystem.Business.Model.Department;
import com.ADIB.FileSystem.Business.Model.File;
import com.ADIB.FileSystem.Business.Model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@Builder
@AllArgsConstructor
public class FileApprovalStepsResponse {
    private Long departmentId;

    private String departmentName;

    private String managerName;

    private String status;

    private LocalDateTime decidedAt;
}
