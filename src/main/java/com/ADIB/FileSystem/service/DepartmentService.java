package com.ADIB.FileSystem.service;

import com.ADIB.FileSystem.Model.Department;
import com.ADIB.FileSystem.dto.request.DepartmentRequest;
import com.ADIB.FileSystem.dto.response.DepartmentResponse;
import com.ADIB.FileSystem.mapper.DepartmentMapper;
import com.ADIB.FileSystem.repository.DepartmentRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepo departmentRepo;
    private final DepartmentMapper departmentMapper;

    public DepartmentResponse createDepartment(DepartmentRequest request){
        Department department = Department.builder()
                .name(request.getName())
                .isActive(request.getIsActive())
                .build();
        departmentRepo.save(department);
        return  DepartmentResponse.builder()
                .id(department.getId())
                .name(department.getName())
                .build();
    }

    public List<DepartmentResponse> getAllDepartments(){
         return departmentRepo.findAll().stream()
                 .map(departmentMapper::MapToDepartmentResponse)
                 .collect(Collectors.toList());
    }


}
