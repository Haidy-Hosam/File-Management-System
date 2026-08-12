package com.ADIB.FileSystem.mapper;

import com.ADIB.FileSystem.Business.Model.FileDepartmentApproval;
import com.ADIB.FileSystem.Business.dto.response.FileApprovalStepsResponse;
import org.springframework.stereotype.Component;

@Component
public class FileApprovalStepsMapper {

    public FileApprovalStepsResponse mapToResponse(FileDepartmentApproval fileApprovalStep) {
        return FileApprovalStepsResponse.builder()
                .departmentId(fileApprovalStep.getDepartment().getId())
                .departmentName(fileApprovalStep.getDepartment().getName())
                .status(fileApprovalStep.getStatus().name())
                .managerName(fileApprovalStep.getManager().getName())
                .decidedAt(fileApprovalStep.getDecidedAt())
                .stepNumber(fileApprovalStep.getCurrentApprovalOrder())
                .build();
    }
}
