package com.ADIB.FileSystem.mapper;

import com.ADIB.FileSystem.Business.Model.Department;
import com.ADIB.FileSystem.Business.Model.File;
import com.ADIB.FileSystem.Business.Model.User;
import com.ADIB.FileSystem.Business.dto.response.DepartmentResponse;
import com.ADIB.FileSystem.Business.dto.response.FileResponse;
import com.ADIB.FileSystem.Business.dto.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DepartmentMapper {
    private final FileMapper fileMapper;
    private final UserMapper userMapper;

    public DepartmentResponse MapToDepartmentResponse(Department department){
        return DepartmentResponse.builder()
                .id(department.getId())
                .name(department.getName())
                .build();
    }

    public DepartmentResponse MapToSummaryResponse(Department department,
                                                   String managerName,
                                                   long employeeCount,
                                                   long fileCount,
                                                   long storageUsed){
        return DepartmentResponse.builder()
                .id(department.getId())
                .name(department.getName())
                .managerName(managerName)
                .isActive(department.getIsActive())
                .employeeCount((int) employeeCount)
                .fileCount(fileCount)
                .storageUsed(storageUsed)
                .build();

    }
    

    public DepartmentResponse MapToDetailResponse (Department department,
                                                   String managerName,
                                                   List<User> employees,
                                                   long fileCount,
                                                   long storageUsed,
                                                   List<File> files){
        List<UserResponse> employeeResponses = employees.stream()
                .map(userMapper::mapToUserResponse)
                .toList();
        List<FileResponse> fileResponses = files.stream().map(fileMapper::mapToResponse).toList();

        return DepartmentResponse.builder()
                .id(department.getId())
                .name(department.getName())
                .managerName(managerName)
                .isActive(department.getIsActive())
                .employeeCount(employeeResponses.size())
                .fileCount(fileCount)
                .employees(employeeResponses)
                .storageUsed(storageUsed)
                .files(fileResponses)
                .build();

    }
}
