package com.ADIB.FileSystem.service;

import com.ADIB.FileSystem.Model.Role;
import com.ADIB.FileSystem.dto.request.RoleRequest;
import com.ADIB.FileSystem.dto.response.RoleResponse;
import com.ADIB.FileSystem.mapper.RoleMapper;
import com.ADIB.FileSystem.repository.RoleRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepo roleRepo;
    private final RoleMapper roleMapper;

    public RoleResponse addRole(RoleRequest request) {

        Role role = roleMapper.toEntity(request);

        role = roleRepo.save(role);

        return roleMapper.toResponse(role);
    }

    public List<RoleResponse> getAllRoles() {

        return roleRepo.findAll()
                .stream()
                .map(roleMapper::toResponse)
                .toList();
    }

    public RoleResponse getRoleById(Long id) {

        Role role = roleRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Role Not Found"));

        return roleMapper.toResponse(role);
    }

    public RoleResponse updateRole(Long id, RoleRequest request) {

        Role role = roleRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Role Not Found"));

        role.setName(request.getName());

        role = roleRepo.save(role);

        return roleMapper.toResponse(role);
    }

    public String deleteRole(Long id) {

        roleRepo.deleteById(id);

        return "Role Deleted Successfully";
    }
}