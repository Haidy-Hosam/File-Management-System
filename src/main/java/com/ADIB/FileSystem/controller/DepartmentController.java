package com.ADIB.FileSystem.controller;

import com.ADIB.FileSystem.Model.Department;
import com.ADIB.FileSystem.dto.request.DepartmentRequest;
import com.ADIB.FileSystem.dto.response.DepartmentResponse;
import com.ADIB.FileSystem.service.DepartmentService;
import com.ADIB.FileSystem.service.PermissionService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("api/department")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@PreAuthorize("@permissionService.hasPage('Departments')")
public class DepartmentController {
    private final DepartmentService departmentService;
    private final PermissionService permissionService;



    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<DepartmentResponse> createDepartment(@RequestBody DepartmentRequest request) {
        return ResponseEntity.ok(departmentService.createDepartment(request));
    }

    @GetMapping
    public ResponseEntity<List<DepartmentResponse>> getAllDepartments() {
        if(!permissionService.hasPage("Departments")){
            throw new AccessDeniedException("Access denied");
        }
            return ResponseEntity.ok(departmentService.getAllDepartments());
    }

}
