package com.ADIB.FileSystem.mapper;

import com.ADIB.FileSystem.Business.Model.FileDepartmentApproval;
import com.ADIB.FileSystem.Business.dto.response.FileApprovalStepsResponse;
import org.springframework.stereotype.Component;

@Component
public class FileApprovalStepsMapper {

    public FileApprovalStepsResponse mapToResponse(FileDepartmentApproval fileApprovalStep) {
        return FileApprovalStepsResponse.builder()
                .department(fileApprovalStep.getDepartment())
                .status(fileApprovalStep.getStatus())
                .manager(fileApprovalStep.getManager())
                .decidedAt(fileApprovalStep.getDecidedAt())
                .build();
    }
}
