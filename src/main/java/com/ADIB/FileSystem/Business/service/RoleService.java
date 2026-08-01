package com.ADIB.FileSystem.Business.service;

import com.ADIB.FileSystem.Business.Model.Permission;
import com.ADIB.FileSystem.Business.Model.Role;
import com.ADIB.FileSystem.Business.Model.Page;
import com.ADIB.FileSystem.Business.Model.RolePagePermission;
import com.ADIB.FileSystem.Business.dto.request.PagePermissionRequest;
import com.ADIB.FileSystem.Business.dto.request.RoleRequest;
import com.ADIB.FileSystem.Business.dto.response.RoleResponse;
import com.ADIB.FileSystem.Business.Exceptions.ResourceNotFoundException;
import com.ADIB.FileSystem.mapper.RoleMapper;
import com.ADIB.FileSystem.DataAccess.repository.PageRepo;
import com.ADIB.FileSystem.DataAccess.repository.PermissionRepo;
import com.ADIB.FileSystem.DataAccess.repository.RoleRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepo roleRepo;
    private final PageRepo pageRepo;
    private final PermissionRepo permissionRepo;
    private final RoleMapper roleMapper;

    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRoles() {
        return roleRepo.findAll().stream()
                .map(roleMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RoleResponse getRoleById(Long id) {
        return roleMapper.toResponse(getRoleOrThrow(id));
    }

    @Transactional
    public RoleResponse createRole(RoleRequest request) {
        Role role = Role.builder()
                .name(request.getName())
                .build();
        role = roleRepo.save(role);
        role.setRolePagePermissions(buildRolePagePermissions(role, request.getPagePermissions()));
        return roleMapper.toResponse(roleRepo.save(role));
    }

    @Transactional
    public RoleResponse updateRole(Long roleId,RoleRequest request) {
        Role role = getRoleOrThrow(roleId);
        role.setName(request.getName());
        replacePagePermissions(role, request.getPagePermissions());
        return roleMapper.toResponse(roleRepo.save(role));
    }

    @Transactional
    public void deleteRole(Long id) {
        if (!roleRepo.existsById(id)) {
            throw new ResourceNotFoundException("Role Not Found");
        }
        roleRepo.deleteById(id);

    }

    @Transactional
    public RoleResponse assignPagePermissions(Long roleId,List<PagePermissionRequest> pagePermissions) {
        Role role = getRoleOrThrow(roleId);
       replacePagePermissions(role, pagePermissions);
        return roleMapper.toResponse(roleRepo.save(role));
    }


    private List<RolePagePermission> buildRolePagePermissions(Role role, List<PagePermissionRequest> pagePermissions){
        if (pagePermissions == null || pagePermissions.isEmpty()) return new ArrayList<>();

        Map<Long, Page> pagesById = getPagesByIdOrThrow(pagePermissions);
        Map<Long, Permission> permissionsById = getPermissionsByIdOrThrow(pagePermissions);

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

    private Map<Long, Page> getPagesByIdOrThrow(List<PagePermissionRequest> pagePermissions) {
        List<Long> pageIds = pagePermissions.stream().map(PagePermissionRequest::getPageId).toList();
        return pageRepo.findAllById(pageIds).stream()
                .collect(Collectors.toMap(Page::getId, p -> p));
    }

    private Map<Long, Permission> getPermissionsByIdOrThrow(List<PagePermissionRequest> pagePermissions) {
        List<Long> permissionIds = pagePermissions.stream()
                .flatMap(pp -> pp.getPermissionIds().stream())
                .distinct()
                .toList();
        return permissionRepo.findAllById(permissionIds).stream()
                .collect(Collectors.toMap(Permission::getPermissionId, p -> p));
    }

    private void replacePagePermissions(Role role, List<PagePermissionRequest> pagePermissions) {
        role.getRolePagePermissions().clear();
        roleRepo.saveAndFlush(role);
        role.getRolePagePermissions().addAll(buildRolePagePermissions(role, pagePermissions));
    }

    private Role getRoleOrThrow(Long id) {
        return roleRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role Not Found"));
    }
}