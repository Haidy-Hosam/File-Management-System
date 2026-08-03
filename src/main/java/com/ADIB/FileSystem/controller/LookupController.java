package com.ADIB.FileSystem.controller;

import com.ADIB.FileSystem.Business.dto.response.DepartmentResponse;
import com.ADIB.FileSystem.Business.dto.response.FileTypeResponse;
import com.ADIB.FileSystem.Business.dto.response.RoleResponse;
import com.ADIB.FileSystem.Business.service.DepartmentService;
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
    private final DepartmentService departmentService;
    private final RoleService roleService;
    private final FileTypeService fileTypeService;

    @GetMapping("/departments")
    public ResponseEntity<List<DepartmentResponse>> getAllDepartments() {
        return ResponseEntity.ok(departmentService.getAllDepartments());
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
