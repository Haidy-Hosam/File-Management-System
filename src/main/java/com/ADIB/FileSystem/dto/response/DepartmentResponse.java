package com.ADIB.FileSystem.dto.response;

import com.ADIB.FileSystem.Model.User;
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
    private String name;
    private String managerName;
    private Boolean isActive;
    private long employeeCount;
    private List<UserResponse>  employees;
    private long fileCount;
    private long storageUsed;
    List<FileResponse>  files;
}
