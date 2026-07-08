package com.ADIB.FileSystem.mapper;

import com.ADIB.FileSystem.Model.Role;
import com.ADIB.FileSystem.dto.request.RoleRequest;
import com.ADIB.FileSystem.dto.response.RoleResponse;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper {

    public Role toEntity(RoleRequest request) {

        Role role = new Role();

        role.setName(request.getName());

        return role;
    }

    public RoleResponse toResponse(Role role) {

        RoleResponse response = new RoleResponse();

        response.setId(role.getId());
        response.setName(role.getName());

        return response;
    }
}