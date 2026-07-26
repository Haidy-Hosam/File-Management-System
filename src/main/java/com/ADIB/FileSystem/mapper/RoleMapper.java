package com.ADIB.FileSystem.mapper;

import com.ADIB.FileSystem.Model.Permission;
import com.ADIB.FileSystem.Model.Role;
import com.ADIB.FileSystem.Model.Page;
import com.ADIB.FileSystem.dto.request.RoleRequest;
import com.ADIB.FileSystem.dto.response.PageResponse;
import com.ADIB.FileSystem.dto.response.PermissionResponse;
import com.ADIB.FileSystem.dto.response.RoleResponse;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class RoleMapper {

    public Role toEntity(RoleRequest request) {

        Role role = new Role();

        role.setName(request.getName());

        return role;
    }

    public RoleResponse toResponse(Role role) {
        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .pages(role.getPages().stream()
                        .map(p -> new PageResponse(p.getId(), p.getPageName()))
                        .collect(Collectors.toList()))
                .permissions(role.getPermissions().stream()
                        .map(p -> new PermissionResponse(p.getPermissionId(), p.getPermissionName()))
                        .collect(Collectors.toList()))
                .build();
    }
}