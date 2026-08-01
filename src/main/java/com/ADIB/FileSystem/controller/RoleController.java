package com.ADIB.FileSystem.controller;

import com.ADIB.FileSystem.Business.dto.request.PagePermissionRequest;
import com.ADIB.FileSystem.Business.dto.request.RoleRequest;
import com.ADIB.FileSystem.Business.dto.response.RoleResponse;
import com.ADIB.FileSystem.Business.service.Permissions.PagePermissionService;
import com.ADIB.FileSystem.Business.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class RoleController {

    private final RoleService roleService;

    @PreAuthorize("@pagePermissionService.hasPermission('Roles','READ')")
    @GetMapping
    public ResponseEntity<List<RoleResponse>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @PreAuthorize("@pagePermissionService.hasPermission('Roles','READ')")
    @GetMapping("/{roleId}")
    public ResponseEntity<RoleResponse> getRole(@PathVariable Long roleId) {
        return ResponseEntity.ok(roleService.getRoleById(roleId));
    }

    @PreAuthorize("@pagePermissionService.hasPermission('Roles','CREATE')")
    @PostMapping
    public ResponseEntity<RoleResponse> createRole(@RequestBody RoleRequest dto) {
        return ResponseEntity.ok(roleService.createRole(dto));
    }

    @PreAuthorize("@pagePermissionService.hasPermission('Roles','UPDATE')")
    @PutMapping("/{roleId}")
    public ResponseEntity<RoleResponse> updateRole(@PathVariable Long roleId, @RequestBody RoleRequest dto) {
        return ResponseEntity.ok(roleService.updateRole(roleId, dto));
    }

    @PreAuthorize("@pagePermissionService.hasPermission('Roles','DELETE')")
    @DeleteMapping("/{roleId}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long roleId) {
        roleService.deleteRole(roleId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("@pagePermissionService.hasPermission('Roles','UPDATE')")
    @PutMapping("/{roleId}/pages")
    public ResponseEntity<RoleResponse> assignPages(@PathVariable Long roleId,  @RequestBody List<PagePermissionRequest> pagePermissions) {
        return ResponseEntity.ok(roleService.assignPagePermissions(roleId, pagePermissions));
    }

}