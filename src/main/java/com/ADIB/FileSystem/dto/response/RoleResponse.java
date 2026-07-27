package com.ADIB.FileSystem.dto.response;

import com.ADIB.FileSystem.Model.Permission;
import com.ADIB.FileSystem.Model.Page;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponse {

    private Long id;
    private String name;
    private List<PagePermissionResponse> pagePermissions;

}