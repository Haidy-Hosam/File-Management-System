package com.ADIB.FileSystem.service;

import com.ADIB.FileSystem.Model.Permission;
import com.ADIB.FileSystem.Model.Role;
import com.ADIB.FileSystem.Model.Page;
import com.ADIB.FileSystem.Model.RolePagePermission;
import com.ADIB.FileSystem.dto.request.PagePermissionRequest;
import com.ADIB.FileSystem.dto.request.RoleRequest;
import com.ADIB.FileSystem.dto.response.RoleResponse;
import com.ADIB.FileSystem.exception.ResourceNotFoundException;
import com.ADIB.FileSystem.mapper.RoleMapper;
import com.ADIB.FileSystem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepo roleRepo;
    private final PageRepo pageRepo;
    private final PermissionRepo permissionRepo;
    private final RolePagePermissionRepo rolePagePermissionRepo;
    private final RoleMapper roleMapper;

    @Transactional
    public RoleResponse createRole(RoleRequest request) {
        Role role = Role.builder()
                .name(request.getName())
                .build();
        role = roleRepo.save(role);
        role.setRolePagePermissions(buildRolePagePermissions(role, request.getPagePermissions()));
        roleRepo.save(role);
        return roleMapper.toResponse(role);
    }

    @Transactional
    public RoleResponse updateRole(Long roleId,RoleRequest request) {
        Role role = roleRepo.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role Not Found"));

        role.setName(request.getName());

        role.getRolePagePermissions().clear();
        roleRepo.saveAndFlush(role);

        role.getRolePagePermissions().addAll(buildRolePagePermissions(role, request.getPagePermissions()));
        return roleMapper.toResponse(roleRepo.save(role));
    }

    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRoles() {

        return roleRepo.findAll()
                .stream()
                .map(roleMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RoleResponse getRoleById(Long id) {

        Role role = roleRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role Not Found"));

        return roleMapper.toResponse(role);
    }

    @Transactional
    public void deleteRole(Long id) {
        Role role = roleRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role Not Found"));
        roleRepo.deleteById(id);

    }

    @Transactional
    public RoleResponse assignPagePermissions(Long roleId,List<PagePermissionRequest> pagePermissions) {
        Role role = roleRepo.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role Not Found"));

        role.getRolePagePermissions().clear();
        roleRepo.saveAndFlush(role);
        role.getRolePagePermissions().addAll(buildRolePagePermissions(role, pagePermissions));
        return roleMapper.toResponse(roleRepo.save(role));
    }



    private List<RolePagePermission> buildRolePagePermissions(Role role, List<PagePermissionRequest> pagePermissions){
        if (pagePermissions == null || pagePermissions.isEmpty()) return new ArrayList<>();

        List<Long> pageIds= pagePermissions.stream().map(PagePermissionRequest::getPageId).toList();
        Map<Long , Page> pagesById = pageRepo.findAllById(pageIds).stream().collect(Collectors.toMap(Page::getId, p -> p));

        List<Long> permissionIds = pagePermissions.stream()
                .flatMap(pp -> pp.getPermissionIds().stream())
                .distinct().toList();
        Map<Long, Permission> permissionsById = permissionRepo.findAllById(permissionIds).stream()
                .collect(Collectors.toMap(Permission::getPermissionId, p -> p));

        List<RolePagePermission> result = new ArrayList<>();
        for (PagePermissionRequest pp : pagePermissions){
            Page page = pagesById.get(pp.getPageId());
            if (page == null)throw new ResourceNotFoundException("Page Not Found: " + pp.getPageId());

            for (Long permissionId : pp.getPermissionIds()) {
                Permission permission = permissionsById.get(permissionId);
                if (permission == null)throw new ResourceNotFoundException("Permission Not Found: " + permissionId);
                result.add(RolePagePermission.builder()
                        .role(role)
                        .page(page)
                        .permission(permission)
                        .build());
            }
        }
        return result;
    }
}