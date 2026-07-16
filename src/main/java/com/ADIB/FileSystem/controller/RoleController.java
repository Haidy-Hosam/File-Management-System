package com.ADIB.FileSystem.controller;

import com.ADIB.FileSystem.dto.request.RoleRequest;
import com.ADIB.FileSystem.dto.response.RoleResponse;
import com.ADIB.FileSystem.service.PermissionService;
import com.ADIB.FileSystem.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@PreAuthorize("@permissionService.hasPage('Roles')")
@CrossOrigin(origins = "http://localhost:4200")
public class RoleController {

    private final RoleService roleService;
    private final PermissionService permissionService;



    @GetMapping
    public List<RoleResponse> getAllRoles() {
//        if(!permissionService.hasPage("Roles")){
//            throw new AccessDeniedException("Access denied");
//        }
        return roleService.getAllRoles();
    }

    @PostMapping
    public RoleResponse addRole(@RequestBody RoleRequest request) {

        return roleService.addRole(request);
    }

    @GetMapping("/{id}")
    public RoleResponse getRoleById(@PathVariable Long id) {

        return roleService.getRoleById(id);
    }

    @PutMapping("/{id}")
    public RoleResponse updateRole(@PathVariable Long id,
                                   @RequestBody RoleRequest request) {

        return roleService.updateRole(id, request);
    }

    @DeleteMapping("/{id}")
    public String deleteRole(@PathVariable Long id) {

        return roleService.deleteRole(id);
    }
}