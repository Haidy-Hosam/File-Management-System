package com.ADIB.FileSystem.Business.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentResponse {
    private Long id;
    private long employeeCount;
    private long fileCount;
    private long storageUsed;

    private String name;
    private String managerName;

    private Boolean isActive;

    private List<UserResponse>  employees;

    List<FileResponse>  files;
}
