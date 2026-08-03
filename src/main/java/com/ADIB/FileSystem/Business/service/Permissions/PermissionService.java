package com.ADIB.FileSystem.Business.service.Permissions;

import com.ADIB.FileSystem.Business.Model.Permission;
import com.ADIB.FileSystem.Business.dto.request.PermissionRequest;
import com.ADIB.FileSystem.Business.dto.response.PermissionResponse;
import com.ADIB.FileSystem.DataAccess.repository.PermissionRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PermissionService {
    private final PermissionRepo permissionRepo;

    public PermissionResponse addPermission(PermissionRequest permissionRequest) {
        Permission permission = Permission.builder()
                .permissionName(permissionRequest.getPermissionName())
                .build();
        permissionRepo.save(permission);
        return PermissionResponse.builder()
                .permissionId(permission.getPermissionId())
                .permissionName(permission.getPermissionName())
                .build();
    }


}
