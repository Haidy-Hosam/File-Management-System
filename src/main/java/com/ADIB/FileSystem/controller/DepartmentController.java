package com.ADIB.FileSystem.controller;

import com.ADIB.FileSystem.Model.Department;
import com.ADIB.FileSystem.dto.request.DepartmentRequest;
import com.ADIB.FileSystem.dto.response.DepartmentResponse;
import com.ADIB.FileSystem.service.DepartmentService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @GetMapping
    public ResponseEntity<List<DepartmentResponse>> getAllDepartments()
    {
        return ResponseEntity.ok(departmentService.getAllDepartments());
    }
}
