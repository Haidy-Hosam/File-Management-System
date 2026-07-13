package com.ADIB.FileSystem.controller;

import com.ADIB.FileSystem.Model.Department;
import com.ADIB.FileSystem.dto.request.DepartmentRequest;
import com.ADIB.FileSystem.dto.response.DepartmentResponse;
import com.ADIB.FileSystem.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/department")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentService departmentService;

    @PostMapping
    public ResponseEntity<DepartmentResponse> createDepartment(@RequestBody DepartmentRequest request)
    {
        return ResponseEntity.ok(departmentService.createDepartment(request));
    }
}
