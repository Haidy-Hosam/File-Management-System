package com.ADIB.FileSystem.controller;

import com.ADIB.FileSystem.dto.request.RoleRequest;
import com.ADIB.FileSystem.dto.response.RoleResponse;
import com.ADIB.FileSystem.service.PermissionService;
import com.ADIB.FileSystem.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@PreAuthorize("@permissionService.hasPage('Roles')")
@CrossOrigin(origins = "http://localhost:4200")
public class RoleController {

    private final RoleService roleService;
    private final PermissionService permissionService;



    @GetMapping
    public ResponseEntity<List<RoleResponse>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @GetMapping("/{roleId}")
    public ResponseEntity<RoleResponse> getRole(@PathVariable Long roleId) {
        return ResponseEntity.ok(roleService.getRoleById(roleId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<RoleResponse> createRole(@RequestBody RoleRequest dto) {
        return ResponseEntity.ok(roleService.createRole(dto));
    }

    @PutMapping("/{roleId}")
    public ResponseEntity<RoleResponse> updateRole(@PathVariable Long roleId, @RequestBody RoleRequest dto) {
        return ResponseEntity.ok(roleService.updateRole(roleId, dto));
    }

    @DeleteMapping("/{roleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteRole(@PathVariable Long roleId) {
        roleService.deleteRole(roleId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{roleId}/pages")
    public ResponseEntity<RoleResponse> assignPages(@PathVariable Long roleId, @RequestBody List<Long> pageIds) {
        return ResponseEntity.ok(roleService.assignPages(roleId, pageIds));
    }

    @PutMapping("/{roleId}/permissions")
    public ResponseEntity<RoleResponse> assignPermissions(@PathVariable Long roleId, @RequestBody List<Long> permissionIds) {
        return ResponseEntity.ok(roleService.assignPermissions(roleId, permissionIds));
    }
}