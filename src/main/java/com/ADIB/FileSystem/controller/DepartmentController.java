package com.ADIB.FileSystem.controller;

import com.ADIB.FileSystem.Business.dto.request.DepartmentDeleteRequest;
import com.ADIB.FileSystem.Business.dto.request.DepartmentRequest;
import com.ADIB.FileSystem.Business.dto.response.DepartmentResponse;
import com.ADIB.FileSystem.Business.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("api/departments")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class DepartmentController {
    private final DepartmentService departmentService;

    @PreAuthorize("@pagePermissionService.hasPermission('Departments','CREATE')")
    @PostMapping
    public ResponseEntity<DepartmentResponse> createDepartment(@RequestBody DepartmentRequest request) {
        return ResponseEntity.ok(departmentService.createDepartment(request));
    }

    @PreAuthorize("@pagePermissionService.hasPermission('Departments','UPDATE')")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateDepartment(@PathVariable Long id) {
        departmentService.activateDepartment(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("@pagePermissionService.hasPermission('Departments','UPDATE')")
    @PutMapping("/{id}")
    public ResponseEntity<DepartmentResponse> updateDepartment(@PathVariable Long id, @RequestBody DepartmentRequest request) {
        return ResponseEntity.ok(departmentService.updateDepartment(id, request));
    }

    @PreAuthorize("@pagePermissionService.canRead('Departments')")
    @GetMapping
    public ResponseEntity<List<DepartmentResponse>> getAllDepartments() {
            return ResponseEntity.ok(departmentService.getAllDepartments());
    }

    @PreAuthorize("@pagePermissionService.canRead('Departments')")
    @GetMapping("/details/{id}")
    public ResponseEntity<DepartmentResponse> getDepartmentDetails(@PathVariable Long id){
        return ResponseEntity.ok(departmentService.getDepartmentDetails(id));
    }

    @PreAuthorize("@pagePermissionService.hasPermission('Departments','DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id,@RequestBody(required = false) DepartmentDeleteRequest request){
       List<DepartmentDeleteRequest.ReassignmentItem> reassignments = request != null ? request.getReassignments() : null;
       departmentService.deleteDepartment(id, reassignments);
       return ResponseEntity.noContent().build();
    }
}
