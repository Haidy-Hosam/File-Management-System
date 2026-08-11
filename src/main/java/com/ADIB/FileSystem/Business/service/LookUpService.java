package com.ADIB.FileSystem.Business.service;


import com.ADIB.FileSystem.Business.dto.response.DepartmentResponse;
import com.ADIB.FileSystem.DataAccess.repository.DepartmentRepo;
import com.ADIB.FileSystem.mapper.DepartmentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LookUpService {
    private final DepartmentRepo departmentRepo;
    private final DepartmentMapper departmentMapper;

    public List<DepartmentResponse> getAllDepartmentsLookUp(){
        return departmentRepo.findAll().stream().map(departmentMapper::MapToDepartmentResponse).toList();
    }
}
