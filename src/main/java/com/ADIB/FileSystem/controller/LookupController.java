package com.ADIB.FileSystem.controller;

import com.ADIB.FileSystem.Business.dto.response.DepartmentResponse;
import com.ADIB.FileSystem.Business.dto.response.FileTypeResponse;
import com.ADIB.FileSystem.Business.dto.response.RoleResponse;
import com.ADIB.FileSystem.Business.service.DepartmentService;
import com.ADIB.FileSystem.Business.service.LookUpService;
import com.ADIB.FileSystem.Business.service.file.FileTypeService;
import com.ADIB.FileSystem.Business.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/lookup")
@RequiredArgsConstructor
public class LookupController {
    private final LookUpService lookUpService;
    private final FileTypeService fileTypeService;
    private final RoleService roleService;
    /**
     @GetMapping("/departments")
     public ResponseEntity<List<DepartmentResponse>> getAllDepartments() {
     return ResponseEntity.ok(departmentService.getAllDepartments());
     }
     */
    @GetMapping("/departments")
    public ResponseEntity<List<DepartmentResponse>> getAllDepartments() {
        return ResponseEntity.ok(lookUpService.getAllDepartmentsLookUp());
    }

    @GetMapping("/fileTypes")
    public ResponseEntity<List<FileTypeResponse>> getAll() {
        return ResponseEntity.ok(fileTypeService.getAll());
    }


    @GetMapping("/roles")
    public ResponseEntity<List<RoleResponse>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }
}