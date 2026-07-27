package com.ADIB.FileSystem.mapper;

import com.ADIB.FileSystem.Model.Permission;
import com.ADIB.FileSystem.Model.Role;
import com.ADIB.FileSystem.Model.Page;
import com.ADIB.FileSystem.Model.RolePagePermission;
import com.ADIB.FileSystem.dto.request.RoleRequest;
import com.ADIB.FileSystem.dto.response.PagePermissionResponse;
import com.ADIB.FileSystem.dto.response.PageResponse;
import com.ADIB.FileSystem.dto.response.PermissionResponse;
import com.ADIB.FileSystem.dto.response.RoleResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RoleMapper {

    public Role toEntity(RoleRequest request) {

        Role role = new Role();

        role.setName(request.getName());

        return role;
    }

    public RoleResponse toResponse(Role role) {
        List<PagePermissionResponse> pagePermissions = role.getRolePagePermissions().stream()
                .collect(Collectors.groupingBy((RolePagePermission::getPage)))
                .entrySet().stream()
                .map(entry -> toPagePermissionResponse(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .pagePermissions(pagePermissions)
                .build();
    }

    private PagePermissionResponse toPagePermissionResponse(Page page, List<RolePagePermission> rpps) {
        return PagePermissionResponse.builder()
                .page(new PageResponse(page.getId(), page.getPageName(), page.getRoute()))
                .permissions(rpps.stream()
                        .map(rpp -> new PermissionResponse(rpp.getPermission().getPermissionId(),
                                rpp.getPermission().getPermissionName())).collect(Collectors.toList()))

                .build();
    }
}