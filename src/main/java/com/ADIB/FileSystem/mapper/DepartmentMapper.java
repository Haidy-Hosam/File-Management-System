package com.ADIB.FileSystem.mapper;

import com.ADIB.FileSystem.Model.Department;
import com.ADIB.FileSystem.dto.response.DepartmentResponse;
import org.springframework.stereotype.Component;

@Component
public class DepartmentMapper {
    public DepartmentResponse MapToDepartmentResponse(Department department){
        return DepartmentResponse.builder()
                .name(department.getName())
                .id(department.getId())
                .build();
    }
}
