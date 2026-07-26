package com.ADIB.FileSystem.dto.request;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleRequest {
    private String name;
    private List<PagePermissionRequest> pagePermissions;
}