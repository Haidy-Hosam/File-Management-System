package com.ADIB.FileSystem.mapper;

import com.ADIB.FileSystem.Business.Enum.FILE_STATUS;
import com.ADIB.FileSystem.Business.Model.FileDepartmentApproval;
import com.ADIB.FileSystem.Business.dto.response.FileApprovalStepsResponse;
import org.springframework.stereotype.Component;

@Component
public class FileApprovalStepsMapper {

    public FileApprovalStepsResponse mapToResponse(FileDepartmentApproval fileApprovalsSteps) {
        return FileApprovalStepsResponse.builder()
                .department(fileApprovalsSteps.getDepartment()) // CHANGED — list, not single dept
                .status(FILE_STATUS.valueOf(fileApprovalsSteps.getStatus().name()))
                .manager(fileApprovalsSteps.getManager())
                .decidedAt(fileApprovalsSteps.getDecidedAt())
                .build();
    }
}
