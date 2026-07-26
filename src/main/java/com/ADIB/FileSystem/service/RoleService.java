package com.ADIB.FileSystem.service;

import com.ADIB.FileSystem.Model.Permission;
import com.ADIB.FileSystem.Model.Role;
import com.ADIB.FileSystem.Model.Page;
import com.ADIB.FileSystem.dto.request.RoleRequest;
import com.ADIB.FileSystem.dto.response.RoleResponse;
import com.ADIB.FileSystem.exception.ResourceNotFoundException;
import com.ADIB.FileSystem.mapper.RoleMapper;
import com.ADIB.FileSystem.repository.PageRepo;
import com.ADIB.FileSystem.repository.PermissionRepo;
import com.ADIB.FileSystem.repository.RoleRepo;
import com.ADIB.FileSystem.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepo roleRepo;
    private final PageRepo pageRepo;
    private final PermissionRepo permissionRepo;
    private final RoleMapper roleMapper;

    @Transactional
    public RoleResponse createRole(RoleRequest request) {
        Role role = Role.builder()
                .name(request.getName())
                .pages(resolvePages(request.getPageIds()))
                .permissions(resolvePermissions((request.getPermissionIds())))
                .build();

        return roleMapper.toResponse(roleRepo.save(role));
    }

    @Transactional
    public RoleResponse updateRole(Long roleId,RoleRequest request) {
        Role role = roleRepo.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role Not Found"));
        role.setName(request.getName());
        role.setPages(resolvePages(request.getPageIds()));
        role.setPermissions(resolvePermissions(request.getPermissionIds()));

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
                .orElseThrow(() -> new ResourceNotFoundException("Role Not Found"));        roleRepo.deleteById(id);

    }

    @Transactional
    public RoleResponse assignPages(Long roleId, List<Long> pagesIds) {
        Role role = roleRepo.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role Not Found"));

        role.setPages(resolvePages(pagesIds));
        return roleMapper.toResponse(roleRepo.save(role));
    }

    @Transactional
    public RoleResponse assignPermissions(Long roleId, List<Long> permissionIds) {
        Role role = roleRepo.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role Not Found"));
        role.setPermissions(resolvePermissions(permissionIds));
        return roleMapper.toResponse(roleRepo.save(role));
    }

    private Set<Page> resolvePages(List<Long> pageIds) {
        if (pageIds == null || pageIds.isEmpty()) return Set.of();
        return Set.copyOf(pageRepo.findAllById(pageIds));
    }

    private Set<Permission> resolvePermissions(List<Long> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) return Set.of();
        return Set.copyOf(permissionRepo.findAllById(permissionIds));
    }


}