package com.ADIB.FileSystem.service;

import com.ADIB.FileSystem.Model.Department;
import com.ADIB.FileSystem.dto.request.DepartmentRequest;
import com.ADIB.FileSystem.dto.response.DepartmentResponse;
import com.ADIB.FileSystem.repository.DepartmentRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepo departmentRepo;

    public DepartmentResponse createDepartment(DepartmentRequest request){
        Department department = Department.builder()
                .name(request.getName())
                .isActive(request.getIsActive())
                .build();
        departmentRepo.save(department);
        return  DepartmentResponse.builder()
                .id(department.getD_id())
                .name(department.getName())
                .build();
    }
}
